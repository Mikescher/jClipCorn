package de.jClipCorn.table.renderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;

public class TableFilesizeRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableFilesizeRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCFileSize)value).getFormatted());
    }
}
