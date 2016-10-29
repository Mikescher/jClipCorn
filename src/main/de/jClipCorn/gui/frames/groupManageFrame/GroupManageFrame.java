package de.jClipCorn.gui.frames.groupManageFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.guiComponents.StringDisplayConverter;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.CBListModel.CBFilter;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.JCheckBoxList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.DialogHelper;

public class GroupManageFrame extends JFrame {
	private static final long serialVersionUID = -5967105649409137866L;
	
	private List<CCGroup> tabGroupsData;
	
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTable tabGroups;
	private JScrollPane scrollPane_1;
	private JCheckBoxList<CCDatabaseElement> listElements;
	private JPanel pnlButtonLeft;
	private JButton btnRename;
	private JPanel pnlButtonRight;
	private JButton btnUpdate;
	private JTextField edFilter;

	private final CCMovieList movielist;
	private JButton btnToggleSerialization;
	private JButton btnSetColor;
	private JButton btnDelete;
	private JPanel panel;
	private JButton btnMoveUp;
	private JButton btnMoveDown;
	private JButton btnResetColors;
	
	/**
	 * Create the frame.
	 */
	public GroupManageFrame(CCMovieList ml, Component owner) {
		super();
		
		movielist = ml;
		
		initGUI();
		
		initData();
		
		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setMinimumSize(new Dimension(500, 300));
		setTitle(LocaleBundle.getString("GroupManagerFrame.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		
		setBounds(100, 100, 750, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("170dlu"), //$NON-NLS-1$
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("max(0dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,}));
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "4, 1, 1, 3, fill, fill"); //$NON-NLS-1$
		
		tabGroups = new JTable() {
			private static final long serialVersionUID = 5372349938763296529L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				if (column != 0) return super.prepareRenderer(renderer, row, column);

				Component component = super.prepareRenderer(renderer, row, column);
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(15);
				return component;
			}
		};
		tabGroups.setFillsViewportHeight(true);
		tabGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabGroups.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateElementList();
			}
		});
		scrollPane.setViewportView(tabGroups);
		
