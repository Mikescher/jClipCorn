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
	public final static String VERSION = "1.3";	//$NON-NLS-1$
	public final static String DBVERSION = "1.5"; 	//$NON-NLS-1$
	
	private final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public final static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	
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
				MainFrame myFrame = new MainFrame(mList);
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
	}
}

//http://www.themoviedb.org/search/movie?query=lazarus%20projekt

//TODO statistics page
//TODO Auto parse Summary 
//TODO import old ClipCorn features
//TODO Recheck all Texts - improve DL-Language (what EN / What GER)
//TODO xml
//TODO Auto rename Files nach Richtlinien

//TODO Improve Time until Mainframe is shown (it now takes 500 ms before geting to the create MainFrame Thing)
//TODO Organize All Cover Files
