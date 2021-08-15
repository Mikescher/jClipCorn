package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;

public class TableFSKRenderer extends TableRenderer {
	private static final long serialVersionUID = -7556536853620039148L;

	public TableFSKRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setText(((CCFSK)value).asString());
		setIcon(((CCFSK)value).getIcon());
    }
}
