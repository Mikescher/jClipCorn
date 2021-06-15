package de.jClipCorn.features.table.filter.customFilter.aggregators;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomChildConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomAllSeasonAggregator extends CustomAggregator {
	public CustomAllSeasonAggregator(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCSeries series) {
		for (CCSeason ss : series.iteratorSeasons()) {
			if (!_filter.includes(ss)) return false;
		}
		
		return true;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addChild("filter", (d) -> this.setProcessorFilter(d),  () -> this.getProcessingFilter());
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomChildConfig(ml, () -> getProcessingFilter(), a -> setProcessorFilter(a), LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.ChangeFilter.text")), //$NON-NLS-1$
		};
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Aggr_AllSeason", _filter.getPrecreateName()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Aggr_AllSeason").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ALLSEASON;
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomAllSeasonAggregator(ml);
	}
}
