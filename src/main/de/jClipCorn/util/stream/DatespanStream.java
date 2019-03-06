package de.jClipCorn.util.stream;

import de.jClipCorn.util.datetime.CCDate;

public class DatespanStream extends CCStream<CCDate> {

	private final CCDate start; // inclusive
	private final CCDate end;  // exclusive

	private CCDate curr;

	public DatespanStream(CCDate start, CCDate end) {
		super();
		this.start = start;
		this.end   = end;

		this.curr  = start;
	}
	
	@Override
	public boolean hasNext() {
		return curr.getDayDifferenceTo(end) > 1;
	}

	@Override
	public CCDate next() {
		CCDate d = curr;
		curr = curr.getAddDay(1);
		return d;
	}

	@Override
	protected CCStream<CCDate> cloneFresh() {
		return new DatespanStream(start, end);
	}

}
