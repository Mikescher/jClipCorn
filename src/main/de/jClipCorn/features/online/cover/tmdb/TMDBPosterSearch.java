package de.jClipCorn.features.online.cover.tmdb;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.cover.AbstractImageSearch;
import de.jClipCorn.features.online.metadata.tmdb.TMDBParser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TMDBPosterSearch extends AbstractImageSearch {

	private final TMDBParser parser;
	
	public TMDBPosterSearch(CCMovieList ml, FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(ml, fl, uc, pc);
		parser = new TMDBParser(ml);
	}

	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, OnlineSearchType typ, CCSingleOnlineReference reference) {
		CCSingleOnlineReference tmpdbRef = reference;
		
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
				BufferedImage biu = HTTPUtilities.getImage(movielist, covers.get(i));
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
