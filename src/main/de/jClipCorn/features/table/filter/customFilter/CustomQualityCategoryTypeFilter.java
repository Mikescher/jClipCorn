package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.util.CCQualityCategoryType;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomQualityCategoryTypeFilter extends AbstractCustomDatabaseElementFilter {
	private CCQualityCategoryType value = CCQualityCategoryType.UNKOWN;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return value.equals(e.getMediaInfoCategory().getCategoryType());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Quality", value.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Quality").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_FSK;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("cat", CCQualityCategoryType.getWrapper(), (d) -> this.value = d,  () -> this.value);
	}
	
	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> value, f -> value = f, CCQualityCategoryType.getWrapper()),
		};
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomQualityCategoryTypeFilter();
	}

	public static CustomQualityCategoryTypeFilter create(CCQualityCategoryType data) {
		CustomQualityCategoryTypeFilter f = new CustomQualityCategoryTypeFilter();
		f.value = data;
		return f;
	}
}
