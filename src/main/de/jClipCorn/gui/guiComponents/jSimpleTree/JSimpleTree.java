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

public class JSimpleTree extends JTree implements TreeSelectionListener {
	private static final long serialVersionUID = 6361624518488393639L;

	private int _lastModifier = 0;
	
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
		Object o = this.getLastSelectedPathComponent();
		if (o != null && o instanceof DefaultMutableTreeNode) {		
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			
			Object user = node.getUserObject();
			if (user != null) {
				((SimpleTreeObject) user).execute((_lastModifier & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK);
			}
		}
	}
	
}
