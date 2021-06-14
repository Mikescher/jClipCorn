package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.exceptions.TagNotFoundException;

import javax.swing.*;
import java.awt.image.BufferedImage;

public enum CCSingleTag implements ContinoousEnum<CCSingleTag>
{
	TAG_BAD_QUALITY 	(0, "CCMovieTags.TAG_00", Resources.ICN_TABLE_TAG_0_0, Resources.ICN_TABLE_TAG_0_1, Resources.ICN_MENUBAR_TAG_0, true,  true,  true),  //$NON-NLS-1$
	TAG_MISSING_TIME 	(1, "CCMovieTags.TAG_01", Resources.ICN_TABLE_TAG_1_0, Resources.ICN_TABLE_TAG_1_1, Resources.ICN_MENUBAR_TAG_1, true,  false, true),  //$NON-NLS-1$
	TAG_FILE_CORRUPTED 	(2, "CCMovieTags.TAG_02", Resources.ICN_TABLE_TAG_2_0, Resources.ICN_TABLE_TAG_2_1, Resources.ICN_MENUBAR_TAG_2, true,  false, true),  //$NON-NLS-1$
	TAG_WATCH_LATER 	(3, "CCMovieTags.TAG_03", Resources.ICN_TABLE_TAG_3_0, Resources.ICN_TABLE_TAG_3_1, Resources.ICN_MENUBAR_TAG_3, true,  true,  true),  //$NON-NLS-1$
	TAG_WRONG_LANGUAGE 	(4, "CCMovieTags.TAG_04", Resources.ICN_TABLE_TAG_4_0, Resources.ICN_TABLE_TAG_4_1, Resources.ICN_MENUBAR_TAG_4, true,  true,  true),  //$NON-NLS-1$
	TAG_WATCH_NEVER 	(5, "CCMovieTags.TAG_05", Resources.ICN_TABLE_TAG_5_0, Resources.ICN_TABLE_TAG_5_1, Resources.ICN_MENUBAR_TAG_5, true,  true,  true),  //$NON-NLS-1$
	TAG_WATCH_CAMRIP	(6, "CCMovieTags.TAG_06", Resources.ICN_TABLE_TAG_6_0, Resources.ICN_TABLE_TAG_6_1, Resources.ICN_MENUBAR_TAG_6, true,  true,  true),  //$NON-NLS-1$
	TAG_WATCH_MICDUBBED	(7, "CCMovieTags.TAG_07", Resources.ICN_TABLE_TAG_7_0, Resources.ICN_TABLE_TAG_7_1, Resources.ICN_MENUBAR_TAG_7, true,  true,  true),  //$NON-NLS-1$
	TAG_WATCH_CANCELLED	(8, "CCMovieTags.TAG_08", Resources.ICN_TABLE_TAG_8_0, Resources.ICN_TABLE_TAG_8_1, Resources.ICN_MENUBAR_TAG_8, false, true,  false); //$NON-NLS-1$

	private final static String[] NAMES =
	{
		LocaleBundle.getString("CCMovieTags.TAG_00"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTags.TAG_01"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTags.TAG_02"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTags.TAG_03"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTags.TAG_04"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTags.TAG_05"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTags.TAG_06"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTags.TAG_07"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTags.TAG_08"),   //$NON-NLS-1$
	};

	public final int Index;
	public final String Description;

	public final MultiSizeIconRef IconOff;
	public final MultiSizeIconRef IconOn;
	public final MultiSizeIconRef Image;

	public final boolean IsMovieTag;
	public final boolean IsSeriesTag;
	public final boolean IsEpisodeTag;


	private final static EnumWrapper<CCSingleTag> wrapper = new EnumWrapper<>(TAG_BAD_QUALITY);

	private CCSingleTag(int idx, String dscIdent, MultiSizeIconRef off, MultiSizeIconRef on, MultiSizeIconRef img, boolean mov, boolean ser, boolean epi) {
		Index = idx;
		Description = LocaleBundle.getString(dscIdent);
		IconOff = off;
		IconOn = on;
		Image = img;
		IsMovieTag = mov;
		IsSeriesTag = ser;
		IsEpisodeTag = epi;
	}

	public static EnumWrapper<CCSingleTag> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}

	@Override
	public int asInt() {
		return Index;
	}

	public static int compare(CCSingleTag s1, CCSingleTag s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	public static CCSingleTag find(int v) throws TagNotFoundException {
		for (CCSingleTag t : CCTagList.TAGS) {
		    if (t.Index == v) return t;
		}
		throw new TagNotFoundException(v);
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

	@Override
	public CCSingleTag[] evalues() {
		return CCSingleTag.values();
	}
}
