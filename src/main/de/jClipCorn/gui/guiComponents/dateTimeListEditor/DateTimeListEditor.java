package de.jClipCorn.gui.guiComponents.dateTimeListEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner.JCCDateTimeSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDateTime;

public class DateTimeListEditor extends JPanel {
	private static final long serialVersionUID = -1991426029921952573L;
	private JPanel panel;
	private JButton btnAdd;
	private JButton btnNewButton;
	private JPanel panel_2;
	private JCCDateTimeSpinner edAddDateTime;
	private JPanel panel_3;
	private JButton btnRemove;
	private JList<CCDateTime> list;
	private JPanel panel_1;
	private DefaultListModel<CCDateTime> listModel;
	private JLabel lblCount;
	private JPanel panel_4;
	private JPanel panel_5;
	private JScrollPane scrollPane;

	public DateTimeListEditor() {
		initGUI();
		
		resortList();
	}
	
	public DateTimeListEditor(CCDateTimeList lst) {
		initGUI();
		
		for (CCDateTime dt : lst) {
			listModel.addElement(dt);
		}
		
		resortList();
	}
	
	private void initGUI() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		this.setLayout(new BorderLayout());
		
		panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panel.add(panel_1, BorderLayout.WEST);
		
		btnNewButton = new JButton(LocaleBundle.getString("DateTimeListEditor.Now")); //$NON-NLS-1$
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.addElement(CCDateTime.getCurrentDateTime());
				
				resortList();
			}
		});
		panel_1.add(btnNewButton);
		
		panel_3 = new JPanel();
		panel_3.setOpaque(false);
		panel.add(panel_3, BorderLayout.EAST);
		
		btnRemove = new JButton(LocaleBundle.getString("DateTimeListEditor.Remove")); //$NON-NLS-1$
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.getSelectedIndex() >= 0) {
					listModel.removeElement(list.getSelectedValue());
					
					resortList();
				}
			}
		});
		panel_3.add(btnRemove);
		
		panel_2 = new JPanel();
		add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		panel_4 = new JPanel();
		panel_4.setBorder(new EmptyBorder(4, 4, 4, 4));
		panel_2.add(panel_4, BorderLayout.WEST);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		lblCount = new JLabel("??"); //$NON-NLS-1$
		panel_4.add(lblCount);
		lblCount.setHorizontalAlignment(SwingConstants.CENTER);
		
		panel_5 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel_2.add(panel_5);
		
		edAddDateTime = new JCCDateTimeSpinner();
		panel_5.add(edAddDateTime);
		
		btnAdd = new JButton("+"); //$NON-NLS-1$
		btnAdd.setMargin(new java.awt.Insets(1, 2, 1, 2));
		panel_5.add(btnAdd);
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.addElement(edAddDateTime.getValue());
				
				resortList();
			}
		});
		listModel = new DefaultListModel<>();
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		
		list = new JList<>();
		scrollPane.setViewportView(list);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedIndex() >= 0) {
					if (list.getSelectedValue().isUnspecifiedDateTime())
						edAddDateTime.setValue(CCDateTime.getCurrentDateTime());
					else
						edAddDateTime.setValue(list.getSelectedValue());
				}
			}
		});
		list.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		list.setModel(listModel);
		list.setCellRenderer(new DTLEListRenderer());
	}
	
	private void resortList() {
	    HashSet<CCDateTime> hash = new HashSet<>();
	    for (int i = 0; i < listModel.size(); i++) {
	    	hash.add(listModel.get(i));
	    }
	    
	    List<CCDateTime> list = new ArrayList<>(hash);
	    Collections.sort(list);
	    
	    listModel.removeAllElements();
	    for (CCDateTime s : list) {
	    	listModel.addElement(s);
	    }
	    
	    lblCount.setText(Integer.toString(listModel.size()));
	}
	
	public void setValue(CCDateTimeList dtlist) {
		listModel.clear();
		
		for (CCDateTime dt : dtlist) {
			listModel.addElement(dt);
		}
		
		resortList();
		
		if (listModel.isEmpty()) {
			list.setSelectedIndex(-1);
		} else {
			list.setSelectedIndex(0);
		}
	}
	
	public CCDateTimeList getValue() {
	    List<CCDateTime> list = new ArrayList<>();
	    for (int i = 0; i < listModel.size(); i++) {
	        list.add(listModel.get(i));
	    }
	    
		return CCDateTimeList.create(list);
	}
}
