package de.jClipCorn.table.filter;

import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.exceptions.DateFormatException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FilterSerializationConfig {

	public class FSCProperty  {
		public final String Name; 
		public final Function<String, Boolean> Setter;
		public final Supplier<String> Getter;

		public FSCProperty(String n, Function<String, Boolean> set, Supplier<String> get) {
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

	public void addInt(String pname, Consumer<Integer> setter, Supplier<Integer> getter) {
		Function<String, Boolean> s = v -> 
		{
			setter.accept(Integer.parseInt(v));
			return true;
		};
		Supplier<String> g = () -> getter.get().toString();

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
	}
	
	public void addDate(String pname, Consumer<CCDate> setter, Supplier<CCDate> getter) {
		Function<String, Boolean> s = v -> 
		{
			try { setter.accept(CCDate.createFromSQL(v)); return true; } catch (DateFormatException e) { return false; }
		};
		Supplier<String> g = () -> getter.get().toStringSQL();

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
	}
	
	public <T extends ContinoousEnum<T>> void addCCEnum(String pname, EnumWrapper<T> wrapper, Consumer<T> setter, Supplier<T> getter) {
		Function<String, Boolean> s = v -> 
		{
			T result = wrapper.find(Integer.parseInt(v));
			if (result == null) return false;
			setter.accept(result);
			return true;
		};
		Supplier<String> g = () -> Integer.toString(getter.get().asInt());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
	}

	public void addString(String pname, Consumer<String> setter, Supplier<String> getter) {
		Function<String, Boolean> s = v -> 
		{
			setter.accept(AbstractCustomFilter.descape(v));
			return true;
		};
		Supplier<String> g = () -> AbstractCustomFilter.escape(getter.get());

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
	}

	public void addChar(String pname, Consumer<String> setter, Supplier<String> getter) {
		Function<String, Boolean> s = v -> 
		{
			String str = AbstractCustomFilter.descape(v);
			if (str.length() != 1) return false;
			setter.accept(str);
			return true;
		};
		Supplier<String> g = () -> AbstractCustomFilter.escape(getter.get().substring(0, 1));

		FSCProperty prop = new FSCProperty(pname, s, g);

		Properties.add(prop);
	}

	public void addBool(String pname, Consumer<Boolean> setter, Supplier<Boolean> getter) {
		Function<String, Boolean> s = v -> 
		{
			setter.accept(Integer.parseInt(v) != 0);
			return true;
		};
		Supplier<String> g = () -> getter.get() ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$

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
			b.append(Properties.get(i).Getter.get());
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
				boolean b = Properties.get(i).Setter.apply(paramsplit[i]);
				if (!b) return false;
			}
			
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}