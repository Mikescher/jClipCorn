package de.jClipCorn.gui.frames.logFrame;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.log.CCLogChangedListener;
import de.jClipCorn.features.log.CCLogElement;
import de.jClipCorn.features.log.CCLogType;

public class LogListModel extends DefaultListModel<String> implements ListSelectionListener, CCLogChangedListener {
	private static final long serialVersionUID = -3406456293654719505L;

	private CCLogType type;
	private JTextArea info;
	private JList<String> list;
	
	public LogListModel(JList<String> lst, CCLogType type, JTextArea info) {
		this.type = type;
		this.info = info;
		this.list = lst;
		
		lst.addListSelectionListener(this);
		CCLog.addChangeListener(this);
	}
	
	@Override
	public String getElementAt(int i) {
		return CCLog.getElement(type, i).getFormatted(CCLogElement.FORMAT_LEVEL_SHORT);
	}
	
	@Override
	public int getSize() {
		return CCLog.getCount(type);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		info.setText(CCLog.getElement(type, list.getSelectedIndex()).getFormatted(CCLogElement.FORMAT_LEVEL_LIST_FULL));
		info.setCaretPosition(0); // Scroll dat bitch to top 
	}

	@Override
	public void onChanged() {
		fireContentsChanged(this, 0, getSize());
	}
}
