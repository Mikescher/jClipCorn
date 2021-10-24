package de.jClipCorn.util.filesystem;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FSPath implements IPath, Comparable<FSPath> {
	public final static FSPath Empty = new FSPath(Str.Empty);

	public static final String SEPERATOR      = File.separator;
	public static final char   SEPERATOR_CHAR = File.separatorChar;

	private final String _path;

	private File    _cacheFile  = null;
	private Path    _cachePath  = null;
	private URI     _cacheURI   = null;
	private Boolean _cacheValid = null;

	private FSPath(@NotNull String path) {
		_path = path;
	}

	public static FSPath create(@NotNull String v) {
		if (Str.isNullOrWhitespace(v)) return Empty;
		if (v.endsWith(SEPERATOR) && !(!ApplicationHelper.isWindows() && Str.equals(v, "/"))) v = v.substring(0, v.length() - SEPERATOR.length());
		return new FSPath(v);
	}

	public static FSPath createAndNormalize(@NotNull String v) {
		if (Str.isNullOrWhitespace(v)) return Empty;
		v = Path.of(v).normalize().toAbsolutePath().toString();
		if (v.endsWith(SEPERATOR) && !(!ApplicationHelper.isWindows() && Str.equals(v, "/"))) v = v.substring(0, v.length() - SEPERATOR.length());
		return new FSPath(v);
	}

	public static FSPath create(@NotNull File f) {
		// getAbsolutePath() returns for long paths under windows mangled paths (eg  `PROGRA~1` )
		// so we first try getCanonicalPath(), and only use getAbsolutePath() as a fallback
		// In theory the exception should never happen
		try {
			return new FSPath(f.getCanonicalPath());
		} catch (IOException e) {
			CCLog.addError("Failed to canonicalize the path '"+f.getPath()+"'", e); //$NON-NLS-1$ //$NON-NLS-2$
			return new FSPath(f.getAbsolutePath());
		}
	}

	public static FSPath create(@NotNull Path path) {
		var v = path.normalize().toAbsolutePath().toString();
		if (v.endsWith(SEPERATOR) && !(!ApplicationHelper.isWindows() && Str.equals(v, "/"))) v = v.substring(0, v.length() - SEPERATOR.length());
		return new FSPath(v);
	}

	public FSPath append(String... tail) {
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

	public File toFile() {
		if (_cacheFile != null) return _cacheFile;
		return _cacheFile = new File(_path);
	}

	public Path toPath() {
		if (_cachePath != null) return _cachePath;
		return _cachePath = Path.of(_path);
	}

	public URI toURI() {
		if (_cacheURI != null) return _cacheURI;
		return _cacheURI = toFile().toURI();
	}

	public BasicFileAttributes readFileAttr() throws IOException {
		return Files.readAttributes(toPath(), BasicFileAttributes.class);
	}

	public boolean exists() {
		return toFile().exists();
	}

	public boolean fileExists() {
		return exists() && isFile();
	}

	public boolean canExecute() {
		return toFile().canExecute();
	}

	public boolean directoryExists() {
		return exists() && isDirectory();
	}

	public boolean isDirectory() {
		return toFile().isDirectory();
	}

	public boolean isFile() {
		return toFile().isFile();
	}

	public boolean isEmpty() {
		return Str.isNullOrWhitespace(_path);
	}

	public String readAsUTF8TextFile() throws IOException
	{
		var result = readAsTextFile(Str.UTF8, SimpleFileUtils.LINE_END);
		if (result.length()>0 && result.charAt(0) == 0xFEFF) result = result.substring(1); // remove BOM
		return result;
	}

	public String readAsTextFile(Charset encoding, String newline) throws IOException
	{
		try (var stream = new FileInputStream(toFile()))
		{
			try (var reader = new InputStreamReader(stream, encoding))
			{
				try (var buffer = new BufferedReader(reader))
				{
					StringBuilder content = new StringBuilder();
					boolean first = true;

					String s;
					while ((s = buffer.readLine()) != null)
					{
						if (!first) content.append(newline);
						content.append(s);
						first = false;
					}

					return content.toString();
				}
			}
		}
	}

	public void writeAsUTF8TextFile(String content) throws IOException {
		writeAsTextFile(Str.UTF8, content);
	}

	public void writeAsTextFile(Charset encoding, String content) throws IOException
	{
		try (var fos = new FileOutputStream(toFile()))
		{
			try (var osw = new OutputStreamWriter(fos, encoding))
			{
				try (var bw = new BufferedWriter(osw))
				{
					bw.write(content);
				}
			}
		}
	}

	public String[] listFilenames() {
		var ls = toFile().list();
		if (ls == null) return new String[0];
		return ls;
	}

	public String[] listFilenames(FilenameFilter ff) {
		var ls = toFile().list();
		if (ls == null) return new String[0];
		return ls;
	}

	public CCStream<FSPath> list() {
		var ls = toFile().listFiles();
		if (ls == null) return CCStreams.empty();
		return CCStreams.iterate(ls).map(FSPath::create);
	}

	public CCStream<FSPath> list(FSPathFilter ff) {
		var ls = toFile().listFiles();
		if (ls == null) return CCStreams.empty();
		return CCStreams.iterate(ls).map(FSPath::create).filter(ff::accept);
	}

	public String getFilenameWithoutExt() {
		int liop = _path.lastIndexOf('.');
		if (liop > 0) {
			return _path.substring(_path.lastIndexOf(SEPERATOR) + 1, liop);
		}
		return _path.substring(_path.lastIndexOf(SEPERATOR) + 1);
	}

	public String getFilenameWithExt() {
		return getLastPathSegment();
	}

	public String getDirectoryName() {
		return getLastPathSegment();
	}

	public String getLastPathSegment() {
		return _path.substring(_path.lastIndexOf(SEPERATOR) + 1);
	}

	public void deleteOnExit() {
		toFile().deleteOnExit();
	}

	public boolean deleteSafe() {
		return toFile().delete();
	}

	public void deleteRecursive() throws IOException {
		FileUtils.deleteDirectory(toFile());
	}

	public void deleteWithException() throws IOException {
		var r = toFile().delete();
		if (!r) throw new IOException("Failed to delete File '"+_path+"' (returned false)");
	}

	public boolean mkdirsSafe() {
		return toFile().mkdirs();
	}

	public void mkdirsWithException() throws IOException {
		var r = toFile().mkdirs();
		if (!r) throw new IOException("Failed to mkdir Direction '"+_path+"' (returned false)");
	}

	public boolean renameToSafe(FSPath pnew) {
		return this.toFile().renameTo(pnew.toFile());
	}

	public void renameToWithException(FSPath pnew) throws IOException {
		var r = this.toFile().renameTo(pnew.toFile());
		if (!r) throw new IOException("Failed to rename File '"+_path+"' to '"+pnew._path+"' (returned false)");
	}

	public void createFolders() throws IOException {
		var parent = getParent();
		if (parent.directoryExists()) return;

		if (!parent.toFile().mkdirs()) throw new IOException("Failed to create (parent) directories of file '"+_path+"' (returned false)");
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

	public CCFileSize filesize() {
		return new CCFileSize(toFile().length());
	}

	public int countAllFilesRecursive() {
		return list().sumInt(p -> p.isDirectory() ? p.countAllFilesRecursive() : 1);
	}

	public FSPath forceExtension(String ext) {
		if (getExtension().equalsIgnoreCase(ext)) {
			return this;
		} else {
			return FSPath.create(_path + '.' + ext);
		}
	}

	public FSPath replaceExtension(String ext) {
		var p = _path;
		if (p.endsWith(SEPERATOR)) p = p.substring(0, p.length() - SEPERATOR.length());

		var sep = p.lastIndexOf(SEPERATOR);

		var idx = _path.lastIndexOf('.');
		if (idx < 0 || (sep > 0 && idx < sep)) return FSPath.create(_path + "." + ext);

		return FSPath.create(_path.substring(0, idx) + "." + ext);
	}

	public FSPath replaceFilename(String newfn) {
		return getParent().append(newfn);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return isEqual(this, (FSPath) o);
	}

	public static boolean isEqual(FSPath a, FSPath b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		return Str.equals(a._path, b._path);
	}

	public boolean equalsOnFilesystem(FSPath other) {
		if (other == null) return false;
		if (other == this) return true;

		var p1 = this.toNormalizedAndAbsolutePathString();
		var p2 = other.toNormalizedAndAbsolutePathString();

		if (ApplicationHelper.isWindows()) {
			return Str.equals(p1.toLowerCase(), p2.toLowerCase());
		} else {
			return Str.equals(p1, p2);
		}
	}

	@Override
	public int hashCode() {
		return _path.hashCode();
	}

	@Override
	public String toString() {
		return _path;
	}

	@Override
	public int compareTo(@NotNull FSPath o) {
		var a = this._path;
		var b = this._path;
		return ApplicationHelper.isWindows() ? a.compareToIgnoreCase(b) : a.compareTo(b);
	}

	public boolean isValidPath()
	{
		if (_cacheValid != null) return _cacheValid;
		try
		{
			Paths.get(_path);
			//noinspection ResultOfMethodCallIgnored
			new File(_path).getCanonicalPath();
		}
		catch (Exception ex)
		{
			return _cacheValid = false;
		}
		return _cacheValid = true;
	}

	public static FSPath getCommonPath(List<FSPath> pathlist) {
		if (pathlist.isEmpty()) return Empty;

		for (int c = 0;;c++)
		{
			Character ckt = null;

			boolean equal = true;
			for (var p : pathlist)
			{
				if (c >= p._path.length()) { equal = false; break; }

				if (ckt == null) {
					ckt = p._path.charAt(c);
				} else {
					var a = ckt;
					var b = p._path.charAt(c);

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
				if (lastidx > 0) return FSPath.create(common.substring(0, lastidx + 1));

				var nextidx = pathlist.get(0)._path.indexOf(SEPERATOR, c);
				if (nextidx > 0) return FSPath.create(common.substring(0, nextidx + 1));

				return FSPath.Empty;
			}
		}
	}

	public String toStringWithTraillingSeparator() {
		if (_path.endsWith(SEPERATOR)) return _path; else return (_path + SEPERATOR);
	}

	public FSPath getParent() {
		return getParent(1);
	}

	public FSPath getParent(int c) {
		var p = _path;
		if (p.endsWith(SEPERATOR)) p = p.substring(0, p.length() - SEPERATOR.length());

		for (int i = 0; i < c; i++) {
			int idx = p.lastIndexOf(SEPERATOR_CHAR);
			if (idx > 0) p = p.substring(0, idx);
		}

		return FSPath.create(p);
	}

	public String toAbsolutePathString() {
		return toFile().getAbsolutePath();
	}

	public String toNormalizedAndAbsolutePathString() {
		return toPath().normalize().toAbsolutePath().toString();
	}

	public static boolean isNullOrEmpty(FSPath p) {
		return p == null || p.isEmpty();
	}
}
