package de.jClipCorn.database.databaseElement.columnTypes;

import java.awt.*;
import java.util.regex.Pattern;

public class CCHexColor {
	private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9A-F]{6}$");
	
	private final Color color;
	
	private CCHexColor(String hex) {
		this.color = parseHexToColor(hex);
	}
	
	public static CCHexColor create(String hex) {
		return new CCHexColor(hex);
	}

	@SuppressWarnings("nls")
	public static CCHexColor fromColor(Color color) {
		String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
		return new CCHexColor(hex);
	}
	
	public static CCHexColor fromRGB(int rgb) {
		Color c = new Color(rgb);
		return fromColor(c);
	}

	private static Color parseHexToColor(String hex) {
		if (hex == null || !HEX_COLOR_PATTERN.matcher(hex).matches()) {
			throw new IllegalArgumentException("Invalid hex color format: " + hex + ". Must be #RRGGBB format with uppercase letters.");
		}

		int r = Integer.parseInt(hex.substring(1, 3), 16);
		int g = Integer.parseInt(hex.substring(3, 5), 16);
		int b = Integer.parseInt(hex.substring(5, 7), 16);

		return new Color(r, g, b);
	}

	@SuppressWarnings("nls")
	public String getHex() {
		return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}

	public Color toColor() {
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	public Color toColorWithAlpha(int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
	
	public int toRGB() {
		return color.getRGB();
	}
	
	@Override
	public String toString() {
		return getHex();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		CCHexColor other = (CCHexColor) obj;
		return color.getRGB() == other.color.getRGB();
	}
	
	@Override
	public int hashCode() {
		return color.hashCode();
	}
}