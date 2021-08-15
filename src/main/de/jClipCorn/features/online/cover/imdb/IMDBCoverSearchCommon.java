package de.jClipCorn.features.online.cover.imdb;

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

public abstract class IMDBCoverSearchCommon extends AbstractImageSearch {

	protected final IMDBImageParserHelper helper;
	
	public IMDBCoverSearchCommon(CCMovieList ml, FinishListener fc, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(ml, fc, uc, pc);
		helper = IMDBImageParserHelper.GetConfiguredHelper(ml);
	}
	
	protected abstract String getSearchResultFromHTML(String searchhtml);
	
	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, OnlineSearchType typ, CCSingleOnlineReference reference) {
		String searchurl = helper.getSearchURL(searchText, typ);
		String searchhtml = HTTPUtilities.getHTML(movielist, searchurl, true, false);
		String direkturl = getSearchResultFromHTML(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(movielist, direkturl, true, false);

			progressCallback.step();

			BufferedImage imgMain = helper.getMainpageImage(direkthtml);
			if (imgMain != null) {
				updateCallback.onUpdate(imgMain);
			}

			progressCallback.step();
			
			if (isForcestop) {
				onEndThread();
				return;
			}

			String posterurl = helper.getCoverUrlPoster(direkturl);
			String posterhtml = HTTPUtilities.getHTML(movielist, posterurl, true, false);

			List<String> posterlinks = helper.extractImageLinks(posterhtml);

			if (posterlinks.size() > 0) {
				int currCID = 0;
				for (String url : posterlinks) {
					if (exclusions.contains(url)) continue;
					exclusions.add(url);
					
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(movielist, url, true, false);

					BufferedImage imgurl = helper.getDirectImage(urlhtml);
					if (imgurl != null) {
						updateCallback.onUpdate(imgurl);
					}
					
					if (isForcestop) {
						onEndThread();
						return;
					}
					
					progressCallback.step();
				}
			} else {
				String allurl = helper.getCoverUrlAll(direkturl);
				String allhtml = HTTPUtilities.getHTML(movielist, allurl, true, false);

				List<String> alllinks = helper.extractImageLinks(allhtml);

				int currCID = 0;
				for (String url : alllinks) {
					if (exclusions.contains(url)) continue;
					exclusions.add(url);
					
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(movielist, url, true, false);

					BufferedImage imgurl = helper.getDirectImage(urlhtml);
					if (imgurl != null) {
						updateCallback.onUpdate(imgurl);
					}
					
					if (isForcestop) {
						onEndThread();
						return;
					}
					
					progressCallback.step();
				}
			}
		}
		
		progressCallback.step();

		// #################################################################################

		onEndThread();
	}

	@Override
	public int getProgressMax() {
		return 24;
	}
}
