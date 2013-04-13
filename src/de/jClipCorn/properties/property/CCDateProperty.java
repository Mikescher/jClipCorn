package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JSpinner;

import de.jClipCorn.gui.guiComponents.CCDateEditor;
import de.jClipCorn.gui.guiComponents.SpinnerCCDateModel;
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
		JSpinner p = new JSpinner(new SpinnerCCDateModel(getValue(), CCDate.getNewMinimumDate(), null));
		p.setEditor(new CCDateEditor(p));
		return p;
	}

	@Override
	public void setComponentValueToValue(Component c, CCDate val) {
		((JSpinner)c).setValue(val);
	}

	@Override
	public CCDate getComponentValue(Component c) {
		return (CCDate) ((JSpinner)c).getValue();
	}

	@Override
	public CCDate getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		CCDate d = CCDate.getNewMinimumDate();
		
		if (! d.parse(val, "D.M.Y")) { //$NON-NLS-1$
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorCCDate", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		return d;
	}

	@Override
	public CCDate setValue(CCDate val) {
		properties.setProperty(identifier, val.getSimpleStringRepresentation());
		
		return getValue();
	}
}
