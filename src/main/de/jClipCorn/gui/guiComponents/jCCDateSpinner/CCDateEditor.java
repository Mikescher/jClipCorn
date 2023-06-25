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

import de.jClipCorn.util.datetime.CCDate;

public class CCDateEditor extends JPanel implements ChangeListener, PropertyChangeListener {
	private static final long serialVersionUID = -7568423030107551542L;

	private JCCDateSpinner owner;
	
	private JTextField tf;

	public boolean preventCommit = false;
	
	public CCDateEditor(JCCDateSpinner owner) {
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
		CCDate date = owner.getModel().getValue();

		getTextField().setText(date.toStringInput());
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
		
		if (owner != null && owner.getModel() != null) {
			String text = getTextField().getText();

			CCDate pDate = CCDate.parseInputOrNull(text);
			if (pDate != null) {
				owner.getModel().setValue(pDate);
			} else {
				update();
			}
		}
	}
}
