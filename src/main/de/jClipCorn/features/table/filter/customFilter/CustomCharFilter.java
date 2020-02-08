package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterCharConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import org.apache.commons.lang.StringUtils;

public class CustomCharFilter extends AbstractCustomFilter {
	@SuppressWarnings("nls")
	private final static String[] EXCLUSIONS = {"Der", "Die", "Das", "The", "Den", "Le"};
	
	private String charset = ""; //$NON-NLS-1$
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		String first = e.getTitle();
		
		for (String s : EXCLUSIONS) {
			if (first.startsWith(s + Str.SingleSpace)) {
				first = first.substring(s.length() + 1);
			}
		}
		
		if (first.length() < 1) {
			return false;
		}
		
		String fchar = first.substring(0, 1);
		return StringUtils.containsIgnoreCase(charset, fchar);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Char", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Char").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		return charset;
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_CHAR;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addChar("char",  (d) -> this.charset = d,  () -> this.charset);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomCharFilter();
	}

	public static CustomCharFilter create(String data) {
		CustomCharFilter f = new CustomCharFilter();
		f.charset = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterCharConfig(() -> charset, a -> charset = a),
		};
	}
}
