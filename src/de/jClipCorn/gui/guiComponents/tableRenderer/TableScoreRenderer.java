package de.jClipCorn.gui.guiComponents.tableRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;

public class TableScoreRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableScoreRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setIcon(((CCMovieScore)value).getIcon());
		setToolTipText(((CCMovieScore)value).asString());
    }

}
