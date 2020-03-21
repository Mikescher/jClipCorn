package de.jClipCorn.features.actionTree;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.features.serialization.legacy.CCBXMLReader;
import de.jClipCorn.gui.frames.aboutFrame.AboutFrame;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.frames.addMultiEpisodesFrame.AddMultiEpisodesFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.addSeriesFrame.AddSeriesFrame;
import de.jClipCorn.gui.frames.autofindRefrenceFrame.AutoFindReferenceFrame;
import de.jClipCorn.gui.frames.backupManagerFrame.BackupsManagerFrame;
import de.jClipCorn.gui.frames.batchEditFrame.BatchEditFrame;
import de.jClipCorn.gui.frames.changeScoreFrame.ChangeScoreFrame;
import de.jClipCorn.gui.frames.changeViewedFrame.ChangeViewedFrame;
import de.jClipCorn.gui.frames.checkDatabaseFrame.CheckDatabaseFrame;
import de.jClipCorn.gui.frames.compareDatabaseFrame.CompareDatabaseFrame;
import de.jClipCorn.gui.frames.compareDatabaseFrame.DatabaseComparator;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.frames.createSeriesFolderStructureFrame.CreateSeriesFolderStructureFrame;
import de.jClipCorn.gui.frames.databaseHistoryFrame.DatabaseHistoryFrame;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.exportElementsFrame.ExportElementsFrame;
import de.jClipCorn.gui.frames.filenameRulesFrame.FilenameRuleFrame;
import de.jClipCorn.gui.frames.groupManageFrame.GroupManageFrame;
import de.jClipCorn.gui.frames.logFrame.LogFrame;
import de.jClipCorn.gui.frames.moveSeriesFrame.MassMoveMoviesDialog;
import de.jClipCorn.gui.frames.moveSeriesFrame.MassMoveSeriesDialog;
import de.jClipCorn.gui.frames.moveSeriesFrame.MoveSeriesDialog;
import de.jClipCorn.gui.frames.parseWatchDataFrame.ParseWatchDataFrame;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.frames.quickAddEpisodeDialog.QuickAddEpisodeDialog;
import de.jClipCorn.gui.frames.randomMovieFrame.RandomMovieFrame;
import de.jClipCorn.gui.frames.scanFolderFrame.ScanFolderFrame;
import de.jClipCorn.gui.frames.searchFrame.SearchFrame;
import de.jClipCorn.gui.frames.settingsFrame.SettingsFrame;
import de.jClipCorn.gui.frames.showIncompleteFilmSeriesFrame.ShowIncompleteFilmSeriesFrame;
import de.jClipCorn.gui.frames.showUpdateFrame.ShowUpdateFrame;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsFrame;
import de.jClipCorn.gui.frames.textExportFrame.TextExportFrame;
import de.jClipCorn.gui.frames.updateCodecFrame.UpdateCodecFrame;
import de.jClipCorn.gui.frames.updateMetadataFrame.UpdateMetadataFrame;
import de.jClipCorn.gui.frames.vlcRobot.VLCRobotFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.UpdateConnector;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.ActionCallbackListener;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class CCActionTree extends UIActionTree{
	public final static String EVENT_ON_MOVIE_EXECUTED_0 = "PlayMovie"; //$NON-NLS-1$
	public final static String EVENT_ON_MOVIE_EXECUTED_1 = "PrevMovie"; //$NON-NLS-1$
	public final static String EVENT_ON_MOVIE_EXECUTED_2 = "EditMovie"; //$NON-NLS-1$
	public final static String EVENT_ON_SERIES_EXECUTED  = "PrevSeries"; //$NON-NLS-1$

	private final static KeyStroke KS_CTRL_F = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
	private final static KeyStroke KS_ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
	private final static KeyStroke KS_CTRL_I = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
	private final static KeyStroke KS_CTRL_S = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
	private final static KeyStroke KS_DEL = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
	private final static KeyStroke KS_CTRL_E = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK);
	private final static KeyStroke KS_CTRL_SHIFT_I = KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
	private final static KeyStroke KS_CTRL_O = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK);
	private final static KeyStroke KS_F1 = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
	
	private final CCMovieList movielist;

	public CCActionTree(MainFrame mf) {
		super();
		
		this.movielist = mf.getMovielist();
	}
	
	@SuppressWarnings("nls")
	@Override
	protected void createStructure() {
		
		createRoot();
		{
			CCActionElement file = addMaster("File", null, "ClipMenuBar.File", null);
			{
				add(file, "Open",    null, "ClipMenuBar.File.Open",    Resources.ICN_MENUBAR_OPENFILE,   this::onClickFileOpen);
				add(file, "Restart", null, "ClipMenuBar.File.Restart", Resources.ICN_MENUBAR_RESTARTAPP, this::onClickFileRestart);
				add(file, "Exit",    null, "ClipMenuBar.File.Exit",    Resources.ICN_MENUBAR_CLOSE,      this::onClickFileExit);
			}
			
			CCActionElement database = addMaster("Database", null, "ClipMenuBar.Database", null);
			{
				add(database, "CheckDatabase",          null,      "ClipMenuBar.Database.CheckDB",        Resources.ICN_MENUBAR_DBCHECK,               false, this::onClickDatabaseCheck);
				add(database, "ClearDatabase",          null,      "ClipMenuBar.Database.ClearDB",        Resources.ICN_MENUBAR_CLEARDB,               true,  this::onClickDatabaseClear);
				add(database, "ExportDatabase",         null,      "ClipMenuBar.Database.ExportDB",       Resources.ICN_MENUBAR_CREATE_JXMLBKP,        false, this::onClickDatabaseExportAsJxmBKP);
				add(database, "ImportDatabase",         null,      "ClipMenuBar.Database.ImportDB",       Resources.ICN_MENUBAR_IMPORTDB,              true,  this::onClickDatabaseImportAsJxmBKP);
				add(database, "ImportMultipleElements", null,      "ClipMenuBar.Database.ImportMultiple", Resources.ICN_MENUBAR_IMPORMULTIPLEELEMENTS, true,  this::onClickDatabaseImportMultipleElements);
				add(database, "SearchDatabase",         KS_CTRL_F, "ClipMenuBar.Database.SearchDB",       Resources.ICN_MENUBAR_SEARCH,                false, this::onClickDatabaseSearchDatabase);
				add(database, "TextExportDatabase",     null,      "ClipMenuBar.Database.TextExport",     Resources.ICN_MENUBAR_EXPORTPLAINDB,         false, this::onClickDatabaseTextExportDatabase);
				add(database, "ManageGroups",           null,      "ClipMenuBar.Database.ManageGroups",   Resources.ICN_MENUBAR_MANAGEGROUPS,          false, this::onClickDatabaseManageGroups);
			}
			
			CCActionElement movies = addMaster("Movies", null, "ClipMenuBar.Movies", null);
			{
				add(movies, "PlayMovie",            null,      "ClipMenuBar.Movies.Play",               Resources.ICN_MENUBAR_PLAY,           false, this::onClickMoviesPlay);
				add(movies, "PlayMovieAnonymous",   null,      "ClipMenuBar.Movies.PlayAnonymous",      Resources.ICN_MENUBAR_HIDDENPLAY,     false, this::onClickMoviesPlayAnonymous);
				add(movies, "QueueMovieInRobot",    null,      "ClipMenuBar.Movies.QueueSeriesInRobot", Resources.ICN_MENUBAR_VLCROBOT,       false, this::onClickMoviesPlayInRobot);
				add(movies, "PrevMovie",            KS_ENTER,  "ClipMenuBar.Movies.Preview",            Resources.ICN_MENUBAR_PREVIEW_MOV,    false, this::onClickMoviesPrev);
				add(movies, "AddMovie",             KS_CTRL_I, "ClipMenuBar.Movies.Add",                Resources.ICN_MENUBAR_ADD_MOV,        true,  this::onClickMoviesAdd);
				add(movies, "ExportSingleMovie",    null,      "ClipMenuBar.Movies.ExportSingle",       Resources.ICN_MENUBAR_EXPORTMOVIE,    false, this::onClickMoviesExportSingle);
				add(movies, "AddMovieToExportList", KS_CTRL_S, "ClipMenuBar.Movies.ExportMultiple",     Resources.ICN_MENUBAR_EXPORTELEMENTS, false, this::onClickMoviesAddToExportList);
				add(movies, "ImportSingleMovie",    null,      "ClipMenuBar.Movies.ImportSingle",       Resources.ICN_MENUBAR_IMPORTMOVIE,    true,  this::onClickMoviesImportSingle);
				add(movies, "EditMovie",            KS_CTRL_E, "ClipMenuBar.Movies.Edit",               Resources.ICN_MENUBAR_EDIT_MOV,       true,  this::onClickMoviesEdit);
				add(movies, "RemMovie",             KS_DEL,    "ClipMenuBar.Movies.Remove",             Resources.ICN_MENUBAR_REMOVE,         true,  this::onClickMoviesRem);
			}
			
			CCActionElement series = addMaster("Serien", null, "ClipMenuBar.Series", null);
			{
				add(series, "PrevSeries",               KS_ENTER,        "ClipMenuBar.Series.Preview",              Resources.ICN_MENUBAR_PREVIEW_SER,           false, this::onClickSeriesPreview);
				add(series, "AddSeries",                KS_CTRL_SHIFT_I, "ClipMenuBar.Series.Add",                  Resources.ICN_MENUBAR_ADD_SER,               true,  this::onClickSeriesAdd);
				add(series, "EditSeries",               KS_CTRL_E,       "ClipMenuBar.Series.Edit",                 Resources.ICN_MENUBAR_EDIT_SER,              true,  this::onClickSeriesEdit);
				add(series, "AddSeason",                null,            "ClipMenuBar.Series.AddSeason",            Resources.ICN_MENUBAR_ADD_SEA,               true,  this::onClickSeasonAdd);
				add(series, "OpenLastPlayedSeries",     null,            "ClipMenuBar.Series.OpenLastPlayedSeries", Resources.ICN_MENUBAR_OPENLASTSERIES,        false, this::onClickSeasonOpenLast);
				add(series, "ExportSingleSeries",       null,            "ClipMenuBar.Series.ExportSingle",         Resources.ICN_MENUBAR_EXPORTSERIES,          false, this::onClickSeriesExportSingle);
				add(series, "AddSeriesToExportList",    KS_CTRL_S,       "ClipMenuBar.Series.ExportMultiple",       Resources.ICN_MENUBAR_EXPORTELEMENTS,        false, this::onClickSeriesAddToExportList);
				add(series, "ImportSingleSeries",       null,            "ClipMenuBar.Series.ImportSingle",         Resources.ICN_MENUBAR_IMPORTSERIES,          true,  this::onClickSeriesImportSingle);
				add(series, "SaveTXTEpisodeguide",      null,            "ClipMenuBar.Series.SaveTXTEpisodeguide",  Resources.ICN_MENUBAR_EPISODEGUIDE,          false, this::onClickSeriesCreateTXTEpisodeguide);
				add(series, "MoveSeries",               null,            "ClipMenuBar.Series.MoveSeries",           Resources.ICN_MENUBAR_MOVESERIES,            true,  this::onClickSeriesMove);
				add(series, "CreateFolderStructSeries", null,            "ClipMenuBar.Series.CreateFolderStruct",   Resources.ICN_MENUBAR_CREATEFOLDERSTRUCTURE, false, this::onClickSeriesCreateFolderStructure);
				add(series, "ShowSeriesHistory",        null,            "ClipMenuBar.Series.ShowHistory",          Resources.ICN_MENUBAR_DATABASEHISTORY,       false, this::onClickOtherShowHistory);
				add(series, "RemSeries",                KS_DEL,          "ClipMenuBar.Series.Remove",               Resources.ICN_MENUBAR_REMOVE,                true,  this::onClickSeriesRem);
			}
			
			CCActionElement extras = addMaster("Extras", null, "ClipMenuBar.Extras", null);
			{
				add(extras, "ScanFolder",               KS_CTRL_O, "ClipMenuBar.Extras.ScanFolder",               Resources.ICN_MENUBAR_SCANFOLDER,           true,  this::onClickExtrasScanFolder);
				add(extras, "CompareDBs",               null,      "ClipMenuBar.Extras.CompareDBs",               Resources.ICN_MENUBAR_COMPARE,              false, this::onClickExtrasCompareDBs);
				add(extras, "ShowWatchHistory",         null,      "ClipMenuBar.Extras.WatchHistory",             Resources.ICN_MENUBAR_WATCHHISTORY,         false, this::onClickExtrasShowWatchHistory);
				add(extras, "ShowDatabaseHistory",      null,      "ClipMenuBar.Extras.DatabaseHistory",          Resources.ICN_MENUBAR_DATABASEHISTORY,      false, this::onClickExtrasShowDatabaseHistory);
				add(extras, "RandomMovie",              null,      "ClipMenuBar.Extras.RandomMovie",              Resources.ICN_MENUBAR_RANDOM,               false, this::onClickExtrasRandomMovie);
				add(extras, "BackupManager",            null,      "ClipMenuBar.Extras.BackupManager",            Resources.ICN_MENUBAR_BACKUPMANAGER,        false, this::onClickExtrasBackupManager);
				add(extras, "ShowStatistics",           null,      "ClipMenuBar.Extras.Statistics",               Resources.ICN_MENUBAR_STATISTICS,           false, this::onClickExtrasShowStatistics);
				add(extras, "ShuffleTable",             null,      "ClipMenuBar.Extras.ShuffleTable",             Resources.ICN_MENUBAR_SHUFFLE,              false, this::onClickExtrasShuffleTable);
				add(extras, "ParseWatchData",           null,      "ClipMenuBar.Extras.ParseWatchData",           Resources.ICN_MENUBAR_WATCHDATA,            true,  this::onClickExtrasParseWatchData);
				add(extras, "ShowIncompleteFilmSeries", null,      "ClipMenuBar.Extras.ShowIncompleteFilmSeries", Resources.ICN_MENUBAR_FINDINCOMPLETEZYKLUS, false, this::onClickExtrasShowIncompleteFilmSeries);
				add(extras, "UpdateMetadata",           null,      "ClipMenuBar.Extras.UpdateMetadata",           Resources.ICN_MENUBAR_UPDATEMETADATA,       false, this::onClickExtrasUpdateMetadata);
				add(extras, "UpdateCodecData",          null,      "ClipMenuBar.Extras.UpdateCodecData",          Resources.ICN_MENUBAR_MEDIAINFO,            false, this::onClickExtrasUpdateCodecData);
				add(extras, "ShowVLCRobot",             null,      "ClipMenuBar.Extras.ShowVLCRobot",             Resources.ICN_MENUBAR_VLCROBOT,             false, this::onClickExtrasShowVLCRobot);
				add(extras, "ShowSettings",             null,      "ClipMenuBar.Extras.Settings",                 Resources.ICN_MENUBAR_SETTINGS,             false, this::onClickExtrasSettings);
			}
			
			CCActionElement maintenance = addMaster("Maintenance", null, "ClipMenuBar.Maintenance", null, CCProperties.getInstance().PROP_SHOW_EXTENDED_FEATURES);
			{
				add(maintenance, "XML",                null, "ClipMenuBar.Maintenance.XML",                Resources.ICN_MENUBAR_PARSEXML,        true, this::onClickMaintenanceXML);
				add(maintenance, "MassChangeViewed",   null, "ClipMenuBar.Maintenance.MassChangeViewed",   Resources.ICN_MENUBAR_MCHANGE_VIEWED,  true, this::onClickMaintenanceMassChangeViewed);
				add(maintenance, "MassChangeScore",    null, "ClipMenuBar.Maintenance.MassChangeScore",    Resources.ICN_MENUBAR_MCHANGE_SCORE,   true, this::onClickMaintenanceMassChangeScore);
				add(maintenance, "MassMoveSeries",     null, "ClipMenuBar.Maintenance.MassMoveSeries",     Resources.ICN_MENUBAR_MOVEALLSERIES,   true, this::onClickMaintenanceMassMoveSeries);
				add(maintenance, "MassMoveMovies",     null, "ClipMenuBar.Maintenance.MassMoveMovies",     Resources.ICN_MENUBAR_MOVEALLMOVIES,   true, this::onClickMaintenanceMassMoveMovies);
				add(maintenance, "ResetViewed",        null, "ClipMenuBar.Maintenance.ResetViewed",        Resources.ICN_MENUBAR_RESETVIEWED,     true, this::onClickMaintenanceResetViewed);
				add(maintenance, "RegenerateDUUID",    null, "ClipMenuBar.Maintenance.RegenerateDUUID",    Resources.ICN_MENUBAR_REGENERATEDUUID, true, this::onClickMaintenanceRegenerateDUUID);
				add(maintenance, "AutoFindReferences", null, "ClipMenuBar.Maintenance.AutoFindReferences", Resources.ICN_MENUBAR_AUTOFINDREF,     true, this::onClickMaintenanceAutoFindReferences);
			}
			
			CCActionElement help = addMaster("Help", null, "ClipMenuBar.Help", null);
			{		
				add(help, "ShowLog",      null,  "ClipMenuBar.Help.Log",           Resources.ICN_MENUBAR_LOG,           this::onClickHelpShowLog);
				add(help, "ShowRules",    null,  "ClipMenuBar.Help.Filenamerules", Resources.ICN_MENUBAR_FILENAMERULES, this::onClickHelpShowRules);
				add(help, "CheckUpdates", null,  "ClipMenuBar.Help.Updates",       Resources.ICN_MENUBAR_UPDATES,       this::onClickHelpCheckUpdates);
				add(help, "ShowAbout",    KS_F1, "ClipMenuBar.Help.About",         Resources.ICN_MENUBAR_ABOUT,         this::onClickHelpShowAbout);
			}
			
			CCActionElement other = addMaster("Other", null, "", null, false);
			{

				CCActionElement movieExtra = add(other, "MovieExtra", null, "", null);
				{
					add(movieExtra, "SetMovieViewed",   null, "ClipMenuBar.Other.MovieExtra.SetViewed",       Resources.ICN_MENUBAR_VIEWED,          this::onClickOtherSetViewed);
					add(movieExtra, "SetMovieUnviewed", null, "ClipMenuBar.Other.MovieExtra.SetUnviewed",     Resources.ICN_MENUBAR_UNVIEWED,        this::onClickOtherSetUnviewed);
					add(movieExtra, "UndoMovieViewed",  null, "ClipMenuBar.Other.MovieExtra.UndoMovieViewed", Resources.ICN_MENUBAR_UNDOVIEWED,      this::onClickOtherOtherUndoMovieViewed);
					add(movieExtra, "OpenFolder",       null, "ClipMenuBar.Other.MovieExtra.OpenFolder",      Resources.ICN_MENUBAR_FOLDER,          this::onClickOtherOpenFolder);
					add(movieExtra, "ShowInBrowser",    null, "ClipMenuBar.Other.MovieExtra.ShowInBrowser",   Resources.ICN_MENUBAR_ONLINEREFERENCE, this::onClickOtherShowInBrowser);
					add(movieExtra, "ShowCover",        null, "ClipMenuBar.Other.MovieExtra.ShowCover",       Resources.ICN_MENUBAR_SHOWCOVER,       this::onClickOtherShowCover);
					add(movieExtra, "ShowMovieHistory", null, "ClipMenuBar.Other.MovieExtra.ShowHistory",     Resources.ICN_MENUBAR_DATABASEHISTORY, this::onClickOtherShowHistory);

					CCActionElement movRating = add(movieExtra, "SetMovieRating", null, "ClipMenuBar.Other.MovieExtra.SetMovieRating", Resources.ICN_SIDEBAR_SCORE);
					{
						add(movRating, "SetMovRatingNO",  null, "CCMovieScore.RNO",  CCUserScore.RATING_NO.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_NO));
						add(movRating, "SetMovRating0",   null, "CCMovieScore.R0",   CCUserScore.RATING_0.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_0));
						add(movRating, "SetMovRating1",   null, "CCMovieScore.R1",   CCUserScore.RATING_I.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_I));
						add(movRating, "SetMovRating2",   null, "CCMovieScore.R2",   CCUserScore.RATING_II.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_II));
						add(movRating, "SetMovRatingMID", null, "CCMovieScore.RMID", CCUserScore.RATING_MID.getIconRef(), true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_MID));
						add(movRating, "SetMovRating3",   null, "CCMovieScore.R3",   CCUserScore.RATING_III.getIconRef(), true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_III));
						add(movRating, "SetMovRating4",   null, "CCMovieScore.R4",   CCUserScore.RATING_IV.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_IV));
						add(movRating, "SetMovRating5",   null, "CCMovieScore.R5",   CCUserScore.RATING_V.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_V));
					}

					CCActionElement elemTagsMov = add(movieExtra, "SetTags_Movies", null, "ClipMenuBar.Other.MovieExtra.SetTags", Resources.ICN_MENUBAR_TAGS);
					{
						for (final CCSingleTag tag : CCTagList.TAGS) {
							if (!tag.IsMovieTag) continue;
							add(elemTagsMov, String.format("SwitchTag_Movie_%02d", tag.Index), null, String.format("CCMovieTags.TAG_%02d", tag.Index), tag.IconOn, true, (e) -> onClickSwitchTag(e, tag));
						}
					}
				}

				CCActionElement season = add(other, "Season", null, "", null);
				{
					add(season, "QueueSeasonInRobot", null, "ClipMenuBar.Other.Season.QueueSeasonInRobot", Resources.ICN_MENUBAR_VLCROBOT,        false, this::onClickOtherSeasonPlayInRobot);
					add(season, "AddEpisodes",        null, "ClipMenuBar.Other.Season.AddEpisodes",        Resources.ICN_MENUBAR_ADD_SEA,         true,  this::onClickOtherSeasonAddEpisodes);
					add(season, "AddSingleEpisodes",  null, "ClipMenuBar.Other.Season.AddSingleEpisodes",  Resources.ICN_MENUBAR_ADD_SEA,         true,  this::onClickOtherSeasonQuickAddEpisodes);
					add(season, "BatchEditSeason",    null, "ClipMenuBar.Other.Season.BatchEditSeason",    Resources.ICN_MENUBAR_BATCHEDIT,       true,  this::onClickOtherSeasonBatchEditEpisodes);
					add(season, "ShowSeasonHistory",  null, "ClipMenuBar.Season.ShowHistory",              Resources.ICN_MENUBAR_DATABASEHISTORY, false, this::onClickOtherShowHistory);
					add(season, "RemSeason",          null, "ClipMenuBar.Other.Season.RemSeason",          Resources.ICN_MENUBAR_REMOVE,          true,  this::onClickOtherSeasonDeleteSeason);
					add(season, "EditSeason",         null, "ClipMenuBar.Other.Season.EditSeason",         Resources.ICN_MENUBAR_EDIT_SER,        true,  this::onClickOtherSeasonEditSeason);
					add(season, "OpenSeasonFolder",   null, "ClipMenuBar.Other.Season.OpenSeasonFolder",   Resources.ICN_MENUBAR_FOLDER,          false, this::onClickOtherSeasonOpenFolder);
				}

				CCActionElement seriesExtra = add(other, "SeriesExtra", null, "", null);
				{
					CCActionElement serRating = add(seriesExtra, "SetSeriesRating", null, "ClipMenuBar.Other.SeriesExtra.SetSeriesRating", Resources.ICN_SIDEBAR_SCORE);
					{
						add(serRating, "SetSerRatingNO",  null, "CCMovieScore.RNO",  CCUserScore.RATING_NO.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_NO));
						add(serRating, "SetSerRating0",   null, "CCMovieScore.R0",   CCUserScore.RATING_0.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_0));
						add(serRating, "SetSerRating1",   null, "CCMovieScore.R1",   CCUserScore.RATING_I.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_I));
						add(serRating, "SetSerRating2",   null, "CCMovieScore.R2",   CCUserScore.RATING_II.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_II));
						add(serRating, "SetSerRatingMID", null, "CCMovieScore.RMID", CCUserScore.RATING_MID.getIconRef(), true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_MID));
						add(serRating, "SetSerRating3",   null, "CCMovieScore.R3",   CCUserScore.RATING_III.getIconRef(), true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_III));
						add(serRating, "SetSerRating4",   null, "CCMovieScore.R4",   CCUserScore.RATING_IV.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_IV));
						add(serRating, "SetSerRating5",   null, "CCMovieScore.R5",   CCUserScore.RATING_V.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_V));
					}

					CCActionElement elemTagsSer = add(seriesExtra, "SetTags_Series", null, "ClipMenuBar.Other.SeriesExtra.SetTags", Resources.ICN_MENUBAR_TAGS);
					{
						for (final CCSingleTag tag : CCTagList.TAGS) {
							if (!tag.IsSeriesTag) continue;
							add(elemTagsSer, String.format("SwitchTag_Series_%02d", tag.Index), null, String.format("CCMovieTags.TAG_%02d", tag.Index), tag.IconOn, true, (e) -> onClickSwitchTag(e, tag));
						}
					}

					add(seriesExtra, "ResumeSeries",        null, "ClipMenuBar.Other.SeriesExtra.ResumeSeries",       null,                           false, this::onClickOtherSeriesResumePlay);
					add(seriesExtra, "QueueSeriesInRobot",  null, "ClipMenuBar.Other.SeriesExtra.QueueSeriesInRobot", Resources.ICN_MENUBAR_VLCROBOT, false, this::onClickOtherSeriesPlayInRobot);

					CCActionElement rnd = add(seriesExtra, "PlayRandomEpisode", null, "ClipMenuBar.Other.SeriesExtra.PlayRandomEpisode", Resources.ICN_MENUBAR_RANDOM, null);
					{
						add(rnd, "PlayRandomEpisodeAll",       null, "ClipMenuBar.Other.SeriesExtra.PlayRandomEpisode.All",       null, false, this::onClickOtherSeriesPlayRandomAll);
						add(rnd, "PlayRandomEpisodeViewed",    null, "ClipMenuBar.Other.SeriesExtra.PlayRandomEpisode.Viewed",    null, false, this::onClickOtherSeriesPlayRandomViewed);
						add(rnd, "PlayRandomEpisodeNotViewed", null, "ClipMenuBar.Other.SeriesExtra.PlayRandomEpisode.NotViewed", null, false, this::onClickOtherSeriesPlayRandomNotViewed);
					}

					add(seriesExtra, "BatchEditSeries", null, "ClipMenuBar.Other.SeriesExtra.BatchEditSeries", Resources.ICN_MENUBAR_BATCHEDIT, true, this::onClickOtherSeriesBatchEditSeries);

				}

				CCActionElement episode = add(other, "Episode", null, "", null);
				{
					add(episode, "PlayEpisode",          null, "ClipMenuBar.Other.Episode.PlayEpisode",          Resources.ICN_MENUBAR_PLAY,         false, this::onClickOtherEpisodePlay);
					add(episode, "PlayEpisodeAnonymous", null, "ClipMenuBar.Other.Episode.PlayEpisodeAnonymous", Resources.ICN_MENUBAR_HIDDENPLAY,   false, this::onClickOtherEpisodePlayAnonymous);
					add(episode, "QueueEpisodeInRobot",  null, "ClipMenuBar.Other.Episode.QueueEpisodeInRobot",  Resources.ICN_MENUBAR_VLCROBOT,     false, this::onClickOtherEpisodePlayInRobot);
					add(episode, "SetEpisodeViewed",     null, "ClipMenuBar.Other.Episode.SetEpisodeViewed",     Resources.ICN_MENUBAR_VIEWED,       true,  this::onClickOtherEpisodeSetViewed);
					add(episode, "SetEpisodeUnviewed",   null, "ClipMenuBar.Other.Episode.SetEpisodeUnviewed",   Resources.ICN_MENUBAR_UNVIEWED,     true,  this::onClickOtherEpisodeUnviewed);
					add(episode, "UndoEpisodeViewed",    null, "ClipMenuBar.Other.Episode.UndoEpisodeViewed",    Resources.ICN_MENUBAR_UNDOVIEWED,   true,  this::onClickOtherEpisodeUndoViewed);

					CCActionElement elemTagsEpi = add(episode, "SetTags_Episode", null, "ClipMenuBar.Other.Episode.SetTags", Resources.ICN_MENUBAR_TAGS);
					{
						for (final CCSingleTag tag : CCTagList.TAGS) {
							if (!tag.IsEpisodeTag) continue;
							add(elemTagsEpi, String.format("SwitchTag_Episode_%02d", tag.Index), null, String.format("CCMovieTags.TAG_%02d", tag.Index), tag.IconOn, true, (e) -> onClickSwitchTag(e, tag));
						}
					}

					add(episode, "OpenEpisodeFolder",  null, "ClipMenuBar.Other.Episode.OpenEpisodeFolder", Resources.ICN_MENUBAR_FOLDER,          false, this::onClickOtherEpisodeOpenFolder);
					add(episode, "ShowEpisodeHistory", null, "ClipMenuBar.Other.Episode.ShowHistory",       Resources.ICN_MENUBAR_DATABASEHISTORY, false, this::onClickOtherShowHistory);
					add(episode, "EditEpisode",        null, "ClipMenuBar.Other.Episode.EditEpisode",       Resources.ICN_MENUBAR_EDIT_MOV,        true,  this::onClickOtherEpisodeEdit);
					add(episode, "RemEpisode",         null, "ClipMenuBar.Other.Episode.RemEpisode",        Resources.ICN_MENUBAR_REMOVE,          true,  this::onClickOtherEpisodeDelete);
				}
			}
		}

		if (Main.DEBUG) {
			root.testTree();
			//printTree();
			CCLog.addDebug(String.format("%d Elements in ActionTree intialized", root.getAllChildren().size())); //$NON-NLS-1$
		}
	}

	// #######################################################################################################
	// ############################################### EVENTS ################################################
	// #######################################################################################################
	
	private void onClickFileOpen(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_all.description", ExportHelper.EXTENSION_FULLEXPORT, ExportHelper.EXTENSION_SINGLEEXPORT, ExportHelper.EXTENSION_MULTIPLEEXPORT, ExportHelper.EXTENSION_CCBACKUP, ExportHelper.EXTENSION_COMPAREFILE)); //$NON-NLS-1$
		chooser.addChoosableFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jxmlbkp.description", ExportHelper.EXTENSION_FULLEXPORT)); //$NON-NLS-1$
		chooser.addChoosableFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
		chooser.addChoosableFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jmccexport.description", ExportHelper.EXTENSION_MULTIPLEEXPORT)); //$NON-NLS-1$
		chooser.addChoosableFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_ccbxml.description", ExportHelper.EXTENSION_CCBACKUP)); //$NON-NLS-1$
		chooser.addChoosableFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jcccf.description", ExportHelper.EXTENSION_COMPAREFILE)); //$NON-NLS-1$
		
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));
		
		int returnval = chooser.showOpenDialog(e.SwingOwner);
		
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String extension = PathFormatter.getExtension(file.getAbsolutePath());
			
			if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_FULLEXPORT)) {
				ExportHelper.openFullBackupFile(chooser.getSelectedFile(), MainFrame.getInstance(), movielist);
			} else if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_SINGLEEXPORT)) {
				ExportHelper.openSingleElementFile(chooser.getSelectedFile(), MainFrame.getInstance(), movielist, null);
			} else if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_MULTIPLEEXPORT)) {
				ExportHelper.openMultipleElementFile(chooser.getSelectedFile(), MainFrame.getInstance(), movielist);
			} else if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_CCBACKUP)) {
				CCBXMLReader.openFile(chooser.getSelectedFile(), MainFrame.getInstance());
			} else if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_COMPAREFILE)) {
				DatabaseComparator.openFile(chooser.getSelectedFile(), MainFrame.getInstance(), movielist);
			}
		}
	}

	private void onClickFileRestart(CCTreeActionEvent e) {
		ApplicationHelper.restartApplication();
	}
	
	private void onClickFileExit(CCTreeActionEvent e) {
		ApplicationHelper.exitApplication(false);
	}

	private void onClickMaintenanceXML(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_ccbxml.description", ExportHelper.EXTENSION_CCBACKUP));  //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(e.SwingOwner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			CCBXMLReader.openFile(chooser.getSelectedFile(), MainFrame.getInstance());
		}
	}
	
	private void onClickExtrasSettings(CCTreeActionEvent e) {
		new SettingsFrame(MainFrame.getInstance(), CCProperties.getInstance()).setVisible(true);
	}
	
	private void onClickExtrasScanFolder(CCTreeActionEvent e) {
		new ScanFolderFrame(MainFrame.getInstance(), movielist).setVisible(true);
	}

	private void onClickExtrasCompareDBs(CCTreeActionEvent e) {
		new CompareDatabaseFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickExtrasShowWatchHistory(CCTreeActionEvent e) {
		new WatchHistoryFrame(e.SwingOwner, movielist).setVisible(true);
	}

	private void onClickExtrasShowDatabaseHistory(CCTreeActionEvent e) {
		new DatabaseHistoryFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickMaintenanceResetViewed(CCTreeActionEvent e) {
		if (DialogHelper.showLocaleYesNo(e.SwingOwner, "Dialogs.ResetViewed")) { //$NON-NLS-1$
			movielist.resetAllMovieViewed(false);
		}
	}
	
	private void onClickMaintenanceRegenerateDUUID(CCTreeActionEvent e) {
		if (DialogHelper.showLocaleYesNo(e.SwingOwner, "Dialogs.ResetDUUID")) { //$NON-NLS-1$
			movielist.resetLocalDUUID();
		}
	}
	
	private void onClickMaintenanceAutoFindReferences(CCTreeActionEvent e) {
		new AutoFindReferenceFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickExtrasRandomMovie(CCTreeActionEvent e) {
		if (movielist.getMovieCount() > 0) new RandomMovieFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickExtrasBackupManager(CCTreeActionEvent e) {
		BackupManager.getInstanceDirect().runWhenInitializedWithProgress(e.SwingOwner, bm -> new BackupsManagerFrame(bm, e.SwingOwner, movielist).setVisible(true));
	}
	
	private void onClickExtrasShowStatistics(CCTreeActionEvent e) {
		new StatisticsFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickExtrasShuffleTable(CCTreeActionEvent e) {
		MainFrame.getInstance().getClipTable().shuffle();
	}
	
	private void onClickExtrasParseWatchData(CCTreeActionEvent e) {
		new ParseWatchDataFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickExtrasShowIncompleteFilmSeries(CCTreeActionEvent e) {
		new ShowIncompleteFilmSeriesFrame(e.SwingOwner, movielist).setVisible(true);
	}

	private void onClickExtrasUpdateMetadata(CCTreeActionEvent e) {
		new UpdateMetadataFrame(e.SwingOwner, movielist).setVisible(true);
	}

	private void onClickExtrasUpdateCodecData(CCTreeActionEvent e) {
		new UpdateCodecFrame(e.SwingOwner, movielist).setVisible(true);
	}

	private void onClickExtrasShowVLCRobot(CCTreeActionEvent e) {
		VLCRobotFrame.show(e.SwingOwner);
	}
	
	private void onClickMaintenanceMassChangeViewed(CCTreeActionEvent e) {
		new ChangeViewedFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickMaintenanceMassChangeScore(CCTreeActionEvent e) {
		new ChangeScoreFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickMaintenanceMassMoveSeries(CCTreeActionEvent e) {
		new MassMoveSeriesDialog(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickMaintenanceMassMoveMovies(CCTreeActionEvent e) {
		new MassMoveMoviesDialog(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickMoviesPlay(CCTreeActionEvent e) {
		e.ifMovieSource(m -> m.play(true));
	}
	
	private void onClickMoviesPlayAnonymous(CCTreeActionEvent e) {
		e.ifMovieSource(m -> m.play(false));
	}

	private void onClickMoviesPrev(CCTreeActionEvent e) {
		e.ifMovieSource(m -> PreviewMovieFrame.show(e.SwingOwner, m));
	}

	private void onClickMoviesAdd(CCTreeActionEvent e) {
		new AddMovieFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickMoviesExportSingle(CCTreeActionEvent e) {
		e.ifMovieSource(m ->
		{
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
			chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

			int returnval = chooser.showSaveDialog(e.SwingOwner);

			if (returnval != JFileChooser.APPROVE_OPTION) return;

			final boolean includeCover = 0 == DialogHelper.showLocaleOptions(e.SwingOwner, "ExportHelper.dialogs.exportCover"); //$NON-NLS-1$
			new Thread(() ->
			{
				MainFrame.getInstance().beginBlockingIntermediate();

				ExportHelper.exportMovie(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_SINGLEEXPORT), m, includeCover, true);

				MainFrame.getInstance().endBlockingIntermediate();
			}, "THREAD_EXPORT_JSCCEXPORT_MOVIE").start(); //$NON-NLS-1$
		});
	}
	
	private void onClickMoviesAddToExportList(CCTreeActionEvent e) {
		e.ifMovieSource(m -> ExportElementsFrame.addElementToList(e.SwingOwner, m));
	}
	
	private void onClickMoviesImportSingle(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		if (chooser.showOpenDialog(e.SwingOwner) == JFileChooser.APPROVE_OPTION) {
			ExportHelper.openSingleElementFile(chooser.getSelectedFile(), MainFrame.getInstance(), movielist, CCDBElementTyp.MOVIE);
		}
	}

	private void onClickMoviesRem(CCTreeActionEvent e) {
		e.ifMovieSource(m ->
		{
			if (DialogHelper.showLocaleYesNo(e.SwingOwner, "Dialogs.DeleteMovie")) movielist.remove(m); //$NON-NLS-1$
		});
	}

	private void onClickDatabaseCheck(CCTreeActionEvent e) {
		new CheckDatabaseFrame(movielist, MainFrame.getInstance()).setVisible(true);
	}

	private void onClickDatabaseClear(CCTreeActionEvent e) {
		if (DialogHelper.showLocaleYesNo(e.SwingOwner, "Dialogs.ClearDatabase")) { //$NON-NLS-1$
			new Thread(() -> movielist.clear(), "THREAD_CLEAR_DATABASE").start(); //$NON-NLS-1$
		}
	}
	
	private void onClickDatabaseExportAsJxmBKP(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jxmlbkp.description", ExportHelper.EXTENSION_FULLEXPORT));  //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));
		
		int returnval = chooser.showSaveDialog(e.SwingOwner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			new Thread(() ->
			{
				MainFrame.getInstance().beginBlockingIntermediate();

				ExportHelper.exportDatabase(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_FULLEXPORT), movielist);

				MainFrame.getInstance().endBlockingIntermediate();
			}, "THREAD_EXPORT_JXMLBKP").start(); //$NON-NLS-1$
		}
	}
	
	private void onClickDatabaseImportAsJxmBKP(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jxmlbkp.description", ExportHelper.EXTENSION_FULLEXPORT));  //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(e.SwingOwner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			ExportHelper.openFullBackupFile(chooser.getSelectedFile(), MainFrame.getInstance(), movielist);
		}
	}
	
	private void onClickDatabaseImportMultipleElements(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jmccexport.description", ExportHelper.EXTENSION_MULTIPLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(e.SwingOwner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			ExportHelper.openMultipleElementFile(chooser.getSelectedFile(), MainFrame.getInstance(), movielist);
		}
	}
	
	private void onClickDatabaseSearchDatabase(CCTreeActionEvent e) {
		new SearchFrame(movielist, e.SwingOwner).setVisible(true);
	}
	
	private void onClickDatabaseTextExportDatabase(CCTreeActionEvent e) {
		new TextExportFrame(movielist, e.SwingOwner).setVisible(true);
	}
	
	private void onClickDatabaseManageGroups(CCTreeActionEvent e) {
		new GroupManageFrame(movielist, e.SwingOwner).setVisible(true);
	}
	
	private void onClickSeriesPreview(CCTreeActionEvent e) {
		e.ifSeriesSource(s -> PreviewSeriesFrame.show(e.SwingOwner, s));
	}
	
	private void onClickSeriesMove(CCTreeActionEvent e) {
		e.ifSeriesSource(s -> new MoveSeriesDialog(e.SwingOwner, s).setVisible(true));
	}
	
	private void onClickSeriesCreateFolderStructure(CCTreeActionEvent e) {
		e.ifSeriesSource(s -> new CreateSeriesFolderStructureFrame(e.SwingOwner, s).setVisible(true));
	}

	private void onClickSeriesAdd(CCTreeActionEvent e) {
		new AddSeriesFrame(e.SwingOwner, movielist).setVisible(true);
	}
	
	private void onClickSeriesExportSingle(CCTreeActionEvent e) {
		e.ifSeriesSource(s ->
		{
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
			chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

			if (chooser.showSaveDialog(e.SwingOwner) != JFileChooser.APPROVE_OPTION) return;

			final boolean includeCover = 0 == DialogHelper.showLocaleOptions(e.SwingOwner, "ExportHelper.dialogs.exportCover"); //$NON-NLS-1$

			new Thread(() ->
			{
				MainFrame.getInstance().beginBlockingIntermediate();

				ExportHelper.exportSeries(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_SINGLEEXPORT), s, includeCover, true);

				MainFrame.getInstance().endBlockingIntermediate();
			}, "THREAD_EXPORT_JSCCEXPORT_SERIES").start(); //$NON-NLS-1$
		});
	}
	
	private void onClickSeriesAddToExportList(CCTreeActionEvent e) {
		e.ifSeriesSource(s -> ExportElementsFrame.addElementToList(e.SwingOwner, s));
	}
	
	private void onClickSeriesCreateTXTEpisodeguide(CCTreeActionEvent e) {
		e.ifSeriesSource(s ->
		{
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_txtguide.description", ExportHelper.EXTENSION_EPISODEGUIDE)); //$NON-NLS-1$
			chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

			if (chooser.showSaveDialog(e.SwingOwner) != JFileChooser.APPROVE_OPTION) return;

			try {
				SimpleFileUtils.writeTextFile(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_EPISODEGUIDE), s.getEpisodeGuide());
			} catch (IOException e2) {
				CCLog.addError(e2);
			}
		});
	}
	
	private void onClickSeriesImportSingle(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(e.SwingOwner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			ExportHelper.openSingleElementFile(chooser.getSelectedFile(), MainFrame.getInstance(), movielist, CCDBElementTyp.SERIES);
		}
	}

	private void onClickSeriesEdit(CCTreeActionEvent e) {
		e.ifSeriesSource(s -> new EditSeriesFrame(e.SwingOwner, s, null).setVisible(true));
	}

	private void onClickSeasonAdd(CCTreeActionEvent e) {
		e.ifSeriesSource(s -> new AddSeasonFrame(e.SwingOwner, s, null).setVisible(true));
	}
	
	private void onClickSeasonOpenLast(CCTreeActionEvent e) {
		CCEpisode ep = movielist.getLastPlayedEpisode();
		if (ep != null) PreviewSeriesFrame.show(e.SwingOwner, ep);
	}

	private void onClickMoviesEdit(CCTreeActionEvent e) {
		e.ifMovieSource(m -> new EditMovieFrame(e.SwingOwner, m, null).setVisible(true));
	}

	private void onClickSeriesRem(CCTreeActionEvent e) {
		e.ifSeriesSource(s ->
		{
			if (DialogHelper.showLocaleYesNo(e.SwingOwner, "Dialogs.DeleteSeries")) {//$NON-NLS-1$
				movielist.remove(s);
			}
		});
	}
	
	private void onClickHelpShowLog(CCTreeActionEvent e) {
		new LogFrame(e.SwingOwner).setVisible(true);
	}
	
	private void onClickHelpShowRules(CCTreeActionEvent e) {
		new FilenameRuleFrame(e.SwingOwner).setVisible(true);
	}

	private void onClickHelpCheckUpdates(CCTreeActionEvent e) {
		new UpdateConnector(Main.TITLE, Main.VERSION, (src, available, version) -> SwingUtils.invokeLater(() ->
		{
			ShowUpdateFrame suf = new ShowUpdateFrame(MainFrame.getInstance(), src, available);
			suf.setVisible(true);
		}), false);
	}
	
	private void onClickHelpShowAbout(CCTreeActionEvent e) {
		new AboutFrame(e.SwingOwner).setVisible(true);
	}
	
	private void onClickOtherOpenFolder(CCTreeActionEvent e) {
		e.ifMovieOrSeriesSource(
			m -> PathFormatter.showInExplorer(m.getAbsolutePart(0)),
			s -> PathFormatter.showInExplorer(PathFormatter.fromCCPath(s.getCommonPathStart(false)))
		);
	}

	private void onClickOtherSetUnviewed(CCTreeActionEvent e) {
		e.ifMovieSource(m -> m.setViewedFromUI(false));
	}

	private void onClickOtherSetViewed(CCTreeActionEvent e) {
		e.ifMovieSource(m -> m.setViewedFromUI(true));
	}
	
	private void onClickOtherOtherUndoMovieViewed(CCTreeActionEvent e) {
		e.ifMovieSource(m ->
		{
			if (!m.isViewed())return;

			CCDateTimeList history = m.getViewedHistory();
			history = history.removeLast();
			m.setViewedHistoryFromUI(history);

			if (history.isEmpty()) m.setViewedFromUI(false);
		});
	}
	
	private void onClickOtherSetRating(CCTreeActionEvent e, CCUserScore rating) {
		e.ifDatabaseElementSource(d ->
		{
			if (e.SourceType == ActionSource.SHORTCUT && d.getScore() == rating && rating != CCUserScore.RATING_NO) {
				d.setScore(CCUserScore.RATING_NO);
				return;
			}
			d.setScore(rating);
		});
	}
	
	private void onClickOtherShowInBrowser(CCTreeActionEvent e) {
		e.ifDatabaseElementSource(d -> d.getOnlineReference().Main.openInBrowser(d));
	}
	
	private void onClickOtherShowCover(CCTreeActionEvent e) {
		e.ifDatabaseElementSource(d -> new CoverPreviewFrame(e.SwingOwner, d).setVisible(true));
	}

	private void onClickOtherShowHistory(CCTreeActionEvent e) {
		e.ifStructureElementSource(d -> new DatabaseHistoryFrame(e.SwingOwner, movielist, String.valueOf(d.getLocalID())).setVisible(true));
	}
	
	private void onClickSwitchTag(CCTreeActionEvent e, CCSingleTag t) {
		e.ifTaggedElementSource(d -> d.switchTag(t));
	}

	private void onClickOtherSeasonAddEpisodes(CCTreeActionEvent e) {
		e.ifSeasonSource(s -> new AddMultiEpisodesFrame(e.SwingOwner, s, ActionCallbackListener.toUpdateCallbackListener(e.SpecialListener)).setVisible(true));
	}

	private void onClickOtherSeasonQuickAddEpisodes(CCTreeActionEvent e) {
		e.ifSeasonSource(s ->
		{
			String p = PathFormatter.fromCCPath(s.getCommonPathStart());
			if (Str.isNullOrWhitespace(p)) p = PathFormatter.getRealSelfDirectory();
			final JFileChooser chooser = new JFileChooser(p);
			String cps = PathFormatter.fromCCPath(s.getCommonPathStart());
			if (!Str.isNullOrWhitespace(cps)) chooser.setCurrentDirectory(new File(cps));
			if (chooser.showOpenDialog(e.SwingOwner) == JFileChooser.APPROVE_OPTION) {
				QuickAddEpisodeDialog.show(e.SwingOwner, o->{/**/}, s, chooser.getSelectedFile());
			}
		});
	}

	private void onClickOtherSeasonBatchEditEpisodes(CCTreeActionEvent e) {
		e.ifSeasonSource(s -> new BatchEditFrame(e.SwingOwner, s, ActionCallbackListener.toUpdateCallbackListener(e.SpecialListener)).setVisible(true));
	}

	private void onClickOtherSeriesBatchEditSeries(CCTreeActionEvent e) {
		e.ifSeriesSource(s -> new BatchEditFrame(e.SwingOwner, s, ActionCallbackListener.toUpdateCallbackListener(e.SpecialListener)).setVisible(true));
	}

	private void onClickOtherSeasonDeleteSeason(CCTreeActionEvent e) {
		e.ifSeasonSource(s ->
		{
			if (DialogHelper.showLocaleYesNo(e.SwingOwner, "Dialogs.DeleteSeason")) s.delete(); //$NON-NLS-1$
		});
	}

	private void onClickOtherSeasonEditSeason(CCTreeActionEvent e) {
		e.ifSeasonSource(s -> new EditSeriesFrame(e.SwingOwner, s, ActionCallbackListener.toUpdateCallbackListener(e.SpecialListener)).setVisible(true));
	}

	private void onClickOtherSeasonOpenFolder(CCTreeActionEvent e) {
		e.ifSeasonSource(s -> PathFormatter.showInExplorer(PathFormatter.fromCCPath(s.getCommonPathStart())));
	}

	private void onClickOtherSeriesResumePlay(CCTreeActionEvent e) {
		e.ifSeriesSource(s ->
		{
			CCEpisode eps = s.getNextEpisode();

			if (eps != null) {
				eps.play(true);
				if (e.SpecialListener != null) e.SpecialListener.onCallbackPlayed(eps);
			}
		});
	}

	private void onClickOtherSeriesPlayRandomAll(CCTreeActionEvent e) {
		e.ifSeriesSource(s ->
		{
			CCEpisode eps = s.getRandomEpisode();

			if (eps != null) {
				eps.play(true);
				if (e.SpecialListener != null) e.SpecialListener.onCallbackPlayed(eps);
			}
		});
	}

	private void onClickOtherSeriesPlayRandomViewed(CCTreeActionEvent e) {
		e.ifSeriesSource(s ->
		{
			CCEpisode eps = s.getRandomEpisodeWithViewState(true);

			if (eps != null) {
				eps.play(true);
				if (e.SpecialListener != null) e.SpecialListener.onCallbackPlayed(eps);
			}
		});
	}

	private void onClickOtherSeriesPlayRandomNotViewed(CCTreeActionEvent e) {
		e.ifSeriesSource(s ->
		{
			CCEpisode eps = s.getRandomEpisodeWithViewState(false);

			if (eps != null) {
				eps.play(true);
				if (e.SpecialListener != null) e.SpecialListener.onCallbackPlayed(eps);
			}
		});
	}

	private void onClickOtherEpisodePlay(CCTreeActionEvent e) {
		e.ifEpisodeSource(p -> p.play(true));
	}

	private void onClickOtherEpisodePlayAnonymous(CCTreeActionEvent e) {
		e.ifEpisodeSource(p -> p.play(false));
	}

	private void onClickOtherEpisodeSetViewed(CCTreeActionEvent e) {
		e.ifEpisodeSource(p ->
		{
			p.setViewedFromUI(true);
			p.addToViewedHistoryFromUI(CCDateTime.getCurrentDateTime());
		});
	}

	private void onClickOtherEpisodeUnviewed(CCTreeActionEvent e) {
		e.ifEpisodeSource(p -> p.setViewedFromUI(false));
	}

	private void onClickOtherEpisodeUndoViewed(CCTreeActionEvent e) {
		e.ifEpisodeSource(p ->
		{
			if (!p.isViewed()) return;

			CCDateTimeList history = p.getViewedHistory();
			history = history.removeLast();
			p.setViewedHistoryFromUI(history);

			if (history.isEmpty()) p.setViewedFromUI(false);
		});
	}

	private void onClickOtherEpisodeOpenFolder(CCTreeActionEvent e) {
		e.ifEpisodeSource(p -> PathFormatter.showInExplorer(p.getAbsolutePart()));
	}

	private void onClickOtherEpisodeEdit(CCTreeActionEvent e) {
		e.ifEpisodeSource(p -> new EditSeriesFrame(e.SwingOwner, p, ActionCallbackListener.toUpdateCallbackListener(e.SpecialListener)).setVisible(true));
	}

	private void onClickOtherEpisodeDelete(CCTreeActionEvent e) {
		e.ifEpisodeSource(p ->
		{
			if (!DialogHelper.showLocaleYesNo(e.SwingOwner, "Dialogs.DeleteEpisode")) return; //$NON-NLS-1$
			p.delete();
		});
	}

	private void onClickMoviesPlayInRobot(CCTreeActionEvent e) {

		if (!CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue()) return;
		if (Str.isNullOrWhitespace(MoviePlayer.getVLCPath())) return;

		e.ifMovieSource(p -> VLCRobotFrame.show(e.SwingOwner).enqueue(p));
	}

	private void onClickOtherSeasonPlayInRobot(CCTreeActionEvent e) {

		if (!CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue()) return;
		if (Str.isNullOrWhitespace(MoviePlayer.getVLCPath())) return;

		e.ifSeasonSource(p -> VLCRobotFrame.show(e.SwingOwner).enqueue(p));
	}

	private void onClickOtherSeriesPlayInRobot(CCTreeActionEvent e) {

		if (!CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue()) return;
		if (Str.isNullOrWhitespace(MoviePlayer.getVLCPath())) return;

		e.ifSeriesSource(p -> VLCRobotFrame.show(e.SwingOwner).enqueue(p));
	}

	private void onClickOtherEpisodePlayInRobot(CCTreeActionEvent e) {

		if (!CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue()) return;
		if (Str.isNullOrWhitespace(MoviePlayer.getVLCPath())) return;

		e.ifEpisodeSource(p -> VLCRobotFrame.show(e.SwingOwner).enqueue(p));
	}
}
