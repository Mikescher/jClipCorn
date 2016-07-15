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
		tf.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		setLayout(new BorderLayout());
		add(tf, BorderLayout.CENTER);

		uptime();
	}
	
	public JTextField getTextField() {
		return tf;
	}

	public void uptime() {
		CCDateTime datetime = owner.getModel().getValue();

		getTextField().setText(datetime.getSQLStringRepresentation());
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
			
			if (CCDateTime.testparse(text, CCDateTime.STRINGREP_SIMPLE)) {
				owner.getModel().setValue(CCDateTime.parse(text, CCDateTime.STRINGREP_SIMPLE));
			} else if (CCDateTime.testparse(text, CCDateTime.STRINGREP_SIMPLESHORT)) {
				owner.getModel().setValue(CCDateTime.parse(text, CCDateTime.STRINGREP_SIMPLESHORT));
			} else {
				uptime();
			}
		}
	}
}
