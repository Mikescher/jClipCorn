package de.jClipCorn.database.history;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;

import java.util.ArrayList;
import java.util.List;

public class CCCombinedHistoryEntry
{
	public CCHistoryTable Table;
	public String ID;

	public CCDateTime Timestamp1;
	public CCDateTime Timestamp2;

	public CCHistoryAction Action;

	public List<CCHistorySingleChange> Changes = new ArrayList<>();

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

	private ICCDatabaseStructureElement findSourceElement() {
		if (Table == CCHistoryTable.COVERS) return null;
		if (Table == CCHistoryTable.INFO) return null;
		if (Table == CCHistoryTable.GROUPS) return null;

		int iid;
		try	{
			iid = Integer.parseInt(ID);
		} catch (NumberFormatException e) {
			return null;
		}
		CCDatabaseElement e1 = CCMovieList.getInstance().findDatabaseElement(iid);
		if (e1 != null) return e1;

		CCSeason e2 = CCMovieList.getInstance().iteratorSeasons().firstOrNull(p -> p.getLocalID() == iid);
		if (e2 != null) return e2;

		CCEpisode e3 = CCMovieList.getInstance().iteratorEpisodes().firstOrNull(p -> p.getLocalID() == iid);
		if (e3 != null) return e3;

		return null;
	}

	public void finishInit() {
		_sourceLink = findSourceElement();
	}

	public boolean isTrivialViewedChangesOnly() {
		if (Action != CCHistoryAction.UPDATE) return false;
		if (Table != CCHistoryTable.ELEMENTS && Table != CCHistoryTable.EPISODES) return false;

		for (CCHistorySingleChange hsc : Changes) {
			if (!hsc.isTrivialViewedChange()) return false;
		}

		return true;
	}
}
