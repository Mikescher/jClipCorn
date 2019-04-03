package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterDateAreaConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateArea;

public class CustomAddDateFilter extends AbstractCustomFilter {
	private CCDateArea area = new CCDateArea();
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return area.contains(e.getAddDate());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.AddDate", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.AddDate").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		switch (area.type) {
		case LESSER:
			return "X < " + area.high.toStringUIShort(); //$NON-NLS-1$
		case GREATER:
			return area.low.toStringUIShort() + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return area.low.toStringUIShort() + " < X < " + area.high.toStringUIShort(); //$NON-NLS-1$
		case EXACT:
			return "X == " + area.low.toStringUIShort(); //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ADDDATE;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addDate("low", (d) -> this.area.low = d,  () -> this.area.low);
		cfg.addDate("high", (d) -> this.area.high = d, () -> this.area.high);
		cfg.addCCEnum("type", DecimalSearchType.getWrapper(), (d) -> this.area.type = d, () -> this.area.type);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomAddDateFilter();
	}

	public static CustomAddDateFilter create(CCDate data) {
		CustomAddDateFilter f = new CustomAddDateFilter();
		f.area.setExact(data);
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterDateAreaConfig(() -> area, a -> area = a),
		};
	}
}
