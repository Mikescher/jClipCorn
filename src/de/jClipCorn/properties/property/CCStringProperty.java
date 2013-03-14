package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JTextField;

import de.jClipCorn.properties.CCProperties;

public class CCStringProperty extends CCProperty<String> {
	public CCStringProperty(int cat, CCProperties prop, String ident, String standard) {
		super(cat, String.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JTextField();
	}

	@Override
	public void setComponentValueToValue(Component c, String val) {
		((JTextField)c).setText(val);
	}

	@Override
	public String getComponentValue(Component c) {
		return ((JTextField)c).getText();
	}
}
