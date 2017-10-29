package de.jClipCorn.table.filter.customFilter.aggregators;

import java.awt.Component;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilterDialogs.CustomAggregatorFilterDialog;
import de.jClipCorn.util.listener.FinishListener;

public class CustomAnyEpisodeAggregator extends CustomAggregator {
	
	@Override
	public boolean includes(CCSeries series) {
		for (CCEpisode ep : series.iteratorEpisodes()) {
			if (_filter.includes(ep)) return true;
		}
		
		return false;
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
	public AbstractCustomFilter createNew() {
		return new CustomAnyEpisodeAggregator();
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomAggregatorFilterDialog(ml, this, fl, parent);
	}


}
