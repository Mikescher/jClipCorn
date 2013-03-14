package de.jClipCorn.gui.guiComponents.tableRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class TableStringTitleRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = -2857849315740108323L;

	public TableStringTitleRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText((String)value);
    }

}
