package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

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
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Quality", quality); //$NON-NLS-1$
	}

	public CCMovieQuality getQuality() {
		return quality;
	}

	public void setQuality(CCMovieQuality quality) {
		this.quality = quality;
	}
	
	@Override
	public int getID() {
		return 9;
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
	
	@Override
	public boolean importFromString(String txt) {
		
	}
}
