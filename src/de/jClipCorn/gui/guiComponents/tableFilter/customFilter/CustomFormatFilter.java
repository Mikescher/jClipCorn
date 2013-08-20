package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

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
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Format", format); //$NON-NLS-1$
	}

	public CCMovieFormat getFormat() {
		return format;
	}

	public void setFormat(CCMovieFormat format) {
		this.format = format;
	}
	
	@Override
	public int getID() {
		return 4;
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
	public boolean importFromString(String txt) {
		
	}
}
