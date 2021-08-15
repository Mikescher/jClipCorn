package de.jClipCorn.features.online.cover;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractImageSearch {
	
	protected final UpdateCallbackListener updateCallback;
	protected final ProgressCallbackListener progressCallback;
	protected final FinishListener finishCallback;
	protected final CCMovieList movielist;

	protected boolean isForcestop = false;
	
	public boolean finished = false;
	
	public AbstractImageSearch(CCMovieList ml, FinishListener fc, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super();
		movielist = ml;
		updateCallback = uc;
		progressCallback = pc;
		finishCallback = fc;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}
	
	public void forceStop() {
		isForcestop = true;
	}
	
	protected void onEndThread() {
		finished = true;
		
		finishCallback.finish();
	}
	
	public abstract void start(CopyOnWriteArrayList<String> exclusions, String searchText, OnlineSearchType typ, CCSingleOnlineReference reference);

	public abstract int getProgressMax();
}
