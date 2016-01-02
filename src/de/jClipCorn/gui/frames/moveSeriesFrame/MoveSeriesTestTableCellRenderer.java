	package de.jClipCorn.gui.frames.moveSeriesFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class MoveSeriesTestTableCellRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = -7348556832478706217L;

	public MoveSeriesTestTableCellRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final Component c = super.getTableCellRendererComponent(table, ((String) value).substring(1), isSelected, hasFocus, row, column);

		char cr = ((String) value).charAt(0);
		if (cr == '0') {
			c.setBackground(Color.RED);
			c.setForeground(Color.BLACK);
		} else if (cr == '1') {
			c.setBackground(Color.GREEN);
			c.setForeground(Color.BLACK);
		}

		return c;
	}
}
