package de.jClipCorn.table.filter.customFilter.aggregators;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.resources.IconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.table.filter.customFilter.operators.CustomAndOperator;

public abstract class CustomAggregator extends AbstractCustomStructureElementFilter {
	protected AbstractCustomFilter _filter = new CustomAndOperator();

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
