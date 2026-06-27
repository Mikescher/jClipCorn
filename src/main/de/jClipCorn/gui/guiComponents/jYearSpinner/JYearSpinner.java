package de.jClipCorn.gui.guiComponents.jYearSpinner;

import de.jClipCorn.util.datatypes.Opt;

import javax.swing.*;

public class JYearSpinner extends JSpinner {

	public JYearSpinner() {
		super(new SpinnerNumberModel(1900, 1900, null, 1));

		var ed = new YearSpinnerEditor(this);
		super.setEditor(ed);
		addChangeListener(ed);
		addPropertyChangeListener(ed);
	}

	/**
	 * @return the entered year, or {@link Opt#empty()} if the field has been cleared
	 */
	public Opt<Integer> getValueOpt() {
		return ((YearSpinnerEditor) getEditor()).getValueOpt();
	}

	/**
	 * Sets the year, or clears the field when {@code v} is empty.
	 */
	public void setValueOpt(Opt<Integer> v) {
		((YearSpinnerEditor) getEditor()).setValueOpt(v);
	}
}
