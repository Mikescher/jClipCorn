package de.jClipCorn.gui.frames.parseWatchDataFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.util.CCDate;

public class EpisodeWatchDataChangedSet extends WatchDataChangeSet {
	private CCEpisode eps;
	private CCDate date;
	
	public EpisodeWatchDataChangedSet(CCDate d, CCEpisode e, boolean newViewed) {
		super(newViewed);
		this.eps = e;
		this.date = d;
	}

	@Override
	public String getDate() {
		return date.getSimpleStringRepresentation();
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
		else if (newState && ! eps.getLastViewed().isEquals(date))
			return String.format("%s -> %s", eps.getLastViewed().getSimpleStringRepresentation(), date.getSimpleStringRepresentation()); //$NON-NLS-1$
		else
			return "#"; //$NON-NLS-1$
	}

	@Override
	public void execute() {
		if (eps.isViewed() ^ newState) {
			eps.setViewed(newState);
		}
		if (newState && ! eps.getLastViewed().isEquals(date)) {
			eps.setLastViewed(date);
		}
	}
}
