package de.jClipCorn.table.filter.customFilter.aggregators;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.FilterSerializationConfig;
import de.jClipCorn.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;

public abstract class CustomAggregator extends AbstractCustomDatabaseElementFilter {

	protected AbstractCustomFilter _filter = new CustomAndOperator();

	@Override
	public List<AbstractCustomFilter> getList() {
		List<AbstractCustomFilter> result = new ArrayList<>();
		result.add(_filter);
		return result;
	}
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		if (e.isMovie()) return false;
		
		return includes((CCSeries)e);
	}
	
	public abstract boolean includes(CCSeries e);

	@Override
	protected void initSerialization(FilterSerializationConfig cfg) {
		// manual
	}

	@Override
	@SuppressWarnings("nls")
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(_filter.exportToString());
		b.append("]");
		
		return b.toString();
	}

	@Override
	public boolean importFromString(String txt) {
		if (txt == null) return false;
		
		String params = FilterSerializationConfig.getParameterFromExport(txt);
		if (params == null) return false;

		String[] paramlist = FilterSerializationConfig.splitParameterFromExport(params);
		if (paramlist.length != 1) return false;

		_filter = AbstractCustomFilter.createFilterFromExport(paramlist[0]);
		
		return true;
	}

	public AbstractCustomFilter getProcessingFilter() {
		return _filter;
	}

	public void setProcessorFilter(AbstractCustomFilter pf) {
		_filter = pf;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[0];
	}
}
