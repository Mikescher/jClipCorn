package de.jClipCorn.database.covertab;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.*;

public class CCMemoryCoverCache implements ICoverCache {
	protected Map<CCUUID, BufferedImage> _data;
	protected final LinkedHashMap<CCUUID, CCCoverData> _elements;

	protected final CCDatabase _db;
	protected final CCProperties _ccprops;

	public CCMemoryCoverCache(CCDatabase database, CCProperties ccprops) {
		_elements = new LinkedHashMap<>(); // listCovers() feeds the (byte-comparable) backup export
		_data = new HashMap<>();
		_db = database;
		_ccprops = ccprops;
	}

	@Override
	public void init() {
		_data.clear();
	}

	public CCProperties ccprops() {
		return _ccprops;
	}

	@Override
	public void addInternal(CCCoverData elem) {
		_elements.put(elem.ID, elem);
	}

	@Override
	public void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles) {
		// do nothing
	}

	private CCCoverData getEntry(CCUUID cid) {
		CCCoverData cce = _elements.get(cid);

		if (cce == null) CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotInCache", cid)); //$NON-NLS-1$

		return cce;
	}

	@Override
	public BufferedImage getCover(CCUUID cid) {
		CCCoverData cce = getEntry(cid);
		if (cce == null) return Resources.IMG_COVER_NOTFOUND.get();

		return getCover(cce);
	}

	@Override
	public BufferedImage getCover(CCCoverData cce) {
		BufferedImage res = _data.get(cce.ID);

		if (res == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", cce.ID)); //$NON-NLS-1$
			return Resources.IMG_COVER_NOTFOUND.get();
		}

		return res;
	}

	@Override
	public Opt<CCUUID> addCover(BufferedImage newCover) {
		try {
			CCUUID cid = CCUUID.generate();

			String fname = cid + "." + ccprops().PROP_COVER_TYPE.getValue(); //$NON-NLS-1$

			FSPath f = SimpleFileUtils.getSystemTempFile(".png"); //$NON-NLS-1$
			ImageIO.write(newCover, ccprops().PROP_COVER_TYPE.getValue(), f.toFile());

			String checksum;
			try (FileInputStream fis = new FileInputStream(f.toFile())) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); }

			ColorQuantizerMethod ptype = ccprops().PROP_DATABASE_COVER_QUANTIZER.getValue();
			ColorQuantizer quant = ptype.create();
			quant.analyze(newCover, 16);
			byte[] preview = ColorQuantizerConverter.quantizeTo4BitRaw(quant, ColorQuantizerConverter.shrink(newCover, ColorQuantizerConverter.PREVIEW_WIDTH));

			CCCoverData cce = new CCCoverData(cid, fname, newCover.getWidth(), newCover.getHeight(), checksum, f.filesize(), preview, ptype, CCDateTime.getCurrentDateTime());

			_db.insertCoverEntry(cce);

			_data.put(cid, newCover);
			_elements.put(cid, cce);

			f.deleteSafe();

			return Opt.of(cid);
		} catch (Exception e) {
			CCLog.addError(e);
			return Opt.empty();
		}
	}

	@Override
	public void deleteCover(CCUUID cid) {

		CCCoverData cce = getEntry(cid);
		if (cce == null) return;

		_db.deleteCoverEntry(cce);

		_data.remove(cid);
		_elements.remove(cid);

	}

	@Override
	public CCCoverData getInfoOrNull(CCUUID cid) {
		CCCoverData cce = _elements.get(cid);
		return cce;
	}

	@Override
	public boolean coverFileExists(CCUUID cid) {
		return _data.containsKey(cid);
	}

	@Override
	public Tuple<Integer, Integer> getDimensions(CCUUID cid) {
		CCCoverData cce = getEntry(cid);
		if (cce == null) return Tuple.Create(0, 0);
		return Tuple.Create(cce.Width, cce.Height);
	}

	@Override
	public boolean isCached(CCUUID cid) {
		return true;
	}

	@Override
	public void preloadCover(CCUUID cid) {
		getCover(cid);
	}

	@Override
	public int getCoverCount() {
		return _elements.values().size();
	}

	@Override
	public List<CCCoverData> listCovers() {
		return List.copyOf(_elements.values());
	}

	@Override
	public FSPath getFilepath(CCCoverData cce) {
		return FSPath.Empty;
	}

	public void resetForTestReload() {
		_elements.clear();
	}
}
