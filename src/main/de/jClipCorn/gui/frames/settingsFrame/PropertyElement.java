package de.jClipCorn.gui.frames.settingsFrame;

import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.util.datatypes.Opt;

import java.awt.*;

public class PropertyElement {
	private final CCProperty<Object> property;
	
	private final Component component;
	
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

	public Opt<String> setPropertyValue() {
		return property.setValueIfDiff(property.getComponentValue(component)).map(e -> property.getIdentifier());
	}
}
