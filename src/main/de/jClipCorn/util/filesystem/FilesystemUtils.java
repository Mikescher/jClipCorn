package de.jClipCorn.util.filesystem;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Either3;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.ProgressCallbackMessageStepListener;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.*;

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

	public static FSPath getAbsoluteSelfDirectory(CCProperties ccprops) {
		String sDir = ccprops.PROP_SELF_DIRECTORY.getValue();

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
				Runtime.getRuntime().exec(new String[]{batch.getAbsolutePath()});
				batch.deleteOnExit();
			}
			else if (ApplicationHelper.isKDE())
			{
				Runtime.getRuntime().exec(new String[]{"dolphin", "--select", abspath.toAbsolutePathString()});
			}
			else if (ApplicationHelper.isUnix() || ApplicationHelper.isMac())
			{
				if (Desktop.isDesktopSupported())
				{
					Desktop desktop = Desktop.getDesktop();
					if (abspath.isFile())
						desktop.browse(abspath.getParent().toURI()); // Throws
					else
						desktop.browse(abspath.toURI()); // Throws
				}
			}
		}
		catch (IOException e)
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

	public static void testWritePermissions(CCProperties ccprops) {
		var cwwd = canWriteInWorkingDir();
		if (! cwwd.Item2 && !ccprops.ARG_READONLY) {
			CCLog.addDebug("getRealSelfDirectory(): " + getRealSelfDirectory());
			CCLog.addDebug("TESTFILE_NAME:          " + TESTFILE_NAME);
			CCLog.addDebug("Exception:              " + cwwd.Item1);
			CCLog.addFatalError(LocaleBundle.getString("LogMessage.NoWritePermissions"));
		}
	}

	private static Tuple<IOException, Boolean> canWriteInWorkingDir() {
		File workingDir = getRealSelfDirectory().toFile();
		if (!workingDir.canRead() || !workingDir.canWrite()) return Tuple.Create(null, false);

		File testFile = new File(getRealSelfDirectory() + TESTFILE_NAME);

		if (testFile.exists()) testFile.delete();

		if (testFile.exists()) return Tuple.Create(null, false);

		try {
			if (!testFile.createNewFile()) return Tuple.Create(null, false);
		} catch (IOException e) {
			return Tuple.Create(e, false); // Should hopefully not be called
		}

		if (!testFile.exists()) return Tuple.Create(null, false);

		if (!testFile.delete()) return Tuple.Create(null, false);

		return Tuple.Create(null, true);
	}

	public static FSPath getTempPath() {
		return FSPath.create(System.getProperty("java.io.tmpdir"));
	}

	public static FSPath getHomePath() {
		return FSPath.create(System.getProperty("user.home"));
	}

	public static List<FSPath> findEmptyDirectories(FSPath dir, int maxDepth, boolean returnRecursivelyEmptyDirs, @Nullable ProgressCallbackMessageStepListener pcl)
	{
		var content = dir.list().toList();

		var result = new ArrayList<FSPath>();

		for (var fse : content)
		{
			if (pcl != null) pcl.step(fse.getFilenameWithExt());

			if (fse.isDirectory())
			{
				var r = findEmptyDirectories(fse, 1, maxDepth);
				if (r.getValueNumber() == 1)
				{
					result.addAll(r.get1());
				}
				else if (r.getValueNumber() == 2)
				{
					result.add(fse);
				}
				else if (r.getValueNumber() == 3)
				{
					if (returnRecursivelyEmptyDirs) result.add(fse);
				}
			}
		}

		return result;
	}

	public static boolean isEmptyDirectories(FSPath dir, int maxDepth, boolean returnRecursivelyEmptyDirs)
	{
		var r = findEmptyDirectories(dir, 1, maxDepth);
		if (r.getValueNumber() == 1)
		{
			return false;
		}
		else if (r.getValueNumber() == 2)
		{
			return true;
		}
		else if (r.getValueNumber() == 3)
		{
			return returnRecursivelyEmptyDirs;
		}
		else
		{
			return false;
		}
	}

	private static boolean isEmptyIgnorableFile(FSPath fse)
	{
		if (fse.getFilenameWithExt().equalsIgnoreCase("desktop.ini")) return true;
		if (fse.getFilenameWithExt().equalsIgnoreCase("Thumbs.db")) return true;
		if (fse.getExtension().equalsIgnoreCase("tmp")) return true;

		return false;
	}

	private static boolean isForceNonEmptyDir(FSPath fse)
	{
		if (fse.getDirectoryName().equalsIgnoreCase("@eaDir")) return true;

		return false;
	}

	private static Either3<List<FSPath>, Boolean, Boolean> findEmptyDirectories(FSPath dir, int depth, int maxDepth)
	{
		if (depth > maxDepth) return Either3.V1(new ArrayList<>()); // max depth reached - tread directory as "full"

		if (isForceNonEmptyDir(dir)) return Either3.V1(new ArrayList<>());

		var dirContent = dir.list().toList();

		var subDeletes   = new ArrayList<FSPath>();
		var emptyDirs    = new ArrayList<FSPath>();
		var recEmptyDirs = new ArrayList<FSPath>();

		var hasFiles = false;
		var hasEmptySubDirs = false;
		var hasNonEmptySubDirs = false;

		for (var fse : dirContent)
		{
			if (fse.isFile())
			{
				if (isEmptyIgnorableFile(fse)) continue;

				hasFiles = true;
			}
			else if (fse.isDirectory())
			{
				var d = findEmptyDirectories(fse, depth+1, maxDepth);
				if (d.getValueNumber() == 1)
				{
					subDeletes.addAll(d.get1());
					hasNonEmptySubDirs = true;
				}
				else if (d.getValueNumber() == 2)
				{
					emptyDirs.add(fse);
					hasEmptySubDirs = true;
				}
				else if (d.getValueNumber() == 3)
				{
					recEmptyDirs.add(fse);
					hasEmptySubDirs = true;
				}
			}
			else
			{
				hasFiles = true;
			}
		}

		if (hasFiles)
		{
			// $dir has files - return delete-able sub directories

			var result = new ArrayList<FSPath>();
			result.addAll(subDeletes);
			result.addAll(emptyDirs);
			result.addAll(recEmptyDirs);
			return Either3.V1(result);
		}

		if (hasNonEmptySubDirs)
		{
			// At least one sub-dir contains files - return delete-able sub directories

			var result = new ArrayList<FSPath>();
			result.addAll(subDeletes);
			result.addAll(emptyDirs);
			result.addAll(recEmptyDirs);
			return Either3.V1(result);
		}


		if (hasEmptySubDirs)
		{
			// $dir contains no files (direct or recursive), but contains (empty) directories
			return Either3.V3(true);
		}

		// $dir contains no files (direct or recursive), and no directories - it's truely empty
		return Either3.V2(true);
	}

	public static boolean deleteEmptyDirectory(FSPath folder) {
		FSPath[] files = folder.list().toArray(new FSPath[0]);

		for(FSPath fse: files)
		{
			if (isForceNonEmptyDir(fse)) return false;

			if(fse.isDirectory())
			{
				if (! deleteEmptyDirectory(fse)) return false;
			}
			else
			{
				if (!isEmptyIgnorableFile(fse)) return false;
				if (! fse.deleteSafe()) return false;
			}

			if (fse.exists()) return false;
		}

		if (! folder.deleteSafe()) return false;
		if (folder.exists()) return false;

		return true;
	}
}
