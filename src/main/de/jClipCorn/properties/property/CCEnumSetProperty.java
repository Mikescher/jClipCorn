package de.jClipCorn.properties.property;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextField;

import de.jClipCorn.gui.guiComponents.StringDisplayConverter;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.JCheckBoxList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public class CCEnumSetProperty<T extends ContinoousEnum<T>> extends CCProperty<Set<T>> {
	public enum EnumSetValue { NONE, ALL, FIRST }
	
	private final EnumWrapper<T> source;
	
	@SuppressWarnings("unchecked")
	public CCEnumSetProperty(CCPropertyCategory cat, CCProperties prop, String ident, Set<T> standard, EnumWrapper<T> source) {
		super(cat, (Class<Set<T>>)standard.getClass(), prop, ident, standard);
		
		this.source = source;
	}

	public CCEnumSetProperty(CCPropertyCategory cat, CCProperties prop, String ident, EnumSetValue stdType, EnumWrapper<T> source) {
		super(cat, getTypeClass(), prop, ident, standardConverter(stdType, source));
		
		this.source = source;
	}

	private static <T extends ContinoousEnum<T>> Set<T> standardConverter(EnumSetValue stdType, EnumWrapper<T> wrapper) {
		Set<T> result = new HashSet<>();
		
		switch (stdType) {
		case ALL:
			result.addAll(wrapper.allValues());
			return result;
		case FIRST:
			result.add(wrapper.firstValue());
			return result;
		case NONE:
			return result;
		default:
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Class<Set<T>> getTypeClass() {
		Set<T> s = new HashSet<>(); // java generics are fucking stupid
		                            // as soon as I inline this constructor the gardle build fails
		                            // and I can't even explain why
		
		return (Class<Set<T>>)(s.getClass());
	}

	@Override
	public Component getComponent() {
		JCheckBoxList<T> cbl = new JCheckBoxList<>(new StringDisplayConverter<T>() {
			@Override
			public String toDisplayString(T value) {
				return source.asString(value);
			}
		}, source.allValues());
				
		return cbl;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setComponentValueToValue(Component c, Set<T> val) {
		((JCheckBoxList<T>)c).setCheckedValues(val);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<T> getComponentValue(Component c) {
		return new HashSet<>(((JCheckBoxList<T>)c).getCheckedElements());
	}
	
	@Override
	public Component getAlternativeComponent() {
		return new JTextField();
	}
	
	@Override
	public void setAlternativeComponentValueToValue(Component c, Set<T> val) {
		
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (T single : val) {
			if (!first)builder.append(';');
			builder.append(single.asInt());
			first = false;
		}
		
		((JTextField)c).setText(builder.toString());
	}
	
	@Override
	public Set<T> getAlternativeComponentValue(Component c) {
		String[] aval = ((JTextField)c).getText().split(";"); //$NON-NLS-1$
		
		Set<T> eval = new HashSet<>();
		
		for (String strVal : aval) {
			if (strVal.isEmpty()) continue;
			
			int ival = Integer.parseInt(strVal);
			
			T singleval = source.find(ival);
			
			if (singleval == null) {
				continue;
			}
			
			eval.add(singleval);
		}
		
		return eval;
	}

	@Override
	public Set<T> getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try {
			String[] aval = val.split(";"); //$NON-NLS-1$
			
			Set<T> eval = new HashSet<>();
			
			for (String strVal : aval) {
				
				if (strVal.isEmpty()) continue;
				
				int ival = Integer.parseInt(strVal);
				
				T singleval = source.find(ival);
				
				if (singleval == null) {
					CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorEnum", identifier, mclass.getName())); //$NON-NLS-1$
					setDefault();
					return standard;
				}
				
				eval.add(singleval);
			}
			
			return eval;
		} catch(NumberFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorNumber", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public Set<T> setValue(Set<T> val) {
		if (val != null) {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (T single : val) {
				if (!first)builder.append(';');
				builder.append(single.asInt());
				first = false;
			}
			
			properties.setProperty(identifier, builder.toString());
		}
		
		return getValue();
	}
}
