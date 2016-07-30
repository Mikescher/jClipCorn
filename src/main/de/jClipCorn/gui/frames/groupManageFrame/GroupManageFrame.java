package de.jClipCorn.gui.frames.groupManageFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.guiComponents.StringDisplayConverter;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.CBListModel.CBFilter;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.JCheckBoxList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.DialogHelper;

public class GroupManageFrame extends JFrame {
	private static final long serialVersionUID = -5967105649409137866L;
	
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JList<CCGroup> listGroups;
	private JScrollPane scrollPane_1;
	private JCheckBoxList<CCDatabaseElement> listElements;
	private JPanel panel;
	private JButton btnDelete;
	private JButton btnRename;
	private JPanel panel_1;
	private JButton btnUpdate;
	private JTextField edFilter;

	private final CCMovieList movielist;
	
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
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("max(0dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "1, 1, 1, 3, fill, fill"); //$NON-NLS-1$
		
		listGroups = new JList<>();
		listGroups.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateElementList();
			}
		});
		listGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(listGroups);
		
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
		contentPane.add(edFilter, "3, 1, fill, default"); //$NON-NLS-1$
		edFilter.setColumns(10);
		
		scrollPane_1 = new JScrollPane();
		contentPane.add(scrollPane_1, "3, 3, fill, fill"); //$NON-NLS-1$
		
		listElements = new JCheckBoxList<>(new StringDisplayConverter<CCDatabaseElement>() {
			@Override
			public String toDisplayString(CCDatabaseElement value) {
				return value.getFullDisplayTitle();
			}
		});
		scrollPane_1.setViewportView(listElements);
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panel, "1, 5, fill, fill"); //$NON-NLS-1$
		
		btnRename = new JButton(LocaleBundle.getString("GroupManagerFrame.btnRename")); //$NON-NLS-1$
		btnRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onRename();
			}
		});
		panel.add(btnRename);
		
		btnDelete = new JButton(LocaleBundle.getString("GroupManagerFrame.btnDelete")); //$NON-NLS-1$
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onDelete();
			}
		});
		panel.add(btnDelete);
		
		panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		contentPane.add(panel_1, "3, 5, fill, fill"); //$NON-NLS-1$
		
		btnUpdate = new JButton(LocaleBundle.getString("GroupManagerFrame.btnUpdate")); //$NON-NLS-1$
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onUpdate();
			}
		});
		panel_1.add(btnUpdate);
	}

	protected void onUpdate() {
		CCGroup group = listGroups.getSelectedValue();
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

	protected void onDelete() {
		CCGroup group = listGroups.getSelectedValue();
		if (group == null) return;
		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.DeleteGroup")) return; //$NON-NLS-1$
		
		for (CCDatabaseElement el : new ArrayList<>(movielist.getDatabaseElementsbyGroup(group))) {
			el.setGroups(el.getGroups().getRemove(group));
		}

		initData();
	}

	protected void onRename() {
		CCGroup group = listGroups.getSelectedValue();
		if (group == null) return;

		String newName = DialogHelper.showLocalInputDialog(this, "Dialogs.NewGroupName_caption", group.Name); //$NON-NLS-1$
		if (newName == null || !CCGroup.isValidGroupName(newName)) return;

		if (!DialogHelper.showLocaleYesNo(this, "Dialogs.RenameGroup")) return; //$NON-NLS-1$
		
		for (CCDatabaseElement el : new ArrayList<>(movielist.getDatabaseElementsbyGroup(group))) {
			el.setGroups(el.getGroups().getRemove(group).getAdd(movielist, newName));
		}
		
		initData();
	}

	protected void onFilter() {
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
		CCGroup g = listGroups.getSelectedValue();
		if (g != null) {
			for (CCDatabaseElement elem : movielist.getInternalListCopy()) {
				listElements.setCheckedFast(elem, elem.getGroups().contains(g));
			}
			listElements.repaint();
		}
		
		edFilter.setText(""); //$NON-NLS-1$
	}

	private void initData() {
		movielist.recalculateGroupCache(true);
		
		DefaultListModel<CCGroup> lmGroups = new DefaultListModel<>();
		
		for (CCGroup group : movielist.getGroupList()) {
			lmGroups.addElement(group);
		}
		
		listGroups.setModel(lmGroups);
		
		//-----------------------------------------------------------------------------
		
		listElements.clear();
		listElements.addAll(movielist.getInternalListCopy());
	}
}
