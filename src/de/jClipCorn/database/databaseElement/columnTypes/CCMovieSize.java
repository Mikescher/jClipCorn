package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.FileSizeFormatter;

public class CCMovieSize {
	private long bytes;

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

	public void add(CCMovieSize afz) {
		add(afz.getBytes());
	}
	
	public void add(long afz) {
		bytes += afz;
	}

	public void setBytes(long sz) {
		bytes = sz;
	}

	public static int compare(CCMovieSize o1, CCMovieSize o2) {
		return Long.compare(o1.getBytes(), o2.getBytes());
	}
	
	@Override
	public String toString() {
		return getFormatted();
	}

	public void div(long divider) {
		bytes /= divider;
	}
}
