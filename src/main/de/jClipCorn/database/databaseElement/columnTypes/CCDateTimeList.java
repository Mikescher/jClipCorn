package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.JSONListFormatException;
import de.jClipCorn.util.formatter.HTMLFormatter;
import de.jClipCorn.util.stream.CCIterable;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.IterableStream;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.*;

/**
 * A viewed history: datetimes, each with a (usually empty) comment.
 * Iterating yields the datetimes, {@link #entries()} the datetimes with their comments.
 */
@SuppressWarnings("nls")
public class CCDateTimeList implements CCIterable<CCDateTime> {
	private static final String JSON_KEY_DATETIME = "d";
	private static final String JSON_KEY_COMMENT  = "c";

	private final List<CCViewedEntry> list;

	private CCDateTimeList(List<CCViewedEntry> lst) {
		lst.sort(CCViewedEntry::compare);

		list = Collections.unmodifiableList(lst);
	}

	private CCDateTimeList() {
		list = Collections.unmodifiableList(new ArrayList<>());
	}

	private CCDateTimeList(String[] rawData) throws CCFormatException {
		List<CCViewedEntry> lst = new ArrayList<>();
		for (String str : rawData) lst.add(CCViewedEntry.create(CCDateTime.createFromSQL(str)));
		lst.sort(CCViewedEntry::compare);

		list = Collections.unmodifiableList(lst);
	}

