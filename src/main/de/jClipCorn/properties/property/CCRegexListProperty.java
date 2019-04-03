package de.jClipCorn.properties.property;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;

import de.jClipCorn.gui.frames.editStringListPropertyFrame.EditRegexListPropertyFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;

@SuppressWarnings("rawtypes")
public class CCRegexListProperty extends CCStringListProperty {

	public CCRegexListProperty(CCPropertyCategory cat, CCProperties prop, String ident, ArrayList<String> standard) {
		super(cat, prop, ident, standard);

		// Test the standard - prevents config errors - because a invalid standard is fatal
		for (String regex : standard) {
			if (! testRegex(regex)) {
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorRegexList", identifier, mclass.getName())); //$NON-NLS-1$

				setDefault();
			}
		}
	}
	
	@Override
	public Component getSecondaryComponent(final Component firstComponent) {
		JButton b = new JButton("..."); //$NON-NLS-1$
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditRegexListPropertyFrame(firstComponent.getParent(), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setComponentValueToValue(firstComponent, e.getActionCommand().split("\n")); //$NON-NLS-1$
					}
				}, firstComponent, CCRegexListProperty.this).setVisible(true);
			}
		});
		return b;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<String> setValue(ArrayList valuelist) {
		for (String regex : (ArrayList<String>)valuelist) {
			if (! testRegex(regex)) {
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorRegexList", identifier, mclass.getName())); //$NON-NLS-1$

				setDefault();
				return standard;
			}
		}
		
		return super.setValue(valuelist);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<String> getValue() {
		ArrayList<String> regexes = super.getValue();

		for (String regex : regexes) {
			if (! testRegex(regex)) {
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorRegexList", identifier, mclass.getName())); //$NON-NLS-1$

				setDefault();
				return standard;
			}
		}
		
		return regexes;
	}
	
	public boolean testRegex(String rex) {
		if (rex.trim().isEmpty()) return true;
		
		try {
			Pattern.compile(rex);
		} catch (PatternSyntaxException e) {
			return false;
		}
		
		return true;
	}
}
