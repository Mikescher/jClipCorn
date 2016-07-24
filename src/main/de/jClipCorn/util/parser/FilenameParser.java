package de.jClipCorn.util.parser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.RomanNumberFormatter;

@SuppressWarnings("nls")
public class FilenameParser {
	public static FilenameParserResult parse(CCMovieList movielist, String filepath) {
		Map<Integer, String> addFiles = new HashMap<>();
		CCGroupList groups = CCGroupList.createEmpty();
		CCMovieLanguage lang = CCMovieLanguage.find(CCProperties.getInstance().PROP_DATABASE_DEFAULTPARSERLANG.getValue());
		CCMovieZyklus zyklus = null;
		CCMovieFormat format = null;
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
		CCGroupList grouplist = CCGroupList.createEmpty();
		while (mGroups.find()) {
			String group = mGroups.group();
			String groupname = group.substring(2, group.length() - 4);
			
			if (! CCGroup.REGEX_GROUP_NAME.matcher(groupname).matches()) continue;
			
			int idx = moviename.indexOf(group);
			if (idx <= 0) continue;
			if (moviename.charAt(idx-1) != ' ') continue;
			
			moviename = moviename.substring(0, idx-1) + moviename.substring(idx+group.length(), moviename.length()-1);
			
			grouplist.add(movielist, groupname);
		}
		
		// ###################  LANGUAGE  ###################
		
		String flang = moviename.substring(moviename.lastIndexOf(' ') + 1);
		String slang = "";
		if ((! flang.equals(moviename)) && (flang.charAt(0) =='[') && flang.endsWith("]")) {
			slang = flang.substring(1, flang.length() - 1);
			boolean succ = false;
			if (slang.equalsIgnoreCase(CCMovieLanguage.ENGLISH.getShortString())) {
				lang = CCMovieLanguage.ENGLISH;
				succ = true;
			} else if (slang.equalsIgnoreCase(CCMovieLanguage.FRENCH.getShortString())) {
				lang = CCMovieLanguage.FRENCH;
				succ = true;
			} else if (slang.equalsIgnoreCase(CCMovieLanguage.MUTED.getShortString())) {
				lang = CCMovieLanguage.MUTED;
				succ = true;
			}
			
			if (succ) {
				moviename = moviename.substring(0, moviename.length() - (flang.length() + 1));
			}
		}
		
		// ###################  ZYKLUS  ###################
		
		String mZyklus = "";
		String mRoman = "";
		int iRoman = -1;
		if (moviename.indexOf(" - ") >= 0) { // There is A Zyklus
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
		
		CCMovieFormat cmf = CCMovieFormat.getMovieFormat(ext);
		if (cmf != null) {
			format = cmf;
		}
		
		return new FilenameParserResult(zyklus, title, lang, format, groups, addFiles);
	}
}