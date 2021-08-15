package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.properties.CCProperties;
import org.pushingpixels.substance.api.renderer.SubstanceDefaultTableCellRenderer;

import javax.swing.*;
import java.awt.*;

public class TableRenderer extends SubstanceDefaultTableCellRenderer implements ResizableColumnRenderer {
	private static final long serialVersionUID = -5742763325569140076L;

	protected final CCMovieList movielist;

	public TableRenderer(CCMovieList ml) {
		super();
		movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (!isSelected && !LookAndFeelManager.isRadiance()) {
			var col = ((TableModelRowColorInterface) table.getModel()).getRowColor(row);
			if (col.isPresent()) c.setBackground(col.get());
		}

		return c;
	}

	@Override
	public boolean getNeedsExtraSpacing() {
		return true; // default for text columns
	}
}
