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
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.LookAndFeelManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ExtendedSettingsFrame extends JFrame
{
	private final CCProperties properties;
	private SettingsTableModel lsModel = null;
	private SettingsTableEditor lsEditor;

	public ExtendedSettingsFrame(Component owner, CCProperties properties)
	{
		super();
		this.properties = properties;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());

		if (! LookAndFeelManager.isSubstance()) {
			edName.setBackground(Color.WHITE);
		}
		if (! LookAndFeelManager.isSubstance()) {
			edTyp.setBackground(Color.WHITE);
		}

		lsModel = new SettingsTableModel(properties); // $hide$

		TableRowSorter<SettingsTableModel> rowsorter = new TableRowSorter<>();
		rowsorter.setModel(lsModel);
		ArrayList<RowSorter.SortKey> sk = new ArrayList<>();
		sk.add( new RowSorter.SortKey(0, SortOrder.ASCENDING) );
		rowsorter.setSortKeys(sk);
		rowsorter.sort();

		tabSettings.setModel(lsModel);
		tabSettings.setRowSorter(rowsorter);

		tabSettings.getColumnModel().getColumn(0).setPreferredWidth(getWidth()*2/3);
		tabSettings.getColumnModel().getColumn(1).setPreferredWidth(getWidth()*1/3);

		tabSettings.getSelectionModel().addListSelectionListener(this::onSelectionValueChanged);
		tabSettings.getColumnModel().getColumn(1).setCellEditor(lsEditor = new SettingsTableEditor(properties));
	}

	private void onReset(ActionEvent e) {
		int sel = tabSettings.convertRowIndexToModel(tabSettings.getSelectedRow());

		lsEditor.stopCellEditing();

		if (sel >= 0) {
			CCProperty<?> prop = properties.getPropertyList().get(sel);
			prop.setDefault();
			refresh();
		}
	}

	private void onSetValue(ActionEvent e) {
		// TODO add your code here
	}

	private void onResetValue(ActionEvent e) {
		// TODO add your code here
	}

	private void onOK(ActionEvent e) {
		lsEditor.stopCellEditing();
		dispose();
	}

	private void onResetAll(ActionEvent e) {
		if (DialogHelper.showLocaleYesNo(ExtendedSettingsFrame.this, "extendedSettingsFrame.dlgResetAll.dlg")) { //$NON-NLS-1$
			properties.resetAll();

			refresh();
		}
	}

	public void onSelectionValueChanged(ListSelectionEvent e) {
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlScroll = new JScrollPane();
		tabSettings = new JTable();
		lblCategory = new JLabel();
		lblDescription = new JLabel();
		edName = new JTextField();
		edTyp = new JTextField();
		btnReset = new JButton();
		pnlEditComponent = new JPanel();
		btnValueSet = new JButton();
		btnValueReset = new JButton();
		btnOK = new JButton();
		button2 = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("extendedSettingsFrame.title")); //$NON-NLS-1$
		setMinimumSize(new Dimension(368, 520));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, 3*(0dlu:grow, $lcgap), 0dlu:grow(0.5), $lcgap, default, $lcgap", //$NON-NLS-1$
			"$rgap, default:grow, 2*($lgap, 12dlu), 3*($lgap, default), $lgap")); //$NON-NLS-1$

		//======== pnlScroll ========
		{

			//---- tabSettings ----
			tabSettings.setFillsViewportHeight(true);
			tabSettings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			pnlScroll.setViewportView(tabSettings);
		}
		contentPane.add(pnlScroll, CC.xywh(2, 2, 9, 1, CC.FILL, CC.FILL));
		contentPane.add(lblCategory, CC.xywh(2, 4, 4, 1));
		contentPane.add(lblDescription, CC.xywh(2, 6, 7, 1));

		//---- edName ----
		edName.setEditable(false);
		edName.setColumns(10);
		contentPane.add(edName, CC.xywh(2, 8, 3, 1));
		contentPane.add(edTyp, CC.xywh(6, 8, 3, 1));

		//---- btnReset ----
		btnReset.setText(LocaleBundle.getString("extendedSettingsFrame.btnReset.title")); //$NON-NLS-1$
		btnReset.addActionListener(e -> onReset(e));
		contentPane.add(btnReset, CC.xy(10, 8));

		//======== pnlEditComponent ========
		{
			pnlEditComponent.setLayout(new BorderLayout());
		}
		contentPane.add(pnlEditComponent, CC.xywh(2, 10, 5, 1, CC.FILL, CC.FILL));

		//---- btnValueSet ----
		btnValueSet.setText(LocaleBundle.getString("extendedSettingsFrame.btnValueSet.title")); //$NON-NLS-1$
		contentPane.add(btnValueSet, CC.xy(8, 10));

		//---- btnValueReset ----
		btnValueReset.setText(LocaleBundle.getString("extendedSettingsFrame.btnReset.title")); //$NON-NLS-1$
		contentPane.add(btnValueReset, CC.xy(10, 10));

		//---- btnOK ----
		btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOK(e));
		contentPane.add(btnOK, CC.xy(4, 12, CC.RIGHT, CC.DEFAULT));

		//---- button2 ----
		button2.setText(LocaleBundle.getString("extendedSettingsFrame.btnResetAll.title")); //$NON-NLS-1$
		button2.addActionListener(e -> onResetAll(e));
		contentPane.add(button2, CC.xy(6, 12, CC.LEFT, CC.DEFAULT));
		setSize(500, 540);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane pnlScroll;
	private JTable tabSettings;
	private JLabel lblCategory;
	private JLabel lblDescription;
	private JTextField edName;
	private JTextField edTyp;
	private JButton btnReset;
	private JPanel pnlEditComponent;
	private JButton btnValueSet;
	private JButton btnValueReset;
	private JButton btnOK;
	private JButton button2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
