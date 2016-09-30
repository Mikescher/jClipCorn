package de.jClipCorn.database.databaseElement.columnTypes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.exceptions.GroupFormatException;
import de.jClipCorn.util.helper.ImageUtilities;

public class CCGroupList implements Iterable<CCGroup> {
	private static final String SEPERATOR = ";"; //$NON-NLS-1$
	
	private final List<CCGroup> list;
	
	private CCGroupList(List<CCGroup> lst) {
		list = lst;
		
		Collections.sort(list);
	}

	private CCGroupList() {
		list = new ArrayList<>();
	}

	private CCGroupList(CCGroup[] groups) {
		list = new ArrayList<>();
		
		for (CCGroup str : groups) {
			list.add(str);
		}

		Collections.sort(list);
	}

	public static CCGroupList create(Collection<CCGroup> keySet) {
		return new CCGroupList(new ArrayList<>(keySet));
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public int count() {
		return list.size();
	}

	public static CCGroupList parse(CCMovieList ml, String data) throws GroupFormatException {
		if (data.isEmpty()) return new CCGroupList();
		
		List<CCGroup> gl = new ArrayList<>();
		for (String str : data.split(SEPERATOR)) {
			if (! CCGroup.isValidGroupName(str))
				throw new GroupFormatException(str);
			
			gl.add(ml.getOrCreateGroup(str));
		}
		
		return new CCGroupList(gl);
	}

	public static CCGroupList createEmpty() {
		return new CCGroupList();
	}

	public CCGroupList getAdd(CCMovieList ml, String value) {
		value = value.replace(SEPERATOR, ""); //$NON-NLS-1$
				
		List<CCGroup> new_list = new ArrayList<>(list);
		
		if (! contains(value)) new_list.add(ml.getOrCreateGroup(value));
		
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
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof CCGroupList)) return false;
		
		CCGroupList gl = (CCGroupList) o;
		
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

	public void drawOnImage(CCMovieList ml, BufferedImage bi) {
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
		
		for (int i = 0; i < groupsSorted.size(); i++) {
			String group = groupsSorted.get(i).Name;
			
			int width = fm.stringWidth(group);
			
			g.setColor(groupsSorted.get(i).Color);
			
			g.fillRoundRect(
					right - MARGIN - PADDING_X - width - PADDING_X, 
					top + i * (height + 2 * PADDING_Y + 2 * MARGIN) + MARGIN, 
					2 * PADDING_X + width, 
					2 * PADDING_Y + height, 
					RADIUS, 
					RADIUS);

			g.setColor(Color.BLACK);
			
			g.drawRoundRect(
					right - MARGIN - PADDING_X - width - PADDING_X, 
					top + i * (height + 2 * PADDING_Y + 2 * MARGIN) + MARGIN, 
					2 * PADDING_X + width, 
					2 * PADDING_Y + height, 
					RADIUS, 
					RADIUS);
			
			g.drawString(
					group, 
					right - MARGIN - PADDING_X - width, 
					top + i * (height + 2 * PADDING_Y + 2 * MARGIN) + MARGIN + PADDING_Y + height - offset);
		}
		
		g.dispose();
	}
}
