package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;

public class TableOnlinescoreRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;
	
	public TableOnlinescoreRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setIcon(((CCMovieOnlineScore)value).getIcon());
    }
}
