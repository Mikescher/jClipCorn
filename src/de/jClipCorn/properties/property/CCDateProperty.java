package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JSpinner;

import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;

public class CCDateProperty extends CCProperty<CCDate> {
	public CCDateProperty(int cat, CCProperties prop, String ident, CCDate std) {
		super(cat, CCDate.class, prop, ident, std);
	}

	@Override
	public Component getComponent() {
		return new JCCDateSpinner(getValue(), CCDate.getMinimumDate(), null);
	}

	@Override
	public void setComponentValueToValue(Component c, CCDate val) {
		((JSpinner)c).setValue(val);
	}

	@Override
	public CCDate getComponentValue(Component c) {
		return ((JCCDateSpinner)c).getValue();
	}

	@Override
	public CCDate getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		if (! CCDate.testparse(val, "D.M.Y")) { //$NON-NLS-1$
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorCCDate", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		return CCDate.parse(val, "D.M.Y"); //$NON-NLS-1$
	}

	@Override
	public CCDate setValue(CCDate val) {
		properties.setProperty(identifier, val.getSimpleStringRepresentation());
		
		return getValue();
	}
}
