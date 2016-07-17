package de.jClipCorn.gui.frames.parseOnlineFrame;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.util.datatypes.SortableString;

public class ParseOnlineDialogElement implements Comparable<ParseOnlineDialogElement> {
	public final SortableString Title;
	public final CCOnlineReference Reference;
	
	public ParseOnlineDialogElement(String t, int o, CCOnlineReference r) {
		Title = new SortableString(t, o);
		Reference = r;
	}
	
	@Override
	public String toString() {
		return Title.toString();
	}

	@Override
	public int compareTo(ParseOnlineDialogElement o) {
		return Title.compareTo(o.Title);
	}

	@Override
	public int hashCode() {
		return Title.hashCode() ^ Reference.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ParseOnlineDialogElement))
			return false;
		if (obj == this)
			return true;

		return ((ParseOnlineDialogElement)obj).Title.equals(Title) && ((ParseOnlineDialogElement)obj).Reference.equals(Reference);
	}
}
