package de.jClipCorn.util.filesystem;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("nls")
public class FilesystemUtils {
	private final static FSPath WORKINGDIR = FSPath.createAndNormalize(getWorkingDir());

	private final static ArrayList<Character> VALID_FILENAME_CHARS = new ArrayList<>(Arrays.asList(
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Ä', 'Ö', 'Ü',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'ä', 'ö', 'ü',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		' ', '!', '#', '&', '\'', '(', ')', '+', ',', '-', '.', ';', '=', '@', '[', ']', '^', '_', '`', '{', '}', 'ß'
	));

	private final static String TESTFILE_NAME = "jClipCornPermissionTest.emptyfile";

    private static final Map<Character, Character> FILENAME_REPLACEMENT_CHARS;
    static
    {
    	FILENAME_REPLACEMENT_CHARS = new HashMap<>();
    	FILENAME_REPLACEMENT_CHARS.put('×', 'x');  // MULTIPLICATION SIGN  -->      LATIN SMALL LETTER X
    	FILENAME_REPLACEMENT_CHARS.put('—', '-');  // EM DASH              -->      HYPHEN-MINUS
    	FILENAME_REPLACEMENT_CHARS.put('–', '-');  // EN DASH              -->      HYPHEN-MINUS
    }


	private static String getWorkingDir() {
		try {
			return new File(".").getCanonicalPath();
		} catch (IOException e) {
			return new File("").getAbsolutePath();
		}
//		Funny old way:
//		return new File(new PathFormatter().getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent().replace("%20", " ");  //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static FSPath getRealSelfDirectory() {
		return WORKINGDIR;
	}

	public static FSPath getAbsoluteSelfDirectory() {
		if (CCProperties.getInstance() == null) return FSPath.Empty; // For WindowBuilder

		String sDir = CCProperties.getInstance().PROP_SELF_DIRECTORY.getValue();

		return WORKINGDIR.append(sDir);
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
				} else if (FILENAME_REPLACEMENT_CHARS.containsKey(fn.charAt(i))) {
					fnbuilder.append(FILENAME_REPLACEMENT_CHARS.get(fn.charAt(i)));
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

	public static String combineWithFSPathSeparator(String base, String... tail) {
		StringBuilder buildr = new StringBuilder(removeFinalFSPathSeparator(base));

		for (int i = 0; i < tail.length; i++) {
			String element = removeFirstAndLastFSPathSeparator(tail[i]);
			if (! element.isEmpty()) {
				buildr.append(FSPath.SEPERATOR);
				buildr.append(element);
			}
		}

		return buildr.toString();
	}

	public static String removeFinalFSPathSeparator(String path) {
		if (path.endsWith(FSPath.SEPERATOR)) return path.substring(0, path.length() - FSPath.SEPERATOR.length());

		return path;
	}

	public static String removeFirstFSPathSeparator(String path) {
		if (path.startsWith(FSPath.SEPERATOR)) return path.substring(1);

		return path;
	}

	public static String removeFirstAndLastFSPathSeparator(String path) {
		return removeFirstFSPathSeparator(removeFinalFSPathSeparator(path));
	}

	public static void showInExplorer(FSPath abspath)
	{
		try
		{
			if (ApplicationHelper.isWindows())
			{
				// [JRE Bug] https://stackoverflow.com/questions/6686592/runtime-exec-on-argument-containing-multiple-spaces

				final File batch = File.createTempFile( "jClipCorn_exec_", ".bat" );
				try(var ps = new PrintStream(batch, "IBM850")) { ps.println( "explorer.exe /select,\"" + abspath + '"'); }
				Runtime.getRuntime().exec( batch.getAbsolutePath());
				batch.deleteOnExit();
			}
			else if (ApplicationHelper.isUnix() || ApplicationHelper.isMac())
			{
				if (Desktop.isDesktopSupported())
				{
					Desktop desktop = Desktop.getDesktop();
					desktop.browse(new URI("File://" + abspath)); // Throws
				}
			}
		}
		catch (IOException | URISyntaxException e)
		{
			CCLog.addError(e);
		}
	}

	public static boolean deleteFolderContent(FSPath folder, boolean deleteParentFolder, ProgressCallbackListener pcl) {
		FSPath[] files = folder.list().toArray(new FSPath[0]);

		if (pcl != null) pcl.setMax(files.length);

		for(FSPath f: files) {
			if(f.isDirectory()) {
				if (! deleteFolderContent(f, true, null)) return false;
			} else {
				if (! f.deleteSafe()) return false;
			}
			if (pcl != null) pcl.step();
		}

		if (deleteParentFolder && !folder.deleteSafe()) return false;

		return true;
	}

	public static boolean isWinDriveIdentifiedPath(String aPath) {
		if (! ApplicationHelper.isWindows()) return false;
		if (aPath.length() <= 3) return false;
		if (! Character.isLetter(aPath.charAt(0))) return false;
		if (aPath.charAt(1) != ':') return false;
		if (aPath.charAt(2) != FSPath.SEPERATOR_CHAR && aPath.charAt(2) != CCPath.SEPERATOR_CHAR) return false;
		return true;
	}

	public static void testWritePermissions() {
		if (! canWriteInWorkingDir() && !CCProperties.getInstance().ARG_READONLY) {
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.NoWritePermissions"));
		}
	}

	private static boolean canWriteInWorkingDir() {
		File workingDir = getRealSelfDirectory().toFile();
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

	public static FSPath getTempPath() {
		return FSPath.create(System.getProperty("java.io.tmpdir"));
	}

	public static FSPath getHomePath() {
		return FSPath.create(System.getProperty("user.home"));
	}
}
