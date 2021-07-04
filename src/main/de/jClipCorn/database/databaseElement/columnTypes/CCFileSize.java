package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.formatter.FileSizeFormatter;
import org.jetbrains.annotations.NotNull;

public class CCFileSize implements Comparable<CCFileSize>  {
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

	public long getKiloBytes() {
		return bytes / 1024;
	}

	public long getMegaBytes() {
		return bytes / (1024 * 1024);
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return isEqual(this, (CCFileSize) o);
	}

	public static boolean isEqual(CCFileSize a, CCFileSize b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		return a.bytes == b.bytes;
	}

	@Override
	public int hashCode() {
		return (int) (bytes ^ (bytes >>> 32));
	}

	@Override
	public int compareTo(@NotNull CCFileSize o) {
		return Long.compare(this.bytes, o.bytes);
	}
}
