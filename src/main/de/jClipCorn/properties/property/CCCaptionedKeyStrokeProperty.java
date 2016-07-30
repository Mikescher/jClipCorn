package de.jClipCorn.properties.property;

import javax.swing.KeyStroke;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

public class CCCaptionedKeyStrokeProperty extends CCKeyStrokeProperty {
	private final String captionIdent;
	
	public CCCaptionedKeyStrokeProperty(CCPropertyCategory cat, CCProperties prop, String ident, String captionIdent, KeyStroke std) {
		super(cat, prop, ident, std);
		this.captionIdent = captionIdent;
	}

	@Override
	public String getDescription() {
		return captionIdent;
	}
}
