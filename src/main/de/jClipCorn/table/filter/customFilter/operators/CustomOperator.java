package de.jClipCorn.table.filter.customFilter.operators;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.table.filter.AbstractCustomFilter;

public abstract class CustomOperator extends AbstractCustomFilter {
	protected List<AbstractCustomFilter> list = new ArrayList<>();

	public List<AbstractCustomFilter> getList() {
		return list;
	}
	
	public AbstractCustomFilter add(AbstractCustomFilter f) {
		list.add(f);
		return f;
	}
	
	public boolean remove(AbstractCustomFilter f) {
		return list.remove(f);
	}

	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				b.append(",");
			}
			b.append(list.get(i).exportToString());
		}
		b.append("]");
		
		return b.toString();
	}

	@Override
	public boolean importFromString(String txt) {
		list.clear();
		
		if (txt == null) return false;
		
		int id = AbstractCustomFilter.getIDFromExport(txt);
		if (id < 0) return false;
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		String[] paramlist = AbstractCustomFilter.splitParameterFromExport(params);
		
		for (int i = 0; i < paramlist.length; i++) {
			AbstractCustomFilter f = AbstractCustomFilter.createFilterFromExport(paramlist[i]);
			if (f == null) return false;
			add(f);
		}
		
		return true;
	}
}
