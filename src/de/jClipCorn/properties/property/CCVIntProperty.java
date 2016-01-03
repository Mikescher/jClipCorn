package de.jClipCorn.properties.property;

import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

public class CCVIntProperty extends CCRIntProperty {
	private final Vector<String> values;
	
	public CCVIntProperty(CCPropertyCategory cat, CCProperties prop, String ident, Integer standard, Vector<String> values) {
		super(cat, prop, ident, standard, 0, values.size());
		this.values = values;
	}
	
	@Override
	public String getTypeName() {
		return getValue().getClass().getSimpleName() + ' ' + '<' + values.size() + '>';
	}

	@Override
	public Component getComponent() {
		return new JComboBox<>(new DefaultComboBoxModel<>(values));
	}
	
	@Override
	public void setComponentValueToValue(Component c, Integer val) {
		((JComboBox<?>)c).setSelectedIndex(val);
	}

	@Override
	public Integer getComponentValue(Component c) {
		return ((JComboBox<?>)c).getSelectedIndex();
	}

	public int getValueCOount() {
		return values.size();
	}
}
