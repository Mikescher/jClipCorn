package de.jClipCorn.gui.guiComponents.jYearSpinner;

import de.jClipCorn.util.Str;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class YearSpinnerEditor extends JPanel implements ChangeListener, PropertyChangeListener
{
	private final JYearSpinner owner;

	private final JTextField tf;

	public boolean preventCommit = false;

	public YearSpinnerEditor(JYearSpinner owner) {
		this.owner = owner;
		tf = new JFormattedTextField();
		tf.setBorder(new EmptyBorder(0, 0, 0, 0));

		setLayout(new BorderLayout());
		add(tf, BorderLayout.CENTER);

		update();
	}

	public JTextField getTextField() {
		return tf;
	}

	public void update() {
		getTextField().setText(String.valueOf(owner.getModel().getValue()));
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		update();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		commitEdit();
	}

	public void commitEdit() {
		if (preventCommit) return;

		if (owner == null || owner.getModel() == null) return;

		String text = getTextField().getText();

		try
		{
			var v = Integer.parseInt(text.trim());
			owner.getModel().setValue(v);

			if (!Str.equals(String.valueOf(owner.getModel().getValue()), text)) update();
		}
		catch(Exception e)
		{
			update();
		}
	}
}
