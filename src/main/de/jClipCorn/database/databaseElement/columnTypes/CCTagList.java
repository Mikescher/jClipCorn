package de.jClipCorn.database.databaseElement.columnTypes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import de.jClipCorn.database.util.iterators.TagsIterator;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.stream.CCStream;

public class CCTagList {
	
	public final static CCSingleTag TAG_BAD_QUALITY 	= new CCSingleTag(0, "CCMovieTags.TAG_00", Resources.ICN_TABLE_TAG_0_0, Resources.ICN_TABLE_TAG_0_1, Resources.ICN_MENUBAR_TAG_0, true,  true,  true);  //$NON-NLS-1$
	public final static CCSingleTag TAG_MISSING_TIME 	= new CCSingleTag(1, "CCMovieTags.TAG_01", Resources.ICN_TABLE_TAG_1_0, Resources.ICN_TABLE_TAG_1_1, Resources.ICN_MENUBAR_TAG_1, true,  false, true);  //$NON-NLS-1$
	public final static CCSingleTag TAG_FILE_CORRUPTED 	= new CCSingleTag(2, "CCMovieTags.TAG_02", Resources.ICN_TABLE_TAG_2_0, Resources.ICN_TABLE_TAG_2_1, Resources.ICN_MENUBAR_TAG_2, true,  false, true);  //$NON-NLS-1$
	public final static CCSingleTag TAG_WATCH_LATER 	= new CCSingleTag(3, "CCMovieTags.TAG_03", Resources.ICN_TABLE_TAG_3_0, Resources.ICN_TABLE_TAG_3_1, Resources.ICN_MENUBAR_TAG_3, true,  true,  true);  //$NON-NLS-1$
	public final static CCSingleTag TAG_WRONG_LANGUAGE 	= new CCSingleTag(4, "CCMovieTags.TAG_04", Resources.ICN_TABLE_TAG_4_0, Resources.ICN_TABLE_TAG_4_1, Resources.ICN_MENUBAR_TAG_4, true,  true,  true);  //$NON-NLS-1$
	public final static CCSingleTag TAG_WATCH_NEVER 	= new CCSingleTag(5, "CCMovieTags.TAG_05", Resources.ICN_TABLE_TAG_5_0, Resources.ICN_TABLE_TAG_5_1, Resources.ICN_MENUBAR_TAG_5, true,  true,  true);  //$NON-NLS-1$
	public final static CCSingleTag TAG_WATCH_CAMRIP	= new CCSingleTag(6, "CCMovieTags.TAG_06", Resources.ICN_TABLE_TAG_6_0, Resources.ICN_TABLE_TAG_6_1, Resources.ICN_MENUBAR_TAG_6, true,  true,  true);  //$NON-NLS-1$
	public final static CCSingleTag TAG_WATCH_MICDUBBED	= new CCSingleTag(7, "CCMovieTags.TAG_07", Resources.ICN_TABLE_TAG_7_0, Resources.ICN_TABLE_TAG_7_1, Resources.ICN_MENUBAR_TAG_7, true,  true,  true);  //$NON-NLS-1$
	public final static CCSingleTag TAG_WATCH_CANCELLED	= new CCSingleTag(8, "CCMovieTags.TAG_08", Resources.ICN_TABLE_TAG_8_0, Resources.ICN_TABLE_TAG_8_1, Resources.ICN_MENUBAR_TAG_8, false, true,  false); //$NON-NLS-1$

	public final static CCSingleTag[] TAGS =
	{
		TAG_BAD_QUALITY,
		TAG_MISSING_TIME,
		TAG_FILE_CORRUPTED,
		TAG_WATCH_LATER,
		TAG_WRONG_LANGUAGE,
		TAG_WATCH_NEVER,
		TAG_WATCH_CAMRIP,
		TAG_WATCH_MICDUBBED,
		TAG_WATCH_CANCELLED,
	};

	public final static int TAGCOUNT = 16;
	public final static int ACTIVETAGS = TAGS.length;

	private boolean[] tags = new boolean[16];
	private boolean createIcon = true;
	private ImageIcon iconcache;

	public CCTagList() {
		clear();
		
		onUpdate();
	}

	public CCTagList(short val) {
		parseFromShort(val);
		
		onUpdate();
	}

	@SuppressWarnings("CopyConstructorMissesField")
	public CCTagList(CCTagList t) {
		for (int i = 0; i < TAGCOUNT; i++) {
			tags[i] = t.getTag(i);
		}
		
		onUpdate();
	}

	public void clear() {
		for (int i = 0; i < TAGCOUNT; i++) {
			tags[i] = false;
		}
		onUpdate();
	}

