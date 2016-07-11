package de.jClipCorn.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import de.jClipCorn.Main;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.frames.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.localization.util.LocalizedVector;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.property.CCBoolProperty;
import de.jClipCorn.properties.property.CCDateProperty;
import de.jClipCorn.properties.property.CCPIntProperty;
import de.jClipCorn.properties.property.CCPathProperty;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.properties.property.CCRIntProperty;
import de.jClipCorn.properties.property.CCSeasonRegexListProperty;
import de.jClipCorn.properties.property.CCStringProperty;
import de.jClipCorn.properties.property.CCToolbarProperty;
import de.jClipCorn.properties.property.CCVIntProperty;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.LookAndFeelManager;

public class CCProperties {
	private final static String HEADER = "jClipCorn Configuration File"; //$NON-NLS-1$
	
	public final static CCPropertyCategory NONVISIBLE			= new CCPropertyCategory();
	public final static CCPropertyCategory CAT_COMMON 			= new CCPropertyCategory(0, "COMMON");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_VIEW 			= new CCPropertyCategory(1, "VIEW");         //$NON-NLS-1$
	public final static CCPropertyCategory CAT_DATABASE 		= new CCPropertyCategory(2, "DATABASE");     //$NON-NLS-1$
	public final static CCPropertyCategory CAT_MOVIES	 		= new CCPropertyCategory(3, "MOVIES");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_SERIES 			= new CCPropertyCategory(4, "SERIES");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_PLAY 			= new CCPropertyCategory(5, "PLAY");         //$NON-NLS-1$
	public final static CCPropertyCategory CAT_BACKUP 			= new CCPropertyCategory(6, "BACKUP");       //$NON-NLS-1$
	public final static CCPropertyCategory CAT_STATUSBAR		= new CCPropertyCategory(7, "STATUSBAR");    //$NON-NLS-1$
	public final static CCPropertyCategory CAT_OTHERFRAMES 		= new CCPropertyCategory(8, "OTHERFRAMES");  //$NON-NLS-1$
	public final static CCPropertyCategory CAT_KEYSTROKES 		= new CCPropertyCategory(9, "KEYSTROKES");   //$NON-NLS-1$
	
	public final static CCPropertyCategory[] CATEGORIES = {
			CAT_COMMON,
			CAT_VIEW,
			CAT_DATABASE,
			CAT_MOVIES,
			CAT_SERIES,
			CAT_PLAY,
			CAT_BACKUP,
			CAT_STATUSBAR,
			CAT_OTHERFRAMES,
			CAT_KEYSTROKES 	
	};
	
	@SuppressWarnings("nls")
	public final static String[] readOnlyArgs = {"readonly", "-readonly", "--readonly", "read-only", "-read-only", "--read-only", "ro", "-ro", "--ro"};
	
	private static CCProperties mainInstance = null;
	
	private List<CCProperty<Object>> propertylist = new Vector<>();
	
