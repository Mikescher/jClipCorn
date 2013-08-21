package de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomFSKFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomFormatFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomGenreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomLanguageFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomOnlinescoreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomQualityFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomScoreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomTagFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomTitleFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomTypFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomViewedFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomYearFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomZyklusFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomNandOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomNorOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomOrOperator;
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
	private JComboBox<String> cbxFilter;
	private JComboBox<String> cbxOperator;
	private JButton btnAddFilter;
	private JButton btnAddOperator;
	private JButton btnDelete;
	private JButton btnEdit;
	private JPanel pnlBottomLeft;
	private JPanel pnlBottomRight;
	private JButton btnExport;
	private JButton btnImport;

	public CustomOperatorFilterDialog(CustomOperator op, FinishListener fl, Component parent, boolean showExporter) {
		super(op, fl);
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
		setSize(new Dimension(450, 300));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("90dlu"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("10dlu"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("10dlu"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		scrollPane = new JScrollPane();
		pnlMiddle.add(scrollPane, "2, 2, 1, 17, fill, fill"); //$NON-NLS-1$
		
		displayList = new JList<>();
		scrollPane.setViewportView(displayList);
		
		cbxFilter = new JComboBox<>();
		cbxFilter.setModel(new DefaultComboBoxModel<>(new String[] { 
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Title"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Format"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.FSK"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Genre"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Language"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Onlinescore"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Quality"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Score"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Tag"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Typ"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Viewed"), //$NON-NLS-1$ 
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Year"), //$NON-NLS-1$,
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Zyklus"), //$NON-NLS-1$
		}));
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
		cbxOperator.setModel(new DefaultComboBoxModel<>(new String[] {
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.OP-AND"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.OP-OR"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.OP-NAND"), //$NON-NLS-1$
				LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.OP-NOR") //$NON-NLS-1$
		}));
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
		
		btnOk = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
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

	private void onOK() {
		dispose();
	}

	private void onAddOperator() {
		CustomOperator childop = null;
		
		switch (cbxOperator.getSelectedIndex()) {
		case 0:
			childop = new CustomAndOperator();
			break;
		case 1:
			childop = new CustomOrOperator();
			break;
		case 2:
			childop = new CustomNandOperator();
			break;
		case 3:
			childop = new CustomNorOperator();
			break;
		}
		
		onStartEdit(getFilter().add(childop));
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
		
		if (f instanceof CustomTitleFilter) {
			new CustomTitleFilterDialog((CustomTitleFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomOperator) {
			new CustomOperatorFilterDialog((CustomOperator) f, this, this, false).setVisible(true);
		}if (f instanceof CustomFormatFilter) {
			new CustomFormatFilterDialog((CustomFormatFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomFSKFilter) {
			new CustomFSKFilterDialog((CustomFSKFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomGenreFilter) {
			new CustomGenreFilterDialog((CustomGenreFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomLanguageFilter) {
			new CustomLanguageFilterDialog((CustomLanguageFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomOnlinescoreFilter) {
			new CustomOnlinescoreFilterDialog((CustomOnlinescoreFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomQualityFilter) {
			new CustomQualityFilterDialog((CustomQualityFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomScoreFilter) {
			new CustomScoreFilterDialog((CustomScoreFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomTagFilter) {
			new CustomTagFilterDialog((CustomTagFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomTypFilter) {
			new CustomTypFilterDialog((CustomTypFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomViewedFilter) {
			new CustomViewedFilterDialog((CustomViewedFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomYearFilter) {
			new CustomYearFilterDialog((CustomYearFilter) f, this, this).setVisible(true);
		} else if (f instanceof CustomZyklusFilter) {
			new CustomZyklusFilterDialog((CustomZyklusFilter) f, this, this).setVisible(true);
		}
	}
	
	private void onAddFilter() {
		switch (cbxFilter.getSelectedIndex()) {
		case 0: //Title
			onStartEdit(getFilter().add(new CustomTitleFilter()));
			break;
		case 1: //Format
			onStartEdit(getFilter().add(new CustomFormatFilter()));
			break;
		case 2: //FSK
			onStartEdit(getFilter().add(new CustomFSKFilter()));
			break;
		case 3: //Genre
			onStartEdit(getFilter().add(new CustomGenreFilter()));
			break;
		case 4: //Language
			onStartEdit(getFilter().add(new CustomLanguageFilter()));
			break;
		case 5: //Onlinescore
			onStartEdit(getFilter().add(new CustomOnlinescoreFilter()));
			break;
		case 6: //Quality
			onStartEdit(getFilter().add(new CustomQualityFilter()));
			break;
		case 7: //Score
			onStartEdit(getFilter().add(new CustomScoreFilter()));
			break;
		case 8: //Tag
			onStartEdit(getFilter().add(new CustomTagFilter()));
			break;
		case 9: //Typ
			onStartEdit(getFilter().add(new CustomTypFilter()));
			break;
		case 10: //Viewed
			onStartEdit(getFilter().add(new CustomViewedFilter()));
			break;
		case 11: //Year
			onStartEdit(getFilter().add(new CustomYearFilter()));
			break;
		case 12: //Zyklus
			onStartEdit(getFilter().add(new CustomZyklusFilter()));
			break;
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
