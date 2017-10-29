package de.jClipCorn.table.renderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;

public class TableFSKRenderer extends TableRenderer {
	private static final long serialVersionUID = -7556536853620039148L;

	public TableFSKRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCFSK)value).asString());
		setIcon(((CCFSK)value).getIcon());
    }
}
