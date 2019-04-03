package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomFSKFilter extends AbstractCustomDatabaseElementFilter {
	private CCFSK fsk = CCFSK.RATING_0;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return fsk.equals(e.getFSK());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.FSK", fsk.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.FSK").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_FSK;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("fsk", CCFSK.getWrapper(), (d) -> this.fsk = d,  () -> this.fsk);
	}
	
	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> fsk, f -> fsk = f, CCFSK.getWrapper()),
		};
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomFSKFilter();
	}

	public static CustomFSKFilter create(CCFSK data) {
		CustomFSKFilter f = new CustomFSKFilter();
		f.fsk = data;
		return f;
	}
}
