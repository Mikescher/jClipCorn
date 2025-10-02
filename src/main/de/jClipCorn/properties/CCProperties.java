package de.jClipCorn.properties;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.frames.vlcRobot.VLCRobotFrequency;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.toolbar.ClipToolbar;
import de.jClipCorn.properties.enumerations.*;
import de.jClipCorn.properties.impl.*;
import de.jClipCorn.properties.property.*;
import de.jClipCorn.properties.property.CCEnumSetProperty.EnumSetValue;
import de.jClipCorn.properties.property.CCFSPathProperty.CCPathPropertyMode;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.datatypes.CharListMatchType;
import de.jClipCorn.util.datatypes.ElemFieldMatchType;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTimeFormat;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ApplicationHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CCProperties implements ICCPropertySource {
	private final static String HEADER = "jClipCorn Configuration File"; //$NON-NLS-1$
	
	public final static CCPropertyCategory NONVISIBLE			= new CCPropertyCategory();
	public final static CCPropertyCategory CAT_COMMON 			= new CCPropertyCategory(0,  "COMMON");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_VIEW 			= new CCPropertyCategory(1,  "VIEW");         //$NON-NLS-1$
	public final static CCPropertyCategory CAT_DATABASE 		= new CCPropertyCategory(2,  "DATABASE");     //$NON-NLS-1$
	public final static CCPropertyCategory CAT_PARSER	 		= new CCPropertyCategory(3,  "PARSER");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_MOVIES	 		= new CCPropertyCategory(4,  "MOVIES");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_SERIES 			= new CCPropertyCategory(5,  "SERIES");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_PLAY 			= new CCPropertyCategory(6,  "PLAY");         //$NON-NLS-1$
	public final static CCPropertyCategory CAT_TOOLS 			= new CCPropertyCategory(7,  "TOOLS");        //$NON-NLS-1$
	public final static CCPropertyCategory CAT_BACKUP 			= new CCPropertyCategory(8,  "BACKUP");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_STATUSBAR		= new CCPropertyCategory(9,  "STATUSBAR");    //$NON-NLS-1$
	public final static CCPropertyCategory CAT_PATHSYNTAX 		= new CCPropertyCategory(10, "PATHSYNTAX");   //$NON-NLS-1$
	public final static CCPropertyCategory CAT_OTHERFRAMES 		= new CCPropertyCategory(11, "OTHERFRAMES");  //$NON-NLS-1$
	public final static CCPropertyCategory CAT_KEYSTROKES 		= new CCPropertyCategory(12, "KEYSTROKES");   //$NON-NLS-1$
	
	public final static CCPropertyCategory[] CATEGORIES = {
		CAT_COMMON,
		CAT_VIEW,
		CAT_DATABASE,
		CAT_PARSER,
		CAT_MOVIES,
		CAT_SERIES,
		CAT_PLAY,
		CAT_TOOLS,
		CAT_BACKUP,
		CAT_STATUSBAR,
		CAT_PATHSYNTAX,
		CAT_OTHERFRAMES,
		CAT_KEYSTROKES 	
	};
	
	@SuppressWarnings("nls")
	public final static String[] readOnlyArgs = {"readonly", "-readonly", "--readonly", "read-only", "-read-only", "--read-only", "ro", "-ro", "--ro"};

	private final Object _fileLock = new Object();
	private final List<CCProperty<Object>> propertylist = new Vector<>();

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
	public CCLookAndFeelProperty                            PROP_UI_APPTHEME;
	public CCExecutableProperty                             PROP_PLAY_VLC_PATH;
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
	public CCBoolProperty                                   PROP_PREVSERIES_SMALLERCOVER;
	public CCDoubleProperty                                 PROP_PREVSERIES_SMALLERCOVER_FACTOR;
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
	public CCCCPathProperty                                 PROP_MAINFRAME_FILTERLISTPATH;
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
	public CCFSPathProperty                                 PROP_DEBUG_HTTPCACHE_PATH;
	public CCPIntProperty                                   PROP_SERIES_PREVIEWFRAME_HEIGHT;
	public CCEnumProperty<NextEpisodeHeuristic>             PROP_SERIES_NEXT_EPISODE_HEURISTIC;
	public CCEnumProperty<CoverImageSize>                   PROP_DATABASE_MAX_COVER_SIZE;
	public CCBoolProperty                                   PROP_MAINFRAME_SHOW_GROUP_ONLY_ON_HOVER;
	public CCBoolProperty                                   PROP_MAINFRAME_ASYNC_COVER_LOADING;
	public CCEnumProperty<AniListTitleLang>                 PROP_ANILIST_PREFERRED_TITLE_LANG;
	public CCBoolProperty                                   PROP_PATHSYNTAX_SELF;
	public CCBoolProperty                                   PROP_PATHSYNTAX_DRIVELABEL;
	public CCBoolProperty                                   PROP_PATHSYNTAX_SELFDIR;
	public CCBoolProperty                                   PROP_PATHSYNTAX_NETDRIVE;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR1;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR2;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR3;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR4;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR5;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR6;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR7;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR8;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR9;
	public CCPathVarProperty                                PROP_PATHSYNTAX_VAR10;
	public CCPIntProperty                                   PROP_MIN_DRIVEMAP_RESCAN_TIME;
	public CCBoolProperty                                   PROP_STATBAR_DRIVESCAN;
	public CCBoolProperty                                   PROP_MAINFRAME_SHOW_VIEWCOUNT;
	public CCBoolProperty                                   PROP_DRIVEMAP_REMOUNT_NETDRIVES;
	public CCExecutableProperty                             PROP_PLAY_MEDIAINFO_PATH;
	public CCExecutableProperty                             PROP_PLAY_FFMPEG_PATH;
	public CCExecutableProperty                             PROP_PLAY_FFPROBE_PATH;
	public CCExecutableProperty                             PROP_PLAY_MP4BOX_PATH;
	public CCBoolProperty                                   PROP_PREVIEWSERIES_SINGLETON;
	public CCBoolProperty                                   PROP_PREVIEWMOVIE_SINGLETON;
	public CCBoolProperty                                   PROP_DATABASE_LOAD_ALL_COVERDATA;
	public CCEnumProperty<ColorQuantizerMethod>             PROP_DATABASE_COVER_QUANTIZER;
	public CCStringSetProperty                              PROP_CHECKDATABASE_OPTIONS;
	public CCEnumSetProperty<MainFrameColumn>               PROP_MAINFRAME_VISIBLE_COLUMNS;
	public CCBoolProperty                                   PROP_VLC_ROBOT_ENABLED;
	public CCRIntProperty                                   PROP_VLC_ROBOT_PORT;
	public CCStringProperty                                 PROP_VLC_ROBOT_PASSWORD;
	public CCBoolProperty                                   PROP_VLC_ROBOT_KEEP_POSITION;
	public CCPIntProperty                                   PROP_FOLDERLANG_IGNORE_PERC;
	public CCEnumProperty<VLCRobotFrequency>                PROP_VLC_ROBOT_FREQUENCY;
	public CCNamedPathProperty                              PROP_PLAY_ALT_PROG_1;
	public CCNamedPathProperty                              PROP_PLAY_ALT_PROG_2;
	public CCNamedPathProperty                              PROP_PLAY_ALT_PROG_3;
	public CCNamedPathProperty                              PROP_PLAY_ALT_PROG_4;
	public CCNamedPathProperty                              PROP_PLAY_ALT_PROG_5;
	public CCPIntProperty                                   PROP_MAX_UNDOLASTWATCH_HOUR_DIFF;
	public CCPIntProperty                                   PROP_MAX_FASTREWATCH_HOUR_DIFF;
	public CCBoolProperty                                   PROP_VLC_ROBOT_QUEUE_PREEMPTIVE;
	public CCStringProperty                                 PROP_MAINFRAME_COLUMN_SIZE_CACHE;
	public CCBoolProperty                                   PROP_MAINFRAME_FILTERTREE_RECOLLAPSE;
	public CCFSPathProperty                                 PROP_DATABASE_DIR;
	public CCEnumProperty<SeriesViewCountMode>              PROP_SERIES_VIEWCOUNT_MODE;
	public CCBoolProperty                                   PROP_SKIP_DEFAULT_LANG_IN_FILENAMES;
	public CCBoolProperty                                   PROP_PLAY_FAILONMISSINGFILES;
	public CCBoolProperty                                   PROP_RESET_SORT_ON_FILTERCLEAR;
	public CCBoolProperty                                   PROP_CHARSELECTOR_DYNAMIC_OPACTITY;
	public CCRIntProperty                                   PROP_TABLE_MAX_SUBTITLE_COUNT;
	public CCEnumProperty<CharListMatchType>                PROP_CHARSELECTOR_MATCHMODE;
	public CCEnumProperty<ElemFieldMatchType>               PROP_CHARSELECTOR_SELMODE;
	public CCBoolProperty                                   PROP_CHARSELECTOR_EXCLUSIONS;
	public CCBoolProperty                                   PROP_CHARSELECTOR_IGNORENONCHARS;
	public CCBoolProperty                                   PROP_MAINTABLE_INSTANTTOOLTIPS;
	public CCBoolProperty                                   PROP_MAINTABLE_INFINITETOOLTIPS;
	public CCBoolProperty                                   PROP_SERIESTABLE_INSTANTTOOLTIPS;
	public CCBoolProperty                                   PROP_SERIESTABLE_INFINITETOOLTIPS;
	public CCBoolProperty                                   PROP_PREVSERIES_SMALLER_COVER;
	public CCPIntProperty                                   PROP_PREVIEWSERIESFRAME_WIDTH;

	// do not use in most cases - use db.isReadonly() or movielist.isReadonly()
	public boolean ARG_READONLY = false;
	
	public boolean firstLaunch = false;
	
	private final Properties properties;
	private final FSPath path;
	private final DriveMap driveMap;

	private CCProperties() {
		properties = new Properties();
		this.path = null;
		this.driveMap = new DriveMap(this);
		
		createProperties();
	}
	
	private CCProperties(FSPath path, String[] args) {
		properties = new Properties();
		this.path = path;
		this.driveMap = new DriveMap(this);
		load(path);
		
		createProperties();
		interpreteArgs(args);

		CCLog.addDebug(propertylist.size() + " Properties in List intialized"); //$NON-NLS-1$
		
		if (firstLaunch) save();

		LocaleBundle.updateLang(this);
	}

	public static CCProperties create(FSPath path, String[] args) {
		return new CCProperties(path, args);
	}

	public static CCProperties createReadonly(FSPath path) {
		return new CCProperties(path, new String[]{readOnlyArgs[0]});
	}
	
	public static CCProperties createInMemory() {
		return new CCProperties();
	}

	public DriveMap getDriveMap() {
		return driveMap;
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
		PROP_MAINFRAME_FILTERLISTPATH           = new CCCCPathProperty(CAT_COMMON,          this,   "PROP_MAINFRAME_FILTERLISTPATH",            getDefFLPath());

		PROP_UI_APPTHEME                        = new CCLookAndFeelProperty(CAT_VIEW,       this,   "PROP_UI_APPTHEME",                         getDefTheme());
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
		PROP_MAINFRAME_VISIBLE_COLUMNS          = new CCEnumSetProperty<>(CAT_VIEW,         this,   "PROP_MAINFRAME_VISIBLE_COLUMNS",           getDefColumns(),                   MainFrameColumn.getWrapper());
		PROP_CHARSELECTOR_DYNAMIC_OPACTITY      = new CCBoolProperty(CAT_VIEW,              this,   "PROP_CHARSELECTOR_DYNAMIC_OPACTITY",       true);
		PROP_TABLE_MAX_SUBTITLE_COUNT           = new CCRIntProperty(CAT_VIEW,              this,   "PROP_TABLE_MAX_SUBTITLE_COUNT",            8,                                  -1, 16384);
		PROP_CHARSELECTOR_MATCHMODE             = new CCEnumProperty<>(CAT_VIEW,            this,   "PROP_CHARSELECTOR_MATCHMODE",              CharListMatchType.WORD_START,         CharListMatchType.getWrapper());
		PROP_CHARSELECTOR_SELMODE               = new CCEnumProperty<>(CAT_VIEW,            this,   "PROP_CHARSELECTOR_SELMODE",                ElemFieldMatchType.TITLE_AND_ZYKLUS,  ElemFieldMatchType.getWrapper());
		PROP_CHARSELECTOR_EXCLUSIONS            = new CCBoolProperty(CAT_VIEW,              this,   "PROP_CHARSELECTOR_EXCLUSIONS",             true);
		PROP_CHARSELECTOR_IGNORENONCHARS        = new CCBoolProperty(CAT_VIEW,              this,   "PROP_CHARSELECTOR_IGNORENONCHARS",         true);

		PROP_DATABASE_NAME                      = new CCStringProperty(CAT_DATABASE,        this,   "PROP_DATABASE_NAME",                       "ClipCornDB");
		PROP_DATABASE_DIR                       = new CCFSPathProperty(CAT_DATABASE,        this,   "PROP_DATABASE_DIR",                        FSPath.Empty,                       "",          CCPathPropertyMode.DIRECTORIES);
		PROP_LOG_PATH                           = new CCStringProperty(CAT_DATABASE,        this,   "PROP_LOG_PATH",                            "jClipcorn.log");
		PROP_SELF_DIRECTORY                     = new CCStringProperty(CAT_DATABASE,        this,   "PROP_SELF_DIRECTORY",                      "");
		PROP_COVER_PREFIX                       = new CCStringProperty(CAT_DATABASE,        this,   "PROP_COVER_PREFIX",                        "cover_");
		PROP_COVER_TYPE                         = new CCStringProperty(CAT_DATABASE,        this,   "PROP_COVER_TYPE",                          "png");
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
		PROP_PREVSERIES_SMALLER_COVER           = new CCBoolProperty(CAT_SERIES,            this,   "PROP_PREVSERIES_SMALLERCOVER",             true);
		PROP_PREVSERIES_COVERBORDER             = new CCBoolProperty(CAT_SERIES,            this,   "PROP_PREVSERIES_COVERBORDER",              true);
		PROP_SERIES_ADDDATECALCULATION          = new CCEnumProperty<>(CAT_SERIES,          this,   "PROP_SERIES_ADDDATECALCULATION",           AddDateAlgorithm.NEWEST_DATE,       AddDateAlgorithm.getWrapper());
		PROP_SERIES_DISPLAYED_DATE              = new CCEnumProperty<>(CAT_SERIES,          this,   "PROP_SERIES_DISPLAYED_DATE",               DisplayDateAlgorithm.LAST_VIEWED,   DisplayDateAlgorithm.getWrapper());
		PROP_VALIDATE_CHECK_SERIES_STRUCTURE    = new CCBoolProperty(CAT_SERIES,            this,   "PROP_VALIDATE_CHECK_SERIES_STRUCTURE",     false);
		PROP_SHOW_PARTIAL_VIEWED_STATE          = new CCBoolProperty(CAT_SERIES,            this,   "PROP_SHOW_PARTIAL_VIEWED_STATE",           false);
		PROP_SEASON_INDEX_REGEXPRESSIONS        = new CCSeasonRegexListProperty(CAT_SERIES, this,   "PROP_SEASON_INDEX_REGEXPRESSIONS",         getDefSeasonRegex());
		PROP_SERIES_NEXT_EPISODE_HEURISTIC      = new CCEnumProperty<>(CAT_SERIES,          this,   "PROP_SERIES_NEXT_EPISODE_HEURISTIC",       NextEpisodeHeuristic.AUTOMATIC,     NextEpisodeHeuristic.getWrapper());
		PROP_PREVIEWSERIES_SINGLETON            = new CCBoolProperty(CAT_SERIES,            this,   "PROP_PREVIEWSERIES_SINGLETON",             true);
		PROP_SERIES_VIEWCOUNT_MODE              = new CCEnumProperty<>(CAT_SERIES,          this,   "PROP_SERIES_VIEWCOUNT_MODE",               SeriesViewCountMode.AGGREGATE_MIN,  SeriesViewCountMode.getWrapper());

		PROP_PLAY_VLC_FULLSCREEN                = new CCBoolProperty(CAT_PLAY,              this,   "PROP_PLAY_VLC_FULLSCREEN",                 false);
		PROP_PLAY_VLC_AUTOPLAY                  = new CCBoolProperty(CAT_PLAY,              this,   "PROP_PLAY_VLC_AUTOPLAY",                   true);
		PROP_PLAY_USESTANDARDONMISSINGVLC       = new CCBoolProperty(CAT_PLAY,              this,   "PROP_PLAY_USESTANDARDONMISSINGVLC",        true);
		PROP_PLAY_VLCSINGLEINSTANCEMODE         = new CCBoolProperty(CAT_PLAY,              this,   "PROP_PLAY_VLCSINGLEINSTANCEMODE",          true);

		PROP_PLAY_VLC_PATH                      = new CCExecutableProperty(CAT_TOOLS,       this,   "PROP_PLAY_VLC_PATH",                       FSPath.Empty,                       VLCPathConf.INST);

		PROP_PLAY_ALT_PROG_1                    = new CCNamedPathProperty(CAT_TOOLS,        this,   "PROP_PLAY_ALT_PROG_1",                     NamedPathVar.EMPTY,                 ".exe",          CCPathPropertyMode.FILES);
		PROP_PLAY_ALT_PROG_2                    = new CCNamedPathProperty(CAT_TOOLS,        this,   "PROP_PLAY_ALT_PROG_2",                     NamedPathVar.EMPTY,                 ".exe",          CCPathPropertyMode.FILES);
		PROP_PLAY_ALT_PROG_3                    = new CCNamedPathProperty(CAT_TOOLS,        this,   "PROP_PLAY_ALT_PROG_3",                     NamedPathVar.EMPTY,                 ".exe",          CCPathPropertyMode.FILES);
		PROP_PLAY_ALT_PROG_4                    = new CCNamedPathProperty(CAT_TOOLS,        this,   "PROP_PLAY_ALT_PROG_4",                     NamedPathVar.EMPTY,                 ".exe",          CCPathPropertyMode.FILES);
		PROP_PLAY_ALT_PROG_5                    = new CCNamedPathProperty(CAT_TOOLS,        this,   "PROP_PLAY_ALT_PROG_5",                     NamedPathVar.EMPTY,                 ".exe",          CCPathPropertyMode.FILES);

		PROP_PLAY_MEDIAINFO_PATH                = new CCExecutableProperty(CAT_TOOLS,       this,   "PROP_PLAY_MEDIAINFO_PATH",                 FSPath.Empty,                       MediaInfoPathConf.INST);
		PROP_PLAY_FFMPEG_PATH                   = new CCExecutableProperty(CAT_TOOLS,       this,   "PROP_PLAY_FFMPEG_PATH",                    FSPath.Empty,                       FFMPEGPathConf.INST);
		PROP_PLAY_FFPROBE_PATH                  = new CCExecutableProperty(CAT_TOOLS,       this,   "PROP_PLAY_FFPROBE_PATH",                   FSPath.Empty,                       FFProbePathConf.INST);
		PROP_PLAY_MP4BOX_PATH                   = new CCExecutableProperty(CAT_TOOLS,       this,   "PROP_PLAY_MP4BOX_PATH",                    FSPath.Empty,                       MP4BoxPathConf.INST);

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
		PROP_SKIP_DEFAULT_LANG_IN_FILENAMES     = new CCBoolProperty(CAT_OTHERFRAMES,       this,   "PROP_SKIP_DEFAULT_LANG_IN_FILENAMES",      true);

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
		PROP_DEBUG_HTTPCACHE_PATH               = new CCFSPathProperty(NONVISIBLE,          this,   "PROP_DEBUG_HTTPCACHE_PATH",                getDefHttpCachePath(),       null, CCPathPropertyMode.DIRECTORIES);
		PROP_SERIES_PREVIEWFRAME_HEIGHT         = new CCPIntProperty(NONVISIBLE,            this,   "PROP_SERIES_PREVIEWFRAME_HEIGHT",          22);
		PROP_ANILIST_PREFERRED_TITLE_LANG       = new CCEnumProperty<>(NONVISIBLE,          this,   "PROP_ANILIST_PREFERRED_TITLE_LANG",        AniListTitleLang.PREFERRED,         AniListTitleLang.getWrapper());
		PROP_MIN_DRIVEMAP_RESCAN_TIME           = new CCPIntProperty(NONVISIBLE,            this,   "PROP_MIN_DRIVEMAP_RESCAN_TIME",            30*1000);
		PROP_DRIVEMAP_REMOUNT_NETDRIVES         = new CCBoolProperty(NONVISIBLE,            this,   "PROP_DRIVEMAP_REMOUNT_NETDRIVES",          false);
		PROP_CHECKDATABASE_OPTIONS              = new CCStringSetProperty(NONVISIBLE,       this,   "PROP_CHECKDATABASE_OPTIONS",               new String[0]);
		PROP_VLC_ROBOT_ENABLED                  = new CCBoolProperty(NONVISIBLE,            this,   "PROP_VLC_ROBOT_ENABLED",                   true);
		PROP_VLC_ROBOT_PORT                     = new CCRIntProperty(NONVISIBLE,            this,   "PROP_VLC_ROBOT_PORT",                      18642,           1024,        65535);
		PROP_VLC_ROBOT_PASSWORD                 = new CCStringProperty(NONVISIBLE,          this,   "PROP_VLC_ROBOT_PASSWORD",                  getRandPass(8));
		PROP_VLC_ROBOT_KEEP_POSITION            = new CCBoolProperty(NONVISIBLE,            this,   "PROP_VLC_ROBOT_KEEP_POSITION",             false);
		PROP_VLC_ROBOT_FREQUENCY                = new CCEnumProperty<>(NONVISIBLE,          this,   "PROP_VLC_ROBOT_FREQUENCY",                 VLCRobotFrequency.MS_0500, VLCRobotFrequency.getWrapper());
		PROP_VLC_ROBOT_QUEUE_PREEMPTIVE         = new CCBoolProperty(NONVISIBLE,            this,   "PROP_VLC_ROBOT_QUEUE_PREEMPTIVE",          true);
		PROP_FOLDERLANG_IGNORE_PERC             = new CCPIntProperty(NONVISIBLE,            this,   "PROP_FOLDERLANG_IGNORE_PERC",              2);
		PROP_MAX_UNDOLASTWATCH_HOUR_DIFF        = new CCPIntProperty(NONVISIBLE,            this,   "PROP_MAX_UNDOLASTWATCH_HOUR_DIFF",         3);
		PROP_MAX_FASTREWATCH_HOUR_DIFF          = new CCPIntProperty(NONVISIBLE,            this,   "PROP_MAX_FASTREWATCH_HOUR_DIFF",           3);
		PROP_MAINFRAME_COLUMN_SIZE_CACHE        = new CCStringProperty(NONVISIBLE,          this,   "PROP_MAINFRAME_COLUMN_SIZE_CACHE",         Str.Empty);
		PROP_MAINFRAME_FILTERTREE_RECOLLAPSE    = new CCBoolProperty(NONVISIBLE,            this,   "PROP_MAINFRAME_FILTERTREE_RECOLLAPSE",     false);
		PROP_PLAY_FAILONMISSINGFILES            = new CCBoolProperty(NONVISIBLE,            this,   "PROP_PLAY_FAILONMISSINGFILES",             true);
		PROP_RESET_SORT_ON_FILTERCLEAR          = new CCBoolProperty(NONVISIBLE,            this,   "PROP_RESET_SORT_ON_FILTERCLEAR",           true);
		PROP_MAINTABLE_INSTANTTOOLTIPS          = new CCBoolProperty(NONVISIBLE,            this,   "PROP_MAINTABLE_INSTANTTOOLTIPS",           true);
		PROP_MAINTABLE_INFINITETOOLTIPS         = new CCBoolProperty(NONVISIBLE,            this,   "PROP_MAINTABLE_INFINITETOOLTIPS",          true);
		PROP_SERIESTABLE_INSTANTTOOLTIPS        = new CCBoolProperty(NONVISIBLE,            this,   "PROP_SERIESTABLE_INSTANTTOOLTIPS",         true);
		PROP_SERIESTABLE_INFINITETOOLTIPS       = new CCBoolProperty(NONVISIBLE,            this,   "PROP_SERIESTABLE_INFINITETOOLTIPS",        true);
		PROP_PREVSERIES_SMALLERCOVER_FACTOR     = new CCDoubleProperty(NONVISIBLE,          this,   "PROP_PREVSERIES_SMALLERCOVER_FACTOR",      0.95);
		PROP_PREVIEWSERIESFRAME_WIDTH           = new CCPIntProperty(NONVISIBLE,            this,   "PROP_PREVIEWSERIESFRAME_WIDTH",            1300);

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
		PROP_PATHSYNTAX_VAR6                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR6",                     PathSyntaxVar.EMPTY);
		PROP_PATHSYNTAX_VAR7                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR7",                     PathSyntaxVar.EMPTY);
		PROP_PATHSYNTAX_VAR8                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR8",                     PathSyntaxVar.EMPTY);
		PROP_PATHSYNTAX_VAR9                    = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR9",                     PathSyntaxVar.EMPTY);
		PROP_PATHSYNTAX_VAR10                   = new CCPathVarProperty(CAT_PATHSYNTAX,     this,   "PROP_PATHSYNTAX_VAR10",                    PathSyntaxVar.EMPTY);
	}

	private FSPath getDefHttpCachePath() {
		return FilesystemUtils.getTempPath().append("jClipCorn").append("httpcache"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getRandPass(int len)
	{
		final String CHARS = "ABCDEFGHKLMNPRSTUVWXYZ_0123456789"; //$NON-NLS-1$
		StringBuilder b = new StringBuilder();
		for (int i =0; i < len; i++) b.append(CHARS.charAt((int)(Math.random() * CHARS.length())));
		return b.toString();
	}

	private Set<MainFrameColumn> getDefColumns() {
		var set = new HashSet<>(Arrays.asList(MainFrameColumn.values()));

		set.remove(MainFrameColumn.GENRES);
		set.remove(MainFrameColumn.LASTVIEWED);
		set.remove(MainFrameColumn.SUBTITLES);

		return set;
	}

	private CCPath getDefFLPath() {
		return CCPath.create("<?self>jClipCorn."+ExportHelper.EXTENSION_FILTERLIST); //$NON-NLS-1$
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
	
	private AppTheme getDefTheme() {
		if (ApplicationHelper.isWindows()) return AppTheme.WINDOWS;
		if (ApplicationHelper.isMac())     return AppTheme.METAL;
		if (ApplicationHelper.isUnix())    return AppTheme.METAL;
		
		return AppTheme.METAL;
	}
	
	private boolean getDefStatbarLength() {
		return getDefTheme() != AppTheme.WINDOWS;
	}
	
	private int getDefMFHeight() {
			return (getDefTheme() == AppTheme.METAL) ? 670 : 660;
	}

	public void load(FSPath path) {
		try {
			synchronized (_fileLock) {
				FileInputStream stream = new FileInputStream(path.toFile());
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
				FileOutputStream stream = new FileOutputStream(path.toFile());
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
		var hostname = ApplicationHelper.getHostname();

		List<PathSyntaxVar> r = new ArrayList<>();

		PathSyntaxVar v1 = PROP_PATHSYNTAX_VAR1.getValue();
		if (!Str.isNullOrWhitespace(v1.Key) && (Str.isNullOrWhitespace(v1.Hostname) || v1.Hostname.equalsIgnoreCase(hostname))) r.add(v1);

		PathSyntaxVar v2 = PROP_PATHSYNTAX_VAR2.getValue();
		if (!Str.isNullOrWhitespace(v2.Key) && (Str.isNullOrWhitespace(v2.Hostname) || v2.Hostname.equalsIgnoreCase(hostname))) r.add(v2);

		PathSyntaxVar v3 = PROP_PATHSYNTAX_VAR3.getValue();
		if (!Str.isNullOrWhitespace(v3.Key) && (Str.isNullOrWhitespace(v3.Hostname) || v3.Hostname.equalsIgnoreCase(hostname))) r.add(v3);

		PathSyntaxVar v4 = PROP_PATHSYNTAX_VAR4.getValue();
		if (!Str.isNullOrWhitespace(v4.Key) && (Str.isNullOrWhitespace(v4.Hostname) || v4.Hostname.equalsIgnoreCase(hostname))) r.add(v4);

		PathSyntaxVar v5 = PROP_PATHSYNTAX_VAR5.getValue();
		if (!Str.isNullOrWhitespace(v5.Key) && (Str.isNullOrWhitespace(v5.Hostname) || v5.Hostname.equalsIgnoreCase(hostname))) r.add(v5);

		PathSyntaxVar v6  = PROP_PATHSYNTAX_VAR6.getValue();
		if (!Str.isNullOrWhitespace(v6.Key) && (Str.isNullOrWhitespace(v6.Hostname) || v6.Hostname.equalsIgnoreCase(hostname))) r.add(v6);

		PathSyntaxVar v7  = PROP_PATHSYNTAX_VAR7.getValue();
		if (!Str.isNullOrWhitespace(v7.Key) && (Str.isNullOrWhitespace(v7.Hostname) || v7.Hostname.equalsIgnoreCase(hostname))) r.add(v7);

		PathSyntaxVar v8  = PROP_PATHSYNTAX_VAR8.getValue();
		if (!Str.isNullOrWhitespace(v8.Key) && (Str.isNullOrWhitespace(v8.Hostname) || v8.Hostname.equalsIgnoreCase(hostname))) r.add(v8);

		PathSyntaxVar v9  = PROP_PATHSYNTAX_VAR9.getValue();
		if (!Str.isNullOrWhitespace(v9.Key) && (Str.isNullOrWhitespace(v9.Hostname) || v9.Hostname.equalsIgnoreCase(hostname))) r.add(v9);

		PathSyntaxVar v10 = PROP_PATHSYNTAX_VAR10.getValue();
		if (!Str.isNullOrWhitespace(v10.Key) && (Str.isNullOrWhitespace(v10.Hostname) || v10.Hostname.equalsIgnoreCase(hostname))) r.add(v10);

		return r;
	}

	private Set<ImageSearchImplementation> getDefImagesearchImpl() {
		HashSet<ImageSearchImplementation> hs = new HashSet<>();
		hs.add(ImageSearchImplementation.TMDP_POSTER);
		hs.add(ImageSearchImplementation.IMDB_COVER);
		hs.add(ImageSearchImplementation.IMDB_SEC_COVER);
		return hs;
	}

	public List<NamedPathVar> getAlternativeMediaPlayers() {
		var r = new ArrayList<NamedPathVar>();
		if (!PROP_PLAY_ALT_PROG_1.getValue().isEmpty()) r.add(PROP_PLAY_ALT_PROG_1.getValue());
		if (!PROP_PLAY_ALT_PROG_2.getValue().isEmpty()) r.add(PROP_PLAY_ALT_PROG_2.getValue());
		if (!PROP_PLAY_ALT_PROG_3.getValue().isEmpty()) r.add(PROP_PLAY_ALT_PROG_3.getValue());
		if (!PROP_PLAY_ALT_PROG_4.getValue().isEmpty()) r.add(PROP_PLAY_ALT_PROG_4.getValue());
		if (!PROP_PLAY_ALT_PROG_5.getValue().isEmpty()) r.add(PROP_PLAY_ALT_PROG_5.getValue());
		return r;
	}

	@Override
	public CCProperties ccprops() {
		return this;
	}
}
