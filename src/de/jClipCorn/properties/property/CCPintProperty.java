package de.jClipCorn.properties.property;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

public class CCPintProperty extends CCRIntProperty {
	public CCPintProperty(CCPropertyCategory cat, CCProperties prop, String ident, Integer standard) {
		super(cat, prop, ident, standard, 0, Integer.MAX_VALUE, null);
	}
	
	@Override
	public String getTypeName() {
		return getValue().getClass().getSimpleName() + ' ' + '<' + '+' + '>';
	}
}