package de.jClipCorn.properties.property;

import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

public class CCRIntProperty extends CCIntProperty {
	private int min;
	private int max;
	
	private Vector<String> values;
	
	public CCRIntProperty(CCPropertyCategory cat, CCProperties prop, String ident, Integer standard, int min, int max, Vector<String> values) {
		super(cat ,prop, ident, standard);
		this.min = min;
		this.max = max;
		this.values = values;
	}
	
	public CCRIntProperty(CCPropertyCategory cat, CCProperties prop, String ident, Integer standard, Vector<String> values) {
		super(cat, prop, ident, standard);
		this.min = 0;
		this.max = values.size();
		this.values = values;
	}
	
	public CCRIntProperty(CCPropertyCategory cat, CCProperties prop, String ident, Integer standard, Integer max) {
		super(cat, prop, ident, standard);
		this.min = 0;
		this.max = max;
		this.values = null;
	}
	
	@Override
	public Integer getValue() {
		Integer i = super.getValue();
		if (i >= min && i < max) {
			return i;
		} else if (i < min) {
			setValue(min);
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropRangeExceeded", min, max, identifier, i)); //$NON-NLS-1$
			return min;
		} else if (i >= max) {
			setValue(max - 1);
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropRangeExceeded", min, max, identifier, i)); //$NON-NLS-1$
			return max - 1;
		} else {
			System.out.println("Should never be reached [CCRIntProperty]"); //$NON-NLS-1$
			return 0;
		}
	}
	
	@Override
	public Integer setValue(Integer val) {
		if (val >= min && val < max) {
			super.setValue(val);
		} else if (val < min) {
			super.setValue(min);
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropRangeExceeded", min, max, identifier, val)); //$NON-NLS-1$
		} else if (val >= max) {
			super.setValue(max - 1);
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropRangeExceeded", min, max, identifier, val)); //$NON-NLS-1$
		} else {
			System.out.println("Should never be reached [CCRIntProperty]"); //$NON-NLS-1$
		}
		
		return getValue();
	}
	
	@Override
	public String getTypeName() {
		return getValue().getClass().getSimpleName() + ' ' + '<' + min + ',' + max + '>';
	}

	@Override
	public Component getComponent() {
		if (values == null) {
			return new JSpinner(new SpinnerNumberModel(min, min, max, 1));
		} else {
			return new JComboBox<>(new DefaultComboBoxModel<>(values));
		}
	}
	
	@Override
	public void setComponentValueToValue(Component c, Integer val) {
		if (values == null) {
			((JSpinner)c).setValue(val);
		} else {
			((JComboBox<?>)c).setSelectedIndex(val);
		}
	}

	@Override
	public Integer getComponentValue(Component c) {
		if (values == null) {
			return (Integer) ((JSpinner)c).getValue();
		} else {
			return ((JComboBox<?>)c).getSelectedIndex();
		}
	}

	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
}
