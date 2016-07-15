package de.jClipCorn.gui.guiComponents.dateTimeListEditor;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner.JCCDateTimeSpinner;
import de.jClipCorn.gui.guiComponents.jCCTimeSpinner.JCCTimeSpinner;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCDatespan;

import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.Color;

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
	
	public DateTimeListEditor() {
		initGUI();
		
		listModel = new DefaultListModel<>();
		list.setModel(listModel);
		listModel.addElement(CCDateTime.getCurrentDateTime());
		listModel.addElement(CCDateTime.getCurrentDateTime());
		listModel.addElement(CCDateTime.getCurrentDateTime());
		listModel.addElement(CCDateTime.getCurrentDateTime());
	}
	
	private void initGUI() {
		this.setLayout(new BorderLayout());
		
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panel.add(panel_1, BorderLayout.WEST);
		
		btnNewButton = new JButton("+ Now");
		panel_1.add(btnNewButton);
		
		panel_3 = new JPanel();
		panel_3.setOpaque(false);
		panel.add(panel_3, BorderLayout.EAST);
		
		btnRemove = new JButton("Remove");
		panel_3.add(btnRemove);
		
		panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(panel_2, BorderLayout.NORTH);
		
		edAddDateTime = new JCCDateTimeSpinner();
		panel_2.add(edAddDateTime);
		
		btnAdd = new JButton("Add");
		panel_2.add(btnAdd);
		
		list = new JList();
		add(list, BorderLayout.CENTER);
	}
}
