package de.jClipCorn.features.nfo;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.covertab.ICoverCache;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.filesystem.FSPath;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class NFOGenerator {

	@FunctionalInterface
	public interface ProgressCallback {
		void onProgress(int current, int max, String message);
	}

	public static List<NFOEntry> generateEntries(CCMovieList movielist) {
		return generateEntries(movielist, null);
	}

	public static List<NFOEntry> generateEntries(CCMovieList movielist, ProgressCallback callback) {
		List<NFOEntry> entries = new ArrayList<>();

		boolean createMovieNfos = movielist.ccprops().PROP_NFO_CREATE_MOVIES.getValue();
		boolean createSeriesNfos = movielist.ccprops().PROP_NFO_CREATE_SERIES.getValue();

		// Count total items for progress
		int totalMovies = movielist.getMovieCount();
		int totalEpisodes = 0;
		for (CCSeries series : movielist.iteratorSeries()) {
			totalEpisodes++; // for tvshow.nfo
			totalEpisodes += series.getEpisodeCount();
		}
		int total = totalMovies + totalEpisodes;
		int current = 0;

		// Process movies
		for (CCMovie movie : movielist.iteratorMovies()) {
			if (callback != null) {
				callback.onProgress(current, total, movie.getTitle());
			}

			FSPath nfoPath = MovieNFOWriter.getNFOPath(movie);
			if (nfoPath.isEmpty()) {
				entries.add(NFOEntry.forMovie(nfoPath, NFOStatus.ERROR, "", movie));
			} else if (!movie.getParts().get(0).toFSPath(movielist.ccprops()).fileExists()) {
				entries.add(NFOEntry.forMovie(nfoPath, NFOStatus.ERROR, "", movie));
			} else {
				String newContent = MovieNFOWriter.generateNFO(movie);
				NFOStatus status = determineStatus(nfoPath, newContent, createMovieNfos);
				if (status == NFOStatus.UNCHANGED && createMovieNfos) {
					if (posterNeedsUpdate(movielist.getCoverCache(), movie.getCoverInfo(), MovieNFOWriter.getPosterPath(movie))) {
						status = NFOStatus.CHANGED;
					}
				}
				entries.add(NFOEntry.forMovie(nfoPath, status, newContent, movie));
			}
			current++;
		}

		// Process series
		for (CCSeries series : movielist.iteratorSeries()) {
			if (callback != null) {
				callback.onProgress(current, total, series.getTitle());
			}

			// Series tvshow.nfo
			FSPath seriesNfoPath = SeriesNFOWriter.getNFOPath(series);
			if (seriesNfoPath.isEmpty()) {
				entries.add(NFOEntry.forSeries(seriesNfoPath, NFOStatus.ERROR, "", series));
			} else if (series.guessSeriesRootPath().isEmpty() || !series.guessSeriesRootPath().directoryExists()) {
				entries.add(NFOEntry.forSeries(seriesNfoPath, NFOStatus.ERROR, "", series));
			} else {
				String newContent = SeriesNFOWriter.generateNFO(series);
				NFOStatus status = determineStatus(seriesNfoPath, newContent, createSeriesNfos);
				if (status == NFOStatus.UNCHANGED && createSeriesNfos) {
					ICoverCache coverCache = movielist.getCoverCache();
					if (posterNeedsUpdate(coverCache, series.getCoverInfo(), SeriesNFOWriter.getPosterPath(series))) {
						status = NFOStatus.CHANGED;
					} else {
						for (int si2 = 0; si2 < series.getSeasonCount(); si2++) {
							CCSeason sea = series.getSeasonByArrayIndex(si2);
							if (posterNeedsUpdate(coverCache, sea.getCoverInfo(), SeriesNFOWriter.getSeasonPosterPath(series, sea))) {
								status = NFOStatus.CHANGED;
								break;
							}
						}
					}
				}
				entries.add(NFOEntry.forSeries(seriesNfoPath, status, newContent, series));
			}
			current++;

			// Episode NFOs
			for (int si = 0; si < series.getSeasonCount(); si++) {
				CCSeason season = series.getSeasonByArrayIndex(si);
				for (int ei = 0; ei < season.getEpisodeCount(); ei++) {
					CCEpisode episode = season.getEpisodeByArrayIndex(ei);

					if (callback != null) {
						callback.onProgress(current, total, series.getTitle() + " - " + episode.getTitle());
					}

					FSPath episodeNfoPath = EpisodeNFOWriter.getNFOPath(episode);
					if (episodeNfoPath.isEmpty()) {
						entries.add(NFOEntry.forEpisode(episodeNfoPath, NFOStatus.ERROR, "", episode));
					} else if (!episode.getParts().get(0).toFSPath(movielist.ccprops()).fileExists()) {
						entries.add(NFOEntry.forEpisode(episodeNfoPath, NFOStatus.ERROR, "", episode));
					} else {
						String newContent = EpisodeNFOWriter.generateNFO(episode);
						NFOStatus status = determineStatus(episodeNfoPath, newContent, createSeriesNfos);

						entries.add(NFOEntry.forEpisode(episodeNfoPath, status, newContent, episode));
					}
					current++;
				}
			}
		}

		return entries;
	}

	private static NFOStatus determineStatus(FSPath nfoPath, String newContent, boolean createEnabled) {
		boolean exists = nfoPath.exists();

		if (!createEnabled) {
			// Setting is off - if file exists, mark for deletion
			return exists ? NFOStatus.DELETE : NFOStatus.UNCHANGED;
		}

		if (!exists) {
			return NFOStatus.CREATE;
		}

		// File exists, compare content
		try {
			String existingContent = nfoPath.readAsUTF8TextFile();
			if (contentMatches(existingContent, newContent)) {
				return NFOStatus.UNCHANGED;
			} else {
				return NFOStatus.CHANGED;
			}
		} catch (IOException e) {
			// Can't read existing file, assume it needs updating
			return NFOStatus.CHANGED;
		}
	}

	private static boolean contentMatches(String existing, String newContent) {
		// Normalize whitespace for comparison
		String normalizedExisting = normalizeXml(existing);
		String normalizedNew = normalizeXml(newContent);
		return normalizedExisting.equals(normalizedNew);
	}

	private static String normalizeXml(String xml) {
		// Remove XML declaration differences and normalize whitespace
		return xml.replaceAll("\\s+", " ").trim();
	}

	public static void applyEntry(NFOEntry entry) throws IOException {
		switch (entry.getStatus()) {
			case CREATE:
			case CHANGED:
				writeNfoFile(entry.getFilePath(), entry.getContent());
				writePosterFiles(entry);
				break;
			case DELETE:
				deleteNfoFile(entry.getFilePath());
				deletePosterFiles(entry);
				break;
			case UNCHANGED:
				// Do nothing
			case ERROR:
				// Do nothing
				break;
		}
	}

	private static void writeNfoFile(FSPath path, String content) throws IOException {
		path.writeAsUTF8TextFile(content);
	}

	private static void deleteNfoFile(FSPath path) throws IOException {
		if (path.exists()) {
			path.deleteWithException();
		}
	}

	private static void writePosterFiles(NFOEntry entry) throws IOException {
		if (entry.getMovie() != null) {
			CCMovie movie = entry.getMovie();
			ICoverCache coverCache = movie.getMovieList().getCoverCache();
			CCCoverData coverData = movie.getCoverInfo();
			FSPath posterPath = MovieNFOWriter.getPosterPath(movie);
			copyCoverFile(coverCache, coverData, posterPath);
		} else if (entry.getSeries() != null) {
			CCSeries series = entry.getSeries();
			ICoverCache coverCache = series.getMovieList().getCoverCache();

			// Series poster
			copyCoverFile(coverCache, series.getCoverInfo(), SeriesNFOWriter.getPosterPath(series));

			// Season posters
			for (int i = 0; i < series.getSeasonCount(); i++) {
				CCSeason season = series.getSeasonByArrayIndex(i);
				FSPath seasonPosterPath = SeriesNFOWriter.getSeasonPosterPath(series, season);
				if (!seasonPosterPath.isEmpty()) {
					copyCoverFile(coverCache, season.getCoverInfo(), seasonPosterPath);
				}
			}
		}
		// Episodes don't have covers - no-op
	}

	private static void deletePosterFiles(NFOEntry entry) {
		if (entry.getMovie() != null) {
			FSPath posterPath = MovieNFOWriter.getPosterPath(entry.getMovie());
			if (!posterPath.isEmpty() && posterPath.exists()) {
				posterPath.toFile().delete();
			}
		} else if (entry.getSeries() != null) {
			CCSeries series = entry.getSeries();

			// Series poster
			FSPath seriesPosterPath = SeriesNFOWriter.getPosterPath(series);
			if (!seriesPosterPath.isEmpty() && seriesPosterPath.exists()) {
				seriesPosterPath.toFile().delete();
			}

			// Season posters
			for (int i = 0; i < series.getSeasonCount(); i++) {
				CCSeason season = series.getSeasonByArrayIndex(i);
				FSPath seasonPosterPath = SeriesNFOWriter.getSeasonPosterPath(series, season);
				if (!seasonPosterPath.isEmpty() && seasonPosterPath.exists()) {
					seasonPosterPath.toFile().delete();
				}
			}
		}
		// Episodes don't have covers - no-op
	}

	private static void copyCoverFile(ICoverCache coverCache, CCCoverData coverData, FSPath targetPath) throws IOException {
		if (coverData == null) return;
		if (targetPath.isEmpty()) return;

		FSPath coverPath = coverCache.getFilepath(coverData);
		if (coverPath.isEmpty() || !coverPath.exists()) return;

		Files.copy(coverPath.toPath(), targetPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	private static boolean posterNeedsUpdate(ICoverCache coverCache, CCCoverData coverData, FSPath posterPath) {
		if (posterPath.isEmpty()) return false;
		if (coverData == null) return false;

		if (!posterPath.exists()) return true;

		try (FileInputStream fis = new FileInputStream(posterPath.toFile())) {
			String fileHash = DigestUtils.sha256Hex(fis).toUpperCase();
			return !fileHash.equals(coverData.Checksum);
		} catch (IOException e) {
			return true;
		}
	}
}
