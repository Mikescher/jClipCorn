package de.jClipCorn.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;

@SuppressWarnings("nls")
public class PathFormatter {
	private final static char BACKSLASH = '\\';
	
	private final static ArrayList<Character> filenameChars = new ArrayList<>(Arrays.asList(
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '�', '�', '�',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '�', '�', '�',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		' ', '!', '#', '&', '\'', '(', ')', '+', ',', '-', '.', ';', '=', '@', '[', ']', '^', '_', '`', '{', '}', '�'
	));
	
	private final static String REGEX_SELF = "\\<\\?self\\>"; // \<\?self\>
	private final static String REGEX_DRIVENAME = "\\<\\?vLabel=\"[^\\\"]+?\"\\>"; // <\?vLabel="[^\"]+?"\>
	private final static String REGEX_DRIVELETTER = "\\<\\?vLetter=\"[A-Z]\"\\>"; // <\?vLetter="[A-Z]"\>
	private final static String REGEX_SELFDRIVE = "\\<\\?self\\[dir\\]\\>";
	
	private final static String WILDCARD_SELF = "<?self>";
	private final static String WILDCARD_DRIVENAME = "<?vLabel=\"%s\">";
	@SuppressWarnings("unused")
	private final static String WILDCARD_DRIVELETTER = "<?vLetter=\"%s\">";
	private final static String WILDCARD_SELFDRIVE = "<?self[dir]>";
	
	private final static String workingDir = getWorkingDir();
	
	private static String getWorkingDir() {// Dafuq - wasn code (wird ja nur 1mal executed)
		return new File(new PathFormatter().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().replace("%20", " ");  //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String getAbsoluteSelfDirectory() {
		String sDir = CCProperties.getInstance().PROP_SELF_DIRECTORY.getValue();
		
		String ret = workingDir + BACKSLASH + sDir;
		
		if (! sDir.isEmpty()) {
			ret += BACKSLASH;
		}
		
		return ret;
	}
	
	public static String getRealSelfDirectory() {
		return workingDir + BACKSLASH;
	}
	
	public static String getAbsolute(String rPath) {
		if (RegExHelper.startsWithRegEx(REGEX_SELF, rPath)) {
			return RegExHelper.replace(REGEX_SELF, rPath, getAbsoluteSelfDirectory());
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVENAME, rPath)) {
			String card = RegExHelper.find(REGEX_DRIVENAME, rPath);
			String name = card.substring(10, card.length()-2);
			char letter = DriveMap.getDriveLetter(name);
			if (letter != '#') {
				return RegExHelper.replace(REGEX_DRIVENAME, rPath, letter + ":\\");
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.DriveNotFound", name));
				return "";
			}
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVELETTER, rPath)) {
			String card = RegExHelper.find(REGEX_DRIVELETTER, rPath);
			char letter = card.charAt(11);
			return RegExHelper.replace(REGEX_DRIVELETTER, rPath, letter + ":\\");
		} else if (RegExHelper.startsWithRegEx(REGEX_SELFDRIVE, rPath)) {
			char letter = workingDir.charAt(0);
			return RegExHelper.replace(REGEX_SELFDRIVE, rPath, letter + ":\\");
		} else {
			return rPath;
		}
	}
	
	public static String getRelative(String aPath) {
		if (aPath.startsWith(getAbsoluteSelfDirectory())) {
			return aPath.replace(getAbsoluteSelfDirectory(), WILDCARD_SELF);
		} else if (aPath.charAt(0) == workingDir.charAt(0)) {
			return WILDCARD_SELFDRIVE.concat(aPath.substring(3));
		} else {
			return String.format(WILDCARD_DRIVENAME, DriveMap.getDriveName(aPath.charAt(0))).concat(aPath.substring(3));
		}
	}
	
	public static String getExtension(String path) {
		return path.substring(path.lastIndexOf('.') + 1, path.length()); // ganz neat, returned den ganzen string wenn kein '.' gefunden ;)
	}
	
	public static String getFilenameWithExt(String path) {
		return path.substring(path.lastIndexOf(BACKSLASH) + 1);
	}
	
	public static String getFilename(String path) {
		int liop = path.lastIndexOf('.');
		if (liop > 0) {
			return path.substring(path.lastIndexOf(BACKSLASH) + 1, liop);
		}
		return new String(path);
	}

	public static String getFilepath(String path) {
		int liop = path.lastIndexOf(BACKSLASH);
		if (liop > 0) {
			return path.substring(0, liop);
		}
		return new String(path);
	}
	
	public static File[] listDirectory(File d) {
		String[] ls = d.list();
		File[] result = new File[ls.length];
		for (int i = 0; i < ls.length; i++) {
			result[i] = new File(d.getPath() + BACKSLASH + ls[i]);
		}
		
		return result;
	}
	
	public static void showInExplorer(String abspath) {
		try {
			Runtime.getRuntime().exec(String.format("explorer.exe /select,\"%s\"", abspath)); //TODO �ffnet zwar - selected aber nicht :'( // Manchmal aber doch // Vielleicht doch immer ??
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}
	
	public static boolean isUntrimmed(String s) {
		return ! s.equals(s.trim());
	}
	
	public static String fixStringToFilename(String fn) {
		String r = "";
		
		for (int i = 0; i < fn.length(); i++) {
			if (filenameChars.contains(fn.charAt(i))) {
				r = r + fn.charAt(i);
			}
		}
		
		return r;
	}
	
	public static String rename(String fn, String newFilename) {
		return getFilepath(fn) + BACKSLASH + newFilename;
	}
}
