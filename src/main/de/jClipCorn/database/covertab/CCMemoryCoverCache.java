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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class CCMemoryCoverCache implements ICoverCache {
	private Map<Integer, BufferedImage> data;
	private final HashMap<Integer, CCCoverData> _elements;

	public CCMemoryCoverCache() {
		_elements = new HashMap<>();
	}

	@Override
	public void init() {
		data = new HashMap<>();
	}

	@Override
	public void addInternal(CCCoverData elem) {
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

	private CCCoverData getFromCache(int cid) {
		CCCoverData cce = _elements.get(cid);

		if (cce == null) CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotInCache", cid)); //$NON-NLS-1$

		return cce;
	}

	@Override
	public BufferedImage getCover(int cid) {
		if (cid == -1) return Resources.IMG_COVER_NOTFOUND.get();

		CCCoverData cce = getFromCache(cid);
		if (cce == null) return Resources.IMG_COVER_NOTFOUND.get();

		return getCover(cce);
	}

	@Override
	public BufferedImage getCover(CCCoverData cce) {
		BufferedImage res = data.get(cce.ID);

		if (res == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", cce.ID)); //$NON-NLS-1$
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
			byte[] preview = ColorQuantizerConverter.quantizeTo4BitRaw(quant, ColorQuantizerConverter.shrink(newCover, ColorQuantizerConverter.PREVIEW_WIDTH));

			CCCoverData cce = new CCCoverData(cid, fname, newCover.getWidth(), newCover.getHeight(), checksum, f.length(), preview, ptype, CCDateTime.getCurrentDateTime());

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
	public CCCoverData getInfo(int cid) {
		CCCoverData cce = _elements.get(cid);
		if (cce == null) CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotInCache", cid)); //$NON-NLS-1$
		return cce;
	}

	@Override
	public boolean coverFileExists(int cid) {
		return data.containsKey(cid);
	}

	@Override
	public Tuple<Integer, Integer> getDimensions(int cid) {
		CCCoverData cce = getFromCache(cid);
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

	@Override
	public int getCoverCount() {
		return _elements.values().size();
	}

	@Override
	public List<CCCoverData> listCovers() {
		return Collections.unmodifiableList(new ArrayList<>(_elements.values()));
	}

	@Override
	public String getFilepath(CCCoverData cce) {
		return null;
	}
}