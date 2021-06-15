package de.jClipCorn.features.table.filter.customFilter.aggregators;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomChildConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomAnyEpisodeAggregator extends CustomAggregator {
	public CustomAnyEpisodeAggregator(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCSeries series) {
		for (CCEpisode ep : series.iteratorEpisodes()) {
			if (_filter.includes(ep)) return true;
		}
		
		return false;
	}

	@Override
	public boolean includes(CCSeason season) {
		for (CCEpisode ep : season.iteratorEpisodes()) {
			if (_filter.includes(ep)) return true;
		}
		
		return false;
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
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Aggr_AnyEpsiode", _filter.getPrecreateName()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Aggr_AnyEpsiode").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ANYEPISODE;
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomAnyEpisodeAggregator(ml);
	}
}
