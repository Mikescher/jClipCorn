package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

import javax.swing.*;
import java.awt.*;

public class TableZyklusRenderer extends TableRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableZyklusRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		if (ccprops().PROP_MAINFRAME_CLICKABLEZYKLUS.getValue()) {
			if (ccprops().PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR.getValue()) {
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
