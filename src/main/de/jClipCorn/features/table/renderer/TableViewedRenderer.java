package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.util.ExtendedViewedState;

public class TableViewedRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableViewedRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setIcon(((ExtendedViewedState)value).getIconTable());
    }

	@Override
	public boolean getNeedsExtraSpacing() {
		return false; // unnecessary for icon-only columns
	}
}
