package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;

public class TableTagsRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableTagsRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		CCMovieTags tags = (CCMovieTags) value;
		
		setIcon(tags.getIcon());
		setToolTipText(tags.getAsString());
    }
}
