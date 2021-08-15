package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;

public class TableStringTitleRenderer extends TableRenderer {
	private static final long serialVersionUID = -2857849315740108323L;

	public TableStringTitleRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setText((String)value);
    }

}
