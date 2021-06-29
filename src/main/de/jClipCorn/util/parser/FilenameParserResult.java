package de.jClipCorn.util.parser;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.Map;

public class FilenameParserResult {
	public final CCMovieZyklus Zyklus;
	public final String Title;
	
	public final CCDBLanguageList Language;
	public final CCFileFormat Format;
	
	public final CCGroupList Groups;

	public final Map<Integer, FSPath> AdditionalFiles;
		
	public FilenameParserResult(CCMovieZyklus z, String t, CCDBLanguageList l, CCFileFormat f, CCGroupList g, Map<Integer, FSPath> a) {
		this.Zyklus = z;
		this.Title = t;
		this.Groups = g;
		this.Format = f;
		this.Language = l;
		this.AdditionalFiles = a;
	}
}
