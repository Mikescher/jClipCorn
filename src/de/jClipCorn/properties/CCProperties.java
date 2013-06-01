package de.jClipCorn.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import de.jClipCorn.Main;
import de.jClipCorn.gui.frames.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.localization.util.LocalizedVector;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.property.CCBoolProperty;
import de.jClipCorn.properties.property.CCDateProperty;
import de.jClipCorn.properties.property.CCPathProperty;
import de.jClipCorn.properties.property.CCPintProperty;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.properties.property.CCRIntProperty;
import de.jClipCorn.properties.property.CCStringProperty;
import de.jClipCorn.properties.property.CCToolbarProperty;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.LookAndFeelManager;


public class CCProperties {
	private final static String HEADER = "jClipCorn Configuration File"; //$NON-NLS-1$
	
	public final static int NONVISIBLE			= -1;
	public final static int CAT_COMMON 			= 0;
	public final static int CAT_VIEW 			= 1;
	public final static int CAT_DATABASE 		= 2;
	public final static int CAT_SERIES 			= 3;
	public final static int CAT_PLAY 			= 4;
	public final static int CAT_BACKUP 			= 5;
	public final static int CAT_STATUSBAR		= 6;
	public final static int CAT_OTHERFRAMES 	= 7;
	public final static int CAT_KEYSTROKES 		= 8;
	
	private static CCProperties mainInstance = null;
	
	private List<CCProperty<Object>> propertylist = new Vector<>();
	
