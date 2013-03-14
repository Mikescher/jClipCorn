package de.jClipCorn;

import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.LookAndFeelManager;

public class Main {
	public final static String TITLE = "jClipCorn"; //$NON-NLS-1$
	public final static String VERSION = "1.0";	//$NON-NLS-1$
	public final static String DBVERSION = "1.5"; 	//$NON-NLS-1$
	
	private final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public final static boolean DEBUG = false;
	
	public static void main(String[] args) {		
		new CCProperties(PROPERTIES_PATH); // MUSS ALS ERSTES CREATED WERDEN - FUCKING IMPORTANT
		
		CCLog.setPath(CCProperties.getInstance().PROP_LOG_PATH.getValue());
		
		LookAndFeelManager.setLookAndFeel(CCProperties.getInstance().PROP_UI_LOOKANDFEEL.getValue());
		
		final CCMovieList mList = new CCMovieList();
		
		init();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame myFrame = new MainFrame(mList);
				myFrame.start();
				
				mList.connect(myFrame);
				mList.getDatabaseDirectory();
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

//TODO Logg ALL EXCEPTIONS (under UNDEFINIED)
//http://www.themoviedb.org/search/movie?query=lazarus%20projekt

//TODO Faster Load
//TODO Fast way to change viewed | score state (frame - shows over - you press key for yes or no -> next cover)
//TODO Possibilty to mass change series File Location (4 my series)
//TODO statistics page
//TODO Auto parse Summary 
//TODO import old ClipCorn features
//TODO Recheck all Texts - improve DL-Language (what EN / What GER)
//TODO UserDataErrorTexts (DL -> German Text ??)
//TODO TESTING [!!!] (Adding / editing / deleting) (series / movies)
//TODO [other] Better jQCCounter ;)
//TODO Test if everything works with Metal UI
//TODO Testen ob die Button gr��en mit allen 3 Sprachen so passen
//TODO About Screen