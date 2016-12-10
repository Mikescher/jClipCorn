package de.jClipCorn.gui.frames.autofindRefrenceFrame;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.online.metadata.OnlineMetadata;

public class AutoFindRefElement {
	public final CCDatabaseElement local;
	
	public final OnlineMetadata tmdbMeta;

	public final OnlineMetadata imdbMeta;
	
	public AutoFindRefElement(CCDatabaseElement e, OnlineMetadata tmdb, OnlineMetadata imdb) {
		local = e;
		
		tmdbMeta = tmdb;
		imdbMeta = imdb;
	}
}
