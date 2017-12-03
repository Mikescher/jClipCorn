package de.jClipCorn.database.util.covercache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import de.jClipCorn.Main;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.SimpleSerializableData;
import de.jClipCorn.util.datatypes.CachedHashMap;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.exceptions.XMLFormatException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.lambda.Func0to1WithIOException;

public class CCFolderCoverCache extends CCCoverCache {
	private final static String COVER_DIRECTORY_NAME = "cover"; //$NON-NLS-1$
	private final static String COVER_CACHEFILE_NAME = "covercache.xml"; //$NON-NLS-1$
	private final static String COVER_DIRECTORY = PathFormatter.appendAndPrependSeparator(COVER_DIRECTORY_NAME);

	private Map<String, BufferedImage> cache;
	private SimpleSerializableData metacache = null;
	
	private String coverPath;
	private String cacheFilepath;
	
	private Vector<Integer> usedCoverIDs;
	
	public CCFolderCoverCache(String dbPath) {
		cache = new CachedHashMap<>(CCProperties.getInstance().PROP_DATABASE_COVERCACHESIZE.getValue());
		
		coverPath = PathFormatter.combine(PathFormatter.getRealSelfDirectory(), dbPath, COVER_DIRECTORY);
		cacheFilepath = PathFormatter.combine(PathFormatter.getRealSelfDirectory(), dbPath, COVER_CACHEFILE_NAME);
	}

	@Override
	public void connect() {
		tryCreatePath();

		calculateBiggestCID();
		
		try {
			if (PathFormatter.fileExists(cacheFilepath)) metacache = SimpleSerializableData.load(cacheFilepath, true);
		} catch (XMLFormatException e) {
			metacache = SimpleSerializableData.createEmpty(true);
			CCLog.addError("CoverCache loading failed", e); //$NON-NLS-1$
		}
	}
	
	@Override
	@SuppressWarnings("nls")
	public Tuple<Integer, Integer> getDimensions(String name) {
		if (metacache.containsChild(name)) {
			SimpleSerializableData d = metacache.getChild(name);
			return Tuple.Create(d.getInt("width"), d.getInt("height"));
		}
		
		BufferedImage bi = getCover(name);
		return Tuple.Create(bi.getWidth(), bi.getHeight());
	}
	
	@Override
	public BufferedImage getCover(String name) {
		if ((name == null) || name.isEmpty()) {
			return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
		}

		BufferedImage res = cache.get(name);

		if (res == null) {
			try {
				File f = new File(PathFormatter.combine(coverPath, name));
				res = ImageIO.read(f);
				if (res != null) {
					cache.put(name, res);
					updateMetaCache(name, res, f);
				} else {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", name)); //$NON-NLS-1$
					return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
				}
			} catch (IOException e) {
				if (! Main.DEBUG) {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFound", name)); //$NON-NLS-1$
				} else {
					CCLog.addDebug(String.format("Cover not found (%s)", name)); //$NON-NLS-1$
				}
				return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
			}
		}
		
		return res;
	}
	
	@SuppressWarnings("nls")
	private void updateMetaCache(String name, BufferedImage img, File f) {
		try {
			try (FileInputStream fis = new FileInputStream(f)) {

				int width = img.getWidth();
				int height = img.getHeight();
				long filesize = f.length();
				String hash = DigestUtils.md5Hex(fis);
				
				if (metacache.containsChild(name)) {
					SimpleSerializableData child = metacache.getChild(name);

					int oldWidth   = child.getInt("width");
					int oldHeight  = child.getInt("height");
					long oldSize   = child.getLong("filesize");
					String oldHash = child.getStr("hash");
					
					if (width != oldWidth || height != oldHeight || filesize != oldSize || !hash.equals(oldHash)) {
						child.set("width", width);
						child.set("height", height);
						child.set("filesize", filesize);
						child.set("hash", hash);

						SaveMetaCache();
					}
				} else {
					SimpleSerializableData child = metacache.addChild(name);

					child.set("width", width);
					child.set("height", height);
					child.set("filesize", filesize);
					child.set("hash", hash);
					
					SaveMetaCache();
				}
			}
		}
		catch (IOException e) {
			CCLog.addError("Could not update cover cache", e);
		}
	}
	
	private void SaveMetaCache() {
		try {
			metacache.save(cacheFilepath);
		}
		catch (IOException e) {
			CCLog.addError("Could not save cover cache", e); //$NON-NLS-1$
		}
	}

	@Override
	public boolean coverExists(String name) {
		return new File(PathFormatter.combine(coverPath, name)).exists();
	}

