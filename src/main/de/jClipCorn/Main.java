package de.jClipCorn;

import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.log.ExceptionHandler;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.SSLUtilities;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.LookAndFeelManager;

//
// ========== GRADLE ==========
//
// $ gradlew betaJar                 // for local release
//
// ============================
//

public class Main {
	public final static String TITLE = "jClipCorn"; //$NON-NLS-1$
	public final static String VERSION = /*<gradle_version_marker>*/"1.10.5.6"/*</gradle_version_marker>*/;	//$NON-NLS-1$
	public final static String DBVERSION = "11"; 	//$NON-NLS-1$
	
	private final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	public static boolean BETA = true;
		
	public static void main(String[] arg) {
		Globals.TIMINGS.start(Globals.TIMING_LOAD_PROPERTIES);
		{
			CCProperties.create(PROPERTIES_PATH, arg); // FIRST ACTION - CACHE THIS SHIT - FUCKING IMPORTANT
		}
		Globals.TIMINGS.stop(Globals.TIMING_LOAD_PROPERTIES);
		
		CCLog.setPath(CCProperties.getInstance().PROP_LOG_PATH.getValue());
		
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance()); // For Main Thread

		Globals.TIMINGS.start(Globals.TIMING_LOAD_TESTREADONLY);
		{
			PathFormatter.testWritePermissions();
		}
		Globals.TIMINGS.stop(Globals.TIMING_LOAD_TESTREADONLY);

		LookAndFeelManager.setLookAndFeel(CCProperties.getInstance().PROP_UI_LOOKANDFEEL.getValue());

		final CCMovieList mList = CCMovieList.create();
		
		init();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mList.showInitialWizard();
								
				final MainFrame myFrame = new MainFrame(mList);
				myFrame.start();
				
				mList.connect(myFrame);
			}
		});
	}
	
	private static void init() {
		DEBUG |= CCProperties.getInstance().PROP_OTHER_DEBUGMODE.getValue();
		
		if (!CCProperties.getInstance().PROP_DATABASE_CREATELOGFILE.getValue()) {
			System.setProperty("derby.stream.error.field", "de.jClipCorn.database.driver.DerbyDatabase.DERBY_OUT"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (CCProperties.getInstance().PROP_COMMON_PRESCANFILESYSTEM.getValue()) {
			DriveMap.preScan(); // creates a new Thread
		}

		Resources.init();
		
		if (CCProperties.getInstance().PROP_LOADING_PRELOADRESOURCES.getValue()) {
			Globals.TIMINGS.start(Globals.TIMING_LOAD_PRELOADRESOURCES);
			Resources.preload();
			Globals.TIMINGS.stop(Globals.TIMING_LOAD_PRELOADRESOURCES);
		}
		
		CCLog.addDebug(LocaleBundle.getTranslationCount() + " Translations in Locale " + LocaleBundle.getCurrentLocale()); //$NON-NLS-1$

		if (CCProperties.getInstance().DISABLE_SSL_VERIFY.getValue()) {
			SSLUtilities.trustAllHostnames();
			SSLUtilities.trustAllHttpsCertificates();
			CCLog.addDebug("SSL Verification disabled"); //$NON-NLS-1$
		}
	}
}

//TODO Use OpenMovieDatabase (they have a ~real~ API)
// http://www.omdbapi.com
// -> it is IMDB Data
//		-> autom find series with new episodes
//		-> autom update imdb score
//		-> find trailer
//		-> find plot summary / synopsis
//		-> autom find age-restriction for different nations

//TODO Auto parse Summary
//TODO Search for Trailer

//TODO (2.0) ONLINE SYNCHRONISATION / Online Profiles / View Ratings + Viewed from others ... ...
	// - XML With MovieDATA is send to Server (no FileInfo / No COver )
	// - YOu send a username and a 16-sign password (to differ the users)
	// - Server keeps track of this xml and lastUpdated of the xml
	// - You Send in an extra thread everytime something changed (global change flag) ?
	// - You have local versions of xml from others (the update in seperate thread when server-LastUpdate differs from local-LastUpdate)
	// - You can look through lists of other users
		// Look what they viewed (and you not) etc
		// Look at their Ratings
		// Look what movies they have but you not ...
	// - you can right click a movie and click something like network view
		// - You see if the othr one has the Movie / what he rates / if he has viewed ...

//TODO Shortcuts für SerienWindow
//TODO Compare Series in CompareDialog
//TODO Option to move Database to Server

//TODO Integrated VLC ? : https://code.google.com/p/vlcj/

//TODO autom find IMDb Score discrepancies (search Movie on imdb -> if found compare score) ~ not in CheckDB (too long) but own dialog

//TODO save original/alternative title (eng title) --> get from TMDB / IMDB

//TODO
//   - Format/Quality etc also for Serien (everything for a;b;a+b with toggle control?  ->  only were applicable)                             
//   - combined timeline row (like github commit overview)                                                                                                               
//   - find missing seasons online
//   - Auto retrieve length button
//   - can use multiple online-references (auto add imdb, add proxer and MAM for many)
//   - Export and import reference mapping file (to get mappings to other dbs) ?

//TODO get actors from online and save to db
//      add filter for actors. 
//      don't show all actors, make it configurable which actors are "enabled" (?)

//TODO Use http://www.iconarchive.com/show/series-season-folder-icons-by-aaron-sinuhe.html 

//TODO Move Series into seperate table
//TODO Option to mark season as extra (ordered last, special filename, not in carousal but on right side of it ...)

//TODO full database transaction log 
//        -> everytime we change a mov/ep/sea/ser we copy its old state with a timestamp to the log_table
//        -> then in the ui we can restore old states and even view the database at a specific timestamp
//        -> option to clean up state (only keep 1 per day for older than month && only keep one per week for older than year)

//TODO send useragent or sth with image(cover) requests (a lot of requests fail but work in browser)

//TODO use omdb instead of direct imdb query

//TODO test stuff under linux:
//     Open in explorer
//     Not all windows work under metal (or is GNOME etc the problem): not enough space, eg CustomFilter windows
//     swing exceptions (in log)

//TODO Dialog to copy movies to other folder 
//     - only copy new ones, bzw otehr filters
//     - progress, async, abort

//TODO MainTable and SeriesTable as JCCSimpleTable

//TODO AniList as OnlineRef https://github.com/AniList/ApiV2-GraphQL-Docs
//TODO Kitsu   as OnlineRef https://kitsu.docs.apiary.io/

//TODO localsync: also autosave to dropbox (or directly sync with server??)
//     php viewer: view synced data online with webpage(read-only) 

//TODO: OpenDirectory (via menu & right-click) on series

