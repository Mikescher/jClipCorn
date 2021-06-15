package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.util.CCQualityCategoryType;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomQualityCategoryTypeFilter extends AbstractCustomDatabaseElementFilter {
	private CCQualityCategoryType value = CCQualityCategoryType.UNKOWN;

	public CustomQualityCategoryTypeFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCDatabaseElement e) {
		return value.equals(e.getMediaInfoCategory().getCategoryType());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.QualityCat", value.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.QualityCat").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_QUALITYCAT;
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
			new CustomFilterEnumChooserConfig<>(ml, () -> value, f -> value = f, CCQualityCategoryType.getWrapper()),
		};
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomQualityCategoryTypeFilter(ml);
	}

	public static CustomQualityCategoryTypeFilter create(CCMovieList ml, CCQualityCategoryType data) {
		CustomQualityCategoryTypeFilter f = new CustomQualityCategoryTypeFilter(ml);
		f.value = data;
		return f;
	}
}
