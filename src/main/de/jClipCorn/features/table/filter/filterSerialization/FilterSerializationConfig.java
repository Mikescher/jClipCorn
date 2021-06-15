package de.jClipCorn.features.table.filter.filterSerialization;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.exceptions.DateFormatException;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class FilterSerializationConfig {

	public class FSCProperty  {
		public final String Name; 
		public final Func1to1<String, Boolean> SetterSingle;
		public final Func1to1<String[], Boolean> SetterParams;
		public final Func0to1<String> GetterSingle;
		public final Func0to1<String[]> GetterParams;
		public final boolean IsParams;

		public FSCProperty(String n, Func1to1<String, Boolean> set, Func0to1<String> get) {
			Name = n;
			SetterSingle = set;
			GetterSingle = get;
			SetterParams = null;
			GetterParams = null;
			IsParams = false;
		}

		public FSCProperty(String n, Func1to1<String[], Boolean> set, Func0to1<String[]> get, boolean x) {
			Name = n;
			SetterSingle = null;
			GetterSingle = null;
			SetterParams = set;
			GetterParams = get;
			IsParams = true;
		}
	}

	private static final Pattern REGEX_STR_DATA = Pattern.compile("^([0-2][0-9][0-9])*$"); //$NON-NLS-1$
	
	private final int ID;
	private final CCMovieList movielist;
	private final List<FSCProperty> Properties = new ArrayList<>();

	public FilterSerializationConfig(CCMovieList ml, int id) {
		movielist = ml;
		ID = id;
	}

	public void addInt(String pname, Func1to0<Integer> setter, Func0to1<Integer> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			try { setter.invoke(convertDataToInt(v)); } catch (Exception e) { return false; }
			return true;
		};
		Func0to1<String> g = () -> convertIntToData(getter.invoke());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addLong(String pname, Func1to0<Long> setter, Func0to1<Long> getter) {
		Func1to1<String, Boolean> s = v ->
		{
			try { setter.invoke(convertDataToLong(v)); } catch (Exception e) { return false; }
			return true;
		};
		Func0to1<String> g = () -> convertLongToData(getter.invoke());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);

		validate();
	}
	
	public void addDate(String pname, Func1to0<CCDate> setter, Func0to1<CCDate> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			if (v.length() == 10) try { setter.invoke(CCDate.createFromSQL(v)); return true; } catch (DateFormatException e) { return false; } // old schema
			if (v.length() ==  8) { setter.invoke(convertDataToDate(v)); return true; }
			return false;
		};
		Func0to1<String> g = () -> convertDateToData(getter.invoke());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}
	
	public <T extends ContinoousEnum<T>> void addCCEnum(String pname, EnumWrapper<T> wrapper, Func1to0<T> setter, Func0to1<T> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			T result = wrapper.findOrNull(convertDataToInt(v));
			if (result == null) return false;
			setter.invoke(result);
			return true;
		};
		Func0to1<String> g = () -> convertIntToData(getter.invoke().asInt());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addString(String pname, Func1to0<String> setter, Func0to1<String> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			try { 
				if (REGEX_STR_DATA.matcher(v).matches() || v.equals("0"))  //$NON-NLS-1$
					setter.invoke(convertDataToString(v));
				else
					setter.invoke(AbstractCustomFilter.descape(v));  // old schema
			} catch (Exception e) { return false; }
			
			return true;
		};
		Func0to1<String> g = () -> convertStringToData(getter.invoke());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addChar(String pname, Func1to0<String> setter, Func0to1<String> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			if (v.length() == 1) { String str = AbstractCustomFilter.descape(v); if (str.length() != 1) return false; setter.invoke(str); return true; } // old schema
			try {
				int cpoint = Integer.valueOf(v);
				if (!Character.isDefined(cpoint)) return false;
				if (!Character.isValidCodePoint(cpoint)) return false;
				setter.invoke(StringUtils.leftPad(String.valueOf((char)cpoint), 2, '0'));
				return true;
			} catch (Exception e) { return false; }
		};
		Func0to1<String> g = () -> String.valueOf((int)getter.invoke().charAt(0));

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addBool(String pname, Func1to0<Boolean> setter, Func0to1<Boolean> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			try { setter.invoke(Integer.parseInt(v) != 0); } catch (Exception e) { return false; }
			return true;
		};
		Func0to1<String> g = () -> getter.invoke() ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addChild(String pname, Func1to0<AbstractCustomFilter> setter, Func0to1<AbstractCustomFilter> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			AbstractCustomFilter f = AbstractCustomFilter.createFilterFromExport(movielist, v);
			if (f == null) return false;
			setter.invoke(f);
			return true;
		};
		Func0to1<String> g = () -> getter.invoke().exportToString();

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addChildren(String pname, Func1to0<List<AbstractCustomFilter>> setter, Func0to1<List<AbstractCustomFilter>> getter) {
		Func1to1<String[], Boolean> s = v -> 
		{
			List<AbstractCustomFilter> l = CCStreams.iterate(v).map(p -> AbstractCustomFilter.createFilterFromExport(movielist, p)).enumerate();
			if (CCStreams.iterate(l).any(Objects::isNull)) return false;
			setter.invoke(l);
			return true;
		};
		Func0to1<String[]> g = () -> CCStreams.iterate(getter.invoke()).map(AbstractCustomFilter::exportToString).toArray(new String[0]);

		FSCProperty prop = new FSCProperty(pname, s, g, true);

		Properties.add(prop);
		
		validate();
	}
	
	@SuppressWarnings("nls")
	private String convertIntToData(int v) {
		return (v>=0) ? Integer.toString(v) : ("0" + Integer.toString(-v));
	}
	
	@SuppressWarnings("nls")
	private int convertDataToInt(String v) {
		if (v.equals("0")) return 0;
		if (v.startsWith("0")) return -1 * Integer.parseInt(v);
		return Integer.parseInt(v);
	}

	@SuppressWarnings("nls")
	private String convertLongToData(long v) {
		return (v>=0) ? Long.toString(v) : ("0" + Long.toString(-v));
	}

	@SuppressWarnings("nls")
	private long convertDataToLong(String v) {
		if (v.equals("0")) return 0;
		if (v.startsWith("0")) return -1 * Long.parseLong(v);
		return Long.parseLong(v);
	}
	
	private String convertStringToData(String v) {
		if (Str.isNullOrEmpty(v)) return "0"; //$NON-NLS-1$
		return CCStreams.iterate(v.getBytes(Str.UTF8)).map(b -> StringUtils.leftPad(String.valueOf(b2i(b)), 3, '0')).stringjoin(b -> b);
	}
	
	private String convertDataToString(String v) {
		if (v.equals("0")) return Str.Empty; //$NON-NLS-1$
		
		byte[] arr = new byte[v.length()/3];
		
		for (int i = 0; i < v.length() / 3; i++) {
			arr[i] = i2b(Integer.valueOf(v.substring(i*3, (i+1)*3)));
		}
		
		return new String(arr, Str.UTF8);
	}
	
	@SuppressWarnings("cast")
	private int b2i(byte b) {
		return (((int)b) + 256) % 256;
	}
	
	private byte i2b(int i) {
		return (i < 128) ? ((byte)i) : ((byte)(i-256));
	}
	
	private String convertDateToData(CCDate v) {
		return StringUtils.leftPad(String.valueOf(v.getYear()),  4, '0') + 
		       StringUtils.leftPad(String.valueOf(v.getMonth()), 2, '0') + 
		       StringUtils.leftPad(String.valueOf(v.getDay()),   2, '0');
	}
	
	private CCDate convertDataToDate(String v) {
		int y = Integer.valueOf(v.substring(0, 4));
		int m = Integer.valueOf(v.substring(4, 6));
		int d = Integer.valueOf(v.substring(6, 8));
		
		return CCDate.create(d, m, y);
	}
	
	private void validate() {
		if (! Main.DEBUG) return;
		
		HashSet<String> names = new HashSet<>();
		
		for (int i = 0; i < Properties.size(); i++) {
			FSCProperty prop = Properties.get(i);

			if (!prop.IsParams && prop.GetterSingle == null) CCLog.addFatalError("[FSC-VALIDATE] GetterSingle == null"); //$NON-NLS-1$
			if (!prop.IsParams && prop.SetterSingle == null) CCLog.addFatalError("[FSC-VALIDATE] SetterSingle == null"); //$NON-NLS-1$
			if (!prop.IsParams && prop.GetterParams != null) CCLog.addFatalError("[FSC-VALIDATE] GetterParams <> null"); //$NON-NLS-1$
			if (!prop.IsParams && prop.SetterParams != null) CCLog.addFatalError("[FSC-VALIDATE] SetterParams <> null"); //$NON-NLS-1$

			if (prop.IsParams && prop.GetterSingle != null) CCLog.addFatalError("[FSC-VALIDATE] GetterSingle <> null"); //$NON-NLS-1$
			if (prop.IsParams && prop.SetterSingle != null) CCLog.addFatalError("[FSC-VALIDATE] SetterSingle <> null"); //$NON-NLS-1$
			if (prop.IsParams && prop.GetterParams == null) CCLog.addFatalError("[FSC-VALIDATE] GetterParams == null"); //$NON-NLS-1$
			if (prop.IsParams && prop.SetterParams == null) CCLog.addFatalError("[FSC-VALIDATE] SetterParams == null"); //$NON-NLS-1$
		
			if (!names.add(prop.Name)) CCLog.addFatalError("[FSC-VALIDATE] Duplicate Name"); //$NON-NLS-1$
			
			if (prop.IsParams && i != Properties.size() - 1) CCLog.addFatalError("[FSC-VALIDATE] Params-Property must be last"); //$NON-NLS-1$
		}
	}
	
	public static String getParameterFromExport(String txt) {
		if (txt.length() < 3) return null;
		
		int pos = txt.indexOf('|');
		int pos2 = txt.indexOf(']');
		if ((pos2 < pos || pos == -1) && pos2 >=0) return Str.Empty;
		
		if (pos > 3 || pos < 1) return null;
				
		return txt.substring(pos+1, txt.length()-1);
	}
	
	public static int getIDFromExport(String txt) {
		if (txt.length() < 3) return -1;

		int pos1 = txt.indexOf('|');
		int pos2 = txt.indexOf(']');
		if (pos1 < 0) pos1 = Integer.MAX_VALUE;
		if (pos2 < 0) pos2 = Integer.MAX_VALUE;
		int pos = Math.min(pos1, pos2);
		
		if (pos > 3 || pos < 1) return -1;

		try {
			return Integer.parseInt(txt.substring(1, pos));
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static String[] splitParameterFromExport(String txt) {
		List<String> resultlist = new ArrayList<>();
		
		StringBuilder builder = new StringBuilder();
		
		int depth = 0;
		boolean escape = false;
		for (int i = 0; i < txt.length(); i++) {
			boolean skip = false;
			char c = txt.charAt(i);
			
			if (escape) {
				escape = false;
			} else {
				if (c == '&') {
					escape = true;
				} else if (c == '[') {
					depth++;
				} else if (c == ']') {
					depth--;
				} else if (c == ',') {
					if (depth == 0) {
						skip = true;
						resultlist.add(builder.toString());
						builder = new StringBuilder(); // clear
					}
				}
			}
			
			if (! skip) {
				builder.append(c);
			}
		}
		if (builder.length() > 0) {
			resultlist.add(builder.toString());
		}
		
		return resultlist.toArray(new String[resultlist.size()]);
	}

	public String serialize() {
		StringBuilder b = new StringBuilder();

		b.append("["); //$NON-NLS-1$
		b.append(ID + ""); //$NON-NLS-1$
		if (Properties.isEmpty() || Properties.size()==1 && Properties.get(0).IsParams && Properties.get(0).GetterParams.invoke().length==0) {
			
			// empty
			
		} else {
			b.append("|"); //$NON-NLS-1$
			for (int i=0; i < Properties.size(); i++) {
				FSCProperty prop = Properties.get(i);
				
				if (!prop.IsParams) {
					if (i>0) b.append(","); //$NON-NLS-1$
					b.append(prop.GetterSingle.invoke());
				} else {
					boolean first = true;
					for (String v : prop.GetterParams.invoke()) {
						if (!first || i>0) b.append(","); //$NON-NLS-1$
						b.append(v);
						first = false;
					}
				}
			}
		}
		b.append("]"); //$NON-NLS-1$
		
		return b.toString();
	}

	public boolean deserialize(String txt) {
		try {
			String params = getParameterFromExport(txt);
			if (params == null) return false;
			
			String[] paramsplit = splitParameterFromExport(params);
			if (Properties.size()>0 && Properties.get(Properties.size()-1).IsParams) {
				if (paramsplit.length < Properties.size()-1) return false;
			} else {
				if (paramsplit.length != Properties.size()) return false;
			}
			
			for (int i=0; i < Properties.size(); i++) {
				FSCProperty prop = Properties.get(i);
				
				if (!prop.IsParams) {

					boolean b = prop.SetterSingle.invoke(paramsplit[i]);
					if (!b) 
						return false; 
					
				} else {

					boolean b = prop.SetterParams.invoke(CCStreams.countRange(i, paramsplit.length).map(p -> paramsplit[p]).toArray(new String[0]));
					if (!b) 
						return false;
					break;
					
				}
			}
			
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}