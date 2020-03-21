package de.jClipCorn.util.parser.watchdata;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.util.datetime.CCDateTime;

public class EpisodeWatchDataChangedSet extends WatchDataChangeSet {
	private CCEpisode eps;
	private CCDateTime date;
	
	public EpisodeWatchDataChangedSet(CCDateTime d, CCEpisode e, boolean newViewed) {
		super(newViewed);
		this.eps = e;
		this.date = d;
	}

	@Override
	public String getDate() {
		return date.toStringUINormal();
	}

	@Override
	public String getName() {
		return eps.getSeries().getTitle();
	}

	@Override
	public String getSubInfo() {
		return eps.getStringIdentifier();
	}

	@Override
	public String getChange() {
		if (eps.isViewed() ^ newState)
			return String.format("%d -> %d", eps.isViewed()?1:0, newState?1:0); //$NON-NLS-1$
		else if (newState && ! eps.getViewedHistory().contains(date))
			return String.format("history += %s", date.toStringUINormal()); //$NON-NLS-1$
		else
			return "#"; //$NON-NLS-1$
	}

	@Override
	public void execute() {
		if (eps.isViewed() ^ newState) {
			eps.setViewedFromUI(newState);
			eps.addToViewedHistoryFromUI(date);
		} else if (newState && ! eps.getViewedHistory().contains(date)) {
			eps.addToViewedHistoryFromUI(date);
		}
	}
}
