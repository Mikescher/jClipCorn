package de.jClipCorn.gui.frames.logFrame;

import de.jClipCorn.features.log.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LogListModel extends DefaultListModel<String> implements ListSelectionListener, CCLogChangedListener {
	private static final long serialVersionUID = -3406456293654719505L;

	private final CCLogType type;
	private final JTextArea extraText;
	private final JTextArea extraTrace;
	private final JList<String> list;
	
	public LogListModel(JList<String> lst, CCLogType type, JTextArea extraText, JTextArea extraTrace) {
		this.type       = type;
		this.extraText  = extraText;
		this.extraTrace = extraTrace;
		this.list       = lst;
		
		lst.addListSelectionListener(this);
	}
	
	@Override
	public String getElementAt(int i) {
		var cle = CCLog.getElement(type, i);
		return cle.getTypeStringRepresentation() + ": " + cle.getFirstLine() + "\n";
	}
	
	@Override
	public int getSize() {
		return CCLog.getCount(type);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		var cle = CCLog.getElement(type, list.getSelectedIndex());

		extraText.setText(cle.getTypeStringRepresentation() + " (" + cle.getTime().toStringUINormal() + "): " + cle.getText());
		extraText.setCaretPosition(0); // Scroll dat bitch to top

		extraTrace.setText(cle.getFormattedStackTrace(false));
		extraTrace.setCaretPosition(0); // Scroll dat bitch to top
	}

	@Override
	public void onChanged() {
		fireContentsChanged(this, 0, getSize());
	}

	@Override
	public void onSQLChanged(CCSQLLogElement cle) {
		//
	}

	@Override
	public void onPropsChanged(CCChangeLogElement cle) {
		//
	}
}
