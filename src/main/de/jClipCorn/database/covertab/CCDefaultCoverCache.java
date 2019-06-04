package de.jClipCorn.database.covertab;

import de.jClipCorn.Main;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.datatypes.CachedHashMap;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.lambda.Func0to1WithIOException;
import de.jClipCorn.util.sqlwrapper.SQLWrapperException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCDefaultCoverCache implements ICoverCache {
	public final static String COVER_DIRECTORY_NAME = "cover"; //$NON-NLS-1$
	public final static String COVER_DIRECTORY = PathFormatter.appendAndPrependSeparator(COVER_DIRECTORY_NAME);

	protected final CCDatabase _db;

	protected Map<Integer, BufferedImage> _cache;

	protected final HashMap<Integer, CoverCacheElement> _elements;

	private String _coverPath;

	public CCDefaultCoverCache(CCDatabase database) {
		_db = database;
		_elements = new HashMap<>();
		_cache = new CachedHashMap<>(CCProperties.getInstance().PROP_DATABASE_COVERCACHESIZE.getValue());

		_coverPath = PathFormatter.combine(PathFormatter.getRealSelfDirectory(), database.getDBPath(), COVER_DIRECTORY);
	}

	@Override
	public void init() {
		tryCreatePath();
	}

	private void tryCreatePath() {
		File dbpF = new File(_coverPath);

		if (!dbpF.exists() && !dbpF.mkdirs()) { // Only mkdir if not exists
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorMKDIRCovers", _coverPath)); //$NON-NLS-1$
		}
	}

	public String getCoverPath() {
		return _coverPath;
	}

	public File getCoverDirectory() {
		return new File(getCoverPath());
	}

	@Override
	public List<Tuple<String, Func0to1WithIOException<BufferedImage>>> listCoversNonCached() {
		final String prefix = CCProperties.getInstance().PROP_COVER_PREFIX.getValue();
		final String suffix = "." + CCProperties.getInstance().PROP_COVER_TYPE.getValue();  //$NON-NLS-1$

		String[] files = getCoverDirectory().list((path, name) -> name.startsWith(prefix) && name.endsWith(suffix));

		List<Tuple<String, Func0to1WithIOException<BufferedImage>>> result = new ArrayList<>();
		for (String file : files) {
			Func0to1WithIOException<BufferedImage> t2 = () -> ImageIO.read(new File(PathFormatter.combine(getCoverPath(), file)));
			result.add(Tuple.Create(file, t2));
		}

		return result;
	}

	@Override
	public boolean coverFileExists(int cid) {
		CoverCacheElement cce = getFromCache(cid);
		if (cce == null) return false;

		return new File(PathFormatter.combine(_coverPath, cce.Filename)).exists();
	}

	@Override
	public BufferedImage getCover(int cid) {
		if (cid == -1) return Resources.IMG_COVER_NOTFOUND.get();

		CoverCacheElement cce = getFromCache(cid);
		if (cce == null) return Resources.IMG_COVER_NOTFOUND.get();

		BufferedImage res = _cache.get(cid);
		if (res != null) return res;

		try {
			File f = new File(PathFormatter.combine(_coverPath, cce.Filename));
			res = ImageIO.read(f);
			if (res != null) {
				_cache.put(cid, res);
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", cid)); //$NON-NLS-1$
				return Resources.IMG_COVER_NOTFOUND.get();
			}
		} catch (IOException e) {
			if (! Main.DEBUG) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFound", cid)); //$NON-NLS-1$
			} else {
				CCLog.addDebug(String.format("Cover not found (%d)", cid)); //$NON-NLS-1$
			}
			return Resources.IMG_COVER_NOTFOUND.get();
		}

		return res;
	}

	@Override
	public void preloadCover(int cid) { //TODO intelligent Precaching
		BufferedImage res = _cache.get(cid);
		if (res == null) return;

		CoverCacheElement cce = getFromCache(cid);
		if (cce == null) return;

		try {
			res = ImageIO.read(new File(PathFormatter.combine(_coverPath, cce.Filename)));
			_cache.put(cid, res);
		} catch (IOException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFoundPreload", cid)); //$NON-NLS-1$
		}
	}

	@Override
	public int addCover(BufferedImage newCover) {
		int cid = getNewCoverID();

		String fname = CCProperties.getInstance().PROP_COVER_PREFIX.getValue() + StringUtils.leftPad(Integer.toString(cid), 5, '0') + '.' + CCProperties.getInstance().PROP_COVER_TYPE.getValue();

		CCLog.addDebug("addingCoverToFolder: " + fname); //$NON-NLS-1$

		try {
			File f = new File(PathFormatter.combine(_coverPath, fname));
			if (! f.exists()) {
				ImageIO.write(newCover, CCProperties.getInstance().PROP_COVER_TYPE.getValue(), f);

				String checksum;
				try (FileInputStream fis = new FileInputStream(f)) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); }

				ColorQuantizerMethod ptype = CCProperties.getInstance().PROP_DATABASE_COVER_QUANTIZER.getValue();
				ColorQuantizer quant = ptype.create();
				quant.analyze(newCover, 16);
				byte[] preview = ColorQuantizerConverter.quantizeTo4BitRaw(quant, ColorQuantizerConverter.shrink(newCover, 24));

				CoverCacheElement cce = new CoverCacheElement(cid, fname, newCover.getWidth(), newCover.getHeight(), checksum, f.length(), preview, ptype, CCDateTime.getCurrentDateTime());

				boolean ok = _db.insertCoverEntry(cce);

				if (!ok) {
					try { f.delete(); } catch (Exception e) { /* */ }
					throw new SQLWrapperException("Could not insert cover into database"); //$NON-NLS-1$
				}

				_elements.put(cid, cce);
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TryOverwriteFile", f.getAbsolutePath())); //$NON-NLS-1$
			}

			return cid;
		} catch (IOException | ColorQuantizerException | SQLWrapperException e) {
			CCLog.addError("LogMessage.ErrorCreatingCoverFile"); //$NON-NLS-1$
			return -1;
		}
	}

	@Override
	public void deleteCover(int cid) {
		_cache.remove(cid);

		CoverCacheElement cce = getFromCache(cid);
		if (cce == null) return;

		File f = new File(PathFormatter.combine(_coverPath, cce.Filename));

		if (!f.delete()) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DeleteCover", cid)); //$NON-NLS-1$
		}

		_db.deleteCoverEntry(cce);

		_elements.remove(cid);

		CCLog.addDebug("removing Cover from Folder: " + cid); //$NON-NLS-1$
	}

	@Override
	public void addInternal(CoverCacheElement elem) {
		_elements.put(elem.ID, elem);
	}

	private CoverCacheElement getFromCache(int cid) {
		CoverCacheElement cce = _elements.get(cid);

		if (cce == null) CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotInCache", cid)); //$NON-NLS-1$

		return cce;
	}

	@Override
	public Tuple<Integer, Integer> getDimensions(int cid) {
		CoverCacheElement cce = getFromCache(cid);
		if (cce == null) return Tuple.Create(0, 0);
		return Tuple.Create(cce.Width, cce.Height);
	}

	@Override
	public boolean isCached(int cid) {
		return _cache.containsKey(cid);
	}

	@Override
	public int getNewCoverID() {
		int i = 0;
		while(_elements.containsKey(i)) i++;
		return i;
	}

	@Override
	public void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles) {
		excludedFolders.add(COVER_DIRECTORY_NAME);
	}

	@Override
	public CoverCacheElement getInfo(int cid) {
		return getFromCache(cid);
	}
}
