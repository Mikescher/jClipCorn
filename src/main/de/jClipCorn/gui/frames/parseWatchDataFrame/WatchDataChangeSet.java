package de.jClipCorn.gui.frames.parseWatchDataFrame;

public abstract class WatchDataChangeSet {

	protected boolean newState;
	
	public WatchDataChangeSet(boolean newViewed) {
		this.newState = newViewed;
	}
	
	public abstract String getDate();
	public abstract String getName();
	public abstract String getSubInfo();
	public abstract String getChange();
	
	public abstract void execute();
}
