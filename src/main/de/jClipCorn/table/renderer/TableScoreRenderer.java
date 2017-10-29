package de.jClipCorn.table.renderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;

public class TableScoreRenderer extends TableRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableScoreRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setIcon(((CCUserScore)value).getIcon());
    }

}