	public void parseFromShort(short val) {
		for (int i = 0; i < TAGCOUNT; i++) {
			tags[i] = (val & (1 << i)) != 0;
		}
		onUpdate();
	}

	public short asShort() {
		short v = 0;

		for (int i = TAGCOUNT - 1; i >= 0; i--) {
			v = (short) (v << 1);
			if (tags[i]) {
				v++;
			}
		}

		return v;
	}

	public boolean getTag(CCSingleTag t) {
		return tags[t.Index];
	}

	public boolean getTag(int c) {
		return tags[c];
	}

	public void setTag(CCSingleTag t, boolean v) {
		tags[t.Index] = v;
		onUpdate();
	}

	public void setTag(int c, boolean v) {
		if (isTagActive(c)) {
			tags[c] = v;
			onUpdate();
		}
	}

	public void doUnion(CCTagList t) {
		for (int i = 0; i < TAGCOUNT; i++) {
			tags[i] |= t.getTag(i);
		}
		onUpdate();
	}

	private static boolean isTagActive(int c) {
		return c >= 0 && c < ACTIVETAGS;
	}

	private Image getTagIconImage(int c) {
		if (!isTagActive(c)) return null;

		return (tags[c]) ? TAGS[c].getOnImage() : TAGS[c].getOffImage();
	}
	
	public static ImageIcon getOnIcon(int c) {
		if (!isTagActive(c)) return null;
		return TAGS[c].getOnIcon();
	}
	
	public ImageIcon getTagIcon(int c) {
		if (!isTagActive(c)) return null;

		return (tags[c]) ? TAGS[c].getOnIcon() : TAGS[c].getOffIcon();
	}
	
	private void onUpdate() {
		createIcon = true;
	}

	public void switchTag(CCSingleTag t) {
		tags[t.Index] = !tags[t.Index];
		onUpdate();
	}

	public void switchTag(int c) {
		if (isTagActive(c)) {
			tags[c] = !tags[c];
			onUpdate();
		}
	}

	public CCTagList copy() {
		return new CCTagList(this);
	}

	public static String getName(int c) {
		if (!isTagActive(c)) return "{??}"; //$NON-NLS-1$
		return TAGS[c].Description;
	}
	
	public static String[] getList() {
		String[] result = new String[ACTIVETAGS];
		
		for (int i = 0; i < ACTIVETAGS; i++) {
			result[i] = getName(i);
		}
		
		return result;
	}
	
	public String getAsString() {
		StringBuilder b = new StringBuilder();
		
		boolean first = true;
		for (int i = 0; i < ACTIVETAGS; i++) {
			if (tags[i]) {
				if (! first) {
					b.append(" | "); //$NON-NLS-1$
				}
				b.append(getName(i));
				first = false;
			}
		}
		
		return b.toString();
	}
	
	@Override
	public String toString() {
		return getAsString();
	}
	
	private int getTagCount() {
		int c = 0;
		for (int i = 0; i < ACTIVETAGS; i++) {
			c += tags[i] ? 1 : 0;
		}
		return c;
	}
	
	private void createIcon() {
		int count = getTagCount();
		
		if (count == 0) {
			iconcache = null;
			return;
		}
		
		BufferedImage bi = new BufferedImage(count*18, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		
		int  pos = 0;
		for (int i = 0; i < ACTIVETAGS; i++) {
			if (tags[i]) {
				g.drawImage(getTagIconImage(i), pos*18, 0, null);
				
				pos++;
			}
		}
		
		iconcache = new ImageIcon(bi);
		
		createIcon = false;
	}
	
	public ImageIcon getIcon() {
		if (createIcon) createIcon();
		
		return iconcache;
	}

	public static int compare(CCTagList o1, CCTagList o2) {
		return Short.compare(o1.asShort(), o2.asShort());
	}

	public void drawOnImage(BufferedImage bi, boolean use32px) {
		int posX = 8;
		
		for (int i = 0; i < ACTIVETAGS; i++) {
			if (getTag(i)) {
				
				if (i == TAG_WATCH_LATER.Index || i == TAG_WATCH_NEVER.Index) continue;
				
				if (use32px) {
					bi.getGraphics().drawImage(TAGS[i].Image.getImage32x32(), posX, 8, null);
					posX += 32;
					posX += 8;
				} else {
					bi.getGraphics().drawImage(TAGS[i].Image.getImage16x16(), posX, 8, null);
					posX += 16;
					posX += 4;
				}
			}
		}
	}

	public boolean hasTags() {
		return getTagCount() > 0;
	}

	public static CCTagList createEmpty() {
		return new CCTagList();
	}

	public CCStream<CCSingleTag> iterate() {
		return new TagsIterator(this);
	}
}
