package de.jClipCorn.features.table.renderer;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDateTime;

public class TableLastViewedRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableLastViewedRenderer() {
		super();
	}

	@Override
	@SuppressWarnings("unchecked")
    public void setValue(Object value) {
		var ts = (Opt<CCDateTime>)value;
		if (!ts.isPresent()) {
			setText(Str.Empty);
		} else {
			setText(ts.get().toStringUIDateOnly());
		}
    }
}
