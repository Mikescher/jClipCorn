package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomLanguageFilter extends AbstractCustomDatabaseElementFilter {
	private CCDBLanguage language = CCDBLanguage.GERMAN;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return language.equals(e.getLanguage());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Language", language); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Language").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_LANGUAGE;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("language", CCDBLanguage.getWrapper(), (d) -> this.language = d,  () -> this.language);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomLanguageFilter();
	}

	public static CustomLanguageFilter create(CCDBLanguage data) {
		CustomLanguageFilter f = new CustomLanguageFilter();
		f.language = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> language, p -> language = p, CCDBLanguage.getWrapper()),
		};
	}
}
