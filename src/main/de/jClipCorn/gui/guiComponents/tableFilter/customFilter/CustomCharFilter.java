package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs.CustomCharFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomCharFilter extends AbstractCustomDatabaseElementFilter {
	@SuppressWarnings("nls")
	private final static String[] EXCLUSIONS = {"Der", "Die", "Das", "The", "Den", "Le"};
	
	private String charset = ""; //$NON-NLS-1$
	
	@Override
	public boolean includes(CCDatabaseElement e) {
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
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomCharFilterDialog(this, fl, parent);
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
}
