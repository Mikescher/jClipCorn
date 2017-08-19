package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs.CustomFormatFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomFormatFilter extends AbstractCustomFilter {
	private CCFileFormat format = CCFileFormat.AVI;
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		if (e instanceof CCDatabaseElement) return format.equals(((CCDatabaseElement)e).getFormat());
		if (e instanceof CCEpisode) return format.equals(((CCEpisode)e).getFormat());
		
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Format", format.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Format").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public CCFileFormat getFormat() {
		return format;
	}

	public void setFormat(CCFileFormat format) {
		this.format = format;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_FORMAT;
	}

	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(format.asInt()+"");
		b.append("]");
		
		return b.toString();
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomFormatFilter();
	}
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 1) return false;
		
		int format;
		try {
			format = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		
		CCFileFormat f = CCFileFormat.getWrapper().find(format);
		if (f == null) return false;
		setFormat(f);
		
		return true;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomFormatFilterDialog(this, fl, parent);
	}
	
	public static CustomFormatFilter create(CCFileFormat data) {
		CustomFormatFilter f = new CustomFormatFilter();
		f.setFormat(data);
		return f;
	}
}
