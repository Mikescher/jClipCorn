package de.jClipCorn.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import de.jClipCorn.Main;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.localization.util.LocalizedVector;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.exceptions.BooleanFormatException;
import de.jClipCorn.properties.exceptions.CCDateFormatException;
import de.jClipCorn.properties.exceptions.PropertyNotFoundException;
import de.jClipCorn.properties.property.CCBoolProperty;
import de.jClipCorn.properties.property.CCDateProperty;
import de.jClipCorn.properties.property.CCPathProperty;
import de.jClipCorn.properties.property.CCPintProperty;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.properties.property.CCRIntProperty;
import de.jClipCorn.properties.property.CCStringProperty;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.LookAndFeelManager;


public class CCProperties {
	private final static String HEADER = "jClipCorn Configuration File"; //$NON-NLS-1$
	
	public final static String TYPE_BOOL_TRUE = "true"; //$NON-NLS-1$
	public final static String TYPE_BOOL_FALSE = "false"; //$NON-NLS-1$
	
	private final static int NONVISIBLE			= -1;
	private final static int CAT_COMMON 		= 0;
	private final static int CAT_VIEW 			= 1;
	private final static int CAT_DATABASE 		= 2;
	private final static int CAT_SERIES 		= 3;
	private final static int CAT_PLAY 			= 4;
	private final static int CAT_BACKUP 		= 5;
	private final static int CAT_OTHERFRAMES 	= 6;
	
	private static CCProperties mainInstance = null;
	
	private Vector<CCProperty<Object>> propertylist = new Vector<>();
	
	public CCBoolProperty 	PROP_ADD_MOVIE_RELATIVE_AUTO;
	public CCStringProperty PROP_DATABASE_NAME;
	public CCStringProperty PROP_LOG_PATH;
	public CCRIntProperty 	PROP_UI_LANG;
	public CCStringProperty PROP_SELF_DIRECTORY;
	public CCStringProperty PROP_COVER_PREFIX;
	public CCStringProperty PROP_COVER_TYPE;
	public CCBoolProperty 	PROP_LOADING_LIVEUPDATE;
	public CCBoolProperty 	PROP_STATUSBAR_CALC_SERIES_IN_LENGTH;
	public CCBoolProperty 	PROP_STATUSBAR_CALC_SERIES_IN_SIZE;
	public CCRIntProperty 	PROP_UI_LOOKANDFEEL;
	public CCStringProperty PROP_PLAY_VLC_PATH;
	public CCBoolProperty 	PROP_PLAY_VLC_FULLSCREEN;
	public CCBoolProperty 	PROP_PLAY_VLC_AUTOPLAY;
	public CCBoolProperty 	PROP_PLAY_USESTANDARDONMISSINGVLC; // Use Standard Player on missing VLC
	public CCRIntProperty 	PROP_ON_DBLCLICK_MOVE; //0=Play | 1=Preview
	public CCBoolProperty 	PROP_USE_INTELLISORT;
	public CCBoolProperty 	PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT;
	public CCPintProperty 	PROP_MAINFRAME_SCROLLSPEED;
	public CCRIntProperty 	PROP_MAINFRAME_TABLEBACKGROUND; //0=WHITE | 1=GRAY-WHITE | 2=Score
	public CCBoolProperty 	PROP_LOADING_PRELOADRESOURCES;
	public CCBoolProperty 	PROP_DATABASE_CREATELOGFILE;
	public CCPintProperty 	PROP_DATABASE_COVERCACHESIZE;
	public CCBoolProperty 	PROP_COMMON_CHECKFORUPDATES;
	public CCBoolProperty 	PROP_COMMON_PRESCANFILESYSTEM;
	public CCBoolProperty 	PROP_SCANFOLDER_INCLUDESERIES;
	public CCBoolProperty 	PROP_SCANFOLDER_EXCLUDEIFOS;
	public CCDateProperty 	PROP_BACKUP_LASTBACKUP;
	public CCBoolProperty 	PROP_BACKUP_CREATEBACKUPS;
	public CCStringProperty PROP_BACKUP_FOLDERNAME;
	public CCPintProperty 	PROP_BACKUP_BACKUPTIME;
	public CCRIntProperty 	PROP_BACKUP_COMPRESSION;
	public CCBoolProperty 	PROP_BACKUP_AUTODELETEBACKUPS;
	public CCPintProperty 	PROP_BACKUP_LIFETIME;
	public CCBoolProperty 	PROP_LOG_APPEND;
	public CCPintProperty 	PROP_LOG_MAX_LINECOUNT;
	public CCRIntProperty 	PROP_VIEW_DB_START_SORT;
	public CCRIntProperty	PROP_VALIDATE_FILESIEDRIFT;
	public CCBoolProperty	PROP_OTHER_DEBUGMODE;
	public CCBoolProperty	PROP_VALIDATE_DUP_IGNORE_IFO;
	public CCBoolProperty	PROP_PREVSERIES_3DCOVER;
	public CCBoolProperty	PROP_PREVSERIES_COVERBORDER;
	public CCBoolProperty	PROP_MASSCHANGESCORE_SKIPRATED;
	public CCBoolProperty	PROP_MASSCHANGESCORE_ONLYVIEWED;
	public CCBoolProperty	PROP_MASSCHANGEVIEWED_ONLYUNVIEWED;
	
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

