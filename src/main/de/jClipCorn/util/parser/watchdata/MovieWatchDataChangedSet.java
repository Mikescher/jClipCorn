package de.jClipCorn.util.parser.watchdata;

import de.jClipCorn.database.databaseElement.CCMovie;

public class MovieWatchDataChangedSet extends WatchDataChangeSet {
	private CCMovie mov;
	
	public MovieWatchDataChangedSet(CCMovie m, boolean newViewed) {
		super(newViewed);
		this.mov = m;
	}

	@Override
	public String getDate() {
		return "-"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return mov.getCompleteTitle();
	}

	@Override
	public String getSubInfo() {
		return "-"; //$NON-NLS-1$
	}

	@Override
	public String getChange() {
		if (mov.isViewed() ^ newState)
			return String.format("%d -> %d", mov.isViewed()?1:0, newState?1:0); //$NON-NLS-1$
		else
			return "#"; //$NON-NLS-1$
	}

	@Override
	public void execute() {
		mov.setViewed(newState);
	}
}
