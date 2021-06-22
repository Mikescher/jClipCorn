package de.jClipCorn.gui.guiComponents.jYearSpinner;

import javax.swing.*;

public class JYearSpinner extends JSpinner {

	public JYearSpinner() {
		super(new SpinnerNumberModel(1900, 1900, null, 1));

		var ed = new YearSpinnerEditor(this);
		super.setEditor(ed);
		addChangeListener(ed);
		addPropertyChangeListener(ed);
	}

	public Integer getValue() {
		return (int)getModel().getValue();
	}
}
