package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.MultiSizeIconRef;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class CCSingleTag
{
	public final int Index;
	public final String Description;

	public final MultiSizeIconRef IconOff;
	public final MultiSizeIconRef IconOn;
	public final MultiSizeIconRef Image;

	public final boolean IsMovieTag;
	public final boolean IsSeriesTag;
	public final boolean IsEpisodeTag;

	public CCSingleTag(int idx, String dscIdent, MultiSizeIconRef off, MultiSizeIconRef on, MultiSizeIconRef img, boolean mov, boolean ser, boolean epi) {
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
		return IconOn.get16x16();
	}

	public ImageIcon getOffIcon() {
		return IconOff.get16x16();
	}

	public BufferedImage getOnImage() {
		return IconOn.getImage16x16();
	}

	public BufferedImage getOffImage() {
		return IconOff.getImage16x16();
	}
}
