package de.jClipCorn.database.databaseElement.columnTypes;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CCMovieTags {
	public final static int TAGCOUNT = 16;
	public final static int ACTIVETAGS = 5;

	private final static String[] ICONS_ON = { Resources.ICN_TABLE_TAG_0_1, Resources.ICN_TABLE_TAG_1_1, Resources.ICN_TABLE_TAG_2_1, Resources.ICN_TABLE_TAG_3_1, Resources.ICN_TABLE_TAG_4_1 };

	private final static String[] ICONS_OFF = { Resources.ICN_TABLE_TAG_0_0, Resources.ICN_TABLE_TAG_1_0, Resources.ICN_TABLE_TAG_2_0, Resources.ICN_TABLE_TAG_3_0, Resources.ICN_TABLE_TAG_4_0 };

	private final static String[] NAMES = { LocaleBundle.getString("CCMovieTags.TAG_00"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieTags.TAG_01"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieTags.TAG_02"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieTags.TAG_03"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieTags.TAG_04") //$NON-NLS-1$
	};

	private boolean[] tags = new boolean[16];
	private boolean createIcon = true;
	private ImageIcon iconcache;

	public CCMovieTags() {
		clear();
		
		onUpdate();
	}

	public CCMovieTags(short val) {
		parseFromShort(val);
		
		onUpdate();
	}

	public CCMovieTags(CCMovieTags t) {
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

	public boolean getTag(int c) {
		return tags[c];
	}

	public boolean setTag(int c, boolean v) {
		if (isTagActive(c)) {
			tags[c] = v;
			onUpdate();
			return true;
		}
		return false;
	}

	public void doUnion(CCMovieTags t) {
		for (int i = 0; i < TAGCOUNT; i++) {
			tags[i] |= t.getTag(i);
		}
		onUpdate();
	}

	public static boolean isTagActive(int c) {
		return c >= 0 && c < ACTIVETAGS;
	}

	public BufferedImage getTagImage(int c) {
		if (isTagActive(c)) {
			if (tags[c]) {
				return CachedResourceLoader.getImage(ICONS_ON[c]);
			} else {
				return CachedResourceLoader.getImage(ICONS_OFF[c]);
			}
		} else {
			return null;
		}
	}
	
	public static ImageIcon getOnIcon(int c) {
		if (isTagActive(c)) {
			return CachedResourceLoader.getImageIcon(ICONS_ON[c]);
		} else {
			return null;
		}
	}
	
	public static ImageIcon getOffIcon(int c) {
		if (isTagActive(c)) {
			return CachedResourceLoader.getImageIcon(ICONS_OFF[c]);
		} else {
			return null;
		}
	}
	
	public ImageIcon getTagIcon(int c) {
		if (isTagActive(c)) {
			if (tags[c]) {
				return CachedResourceLoader.getImageIcon(ICONS_ON[c]);
			} else {
				return CachedResourceLoader.getImageIcon(ICONS_OFF[c]);
			}
		} else {
			return null;
		}
	}
	
	private void onUpdate() {
		createIcon = true;
	}

	public boolean switchTag(int c) {
		if (isTagActive(c)) {
			tags[c] = !tags[c];
			onUpdate();
		}
		return tags[c];
	}

	public CCMovieTags copy() {
		return new CCMovieTags(this);
	}

	public static String getName(int c) {
		if (isTagActive(c)) {
			return NAMES[c];
		} else {
			return "{??}"; //$NON-NLS-1$
		}
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
	
	public int getTagCount() {
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
		
		BufferedImage bi = new BufferedImage(count*16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		
		int  pos = 0;
		for (int i = 0; i < ACTIVETAGS; i++) {
			if (tags[i]) {
				g.drawImage(getTagImage(i), pos*16, 0, null);
				
				pos++;
			}
		}
		
		iconcache = new ImageIcon(bi);
		
		createIcon = false;
	}
	
	public ImageIcon getIcon() {
		if (createIcon) {
			createIcon();
		}
		
		return iconcache;
	}

	public static int compare(CCMovieTags o1, CCMovieTags o2) {
		return Short.compare(o1.asShort(), o2.asShort());
	}
}
