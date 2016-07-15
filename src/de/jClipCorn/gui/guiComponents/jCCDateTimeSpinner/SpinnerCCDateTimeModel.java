package de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner;

import javax.swing.AbstractSpinnerModel;

import de.jClipCorn.util.datetime.CCDateTime;

public class SpinnerCCDateTimeModel extends AbstractSpinnerModel {
	private static final long serialVersionUID = 1275113021121391715L;
	
	private JCCDateTimeSpinner owner;
	
	private CCDateTime current;
	
	public SpinnerCCDateTimeModel(CCDateTime current) {
		this.current = current;
	}

	@Override
	public CCDateTime getNextValue() {
		if (owner != null) ((CCDateTimeEditor)owner.getEditor()).commitEdit();

		return current.getAddDay(1);
	}

	@Override
	public CCDateTime getPreviousValue() {
		if (owner != null) ((CCDateTimeEditor)owner.getEditor()).commitEdit();

		return current.getSubDay(1);
	}

	@Override
	public CCDateTime getValue() {
		return current;
	}

	@Override
	public void setValue(Object o) {
		if (! current.equals(o)) {
			current = (CCDateTime) o;
			
			fireStateChanged();
		}
	}

	public void setOwner(JCCDateTimeSpinner powner) {
		this.owner = powner;
	}
}
