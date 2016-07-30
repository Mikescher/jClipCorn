package de.jClipCorn.util.parser.imagesearch;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.imagesearch.imageparser.GoogleImageParser;

public class GooglePosterSearch extends AbstractImageSearch {

	public GooglePosterSearch(FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(fl, uc, pc);
	}

	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, CCMovieTyp typ, CCOnlineReference reference) {
		String url = GoogleImageParser.getSearchURL(searchText, GoogleImageParser.SEARCH_APPENDIX_2);
		String html = HTTPUtilities.getJavascriptHTML(url, GoogleImageParser.HTMLUNIT_JS_TIMEOUT);
		List<String> links = GoogleImageParser.extractImageLinks(html, 16, exclusions, progressCallback);

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
