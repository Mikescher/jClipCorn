package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;

public class TableEpisodeRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableEpisodeRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setText(value.toString());
    }
}
