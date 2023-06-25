package de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jClipCorn.util.datetime.CCDateTime;

public class CCDateTimeEditor extends JPanel implements ChangeListener, PropertyChangeListener {
	private static final long serialVersionUID = -7568423030107551542L;

	private JCCDateTimeSpinner owner;
	
	private JTextField tf;

	public CCDateTimeEditor(JCCDateTimeSpinner owner) {
		this.owner = owner;
		tf = new JFormattedTextField();
		tf.setHorizontalAlignment(JTextField.LEFT);
		tf.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 6));
		
		setLayout(new BorderLayout());
		add(tf, BorderLayout.CENTER);

		update();
	}
	
	public JTextField getTextField() {
		return tf;
	}

	public void update() {
		CCDateTime datetime = owner.getModel().getValue();

		getTextField().setText(datetime.toStringInput());
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
		if (owner != null && owner.getModel() != null) {
			JTextField tf = getTextField();
			int caret = tf.getCaretPosition();
			String text = tf.getText();

			CCDateTime pDateTime = CCDateTime.parseInputOrNull(text);
			if (pDateTime != null) {
				owner.getModel().setValue(pDateTime);
			} else {
				update();
			}
			
			if (caret > tf.getText().length()) caret = tf.getText().length();

			getTextField().setCaretPosition(caret);
		}
	}
}
