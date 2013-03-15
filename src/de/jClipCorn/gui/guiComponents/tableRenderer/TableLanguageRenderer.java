package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;

public class TableLanguageRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableLanguageRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCMovieLanguage)value).asString());
		
		setIcon(((CCMovieLanguage)value).getIcon());
    }
}
