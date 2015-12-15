package de.jClipCorn.properties.property;

import java.awt.Component;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

public abstract class CCProperty<T extends Object> {
	protected final CCProperties properties;
	protected final String identifier;
	protected final T standard;
	protected final CCPropertyCategory category;
	protected final Class<T> mclass;

	@SuppressWarnings("unchecked")
	public CCProperty(CCPropertyCategory cat, Class<T> pclass, CCProperties prop, String ident, T std) {
		prop.addPropertyToList((CCProperty<Object>)this);
		
		this.category = cat;
		this.properties = prop;
		this.identifier = ident;
		this.standard = std;
		this.mclass = pclass;
	}

	public abstract T getValue();
	public abstract T setValue(T val);
	
	public String getValueAsString() {
		return getValue().toString();
	}
	
	public T setDefault() {
		return setValue(standard);
	}
	
	public T getDefault() {
		return standard;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getTypeName() {
		return getValue().getClass().getSimpleName();
	}
	
	public CCPropertyCategory getCategory() {
		return category;
	}
	
	public String getDescription() {
		return LocaleBundle.getString("Settingsframe.tabbedPnl." + getIdentifier()); //$NON-NLS-1$
	}
	
	public Component getAlternativeComponent() {
		return getComponent();
	}
	
	public void setAlternativeComponentValueToValue(Component c, T val) {
		setComponentValueToValue(c, val);
	}
	
	public T getAlternativeComponentValue(Component c) {
		return getComponentValue(c);
	}
	
	public Component getSecondaryComponent(Component firstComp) {
		return null;
	}
	
	public abstract Component getComponent();
	public abstract void setComponentValueToValue(Component c, T val);
	public abstract T getComponentValue(Component c);
}
