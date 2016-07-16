package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;

public class TableQualityRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableQualityRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCMovieQuality)value).asString());
		setIcon(((CCMovieQuality)value).getIcon());
    }
}
