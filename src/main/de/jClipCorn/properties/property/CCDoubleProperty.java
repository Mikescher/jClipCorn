package de.jClipCorn.properties.property;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class CCDoubleProperty extends CCProperty<Double> {
	public CCDoubleProperty(CCPropertyCategory cat, CCProperties prop, String ident, Double standard) {
		super(cat, Double.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JSpinner(new SpinnerNumberModel(0.0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1.0));
	}

	@Override
	public void setComponentValueToValue(Component c, Double val) {
		((JSpinner)c).setValue(val);
	}

	@Override
	public Double getComponentValue(Component c) {
		return (Double) ((JSpinner)c).getValue();
	}

	@Override
	public Double getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		try {
			double ival = Double.parseDouble(val);
			return ival;
		} catch(NumberFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorNumber", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public Double setValue(Double val) {
		properties.setProperty(identifier, "" + val); //$NON-NLS-1$
		
		return getValue();
	}

	@Override
	public boolean isValue(Double val) {
		return Objects.equals(val, getValue());
	}
}
