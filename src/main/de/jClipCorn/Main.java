package de.jClipCorn;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.DatabaseConnectResult;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.features.databaseErrors.CCDatabaseValidator;
import de.jClipCorn.features.databaseErrors.DatabaseError;
import de.jClipCorn.features.databaseErrors.DatabaseValidatorOptions;
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
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.properties.enumerations.ResourcePreloadMode;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;

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
	public final static String TITLE             = "jClipCorn";                                                             //$NON-NLS-1$
	public final static String VERSION           = /*<gradle_version_marker>*/"1.10.11.30"/*</gradle_version_marker>*/;     //$NON-NLS-1$
	public final static String DBVERSION         = "34";                                                                    //$NON-NLS-1$
	public final static String JXMLVER           = "10";                                                                    //$NON-NLS-1$
	public final static String DATABASE_NAME     = "ClipCornDB";                                                            //$NON-NLS-1$
	public final static String LOG_PATH          = "jClipcorn.log";                                                         //$NON-NLS-1$
	public final static String BACKUP_FOLDERNAME = "jClipCorn_backup";                                                      //$NON-NLS-1$
	
	public static boolean DEBUG = "true".equals(System.getProperty("ineclipse"));  //$NON-NLS-1$//$NON-NLS-2$
	public static boolean BETA = true;

	// do not use in most cases - use db.isReadonly() or movielist.isReadonly()
	public static boolean ARG_READONLY = false;

	// force a CCMemoryCoverCache regardless of the DatabaseDriver
	public static boolean ARG_MEM_COVER_CACHE = false;

	// force a CCPrevCoverCache regardless of the DatabaseDriver
	public static boolean ARG_PREV_COVER_CACHE = false;

	// CCPath syntax variable overrides supplied via commandline (--ccpath key=value)
	// these have the highest priority and override the values configured in the properties
	public static final java.util.LinkedHashMap<String, String> ARG_CCPATH_OVERRIDES = new java.util.LinkedHashMap<>();

	// "--validate-db <preset|tokenlist>": run a headless database-validation, print the result and exit
	// (preset = "full"/"default"/"quick", or a token list like "MOVIES,SERIES,NFO_FILES")
	public static String ARG_VALIDATE_DB = null;

	private static CCProperties _uiPropertyAcc;

	public static void main(String[] arg) {
		Globals.MILLIS_MAIN = System.currentTimeMillis();

		Globals.TIMINGS.start(Globals.TIMING_STARTUP_TOTAL);

		interpreteArgs(arg);

		if (Main.ARG_VALIDATE_DB != null) {
			runHeadlessDbValidation(Main.ARG_VALIDATE_DB); // loads db, validates, prints, then halts the jvm
			return;
		}

		Globals.TIMINGS.start(Globals.TIMING_INIT_TOTAL);
		{
			var database = CCDatabase.create(CCDatabaseDriver.SQLITE, FilesystemUtils.getRealSelfDirectory(), Main.DATABASE_NAME, Main.ARG_READONLY);

			CCLog.setPath(LOG_PATH);

			Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.getInstance()); // For Main Thread

			Globals.TIMINGS.start(Globals.TIMING_INIT_TESTREADONLY);
			{
				FilesystemUtils.testWritePermissions();
			}
			Globals.TIMINGS.stop(Globals.TIMING_INIT_TESTREADONLY);

			CCProperties ccprops;

			Globals.TIMINGS.start(Globals.TIMING_LOAD_DATABASE_CONNECT);
			{
				var lastConnectResult = database.tryconnect();

				if (lastConnectResult == DatabaseConnectResult.ERROR_CANTCONNECT) {
					CCLog.addFatalError(LocaleBundle.getString("LogMessage.ErrorConnectDB"), database.getLastError()); //$NON-NLS-1$
				} else if (lastConnectResult == DatabaseConnectResult.ERROR_CANTCREATE) {
					CCLog.addFatalError(LocaleBundle.getString("LogMessage.ErrorCreateDB"), database.getLastError()); //$NON-NLS-1$
				}
			}
			Globals.TIMINGS.stop(Globals.TIMING_LOAD_DATABASE_CONNECT);

			Globals.TIMINGS.start(Globals.TIMING_INIT_LOAD_PROPERTIES);
			{
				_uiPropertyAcc = ccprops = CCProperties.createAndLoad(database);
				CCLog.setCCProps(ccprops);
				LocaleBundle.updateLang(ccprops);
			}
			Globals.TIMINGS.stop(Globals.TIMING_INIT_LOAD_PROPERTIES);

			final CCMovieList mList = CCMovieList.createInstanceMovieList(ccprops, database);

			Resources.init();

			BackupManager bm = new BackupManager(mList);
			bm.init();
			bm.doActions(null);

			DEBUG |= mList.ccprops().PROP_OTHER_DEBUGMODE.getValue();

			if (mList.ccprops().PROP_COMMON_PRESCANFILESYSTEM.getValue()) {
				mList.getDriveMap().preScan(); // creates a new Thread
			}

			if (mList.ccprops().PROP_LOADING_PRELOADRESOURCES.getValue() == ResourcePreloadMode.SYNC_PRELOAD) {
				Globals.TIMINGS.start(Globals.TIMING_INIT_PRELOADRESOURCES);
				Resources.preload();
				Globals.TIMINGS.stop(Globals.TIMING_INIT_PRELOADRESOURCES);
			} else if (mList.ccprops().PROP_LOADING_PRELOADRESOURCES.getValue() == ResourcePreloadMode.ASYNC_PRELOAD) {
				Globals.TIMINGS.start(Globals.TIMING_INIT_PRELOADRESOURCES);
				Resources.preload_async();
				Globals.TIMINGS.stop(Globals.TIMING_INIT_PRELOADRESOURCES);
			}

			CCLog.addDebug(LocaleBundle.getTranslationCount(mList.ccprops()) + " Translations in Locale " + LocaleBundle.getCurrentLocale(mList.ccprops())); //$NON-NLS-1$

			SwingUtils.invokeLater(() ->
			{
				LookAndFeelManager.setLookAndFeel(ccprops.PROP_UI_APPTHEME.getValue(), false);

				mList.showInitialWizard();

				final MainFrame myFrame = new MainFrame(mList);
				myFrame.start();

				mList.connectAndLoad(myFrame, () -> post_init(ccprops, arg));
			});
		}
		Globals.TIMINGS.stop(Globals.TIMING_INIT_TOTAL);
	}

	// Headless database-validation entrypoint (triggered by "--validate-db <spec>").
	// Loads the database in the working directory read-only, runs the validation described by {@code spec}
	// (see DatabaseValidatorOptions.parse), prints the result and halts the jvm (exit code != 0 when errors are found).
	@SuppressWarnings("nls")
	private static void runHeadlessDbValidation(String spec) {
		try {
			CCLog.setPath(LOG_PATH);

			final CCMovieList ml = CCMovieList.connectAndLoadDirect(
					CCDatabaseDriver.SQLITE,
					FilesystemUtils.getRealSelfDirectory(),
					Main.DATABASE_NAME,
					true,    // readonly - the validation never writes
					false);  // do not create

			LocaleBundle.updateLang(ml.ccprops());

			DatabaseValidatorOptions opts = DatabaseValidatorOptions.parse(spec);
			opts.IgnoreDuplicateIfos = ml.ccprops().PROP_VALIDATE_DUP_IGNORE_IFO.getValue();

			var errs = new java.util.ArrayList<DatabaseError>();
			var validator = new CCDatabaseValidator(ml);

			long t = System.nanoTime();
			validator.validate(errs, opts, DoubleProgressCallbackListener.EMPTY);
			long ms = (System.nanoTime() - t) / 1_000_000;

			var byType = new java.util.TreeMap<String, Integer>();
			for (DatabaseError e : errs) byType.merge(String.format("ERR_%02d  %s", e.ErrorType.getType(), e.ErrorType.toString()), 1, Integer::sum);

			System.out.println();
			System.out.println("################## DB-VALIDATOR ##################");
			System.out.println("Database : " + FilesystemUtils.getRealSelfDirectory());
			System.out.println("Options  : " + String.join(",", new java.util.TreeSet<>(opts.serialize())));
			System.out.println(String.format("Elements=%d  Movies=%d  Series=%d  Seasons=%d  Episodes=%d", ml.getElementCount(), ml.getMovieCount(), ml.getSeriesCount(), ml.getSeasonCount(), ml.getEpisodeCount()));
			System.out.println(String.format("Time     : %,d ms", ms));
			System.out.println("--- errors by type ---");
			for (var en : byType.entrySet()) System.out.println(String.format("  %6d  %s", en.getValue(), en.getKey()));
			System.out.println("Total errors: " + errs.size());
			System.out.println("#################################################");
			System.out.flush();

			System.exit(errs.isEmpty() ? 0 : 1);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(2);
		}
	}

	private static void interpreteArgs(String[] args) {
		@SuppressWarnings("nls")
		String[] readOnlyArgs = {"readonly", "-readonly", "--readonly", "read-only", "-read-only", "--read-only", "ro", "-ro", "--ro"};

		String[] memCoverCacheArgs = {"mem-cover-cache", "-mem-cover-cache", "--mem-cover-cache"};

		String[] prevCoverCacheArgs = {"prev-cover-cache", "-prev-cover-cache", "--prev-cover-cache"};

		String[] ccPathArgs = {"ccpath", "-ccpath", "--ccpath"};

		String[] validateDbArgs = {"validate-db", "-validate-db", "--validate-db"};

		for (String arg : args) {
			for (String readOnlyArg : readOnlyArgs) {
				if (arg.equalsIgnoreCase(readOnlyArg)) {
					Main.ARG_READONLY = true;

					CCLog.addDebug("ReadOnly Mode activated (" + arg + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			for (String memCoverCacheArg : memCoverCacheArgs) {
				if (arg.equalsIgnoreCase(memCoverCacheArg)) {
					Main.ARG_MEM_COVER_CACHE = true;

					CCLog.addDebug("Memory CoverCache forced (" + arg + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}

			for (String prevCoverCacheArg : prevCoverCacheArgs) {
				if (arg.equalsIgnoreCase(prevCoverCacheArg)) {
					Main.ARG_PREV_COVER_CACHE = true;

					CCLog.addDebug("Preview CoverCache forced (" + arg + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		// "--ccpath key=value" - the variable name and value are supplied as the following argument
		for (int i = 0; i < args.length; i++) {
			for (String ccPathArg : ccPathArgs) {
				if (args[i].equalsIgnoreCase(ccPathArg) && i + 1 < args.length) {
					String kv = args[i + 1];
					int sep = kv.indexOf('=');
					if (sep > 0) {
						String key = kv.substring(0, sep);
						String value = kv.substring(sep + 1);
						Main.ARG_CCPATH_OVERRIDES.put(key, value);
						CCLog.addInformation("CCPath syntax variable overwritten via commandline: '" + key + "' := '" + value + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					} else {
						CCLog.addWarning("Invalid --ccpath argument (expected 'key=value'): '" + kv + "'"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					i++; // consume the value argument
					break;
				}
			}
		}

		// "--validate-db <preset|tokenlist>" - the spec is supplied as the following argument
		for (int i = 0; i < args.length; i++) {
			for (String validateDbArg : validateDbArgs) {
				if (args[i].equalsIgnoreCase(validateDbArg) && i + 1 < args.length) {
					Main.ARG_VALIDATE_DB = args[i + 1];
					i++; // consume the value argument
					break;
				}
			}
		}
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
 - [x] CreateSeriesFolderStructureFrame
 - [ ] CustomFilterEditDialog
 - [X] DatabaseHistoryFrame
 - [ ] DisplayGenresDialog
 - [x] EditMediaInfoDialog
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
 - [x] InitialConfigFrame
 - [x] InputErrorFrame
 - [X] LogFrame
 - [x] MoveSeriesFrame
 - [x] OmniParserFrame
 - [ ] OrganizeFilterFrame
 - [X] ParseOnlineFrame
 - [x] ParseWatchDataFrame
 - [X] PreviewMovieFrame
 - [X] PreviewSeriesFrame
 - [X] QuickAddEpisodeDialog
 - [X] QuickAddMoviesDialog
 - [ ] RandomMovieFrame
 - [x] ScanFolderFrame
 - [X] SearchFrame
 - [/] SettingsFrame
 - [ ] ShowIncompleteFilmSeriesFrame
 - [x] ShowUpdateFrame
 - [x] StatisticsFrame
 - [ ] TextExportFrame
 - [X] UpdateCodecFrame
 - [X] UpdateMetadataFrame
 - [X] VLCRobotFrame
 - [X] WatchHistoryFrame

*/