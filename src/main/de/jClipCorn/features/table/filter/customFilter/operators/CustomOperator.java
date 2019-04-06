package de.jClipCorn.features.table.filter.customFilter.operators;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.resources.reftypes.IconRef;
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
		cfg.addChildren("children", (d) -> list = new ArrayList<>(d),  () -> list); //$NON-NLS-1$
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[0];
	}

	@Override
	public IconRef getListIcon() {
		return Resources.ICN_FILTER_OPERATOR;
	}
}
