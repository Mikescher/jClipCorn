package de.jClipCorn.gui.guiComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.properties.property.CCBoolProperty;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PropertyCheckbox extends JCheckBox implements ActionListener {
	private static final long serialVersionUID = 4712086332201573643L;

	private CCBoolProperty property;

	@DesignCreate
	private static PropertyCheckbox designCreate() { return new PropertyCheckbox(null); }

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
