package de.jClipCorn.gui.guiComponents.tableRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.gui.localization.LocaleBundle;

public class TableOnlinescoreRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;
	
	public TableOnlinescoreRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setIcon(((CCMovieOnlineScore)value).getIcon());
		
		setToolTipText(LocaleBundle.getString("CCMovieScore.Score") + ": " + ((CCMovieOnlineScore)value).asInt());  //$NON-NLS-1$//$NON-NLS-2$
    }
}
