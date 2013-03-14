package de.jClipCorn.gui.guiComponents.tableRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class TableZyklusRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableZyklusRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCMovieZyklus)value).getFormatted());
    }
}
