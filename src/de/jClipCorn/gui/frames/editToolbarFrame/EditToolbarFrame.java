package de.jClipCorn.gui.frames.editToolbarFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

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
	private JPanel pnlMid;
	private JButton btnAdd;
	private JButton btnDelete;
	private JButton btnAddSep;
	
	private CCToolbarProperty property;
	private ToolbarConfigPanel display;
	
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
		pnlMain.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("fill:min:grow"),})); //$NON-NLS-1$
		
		scpnAll = new JScrollPane();
		pnlMain.add(scpnAll, "2, 2"); //$NON-NLS-1$
		
		lbAll = new JList<>();
		lbAll.setVisibleRowCount(0);
		lbAll.setCellRenderer(new ToolbarElementsCellRenderer());
		scpnAll.setViewportView(lbAll);
		
		pnlMid = new JPanel();
		pnlMain.add(pnlMid, "4, 2, fill, center"); //$NON-NLS-1$
		pnlMid.setLayout(new GridLayout(0, 1, 0, 0));
		
		btnAdd = new JButton(">"); //$NON-NLS-1$
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lbAll.getSelectedValue() != null) {
					lmChoosen.addElement(lbAll.getSelectedValue());
				}
			}
		});
		pnlMid.add(btnAdd);
		
		btnDelete = new JButton("<"); //$NON-NLS-1$
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lbChoosen.getSelectedValue() != null) {
					lmChoosen.remove(lbChoosen.getSelectedIndex());
				}
			}
		});
		pnlMid.add(btnDelete);
		
		btnAddSep = new JButton("-"); //$NON-NLS-1$
		btnAddSep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lmChoosen.addElement(ClipToolbar.IDENT_SEPERATOR);
			}
		});
		pnlMid.add(btnAddSep);
		
		scpnChoosen = new JScrollPane();
		pnlMain.add(scpnChoosen, "6, 2"); //$NON-NLS-1$
		
		lbChoosen = new DnDList<>();
		lbChoosen.setVisibleRowCount(0);
		lbChoosen.setCellRenderer(new ToolbarElementsCellRenderer());
		scpnChoosen.setViewportView(lbChoosen);
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		btnOk = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnOK();
			}
		});
		panel.add(btnOk);
		
		btnCancel = new JButton(LocaleBundle.getString("AddMovieFrame.btnCancel.text")); //$NON-NLS-1$
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
