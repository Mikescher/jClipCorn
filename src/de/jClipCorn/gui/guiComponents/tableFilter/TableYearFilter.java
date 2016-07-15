package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.util.datetime.YearRange;

public class TableYearFilter extends RowFilter<ClipTableModel, Object> {
	private Integer defYear;
	
	public TableYearFilter(Integer year) {
		super();
		this.defYear = year;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((YearRange)e.getValue(ClipTableModel.COLUMN_YEAR)).includes(defYear);
	}
}
