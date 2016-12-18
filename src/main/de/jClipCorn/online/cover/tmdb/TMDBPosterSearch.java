package de.jClipCorn.online.cover.tmdb;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.online.cover.AbstractImageSearch;
import de.jClipCorn.online.metadata.tmdb.TMDBParser;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public class TMDBPosterSearch extends AbstractImageSearch {

	private final static TMDBParser parser = new TMDBParser();
	
	public TMDBPosterSearch(FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(fl, uc, pc);
	}

	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, OnlineSearchType typ, CCOnlineReference reference) {
		CCOnlineReference tmpdbRef = reference;
		
		if (tmpdbRef.isUnset()) {
			switch (typ) {
			case MOVIES:
				tmpdbRef = parser.findMovieDirect(searchText);
				break;
			case SERIES:
				tmpdbRef = parser.findSeriesDirect(searchText);
				break;
			case BOTH:
				tmpdbRef = parser.findMovieDirect(searchText);
				if (tmpdbRef == null)tmpdbRef = parser.findSeriesDirect(searchText);
				break;

			default:
				CCLog.addDefaultSwitchError(this, typ);
				return;
			}
		}
		
		progressCallback.step();
		
		if (tmpdbRef.isUnset()) {
			onEndThread();
			return;
		}
		
		List<String> covers = parser.findCovers(tmpdbRef);
		
		for (int i = 0; i < 8; i++) {
			if (covers.size() > i) {
				BufferedImage biu = HTTPUtilities.getImage(covers.get(i));
				if (biu != null) 
					updateCallback.onUpdate(biu);
				else
					CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotGetImage", covers.get(i))); //$NON-NLS-1$
			}
			
			progressCallback.step();
		}
		
		// #################################################################################

		onEndThread();
	}

	@Override
	public int getProgressMax() {
		return 10;
	}

}
