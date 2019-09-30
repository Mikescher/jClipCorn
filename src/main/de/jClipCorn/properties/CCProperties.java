package de.jClipCorn.properties;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.properties.enumerations.*;
import de.jClipCorn.properties.property.*;
import de.jClipCorn.properties.property.CCEnumSetProperty.EnumSetValue;
import de.jClipCorn.properties.property.CCPathProperty.CCPathPropertyMode;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTimeFormat;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.LookAndFeelManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CCProperties {
	private final static String HEADER = "jClipCorn Configuration File"; //$NON-NLS-1$
	
	public final static CCPropertyCategory NONVISIBLE			= new CCPropertyCategory();
	public final static CCPropertyCategory CAT_COMMON 			= new CCPropertyCategory(0,  "COMMON");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_VIEW 			= new CCPropertyCategory(1,  "VIEW");         //$NON-NLS-1$
	public final static CCPropertyCategory CAT_DATABASE 		= new CCPropertyCategory(2,  "DATABASE");     //$NON-NLS-1$
	public final static CCPropertyCategory CAT_PARSER	 		= new CCPropertyCategory(3,  "PARSER");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_MOVIES	 		= new CCPropertyCategory(4,  "MOVIES");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_SERIES 			= new CCPropertyCategory(5,  "SERIES");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_PLAY 			= new CCPropertyCategory(6,  "PLAY");         //$NON-NLS-1$
	public final static CCPropertyCategory CAT_BACKUP 			= new CCPropertyCategory(7,  "BACKUP");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_STATUSBAR		= new CCPropertyCategory(8,  "STATUSBAR");    //$NON-NLS-1$
	public final static CCPropertyCategory CAT_PATHSYNTAX 		= new CCPropertyCategory(9,  "PATHSYNTAX");   //$NON-NLS-1$
	public final static CCPropertyCategory CAT_OTHERFRAMES 		= new CCPropertyCategory(10, "OTHERFRAMES");  //$NON-NLS-1$
	public final static CCPropertyCategory CAT_KEYSTROKES 		= new CCPropertyCategory(11, "KEYSTROKES");   //$NON-NLS-1$
	
	public final static CCPropertyCategory[] CATEGORIES = {
		CAT_COMMON,
		CAT_VIEW,
		CAT_DATABASE,
		CAT_PARSER,
		CAT_MOVIES,
		CAT_SERIES,
		CAT_PLAY,
		CAT_BACKUP,
		CAT_STATUSBAR,
		CAT_PATHSYNTAX,
		CAT_OTHERFRAMES,
		CAT_KEYSTROKES 	
	};
	
	@SuppressWarnings("nls")
	public final static String[] readOnlyArgs = {"readonly", "-readonly", "--readonly", "read-only", "-read-only", "--read-only", "ro", "-ro", "--ro"};
	
	private static CCProperties mainInstance = null;
	
	private final Object _fileLock = new Object();
	private List<CCProperty<Object>> propertylist = new Vector<>();

	public CCBoolProperty                                   PROP_ADD_MOVIE_RELATIVE_AUTO;
	public CCStringProperty                                 PROP_DATABASE_NAME;
	public CCStringProperty                                 PROP_LOG_PATH;
	public CCEnumProperty<UILanguage>                       PROP_UI_LANG;
	public CCStringProperty                                 PROP_SELF_DIRECTORY;
	public CCStringProperty                                 PROP_COVER_PREFIX;
	public CCStringProperty                                 PROP_COVER_TYPE;
	public CCBoolProperty                                   PROP_LOADING_LIVEUPDATE;
	public CCBoolProperty                                   PROP_STATUSBAR_CALC_SERIES_IN_LENGTH;
	public CCBoolProperty                                   PROP_STATUSBAR_CALC_SERIES_IN_SIZE;
	public CCVIntProperty                                   PROP_UI_LOOKANDFEEL;
	public CCStringProperty                                 PROP_PLAY_VLC_PATH;
	public CCBoolProperty                                   PROP_PLAY_VLC_FULLSCREEN;
	public CCBoolProperty                                   PROP_PLAY_VLC_AUTOPLAY;
	public CCBoolProperty                                   PROP_PLAY_USESTANDARDONMISSINGVLC;
	public CCBoolProperty                                   PROP_PLAY_VLCSINGLEINSTANCEMODE;
	public CCEnumProperty<DoubleClickAction>                PROP_ON_DBLCLICK_MOVE;
	public CCBoolProperty                                   PROP_USE_INTELLISORT;
	public CCBoolProperty                                   PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT;
	public CCPIntProperty                                   PROP_MAINFRAME_SCROLLSPEED;
	public CCEnumProperty<UITableBackground>                PROP_MAINFRAME_TABLEBACKGROUND;
	public CCEnumProperty<ResourcePreloadMode>              PROP_LOADING_PRELOADRESOURCES;
	public CCBoolProperty                                   PROP_LOADING_INITBACKUPMANAGERASYNC;
	public CCBoolProperty                                   PROP_DATABASE_CREATELOGFILE;
	public CCPIntProperty                                   PROP_DATABASE_COVERCACHESIZE;
	public CCBoolProperty                                   PROP_COMMON_CHECKFORUPDATES;
	public CCBoolProperty                                   PROP_COMMON_PRESCANFILESYSTEM;
	public CCBoolProperty                                   PROP_SCANFOLDER_INCLUDESERIES;
	public CCBoolProperty                                   PROP_SCANFOLDER_EXCLUDEIFOS;
	public CCDateProperty                                   PROP_BACKUP_LASTBACKUP;
	public CCBoolProperty                                   PROP_BACKUP_CREATEBACKUPS;
	public CCStringProperty                                 PROP_BACKUP_FOLDERNAME;
	public CCPIntProperty                                   PROP_BACKUP_BACKUPTIME;
	public CCRIntProperty                                   PROP_BACKUP_COMPRESSION;
	public CCBoolProperty                                   PROP_BACKUP_AUTODELETEBACKUPS;
	public CCPIntProperty                                   PROP_BACKUP_LIFETIME;
	public CCBoolProperty                                   PROP_BACKUP_EXCLUDECOVERS;
	public CCBoolProperty                                   PROP_LOG_APPEND;
	public CCPIntProperty                                   PROP_LOG_MAX_LINECOUNT;
	public CCEnumProperty<InitalSortingColumn>              PROP_VIEW_DB_START_SORT;
	public CCRIntProperty                                   PROP_VALIDATE_FILESIZEDRIFT;
	public CCBoolProperty                                   PROP_OTHER_DEBUGMODE;
	public CCBoolProperty                                   PROP_VALIDATE_DUP_IGNORE_IFO;
	public CCBoolProperty                                   PROP_PREVSERIES_3DCOVER;
	public CCBoolProperty                                   PROP_PREVSERIES_COVERBORDER;
	public CCBoolProperty                                   PROP_MASSCHANGESCORE_SKIPRATED;
	public CCBoolProperty                                   PROP_MASSCHANGESCORE_ONLYVIEWED;
	public CCBoolProperty                                   PROP_MASSCHANGEVIEWED_ONLYUNVIEWED;
	public CCBoolProperty                                   PROP_IMPORT_RESETVIEWED;
	public CCBoolProperty                                   PROP_IMPORT_ONLYWITHCOVER;
	public CCBoolProperty                                   PROP_IMPORT_RESETADDDATE;
	public CCBoolProperty                                   PROP_IMPORT_RESETSCORE;
	public CCBoolProperty                                   PROP_IMPORT_RESETTAGS;
	public CCRIntProperty                                   PROP_PARSEIMDB_LANGUAGE;
	public CCToolbarProperty                                PROP_TOOLBAR_ELEMENTS;
	public CCEnumProperty<AddDateAlgorithm>                 PROP_SERIES_ADDDATECALCULATION;
	public CCBoolProperty                                   PROP_STATBAR_ELCOUNT;
	public CCBoolProperty                                   PROP_STATBAR_PROGRESSBAR;
	public CCBoolProperty                                   PROP_STATBAR_LOG;
	public CCBoolProperty                                   PROP_STATBAR_VIEWEDCOUNT;
	public CCBoolProperty                                   PROP_STATBAR_SERIESCOUNT;
	public CCBoolProperty                                   PROP_STATBAR_LENGTH;
	public CCBoolProperty                                   PROP_STATBAR_SIZE;
	public CCBoolProperty                                   PROP_STATBAR_STARTTIME;
	public CCBoolProperty                                   PROP_MAINFRAME_CLICKABLEZYKLUS;
	public CCBoolProperty                                   PROP_MAINFRAME_CLICKABLESCORE;
	public CCBoolProperty                                   PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR;
	public CCBoolProperty                                   PROP_MAINFRAME_AUTOMATICRESETWATCHLATER;
	public CCBoolProperty                                   PROP_MAINFRAME_AUTOMATICRESETWATCHNEVER;
	public CCBoolProperty                                   PROP_STATISTICS_INTERACTIVECHARTS;
	public CCBoolProperty                                   PROP_DATABASE_CLEANSHUTDOWN;
	public CCBoolProperty                                   PROP_MAINFRAME_SHOWTAGS;
	public CCStringProperty                                 PROP_MAINFRAME_FILTERLISTPATH;
	public CCBoolProperty                                   PROP_MAINFRAME_SHOWCOVERCORNER;
	public CCBoolProperty                                   PROP_VALIDATE_CHECK_SERIES_STRUCTURE;
	public CCBoolProperty                                   PROP_MAINFRAME_DONT_FILTER_WATCHNEVER;
	public CCBoolProperty                                   PROP_SHOW_PARTIAL_VIEWED_STATE;
	public CCPIntProperty                                   PROP_MAINFRAME_WIDTH;
	public CCPIntProperty                                   PROP_MAINFRAME_HEIGHT;
	public CCSeasonRegexListProperty                        PROP_SEASON_INDEX_REGEXPRESSIONS;
	public CCPIntProperty                                   PROP_STATISTICS_TIMELINEGRAVITY;
	public CCEnumProperty<CCDatabaseDriver>                 PROP_DATABASE_DRIVER;
	public CCEnumProperty<DisplayDateAlgorithm>             PROP_SERIES_DISPLAYED_DATE;
	public CCBoolProperty                                   PROP_MAINFRAME_SHOWGROUPS;
	public CCEnumProperty<CCDBLanguage>                     PROP_DATABASE_DEFAULTPARSERLANG;
	public CCEnumProperty<BrowserLanguage>                  PROP_TMDB_LANGUAGE;
	public CCEnumSetProperty<ImageSearchImplementation>     PROP_IMAGESEARCH_IMPL;
	public CCEnumSetProperty<MetadataParserImplementation>  PROP_METAPARSER_IMPL;
	public CCBoolProperty                                   PROP_SHOW_EXTENDED_FEATURES;
	public CCBoolProperty                                   PROP_MAINFRAME_SORT_GENRES;
	public CCEnumProperty<CCDateTimeFormat>                 PROP_UI_DATETIME_FORMAT;
	public CCBoolProperty                                   PROP_DEBUG_USE_HTTPCACHE;
	public CCPathProperty                                   PROP_DEBUG_HTTPCACHE_PATH;
	public CCPIntProperty                                   PROP_SERIES_PREVIEWFRAME_HEIGHT;
	public CCEnumProperty<NextEpisodeHeuristic>             PROP_SERIES_NEXT_EPISODE_HEURISTIC;
	public CCEnumProperty<CoverImageSize>                   PROP_DATABASE_MAX_COVER_SIZE;
	public CCBoolProperty                                   PROP_MAINFRAME_SHOW_GROUP_ONLY_ON_HOVER;
	public CCBoolProperty                                   PROP_MAINFRAME_ASYNC_COVER_LOADING;
	public CCEnumProperty<AniListTitleLang>                 PROP_ANILIST_PREFERRED_TITLE_LANG;
	public CCBoolProperty                                   PROP_DISABLE_SSL_VERIFY;
	public CCBoolProperty                                   PROP_PATHSYNTAX_SELF;
	public CCBoolProperty                                   PROP_PATHSYNTAX_DRIVELABEL;
	public CCBoolProperty                                   PROP_PATHSYNTAX_SELFDIR;
	public CCBoolProperty                                   PROP_PATHSYNTAX_NETDRIVE;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR1;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR2;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR3;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR4;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR5;
	public CCPIntProperty                                   PROP_MIN_DRIVEMAP_RESCAN_TIME;
	public CCBoolProperty                                   PROP_STATBAR_DRIVESCAN;
	public CCBoolProperty                                   PROP_MAINFRAME_SHOW_VIEWCOUNT;
	public CCBoolProperty                                   PROP_DRIVEMAP_REMOUNT_NETDRIVES;
	public CCStringProperty                                 PROP_PLAY_MEDIAINFO_PATH;
	public CCStringProperty                                 PROP_PLAY_FFMPEG_PATH;
	public CCStringProperty                                 PROP_PLAY_FFPROBE_PATH;
	public CCStringProperty                                 PROP_PLAY_MP4BOX_PATH;
	public CCBoolProperty                                   PROP_PREVIEWSERIES_SINGLETON;
	public CCBoolProperty                                   PROP_PREVIEWMOVIE_SINGLETON;
	public CCBoolProperty                                   PROP_DATABASE_LOAD_ALL_COVERDATA;
	public CCEnumProperty<ColorQuantizerMethod>             PROP_DATABASE_COVER_QUANTIZER;
	public CCBoolProperty                                   PROP_CHECKDATABASE_OPT_MOVIES;
	public CCBoolProperty                                   PROP_CHECKDATABASE_OPT_SEASONS;
	public CCBoolProperty                                   PROP_CHECKDATABASE_OPT_SERIES;
	public CCBoolProperty                                   PROP_CHECKDATABASE_OPT_EPISODES;
	public CCBoolProperty                                   PROP_CHECKDATABASE_OPT_COVERS;
	public CCBoolProperty                                   PROP_CHECKDATABASE_OPT_FILES;
	public CCBoolProperty                                   PROP_CHECKDATABASE_OPT_EXTRA;
	public CCEnumSetProperty<MainFrameColumn>               PROP_MAINFRAME_VISIBLE_COLUMNS;

	public boolean ARG_READONLY = false;
	
	public boolean firstLaunch = false;
	
	private Properties properties;
	private String path;
	
	private CCProperties() {
		properties = new Properties();
		this.path = null;
		
		createProperties();
		
		mainInstance = this;
	}
	
	private CCProperties(String path, String[] args) {
		properties = new Properties();
		this.path = path;
		load(path);
		
		createProperties();
		interpreteArgs(args);

		CCLog.addDebug(propertylist.size() + " Properties in List intialized"); //$NON-NLS-1$
		
		if (firstLaunch) save();
		
		mainInstance = this;

		LocaleBundle.updateLang();
	}
	
	public static void create(String path, String[] args) {
		new CCProperties(path, args);
	}
	
	public static void createInMemory() {
		new CCProperties();
	}
	
	@SuppressWarnings("nls")
	private void createProperties() {
		PROP_UI_LANG                            = new CCEnumProperty<>(CAT_COMMON,          this,   "PROP_UI_LANG",                             getDefLanguage(),                   UILanguage.getWrapper());
		PROP_UI_DATETIME_FORMAT                 = new CCEnumProperty<>(CAT_COMMON,          this,   "PROP_UI_DATETIME_FORMAT",                  getDefDTFormat(),                   CCDateTimeFormat.getWrapper());
		PROP_LOADING_PRELOADRESOURCES           = new CCEnumProperty<>(CAT_COMMON,          this,   "PROP_LOADING_PRELOADRESOURCES",            ResourcePreloadMode.SYNC_PRELOAD,   ResourcePreloadMode.getWrapper());
		PROP_LOADING_INITBACKUPMANAGERASYNC     = new CCBoolProperty(CAT_COMMON,            this,   "PROP_LOADING_INITBACKUPMANAGERASYNC",      false);
		PROP_USE_INTELLISORT                    = new CCBoolProperty(CAT_COMMON,            this,   "PROP_USE_INTELLISORT",                     false);
		PROP_COMMON_CHECKFORUPDATES             = new CCBoolProperty(CAT_COMMON,            this,   "PROP_COMMON_CHECKFORUPDATES",              true);
		PROP_COMMON_PRESCANFILESYSTEM           = new CCBoolProperty(CAT_COMMON,            this,   "PROP_COMMON_PRESCANFILESYSTEM",            true);
		PROP_LOG_APPEND                         = new CCBoolProperty(CAT_COMMON,            this,   "PROP_LOG_APPEND",                          true);
		PROP_DATABASE_CLEANSHUTDOWN             = new CCBoolProperty(CAT_COMMON,            this,   "PROP_DATABASE_CLEANSHUTDOWN",              false);
		PROP_MAINFRAME_FILTERLISTPATH           = new CCStringProperty(CAT_COMMON,          this,   "PROP_MAINFRAME_FILTERLISTPATH",            getDefFLPath());

		PROP_UI_LOOKANDFEEL                     = new CCVIntProperty(CAT_VIEW,              this,   "PROP_UI_LOOKANDFEEL",                      getDefStyle(),                      LookAndFeelManager.getLookAndFeelVector());
		PROP_MAINFRAME_TABLEBACKGROUND          = new CCEnumProperty<>(CAT_VIEW,            this,   "PROP_MAINFRAME_TABLEBACKGROUND",           UITableBackground.WHITE,            UITableBackground.getWrapper());
		PROP_LOADING_LIVEUPDATE                 = new CCBoolProperty(CAT_VIEW,              this,   "PROP_LOADING_LIVEUPDATE",                  false);
		PROP_MAINFRAME_SCROLLSPEED              = new CCPIntProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_SCROLLSPEED",               3);
		PROP_VIEW_DB_START_SORT                 = new CCEnumProperty<>(CAT_VIEW,            this,   "PROP_VIEW_DB_START_SORT",                  InitalSortingColumn.LOCALID,        InitalSortingColumn.getWrapper());
		PROP_TOOLBAR_ELEMENTS                   = new CCToolbarProperty(CAT_VIEW,           this,   "PROP_TOOLBAR_ELEMENTS",                    ClipToolbar.STANDARD_CONFIG);
		PROP_MAINFRAME_CLICKABLEZYKLUS          = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_CLICKABLEZYKLUS",           false);
		PROP_MAINFRAME_CLICKABLESCORE           = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_CLICKABLESCORE",            false);
		PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR    = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR",     false);
		PROP_MAINFRAME_SHOWTAGS                 = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_SHOWTAGS",                  true);
		PROP_MAINFRAME_SHOWGROUPS               = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_SHOWGROUPS",                true);
		PROP_MAINFRAME_SHOWCOVERCORNER          = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_SHOWCOVERCORNER",           true);
		PROP_MAINFRAME_DONT_FILTER_WATCHNEVER   = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_DONT_FILTER_WATCHNEVER",    true);
		PROP_MAINFRAME_SORT_GENRES              = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_SORT_GENRES",               true);
		PROP_MAINFRAME_SHOW_GROUP_ONLY_ON_HOVER = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_SHOW_GROUP_ONLY_ON_HOVER",  false);
		PROP_MAINFRAME_ASYNC_COVER_LOADING      = new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_ASYNC_COVER_LOADING",       false);
		PROP_MAINFRAME_SHOW_VIEWCOUNT      		= new CCBoolProperty(CAT_VIEW,              this,   "PROP_MAINFRAME_SHOW_VIEWCOUNT",       		true);
		PROP_MAINFRAME_VISIBLE_COLUMNS          = new CCEnumSetProperty<>(CAT_VIEW,         this,   "PROP_MAINFRAME_VISIBLE_COLUMNS",           EnumSetValue.ALL,                   MainFrameColumn.getWrapper());

		PROP_DATABASE_NAME                      = new CCStringProperty(CAT_DATABASE,        this,   "PROP_DATABASE_NAME",                       "ClipCornDB");
		PROP_LOG_PATH                           = new CCStringProperty(CAT_DATABASE,        this,   "PROP_LOG_PATH",                            "jClipcorn.log");
		PROP_SELF_DIRECTORY                     = new CCStringProperty(CAT_DATABASE,        this,   "PROP_SELF_DIRECTORY",                      "");
		PROP_COVER_PREFIX                       = new CCStringProperty(CAT_DATABASE,        this,   "PROP_COVER_PREFIX",                        "cover_");
		PROP_COVER_TYPE                         = new CCStringProperty(CAT_DATABASE,        this,   "PROP_COVER_TYPE",                          "png");
		PROP_DATABASE_CREATELOGFILE             = new CCBoolProperty(CAT_DATABASE,          this,   "PROP_DATABASE_CREATELOGFILE",              true);
		PROP_DATABASE_COVERCACHESIZE            = new CCPIntProperty(CAT_DATABASE,          this,   "PROP_DATABASE_COVERCACHESIZE",             128);
		PROP_DATABASE_MAX_COVER_SIZE            = new CCEnumProperty<>(CAT_DATABASE,        this,   "PROP_DATABASE_MAX_COVER_SIZE",             CoverImageSize.BASE_SIZE,           CoverImageSize.getWrapper());
		PROP_LOG_MAX_LINECOUNT                  = new CCPIntProperty(CAT_DATABASE,          this,   "PROP_LOG_MAX_LINECOUNT",                   1048576); // 2^20
		PROP_DATABASE_LOAD_ALL_COVERDATA        = new CCBoolProperty(CAT_DATABASE,          this,   "PROP_DATABASE_LOAD_ALL_COVERDATA",         false);
		PROP_DATABASE_COVER_QUANTIZER           = new CCEnumProperty<>(CAT_DATABASE,        this,   "PROP_DATABASE_COVER_QUANTIZER",            ColorQuantizerMethod.HSL_DISTINCT_SELECTION, ColorQuantizerMethod.getWrapper());

		PROP_DATABASE_DEFAULTPARSERLANG         = new CCEnumProperty<>(CAT_PARSER,          this,   "PROP_DATABASE_DEFAULTPARSERLANG",          CCDBLanguage.GERMAN,                CCDBLanguage.getWrapper());
		PROP_TMDB_LANGUAGE                      = new CCEnumProperty<>(CAT_PARSER,          this,   "PROP_TMDB_LANGUAGE",                       getDefBLanguage(),                  BrowserLanguage.getWrapper());
		PROP_IMAGESEARCH_IMPL                   = new CCEnumSetProperty<>(CAT_PARSER,       this,   "PROP_IMAGESEARCH_IMPL",                    getDefImagesearchImpl(),            ImageSearchImplementation.getWrapper());
		PROP_METAPARSER_IMPL                    = new CCEnumSetProperty<>(CAT_PARSER,       this,   "PROP_METAPARSER_IMPL",                     EnumSetValue.ALL,                   MetadataParserImplementation.getWrapper());

		PROP_ON_DBLCLICK_MOVE                   = new CCEnumProperty<>(CAT_MOVIES,          this,   "PROP_ON_DBLCLICK_MOVE",                    DoubleClickAction.PLAY,             DoubleClickAction.getWrapper());
		PROP_MAINFRAME_AUTOMATICRESETWATCHLATER = new CCBoolProperty(CAT_MOVIES,            this,   "PROP_MAINFRAME_AUTOMATICRESETWATCHLATER",  true);
		PROP_MAINFRAME_AUTOMATICRESETWATCHNEVER = new CCBoolProperty(CAT_MOVIES,            this,   "PROP_MAINFRAME_AUTOMATICRESETWATCHNEVER",  true);
		PROP_PREVIEWMOVIE_SINGLETON             = new CCBoolProperty(CAT_MOVIES,            this,   "PROP_PREVIEWMOVIE_SINGLETON",              true);

		PROP_STATUSBAR_CALC_SERIES_IN_LENGTH    = new CCBoolProperty(CAT_SERIES,            this,   "PROP_STATUSBAR_CALC_SERIES_IN_LENGTH",     false);
		PROP_STATUSBAR_CALC_SERIES_IN_SIZE      = new CCBoolProperty(CAT_SERIES,            this,   "PROP_STATUSBAR_CALC_SERIES_IN_SIZE",       false);
		PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT      = new CCBoolProperty(CAT_SERIES,            this,   "PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT",       false);
		PROP_PREVSERIES_3DCOVER                 = new CCBoolProperty(CAT_SERIES,            this,   "PROP_PREVSERIES_3DCOVER",                  true);
		PROP_PREVSERIES_COVERBORDER             = new CCBoolProperty(CAT_SERIES,            this,   "PROP_PREVSERIES_COVERBORDER",              true);
		PROP_SERIES_ADDDATECALCULATION          = new CCEnumProperty<>(CAT_SERIES,          this,   "PROP_SERIES_ADDDATECALCULATION",           AddDateAlgorithm.NEWEST_DATE,       AddDateAlgorithm.getWrapper());
		PROP_SERIES_DISPLAYED_DATE              = new CCEnumProperty<>(CAT_SERIES,          this,   "PROP_SERIES_DISPLAYED_DATE",               DisplayDateAlgorithm.LAST_VIEWED,   DisplayDateAlgorithm.getWrapper());
		PROP_VALIDATE_CHECK_SERIES_STRUCTURE    = new CCBoolProperty(CAT_SERIES,            this,   "PROP_VALIDATE_CHECK_SERIES_STRUCTURE",     false);
		PROP_SHOW_PARTIAL_VIEWED_STATE          = new CCBoolProperty(CAT_SERIES,            this,   "PROP_SHOW_PARTIAL_VIEWED_STATE",           false);
		PROP_SEASON_INDEX_REGEXPRESSIONS        = new CCSeasonRegexListProperty(CAT_SERIES, this,   "PROP_SEASON_INDEX_REGEXPRESSIONS",         getDefSeasonRegex());
		PROP_SERIES_NEXT_EPISODE_HEURISTIC      = new CCEnumProperty<>(CAT_SERIES,          this,   "PROP_SERIES_NEXT_EPISODE_HEURISTIC",       NextEpisodeHeuristic.AUTOMATIC,     NextEpisodeHeuristic.getWrapper());
		PROP_PREVIEWSERIES_SINGLETON            = new CCBoolProperty(CAT_SERIES,            this,   "PROP_PREVIEWSERIES_SINGLETON",             true);

		PROP_PLAY_VLC_PATH                      = new CCPathProperty(CAT_PLAY,              this,   "PROP_PLAY_VLC_PATH",                       "",                                 "vlc.exe",       CCPathPropertyMode.FILES);
		PROP_PLAY_MEDIAINFO_PATH                = new CCPathProperty(CAT_PLAY,              this,   "PROP_PLAY_MEDIAINFO_PATH",                 "",                                 "mediainfo.exe", CCPathPropertyMode.FILES);
		PROP_PLAY_FFMPEG_PATH                   = new CCPathProperty(CAT_PLAY,              this,   "PROP_PLAY_FFMPEG_PATH",                    "",                                 "ffmpeg.exe",    CCPathPropertyMode.FILES);
		PROP_PLAY_FFPROBE_PATH                  = new CCPathProperty(CAT_PLAY,              this,   "PROP_PLAY_FFPROBE_PATH",                   "",                                 "ffprobe.exe",   CCPathPropertyMode.FILES);
		PROP_PLAY_MP4BOX_PATH                   = new CCPathProperty(CAT_PLAY,              this,   "PROP_PLAY_MP4BOX_PATH",                    "",                                 "mp4box.exe",    CCPathPropertyMode.FILES);
		PROP_PLAY_VLC_FULLSCREEN                = new CCBoolProperty(CAT_PLAY,              this,   "PROP_PLAY_VLC_FULLSCREEN",                 false);
		PROP_PLAY_VLC_AUTOPLAY                  = new CCBoolProperty(CAT_PLAY,              this,   "PROP_PLAY_VLC_AUTOPLAY",                   true);
		PROP_PLAY_USESTANDARDONMISSINGVLC       = new CCBoolProperty(CAT_PLAY,              this,   "PROP_PLAY_USESTANDARDONMISSINGVLC",        true);
		PROP_PLAY_VLCSINGLEINSTANCEMODE         = new CCBoolProperty(CAT_PLAY,              this,   "PROP_PLAY_VLCSINGLEINSTANCEMODE",          true);

		PROP_BACKUP_CREATEBACKUPS               = new CCBoolProperty(CAT_BACKUP,            this,   "PROP_BACKUP_CREATEBACKUPS",                false);
		PROP_BACKUP_FOLDERNAME                  = new CCStringProperty(CAT_BACKUP,          this,   "PROP_BACKUP_FOLDERNAME",                   "jClipCorn_backup");
		PROP_BACKUP_BACKUPTIME                  = new CCPIntProperty(CAT_BACKUP,            this,   "PROP_BACKUP_BACKUPTIME",                   7);
		PROP_BACKUP_COMPRESSION                 = new CCRIntProperty(CAT_BACKUP,            this,   "PROP_BACKUP_COMPRESSION",                  0,                                  10);
		PROP_BACKUP_AUTODELETEBACKUPS           = new CCBoolProperty(CAT_BACKUP,            this,   "PROP_BACKUP_AUTODELETEBACKUPS",            true);
		PROP_BACKUP_LIFETIME                    = new CCPIntProperty(CAT_BACKUP,            this,   "PROP_BACKUP_LIFETIME",                     56);
		PROP_BACKUP_EXCLUDECOVERS               = new CCBoolProperty(CAT_BACKUP,            this,   "PROP_BACKUP_EXCLUDECOVERS",                false);

		PROP_STATBAR_ELCOUNT                    = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_ELCOUNT",                     true);
		PROP_STATBAR_PROGRESSBAR                = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_PROGRESSBAR",                 true);
		PROP_STATBAR_LOG                        = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_LOG",                         true);
		PROP_STATBAR_VIEWEDCOUNT                = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_VIEWEDCOUNT",                 true);
		PROP_STATBAR_SERIESCOUNT                = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_SERIESCOUNT",                 true);
		PROP_STATBAR_LENGTH                     = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_LENGTH",                      getDefStatbarLength());
		PROP_STATBAR_SIZE                       = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_SIZE",                        true);
		PROP_STATBAR_STARTTIME                  = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_STARTTIME",                   true);
		PROP_STATBAR_DRIVESCAN                  = new CCBoolProperty(CAT_STATUSBAR,         this,   "PROP_STATBAR_DRIVESCAN",                   false);

		PROP_SCANFOLDER_INCLUDESERIES           = new CCBoolProperty(CAT_OTHERFRAMES,       this,   "PROP_SCANFOLDER_INCLUDESERIES",            false);
		PROP_SCANFOLDER_EXCLUDEIFOS             = new CCBoolProperty(CAT_OTHERFRAMES,       this,   "PROP_SCANFOLDER_EXCLUDEIFOS",              false);
		PROP_VALIDATE_FILESIZEDRIFT             = new CCRIntProperty(CAT_OTHERFRAMES,       this,   "PROP_VALIDATE_FILESIZEDRIFT",              0,                                  100);
		PROP_VALIDATE_DUP_IGNORE_IFO            = new CCBoolProperty(CAT_OTHERFRAMES,       this,   "PROP_VALIDATE_DUP_IGNORE_IFO",             true);
		PROP_STATISTICS_TIMELINEGRAVITY         = new CCPIntProperty(CAT_OTHERFRAMES,       this,   "PROP_STATISTICS_TIMELINEGRAVITY",          7);

		PROP_BACKUP_LASTBACKUP                  = new CCDateProperty(NONVISIBLE,            this,   "PROP_BACKUP_LASTBACKUP",                   CCDate.getMinimumDate());
		PROP_OTHER_DEBUGMODE                    = new CCBoolProperty(NONVISIBLE,            this,   "PROP_OTHER_DEBUGMODE",                     false);
		PROP_MASSCHANGESCORE_SKIPRATED          = new CCBoolProperty(NONVISIBLE,            this,   "PROP_MASSCHANGESCORE_SKIPRATED",           false);
		PROP_MASSCHANGESCORE_ONLYVIEWED         = new CCBoolProperty(NONVISIBLE,            this,   "PROP_MASSCHANGESCORE_ONLYVIEWED",          false);
		PROP_MASSCHANGEVIEWED_ONLYUNVIEWED      = new CCBoolProperty(NONVISIBLE,            this,   "PROP_MASSCHANGEVIEWED_ONLYUNVIEWED",       false);
		PROP_IMPORT_RESETVIEWED                 = new CCBoolProperty(NONVISIBLE,            this,   "PROP_IMPORT_RESETVIEWED",                  true);
		PROP_IMPORT_ONLYWITHCOVER               = new CCBoolProperty(NONVISIBLE,            this,   "PROP_IMPORT_ONLYWITHCOVER",                true);
		PROP_IMPORT_RESETADDDATE                = new CCBoolProperty(NONVISIBLE,            this,   "PROP_IMPORT_RESETADDDATE",                 true);
		PROP_IMPORT_RESETSCORE                  = new CCBoolProperty(NONVISIBLE,            this,   "PROP_IMPORT_RESETSCORE",                   true);
		PROP_IMPORT_RESETTAGS                   = new CCBoolProperty(NONVISIBLE,            this,   "PROP_IMPORT_RESETTAGS",                    true);
		PROP_PARSEIMDB_LANGUAGE                 = new CCRIntProperty(NONVISIBLE,            this,   "PROP_PARSEIMDB_LANGUAGE",                  1,                                  2);
		PROP_STATISTICS_INTERACTIVECHARTS       = new CCBoolProperty(NONVISIBLE,            this,   "PROP_STATISTICS_INTERACTIVECHARTS",        false);
		PROP_MAINFRAME_WIDTH                    = new CCPIntProperty(NONVISIBLE,            this,   "PROP_MAINFRAME_WIDTH",                     875);
		PROP_MAINFRAME_HEIGHT                   = new CCPIntProperty(NONVISIBLE,            this,   "PROP_MAINFRAME_HEIGHT",                    getDefMFHeight());
		PROP_DATABASE_DRIVER                    = new CCEnumProperty<>(NONVISIBLE,          this,   "PROP_DATABASE_DRIVER",                     CCDatabaseDriver.SQLITE,            CCDatabaseDriver.getWrapper());
		PROP_SHOW_EXTENDED_FEATURES             = new CCBoolProperty(NONVISIBLE,            this,   "PROP_SHOW_EXTENDED_FEATURES",              true);
		PROP_DEBUG_USE_HTTPCACHE                = new CCBoolProperty(NONVISIBLE,            this,   "PROP_DEBUG_USE_HTTPCACHE",                 false);
		PROP_DEBUG_HTTPCACHE_PATH               = new CCPathProperty(NONVISIBLE,            this,   "PROP_DEBUG_HTTPCACHE_PATH",                "%temp%/jClipCorn/httpcache/",      null, CCPathPropertyMode.DIRECTORIES);
		PROP_SERIES_PREVIEWFRAME_HEIGHT         = new CCPIntProperty(NONVISIBLE,            this,   "PROP_SERIES_PREVIEWFRAME_HEIGHT",          22);
		PROP_ANILIST_PREFERRED_TITLE_LANG       = new CCEnumProperty<>(NONVISIBLE,          this,   "PROP_ANILIST_PREFERRED_TITLE_LANG",        AniListTitleLang.PREFERRED,         AniListTitleLang.getWrapper());
		PROP_DISABLE_SSL_VERIFY                 = new CCBoolProperty(NONVISIBLE,            this,   "PROP_DISABLE_SSL_VERIFY",                  false);
		PROP_MIN_DRIVEMAP_RESCAN_TIME           = new CCPIntProperty(NONVISIBLE,            this,   "PROP_MIN_DRIVEMAP_RESCAN_TIME",            30*1000);
		PROP_DRIVEMAP_REMOUNT_NETDRIVES         = new CCBoolProperty(NONVISIBLE,            this,   "PROP_DRIVEMAP_REMOUNT_NETDRIVES",          false);
		PROP_CHECKDATABASE_OPT_MOVIES           = new CCBoolProperty(NONVISIBLE,            this,   "PROP_CHECKDATABASE_OPT_MOVIES",            true);
		PROP_CHECKDATABASE_OPT_SEASONS          = new CCBoolProperty(NONVISIBLE,            this,   "PROP_CHECKDATABASE_OPT_SEASONS",           true);
		PROP_CHECKDATABASE_OPT_SERIES           = new CCBoolProperty(NONVISIBLE,            this,   "PROP_CHECKDATABASE_OPT_SERIES",            true);
		PROP_CHECKDATABASE_OPT_EPISODES         = new CCBoolProperty(NONVISIBLE,            this,   "PROP_CHECKDATABASE_OPT_EPISODES",          true);
		PROP_CHECKDATABASE_OPT_COVERS           = new CCBoolProperty(NONVISIBLE,            this,   "PROP_CHECKDATABASE_OPT_COVERS",            true);
		PROP_CHECKDATABASE_OPT_FILES            = new CCBoolProperty(NONVISIBLE,            this,   "PROP_CHECKDATABASE_OPT_FILES",             true);
		PROP_CHECKDATABASE_OPT_EXTRA            = new CCBoolProperty(NONVISIBLE,            this,   "PROP_CHECKDATABASE_OPT_EXTRA",             true);

		PROP_ADD_MOVIE_RELATIVE_AUTO            = new CCBoolProperty(CAT_PATHSYNTAX,        this,   "PROP_ADD_MOVIE_RELATIVE_AUTO",             true);
		PROP_PATHSYNTAX_SELF                    = new CCBoolProperty(CAT_PATHSYNTAX,        this,   "PROP_PATHSYNTAX_SELF",                     true);
		PROP_PATHSYNTAX_DRIVELABEL              = new CCBoolProperty(CAT_PATHSYNTAX,        this,   "PROP_PATHSYNTAX_DRIVELABEL",               true);
		PROP_PATHSYNTAX_SELFDIR                 = new CCBoolProperty(CAT_PATHSYNTAX,        this,   "PROP_PATHSYNTAX_SELFDIR",                  true);
		PROP_PATHSYNTAX_NETDRIVE                = new CCBoolProperty(CAT_PATHSYNTAX,        this,   "PROP_PATHSYNTAX_NETDRIVE",                 true);
		PROP_PATHSYNTAX_VAR1                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR1",                     PathSyntaxVar.EMPTY);
		PROP_PATHSYNTAX_VAR2                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR2",                     PathSyntaxVar.EMPTY);
		PROP_PATHSYNTAX_VAR3                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR3",                     PathSyntaxVar.EMPTY);
		PROP_PATHSYNTAX_VAR4                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR4",                     PathSyntaxVar.EMPTY);
		PROP_PATHSYNTAX_VAR5                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR5",                     PathSyntaxVar.EMPTY);
	}

	private String getDefFLPath() {
		return "<?self>jClipCorn."+ExportHelper.EXTENSION_FILTERLIST; //$NON-NLS-1$
	}
	
	private ArrayList<String> getDefSeasonRegex() {
		ArrayList<String> result = new ArrayList<>();
		result.add("Staffel[ ]?(?<index>[0-9]+).*"); //$NON-NLS-1$
		result.add("Season[ ]?(?<index>[0-9]+).*"); //$NON-NLS-1$
		result.add("Volume[ ]?(?<index>[0-9]+).*"); //$NON-NLS-1$
		result.add(""); //$NON-NLS-1$
		result.add("(?<index>[0-9]+) - .*"); //$NON-NLS-1$
		result.add("S(?<index>[0-9]+) - .*"); //$NON-NLS-1$
		return result;
	}

	private UILanguage getDefLanguage() {
		if (Locale.getDefault().getCountry().equals(Locale.GERMANY.getCountry())) return UILanguage.DUAL;
		if (Locale.getDefault().getCountry().equals(Locale.US.getCountry())) return UILanguage.ENGLISCH;
		if (Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage())) return UILanguage.ENGLISCH;
		
		return UILanguage.DEFAULT;
	}
	
	private CCDateTimeFormat getDefDTFormat() {
		if (Locale.getDefault().getCountry().equals(Locale.GERMANY.getCountry())) return CCDateTimeFormat.GERMAN;
		if (Locale.getDefault().getCountry().equals(Locale.US.getCountry())) return CCDateTimeFormat.AMERICA;

		return CCDateTimeFormat.ISO_8601;
	}

	private BrowserLanguage getDefBLanguage() {
		if (Locale.getDefault().getCountry().equals(Locale.GERMANY.getCountry())) return BrowserLanguage.GERMAN;
		if (Locale.getDefault().getCountry().equals(Locale.US.getCountry())) return BrowserLanguage.ENGLISH;
		if (Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage())) return BrowserLanguage.ENGLISH;
		
		return BrowserLanguage.ENGLISH;
	}
	
	private int getDefStyle() {
		if (ApplicationHelper.isWindows()) return LookAndFeelManager.ID_LNF_WINDOWS;
		if (ApplicationHelper.isMac())     return LookAndFeelManager.ID_LNF_METAL;
		if (ApplicationHelper.isUnix())    return LookAndFeelManager.ID_LNF_METAL;
		
		return LookAndFeelManager.ID_LNF_METAL;
	}
	
	private boolean getDefStatbarLength() {
		return getDefStyle() != LookAndFeelManager.ID_LNF_WINDOWS;
	}
	
	private int getDefMFHeight() {
		if (LookAndFeelManager.isMetal())
			return 670;
		else
			return 660;
	}

	public static CCProperties getInstance() {
		if (Main.DEBUG && mainInstance == null) { //ONLY FOR WindowBuilder
			return new CCProperties();
		}
		return mainInstance;
	}
	
	public void load(String path) {
		try {
			synchronized (_fileLock) {
				FileInputStream stream = new FileInputStream(path);
				properties.load(stream);
				stream.close();
			}
		} catch (IOException e) {
			firstLaunch = true;
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFileNotFound", path)); //$NON-NLS-1$
		}
	}
	
	public void save() {
		if (path == null) return;
		
		try {
			synchronized (_fileLock) {
				FileOutputStream stream = new FileOutputStream(path);
				properties.store(stream, HEADER);
				stream.close();
			}
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}
	
	public void setProperty(String ident, String val) {
		properties.setProperty(ident, val);
		fireChangedEvent();
	}
	
	public String getProperty(String ident) {
		return properties.getProperty(ident);
	}
	
	public void addPropertyToList(CCProperty<Object> p) {
		propertylist.add(p);
	}
	
	private void fireChangedEvent() {
		save();
	}
	
	public List<CCProperty<Object>> getPropertyList() {
		return propertylist;
	}

	public void resetAll() {
		for (CCProperty<?> prop : propertylist) {
			prop.setDefault();
		}
	}

	public int getCountForCategory(CCPropertyCategory cat) {
		int c = 0;
		
		for (CCProperty<?> p : propertylist) {
			if (p.getCategory().equals(cat)) c++;
		}
		
		return c;
	}

	public int getCategoryCount() {
		int cat = -1;
		
		for (CCProperty<?> p : propertylist) {
			if (p.getCategory().Index > cat) {
				cat = p.getCategory().Index;
			}
		}
		
		return cat + 1;
	}

	private void interpreteArgs(String[] args) {
		for (String arg : args) {
			for (String readOnlyArg : readOnlyArgs) {
				if (arg.equalsIgnoreCase(readOnlyArg)) {
					ARG_READONLY = true;

					CCLog.addDebug("ReadOnly Mode activated (" + arg + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	public List<PathSyntaxVar> getActivePathVariables() {
		List<PathSyntaxVar> r = new ArrayList<>();

		PathSyntaxVar v1 = PROP_PATHSYNTAX_VAR1.getValue();
		if (!Str.isNullOrWhitespace(v1.Key)) r.add(v1);

		PathSyntaxVar v2 = PROP_PATHSYNTAX_VAR2.getValue();
		if (!Str.isNullOrWhitespace(v2.Key)) r.add(v2);

		PathSyntaxVar v3 = PROP_PATHSYNTAX_VAR3.getValue();
		if (!Str.isNullOrWhitespace(v3.Key)) r.add(v3);

		PathSyntaxVar v4 = PROP_PATHSYNTAX_VAR4.getValue();
		if (!Str.isNullOrWhitespace(v4.Key)) r.add(v4);

		PathSyntaxVar v5 = PROP_PATHSYNTAX_VAR5.getValue();
		if (!Str.isNullOrWhitespace(v5.Key)) r.add(v5);

		return r;
	}

	private Set<ImageSearchImplementation> getDefImagesearchImpl() {
		HashSet<ImageSearchImplementation> hs = new HashSet<>();
		hs.add(ImageSearchImplementation.TMDP_POSTER);
		hs.add(ImageSearchImplementation.IMDB_COVER);
		hs.add(ImageSearchImplementation.IMDB_SEC_COVER);
		return hs;
	}
}
