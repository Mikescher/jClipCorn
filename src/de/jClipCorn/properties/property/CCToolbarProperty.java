package de.jClipCorn.properties.property;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;

import de.jClipCorn.gui.frames.editToolbarFrame.EditToolbarFrame;
import de.jClipCorn.gui.guiComponents.ToolbarConfigPanel;
import de.jClipCorn.properties.CCProperties;

public class CCToolbarProperty extends CCStringProperty {

	public CCToolbarProperty(int cat, CCProperties prop, String ident, String standard) {
		super(cat, prop, ident, standard);
	}
	
	public List<String> getValueAsArray() {
		return splitStringList(getValue()); 
	}
	
	public static List<String> splitStringList(String list) {
		return Arrays.asList(list.split("\\|")); // Man beachte dass das hier ne RegEx ist		//$NON-NLS-1$
	}
	
	@Override
	public Component getComponent() {
		return new ToolbarConfigPanel();
	}
	
	@Override
	public void setComponentValueToValue(Component c, String val) {
		((ToolbarConfigPanel)c).setValue(val);
	}

	@Override
	public String getComponentValue(Component c) {
		return ((ToolbarConfigPanel)c).getValue();
	}
	
	@Override
	public Component getSecondaryComponent(final Component firstComponent) {
		JButton b = new JButton("..."); //$NON-NLS-1$
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditToolbarFrame(firstComponent.getParent(), (ToolbarConfigPanel) firstComponent, CCToolbarProperty.this).setVisible(true);
			}
		});
		return b;
	}
	
	//TODO Add Alternative Component (edit)
	//TODO Add Integrity check on load
}
