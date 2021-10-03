package de.jClipCorn.gui.frames.customFilterEditDialog;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomOperator;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.filterTree.CustomFilterObject;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomFilterEditDialog extends JCCDialog {
	private final CustomFilterObject filterObject;
	private CustomFilterEditTreeNode root;
	private final FinishListener finListener;

	private final List<Tuple<CustomFilterConfig, JComponent>> _currentConfigEntries = new ArrayList<>();
	private AbstractCustomFilter _currentSelectedFilter = null;

	public CustomFilterEditDialog(Component owner, CCMovieList ml, CustomFilterObject filter, FinishListener fl) {
		super(ml);

		filterObject = filter;
		finListener = fl;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		root = new CustomFilterEditTreeNode(filterObject.getFilter());
		treeMain.setModel(new DefaultTreeModel(root));
		treeMain.setCellRenderer(new CustomFilterEditTreeRenderer());

		edName.setText(filterObject.getName());
		edName.getDocument().addDocumentListener(new DocumentLambdaAdapter(() -> filterObject.setName(edName.getText())));

		lblCaption.setText(Str.Empty);

		updateTree();

		expandAllNodes(treeMain, 0, treeMain.getRowCount());

		treeMain.setSelectionRow(0);
	}

	private void onClear() {
		filterObject.getFilter().removeAll();
		updateTree();
	}

	private void onExport() {
		DialogHelper.showPlainInputDialog(CustomFilterEditDialog.this, filterObject.getFilter().exportToString());
	}

	private void onCancel() {
		dispose();
	}

	private void onOkay() {
		if (finListener != null) finListener.finish();

		dispose();
	}

	private void onImport() {
		String imp = DialogHelper.showPlainInputDialog(CustomFilterEditDialog.this);

		AbstractCustomFilter copy = filterObject.getFilter().createCopy(movielist);

		if (imp != null && (copy == null || !copy.importFromString(imp))) {
			DialogHelper.showLocalError(CustomFilterEditDialog.this, "Dialogs.CustomFilterImportFailed"); //$NON-NLS-1$
			return;
		}

		filterObject.setFilter((CustomOperator)copy);

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

	private void updateEditPanel(AbstractCustomFilter f)
	{
		_currentSelectedFilter = f;

		pnlRight.removeAll();

		if (f == null) {
			_currentConfigEntries.clear();

			lblCaption.setText(""); //$NON-NLS-1$

			repaint();

		} else if (f instanceof CustomOperator) {
			_currentConfigEntries.clear();

			JPanel pnl = new JPanel(new FormLayout(
				new ColumnSpec[]
				{
						ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				},
				new RowSpec[]
				{
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,

						RowSpec.decode("default:grow"), //$NON-NLS-1$
				}));

			CustomOperator filter = (CustomOperator)f;
			createEditPanel_Operator(pnl, filter);
			lblCaption.setText(filter.getPrecreateName());

			pnlRight.add(pnl, BorderLayout.CENTER);
			pnlRight.revalidate();
			pnl.revalidate();
			repaint();

		} else {
			_currentConfigEntries.clear();

			CustomFilterConfig[] cfgarr = f.createConfig(movielist);

			boolean anyGrow = false;
			List<RowSpec> rs = new ArrayList<>();
			for (CustomFilterConfig cfg : cfgarr) {
				rs.add(FormSpecs.RELATED_GAP_ROWSPEC);
				rs.add(cfg.shouldGrow() ? RowSpec.decode("default:grow") : FormSpecs.DEFAULT_ROWSPEC); //$NON-NLS-1$
				if (cfg.shouldGrow()) anyGrow = true;
			}
			if (!anyGrow) rs.add(RowSpec.decode("default:grow")); //$NON-NLS-1$

			JPanel pnl = new JPanel(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("default:grow") }, rs.toArray(new RowSpec[0]))); //$NON-NLS-1$

			// createEditPanel_Simple
			{
				int row = 2;
				for (CustomFilterConfig cfg : cfgarr) {
					JComponent c = cfg.getComponent(this::updateSimple);

					if (cfg.shouldGrow()) pnl.add(c, "1, " + row + ", fill, fill");    //$NON-NLS-1$ //$NON-NLS-2$
					else                  pnl.add(c, "1, " + row + ", fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
					row += 2;

					_currentConfigEntries.add(Tuple.Create(cfg, c));

					cfg.onFilterDataChanged(c, f);
				}
			}

			lblCaption.setText(f.getPrecreateName());

			pnlRight.add(pnl, BorderLayout.CENTER);
			pnlRight.revalidate();
			pnl.revalidate();
			repaint();
		}
	}

	private void createEditPanel_Operator(JPanel pnl, CustomOperator filter) {
		{
			JComboBox<AbstractCustomFilter> cbxFilter = new JComboBox<>();
			cbxFilter.setModel(new DefaultComboBoxModel<>(AbstractCustomFilter.iterateAllSimpleFilterSorted(movielist).toArray(new AbstractCustomFilter[0])));
			cbxFilter.setRenderer(new CustomFilterEditFilterComboboxRenderer());
			cbxFilter.setMaximumRowCount(32);
			pnl.add(cbxFilter, "1, 2, fill, default"); //$NON-NLS-1$

			JButton btnAddFilter = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnAddFilter.text")); //$NON-NLS-1$
			btnAddFilter.addActionListener(e ->
			{
				if (cbxFilter.getSelectedIndex() >= 0) {
					filter.add(((AbstractCustomFilter)cbxFilter.getSelectedItem()).createNew(movielist));
					updateTree();
				}
			});
			pnl.add(btnAddFilter, "1, 4"); //$NON-NLS-1$
		}

		{
			JComboBox<AbstractCustomFilter> cbxOperator = new JComboBox<>();
			cbxOperator.setModel(new DefaultComboBoxModel<>(AbstractCustomFilter.getAllOperatorFilter(movielist)));
			cbxOperator.setRenderer(new CustomFilterEditFilterComboboxRenderer());
			cbxOperator.setMaximumRowCount(32);
			pnl.add(cbxOperator, "1, 6, fill, default"); //$NON-NLS-1$

			JButton btnAddOperator = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnAddOp.text")); //$NON-NLS-1$
			btnAddOperator.addActionListener(e ->
			{
				if (cbxOperator.getSelectedIndex() >= 0) {
					filter.add(((AbstractCustomFilter)cbxOperator.getSelectedItem()).createNew(movielist));
					updateTree();
				}
			});
			pnl.add(btnAddOperator, "1, 8"); //$NON-NLS-1$
		}

		{
			JComboBox<AbstractCustomFilter> cbxAggregator = new JComboBox<>();
			cbxAggregator.setModel(new DefaultComboBoxModel<>(AbstractCustomFilter.getAllAggregatorFilter(movielist)));
			cbxAggregator.setRenderer(new CustomFilterEditFilterComboboxRenderer());
			cbxAggregator.setMaximumRowCount(32);
			pnl.add(cbxAggregator, "1, 10, fill, default"); //$NON-NLS-1$

			JButton btnAddAggregator = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnAddAgg.text")); //$NON-NLS-1$
			btnAddAggregator.addActionListener(e ->
			{
				if (cbxAggregator.getSelectedIndex() >= 0) {
					filter.add(((AbstractCustomFilter)cbxAggregator.getSelectedItem()).createNew(movielist));
					updateTree();
				}
			});
			pnl.add(btnAddAggregator, "1, 12"); //$NON-NLS-1$
		}
	}

	private void updateSimple() {
		if (_currentSelectedFilter != null) {
			for (Tuple<CustomFilterConfig, JComponent> entr : _currentConfigEntries) {
				entr.Item1.onFilterDataChanged(entr.Item2, _currentSelectedFilter);
			}
		}

		updateTree();
	}

	private void updateTree() {

		DefaultTreeModel model = (treeMain != null) ? ((DefaultTreeModel)treeMain.getModel()) : (null);

		TreePath sel = null;
		if (treeMain != null) sel = treeMain.getSelectionPath();

		var addXRoot = (root.getChildCount() == 0);
		if (addXRoot) root.add(new CustomFilterEditTreeNode(filterObject.getFilter()));

		boolean changed = updateTree(model, filterObject.getFilter(), (CustomFilterEditTreeNode)root.getFirstChild(), root);

		if ((addXRoot || changed) && model != null && treeMain != null) {
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
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		edName = new JTextField();
		splitPane1 = new JSplitPane();
		scrollPane1 = new JScrollPane();
		treeMain = new JTree();
		panel2 = new JPanel();
		lblCaption = new JLabel();
		pnlRight = new JPanel();
		btnRemove = new JButton();
		panel1 = new JPanel();
		btnClear = new JButton();
		btnCancel = new JButton();
		btnOK = new JButton();
		btnExport = new JButton();
		btnImport = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("CustomFilterEditDialog.Title")); //$NON-NLS-1$
		setMinimumSize(new Dimension(600, 400));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
			"$lgap, default, $lgap, default:grow, $lgap, default, $lgap")); //$NON-NLS-1$

		//---- edName ----
		edName.setText("{name}"); //$NON-NLS-1$
		contentPane.add(edName, CC.xy(2, 2));

		//======== splitPane1 ========
		{
			splitPane1.setResizeWeight(0.5);

			//======== scrollPane1 ========
			{

				//---- treeMain ----
				treeMain.setRootVisible(false);
				treeMain.setEditable(true);
				treeMain.addTreeSelectionListener(e -> OnSelectionChanged(e));
				scrollPane1.setViewportView(treeMain);
			}
			splitPane1.setLeftComponent(scrollPane1);

			//======== panel2 ========
			{
				panel2.setLayout(new FormLayout(
					"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
					"$lgap, default, $lgap, default:grow, $lgap, default, $lgap")); //$NON-NLS-1$

				//---- lblCaption ----
				lblCaption.setText("{TITLE}"); //$NON-NLS-1$
				lblCaption.setFont(lblCaption.getFont().deriveFont(lblCaption.getFont().getStyle() | Font.BOLD, lblCaption.getFont().getSize() + 4f));
				lblCaption.setHorizontalAlignment(SwingConstants.CENTER);
				panel2.add(lblCaption, CC.xy(2, 2));

				//======== pnlRight ========
				{
					pnlRight.setLayout(new BorderLayout());
				}
				panel2.add(pnlRight, CC.xy(2, 4, CC.FILL, CC.FILL));

				//---- btnRemove ----
				btnRemove.setText(LocaleBundle.getString("CustomFilterEditDialog.Remove")); //$NON-NLS-1$
				btnRemove.addActionListener(e -> RemoveSelectedFilter());
				panel2.add(btnRemove, CC.xy(2, 6, CC.RIGHT, CC.DEFAULT));
			}
			splitPane1.setRightComponent(panel2);
		}
		contentPane.add(splitPane1, CC.xy(2, 4, CC.FILL, CC.FILL));

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"default, $lcgap, default:grow, 2*($lcgap, default), $lcgap, default:grow, 2*($lcgap, default)", //$NON-NLS-1$
				"default")); //$NON-NLS-1$

			//---- btnClear ----
			btnClear.setText(LocaleBundle.getString("UIGeneric.btnClear.text")); //$NON-NLS-1$
			btnClear.addActionListener(e -> onClear());
			panel1.add(btnClear, CC.xy(1, 1));

			//---- btnCancel ----
			btnCancel.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
			btnCancel.addActionListener(e -> onCancel());
			panel1.add(btnCancel, CC.xy(5, 1));

			//---- btnOK ----
			btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			btnOK.addActionListener(e -> onOkay());
			panel1.add(btnOK, CC.xy(7, 1));

			//---- btnExport ----
			btnExport.setText(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnExport.text")); //$NON-NLS-1$
			btnExport.addActionListener(e -> onExport());
			panel1.add(btnExport, CC.xy(11, 1));

			//---- btnImport ----
			btnImport.setText(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnImport.text")); //$NON-NLS-1$
			btnImport.addActionListener(e -> onImport());
			panel1.add(btnImport, CC.xy(13, 1));
		}
		contentPane.add(panel1, CC.xy(2, 6));
		setSize(800, 500);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTextField edName;
	private JSplitPane splitPane1;
	private JScrollPane scrollPane1;
	private JTree treeMain;
	private JPanel panel2;
	private JLabel lblCaption;
	private JPanel pnlRight;
	private JButton btnRemove;
	private JPanel panel1;
	private JButton btnClear;
	private JButton btnCancel;
	private JButton btnOK;
	private JButton btnExport;
	private JButton btnImport;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
