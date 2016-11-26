package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;

public class TableFormatRenderer extends TableRenderer {
	private static final long serialVersionUID = -7556536853620039148L;

	public TableFormatRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCFileFormat)value).asString());
		setIcon(((CCFileFormat)value).getIcon());
    }
}
