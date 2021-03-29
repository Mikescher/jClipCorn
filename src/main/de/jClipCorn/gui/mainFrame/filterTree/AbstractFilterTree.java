package de.jClipCorn.gui.mainFrame.filterTree;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.gui.guiComponents.jSimpleTree.JSimpleTree;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeObject;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeObject.SimpleTreeEvent;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeRenderer;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.util.Collections;

public abstract class AbstractFilterTree extends JScrollPane implements CCDBUpdateListener, TreeExpansionListener {
	private static final long serialVersionUID = -1226727910191440220L;
	
	protected JSimpleTree tree;
	private DefaultTreeModel model;
	protected DefaultMutableTreeNode root;
	
	private boolean databaseHasLoaded = false;

	public AbstractFilterTree(CCMovieList list) {
		super();
		
		if (list != null) list.addChangeListener(this);
		
		root = new DefaultMutableTreeNode(null);
		model = new DefaultTreeModel(root);
		
		tree = new JSimpleTree(model);
		
		configureTree();
				
		this.setViewportView(tree);

		validate();
	}
	
	private void configureTree() {
		tree.setVisibleRowCount(14);
		tree.setToggleClickCount(999);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRowHeight(16 + 2);
		
		tree.addTreeExpansionListener(this);
		tree.setRootVisible(false);
		
		tree.setCellRenderer(new SimpleTreeRenderer());
	}
	
	protected abstract void addFields();
	
	protected DefaultMutableTreeNode addNode(DefaultMutableTreeNode aroot, IconRef icon, String txt, Func1to0<SimpleTreeEvent> listener) {
		return addNodeI(aroot, icon.get(), txt, listener);
	}

	protected DefaultMutableTreeNode addNode(DefaultMutableTreeNode aroot, MultiSizeIconRef icon, String txt, Func1to0<SimpleTreeEvent> listener) {
		return addNodeI(aroot, icon.get16x16(), txt, listener);
	}
	
	protected DefaultMutableTreeNode addNodeI(DefaultMutableTreeNode aroot, Icon icon, String txt, Func1to0<SimpleTreeEvent> listener) {
		if (aroot == null) {
			aroot = root;
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new SimpleTreeObject(icon, txt, listener));
		aroot.add(node);
		return node;
	}
	
	@SuppressWarnings("unchecked")
	protected void updateTree() {
		TreePath sel = tree.getSelectionPath();
		
		root.removeAllChildren();
		
		addFields();

		tree.expandPath(new TreePath(root.getPath()));
		
		model.reload();

		if (sel != null && sel.getPathCount() > 0) {
			TreePath p = null;
			
			Object o =sel.getPathComponent(1);
			if (o instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode dmtno = (DefaultMutableTreeNode)o;
				if (dmtno.getUserObject() instanceof SimpleTreeObject) {
					SimpleTreeObject sto = (SimpleTreeObject)dmtno.getUserObject();
					
					for (Object child : Collections.list(root.children())) {
						if (!(child instanceof DefaultMutableTreeNode)) continue;
						DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)child;
						if (!(dmtn.getUserObject() instanceof SimpleTreeObject)) continue;
						SimpleTreeObject sto2 = (SimpleTreeObject)dmtn.getUserObject();
						
						if (sto2.getText().equals(sto.getText())) p = new TreePath(dmtn.getPath());
					}
				}
			}
			
			if (p != null) tree.expandPath(p);
		}
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
		tree.collapsePath(e.getPath());
	}

	@Override
	public void treeExpanded(TreeExpansionEvent e) {
		if (e.getPath().getPathCount() > 2) return;
		
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
		getVerticalScrollBar().setValue(0);
	}
}
