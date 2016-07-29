package de.jClipCorn.util.formatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.listener.ProgressCallbackListener;

@SuppressWarnings("nls")
public class PathFormatter {
	public static final String SEPERATOR = File.separator;
	public static final char SEPERATOR_CHAR = File.separatorChar;

	public static final String SERIALIZATION_SEPERATOR = "/";
	public static final char SERIALIZATION_SEPERATOR_CHAR = '/';
	
	private final static ArrayList<Character> VALID_FILENAME_CHARS = new ArrayList<>(Arrays.asList(
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ä', 'Ö', 'Ü',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ü',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		' ', '!', '#', '&', '\'', '(', ')', '+', ',', '-', '.', ';', '=', '@', '[', ']', '^', '_', '`', '{', '}', 'ß'
	));
	
	private final static ArrayList<Character> INVALID_PATH_CHARS_SERIALIZED = new ArrayList<>(Arrays.asList('\\', '"', '<', '>', '?', '*', '|'));
	private final static ArrayList<Character> INVALID_PATH_CHARS_SYSTEM     = new ArrayList<>(Arrays.asList('"', '<', '>', '?', '*', '|'));
	
	private final static String TESTFILE_NAME = "jClipCornPermissionTest.emptyfile";
	
	private final static String REGEX_SELF = "\\<\\?self\\>"; // \<\?self\>
	private final static String REGEX_DRIVENAME = "\\<\\?vLabel=\"[^\\\"]+?\"\\>"; // <\?vLabel="[^\"]+?"\>
	private final static String REGEX_DRIVELETTER = "\\<\\?vLetter=\"[A-Z]\"\\>"; // <\?vLetter="[A-Z]"\>
	private final static String REGEX_SELFDRIVE = "\\<\\?self\\[dir\\]\\>";
	
	private final static String WILDCARD_SELF = "<?self>";
	private final static String WILDCARD_DRIVENAME = "<?vLabel=\"%s\">";
	@SuppressWarnings("unused")
	private final static String WILDCARD_DRIVELETTER = "<?vLetter=\"%s\">";
	private final static String WILDCARD_SELFDRIVE = "<?self[dir]>";
	
	private final static String WORKINGDIR = getWorkingDir();
	
	private static String getWorkingDir() {// Dafuq - wasn code (wird ja nur 1mal executed)
		try {
			return new File(".").getCanonicalPath();
		} catch (IOException e) {
			return new File("").getAbsolutePath();
		}
//		Funny old way: 
//		return new File(new PathFormatter().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().replace("%20", " ");  //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String getAbsoluteSelfDirectory() {
		if (CCProperties.getInstance() == null) return ""; // For WindowBuilder
		
		String sDir = CCProperties.getInstance().PROP_SELF_DIRECTORY.getValue();
		
		String ret = combineAndAppend(WORKINGDIR, sDir);
		
		return ret;
	}
	
	public static String getRealSelfDirectory() {
		return appendSeparator(WORKINGDIR);
	}
	
