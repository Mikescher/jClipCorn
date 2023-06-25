package de.jClipCorn.gui.guiComponents.jCCTimeSpinner;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jClipCorn.util.datetime.CCTime;

public class CCTimeEditor extends JPanel implements ChangeListener, PropertyChangeListener {
	private static final long serialVersionUID = -7568423030107551542L;

	private JCCTimeSpinner owner;
	
	private JTextField tf;

	public CCTimeEditor(JCCTimeSpinner owner) {
		this.owner = owner;
		tf = new JFormattedTextField();
		tf.setHorizontalAlignment(JTextField.LEFT);
		tf.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 6, 0, 6));
		
		setLayout(new BorderLayout());
		add(tf, BorderLayout.CENTER);

		uptime();
	}
	
	public JTextField getTextField() {
		return tf;
	}

	public void uptime() {
		CCTime time = owner.getModel().getValue();

		getTextField().setText(time.toStringInput());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		uptime();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		commitEdit();
	}

	public void commitEdit() {
		if (owner != null && owner.getModel() != null) {
			String text = getTextField().getText();

			CCTime pTime = CCTime.parseInputOrNull(text);
			if (pTime != null) {
				owner.getModel().setValue(pTime);
			} else {
				uptime();
			}
		}
	}
}
