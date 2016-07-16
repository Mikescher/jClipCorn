package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.properties.CCProperties;

public class TableViewedFilter extends RowFilter<ClipTableModel, Object> {
	private boolean defViewed;
	
	public TableViewedFilter(boolean isViewed) {
		super();
		this.defViewed = isViewed;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		ExtendedViewedState evs = ((ExtendedViewedState)e.getValue(ClipTableModel.COLUMN_VIEWED));
		
		if (defViewed) {
			return evs.toBool();
		} else {
			if (evs.getType() != ExtendedViewedStateType.MARKED_FOR_NEVER || evs.toBool())
				return ! evs.toBool();
			else
				return ! CCProperties.getInstance().PROP_MAINFRAME_DONT_FILTER_WATCHNEVER.getValue();
		}
	}
}
