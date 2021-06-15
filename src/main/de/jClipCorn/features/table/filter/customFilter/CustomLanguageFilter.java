package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomLanguageFilter extends AbstractCustomStructureElementFilter {
	private CCDBLanguage language = CCDBLanguage.GERMAN;

	public CustomLanguageFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCMovie e) {
		return e.getLanguage().contains(language);
	}

	@Override
	public boolean includes(CCSeries e) {
		return e.getAllLanguages().contains(language);
	}

	@Override
	public boolean includes(CCEpisode e) {
		return e.getLanguage().contains(language);
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
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomLanguageFilter(ml);
	}

	public static CustomLanguageFilter create(CCMovieList ml, CCDBLanguage data) {
		CustomLanguageFilter f = new CustomLanguageFilter(ml);
		f.language = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(ml, () -> language, p -> language = p, CCDBLanguage.getWrapper()),
		};
	}
}
