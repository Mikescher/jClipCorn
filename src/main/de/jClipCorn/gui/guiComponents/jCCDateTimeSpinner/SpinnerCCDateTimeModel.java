package de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;

public class SpinnerCCDateTimeModel extends AbstractSpinnerModel {
	private static final long serialVersionUID = 1275113021121391715L;
	
	private JCCDateTimeSpinner owner;
	
	private CCDateTime current;
	
	public SpinnerCCDateTimeModel(CCDateTime current) {
		this.current = current;
	}

	@Override
	public CCDateTime getNextValue() {
		char spec = owner.getSelectedSpecifier();
		
		if (owner != null) ((CCDateTimeEditor)owner.getEditor()).commitEdit();
		
		switch (spec) {
		case 'y':
			return current.getSpecifyTimeIfNeeded(CCTime.getMidnight()).getAddYear(1);
		case 'M':
			return current.getSpecifyTimeIfNeeded(CCTime.getMidnight()).getAddMonth(1);
		case 'd':
			return current.getSpecifyTimeIfNeeded(CCTime.getMidnight()).getAddDay(1);
		case 'H':
			return current.getSpecifyTimeIfNeeded(CCTime.getMidnight()).getAddHour(1);
		case 'm':
			return current.getSpecifyTimeIfNeeded(CCTime.getMidnight()).getAddMinute(1);
		case 's':
			return current.getSpecifyTimeIfNeeded(CCTime.getMidnight()).getAddSecond(1);
		default:
			return current.getSpecifyTimeIfNeeded(CCTime.getMidnight()).getAddDay(1);
		}
		
	}

	@Override
	public CCDateTime getPreviousValue() {
		char spec = owner.getSelectedSpecifier();
		
		if (owner != null) ((CCDateTimeEditor)owner.getEditor()).commitEdit();
		
		switch (spec) {
		case 'y':
			return current.getSubYear(1);
		case 'M':
			return current.getSubMonth(1);
		case 'd':
			return current.getSubDay(1);
		case 'H':
			return current.getSubHour(1);
		case 'm':
			return current.getSubMinute(1);
		case 's':
			return current.getSubSecond(1);
		default:
			return current.getSubDay(1);
		}
	}

	@Override
	public CCDateTime getValue() {
		return current;
	}

	@Override
	public void setValue(Object o) {
		if (! current.equals(o)) {

			int caret = 0;
			if (owner != null) caret = ((CCDateTimeEditor)owner.getEditor()).getTextField().getCaretPosition();
			
			current = (CCDateTime) o;
			
			fireStateChanged();

			if (owner != null)  {
				JTextField tf = ((CCDateTimeEditor)owner.getEditor()).getTextField();
				
				Document doc = tf.getDocument();
		        if (doc != null && caret > doc.getLength()) {
		            caret = doc.getLength();
		        }
		        
				tf.setCaretPosition(caret);
			}
		}
	}

	public void setOwner(JCCDateTimeSpinner powner) {
		this.owner = powner;
	}
}
