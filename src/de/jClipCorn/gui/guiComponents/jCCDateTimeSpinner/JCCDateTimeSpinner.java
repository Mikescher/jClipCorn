package de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import de.jClipCorn.util.datetime.CCDateTime;

public class JCCDateTimeSpinner extends JSpinner {
	private static final long serialVersionUID = -7562943010812302334L;

	public JCCDateTimeSpinner(CCDateTime current) {
		super(new SpinnerCCDateTimeModel(current));
		getModel().setOwner(this);
		
		CCDateTimeEditor de = new CCDateTimeEditor(this);
		super.setEditor(de);
		addChangeListener(de);
		addPropertyChangeListener(de);
	}

	public JCCDateTimeSpinner() {
		this(CCDateTime.getCurrentDateTime());
	}

	@Override
	public SpinnerCCDateTimeModel getModel() {
		return (SpinnerCCDateTimeModel) super.getModel();
	}

	@Override
	public CCDateTime getValue() {
		if (getEditor() != null && getEditor() instanceof CCDateTimeEditor) {
			((CCDateTimeEditor) getEditor()).commitEdit();
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
