package de.jClipCorn.util.parser.imagesearch;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.onlineparser.TMDBParser;

public class TMDBPosterSearch extends AbstractImageSearch {

	public TMDBPosterSearch(FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(fl, uc, pc);
	}

	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, CCDBElementTyp typ, CCOnlineReference reference) {
		CCOnlineReference tmpdbRef = reference;
		
		if (tmpdbRef.isUnset()) {
			if (typ == CCDBElementTyp.MOVIE)
				tmpdbRef = TMDBParser.findMovieDirect(searchText);
			else
				tmpdbRef = TMDBParser.findSeriesDirect(searchText);
		}
		
		progressCallback.step();
		
		if (tmpdbRef.isUnset()) {
			onEndThread();
			return;
		}
		
		List<String> covers = TMDBParser.findCovers(tmpdbRef);
		
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
