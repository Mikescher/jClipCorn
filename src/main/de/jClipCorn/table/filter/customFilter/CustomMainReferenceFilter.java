package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomMainReferenceFilter extends AbstractCustomDatabaseElementFilter {
	private CCOnlineRefType reftype = CCOnlineRefType.NONE;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return e.getOnlineReference().Main.type.equals(reftype);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.MainReference", reftype.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.MainReference").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_MAINREFERENCE;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("reftype", CCOnlineRefType.getWrapper(), (d) -> this.reftype = d,  () -> this.reftype);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomMainReferenceFilter();
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> reftype, p -> reftype = p, CCOnlineRefType.getWrapper()),
		};
	}
}
