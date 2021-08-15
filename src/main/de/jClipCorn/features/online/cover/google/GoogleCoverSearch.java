package de.jClipCorn.features.online.cover.google;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.cover.AbstractImageSearch;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GoogleCoverSearch extends AbstractImageSearch {

	public GoogleCoverSearch(CCMovieList ml, FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(ml, fl, uc, pc);
	}

	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, OnlineSearchType typ, CCSingleOnlineReference reference) {
		String url = GoogleSearchCommon.getSearchURL(searchText, GoogleSearchCommon.SEARCH_APPENDIX_1);
		String html = HTTPUtilities.getJavascriptHTML(url, GoogleSearchCommon.HTMLUNIT_JS_TIMEOUT);
		List<String> links = GoogleSearchCommon.extractImageLinks(html, 16, exclusions, progressCallback);

		for (String s : links) {
			progressCallback.step();

			BufferedImage biu = HTTPUtilities.getImage(movielist, s);
			if (biu != null) {
				updateCallback.onUpdate(biu);
			}
			
			if (isForcestop) {
				onEndThread();
				return;
			}
		}
		progressCallback.step();

		// #################################################################################

		onEndThread();
	}

	@Override
	public int getProgressMax() {
		return 16;
	}

}
