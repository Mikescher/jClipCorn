package de.jClipCorn.gui.frames.autofindRefrenceFrame;

import java.awt.image.BufferedImage;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.util.parser.onlineparser.ImDBParser.IMDBLimitedResult;
import de.jClipCorn.util.parser.onlineparser.TMDBParser.TMDBFullResult;

public class AutoFindRefElement {
	public final CCDatabaseElement local;
	
	public final CCOnlineReference tmdbRef;
	public final TMDBFullResult tmdbMeta;
	public final BufferedImage tmdbCover;

	public final IMDBLimitedResult imdbMeta;
	
	public AutoFindRefElement(CCDatabaseElement e, CCOnlineReference r, TMDBFullResult m, BufferedImage i, IMDBLimitedResult imdb) {
		local = e;
		
		tmdbRef = r;
		tmdbMeta = m;
		tmdbCover = i;
		
		imdbMeta = imdb;
	}
}
