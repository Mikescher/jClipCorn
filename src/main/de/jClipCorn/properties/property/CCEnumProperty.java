package de.jClipCorn.properties.property;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.CCEnumComboBox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

import java.awt.*;

public class CCEnumProperty<T extends ContinoousEnum<T>> extends CCProperty<T> {
	private final EnumWrapper<T> source;
	
	@SuppressWarnings("unchecked")
	public CCEnumProperty(CCPropertyCategory cat, CCProperties prop, String ident, T standard, EnumWrapper<T> source) {
		super(cat, (Class<T>)standard.getClass(), prop, ident, standard);
		
		this.source = source;
	}

	@Override
	public Component getComponent() {
		return new CCEnumComboBox<>(source);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setComponentValueToValue(Component c, T val) {
		((CCEnumComboBox<T>)c).setSelectedEnum(val);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getComponentValue(Component c) {
		return ((CCEnumComboBox<T>)c).getSelectedEnum();
	}

	@Override
	public T getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try {
			int ival = Integer.parseInt(val);
			
			T eval = source.findOrNull(ival);
			
			if (eval == null) {
				CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorEnum", identifier, mclass.getName())); //$NON-NLS-1$
				setDefault();
				return standard;
			}
			
			return eval;
		} catch(NumberFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorNumber", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public T setValue(T val) {
		if (val != null) {
			properties.setProperty(identifier, "" + val.asInt()); //$NON-NLS-1$
		}
		
		return getValue();
	}
}
