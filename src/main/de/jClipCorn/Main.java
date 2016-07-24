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
	public final static String VERSION = "1.10.0";	//$NON-NLS-1$
	public final static String DBVERSION = "1.8"; 	//$NON-NLS-1$
	
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
// !! save IMDB ID
//		-> autom find series with new episodes
//		-> autom update imdb score
//		-> better open in imdb action
//		-> find trailer
//		-> find plot summary / synopsis
//		-> autom find age-restriction for different nations

//TODO Parse that (eventually ?): http://www.themoviedb.org/search/movie?query=lazarus%20projekt
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

//TODO save original/alternative title (eng title)

//TODO Missing Legend for statistics: "Format/Zeit" --> Use stacked line diagramm (-> continoous stacked bar diagram)

//TODO
//   - jClipCorn  - better cover resolution,  
//   - Get Viewed Values from backups
//   - show cover in HD (right click in coverpanel + series cover control + cbl click coverpanel)                                             
//   - mass reverse image search for better cover versions (more HD)                                                                          
// X - generate filename strip down chars (á -> a, ê -> e, etc)                                                                               
//   - Format/Quality etc also for Serien (everything for a;b;a+b with toggle control?  ->  only were applicable)                             
//   - combined timeline row                                                                                                                  
//   - db_err: find series with non-continoous episode numbers                                                                                
// X - remember online id (+ dialog to find them) as string with ident for website                                                            
// X     - imdb:????                                                                                                                          
// X     - amazon:???                                                                                                                         
// X     - moviepilot:???                                                                                                                     
// X     - themoviedb:???                                                                                                                     
// X - Custom Movie Groups (SpencerHill, Statham, Disney, Marvel, DC, Tarrantino)                                                             
// X         -> then remove the big SpencerHill cycle, make to group and go back to real cycles (Zwei Supertypen in Miami, Jack Clementi, ...)
// X - Calc Button for Series Quali                                                                                                           
//   - Auto retrieve length button                                                                                                            
// X - dont allow no cover add (leads to exception)
// X - add db err (mov+epi) not viewed but has history
// X - add db err (ser) has history
// X - auto find imdb for non ref movies/series (TMDB FIRST !)
//   - dialog show unreffed movies
// X - add groups to (both?) search
// X - serialize groups/refs/viewed-history
//   - AddMovieFrame.parseFromXML --> wtf? (auch für ser)
// X - statistics online reference provider
// X - add db error invalid online ref
//   - make it configurable which metadata-provider and which image-provider to use (new property type FlagList->CheckBoxList)
//   - extended option to hide Wartungs menu from deafult user
// X - search and extended search for groups
// X - strange xml parse in addmovframe
// X - viewed | groups | ref to filter
// X - undo last viewed
// / - manage groups dialog (add to group, remove from group, delete group, rename group (!), reindex groups)
//   - measure startup time
// X - error: two groups with different case-ing
// X - error: movie has one group two times (FATAL! should not be possible)
// X - Sidebar needs to listen to group changed events and update
// X - undo viewed for episodes
// X - play unchanged for movies
// X - Groups in filename "[G1] Zyklus IX - title (Part 1).avi"
//   - Filenameparser test