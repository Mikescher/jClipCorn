package de.jClipCorn.gui.frames.customFilterEditDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.mainFrame.filterTree.CustomFilterObject;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.customFilter.operators.CustomOperator;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.stream.CCStreams;

public class CustomFilterEditDialog extends JDialog {
	private static final long serialVersionUID = -3117017940124236602L;

	private final CustomFilterObject filterObject;
	private CustomFilterEditTreeNode root;
	private final CCMovieList movielist;
	private final FinishListener finListener;
	
	private JPanel contentPane;
	private JPanel pnlBottom;
	private JButton btnOK;
	private JButton btnExport;
	private JButton btnImport;
	private JTextField edName;
	private JScrollPane pnlLeft;
	private JTree treeMain;
	private JPanel pnlRightTop;
	private JPanel pnlRight;
	private JPanel panel;
	private JButton btnRemove;
	private JLabel lblCaption;
	private JPanel pnlCenter;
	private JButton btnClear;
	private JPanel panel_1;
	private JButton btnCancel;
	
	public CustomFilterEditDialog(Component owner, CCMovieList ml,  CustomFilterObject filter, FinishListener fl) {
		super();
		
		filterObject = filter;
		movielist = ml;
		finListener = fl;
		
		init();
		
		initGUI();
		
		expandAllNodes(treeMain, 0, treeMain.getRowCount());
		
		pnlRightTop = new JPanel();
		pnlCenter.add(pnlRightTop);
		pnlRightTop.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("162px:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("fill:10px:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblCaption = new JLabel(""); //$NON-NLS-1$
		lblCaption.setHorizontalAlignment(SwingConstants.CENTER);
		lblCaption.setFont(new Font("Dialog", Font.BOLD, 16)); //$NON-NLS-1$
		pnlRightTop.add(lblCaption, "2, 1"); //$NON-NLS-1$
		
		pnlRight = new JPanel();
		pnlRightTop.add(pnlRight, "2, 3, fill, top"); //$NON-NLS-1$
		pnlRight.setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		pnlRightTop.add(panel, "2, 5, fill, fill"); //$NON-NLS-1$
		
		btnRemove = new JButton(LocaleBundle.getString("CustomFilterEditDialog.Remove")); //$NON-NLS-1$
		btnRemove.addActionListener(e -> RemoveSelectedFilter());
		panel.add(btnRemove);
		
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Edit custom filter"); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		edName = new JTextField(filterObject.getName());
		edName.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) { filterObject.setName(edName.getText()); }
			
			@Override
			public void insertUpdate(DocumentEvent e) { filterObject.setName(edName.getText()); }
			
			@Override
			public void changedUpdate(DocumentEvent e) { filterObject.setName(edName.getText()); }
		});
		contentPane.add(edName, "2, 2, fill, default"); //$NON-NLS-1$
		edName.setColumns(10);
		
		pnlCenter = new JPanel();
		contentPane.add(pnlCenter, "2, 4, fill, fill"); //$NON-NLS-1$
		pnlCenter.setLayout(new GridLayout(1, 2, 0, 0));
		
		pnlLeft = new JScrollPane();
		pnlLeft.setViewportBorder(new EmptyBorder(2, 2, 2, 2));
		pnlCenter.add(pnlLeft);
		
		treeMain = new JTree(root);
		treeMain.setRootVisible(false);
		treeMain.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeMain.getSelectionModel().addTreeSelectionListener(e -> OnSelectionChanged(e));
		treeMain.setCellRenderer(new CustomFilterEditTreeRenderer());
		pnlLeft.setViewportView(treeMain);
		
		pnlBottom = new JPanel();
		contentPane.add(pnlBottom, "2, 6, fill, fill"); //$NON-NLS-1$
		pnlBottom.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.DEFAULT_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		btnClear = new JButton(LocaleBundle.getString("UIGeneric.btnClear.text")); //$NON-NLS-1$
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterObject.getFilter().removeAll();
				updateTree();
			}
		});
		pnlBottom.add(btnClear, "1, 1"); //$NON-NLS-1$
		
		btnExport = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnExport.text")); //$NON-NLS-1$
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DialogHelper.showPlainInputDialog(CustomFilterEditDialog.this, filterObject.getFilter().exportToString());
			}
		});
		
		panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setVgap(0);
		pnlBottom.add(panel_1, "2, 1, fill, fill"); //$NON-NLS-1$
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel_1.add(btnCancel);
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		panel_1.add(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
								
				if (finListener != null) finListener.finish();

				dispose();
			}
		});
		pnlBottom.add(btnExport, "3, 1"); //$NON-NLS-1$
		
		btnImport = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnImport.text")); //$NON-NLS-1$
		btnImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String imp = DialogHelper.showPlainInputDialog(CustomFilterEditDialog.this);
				
				AbstractCustomFilter copy = filterObject.getFilter().createCopy();
				
				if (imp != null && !copy.importFromString(imp)) {
					DialogHelper.showLocalError(CustomFilterEditDialog.this, "Dialogs.CustomFilterImportFailed"); //$NON-NLS-1$
					return;
				} 
				
				filterObject.setFilter((CustomOperator)copy);
				
				updateTree();
			}
		});
		pnlBottom.add(btnImport, "5, 1"); //$NON-NLS-1$

		setSize(800, 500);
	}

	private void RemoveSelectedFilter() {
		TreePath p = treeMain.getSelectionPath();
		if (p == null) return;

		Object node = p.getLastPathComponent();
		if (!(node instanceof CustomFilterEditTreeNode)) return;
		
		AbstractCustomFilter filter = ((CustomFilterEditTreeNode)node).filter;
		
		if (filter == filterObject.getFilter()) return;

		Object parentNode = p.getPathComponent(p.getPathCount()-2);
		if (!(parentNode instanceof CustomFilterEditTreeNode)) return;
		if (!(((CustomFilterEditTreeNode)parentNode).filter instanceof CustomOperator)) return;

		CustomOperator parentFilter = (CustomOperator)((CustomFilterEditTreeNode)parentNode).filter;
		
		parentFilter.remove(filter);
		
		updateTree();
	}

	private void OnSelectionChanged(TreeSelectionEvent e) {
		TreePath p = e.getNewLeadSelectionPath();
		
		if (p == null) {
			updateEditPanel(null);
			return;
		}
		
		Object node = p.getLastPathComponent();
		if (node instanceof CustomFilterEditTreeNode) {
			updateEditPanel(((CustomFilterEditTreeNode)node).filter);
		} else {
			updateEditPanel(null);
		}
	}
	
	private void updateEditPanel(AbstractCustomFilter f)
	{
		pnlRight.removeAll();
		
		JPanel pnl = new JPanel(new FormLayout(
				new ColumnSpec[] 
				{
					ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				},
				new RowSpec[] 
				{
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
					FormSpecs.DEFAULT_ROWSPEC,
					FormSpecs.RELATED_GAP_ROWSPEC,
					FormSpecs.DEFAULT_ROWSPEC,
					RowSpec.decode("default:grow"), //$NON-NLS-1$
					FormSpecs.DEFAULT_ROWSPEC,
					FormSpecs.RELATED_GAP_ROWSPEC,
				}));
	
		if (f == null) {

			lblCaption.setText(""); //$NON-NLS-1$
			
		} else if (f instanceof CustomOperator) {
			
			CustomOperator filter = (CustomOperator)f;
			createEditPanel_Operator(pnl, filter);
			lblCaption.setText(filter.getPrecreateName());
			
		} else {
			
			AbstractCustomFilter filter = f;
			createEditPanel_Simple(pnl, filter);
			lblCaption.setText(filter.getPrecreateName());
			
		}
		
		pnlRight.add(pnl, BorderLayout.CENTER);
		pnlRight.revalidate();
		pnl.revalidate();
		repaint();
	}

	private void createEditPanel_Operator(JPanel pnl, CustomOperator filter) {
		{
			JComboBox<AbstractCustomFilter> cbxFilter = new JComboBox<>();
			cbxFilter.setModel(new DefaultComboBoxModel<>(AbstractCustomFilter.iterateAllSimpleFilterSorted().toArray(new AbstractCustomFilter[0])));
			cbxFilter.setRenderer(new CustomFilterEditFilterComboboxRenderer());
			pnl.add(cbxFilter, "1, 2, fill, default"); //$NON-NLS-1$
			
			JButton btnAddFilter = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnAddFilter.text")); //$NON-NLS-1$
			btnAddFilter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cbxFilter.getSelectedIndex() >= 0) {
						filter.add(((AbstractCustomFilter)cbxFilter.getSelectedItem()).createNew());
						updateTree();
					}
				}
			});
			pnl.add(btnAddFilter, "1, 4"); //$NON-NLS-1$
		}
		
		{
			JComboBox<AbstractCustomFilter> cbxOperator = new JComboBox<>();
			cbxOperator.setModel(new DefaultComboBoxModel<>(AbstractCustomFilter.getAllOperatorFilter()));
			cbxOperator.setRenderer(new CustomFilterEditFilterComboboxRenderer());
			pnl.add(cbxOperator, "1, 6, fill, default"); //$NON-NLS-1$
			
			JButton btnAddOperator = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnAddOp.text")); //$NON-NLS-1$
			btnAddOperator.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cbxOperator.getSelectedIndex() >= 0) {
						filter.add(((AbstractCustomFilter)cbxOperator.getSelectedItem()).createNew());
						updateTree();
					}
				}
			});
			pnl.add(btnAddOperator, "1, 8"); //$NON-NLS-1$
		}
		
		{
			JComboBox<AbstractCustomFilter> cbxAggregator = new JComboBox<>();
			cbxAggregator.setModel(new DefaultComboBoxModel<>(AbstractCustomFilter.getAllAggregatorFilter()));
			cbxAggregator.setRenderer(new CustomFilterEditFilterComboboxRenderer());
			pnl.add(cbxAggregator, "1, 10, fill, default"); //$NON-NLS-1$
			
			JButton btnAddAggregator = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnAddAgg.text")); //$NON-NLS-1$
			btnAddAggregator.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (cbxAggregator.getSelectedIndex() >= 0) {
						filter.add(((AbstractCustomFilter)cbxAggregator.getSelectedItem()).createNew());
						updateTree();
					}
				}
			});
			pnl.add(btnAddAggregator, "1, 12"); //$NON-NLS-1$
		}
	}

	private void createEditPanel_Simple(JPanel pnl, AbstractCustomFilter filter) {
		
		int row = 2;
		for (CustomFilterConfig cfg : filter.createConfig(movielist)) {
			JComponent c = cfg.getComponent(() -> updateTree());

			pnl.add(c, "1, " + row + ", fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
			row += 2;
		}
		
	}
	
	private void init() {
		root = new CustomFilterEditTreeNode(filterObject.getFilter());
		
		updateTree();
	}
	
	private void updateTree() {
		
		DefaultTreeModel model = (treeMain != null) ? ((DefaultTreeModel)treeMain.getModel()) : (null);
		
		TreePath sel = null;
		if (treeMain != null) sel = treeMain.getSelectionPath();
		
		if (root.getChildCount() == 0) root.add(new CustomFilterEditTreeNode(filterObject.getFilter()));
		
		boolean changed = updateTree(model, filterObject.getFilter(), (CustomFilterEditTreeNode)root.getFirstChild(), root);
		
		if (changed && model != null && treeMain != null) {
			model.reload();
			
			treeMain.invalidate();
			treeMain.repaint();

			expandAllNodes(treeMain, 0, treeMain.getRowCount());
			
			if (sel != null) {

				treeMain.setSelectionPath(sel);
				
				if (treeMain.getRowForPath(sel) < 0) {
					updateEditPanel(null);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean updateTree(DefaultTreeModel model, AbstractCustomFilter filter, CustomFilterEditTreeNode node, CustomFilterEditTreeNode parent) {
		boolean changed = false;
		
		if (node == null) {
			node = new CustomFilterEditTreeNode(filter);
			parent.add(node);
			
			changed = true;
		} else if (node.filter != filter) {
			int idx = parent.getIndex(node);
			parent.remove(node);
			node = new CustomFilterEditTreeNode(filter);
			parent.insert(node, idx);
		}
		
		if (!node.textCache.equals(node.filter.getName())) { 
			node.textCache = node.filter.getName(); 
			if (model != null && !changed) model.nodeChanged(node); 
		}

		for (AbstractCustomFilter f : filter.getList()) {
			
			CustomFilterEditTreeNode n = null;
			for (Object mtn : CCStreams.iterate(node.children())) {
				if (!(mtn instanceof CustomFilterEditTreeNode)) continue;
				if (((CustomFilterEditTreeNode)mtn).filter == f) n = (CustomFilterEditTreeNode)mtn;
			}
			
			boolean sub = updateTree(model, f, n, node);
			
			changed |= sub;
		}
		
		for (Object mtn : CCStreams.iterate(node.children()).enumerate()) {
			if (!(mtn instanceof CustomFilterEditTreeNode)) continue;
			
			AbstractCustomFilter mtnFilter = ((CustomFilterEditTreeNode)mtn).filter;
			
			boolean found = CCStreams.iterate(filter.getList()).any(f -> f == mtnFilter);
			
			if (!found) { 
				node.remove((CustomFilterEditTreeNode)mtn); 
				changed = true; 
			}
		}
		
		return changed;
	}
	
	private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
	    for(int i=startingIndex;i<rowCount;++i){
	        tree.expandRow(i);
	    }

	    if(tree.getRowCount()!=rowCount){
	        expandAllNodes(tree, rowCount, tree.getRowCount());
	    }
	}
}
