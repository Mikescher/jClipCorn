package de.jClipCorn.features.online.cover.google;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.cover.AbstractImageSearch;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public class GooglePosterSearch extends AbstractImageSearch {

	public GooglePosterSearch(FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(fl, uc, pc);
	}

	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, OnlineSearchType typ, CCSingleOnlineReference reference) {
		String url = GoogleSearchCommon.getSearchURL(searchText, GoogleSearchCommon.SEARCH_APPENDIX_2);
		String html = HTTPUtilities.getJavascriptHTML(url, GoogleSearchCommon.HTMLUNIT_JS_TIMEOUT);
		List<String> links = GoogleSearchCommon.extractImageLinks(html, 16, exclusions, progressCallback);

		for (String s : links) {
			progressCallback.step();

			BufferedImage biu = HTTPUtilities.getImage(s);
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
