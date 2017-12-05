package de.jClipCorn.table.filter;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.exceptions.DateFormatException;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;
import de.jClipCorn.util.lambda.Func1to1;

public class FilterSerializationConfig {

	public class FSCProperty  {
		public final String Name; 
		public final Func1to1<String, Boolean> Setter;
		public final Func0to1<String> Getter;

		public FSCProperty(String n, Func1to1<String, Boolean> set, Func0to1<String> get) {
			Name = n;
			Setter = set;
			Getter = get;
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
	}
	
	public void addDate(String pname, Func1to0<CCDate> setter, Func0to1<CCDate> getter) {
		Func1to1<String, Boolean> s = v -> 
		{
			try { setter.invoke(CCDate.createFromSQL(v)); return true; } catch (DateFormatException e) { return false; }
		};
		Func0to1<String> g = () -> getter.invoke().toStringSQL();

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
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
			if (i>0) b.append(","); //$NON-NLS-1$
			b.append(Properties.get(i).Getter.invoke());
		}
		b.append("]"); //$NON-NLS-1$
		
		return b.toString();
	}

	public boolean deserialize(String txt) {
		try {
			String params = getParameterFromExport(txt);
			if (params == null) return false;
			
			String[] paramsplit = splitParameterFromExport(params);
			if (paramsplit.length != Properties.size()) return false;
			
			for (int i=0; i < Properties.size(); i++) {
				boolean b = Properties.get(i).Setter.invoke(paramsplit[i]);
				if (!b) return false;
			}
			
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}