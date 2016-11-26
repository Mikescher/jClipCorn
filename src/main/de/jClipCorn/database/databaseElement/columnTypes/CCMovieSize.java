package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.formatter.FileSizeFormatter;

public class CCMovieSize {
	public static final CCMovieSize ZERO = new CCMovieSize(0);
	private final long bytes;

	public CCMovieSize(long bytes) {
		this.bytes = bytes;
	}

	public CCMovieSize() {
		this.bytes = 0;
	}

	public long getBytes() {
		return bytes;
	}

	public String getFormatted() {
		return FileSizeFormatter.format(bytes);
	}

	public static int compare(CCMovieSize o1, CCMovieSize o2) {
		return Long.compare(o1.getBytes(), o2.getBytes());
	}
	
	@Override
	public String toString() {
		return getFormatted();
	}
	
	public static CCMovieSize add(CCMovieSize a, CCMovieSize b) {
		return new CCMovieSize(a.bytes + b.bytes);
	}
	
	public static CCMovieSize addBytes(CCMovieSize a, long b) {
		return new CCMovieSize(a.bytes + b);
	}
	
	public static CCMovieSize div(CCMovieSize a, long divider) {
		return new CCMovieSize(a.bytes / divider);
	}
}
