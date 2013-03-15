package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.util.CCDate;

public class TableDateRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableDateRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		CCDate d = ((CCDate)value);
		if (d.isMinimum()) {
			setText(" - "); //$NON-NLS-1$
		} else {
			setText(((CCDate)value).getSimpleStringRepresentation());
		}
    }
}
