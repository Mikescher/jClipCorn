package de.jClipCorn.properties.property;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;

import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.frames.editToolbarFrame.EditToolbarFrame;
import de.jClipCorn.gui.frames.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.gui.guiComponents.ToolbarConfigPanel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
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
	public Component getAlternativeComponent() {
		return new JTextField(1);
	}
	
	@Override
	public void setAlternativeComponentValueToValue(Component c, String val) {
		((JTextField)c).setText(val);
	}
	
	@Override
	public String getAlternativeComponentValue(Component c) {
		return ((JTextField)c).getText();
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
	
	@Override
	public String getValue() {
		String val = super.getValue();
		if (isValid(val)) {
			return val;
		} else  {
			setValue(getDefault());
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorToolbar", identifier, mclass.getName())); //$NON-NLS-1$
			return standard;
		}
	}
	
	@Override
	public String setValue(String val) {
		if (isValid(val)) {
			properties.setProperty(identifier, val);
		} else {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorToolbar", identifier, mclass.getName())); //$NON-NLS-1$
			properties.setProperty(identifier, standard);
			return standard;
		}
		
		return getValue();
	}
	
	private boolean isValid(String str) {
		List<String> list= splitStringList(str);
		
		for (int i = 0; i < list.size(); i++) {
			String el = list.get(i);
			if (el.isEmpty() || !(el.equals(ClipToolbar.IDENT_SEPERATOR) || CCActionTree.getInstance() == null || CCActionTree.getInstance().find(el) != null)) {
				return false;
			}
		}
		return true;
		
	}
}
