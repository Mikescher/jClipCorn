package de.jClipCorn.gui.frames.mainFrame.filterTree;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.gui.CachedResourceLoader;

public abstract class AbstractFilterTree extends JScrollPane implements TreeSelectionListener, CCDBUpdateListener, TreeExpansionListener {
	private static final long serialVersionUID = -1226727910191440220L;
	
	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	
	private boolean databaseHasLoaded = false;

	public AbstractFilterTree(CCMovieList list) {
		super();
		
		list.addChangeListener(this);
		
		root = new DefaultMutableTreeNode(null);
		model = new DefaultTreeModel(root);
		
		tree = new JTree(model);
		
		configureTree();
				
		this.setViewportView(tree);

		validate();
	}
	
	private void configureTree() {
		tree.setVisibleRowCount(14);
		tree.setToggleClickCount(1);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRowHeight(16 + 2);
		
		tree.addTreeExpansionListener(this);
		tree.getSelectionModel().addTreeSelectionListener(this);		
		
		tree.setRootVisible(false);
		
		tree.setCellRenderer(new FilterRenderer());
	}
	
	protected abstract void addFields();
	
	protected DefaultMutableTreeNode addNode(DefaultMutableTreeNode aroot, String icon, String txt, ActionListener listener) {
		return addNodeI(aroot, CachedResourceLoader.getImageIcon(icon), txt, listener);
	}
	
	protected DefaultMutableTreeNode addNodeI(DefaultMutableTreeNode aroot, Icon icon, String txt, ActionListener listener) {
		if (aroot == null) {
			aroot = root;
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new FilterTreeNode(icon, txt, listener));
		aroot.add(node);
		return node;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object o = tree.getLastSelectedPathComponent();
		if (o instanceof DefaultMutableTreeNode) {		
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			
			Object user = node.getUserObject();
			if (user != null) {
				((FilterTreeNode) user).execute();
			}
		}
	}
	
	protected void updateTree() {
		root.removeAllChildren();
		
		addFields();
		
		tree.expandPath(new TreePath(root.getPath()));
		
		model.reload();
	}
	
	@Override
	public void onAddDatabaseElement(CCDatabaseElement mov) {
		if (databaseHasLoaded) { // Sonnst called der des ganze update bei jedem adden von nem Movie onLoad
			updateTree();
		}
	}

	@Override
	public void onRemMovie(CCDatabaseElement el) {
		updateTree();
	}

	@Override
	public void onChangeDatabaseElement(CCDatabaseElement el) {
		updateTree();
	}

	@Override
	public void onAfterLoad() {
		updateTree();
		databaseHasLoaded = true;
	}
	
	@Override
	public void onRefresh() {
		updateTree();
	}
	
	@Override
	public void treeCollapsed(TreeExpansionEvent e) {
		// nothing
	}

	@Override
	public void treeExpanded(TreeExpansionEvent e) {
		for (int i = 0; i < root.getChildCount(); i++) {
			TreePath tp = new TreePath(((DefaultMutableTreeNode)root.getChildAt(i)).getPath());
			if (! tp.equals(e.getPath())) {
				tree.collapsePath(tp);
			}
		}
	}
	
	public void collapseAll() {
		for (int i = 0; i < root.getChildCount(); i++) {
			tree.collapsePath(new TreePath(((DefaultMutableTreeNode)root.getChildAt(i)).getPath()));
		}
	}
	
	public void reset() {
		collapseAll();
		tree.getSelectionModel().clearSelection();
	}
}
