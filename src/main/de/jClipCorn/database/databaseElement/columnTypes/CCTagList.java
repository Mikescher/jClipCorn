package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.database.util.iterators.TagsIterator;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.TagNotFoundException;
import de.jClipCorn.util.stream.CCIterable;
import de.jClipCorn.util.stream.CCStream;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class CCTagList implements CCIterable<CCSingleTag>
{
	public static final CCTagList EMPTY = new CCTagList();

	public final static CCSingleTag[] TAGS = CCSingleTag.values();

	public final static int TAGCOUNT = 16;
	public final static int ACTIVETAGS = TAGS.length;


	private final boolean[] tags = new boolean[TAGCOUNT];
	private boolean createIcon = true;
	private ImageIcon iconcache;

	private CCTagList()
	{
		for (int i = 0; i < TAGCOUNT; i++) tags[i] = false;
	}

	private CCTagList(short val)
	{
		for (int i = 0; i < TAGCOUNT; i++) tags[i] = (val & (1 << i)) != 0;
	}

	private CCTagList(CCTagList t, int idx, boolean val)
	{
		for (int i = 0; i < TAGCOUNT; i++) tags[i] = t.getTag(i);
		tags[idx]=val;
	}

	private CCTagList(boolean[] dat)
	{
		System.arraycopy(dat, 0, tags, 0, TAGCOUNT);
	}

	public static CCTagList fromShort(short data) {
		return new CCTagList(data);
	}

	public static CCTagList create(CCSingleTag... list) {
		CCTagList e = new CCTagList();
		for (CCSingleTag t : list) e = e.getSetTag(t, true);
		return e;
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

	public CCTagList getSetTag(CCSingleTag t, boolean v) {
		if (getTag(t) == v) return this;
		return new CCTagList(this, t.Index, v);
	}

	public CCTagList getSetTag(int c, boolean v) {
		if (!isTagActive(c)) return this;
		if (getTag(c) == v) return this;
		return new CCTagList(this, c, v);
	}

	public CCTagList getUnion(CCTagList t) {
		boolean[] n = new boolean[TAGCOUNT];
		for (int i = 0; i < TAGCOUNT; i++) n[i] = tags[i] | t.getTag(i);
		return new CCTagList(n);
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

	public CCTagList getSwitchTag(CCSingleTag t) {
		return new CCTagList(this, t.Index, !getTag(t));
	}

	public CCTagList getSwitchTag(int c) {
		if (!isTagActive(c)) return this;
		return new CCTagList(this, c, !getTag(c));
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
				
				if (i == CCSingleTag.TAG_WATCH_LATER.Index || i == CCSingleTag.TAG_WATCH_NEVER.Index) continue;

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

	public String serialize() {
		return ccstream().stringjoin(g -> String.valueOf(g.Index), ";"); //$NON-NLS-1$
	}

	public static CCTagList deserialize(String v) throws TagNotFoundException {
		boolean[] n = new boolean[TAGCOUNT];
		for (int i = 0; i < TAGCOUNT; i++) n[i] = false;

		for (String str : v.split(";")) //$NON-NLS-1$
		{
			if (!Str.isNullOrWhitespace(str)) n[CCSingleTag.find(Integer.parseInt(str)).Index] = true;
		}
		return new CCTagList(n);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		return isEqual((CCTagList) o);
	}

	public boolean isEqual(CCTagList that) {
		for (int i = 0; i < TAGCOUNT; i++) {
			if (tags[i] != that.tags[i]) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(tags)
				.toHashCode();
	}

	@Override
	public Iterator<CCSingleTag> iterator() {
		return ccstream();
	}

	public CCStream<CCSingleTag> ccstream() {
		return new TagsIterator(this);
	}
}
