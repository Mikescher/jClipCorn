package de.jClipCorn.table.filter.customFilter;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterCharConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;

public class CustomCharFilter extends AbstractCustomFilter {
	@SuppressWarnings("nls")
	private final static String[] EXCLUSIONS = {"Der", "Die", "Das", "The", "Den", "Le"};
	
	private String charset = ""; //$NON-NLS-1$
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		String first = e.getTitle();
		
		for (String s : EXCLUSIONS) {
			if (first.startsWith(s + " ")) { //$NON-NLS-1$
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

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_CHAR;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(AbstractCustomFilter.escape(charset));
		b.append("]");
		
		return b.toString();
	}
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 1) return false;

		setCharset(AbstractCustomFilter.descape(paramsplit[0]));
		
		return true;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomCharFilter();
	}

	public static CustomCharFilter create(String data) {
		CustomCharFilter f = new CustomCharFilter();
		f.setCharset(data);
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
