package de.jClipCorn.gui.guiComponents.jCCDateSpinner;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import de.jClipCorn.util.CCDate;

public class JCCDateSpinner extends JSpinner {
	private static final long serialVersionUID = -7562943010812302334L;

	public JCCDateSpinner(CCDate current, CCDate min, CCDate max) {
		super(new SpinnerCCDateModel(current, min, max));
		super.setEditor(new CCDateEditor(this));
	}

	@Override
	public SpinnerCCDateModel getModel() {
		return (SpinnerCCDateModel) super.getModel();
	}

	@Override
	public CCDate getValue() {
		if (getEditor() != null && getEditor() instanceof CCDateEditor) {
			((CCDateEditor) getEditor()).commitEdit();
		}

		return getModel().getValue();
	}

	@Override
	public void setModel(SpinnerModel m) {
		// Do nothing
	}

	@Override
	public void setEditor(JComponent Editor) {
		// Do nothing
	}

	@Override
	public JComponent getEditor() {
		return super.getEditor();
	}
}
