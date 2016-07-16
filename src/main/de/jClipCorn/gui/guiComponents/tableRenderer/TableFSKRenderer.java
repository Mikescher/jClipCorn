package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;

public class TableFSKRenderer extends TableRenderer {
	private static final long serialVersionUID = -7556536853620039148L;

	public TableFSKRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCMovieFSK)value).asString());
		setIcon(((CCMovieFSK)value).getIcon());
    }
}
