package de.jClipCorn.table.filter.customFilter.aggregators;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;

public class CustomAllSeasonAggregator extends CustomAggregator {
	
	@Override
	public boolean includes(CCSeries series) {
		for (CCSeason ss : series.iteratorSeasons()) {
			if (!_filter.includes(ss)) return false;
		}
		
		return true;
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
	public AbstractCustomFilter createNew() {
		return new CustomAllSeasonAggregator();
	}
}
