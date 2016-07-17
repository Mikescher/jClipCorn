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

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.exceptions.CCFormatException;

public class CCTimeEditor extends JPanel implements ChangeListener, PropertyChangeListener {
	private static final long serialVersionUID = -7568423030107551542L;

	private JCCTimeSpinner owner;
	
	private JTextField tf;

	public CCTimeEditor(JCCTimeSpinner owner) {
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
		CCTime time = owner.getModel().getValue();

		getTextField().setText(time.getSimpleStringRepresentation());
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

			try {
				if (CCTime.testparse(text, CCTime.STRINGREP_SIMPLE)) {
					owner.getModel().setValue(CCTime.parse(text, CCTime.STRINGREP_SIMPLE));
				} else if (CCTime.testparse(text, CCTime.STRINGREP_SHORT)) {
					owner.getModel().setValue(CCTime.parse(text, CCTime.STRINGREP_SHORT));
				} else {
					uptime();
				}
			} catch (CCFormatException e) {
				CCLog.addError(e);
			}
		}
	}
}
