package de.jClipCorn.gui.guiComponents.jCCDateSpinner;

import java.beans.PropertyChangeEvent;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;

import de.jClipCorn.util.CCDate;

public class CCDateEditor extends JSpinner.DefaultEditor {
	private static final long serialVersionUID = -7568423030107551542L;

	private JCCDateSpinner owner;

	public CCDateEditor(JCCDateSpinner owner) {
		super(owner);

		this.owner = owner;

		update();
		getTextField().setEditable(true);
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
			CCDate d = new CCDate();
			if (d.parse(getTextField().getText(), CCDate.STRINGREP_SIMPLE) || d.parse(getTextField().getText(), CCDate.STRINGREP_SIMPLESHORT)) {
				owner.getModel().setValue(d);
			} else {
				update();
			}
		}
	}
}
