package de.jClipCorn.gui.guiComponents.jCCDateSpinner;

import javax.swing.AbstractSpinnerModel;

import de.jClipCorn.util.CCDate;

public class SpinnerCCDateModel extends AbstractSpinnerModel {
	private static final long serialVersionUID = 1275113021121391715L;
	
	private JCCDateSpinner owner;
	
	private CCDate current;
	private CCDate max;
	private CCDate min;
	
	public SpinnerCCDateModel(CCDate current, CCDate min, CCDate max) {
		this.current = current;
		this.min = min;
		this.max = max;
	}

	@Override
	public CCDate getNextValue() {
		if (owner != null) ((CCDateEditor)owner.getEditor()).commitEdit();
		
		if (max == null || current.isLessThan(max)) {
			return current.getAddDay(1);
		}
		
		return current;
	}

	@Override
	public CCDate getPreviousValue() {
		if (owner != null) ((CCDateEditor)owner.getEditor()).commitEdit();
		
		if (min == null || current.isGreaterThan(min)) {
			return current.getSubDay(1);
		}
		
		return current;
	}

	@Override
	public CCDate getValue() {
		return current;
	}

	@Override
	public void setValue(Object o) {
		if (isValidDate((CCDate) o) && ! current.equals(o)) {
			current = (CCDate) o;
			
			fireStateChanged();
		}
	}

	private boolean isValidDate(CCDate d) {
		if (max == null) {
			if (min == null) {
				return true;
			} else {
				return d.equals(min) || d.isGreaterThan(min);
			}
		} else {
			if (min == null) {
				return d.equals(max) || d.isLessThan(max);
			} else {
				return d.equals(max) || d.equals(min) || (d.isGreaterThan(min) && d.isLessThan(max));
			}
		}
	}

	public void setOwner(JCCDateSpinner powner) {
		this.owner = powner;
	}
}
