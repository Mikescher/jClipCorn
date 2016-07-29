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
import de.jClipCorn.util.parser.imagesearch.imageparser.ImDBImageParser;

public class IMDbCoverSearch extends AbstractImageSearch {

	public IMDbCoverSearch(FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(fl, uc, pc);
	}

	@Override
	public void start(CopyOnWriteArrayList<String> exclusions, String searchText, CCMovieTyp typ, CCOnlineReference reference) {
		String searchurl = ImDBImageParser.getSearchURL(searchText, typ);
		String searchhtml = HTTPUtilities.getHTML(searchurl, true, false);
		String direkturl = ImDBImageParser.getFirstSearchResult(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(direkturl, true, false);

			progressCallback.step();

			BufferedImage imgMain = ImDBImageParser.getMainpageImage(direkthtml);
			if (imgMain != null) {
				updateCallback.onUpdate(imgMain);
			}

			progressCallback.step();
			
			if (isForcestop) {
				onEndThread();
				return;
			}

			String posterurl = ImDBImageParser.getCoverUrlPoster(direkturl);
			String posterhtml = HTTPUtilities.getHTML(posterurl, true, false);

			List<String> posterlinks = ImDBImageParser.extractImageLinks(posterhtml);

			if (posterlinks.size() > 0) {
				int currCID = 0;
				for (String url : posterlinks) {
					if (exclusions.contains(url)) continue;
					exclusions.add(url);
					
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true, false);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
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
				String allurl = ImDBImageParser.getCoverUrlAll(direkturl);
				String allhtml = HTTPUtilities.getHTML(allurl, true, false);

				List<String> alllinks = ImDBImageParser.extractImageLinks(allhtml);

				int currCID = 0;
				for (String url : alllinks) {
					if (exclusions.contains(url)) continue;
					exclusions.add(url);
					
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true, false);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
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
