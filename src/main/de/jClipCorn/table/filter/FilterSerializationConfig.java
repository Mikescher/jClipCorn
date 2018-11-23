package de.jClipCorn.table.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.jClipCorn.Main;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.exceptions.DateFormatException;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;

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

	private final int ID;
	private final List<FSCProperty> Properties = new ArrayList<>();

	public FilterSerializationConfig(int id) {
		ID = id;
	}

	public void addInt(String pname, Func1to0<Integer> setter, Func0to1<Integer> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			setter.invoke(Integer.parseInt(v));
			return true;
		};
		Func0to1<String> g = () -> getter.invoke().toString();

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}
	
	public void addDate(String pname, Func1to0<CCDate> setter, Func0to1<CCDate> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			try { setter.invoke(CCDate.createFromSQL(v)); return true; } catch (DateFormatException e) { return false; }
		};
		Func0to1<String> g = () -> getter.invoke().toStringSQL();

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}
	
	public <T extends ContinoousEnum<T>> void addCCEnum(String pname, EnumWrapper<T> wrapper, Func1to0<T> setter, Func0to1<T> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			T result = wrapper.find(Integer.parseInt(v));
			if (result == null) return false;
			setter.invoke(result);
			return true;
		};
		Func0to1<String> g = () -> Integer.toString(getter.invoke().asInt());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addString(String pname, Func1to0<String> setter, Func0to1<String> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			setter.invoke(AbstractCustomFilter.descape(v));
			return true;
		};
		Func0to1<String> g = () -> AbstractCustomFilter.escape(getter.invoke());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addChar(String pname, Func1to0<String> setter, Func0to1<String> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			String str = AbstractCustomFilter.descape(v);
			if (str.length() != 1) return false;
			setter.invoke(str);
			return true;
		};
		Func0to1<String> g = () -> AbstractCustomFilter.escape(getter.invoke().substring(0, 1));

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
		
		validate();
	}

	public void addBool(String pname, Func1to0<Boolean> setter, Func0to1<Boolean> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			setter.invoke(Integer.parseInt(v) != 0);
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
			setter.invoke(AbstractCustomFilter.createFilterFromExport(v));
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
			setter.invoke(CCStreams.iterate(v).map(p -> AbstractCustomFilter.createFilterFromExport(p)).enumerate());
			return true;
		};
		Func0to1<String[]> g = () -> CCStreams.iterate(getter.invoke()).map(p -> p.exportToString()).toArray(new String[0]);

		FSCProperty prop = new FSCProperty(pname, s, g, true);

		Properties.add(prop);
		
		validate();
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
		if (txt.length() < 4) return null;
		txt = txt.substring(1, txt.length() - 1);
		int pos = txt.indexOf('|');
		if (pos > 3 || pos < 1) return null;
		return txt.substring(pos + 1);
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
		b.append("|"); //$NON-NLS-1$
		for (int i=0; i < Properties.size(); i++) {
			FSCProperty prop = Properties.get(i);
			
			if (i>0) b.append(","); //$NON-NLS-1$
			
			if (!prop.IsParams) {
				b.append(prop.GetterSingle.invoke());
			} else {
				boolean first = true;
				for (String v : prop.GetterParams.invoke()) {
					if (!first) b.append(","); //$NON-NLS-1$
					b.append(v);
					first = false;
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
					if (!b) return false;
					
				} else {

					boolean b = prop.SetterParams.invoke(CCStreams.countRange(i, paramsplit.length).map(p -> paramsplit[p]).toArray(new String[0]));
					if (!b) return false;
					break;
					
				}
			}
			
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}