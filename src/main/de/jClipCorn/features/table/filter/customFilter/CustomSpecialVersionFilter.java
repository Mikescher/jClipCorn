package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCStringList;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomSpecialVersionFilter extends AbstractCustomDatabaseElementFilter {
	private String searchString = ""; //$NON-NLS-1$

	public CustomSpecialVersionFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCDatabaseElement e) {
		CCStringList specialVersions = e.SpecialVersion.get();

		// If search string is empty, match elements that have no anime seasons
		if (searchString.isEmpty()) {
			return specialVersions.isEmpty();
		}

		// Check if the search string is in any of the anime seasons (case insensitive)
		for (String season : specialVersions) {
			if (season.equalsIgnoreCase(searchString)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.SpecialVersion", searchString.isEmpty() ? "(empty)" : searchString); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.SpecialVersion").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_SPECIALVERSION;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("searchstring", (d) -> this.searchString = d,  () -> this.searchString);
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomSpecialVersionFilter(ml);
	}

	public static CustomSpecialVersionFilter create(CCMovieList ml, String data) {
		CustomSpecialVersionFilter f = new CustomSpecialVersionFilter(ml);
		f.searchString = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
				{
						new CustomFilterStringChooserConfig(ml, () -> searchString, p -> searchString = p, ml.getSpecialVersionList(), true, true),
				};
	}
}