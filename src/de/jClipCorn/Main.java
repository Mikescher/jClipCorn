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
import de.jClipCorn.util.LookAndFeelManager;

public class Main {
	public final static String TITLE = "jClipCorn"; //$NON-NLS-1$
	public final static String VERSION = "1.8.3";	//$NON-NLS-1$
	public final static String DBVERSION = "1.6"; 	//$NON-NLS-1$
	
	private final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	public static boolean BETA = false;
	
	public static void main(String[] arg) {
		new CCProperties(PROPERTIES_PATH, arg); // MUSS ALS ERSTES CREATED WERDEN - FUCKING IMPORTANT
		
		CCLog.setPath(CCProperties.getInstance().PROP_LOG_PATH.getValue());
		
		Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance()); // For Main Thread

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

//TODO Shortcuts für SerienWindow

//TODO [[ 1.9 FEATURES: ?? ]]

//TODO Compare Series in CompareDialog

//TODO Backup Progressdialog has a non-functional abortbutton (should do nothing and not close window, perhaps open new window)
    //- Add proper Helper class, this will become handy in other playes too (BackupManager ...)
//TODO Add Link to, so you caj identify it "WARNING (12:36:14): Could not parse FSK (???) at de.jClipCorn.util.parser.AgeRatingParser.getMinimumAge (AgeRatingParser.java:208)"
//TODO Edit Series Frame -> Edit EPisodes -> Open File DIalog (Use Series Folder)
//TODO Watch Episode without resetting/setting LastViewedDate
//TODO Add Backup Information Page (Backup Manager)
	//- List All Backups in Backup Folder
	//- Delete Backups
	//- Create new Backup (short disconnect from DB)
	//- Insert Backup (Instantly (w/o restart) would be cool, warn because you will loose current data)
	//- Backups get an Information file [CreationDate, Name (only on Custom Backup), PreventAutoDelete, MovieCount?, CoverCount]
	//- Show Backup Information (Things from InfoFile (or N/A on Backups b4 this version), Time until Deletion, Size)
	//- Add Index to 2 Backups on same day (no override, Date is in Infofile)
	//- Open Backup in Explorer
	//- Time until next Backup
	//=> Change connect mechanics so that you can choose were to show the progress (blocking-mainframe, or progresswindow in B-Manager)
	//=> Cahnge disconnect mechanic so you can unload an reload a Database in mid-progress (TESTING!!)
//TODO Auto-Recognize Structure of AVI-File when importing Series (Get Filename)
	//- Multiple versions to choose from
	//- Start collecting samples !!
	//- (Also parse from wikipedia) (perhaps from various plaintext ressource)
	//- Perhaps one Master parse code that has various sources (wikipedia, website, plaintext, explorer)