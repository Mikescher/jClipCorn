package de.jClipCorn.features.nfo;

import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

@SuppressWarnings("nls")
public class SeasonNFOWriter {

	public static FSPath getNFOPath(CCSeries series, CCSeason season) {
		// season.nfo lives inside the season folder (Jellyfin convention)
		if (season.getEpisodeCount() == 0) return FSPath.Empty;

		FSPath episodePath = season.getEpisodeByArrayIndex(0).getPart().toFSPath(season);
		if (episodePath.isEmpty()) return FSPath.Empty;

		FSPath seasonFolder = episodePath.getParent();
		if (seasonFolder.isEmpty()) return FSPath.Empty;

		return seasonFolder.append("season.nfo");
	}

	public static FSPath getPosterPath(CCSeries series, CCSeason season) {
		return getPosterPath(series, season, series.guessSeriesBasePath());
	}

	public static FSPath getPosterPath(CCSeries series, CCSeason season, FSPath basePath) {
		if (basePath.isEmpty()) return FSPath.Empty;

		if (season.getEpisodeCount() == 0) return FSPath.Empty;

		FSPath episodePath = season.getEpisodeByArrayIndex(0).getPart().toFSPath(season);
		if (episodePath.isEmpty()) return FSPath.Empty;

		String seasonFolderName = episodePath.getParent().getDirectoryName();
		String coverExt = series.getMovieList().ccprops().PROP_COVER_TYPE.getValue();
		return basePath.append(seasonFolderName + "." + coverExt);
	}

	public static String generateNFO(CCSeries series, CCSeason season) {
		Element root = new Element("season");
		Document doc = new Document(root);

		// Title
		root.addContent(new Element("title").setText(season.getTitle()));

		// Sort title
		root.addContent(new Element("sorttitle").setText(season.getTitle()));

		// Season number (matches the <season> value written into the episode NFOs of this season)
		root.addContent(new Element("seasonnumber").setText(String.valueOf(season.getSeasonNumber())));

		// Year + premiered
		season.getYear().ifPresent(year -> {
			root.addContent(new Element("year").setText(String.valueOf(year)));
			root.addContent(new Element("premiered").setText(String.valueOf(year)));
		});

		// Studio(s)
		for (String studio : season.getAnimeStudio()) {
			if (!Str.isNullOrWhitespace(studio)) {
				root.addContent(new Element("studio").setText(studio));
			}
		}

		// Thumb (season poster)
		writeCoverThumb(root, series, season);

		// User rating (1-10 scale, jClipCorn uses 0-6)
		int score = season.getScore().asInt();
		if (score > 0) {
			// Convert from 0-6 scale to 1-10 scale
			int userRating = (int) Math.round(score * 10.0 / 6.0);
			root.addContent(new Element("userrating").setText(String.valueOf(userRating)));
		}

		// Unique IDs
		writeUniqueIds(root, season);

		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));

		return xout.outputString(doc).replace("\r", "");
	}

	private static void writeUniqueIds(Element root, CCSeason season) {
		boolean hasDefault = false;

		for (CCSingleOnlineReference ref : season.getOnlineReference()) {
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
		clipcornId.setText(String.valueOf(season.getLocalID()));
		root.addContent(clipcornId);
	}

	private static void writeCoverThumb(Element root, CCSeries series, CCSeason season) {
		FSPath posterPath = getPosterPath(series, season);
		if (posterPath.isEmpty()) return;

		CCCoverData coverData = season.getCoverInfo();
		if (coverData == null) return;

		// The season poster lives in the parent (series) folder, the season.nfo inside the season folder
		Element thumb = new Element("thumb");
		thumb.setAttribute("aspect", "poster");
		thumb.setText("../" + posterPath.getFilenameWithExt());
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
