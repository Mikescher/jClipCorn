package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.formatter.FileSizeFormatter;

public class CCFileSize {
	public static final CCFileSize ZERO = new CCFileSize(0);
	private final long bytes;

	public CCFileSize(long bytes) {
		this.bytes = bytes;
	}

	public CCFileSize() {
		this.bytes = 0;
	}

	public long getBytes() {
		return bytes;
	}

	public String getFormatted() {
		return FileSizeFormatter.format(bytes);
	}

	public static int compare(CCFileSize o1, CCFileSize o2) {
		return Long.compare(o1.getBytes(), o2.getBytes());
	}
	
	@Override
	public String toString() {
		return getFormatted();
	}
	
	public static CCFileSize add(CCFileSize a, CCFileSize b) {
		return new CCFileSize(a.bytes + b.bytes);
	}
	
	public static CCFileSize addBytes(CCFileSize a, long b) {
		return new CCFileSize(a.bytes + b);
	}
	
	public static CCFileSize div(CCFileSize a, long divider) {
		return new CCFileSize(a.bytes / divider);
	}
}
