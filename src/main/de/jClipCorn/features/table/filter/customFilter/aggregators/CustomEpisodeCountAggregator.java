package de.jClipCorn.features.table.filter.customFilter.aggregators;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomChildConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterIntAreaConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datatypes.CCIntArea;

public class CustomEpisodeCountAggregator extends CustomAggregator {
	private CCIntArea area = new CCIntArea(1, 1, DecimalSearchType.EXACT);

	@Override
	public boolean includes(CCSeries series) {
		int c = 0;
		for (CCEpisode ep : series.iteratorEpisodes()) {
			if (_filter.includes(ep)) c++;
		}
		
		return area.contains(c);
	}

	@Override
	public boolean includes(CCSeason season) {
		int c = 0;
		for (CCEpisode ep : season.iteratorEpisodes()) {
			if (_filter.includes(ep)) c++;
		}
		
		return area.contains(c);
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addInt("low", (d) -> this.area.low = d,  () -> this.area.low);
		cfg.addInt("high", (d) -> this.area.high = d,  () -> this.area.high);
		cfg.addCCEnum("score", DecimalSearchType.getWrapper(), (d) -> this.area.type = d,  () -> this.area.type);
		cfg.addChild("filter", (d) -> this.setProcessorFilter(d),  () -> this.getProcessingFilter());
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterIntAreaConfig(() -> area, a -> area = a, 0, null),
			new CustomChildConfig(() -> getProcessingFilter(), a -> setProcessorFilter(a), LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.ChangeFilter.text")), //$NON-NLS-1$
		};
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.EpisodeCounter", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.EpisodeCounter").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		switch (area.type) {
		case LESSER:
			return "X < " + area.high; //$NON-NLS-1$
		case GREATER:
			return area.low + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return area.low + " < X < " + area.high; //$NON-NLS-1$
		case EXACT:
			return "X == " + area.low; //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_EPISODECOUNTER;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomEpisodeCountAggregator();
	}

	@Override
	public IconRef getListIcon() {
		return Resources.ICN_FILTER_COUNTER;
	}
}
