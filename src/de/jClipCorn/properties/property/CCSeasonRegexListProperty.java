package de.jClipCorn.properties.property;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

public class CCSeasonRegexListProperty extends CCRegexListProperty {

	public CCSeasonRegexListProperty(CCPropertyCategory cat, CCProperties prop, String ident, ArrayList<String> standard) {
		super(cat, prop, ident, standard);
	}
	
	@Override
	public boolean testRegex(String rex) {
		if (rex.trim().isEmpty()) return true;
		
		try {
			Pattern.compile("^" + rex + "$"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (PatternSyntaxException e) {
			return false;
		}
		
		return rex.contains("(?<index>"); //$NON-NLS-1$
	}
}
