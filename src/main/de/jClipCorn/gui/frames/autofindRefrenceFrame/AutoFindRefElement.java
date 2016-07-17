package de.jClipCorn.gui.frames.autofindRefrenceFrame;

import java.awt.image.BufferedImage;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.util.parser.onlineparser.TMDBParser.TMDBFullResult;

public class AutoFindRefElement {
	public final CCDatabaseElement local;
	public final CCOnlineReference onlineRef;
	public final TMDBFullResult onlineMeta;
	public final BufferedImage onlineCover;
	
	public AutoFindRefElement(CCDatabaseElement e, CCOnlineReference r, TMDBFullResult m, BufferedImage i) {
		local = e;
		onlineRef = r;
		onlineMeta = m;
		onlineCover = i;
	}
}
