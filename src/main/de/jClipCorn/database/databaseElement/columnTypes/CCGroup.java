package de.jClipCorn.database.databaseElement.columnTypes;

import java.awt.Color;

import de.jClipCorn.database.CCMovieList;

public class CCGroup implements Comparable<CCGroup> {
	private final static int COLOR_TAG_ALPHA = 224;
	
	public final static Color[] TAG_COLORS = new Color[]
	{
		new Color( 26, 188, 156, COLOR_TAG_ALPHA),
		new Color( 41, 128, 185, COLOR_TAG_ALPHA),
		new Color(142,  68, 173, COLOR_TAG_ALPHA),
		new Color(241, 196,  15, COLOR_TAG_ALPHA),
		new Color(127, 140, 141, COLOR_TAG_ALPHA),
		new Color(243, 156,  18, COLOR_TAG_ALPHA),
		new Color(211,  84,   0, COLOR_TAG_ALPHA),
		new Color(192,  57,  43, COLOR_TAG_ALPHA),
		new Color(189, 195, 199, COLOR_TAG_ALPHA),
		new Color( 46, 204, 113, COLOR_TAG_ALPHA),
	};
	
	public final String Name;
	
	private CCGroup(String n) {
		Name = n;
	}
	
	public static CCGroup create(String name) {
		CCGroup g = new CCGroup(name);
		return g;
	}

	@Override
	public int compareTo(CCGroup o) {
		return Name.compareTo(o.Name);
	}
	
	@Override
	public String toString() {
		return Name;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof CCGroup) && ((CCGroup)o).Name.equals(Name);
	}

	@Override
	public int hashCode() {
		return Name.hashCode();
	}

	public Color getTagColor(CCMovieList ml) {
		int idx = ml.getGroupIndex(this);
		
		if (idx >= 0) return TAG_COLORS[idx % TAG_COLORS.length];
		
		else return TAG_COLORS[Math.abs(hashCode()) % TAG_COLORS.length];
	}
}
