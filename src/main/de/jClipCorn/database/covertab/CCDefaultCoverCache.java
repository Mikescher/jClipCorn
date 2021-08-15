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
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.lambda.Func0to1WithIOException;
import de.jClipCorn.util.sqlwrapper.SQLWrapperException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class CCDefaultCoverCache implements ICoverCache {
	public final static String COVER_DIRECTORY_NAME = "cover"; //$NON-NLS-1$

	protected final CCDatabase _db;

	protected Map<Integer, BufferedImage> _cache;

	protected final HashMap<Integer, CCCoverData> _elements;
	protected final List<CCCoverData> _elementsList;

	private final FSPath _coverPath;

	public CCDefaultCoverCache(CCDatabase database) {
		_db = database;
		_elements = new HashMap<>();
		_elementsList = new ArrayList<>();
		_cache = new CachedHashMap<>(ccprops().PROP_DATABASE_COVERCACHESIZE.getValue());

		_coverPath = database.getDBPath().append(COVER_DIRECTORY_NAME);
	}

	@Override
	public void init() {
		tryCreatePath();
	}

	public CCProperties ccprops() {
		return _db.ccprops();
	}

	private void tryCreatePath() {
		if (!_coverPath.exists() && !_coverPath.toFile().mkdirs()) { // Only mkdir if not exists
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorMKDIRCovers", _coverPath)); //$NON-NLS-1$
		}
	}

	public FSPath getCoverPath() {
		return _coverPath;
	}

	public FSPath getCoverDirectory() {
		return _coverPath;
	}

	public List<Tuple<String, Func0to1WithIOException<BufferedImage>>> listCoversInFilesystem() {
		final String prefix = ccprops().PROP_COVER_PREFIX.getValue();
		final String suffix = "." + ccprops().PROP_COVER_TYPE.getValue();  //$NON-NLS-1$

		var files = getCoverDirectory().listFilenames((path, name) -> name.startsWith(prefix) && name.endsWith(suffix));

		List<Tuple<String, Func0to1WithIOException<BufferedImage>>> result = new ArrayList<>();
		for (String file : files) {
			Func0to1WithIOException<BufferedImage> t2 = () -> ImageIO.read(getCoverPath().append(file).toFile());
			result.add(Tuple.Create(file, t2));
		}

		return result;
	}

	@Override
	public boolean coverFileExists(int cid) {
		CCCoverData cce = getEntry(cid);
		if (cce == null) return false;

		return getFilepath(cce).exists();
	}

	@Override
	public BufferedImage getCover(int cid) {
		if (cid == -1) return Resources.IMG_COVER_NOTFOUND.get();

		CCCoverData cce = getEntry(cid);
		if (cce == null) return Resources.IMG_COVER_NOTFOUND.get();

		return getCover(cce);
	}

	@Override
	public BufferedImage getCover(CCCoverData cce) {
		BufferedImage res = _cache.get(cce.ID);
		if (res != null) return res;

		try {
			var f = getFilepath(cce);
			res = ImageIO.read(f.toFile());
			if (res != null) {
				_cache.put(cce.ID, res);
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", cce.ID)); //$NON-NLS-1$
				return Resources.IMG_COVER_NOTFOUND.get();
			}
		} catch (IOException e) {
			if (! Main.DEBUG) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFound", cce.ID)); //$NON-NLS-1$
			} else {
				CCLog.addDebug(String.format("Cover not found (%d)", cce.ID)); //$NON-NLS-1$
			}
			return Resources.IMG_COVER_NOTFOUND.get();
		}

		return res;
	}

	@Override
	public void preloadCover(int cid) {
		BufferedImage res = _cache.get(cid);
		if (res == null) return;

		CCCoverData cce = getEntry(cid);
		if (cce == null) return;

		try {
			res = ImageIO.read(getFilepath(cce).toFile());
			_cache.put(cid, res);
		} catch (IOException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFoundPreload", cid)); //$NON-NLS-1$
		}
	}

	@Override
	public int getCoverCount() {
		return _elementsList.size();
	}

	@Override
	public List<CCCoverData> listCovers() {
		return Collections.unmodifiableList(_elementsList);
	}

	@Override
	public FSPath getFilepath(CCCoverData cce) {
		return _coverPath.append(cce.Filename);
	}

	@Override
	public int addCover(BufferedImage newCover) {
		int cid = getNewCoverID();

		String fname = ccprops().PROP_COVER_PREFIX.getValue() + StringUtils.leftPad(Integer.toString(cid), 5, '0') + '.' + ccprops().PROP_COVER_TYPE.getValue();

		CCLog.addDebug("addingCoverToFolder: " + fname); //$NON-NLS-1$

		try {
			var f = _coverPath.append(fname);
			if (! f.exists()) {
				ImageIO.write(newCover, ccprops().PROP_COVER_TYPE.getValue(), f.toFile());

				String checksum;
				try (FileInputStream fis = new FileInputStream(f.toFile())) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); }

				ColorQuantizerMethod ptype = ccprops().PROP_DATABASE_COVER_QUANTIZER.getValue();
				ColorQuantizer quant = ptype.create();
				quant.analyze(newCover, 16);
				byte[] preview = ColorQuantizerConverter.quantizeTo4BitRaw(quant, ColorQuantizerConverter.shrink(newCover, ColorQuantizerConverter.PREVIEW_WIDTH));

				CCCoverData cce = new CCCoverData(cid, fname, newCover.getWidth(), newCover.getHeight(), checksum, f.filesize(), preview, ptype, CCDateTime.getCurrentDateTime());

				boolean ok = _db.insertCoverEntry(cce);

				if (!ok) {
					try { f.deleteSafe(); } catch (Exception e) { /* */ }
					throw new SQLWrapperException("Could not insert cover into database"); //$NON-NLS-1$
				}

				_elements.put(cid, cce);
				_elementsList.add(cce);
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TryOverwriteFile", f.toString())); //$NON-NLS-1$
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

		CCCoverData cce = getEntry(cid);
		if (cce == null) return;

		var f = getFilepath(cce);

		if (!f.deleteSafe()) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DeleteCover", cid)); //$NON-NLS-1$
		}

		_db.deleteCoverEntry(cce);

		_elements.remove(cid);
		_elementsList.remove(cce);

		CCLog.addDebug("removing Cover from Folder: " + cid); //$NON-NLS-1$
	}

	@Override
	public void addInternal(CCCoverData elem) {
		_elements.put(elem.ID, elem);
		_elementsList.add(elem);
	}

	private CCCoverData getEntry(int cid) {
		CCCoverData cce = _elements.get(cid);

		if (cce == null) CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotInCache", cid)); //$NON-NLS-1$

		return cce;
	}

	@Override
	public Tuple<Integer, Integer> getDimensions(int cid) {
		CCCoverData cce = getEntry(cid);
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
	public CCCoverData getInfoOrNull(int cid) {
		return _elements.get(cid);
	}
}
