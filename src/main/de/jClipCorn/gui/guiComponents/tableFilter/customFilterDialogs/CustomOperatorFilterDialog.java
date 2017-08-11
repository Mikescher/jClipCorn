package de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomOperator;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.FinishListener;

public class CustomOperatorFilterDialog extends CustomFilterDialog implements FinishListener{
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JList<String> displayList;
	private JScrollPane scrollPane;
	private JComboBox<AbstractCustomFilter> cbxFilter;
	private JComboBox<AbstractCustomFilter> cbxOperator;
	private JButton btnAddFilter;
	private JButton btnAddOperator;
	private JButton btnDelete;
	private JButton btnEdit;
	private JPanel pnlBottomLeft;
	private JPanel pnlBottomRight;
	private JButton btnExport;
	private JButton btnImport;

	private final CCMovieList movielist;
	
	public CustomOperatorFilterDialog(CCMovieList ml, CustomOperator op, FinishListener fl, Component parent, boolean showExporter) {
		super(op, fl);
		movielist = ml;
		initGUI(showExporter);
		
		updateList();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomOperator getFilter() {
		return (CustomOperator) super.getFilter();
	}
	
	private void initGUI(boolean showExporter) {
		setResizable(true);
		setMinimumSize(new Dimension(450, 300));
		setSize(new Dimension(450, 350));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("90dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("10dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("10dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		scrollPane = new JScrollPane();
		pnlMiddle.add(scrollPane, "2, 2, 1, 17, fill, fill"); //$NON-NLS-1$
		
		displayList = new JList<>();
		scrollPane.setViewportView(displayList);
		
		cbxFilter = new JComboBox<>();
		cbxFilter.setModel(new DefaultComboBoxModel<>(AbstractCustomFilter.getAllSimpleFilter()));
		cbxFilter.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1710057818185541683L;

		    @Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		    	JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		    	comp.setText(((AbstractCustomFilter)value).getPrecreateName());
		        return comp;
		    }
		});
		pnlMiddle.add(cbxFilter, "4, 2, fill, default"); //$NON-NLS-1$
		
		btnAddFilter = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnAddFilter.text")); //$NON-NLS-1$
		btnAddFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAddFilter();
			}
		});
		pnlMiddle.add(btnAddFilter, "4, 4"); //$NON-NLS-1$
		
		cbxOperator = new JComboBox<>();
		cbxOperator.setModel(new DefaultComboBoxModel<>(AbstractCustomFilter.getAllOperatorFilter()));
		cbxOperator.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1710057818185541683L;

		    @Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		    	JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		    	comp.setText(((AbstractCustomFilter)value).getPrecreateName());
		        return comp;
		    }
		});
		pnlMiddle.add(cbxOperator, "4, 8, fill, default"); //$NON-NLS-1$
		
		btnAddOperator = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnAddOp.text")); //$NON-NLS-1$
		btnAddOperator.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAddOperator();
			}
		});
		pnlMiddle.add(btnAddOperator, "4, 10"); //$NON-NLS-1$
		
		btnDelete = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnDelete.text")); //$NON-NLS-1$
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDelete();
			}
		});
		
		btnEdit = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnEdit.text")); //$NON-NLS-1$
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onEdit();
			}
		});
		pnlMiddle.add(btnEdit, "4, 14"); //$NON-NLS-1$
		pnlMiddle.add(btnDelete, "4, 16"); //$NON-NLS-1$
		
		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new BorderLayout(0, 0));
		
		pnlBottomLeft = new JPanel();
		pnlBottom.add(pnlBottomLeft, BorderLayout.CENTER);
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		pnlBottomLeft.add(btnOk);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		
		pnlBottomRight = new JPanel();
		pnlBottom.add(pnlBottomRight, BorderLayout.EAST);
		
		btnExport = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnExport.text")); //$NON-NLS-1$
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DialogHelper.showPlainInputDialog(CustomOperatorFilterDialog.this, getFilter().exportToString());
			}
		});
		btnExport.setVisible(showExporter);
		pnlBottomRight.add(btnExport);
		
		btnImport = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnImport.text")); //$NON-NLS-1$
		btnImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String imp = DialogHelper.showPlainInputDialog(CustomOperatorFilterDialog.this);
				if (imp != null && !getFilter().importFromString(imp)) {
					DialogHelper.showLocalError(CustomOperatorFilterDialog.this, "Dialogs.CustomFilterImportFailed"); //$NON-NLS-1$
					getFilter().getList().clear();
				} 
				updateList();
			}
		});
		btnImport.setVisible(showExporter);
		pnlBottomRight.add(btnImport);
	}
	
	@Override
	protected void onAfterOK() {
		// Nothing
	}

	private void onAddOperator() {
		if (cbxOperator.getSelectedIndex() >= 0) {
			onStartEdit(getFilter().add(((AbstractCustomFilter)cbxOperator.getSelectedItem()).createNew()));
		}
	}
	
	private void onDelete() {
		int sel = displayList.getSelectedIndex();
		
		if (sel >= 0 && sel < getFilter().getList().size()) {
			AbstractCustomFilter f = getFilter().getList().get(sel);
			getFilter().remove(f);
			updateList();
		}
	}
	
	private void onEdit() {
		int sel = displayList.getSelectedIndex();
		
		if (sel >= 0 && sel < getFilter().getList().size()) {
			AbstractCustomFilter f = getFilter().getList().get(sel);

			onStartEdit(f);
		}
	}
	
	private void onStartEdit(AbstractCustomFilter f) {
		updateList();
		
		if (f != null) {
			f.CreateDialog(this, this, movielist).setVisible(true);
		}
	}
	
	private void onAddFilter() {
		if (cbxFilter.getSelectedIndex() >= 0) {
			onStartEdit(getFilter().add(((AbstractCustomFilter)cbxFilter.getSelectedItem()).createNew()));
		}
	}
	
	private void updateList() {
		DefaultListModel<String> dlm = new DefaultListModel<>();
		
		for (int i = 0; i < getFilter().getList().size(); i++) {
			dlm.addElement(getFilter().getList().get(i).getName());
		}
		
		displayList.setModel(dlm);
		
		updateTitle();
	}

	@Override
	public void finish() {
		updateList();
	}
}
