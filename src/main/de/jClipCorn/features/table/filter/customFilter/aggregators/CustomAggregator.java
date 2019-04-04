package de.jClipCorn.features.table.filter.customFilter.aggregators;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.resources.reftypes.IconRef;

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
