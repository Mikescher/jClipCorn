package de.jClipCorn.gui.frames.editToolbarFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.frames.editToolbarFrame.toolbarElementsList.ToolbarElementsCellRenderer;
import de.jClipCorn.gui.frames.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.gui.guiComponents.ToolbarConfigPanel;
import de.jClipCorn.gui.guiComponents.dndList.DnDList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.property.CCToolbarProperty;

public class EditToolbarFrame extends JFrame {
	private static final long serialVersionUID = -7633657374117523902L;
	private JPanel contentPane;

	private JScrollPane scpnAll;
	private JScrollPane scpnChoosen;
	private JList<String> lbAll;
	private DefaultListModel<String> lmAll;
	private DnDList<String> lbChoosen;
	private DefaultListModel<String> lmChoosen;
	private JPanel pnlMain;
	private JPanel panel;
	private JButton btnOk;
	private JButton btnCancel;
	private JButton btnAdd;
	private JButton btnDelete;
	private JButton btnAddSep;
	
	private CCToolbarProperty property;
	private ToolbarConfigPanel display;
	private JPanel pnlRightBottom;
	private JPanel pnlLeft;
	private JPanel pnlLeftBottom;
	private JPanel pnlRight;
	
	public EditToolbarFrame(Component owner, ToolbarConfigPanel pnl, CCToolbarProperty property) {
		super();
		this.property = property;
		this.display = pnl;
		
		initGUI();
		setLocationRelativeTo(owner);
		initData();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("EditToolbarFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setType(Type.UTILITY);
		setMinimumSize(new Dimension(250, 200));
		setSize(new Dimension(550, 300));
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		pnlMain = new JPanel();
		contentPane.add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new GridLayout(1, 2, 5, 5));
		
		pnlLeft = new JPanel();
		pnlMain.add(pnlLeft);
		pnlLeft.setLayout(new BorderLayout(0, 4));
		
		scpnAll = new JScrollPane();
		scpnAll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pnlLeft.add(scpnAll);
		
		lbAll = new JList<>();
		lbAll.setVisibleRowCount(0);
		lbAll.setCellRenderer(new ToolbarElementsCellRenderer());
		scpnAll.setViewportView(lbAll);
		
		pnlLeftBottom = new JPanel();
		FlowLayout fl_pnlLeftBottom = (FlowLayout) pnlLeftBottom.getLayout();
		fl_pnlLeftBottom.setVgap(0);
		pnlLeft.add(pnlLeftBottom, BorderLayout.SOUTH);
		
		btnAdd = new JButton(">"); //$NON-NLS-1$
		pnlLeftBottom.add(btnAdd);
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lbAll.getSelectedValue() != null) {
					lmChoosen.addElement(lbAll.getSelectedValue());
				}
			}
		});
		
		pnlRight = new JPanel();
		pnlMain.add(pnlRight);
		pnlRight.setLayout(new BorderLayout(0, 4));
		
		scpnChoosen = new JScrollPane();
		scpnChoosen.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pnlRight.add(scpnChoosen);
		
		lbChoosen = new DnDList<>();
		lbChoosen.setVisibleRowCount(0);
		lbChoosen.setCellRenderer(new ToolbarElementsCellRenderer());
		scpnChoosen.setViewportView(lbChoosen);
		
		pnlRightBottom = new JPanel();
		pnlRight.add(pnlRightBottom, BorderLayout.SOUTH);
		FlowLayout flowLayout = (FlowLayout) pnlRightBottom.getLayout();
		flowLayout.setVgap(0);
		
		btnDelete = new JButton("<"); //$NON-NLS-1$
		pnlRightBottom.add(btnDelete);
		
		btnAddSep = new JButton("-"); //$NON-NLS-1$
		pnlRightBottom.add(btnAddSep);
		btnAddSep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lmChoosen.addElement(ClipToolbar.IDENT_SEPERATOR);
			}
		});
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lbChoosen.getSelectedValue() != null) {
					lmChoosen.remove(lbChoosen.getSelectedIndex());
				}
			}
		});
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnOK();
			}
		});
		panel.add(btnOk);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(btnCancel);
	}
	
	private void initData() {
		List<String> allelements = CCToolbarProperty.splitStringList(CCActionTree.getInstance().getCompleteToolbarConfig());
		
		lmAll = new DefaultListModel<>();
		
		for (int i = 0; i < allelements.size(); i++) {
			lmAll.addElement(allelements.get(i));
		}
		
		lbAll.setModel(lmAll);
		
		//######################################################################################################################
		
		List<String> propelements = property.getValueAsArray();
		
		lmChoosen = new DefaultListModel<>();
		
		for (int i = 0; i < propelements.size(); i++) {
			lmChoosen.addElement(propelements.get(i));
		}
		
		lbChoosen.setModel(lmChoosen);
	}
	
	private void onBtnOK() {
		StringBuilder value = new StringBuilder();
		
		for (int i = 0; i < lmChoosen.size(); i++) {
			value.append(lmChoosen.get(i));
			
			if (i + 1 < lmChoosen.size()) {
				value.append('|');
			}
		}

		display.setValue(value.toString());
		dispose();
	}
}
