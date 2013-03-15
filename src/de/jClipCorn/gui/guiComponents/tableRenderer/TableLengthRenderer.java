package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.util.TimeIntervallFormatter;

public class TableLengthRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableLengthRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(TimeIntervallFormatter.formatShort(((int)value)));
		setToolTipText(TimeIntervallFormatter.format(((int)value)));
    }
}
