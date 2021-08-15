package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;

public class TableScoreRenderer extends TableRenderer {
	private static final long serialVersionUID = -6477856445849848822L;

	public TableScoreRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		setIcon(((CCUserScore)value).getIcon());
    }

	@Override
	public boolean getNeedsExtraSpacing() {
		return false; // unnecessary for icon-only columns
	}
}
