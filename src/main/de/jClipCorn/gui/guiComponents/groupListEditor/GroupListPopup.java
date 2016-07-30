package de.jClipCorn.gui.guiComponents.groupListEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;

public class GroupListPopup extends JDialog implements WindowFocusListener {
	private static final long serialVersionUID = 5984142965004380779L;
	
	private static GroupListPopup instance = null;
	
	private Map<CCGroup, JCheckBox> uiMap = new HashMap<>();
	
	private GroupListEditor parent;
	private JScrollPane pnlScroll;
	private JPanel pnlContent;
	private JPanel pnllTop;
	private JTextField edNewGroup;
	private JButton btnAdd;
	private JPanel pnlBase;

	public GroupListPopup(CCMovieList db, CCGroupList initial, GroupListEditor parent) {
		super();
		
		if (instance != null) {
			instance.dispose();
		}
		instance = this;
		
		this.parent = parent;
				
		setAlwaysOnTop(true);
		initGUI(parent);
		initData(db, initial);
		
		updateHeight();
		
		setLocation((int) parent.getLocationOnScreen().getX(), (int) (parent.getLocationOnScreen().getY() + parent.getSize().getHeight() + 2));
		
		addWindowFocusListener(this);
	}

	private void initGUI(GroupListEditor parent) { 
		setSize(Math.max(200, parent.getWidth()), 25);
		
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setType(Type.POPUP);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		
		pnlBase = new JPanel();
		pnlBase.setBorder(null);
		getContentPane().add(pnlBase, BorderLayout.SOUTH);
		pnlBase.setLayout(new BorderLayout(0, 0));
		
		pnllTop = new JPanel();
		pnlBase.add(pnllTop, BorderLayout.NORTH);
		pnllTop.setLayout(new BorderLayout(0, 0));
		
		edNewGroup = new JTextField();
		edNewGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (edNewGroup.getText().trim().isEmpty()) {
					disposeInstance();
				} else {
					onAdd();
				}
			}
		});
		pnllTop.add(edNewGroup, BorderLayout.CENTER);
		edNewGroup.setColumns(10);
		
		btnAdd = new JButton("+"); //$NON-NLS-1$
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAdd();
			}
		});
		btnAdd.setMargin(new java.awt.Insets(1, 2, 1, 2));
		pnllTop.add(btnAdd, BorderLayout.EAST);
		
		pnlScroll = new JScrollPane();
		pnlScroll.getVerticalScrollBar().setUnitIncrement(16);
		pnlBase.add(pnlScroll);
		
		pnlContent = new JPanel();
		pnlContent.setBackground(Color.WHITE);
		pnlScroll.setViewportView(pnlContent);
		pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
	}
	
	private void initData(CCMovieList db, CCGroupList initial) {
		List<CCGroup> gl = db.getGroupList();
		for (CCGroup ccGroup : initial) {
			if (! gl.contains(ccGroup)) gl.add(ccGroup);
		}
		Collections.sort(gl);
		
		for (CCGroup group : gl) {
			addCCGroupCheckBox(group, initial.contains(group));
		}

		updateHeight();
	}

	public void addCCGroupCheckBox(CCGroup group, boolean checked) {
		JCheckBox dynBox = new JCheckBox(group.Name);
		dynBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueChanged();
			}
		});
		dynBox.setBackground(Color.WHITE);
		dynBox.setSize(pnlContent.getWidth(), (int) dynBox.getPreferredSize().getHeight());
		
		dynBox.setSelected(checked);
		pnlContent.add(dynBox);

		uiMap.put(group, dynBox);
	}

	public void updateHeight() {
		pack();
		
		if (uiMap.isEmpty()) {
			pnlScroll.setVisible(false);
		} else {
			pnlScroll.setVisible(true);
		}
				
		int contentHeight = pnlContent.getHeight();
		
		int width = Math.max(200, parent.getWidth());
		int height = pnllTop.getHeight() + contentHeight;
		
		if (height > 250) height = 250;
		if (height < 24) height = 24;
		
		int scrollHeight = 2 + height - pnllTop.getHeight();
		
		if (contentHeight > scrollHeight) {
			pnlScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		} else {
			pnlScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
		
		pnlScroll.setPreferredSize(new Dimension(width, scrollHeight));
		
		setSize(width, height);

		// you guessed right - I _hate_ swing with a burning passion...
		invalidate();
		revalidate();
		repaint();
		pnlScroll.revalidate();
		pnlScroll.repaint();
		pnlContent.revalidate();
		pnlContent.repaint();
		pnlBase.revalidate();
		pnlBase.repaint();
		pnlScroll.revalidate();
		pnlScroll.repaint();
		revalidate();
		repaint();
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		// everything ok
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		dispose();
	}

	private void onAdd() {
		if (! CCGroup.isValidGroupName(edNewGroup.getText())) return;
		
		CCGroup g = CCGroup.create(edNewGroup.getText());
		if (uiMap.containsKey(g)) {
			uiMap.get(g).setSelected(true);
		} else {
			addCCGroupCheckBox(g, true);

			updateHeight();
		}
		
		edNewGroup.setText(""); //$NON-NLS-1$
		edNewGroup.requestFocus();
		
		valueChanged();
	}
	
	private void valueChanged() {
		List<CCGroup> g = new ArrayList<>();
		
		for (Entry<CCGroup, JCheckBox> entry : uiMap.entrySet()) {
			if (entry.getValue().isSelected()) g.add(entry.getKey());
		}
		
		parent.setValue(CCGroupList.create(g));
	}

	public static void disposeInstance() {
		if (instance != null) {
			instance.dispose();
			instance = null;
		}
	}
}
