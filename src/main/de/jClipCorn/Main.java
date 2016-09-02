package de.jClipCorn;

import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.log.ExceptionHandler;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.LookAndFeelManager;

public class Main {
	public final static String TITLE = "jClipCorn"; //$NON-NLS-1$
	public final static String VERSION = "1.10.1";	//$NON-NLS-1$
	public final static String DBVERSION = "1.9"; 	//$NON-NLS-1$
	
	private final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	public static boolean BETA = true;
	
	public static void main(String[] arg) {
		new CCProperties(PROPERTIES_PATH, arg); // FIRST ACTION - CACHE THIS SHIT - FUCKING IMPORTANT
		
		CCLog.setPath(CCProperties.getInstance().PROP_LOG_PATH.getValue());
		
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance()); // For Main Thread
		
		PathFormatter.testWritePermissions();

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
			System.setProperty("derby.stream.error.field", "de.jClipCorn.database.DerbyDatabase.DERBY_OUT"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (CCProperties.getInstance().PROP_COMMON_PRESCANFILESYSTEM.getValue()) {
			DriveMap.preScan(); // creates a new Thread
		}

		if (CCProperties.getInstance().PROP_LOADING_PRELOADRESOURCES.getValue()) {
			Resources.preload();
		}
		
		if (DEBUG) {
			System.out.println("[DBG] " + LocaleBundle.getTranslationCount() + " Translations in Locale " + LocaleBundle.getCurrentLocale()); //$NON-NLS-1$ //$NON-NLS-2$
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

//TODO Cover with higher resolution // <--

//TODO save original/alternative title (eng title) --> get from TMDB / IMDB

//TODO Missing Legend for statistics: "Format/Zeit" --> Use stacked line diagramm (-> continoous stacked bar diagram)

//TODO
//   - jClipCorn  - better cover resolution,  
//   - show cover in HD (right click in coverpanel + series cover control + cbl click coverpanel)                                             
//   - mass reverse image search for better cover versions (more HD)                                                                     
//   - Format/Quality etc also for Serien (everything for a;b;a+b with toggle control?  ->  only were applicable)                             
//   - combined timeline row (like github commit overview)                                                                                                               
//   - db_err: find series with non-continoous episode numbers
//   - find missing seasons online
//   - Auto retrieve length button
//   - dialog show unreffed movies
//   - Filenameparser test
//   - dialog to find onlinescore discrepancies (and optionally update them)
//   - can use multiple online-references (auto add imdb, add proxer and MAM for many)
//   - Export and import reference mapping file (to get mappings to other dbs) ?

// TODO Unit tests (with memory database)
// - add movie
// - update movie
// - delete movie
// - parse watchdata (test data in ParseeWatchData.java)
// - Filenameparser
// - XML Export import (various flavours)

// TODO get actors from online and save to db
//      add filter for actors. 
//      don't show all actors, make it configurable which actors are "enabled" (?)

// TODO add "invalid time" -> leads to datetime with no time set (-1:-1:-1 or 99:99:99)
//      better datetime UI formatting and comparing
//      -> convert MIDNIGHT (only this time) to INV_TIME
//      -> convert with new db version mechanism (?)

// TODO Backup database (only DB file ?) before auto upgrade
// TODO Show Message after DB auto upgrade