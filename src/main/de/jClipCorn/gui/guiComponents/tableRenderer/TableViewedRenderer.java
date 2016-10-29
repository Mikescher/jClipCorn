package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;

public class TableViewedRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableViewedRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		switch (((ExtendedViewedState)value).getType()) {
		case VIEWED:
			setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_TRUE));
			break;
		case NOT_VIEWED:
			setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_FALSE));
			break;
		case MARKED_FOR_LATER:
			setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_LATER));
			break;
		case MARKED_FOR_NEVER:
			setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_NEVER));
			break;
		case PARTIAL_VIEWED:
			setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_PARTIAL));
			break;
		}
    }
}
