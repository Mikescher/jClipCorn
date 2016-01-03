package de.jClipCorn.properties.property;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

public class CCPIntProperty extends CCRIntProperty {
	public CCPIntProperty(CCPropertyCategory cat, CCProperties prop, String ident, Integer standard) {
		super(cat, prop, ident, standard, 0, Integer.MAX_VALUE);
	}
	
	@Override
	public String getTypeName() {
		return getValue().getClass().getSimpleName() + ' ' + '<' + '+' + '>';
	}
}