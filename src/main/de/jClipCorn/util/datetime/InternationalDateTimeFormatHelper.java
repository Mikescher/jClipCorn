package de.jClipCorn.util.datetime;

import java.util.HashMap;
import java.util.Map;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;

@SuppressWarnings("nls")
public final class InternationalDateTimeFormatHelper {

	public final static String DATE_SQL 		= "yyyy-MM-dd";
	public final static String TIME_SQL    		= "HH:mm:ss";

	public final static String SERIALIZE_DATE 	= "d.M.y";
	public final static String SERIALIZE_TIME 	= "H:m:s";
	
	public final static String[] DESERIALIZE_DATE_INPUT = new String[] { 
		"y-M-d", 
		"M/d/y", 
		"d.M.y"
	};

	public final static String[] DESERIALIZE_TIME_INPUT = new String[]{
		"H:m:s",
		"H:m"
	};

	public final static String[] DESERIALIZE_DATETIME_INPUT = new String[] {
		"y-M-d H:m:s",
		"M/d/y H:m:s",
		"d.M.y H:m:s",
		"y-M-d H:m",
		"M/d/y H:m",
		"d.M.y H:m",
		"y-M-d",
		"M/d/y",
		"d.M.y"
	};
	
	public static Map<CCDateTimeFormat, String> FORMAT_DATE_SHORT;
	public static Map<CCDateTimeFormat, String> FORMAT_DATE_NORMAL;
	public static Map<CCDateTimeFormat, String> FORMAT_DATE_VERBOSE;
	public static Map<CCDateTimeFormat, String> FORMAT_DATE_INPUT;
	
	public static Map<CCDateTimeFormat, String> FORMAT_TIME_SHORT;
	public static Map<CCDateTimeFormat, String> FORMAT_TIME_NORMAL;
	public static Map<CCDateTimeFormat, String> FORMAT_TIME_INPUT;

	public static Map<CCDateTimeFormat, String> FORMAT_DATETIME_DATEONLY;
	public static Map<CCDateTimeFormat, String> FORMAT_DATETIME_SHORT;
	public static Map<CCDateTimeFormat, String> FORMAT_DATETIME_NORMAL;
	public static Map<CCDateTimeFormat, String> FORMAT_DATETIME_INPUT;

	// cache for of performance reasons
	public static CCDateTimeFormat currentFormat;
	static { currentFormat = CCProperties.getInstance().PROP_UI_DATETIME_FORMAT.getValue(); }
	
	static {
		FORMAT_DATE_SHORT = new HashMap<>();
		FORMAT_DATE_SHORT.put(CCDateTimeFormat.ISO_8601, "yy-MM-dd");
		FORMAT_DATE_SHORT.put(CCDateTimeFormat.AMERICA, "MM/dd/yy");
		FORMAT_DATE_SHORT.put(CCDateTimeFormat.GERMAN, "dd.MM.yy");

		FORMAT_DATE_NORMAL = new HashMap<>();
		FORMAT_DATE_NORMAL.put(CCDateTimeFormat.ISO_8601, "yyyy-MM-dd");
		FORMAT_DATE_NORMAL.put(CCDateTimeFormat.AMERICA, "MM/dd/yyyy");
		FORMAT_DATE_NORMAL.put(CCDateTimeFormat.GERMAN, "dd.MM.yyyy");
		
		FORMAT_DATE_VERBOSE = new HashMap<>();
		FORMAT_DATE_VERBOSE.put(CCDateTimeFormat.ISO_8601, "yyyy-MM-dd");
		FORMAT_DATE_VERBOSE.put(CCDateTimeFormat.AMERICA, "MMMM dd, yyyy");
		FORMAT_DATE_VERBOSE.put(CCDateTimeFormat.GERMAN, "dd.MMMM.yyyy");

		FORMAT_DATE_INPUT = new HashMap<>();
		FORMAT_DATE_INPUT.put(CCDateTimeFormat.ISO_8601, "yyyy-MM-dd");
		FORMAT_DATE_INPUT.put(CCDateTimeFormat.AMERICA, "MM/dd/yyyy");
		FORMAT_DATE_INPUT.put(CCDateTimeFormat.GERMAN, "dd.MM.yyyy");
	}

