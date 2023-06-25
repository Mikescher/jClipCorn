package de.jClipCorn.util.filesystem;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.ICCPropertySource;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.RegExHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCPath implements IPath, Comparable<CCPath> {
	public final static CCPath Empty = new CCPath(Str.Empty);

	public static final String SEPERATOR = "/";
	public static final char SEPERATOR_CHAR = '/';

	private final static Pattern REGEX_SELF        = Pattern.compile("\\<\\?self\\>");                   // <?self>                 <\?self\>
	private final static Pattern REGEX_DRIVENAME   = Pattern.compile("\\<\\?vLabel=\"[^\\\"]+?\"\\>");   // <?vLabel="...">         <\?vLabel="[^\"]+?"\>
	private final static Pattern REGEX_DRIVELETTER = Pattern.compile("\\<\\?vLetter=\"[A-Z]\"\\>");      // <?vLetter="...">        <\?vLetter="[A-Z]"\>
	private final static Pattern REGEX_SELFDRIVE   = Pattern.compile("\\<\\?self\\[dir\\]\\>");          // <?self[dir]>            <\?self\[dir\]\>
	private final static Pattern REGEX_NETDRIVE    = Pattern.compile("\\<\\?vNetwork=\"[^\"]+?\"\\>");   // <?vNetwork="...">       <\?vNetwork="[^\"]+?"\>
	private final static Pattern REGEX_VARIABLE    = Pattern.compile("\\<\\?[^\\\"]+\\]\\>");            // <?[...]>                <\?[^\"]+\]\>

	private final static Pattern[] REGEX_ALL = {
		REGEX_SELF,
		REGEX_DRIVENAME,
		REGEX_DRIVELETTER,
		REGEX_SELFDRIVE,
		REGEX_NETDRIVE,
		REGEX_VARIABLE,
	};

	private final static String WILDCARD_SELF        = "<?self>";
	private final static String WILDCARD_DRIVENAME   = "<?vLabel=\"%s\">";
	private final static String WILDCARD_SELFDRIVE   = "<?self[dir]>";
	private final static String WILDCARD_NETDRIVE    = "<?vNetwork=\"%s\">";
	private final static String WILDCARD_VARIABLE    = "<?[%s]>";

	private final static List<Character> INVALID_PATH_CHARS = Arrays.asList('\\', '"', '<', '>', '?', '*', '|');

	private final String _path;

	private CCPath(@NotNull String path) {
		_path = path;
	}

	public static CCPath create(String v) {
		if (Str.isNullOrWhitespace(v)) return Empty;
		if (v.endsWith(SEPERATOR) && !(!ApplicationHelper.isWindows() && Str.equals(v, "/"))) v = v.substring(0, v.length() - SEPERATOR.length());
		return new CCPath(v);
	}

	public static CCPath createFromFSPath(FSPath p, ICCPropertySource ccps) {
		return createFromFSPath(p, Opt.empty(), ccps);
	}

	public static CCPath createFromFSPath(FSPath p, Opt<Boolean> makeRelative, ICCPropertySource ccps) {
		if (p.isEmpty()) return CCPath.Empty;

		var ccprops = ccps.ccprops();

		var aPath = p.toString();

		aPath = aPath.replace(FSPath.SEPERATOR, CCPath.SEPERATOR);

		var mkBase   = makeRelative.orElse(ccprops.PROP_ADD_MOVIE_RELATIVE_AUTO.getValue());
		var mkNet    = makeRelative.orElse(ccprops.PROP_PATHSYNTAX_NETDRIVE.getValue());
		var mkSelf   = makeRelative.orElse(ccprops.PROP_PATHSYNTAX_SELF.getValue());
		var mkSDir   = makeRelative.orElse(ccprops.PROP_PATHSYNTAX_SELFDIR.getValue());
		var mkDLabel = makeRelative.orElse(ccprops.PROP_PATHSYNTAX_DRIVELABEL.getValue());
		var mkVars   = makeRelative.orElse(ccprops.getActivePathVariables().size()>0);

		if (! mkBase) return CCPath.create(aPath);

		// <?[...]">
		if (mkVars) aPath = insertCCPathVariables(aPath, ccprops);

		// <?vNetwork="...">
		if (mkNet && FilesystemUtils.isWinDriveIdentifiedPath(aPath) && ccprops.getDriveMap().hasDriveUNC(aPath.charAt(0))) {
			String unc = ccprops.getDriveMap().getDriveUNC(aPath.charAt(0));
			aPath = String.format(WILDCARD_NETDRIVE, unc).concat(aPath.substring(3));
		}

		// <?self>
		String self = FilesystemUtils.getAbsoluteSelfDirectory(ccprops).toString().replace(FSPath.SEPERATOR, CCPath.SEPERATOR);
		if (mkSelf && !Str.isNullOrWhitespace(self) && aPath.startsWith(self)) {
			aPath = WILDCARD_SELF.concat(aPath.substring(self.length()));
		}

		// <?self[dir]>
		if (mkSDir && ApplicationHelper.isWindows() && aPath.charAt(0) == FilesystemUtils.getAbsoluteSelfDirectory(ccprops).toString().charAt(0)) {
			aPath = WILDCARD_SELFDRIVE.concat(aPath.substring(3));
		}

		// <?vLabel="...">
		if (mkDLabel && FilesystemUtils.isWinDriveIdentifiedPath(aPath) && ccprops.getDriveMap().hasDriveLabel(aPath.charAt(0))){
			aPath = String.format(WILDCARD_DRIVENAME, ccprops.getDriveMap().getDriveLabel(aPath.charAt(0))).concat(aPath.substring(3));
		}

		// <?[...]">
		if (mkVars) aPath = insertCCPathVariables(aPath, ccprops);

		return CCPath.create(aPath);
	}

	public static CCPath createFromComponents(List<String> components) {
		return Empty.append(components.toArray(new String[0]));
	}

	private static String insertCCPathVariables(String path, CCProperties ccprops) {

		for (PathSyntaxVar psv : ccprops.getActivePathVariables()) {

			var repl = psv.Value.toStringWithTraillingSeparator();

			var doinsert = ApplicationHelper.isWindows() ? path.toLowerCase().startsWith(repl.toLowerCase()) : path.startsWith(repl) ;

			if (doinsert) return String.format(WILDCARD_VARIABLE, psv.Key) + path.substring(repl.length());
		}

		return path;
	}

	public FSPath toFSPath(ICCPropertySource ccps) {
		if (isEmpty()) return FSPath.Empty;

		var ccprops = ccps.ccprops();

		var rPath = _path;

		if (RegExHelper.startsWithRegEx(REGEX_VARIABLE, rPath))
		{
			String card = RegExHelper.find(REGEX_VARIABLE, rPath);
			String keyident = card.substring(3, card.length() - 2);
			for (PathSyntaxVar psv : ccprops.getActivePathVariables()) {
				if (psv.Key.equals(keyident)) {
					rPath =  RegExHelper.replace(REGEX_VARIABLE, rPath, psv.Value.toStringWithTraillingSeparator());
					break;
				}
			}
		}

		if (RegExHelper.startsWithRegEx(REGEX_SELF, rPath))
		{
			rPath = RegExHelper.replace(REGEX_SELF, rPath, FilesystemUtils.getAbsoluteSelfDirectory(ccprops) + FSPath.SEPERATOR);
		}
		else if (RegExHelper.startsWithRegEx(REGEX_DRIVENAME, rPath))
		{
			String card = RegExHelper.find(REGEX_DRIVENAME, rPath);
			String name = card.substring(10, card.length() - 2);
			char letter = ccprops.getDriveMap().getDriveLetterByLabel(name);
			if (letter != '#') {
				rPath = RegExHelper.replace(REGEX_DRIVENAME, rPath, letter + ":" + FSPath.SEPERATOR);
			} else {
				ccprops.getDriveMap().conditionalRescan();
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DriveNotFound", name));
				return FSPath.Empty;
			}
		}
		else if (RegExHelper.startsWithRegEx(REGEX_DRIVELETTER, rPath))
		{
			String card = RegExHelper.find(REGEX_DRIVELETTER, rPath);
			char letter = card.charAt(11);
			rPath = RegExHelper.replace(REGEX_DRIVELETTER, rPath, letter + ":" + FSPath.SEPERATOR);
		}
		else if (RegExHelper.startsWithRegEx(REGEX_SELFDRIVE, rPath))
		{
			if (ApplicationHelper.isWindows()) {
				char letter = FilesystemUtils.getRealSelfDirectory().toString().charAt(0);
				rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, letter + ":" + FSPath.SEPERATOR);
			} else if (ApplicationHelper.isUnix() || ApplicationHelper.isMac()) {
				rPath = RegExHelper.replace(REGEX_SELFDRIVE, rPath, "/");
			}
		}
		else if (RegExHelper.startsWithRegEx(REGEX_NETDRIVE, rPath))
		{
			String card = RegExHelper.find(REGEX_NETDRIVE, rPath);
			String name = card.substring(12, card.length() - 2);
			char letter = ccprops.getDriveMap().getDriveLetterByUNC(name);
			if (letter != '#') {
				rPath = RegExHelper.replace(REGEX_NETDRIVE, rPath, letter + ":" + FSPath.SEPERATOR);
			} else {
				ccprops.getDriveMap().conditionalRescan();
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
				if (!isRawPathVar(buildr.toString()) && buildr.length() > 0) buildr.append(SEPERATOR);
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

		var components = this.getComponents();

		for (int i = 0; i < c; i++) {
			if (components.size() > 0) {
				components.remove(components.size()-1);
			}
		}

		return createFromComponents(components);
	}

	public List<String> getComponents() {

		var prefixLen = -1;

		regexFor: for (Pattern pattern : REGEX_ALL) {
			Matcher matcher = pattern.matcher(this._path);
			while (matcher.find()) {
				if (matcher.start() == 0) {
					prefixLen = matcher.end();
					break regexFor;
				}
			}
		}

		var result = new ArrayList<String>(6);
		
		var pathStr = _path;
		
		if (prefixLen > 0) {
			result.add(pathStr.substring(0, prefixLen));
			pathStr = pathStr.substring(prefixLen);
		}

		result.addAll(Arrays.asList(pathStr.split(SEPERATOR)));

		return result;
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

	public String toStringWithTraillingSeparator() {
		if (_path.endsWith(SEPERATOR)) return _path; else return (_path + SEPERATOR);
	}

	@Override
	public int compareTo(@NotNull CCPath o) {
		var a = this._path;
		var b = this._path;
		return ApplicationHelper.isWindows() ? a.compareToIgnoreCase(b) : a.compareTo(b);
	}

	private static boolean isRawPathVar(String p) {
		if (p.length() == 0 || p.charAt(0) != '<' || p.charAt(p.length()-1) != '>') return false;

		for (Pattern pattern : REGEX_ALL) {
			Matcher matcher = pattern.matcher(p);
			while (matcher.find()) {
				if (matcher.start() == 0 && matcher.end() == p.length()) {
					return true;
				}
			}
		}
		return false;
	}

	public static CCPath getCommonPath(List<CCPath> pathlist) {
		if (pathlist.isEmpty()) return Empty;

		List<List<String>> complist = new ArrayList<>(pathlist.size());
		for (var p : pathlist) complist.add(p.getComponents());

		complist.add(pathlist.get(0).getComponents().subList(0, complist.get(0).size()-1)); // ensure that we do not get the last path component (probably the filename)

		for (int ic = 0;;ic++)
		{
			String cktComp = null;

			boolean equal = true;
			for (var ccCompPath : complist)
			{
				if (ic >= ccCompPath.size()) { equal = false; break; }

				if (cktComp == null) {
					cktComp = ccCompPath.get(ic);
				} else {
					if (ApplicationHelper.isWindows()) {
						if (!cktComp.equalsIgnoreCase(ccCompPath.get(ic))) { equal = false; break; }
					} else {
						if (!cktComp.equals(ccCompPath.get(ic))) { equal = false; break; }
					}

				}
			}

			if (! equal) {
				return createFromComponents(complist.get(0).subList(0, ic));
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
