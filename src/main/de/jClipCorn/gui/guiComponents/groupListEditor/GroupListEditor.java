package de.jClipCorn.gui.guiComponents.groupListEditor;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.guiComponents.tags.TagsChangedListener;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GroupListEditor extends JPanel
{
	private static final long serialVersionUID = -2532456454735632414L;

	private final CCMovieList db;
	
	private CCGroupList value = CCGroupList.EMPTY;
	
	private JTextField edEditor;
	private JButton btnDropDown;

	@DesignCreate
	private static GroupListEditor designCreate()
	{
		return new GroupListEditor(null);
	}

	public GroupListEditor(CCMovieList movielist)
	{
		super();
		
		db = movielist;
		
		initGUI();
		
		updateEditor();
	}
		
	private void initGUI()
	{
		//setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		setLayout(new BorderLayout(0, 0));
		
		edEditor = new JTextField();
		edEditor.setEnabled(false);
		add(edEditor, BorderLayout.CENTER);
		edEditor.setColumns(10);
		if (LookAndFeelManager.isWindows()) edEditor.setBackground(Color.WHITE);
		
		btnDropDown = new JButton("V"); //$NON-NLS-1$
		btnDropDown.addActionListener(e ->
		{
			GroupListPopup popup = new GroupListPopup(db, value, GroupListEditor.this);
			popup.setVisible(true);
		});
		btnDropDown.setFocusable(false);
		btnDropDown.setFocusTraversalKeysEnabled(false);
		btnDropDown.setFocusPainted(false);
		btnDropDown.setMargin(new java.awt.Insets(1, 2, 1, 2));
		add(btnDropDown, BorderLayout.EAST);
	}

	@Override
	public void setEnabled(boolean b)
	{
		edEditor.setEnabled(b);
		btnDropDown.setEnabled(b);
		super.setEnabled(b);
	}

	public void addGroupListChangedListener(final GroupListChangedListener l) {
		listenerList.add(GroupListChangedListener.class, l);
	}
	public void removeGroupListChangedListener(final GroupListChangedListener l) {
		listenerList.add(GroupListChangedListener.class, l);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(80, 20);
	}

	private void updateEditor()
	{
		if (value.isEmpty()) {
			edEditor.setText(LocaleBundle.getString("GroupListEditor.summary_none")); //$NON-NLS-1$
		} else if (value.count() == 1) {
			edEditor.setText(value.get(0).Name);
		} else {
			edEditor.setText(LocaleBundle.getFormattedString("GroupListEditor.summary_many", value.count())); //$NON-NLS-1$
		}
	}
	
	public void setValue(CCGroupList gl)
	{
		value = gl;
		
		updateEditor();

		for (var ac : listenerList.getListeners(TagsChangedListener.class)) ac.tagsChanged(new ActionEvent(this, -1, Str.Empty));
	}
	
	public CCGroupList getValue() {
		return value;
	}
}
