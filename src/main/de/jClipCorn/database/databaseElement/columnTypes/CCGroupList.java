package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jClipCorn.database.CCMovieList;

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

	public static CCGroupList parse(CCMovieList ml, String data) {
		if (data.isEmpty()) return new CCGroupList();
		
		List<CCGroup> gl = new ArrayList<>();
		for (String str : data.split(SEPERATOR)) {
			gl.add(ml.getOrCreateGroup(str));
		}
		
		return new CCGroupList(gl);
	}

	public static CCGroupList createEmpty() {
		return new CCGroupList();
	}

	public CCGroupList add(CCMovieList ml, String value) {
		value = value.replace(SEPERATOR, ""); //$NON-NLS-1$
				
		List<CCGroup> new_list = new ArrayList<>(list);
		
		if (! contains(value)) new_list.add(ml.getOrCreateGroup(value));
		
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
}
