package de.jClipCorn.gui.guiComponents;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.UIManager;

public class ReadableSpinner extends JSpinner {
	private static final long serialVersionUID = -315790707029700239L;

	public ReadableSpinner() {
		super();
		setReadable();
	}

	public ReadableSpinner(SpinnerModel arg0) {
		super(arg0);
		setReadable();
	}
	
	private void setReadable() {
		super.setEnabled(false);
		
		((JSpinner.DefaultEditor)getEditor()).getTextField().setEnabled(false);
		
		((JSpinner.DefaultEditor)getEditor()).getTextField().setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		((JSpinner.DefaultEditor)getEditor()).getTextField().setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$;
		((JSpinner.DefaultEditor)getEditor()).getTextField().setDisabledTextColor(UIManager.getColor("TextField.foreground")); //$NON-NLS-1$
	}

	@Override
	public void setEnabled(boolean e) {		
		setReadable();
	}
	
	@Override
	public void setEditor(JComponent editor) {
		super.setEditor(editor);
		
		setReadable();
	}
	
	@Override
	public void setValue(Object value) {
		super.setValue(value);
		
		setReadable();
	}
}
