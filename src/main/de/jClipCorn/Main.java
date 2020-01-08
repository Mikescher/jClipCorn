package de.jClipCorn;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.log.ExceptionHandler;
import de.jClipCorn.gui.frames.settingsFrame.SettingsFrame;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsFrame;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.ResourcePreloadMode;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.SSLUtilities;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.LookAndFeelManager;

import javax.swing.*;

//
// ========== GRADLE ==========
//
// $ gradlew betaJar                 // for local release
//
// $ gradlew manualReleaseJar        // full (local) release  (add tag first for proper vers number)
//
// ============================
//

public class Main {
	public final static String TITLE     = "jClipCorn"; //$NON-NLS-1$
	public final static String VERSION   = /*<gradle_version_marker>*/"1.10.7.3"/*</gradle_version_marker>*/; //$NON-NLS-1$
	public final static String DBVERSION = "14";    //$NON-NLS-1$
	public final static String JXMLVER   = "4";     //$NON-NLS-1$

	private final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	public static boolean BETA = true;
		
	public static void main(String[] arg) {
		Globals.TIMINGS.start(Globals.TIMING_STARTUP_TOTAL);

		Globals.TIMINGS.start(Globals.TIMING_INIT_TOTAL);
		{
			Globals.TIMINGS.start(Globals.TIMING_INIT_LOAD_PROPERTIES);
			{
				CCProperties.create(PROPERTIES_PATH, arg); // FIRST ACTION - CACHE THIS SHIT - FUCKING IMPORTANT
			}
			Globals.TIMINGS.stop(Globals.TIMING_INIT_LOAD_PROPERTIES);

			CCLog.setPath(CCProperties.getInstance().PROP_LOG_PATH.getValue());

			Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance()); // For Main Thread

			Globals.TIMINGS.start(Globals.TIMING_INIT_TESTREADONLY);
			{
				PathFormatter.testWritePermissions();
			}
			Globals.TIMINGS.stop(Globals.TIMING_INIT_TESTREADONLY);

			LookAndFeelManager.setLookAndFeel(CCProperties.getInstance().PROP_UI_LOOKANDFEEL.getValue());

			final CCMovieList mList = CCMovieList.create(true);

			init();

			SwingUtilities.invokeLater(() ->
			{
				mList.showInitialWizard();

				final MainFrame myFrame = new MainFrame(mList);
				myFrame.start();

				mList.connect(myFrame, () -> post_init(arg));
			});
		}
		Globals.TIMINGS.stop(Globals.TIMING_INIT_TOTAL);
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
		
		if (CCProperties.getInstance().PROP_LOADING_PRELOADRESOURCES.getValue() == ResourcePreloadMode.SYNC_PRELOAD) {
			Globals.TIMINGS.start(Globals.TIMING_INIT_PRELOADRESOURCES);
			Resources.preload();
			Globals.TIMINGS.stop(Globals.TIMING_INIT_PRELOADRESOURCES);
		} else if (CCProperties.getInstance().PROP_LOADING_PRELOADRESOURCES.getValue() == ResourcePreloadMode.ASYNC_PRELOAD) {
			Globals.TIMINGS.start(Globals.TIMING_INIT_PRELOADRESOURCES);
			Resources.preload_async();
			Globals.TIMINGS.stop(Globals.TIMING_INIT_PRELOADRESOURCES);
		}
		
		CCLog.addDebug(LocaleBundle.getTranslationCount() + " Translations in Locale " + LocaleBundle.getCurrentLocale()); //$NON-NLS-1$

		if (CCProperties.getInstance().PROP_DISABLE_SSL_VERIFY.getValue()) {
			SSLUtilities.trustAllHostnames();
			SSLUtilities.trustAllHttpsCertificates();
			CCLog.addDebug("SSL Verification disabled"); //$NON-NLS-1$
		}
	}

	@SuppressWarnings({"unchecked", "nls"})
	private static void post_init(String[] args) {
		try {
			for (String a : args) {
				if (a.toLowerCase().startsWith("--statistics="))
				{
					String cname = "de.jClipCorn.features.statistics.charts." + a.substring("--statistics=".length());
					Class<? extends StatisticsPanel> c = (Class<? extends StatisticsPanel>)Class.forName(cname);

					StatisticsFrame sf = new StatisticsFrame(MainFrame.getInstance(), MainFrame.getInstance().getMovielist());
					sf.setVisible(true);
					sf.switchTo(c);

					return;
				}

				if (a.toLowerCase().startsWith("--settings"))
				{
					SettingsFrame sf = new SettingsFrame(MainFrame.getInstance(), CCProperties.getInstance());
					sf.setVisible(true);
				}
			}
		} catch (Exception e) {
			CCLog.addFatalError(e);
		}
	}
}

//=====================================================================
// ========================= PLANNED FEATURES =========================
//=====================================================================

// Use OpenMovieDatabase (they have a ~real~ API)
//	http://www.omdbapi.com
//	-> it is IMDB Data
//		-> autom find series with new episodes
//		-> autom update imdb score
//		-> find trailer
//		-> find plot summary / synopsis
//		-> autom find age-restriction for different nations

// Auto parse Summary
// Search for Trailer

//(2.0) ONLINE SYNCHRONISATION / Online Profiles / View Ratings + Viewed from others ... ...
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

// Shortcuts für SerienWindow
// Compare Series in CompareDialog
// Option to move Database to Server

// Integrated VLC ? : https://code.google.com/p/vlcj/

// save original/alternative title (eng title) --> get from TMDB / IMDB

// find missing seasons online
// Export and import reference mapping file (to get mappings to other dbs) ?

// get actors from online and save to db
// add filter for actors.
// don't show all actors, make it configurable which actors are "enabled" (?)

// Use http://www.iconarchive.com/show/series-season-folder-icons-by-aaron-sinuhe.html

// Move Series into separate table (???)
// Option to mark season as extra (ordered last, special filename, not in carousal but on right side of it ...)

// use omdb instead of direct imdb query

// test stuff under linux:
// Open in explorer
// Not all windows work under metal (or is GNOME etc the problem): not enough space, eg CustomFilter windows
// swing exceptions (in log)

// Dialog to copy movies to other folder
// - only copy new ones, bzw otehr filters
// - progress, async, abort

// MainTable and SeriesTable as JCCSimpleTable

// (optional) add a season ref to an online ref to associate it with a specific season

// re-do PlainTextExporter

// remove viewed field from db (get from history)

// option recreate previews (covers dialog to show content of covers table)

// Compare speed of stream vs ccstream (just out of interest)
