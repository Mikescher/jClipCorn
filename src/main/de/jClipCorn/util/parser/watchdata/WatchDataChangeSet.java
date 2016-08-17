package de.jClipCorn.util.parser.watchdata;

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
