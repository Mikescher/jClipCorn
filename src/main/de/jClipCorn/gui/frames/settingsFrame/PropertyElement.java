package de.jClipCorn.gui.frames.settingsFrame;

import java.awt.Component;

import de.jClipCorn.properties.property.CCProperty;

public class PropertyElement {
	private CCProperty<Object> property;
	
	private Component component;
	
	public PropertyElement(CCProperty<Object> p, Component c) {
		this.property = p;
		this.component = c;
	}

	public Component getComponent() {
		return component;
	}
	
	public CCProperty<?> getProperty() {
		return property;
	}

	public void setComponentValue() {
		property.setComponentValueToValue(component, property.getValue());
	}

	public Object getComponentValue() {
		return property.getComponentValue(component);
	}

	public void setPropertyValue() {
		property.setValue(property.getComponentValue(component));
	}
}
