package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.util.CCQualityCategory;

public class TableMediaInfoCatRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableMediaInfoCatRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		CCQualityCategory cat = ((CCQualityCategory)value);

		setText(cat.getCaption());
		setIcon(cat.getIcon());
		setToolTipText(cat.getTooltip());
    }
}
