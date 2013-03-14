package de.jClipCorn.gui.guiComponents.tableRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;

public class TableTitleRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public TableTitleRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCDatabaseElement)value).getTitle());
		
		if (((CCDatabaseElement)value).isSeries()) {
			setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SERIES));
		} else {
			setIcon(null);
		}
    }
}