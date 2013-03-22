package de.jClipCorn.util.parser;

import java.io.File;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.FileSizeFormatter;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.RomanNumberFormatter;

@SuppressWarnings("nls")
public class FilenameParser {
	private static long FSIZE_MAX_STREAM = 512L * 1024 * 1024;	 // 500 MB
	private static long FSIZE_MAX_CD = 1024L * 1024 * 1024;		 // 1 GB
	private static long FSIZE_MAX_DVD = 4L * 1024 * 1024 * 1024; // 4 GB
	
	private AddMovieFrame frame;
	
	public FilenameParser(AddMovieFrame amf) {
		this.frame  = amf;
	}
	
	public void parse(String filepath) {
		String path = PathFormatter.getFilepath(filepath);
		String name = PathFormatter.getFilename(filepath); // Filename
		String ext = PathFormatter.getExtension(filepath);
		int partCount = 1;
		
		if (path.equals(filepath) || name.equals(filepath) || ext.equals(filepath)) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorParsingFN", filepath));
			return;
		}
		
		// ###################  PART  ###################
		
		String mName = name; // Moviename
		
		if (name.endsWith(" (Part 1)")) {
			mName = mName.substring(0, name.indexOf(" (Part 1)"));
			
			for (int p = 2; p <= CCMovie.PARTCOUNT_MAX; p++) {
				String newFP = path + '\\' + mName + " (Part " + p + ")" + '.' + ext;
				if (checkFileExists(newFP)) {
					frame.setFilepath(p - 1, newFP);
					partCount++;
				} else {
					break;
				}
			}
		}
		
		// ###################  LANGUAGE  ###################
		
		String flang = mName.substring(mName.lastIndexOf(' ') + 1);
		String lang = "";
		if ((! flang.equals(mName)) && flang.startsWith("]") && flang.endsWith("[")) {
			lang = flang.substring(1, flang.length() - 1);
			boolean succ = false;
			if (lang.equalsIgnoreCase("ENG")) {
				frame.setMovieLanguage(CCMovieLanguage.ENGLISH);
				succ = true;
			} else if (lang.equalsIgnoreCase("FR")) {
				frame.setMovieLanguage(CCMovieLanguage.FRENCH);
				succ = true;
			} else if (lang.equalsIgnoreCase("MUT")) {
				frame.setMovieLanguage(CCMovieLanguage.MUTED);
				succ = true;
			} else { // auch wen "GER"
				frame.setMovieLanguage(CCMovieLanguage.GERMAN);
				succ = true;
			}
			
			if (succ) {
				mName = mName.substring(0, mName.length() - (flang.length() + 1));
			}
		} else {
			frame.setMovieLanguage(CCMovieLanguage.GERMAN);
		}
		
		// ###################  ZYKLUS  ###################
		
		String mZyklus = "";
		String mRoman = "";
		int iRoman = -1;
		if (mName.indexOf(" - ") >= 0) { // There is A Zyklus
			iRoman = 0;
			mZyklus = mName.substring(0, mName.indexOf(" - "));
			mName = mName.substring(mName.indexOf(" - ") + 3);
			mRoman = mZyklus.substring(mZyklus.lastIndexOf(' ') + 1);
			if (RomanNumberFormatter.isRoman(mRoman)) { // There is a Zyklus with an Roman Number
				iRoman = RomanNumberFormatter.romToDec(mRoman);
				mZyklus = mZyklus.substring(0, mZyklus.lastIndexOf(' '));
			}
		}
		
		frame.setZyklus(mZyklus);
		frame.setZyklusNumber(iRoman);
		
		// ###################  NAME  ###################
		
		mName = mName.replaceFirst(" - ", ": ");
		
		frame.setMovieName(mName);
		
		// ###################  SIZE  ###################
		
		long size = FileSizeFormatter.getFileSize(filepath); // from first file
		
		// ###################  QUALITY  ###################
		
		if (size <= FSIZE_MAX_STREAM) {
			frame.setQuality(CCMovieQuality.STREAM);
		} else if (size <= FSIZE_MAX_CD) {
			if (partCount == 1) {
				frame.setQuality(CCMovieQuality.ONE_CD);
			} else {
				frame.setQuality(CCMovieQuality.MULTIPLE_CD);
			}
				
		} else if (size <= FSIZE_MAX_DVD) {
			frame.setQuality(CCMovieQuality.DVD);
		} else {
			frame.setQuality(CCMovieQuality.BLURAY);
		}
		
		// ###################  FORMAT  ###################
		
		CCMovieFormat cmf = CCMovieFormat.getMovieFormat(ext);
		if (cmf != null) {
			frame.setMovieFormat(cmf);
		}
	}
	
	private static boolean checkFileExists(String filepath) {
		return new File(filepath).exists();
	}
}