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

public class CustomAnimeStudioFilter extends AbstractCustomDatabaseElementFilter {
	private String searchString = ""; //$NON-NLS-1$

	public CustomAnimeStudioFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCDatabaseElement e) {
		CCStringList animeStudios = e.AnimeStudio.get();
		
		// If search string is empty, match elements that have no anime studios
		if (searchString.isEmpty()) {
			return animeStudios.isEmpty();
		}
		
		// Check if the search string is in any of the anime studios (case insensitive)
		for (String studio : animeStudios) {
			if (studio.equalsIgnoreCase(searchString)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.AnimeStudio", searchString.isEmpty() ? "(empty)" : searchString); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.AnimeStudio").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ANIMESTUDIO;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("searchstring", (d) -> this.searchString = d,  () -> this.searchString);
	}
	
	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomAnimeStudioFilter(ml);
	}

	public static CustomAnimeStudioFilter create(CCMovieList ml, String data) {
		CustomAnimeStudioFilter f = new CustomAnimeStudioFilter(ml);
		f.searchString = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterStringChooserConfig(ml, () -> searchString, p -> searchString = p, ml.getAnimeStudioList(), true, true),
		};
	}
}