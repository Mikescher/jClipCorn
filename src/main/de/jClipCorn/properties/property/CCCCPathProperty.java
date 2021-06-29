package de.jClipCorn.properties.property;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.filesystem.CCPath;

import java.awt.*;

public class CCCCPathProperty extends CCProperty<CCPath> {
	public CCCCPathProperty(CCPropertyCategory cat, CCProperties prop, String ident, CCPath standard) {
		super(cat, CCPath.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JCCPathTextField(1);
	}

	@Override
	public void setComponentValueToValue(Component c, CCPath val) {
		((JCCPathTextField)c).setPath(val);
	}

	@Override
	public CCPath getComponentValue(Component c) {
		return ((JCCPathTextField)c).getPath();
	}

	@Override
	public CCPath getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		return transformFromStorage(val);
	}

	@Override
	public CCPath setValue(CCPath val) {
		properties.setProperty(identifier, transformToStorage(val));

		return getValue();
	}

	@Override
	public boolean isValue(CCPath val) {
		return CCPath.isEqual(val, getValue());
	}

	protected String transformToStorage(CCPath value) {
		return value.toString();
	}

	protected CCPath transformFromStorage(String value) {
		return CCPath.create(value);
	}
}
