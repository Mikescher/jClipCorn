package de.jClipCorn.properties.property;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;

public class CCStringProperty extends CCProperty<String> {
	public CCStringProperty(CCPropertyCategory cat, CCProperties prop, String ident, String standard) {
		super(cat, String.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JTextField(1);
	}

	@Override
	public void setComponentValueToValue(Component c, String val) {
		((JTextField)c).setText(val);
	}

	@Override
	public String getComponentValue(Component c) {
		return ((JTextField)c).getText();
	}

	@Override
	public String getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		return transformFromStorage(val);
	}

	@Override
	public String setValue(String val) {
		properties.setProperty(identifier, transformToStorage(val));

		return getValue();
	}

	@Override
	public boolean isValue(String val) {
		return Str.equals(val, getValue());
	}

	protected String transformToStorage(String value) {
		return value;
	}
	
	protected String transformFromStorage(String value) {
		return value;
	}
}
