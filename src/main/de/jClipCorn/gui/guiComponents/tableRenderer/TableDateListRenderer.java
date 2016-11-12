package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.properties.CCProperties;

public class TableDateListRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableDateListRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		CCDateTimeList d = ((CCDateTimeList)value);
		if (d.isEmptyOrOnlyUnspecified()) {
			setText(" - "); //$NON-NLS-1$
		} else {
			switch (CCProperties.getInstance().PROP_SERIES_DISPLAYED_DATE.getValue()) {
			case AVERAGE:
				setText(d.getAverageDateOrInvalid().toStringUINormal());
				break;
			case FIRST_VIEWED:
				setText(d.getFirstDateOrInvalid().toStringUINormal());
				break;
			case LAST_VIEWED:
				setText(d.getLastDateOrInvalid().toStringUINormal());
				break;
			default:
				setText("??_ERR_??"); //$NON-NLS-1$
				break;
			}
		}
    }
}
