package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterIntAreaConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.datatypes.DecimalSearchType;
import de.jClipCorn.util.datatypes.CCIntArea;

public class CustomViewcountFilter extends AbstractCustomStructureElementFilter {
	private CCIntArea area = new CCIntArea(1, 1, DecimalSearchType.EXACT);
	
	@Override
	public boolean includes(CCMovie m) {
		return area.contains(m.getViewedHistory().count());
	}

	@Override
	public boolean includes(CCSeries s) {
		int ct = 0;
		int sm = 0;
		for (CCEpisode ep : s.iteratorEpisodes()) {
			ct++;
			sm += ep.getViewedHistory().count();
		}
		
		double c = (ct == 0) ? 0 : ((sm * 1.0 )/ ct);

		return area.contains(c);
	}

	@Override
	public boolean includes(CCSeason s) {
		int ct = 0;
		int sm = 0;
		for (CCEpisode ep : s.iteratorEpisodes()) {
			ct++;
			sm += ep.getViewedHistory().count();
		}
		
		double c = (ct == 0) ? 0 : ((sm * 1.0 )/ ct);

		return area.contains(c);
	}

	@Override
	public boolean includes(CCEpisode e) {
		return area.contains(e.getViewedHistory().count());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Viewcount", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Viewcount").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		return AbstractCustomFilter.CUSTOMFILTERID_VIEWCOUNT;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addInt("low", (d) -> this.area.low = d,  () -> this.area.low);
		cfg.addInt("high", (d) -> this.area.high = d,  () -> this.area.high);
		cfg.addCCEnum("score", DecimalSearchType.getWrapper(), (d) -> this.area.type = d,  () -> this.area.type);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomViewcountFilter();
	}

	public static CustomViewcountFilter create(Integer data) {
		CustomViewcountFilter f = new CustomViewcountFilter();
		f.area.setExact(data);
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterIntAreaConfig(() -> area, a -> area = a, 0, null),
		};
	}
}
