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
	public final static String VERSION = "1.9.2";	//$NON-NLS-1$
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
			System.out.println("[DBG] " + LocaleBundle.getTranslationCount() + " Translations in Locale " + LocaleBundle.getCurrentLocale()); //$NON-NLS-1$ //$NON-NLS-2$
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

//TODO Import Dialog -> Reset Tags

//TODO Add Credits to used libs + Add License + Disclaimer

//TODO : You can close the Backup running DIalog with [X]
//TODO : Add multiple Episodes -> One Click set Epsiodelen + Calc Quality
//TODO : Exception:
/*
UNDEFINIED (10:56:54): [Thread[THREAD_IMGPARSER_GOOGLE,6,main]] java.lang.OutOfMemoryError: Java heap space
	at java.awt.image.DataBufferByte.<init>
	at java.awt.image.ComponentSampleModel.createDataBuffer
	at java.awt.image.Raster.createWritableRaster
	at javax.imageio.ImageTypeSpecifier.createBufferedImage
	at javax.imageio.ImageReader.getDestination
	at com.sun.imageio.plugins.jpeg.JPEGImageReader.readInternal
	at com.sun.imageio.plugins.jpeg.JPEGImageReader.read
	at javax.imageio.ImageIO.read
	at javax.imageio.ImageIO.read
	at de.jClipCorn.util.helper.HTTPUtilities.getImage (HTTPUtilities.java:125)
	at de.jClipCorn.gui.frames.findCoverFrame.CoverImageParser.parseGoogleImages (CoverImageParser.java:77)
	at de.jClipCorn.gui.frames.findCoverFrame.CoverImageParser.access$0 (CoverImageParser.java:69)
	at de.jClipCorn.gui.frames.findCoverFrame.CoverImageParser$1.run (CoverImageParser.java:46)
	at java.lang.Thread.run
*/

//TODO : Exception: (delete Backup in manager)
/*
UNDEFINIED (13:15:29): [Thread[AWT-EventQueue-0,6,main]] java.lang.NullPointerException
	at java.util.Hashtable.put
	at java.util.Properties.setProperty
	at de.jClipCorn.database.util.backupManager.CCBackup.setName (CCBackup.java:70)
	at de.jClipCorn.gui.frames.backupManagerFrame.BackupsManagerFrame$3.actionPerformed (BackupsManagerFrame.java:169)
	at javax.swing.AbstractButton.fireActionPerformed
	at javax.swing.AbstractButton$Handler.actionPerformed
	at javax.swing.DefaultButtonModel.fireActionPerformed
	at javax.swing.DefaultButtonModel.setPressed
	at javax.swing.plaf.basic.BasicButtonListener.mouseReleased
	at java.awt.Component.processMouseEvent
	at javax.swing.JComponent.processMouseEvent
	at java.awt.Component.processEvent
	at java.awt.Container.processEvent
	at java.awt.Component.dispatchEventImpl
	at java.awt.Container.dispatchEventImpl
	at java.awt.Component.dispatchEvent
	at java.awt.LightweightDispatcher.retargetMouseEvent
	at java.awt.LightweightDispatcher.processMouseEvent
	at java.awt.LightweightDispatcher.dispatchEvent
	at java.awt.Container.dispatchEventImpl
	at java.awt.Window.dispatchEventImpl
	at java.awt.Component.dispatchEvent
	at java.awt.EventQueue.dispatchEventImpl
	at java.awt.EventQueue.access$200
	at java.awt.EventQueue$3.run
	at java.awt.EventQueue$3.run
	at java.security.AccessController.doPrivileged
	at java.security.ProtectionDomain$1.doIntersectionPrivilege
	at java.security.ProtectionDomain$1.doIntersectionPrivilege
	at java.awt.EventQueue$4.run
	at java.awt.EventQueue$4.run
	at java.security.AccessController.doPrivileged
	at java.security.ProtectionDomain$1.doIntersectionPrivilege
	at java.awt.EventQueue.dispatchEvent
	at java.awt.EventDispatchThread.pumpOneEventForFilters
	at java.awt.EventDispatchThread.pumpEventsForFilter
	at java.awt.EventDispatchThread.pumpEventsForHierarchy
	at java.awt.EventDispatchThread.pumpEvents
	at java.awt.EventDispatchThread.pumpEvents
	at java.awt.EventDispatchThread.run
*/