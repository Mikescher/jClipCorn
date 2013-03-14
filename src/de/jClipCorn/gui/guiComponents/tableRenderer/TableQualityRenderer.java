package de.jClipCorn.gui.guiComponents.tableRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;

public class TableQualityRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableQualityRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		if (value instanceof CCMovie) {
			setText(((CCMovie)value).getQuality().asString());
			if (((CCMovie)value).getStatus() == CCMovieStatus.STATUS_OK) {
				setIcon(((CCMovie)value).getQuality().getIcon());
			} else {
				setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_QUALITY_STATUS));
				setToolTipText(((CCMovie)value).getStatus().asString());
			}
			
		} else {
			setText(((CCMovieQuality)value).asString());
			
			setIcon(((CCMovieQuality)value).getIcon());
		}
    }
}
