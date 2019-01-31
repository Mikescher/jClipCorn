package de.jClipCorn.util.parser;

public class EpisodeFilenameParserResult {
	public final int SeasonNumber;
	public final int EpisodeNumber;
	public final String Title;
	public final String Extension;

	public EpisodeFilenameParserResult(int s, int e, String t, String x) {
		this.SeasonNumber = s;
		this.EpisodeNumber = e;
		this.Title = t;
		this.Extension = x;
	}
}
