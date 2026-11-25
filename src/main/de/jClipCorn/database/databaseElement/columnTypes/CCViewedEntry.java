package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * One entry of a viewed history. Order and the duplicate check of {@link CCDateTimeList} only look at the
 * datetime; the comment (e.g. "Kino") is free text, empty when there is none.
 */
public class CCViewedEntry {
	public final CCDateTime datetime;
	public final String comment;

	private CCViewedEntry(@NotNull CCDateTime datetime, @NotNull String comment) {
		this.datetime = datetime;
		this.comment = comment;
	}

	public static CCViewedEntry create(CCDateTime datetime) {
		return new CCViewedEntry(datetime, Str.Empty);
	}

	public static CCViewedEntry create(CCDateTime datetime, String comment) {
		return new CCViewedEntry(datetime, comment == null ? Str.Empty : comment);
	}

	public boolean hasComment() {
		return !Str.isNullOrWhitespace(comment);
	}

	public CCViewedEntry withComment(String newComment) {
		return create(datetime, newComment);
	}

	public boolean isExactEqual(CCViewedEntry other) {
		if (other == null) return false;
		return datetime.isExactEqual(other.datetime) && comment.equals(other.comment);
	}

	public static int compare(CCViewedEntry a, CCViewedEntry b) {
		return a.datetime.compareTo(b.datetime);
	}

	public String toStringUINormal() {
		if (!hasComment()) return datetime.toStringUINormal();
		return datetime.toStringUINormal() + " (" + comment + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String toStringUIShort() {
		if (!hasComment()) return datetime.toStringUIShort();
		return datetime.toStringUIShort() + " (" + comment + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return isExactEqual((CCViewedEntry) o);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(datetime).append(comment).toHashCode();
	}

	@Override
	public String toString() {
		return toStringUINormal();
	}
}
