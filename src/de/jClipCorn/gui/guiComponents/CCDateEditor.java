package de.jClipCorn.gui.guiComponents;

import java.beans.PropertyChangeEvent;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;

import de.jClipCorn.util.CCDate;

public class CCDateEditor extends JSpinner.DefaultEditor {
	private static final long serialVersionUID = -7568423030107551542L;

	private JSpinner owner;

	public CCDateEditor(JSpinner owner) {
		super(owner);

		this.owner = owner;

		update();

		getTextField().setEditable(true);
	}

	public void update() {
		if (owner.getModel().getValue() instanceof CCDate) {
			CCDate date = (CCDate) owner.getModel().getValue();

			getTextField().setText(date.getSimpleStringRepresentation());
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		update();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		commitEdit();
	}

	@Override
	public void commitEdit() {
		if (owner != null && owner.getModel() !=null && owner.getModel() instanceof SpinnerCCDateModel) {
			SpinnerCCDateModel model = (SpinnerCCDateModel) owner.getModel();
			CCDate d = new CCDate();
			if (d.parse(getTextField().getText(), CCDate.STRINGREP_SIMPLE)) {
				model.setValue(d);
			} else {
				update();
			}
		}
	}
}
