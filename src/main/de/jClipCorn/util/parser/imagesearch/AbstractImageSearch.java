package de.jClipCorn.util.parser.imagesearch;

import java.util.concurrent.CopyOnWriteArrayList;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public abstract class AbstractImageSearch {
	
	protected final UpdateCallbackListener updateCallback;
	protected final ProgressCallbackListener progressCallback;
	protected final FinishListener finishCallback;
	
	protected boolean isForcestop = false;
	
	public boolean finished = false;
	
	public AbstractImageSearch(FinishListener fc, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super();

		updateCallback = uc;
		progressCallback = pc;
		finishCallback = fc;
	}
	
	public void forceStop() {
		isForcestop = true;
	}
	
	protected void onEndThread() {
		finished = true;
		
		finishCallback.finish();
	}
	
	public abstract void start(CopyOnWriteArrayList<String> exclusions, String searchText, CCMovieTyp typ, CCOnlineReference reference);

	public abstract int getProgressMax();
}