	public static String fromCCPath(String rPath) {
		if (RegExHelper.startsWithRegEx(REGEX_SELF, rPath)) {
			rPath =  RegExHelper.replace(REGEX_SELF, rPath, getAbsoluteSelfDirectory());
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVENAME, rPath)) {
			String card = RegExHelper.find(REGEX_DRIVENAME, rPath);
			String name = card.substring(10, card.length() - 2);
			char letter = DriveMap.getDriveLetter(name);
			if (letter != '#') {
				rPath = RegExHelper.replace(REGEX_DRIVENAME, rPath, letter + ":" + SEPERATOR);
			} else {
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DriveNotFound", name));
				return "";
			}
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVELETTER, rPath)) {
			String card = RegExHelper.find(REGEX_DRIVELETTER, rPath);
			char letter = card.charAt(11);
			rPath = RegExHelper.replace(REGEX_DRIVELETTER, rPath, letter + ":" + SEPERATOR);
		} else if (RegExHelper.startsWithRegEx(REGEX_SELFDRIVE, rPath)) {
			if (ApplicationHelper.isWindows()) {
				char letter = WORKINGDIR.charAt(0);
				rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, letter + ":" + SEPERATOR);
			} else if (ApplicationHelper.isUnix() || ApplicationHelper.isMac()) {
				rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, "/");
			}
		}
		
		return rPath.replace(SERIALIZATION_SEPERATOR, SEPERATOR);
	}
	
	public static String getCCPath(String aPath, boolean relative) {
		aPath = aPath.replace(SEPERATOR, SERIALIZATION_SEPERATOR);
		
		if (relative) {
			String self = getAbsoluteSelfDirectory().replace(SEPERATOR, SERIALIZATION_SEPERATOR);
			
			if (aPath.startsWith(self)) {
				return aPath.replace(self, WILDCARD_SELF);
			} else if (ApplicationHelper.isWindows() && aPath.charAt(0) == WORKINGDIR.charAt(0)) {
				return WILDCARD_SELFDRIVE.concat(aPath.substring(3));
			} else if (ApplicationHelper.isWindows() && aPath.length() > 3 && Character.isLetter(aPath.charAt(0)) && aPath.charAt(1) == ':' && (aPath.charAt(2) == SEPERATOR_CHAR || aPath.charAt(2) == SERIALIZATION_SEPERATOR_CHAR) && DriveMap.hasDriveName(aPath.charAt(0))){
				return String.format(WILDCARD_DRIVENAME, DriveMap.getDriveName(aPath.charAt(0))).concat(aPath.substring(3));
			} else {
				return aPath;
			}
		} else {
			return aPath;
		}
	}
	
	public static String getExtension(String path) {
		return path.substring(path.lastIndexOf('.') + 1, path.length()); // ganz neat, returned den ganzen string wenn kein '.' gefunden ;)
	}
	
	public static String getFilenameWithExt(String path) {
		return path.substring(path.lastIndexOf(SEPERATOR) + 1);
	}
	
	public static String getFilename(String path) {
		int liop = path.lastIndexOf('.');
		if (liop > 0) {
			return path.substring(path.lastIndexOf(SEPERATOR) + 1, liop);
		}
		return new String(path);
	}
	
	public static String getWithoutExtension(String path) {
		return path.substring(0, path.lastIndexOf('.'));
	}

	public static String getFilepath(String path) {
		int liop = path.lastIndexOf(SEPERATOR);
		if (liop > 0) {
			return path.substring(0, liop);
		}
		return new String(path);
	}
	
	public static File[] listDirectory(File d) {
		String[] ls = d.list();
		File[] result = new File[ls.length];
		for (int i = 0; i < ls.length; i++) {
			result[i] = new File(combine(d.getPath(), ls[i]));
		}
		
		return result;
	}
	
	public static void showInExplorer(File file) {
		showInExplorer(file.getAbsolutePath());
	}
	
	public static void showInExplorer(String abspath) {
		try {
			Runtime.getRuntime().exec(String.format("explorer.exe /select,\"%s\"", abspath));
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}
	
	public static boolean isUntrimmed(String s) {
		return ! s.equals(s.trim());
	}
	
	public static String fixStringToFilesystemname(String fn) {
		StringBuilder fnbuilder = new StringBuilder();
		
		for (int i = 0; i < fn.length(); i++) {
			if (VALID_FILENAME_CHARS.contains(fn.charAt(i))) {
				fnbuilder.append(fn.charAt(i));
			} else {
				String ncs = StringUtils.stripAccents(Character.toString(fn.charAt(i)));
				if (ncs.length() == 1 && VALID_FILENAME_CHARS.contains(ncs.charAt(0))) {
					fnbuilder.append(ncs.charAt(0));
				}
			}
		}
		
		String fnstring = fnbuilder.toString();
		
		while (fnstring.endsWith(" ") || fnstring.endsWith(".")) {
			fnstring = fnstring.substring(0, fnstring.length() - 1);
		}
		
		if (fnstring.isEmpty()) {
			CCLog.addError(new Exception("INTERNAL ERROR: Could not create Filename for String '" + fn + "'"));
			fnstring = "_ERROR_" + CCDate.getCurrentDate().toString() + "_";
		}
		
		return fnstring;
	}
	
	public static String rename(String fn, String newFilename) {
		return combine(getFilepath(fn), newFilename);
	}
	
	public static String forceExtension(String fn, String ext) {
		if (getExtension(fn).equalsIgnoreCase(ext)) {
			return fn;
		} else {
			return fn + '.' + ext;
		}
	}
	
	public static File forceExtension(File f, String ext) {
		return new File(forceExtension(f.getAbsolutePath(), ext));
	}
	
	public static String getCommonFolderPath(List<String> pathlist) {
		if (pathlist.isEmpty()) {
			return "";
		}
		
		for (int c = 0;;c++) {
			Character ckt = null;
			
			boolean equal = true;
			for (int elem = 0; elem < pathlist.size(); elem++) {
				if (c >= pathlist.get(elem).length()) {
					equal = false;
					break;
				}

				if (ckt == null) {
					ckt = pathlist.get(elem).charAt(c);
				} else {
					Character a = ckt;
					Character b = pathlist.get(elem).charAt(c);
					
					if (ApplicationHelper.isWindows()) {
						a = Character.toLowerCase(a);
						b = Character.toLowerCase(b);
					}
							
					if (!a.equals(b)) {
						equal = false;
						break;
					}
				}
			}
			
			if (! equal) {
				String common = pathlist.get(0).substring(0, c);
				if (common.lastIndexOf(SERIALIZATION_SEPERATOR) < 0) {
					return common;
				} else {
					return common.substring(0, common.lastIndexOf(SERIALIZATION_SEPERATOR) + 1);
				}
			}
		}
	}

	public static int countAllFiles(File path) {
		int c = 0;

		File[] list = path.listFiles();

		if (list != null) {
			for (File f : list) {
				if (f.isDirectory()) {
					c += countAllFiles(f);
				} else {
					c++;
				}
			}
		}
		
		return c;
	}
	
	private static boolean canWriteInWorkingDir() {
		File workingDir = new File(getRealSelfDirectory());
		if (!workingDir.canRead() || !workingDir.canWrite()) return false;

		File testFile = new File(getRealSelfDirectory() + TESTFILE_NAME);

		if (testFile.exists()) testFile.delete();

		if (testFile.exists()) return false;

		try {
			if (!testFile.createNewFile()) return false;
		} catch (IOException e) {
			return false; // Should hopefully not be called
		}

		if (!testFile.exists()) return false;

		if (!testFile.delete()) return false;

		return true;
	}
	
	public static void testWritePermissions() {
		if (! canWriteInWorkingDir() && ! CCProperties.getInstance().ARG_READONLY) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.NoWritePermissions"));
		}
	}
	
	public static boolean deleteFolderContent(File folder, boolean deleteParentFolder, ProgressCallbackListener pcl) {
		File[] files = folder.listFiles();
		
		if (pcl != null) pcl.setMax(files.length);
		
	    if(files != null) {
	        for(File f: files) {
	            if(f.isDirectory()) {
	            	if (! deleteFolderContent(f, true, null)) {
	            		return false;
	            	}
	            } else {
	            	if (! f.delete()) {
	            		return false;
	            	}
	            }
	            
	            if (pcl != null) pcl.step();
	        }
	    }
	    
	    if (deleteParentFolder && !folder.delete()) {
        	return false;
	    }
	    
	    return true;
	}
	
	public static String appendSeparator(String path) {
		if (path.endsWith(SEPERATOR)) return path;
		
		return path + SEPERATOR;
	}
	
	public static String prependSeparator(String path) {
		if (path.startsWith(SEPERATOR)) return path;
		
		return SEPERATOR + path;
	}
	
	public static String appendAndPrependSeparator(String path) {
		if (path.endsWith(SEPERATOR)){
			if (path.startsWith(SEPERATOR)){
				return path;
			} else {
				return SEPERATOR + path;
			}
		} else {
			if (path.startsWith(SEPERATOR)){
				return path + SEPERATOR;
			} else {
				return SEPERATOR + path + SEPERATOR;
			}
		}
	}
	
	public static String removeFinalSeparator(String path) {
		if (path.endsWith(SEPERATOR)) return path.substring(0, path.length() - SEPERATOR.length());
		
		return path;
	}
	
	public static String removeFirstSeparator(String path) {
		if (path.startsWith(SEPERATOR)) return path.substring(1);
		
		return path;
	}
	
	public static String removeFirstAndLastSeparator(String path) {
		return removeFirstSeparator(removeFinalSeparator(path));
	}
	
	public static String combine(String base, String... tail) {
		StringBuilder buildr = new StringBuilder(removeFinalSeparator(base));
		
		for (int i = 0; i < tail.length; i++) {
			String element = removeFirstAndLastSeparator(tail[i]);
			if (! element.isEmpty()) {
				buildr.append(SEPERATOR);
				buildr.append(element);
			}
		}
		
		return buildr.toString();
	}
	
	public static String combineAndAppend(String base, String... tail) {
		return combine(base, tail) + SEPERATOR;
	}

	public static boolean containsIllegalPathSymbolsInSerializedFormat(String rPath) {
		if (rPath.isEmpty()) return false;
		
		if (RegExHelper.startsWithRegEx(REGEX_SELF, rPath)) {
			rPath =  RegExHelper.replace(REGEX_SELF, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVENAME, rPath)) {
			rPath = RegExHelper.replace(REGEX_DRIVENAME, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVELETTER, rPath)) {
			rPath = RegExHelper.replace(REGEX_DRIVELETTER, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_SELFDRIVE, rPath)) {
			rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, "");
		}
		
		for (Character chr : INVALID_PATH_CHARS_SERIALIZED) {
			if (rPath.indexOf(chr) >= 0) return true;
		}

		if (rPath.indexOf(':') >= 2) return true;
		
		return false;
	}

	public static boolean containsIllegalPathSymbolsInSystemFormat(String rPath) {
		if (rPath.isEmpty()) return false;
		
		if (RegExHelper.startsWithRegEx(REGEX_SELF, rPath)) {
			rPath =  RegExHelper.replace(REGEX_SELF, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVENAME, rPath)) {
			rPath = RegExHelper.replace(REGEX_DRIVENAME, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVELETTER, rPath)) {
			rPath = RegExHelper.replace(REGEX_DRIVELETTER, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_SELFDRIVE, rPath)) {
			rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, "");
		}
		
		for (Character chr : INVALID_PATH_CHARS_SYSTEM) {
			if (rPath.indexOf(chr) >= 0) return true;
		}

		if (rPath.indexOf(':') >= 2) return true;
		
		if (ApplicationHelper.isWindows() && rPath.indexOf('/') >= 0) return true;
		if (ApplicationHelper.isUnix() && rPath.indexOf('\\') >= 0) return true;
		if (ApplicationHelper.isMac() && rPath.indexOf('\\') >= 0) return true;
		
		return false;
	}

	public static void createFolders(String filePath) {
		new File(filePath).getParentFile().mkdirs();
	}

	public static boolean validateDatabaseName(String name) {
		if (name == null) return false;

		if (name.length() == 0) return false;
		
		for (int i = 0; i < name.length(); i++) {
			char chr = name.charAt(i);

			boolean isDigit = (chr >= '0' && chr <= '9');
			boolean isUpper = (chr >= 'A' && chr <= 'Z');
			boolean isLower = (chr >= 'a' && chr <= 'z');
			boolean isSpecial = (chr == '_' || chr == '-');
			
			if (!(isDigit || isUpper || isLower || isSpecial)) return false;
		}
		
		return true;
	}
}
