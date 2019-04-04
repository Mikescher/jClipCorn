package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;

public class TableTagsRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableTagsRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setIcon(((CCTagList) value).getIcon());
    }

	@Override
	public boolean getNeedsExtraSpacing() {
		return false; // unnecessary for icon-only columns
	}
}
