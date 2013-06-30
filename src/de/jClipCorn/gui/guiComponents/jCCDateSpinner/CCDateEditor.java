package de.jClipCorn.gui.guiComponents.jCCDateSpinner;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jClipCorn.util.CCDate;

public class CCDateEditor extends JPanel implements ChangeListener, PropertyChangeListener {
	private static final long serialVersionUID = -7568423030107551542L;

	private JCCDateSpinner owner;
	
	private JTextField tf;

	public CCDateEditor(JCCDateSpinner owner) {
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
		CCDate date = owner.getModel().getValue();

		getTextField().setText(date.getSimpleStringRepresentation());
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
			String text = getTextField().getText();
			
			if (CCDate.testparse(text, CCDate.STRINGREP_SIMPLE)) {
				owner.getModel().setValue(CCDate.parse(text, CCDate.STRINGREP_SIMPLE));
			} else if (CCDate.testparse(text, CCDate.STRINGREP_SIMPLESHORT)) {
				owner.getModel().setValue(CCDate.parse(text, CCDate.STRINGREP_SIMPLESHORT));
			} else {
				update();
			}
		}
	}
}
