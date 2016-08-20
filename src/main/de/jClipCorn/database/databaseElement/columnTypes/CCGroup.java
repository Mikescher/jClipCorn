package de.jClipCorn.database.databaseElement.columnTypes;

import java.awt.Color;
import java.util.regex.Pattern;

public class CCGroup implements Comparable<CCGroup> {
	private final static int COLOR_TAG_ALPHA = 224;

	public final static Pattern REGEX_GROUP_SYNTAX = Pattern.compile("\\[\\[[A-Za-z0-0\\-_ ]+\\]\\]"); //$NON-NLS-1$
	private final static Pattern REGEX_GROUP_NAME = Pattern.compile("^[A-Za-z0-0\\-_ ]+$"); //$NON-NLS-1$
	
	private static int staticGroupCounter = 10000;
	
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
	public final int Order;
	public final Color Color;
	public final boolean DoSerialize;
	
	private CCGroup(String n, int o, Color c, boolean s) {
		Name = n;
		Order = o;
		Color = c;
		DoSerialize = s;
	}
	
	public static CCGroup create(String name) {
		CCGroup g = new CCGroup(name, staticGroupCounter, TAG_COLORS[staticGroupCounter % TAG_COLORS.length], true);
		staticGroupCounter++;
		
		return g;
	}
	
	public static CCGroup create(String name, int order, int color, boolean ser) {
		CCGroup g = new CCGroup(name, order, new Color(color), ser);
		staticGroupCounter++;
		
		return g;
	}
	
	public static boolean isValidGroupName(String name) {
		if (name == null) return false;
		
		if (name.trim().isEmpty()) return false;
		
		return REGEX_GROUP_NAME.matcher(name).matches();
	}

	@Override
	public int compareTo(CCGroup o) {
		return Integer.compare(this.Order, o.Order);
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
}
