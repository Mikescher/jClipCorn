package de.jClipCorn.gui.frames.extendedSettingsFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
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

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable.SettingsTableEditor;
import de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable.SettingsTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.LookAndFeelManager;

public class ExtendedSettingsFrame extends JFrame implements ListSelectionListener {
	private static final long serialVersionUID = 6757118153580402337L;

	private final CCProperties properties;
	private JPanel pnlBottom;
	private JPanel pnlTop;
	private JButton btnOK;
	private JButton btnReset;
	private JScrollPane pnlScroll;
	private JTable tabSettings;
	private SettingsTableModel lsModel = null;
	private TableRowSorter<SettingsTableModel> rowsorter;
	private	SettingsTableEditor lsEditor;
	private JPanel pnlBottomTopInside;
	private JPanel pnlBottomBottom;
	private JTextField edName;
	private JTextField edTyp;
	private JTextField edValue;
	private JPanel pnlBottomTop;
	private Component hStrut_1;
	private Component hStrut_2;
	private Component vStrut_2;
	private Component vStrut_1;
	private JButton btnResetAll;

	public ExtendedSettingsFrame(Component owner, CCProperties properties) {
		super();
		this.properties = properties;
		initGUI();
		
		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("extendedSettingsFrame.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new BorderLayout(0, 0));

		pnlBottomTop = new JPanel();
		pnlBottom.add(pnlBottomTop, BorderLayout.NORTH);
		pnlBottomTop.setLayout(new BorderLayout(0, 0));

		hStrut_1 = Box.createHorizontalStrut(3);
		pnlBottomTop.add(hStrut_1, BorderLayout.WEST);

		vStrut_1 = Box.createVerticalStrut(3);
		pnlBottomTop.add(vStrut_1, BorderLayout.SOUTH);

		vStrut_2 = Box.createVerticalStrut(3);
		pnlBottomTop.add(vStrut_2, BorderLayout.NORTH);

		hStrut_2 = Box.createHorizontalStrut(3);
		pnlBottomTop.add(hStrut_2, BorderLayout.EAST);

		pnlBottomTopInside = new JPanel();
		pnlBottomTop.add(pnlBottomTopInside);
		pnlBottomTopInside.setLayout(
				new FormLayout(
						new ColumnSpec[] { 
								ColumnSpec.decode("default:grow(10)"),  //$NON-NLS-1$
								FormFactory.RELATED_GAP_COLSPEC, 
								ColumnSpec.decode("default:grow"), //$NON-NLS-1$
								FormFactory.RELATED_GAP_COLSPEC, 
								ColumnSpec.decode("default:grow"),  //$NON-NLS-1$
								FormFactory.RELATED_GAP_COLSPEC,
								ColumnSpec.decode("max(50dlu;default)"),  //$NON-NLS-1$
				}, 
				new RowSpec[] { 
								RowSpec.decode("23px"),  //$NON-NLS-1$
							}));

		edName = new JTextField();
		edName.setEditable(false);
		pnlBottomTopInside.add(edName, "1, 1, fill, fill"); //$NON-NLS-1$
		edName.setColumns(10);
		if (! LookAndFeelManager.isSubstance()) {
			edName.setBackground(Color.WHITE);
		}

		edTyp = new JTextField();
		edTyp.setEditable(false);
		pnlBottomTopInside.add(edTyp, "3, 1, fill, fill"); //$NON-NLS-1$
		edTyp.setColumns(10);
		if (! LookAndFeelManager.isSubstance()) {
			edTyp.setBackground(Color.WHITE);
		}

		edValue = new JTextField();
		edValue.setEditable(false);
		pnlBottomTopInside.add(edValue, "5, 1, fill, fill"); //$NON-NLS-1$
		edValue.setColumns(10);
		if (! LookAndFeelManager.isSubstance()) {
			edValue.setBackground(Color.WHITE);
		}

		btnReset = new JButton(LocaleBundle.getString("extendedSettingsFrame.btnReset.title")); //$NON-NLS-1$
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int sel = tabSettings.convertRowIndexToModel(tabSettings.getSelectedRow());
				
				lsEditor.stopCellEditing();
				
				if (sel >= 0) {
					CCProperty<?> prop = properties.getPropertyList().get(sel);
					prop.setDefault();
					refresh();
				}
			}
		});
		pnlBottomTopInside.add(btnReset, "7, 1, fill, fill"); //$NON-NLS-1$

		pnlBottomBottom = new JPanel();
		pnlBottom.add(pnlBottomBottom, BorderLayout.SOUTH);

		btnOK = new JButton(LocaleBundle.getString("extendedSettingsFrame.btnOK.title")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lsEditor.stopCellEditing();
				dispose();
			}
		});
		pnlBottomBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		pnlBottomBottom.add(btnOK);
		
		btnResetAll = new JButton(LocaleBundle.getString("extendedSettingsFrame.btnResetAll.title")); //$NON-NLS-1$
		btnResetAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (DialogHelper.showLocaleYesNo(ExtendedSettingsFrame.this, "extendedSettingsFrame.dlgResetAll.dlg")) { //$NON-NLS-1$
					properties.resetAll();
				
					refresh();
				}
			}
		});
		pnlBottomBottom.add(btnResetAll);

		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.CENTER);
		pnlTop.setLayout(new BorderLayout(0, 0));

		pnlScroll = new JScrollPane();
		pnlTop.add(pnlScroll, BorderLayout.CENTER);

		lsModel = new SettingsTableModel(properties); // $hide$

		tabSettings = new JTable(lsModel);
		tabSettings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabSettings.setFillsViewportHeight(true);
		tabSettings.getSelectionModel().addListSelectionListener(this);
		tabSettings.getColumnModel().getColumn(1).setCellEditor(lsEditor = new SettingsTableEditor(properties));
		tabSettings.setRowSorter(rowsorter = new TableRowSorter<>());
		rowsorter.setModel(lsModel);
		List<SortKey> sk = new ArrayList<>();
    	sk.add( new RowSorter.SortKey(0, SortOrder.ASCENDING) );
		rowsorter.setSortKeys(sk);
		rowsorter.sort();
		pnlScroll.setViewportView(tabSettings);

		pack();
		
		setMinimumSize(getSize());
		
		tabSettings.getColumnModel().getColumn(0).setPreferredWidth(getWidth()*2/3);
		tabSettings.getColumnModel().getColumn(1).setPreferredWidth(getWidth()*1/3);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int sel;
		if (tabSettings.getSelectedRow() >= 0 && !e.getValueIsAdjusting() && (sel = tabSettings.convertRowIndexToModel(tabSettings.getSelectedRow())) >= 0) {
			CCProperty<?> prop = properties.getPropertyList().get(sel);
			edName.setText(prop.getIdentifier());
			edTyp.setText(prop.getTypeName());
			edValue.setText(prop.getValueAsString());
		} else {
			edName.setText(new String());
			edTyp.setText(new String());
			edValue.setText(new String());
		}
	}

	public void refresh() {
		int row = tabSettings.getSelectedRow();

		lsModel.fireTableDataChanged();

		tabSettings.getSelectionModel().setSelectionInterval(row, row);
	}
}
