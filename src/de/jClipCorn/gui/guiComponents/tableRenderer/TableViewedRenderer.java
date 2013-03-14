package de.jClipCorn.gui.guiComponents.tableRenderer;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;

public class TableViewedRenderer extends SubstanceDefaultTableCellRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableViewedRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		if((Boolean)value) {
			setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_VIEWED_TRUE));
		} else {
			setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_VIEWED_FALSE));
		}
    }
}
