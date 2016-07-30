package de.jClipCorn.gui.guiComponents.jCCTimeSpinner;

import javax.swing.AbstractSpinnerModel;

import de.jClipCorn.util.datetime.CCTime;

public class SpinnerCCTimeModel extends AbstractSpinnerModel {
	private static final long serialVersionUID = 1275113021121391715L;
	
	private JCCTimeSpinner owner;
	
	private CCTime current;
	
	public SpinnerCCTimeModel(CCTime current) {
		this.current = current;
	}

	@Override
	public CCTime getNextValue() {
		if (owner != null) ((CCTimeEditor)owner.getEditor()).commitEdit();

		return current.getAddMinute(1).getSetSecond(0);
	}

	@Override
	public CCTime getPreviousValue() {
		if (owner != null) ((CCTimeEditor)owner.getEditor()).commitEdit();

		return current.getSubMinute(1).getSetSecond(0);
	}

	@Override
	public CCTime getValue() {
		return current;
	}

	@Override
	public void setValue(Object o) {
		if (! current.equals(o)) {
			current = (CCTime) o;
			
			fireStateChanged();
		}
	}

	public void setOwner(JCCTimeSpinner powner) {
		this.owner = powner;
	}
}
