package de.jClipCorn.features.table.renderer;

import java.awt.Component;

import javax.swing.JTable;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.properties.CCProperties;

public class TableZyklusRenderer extends TableRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableZyklusRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		if (CCProperties.getInstance().PROP_MAINFRAME_CLICKABLEZYKLUS.getValue()) {
			if (CCProperties.getInstance().PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR.getValue()) {
				setText(((CCMovieZyklus)value).getSimpleHTMLFormatted());
			} else {
				setText(((CCMovieZyklus)value).getHTMLFormatted());
			}
			
		} else {
			setText(((CCMovieZyklus)value).getFormatted());
		}
		
    }
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
		return c;
	}
}
