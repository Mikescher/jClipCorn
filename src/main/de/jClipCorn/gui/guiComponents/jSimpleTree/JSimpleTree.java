package de.jClipCorn.gui.guiComponents.jSimpleTree;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class JSimpleTree extends JTree implements TreeSelectionListener {
	private static final long serialVersionUID = 6361624518488393639L;

	private int _lastModifier = 0;
	private SimpleTreeObject _lastExecBySelection = null;
	private long _lastExecBySelectionTime = 0;
	
	public SimpletreeActionMode ActionMode = SimpletreeActionMode.OnSelect;
	
	public JSimpleTree() {
		super();
		
		configure();
	}
	
	public JSimpleTree(DefaultTreeModel model) {
		super(model);
		
		configure();
	}

	public JSimpleTree(DefaultMutableTreeNode root) {
		super(root);
		
		configure();
	}

	private void configure() {

		this.getSelectionModel().addTreeSelectionListener(this);
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				_lastModifier = e.getModifiers(); 
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				_lastModifier = e.getModifiers(); 

		        TreePath selPath = JSimpleTree.this.getPathForLocation(e.getX(), e.getY());
		        if(selPath != null && e.getClickCount() == 1 && ActionMode == SimpletreeActionMode.OnClick) {
	    			Object user = ((DefaultMutableTreeNode) selPath.getLastPathComponent()).getUserObject();
	    			if (user != null) {
	    				if (user == _lastExecBySelection && (System.currentTimeMillis() - _lastExecBySelectionTime) < 300) return; // skip
	    				
	    				if (!JSimpleTree.this.isPathSelected(selPath)) return;
	    				
	    				((SimpleTreeObject) user).execute(selPath, (_lastModifier & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
	    			}
		        }
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				_lastModifier = e.getModifiers(); 
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				_lastModifier = e.getModifiers(); 
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				_lastModifier = e.getModifiers(); 
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				_lastModifier = e.getModifiers(); 
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				_lastModifier = e.getModifiers(); 
			}
		});
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath p = this.getSelectionPath();
		if (p == null) return;
		
		Object o = p.getLastPathComponent();
		if (o == null) return;
		if (!(o instanceof DefaultMutableTreeNode)) return;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
		
		Object user = node.getUserObject();
		if (user == null) return;

		((SimpleTreeObject) user).execute(p, (_lastModifier & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
		
		_lastExecBySelection = ((SimpleTreeObject) user);
		_lastExecBySelectionTime = System.currentTimeMillis();
	}
	
}