		Vector<String> vlf = LookAndFeelManager.getLookAndFeelList();
		
		LocalizedVector vb = new LocalizedVector();
		vb.add("CCProperties.TabBackground.Opt0"); //$NON-NLS-1$
		vb.add("CCProperties.TabBackground.Opt1"); //$NON-NLS-1$
		vb.add("CCProperties.TabBackground.Opt2"); //$NON-NLS-1$
		
		LocalizedVector vs = new LocalizedVector();
		vs.add("ClipTableModel.LocalID"); //$NON-NLS-1$
		vs.add("ClipTableModel.Title"); //$NON-NLS-1$
		vs.add("ClipTableModel.Added"); //$NON-NLS-1$
		
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
		PROP_LOG_MAX_LINECOUNT 					= new CCPintProperty(CAT_COMMON, 		this, 	"PROP_LOG_MAX_LINECOUNT", 					1048576); // 2^20
		PROP_VIEW_DB_START_SORT					= new CCRIntProperty(CAT_VIEW, 			this, 	"PROP_VIEW_DB_START_SORT", 					0,					vs);
		PROP_OTHER_DEBUGMODE					= new CCBoolProperty(NONVISIBLE, 		this,   "PROP_OTHER_DEBUGMODE", 					false);
		PROP_VALIDATE_FILESIEDRIFT				= new CCRIntProperty(CAT_OTHERFRAMES, 	this, 	"PROP_VALIDATE_FILESIEDRIFT", 				5,					100);
		PROP_VALIDATE_DUP_IGNORE_IFO			= new CCBoolProperty(CAT_OTHERFRAMES, 	this,   "PROP_VALIDATE_DUP_IGNORE_IFO",				true);
		PROP_PREVSERIES_3DCOVER					= new CCBoolProperty(CAT_OTHERFRAMES, 	this,   "PROP_PREVSERIES_3DCOVER",					true);
		PROP_PREVSERIES_COVERBORDER				= new CCBoolProperty(CAT_OTHERFRAMES, 	this,   "PROP_PREVSERIES_COVERBORDER",				true);
		PROP_MASSCHANGESCORE_SKIPRATED			= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_MASSCHANGESCORE_SKIPRATED",			false);
		PROP_MASSCHANGESCORE_ONLYVIEWED			= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_MASSCHANGESCORE_ONLYVIEWED",			false);
		PROP_MASSCHANGEVIEWED_ONLYUNVIEWED		= new CCBoolProperty(NONVISIBLE,	 	this,   "PROP_MASSCHANGEVIEWED_ONLYUNVIEWED",		false);
	}
	
	public static CCProperties getInstance() {
		return mainInstance;
	}
	
	private static String boolToString(boolean b) {
		return (b) ? (TYPE_BOOL_TRUE) : (TYPE_BOOL_FALSE);
	}
	
	private static boolean stringToBool(String b) throws BooleanFormatException{
		if (TYPE_BOOL_TRUE.equals(b)) {
			return true;
		} else if (TYPE_BOOL_FALSE.equals(b)) {
			return false;
		} else {
			throw new BooleanFormatException();
		}
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
	
	public void setString(String ident, String val) {
		properties.setProperty(ident, val);
		changed();
	}
	
	public void setInt(String ident, int val) {
		properties.setProperty(ident, "" + val); //$NON-NLS-1$
		changed();
	}
	
	public void setDouble(String ident, double val) {
		properties.setProperty(ident, "" + val); //$NON-NLS-1$
		changed();
	}
	
	public void setBoolean(String ident, boolean val) {
		properties.setProperty(ident, boolToString(val));
		changed();
	}
	
	public void setCCDate(String ident, CCDate val) {
		properties.setProperty(ident, val.getSimpleStringRepresentation());
		changed();
	}
	
	public String getString(String ident) throws PropertyNotFoundException{
		String val = properties.getProperty(ident);
		if (val == null) throw new PropertyNotFoundException();
		return val;
	}
	
	public Integer getInt(String ident) throws PropertyNotFoundException, NumberFormatException {
		String val = properties.getProperty(ident);
		if (val == null) throw new PropertyNotFoundException();
		return Integer.parseInt(val);
	}
	
	public Double getDouble(String ident) throws PropertyNotFoundException, NumberFormatException {
		String val = properties.getProperty(ident);
		if (val == null) throw new PropertyNotFoundException();
		return Double.parseDouble(val);
	}
	
	public Boolean getBoolean(String ident) throws PropertyNotFoundException, BooleanFormatException {
		String val = properties.getProperty(ident);
		if (val == null) throw new PropertyNotFoundException();
		return stringToBool(val);
	}
	
	public CCDate getCCDate(String ident) throws PropertyNotFoundException, CCDateFormatException {
		String val = properties.getProperty(ident);
		if (val == null) throw new PropertyNotFoundException();
		CCDate d = CCDate.getNewMinimumDate();
		if (! d.parse(val, "D.M.Y")) { //$NON-NLS-1$
			throw new CCDateFormatException();
		}
		
		return d;
	}
	
	public void addPropertyToList(CCProperty<Object> p) {
		propertylist.add(p);
	}
	
	private void changed() {
		save();
	}
	
	public Vector<CCProperty<Object>> getPropertyList() {
		return propertylist;
	}

	public void resetAll() {
		for (CCProperty<?> prop : propertylist) {
			prop.setDefault();
		}
	}
}
