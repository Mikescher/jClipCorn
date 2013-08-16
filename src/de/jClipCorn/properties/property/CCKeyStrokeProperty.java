package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.KeyStroke;

import de.jClipCorn.gui.guiComponents.KeyStrokeTextfield;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.KeyStrokeUtil;

public class CCKeyStrokeProperty  extends CCProperty<KeyStroke>{
	public CCKeyStrokeProperty(int cat, CCProperties prop, String ident, KeyStroke std) {
		super(cat, KeyStroke.class, prop, ident, std);
	}

	@Override
	public Component getComponent() {
		return new KeyStrokeTextfield();
	}

	@Override
	public void setComponentValueToValue(Component c, KeyStroke val) {
		((KeyStrokeTextfield)c).setKeyStroke(val);
	}

	@Override
	public KeyStroke getComponentValue(Component c) {
		return ((KeyStrokeTextfield)c).getKeyStroke();
	}

	@Override
	public KeyStroke getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		KeyStroke kval;
		
		if (val.isEmpty()) {
			kval = KeyStrokeUtil.getEmptyKeyStroke();
		} else {
			kval = KeyStroke.getKeyStroke(val);
		}
		
		if (kval == null) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorStroke", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		return kval;
	}

	@Override
	public KeyStroke setValue(KeyStroke val) {
		if (KeyStrokeUtil.isEmpty(val)) {
			properties.setProperty(identifier, ""); //$NON-NLS-1$
		} else {
			properties.setProperty(identifier, KeyStrokeUtil.keyStrokeToDirectString(val));
		}
		
		return getValue();
	}
	
	@Override
	public String getValueAsString() {
		return KeyStrokeUtil.keyStrokeToString(getValue());
	}
}
