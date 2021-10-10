package de.jClipCorn;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.log.ExceptionHandler;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.frames.settingsFrame.SettingsFrame;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsFrame;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.ResourcePreloadMode;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.SwingUtils;

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
	public final static String VERSION   = /*<gradle_version_marker>*/"1.10.9.13"/*</gradle_version_marker>*/; //$NON-NLS-1$
	public final static String DBVERSION = "22";    //$NON-NLS-1$
	public final static String JXMLVER   = "9";     //$NON-NLS-1$

	public final static String PROPERTIES_PATH = "jClipcorn.properties"; //$NON-NLS-1$
	
	public static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	public static boolean BETA = true;

	private static CCProperties _uiPropertyAcc;

	public static void main(String[] arg) {
		Globals.MILLIS_MAIN = System.currentTimeMillis();

		Globals.TIMINGS.start(Globals.TIMING_STARTUP_TOTAL);

		Globals.TIMINGS.start(Globals.TIMING_INIT_TOTAL);
		{
			CCProperties ccprops;

			Globals.TIMINGS.start(Globals.TIMING_INIT_LOAD_PROPERTIES);
			{
				_uiPropertyAcc = ccprops = CCProperties.create(FilesystemUtils.getRealSelfDirectory().append(PROPERTIES_PATH), arg);
			}
			Globals.TIMINGS.stop(Globals.TIMING_INIT_LOAD_PROPERTIES);

			CCLog.setPath(ccprops.PROP_LOG_PATH.getValue());
			CCLog.setCCProps(ccprops);

			Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance()); // For Main Thread

			Globals.TIMINGS.start(Globals.TIMING_INIT_TESTREADONLY);
			{
				FilesystemUtils.testWritePermissions(ccprops);
			}
			Globals.TIMINGS.stop(Globals.TIMING_INIT_TESTREADONLY);

			final CCMovieList mList = CCMovieList.createInstanceMovieList(ccprops);

			init(mList);

			SwingUtils.invokeLater(() ->
			{
				LookAndFeelManager.setLookAndFeel(ccprops.PROP_UI_APPTHEME.getValue(), false);

				mList.showInitialWizard();

				final MainFrame myFrame = new MainFrame(mList);
				myFrame.start();

				mList.connect(myFrame, () -> post_init(ccprops, arg));
			});
		}
		Globals.TIMINGS.stop(Globals.TIMING_INIT_TOTAL);
	}
	
	private static void init(CCMovieList ml) {
		DEBUG |= ml.ccprops().PROP_OTHER_DEBUGMODE.getValue();

		if (ml.ccprops().PROP_COMMON_PRESCANFILESYSTEM.getValue()) {
			ml.getDriveMap().preScan(); // creates a new Thread
		}

		Resources.init();
		
		if (ml.ccprops().PROP_LOADING_PRELOADRESOURCES.getValue() == ResourcePreloadMode.SYNC_PRELOAD) {
			Globals.TIMINGS.start(Globals.TIMING_INIT_PRELOADRESOURCES);
			Resources.preload();
			Globals.TIMINGS.stop(Globals.TIMING_INIT_PRELOADRESOURCES);
		} else if (ml.ccprops().PROP_LOADING_PRELOADRESOURCES.getValue() == ResourcePreloadMode.ASYNC_PRELOAD) {
			Globals.TIMINGS.start(Globals.TIMING_INIT_PRELOADRESOURCES);
			Resources.preload_async();
			Globals.TIMINGS.stop(Globals.TIMING_INIT_PRELOADRESOURCES);
		}
		
		CCLog.addDebug(LocaleBundle.getTranslationCount(ml.ccprops()) + " Translations in Locale " + LocaleBundle.getCurrentLocale(ml.ccprops())); //$NON-NLS-1$
	}

	@SuppressWarnings({"unchecked", "nls"})
	private static void post_init(CCProperties ccprops, String[] args) {
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
					SettingsFrame sf = new SettingsFrame(MainFrame.getInstance(), ccprops);
					sf.setVisible(true);
				}
			}
		} catch (Exception e) {
			CCLog.addFatalError(e);
		}
	}

	/**
	 * This method should only be used in very specific cases
	 * Almost always you should have a CCMovieList instance and take its ccprops()
	 * Because there can exist multiple CCProperties, and they kinda are related with their databases
	 *
	 * But anyway - if you really need the CCProperties that were loaded on startup - use these...
	 */
	public static CCProperties getCurrentGlobalCCProperties() {
		return _uiPropertyAcc;
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

// option recreate previews (covers dialog to show content of covers table)

// Compare speed of stream vs ccstream (just out of interest)


// ---------------------------------------------------------------------------- //
/*

 ## jGoodies FormDesigner Migration ##

 - [X] MainFrame
 - [X] AboutFrame
 - [X] AddMovieFrame
 - [X] AddMultiEpisodesFrame
 - [X] AddSeasonFrame
 - [X] AddSeriesFrame
 - [ ] AllRatingsFrame
 - [ ] AutofindRefrenceFrame
 - [X] BackupManagerFrame
 - [X] BatchEditFrame
 - [ ] ChangeScoreFrame
 - [ ] ChangeViewedFrame
 - [X] CheckDatabaseFrame
 - [X] CompareDatabaseFrame
 - [X] CoverCropFrame
 - [ ] CoverPreviewFrame
 - [ ] CreateSeriesFolderStructureFrame
 - [ ] CustomFilterEditDialog
 - [X] DatabaseHistoryFrame
 - [ ] DisplayGenresDialog
 - [ ] EditMediaInfoDialog
 - [X] EditMovieFrame
 - [X] EditSeriesFrame
 - [ ] EditStringListPropertyFrame
 - [ ] EditToolbarFrame
 - [ ] ExportElementsFrame
 - [X] ExtendedSettingsFrame
 - [ ] FilenameRulesFrame
 - [ ] FindCoverFrame
 - [ ] GenericTextDialog
 - [X] GroupManageFrame
 - [ ] ImportElementsFrame
 - [ ] InitialConfigFrame
 - [ ] InputErrorFrame
 - [X] LogFrame
 - [ ] MoveSeriesFrame
 - [ ] OmniParserFrame
 - [ ] OrganizeFilterFrame
 - [X] ParseOnlineFrame
 - [ ] ParseWatchDataFrame
 - [X] PreviewMovieFrame
 - [X] PreviewSeriesFrame
 - [X] QuickAddEpisodeDialog
 - [X] QuickAddMoviesDialog
 - [ ] RandomMovieFrame
 - [ ] ScanFolderFrame
 - [X] SearchFrame
 - [ ] SettingsFrame
 - [ ] ShowIncompleteFilmSeriesFrame
 - [ ] ShowUpdateFrame
 - [ ] StatisticsFrame
 - [ ] TextExportFrame
 - [X] UpdateCodecFrame
 - [X] UpdateMetadataFrame
 - [X] VLCRobotFrame
 - [X] WatchHistoryFrame

*/