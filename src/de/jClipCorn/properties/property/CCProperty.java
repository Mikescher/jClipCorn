package de.jClipCorn.properties.property;

import java.awt.Component;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.exceptions.BooleanFormatException;
import de.jClipCorn.properties.exceptions.CCDateFormatException;
import de.jClipCorn.properties.exceptions.PropertyNotFoundException;
import de.jClipCorn.util.CCDate;

public abstract class CCProperty<T extends Object> {
	protected final CCProperties properties;
	protected final String identifier;
	protected final T standard;
	protected final int category;
	public final Class<T> mclass;

	@SuppressWarnings("unchecked")
	CCProperty(int cat, Class<T> pclass, CCProperties prop, String ident, T std) {
		prop.addPropertyToList((CCProperty<Object>)this);
		
		this.category = cat;
		this.properties = prop;
		this.identifier = ident;
		this.standard = std;
		this.mclass = pclass;
	}

	@SuppressWarnings("unchecked")
	public T getValue() {
		try {
			if (String.class.equals(mclass)) {
				return (T) properties.getString(identifier);
			} else if (Integer.class.equals(mclass)) {
				return (T) properties.getInt(identifier);
			} else if (Double.class.equals(mclass)) {
				return (T) properties.getDouble(identifier);
			} else if (Boolean.class.equals(mclass)) {
				return (T) properties.getBoolean(identifier);
			} else if (CCDate.class.equals(mclass)) {
				return (T) properties.getCCDate(identifier);
			}  else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.PropClassNotDefinied", identifier, mclass.getName())); //$NON-NLS-1$
				return null;
			}
		} catch (PropertyNotFoundException e) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		} catch (NumberFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorNumber", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		} catch (BooleanFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorBool", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		} catch (CCDateFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorCCDate", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}
	
	public String getValueAsString() {
		return getValue().toString();
	}
	
	public T setValue(T val) {
		if (String.class.equals(mclass)) {
			properties.setString(identifier, (String) val);
		} else if (Integer.class.equals(mclass)) {
			properties.setInt(identifier, (Integer) val);
		} else if (Double.class.equals(mclass)) {
			properties.setDouble(identifier, (Double) val);
		} else if (Boolean.class.equals(mclass)) {
			properties.setBoolean(identifier, (Boolean) val);
		} else if (CCDate.class.equals(mclass)) {
			properties.setCCDate(identifier, (CCDate) val);
		} else {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.PropClassNotDefinied", identifier, mclass.getName())); //$NON-NLS-1$
		}
		return getValue();
	}
	
	public T setDefault() {
		return setValue(standard);
	}
	
	public T getDefault() {
		return standard;
	}
	
	public T getStandard() {
		return standard;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getTypeName() {
		return getValue().getClass().getSimpleName();
	}
	
	public int getCategory() {
		return category;
	}
	
	public abstract Component getComponent();
	public abstract void setComponentValueToValue(Component c, T val);
	public abstract T getComponentValue(Component c);
}
