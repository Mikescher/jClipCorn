package de.jClipCorn.gui.guiComponents.enumComboBox;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

// https://stackoverflow.com/a/10808955/1761622
public class CCReadableEnumComboBoxEditor<T extends ContinoousEnum<T>> implements ComboBoxEditor
{
	private final JTextField textField;
	private T myObject;
	private Object myReturnObject;
	private EnumWrapper<T> wrapper;

	public CCReadableEnumComboBoxEditor(EnumWrapper<T> ew) {
		textField = new JTextField();
		wrapper = ew;
	}

	@Override
	public Component getEditorComponent() {
		return textField;
	}

	@Override
	public void setItem(Object anObject) {

		if(anObject != null) {
			myObject = (T)anObject;
			myReturnObject = anObject;
			textField.setText(wrapper.asDisplayString(myObject));
		} else {
			myReturnObject = null;
			textField.setText(Str.Empty);
		}
	}

	@Override
	public Object getItem() {
		String objectTxt = wrapper.asDisplayString(myObject);
		String feildTxt = textField.getText();

		if(objectTxt.equals(feildTxt)){
			return myReturnObject;
		} else {
			String txt = textField.getText();
			for (T val: wrapper.allValues())
			{
				if (Str.equals(txt, wrapper.asDisplayString(val))) return val;
			}
			return null;
		}
	}

	@Override
	public void selectAll() {
		throw new UnsupportedOperationException("Not supported yet. in select All");
	}

	@Override
	public void addActionListener(ActionListener l) {
		textField.addActionListener(l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		textField.removeActionListener(l);
	}
}
