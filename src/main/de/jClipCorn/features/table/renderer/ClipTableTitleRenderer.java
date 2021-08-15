package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.resources.Resources;

public class ClipTableTitleRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public ClipTableTitleRenderer(CCMovieList ml) {
		super(ml);
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