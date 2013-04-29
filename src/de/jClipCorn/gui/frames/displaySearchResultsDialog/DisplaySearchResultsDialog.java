package de.jClipCorn.gui.frames.displaySearchResultsDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.util.EpisodeSearchCallbackListener;

public class DisplaySearchResultsDialog extends JDialog implements MouseListener, WindowFocusListener, KeyListener {
	private static final long serialVersionUID = 5984142965004380779L;
	
	private static DisplaySearchResultsDialog instance = null;
	
	private JList<CCEpisode> list;
	private JScrollPane scrollPane;
	
	private List<EpisodeSearchCallbackListener> listener = new ArrayList<>();

	public DisplaySearchResultsDialog(List<CCEpisode> eplist, Component rel) {
		super();
		
		if (instance != null) {
			instance.dispose();
			instance = this;
		}
		
		
		setAlwaysOnTop(true);
		initGUI();
		
		if (rel != null) {
			initData(eplist, rel);
			setLocation((int) rel.getLocationOnScreen().getX(), (int) (rel.getLocationOnScreen().getY() + rel.getSize().getHeight()));
		}
		
		addWindowFocusListener(this);
		list.addMouseListener(this);
		list.addKeyListener(this);
	}
	
	public void addListener(EpisodeSearchCallbackListener escl) {
		listener.add(escl);
	}

	private void initGUI() {
		setSize(250, 250);
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setType(Type.POPUP);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		list = new JList<>();
		list.setVisibleRowCount(0);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);
	}
	
	private void initData(List<CCEpisode> eplist, Component rel) {
		int count = Math.max(1, eplist.size());
		
		DefaultListModel<CCEpisode> dlm = new DefaultListModel<>();
		
		for (int i = 0; i < eplist.size(); i++) {
			dlm.addElement(eplist.get(i));
		}
		
		list.setVisibleRowCount(count);
		
		pack();
		
		int w = rel.getWidth();
		int h = Math.min(125, getHeight() + 5);
		
		setSize(w, h);
		
		list.setModel(dlm);
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		// everything ok
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		dispose();
	}

	public static void disposeInstance() {
		if (instance != null) {
			instance.dispose();
			instance = null;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			onAction();
		}
	}

	private void onAction() {
		CCEpisode ep = list.getSelectedValue();
		
		if (ep != null) {
			for (int i = 0; i < listener.size(); i++) {
				listener.get(i).show(ep);
			}
			
			dispose();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// everything ok
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// everything ok
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// everything ok
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// everything ok
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			onAction();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// everything ok
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// everything ok
	}
}
