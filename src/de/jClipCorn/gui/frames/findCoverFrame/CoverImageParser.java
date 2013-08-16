package de.jClipCorn.gui.frames.findCoverFrame;

import java.awt.image.BufferedImage;
import java.util.List;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.imageparser.GoogleImageParser;
import de.jClipCorn.util.parser.imageparser.ImDBImageParser;

public class CoverImageParser {
	private boolean thread_1_finished = false;
	private boolean thread_2_finished = false;
	private boolean thread_3_finished = false;
	
	private boolean thread_forcestop = false;
	
	private final ProgressCallbackListener proglistener;
	private final UpdateCallbackListener updatelistener;
	private final UpdateCallbackListener finishlistener;
	private final String searchText;
	private final CCMovieTyp typ;
	
	public CoverImageParser(ProgressCallbackListener pcl, UpdateCallbackListener ucl, UpdateCallbackListener fcl, CCMovieTyp typ, String search) {
		this.proglistener = pcl;
		this.updatelistener = ucl;
		this.finishlistener = fcl;
		this.searchText = search;
		this.typ = typ;
	}
	
	public void start() {
		thread_1_finished = false;
		thread_2_finished = false;
		thread_3_finished = false;
		
		thread_forcestop = false;
		
		proglistener.setMax(8 + 24 + 24);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				parseGoogleImages();
			}
		}, "THREAD_IMGPARSER_GOOGLE").start(); //$NON-NLS-1$
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				parseCoverSearch();
			}
		}, "THREAD_IMGPARSER_IMDB_1").start(); //$NON-NLS-1$
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				parseImDBImages();
			}
		}, "THREAD_IMGPARSE_IMDB_2").start(); //$NON-NLS-1$
	}
	
	public void stop() {
		thread_forcestop = true;
	}

	private void parseGoogleImages() { // Parse the Google Image Search
		String url = GoogleImageParser.getSearchURL(searchText);
		String json = HTTPUtilities.getHTML(url, false);
		List<String> links = GoogleImageParser.extractImageLinks(json);

		for (String s : links) {
			proglistener.step();

			BufferedImage biu = HTTPUtilities.getImage(s);
			if (biu != null) {
				updatelistener.onUpdate(biu);
			}
			
			if (thread_forcestop) {
				onEndThread(1);
				return;
			}
		}
		proglistener.step();

		// #################################################################################

		onEndThread(1);
	}

	private void parseImDBImages() { // Parses the Images from the first Result of ImDB CoverSearch
		String searchurl = ImDBImageParser.getSearchURL(searchText, typ);
		String searchhtml = HTTPUtilities.getHTML(searchurl, true);
		String direkturl = ImDBImageParser.getFirstSearchResult(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(direkturl, true);

			proglistener.step();

			BufferedImage imgMain = ImDBImageParser.getMainpageImage(direkthtml);
			if (imgMain != null) {
				updatelistener.onUpdate(imgMain);
			}

			proglistener.step();
			
			if (thread_forcestop) {
				onEndThread(2);
				return;
			}

			String posterurl = ImDBImageParser.getCoverUrlPoster(direkturl);
			String posterhtml = HTTPUtilities.getHTML(posterurl, true);

			List<String> posterlinks = ImDBImageParser.extractImageLinks(posterhtml);

			if (posterlinks.size() > 0) {
				int currCID = 0;
				for (String url : posterlinks) {
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
					if (imgurl != null) {
						updatelistener.onUpdate(imgurl);
					}
					
					if (thread_forcestop) {
						onEndThread(2);
						return;
					}
					
					proglistener.step();
				}
			} else {
				String allurl = ImDBImageParser.getCoverUrlAll(direkturl);
				String allhtml = HTTPUtilities.getHTML(allurl, true);

				List<String> alllinks = ImDBImageParser.extractImageLinks(allhtml);

				int currCID = 0;
				for (String url : alllinks) {
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
					if (imgurl != null) {
						updatelistener.onUpdate(imgurl);
					}
					
					if (thread_forcestop) {
						onEndThread(2);
						return;
					}
					
					proglistener.step();
				}
			}
		}
		
		proglistener.step();

		// #################################################################################

		onEndThread(2);
	}

	private void parseCoverSearch() { // Parses the Images from the second Result of ImDB CoverSearch
		String searchurl = ImDBImageParser.getSearchURL(searchText, typ);
		String searchhtml = HTTPUtilities.getHTML(searchurl, true);
		String direkturl = ImDBImageParser.getSecondSearchResult(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(direkturl, true);

			proglistener.step();

			BufferedImage imgMain = ImDBImageParser.getMainpageImage(direkthtml);
			if (imgMain != null) {
				updatelistener.onUpdate(imgMain);
			}
			
			if (thread_forcestop) {
				onEndThread(3);
				return;
			}

			proglistener.step();

			String posterurl = ImDBImageParser.getCoverUrlPoster(direkturl);
			String posterhtml = HTTPUtilities.getHTML(posterurl, true);

			List<String> posterlinks = ImDBImageParser.extractImageLinks(posterhtml);

			if (posterlinks.size() > 0) {
				int currCID = 0;
				for (String url : posterlinks) {
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
					if (imgurl != null) {
						updatelistener.onUpdate(imgurl);
					}
					
					if (thread_forcestop) {
						onEndThread(3);
						return;
					}
					
					proglistener.step();
				}
			} else {
				String allurl = ImDBImageParser.getCoverUrlAll(direkturl);
				String allhtml = HTTPUtilities.getHTML(allurl, true);

				List<String> alllinks = ImDBImageParser.extractImageLinks(allhtml);

				int currCID = 0;
				for (String url : alllinks) {
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
					if (imgurl != null) {
						updatelistener.onUpdate(imgurl);
					}
					
					if (thread_forcestop) {
						onEndThread(3);
						return;
					}
					
					proglistener.step();
				}
			}
		}
		
		proglistener.step();

		// #################################################################################

		onEndThread(3);
	}

	private void onEndThread(int thread) {
		switch (thread) {
		case 1:
			thread_1_finished = true;
			break;
		case 2:
			thread_2_finished = true;
			break;
		case 3:
			thread_3_finished = true;
			break;
		}
		
		if (isFinished()) {
			proglistener.reset();
			finishlistener.onUpdate(null);
		}
	}
	
	public boolean isFinished() {
		return thread_1_finished && thread_2_finished && thread_3_finished;
	}
}
