package de.jClipCorn.table.renderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;

public class TableLanguageRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableLanguageRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCDBLanguage)value).asString());
		
		setIcon(((CCDBLanguage)value).getIcon());
    }
}
