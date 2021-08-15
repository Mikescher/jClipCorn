package de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner;

import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.InternationalDateTimeFormatHelper;

import javax.swing.*;
import java.util.HashSet;

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
	
	public char getSelectedSpecifier() {
		if (getEditor() != null && getEditor() instanceof CCDateTimeEditor) {
			CCDateTimeEditor ed = (CCDateTimeEditor) getEditor();
			
			int caret = ed.getTextField().getCaretPosition();
			
			HashSet<Character> specs = CCDateTime.STATIC_SUPPLIER.getAllStringSpecifier();
			String rep = InternationalDateTimeFormatHelper.FORMAT_DATETIME_NORMAL.get(InternationalDateTimeFormatHelper.getCurrentFormat());
			
			int rd = 999999;
			char rc = ' ';
			
			for (int i = 0; i < rep.length(); i++) {
				if (specs.contains(rep.charAt(i))) {
					int nd = Math.abs(i - caret);
					if (nd < rd) {
						rd = nd;
						rc = rep.charAt(i);
					}
				}
			}
			
			return rc;
		}
		
		return ' ';
	}
}
