package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.exceptions.GroupFormatException;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.stream.CCIterable;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class CCGroupList implements CCIterable<CCGroup> {
	public static final CCGroupList EMPTY = new CCGroupList();

	private static final String SEPERATOR = ";"; //$NON-NLS-1$
	
	private final List<CCGroup> list;
	
	private CCGroupList(List<CCGroup> lst) {
		Collections.sort(lst);
		list = Collections.unmodifiableList(lst);
	}

	private CCGroupList() {
		list = Collections.unmodifiableList(new ArrayList<>());
	}

	private CCGroupList(CCGroup[] groups) {

		ArrayList<CCGroup> gl = new ArrayList<>(Arrays.asList(groups));
		Collections.sort(gl);
		list = Collections.unmodifiableList(gl);
	}

	public static CCGroupList create(Collection<CCGroup> keySet) {
		return new CCGroupList(new ArrayList<>(keySet));
	}

	public static CCGroupList create(CCGroup... g) {
		return new CCGroupList(Arrays.asList(g));
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public int count() {
		return list.size();
	}

	public static CCGroupList parseWithoutAddingNewGroups(CCMovieList ml, String data) throws GroupFormatException {
		if (data.isEmpty()) return new CCGroupList();
		
		List<CCGroup> gl = new ArrayList<>();
		for (String str : data.split(SEPERATOR)) {
			if (! CCGroup.isValidGroupName(str))
				throw new GroupFormatException(str);
			
			CCGroup g = ml.getGroupOrNull(str);
			if (g == null) g = CCGroup.create(str);
			
			gl.add(g);
		}
		
		return new CCGroupList(gl);
	}

	public CCGroupList getAdd(CCMovieList ml, String value) {
		value = value.replace(SEPERATOR, ""); //$NON-NLS-1$
				
		List<CCGroup> new_list = new ArrayList<>(list);
		
		if (! contains(value)) new_list.add(ml.getOrCreateAndAddGroup(value));
		
		return new CCGroupList(new_list);
	}

	public CCGroupList getAdd(CCGroup g) {
		List<CCGroup> new_list = new ArrayList<>(list);
		
		if (! contains(g)) new_list.add(g);
		
		return new CCGroupList(new_list);
	}

	public CCGroupList getRemove(CCGroup rem) {
		List<CCGroup> new_list = new ArrayList<>(list);
		
		new_list.remove(rem);

		return new CCGroupList(new_list);
	}

	public boolean contains(String name) {
		for (CCGroup ccGroup : list) {
			if (ccGroup.Name.equals(name)) return true;
		}
		return false;
	}

	public boolean contains(CCGroup group) {
		for (CCGroup ccGroup : list) {
			if (ccGroup.Name.equals(group.Name)) return true;
		}
		return false;
	}

	public boolean containsIgnoreCase(String name) {
		for (CCGroup ccGroup : list) {
			if (ccGroup.Name.equalsIgnoreCase(name)) return true;
		}
		return false;
	}

	public String toSerializationString() {
		if (isEmpty()) return ""; //$NON-NLS-1$
		
		StringBuilder b = new StringBuilder();
		
		boolean f = true;
		for (CCGroup ccGroup : list) {
			if (!f) b.append(SEPERATOR);
			b.append(ccGroup.Name);
			f = false;
		}
		
		return b.toString();
	}

	public CCGroup get(int i) {
		return list.get(i);
	}

	@Override
	public Iterator<CCGroup> iterator() {
		return list.iterator();
	}

	public CCStream<CCGroup> ccstream() {
		return CCStreams.iterate(list);
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof CCGroupList)) return false;
		return isEqual((CCGroupList) o);
	}

	public boolean isEqual(CCGroupList gl) {
		if (gl.count() != this.count()) return false;

		for (int i = 0; i < count(); i++) {
			if (! gl.get(i).equals(get(i))) return false; // it's ok - groups are always sorted
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hc = 0;
		for (CCGroup g : list) {
			hc = 17*hc ^ hc ^ g.hashCode();
		}
		return hc;
	}

	public void drawOnImage(CCMovieList ml, BufferedImage bi, boolean alpha) {
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
		
		List<CCGroup> groupsSorted = new ArrayList<>(list);
		groupsSorted.sort((a, b) -> Integer.compare(b.Name.length(), a.Name.length()));
		
		int di = 0;
		for (int ii = 0; ii < groupsSorted.size(); ii++) {
			
			if (!groupsSorted.get(ii).Visible) continue;
				
			String group = groupsSorted.get(ii).Name;
			int width = fm.stringWidth(group);

			var limited = false;
			while (!group.isEmpty() && width > (bi.getWidth() - 2 * PADDING_X - MARGIN )) {
				group = group.substring(0, group.length()-1);
				width = fm.stringWidth(group + "...");
				limited = true;
			}
			if (limited) group = group + "...";
			
			if (alpha)
				g.setColor(groupsSorted.get(ii).HexColor.toColorWithAlpha(CCGroup.COLOR_TAG_ALPHA));
			else
				g.setColor(groupsSorted.get(ii).HexColor.toColor());
			
			g.fillRoundRect(
					right - MARGIN - PADDING_X - width - PADDING_X, 
					top + di * (height + 2 * PADDING_Y + 2 * MARGIN) + MARGIN, 
					2 * PADDING_X + width, 
					2 * PADDING_Y + height, 
					RADIUS, 
					RADIUS);

			g.setColor(Color.BLACK);
			
			g.drawRoundRect(
					right - MARGIN - PADDING_X - width - PADDING_X, 
					top + di * (height + 2 * PADDING_Y + 2 * MARGIN) + MARGIN, 
					2 * PADDING_X + width, 
					2 * PADDING_Y + height, 
					RADIUS, 
					RADIUS);
			
			g.drawString(
					group, 
					right - MARGIN - PADDING_X - width, 
					top + di * (height + 2 * PADDING_Y + 2 * MARGIN) + MARGIN + PADDING_Y + height - offset);
			
			di++;
		}
		
		g.dispose();
	}

	public CCStream<CCGroup> iterate() {
		return CCStreams.iterate(list);
	}
}
