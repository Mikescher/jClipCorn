package de.jClipCorn.gui.guiComponents.jCCTimeSpinner;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import de.jClipCorn.util.datetime.CCTime;

public class JCCTimeSpinner extends JSpinner {
	private static final long serialVersionUID = -7562943010812302334L;

	public JCCTimeSpinner(CCTime current) {
		super(new SpinnerCCTimeModel(current));
		getModel().setOwner(this);
		
		CCTimeEditor de = new CCTimeEditor(this);
		super.setEditor(de);
		addChangeListener(de);
		addPropertyChangeListener(de);
	}

	public JCCTimeSpinner() {
		this(CCTime.getCurrentTime());
	}

	@Override
	public SpinnerCCTimeModel getModel() {
		return (SpinnerCCTimeModel) super.getModel();
	}

	@Override
	public CCTime getValue() {
		if (getEditor() != null && getEditor() instanceof CCTimeEditor) {
			((CCTimeEditor) getEditor()).commitEdit();
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
