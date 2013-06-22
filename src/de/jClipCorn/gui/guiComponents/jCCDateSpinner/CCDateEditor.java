package de.jClipCorn.gui.guiComponents.jCCDateSpinner;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DefaultFormatter;

import de.jClipCorn.util.CCDate;

public class CCDateEditor extends JSpinner.DefaultEditor implements FocusListener, MouseListener {
	private static final long serialVersionUID = -7568423030107551542L;

	private JCCDateSpinner owner;

	public CCDateEditor(JCCDateSpinner owner) {
		super(owner);

		this.owner = owner;

		update();
		
		getTextField().setEditable(true);
		getTextField().addFocusListener(this);
		getTextField().addMouseListener(this);
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

	@Override
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

	@Override
	public void focusGained(FocusEvent e) {
		DefaultFormatter fm = new DefaultFormatter();
		fm.setOverwriteMode(false);
		fm.install(getTextField());
	}

	@Override
	public void focusLost(FocusEvent e) {
		//not code
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//no code
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//no code
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//no code
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		SwingUtilities.invokeLater(new Runnable() { // invoke later is important for delay
            @Override
			public void run() {
                JTextField tf = (JTextField)e.getSource();
                int offset = tf.viewToModel(e.getPoint());
                tf.setCaretPosition(offset);
            }
        });
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//no code
	}
}
