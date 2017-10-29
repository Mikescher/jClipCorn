package de.jClipCorn.table.filter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilterDialogs.CustomQualityFilterDialog;
import de.jClipCorn.util.listener.FinishListener;

public class CustomQualityFilter extends AbstractCustomFilter {
	private CCQuality quality = CCQuality.STREAM;
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return quality.equals(e.getQuality());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Quality", quality.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Quality").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public CCQuality getQuality() {
		return quality;
	}

	public void setQuality(CCQuality quality) {
		this.quality = quality;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_QUALITY;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(quality.asInt()+"");
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
		
		int intval;
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		
		CCQuality f = CCQuality.getWrapper().find(intval);
		if (f == null) return false;
		setQuality(f);
		
		return true;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomQualityFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomQualityFilter();
	}

	public static CustomQualityFilter create(CCQuality data) {
		CustomQualityFilter f = new CustomQualityFilter();
		f.setQuality(data);
		return f;
	}
}
