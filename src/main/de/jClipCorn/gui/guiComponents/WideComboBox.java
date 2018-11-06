package de.jClipCorn.gui.guiComponents;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class WideComboBox<T> extends JComboBox<T> {
	private static final long serialVersionUID = -8525448509556253183L;

	public WideComboBox() {
		//
	}

	public WideComboBox(final T items[]) {
		super(items);
	}

	public WideComboBox(Vector<T> items) {
		super(items);
	}

	public WideComboBox(ComboBoxModel<T> aModel) {
		super(aModel);
	}

	private boolean layingOut = false;

	@Override
	public void doLayout() {
		try {
			layingOut = true;
			super.doLayout();
		} finally {
			layingOut = false;
		}
	}

	@Override
	public Dimension getSize() {
		Dimension dim = super.getSize();
		if (!layingOut)
			dim.width = 175;
		return dim;
	}
}