	public CCBoolProperty 		PROP_ADD_MOVIE_RELATIVE_AUTO;
	public CCStringProperty 	PROP_DATABASE_NAME;
	public CCStringProperty 	PROP_LOG_PATH;
	public CCRIntProperty 		PROP_UI_LANG;
	public CCStringProperty 	PROP_SELF_DIRECTORY;
	public CCStringProperty 	PROP_COVER_PREFIX;
	public CCStringProperty 	PROP_COVER_TYPE;
	public CCBoolProperty 		PROP_LOADING_LIVEUPDATE;
	public CCBoolProperty 		PROP_STATUSBAR_CALC_SERIES_IN_LENGTH;
	public CCBoolProperty 		PROP_STATUSBAR_CALC_SERIES_IN_SIZE;
	public CCRIntProperty 		PROP_UI_LOOKANDFEEL;
	public CCStringProperty 	PROP_PLAY_VLC_PATH;
	public CCBoolProperty 		PROP_PLAY_VLC_FULLSCREEN;
	public CCBoolProperty 		PROP_PLAY_VLC_AUTOPLAY;
	public CCBoolProperty 		PROP_PLAY_USESTANDARDONMISSINGVLC; // Use Standard Player on missing VLC
	public CCRIntProperty 		PROP_ON_DBLCLICK_MOVE; //0=Play | 1=Preview
	public CCBoolProperty 		PROP_USE_INTELLISORT;
	public CCBoolProperty 		PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT;
	public CCPintProperty 		PROP_MAINFRAME_SCROLLSPEED;
	public CCRIntProperty 		PROP_MAINFRAME_TABLEBACKGROUND; //0=WHITE | 1=GRAY-WHITE | 2=Score
	public CCBoolProperty 		PROP_LOADING_PRELOADRESOURCES;
	public CCBoolProperty 		PROP_DATABASE_CREATELOGFILE;
	public CCPintProperty 		PROP_DATABASE_COVERCACHESIZE;
	public CCBoolProperty 		PROP_COMMON_CHECKFORUPDATES;
	public CCBoolProperty 		PROP_COMMON_PRESCANFILESYSTEM;
	public CCBoolProperty 		PROP_SCANFOLDER_INCLUDESERIES;
	public CCBoolProperty 		PROP_SCANFOLDER_EXCLUDEIFOS;
	public CCDateProperty 		PROP_BACKUP_LASTBACKUP;
	public CCBoolProperty 		PROP_BACKUP_CREATEBACKUPS;
	public CCStringProperty 	PROP_BACKUP_FOLDERNAME;
	public CCPintProperty 		PROP_BACKUP_BACKUPTIME;
	public CCRIntProperty 		PROP_BACKUP_COMPRESSION;
	public CCBoolProperty 		PROP_BACKUP_AUTODELETEBACKUPS;
	public CCPintProperty 		PROP_BACKUP_LIFETIME;
	public CCBoolProperty 		PROP_LOG_APPEND;
	public CCPintProperty 		PROP_LOG_MAX_LINECOUNT;
	public CCRIntProperty 		PROP_VIEW_DB_START_SORT;
	public CCRIntProperty		PROP_VALIDATE_FILESIEDRIFT;
	public CCBoolProperty		PROP_OTHER_DEBUGMODE;
	public CCBoolProperty		PROP_VALIDATE_DUP_IGNORE_IFO;
	public CCBoolProperty		PROP_PREVSERIES_3DCOVER;
	public CCBoolProperty		PROP_PREVSERIES_COVERBORDER;
	public CCBoolProperty		PROP_MASSCHANGESCORE_SKIPRATED;
	public CCBoolProperty		PROP_MASSCHANGESCORE_ONLYVIEWED;
	public CCBoolProperty		PROP_MASSCHANGEVIEWED_ONLYUNVIEWED;
	public CCBoolProperty		PROP_IMPORT_RESETVIEWED;
	public CCBoolProperty		PROP_IMPORT_ONLYWITHCOVER;
	public CCBoolProperty		PROP_IMPORT_RESETADDDATE;
	public CCRIntProperty 		PROP_PARSEIMDB_LANGUAGE;
	public CCToolbarProperty	PROP_TOOLBAR_ELEMENTS;
	public CCRIntProperty		PROP_SERIES_ADDDATECALCULATION; //0 = Lowest EpisdenAddDate || 1 = Highest EpisdenAddDate || 2 = Average EpisdenAddDate
	public CCBoolProperty		PROP_STATBAR_ELCOUNT;
	public CCBoolProperty		PROP_STATBAR_PROGRESSBAR;
	public CCBoolProperty		PROP_STATBAR_LOG;
	public CCBoolProperty		PROP_STATBAR_VIEWEDCOUNT;
	public CCBoolProperty		PROP_STATBAR_SERIESCOUNT;
	public CCBoolProperty		PROP_STATBAR_LENGTH;
	public CCBoolProperty		PROP_STATBAR_SIZE;
	public CCBoolProperty		PROP_STATBAR_STARTTIME;
	public CCBoolProperty		PROP_MAINFRAME_CLICKABLEZYKLUS;
	public CCBoolProperty		PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR;
	public CCBoolProperty		PROP_STATISTICS_INTERACTIVECHARTS;
	
	private Properties properties;
	private String path;
	
