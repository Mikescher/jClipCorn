package de.jClipCorn.properties.property;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CCStringSetProperty extends CCProperty<Set<String>> {

	public CCStringSetProperty(CCPropertyCategory cat, CCProperties prop, String ident, Set<String> standard) {
		super(cat, typeClass(), prop, ident, standard);
	}

	public CCStringSetProperty(CCPropertyCategory cat, CCProperties prop, String ident, String[] std) {
		super(cat, typeClass(), prop, ident, new HashSet<String>(Arrays.asList(std)));
	}

	@SuppressWarnings("unchecked")
	private static Class<Set<String>> typeClass() {
		return (Class<Set<String>>)(Object)Set.class;
	}

	@Override
	public Component getComponent() {
		return new JTextArea();
	}

	@Override
	public void setComponentValueToValue(Component c, Set<String> val) {
		((JTextArea)c).setText(CCStreams.iterate(val).stringjoin(System.lineSeparator()));
	}

	@Override
	public Set<String> getComponentValue(Component c) {
		return new HashSet<>(Arrays.asList(((JTextArea)c).getText().split("\\r?\\n")));
	}

	@Override
	public Set<String> getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try {
			String[] aval = val.split(";"); //$NON-NLS-1$
			
			Set<String> eval = new HashSet<>();
			
			for (String strVal : aval) {
				var decStr = Str.fromBase64(strVal);
				eval.add(decStr);
			}
			
			return eval;
		} catch(NumberFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorNumber", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public Set<String> setValue(Set<String> val) {
		if (val != null) {
			properties.setProperty(identifier, setToString(val));
		}
		
		return getValue();
	}

	private static String setToString(Set<String> val) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (var single : val) {
			if (!first)builder.append(';');
			builder.append(Str.toBase64(single));
			first = false;
		}
		return builder.toString();
	}

	@Override
	public boolean isValue(Set<String> val) {
		if (val == null) return false;
		return Str.equals(setToString(val), setToString(getValue()));
	}
}