	static {
		FORMAT_TIME_SHORT = new HashMap<>();
		FORMAT_TIME_SHORT.put(CCDateTimeFormat.ISO_8601, "HH:mm");
		FORMAT_TIME_SHORT.put(CCDateTimeFormat.AMERICA, "hh:mm t");
		FORMAT_TIME_SHORT.put(CCDateTimeFormat.GERMAN, "HH:mm");

		FORMAT_TIME_NORMAL = new HashMap<>();
		FORMAT_TIME_NORMAL.put(CCDateTimeFormat.ISO_8601, "HH:mm:ss");
		FORMAT_TIME_NORMAL.put(CCDateTimeFormat.AMERICA, "hh:mm:ss t");
		FORMAT_TIME_NORMAL.put(CCDateTimeFormat.GERMAN, "HH:mm:ss");

		FORMAT_TIME_INPUT = new HashMap<>();
		FORMAT_TIME_INPUT.put(CCDateTimeFormat.ISO_8601, "HH:mm:ss");
		FORMAT_TIME_INPUT.put(CCDateTimeFormat.AMERICA, "HH:mm:ss");
		FORMAT_TIME_INPUT.put(CCDateTimeFormat.GERMAN, "HH:mm:ss");
	}
	
	static {
		FORMAT_DATETIME_DATEONLY = new HashMap<>();
		FORMAT_DATETIME_DATEONLY.put(CCDateTimeFormat.ISO_8601, "yyyy-MM-dd");
		FORMAT_DATETIME_DATEONLY.put(CCDateTimeFormat.AMERICA, "MM/dd/yy");
		FORMAT_DATETIME_DATEONLY.put(CCDateTimeFormat.GERMAN, "dd.MM.yyyy");
		
		FORMAT_DATETIME_SHORT = new HashMap<>();
		FORMAT_DATETIME_SHORT.put(CCDateTimeFormat.ISO_8601, "yyyy-MM-dd HH:mm");
		FORMAT_DATETIME_SHORT.put(CCDateTimeFormat.AMERICA, "MM/dd/y hh:mm t");
		FORMAT_DATETIME_SHORT.put(CCDateTimeFormat.GERMAN, "dd.MM.yyyy HH:mm");

		FORMAT_DATETIME_NORMAL = new HashMap<>();
		FORMAT_DATETIME_NORMAL.put(CCDateTimeFormat.ISO_8601, "yyyy-MM-dd HH:mm:ss");
		FORMAT_DATETIME_NORMAL.put(CCDateTimeFormat.AMERICA, "MM/dd/y hh:mm:ss t");
		FORMAT_DATETIME_NORMAL.put(CCDateTimeFormat.GERMAN, "dd.MM.yyyy HH:mm:ss");

		FORMAT_DATETIME_INPUT = new HashMap<>();
		FORMAT_DATETIME_INPUT.put(CCDateTimeFormat.ISO_8601, "yyyy-MM-dd HH:mm:ss");
		FORMAT_DATETIME_INPUT.put(CCDateTimeFormat.AMERICA, "MM/dd/yyyy HH:mm:ss");
		FORMAT_DATETIME_INPUT.put(CCDateTimeFormat.GERMAN, "dd.MM.yyyy HH:mm:ss");
	}
	
	public static String fmtUIShort(CCDate d) {
		return d.getStringRepresentation(FORMAT_DATE_SHORT.get(currentFormat));
	}

	public static String fmtUINormal(CCDate d) {
		return d.getStringRepresentation(FORMAT_DATE_NORMAL.get(currentFormat));
	}

	public static String fmtUIVerbose(CCDate d) {
		return d.getStringRepresentation(FORMAT_DATE_VERBOSE.get(currentFormat));
	}
	
	public static String fmtInput(CCDate d) {
		return d.getStringRepresentation(FORMAT_DATE_INPUT.get(currentFormat));
	}

	public static String fmtUINormal(CCTime t) {
		return t.getStringRepresentation(FORMAT_TIME_NORMAL.get(currentFormat));
	}

	public static String fmtUIShort(CCTime t) {
		return t.getStringRepresentation(FORMAT_TIME_SHORT.get(currentFormat));
	}

	public static String fmtInput(CCTime t) {
		return t.getStringRepresentation(FORMAT_TIME_INPUT.get(currentFormat));
	}

	public static String fmtUIDateOnly(CCDateTime s) {
		return s.getStringRepresentation(FORMAT_DATETIME_DATEONLY.get(currentFormat));
	}
	
	public static String fmtUIShort(CCDateTime s) {
		return s.getStringRepresentation(FORMAT_DATETIME_SHORT.get(currentFormat));
	}

	public static String fmtUINormal(CCDateTime s) {
		return s.getStringRepresentation(FORMAT_DATETIME_NORMAL.get(currentFormat));
	}

	public static String fmtInput(CCDateTime s) {
		if (s.isUnspecifiedDateTime())
			return LocaleBundle.getString("CCDate.STRINGREP_UNSPEC");
		else if (s.time.isUnspecifiedTime()) 
			return s.date.getStringRepresentation(FORMAT_DATE_INPUT.get(currentFormat));
		else
			return s.getStringRepresentation(FORMAT_DATETIME_INPUT.get(currentFormat));
	}
}
