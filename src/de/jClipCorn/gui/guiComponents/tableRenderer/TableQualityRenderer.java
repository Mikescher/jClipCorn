package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CombinedMovieQuality;

public class TableQualityRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableQualityRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CombinedMovieQuality)value).asString());
		setToolTipText(((CombinedMovieQuality)value).getToltipText());
		setIcon(((CombinedMovieQuality)value).getIcon());
    }
}
