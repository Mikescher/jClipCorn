package de.jClipCorn.database.covertab;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.helper.ImageUtilities;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Renders Preview from CCCoverData
 */
public class CCPrevCoverCache extends CCMemoryCoverCache {

	public CCPrevCoverCache(CCDatabase database, CCProperties ccprops) {
		super(database, ccprops);
	}

	@Override
	public BufferedImage getCover(CCCoverData cce) {
		BufferedImage res = _data.get(cce.ID);
		if (res != null) return res;

		BufferedImage generated = generateFromPreview(cce);
		if (generated == null) return Resources.IMG_COVER_NOTFOUND.get();

		_data.put(cce.ID, generated);
		return generated;
	}

	private BufferedImage generateFromPreview(CCCoverData cce) {
		try {
			byte[] preview = cce.getPreviewOrNull();
			if (preview == null) preview = _db.getCoverPreviewOrNull(cce.ID); // db was loaded in 'fast-cover' mode

			if (preview == null) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", cce.ID)); //$NON-NLS-1$
				return null;
			}

			BufferedImage small = ColorQuantizerConverter.quantizeFrom4BitRaw(preview);

			return ImageUtilities.smoothResize(small, cce.Width, cce.Height);
		} catch (Exception e) {
			CCLog.addError(e);
			return null;
		}
	}
}
