package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;

@SuppressWarnings("nls")
public class CCDateTimeList implements Iterable<CCDateTime> {
	private final List<CCDateTime> list;
	
	private CCDateTimeList(List<CCDateTime> lst) {
		list = lst;
		
		Collections.sort(list);
	}

	private CCDateTimeList() {
		list = new ArrayList<>();
	}

	private CCDateTimeList(String[] rawData) throws CCFormatException {
		list = new ArrayList<>();
		
		for (String str : rawData) {
			list.add(CCDateTime.createFromSQL(str));
		}

		Collections.sort(list);
	}
	
	public CCDateTimeList(CCDateTime datetime) {
		list = new ArrayList<>();
		list.add(datetime);

		Collections.sort(list);
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean any() {
		return !list.isEmpty();
	}
	
	public int count() {
		return list.size();
	}

	public static CCDateTimeList parse(String data) throws CCFormatException {
		if (data.isEmpty()) return new CCDateTimeList();
		
		return new CCDateTimeList(data.split(","));
	}

	public static CCDateTimeList createEmpty() {
		return new CCDateTimeList();
	}

	public static CCDateTimeList create(List<CCDateTime> dtlist) {
		return new CCDateTimeList(new ArrayList<>(dtlist));
	}

	public CCDateTimeList add(CCDateTime value) {
		List<CCDateTime> new_list = new ArrayList<>(list);
		
		if (! contains(value))
			new_list.add(value);
		
		return new CCDateTimeList(new_list);
	}

	public String toSerializationString() {
		StringBuilder builder = new StringBuilder();
		
		boolean first = true;
		for (CCDateTime dt : list) {
			if (! first) builder.append(",");
			first = false;
			
			builder.append(dt.getSQLStringRepresentation());
		}
		
		return builder.toString();
	}

	public static CCDateTimeList create(CCDate date) {
		return new CCDateTimeList(CCDateTime.create(date));
	}

	public CCDate getLastDateOrInvalid() {
		return isEmpty() ? CCDate.getMinimumDate() : list.get(count() - 1).date;
	}

	public CCDate getFirstDateOrInvalid() {
		return isEmpty() ? CCDate.getMinimumDate() : list.get(0).date;
	}

	public CCDate getAverageDateOrInvalid() {
		if (isEmpty()) return CCDate.getMinimumDate();
		
		return CCDate.getAverageDate(getDateList());
	}
	
	public boolean contains(CCDateTime time) {
		for (CCDateTime dt : list) {
			if (dt.isEquals(time)) return true;
		}
		return false;
	}

	@Override
	public Iterator<CCDateTime> iterator() {
		return list.iterator();
	}

	/*
	 * List of dates
	 * (dates can be not unique (cause of different times) 
	 */
	public List<CCDate> getDateList() {
		List<CCDate> l = new ArrayList<>();
		for (CCDateTime dt : list) {
			l.add(dt.date);
		}
		return l;
	}

	public String getHTMLListFormatted() {
		return getHTMLListFormatted(0);
	}

	public String getHTMLListFormatted(int hiddenHack) {
		if (isEmpty()) return "";
		
		StringBuilder b = new StringBuilder();
		
		b.append("<html>");
		for (CCDateTime datetime : list) {
			b.append(datetime.getSimpleShortStringRepresentation() + "<br/>");
		}
		for (int i = 0; i < hiddenHack; i++) {
			b.append("<!--HACK-->");
		}
		b.append("</html>");
		
		return b.toString();
	}

	public CCDateTimeList removeLast() {
		List<CCDateTime> lst = new ArrayList<>(list);
		
		if (! lst.isEmpty()) lst.remove(lst.size()-1);
			
		return new CCDateTimeList(lst);
	}
}
