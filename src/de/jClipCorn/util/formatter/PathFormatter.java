package de.jClipCorn.util.formatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.listener.ProgressCallbackListener;

@SuppressWarnings("nls")
public class PathFormatter {
	private final static char BACKSLASH = '\\';
	
	private final static ArrayList<Character> VALIDFILENAMECHARS = new ArrayList<>(Arrays.asList(
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ä', 'Ö', 'Ü',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ü',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		' ', '!', '#', '&', '\'', '(', ')', '+', ',', '-', '.', ';', '=', '@', '[', ']', '^', '_', '`', '{', '}', 'ß'
	));
	
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
		
		String ret = WORKINGDIR + BACKSLASH + sDir;
		
		if (! sDir.isEmpty()) {
			ret += BACKSLASH;
		}
		
		return ret;
	}
	
	public static String getRealSelfDirectory() {
		return WORKINGDIR + BACKSLASH;
	}
	
	public static String getAbsolute(String rPath) {
		if (RegExHelper.startsWithRegEx(REGEX_SELF, rPath)) {
			return RegExHelper.replace(REGEX_SELF, rPath, getAbsoluteSelfDirectory());
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVENAME, rPath)) {
			String card = RegExHelper.find(REGEX_DRIVENAME, rPath);
			String name = card.substring(10, card.length() - 2);
			char letter = DriveMap.getDriveLetter(name);
			if (letter != '#') {
				return RegExHelper.replace(REGEX_DRIVENAME, rPath, letter + ":\\");
			} else {
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DriveNotFound", name));
				return "";
			}
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVELETTER, rPath)) {
			String card = RegExHelper.find(REGEX_DRIVELETTER, rPath);
			char letter = card.charAt(11);
			return RegExHelper.replace(REGEX_DRIVELETTER, rPath, letter + ":\\");
		} else if (RegExHelper.startsWithRegEx(REGEX_SELFDRIVE, rPath)) {
			char letter = WORKINGDIR.charAt(0);
			return RegExHelper.replace(REGEX_SELFDRIVE, rPath, letter + ":\\");
		} else {
			return rPath;
		}
	}
	
	public static String getRelative(String aPath) {
		if (aPath.startsWith(getAbsoluteSelfDirectory())) {
			return aPath.replace(getAbsoluteSelfDirectory(), WILDCARD_SELF);
		} else if (aPath.charAt(0) == WORKINGDIR.charAt(0)) {
			return WILDCARD_SELFDRIVE.concat(aPath.substring(3));
		} else if (aPath.length() > 3 && Character.isLetter(aPath.charAt(0)) && aPath.charAt(1) == ':' && aPath.charAt(2) == '\\' && DriveMap.hasDriveName(aPath.charAt(0))){
			return String.format(WILDCARD_DRIVENAME, DriveMap.getDriveName(aPath.charAt(0))).concat(aPath.substring(3));
		} else {
			return aPath;
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
	
	public static String getWithoutExtension(String path) {
		return path.substring(0, path.lastIndexOf('.'));
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
	
	public static String fixStringToFilename(String fn) {
		StringBuilder fnbuilder = new StringBuilder();
		
		for (int i = 0; i < fn.length(); i++) {
			if (VALIDFILENAMECHARS.contains(fn.charAt(i))) {
				fnbuilder.append(fn.charAt(i));
			}
		}
		
		return fnbuilder.toString();
	}
	
	public static String rename(String fn, String newFilename) {
		return getFilepath(fn) + BACKSLASH + newFilename;
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
					if (!ckt.equals(pathlist.get(elem).charAt(c))) {
						equal = false;
						break;
					}
				}
			}
			
			if (! equal) {
				String common = pathlist.get(0).substring(0, c);
				if (common.lastIndexOf('\\') < 0) {
					return common;
				} else {
					return common.substring(0, common.lastIndexOf('\\') + 1);
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
}
