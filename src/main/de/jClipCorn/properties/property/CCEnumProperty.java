package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public class CCEnumProperty<T extends ContinoousEnum<T>> extends CCProperty<T> {
	private final EnumWrapper<T> source;
	
	@SuppressWarnings("unchecked")
	public CCEnumProperty(CCPropertyCategory cat, CCProperties prop, String ident, T standard, EnumWrapper<T> source) {
		super(cat, (Class<T>)standard.getClass(), prop, ident, standard);
		
		this.source = source;
	}

	@Override
	public Component getComponent() {
		return new JComboBox<>(new DefaultComboBoxModel<>(source.getList()));
	}

	@Override
	public void setComponentValueToValue(Component c, T val) {
		((JComboBox<?>)c).setSelectedIndex(val.asInt());
	}

	@Override
	public T getComponentValue(Component c) {
		return source.find(((JComboBox<?>)c).getSelectedIndex());
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
			
			T eval = source.find(ival);
			
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
