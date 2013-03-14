package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JSpinner;

import de.jClipCorn.gui.guiComponents.CCDateEditor;
import de.jClipCorn.gui.guiComponents.SpinnerCCDateModel;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;

public class CCDateProperty extends CCProperty<CCDate> {
	public CCDateProperty(int cat, CCProperties prop, String ident, CCDate std) {
		super(cat, CCDate.class, prop, ident, std);
	}

	@Override
	public Component getComponent() {
		JSpinner p = new JSpinner(new SpinnerCCDateModel(getValue(), CCDate.getNewMinimumDate(), null));
		p.setEditor(new CCDateEditor(p));
		return p;
	}

	@Override
	public void setComponentValueToValue(Component c, CCDate val) {
		((JSpinner)c).setValue(val);
	}

	@Override
	public CCDate getComponentValue(Component c) {
		return (CCDate) ((JSpinner)c).getValue();
	}

	@Override
	public String getValueAsString() {
		return getValue().getSimpleStringRepresentation();
	}
}
