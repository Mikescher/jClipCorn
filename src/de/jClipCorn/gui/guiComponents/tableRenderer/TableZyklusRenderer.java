package de.jClipCorn.gui.guiComponents.tableRenderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class TableZyklusRenderer extends TableRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableZyklusRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCMovieZyklus)value).getHTMLFormatted());
    }
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		return c;
	}
}