	public CCBoolProperty 				PROP_ADD_MOVIE_RELATIVE_AUTO;
	public CCStringProperty 			PROP_DATABASE_NAME;
	public CCStringProperty 			PROP_LOG_PATH;
	public CCVIntProperty 				PROP_UI_LANG;
	public CCStringProperty 			PROP_SELF_DIRECTORY;
	public CCStringProperty 			PROP_COVER_PREFIX;
	public CCStringProperty 			PROP_COVER_TYPE;
	public CCBoolProperty 				PROP_LOADING_LIVEUPDATE;
	public CCBoolProperty 				PROP_STATUSBAR_CALC_SERIES_IN_LENGTH;
	public CCBoolProperty 				PROP_STATUSBAR_CALC_SERIES_IN_SIZE;
	public CCVIntProperty 				PROP_UI_LOOKANDFEEL;
	public CCStringProperty 			PROP_PLAY_VLC_PATH;
	public CCBoolProperty 				PROP_PLAY_VLC_FULLSCREEN;
	public CCBoolProperty 				PROP_PLAY_VLC_AUTOPLAY;
	public CCBoolProperty 				PROP_PLAY_USESTANDARDONMISSINGVLC; // Use Standard Player on missing VLC
	public CCVIntProperty 				PROP_ON_DBLCLICK_MOVE; //0=Play | 1=Preview
	public CCBoolProperty 				PROP_USE_INTELLISORT;
	public CCBoolProperty 				PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT;
	public CCPIntProperty 				PROP_MAINFRAME_SCROLLSPEED;
	public CCVIntProperty 				PROP_MAINFRAME_TABLEBACKGROUND; //0=WHITE | 1=GRAY-WHITE | 2=Score
	public CCBoolProperty 				PROP_LOADING_PRELOADRESOURCES;
	public CCBoolProperty 				PROP_DATABASE_CREATELOGFILE;
	public CCPIntProperty 				PROP_DATABASE_COVERCACHESIZE;
	public CCBoolProperty 				PROP_COMMON_CHECKFORUPDATES;
	public CCBoolProperty 				PROP_COMMON_PRESCANFILESYSTEM;
	public CCBoolProperty 				PROP_SCANFOLDER_INCLUDESERIES;
	public CCBoolProperty 				PROP_SCANFOLDER_EXCLUDEIFOS;
	public CCDateProperty 				PROP_BACKUP_LASTBACKUP;
	public CCBoolProperty 				PROP_BACKUP_CREATEBACKUPS;
	public CCStringProperty 			PROP_BACKUP_FOLDERNAME;
	public CCPIntProperty 				PROP_BACKUP_BACKUPTIME;
	public CCRIntProperty 				PROP_BACKUP_COMPRESSION;
	public CCBoolProperty 				PROP_BACKUP_AUTODELETEBACKUPS;
	public CCPIntProperty 				PROP_BACKUP_LIFETIME;
	public CCBoolProperty 				PROP_LOG_APPEND;
	public CCPIntProperty 				PROP_LOG_MAX_LINECOUNT;
	public CCVIntProperty 				PROP_VIEW_DB_START_SORT;
	public CCRIntProperty				PROP_VALIDATE_FILESIEDRIFT;
	public CCBoolProperty				PROP_OTHER_DEBUGMODE;
	public CCBoolProperty				PROP_VALIDATE_DUP_IGNORE_IFO;
	public CCBoolProperty				PROP_PREVSERIES_3DCOVER;
	public CCBoolProperty				PROP_PREVSERIES_COVERBORDER;
	public CCBoolProperty				PROP_MASSCHANGESCORE_SKIPRATED;
	public CCBoolProperty				PROP_MASSCHANGESCORE_ONLYVIEWED;
	public CCBoolProperty				PROP_MASSCHANGEVIEWED_ONLYUNVIEWED;
	public CCBoolProperty				PROP_IMPORT_RESETVIEWED;
	public CCBoolProperty				PROP_IMPORT_ONLYWITHCOVER;
	public CCBoolProperty				PROP_IMPORT_RESETADDDATE;
	public CCBoolProperty				PROP_IMPORT_RESETSCORE;
	public CCBoolProperty				PROP_IMPORT_RESETTAGS;
	public CCRIntProperty 				PROP_PARSEIMDB_LANGUAGE;
	public CCToolbarProperty			PROP_TOOLBAR_ELEMENTS;
	public CCVIntProperty				PROP_SERIES_ADDDATECALCULATION; //0 = Lowest EpisdenAddDate || 1 = Highest EpisdenAddDate || 2 = Average EpisdenAddDate
	public CCBoolProperty				PROP_STATBAR_ELCOUNT;
	public CCBoolProperty				PROP_STATBAR_PROGRESSBAR;
	public CCBoolProperty				PROP_STATBAR_LOG;
	public CCBoolProperty				PROP_STATBAR_VIEWEDCOUNT;
	public CCBoolProperty				PROP_STATBAR_SERIESCOUNT;
	public CCBoolProperty				PROP_STATBAR_LENGTH;
	public CCBoolProperty				PROP_STATBAR_SIZE;
	public CCBoolProperty				PROP_STATBAR_STARTTIME;
	public CCBoolProperty				PROP_MAINFRAME_CLICKABLEZYKLUS;
	public CCBoolProperty				PROP_MAINFRAME_CLICKABLESCORE;
	public CCBoolProperty				PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR;
	public CCBoolProperty				PROP_MAINFRAME_AUTOMATICRESETWATCHLATER;
	public CCBoolProperty				PROP_MAINFRAME_AUTOMATICRESETWATCHNEVER;
	public CCBoolProperty				PROP_STATISTICS_INTERACTIVECHARTS;
	public CCBoolProperty 				PROP_DATABASE_CLEANSHUTDOWN;
	public CCBoolProperty 				PROP_MAINFRAME_SHOWTAGS;
	public CCStringProperty 			PROP_MAINFRAME_FILTERLISTPATH;
	public CCBoolProperty				PROP_MAINFRAME_SHOWCOVERCORNER;
	public CCBoolProperty				PROP_VALIDATE_CHECK_SERIES_STRUCTURE;
	public CCBoolProperty				PROP_MAINFRAME_DONT_FILTER_WATCHNEVER;
	public CCBoolProperty				PROP_SHOW_PARTIAL_VIEWED_STATE;
	public CCPIntProperty 				PROP_MAINFRAME_WIDTH;
	public CCPIntProperty 				PROP_MAINFRAME_HEIGHT;
	public CCSeasonRegexListProperty	PROP_SEASON_INDEX_REGEXPRESSIONS;
	public CCPIntProperty 				PROP_STATISTICS_TIMELINEGRAVITY;
	public CCVIntProperty 				PROP_DATABASE_DRIVER;
	public CCBoolProperty				PROP_SERIES_DISPLAYED_DATE; //TODO LastViewed | FirstViewed | AverageViewed
	
