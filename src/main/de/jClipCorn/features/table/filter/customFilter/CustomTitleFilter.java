package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumOptionConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.datatypes.StringMatchType;

public class CustomTitleFilter extends AbstractCustomFilter {
	private String searchString = ""; //$NON-NLS-1$
	
	private StringMatchType stringMatch = StringMatchType.SM_INCLUDES;
	private boolean caseSensitive = true;

	public CustomTitleFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		String title = e.title().get();
		
		String search = searchString;
		
		if (caseSensitive) {
			title = title.toLowerCase();
			search = search.toLowerCase();
		}
		
		switch (stringMatch) {
		case SM_STARTSWITH:
			return title.startsWith(search);
		case SM_INCLUDES:
			return title.contains(search);
		case SM_ENDSWITH:
			return title.endsWith(search);
		case SM_EQUALS:
			return title.equals(search);
		}
		
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Title", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Title").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		String appendix = (caseSensitive) ? (" [Xx]") : (" [XX]"); //$NON-NLS-1$ //$NON-NLS-2$
		
		switch (stringMatch) {
		case SM_STARTSWITH:
			return searchString + " ***" + appendix; //$NON-NLS-1$
		case SM_INCLUDES:
			return "*** " + searchString + " ***" + appendix; //$NON-NLS-1$ //$NON-NLS-2$
		case SM_ENDSWITH:
			return "*** " + searchString + appendix; //$NON-NLS-1$
		case SM_EQUALS:
			return "== " + searchString + appendix; //$NON-NLS-1$
		}
		
		return "?"; //$NON-NLS-1$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_TITLE;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("searchstring", (d) -> this.searchString = d,  () -> this.searchString);
		cfg.addCCEnum("stringmatch", StringMatchType.getWrapper(), (d) -> this.stringMatch = d,  () -> this.stringMatch);
		cfg.addBool("casesensitive", (d) -> this.caseSensitive = d,  () -> this.caseSensitive);
	}
	
	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomTitleFilter(ml);
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumOptionConfig<>(ml, () -> stringMatch, p -> stringMatch = p, StringMatchType.getWrapper()),
			new CustomFilterStringConfig(ml, () -> searchString, p -> searchString = p),
			new CustomFilterBoolConfig(ml, () -> caseSensitive, p -> caseSensitive = p, LocaleBundle.getString("FilterTree.Custom.FilterFrames.CaseSensitive")), //$NON-NLS-1$
		};
	}
}
