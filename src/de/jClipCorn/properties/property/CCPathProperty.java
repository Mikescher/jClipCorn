package de.jClipCorn.properties.property;

import de.jClipCorn.properties.CCProperties;

public class CCPathProperty extends CCStringProperty {
	private final String filterEnd;
	
	public CCPathProperty(int cat, CCProperties prop, String ident, String standard, String filter) {
		super(cat, prop, ident, standard);
		filterEnd = filter;
	}

	public String getFilterEnding() {
		return filterEnd;
	}
}