	public CCDateTimeList(CCDateTime datetime) {
		List<CCViewedEntry> lst = new ArrayList<>();
		lst.add(CCViewedEntry.create(datetime));

		list = Collections.unmodifiableList(lst);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public boolean isEmptyOrOnlyUnspecified() {
		for (CCViewedEntry e : list) if (! e.datetime.isUnspecifiedDateTime()) return false;

		return true;
	}

	public boolean isOnlyUnspecified() {
		if (isEmpty()) return false;

		for (CCViewedEntry e : list) if (! e.datetime.isUnspecifiedDateTime()) return false;

		return true;
	}

	public boolean any() {
		return !list.isEmpty();
	}

	public int count() {
		return list.size();
	}

	/** Legacy format: comma separated datetimes, no comments */
	public static CCDateTimeList parse(String data) throws CCFormatException {
		if (data.isEmpty()) return new CCDateTimeList();

		return new CCDateTimeList(data.split(","));
	}

	public static CCDateTimeList createEmpty() {
		return new CCDateTimeList();
	}

	public static CCDateTimeList create(List<CCDateTime> dtlist) {
		List<CCViewedEntry> lst = new ArrayList<>();
		for (CCDateTime dt : dtlist) lst.add(CCViewedEntry.create(dt));
		return new CCDateTimeList(lst);
	}

	public static CCDateTimeList create(CCDateTime... dtlist) {
		return create(Arrays.asList(dtlist));
	}

	public static CCDateTimeList createFromEntries(List<CCViewedEntry> entries) {
		return new CCDateTimeList(new ArrayList<>(entries));
	}

	public CCDateTimeList add(CCDateTime value) {
		return add(value, Str.Empty);
	}

	/** Does nothing when an entry that {@link CCDateTime#isEqual} the value already exists */
	public CCDateTimeList add(CCDateTime value, String comment) {
		List<CCViewedEntry> new_list = new ArrayList<>(list);

		if (! contains(value))
			new_list.add(CCViewedEntry.create(value, comment));

		return new CCDateTimeList(new_list);
	}

	/** Legacy format (comma separated datetimes): loses the comments */
	public String toSerializationString() {
		StringBuilder builder = new StringBuilder();

		boolean first = true;
		for (CCViewedEntry e : list) {
			if (! first) builder.append(",");
			first = false;

			builder.append(e.datetime.getSQLStringRepresentation());
		}

		return builder.toString();
	}

	public static CCDateTimeList create(CCDate date) {
		return new CCDateTimeList(CCDateTime.create(date));
	}

	/** <code>[{"d":"2016-11-27 16:48:30","c":"Kino"}]</code> - written with JSONStringer because JSONObject does not keep the key order */
	public String asJSONArray() {
		JSONStringer w = new JSONStringer();
		w.array();
		for (CCViewedEntry e : list) {
			w.object();
			w.key(JSON_KEY_DATETIME).value(e.datetime.getSQLStringRepresentation());
			w.key(JSON_KEY_COMMENT).value(e.comment);
			w.endObject();
		}
		w.endArray();
		return w.toString();
	}

	/** Also accepts entries without comment (plain datetime strings instead of objects), element by element */
	public static CCDateTimeList fromJSONArray(String json) throws CCFormatException {
		if (Str.isNullOrWhitespace(json) || json.equals("[]")) return new CCDateTimeList();

		try {
			JSONArray arr = new JSONArray(json);
			List<CCViewedEntry> lst = new ArrayList<>();
			for (int i = 0; i < arr.length(); i++) {
				Object v = arr.get(i);
				if (v instanceof String str) {
					lst.add(CCViewedEntry.create(CCDateTime.createFromSQL(str)));
				} else if (v instanceof JSONObject obj) {
					String comment = obj.has(JSON_KEY_COMMENT) ? obj.getString(JSON_KEY_COMMENT) : Str.Empty;
					lst.add(CCViewedEntry.create(CCDateTime.createFromSQL(obj.getString(JSON_KEY_DATETIME)), comment));
				} else {
					throw new JSONException("Invalid viewed history entry: " + v);
				}
			}
			return new CCDateTimeList(lst);
		} catch (JSONException | CCFormatException e) {
			throw new JSONListFormatException(json, e);
		}
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
		for (CCViewedEntry e : list) {
			if (e.datetime.isEqual(time)) return true;
		}
		return false;
	}

	public boolean containsDate(CCDate date) {
		for (CCViewedEntry e : list) {
			if (e.datetime.date.isEqual(date)) return true;
		}
		return false;
	}

	public boolean containsDateBetween(CCDate first, CCDate second) {
		for (CCViewedEntry e : list) {
			if (e.datetime.date.isGreaterEqualsThan(first) && e.datetime.date.isLessEqualsThan(second)) return true;
		}
		return false;
	}

	@Override
	public Iterator<CCDateTime> iterator() {
		return ccstream();
	}

	public CCStream<CCDateTime> ccstream() {
		return new IterableStream<>(list).map(e -> e.datetime);
	}

	public CCStream<CCViewedEntry> entries() {
		return new IterableStream<>(list);
	}

	/*
	 * List of dates
	 * (dates can be not unique (cause of different times)
	 */
	public List<CCDate> getDateList() {
		List<CCDate> l = new ArrayList<>();
		for (CCViewedEntry e : list) {
			l.add(e.datetime.date);
		}
		return l;
	}

	/*
	 * List of dates
	 * (dates can be not unique (cause of different times)
	 */
	public List<CCDate> getSpecifiedDateList() {
		List<CCDate> l = new ArrayList<>();
		for (CCViewedEntry e : list) {
			if (! e.datetime.isUnspecifiedDateTime()) l.add(e.datetime.date);
		}
		return l;
	}

	public List<CCDateTime> getSpecifiedList() {
		List<CCDateTime> l = new ArrayList<>();
		for (CCViewedEntry e : list) {
			if (! e.datetime.isUnspecifiedDateTime()) l.add(e.datetime);
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
		for (CCViewedEntry e : list) {
			b.append(HTMLFormatter.escape(e.toStringUIShort())).append("<br/>");
		}
		b.append(Str.repeat("<!--HACK-->", Math.max(0, hiddenHack)));
		b.append("</html>");

		return b.toString();
	}

	public CCDateTimeList removeLast() {
		List<CCViewedEntry> lst = new ArrayList<>(list);

		if (! lst.isEmpty()) lst.remove(lst.size()-1);

		return new CCDateTimeList(lst);
	}

	public boolean isValid() {
		for (CCViewedEntry e : list) {
			if (e.datetime.date.isMinimum()) return false;
			if (e.datetime.isGreaterThan(CCDateTime.getCurrentDateTime())) return false;
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
			if (!list.get(i).isExactEqual(that.list.get(i))) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 37);
		hcb.append(list.size());
		for (CCViewedEntry e : list) hcb.append(e);
		return hcb.toHashCode();
	}
}
