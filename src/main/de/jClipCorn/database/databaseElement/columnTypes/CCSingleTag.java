package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.MultiIconRef;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class CCSingleTag
{
	public final int Index;
	public final String Description;

	public final MultiIconRef IconOff;
	public final MultiIconRef IconOn;
	public final MultiIconRef Image;

	public final boolean IsMovieTag;
	public final boolean IsSeriesTag;
	public final boolean IsEpisodeTag;

	public CCSingleTag(int idx, String dscIdent, MultiIconRef off, MultiIconRef on, MultiIconRef img, boolean mov, boolean ser, boolean epi) {
		Index = idx;
		Description = LocaleBundle.getString(dscIdent);
		IconOff = off;
		IconOn = on;
		Image = img;
		IsMovieTag = mov;
		IsSeriesTag = ser;
		IsEpisodeTag = epi;
	}

	public ImageIcon getOnIcon() {
		return CachedResourceLoader.getIcon(IconOn.icon16x16);
	}

	public ImageIcon getOffIcon() {
		return CachedResourceLoader.getIcon(IconOff.icon16x16);
	}

	public BufferedImage getOnImage() {
		return CachedResourceLoader.getImage(IconOn.icon16x16);
	}

	public BufferedImage getOffImage() {
		return CachedResourceLoader.getImage(IconOff.icon16x16);
	}
}
