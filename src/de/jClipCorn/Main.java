package de.jClipCorn;

import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.log.ExceptionHandler;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.LookAndFeelManager;

public class Main {
	public final static String TITLE = "jClipCorn"; //$NON-NLS-1$
	public final static String VERSION = "1.7.1";	//$NON-NLS-1$
	public final static String DBVERSION = "1.5"; 	//$NON-NLS-1$
	
	private final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	
	public static void main(String[] arg) {
		new CCProperties(PROPERTIES_PATH); // MUSS ALS ERSTES CREATED WERDEN - FUCKING IMPORTANT
		
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
		if (!CCProperties.getInstance().PROP_DATABASE_CREATELOGFILE.getValue()) {
			System.setProperty("derby.stream.error.field", "de.jClipCorn.database.DerbyDatabase.DERBY_OUT"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (CCProperties.getInstance().PROP_COMMON_PRESCANFILESYSTEM.getValue()) {
			DriveMap.preScan(); // creates a new Thread
		}

		if (CCProperties.getInstance().PROP_LOADING_PRELOADRESOURCES.getValue()) {
			Resources.preload();
		}
		
		DEBUG |= CCProperties.getInstance().PROP_OTHER_DEBUGMODE.getValue();
	}
}

//TODO Parse that (eventually ?): http://www.themoviedb.org/search/movie?query=lazarus%20projekt
//TODO Auto parse Summary 
//TODO Parse Cover from more sources (coverparadies, darktown ...) ((Erst mal sehen ob die was taugen))
//TODO Search for Trailer

//TODO statistics page

//TODO WatchItLaterFeature (And Download inHigherQuality)
//TODO Read Only - Mode

//TODO (2.0) ONLINE SYNCHRONISATION / Online Profiles / View Ratings + Viewed from others ... ...