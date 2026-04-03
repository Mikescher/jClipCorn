package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.stream.CCIterable;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;
import org.json.JSONArray;
import org.json.JSONException;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.*;

public class CCStringList implements CCIterable<String> {
	public static final CCStringList EMPTY = new CCStringList();

	private final List<String> list;

	private CCStringList(List<String> lst) {
		List<String> filtered = new ArrayList<>();
		for (String s : lst) {
			if (s != null && !s.trim().isEmpty()) {
				filtered.add(s.trim());
			}
		}
		Collections.sort(filtered);
		list = Collections.unmodifiableList(filtered);
	}

	private CCStringList() {
		list = Collections.unmodifiableList(new ArrayList<>());
	}

	public static CCStringList create(Collection<String> values) {
		return new CCStringList(new ArrayList<>(values));
	}

	public static CCStringList create(String... values) {
		return new CCStringList(Arrays.asList(values));
	}
	
	public static CCStringList deserialize(String jsonString) throws CCFormatException {
		if (jsonString == null || jsonString.trim().isEmpty()) {
			return EMPTY;
		}
		
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			List<String> values = new ArrayList<>();
			for (int i = 0; i < jsonArray.length(); i++) {
				values.add(jsonArray.getString(i));
			}
			return new CCStringList(values);
		} catch (JSONException e) {
			throw new de.jClipCorn.util.exceptions.StringSpecFormatException(jsonString, "CCStringList JSON format");
		}
	}
	
	public String serialize() {
		if (isEmpty()) {
			return "";
		}
		
		JSONArray jsonArray = new JSONArray();
		for (String value : list) {
			jsonArray.put(value);
		}
		return jsonArray.toString();
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public int count() {
		return list.size();
	}
	
	public CCStringList getAdd(String value) {
		if (value == null || value.trim().isEmpty()) {
			return this;
		}
		
		value = value.trim();
		if (contains(value)) {
			return this;
		}
		
		List<String> new_list = new ArrayList<>(list);
		new_list.add(value);
		return new CCStringList(new_list);
	}
	
	public CCStringList getRemove(String value) {
		if (value == null || !contains(value)) {
			return this;
		}
		
		List<String> new_list = new ArrayList<>(list);
		new_list.remove(value.trim());
		return new CCStringList(new_list);
	}
	
	public boolean contains(String value) {
		if (value == null) return false;
		return list.contains(value.trim());
	}
	
	public boolean containsIgnoreCase(String value) {
		if (value == null) return false;
		String trimmedValue = value.trim();
		for (String item : list) {
			if (item.equalsIgnoreCase(trimmedValue)) {
				return true;
			}
		}
		return false;
	}
	
	public String get(int index) {
		return list.get(index);
	}
	
	@Override
	public Iterator<String> iterator() {
		return list.iterator();
	}
	
	public CCStream<String> ccstream() {
		return CCStreams.iterate(list);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CCStringList)) return false;
		return isEqual((CCStringList) o);
	}
	
	public boolean isEqual(CCStringList other) {
		if (other.count() != this.count()) return false;
		
		for (int i = 0; i < count(); i++) {
			if (!other.get(i).equals(get(i))) return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hc = 0;
		for (String s : list) {
			hc = 17 * hc ^ s.hashCode();
		}
		return hc;
	}
	
	public int drawOnImage(BufferedImage bi, boolean alpha, Color baseColor, int verticalOffset) {
		if (isEmpty()) return 0;

		final int PADDING_X = 4;
		final int PADDING_Y = 2;
		final int RADIUS = 5;
		final int MARGIN = 3;

		Graphics2D g = bi.createGraphics();

		Font old = g.getFont();
		g.setFont(new Font(old.getFontName(), Font.BOLD, old.getSize()));

		FontMetrics fm = g.getFontMetrics();

		Point tr = ImageUtilities.getTopRightNonTransparentPixel(bi);

		int right = tr.x;
		int top = tr.y;

		int height = fm.getHeight();
		int offset = fm.getDescent();

		int di = 0;
		for (String value : list) {
			String display = value;
			int width = fm.stringWidth(display);

			boolean limited = false;
			while (!display.isEmpty() && width > (bi.getWidth() - 2 * PADDING_X - MARGIN)) {
				display = display.substring(0, display.length() - 1);
				width = fm.stringWidth(display + "...");
				limited = true;
			}
			if (limited) display = display + "...";

			if (alpha)
				g.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), CCGroup.COLOR_TAG_ALPHA));
			else
				g.setColor(baseColor);

			int yBase = top + (verticalOffset + di) * (height + 2 * PADDING_Y + 2 * MARGIN) + MARGIN;

			g.fillRoundRect(
					right - MARGIN - PADDING_X - width - PADDING_X,
					yBase,
					2 * PADDING_X + width,
					2 * PADDING_Y + height,
					RADIUS,
					RADIUS);

			g.setColor(Color.BLACK);

			g.drawRoundRect(
					right - MARGIN - PADDING_X - width - PADDING_X,
					yBase,
					2 * PADDING_X + width,
					2 * PADDING_Y + height,
					RADIUS,
					RADIUS);

			g.drawString(
					display,
					right - MARGIN - PADDING_X - width,
					yBase + PADDING_Y + height - offset);

			di++;
		}

		g.dispose();

		return di;
	}

	@Override
	public String toString() {
		return String.join(", ", list);
	}
}