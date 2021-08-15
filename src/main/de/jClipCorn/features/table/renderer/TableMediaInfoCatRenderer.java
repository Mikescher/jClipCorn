package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.CCQualityCategory;

public class TableMediaInfoCatRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableMediaInfoCatRenderer(CCMovieList ml) {
		super(ml);
	}

	@Override
    public void setValue(Object value) {
		CCQualityCategory cat = ((CCQualityCategory)value);

		setText(cat.getShortText());
		setIcon(cat.getIcon());
		setToolTipText(cat.getTooltip());
    }
}
