package de.jClipCorn.features.table.renderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

import javax.swing.*;

public class TableLanguageRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableLanguageRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setHorizontalAlignment(SwingConstants.CENTER);
		setIcon(((CCDBLanguageList)value).getIcon());
    }
}
