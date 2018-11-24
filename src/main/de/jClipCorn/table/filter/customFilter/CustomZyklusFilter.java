package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.AbstractCustomMovieOrSeriesFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumOptionConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterStringConfig;
import de.jClipCorn.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.datatypes.StringMatchType;

public class CustomZyklusFilter extends AbstractCustomMovieOrSeriesFilter {
	private String searchString = ""; //$NON-NLS-1$
	
	private StringMatchType stringMatch = StringMatchType.SM_INCLUDES;
	private boolean caseSensitive = true;
	
	@Override
	public boolean includes(CCMovie m) {
		String zyklus = m.getZyklus().getTitle();
		
		String search = searchString;
		
		if (caseSensitive) {
			zyklus = zyklus.toLowerCase();
			search = search.toLowerCase();
		}
		
		switch (stringMatch) {
		case SM_STARTSWITH:
			return zyklus.startsWith(search);
		case SM_INCLUDES:
			return zyklus.indexOf(search) != -1;
		case SM_ENDSWITH:
			return zyklus.endsWith(search);
		case SM_EQUALS:
			return zyklus.equals(search);
		default:
			break;
		}
		
		return false;
	}

	@Override
	public boolean includes(CCSeries m) {
		return false;
	}

	@Override
	public boolean includes(CCSeason m) {
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Zyklus", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Zyklus").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		default:
			return ""; //$NON-NLS-1$
		}
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ZYKLUS;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("searchstring", (d) -> this.searchString = d,  () -> this.searchString);
		cfg.addCCEnum("stringmatch", StringMatchType.getWrapper(), (d) -> this.stringMatch = d,  () -> this.stringMatch);
		cfg.addBool("casesensitive", (d) -> this.caseSensitive = d,  () -> this.caseSensitive);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomZyklusFilter();
	}

	public static AbstractCustomFilter create(String data) {
		CustomZyklusFilter f = new CustomZyklusFilter();
		f.searchString = data;
		f.caseSensitive = true;
		f.stringMatch = StringMatchType.SM_EQUALS;
		return f;
	}

	public static CustomZyklusFilter create(CCMovieZyklus data) {
		CustomZyklusFilter f = new CustomZyklusFilter();
		f.searchString = data.getTitle();
		f.caseSensitive = true;
		f.stringMatch = StringMatchType.SM_EQUALS;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumOptionConfig<>(() -> stringMatch, p -> stringMatch = p, StringMatchType.getWrapper()),
			new CustomFilterStringConfig(() -> searchString, p -> searchString = p),
			new CustomFilterBoolConfig(() -> caseSensitive, p -> caseSensitive = p, LocaleBundle.getString("FilterTree.Custom.FilterFrames.CaseSensitive")), //$NON-NLS-1$
		};
	}
}
