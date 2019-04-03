package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomGenreFilter extends AbstractCustomDatabaseElementFilter {
	private CCGenre genre = CCGenre.GENRE_000;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return e.getGenres().includes(genre);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Genre", genre.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Genre").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_GENRE;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("genre", CCGenre.getWrapper(), (d) -> this.genre = d,  () -> this.genre);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomGenreFilter();
	}

	public static CustomGenreFilter create(CCGenre data) {
		CustomGenreFilter f = new CustomGenreFilter();
		f.genre = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> genre, p -> genre = p, CCGenre.getWrapper()),
		};
	}
}
