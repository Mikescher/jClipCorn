package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomMovieOrSeriesFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterIntAreaConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datatypes.CCIntArea;
import de.jClipCorn.util.datetime.YearRange;

public class CustomYearFilter extends AbstractCustomMovieOrSeriesFilter {
	private CCIntArea area = new CCIntArea(1900, 1900, DecimalSearchType.EXACT);
	
	@Override
	public boolean includes(CCMovie m) {
		return area.contains(new YearRange(m.getYear()));
	}

	@Override
	public boolean includes(CCSeries s) {
		return area.contains(s.getYearRange());
	}

	@Override
	public boolean includes(CCSeason s) {
		return area.contains(new YearRange(s.getYear()));
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Year", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Year").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		return AbstractCustomFilter.CUSTOMFILTERID_YEAR;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addInt("low", (d) -> this.area.low = d,  () -> this.area.low);
		cfg.addInt("high", (d) -> this.area.high = d,  () -> this.area.high);
		cfg.addCCEnum("type", DecimalSearchType.getWrapper(), (d) -> this.area.type = d,  () -> this.area.type);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomYearFilter();
	}

	public static CustomYearFilter create(Integer data) {
		CustomYearFilter f = new CustomYearFilter();
		f.area.setExact(data);
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterIntAreaConfig(() -> area, a -> area = a, 1900, null),
		};
	}
}
