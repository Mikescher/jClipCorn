package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JCheckBox;

import de.jClipCorn.properties.CCProperties;

public class CCBoolProperty extends CCProperty<Boolean> {
	public CCBoolProperty(int cat, CCProperties prop, String ident, boolean standard) {
		super(cat, Boolean.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JCheckBox();
	}

	@Override
	public void setComponentValueToValue(Component c, Boolean val) {
		((JCheckBox)c).setSelected(val);
	}

	@Override
	public Boolean getComponentValue(Component c) {
		return ((JCheckBox)c).isSelected();
	}
}
