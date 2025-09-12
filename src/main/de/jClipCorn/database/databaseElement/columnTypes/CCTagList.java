package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.TagNotFoundException;
import de.jClipCorn.util.stream.CCIterable;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CCTagList implements CCIterable<CCSingleTag>
{
	public static final CCTagList EMPTY = new CCTagList(Collections.emptySet());

	private final Set<CCSingleTag> tags;
	private boolean createIcon = true;
	private ImageIcon iconcache;

	private CCTagList(Set<CCSingleTag> tagSet)
	{
		this.tags = Collections.unmodifiableSet(new HashSet<>(tagSet));
	}

	public static CCTagList fromJSONArray(String jsonString) {
		if (Str.isNullOrWhitespace(jsonString) || jsonString.equals("[]")) {
			return EMPTY;
		}
		
		try {
			JSONArray array = new JSONArray(jsonString);
			Set<CCSingleTag> tagSet = new HashSet<>();
			
			for (int i = 0; i < array.length(); i++) {
				int tagIndex = array.getInt(i);
				CCSingleTag tag = CCSingleTag.find(tagIndex);
				tagSet.add(tag);
			}
			
			return new CCTagList(tagSet);
		} catch (JSONException | TagNotFoundException e) {
			// Return empty on parse error
			return EMPTY;
		}
	}

	public static CCTagList create(CCSingleTag... list) {
		if (list.length == 0) return EMPTY;
		
		Set<CCSingleTag> tagSet = new HashSet<>();
		Collections.addAll(tagSet, list);
		return new CCTagList(tagSet);
	}

	public String asJSONArray() {
		if (tags.isEmpty()) return "[]";

		return new JSONArray(CCStreams.iterate(tags).autosortByProperty(p -> p.Index).map(p -> p.Index)).toString();
	}

	public boolean getTag(CCSingleTag t) {
		return tags.contains(t);
	}

	public boolean getTag(int c) {
		try {
			return tags.contains(CCSingleTag.find(c));
		} catch (TagNotFoundException e) {
			return false;
		}
	}

	public CCTagList getSetTag(CCSingleTag t, boolean v) {
		if (getTag(t) == v) return this;
		
		Set<CCSingleTag> newTags = new HashSet<>(tags);
		if (v) {
			newTags.add(t);
		} else {
			newTags.remove(t);
		}
		return new CCTagList(newTags);
	}

	public CCTagList getSetTag(int c, boolean v) {
		try {
			CCSingleTag tag = CCSingleTag.find(c);
			return getSetTag(tag, v);
		} catch (TagNotFoundException e) {
			return this;
		}
	}

	public CCTagList getUnion(CCTagList t) {
		Set<CCSingleTag> newTags = new HashSet<>(tags);
		newTags.addAll(t.tags);
		return new CCTagList(newTags);
	}

	private Image getTagIconImage(CCSingleTag tag) {
		return tag.getOnImage();
	}
	
	public static ImageIcon getOnIcon(int c) {
		try {
			return CCSingleTag.find(c).getOnIcon();
		} catch (TagNotFoundException e) {
			return null;
		}
	}
	
	public ImageIcon getTagIcon(int c) {
		try {
			CCSingleTag tag = CCSingleTag.find(c);
			return tags.contains(tag) ? tag.getOnIcon() : tag.getOffIcon();
		} catch (TagNotFoundException e) {
			return null;
		}
	}

	public CCTagList getSwitchTag(CCSingleTag t) {
		return getSetTag(t, !getTag(t));
	}

	public CCTagList getSwitchTag(int c) {
		try {
			CCSingleTag tag = CCSingleTag.find(c);
			return getSwitchTag(tag);
		} catch (TagNotFoundException e) {
			return this;
		}
	}

	public static String getName(int c) {
		try {
			return CCSingleTag.find(c).Description;
		} catch (TagNotFoundException e) {
			return "{??}"; //$NON-NLS-1$
		}
	}
	
	public static String[] getList() {
		CCSingleTag[] allTags = CCSingleTag.values();
		String[] result = new String[allTags.length];
		
		for (int i = 0; i < allTags.length; i++) {
			result[i] = allTags[i].Description;
		}
		
		return result;
	}
	
	public String getAsString() {
		StringBuilder b = new StringBuilder();
		
		boolean first = true;
		for (CCSingleTag tag : tags) {
			if (!first) {
				b.append(" | "); //$NON-NLS-1$
			}
			b.append(tag.Description);
			first = false;
		}
		
		return b.toString();
	}
	
	@Override
	public String toString() {
		return getAsString();
	}
	
	private int getTagCount() {
		return tags.size();
	}
	
	private void createIcon() {
		int count = getTagCount();
		
		if (count == 0) {
			iconcache = null;
			return;
		}
		
		BufferedImage bi = new BufferedImage(count*18, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		
		int pos = 0;
		for (CCSingleTag tag : tags) {
			g.drawImage(getTagIconImage(tag), pos*18, 0, null);
			pos++;
		}
		
		iconcache = new ImageIcon(bi);
		
		createIcon = false;
	}
	
	public ImageIcon getIcon() {
		if (createIcon) createIcon();
		
		return iconcache;
	}

	public static int compare(CCTagList o1, CCTagList o2) {
		// Compare by JSON string representation for consistent ordering
		return o1.asJSONArray().compareTo(o2.asJSONArray());
	}

	public void drawOnImage(BufferedImage bi, boolean use32px) {
		int posX = 8;
		
		for (CCSingleTag tag : tags) {
			if (tag == CCSingleTag.TAG_WATCH_LATER || tag == CCSingleTag.TAG_WATCH_NEVER) continue;

			if (use32px) {
				bi.getGraphics().drawImage(tag.Image.getImage32x32(), posX, 8, null);
				posX += 32;
				posX += 8;
			} else {
				bi.getGraphics().drawImage(tag.Image.getImage16x16(), posX, 8, null);
				posX += 16;
				posX += 4;
			}
		}
	}

	public boolean hasTags() {
		return !tags.isEmpty();
	}

	public String serialize() {
		return ccstream().stringjoin(g -> String.valueOf(g.Index), ";"); //$NON-NLS-1$
	}

	public static CCTagList deserialize(String v) throws TagNotFoundException {
		if (Str.isNullOrWhitespace(v)) return EMPTY;
		
		Set<CCSingleTag> tagSet = new HashSet<>();
		for (String str : v.split(";")) //$NON-NLS-1$
		{
			if (!Str.isNullOrWhitespace(str)) {
				tagSet.add(CCSingleTag.find(Integer.parseInt(str)));
			}
		}
		return new CCTagList(tagSet);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		return isEqual((CCTagList) o);
	}

	public boolean isEqual(CCTagList that) {
		return tags.equals(that.tags);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(tags)
				.toHashCode();
	}

	@Override
	public @NotNull Iterator<CCSingleTag> iterator() {
		return tags.stream().sorted(CCSingleTag::compare).iterator();
	}

	public CCStream<CCSingleTag> ccstream() {
		return CCStreams.iterate(this);
	}
}