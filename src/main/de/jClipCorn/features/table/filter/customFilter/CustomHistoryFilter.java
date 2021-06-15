package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterDateSearchConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDateSearchParameter;
import de.jClipCorn.util.datetime.CCDateTime;

import java.util.ArrayList;
import java.util.List;

public class CustomHistoryFilter extends AbstractCustomStructureElementFilter {
	public CCDateSearchParameter Search = new CCDateSearchParameter();
	public boolean Recursive = true;

	public CustomHistoryFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCMovie mov) {
		return Search.includes(mov.ViewedHistory.get());
	}

	@Override
	public boolean includes(CCSeries ser) {
		if (!Recursive) return false;
		
		List<CCDateTime> l = new ArrayList<>();
		for (CCEpisode epi : ser.iteratorEpisodes()) l.addAll(epi.ViewedHistory.get().iterator().enumerate());

		return Search.includes(CCDateTimeList.create(l));
	}

	@Override
	public boolean includes(CCSeason sea) {
		if (!Recursive) return false;
		
		List<CCDateTime> l = new ArrayList<>();
		for (CCEpisode epi : sea.iteratorEpisodes()) l.addAll(epi.ViewedHistory.get().iterator().enumerate());

		return Search.includes(CCDateTimeList.create(l));
	}
	
	@Override
	public boolean includes(CCEpisode epi) {
		return Search.includes(epi.ViewedHistory.get());
	}

	@Override
	public String getName() {
		
		String suffix = ""; //$NON-NLS-1$
		if (Recursive) suffix = " [R]"; //$NON-NLS-1$
		
		switch (Search.Type) {
		case CONTAINS:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", Search.First.toStringUIVerbose()) + suffix; //$NON-NLS-1$
		case CONTAINS_BETWEEN:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", Search.First.toStringUIVerbose() + " - " + Search.Second.toStringUIVerbose()) + suffix; //$NON-NLS-1$ //$NON-NLS-2$
		case CONTAINS_NOT:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "not " + Search.First.toStringUIVerbose()) + suffix; //$NON-NLS-1$ //$NON-NLS-2$
		case CONTAINS_NOT_BETWEEEN:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "not " + Search.First.toStringUIVerbose() + " - " + Search.Second.toStringUIVerbose()) + suffix; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case CONTAINS_ONLY_BETWEEN:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "only " + Search.First.toStringUIVerbose() + " - " + Search.Second.toStringUIVerbose()) + suffix; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		default:
			CCLog.addDefaultSwitchError(this, Search.Type);
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "?"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.History").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_HISTORY;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("type", CCDateSearchParameter.DateSearchType.getWrapper(), (d) -> this.Search.Type = d,  () -> this.Search.Type);
		cfg.addDate("first", (d) -> this.Search.First = d,  () -> this.Search.First);
		cfg.addDate("second", (d) -> this.Search.Second = d,  () -> this.Search.Second);
		cfg.addBool("recursive", (d) -> this.Recursive = d,  () -> this.Recursive);
	}
	
	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomHistoryFilter(ml);
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterDateSearchConfig(ml, () -> Search, p -> Search = p),
			new CustomFilterBoolConfig(ml, () -> Recursive, p -> Recursive = p, LocaleBundle.getString("FilterTree.Custom.Range.Recursive")), //$NON-NLS-1$
		};
	}
}
