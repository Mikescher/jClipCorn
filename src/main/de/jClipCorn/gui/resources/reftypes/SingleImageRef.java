package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.Main;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class SingleImageRef extends ImageRef {
	public final String path;

	public SingleImageRef(String respath, boolean preload) {
		super("image://" + respath, preload); //$NON-NLS-1$
		path = respath;
	}

	@Override
	public List<ResourceRef> getDirectDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void preload() {
		CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public BufferedImage get() {
		return CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public BufferedImage createImage() {
		try
		{
			return ImageIO.read(Main.class.getResource(this.path));
		}
		catch (IOException | IllegalArgumentException e)
		{
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.RessourceNotFound", this.path)); //$NON-NLS-1$
			return new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		}
	}
}
