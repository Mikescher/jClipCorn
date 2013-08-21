package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomQualityFilter extends AbstractCustomFilter {
	private CCMovieQuality quality = CCMovieQuality.STREAM;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return quality.equals(e.getValue(ClipTableModel.COLUMN_QUALITY));
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Quality", quality.asString()); //$NON-NLS-1$
	}

	public CCMovieQuality getQuality() {
		return quality;
	}

	public void setQuality(CCMovieQuality quality) {
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
		
		CCMovieQuality f = CCMovieQuality.find(intval);
		if (f == null) return false;
		setQuality(f);
		
		return true;
	}
}
