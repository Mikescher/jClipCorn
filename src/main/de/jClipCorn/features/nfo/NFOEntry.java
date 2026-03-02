package de.jClipCorn.features.nfo;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.filesystem.FSPath;

public class NFOEntry {
	private final FSPath filePath;
	private final NFOStatus status;
	private final NFOElementType elementType;
	private final String elementTitle;
	private final String content;

	// Reference to the source element (one of these will be set)
	private final CCMovie movie;
	private final CCSeries series;
	private final CCEpisode episode;

	private NFOEntry(FSPath filePath, NFOStatus status, NFOElementType elementType, String elementTitle, String content, CCMovie movie, CCSeries series, CCEpisode episode) {
		this.filePath = filePath;
		this.status = status;
		this.elementType = elementType;
		this.elementTitle = elementTitle;
		this.content = content;
		this.movie = movie;
		this.series = series;
		this.episode = episode;
	}

	public static NFOEntry forMovie(FSPath filePath, NFOStatus status, String content, CCMovie movie) {
		return new NFOEntry(filePath, status, NFOElementType.MOVIE, movie.getTitle(), content, movie, null, null);
	}

	public static NFOEntry forSeries(FSPath filePath, NFOStatus status, String content, CCSeries series) {
		return new NFOEntry(filePath, status, NFOElementType.SERIES, series.getTitle(), content, null, series, null);
	}

	public static NFOEntry forEpisode(FSPath filePath, NFOStatus status, String content, CCEpisode episode) {
		String title = episode.getSeries().getTitle() + " - " + episode.getStringIdentifier();
		return new NFOEntry(filePath, status, NFOElementType.EPISODE, title, content, null, null, episode);
	}

	public FSPath getFilePath() {
		return filePath;
	}

	public NFOStatus getStatus() {
		return status;
	}

	public NFOElementType getElementType() {
		return elementType;
	}

	public String getElementTitle() {
		return elementTitle;
	}

	public String getContent() {
		return content;
	}

	public CCMovie getMovie() {
		return movie;
	}

	public CCSeries getSeries() {
		return series;
	}

	public CCEpisode getEpisode() {
		return episode;
	}

	public NFOEntry withStatus(NFOStatus newStatus) {
		return new NFOEntry(filePath, newStatus, elementType, elementTitle, content, movie, series, episode);
	}

	public NFOEntry withContent(String newContent) {
		return new NFOEntry(filePath, status, elementType, elementTitle, newContent, movie, series, episode);
	}
}
