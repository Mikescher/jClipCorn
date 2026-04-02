package de.jClipCorn.features.nfo;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.filesystem.FSPath;

@SuppressWarnings("nls")
public class SeasonNFOWriter {
	public static FSPath getPosterPath(CCSeries series, CCSeason season) {
		FSPath basePath = series.guessSeriesBasePath();
		if (basePath.isEmpty()) return FSPath.Empty;

		if (season.getEpisodeCount() == 0) return FSPath.Empty;

		FSPath episodePath = season.getEpisodeByArrayIndex(0).getPart().toFSPath(season);
		if (episodePath.isEmpty()) return FSPath.Empty;

		String seasonFolderName = episodePath.getParent().getDirectoryName();
		String coverExt = series.getMovieList().ccprops().PROP_COVER_TYPE.getValue();
		return basePath.append(seasonFolderName + "." + coverExt);
	}

}
