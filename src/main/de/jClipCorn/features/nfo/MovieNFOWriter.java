package de.jClipCorn.features.nfo;

import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.covertab.ICoverCache;
import de.jClipCorn.database.databaseElement.CCMovie;
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
public class MovieNFOWriter {

	public static FSPath getNFOPath(CCMovie movie) {
		if (movie.getPartcount() == 0) return FSPath.Empty;

		FSPath videoPath = movie.Parts.get(0).toFSPath(movie);
		if (videoPath.isEmpty()) return FSPath.Empty;

		return videoPath.replaceExtension("nfo");
	}

	public static String generateNFO(CCMovie movie) {
		Element root = new Element("movie");
		Document doc = new Document(root);

		// Title
		root.addContent(new Element("title").setText(movie.getTitle()));

		// Original title (if zyklus title is set, use as original)
		if (!movie.Zyklus.get().isEmpty()) {
			root.addContent(new Element("originaltitle").setText(movie.Zyklus.get().getFormatted()));
		}

		// Sort title
		root.addContent(new Element("sorttitle").setText(movie.getTitle()));

		// Year
		root.addContent(new Element("year").setText(String.valueOf(movie.Year.get())));

		// Runtime in minutes
		int runtimeMinutes = movie.Length.get() / 60;
		root.addContent(new Element("runtime").setText(String.valueOf(runtimeMinutes)));

		// Genres - each in separate tag
		for (CCGenre genre : movie.Genres.get().getGenres()) {
			root.addContent(new Element("genre").setText(genre.asString()));
		}

		// Thumb (cover image)
		writeCoverThumb(root, movie);

		// Movie set (Zyklus)
		if (!movie.Zyklus.get().isEmpty()) {
			Element set = new Element("set");
			set.addContent(new Element("name").setText(movie.Zyklus.get().getTitle()));
			root.addContent(set);
		}

		// User rating (1-10 scale, jClipCorn uses 0-6)
		int score = movie.Score.get().asInt();
		if (score > 0) {
			// Convert from 0-6 scale to 1-10 scale
			int userRating = (int) Math.round(score * 10.0 / 6.0);
			root.addContent(new Element("userrating").setText(String.valueOf(userRating)));
		}

		// Unique IDs
		writeUniqueIds(root, movie);

		// Add date
		root.addContent(new Element("dateadded").setText(movie.AddDate.get().toStringSQL()));

		// Playcount
		int viewCount = movie.ViewedHistory.get().count();
		root.addContent(new Element("playcount").setText(String.valueOf(viewCount)));

		// Last played
		if (!movie.ViewedHistory.get().isEmpty()) {
			root.addContent(new Element("lastplayed").setText(movie.ViewedHistory.get().getLastOrInvalid().toStringUINormal()));
		}

		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());
		return xout.outputString(doc);
	}

	private static void writeUniqueIds(Element root, CCMovie movie) {
		boolean hasDefault = false;

		for (CCSingleOnlineReference ref : movie.OnlineReference.get()) {
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
		clipcornId.setText(String.valueOf(movie.LocalID.get()));
		root.addContent(clipcornId);
	}

	private static void writeCoverThumb(Element root, CCMovie movie) {
		FSPath nfoPath = getNFOPath(movie);
		if (nfoPath.isEmpty()) return;

		ICoverCache coverCache = movie.getMovieList().getCoverCache();
		CCCoverData coverData = movie.getCoverInfo();
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
