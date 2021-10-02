package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterLanguageListConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomExactLanguageFilter extends AbstractCustomStructureElementFilter {
	private CCDBLanguageSet languagelist = CCDBLanguageSet.EMPTY;

	public CustomExactLanguageFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCMovie e) {
		return e.getLanguage().equals(languagelist);
	}

	@Override
	public boolean includes(CCEpisode e) {
		return e.getLanguage().equals(languagelist);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.ExactLanguage", languagelist.toOutputString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.ExactLanguage").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_EXACTLANGUAGE;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addLong("languagelist", (d) -> this.languagelist = CCDBLanguageSet.fromBitmask(d),  () -> this.languagelist.serializeToLong());
	}
	
	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomExactLanguageFilter(ml);
	}

	public static CustomExactLanguageFilter create(CCMovieList ml, CCDBLanguageSet data) {
		CustomExactLanguageFilter f = new CustomExactLanguageFilter(ml);
		f.languagelist = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterLanguageListConfig(ml, () -> languagelist,p -> languagelist = p),
		};
	}
}
