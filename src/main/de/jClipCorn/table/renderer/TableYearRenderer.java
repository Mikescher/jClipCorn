package de.jClipCorn.table.renderer;

import de.jClipCorn.util.datetime.YearRange;

public class TableYearRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableYearRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((YearRange)value).asString());
    }
}
