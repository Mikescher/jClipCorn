package de.jClipCorn.gui.guiComponents.tableRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import de.jClipCorn.util.YearRange;

public class TableYearRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public TableYearRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((YearRange)value).asString());
    }
}
