package de.jClipCorn.gui.frames.extendedSettingsFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable.SettingsTableEditor;
import de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable.SettingsTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.LookAndFeelManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ExtendedSettingsFrame extends JFrame
{
	private final CCProperties properties;
	private SettingsTableModel lsModel = null;
	private SettingsTableEditor lsEditor;
	private TableRowSorter<SettingsTableModel> rowsorter;
	private Component currentEditComponent;

	public ExtendedSettingsFrame(Component owner, CCProperties properties)
	{
		super();
		this.properties = properties;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
		setMinimumSize(getSize());
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());

		if (! LookAndFeelManager.isSubstance())
		{
			edKey.setBackground(Color.WHITE);
			edTyp.setBackground(Color.WHITE);
			edDescription.setBackground(Color.WHITE);
			edCategory.setBackground(Color.WHITE);
			edDefaultValue.setBackground(Color.WHITE);
			edCurrentValue.setBackground(Color.WHITE);
		}

		lsModel = new SettingsTableModel(properties);

		rowsorter = new TableRowSorter<>();
		rowsorter.setModel(lsModel);
		ArrayList<RowSorter.SortKey> sk = new ArrayList<>();
		sk.add( new RowSorter.SortKey(0, SortOrder.ASCENDING) );
		rowsorter.setSortKeys(sk);
		rowsorter.sort();

		tabSettings.setModel(lsModel);
		tabSettings.setRowSorter(rowsorter);

		tabSettings.getColumnModel().getColumn(0).setPreferredWidth(getWidth()*2 / 3);
		tabSettings.getColumnModel().getColumn(1).setPreferredWidth(getWidth()   / 3);

		tabSettings.getSelectionModel().addListSelectionListener(this::onSelectionValueChanged);
		tabSettings.getColumnModel().getColumn(1).setCellEditor(lsEditor = new SettingsTableEditor(properties));

		edFilter.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::onFilter));
	}

	private void onFilter()
	{
		final var search = edFilter.getText().toLowerCase();
		if (Str.isNullOrWhitespace(search))
		{
			rowsorter.setRowFilter(null);
		}
		else
		{
			rowsorter.setRowFilter(new RowFilter<>()
			{
				@Override public boolean include(Entry<? extends SettingsTableModel, ? extends Integer> entry)
				{
					var cat = properties.getPropertyList().get(entry.getIdentifier()).getCategory().Name;
					var key = properties.getPropertyList().get(entry.getIdentifier()).getIdentifier();
					var val = properties.getPropertyList().get(entry.getIdentifier()).getValueAsString();
					var des = properties.getPropertyList().get(entry.getIdentifier()).getDescriptionOrEmpty();

					if (cat.toLowerCase().contains(search)) return true;
					if (key.toLowerCase().contains(search)) return true;
					if (val.toLowerCase().contains(search)) return true;
					if (des.toLowerCase().contains(search)) return true;

					return false;
				}
			});
		}
	}

	private void onReset(ActionEvent e)
	{
		int sel = tabSettings.convertRowIndexToModel(tabSettings.getSelectedRow());

		lsEditor.stopCellEditing();

		if (sel >= 0)
		{
			CCProperty<?> prop = properties.getPropertyList().get(sel);
			prop.setDefault();
			refresh();
		}
	}

	private void onOK(ActionEvent e)
	{
		lsEditor.stopCellEditing();
		dispose();
	}

	private void onResetAll(ActionEvent e)
	{
		if (DialogHelper.showLocaleYesNo(ExtendedSettingsFrame.this, "extendedSettingsFrame.dlgResetAll.dlg")) { //$NON-NLS-1$
			properties.resetAll();

			refresh();
		}
	}

	private void onSetValue(ActionEvent e)
	{
		int sel;
		if (currentEditComponent != null && tabSettings.getSelectedRow() >= 0 && (sel = tabSettings.convertRowIndexToModel(tabSettings.getSelectedRow())) >= 0)
		{
			final CCProperty<Object> prop = properties.getPropertyList().get(sel);

			prop.setValue(prop.getComponentValue(currentEditComponent));
			refresh();
		}
	}

	public void onSelectionValueChanged(ListSelectionEvent e) {
		int sel;
		if (tabSettings.getSelectedRow() >= 0 && !e.getValueIsAdjusting() && (sel = tabSettings.convertRowIndexToModel(tabSettings.getSelectedRow())) >= 0) {
			final CCProperty<Object> prop = properties.getPropertyList().get(sel);

			final Component comp1 = prop.getComponent();
			final Component comp2 = prop.getSecondaryComponent(comp1);

			prop.setComponentValueToValue(comp1, prop.getValue());

			pnlEditComponent.removeAll();
			pnlEditComponent.add(currentEditComponent = comp1, BorderLayout.CENTER);
			if (comp2 != null) pnlEditComponent.add(comp2, BorderLayout.EAST);
			pnlEditComponent.validate();

			edKey.setText(prop.getIdentifier());
			edTyp.setText(prop.getTypeName());
			edDescription.setText(prop.getDescriptionOrEmpty());
			edCategory.setText(prop.getCategory().Index + " :: " + (prop.getCategory().isVisble() ? prop.getCategory().Name :  "~~HIDDEN~~")); //$NON-NLS-1$ //$NON-NLS-2$
			edDefaultValue.setText(prop.getDefaultAsString());
			edCurrentValue.setText(prop.getValueAsString());
		} else {
			edKey.setText(Str.Empty);
			edTyp.setText(Str.Empty);
			edDescription.setText(Str.Empty);
			edCategory.setText(Str.Empty);
			edDefaultValue.setText(Str.Empty);
			edCurrentValue.setText(Str.Empty);

			pnlEditComponent.removeAll();

			currentEditComponent = null;
		}
	}

	private void refresh() {
		int row = tabSettings.getSelectedRow();

		lsModel.fireTableDataChanged();

		tabSettings.getSelectionModel().setSelectionInterval(row, row);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		edFilter = new JTextField();
		pnlScroll = new JScrollPane();
		tabSettings = new JTable();
		label1 = new JLabel();
		edCategory = new JTextField();
		label5 = new JLabel();
		edKey = new JTextField();
		label6 = new JLabel();
		edTyp = new JTextField();
		label2 = new JLabel();
		edDescription = new JTextField();
		label3 = new JLabel();
		edDefaultValue = new JTextField();
		btnReset = new JButton();
		label4 = new JLabel();
		edCurrentValue = new JTextField();
		pnlEditComponent = new JPanel();
		btnValueSet = new JButton();
		btnOK = new JButton();
		button2 = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("extendedSettingsFrame.title")); //$NON-NLS-1$
		setMinimumSize(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, default, 2*($lcgap, 0dlu:grow), $lcgap, 0dlu:grow(0.5), $lcgap, default, $lcgap", //$NON-NLS-1$
			"$lgap, default, $rgap, default:grow, 8*($lgap, default), $lgap")); //$NON-NLS-1$
		contentPane.add(edFilter, CC.xywh(2, 2, 9, 1));

		//======== pnlScroll ========
		{

			//---- tabSettings ----
			tabSettings.setFillsViewportHeight(true);
			tabSettings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			pnlScroll.setViewportView(tabSettings);
		}
		contentPane.add(pnlScroll, CC.xywh(2, 4, 9, 1, CC.FILL, CC.FILL));

		//---- label1 ----
		label1.setText("Kategorie:"); //$NON-NLS-1$
		contentPane.add(label1, CC.xy(2, 6));

		//---- edCategory ----
		edCategory.setEditable(false);
		contentPane.add(edCategory, CC.xywh(4, 6, 7, 1));

		//---- label5 ----
		label5.setText("Schl\u00fcssel:"); //$NON-NLS-1$
		contentPane.add(label5, CC.xy(2, 8));

		//---- edKey ----
		edKey.setEditable(false);
		edKey.setColumns(10);
		contentPane.add(edKey, CC.xywh(4, 8, 7, 1));

		//---- label6 ----
		label6.setText("Typ:"); //$NON-NLS-1$
		contentPane.add(label6, CC.xy(2, 10));

		//---- edTyp ----
		edTyp.setEditable(false);
		contentPane.add(edTyp, CC.xywh(4, 10, 7, 1));

		//---- label2 ----
		label2.setText("Beschreibung:"); //$NON-NLS-1$
		contentPane.add(label2, CC.xy(2, 12));

		//---- edDescription ----
		edDescription.setEditable(false);
		contentPane.add(edDescription, CC.xywh(4, 12, 7, 1));

		//---- label3 ----
		label3.setText("Standardwert:"); //$NON-NLS-1$
		contentPane.add(label3, CC.xy(2, 14));

		//---- edDefaultValue ----
		edDefaultValue.setEditable(false);
		contentPane.add(edDefaultValue, CC.xywh(4, 14, 5, 1));

		//---- btnReset ----
		btnReset.setText(LocaleBundle.getString("extendedSettingsFrame.btnReset.title")); //$NON-NLS-1$
		btnReset.addActionListener(e -> onReset(e));
		contentPane.add(btnReset, CC.xy(10, 14));

		//---- label4 ----
		label4.setText("Aktueller Wert:"); //$NON-NLS-1$
		contentPane.add(label4, CC.xy(2, 16));

		//---- edCurrentValue ----
		edCurrentValue.setEditable(false);
		contentPane.add(edCurrentValue, CC.xywh(4, 16, 7, 1));

		//======== pnlEditComponent ========
		{
			pnlEditComponent.setLayout(new BorderLayout());
		}
		contentPane.add(pnlEditComponent, CC.xywh(2, 18, 7, 1, CC.FILL, CC.FILL));

		//---- btnValueSet ----
		btnValueSet.setText(LocaleBundle.getString("extendedSettingsFrame.btnValueSet.title")); //$NON-NLS-1$
		btnValueSet.addActionListener(e -> onSetValue(e));
		contentPane.add(btnValueSet, CC.xy(10, 18));

		//---- btnOK ----
		btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOK(e));
		contentPane.add(btnOK, CC.xy(4, 20, CC.RIGHT, CC.DEFAULT));

		//---- button2 ----
		button2.setText(LocaleBundle.getString("extendedSettingsFrame.btnResetAll.title")); //$NON-NLS-1$
		button2.addActionListener(e -> onResetAll(e));
		contentPane.add(button2, CC.xy(6, 20, CC.LEFT, CC.DEFAULT));
		pack();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTextField edFilter;
	private JScrollPane pnlScroll;
	private JTable tabSettings;
	private JLabel label1;
	private JTextField edCategory;
	private JLabel label5;
	private JTextField edKey;
	private JLabel label6;
	private JTextField edTyp;
	private JLabel label2;
	private JTextField edDescription;
	private JLabel label3;
	private JTextField edDefaultValue;
	private JButton btnReset;
	private JLabel label4;
	private JTextField edCurrentValue;
	private JPanel pnlEditComponent;
	private JButton btnValueSet;
	private JButton btnOK;
	private JButton button2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
