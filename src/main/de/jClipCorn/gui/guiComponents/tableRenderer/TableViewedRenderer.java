package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.util.ExtendedViewedState;

public class TableViewedRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableViewedRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setIcon(((ExtendedViewedState)value).getType().getIconTable());
    }
}
