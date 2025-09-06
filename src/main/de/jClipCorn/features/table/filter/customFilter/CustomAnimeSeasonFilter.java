package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCStringList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;

public class CustomAnimeSeasonFilter extends AbstractCustomDatabaseElementFilter {
	private String searchString = ""; //$NON-NLS-1$

	public CustomAnimeSeasonFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCDatabaseElement e) {
		CCStringList animeSeasons = e.AnimeSeason.get();
		
		// If search string is empty, match elements that have no anime seasons
		if (searchString.isEmpty()) {
			return animeSeasons.isEmpty();
		}
		
		// Check if the search string is in any of the anime seasons (case insensitive)
		for (String season : animeSeasons) {
			if (season.equalsIgnoreCase(searchString)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.AnimeSeason", searchString.isEmpty() ? "(empty)" : searchString); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.AnimeSeason").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ANIMESEASON;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("searchstring", (d) -> this.searchString = d,  () -> this.searchString);
	}
	
	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomAnimeSeasonFilter(ml);
	}

	public static CustomAnimeSeasonFilter create(CCMovieList ml, String data) {
		CustomAnimeSeasonFilter f = new CustomAnimeSeasonFilter(ml);
		f.searchString = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterStringChooserConfig(ml, () -> searchString, p -> searchString = p, ml.getAnimeSeasonList(), true, true),
		};
	}
}