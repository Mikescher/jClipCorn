package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CCGroupList {
	private final List<String> list;
	
	private CCGroupList(List<String> lst) {
		list = lst;
		
		Collections.sort(list);
	}

	private CCGroupList() {
		list = new ArrayList<>();
	}

	private CCGroupList(String[] groups) {
		list = new ArrayList<>();
		
		for (String str : groups) {
			list.add(str);
		}

		Collections.sort(list);
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public int count() {
		return list.size();
	}

	public static CCGroupList parse(String data) {
		return new CCGroupList(data.split("|")); //$NON-NLS-1$
	}

	public static CCGroupList createEmpty() {
		return new CCGroupList();
	}

	public CCGroupList add(String value) {
		value = value.replace("|", ""); //$NON-NLS-1$ //$NON-NLS-2$
				
		List<String> new_list = new ArrayList<>(list);
		
		if (! new_list.contains(value)) new_list.add(value);
		
		return new CCGroupList(new_list);
	}

	public String toSerializationString() {
		return String.join("|", list); //$NON-NLS-1$
	}
}
