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

public class CustomAllEpisodeAggregator extends CustomAggregator {
	
	@Override
	public boolean includes(CCSeries series) {
		for (CCEpisode ep : series.iteratorEpisodes()) {
			if (!_filter.includes(ep)) return false;
		}
		
		return true;
	}

	@Override
	public boolean includes(CCSeason season) {
		for (CCEpisode ep : season.iteratorEpisodes()) {
			if (!_filter.includes(ep)) return false;
		}
		
		return true;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addChild("filter", this::setProcessorFilter, this::getProcessingFilter);
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomChildConfig(this::getProcessingFilter, this::setProcessorFilter, LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.ChangeFilter.text")), //$NON-NLS-1$
		};
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Aggr_AllEpsiode", _filter.getPrecreateName()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Aggr_AllEpsiode").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ALLEPISODE;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomAllEpisodeAggregator();
	}
}
