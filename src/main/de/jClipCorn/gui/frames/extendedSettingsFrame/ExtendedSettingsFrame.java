package de.jClipCorn.gui.frames.extendedSettingsFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable.SettingsTableEditor;
import de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable.SettingsTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.LookAndFeelManager;

public class ExtendedSettingsFrame extends JFrame implements ListSelectionListener {
	private static final long serialVersionUID = 6757118153580402337L;

	private final CCProperties properties;
	private JPanel pnlContent;
	private JButton btnOK;
	private JButton btnReset;
	private JScrollPane pnlScroll;
	private JTable tabSettings;
	private SettingsTableModel lsModel = null;
	private TableRowSorter<SettingsTableModel> rowsorter;
	private	SettingsTableEditor lsEditor;
	private JTextField edName;
	private JTextField edTyp;
	private JButton btnResetAll;
	private JLabel lblDescription;
	private JButton btnValueSet;
	private JPanel pnlEditComponent;
	private JButton btnValueReset;
	private JLabel lblCategory;

	public ExtendedSettingsFrame(Component owner, CCProperties properties) {
		super();
		this.properties = properties;
		initGUI();
		finalizeGUI();

		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("extendedSettingsFrame.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		pnlContent = new JPanel();
		getContentPane().add(pnlContent, BorderLayout.CENTER);
		pnlContent.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(2)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(2)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("15dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("15dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		pnlScroll = new JScrollPane();
		pnlContent.add(pnlScroll, "2, 2, 9, 1, fill, fill"); //$NON-NLS-1$

		tabSettings = new JTable();
		tabSettings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabSettings.setFillsViewportHeight(true);
		pnlScroll.setViewportView(tabSettings);
		
		lblCategory = new JLabel(""); //$NON-NLS-1$
		pnlContent.add(lblCategory, "2, 4, 3, 1, fill, center"); //$NON-NLS-1$

		lblDescription = new JLabel(""); //$NON-NLS-1$
		pnlContent.add(lblDescription, "2, 6, 7, 1, left, center"); //$NON-NLS-1$

		edName = new JTextField();
		pnlContent.add(edName, "2, 8, 3, 1, fill, center"); //$NON-NLS-1$
		edName.setEditable(false);
		edName.setColumns(10);
		
				edTyp = new JTextField();
				pnlContent.add(edTyp, "6, 8, 3, 1, fill, center"); //$NON-NLS-1$
				edTyp.setEditable(false);
				edTyp.setColumns(10);

		btnReset = new JButton(LocaleBundle.getString("extendedSettingsFrame.btnReset.title")); //$NON-NLS-1$
		pnlContent.add(btnReset, "10, 8, left, center"); //$NON-NLS-1$
		btnReset.addActionListener(arg0 ->
		{
			int sel = tabSettings.convertRowIndexToModel(tabSettings.getSelectedRow());

			lsEditor.stopCellEditing();

			if (sel >= 0) {
				CCProperty<?> prop = properties.getPropertyList().get(sel);
				prop.setDefault();
				refresh();
			}
		});

		pnlEditComponent = new JPanel();
		pnlContent.add(pnlEditComponent, "2, 10, 5, 1, fill, fill"); //$NON-NLS-1$
		pnlEditComponent.setLayout(new BorderLayout(0, 0));

		btnValueSet = new JButton(LocaleBundle.getString("extendedSettingsFrame.btnValueSet.title")); //$NON-NLS-1$
		pnlContent.add(btnValueSet, "8, 10"); //$NON-NLS-1$

		btnValueReset = new JButton(LocaleBundle.getString("extendedSettingsFrame.btnValueReset.title")); //$NON-NLS-1$
		pnlContent.add(btnValueReset, "10, 10"); //$NON-NLS-1$

		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		pnlContent.add(btnOK, "4, 12, right, center"); //$NON-NLS-1$
		btnOK.addActionListener(arg0 ->
		{
			lsEditor.stopCellEditing();
			dispose();
		});
		btnOK.setEnabled(!CCProperties.getInstance().ARG_READONLY);

		btnResetAll = new JButton(LocaleBundle.getString("extendedSettingsFrame.btnResetAll.title")); //$NON-NLS-1$
		pnlContent.add(btnResetAll, "6, 12, left, center"); //$NON-NLS-1$
		btnResetAll.addActionListener(arg0 ->
		{
			if (DialogHelper.showLocaleYesNo(ExtendedSettingsFrame.this, "extendedSettingsFrame.dlgResetAll.dlg")) { //$NON-NLS-1$
				properties.resetAll();

				refresh();
			}
		});

		pack();

		setMinimumSize(new Dimension(368, 520));
	}

	private void finalizeGUI() {
		if (! LookAndFeelManager.isSubstance()) {
			edName.setBackground(Color.WHITE);
		}
		if (! LookAndFeelManager.isSubstance()) {
			edTyp.setBackground(Color.WHITE);
		}

		lsModel = new SettingsTableModel(properties); // $hide$
		rowsorter = new TableRowSorter<>();
		rowsorter.setModel(lsModel);
		List<SortKey> sk = new ArrayList<>();
		sk.add( new RowSorter.SortKey(0, SortOrder.ASCENDING) );
		rowsorter.setSortKeys(sk);
		rowsorter.sort();

		tabSettings.setModel(lsModel);
		tabSettings.setRowSorter(rowsorter);

		tabSettings.getColumnModel().getColumn(0).setPreferredWidth(getWidth()*2/3);
		tabSettings.getColumnModel().getColumn(1).setPreferredWidth(getWidth()*1/3);

		tabSettings.getSelectionModel().addListSelectionListener(this);
		tabSettings.getColumnModel().getColumn(1).setCellEditor(lsEditor = new SettingsTableEditor(properties));
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int sel;
		if (tabSettings.getSelectedRow() >= 0 && !e.getValueIsAdjusting() && (sel = tabSettings.convertRowIndexToModel(tabSettings.getSelectedRow())) >= 0) {
			final CCProperty<Object> prop = properties.getPropertyList().get(sel);

			final Component comp1 = prop.getComponent();
			final Component comp2 = prop.getSecondaryComponent(comp1);

			for (ActionListener l : btnValueSet.getActionListeners()) btnValueSet.removeActionListener(l);
			for (ActionListener l : btnValueReset.getActionListeners()) btnValueReset.removeActionListener(l);
			
			btnValueSet.addActionListener(arg0 ->
			{
				prop.setValue(prop.getComponentValue(comp1));
				refresh();
			});
			
			btnValueReset.addActionListener(arg0 ->
			{
				prop.setValue(prop.getDefault());
				prop.setComponentValueToValue(comp1, prop.getValue());
				refresh();
			});

			prop.setComponentValueToValue(comp1, prop.getValue());
			
			pnlEditComponent.removeAll();
			pnlEditComponent.add(comp1, BorderLayout.CENTER);
			if (comp2 != null) pnlEditComponent.add(comp2, BorderLayout.EAST);
			pnlEditComponent.validate();
			
			edName.setText(prop.getIdentifier());
			edTyp.setText(prop.getTypeName());
			lblDescription.setText(prop.getDescriptionOrEmpty());
			lblCategory.setText(prop.getCategory().Index + " :: " + (prop.getCategory().isVisble() ? prop.getCategory().Name :  "~~HIDDEN~~")); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			edName.setText(Str.Empty);
			edTyp.setText(Str.Empty);
			lblDescription.setText(Str.Empty);
			lblCategory.setText(Str.Empty);

			for (ActionListener l : btnValueSet.getActionListeners()) btnValueSet.removeActionListener(l);
			for (ActionListener l : btnValueReset.getActionListeners()) btnValueReset.removeActionListener(l);
			
			pnlEditComponent.removeAll();
		}
	}

	public void refresh() {
		int row = tabSettings.getSelectedRow();

		lsModel.fireTableDataChanged();

		tabSettings.getSelectionModel().setSelectionInterval(row, row);
	}
}
