package de.jClipCorn.gui.frames.groupManageFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.CheckBoxChangedActionListener;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class GroupManageFrame extends JFrame {
	private final CCMovieList movielist;

	public GroupManageFrame(CCMovieList ml, Component owner) {
		super();

		movielist = ml;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit() {
		setIconImage(Resources.IMG_FRAME_ICON.get());

		edFilter.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::onFilter));
		tabGroups.addListSelectionListener(e -> updateElementList());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				movielist.fireOnRefresh();
			}
		});

		initData();
	}

	private void onResetColors() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;

		List<CCGroup> list = movielist.getSortedGroupList();

		for (int i = 0; i < list.size(); i++) {
			CCGroup g = list.get(i);

			if (g == group) {
				pnlDataColor.setBackground(CCGroup.TAG_COLORS[i % CCGroup.TAG_COLORS.length]);
				return;
			}
		}
	}

	private void onAddGroup() {
		String name = DialogHelper.showLocalInputDialog(this, "GroupManagerFrame.addGroupDialogText", ""); //$NON-NLS-1$ //$NON-NLS-2$

		if (name == null || name.isEmpty()) return;

		if (!CCGroup.isValidGroupName(name)) {
			DialogHelper.showLocalError(this, "Dialogs.WrongGroupName"); //$NON-NLS-1$
			return;
		}

		CCGroup search = movielist.getGroupOrNull(name);
		if (search != null) {
			DialogHelper.showLocalError(this, "Dialogs.WrongGroupName"); //$NON-NLS-1$
			return;
		}

		movielist.addGroup(CCGroup.create(name));

		reinitData();
	}

	private void onMoveUp() {
		onMove(-1);
	}

	private void onMoveDown() {
		onMove(+1);
	}

	private void onMoveUpFast() {
		onMove(-Math.max(5, movielist.getGroupCount()/16));
	}

	private void onMoveDownFast() {
		onMove(+Math.max(5, movielist.getGroupCount()/16));
	}

	private void onMove(int delta) {
		CCGroup group = getSelectedGroup();
		if (group == null) return;

		List<CCGroup> list = movielist.getSortedGroupList();

		int idx = list.indexOf(group);
		int newindex = idx + delta;

		if (newindex < 0) newindex = 0;
		if (newindex >= list.size()) newindex = list.size()-1;

		if (newindex == idx) return;

		list.remove(idx);
		list.add(newindex, group);

		for (int i = 0; i < list.size(); i++) {
			CCGroup g = list.get(i);

			if (g.Order == (100 + i*10)) continue;

			movielist.updateGroup(g, CCGroup.create(g.Name, 100 + i*10, g.Color, g.DoSerialize, g.Parent, g.Visible));
		}

		reinitData();

		tabGroups.setSelectedRow(newindex);
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

	private void onUpdateData() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;
		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.EditGroup")) return; //$NON-NLS-1$

		movielist.updateGroup(group, CCGroup.create(
				group.Name,
				group.Order,
				pnlDataColor.getBackground(),
				cbDataSerialization.isSelected(),
				cbxDataParent.getSelectedItem().toString(),
				cbDataVisible.isSelected()));

		if (!group.Name.equals(edDataName.getText())) {

			if (!CCGroup.isValidGroupName(edDataName.getText())) {
				DialogHelper.showLocalError(this, "Dialogs.WrongGroupName"); //$NON-NLS-1$
				return;
			}

			CCGroup search = movielist.getGroupOrNull(edDataName.getText());
			if (search != null && search != group) {
				DialogHelper.showLocalError(this, "Dialogs.WrongGroupName"); //$NON-NLS-1$
				return;
			}

			CCGroup gOld = movielist.getGroupOrNull(group.Name);

			if (gOld != null && DialogHelper.showLocaleYesNo(this, "Dialogs.RenameGroup")) { //$NON-NLS-1$

				movielist.updateGroup(gOld, CCGroup.create(edDataName.getText(), gOld.Order, gOld.Color, gOld.DoSerialize, gOld.Parent, gOld.Visible));

				for (CCDatabaseElement el : new ArrayList<>(movielist.getDatabaseElementsbyGroup(group))) {
					el.setGroups(el.getGroups().getRemove(group).getAdd(movielist, edDataName.getText()));
				}

				for (CCGroup og : movielist.getGroupList()) {
					if (og.Parent.equals(gOld.Name)) {
						movielist.updateGroup(og, CCGroup.create(og.Name, og.Order, og.Color, og.DoSerialize, edDataName.getText(), og.Visible));
					}
				}
			}
		}

		reinitData();
	}

	private void onDelete() {
		CCGroup group = getSelectedGroup();
		if (group == null) return;
		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.DeleteGroup")) return; //$NON-NLS-1$

		movielist.removeGroup(group);

		initData();
	}

	private void onFilter() {
		if (edFilter.getText().trim().isEmpty()) {
			if (edFilterOnlyActive.isSelected()) {
				listElements.setFilter(AbstractButton::isSelected);
			} else {
				listElements.setFilter(element -> true);
			}
		} else {
			final String txt = edFilter.getText().toLowerCase();

			if (edFilterOnlyActive.isSelected()) {
				listElements.setFilter(element -> element.isSelected() && element.getText().toLowerCase().contains(txt));
			} else {
				listElements.setFilter(element -> element.getText().toLowerCase().contains(txt));
			}
		}
	}

	private void updateElementList() {
		CCGroup g = getSelectedGroup();
		if (g != null) {

			for (CCDatabaseElement elem : movielist.getInternalListCopy()) {
				listElements.setCheckedFast(elem, elem.getGroups().contains(g));
			}
			listElements.repaint();

			edDataName.setText(g.Name);
			pnlDataColor.setBackground(g.Color);
			cbDataSerialization.setSelected(g.DoSerialize);
			List<String> cbxdata = CCStreams.iterate(movielist.getSortedGroupList()).map(p -> p.Name).prepend(g.Parent).autosort().prepend("").unique().enumerate(); //$NON-NLS-1$
			cbxDataParent.setModel(new DefaultComboBoxModel<>(cbxdata.toArray(new String[0])));
			cbxDataParent.setSelectedIndex(CCStreams.iterate(cbxdata).findIndex(p -> p.equals(g.Parent)));
			cbDataVisible.setSelected(g.Visible);

			edDataName.setEnabled(true);
			pnlDataColor.setEnabled(true);
			cbDataSerialization.setEnabled(true);
			cbxDataParent.setEnabled(true);
			pnlData.setEnabled(true);
			cbDataVisible.setEnabled(true);

			pnlData.repaint();

		} else {

			edDataName.setText(""); //$NON-NLS-1$
			pnlDataColor.setBackground(Color.WHITE);
			cbDataSerialization.setSelected(false);
			cbxDataParent.setModel(new DefaultComboBoxModel<>(new String[] {""})); //$NON-NLS-1$
			cbxDataParent.setSelectedItem(""); //$NON-NLS-1$
			cbDataVisible.setSelected(false);

			edDataName.setEnabled(false);
			pnlDataColor.setEnabled(false);
			cbDataSerialization.setEnabled(false);
			cbxDataParent.setEnabled(false);
			cbDataVisible.setEnabled(false);

			pnlData.setEnabled(false);

		}

		edFilter.setText(""); //$NON-NLS-1$
	}

	private void onSetColor() {
		if (!pnlDataColor.isEnabled()) return;

		CCGroup group = getSelectedGroup();
		if (group == null) return;

		Color newColor = JColorChooser.showDialog(null, LocaleBundle.getString("GroupManagerFrame.ChooseColorDialog"), group.Color); //$NON-NLS-1$
		if (newColor == null) return;

		pnlDataColor.setBackground(newColor);
	}

	private CCGroup getSelectedGroup() {
		var d = tabGroups.getSelectedElement();
		if (d == null) return null;

		return d.Item1;
	}

	private void initData() {
		//movielist.recalculateGroupCache(true);

		reinitData();

		onFilter();

		updateElementList();
	}

	private void reinitData() {
		tabGroups.setData(CCStreams.iterate(movielist.getSortedGroupList()).map(g -> Tuple.Create(g, movielist.getDatabaseElementsbyGroup(g).size())).toList());

		//-----------------------------------------------------------------------------

		listElements.clear();
		listElements.addAll(movielist.getInternalListCopy());
	}

	private void onElementsListChecked(CheckBoxChangedActionListener.CheckBoxChangedEvent<CCDatabaseElement> e) {
		CCGroup group = getSelectedGroup();
		if (group == null) return;

		if (e.NewValue) {
			e.Data.setGroups(e.Data.getGroups().getAdd(group));
		} else {
			e.Data.setGroups(e.Data.getGroups().getRemove(group));
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel2 = new JPanel();
		btnMoveUpFast = new JButton();
		btnMoveUp = new JButton();
		btnMoveDown = new JButton();
		btnMoveDownFast = new JButton();
		btnAdd = new JButton();
		edFilterOnlyActive = new JCheckBox();
		edFilter = new JTextField();
		tabGroups = new GroupManageTable();
		scrollPane1 = new JScrollPane();
		listElements = new GroupManagerCheckBoxList();
		pnlData = new JPanel();
		label1 = new JLabel();
		edDataName = new JTextField();
		label2 = new JLabel();
		cbDataSerialization = new JCheckBox();
		label3 = new JLabel();
		pnlDataColor = new JPanel();
		btnResetColors = new JButton();
		label4 = new JLabel();
		cbxDataParent = new JComboBox<>();
		label5 = new JLabel();
		cbDataVisible = new JCheckBox();
		btnDelete = new JButton();
		btnUpdateData = new JButton();

		//======== this ========
		setMinimumSize(new Dimension(500, 300));
		setTitle(LocaleBundle.getString("GroupManagerFrame.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$lcgap, default, $lcgap, 325dlu, $lcgap, default, $lcgap, default:grow, $lcgap", //$NON-NLS-1$
			"$lgap, default, $lgap, default:grow, $lgap, default, $lgap")); //$NON-NLS-1$

		//======== panel2 ========
		{
			panel2.setLayout(new FormLayout(
				"default", //$NON-NLS-1$
				"default:grow, 2*($lgap, default), $ugap, default, $lgap, default, $pgap, default, $lgap, default:grow")); //$NON-NLS-1$

			//---- btnMoveUpFast ----
			btnMoveUpFast.setText("\u25b2\u25b2"); //$NON-NLS-1$
			btnMoveUpFast.setMargin(new Insets(2, 4, 2, 4));
			btnMoveUpFast.addActionListener(e -> onMoveUpFast());
			panel2.add(btnMoveUpFast, CC.xy(1, 3));

			//---- btnMoveUp ----
			btnMoveUp.setText("\u25b2"); //$NON-NLS-1$
			btnMoveUp.setMargin(new Insets(2, 4, 2, 4));
			btnMoveUp.addActionListener(e -> onMoveUp());
			panel2.add(btnMoveUp, CC.xy(1, 5));

			//---- btnMoveDown ----
			btnMoveDown.setText("\u25bc"); //$NON-NLS-1$
			btnMoveDown.setMargin(new Insets(2, 4, 2, 4));
			btnMoveDown.addActionListener(e -> onMoveDown());
			panel2.add(btnMoveDown, CC.xy(1, 7));

			//---- btnMoveDownFast ----
			btnMoveDownFast.setText("\u25bc\u25bc"); //$NON-NLS-1$
			btnMoveDownFast.setMargin(new Insets(2, 4, 2, 4));
			btnMoveDownFast.addActionListener(e -> onMoveDownFast());
			panel2.add(btnMoveDownFast, CC.xy(1, 9));

			//---- btnAdd ----
			btnAdd.setText("+"); //$NON-NLS-1$
			btnAdd.setMargin(new Insets(2, 12, 2, 12));
			btnAdd.addActionListener(e -> onAddGroup());
			panel2.add(btnAdd, CC.xy(1, 11));
		}
		contentPane.add(panel2, CC.xywh(2, 2, 1, 3, CC.FILL, CC.FILL));

		//---- edFilterOnlyActive ----
		edFilterOnlyActive.addActionListener(e -> onFilter());
		contentPane.add(edFilterOnlyActive, CC.xy(6, 2));
		contentPane.add(edFilter, CC.xy(8, 2));
		contentPane.add(tabGroups, CC.xy(4, 4, CC.FILL, CC.FILL));

		//======== scrollPane1 ========
		{

			//---- listElements ----
			listElements.addCheckBoxChangedActionListener(e -> onElementsListChecked(e));
			scrollPane1.setViewportView(listElements);
		}
		contentPane.add(scrollPane1, CC.xywh(6, 4, 3, 3, CC.DEFAULT, CC.FILL));

		//======== pnlData ========
		{
			pnlData.setBorder(new TitledBorder(LocaleBundle.getString("GroupManagerFrame.hdrData"))); //$NON-NLS-1$
			pnlData.setLayout(new FormLayout(
				"[65dlu,default], $lcgap, default:grow, $lcgap, default", //$NON-NLS-1$
				"5*(default, $lgap), default")); //$NON-NLS-1$

			//---- label1 ----
			label1.setText(LocaleBundle.getString("GroupManagerFrame.colName")); //$NON-NLS-1$
			pnlData.add(label1, CC.xy(1, 1, CC.RIGHT, CC.DEFAULT));
			pnlData.add(edDataName, CC.xy(3, 1));

			//---- label2 ----
			label2.setText(LocaleBundle.getString("GroupManagerFrame.colSerialization")); //$NON-NLS-1$
			pnlData.add(label2, CC.xy(1, 3, CC.RIGHT, CC.DEFAULT));
			pnlData.add(cbDataSerialization, CC.xy(3, 3));

			//---- label3 ----
			label3.setText(LocaleBundle.getString("GroupManagerFrame.colColor")); //$NON-NLS-1$
			pnlData.add(label3, CC.xy(1, 5, CC.RIGHT, CC.DEFAULT));

			//======== pnlDataColor ========
			{
				pnlDataColor.setBackground(Color.yellow);
				pnlDataColor.setBorder(new LineBorder(UIManager.getColor("ToolBar.borderColor"))); //$NON-NLS-1$
				pnlDataColor.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						onSetColor();
					}
				});
				pnlDataColor.setLayout(null);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < pnlDataColor.getComponentCount(); i++) {
						Rectangle bounds = pnlDataColor.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = pnlDataColor.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					pnlDataColor.setMinimumSize(preferredSize);
					pnlDataColor.setPreferredSize(preferredSize);
				}
			}
			pnlData.add(pnlDataColor, CC.xy(3, 5, CC.FILL, CC.FILL));

			//---- btnResetColors ----
			btnResetColors.setText(LocaleBundle.getString("GroupManagerFrame.btnResetColors")); //$NON-NLS-1$
			btnResetColors.addActionListener(e -> onResetColors());
			pnlData.add(btnResetColors, CC.xy(5, 5));

			//---- label4 ----
			label4.setText(LocaleBundle.getString("GroupManagerFrame.colParent")); //$NON-NLS-1$
			pnlData.add(label4, CC.xy(1, 7, CC.RIGHT, CC.DEFAULT));
			pnlData.add(cbxDataParent, CC.xy(3, 7));

			//---- label5 ----
			label5.setText(LocaleBundle.getString("GroupManagerFrame.lblVisible")); //$NON-NLS-1$
			pnlData.add(label5, CC.xy(1, 9, CC.RIGHT, CC.DEFAULT));
			pnlData.add(cbDataVisible, CC.xy(3, 9));

			//---- btnDelete ----
			btnDelete.setText(LocaleBundle.getString("GroupManagerFrame.btnDelete")); //$NON-NLS-1$
			btnDelete.addActionListener(e -> onDelete());
			pnlData.add(btnDelete, CC.xy(1, 11));

			//---- btnUpdateData ----
			btnUpdateData.setText(LocaleBundle.getString("GroupManagerFrame.btnUpdate2")); //$NON-NLS-1$
			btnUpdateData.addActionListener(e -> onUpdateData());
			pnlData.add(btnUpdateData, CC.xy(5, 11));
		}
		contentPane.add(pnlData, CC.xywh(2, 6, 3, 1));
		setSize(1000, 700);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel2;
	private JButton btnMoveUpFast;
	private JButton btnMoveUp;
	private JButton btnMoveDown;
	private JButton btnMoveDownFast;
	private JButton btnAdd;
	private JCheckBox edFilterOnlyActive;
	private JTextField edFilter;
	private GroupManageTable tabGroups;
	private JScrollPane scrollPane1;
	private GroupManagerCheckBoxList listElements;
	private JPanel pnlData;
	private JLabel label1;
	private JTextField edDataName;
	private JLabel label2;
	private JCheckBox cbDataSerialization;
	private JLabel label3;
	private JPanel pnlDataColor;
	private JButton btnResetColors;
	private JLabel label4;
	private JComboBox<String> cbxDataParent;
	private JLabel label5;
	private JCheckBox cbDataVisible;
	private JButton btnDelete;
	private JButton btnUpdateData;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
