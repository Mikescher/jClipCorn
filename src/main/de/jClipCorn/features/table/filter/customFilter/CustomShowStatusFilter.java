package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCTVShowStatus;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomShowStatusFilter extends AbstractCustomDatabaseElementFilter {
	private CCTVShowStatus status = CCTVShowStatus.UNKNOWN;

	public CustomShowStatusFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCDatabaseElement e) {
		return e.isSeries() && e.asSeries().ShowStatus.get() == status;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.ShowStatus", status.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.ShowStatus").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_SHOWSTATUS;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("status", CCTVShowStatus.getWrapper(), (d) -> this.status = d, () -> this.status);
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(ml, () -> status, s -> status = s, CCTVShowStatus.getWrapper()),
		};
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomShowStatusFilter(ml);
	}

	public static CustomShowStatusFilter create(CCMovieList ml, CCTVShowStatus data) {
		CustomShowStatusFilter f = new CustomShowStatusFilter(ml);
		f.status = data;
		return f;
	}
}
