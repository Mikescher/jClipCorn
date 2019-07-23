package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.IterableStream;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@SuppressWarnings("nls")
public class CCDateTimeList {
	private final List<CCDateTime> list;
	
	private CCDateTimeList(List<CCDateTime> lst) {
		Collections.sort(lst);

		list = Collections.unmodifiableList(lst);
	}

	private CCDateTimeList() {
		list = Collections.unmodifiableList(new ArrayList<>());
	}

	private CCDateTimeList(String[] rawData) throws CCFormatException {
		List<CCDateTime> lst = new ArrayList<>();
		for (String str : rawData) lst.add(CCDateTime.createFromSQL(str));
		Collections.sort(lst);

		list = Collections.unmodifiableList(lst);
	}
	
	public CCDateTimeList(CCDateTime datetime) {
		List<CCDateTime> lst = new ArrayList<>();
		lst.add(datetime);
		Collections.sort(lst);

		list = Collections.unmodifiableList(lst);
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public boolean isEmptyOrOnlyUnspecified() {
		for (CCDateTime dt : list) if (! dt.isUnspecifiedDateTime()) return false;
		
		return true;
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

	public CCDateTime getFirstOrInvalid() {
		List<CCDateTime> specList = getSpecifiedList();
		
		if (specList.isEmpty()) return CCDateTime.getMinimumDateTime();
		
		return specList.get(0);
	}

	public CCDateTime getLastOrInvalid() {
		List<CCDateTime> specList = getSpecifiedList();
		
		if (specList.isEmpty()) return CCDateTime.getMinimumDateTime();
		
		return specList.get(specList.size() - 1);
	}
	
	public CCDate getLastDateOrInvalid() {
		List<CCDate> specList = getSpecifiedDateList();
		
		if (specList.isEmpty()) return CCDate.getMinimumDate();
		
		return specList.get(specList.size() - 1);
	}

	public CCDate getFirstDateOrInvalid() {
		List<CCDate> specList = getSpecifiedDateList();
		
		if (specList.isEmpty()) return CCDate.getMinimumDate();
		
		return specList.get(0);
	}

	public CCDate getAverageDateOrInvalid() {
		List<CCDate> specList = getSpecifiedDateList();
		
		if (specList.isEmpty()) return CCDate.getMinimumDate();
		
		return CCDate.getAverageDate(specList);
	}
	
	public boolean contains(CCDateTime time) {
		for (CCDateTime dt : list) {
			if (dt.isEqual(time)) return true;
		}
		return false;
	}

	public boolean containsDate(CCDate date) {
		for (CCDateTime dt : list) {
			if (dt.date.isEqual(date)) return true;
		}
		return false;
	}

	public boolean containsDateBetween(CCDate first, CCDate second) {
		for (CCDateTime dt : list) {
			if (dt.date.isGreaterEqualsThan(first) && dt.date.isLessEqualsThan(second)) return true;
		}
		return false;
	}

	public CCStream<CCDateTime> iterator() {
		return new IterableStream<>(list);
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
	
	/*
	 * List of dates
	 * (dates can be not unique (cause of different times) 
	 */
	public List<CCDate> getSpecifiedDateList() {
		List<CCDate> l = new ArrayList<>();
		for (CCDateTime dt : list) {
			if (! dt.isUnspecifiedDateTime()) l.add(dt.date);
		}
		return l;
	}
	
	public List<CCDateTime> getSpecifiedList() {
		List<CCDateTime> l = new ArrayList<>();
		for (CCDateTime dt : list) {
			if (! dt.isUnspecifiedDateTime()) l.add(dt);
		}
		return l;
	}

	public String getHTMLListFormatted() {
		return getHTMLListFormatted(0);
	}

	public String getHTMLListFormatted(int hiddenHack) {
		if (isEmptyOrOnlyUnspecified()) return "";
		
		StringBuilder b = new StringBuilder();
		
		b.append("<html>");
		for (CCDateTime datetime : list) {
			b.append(datetime.toStringUIShort() + "<br/>");
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

	public boolean isValid() {
		for (CCDateTime cdt : list) {
			if (cdt.date.isMinimum()) return false;
			if (cdt.isGreaterThan(CCDateTime.getCurrentDateTime())) return false;
		}
		
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		return isEqual((CCDateTimeList) o);
	}

	public boolean isEqual(CCDateTimeList that) {
		if (list.size() != that.list.size()) return false;

		for (int i=0; i<list.size(); i++) {
			if (!list.get(i).isEqual(that.list.get(i))) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 37);
		hcb.append(list.size());
		for (CCDateTime dtl : list) hcb.append(dtl);
		return hcb.toHashCode();
	}
}
