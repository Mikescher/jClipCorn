package de.jClipCorn.gui.guiComponents.groupListEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.LookAndFeelManager;

public class GroupListEditor extends JPanel {
	private static final long serialVersionUID = -2532456454735632414L;

	private final CCMovieList db;
	
	private CCGroupList value = CCGroupList.EMPTY;
	
	private JTextField edEditor;
	private JButton btnDropDown;

	/**
	 * Create the panel.
	 */
	public GroupListEditor(CCMovieList movielist) {
		super();
		
		db = movielist;
		
		initGUI();
		
		updateEditor();
	}
		
	private void initGUI() {
		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		setLayout(new BorderLayout(0, 0));
		
		edEditor = new JTextField();
		edEditor.setEnabled(false);
		add(edEditor, BorderLayout.CENTER);
		edEditor.setColumns(10);
		if (LookAndFeelManager.isWindows()) edEditor.setBackground(Color.WHITE);
		
		btnDropDown = new JButton("V"); //$NON-NLS-1$
		btnDropDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GroupListPopup popup = new GroupListPopup(db, value, GroupListEditor.this);
				popup.setVisible(true);
			}
		});
		btnDropDown.setFocusable(false);
		btnDropDown.setFocusTraversalKeysEnabled(false);
		btnDropDown.setFocusPainted(false);
		btnDropDown.setMargin(new java.awt.Insets(1, 2, 1, 2));
		add(btnDropDown, BorderLayout.EAST);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(80, 20);
	}

	private void updateEditor() {
		if (value.isEmpty()) {
			edEditor.setText(LocaleBundle.getString("GroupListEditor.summary_none")); //$NON-NLS-1$
		} else if (value.count() == 1) {
			edEditor.setText(value.get(0).Name);
		} else {
			edEditor.setText(LocaleBundle.getFormattedString("GroupListEditor.summary_many", value.count())); //$NON-NLS-1$
		}
	}
	
	public void setValue(CCGroupList gl) {
		value = gl;
		
		updateEditor();
	}
	
	public CCGroupList getValue() {
		return value;
	}
}
