package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomViewedFilter extends AbstractCustomFilter {
	private boolean viewed = false;

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return ! viewed ^ e.getExtendedViewedState().toBool();
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Viewed", (viewed) ? (LocaleBundle.getString("FilterTree.Viewed.Viewed")) : (LocaleBundle.getString("FilterTree.Viewed.Unviewed"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Viewed").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_VIEWED;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addBool("viewed", (d) -> this.viewed = d,  () -> this.viewed);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomViewedFilter();
	}

	public static AbstractCustomFilter create(boolean data) {
		CustomViewedFilter f = new CustomViewedFilter();
		f.viewed = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterBoolConfig(() -> viewed, a -> viewed = a, LocaleBundle.getString("FilterTree.Viewed")), //$NON-NLS-1$
		};
	}
}