	@Override
	public void preloadCover(String name) { //TODO intelligent Precaching
		BufferedImage res = cache.get(name);

		if (res == null) {
			try {
				res = ImageIO.read(new File(PathFormatter.combine(coverPath, name)));
				cache.put(name, res);
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFoundPreload", coverPath + name)); //$NON-NLS-1$
			}
		}
	}

	private void tryCreatePath() {
		File dbpF = new File(coverPath);

		if (!dbpF.exists() && !dbpF.mkdirs()) { // Only mkdir if not exists
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorMKDIRCovers", coverPath)); //$NON-NLS-1$
		}
	}

	private void calculateBiggestCID() {
		usedCoverIDs = new Vector<>();
		
		String[] files = getCoverDirectory().list();
		
		Pattern pattern = Pattern.compile(String.format("(?<=%s)[0-9]+(?=\\.%s)", Pattern.quote(CCProperties.getInstance().PROP_COVER_PREFIX.getValue().toLowerCase()), Pattern.quote(CCProperties.getInstance().PROP_COVER_TYPE.getValue().toLowerCase()))); //$NON-NLS-1$
		
		for (String f : files) {
			Matcher matcher = pattern.matcher(PathFormatter.getFilenameWithExt(f).toLowerCase());
			if (matcher.find()){
				usedCoverIDs.add(Integer.parseInt(matcher.group()));
			}
		}
		
		Collections.sort(usedCoverIDs);
	}
	
	@Override
	public int getNewCoverID() {
		int i = 0;
		while(usedCoverIDs.size() > i && usedCoverIDs.get(i).equals(i)) {
			i++;
		}
		usedCoverIDs.add(i, i);
		
		return i;
	}

	@Override
	public String addCover(BufferedImage newCover) {
		String id = StringUtils.leftPad(Integer.toString(getNewCoverID()), 5, '0');
		
		CCLog.addDebug("addingCoverToFolder: " + id); //$NON-NLS-1$

		String fname = CCProperties.getInstance().PROP_COVER_PREFIX.getValue() + id + '.' + CCProperties.getInstance().PROP_COVER_TYPE.getValue();
		String path = PathFormatter.combine(coverPath, fname);
		
		try {
			File f = new File(path);
			if (! f.exists()) {
				ImageIO.write(newCover, CCProperties.getInstance().PROP_COVER_TYPE.getValue(), f);
				updateMetaCache(fname, newCover, f);
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TryOverwriteFile", path)); //$NON-NLS-1$
			}

			return fname;
		} catch (IOException e) {
			CCLog.addError("LogMessage.ErrorCreatingCoverFile"); //$NON-NLS-1$

			return ""; //$NON-NLS-1$
		}
	}
	
	@Override
	public void deleteCover(String covername) {
		cache.remove(covername);
		
		File f = new File(PathFormatter.combine(coverPath, covername));

		if (!f.delete()) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DeleteCover", covername)); //$NON-NLS-1$
		}
		
		metacache.removeChild(covername);
		SaveMetaCache();
		
		CCLog.addDebug("removing Cover from Folder: " + covername); //$NON-NLS-1$
	}
	
	public String getCoverPath() {
		return coverPath;
	}

	public File getCoverDirectory() {
		return new File(getCoverPath());
	}

	@Override
	public void addCover(String name, InputStream stream) throws Exception {
		byte[] buffer = new byte[2048];
		
		String outpath = PathFormatter.combine(coverPath, name);
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(outpath);
			int len = 0;
			while ((len = stream.read(buffer)) > 0) {
				output.write(buffer, 0, len);
			}
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	@Override
	public List<Tuple<String, Func0to1WithIOException<BufferedImage>>> listCoversNonCached() {
		final String prefix = CCProperties.getInstance().PROP_COVER_PREFIX.getValue();
		final String suffix = "." + CCProperties.getInstance().PROP_COVER_TYPE.getValue();  //$NON-NLS-1$
		
		String[] files = getCoverDirectory().list(new FilenameFilter() {
			@Override
			public boolean accept(File path, String name) {
				return name.startsWith(prefix) && name.endsWith(suffix);
			}
		});
		
		List<Tuple<String, Func0to1WithIOException<BufferedImage>>> result = new ArrayList<>();
		for (String file : files) {
			String t1 = file;
			Func0to1WithIOException<BufferedImage> t2 = () -> ImageIO.read(new File(PathFormatter.combine(getCoverPath(), file)));
			result.add(Tuple.Create(t1, t2));
		}
		
		return result;
	}
	
	@Override
	public void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles) {
		excludedFolders.add(COVER_DIRECTORY_NAME);
	}
}
