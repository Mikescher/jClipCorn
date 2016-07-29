package de.jClipCorn.util.parser.imagesearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.settings.ImageSearchImplementation;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public class CoverImageParser implements FinishListener {
	private final ProgressCallbackListener proglistener;
	private final UpdateCallbackListener updatelistener;
	private final UpdateCallbackListener finishlistener;
	private final String searchText;
	private final CCMovieTyp typ;
	private final CCOnlineReference reference;
	
	private final List<AbstractImageSearch> searchImplementations;
	
	public CoverImageParser(ProgressCallbackListener pcl, UpdateCallbackListener ucl, UpdateCallbackListener fcl, CCMovieTyp typ, String search, CCOnlineReference ref) {
		this.proglistener = pcl;
		this.updatelistener = ucl;
		this.finishlistener = fcl;
		this.searchText = search;
		this.typ = typ;
		this.reference = ref;
		
		searchImplementations = new ArrayList<>();
		
		for (ImageSearchImplementation impl : CCProperties.getInstance().PROP_IMAGESEARCH_IMPL.getValue()) {
			searchImplementations.add(impl.getImplementation(this, updatelistener, proglistener));
		}
	}
	
	public void start() {
		final CopyOnWriteArrayList<String> exclusions = new CopyOnWriteArrayList<>();
		
		int max = 0;
		for (AbstractImageSearch impl : searchImplementations) {
			max  += impl.getProgressMax();
		}
		proglistener.setMax(max);

		for (AbstractImageSearch impl : searchImplementations) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					impl.start(exclusions, searchText, typ, reference);
				}
			}, "THREAD_COVERIMAGEPARSER_" + impl.getClass().getSimpleName().toUpperCase()).start(); //$NON-NLS-1$
		}
		
		// test for zero threads
		finish();
	}
	
	public void stop() {
		for (AbstractImageSearch impl : searchImplementations) {
			impl.forceStop();
		}
	}
	
	public boolean isFinished() {
		for (AbstractImageSearch impl : searchImplementations) {
			if (!impl.finished) return false;
		}
		return true;
	}

	@Override
	public void finish() {
		// One Thread has finished
		
		if (isFinished()) {
			// All thrads finished

			proglistener.reset();
			finishlistener.onUpdate(null);
		}
	}
}
