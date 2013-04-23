package de.jClipCorn.properties.property;

import javax.swing.KeyStroke;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;

public class CCCaptionedKeyStrokeProperty extends CCKeyStrokeProperty {
	private final String captionIdent;
	
	public CCCaptionedKeyStrokeProperty(int cat, CCProperties prop, String ident, String captionIdent, KeyStroke std) {
		super(cat, prop, ident, std);
		this.captionIdent = captionIdent;
	}

	@Override
	public String getDescription() {
		if (captionIdent.isEmpty()) {
			return ""; //$NON-NLS-1$
		}
		
		return LocaleBundle.getString(captionIdent);
	}
}