		edFilter = new JTextField();
		edFilter.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				onFilter();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				onFilter();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				onFilter();
			}
		});
		contentPane.add(edFilter, "6, 1, fill, default"); //$NON-NLS-1$
		edFilter.setColumns(10);
		
		panel = new JPanel();
		contentPane.add(panel, "2, 1, 1, 3, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("41px"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("26px:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		btnMoveUp = new JButton("^"); //$NON-NLS-1$
		btnMoveUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onMoveUp();
			}
		});
		panel.add(btnMoveUp, "1, 3, left, top"); //$NON-NLS-1$
		
		btnMoveDown = new JButton("v"); //$NON-NLS-1$
		btnMoveDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onMoveDown();
			}
		});
		panel.add(btnMoveDown, "1, 5, left, top"); //$NON-NLS-1$
		
		scrollPane_1 = new JScrollPane();
		contentPane.add(scrollPane_1, "6, 3, fill, fill"); //$NON-NLS-1$
		
		listElements = new JCheckBoxList<>(new StringDisplayConverter<CCDatabaseElement>() {
			@Override
			public String toDisplayString(CCDatabaseElement value) {
				return value.getFullDisplayTitle();
			}
		});
		scrollPane_1.setViewportView(listElements);
		
		pnlButtonLeft = new JPanel();
		contentPane.add(pnlButtonLeft, "2, 5, 3, 1, fill, fill"); //$NON-NLS-1$
		
		btnRename = new JButton(LocaleBundle.getString("GroupManagerFrame.btnRename")); //$NON-NLS-1$
		btnRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onRename();
			}
		});
		pnlButtonLeft.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(80dlu;default):grow"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("max(80dlu;default):grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("26px"), //$NON-NLS-1$
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("26px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		pnlButtonLeft.add(btnRename, "1, 1, fill, top"); //$NON-NLS-1$
		
		btnToggleSerialization = new JButton(LocaleBundle.getString("GroupManagerFrame.btnToggleSerialization")); //$NON-NLS-1$
		btnToggleSerialization.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onToggleSerialization();
			}
		});
		pnlButtonLeft.add(btnToggleSerialization, "3, 1, fill, top"); //$NON-NLS-1$
		
		btnDelete = new JButton(LocaleBundle.getString("GroupManagerFrame.btnDelete")); //$NON-NLS-1$
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDelete();
			}
		});
		pnlButtonLeft.add(btnDelete, "1, 3, fill, default"); //$NON-NLS-1$
		
		btnSetColor = new JButton(LocaleBundle.getString("GroupManagerFrame.btnSetColor")); //$NON-NLS-1$
		btnSetColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onSetColor();
			}
		});
		pnlButtonLeft.add(btnSetColor, "3, 3, fill, top"); //$NON-NLS-1$
		
		btnResetColors = new JButton("Reset Colors"); //$NON-NLS-1$
		btnResetColors.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onResetColors();
			}
		});
		pnlButtonLeft.add(btnResetColors, "1, 5"); //$NON-NLS-1$
		
		pnlButtonRight = new JPanel();
		FlowLayout fl_pnlButtonRight = (FlowLayout) pnlButtonRight.getLayout();
		fl_pnlButtonRight.setAlignment(FlowLayout.RIGHT);
		contentPane.add(pnlButtonRight, "6, 5, right, bottom"); //$NON-NLS-1$
		
		btnUpdate = new JButton(LocaleBundle.getString("GroupManagerFrame.btnUpdate")); //$NON-NLS-1$
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onUpdate();
			}
		});
		pnlButtonRight.add(btnUpdate);
	}

	private void onResetColors() {
		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.ChangeGroup")) return; //$NON-NLS-1$

		List<CCGroup> list = movielist.getSortedGroupList();
		
		for (int i = 0; i < list.size(); i++) {
			CCGroup g = list.get(i);
			
			movielist.updateGroup(g, CCGroup.create(g.Name, g.Order, CCGroup.TAG_COLORS[i % CCGroup.TAG_COLORS.length], g.DoSerialize));
		}
				
		reinitData();
	}
	
	private void onMoveUp() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;
		
		List<CCGroup> list = movielist.getSortedGroupList();
		
		int idx = list.indexOf(group);
		if (idx <= 0) return;
		
		list.remove(idx);
		list.add(idx-1, group);
		
		for (int i = 0; i < list.size(); i++) {
			CCGroup g = list.get(i);
			
			movielist.updateGroup(g, CCGroup.create(g.Name, 100 + i*10, g.Color, g.DoSerialize));
		}
		
		reinitData();
		
		tabGroups.getSelectionModel().setSelectionInterval(idx-1, idx-1);
	}
	
	private void onMoveDown() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;
		
		List<CCGroup> list = movielist.getSortedGroupList();
		
		int idx = list.indexOf(group);
		if (idx < 0 || idx == list.size()-1) return;
		
		list.remove(idx);
		list.add(idx+1, group);
		
		for (int i = 0; i < list.size(); i++) {
			CCGroup g = list.get(i);
			
			movielist.updateGroup(g, CCGroup.create(g.Name, 100 + i*10, g.Color, g.DoSerialize));
		}
		
		reinitData();
		
		tabGroups.getSelectionModel().setSelectionInterval(idx+1, idx+1);
	}
	
	private void onSetColor() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;

		Color newColor = JColorChooser.showDialog(null, LocaleBundle.getString("GroupManagerFrame.ChooseColorDialog"), group.Color); //$NON-NLS-1$
		if (newColor == null) return;

		movielist.updateGroup(group, CCGroup.create(group.Name, group.Order, newColor, group.DoSerialize));

		reinitData();
	}
	
	private void onToggleSerialization() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;
		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.ChangeGroup")) return; //$NON-NLS-1$

		movielist.updateGroup(group, CCGroup.create(group.Name, group.Order, group.Color, !group.DoSerialize));
		
		reinitData();
	}

	private void onUpdate() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;
		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.UpdateGroup")) return; //$NON-NLS-1$

		for (CCDatabaseElement elem : movielist.getInternalListCopy()) {
			if (listElements.getChecked(elem)) {
				elem.setGroups(elem.getGroups().getAdd(group));
			} else {
				elem.setGroups(elem.getGroups().getRemove(group));
			}
		}
	}

	private void onDelete() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;
		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.DeleteGroup")) return; //$NON-NLS-1$
		
		for (CCDatabaseElement el : new ArrayList<>(movielist.getDatabaseElementsbyGroup(group))) {
			el.setGroups(el.getGroups().getRemove(group));
		}

		initData();
	}

	private void onRename() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;

		String newName = DialogHelper.showLocalInputDialog(this, "Dialogs.NewGroupName_caption", group.Name); //$NON-NLS-1$
		if (newName == null || !CCGroup.isValidGroupName(newName)) return;

		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.RenameGroup")) return; //$NON-NLS-1$
		
		for (CCDatabaseElement el : new ArrayList<>(movielist.getDatabaseElementsbyGroup(group))) {
			el.setGroups(el.getGroups().getRemove(group).getAdd(movielist, newName));
		}
		
		reinitData();
	}

	private void onFilter() {
		if (edFilter.getText().trim().isEmpty()) {
			listElements.setFilter(new CBFilter() {
				@Override
				public boolean accept(JCheckBox element) {
					return true;
				}
			});
		} else {
			final String txt = edFilter.getText().toLowerCase();
			listElements.setFilter(new CBFilter() {
				@Override
				public boolean accept(JCheckBox element) {
					return element.getText().toLowerCase().contains(txt);
				}
			});
		}
	}

	protected void updateElementList() {
		CCGroup g = getSelectedGroup();
		if (g != null) {
			for (CCDatabaseElement elem : movielist.getInternalListCopy()) {
				listElements.setCheckedFast(elem, elem.getGroups().contains(g));
			}
			listElements.repaint();
		}
		
		edFilter.setText(""); //$NON-NLS-1$
	}

	private CCGroup getSelectedGroup() {
		int idx = tabGroups.getSelectedRow();
		if (idx < 0) return null;
		
		return tabGroupsData.get(idx);
	}
	
	private void initData() {
		movielist.recalculateGroupCache(true);
		
		reinitData();
	}

	private void reinitData() {
		tabGroupsData = movielist.getSortedGroupList();
		
		AbstractTableModel tmGroups = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (rowIndex < 0 || rowIndex >= tabGroupsData.size()) return null;
				
				CCGroup group = tabGroupsData.get(rowIndex);
				
				if (columnIndex == 0) return group.Color;
				
				if (columnIndex == 1) return group.Name;
				
				if (columnIndex == 2) return group.DoSerialize 
						? LocaleBundle.getString("ImportElementsFrame.common.bool_true")   //$NON-NLS-1$	
						: LocaleBundle.getString("ImportElementsFrame.common.bool_false"); //$NON-NLS-1$		
				
				return null;
			}
			
			@Override
			public int getRowCount() {
				return tabGroupsData.size();
			}
			
			@Override
			public int getColumnCount() {
				return 3;
			}
		};
		
		tabGroups.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 595606865173898458L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component sup = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				
				if (value instanceof Color) {
					JPanel pnl = new JPanel();
					
					pnl.setBackground((Color)value);
					
					JPanel pnl2 = new JPanel(new BorderLayout());
					pnl2.setBorder(new EmptyBorder(2, 2, 2, 2));
					pnl2.add(pnl, BorderLayout.CENTER);
					pnl2.setBackground(sup.getBackground());
					
					return pnl2;
				}

				return sup;
			}
		});
		
		tabGroups.setModel(tmGroups);
		
		tabGroups.getColumnModel().getColumn(0).setHeaderValue(LocaleBundle.getString("GroupManagerFrame.colColor")); //$NON-NLS-1$
		tabGroups.getColumnModel().getColumn(1).setHeaderValue(LocaleBundle.getString("GroupManagerFrame.colName")); //$NON-NLS-1$
		tabGroups.getColumnModel().getColumn(2).setHeaderValue(LocaleBundle.getString("GroupManagerFrame.colSerialization")); //$NON-NLS-1$
		
		//-----------------------------------------------------------------------------
		
		listElements.clear();
		listElements.addAll(movielist.getInternalListCopy());
	}
}