	public CCProperties(String path) {
		properties = new Properties();
		this.path = path;
		load(path);
		
		createProperties();
		
		if (Main.DEBUG) {
			System.out.println("[DBG] " + propertylist.size() + " Properties in List intialized"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		mainInstance = this;

		LocaleBundle.updateLang();
	}
	
	@SuppressWarnings("nls")
	private void createProperties() {
		LocalizedVector vd = new LocalizedVector();
		vd.add("CCProperties.DblClickMove.Opt0"); //$NON-NLS-1$
		vd.add("CCProperties.DblClickMove.Opt1"); //$NON-NLS-1$
		vd.add("CCProperties.DblClickMove.Opt2"); //$NON-NLS-1$
		
		LocalizedVector vl = new LocalizedVector();
		vl.add("CCProperties.Language.Opt0"); //$NON-NLS-1$
		vl.add("CCProperties.Language.Opt1"); //$NON-NLS-1$
		vl.add("CCProperties.Language.Opt2"); //$NON-NLS-1$
		vl.add("CCProperties.Language.Opt3"); //$NON-NLS-1$

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
		
		PROP_ADD_MOVIE_RELATIVE_AUTO 			= new CCBoolProperty(CAT_OTHERFRAMES, 	this,   "PROP_ADD_MOVIE_RELATIVE_AUTO", 			true);
		PROP_DATABASE_NAME 						= new CCStringProperty(CAT_DATABASE, 	this,	"PROP_DATABASE_NAME",						"ClipCornDB");
		PROP_LOG_PATH							= new CCStringProperty(CAT_DATABASE, 	this,	"PROP_LOG_PATH",							"jClipcorn.log");
		PROP_UI_LANG							= new CCRIntProperty(CAT_COMMON, 		this, 	"PROP_UI_LANG", 							1, 					vl);
		PROP_ON_DBLCLICK_MOVE					= new CCRIntProperty(CAT_COMMON, 		this, 	"PROP_ON_DBLCLICK_MOVE", 					0, 					vd);
		PROP_UI_LOOKANDFEEL						= new CCRIntProperty(CAT_VIEW, 			this,	"PROP_UI_LOOKANDFEEL", 						0, 					vlf);
		PROP_MAINFRAME_TABLEBACKGROUND			= new CCRIntProperty(CAT_VIEW, 			this,	"PROP_MAINFRAME_TABLEBACKGROUND",			0, 					vb);
		PROP_SELF_DIRECTORY						= new CCStringProperty(CAT_DATABASE, 	this,	"PROP_SELF_DIRECTORY",						"");
		PROP_COVER_PREFIX						= new CCStringProperty(CAT_DATABASE, 	this,	"PROP_COVER_PREFIX",						"cover_");
		PROP_COVER_TYPE							= new CCStringProperty(CAT_DATABASE, 	this,	"PROP_COVER_TYPE",							"png");
		PROP_LOADING_LIVEUPDATE					= new CCBoolProperty(CAT_VIEW, 			this, 	"PROP_LOADING_LIVEUPDATE", 					true);
		PROP_STATUSBAR_CALC_SERIES_IN_LENGTH	= new CCBoolProperty(CAT_SERIES, 		this,	"PROP_STATUSBAR_CALC_SERIES_IN_LENGTH",	 	false);
		PROP_STATUSBAR_CALC_SERIES_IN_SIZE		= new CCBoolProperty(CAT_SERIES, 		this,	"PROP_STATUSBAR_CALC_SERIES_IN_SIZE", 		false);
		PROP_PLAY_VLC_PATH						= new CCPathProperty(CAT_PLAY, 			this,	"PROP_PLAY_VLC_PATH",						"", 				"\\vlc.exe");
		PROP_PLAY_VLC_FULLSCREEN				= new CCBoolProperty(CAT_PLAY, 			this,   "PROP_PLAY_VLC_FULLSCREEN", 				false);
		PROP_PLAY_VLC_AUTOPLAY					= new CCBoolProperty(CAT_PLAY, 			this,   "PROP_PLAY_VLC_AUTOPLAY", 					true);
		PROP_PLAY_USESTANDARDONMISSINGVLC		= new CCBoolProperty(CAT_PLAY, 			this,   "PROP_PLAY_USESTANDARDONMISSINGVLC", 		true);
		PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT		= new CCBoolProperty(CAT_SERIES, 		this,	"PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT", 		false);
		PROP_MAINFRAME_SCROLLSPEED				= new CCPintProperty(CAT_VIEW, 			this, 	"PROP_MAINFRAME_SCROLLSPEED", 				3);
		PROP_LOADING_PRELOADRESOURCES			= new CCBoolProperty(CAT_COMMON, 		this,   "PROP_LOADING_PRELOADICONS", 				false);
		PROP_DATABASE_CREATELOGFILE				= new CCBoolProperty(CAT_DATABASE,		this, 	"PROP_DATABASE_CREATELOGFILE", 				true);
		PROP_USE_INTELLISORT					= new CCBoolProperty(CAT_COMMON,		this, 	"PROP_USE_INTELLISORT", 					false);
		PROP_DATABASE_COVERCACHESIZE			= new CCPintProperty(CAT_DATABASE, 		this, 	"PROP_DATABASE_COVERCACHESIZE", 			128);
		PROP_COMMON_CHECKFORUPDATES				= new CCBoolProperty(CAT_COMMON, 		this, 	"PROP_COMMON_CHECKFORUPDATES", 				true);
		PROP_COMMON_PRESCANFILESYSTEM			= new CCBoolProperty(CAT_COMMON, 		this, 	"PROP_COMMON_PRESCANFILESYSTEM", 			true);
		PROP_SCANFOLDER_INCLUDESERIES			= new CCBoolProperty(CAT_OTHERFRAMES, 	this, 	"PROP_SCANFOLDER_INCLUDESERIES", 			false);
		PROP_SCANFOLDER_EXCLUDEIFOS 			= new CCBoolProperty(CAT_OTHERFRAMES, 	this, 	"PROP_SCANFOLDER_EXCLUDEIFOS", 				false);
		PROP_BACKUP_LASTBACKUP					= new CCDateProperty(NONVISIBLE, 		this, 	"PROP_BACKUP_LASTBACKUP", 					CCDate.getNewMinimumDate());
		PROP_BACKUP_CREATEBACKUPS				= new CCBoolProperty(CAT_BACKUP, 		this, 	"PROP_BACKUP_CREATEBACKUPS", 				true);
		PROP_BACKUP_FOLDERNAME					= new CCStringProperty(CAT_BACKUP,	 	this,	"PROP_BACKUP_FOLDERNAME",					"jClipCorn_backup");
		PROP_BACKUP_BACKUPTIME					= new CCPintProperty(CAT_BACKUP, 		this, 	"PROP_BACKUP_BACKUPTIME", 					7);
		PROP_BACKUP_COMPRESSION					= new CCRIntProperty(CAT_BACKUP, 		this, 	"PROP_BACKUP_COMPRESSION", 					0,					10);
		PROP_BACKUP_AUTODELETEBACKUPS			= new CCBoolProperty(CAT_BACKUP, 		this,   "PROP_BACKUP_AUTODELETEBACKUPS", 			true);
		PROP_BACKUP_LIFETIME					= new CCPintProperty(CAT_BACKUP, 		this, 	"PROP_BACKUP_LIFETIME", 					56);
		PROP_LOG_APPEND							= new CCBoolProperty(CAT_COMMON, 		this,   "PROP_LOG_APPEND", 							true);
		PROP_LOG_MAX_LINECOUNT 					= new CCPintProperty(CAT_DATABASE, 		this, 	"PROP_LOG_MAX_LINECOUNT", 					1048576); // 2^20
		PROP_VIEW_DB_START_SORT					= new CCRIntProperty(CAT_VIEW, 			this, 	"PROP_VIEW_DB_START_SORT", 					0,					vs);
		PROP_OTHER_DEBUGMODE					= new CCBoolProperty(NONVISIBLE, 		this,   "PROP_OTHER_DEBUGMODE", 					false);
		PROP_VALIDATE_FILESIEDRIFT				= new CCRIntProperty(CAT_OTHERFRAMES, 	this, 	"PROP_VALIDATE_FILESIEDRIFT", 				5,					100);
		PROP_VALIDATE_DUP_IGNORE_IFO			= new CCBoolProperty(CAT_OTHERFRAMES, 	this,   "PROP_VALIDATE_DUP_IGNORE_IFO",				true);
		PROP_PREVSERIES_3DCOVER					= new CCBoolProperty(CAT_SERIES, 		this,   "PROP_PREVSERIES_3DCOVER",					true);
		PROP_PREVSERIES_COVERBORDER				= new CCBoolProperty(CAT_SERIES, 		this,   "PROP_PREVSERIES_COVERBORDER",				true);
		PROP_MASSCHANGESCORE_SKIPRATED			= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_MASSCHANGESCORE_SKIPRATED",			false);
		PROP_MASSCHANGESCORE_ONLYVIEWED			= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_MASSCHANGESCORE_ONLYVIEWED",			false);
		PROP_MASSCHANGEVIEWED_ONLYUNVIEWED		= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_MASSCHANGEVIEWED_ONLYUNVIEWED",		false);
		PROP_IMPORT_RESETVIEWED					= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_IMPORT_RESETVIEWED",					true);
		PROP_IMPORT_ONLYWITHCOVER				= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_IMPORT_ONLYWITHCOVER",				true);
		PROP_IMPORT_RESETADDDATE				= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_IMPORT_RESETADDDATE",					true);
		PROP_PARSEIMDB_LANGUAGE					= new CCRIntProperty(NONVISIBLE,	 	this,   "PROP_PARSEIMDB_LANGUAGE",					1, 					2);
		PROP_TOOLBAR_ELEMENTS					= new CCToolbarProperty(CAT_VIEW, 		this, 	"PROP_TOOLBAR_ELEMENTS", 					ClipToolbar.STANDARD_CONFIG);
		PROP_SERIES_ADDDATECALCULATION			= new CCRIntProperty(CAT_SERIES, 		this, 	"PROP_SERIES_ADDDATECALCULATION", 			1, 					va);
		PROP_STATBAR_ELCOUNT					= new CCBoolProperty(CAT_STATUSBAR,	 	this,   "PROP_STATBAR_ELCOUNT",						true);
		PROP_STATBAR_PROGRESSBAR				= new CCBoolProperty(CAT_STATUSBAR,	 	this,   "PROP_STATBAR_PROGRESSBAR",					true);
		PROP_STATBAR_LOG						= new CCBoolProperty(CAT_STATUSBAR,	 	this,   "PROP_STATBAR_LOG",							true);
		PROP_STATBAR_VIEWEDCOUNT				= new CCBoolProperty(CAT_STATUSBAR,	 	this,   "PROP_STATBAR_VIEWEDCOUNT",					true);
		PROP_STATBAR_SERIESCOUNT				= new CCBoolProperty(CAT_STATUSBAR,	 	this,   "PROP_STATBAR_SERIESCOUNT",					true);
		PROP_STATBAR_LENGTH						= new CCBoolProperty(CAT_STATUSBAR,	 	this,   "PROP_STATBAR_LENGTH",						true);
		PROP_STATBAR_SIZE						= new CCBoolProperty(CAT_STATUSBAR,	 	this,   "PROP_STATBAR_SIZE",						true);
		PROP_STATBAR_STARTTIME					= new CCBoolProperty(CAT_STATUSBAR,	 	this,   "PROP_STATBAR_STARTTIME",					true);
		PROP_MAINFRAME_CLICKABLEZYKLUS			= new CCBoolProperty(CAT_VIEW,		 	this,   "PROP_MAINFRAME_CLICKABLEZYKLUS",			false);
		PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR	= new CCBoolProperty(CAT_VIEW,		 	this,   "PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR",		false);
		PROP_STATISTICS_INTERACTIVECHARTS		= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_STATISTICS_INTERACTIVECHARTS",		false);
	}
	
	public static CCProperties getInstance() {
		return mainInstance;
	}
	
	public void load(String path) {
		try {
			properties.load( new FileInputStream( path ) );
		} catch (IOException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFileNotFound", path)); //$NON-NLS-1$
		}
	}
	
	public void save() {
		try {
			properties.store(new FileOutputStream(path), HEADER);
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
	
	public int getCategoryCount(int cat) {
		int c = 0;
		
		for (CCProperty<?> p : propertylist) {
			if (p.getCategory() == cat) {
				c++;
			}
		}
		
		return c;
	}
	
	public int getHighestCategory() {
		int cat = -1;
		
		for (CCProperty<?> p : propertylist) {
			if (p.getCategory() > cat) {
				cat = p.getCategory();
			}
		}
		
		return cat;
	}
}
