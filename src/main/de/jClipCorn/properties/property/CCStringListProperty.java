package de.jClipCorn.properties.property;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;

import de.jClipCorn.gui.frames.editStringListPropertyFrame.EditStringListPropertyFrame;
import de.jClipCorn.gui.guiComponents.StringListConfigPanel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import org.apache.commons.text.StringEscapeUtils;

@SuppressWarnings("rawtypes")
public class CCStringListProperty extends CCProperty<ArrayList> {
	public CCStringListProperty(CCPropertyCategory cat, CCProperties prop, String ident, ArrayList<String> standard) {
		super(cat, ArrayList.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new StringListConfigPanel();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setComponentValueToValue(Component c, ArrayList val) {
		((StringListConfigPanel)c).setValue(val);
	}

	public void setComponentValueToValue(Component component, String[] value) {
		setComponentValueToValue(component, new ArrayList<>(Arrays.asList(value)));
	}

	@Override
	public ArrayList<String> getComponentValue(Component c) {
		return ((StringListConfigPanel)c).getValue();
	}
	
	@Override
	public Component getAlternativeComponent() {
		return new JTextField(1);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setAlternativeComponentValueToValue(Component c, ArrayList val) {
		((JTextField)c).setText(serializeList(val));
	}
	
	@Override
	public ArrayList<String> getAlternativeComponentValue(Component c) {
		return deserializeList(((JTextField)c).getText());
	}
	
	@Override
	public Component getSecondaryComponent(final Component firstComponent) {
		JButton b = new JButton("..."); //$NON-NLS-1$
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new EditStringListPropertyFrame(firstComponent.getParent(), new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setComponentValueToValue(firstComponent, getValue());
					}
				}, firstComponent, CCStringListProperty.this).setVisible(true);
			}
		});
		return b;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<String> getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		return deserializeList(val);
	}

	@Override
	public String getValueAsString() {
		return serializeList(getValue());
	}
	
	public List<String> getNonEmptyValues() {
		List<String> values = getValue();
		List<String> result = new ArrayList<>();
		
		for (String value : values) {
			if (!value.trim().isEmpty()) result.add(value);
		}
		
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<String> setValue(ArrayList val) {
		properties.setProperty(identifier, serializeList(val));
		
		return getValue();
	}

	public ArrayList<String> setValue(String[] value) {
		return setValue(new ArrayList<>(Arrays.asList(value)));
	}
	
	@SuppressWarnings("nls")
	protected String serializeList(ArrayList<String> value) {
		StringBuilder buildr = new StringBuilder();
		buildr.append("{");
		for (int i = 0; i < value.size(); i++) {
			if (i > 0) buildr.append(", ");

			buildr.append('"');
			buildr.append(StringEscapeUtils.escapeJava(value.get(i)));
			buildr.append('"');
		}
		buildr.append("}");
		
		return buildr.toString();
	}
	
	@SuppressWarnings("nls")
	protected ArrayList<String> deserializeList(String value) {
		if (! (value.startsWith("{") && value.endsWith("}"))) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorStringList", identifier, mclass.getName())); //$NON-NLS-1$
			return new ArrayList<>();
		}
		
		value = value.substring(1, value.length() - 1);
		String[] values = value.split(",");
		
		if (value.trim().isEmpty()) return new ArrayList<>();
		
		ArrayList<String> result = new ArrayList<>();
		for (String split : values) {
			split = split.trim();

			if (! (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2)) {
				CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorStringList", identifier, mclass.getName())); //$NON-NLS-1$
				return new ArrayList<>();
			}
			
			split = split.substring(1, split.length() - 1);
			
			result.add(StringEscapeUtils.unescapeJava(split));
		}
		
		return result;
	}
}
