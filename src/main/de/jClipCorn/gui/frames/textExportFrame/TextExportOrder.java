package de.jClipCorn.gui.frames.textExportFrame;

import de.jClipCorn.gui.localization.LocaleBundle;

public enum TextExportOrder {
	TITLE,
	TITLE_SMART,
	ADD_DATE,
	YEAR;
	
	@Override
	public String toString() {
		switch (this) {
			case TITLE: return LocaleBundle.getString("TextExportFrame.cbxIncludeSize.TextExportOrder.TITLE"); //$NON-NLS-1$
			case TITLE_SMART: return LocaleBundle.getString("TextExportFrame.cbxIncludeSize.TextExportOrder.TITLE_SMART"); //$NON-NLS-1$
			case ADD_DATE: return LocaleBundle.getString("TextExportFrame.cbxIncludeSize.TextExportOrder.ADD_DATE"); //$NON-NLS-1$
			case YEAR: return LocaleBundle.getString("TextExportFrame.cbxIncludeSize.TextExportOrder.YEAR"); //$NON-NLS-1$
		
			default: return "ERROR"; //$NON-NLS-1$
		}
	}
}
