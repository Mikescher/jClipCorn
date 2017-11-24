package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.FilterSerializationConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;

public class CustomExtendedViewedFilter extends AbstractCustomFilter {
	private ExtendedViewedStateType state = ExtendedViewedStateType.NOT_VIEWED;
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return e.getExtendedViewedState().Type == state;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.ExtViewed", state.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.ExtViewed").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_EXTVIEWED;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("state", ExtendedViewedStateType.getWrapper(), (d) -> this.state = d,  () -> this.state);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomExtendedViewedFilter();
	}

	public static CustomExtendedViewedFilter create(ExtendedViewedStateType data) {
		CustomExtendedViewedFilter f = new CustomExtendedViewedFilter();
		f.state = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> state, p -> state = p, ExtendedViewedStateType.getWrapper()),
		};
	}
}
