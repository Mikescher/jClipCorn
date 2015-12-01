package de.jClipCorn.util.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.RomanNumberFormatter;

@SuppressWarnings("nls")
public class FilenameParser {
	private AddMovieFrame frame;
	
	public FilenameParser(AddMovieFrame amf) {
		this.frame  = amf;
	}
	
	public void parse(String filepath) {
		List<File> files = new ArrayList<>();
		files.add(new File(filepath));
		
		String path = PathFormatter.getFilepath(filepath);
		String filename = PathFormatter.getFilename(filepath); // Filename
		String ext = PathFormatter.getExtension(filepath);
		
		if (path.equals(filepath) || filename.equals(filepath) || ext.equals(filepath)) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorParsingFN", filepath));
			return;
		}
		
		// ###################  PART  ###################
		
		String moviename = filename; // Moviename
		
		if (filename.endsWith(" (Part 1)")) {
			moviename = moviename.substring(0, filename.indexOf(" (Part 1)"));
			
			for (int p = 2; p <= CCMovie.PARTCOUNT_MAX; p++) {
				String newFP = PathFormatter.combine(path, moviename + " (Part " + p + ")" + '.' + ext);
				File f = new File(newFP);
				if (f.exists()) {
					files.add(f);
					frame.setFilepath(p - 1, newFP);
				} else {
					break;
				}
			}
		}
		
		// ###################  LANGUAGE  ###################
		
		String flang = moviename.substring(moviename.lastIndexOf(' ') + 1);
		String lang = "";
		if ((! flang.equals(moviename)) && (flang.charAt(0) =='[') && flang.endsWith("]")) {
			lang = flang.substring(1, flang.length() - 1);
			boolean succ = false;
			if (lang.equalsIgnoreCase(CCMovieLanguage.ENGLISH.getShortString())) {
				frame.setMovieLanguage(CCMovieLanguage.ENGLISH);
				succ = true;
			} else if (lang.equalsIgnoreCase(CCMovieLanguage.FRENCH.getShortString())) {
				frame.setMovieLanguage(CCMovieLanguage.FRENCH);
				succ = true;
			} else if (lang.equalsIgnoreCase(CCMovieLanguage.MUTED.getShortString())) {
				frame.setMovieLanguage(CCMovieLanguage.MUTED);
				succ = true;
			} else { // auch wen "GER"
				frame.setMovieLanguage(CCMovieLanguage.GERMAN);
				succ = true;
			}
			
			if (succ) {
				moviename = moviename.substring(0, moviename.length() - (flang.length() + 1));
			}
		} else {
			frame.setMovieLanguage(CCMovieLanguage.GERMAN);
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
		
		frame.setZyklus(mZyklus);
		frame.setZyklusNumber(iRoman);
		
		// ###################  NAME  ###################
		
		if (RomanNumberFormatter.endsWithRoman(moviename)) {
			mRoman = moviename.substring(moviename.lastIndexOf(' ') + 1);
			iRoman = RomanNumberFormatter.romToDec(mRoman);
			mZyklus = moviename.substring(0, moviename.lastIndexOf(' '));
			
			frame.setMovieName(mZyklus);
			frame.setZyklus(mZyklus);
			frame.setZyklusNumber(iRoman);
		} else {
			moviename = moviename.replaceFirst(" - ", ": ");
			
			frame.setMovieName(moviename);
		}
		
		// ###################  FORMAT  ###################
		
		CCMovieFormat cmf = CCMovieFormat.getMovieFormat(ext);
		if (cmf != null) {
			frame.setMovieFormat(cmf);
		}
	}
}