package de.jClipCorn.features.nfo;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.util.filesystem.FSPath;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

@SuppressWarnings("nls")
public class EpisodeNFOWriter {

	public static FSPath getNFOPath(CCEpisode episode) {
		FSPath videoPath = episode.Part.get().toFSPath(episode.getSeries());
		if (videoPath.isEmpty()) return FSPath.Empty;

		return videoPath.replaceExtension("nfo");
	}

	public static String generateNFO(CCEpisode episode) {
		Element root = new Element("episodedetails");
		Document doc = new Document(root);

		// Title
		root.addContent(new Element("title").setText(episode.getTitle()));

		// Season number
		int seasonNumber = episode.getSeason().getSeasonNumber();
		root.addContent(new Element("season").setText(String.valueOf(seasonNumber)));

		// Episode number
		int episodeNumber = episode.getEpisodeNumber();
		root.addContent(new Element("episode").setText(String.valueOf(episodeNumber)));

		// Runtime in minutes
		int runtimeMinutes = episode.Length.get() / 60;
		root.addContent(new Element("runtime").setText(String.valueOf(runtimeMinutes)));

		// User rating (1-10 scale, jClipCorn uses 0-6)
		int score = episode.Score.get().asInt();
		if (score > 0) {
			// Convert from 0-6 scale to 1-10 scale
			int userRating = (int) Math.round(score * 10.0 / 6.0);
			root.addContent(new Element("userrating").setText(String.valueOf(userRating)));
		}

		// Add date / aired
		root.addContent(new Element("dateadded").setText(episode.AddDate.get().toStringSQL()));

		// Playcount
		int viewCount = episode.ViewedHistory.get().count();
		root.addContent(new Element("playcount").setText(String.valueOf(viewCount)));

		// Last played
		if (!episode.ViewedHistory.get().isEmpty()) {
			root.addContent(new Element("lastplayed").setText(episode.ViewedHistory.get().getLastOrInvalid().toStringUINormal()));
		}

		// Unique IDs
		writeUniqueIds(root, episode);

		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat().setLineSeparator("\n"));

		return xout.outputString(doc).replace("\r", "");
	}

	private static void writeUniqueIds(Element root, CCEpisode episode) {
		// Add clipcorn internal ID (episode has LocalEpisodeID)
		Element clipcornId = new Element("uniqueid");
		clipcornId.setAttribute("type", "clipcorn");
		clipcornId.setText(String.valueOf(episode.LocalID.get()));
		root.addContent(clipcornId);
	}
}
