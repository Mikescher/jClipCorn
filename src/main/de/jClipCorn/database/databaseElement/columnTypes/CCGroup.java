package de.jClipCorn.database.databaseElement.columnTypes;

import java.awt.Color;
import java.util.regex.Pattern;

public class CCGroup implements Comparable<CCGroup> {
	private final static int COLOR_TAG_ALPHA = 224;

	public final static Pattern REGEX_GROUP_SYNTAX = Pattern.compile("\\[\\[[A-Za-z0-9\\-_ ]+\\]\\]"); //$NON-NLS-1$
	private final static Pattern REGEX_GROUP_NAME = Pattern.compile("^[A-Za-z0-9\\-_ ]+$"); //$NON-NLS-1$
	
	private static int staticGroupCounter = 10000;
	
	public final static Color[] TAG_COLORS = new Color[]
	{
		new Color( 26, 188, 156),
		new Color( 41, 128, 185),
		new Color(142,  68, 173),
		new Color(241, 196,  15),
		new Color(127, 140, 141),
		new Color(243, 156,  18),
		new Color(211,  84,   0),
		new Color(192,  57,  43),
		new Color(189, 195, 199),
		new Color( 46, 204, 113),
	};
	
	public final String Name;
	public final int Order;
	public final Color Color;
	public final boolean DoSerialize;
	public final String Parent;
	public final boolean Visible;
	
	private CCGroup(String n, int o, Color c, boolean s, String p, boolean v) {
		Name = n;
		Order = o;
		Color = new Color(c.getRed(), c.getGreen(), c.getBlue(), COLOR_TAG_ALPHA);
		DoSerialize = s;
		Parent = p;
		Visible = v;
	}
	
	public static CCGroup create(String name) {
		CCGroup g = new CCGroup(name, staticGroupCounter, TAG_COLORS[staticGroupCounter % TAG_COLORS.length], true, "", true); //$NON-NLS-1$
		staticGroupCounter++;
		
		return g;
	}
	
	public static CCGroup create(String name, int order, int color, boolean ser, String parent, boolean visible) {
		CCGroup g = new CCGroup(name, order, new Color(color), ser, parent, visible);
		staticGroupCounter++;
		
		return g;
	}
	
	public static CCGroup create(String name, int order, Color color, boolean ser, String parent, boolean visible) {
		CCGroup g = new CCGroup(name, order, color, ser, parent, visible);
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

	public String getHexColor() {
		return String.format("#%02X%02X%02X", Color.getRed(), Color.getGreen(), Color.getBlue()); //$NON-NLS-1$
	}
}
