package de.jClipCorn.table.renderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;

public class TableOnlinescoreRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;
	
	public TableOnlinescoreRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setIcon(((CCOnlineScore)value).getIcon());
    }
}
