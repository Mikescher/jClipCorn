package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.resources.Resources;

public class TableTitleRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableTitleRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCDatabaseElement)value).getTitle());
		
		if (((CCDatabaseElement)value).isSeries()) {
			setIcon(Resources.ICN_TABLE_SERIES.get());
		} else {
			setIcon(null);
		}
    }
}