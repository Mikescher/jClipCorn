package de.jClipCorn.gui.frames.organizeFilterFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.frames.customFilterEditDialog.CustomFilterEditDialog;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.filterTree.CustomFilterList;
import de.jClipCorn.gui.mainFrame.filterTree.CustomFilterObject;
import de.jClipCorn.util.listener.FinishListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class OrganizeFilterDialog extends JCCDialog {
	private static final long serialVersionUID = -8210148094781041350L;
	
	private CustomFilterList filterlist;
	private FinishListener action;
	
	private JScrollPane scrollPane;
	private JList<CustomFilterObject> lstBoxFilter;
	private JPanel panel;
	private JButton btnAdd;
	private JButton btnRem;
	private JButton btnEdit;
	private JPanel panel_1;
	private JButton btnOK;
	private JButton btnUp;
	private JButton btnDown;
	
	public OrganizeFilterDialog(CCMovieList ml, Component owner, CustomFilterList flist, FinishListener okAction) {
		super(ml);
		this.filterlist = flist;
		this.action = okAction;

		setSize(350, 300);
		
		initGUI();
		setLocationRelativeTo(owner);
		
		updateList();
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setModal(true);
		setTitle(LocaleBundle.getString("OrganizeFilterDialog.this.title")); //$NON-NLS-1$
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("top:default:grow"), //$NON-NLS-1$
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane, "1, 1, fill, fill"); //$NON-NLS-1$
		
		lstBoxFilter = new JList<>();
		scrollPane.setViewportView(lstBoxFilter);
		lstBoxFilter.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				updateButtons();
			}
		});
		
		panel = new JPanel();
		getContentPane().add(panel, "2, 1, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("center:pref:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		btnAdd = new JButton("+"); //$NON-NLS-1$
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onActionAdd();
			}
		});
		btnAdd.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		panel.add(btnAdd, "2, 2, fill, top"); //$NON-NLS-1$
		
		btnRem = new JButton("-"); //$NON-NLS-1$
		btnRem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onActionRem();
			}
		});
		btnRem.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		panel.add(btnRem, "2, 4, fill, top"); //$NON-NLS-1$
		
		btnUp = new JButton("\u25B2"); //$NON-NLS-1$
		btnUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onActionUp();
			}
		});
		panel.add(btnUp, "2, 8, fill, default"); //$NON-NLS-1$
		
		btnDown = new JButton("\u25BC"); //$NON-NLS-1$
		btnDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onActionDown();
			}
		});
		panel.add(btnDown, "2, 10, fill, default"); //$NON-NLS-1$
		
		btnEdit = new JButton(LocaleBundle.getString("OrganizeFilterDialog.btnEdit.text")); //$NON-NLS-1$
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onActionEdit();
			}
		});
		btnEdit.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		panel.add(btnEdit, "2, 16, fill, top"); //$NON-NLS-1$
		
		panel_1 = new JPanel();
		getContentPane().add(panel_1, "1, 2, 2, 1"); //$NON-NLS-1$
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onActionOK();
			}
		});
		panel_1.add(btnOK);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (action != null) action.finish();
			}
		});
	}

	private void updateList() {
		DefaultListModel<CustomFilterObject> model;
		lstBoxFilter.setModel(model = new DefaultListModel<>());
		lstBoxFilter.removeAll();
		
		for (int i = 0; i < filterlist.size(); i++) {
			model.addElement(filterlist.get(i));
		}
		
		lstBoxFilter.clearSelection();
		
		updateButtons();
	}
	
	private void onActionOK() {
		dispose();
	}
	
	private void onActionEdit() {
		int sel = lstBoxFilter.getSelectedIndex();
		
		if (sel == -1) return;
		
		final CustomFilterObject cfo = filterlist.get(sel).copy(movielist);
		
		new CustomFilterEditDialog(this, movielist, cfo, () ->
		{
			filterlist.get(sel).apply(cfo);
			updateList();
		}).setVisible(true);
	}
	
	private void onActionRem() {
		int sel = lstBoxFilter.getSelectedIndex();
		
		if (sel == -1) return;
		
		filterlist.remove(sel);
		
		updateList();
	}

	private void onActionAdd() {
		final CustomFilterObject cfo = new CustomFilterObject("New filter", new CustomAndOperator(movielist)); //$NON-NLS-1$
		
		new CustomFilterEditDialog(this, movielist, cfo, () ->
		{
			filterlist.add(cfo);
			updateList();
		}).setVisible(true);
	}
	
	private void onActionDown() {
		int sel = lstBoxFilter.getSelectedIndex();
		
		if (sel == -1 || sel == filterlist.size()-1) return;
		
		CustomFilterObject obj = filterlist.get(sel);
		
		filterlist.remove(sel);
		filterlist.add(sel+1, obj);
		
		updateList();
		
		lstBoxFilter.setSelectedIndex(sel + 1);
		
		updateButtons();
	}
	
	private void onActionUp() {
		int sel = lstBoxFilter.getSelectedIndex();
		
		if (sel == -1 || sel == 0) return;
		
		CustomFilterObject obj = filterlist.get(sel);
		
		filterlist.remove(sel);
		filterlist.add(sel-1, obj);
		
		updateList();
		
		lstBoxFilter.setSelectedIndex(sel - 1);
		
		updateButtons();
	}
	
	private void updateButtons() {
		btnUp.setEnabled(lstBoxFilter.getSelectedIndex() > 0);
		btnDown.setEnabled(lstBoxFilter.getSelectedIndex() < (lstBoxFilter.getModel().getSize()-1));
		btnRem.setEnabled(lstBoxFilter.getSelectedIndex() >= 0);
		btnEdit.setEnabled(lstBoxFilter.getSelectedIndex() >= 0);
	}
}
