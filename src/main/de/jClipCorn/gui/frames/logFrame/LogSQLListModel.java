package de.jClipCorn.gui.frames.logFrame;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.log.CCLogChangedListener;

public class LogSQLListModel extends DefaultListModel<String> implements ListSelectionListener, CCLogChangedListener {
	private static final long serialVersionUID = -3406456293654719505L;

	private JTextArea info;
	private JList<String> list;
	
	public LogSQLListModel(JList<String> lst, JTextArea info) {
		this.info = info;
		this.list = lst;
		
		lst.addListSelectionListener(this);
		CCLog.addChangeListener(this);
	}
	
	@Override
	public String getElementAt(int i) {
		return CCLog.getSQLElement(i).getFormatted();
	}
	
	@Override
	public int getSize() {
		return CCLog.getSQLCount();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		info.setText(CCLog.getSQLElement(list.getSelectedIndex()).getFullFormatted());
		info.setCaretPosition(0); // Scroll dat bitch to top 
	}

	@Override
	public void onChanged() {
		fireContentsChanged(this, 0, getSize());
	}
}
