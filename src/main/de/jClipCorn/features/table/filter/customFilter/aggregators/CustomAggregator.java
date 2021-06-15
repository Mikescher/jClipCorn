package de.jClipCorn.features.table.filter.customFilter.aggregators;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomAggregator extends AbstractCustomStructureElementFilter {
	protected AbstractCustomFilter _filter;

	public CustomAggregator(CCMovieList ml) {
		super(ml);
		_filter = new CustomAndOperator(ml);
	}

	@Override
	public List<AbstractCustomFilter> getList() {
		List<AbstractCustomFilter> result = new ArrayList<>();
		result.add(_filter);
		return result;
	}
	
	public AbstractCustomFilter getProcessingFilter() {
		return _filter;
	}

	public void setProcessorFilter(AbstractCustomFilter pf) {
		_filter = pf;
	}

	@Override
	public IconRef getListIcon() {
		return Resources.ICN_FILTER_AGGREGATOR;
	}
}
