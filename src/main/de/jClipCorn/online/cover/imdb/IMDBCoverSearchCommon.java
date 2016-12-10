package de.jClipCorn.online.cover.imdb;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.online.cover.AbstractImageSearch;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public abstract class IMDBCoverSearchCommon extends AbstractImageSearch {

	protected IMDBImageParserHelper helper = IMDBImageParserHelper.GetConfiguredHelper();
	
	public IMDBCoverSearchCommon(FinishListener fc, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(fc, uc, pc);
	}
	
	protected abstract String getSearchResultFromHTML(String searchhtml);
	
	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, OnlineSearchType typ, CCOnlineReference reference) {
		String searchurl = helper.getSearchURL(searchText, typ);
		String searchhtml = HTTPUtilities.getHTML(searchurl, true, false);
		String direkturl = getSearchResultFromHTML(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(direkturl, true, false);

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
			String posterhtml = HTTPUtilities.getHTML(posterurl, true, false);

			List<String> posterlinks = helper.extractImageLinks(posterhtml);

			if (posterlinks.size() > 0) {
				int currCID = 0;
				for (String url : posterlinks) {
					if (exclusions.contains(url)) continue;
					exclusions.add(url);
					
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true, false);

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
				String allhtml = HTTPUtilities.getHTML(allurl, true, false);

				List<String> alllinks = helper.extractImageLinks(allhtml);

				int currCID = 0;
				for (String url : alllinks) {
					if (exclusions.contains(url)) continue;
					exclusions.add(url);
					
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true, false);

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
