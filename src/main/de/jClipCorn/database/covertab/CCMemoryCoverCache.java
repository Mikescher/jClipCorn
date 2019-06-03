package de.jClipCorn.database.covertab;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.lambda.Func0to1WithIOException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCMemoryCoverCache implements ICoverCache {
	private Map<Integer, BufferedImage> data;
	private final HashMap<Integer, CoverCacheElement> _elements;

	public CCMemoryCoverCache() {
		_elements = new HashMap<>();
	}

	@Override
	public void init() {
		data = new HashMap<>();
	}

	@Override
	public List<Tuple<String, Func0to1WithIOException<BufferedImage>>> listCoversNonCached() {
		return null;
	}

	@Override
	public void addInternal(CoverCacheElement elem) {
		_elements.put(elem.ID, elem);
	}

	@Override
	public int getNewCoverID() {
		int i = 0;
		while(_elements.containsKey(i)) i++;
		return i;
	}

	@Override
	public void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles) {
		// do nothing
	}

	private CoverCacheElement getFromCache(int cid) {
		CoverCacheElement cce = _elements.get(cid);

		if (cce == null) CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotInCache", cid)); //$NON-NLS-1$

		return cce;
	}

	@Override
	public BufferedImage getCover(int cid) {
		if (cid == -1) return Resources.IMG_COVER_NOTFOUND.get();

		CoverCacheElement cce = getFromCache(cid);
		if (cce == null) return Resources.IMG_COVER_NOTFOUND.get();

		BufferedImage res = data.get(cce.ID);

		if (res == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", cid)); //$NON-NLS-1$
			return Resources.IMG_COVER_NOTFOUND.get();
		}

		return res;
	}

	@Override
	public int addCover(BufferedImage newCover) {
		try {
			int cid = getNewCoverID();

			String fname = CCProperties.getInstance().PROP_COVER_PREFIX.getValue() + StringUtils.leftPad(Integer.toString(cid), 5, '0') + '.' + CCProperties.getInstance().PROP_COVER_TYPE.getValue();

			File f = new File(SimpleFileUtils.getSystemTempFile(".png")); //$NON-NLS-1$
			ImageIO.write(newCover, CCProperties.getInstance().PROP_COVER_TYPE.getValue(), f);

			String checksum;
			try (FileInputStream fis = new FileInputStream(f)) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); }

			ColorQuantizerMethod ptype = CCProperties.getInstance().PROP_DATABASE_COVER_QUANTIZER.getValue();
			ColorQuantizer quant = ptype.create();
			quant.analyze(newCover, 16);
			byte[] preview = ColorQuantizerConverter.quantizeTo4BitRaw(quant, ColorQuantizerConverter.shrink(newCover, 24));

			CoverCacheElement cce = new CoverCacheElement(cid, fname, newCover.getWidth(), newCover.getHeight(), checksum, f.length(), preview, ptype, CCDateTime.getCurrentDateTime());

			data.put(cid, newCover);
			_elements.put(cid, cce);

			f.delete();

			return cid;
		} catch (Exception e) {
			CCLog.addError(e);
			return -1;
		}
	}

	@Override
	public void deleteCover(int cid) {
		data.remove(cid);
		_elements.remove(cid);
	}

	@Override
	public CoverCacheElement getInfo(int cid) {
		CoverCacheElement cce = _elements.get(cid);
		if (cce == null) CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotInCache", cid)); //$NON-NLS-1$
		return cce;
	}

	@Override
	public boolean coverExists(int cid) {
		return data.containsKey(cid);
	}

	@Override
	public Tuple<Integer, Integer> getDimensions(int cid) {
		CoverCacheElement cce = getFromCache(cid);
		if (cce == null) return Tuple.Create(0, 0);
		return Tuple.Create(cce.Width, cce.Height);
	}

	@Override
	public boolean isCached(int cid) {
		return true;
	}

	@Override
	public void preloadCover(int cid) {
		getCover(cid);
	}
}
