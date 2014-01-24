package de.jClipCorn.gui.guiComponents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import de.jClipCorn.properties.property.CCBoolProperty;

public class PropertyCheckbox extends JCheckBox implements ActionListener {
	private static final long serialVersionUID = 4712086332201573643L;

	private CCBoolProperty property;
	
	public PropertyCheckbox(CCBoolProperty property) {
		super();
		
		if (property == null) { // WindowBuilder Bugfix
			setText("[WindowBuilder] DUMMY TXT"); //$NON-NLS-1$
			return;
		}
		
		setText(property.getDescription());
		this.property = property;
		setSelected(property.getValue());
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		property.setValue(isSelected());
	}
}
