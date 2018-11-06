package de.jClipCorn.gui.actionTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.database.xml.CCBXMLReader;
import de.jClipCorn.gui.frames.aboutFrame.AboutFrame;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.addSeriesFrame.AddSeriesFrame;
import de.jClipCorn.gui.frames.autofindRefrenceFrame.AutoFindReferenceFrame;
import de.jClipCorn.gui.frames.backupManagerFrame.BackupsManagerFrame;
import de.jClipCorn.gui.frames.changeScoreFrame.ChangeScoreFrame;
import de.jClipCorn.gui.frames.changeViewedFrame.ChangeViewedFrame;
import de.jClipCorn.gui.frames.checkDatabaseFrame.CheckDatabaseFrame;
import de.jClipCorn.gui.frames.compareDatabaseFrame.CompareDatabaseFrame;
import de.jClipCorn.gui.frames.compareDatabaseFrame.DatabaseComparator;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.frames.createSeriesFolderStructureFrame.CreateSeriesFolderStructureFrame;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.exportElementsFrame.ExportElementsFrame;
import de.jClipCorn.gui.frames.filenameRulesFrame.FilenameRuleFrame;
import de.jClipCorn.gui.frames.groupManageFrame.GroupManageFrame;
import de.jClipCorn.gui.frames.logFrame.LogFrame;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.frames.moveSeriesFrame.MassMoveMoviesDialog;
import de.jClipCorn.gui.frames.moveSeriesFrame.MassMoveSeriesDialog;
import de.jClipCorn.gui.frames.moveSeriesFrame.MoveSeriesDialog;
import de.jClipCorn.gui.frames.parseWatchDataFrame.ParseWatchDataFrame;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.frames.randomMovieFrame.RandomMovieFrame;
import de.jClipCorn.gui.frames.scanFolderFrame.ScanFolderFrame;
import de.jClipCorn.gui.frames.searchFrame.SearchFrame;
import de.jClipCorn.gui.frames.settingsFrame.SettingsFrame;
import de.jClipCorn.gui.frames.showIncompleteFilmSeriesFrame.ShowIncompleteFilmSeriesFrame;
import de.jClipCorn.gui.frames.showUpdateFrame.ShowUpdateFrame;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsFrame;
import de.jClipCorn.gui.frames.textExportFrame.TextExportFrame;
import de.jClipCorn.gui.frames.updateMetadataFrame.UpdateMetadataFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.UpdateConnector;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;

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
	
	private MainFrame owner;
	private CCMovieList movielist;

	public CCActionTree(MainFrame mf) {
		super();
		
		this.owner = mf;
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
				add(movies, "PlayMovie",            null,      "ClipMenuBar.Movies.Play",           Resources.ICN_MENUBAR_PLAY,           false, this::onClickMoviesPlay);
				add(movies, "PlayMovieAnonymous",   null,      "ClipMenuBar.Movies.PlayAnonymous",  Resources.ICN_MENUBAR_HIDDENPLAY,     false, this::onClickMoviesPlayAnonymous);
				add(movies, "PrevMovie",            KS_ENTER,  "ClipMenuBar.Movies.Preview",        Resources.ICN_MENUBAR_PREVIEW_MOV,    false, this::onClickMoviesPrev);
				add(movies, "AddMovie",             KS_CTRL_I, "ClipMenuBar.Movies.Add",            Resources.ICN_MENUBAR_ADD_MOV,        true,  this::onClickMoviesAdd);
				add(movies, "ExportSingleMovie",    null,      "ClipMenuBar.Movies.ExportSingle",   Resources.ICN_MENUBAR_EXPORTMOVIE,    false, this::onClickMoviesExportSingle);
				add(movies, "AddMovieToExportList", KS_CTRL_S, "ClipMenuBar.Movies.ExportMultiple", Resources.ICN_MENUBAR_EXPORTELEMENTS, false, this::onClickMoviesAddToExportList);
				add(movies, "ImportSingleMovie",    null,      "ClipMenuBar.Movies.ImportSingle",   Resources.ICN_MENUBAR_IMPORTMOVIE,    true,  this::onClickMoviesImportSingle);
				add(movies, "EditMovie",            KS_CTRL_E, "ClipMenuBar.Movies.Edit",           Resources.ICN_MENUBAR_EDIT_MOV,       true,  this::onClickMoviesEdit);
				add(movies, "RemMovie",             KS_DEL,    "ClipMenuBar.Movies.Remove",         Resources.ICN_MENUBAR_REMOVE,         true,  this::onClickMoviesRem);
			}
			
			CCActionElement series = addMaster("Serien", null, "ClipMenuBar.Series", null);
			{
				add(series, "PrevSeries",               KS_ENTER,        "ClipMenuBar.Series.Preview",              Resources.ICN_MENUBAR_PREVIEW_SER,           true,  this::onClickSeriesPreview);
				add(series, "AddSeries",                KS_CTRL_SHIFT_I, "ClipMenuBar.Series.Add",                  Resources.ICN_MENUBAR_ADD_SER,               true,  this::onClickSeriesAdd);
				add(series, "EditSeries",               KS_CTRL_E,       "ClipMenuBar.Series.Edit",                 Resources.ICN_MENUBAR_EDIT_SER,              true,  this::onClickSeriesEdit);
				add(series, "AddSeason",                null,            "ClipMenuBar.Series.AddSeason",            Resources.ICN_MENUBAR_ADD_SEA,               true,  this::onClickSeasonAdd);
				add(series, "OpenLastPlayedSeries",     null,            "ClipMenuBar.Series.OpenLastPlayedSeries", Resources.ICN_MENUBAR_OPENLASTSERIES,        true,  this::onClickSeasonOpenLast);
				add(series, "ExportSingleSeries",       null,            "ClipMenuBar.Series.ExportSingle",         Resources.ICN_MENUBAR_EXPORTSERIES,          true,  this::onClickSeriesExportSingle);
				add(series, "AddSeriesToExportList",    KS_CTRL_S,       "ClipMenuBar.Series.ExportMultiple",       Resources.ICN_MENUBAR_EXPORTELEMENTS,        true,  this::onClickSeriesAddToExportList);
				add(series, "ImportSingleSeries",       null,            "ClipMenuBar.Series.ImportSingle",         Resources.ICN_MENUBAR_IMPORTSERIES,          true,  this::onClickSeriesImportSingle);
				add(series, "SaveTXTEpisodeguide",      null,            "ClipMenuBar.Series.SaveTXTEpisodeguide",  Resources.ICN_MENUBAR_EPISODEGUIDE,          false, this::onClickSeriesCreateTXTEpisodeguide);
				add(series, "MoveSeries",               null,            "ClipMenuBar.Series.MoveSeries",           Resources.ICN_MENUBAR_MOVESERIES,            true,  this::onClickSeriesMove);
				add(series, "CreateFolderStructSeries", null,            "ClipMenuBar.Series.CreateFolderStruct",   Resources.ICN_MENUBAR_CREATEFOLDERSTRUCTURE, true,  this::onClickSeriesCreateFolderStructure);
				add(series, "RemSeries",                KS_DEL,          "ClipMenuBar.Series.Remove",               Resources.ICN_MENUBAR_REMOVE,                true,  this::onClickSeriesRem);
			}
			
			CCActionElement extras = addMaster("Extras", null, "ClipMenuBar.Extras", null);
			{
				add(extras, "ScanFolder",               KS_CTRL_O, "ClipMenuBar.Extras.ScanFolder",               Resources.ICN_MENUBAR_SCANFOLDER,           true,  this::onClickExtrasScanFolder);
				add(extras, "CompareDBs",               null,      "ClipMenuBar.Extras.CompareDBs",               Resources.ICN_MENUBAR_COMPARE,              false, this::onClickExtrasCompareDBs);
				add(extras, "ShowWatchHistory",         null,      "ClipMenuBar.Extras.WatchHistory",             Resources.ICN_MENUBAR_WATCHHISTORY,         false, this::onClickExtrasShowWatchHistory);
				add(extras, "RandomMovie",              null,      "ClipMenuBar.Extras.RandomMovie",              Resources.ICN_MENUBAR_RANDOMMOVIE,          false, this::onClickExtrasRandomMovie);
				add(extras, "BackupManager",            null,      "ClipMenuBar.Extras.BackupManager",            Resources.ICN_MENUBAR_BACKUPMANAGER,        false, this::onClickExtrasBackupManager);
				add(extras, "ShowStatistics",           null,      "ClipMenuBar.Extras.Statistics",               Resources.ICN_MENUBAR_STATISTICS,           false, this::onClickExtrasShowStatistics);
				add(extras, "ShuffleTable",             null,      "ClipMenuBar.Extras.ShuffleTable",             Resources.ICN_MENUBAR_SHUFFLE,              false, this::onClickExtrasShuffleTable);
				add(extras, "ParseWatchData",           null,      "ClipMenuBar.Extras.ParseWatchData",           Resources.ICN_MENUBAR_WATCHDATA,            true,  this::onClickExtrasParseWatchData);
				add(extras, "ShowIncompleteFilmSeries", null,      "ClipMenuBar.Extras.ShowIncompleteFilmSeries", Resources.ICN_MENUBAR_FINDINCOMPLETEZYKLUS, false, this::onClickExtrasShowIncompleteFilmSeries);
				add(extras, "UpdateMetadata",           null,      "ClipMenuBar.Extras.UpdateMetadata",           Resources.ICN_MENUBAR_UPDATEMETADATA,       true,  this::onClickExtrasUpdateMetadata);
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
				add(other, "SetMovieViewed",   null, "ClipMenuBar.Other.SetViewed",       Resources.ICN_MENUBAR_VIEWED,          this::onClickOtherSetViewed);
				add(other, "SetMovieUnviewed", null, "ClipMenuBar.Other.SetUnviewed",     Resources.ICN_MENUBAR_UNVIEWED,        this::onClickOtherSetUnviewed);
				add(other, "UndoMovieViewed",  null, "ClipMenuBar.Other.UndoMovieViewed", Resources.ICN_MENUBAR_UNDOVIEWED,      this::onClickOtherOtherUndoMovieViewed);
				add(other, "OpenFolder",       null, "ClipMenuBar.Other.OpenFolder",      Resources.ICN_MENUBAR_FOLDER,          this::onClickOtherOpenFolder);
				add(other, "ShowInBrowser",    null, "ClipMenuBar.Other.ShowInBrowser",   Resources.ICN_MENUBAR_ONLINEREFERENCE, this::onClickOtherShowInBrowser);
				add(other, "ShowCover", 	   null, "ClipMenuBar.Other.ShowCover",   	  Resources.ICN_MENUBAR_SHOWCOVER,       this::onClickOtherShowCover);
				
				CCActionElement movRating = add(other, "SetMovieRating", null, "ClipMenuBar.Other.SetMovieRating", Resources.ICN_SIDEBAR_SCORE);
				{
					add(movRating, "SetMovRatingNO", null, "CCMovieScore.RNO", CCUserScore.RATING_NO.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_NO));
					add(movRating, "SetMovRating0",  null, "CCMovieScore.R0",  CCUserScore.RATING_0.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_0));
					add(movRating, "SetMovRating1",  null, "CCMovieScore.R1",  CCUserScore.RATING_I.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_I));
					add(movRating, "SetMovRating2",  null, "CCMovieScore.R2",  CCUserScore.RATING_II.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_II));
					add(movRating, "SetMovRating3",  null, "CCMovieScore.R3",  CCUserScore.RATING_III.getIconRef(), true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_III));
					add(movRating, "SetMovRating4",  null, "CCMovieScore.R4",  CCUserScore.RATING_IV.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_IV));
					add(movRating, "SetMovRating5",  null, "CCMovieScore.R5",  CCUserScore.RATING_V.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_V));
				}

				CCActionElement serRating = add(other, "SetSeriesRating", null, "ClipMenuBar.Other.SetSeriesRating", Resources.ICN_SIDEBAR_SCORE);
				{
					add(serRating, "SetSerRatingNO", null, "CCMovieScore.RNO", CCUserScore.RATING_NO.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_NO));
					add(serRating, "SetSerRating0",  null, "CCMovieScore.R0",  CCUserScore.RATING_0.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_0));
					add(serRating, "SetSerRating1",  null, "CCMovieScore.R1",  CCUserScore.RATING_I.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_I));
					add(serRating, "SetSerRating2",  null, "CCMovieScore.R2",  CCUserScore.RATING_II.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_II));
					add(serRating, "SetSerRating3",  null, "CCMovieScore.R3",  CCUserScore.RATING_III.getIconRef(), true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_III));
					add(serRating, "SetSerRating4",  null, "CCMovieScore.R4",  CCUserScore.RATING_IV.getIconRef(),  true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_IV));
					add(serRating, "SetSerRating5",  null, "CCMovieScore.R5",  CCUserScore.RATING_V.getIconRef(),   true, (e) -> onClickOtherSetRating(e, CCUserScore.RATING_V));
				}

				CCActionElement elemTags = add(other, "SetTags", null, "ClipMenuBar.Other.SetTags", Resources.ICN_MENUBAR_TAGS);
				{
					for (int i = 0; i < CCTagList.ACTIVETAGS; i++) {
						final int idx = i;
						add(elemTags, String.format("SwitchTag_%02d", idx), null, String.format("CCMovieTags.TAG_%02d", idx), Resources.ICN_TABLE_TAG_X_1[idx], true, (e) -> onClickSwitchTag(idx));
					}
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
		
		int returnval = chooser.showOpenDialog(owner);
		
		if (returnval == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String extension = PathFormatter.getExtension(file.getAbsolutePath());
			
			if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_FULLEXPORT)) {
				ExportHelper.openFullBackupFile(chooser.getSelectedFile(), owner, movielist);
			} else if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_SINGLEEXPORT)) {
				ExportHelper.openSingleElementFile(chooser.getSelectedFile(), owner, movielist, null);
			} else if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_MULTIPLEEXPORT)) {
				ExportHelper.openMultipleElementFile(chooser.getSelectedFile(), owner, movielist);
			} else if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_CCBACKUP)) {
				CCBXMLReader.openFile(chooser.getSelectedFile(), owner);
			} else if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_COMPAREFILE)) {
				DatabaseComparator.openFile(chooser.getSelectedFile(), owner, movielist);
			}
		}
	}

	private void onClickFileRestart(CCTreeActionEvent e) {
		ApplicationHelper.restartApplication();
	}
	
	private void onClickFileExit(CCTreeActionEvent e) {
		ApplicationHelper.exitApplication();
	}

	private void onClickMaintenanceXML(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_ccbxml.description", ExportHelper.EXTENSION_CCBACKUP));  //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(owner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			CCBXMLReader.openFile(chooser.getSelectedFile(), owner);
		}
	}
	
	private void onClickExtrasSettings(CCTreeActionEvent e) {
		SettingsFrame sf = new SettingsFrame(owner, CCProperties.getInstance());
		sf.setVisible(true);
	}
	
	private void onClickExtrasScanFolder(CCTreeActionEvent e) {
		ScanFolderFrame sff = new ScanFolderFrame(owner);
		sff.setVisible(true);
	}
	
	private void onClickExtrasCompareDBs(CCTreeActionEvent e) {
		CompareDatabaseFrame cdf = new CompareDatabaseFrame(owner, movielist);
		cdf.setVisible(true);
	}
	
	private void onClickExtrasShowWatchHistory(CCTreeActionEvent e) {
		WatchHistoryFrame whf = new WatchHistoryFrame(owner, movielist);
		whf.setVisible(true);
	}
	
	private void onClickMaintenanceResetViewed(CCTreeActionEvent e) {
		if (DialogHelper.showLocaleYesNo(owner, "Dialogs.ResetViewed")) { //$NON-NLS-1$
			owner.getMovielist().resetAllMovieViewed(false);
		}
	}
	
	private void onClickMaintenanceRegenerateDUUID(CCTreeActionEvent e) {
		if (DialogHelper.showLocaleYesNo(owner, "Dialogs.ResetDUUID")) { //$NON-NLS-1$
			owner.getMovielist().resetLocalDUUID();
		}
	}
	
	private void onClickMaintenanceAutoFindReferences(CCTreeActionEvent e) {
		AutoFindReferenceFrame afrf = new AutoFindReferenceFrame(owner, movielist);
		afrf.setVisible(true);
	}
	
	private void onClickExtrasRandomMovie(CCTreeActionEvent e) {
		if (movielist.getMovieCount() > 0) {
			RandomMovieFrame rmf = new RandomMovieFrame(owner, movielist);
			rmf.setVisible(true);
		}
	}
	
	private void onClickExtrasBackupManager(CCTreeActionEvent e) {
		BackupsManagerFrame bmf = new BackupsManagerFrame(owner);
		bmf.setVisible(true);
	}
	
	private void onClickExtrasShowStatistics(CCTreeActionEvent e) {
		StatisticsFrame sf = new StatisticsFrame(owner, movielist);
		sf.setVisible(true);
	}
	
	private void onClickExtrasShuffleTable(CCTreeActionEvent e) {
		owner.getClipTable().shuffle();
	}
	
	private void onClickExtrasParseWatchData(CCTreeActionEvent e) {
		ParseWatchDataFrame pwdf = new ParseWatchDataFrame(owner, movielist);
		pwdf.setVisible(true);
	}
	
	private void onClickExtrasShowIncompleteFilmSeries(CCTreeActionEvent e) {
		ShowIncompleteFilmSeriesFrame sifsf = new ShowIncompleteFilmSeriesFrame(owner, movielist);
		sifsf.setVisible(true);
	}
	
	private void onClickExtrasUpdateMetadata(CCTreeActionEvent e) {
		UpdateMetadataFrame umf = new UpdateMetadataFrame(owner, movielist);
		umf.setVisible(true);
	}
	
	private void onClickMaintenanceMassChangeViewed(CCTreeActionEvent e) {
		ChangeViewedFrame cvf = new ChangeViewedFrame(owner, movielist);
		cvf.setVisible(true);
	}
	
	private void onClickMaintenanceMassChangeScore(CCTreeActionEvent e) {
		ChangeScoreFrame csf = new ChangeScoreFrame(owner, movielist);
		csf.setVisible(true);
	}
	
	private void onClickMaintenanceMassMoveSeries(CCTreeActionEvent e) {
		MassMoveSeriesDialog mmsd = new MassMoveSeriesDialog(owner, movielist);
		mmsd.setVisible(true);
	}
	
	private void onClickMaintenanceMassMoveMovies(CCTreeActionEvent e) {
		MassMoveMoviesDialog mmmd = new MassMoveMoviesDialog(owner, movielist);
		mmmd.setVisible(true);
	}
	
	private void onClickMoviesPlay(CCTreeActionEvent e) {
		CCDatabaseElement element = owner.getSelectedElement();

		if (element != null && element.isMovie()) {
			((CCMovie) element).play(true);
		}
	}
	
	private void onClickMoviesPlayAnonymous(CCTreeActionEvent e) {
		CCDatabaseElement element = owner.getSelectedElement();

		if (element != null && element.isMovie()) {
			((CCMovie) element).play(false);
		}
	}

	private void onClickMoviesPrev(CCTreeActionEvent e) {
		CCDatabaseElement element = owner.getSelectedElement();
		
		if (element != null && element.isMovie()) {
			PreviewMovieFrame frame = new PreviewMovieFrame(owner, (CCMovie) element);

			frame.setVisible(true);
		}
	}

	private void onClickMoviesAdd(CCTreeActionEvent e) {
		AddMovieFrame nFrame = new AddMovieFrame(owner, movielist);

		nFrame.setVisible(true);
	}
	
	private void onClickMoviesExportSingle(CCTreeActionEvent e) {
		final CCDatabaseElement element = owner.getSelectedElement();

		if (element != null && element.isMovie()) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
			chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

			int returnval = chooser.showSaveDialog(owner);

			if (returnval == JFileChooser.APPROVE_OPTION) {
				final boolean includeCover = 0 == DialogHelper.showLocaleOptions(owner, "ExportHelper.dialogs.exportCover"); //$NON-NLS-1$

				new Thread(new Runnable() {
					@Override
					public void run() {
						owner.beginBlockingIntermediate();

						ExportHelper.exportMovie(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_SINGLEEXPORT), movielist, (CCMovie) element, includeCover);
						
						owner.endBlockingIntermediate();
					}
				}, "THREAD_EXPORT_JSCCEXPORT_MOVIE").start(); //$NON-NLS-1$
			}
		}
	}
	
	private void onClickMoviesAddToExportList(CCTreeActionEvent e) {
		CCDatabaseElement element = owner.getSelectedElement();

		if (element != null && element.isMovie()) {
			ExportElementsFrame.addElementToList(owner, movielist, element);
		}
	}
	
	private void onClickMoviesImportSingle(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(owner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			ExportHelper.openSingleElementFile(chooser.getSelectedFile(), owner, movielist, CCDBElementTyp.MOVIE);
		}
	}

	private void onClickMoviesRem(CCTreeActionEvent e) {
		if (owner.getSelectedElement() != null && owner.getSelectedElement().isMovie()) {
			if (DialogHelper.showLocaleYesNo(owner, "Dialogs.DeleteMovie")) { //$NON-NLS-1$
				movielist.remove(owner.getSelectedElement());
			}
		}
	}

	private void onClickDatabaseCheck(CCTreeActionEvent e) {
		CheckDatabaseFrame cdd = new CheckDatabaseFrame(movielist, owner);

		cdd.setVisible(true);
	}

	private void onClickDatabaseClear(CCTreeActionEvent e) {
		if (DialogHelper.showLocaleYesNo(owner, "Dialogs.ClearDatabase")) { //$NON-NLS-1$
			new Thread(new Runnable() {
				@Override
				public void run() {
					movielist.clear();
				}
			}, "THREAD_CLEAR_DATABASE").start(); //$NON-NLS-1$
		}
	}
	
	private void onClickDatabaseExportAsJxmBKP(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jxmlbkp.description", ExportHelper.EXTENSION_FULLEXPORT));  //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));
		
		int returnval = chooser.showSaveDialog(owner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					owner.beginBlockingIntermediate();

					ExportHelper.exportDatabase(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_FULLEXPORT), movielist);

					owner.endBlockingIntermediate();
				}
			}, "THREAD_EXPORT_JXMLBKP").start(); //$NON-NLS-1$
		}
	}
	
	private void onClickDatabaseImportAsJxmBKP(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jxmlbkp.description", ExportHelper.EXTENSION_FULLEXPORT));  //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(owner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			ExportHelper.openFullBackupFile(chooser.getSelectedFile(), owner, movielist);
		}
	}
	
	private void onClickDatabaseImportMultipleElements(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jmccexport.description", ExportHelper.EXTENSION_MULTIPLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(owner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			ExportHelper.openMultipleElementFile(chooser.getSelectedFile(), owner, movielist);
		}
	}
	
	private void onClickDatabaseSearchDatabase(CCTreeActionEvent e) {
		SearchFrame sf = new SearchFrame(movielist, owner);
		sf.setVisible(true);
	}
	
	private void onClickDatabaseTextExportDatabase(CCTreeActionEvent e) {
		TextExportFrame tef = new TextExportFrame(movielist, owner);
		tef.setVisible(true);
	}
	
	private void onClickDatabaseManageGroups(CCTreeActionEvent e) {
		GroupManageFrame gm = new GroupManageFrame(movielist, owner);
		gm.setVisible(true);
	}
	
	private void onClickSeriesPreview(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			PreviewSeriesFrame psf = new PreviewSeriesFrame(owner, (CCSeries) el);
			psf.setVisible(true);
		}
	}
	
	private void onClickSeriesMove(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			MoveSeriesDialog msf = new MoveSeriesDialog(owner, (CCSeries) el);
			msf.setVisible(true);
		}
	}
	
	private void onClickSeriesCreateFolderStructure(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			CreateSeriesFolderStructureFrame csfsf = new CreateSeriesFolderStructureFrame(owner, (CCSeries) el);
			csfsf.setVisible(true);
		}
	}

	private void onClickSeriesAdd(CCTreeActionEvent e) {
		(new AddSeriesFrame(owner, movielist)).setVisible(true);
	}
	
	private void onClickSeriesExportSingle(CCTreeActionEvent e) {
		final CCDatabaseElement element = owner.getSelectedElement();

		if (element != null && element.isSeries()) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
			chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

			int returnval = chooser.showSaveDialog(owner);

			if (returnval == JFileChooser.APPROVE_OPTION) {
				final boolean includeCover = 0 == DialogHelper.showLocaleOptions(owner, "ExportHelper.dialogs.exportCover"); //$NON-NLS-1$

				new Thread(new Runnable() {
					@Override
					public void run() {
						owner.beginBlockingIntermediate();

						ExportHelper.exportSeries(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_SINGLEEXPORT), movielist, (CCSeries) element, includeCover);
						
						owner.endBlockingIntermediate();
					}
				}, "THREAD_EXPORT_JSCCEXPORT_SERIES").start(); //$NON-NLS-1$
			}
		}
	}
	
	private void onClickSeriesAddToExportList(CCTreeActionEvent e) {
		CCDatabaseElement element = owner.getSelectedElement();

		if (element != null && element.isSeries()) {
			ExportElementsFrame.addElementToList(owner, movielist, element);
		}
	}
	
	private void onClickSeriesCreateTXTEpisodeguide(CCTreeActionEvent e) {
		CCDatabaseElement element = owner.getSelectedElement();

		if (element != null && element.isSeries()) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_txtguide.description", ExportHelper.EXTENSION_EPISODEGUIDE)); //$NON-NLS-1$
			chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

			int returnval = chooser.showSaveDialog(owner);

			if (returnval == JFileChooser.APPROVE_OPTION) {
				try {
					SimpleFileUtils.writeTextFile(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_EPISODEGUIDE), ((CCSeries)element).getEpisodeGuide());
				} catch (IOException e2) {
					CCLog.addError(e2);
				}
			}
		}
	}
	
	private void onClickSeriesImportSingle(CCTreeActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(owner);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			ExportHelper.openSingleElementFile(chooser.getSelectedFile(), owner, movielist, CCDBElementTyp.SERIES);
		}
	}

	private void onClickSeriesEdit(CCTreeActionEvent e) {		
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			EditSeriesFrame esf = new EditSeriesFrame(owner, (CCSeries) el, null);
			esf.setVisible(true);
		}
	}

	private void onClickSeasonAdd(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			AddSeasonFrame asf = new AddSeasonFrame(owner, (CCSeries) el, null);
			asf.setVisible(true);
		}
	}
	
	private void onClickSeasonOpenLast(CCTreeActionEvent e) {
		CCEpisode ep = movielist.getLastPlayedEpisode();
			
		if (ep != null) {
			new PreviewSeriesFrame(owner, ep).setVisible(true);	
		}
	}

	private void onClickMoviesEdit(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isMovie()) {
			EditMovieFrame emf = new EditMovieFrame(owner, (CCMovie) el, null);
			emf.setVisible(true);
		}
	}

	private void onClickSeriesRem(CCTreeActionEvent e) {
		if (owner.getSelectedElement() != null && owner.getSelectedElement().isSeries()) {
			if (DialogHelper.showLocaleYesNo(owner, "Dialogs.DeleteSeries")) {//$NON-NLS-1$
				movielist.remove(owner.getSelectedElement());
			}
		}
	}
	
	private void onClickHelpShowLog(CCTreeActionEvent e) {
		LogFrame lf = new LogFrame(owner);
		lf.setVisible(true);
	}
	
	private void onClickHelpShowRules(CCTreeActionEvent e) {
		FilenameRuleFrame frf = new FilenameRuleFrame(owner);
		frf.setVisible(true);
	}

	private void onClickHelpCheckUpdates(CCTreeActionEvent e) {
		new UpdateConnector(Main.TITLE, Main.VERSION, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (e.getID() == 1) {
							ShowUpdateFrame suf = new ShowUpdateFrame(owner, (UpdateConnector) e.getSource(), true);
							suf.setVisible(true);
						} else {
							ShowUpdateFrame suf = new ShowUpdateFrame(owner, (UpdateConnector) e.getSource(), false);
							suf.setVisible(true);
						}
					}
				});
			}
		}, false);
	}
	
	private void onClickHelpShowAbout(CCTreeActionEvent e) {
		AboutFrame af = new AboutFrame(owner);
		af.setVisible(true);
	}
	
	private void onClickOtherOpenFolder(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();
		
		if (el == null) {
			return;
		}
		
		if (el.isMovie()) {
			PathFormatter.showInExplorer(((CCMovie)el).getAbsolutePart(0));
		} else {
			PathFormatter.showInExplorer(PathFormatter.fromCCPath(((CCSeries)el).getCommonPathStart(false)));
		}
	}

	private void onClickOtherSetUnviewed(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null && el.isMovie()) {
			((CCMovie)el).setViewed(false);
		}
	}

	private void onClickOtherSetViewed(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null && el.isMovie()) {
			((CCMovie)el).setViewed(true);
		}
	}
	
	private void onClickOtherOtherUndoMovieViewed(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null && el.isMovie() && ((CCMovie)el).isViewed()) {
			CCMovie mov = ((CCMovie)el);
			
			CCDateTimeList history = mov.getViewedHistory();
			history = history.removeLast();
			mov.setViewedHistory(history);
			
			if (history.isEmpty())
				mov.setViewed(false);
		}
	}
	
	private void onClickOtherSetRating(CCTreeActionEvent e, CCUserScore rating) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null) {

			if (e.SourceType == ActionSource.SHORTCUT && el.getScore() == rating && rating != CCUserScore.RATING_NO) {
				el.setScore(CCUserScore.RATING_NO);
				return;
			}
			
			el.setScore(rating);
		}
	}
	
	private void onClickOtherShowInBrowser(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el == null) return;
		
		el.getOnlineReference().Main.openInBrowser(el);
	}
	
	private void onClickOtherShowCover(CCTreeActionEvent e) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el == null) return;

		CoverPreviewFrame cpf = new CoverPreviewFrame(owner, el);
		cpf.setVisible(true);
	}
	
	private void onClickSwitchTag(int c) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null && el.isMovie()) {
			((CCMovie)el).switchTag(c);
		}
	}
}
