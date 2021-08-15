package de.jClipCorn.features.online.cover;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.properties.enumerations.ImageSearchImplementation;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CoverImageParser implements FinishListener {
	private final ProgressCallbackListener proglistener;
	private final UpdateCallbackListener updatelistener;
	private final UpdateCallbackListener finishlistener;
	private final String searchText;
	private final OnlineSearchType typ;
	private final CCOnlineReferenceList reference;
	
	private final List<AbstractImageSearch> searchImplementations;

	private final CCMovieList movielist;

	public CoverImageParser(CCMovieList ml, ProgressCallbackListener pcl, UpdateCallbackListener ucl, UpdateCallbackListener fcl, CCDBElementTyp typ, String search, CCOnlineReferenceList ref) {
		super();

		this.movielist = ml;
		this.proglistener = pcl;
		this.updatelistener = ucl;
		this.finishlistener = fcl;
		this.searchText = search;

		switch (typ) {
		case MOVIE:
			this.typ = OnlineSearchType.MOVIES;
			break;
		case SERIES:
			this.typ = OnlineSearchType.SERIES;
			break;
		default:
			this.typ = OnlineSearchType.MOVIES;
			CCLog.addDefaultSwitchError(this, typ);
			break;
		}

		this.reference = ref;
		
		searchImplementations = new ArrayList<>();
		
		for (ImageSearchImplementation impl : movielist.ccprops().PROP_IMAGESEARCH_IMPL.getValue()) {
			searchImplementations.add(impl.getImplementation(movielist, this, updatelistener, proglistener));
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
			if (!reference.isEmpty())
			{
				for	(CCSingleOnlineReference soref : reference)
				{
					String tname = "THREAD_COVERIMAGEPARSER_" + impl.getClass().getSimpleName().toUpperCase() + "_" + soref.type; //$NON-NLS-1$ //$NON-NLS-2$
					new Thread(() -> impl.start(exclusions, searchText, typ, soref), tname).start();
				}
			}
			else
			{
				String tname = "THREAD_COVERIMAGEPARSER_" + impl.getClass().getSimpleName().toUpperCase() + "_" + "EMPTY"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				new Thread(() -> impl.start(exclusions, searchText, typ, CCSingleOnlineReference.EMPTY), tname).start();
			}
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
