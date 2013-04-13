package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.KeyStroke;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.KeyStrokeUtil;

public class CCKeyStrokeProperty  extends CCProperty<KeyStroke>{
	public CCKeyStrokeProperty(int cat, CCProperties prop, String ident, KeyStroke std) {
		super(cat, KeyStroke.class, prop, ident, std);
	}

	@Override
	public Component getComponent() {
		return new JTextField();
	}

	@Override
	public void setComponentValueToValue(Component c, KeyStroke val) {
		((JTextField)c).setText(KeyStrokeUtil.keyStrokeToString(val));
	}

	@Override
	public KeyStroke getComponentValue(Component c) {
		return null;
	}

	@Override
	public KeyStroke getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		KeyStroke kval = KeyStroke.getKeyStroke(val);
		
		if (kval == null) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorBool", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		return kval;
	}

	@Override
	public KeyStroke setValue(KeyStroke val) {
		properties.setProperty(identifier, val.toString());

		return getValue();
	}
}
