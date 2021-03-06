package de.jClipCorn.database.history;

import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CCCombinedHistoryEntry
{
	public CCHistoryTable Table;
	public String ID;

	public CCDateTime Timestamp1;
	public CCDateTime Timestamp2;

	public CCHistoryAction Action;

	public List<CCHistorySingleChange> Changes = new ArrayList<>();
	public int HistoryRowCount;

	private ICCDatabaseStructureElement _sourceLink = null;

	public String formatTime() {
		if (Timestamp1.isEqual(Timestamp2)) return Timestamp1.toStringUINormal();

		if (Timestamp1.date.isEqual(Timestamp2.date)) {
			return Str.format("{0} [{1} - {2}]", Timestamp1.date.toStringUINormal(), Timestamp1.time.toStringUINormal(), Timestamp2.time.toStringUINormal()); //$NON-NLS-1$
		}

		return Str.format("{0} - {1}", Timestamp1.toStringUINormal(), Timestamp2.toStringUINormal()); //$NON-NLS-1$
	}

	public ICCDatabaseStructureElement getSourceElement() {
		return _sourceLink;
	}

	public boolean isTrivialViewedChangesOnly() {
		if (Action != CCHistoryAction.UPDATE) return false;
		if (Table != CCHistoryTable.MOVIES && Table != CCHistoryTable.SERIES && Table != CCHistoryTable.EPISODES) return false;

		for (CCHistorySingleChange hsc : Changes) {
			if (!hsc.isTrivialViewedChange()) return false;
		}

		return true;
	}

	public boolean isIDChangeOnly() {
		//if (Action != CCHistoryAction.UPDATE) return false;
		if (Table != CCHistoryTable.INFO) return false;
		//if (Changes.size() != 1) return false;

		if (Str.equals(ID, "LAST_ID")) return true; //$NON-NLS-1$
		if (Str.equals(ID, "LAST_COVERID")) return true; //$NON-NLS-1$

		return false;
	}

	public boolean isGroupOrderingChange() {
		if (Action != CCHistoryAction.UPDATE) return false;
		if (Table != CCHistoryTable.GROUPS) return false;
		if (Changes.size() != 1) return false;
		
		if (CCStreams.iterate(Changes).any(c -> !Str.equals(c.Field, "ORDERING"))) return false; //$NON-NLS-1$
		
		return true;
	}

	public void setSourceLink(HashMap<Integer, ICCDatabaseStructureElement> elements) {
		if (Table == CCHistoryTable.COVERS) return;
		if (Table == CCHistoryTable.INFO) return;
		if (Table == CCHistoryTable.GROUPS) return;

		int iid;
		try	{
			iid = Integer.parseInt(ID);
		} catch (NumberFormatException e) {
			_sourceLink = null;
			return;
		}
		_sourceLink = elements.getOrDefault(iid, null);
	}

	public Opt<String> getNewValue(String key) {
		Opt<String> r = Opt.empty();
		for (CCHistorySingleChange change : Changes) {
			if (change.Field.equalsIgnoreCase(key)) r = Opt.of(change.NewValue);
		}
		return r;
	}
}
