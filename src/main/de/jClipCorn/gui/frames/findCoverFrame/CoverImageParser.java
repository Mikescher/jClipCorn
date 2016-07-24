package de.jClipCorn.gui.frames.findCoverFrame;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.imageparser.GoogleImageParser;
import de.jClipCorn.util.parser.imageparser.ImDBImageParser;
import de.jClipCorn.util.parser.onlineparser.TMDBParser;

public class CoverImageParser {
	private boolean thread_1_finished = false;
	private boolean thread_2_finished = false;
	private boolean thread_3_finished = false;
	private boolean thread_4_finished = false;
	private boolean thread_5_finished = false;
	
	private boolean thread_forcestop = false;
	
	private final ProgressCallbackListener proglistener;
	private final UpdateCallbackListener updatelistener;
	private final UpdateCallbackListener finishlistener;
	private final String searchText;
	private final CCMovieTyp typ;
	private final CCOnlineReference reference;
	
	public CoverImageParser(ProgressCallbackListener pcl, UpdateCallbackListener ucl, UpdateCallbackListener fcl, CCMovieTyp typ, String search, CCOnlineReference ref) {
		this.proglistener = pcl;
		this.updatelistener = ucl;
		this.finishlistener = fcl;
		this.searchText = search;
		this.typ = typ;
		this.reference = ref;
	}
	
	public void start() {
		thread_1_finished = false;
		thread_2_finished = false;
		thread_3_finished = false;
		thread_4_finished = false;
		thread_5_finished = false;
		
		thread_forcestop = false;
		
		final CopyOnWriteArrayList<String> exclusions = new CopyOnWriteArrayList<>();
		
		// 16  = GoogleImage_1
		// 16  = GoogleImage_2
		// 24  = IMDB_1 
		// 24  = IMDB_2
		// 10  = TMDB_1
		proglistener.setMax(16 + 16 + 24 + 24 + 10);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				parseGoogleImages1(exclusions);
			}
		}, "THREAD_IMGPARSER_GOOGLE_1").start(); //$NON-NLS-1$
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				parseGoogleImages2(exclusions);
			}
		}, "THREAD_IMGPARSER_GOOGLE_2").start(); //$NON-NLS-1$
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				parseCoverSearch(exclusions);
			}
		}, "THREAD_IMGPARSER_IMDB_1").start(); //$NON-NLS-1$
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				parseImDBImages(exclusions);
			}
		}, "THREAD_IMGPARSE_IMDB_2").start(); //$NON-NLS-1$
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				parseTMDBImages(exclusions);
			}
		}, "THREAD_IMGPARSER_TMDB_1").start(); //$NON-NLS-1$
	}
	
	public void stop() {
		thread_forcestop = true;
	}

	private void parseGoogleImages1(CopyOnWriteArrayList<String> exclusions) { // Parse the Google Image Search
		String url = GoogleImageParser.getSearchURL(searchText, GoogleImageParser.SEARCH_APPENDIX_1);
		String html = HTTPUtilities.getJavascriptHTML(url, GoogleImageParser.HTMLUNIT_JS_TIMEOUT);
		List<String> links = GoogleImageParser.extractImageLinks(html, 16, exclusions, proglistener);

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

	private void parseGoogleImages2(CopyOnWriteArrayList<String> exclusions) { // Parse the Google Image Search
		String url = GoogleImageParser.getSearchURL(searchText, GoogleImageParser.SEARCH_APPENDIX_2);
		String html = HTTPUtilities.getJavascriptHTML(url, GoogleImageParser.HTMLUNIT_JS_TIMEOUT);
		List<String> links = GoogleImageParser.extractImageLinks(html, 16, exclusions, proglistener);

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

		onEndThread(2);
	}

	private void parseImDBImages(CopyOnWriteArrayList<String> exclusions) { // Parses the Images from the first Result of ImDB CoverSearch
		String searchurl = ImDBImageParser.getSearchURL(searchText, typ);
		String searchhtml = HTTPUtilities.getHTML(searchurl, true, false);
		String direkturl = ImDBImageParser.getFirstSearchResult(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(direkturl, true, false);

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

		onEndThread(3);
	}

	private void parseTMDBImages(CopyOnWriteArrayList<String> exclusions) {
		CCOnlineReference tmpdbRef = reference;
		
		if (tmpdbRef.isUnset()) {
			if (typ == CCMovieTyp.MOVIE)
				tmpdbRef = TMDBParser.findMovieDirect(searchText);
			else
				tmpdbRef = TMDBParser.findSeriesDirect(searchText);
		}
		
		proglistener.step();
		
		if (tmpdbRef.isUnset()) {
			onEndThread(5);
			return;
		}
		
		List<String> covers = TMDBParser.findCovers(tmpdbRef);
		
		for (int i = 0; i < 8; i++) {
			if (covers.size() > i) {
				BufferedImage biu = HTTPUtilities.getImage(covers.get(i));
				if (biu != null) 
					updatelistener.onUpdate(biu);
				else
					CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotGetImage", covers.get(i))); //$NON-NLS-1$
			}
			
			proglistener.step();
		}
		
		// #################################################################################

		onEndThread(5);
	}

	private void parseCoverSearch(CopyOnWriteArrayList<String> exclusions) { // Parses the Images from the second Result of ImDB CoverSearch
		String searchurl = ImDBImageParser.getSearchURL(searchText, typ);
		String searchhtml = HTTPUtilities.getHTML(searchurl, true, false);
		String direkturl = ImDBImageParser.getSecondSearchResult(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(direkturl, true, false);

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

		onEndThread(4);
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
		case 4:
			thread_4_finished = true;
			break;
		case 5:
			thread_5_finished = true;
			break;
		}
		
		if (isFinished()) {
			proglistener.reset();
			finishlistener.onUpdate(null);
		}
	}
	
	public boolean isFinished() {
		return thread_1_finished && thread_2_finished && thread_3_finished && thread_4_finished && thread_5_finished;
	}
}