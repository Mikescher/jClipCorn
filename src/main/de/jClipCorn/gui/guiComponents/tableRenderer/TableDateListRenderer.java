package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.properties.CCProperties;

public class TableDateListRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;
	
	private int displayDateSetting = CCProperties.getInstance().PROP_SERIES_DISPLAYED_DATE.getValue();

	public TableDateListRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		CCDateTimeList d = ((CCDateTimeList)value);
		if (d.isEmpty()) {
			setText(" - "); //$NON-NLS-1$
		} else {
			if (displayDateSetting == 0) 
				setText(d.getLastDateOrInvalid().getSimpleStringRepresentation());
			else if (displayDateSetting == 1) 
				setText(d.getFirstDateOrInvalid().getSimpleStringRepresentation());
			else if (displayDateSetting == 2) 
				setText(d.getAverageDateOrInvalid().getSimpleStringRepresentation());
		}
    }
}
