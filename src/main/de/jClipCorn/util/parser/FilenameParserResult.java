package de.jClipCorn.util.parser;

import java.util.Map;

import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class FilenameParserResult {
	public final CCMovieZyklus Zyklus;
	public final String Title;
	
	public final CCMovieLanguage Language;
	public final CCMovieFormat Format;
	
	public final CCGroupList Groups;

	public final Map<Integer, String> AdditionalFiles;
		
	public FilenameParserResult(CCMovieZyklus z, String t, CCMovieLanguage l, CCMovieFormat f, CCGroupList g, Map<Integer, String> a) {
		this.Zyklus = z;
		this.Title = t;
		this.Groups = g;
		this.Format = f;
		this.Language = l;
		this.AdditionalFiles = a;
	}
}
