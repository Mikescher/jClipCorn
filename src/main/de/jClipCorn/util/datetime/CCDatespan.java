package de.jClipCorn.util.datetime;

import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.DatespanStream;

public class CCDatespan {
	public final CCDate start; // inclusive
	public final CCDate end;   // exclusive
	
	public CCDatespan(CCDate s, CCDate e) {
		start = s;
		end = e;
	}

	public CCStream<CCDate> iterateDays() {
		return new DatespanStream(start, end.getAddDay(1));
	}

	public int getDayCount() {
		return start.getDayDifferenceTo(end);
	}
}
