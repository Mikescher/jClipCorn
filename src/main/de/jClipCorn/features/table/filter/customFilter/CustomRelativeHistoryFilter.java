package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterIntAreaConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datatypes.CCIntArea;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.stream.CCStream;

public class CustomRelativeHistoryFilter extends AbstractCustomStructureElementFilter {
	private CCIntArea Search = new CCIntArea(1, 1, DecimalSearchType.EXACT);
	public boolean Recursive = true;
	
	@Override
	public boolean includes(CCMovie mov) {
		return includes(mov.getViewedHistory().iterator());
	}

	@Override
	public boolean includes(CCSeries ser) {
		if (!Recursive) return false;
		return includes(ser.iteratorEpisodes().flatten(e -> e.getViewedHistory().iterator()));
	}

	@Override
	public boolean includes(CCSeason sea) {
		if (!Recursive) return false;
		return includes(sea.iteratorEpisodes().flatten(e -> e.getViewedHistory().iterator()));
	}

	@Override
	public boolean includes(CCEpisode epi) {
		return includes(epi.getViewedHistory().iterator());
	}

	private boolean includes(CCStream<CCDateTime> dl)
	{
		return Search.contains(dl.filter(e -> !e.isUnspecifiedOrMinimum()).map(t -> CCDate.getCurrentDate().getDayDifferenceTo(t.date)));
	}


	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.RelativeHistory", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.RelativeHistory").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public String asString() {
		switch (Search.type) {
			case LESSER:
				return "X < " + Search.high; //$NON-NLS-1$
			case GREATER:
				return Search.low + " < X"; //$NON-NLS-1$
			case IN_RANGE:
				return Search.low + " < X < " + Search.high; //$NON-NLS-1$
			case EXACT:
				return "X == " + Search.low; //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
		}
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_RELATIVE_HISTORY;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addInt("low", (d) -> this.Search.low = d,  () -> this.Search.low);
		cfg.addInt("high", (d) -> this.Search.high = d,  () -> this.Search.high);
		cfg.addCCEnum("score", DecimalSearchType.getWrapper(), (d) -> this.Search.type = d,  () -> this.Search.type);
		cfg.addBool("recursive", (d) -> this.Recursive = d,  () -> this.Recursive);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomRelativeHistoryFilter();
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{new CustomFilterIntAreaConfig(() -> Search, a -> Search = a, null, null),
			new CustomFilterBoolConfig(() -> Recursive, p -> Recursive = p, LocaleBundle.getString("FilterTree.Custom.Range.Recursive")), //$NON-NLS-1$
		};
	}
}
