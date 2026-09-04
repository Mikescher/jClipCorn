package de.jClipCorn.features.nfo;

import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
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

	public static FSPath getPosterPath(CCMovie movie) {
		if (movie.getPartcount() == 0) return FSPath.Empty;

		FSPath videoPath = movie.Parts.get(0).toFSPath(movie);
		if (videoPath.isEmpty()) return FSPath.Empty;

		String coverExt = movie.getMovieList().ccprops().PROP_COVER_TYPE.getValue();
		return videoPath.replaceExtension(coverExt);
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
		movie.Year.get().ifPresent(year -> root.addContent(new Element("year").setText(String.valueOf(year))));

		// Runtime - Length is already stored in minutes
		root.addContent(new Element("runtime").setText(String.valueOf(movie.Length.get())));

		// Genres - each in separate tag
		for (CCGenre genre : movie.Genres.get().getGenres()) {
			root.addContent(new Element("genre").setText(genre.asString()));
		}

		// Studio(s)
		for (String studio : movie.getAnimeStudio()) {
			if (!Str.isNullOrWhitespace(studio)) {
				root.addContent(new Element("studio").setText(studio));
			}
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
		xout.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));

		return xout.outputString(doc).replace("\r", "");
	}

	private static void writeUniqueIds(Element root, CCMovie movie) {
		NFOUniqueIdWriter.write(root, movie.OnlineReference.get(), movie.ID.get().toString());
	}

	private static void writeCoverThumb(Element root, CCMovie movie) {
		FSPath posterPath = getPosterPath(movie);
		if (posterPath.isEmpty()) return;

		CCCoverData coverData = movie.getCoverInfo();
		if (coverData == null) return;

		Element thumb = new Element("thumb");
		thumb.setAttribute("aspect", "poster");
		thumb.setText(posterPath.getFilenameWithExt());
		root.addContent(thumb);
	}
}
