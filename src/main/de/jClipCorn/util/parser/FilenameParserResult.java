package de.jClipCorn.util.parser;

import java.util.Map;

import de.jClipCorn.database.databaseElement.columnTypes.*;

public class FilenameParserResult {
	public final CCMovieZyklus Zyklus;
	public final String Title;
	
	public final CCDBLanguageList Language;
	public final CCFileFormat Format;
	
	public final CCGroupList Groups;

	public final Map<Integer, String> AdditionalFiles;
		
	public FilenameParserResult(CCMovieZyklus z, String t, CCDBLanguageList l, CCFileFormat f, CCGroupList g, Map<Integer, String> a) {
		this.Zyklus = z;
		this.Title = t;
		this.Groups = g;
		this.Format = f;
		this.Language = l;
		this.AdditionalFiles = a;
	}
}
