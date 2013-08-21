package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomFormatFilter extends AbstractCustomFilter {
	private CCMovieFormat format = CCMovieFormat.AVI;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return format.equals(e.getValue(ClipTableModel.COLUMN_FORMAT));
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Format", format.asString()); //$NON-NLS-1$
	}

	public CCMovieFormat getFormat() {
		return format;
	}

	public void setFormat(CCMovieFormat format) {
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
		
		CCMovieFormat f = CCMovieFormat.find(format);
		if (f == null) return false;
		setFormat(f);
		
		return true;
	}
}
