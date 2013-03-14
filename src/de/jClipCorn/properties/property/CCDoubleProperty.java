package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.jClipCorn.properties.CCProperties;

public class CCDoubleProperty extends CCProperty<Double> {
	CCDoubleProperty(int cat, CCProperties prop, String ident, Double standard) {
		super(cat, Double.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JSpinner(new SpinnerNumberModel(0.0, Double.MIN_VALUE, Double.MAX_VALUE, 1.0));
	}

	@Override
	public void setComponentValueToValue(Component c, Double val) {
		((JSpinner)c).setValue(val);
	}

	@Override
	public Double getComponentValue(Component c) {
		return (Double) ((JSpinner)c).getValue();
	}
}
