package de.jClipCorn.util.filesystem;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.RegExHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class CCPath implements IPath {
	public static final String SEPERATOR = "/";
	public static final char SEPERATOR_CHAR = '/';

	private final static String REGEX_SELF        = "\\<\\?self\\>";                   // <?self>                 <\?self\>
	private final static String REGEX_DRIVENAME   = "\\<\\?vLabel=\"[^\\\"]+?\"\\>";   // <?vLabel="...">         <\?vLabel="[^\"]+?"\>
	private final static String REGEX_DRIVELETTER = "\\<\\?vLetter=\"[A-Z]\"\\>";      // <?vLetter="...">        <\?vLetter="[A-Z]"\>
	private final static String REGEX_SELFDRIVE   = "\\<\\?self\\[dir\\]\\>";          // <?self[dir]>            <\?self\[dir\]\>
	private final static String REGEX_NETDRIVE    = "\\<\\?vNetwork=\"[^\"]+?\"\\>";   // <?vNetwork="...">       <\?vNetwork="[^\"]+?"\>
	private final static String REGEX_VARIABLE    = "\\<\\?[^\\\"]+\\]\\>";            // <?[...]>                <\?[^\"]+\]\>

	private final static String WILDCARD_SELF        = "<?self>";
	private final static String WILDCARD_DRIVENAME   = "<?vLabel=\"%s\">";
	private final static String WILDCARD_SELFDRIVE   = "<?self[dir]>";
	private final static String WILDCARD_NETDRIVE    = "<?vNetwork=\"%s\">";
	private final static String WILDCARD_VARIABLE    = "<?[%s]>";

	private final static List<Character> INVALID_PATH_CHARS = Arrays.asList('\\', '"', '<', '>', '?', '*', '|');

	public final static CCPath Empty = CCPath.create(Str.Empty);

	private final String _path;

	private CCPath(@NotNull String path) {
		_path = path;
	}

	public static CCPath create(String v) {
		if (Str.isNullOrWhitespace(v)) return Empty;
		if (v.endsWith(SEPERATOR)) v = v.substring(0, v.length() - SEPERATOR.length());
		return new CCPath(v);
	}

	public static CCPath createFromFSPath(FSPath p) {
		if (p.isEmpty()) return CCPath.Empty;

		var aPath = p.toString();

		aPath = aPath.replace(FSPath.SEPERATOR, CCPath.SEPERATOR);

		if (CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()) {
			String self = FilesystemUtils.getAbsoluteSelfDirectory().toString().replace(FSPath.SEPERATOR, CCPath.SEPERATOR);

			// <?vNetwork="...">
			if (CCProperties.getInstance().PROP_PATHSYNTAX_NETDRIVE.getValue() && FilesystemUtils.isWinDriveIdentifiedPath(aPath) && DriveMap.hasDriveUNC(aPath.charAt(0))) {
				String unc = DriveMap.getDriveUNC(aPath.charAt(0));
				String cc = String.format(WILDCARD_NETDRIVE, unc).concat(aPath.substring(3));
				return insertCCPathVariables(cc);
			}

			// <?self>
			if (CCProperties.getInstance().PROP_PATHSYNTAX_SELF.getValue() && !Str.isNullOrWhitespace(self) && aPath.startsWith(self)) {
				String cc = WILDCARD_SELF.concat(aPath.substring(self.length()));
				return insertCCPathVariables(cc);
			}

			// <?self[dir]>
			if (CCProperties.getInstance().PROP_PATHSYNTAX_SELFDIR.getValue() && ApplicationHelper.isWindows() && aPath.charAt(0) == FilesystemUtils.getAbsoluteSelfDirectory().toString().charAt(0)) {
				String cc = WILDCARD_SELFDRIVE.concat(aPath.substring(3));
				return insertCCPathVariables(cc);
			}

			// <?vLabel="...">
			if (CCProperties.getInstance().PROP_PATHSYNTAX_DRIVELABEL.getValue() && FilesystemUtils.isWinDriveIdentifiedPath(aPath) && DriveMap.hasDriveLabel(aPath.charAt(0))){
				String cc = String.format(WILDCARD_DRIVENAME, DriveMap.getDriveLabel(aPath.charAt(0))).concat(aPath.substring(3));
				return insertCCPathVariables(cc);
			}

			return insertCCPathVariables(aPath);
		}

		return CCPath.create(aPath);
	}

	private static CCPath insertCCPathVariables(String path) {
		if (ApplicationHelper.isWindows())
		{
			for (PathSyntaxVar psv : CCProperties.getInstance().getActivePathVariables()) {
				if (path.toLowerCase().startsWith(psv.Value.toString().toLowerCase()))
				{
					path = String.format(WILDCARD_VARIABLE, psv.Key) + path.substring(psv.Value.toString().length());
					return CCPath.create(path);
				}
			}
		}
		else
		{
			for (PathSyntaxVar psv : CCProperties.getInstance().getActivePathVariables()) {
				if (path.startsWith(psv.Value.toString()))
				{
					path = String.format(WILDCARD_VARIABLE, psv.Key) + path.substring(psv.Value.toString().length());
					return CCPath.create(path);
				}
			}
		}
		return CCPath.create(path);
	}

	public FSPath toFSPath() {
		if (isEmpty()) return FSPath.Empty;

		var rPath = _path;

		if (RegExHelper.startsWithRegEx(REGEX_VARIABLE, rPath)) {
			String card = RegExHelper.find(REGEX_VARIABLE, rPath);
			String keyident = card.substring(3, card.length() - 2);
			for (PathSyntaxVar psv : CCProperties.getInstance().getActivePathVariables()) {
				if (psv.Key.equals(keyident)) {
					rPath =  RegExHelper.replace(REGEX_VARIABLE, rPath, psv.Value.toString());
					break;
				}
			}
		}

		if (RegExHelper.startsWithRegEx(REGEX_SELF, rPath)) {
			rPath =  RegExHelper.replace(REGEX_SELF, rPath, FilesystemUtils.getAbsoluteSelfDirectory() + FSPath.SEPERATOR);
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVENAME, rPath)) {
			String card = RegExHelper.find(REGEX_DRIVENAME, rPath);
			String name = card.substring(10, card.length() - 2);
			char letter = DriveMap.getDriveLetterByLabel(name);
			if (letter != '#') {
				rPath = RegExHelper.replace(REGEX_DRIVENAME, rPath, letter + ":" + FSPath.SEPERATOR);
			} else {
				DriveMap.conditionalRescan();
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DriveNotFound", name));
				return FSPath.Empty;
			}
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVELETTER, rPath)) {
			String card = RegExHelper.find(REGEX_DRIVELETTER, rPath);
			char letter = card.charAt(11);
			rPath = RegExHelper.replace(REGEX_DRIVELETTER, rPath, letter + ":" + FSPath.SEPERATOR);
		} else if (RegExHelper.startsWithRegEx(REGEX_SELFDRIVE, rPath)) {
			if (ApplicationHelper.isWindows()) {
				char letter = FilesystemUtils.getRealSelfDirectory().toString().charAt(0);
				rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, letter + ":" + FSPath.SEPERATOR);
			} else if (ApplicationHelper.isUnix() || ApplicationHelper.isMac()) {
				rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, "/");
			}
		} else if (RegExHelper.startsWithRegEx(REGEX_NETDRIVE, rPath)) {
			String card = RegExHelper.find(REGEX_NETDRIVE, rPath);
			String name = card.substring(12, card.length() - 2);
			char letter = DriveMap.getDriveLetterByUNC(name);
			if (letter != '#') {
				rPath = RegExHelper.replace(REGEX_NETDRIVE, rPath, letter + ":" + FSPath.SEPERATOR);
			} else {
				DriveMap.conditionalRescan();
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DriveNotFound", name));
				rPath = RegExHelper.replace(REGEX_NETDRIVE, rPath, name + FSPath.SEPERATOR);
			}
		}

		rPath = rPath.replace(CCPath.SEPERATOR, FSPath.SEPERATOR);

		return FSPath.createAndNormalize(rPath);
	}

	public boolean isEmpty() {
		return Str.isNullOrWhitespace(_path);
	}

	public CCPath append(String... tail) {
		if (tail.length == 0) return this;

		var path = _path;
		if (path.endsWith(SEPERATOR)) path = path.substring(0, path.length() - SEPERATOR.length());

		StringBuilder buildr = new StringBuilder(path);

		for (String element : tail) {
			if (element.startsWith(SEPERATOR)) element = element.substring(1);
			if (element.endsWith(SEPERATOR)) element = element.substring(0, element.length() - SEPERATOR.length());

			if (!element.isEmpty()) {
				buildr.append(SEPERATOR);
				buildr.append(element);
			}
		}

		return create(buildr.toString());
	}

	public String getExtension() {
		// returns extension without leading dot

		var p = _path;
		if (p.endsWith(SEPERATOR)) p = p.substring(0, p.length() - SEPERATOR.length());

		var sep = p.lastIndexOf(SEPERATOR);

		var idx = _path.lastIndexOf('.');
		if (idx < 0 || (sep > 0 && idx < sep)) return Str.Empty;

		return _path.substring(idx + 1); // ganz neat, returned den ganzen string wenn kein '.' gefunden ;)
	}

	public String getFilenameWithoutExt() {
		int liop = _path.lastIndexOf('.');
		if (liop > 0) {
			return _path.substring(_path.lastIndexOf(SEPERATOR) + 1, liop);
		}
		return _path.substring(_path.lastIndexOf(SEPERATOR) + 1);
	}

	public String getFilenameWithExt() {
		return _path.substring(_path.lastIndexOf(SEPERATOR) + 1);
	}

	public CCPath getParent() {
		return getParent(1);
	}

	public CCPath getParent(int c) {
		var p = _path;
		if (p.endsWith(SEPERATOR)) p = p.substring(0, p.length() - SEPERATOR.length());

		for (int i = 0; i < c; i++) {
			int idx = p.lastIndexOf(SEPERATOR_CHAR);
			if (idx > 0) p = p.substring(0, idx);
		}

		return CCPath.create(p);
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return isEqual(this, (CCPath) o);
	}

	public static boolean isEqual(CCPath a, CCPath b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		return Str.equals(a._path, b._path);
	}

	public static boolean equalsIgnoreCase(CCPath a, CCPath b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		return Str.equalsIgnoreCase(a._path, b._path);
	}

	@Override
	public int hashCode() {
		return _path.hashCode();
	}

	@Override
	public String toString() {
		return _path;
	}

	public static CCPath getCommonPath(List<CCPath> pathlist) {
		if (pathlist.isEmpty()) return Empty;

		for (int c = 0;;c++)
		{
			Character ckt = null;

			boolean equal = true;
			for (CCPath ccPath : pathlist)
			{
				if (c >= ccPath._path.length()) { equal = false; break; }

				if (ckt == null) {
					ckt = ccPath._path.charAt(c);
				} else {
					var a = ckt;
					var b = ccPath._path.charAt(c);

					if (ApplicationHelper.isWindows()) {
						a = Character.toLowerCase(a);
						b = Character.toLowerCase(b);
					}

					if (!a.equals(b)) { equal = false; break; }
				}
			}

			if (! equal) {
				String common = pathlist.get(0)._path.substring(0, c);

				var lastidx = common.lastIndexOf(SEPERATOR);
				if (lastidx > 0) return CCPath.create(common.substring(0, lastidx + 1));

				var nextidx = pathlist.get(0)._path.indexOf(SEPERATOR, c);
				if (nextidx > 0) return CCPath.create(common.substring(0, nextidx + 1));

				return CCPath.Empty;
			}
		}
	}

	public static boolean containsIllegalSymbols(CCPath p) {
		if (p.isEmpty()) return false;

		var rPath = p._path;

		if (RegExHelper.startsWithRegEx(REGEX_SELF, rPath)) {
			rPath =  RegExHelper.replace(REGEX_SELF, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVENAME, rPath)) {
			rPath = RegExHelper.replace(REGEX_DRIVENAME, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_DRIVELETTER, rPath)) {
			rPath = RegExHelper.replace(REGEX_DRIVELETTER, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_SELFDRIVE, rPath)) {
			rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_VARIABLE, rPath)) {
			rPath = RegExHelper.replace(REGEX_VARIABLE, rPath, "");
		} else if (RegExHelper.startsWithRegEx(REGEX_NETDRIVE, rPath)) {
			rPath = RegExHelper.replace(REGEX_NETDRIVE, rPath, "");
		}

		for (Character chr : INVALID_PATH_CHARS) {
			if (rPath.indexOf(chr) >= 0) return true;
		}

		if (rPath.indexOf(':') >= 2) return true;

		return false;
	}

	public static boolean isNullOrEmpty(CCPath p) {
		return p == null || p.isEmpty();
	}
}
