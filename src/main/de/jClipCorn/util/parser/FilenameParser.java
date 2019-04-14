package de.jClipCorn.util.parser;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.RomanNumberFormatter;

@SuppressWarnings("nls")
public class FilenameParser {

	private final static Pattern REGEX_EPISODE = Pattern.compile("^S(?<s>[0-9])+E(?<e>[0-9]+) - (?<n>.*)\\.(?<x>[a-zA-Z][a-zA-Z][a-zA-Z]+)$");

	public static FilenameParserResult parse(CCMovieList movielist, String filepath) {
		Map<Integer, String> addFiles = new HashMap<>();
		CCGroupList groups = CCGroupList.EMPTY;
		CCDBLanguageList lang = CCDBLanguageList.single(CCProperties.getInstance().PROP_DATABASE_DEFAULTPARSERLANG.getValue());
		CCMovieZyklus zyklus = null;
		CCFileFormat format = null;
		String title = "";
		
		String path = PathFormatter.getFilepath(filepath);
		String filename = PathFormatter.getFilename(filepath); // Filename
		String ext = PathFormatter.getExtension(filepath);
		
		if (path.equals(filepath) || filename.equals(filepath) || ext.equals(filepath)) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorParsingFN", filepath));
			return null;
		}
		
		// ###################  PART  ###################
		
		String moviename = filename; // Moviename
		
		if (filename.endsWith(" (Part 1)")) {
			moviename = moviename.substring(0, filename.indexOf(" (Part 1)"));
			
			for (int p = 2; p <= CCMovie.PARTCOUNT_MAX; p++) {
				String newFP = PathFormatter.combine(path, moviename + " (Part " + p + ")" + '.' + ext);
				File f = new File(newFP);
				if (f.exists()) {
					addFiles.put(p, newFP);
				} else {
					break;
				}
			}
		}
		
		// ###################  GROUPS  ###################
		
		Matcher mGroups = CCGroup.REGEX_GROUP_SYNTAX.matcher(moviename);
		CCGroupList grouplist = CCGroupList.EMPTY;
		while (mGroups.find()) {
			String group = mGroups.group();
			String groupname = group.substring(2, group.length() - 2);
			
			if (! CCGroup.isValidGroupName(groupname)) continue;
			
			int idx = moviename.indexOf(group);
			if (idx <= 0) continue;
			if (moviename.charAt(idx-1) != ' ') continue;
			
			if (idx+group.length() == moviename.length())
				moviename = moviename.substring(0, idx-1);
			else
				moviename = moviename.substring(0, idx-1) + moviename.substring(idx+group.length(), moviename.length());
			
			grouplist = grouplist.getAdd(movielist, groupname);
		}
		if (!grouplist.isEmpty()) groups = grouplist;
		
		// ###################  LANGUAGE  ###################
		
		String flang = moviename.substring(moviename.lastIndexOf(' ') + 1);
		if ((! flang.equals(moviename)) && (flang.charAt(0) =='[') && flang.endsWith("]")) {
			String slang = flang.substring(1, flang.length() - 1);
			
			HashSet<CCDBLanguage> dll = new HashSet<>();
			for	(String str : slang.split("\\+"))
			{
				for (CCDBLanguage itlang : CCDBLanguage.values()) {
					if (str.equalsIgnoreCase(itlang.getShortString())) {
						dll.add(itlang);
					}
				}
			}
			
			if (dll.size()>0) {
				moviename = moviename.substring(0, moviename.length() - (flang.length() + 1));
				lang = CCDBLanguageList.createDirect(dll);
			}
		}
		
		// ###################  ZYKLUS  ###################
		
		String mZyklus = "";
		String mRoman = "";
		int iRoman = -1;
		if (moviename.contains(" - ")) { // There is A Zyklus
			iRoman = 0;
			mZyklus = moviename.substring(0, moviename.indexOf(" - "));
			mRoman = mZyklus.substring(mZyklus.lastIndexOf(' ') + 1);
			if (RomanNumberFormatter.isRoman(mRoman)) { // There is a Zyklus with an Roman Number
				moviename = moviename.substring(moviename.indexOf(" - ") + 3);
				iRoman = RomanNumberFormatter.romToDec(mRoman);
				mZyklus = mZyklus.substring(0, mZyklus.lastIndexOf(' '));
			} else { //Doch kein Zyklus
				mZyklus = "";
				iRoman = -1;
			}
		}
		
		zyklus = new CCMovieZyklus(mZyklus, iRoman);
		
		// ###################  NAME  ###################
		
		if (RomanNumberFormatter.endsWithRoman(moviename)) {
			mRoman = moviename.substring(moviename.lastIndexOf(' ') + 1);
			iRoman = RomanNumberFormatter.romToDec(mRoman);
			mZyklus = moviename.substring(0, moviename.lastIndexOf(' '));
			
			title = mZyklus;
			
			zyklus = new CCMovieZyklus(mZyklus, iRoman);
		} else {
			moviename = moviename.replaceFirst(" - ", ": ");

			title = moviename;
		}
		
		// ###################  FORMAT  ###################
		
		CCFileFormat cmf = CCFileFormat.getMovieFormat(ext);
		if (cmf != null) {
			format = cmf;
		}
		
		return new FilenameParserResult(zyklus, title, lang, format, groups, addFiles);
	}

	public static EpisodeFilenameParserResult parseEpisode(String filepath) {
		Matcher m = REGEX_EPISODE.matcher(PathFormatter.getFilenameWithExt(filepath));
		if (m.matches()) {
			return new EpisodeFilenameParserResult(Integer.parseInt(m.group("s")), Integer.parseInt(m.group("e")), m.group("n"), m.group("x"));
		}
		return null;
	}
}