	public boolean ARG_READONLY = false;
	
	public boolean firstLaunch = false;
	
	private Properties properties;
	private String path;
	
	private CCProperties() { //Dummy Constructor
		
	}
	
	public CCProperties(String path, String[] args) {
		properties = new Properties();
		this.path = path;
		load(path);
		
		createProperties();
		interpreteArgs(args);
		
		if (Main.DEBUG) {
			System.out.println("[DBG] " + propertylist.size() + " Properties in List intialized"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (firstLaunch) save();
		
		mainInstance = this;

		LocaleBundle.updateLang();
	}
	
	@SuppressWarnings("nls")
	private void createProperties() {
		LocalizedVector vd = new LocalizedVector();
		vd.add("CCProperties.DblClickMove.Opt0"); //$NON-NLS-1$
		vd.add("CCProperties.DblClickMove.Opt1"); //$NON-NLS-1$
		vd.add("CCProperties.DblClickMove.Opt2"); //$NON-NLS-1$
		
		LocalizedVector vl = getUILanguageOptions();

		Vector<String> vlf = new Vector<>(LookAndFeelManager.getLookAndFeelList());
		
		LocalizedVector vb = new LocalizedVector();
		vb.add("CCProperties.TabBackground.Opt0"); //$NON-NLS-1$
		vb.add("CCProperties.TabBackground.Opt1"); //$NON-NLS-1$
		vb.add("CCProperties.TabBackground.Opt2"); //$NON-NLS-1$
		
		LocalizedVector vs = new LocalizedVector();
		vs.add("ClipTableModel.LocalID"); //$NON-NLS-1$
		vs.add("ClipTableModel.Title"); //$NON-NLS-1$
		vs.add("ClipTableModel.Added"); //$NON-NLS-1$
		
		LocalizedVector va = new LocalizedVector();
		va.add("CCProperties.AddDateCalculation.Opt0"); //$NON-NLS-1$
		va.add("CCProperties.AddDateCalculation.Opt1"); //$NON-NLS-1$
		va.add("CCProperties.AddDateCalculation.Opt2"); //$NON-NLS-1$
		
		LocalizedVector vdb = getDatabaseDriverOptions();
		
		PROP_UI_LANG							= new CCVIntProperty(CAT_COMMON, 			this, 	"PROP_UI_LANG", 							getDefLanguage(),	vl);
		PROP_LOADING_PRELOADRESOURCES			= new CCBoolProperty(CAT_COMMON, 			this,   "PROP_LOADING_PRELOADICONS", 				false);
		PROP_USE_INTELLISORT					= new CCBoolProperty(CAT_COMMON,			this, 	"PROP_USE_INTELLISORT", 					false);
		PROP_COMMON_CHECKFORUPDATES				= new CCBoolProperty(CAT_COMMON, 			this, 	"PROP_COMMON_CHECKFORUPDATES", 				true);
		PROP_COMMON_PRESCANFILESYSTEM			= new CCBoolProperty(CAT_COMMON, 			this, 	"PROP_COMMON_PRESCANFILESYSTEM", 			true);
		PROP_LOG_APPEND							= new CCBoolProperty(CAT_COMMON, 			this,   "PROP_LOG_APPEND", 							true);
		PROP_DATABASE_CLEANSHUTDOWN				= new CCBoolProperty(CAT_COMMON,	 		this,   "PROP_DATABASE_CLEANSHUTDOWN",				false);
		PROP_MAINFRAME_FILTERLISTPATH			= new CCStringProperty(CAT_COMMON,	 		this,	"PROP_MAINFRAME_FILTERLISTPATH",			"<?self>jClipCorn."+ExportHelper.EXTENSION_FILTERLIST);
		                                                                                	
		PROP_UI_LOOKANDFEEL						= new CCVIntProperty(CAT_VIEW, 				this,	"PROP_UI_LOOKANDFEEL", 						getDefStyle(),		vlf);
		PROP_MAINFRAME_TABLEBACKGROUND			= new CCVIntProperty(CAT_VIEW, 				this,	"PROP_MAINFRAME_TABLEBACKGROUND",			0, 					vb);
		PROP_LOADING_LIVEUPDATE					= new CCBoolProperty(CAT_VIEW, 				this, 	"PROP_LOADING_LIVEUPDATE", 					false);
		PROP_MAINFRAME_SCROLLSPEED				= new CCPIntProperty(CAT_VIEW, 				this, 	"PROP_MAINFRAME_SCROLLSPEED", 				3);
		PROP_VIEW_DB_START_SORT					= new CCVIntProperty(CAT_VIEW, 				this, 	"PROP_VIEW_DB_START_SORT", 					0,					vs);
		PROP_TOOLBAR_ELEMENTS					= new CCToolbarProperty(CAT_VIEW, 			this, 	"PROP_TOOLBAR_ELEMENTS", 					ClipToolbar.STANDARD_CONFIG);
		PROP_MAINFRAME_CLICKABLEZYKLUS			= new CCBoolProperty(CAT_VIEW,		 		this,   "PROP_MAINFRAME_CLICKABLEZYKLUS",			false);
		PROP_MAINFRAME_CLICKABLESCORE			= new CCBoolProperty(CAT_VIEW,		 		this,   "PROP_MAINFRAME_CLICKABLESCORE",			false);
		PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR	= new CCBoolProperty(CAT_VIEW,		 		this,   "PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR",		false);
		PROP_MAINFRAME_SHOWTAGS					= new CCBoolProperty(CAT_VIEW,	 			this,   "PROP_MAINFRAME_SHOWTAGS",					true);
		PROP_MAINFRAME_SHOWCOVERCORNER			= new CCBoolProperty(CAT_VIEW,		 		this,   "PROP_MAINFRAME_SHOWCOVERCORNER",			true);
		PROP_MAINFRAME_DONT_FILTER_WATCHNEVER	= new CCBoolProperty(CAT_VIEW,				this, 	"PROP_MAINFRAME_DONT_FILTER_WATCHNEVER", 	true);
		
		PROP_DATABASE_NAME 						= new CCStringProperty(CAT_DATABASE, 		this,	"PROP_DATABASE_NAME",						"ClipCornDB");
		PROP_LOG_PATH							= new CCStringProperty(CAT_DATABASE, 		this,	"PROP_LOG_PATH",							"jClipcorn.log");
		PROP_SELF_DIRECTORY						= new CCStringProperty(CAT_DATABASE, 		this,	"PROP_SELF_DIRECTORY",						"");
		PROP_COVER_PREFIX						= new CCStringProperty(CAT_DATABASE, 		this,	"PROP_COVER_PREFIX",						"cover_");
		PROP_COVER_TYPE							= new CCStringProperty(CAT_DATABASE, 		this,	"PROP_COVER_TYPE",							"png");
		PROP_DATABASE_CREATELOGFILE				= new CCBoolProperty(CAT_DATABASE,			this, 	"PROP_DATABASE_CREATELOGFILE", 				true);
		PROP_DATABASE_COVERCACHESIZE			= new CCPIntProperty(CAT_DATABASE, 			this, 	"PROP_DATABASE_COVERCACHESIZE", 			128);
		PROP_LOG_MAX_LINECOUNT 					= new CCPIntProperty(CAT_DATABASE, 			this, 	"PROP_LOG_MAX_LINECOUNT", 					1048576); // 2^20

		PROP_ON_DBLCLICK_MOVE					= new CCVIntProperty(CAT_MOVIES, 			this, 	"PROP_ON_DBLCLICK_MOVE", 					0, 					vd);
		PROP_MAINFRAME_AUTOMATICRESETWATCHLATER = new CCBoolProperty(CAT_MOVIES,			this,   "PROP_MAINFRAME_AUTOMATICRESETWATCHLATER",	true);
		PROP_MAINFRAME_AUTOMATICRESETWATCHNEVER = new CCBoolProperty(CAT_MOVIES,			this,   "PROP_MAINFRAME_AUTOMATICRESETWATCHNEVER",	true);
                                                                                        	
		PROP_STATUSBAR_CALC_SERIES_IN_LENGTH	= new CCBoolProperty(CAT_SERIES, 			this,	"PROP_STATUSBAR_CALC_SERIES_IN_LENGTH",	 	false);
		PROP_STATUSBAR_CALC_SERIES_IN_SIZE		= new CCBoolProperty(CAT_SERIES, 			this,	"PROP_STATUSBAR_CALC_SERIES_IN_SIZE", 		false);
		PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT		= new CCBoolProperty(CAT_SERIES, 			this,	"PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT", 		false);
		PROP_PREVSERIES_3DCOVER					= new CCBoolProperty(CAT_SERIES, 			this,   "PROP_PREVSERIES_3DCOVER",					true);
		PROP_PREVSERIES_COVERBORDER				= new CCBoolProperty(CAT_SERIES, 			this,   "PROP_PREVSERIES_COVERBORDER",				true);
		PROP_SERIES_ADDDATECALCULATION			= new CCVIntProperty(CAT_SERIES, 			this, 	"PROP_SERIES_ADDDATECALCULATION", 			1, 					va);
		PROP_VALIDATE_CHECK_SERIES_STRUCTURE	= new CCBoolProperty(CAT_SERIES,			this,   "PROP_VALIDATE_CHECK_SERIES_STRUCTURE",		false);
		PROP_SHOW_PARTIAL_VIEWED_STATE			= new CCBoolProperty(CAT_SERIES,			this,   "PROP_SHOW_PARTIAL_VIEWED_STATE",			false);
		PROP_SEASON_INDEX_REGEXPRESSIONS		= new CCSeasonRegexListProperty(CAT_SERIES, this, 	"PROP_SEASON_INDEX_REGEXPRESSIONS", 		getDefSeasonRegex());
		
		PROP_PLAY_VLC_PATH						= new CCPathProperty(CAT_PLAY, 				this,	"PROP_PLAY_VLC_PATH",						"", 				PathFormatter.appendAndPrependSeparator("vlc.exe"));
		PROP_PLAY_VLC_FULLSCREEN				= new CCBoolProperty(CAT_PLAY, 				this,   "PROP_PLAY_VLC_FULLSCREEN", 				false);
		PROP_PLAY_VLC_AUTOPLAY					= new CCBoolProperty(CAT_PLAY, 				this,   "PROP_PLAY_VLC_AUTOPLAY", 					true);
		PROP_PLAY_USESTANDARDONMISSINGVLC		= new CCBoolProperty(CAT_PLAY, 				this,   "PROP_PLAY_USESTANDARDONMISSINGVLC", 		true);
		
		PROP_BACKUP_CREATEBACKUPS				= new CCBoolProperty(CAT_BACKUP, 			this, 	"PROP_BACKUP_CREATEBACKUPS", 				false);
		PROP_BACKUP_FOLDERNAME					= new CCStringProperty(CAT_BACKUP,	 		this,	"PROP_BACKUP_FOLDERNAME",					"jClipCorn_backup");
		PROP_BACKUP_BACKUPTIME					= new CCPIntProperty(CAT_BACKUP, 			this, 	"PROP_BACKUP_BACKUPTIME", 					7);
		PROP_BACKUP_COMPRESSION					= new CCRIntProperty(CAT_BACKUP, 			this, 	"PROP_BACKUP_COMPRESSION", 					0,					10);
		PROP_BACKUP_AUTODELETEBACKUPS			= new CCBoolProperty(CAT_BACKUP, 			this,   "PROP_BACKUP_AUTODELETEBACKUPS", 			true);
		PROP_BACKUP_LIFETIME					= new CCPIntProperty(CAT_BACKUP, 			this, 	"PROP_BACKUP_LIFETIME", 					56);
		
		PROP_STATBAR_ELCOUNT					= new CCBoolProperty(CAT_STATUSBAR,	 		this,   "PROP_STATBAR_ELCOUNT",						true);
		PROP_STATBAR_PROGRESSBAR				= new CCBoolProperty(CAT_STATUSBAR,	 		this,   "PROP_STATBAR_PROGRESSBAR",					true);
		PROP_STATBAR_LOG						= new CCBoolProperty(CAT_STATUSBAR,	 		this,   "PROP_STATBAR_LOG",							true);
		PROP_STATBAR_VIEWEDCOUNT				= new CCBoolProperty(CAT_STATUSBAR,	 		this,   "PROP_STATBAR_VIEWEDCOUNT",					true);
		PROP_STATBAR_SERIESCOUNT				= new CCBoolProperty(CAT_STATUSBAR,	 		this,   "PROP_STATBAR_SERIESCOUNT",					true);
		PROP_STATBAR_LENGTH						= new CCBoolProperty(CAT_STATUSBAR,	 		this,   "PROP_STATBAR_LENGTH",						getDefStatbarLength());
		PROP_STATBAR_SIZE						= new CCBoolProperty(CAT_STATUSBAR,	 		this,   "PROP_STATBAR_SIZE",						true);
		PROP_STATBAR_STARTTIME					= new CCBoolProperty(CAT_STATUSBAR,	 		this,   "PROP_STATBAR_STARTTIME",					true);
		
		PROP_ADD_MOVIE_RELATIVE_AUTO 			= new CCBoolProperty(CAT_OTHERFRAMES, 		this,   "PROP_ADD_MOVIE_RELATIVE_AUTO", 			true);
		PROP_SCANFOLDER_INCLUDESERIES			= new CCBoolProperty(CAT_OTHERFRAMES, 		this, 	"PROP_SCANFOLDER_INCLUDESERIES", 			false);
		PROP_SCANFOLDER_EXCLUDEIFOS 			= new CCBoolProperty(CAT_OTHERFRAMES, 		this, 	"PROP_SCANFOLDER_EXCLUDEIFOS", 				false);
		PROP_VALIDATE_FILESIEDRIFT				= new CCRIntProperty(CAT_OTHERFRAMES, 		this, 	"PROP_VALIDATE_FILESIEDRIFT", 				5,					100);
		PROP_VALIDATE_DUP_IGNORE_IFO			= new CCBoolProperty(CAT_OTHERFRAMES, 		this,   "PROP_VALIDATE_DUP_IGNORE_IFO",				true);
		PROP_STATISTICS_TIMELINEGRAVITY         = new CCPIntProperty(CAT_OTHERFRAMES,	 	this,   "PROP_STATISTICS_TIMELINEGRAVITY",			7);
		
		PROP_BACKUP_LASTBACKUP					= new CCDateProperty(NONVISIBLE, 			this, 	"PROP_BACKUP_LASTBACKUP", 					CCDate.getMinimumDate());
		PROP_OTHER_DEBUGMODE					= new CCBoolProperty(NONVISIBLE, 			this,   "PROP_OTHER_DEBUGMODE", 					false);
		PROP_MASSCHANGESCORE_SKIPRATED			= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_MASSCHANGESCORE_SKIPRATED",			false);
		PROP_MASSCHANGESCORE_ONLYVIEWED			= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_MASSCHANGESCORE_ONLYVIEWED",			false);
		PROP_MASSCHANGEVIEWED_ONLYUNVIEWED		= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_MASSCHANGEVIEWED_ONLYUNVIEWED",		false);
		PROP_IMPORT_RESETVIEWED					= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_IMPORT_RESETVIEWED",					true);
		PROP_IMPORT_ONLYWITHCOVER				= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_IMPORT_ONLYWITHCOVER",				true);
		PROP_IMPORT_RESETADDDATE				= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_IMPORT_RESETADDDATE",					true);
		PROP_IMPORT_RESETSCORE					= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_IMPORT_RESETSCORE",					true);
		PROP_IMPORT_RESETTAGS					= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_IMPORT_RESETTAGS",					true);
		PROP_PARSEIMDB_LANGUAGE					= new CCRIntProperty(NONVISIBLE,	 		this,   "PROP_PARSEIMDB_LANGUAGE",					1, 					2);
		PROP_STATISTICS_INTERACTIVECHARTS		= new CCBoolProperty(NONVISIBLE,	 		this,   "PROP_STATISTICS_INTERACTIVECHARTS",		false);
		PROP_MAINFRAME_WIDTH					= new CCPIntProperty(NONVISIBLE,	 		this,   "PROP_MAINFRAME_WIDTH",						875);
		PROP_MAINFRAME_HEIGHT					= new CCPIntProperty(NONVISIBLE,	 		this,   "PROP_MAINFRAME_HEIGHT",					getDefMFHeight());
		PROP_DATABASE_DRIVER					= new CCVIntProperty(NONVISIBLE, 			this, 	"PROP_DATABASE_DRIVER", 					1,					vdb);
	}
	
	public LocalizedVector getUILanguageOptions() {
		LocalizedVector vl = new LocalizedVector();
		vl.add("CCProperties.Language.Opt0"); //$NON-NLS-1$
		vl.add("CCProperties.Language.Opt1"); //$NON-NLS-1$
		vl.add("CCProperties.Language.Opt2"); //$NON-NLS-1$
		vl.add("CCProperties.Language.Opt3"); //$NON-NLS-1$
		return vl;
	}
	
	public LocalizedVector getDatabaseDriverOptions() {
		LocalizedVector vdb = new LocalizedVector();
		vdb.add("CCProperties.DatabaseDriver.Opt0"); //$NON-NLS-1$
		vdb.add("CCProperties.DatabaseDriver.Opt1"); //$NON-NLS-1$
		return vdb;
	}
	
	private ArrayList<String> getDefSeasonRegex() {
		ArrayList<String> result = new ArrayList<>();
		result.add("Staffel[ ]?(?<index>[0-9]+).*"); //$NON-NLS-1$
		result.add("Season[ ]?(?<index>[0-9]+).*"); //$NON-NLS-1$
		result.add("Volume[ ]?(?<index>[0-9]+).*"); //$NON-NLS-1$
		result.add(""); //$NON-NLS-1$
		result.add("(?<index>[0-9]+) - .*"); //$NON-NLS-1$
		return result;
	}

	private int getDefLanguage() {
		if (Locale.getDefault().getCountry().equals(Locale.GERMANY.getCountry())) return LocaleBundle.LOCALE_DUAL;
		if (Locale.getDefault().getCountry().equals(Locale.US.getCountry())) return LocaleBundle.LOCALE_ENGLISCH;
		if (Locale.getDefault().getLanguage().equals(Locale.ENGLISH.getLanguage())) return LocaleBundle.LOCALE_ENGLISCH;
		
		return LocaleBundle.LOCALE_SYSTEMDEFAULT;
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
			return 650;
		else
			return 640;
	}

	public static CCProperties getInstance() {
		if (Main.DEBUG && mainInstance == null) { //ONLY FOR WindowBuilder
			return new CCProperties();
		}
		return mainInstance;
	}
	
	public void load(String path) {
		try {
			FileInputStream stream = new FileInputStream(path);
			properties.load(stream);
			stream.close();
		} catch (IOException e) {
			firstLaunch = true;
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFileNotFound", path)); //$NON-NLS-1$
		}
	}
	
	public void save() {
		try {
			FileOutputStream stream = new FileOutputStream(path);
			properties.store(stream, HEADER);
			stream.close();
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
	
	public CCProperty<?> findProperty(String ident) {
		for (CCProperty<?> prop : propertylist) {
			if (prop.getIdentifier().equals(ident)) {
				return prop;
			}
		}
		
		return null;
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

	private void interpreteArgs(String args[]) {
		for (int i = 0; i < args.length; i++) {
			for (int j = 0; j < readOnlyArgs.length; j++) {
				if (args[i].equalsIgnoreCase(readOnlyArgs[j])) {
					ARG_READONLY = true;
					if (Main.DEBUG) {
						System.out.println("[DBG] ReadOnly Mode activated (" + args[i] + ")"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
	}
}
