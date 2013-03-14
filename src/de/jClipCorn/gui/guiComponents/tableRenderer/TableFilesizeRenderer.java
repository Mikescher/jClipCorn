package de.jClipCorn.gui.guiComponents.tableRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;

public class TableFilesizeRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	public TableFilesizeRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCMovieSize)value).getFormatted());
		setToolTipText(((CCMovieSize)value).getBytes() + " bytes"); //$NON-NLS-1$
    }
}
