package de.jClipCorn.table.filter.customFilter.operators;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.FilterSerializationConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

public abstract class CustomOperator extends AbstractCustomFilter {
	protected List<AbstractCustomFilter> list = new ArrayList<>();

	@Override
	public List<AbstractCustomFilter> getList() {
		return list;
	}

	public CCStream<AbstractCustomFilter> iterate() {
		return CCStreams.iterate(list);
	}
	
	public AbstractCustomFilter add(AbstractCustomFilter f) {
		list.add(f);
		return f;
	}
	
	public boolean remove(AbstractCustomFilter f) {
		return list.remove(f);
	}

	public void removeAll() {
		list.clear();
	}

	@Override
	protected void initSerialization(FilterSerializationConfig cfg) {
		// manual
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
		String params = FilterSerializationConfig.getParameterFromExport(txt);
		if (params == null) return false;
		String[] paramlist = FilterSerializationConfig.splitParameterFromExport(params);
		
		for (int i = 0; i < paramlist.length; i++) {
			AbstractCustomFilter f = AbstractCustomFilter.createFilterFromExport(paramlist[i]);
			if (f == null) return false;
			add(f);
		}
		
		return true;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[0];
	}
}
