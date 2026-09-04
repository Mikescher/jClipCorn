package de.jClipCorn.features.nfo;

import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.FSPath;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("nls")
public class SeriesNFOWriter {

	public static FSPath getNFOPath(CCSeries series) {
		return getNFOPath(series, series.guessSeriesBasePath());
	}

	public static FSPath getNFOPath(CCSeries series, FSPath rootPath) {
		if (rootPath.isEmpty()) return FSPath.Empty;

		return rootPath.append("tvshow.nfo");
	}

	public static FSPath getPosterPath(CCSeries series) {
		return getPosterPath(series, series.guessSeriesBasePath());
	}

	public static FSPath getPosterPath(CCSeries series, FSPath rootPath) {
		if (rootPath.isEmpty()) return FSPath.Empty;

		// Named "poster.<ext>" so Jellyfin/Kodi auto-detect it as the series cover.
		// (Jellyfin only recognizes poster/folder/cover/default/show - NOT "tvshow.jpg")
		String coverExt = series.getMovieList().ccprops().PROP_COVER_TYPE.getValue();
		return rootPath.append("poster." + coverExt);
	}

	public static String generateNFO(CCSeries series) {
		Element root = new Element("tvshow");
		Document doc = new Document(root);

		// Title
		root.addContent(new Element("title").setText(series.getTitle()));

		// Sort title
		root.addContent(new Element("sorttitle").setText(series.getTitle()));

		// Year (first year from YearRange)
		int year = series.getYearRange().getLowestYear();
		if (year > 0) {
			root.addContent(new Element("year").setText(String.valueOf(year)));
		}

		// Premiered
		if (year > 0) {
			root.addContent(new Element("premiered").setText(String.valueOf(year)));
		}

		// Genres - each in separate tag
		for (CCGenre genre : series.getGenres().getGenres()) {
			root.addContent(new Element("genre").setText(genre.asString()));
		}

		// Studio(s) (aggregated over all seasons)
		for (String studio : series.getAnimeStudio()) {
			if (!Str.isNullOrWhitespace(studio)) {
				root.addContent(new Element("studio").setText(studio));
			}
		}

		// Thumb (cover image)
		writeCoverThumb(root, series);

		// User rating (1-10 scale, jClipCorn uses 0-6)
		int score = series.Score.get().asInt();
		if (score > 0) {
			// Convert from 0-6 scale to 1-10 scale
			int userRating = (int) Math.round(score * 10.0 / 6.0);
			root.addContent(new Element("userrating").setText(String.valueOf(userRating)));
		}

		// Add date
		CCDate addDate = series.getAddDate_Oldest();
		if (addDate != null && !addDate.isMinimum()) {
			root.addContent(new Element("dateadded").setText(addDate.toStringSQL()));
		}

		// Status
		// Kodi uses: Continuing, Ended, In Production, etc.
		// We don't have this data, so skip it

		// Unique IDs
		writeUniqueIds(root, series);

		// Season count
		int seasonCount = series.getSeasonCount();
		root.addContent(new Element("season").setText(String.valueOf(seasonCount)));

		// Episode count
		int episodeCount = series.getEpisodeCount();
		root.addContent(new Element("episode").setText(String.valueOf(episodeCount)));

		// Named seasons
		for (int i = 0; i < series.getSeasonCount(); i++) {
			var season = series.getSeasonByArrayIndex(i);
			Element namedSeason = new Element("namedseason");
			namedSeason.setAttribute("number", String.valueOf(season.getIndexForCreatedFolderStructure()));
			namedSeason.setText(season.getTitle());
			root.addContent(namedSeason);
		}

		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));

		return xout.outputString(doc).replace("\r", "");
	}

	private static void writeUniqueIds(Element root, CCSeries series) {
		Set<String> skipped = hasIndependentSeasons(series) ? Set.of("tmdb", "imdb") : Collections.emptySet();

		NFOUniqueIdWriter.write(root, series.OnlineReference.get(), skipped, series.ID.get().toString());
	}

	/**
	 * Whether the seasons of this series are separate works with their own online identity - the usual shape of
	 * anime that is split into parts, where every part has its own AniDB/MAL/AniList entry.
	 * <p>
	 * Jellyfin resolves episode metadata as (series-id, {@code <season>}, {@code <episode>}). Those numbers are
	 * ClipCorn's own folder index ({@link CCSeason#getIndexForCreatedFolderStructure()}), which only lines up with
	 * a provider's season layout when the seasons are not independent works - TMDB for instance keeps three parts
	 * of an anime as one 36-episode season. Writing tmdb/imdb on such a series makes Jellyfin fill every episode
	 * with the plot of an unrelated one, so those ids are left out and the per-season ids in season.nfo resolve it.
	 */
	private static boolean hasIndependentSeasons(CCSeries series) {
		Set<CCOnlineReferenceList> distinct = new HashSet<>();

		for (int i = 0; i < series.getSeasonCount(); i++) {
			CCOnlineReferenceList refs = series.getSeasonByArrayIndex(i).getOnlineReference();
			if (refs.isEmpty()) continue;

			distinct.add(refs);
			if (distinct.size() >= 2) return true;
		}

		return false;
	}

	private static void writeCoverThumb(Element root, CCSeries series) {
		FSPath posterPath = getPosterPath(series);
		if (posterPath.isEmpty()) return;

		CCCoverData coverData = series.getCoverInfo();
		if (coverData == null) return;

		Element thumb = new Element("thumb");
		thumb.setAttribute("aspect", "poster");
		thumb.setText(posterPath.getFilenameWithExt());
		root.addContent(thumb);
	}
}
