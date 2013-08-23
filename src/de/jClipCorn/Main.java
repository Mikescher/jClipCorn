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
	public final static String VERSION = "1.9";	//$NON-NLS-1$
	public final static String DBVERSION = "1.6"; 	//$NON-NLS-1$
	
	private final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	public static boolean BETA = true;
	
	public static void main(String[] arg) {
		new CCProperties(PROPERTIES_PATH, arg); // MUSS ALS ERSTES CREATED WERDEN - FUCKING IMPORTANT
		
		CCLog.setPath(CCProperties.getInstance().PROP_LOG_PATH.getValue());
		
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance()); // For Main Thread
		
		PathFormatter.testWritePermissions();

		LookAndFeelManager.setLookAndFeel(CCProperties.getInstance().PROP_UI_LOOKANDFEEL.getValue());

		final CCMovieList mList = new CCMovieList();
		
		init();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
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
			System.out.println("[DBG] " + LocaleBundle.getTranslationCount() + " Translations int Locale " + LocaleBundle.getCurrentLocale()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}

//TODO Parse that (eventually ?): http://www.themoviedb.org/search/movie?query=lazarus%20projekt
//TODO Auto parse Summary 
//TODO Parse Cover from more sources (coverparadies, darktown ...) ((Erst mal sehen ob die was taugen))
//TODO Search for Trailer

//TODO (2.0) ONLINE SYNCHRONISATION / Online Profiles / View Ratings + Viewed from others ... ...
	// - XML With MovieDATA is send to Server (no FileInfo / No COver )
	// - YOu send a username and a 16-sign password (to differ the users)
	// - Server keeps track of this xml and lastUpdated of the xml
	// - You Send in an extra thread everytime something changed (global cahnge flag) ?
	// - You have local versions of xml from others (the update in seperate thread when server-LastUpdate differs from local-LastUpdate)
	// - You can look through lists of other users
		// Look what they viewed (and you not) etc
		// Look at their Ratings
		// Look what movies they have but you not ...
	// - you can right click a movie and click something like network view
		// - YOu see if the othr one has the Movie / what he rates / if he has viewed ...

//TODO Shortcuts f�r SerienWindow

//TODO [[ 1.9 FEATURES: ?? ]]

//TODO Compare Series in CompareDialog
//TODO Backup Progressdialog has a non-functional abortbutton (should do nothing and not close window, perhaps open new window)
//   - Add proper Helper class, this will become handy in other playes too (BackupManager ...)
//TODO Add Backup Information Page (Backup Manager)
//   - List All Backups in Backup Folder
//   - Delete Backups
//   - Create new Backup (short disconnect from DB)
//   - Insert Backup (Instantly (w/o restart) would be cool, warn because you will loose current data)
//   - Backups get an Information file [CreationDate, Name (only on Custom Backup), PreventAutoDelete, MovieCount?, CoverCount]
//   - Show Backup Information (Things from InfoFile (or N/A on Backups b4 this version), Time until Deletion, Size)
//   - Add Index to 2 Backups on same day (no override, Date is in Infofile)
//   - Open Backup in Explorer
//   - Time until next Backup
//   => Change connect mechanics so that you can choose were to show the progress (blocking-mainframe, or progresswindow in B-Manager)
//   => Cahnge disconnect mechanic so you can unload an reload a Database in mid-progress (TESTING!!)
//TODO Beim starten auf schreibrechte pr�fen (DB �ndern / Backup / Settingsfile ...) wenn nicht FATAL ERROR (auser bei ReadOnlyMode)
//TODO ERROR Besser Highlighten (Iwas blinkendes gro�es auf der Main-View) -> TNA-Style Popup-Thingie Ya know
//TODO Recent Custom Filters as SubTrees in FilterTree