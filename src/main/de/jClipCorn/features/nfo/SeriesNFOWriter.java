package de.jClipCorn.features.nfo;

import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.covertab.ICoverCache;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

@SuppressWarnings("nls")
public class SeriesNFOWriter {

	public static FSPath getNFOPath(CCSeries series) {
		FSPath rootPath = series.guessSeriesRootPath();
		if (rootPath.isEmpty()) return FSPath.Empty;

		return rootPath.append("tvshow.nfo");
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

		// Thumb (cover image)
		writeCoverThumb(root, series);

		// User rating (1-10 scale, jClipCorn uses 0-6)
		int score = series.Score.get().asInt();
		if (score > 0) {
			// Convert from 0-6 scale to 1-10 scale
			int userRating = (int) Math.round(score * 10.0 / 6.0);
			root.addContent(new Element("userrating").setText(String.valueOf(userRating)));
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
			namedSeason.setAttribute("number", String.valueOf(season.getSeasonNumber()));
			namedSeason.setText(season.getTitle());
			root.addContent(namedSeason);
		}

		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());
		return xout.outputString(doc);
	}

	private static void writeUniqueIds(Element root, CCSeries series) {
		boolean hasDefault = false;

		for (CCSingleOnlineReference ref : series.OnlineReference.get()) {
			if (ref.type == CCOnlineRefType.NONE) continue;

			String typeId = getKodiProviderType(ref.type);
			if (Str.isNullOrEmpty(typeId)) continue;

			Element uniqueid = new Element("uniqueid");
			uniqueid.setAttribute("type", typeId);
			if (!hasDefault) {
				uniqueid.setAttribute("default", "true");
				hasDefault = true;
			}
			uniqueid.setText(ref.id);
			root.addContent(uniqueid);
		}

		// Add clipcorn internal ID
		Element clipcornId = new Element("uniqueid");
		clipcornId.setAttribute("type", "clipcorn");
		clipcornId.setText(String.valueOf(series.LocalID.get()));
		root.addContent(clipcornId);
	}

	private static void writeCoverThumb(Element root, CCSeries series) {
		FSPath nfoPath = getNFOPath(series);
		if (nfoPath.isEmpty()) return;

		ICoverCache coverCache = series.getMovieList().getCoverCache();
		CCCoverData coverData = series.getCoverInfo();
		if (coverData == null) return;

		FSPath coverPath = coverCache.getFilepath(coverData);
		if (coverPath.isEmpty() || !coverPath.exists()) return;

		// Calculate relative path from NFO location to cover file
		String relativePath = nfoPath.getParent().toPath().relativize(coverPath.toPath()).toString();

		Element thumb = new Element("thumb");
		thumb.setAttribute("aspect", "poster");
		thumb.setText(relativePath);
		root.addContent(thumb);
	}

	private static String getKodiProviderType(CCOnlineRefType type) {
		switch (type) {
			case IMDB:        return "imdb";
			case THEMOVIEDB:  return "tmdb";
			case ANIDB:       return "anidb";
			case MYANIMELIST: return "myanimelist";
			case ANILIST:     return "anilist";
			default:          return null;
		}
	}
}
