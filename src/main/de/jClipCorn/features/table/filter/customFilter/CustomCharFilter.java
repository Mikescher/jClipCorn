package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterCharConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;

public class CustomCharFilter extends AbstractCustomFilter {
	@SuppressWarnings("nls")
	private final static String[] EXCLUSIONS = {"Der", "Die", "Das", "The", "Den", "Le"};
	
	private String[] charset = new String[0]; //$NON-NLS-1$

	public CustomCharFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		String title = e.title().get();
		
		for (String s : EXCLUSIONS) {
			if (title.startsWith(s + Str.SingleSpace)) {
				title = title.substring(s.length() + 1);
			}
		}

		title = title.replaceAll(" ", "");

		if (title.length() < this.charset.length) {
			return false;
		}

		for (int i = 0; i < this.charset.length; i++) {
			if (!StringUtils.containsIgnoreCase(charset[i], title.substring(i, i+1))) {
				return false;
			}
		}

		return true;
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
		return CCStreams.iterate(charset).stringjoin(p->p, " -> ");
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_CHAR;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("charset",  (d) -> this.charset = deserializeCharset(d),  () -> serializeCharset(this.charset));
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomCharFilter(ml);
	}

	public static CustomCharFilter createSingle(CCMovieList ml, String data) {
		CustomCharFilter f = new CustomCharFilter(ml);
		f.charset = new String[]{data};
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterStringConfig(ml, () -> serializeCharset(charset), a -> charset = deserializeCharset(a)),
		};
	}

	public CustomCharFilter appendCharset(String cs) {
		CustomCharFilter f = new CustomCharFilter(this.movielist);
		f.charset = CCStreams.iterate(this.charset).append(cs).toArray(new String[0]);
		return f;
	}

	private static String serializeCharset(String[] v) {
		return CCStreams.iterate(v).stringjoin(p->p, "|");
	}

	private static String[] deserializeCharset(String v) {
		return v.split("\\|");
	}
}
