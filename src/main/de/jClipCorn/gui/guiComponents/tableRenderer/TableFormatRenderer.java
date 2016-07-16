package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;

public class TableFormatRenderer extends TableRenderer {
	private static final long serialVersionUID = -7556536853620039148L;

	public TableFormatRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCMovieFormat)value).asString());
		setIcon(((CCMovieFormat)value).getIcon());
    }
}
