package de.jClipCorn.gui.guiComponents.tableRenderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.jClipCorn.util.LookAndFeelManager;

public class TableRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -5742763325569140076L;

	public TableRenderer() {
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if ((! isSelected) && (! LookAndFeelManager.isSubstance())) {
			c.setBackground(((TableModelRowColorInterface) table.getModel()).getRowColor(row));
		}

		return c;
	}
}
