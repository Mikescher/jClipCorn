package de.jClipCorn.table.renderer;

import java.awt.Component;

import javax.swing.JTable;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import de.jClipCorn.util.helper.LookAndFeelManager;

public class TableRenderer extends SubstanceDefaultTableCellRenderer {
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