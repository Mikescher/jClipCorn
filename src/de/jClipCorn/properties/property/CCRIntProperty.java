package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

public class CCRIntProperty extends CCIntProperty {
	private final int min;
	private final int max;
	
	public CCRIntProperty(CCPropertyCategory cat, CCProperties prop, String ident, Integer standard, Integer min, Integer max) {
		super(cat, prop, ident, standard);
		this.min = min;
		this.max = max;
	}
	
	public CCRIntProperty(CCPropertyCategory cat, CCProperties prop, String ident, Integer standard, Integer max) {
		super(cat, prop, ident, standard);
		this.min = 0;
		this.max = max;
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
		return new JSpinner(new SpinnerNumberModel(min, min, max - 1, 1));
	}
	
	@Override
	public void setComponentValueToValue(Component c, Integer val) {
		((JSpinner)c).setValue(val);
	}

	@Override
	public Integer getComponentValue(Component c) {
		return (Integer) ((JSpinner)c).getValue();
	}

	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
}
