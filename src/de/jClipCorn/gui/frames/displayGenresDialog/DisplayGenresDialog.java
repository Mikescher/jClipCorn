package de.jClipCorn.gui.frames.displayGenresDialog;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;

public class DisplayGenresDialog extends JDialog {
	private static final long serialVersionUID = 7628037231286957759L;
	
	private final CCMovieGenreList genreList;
	private final int cwidth;
	private JList<Object> lsGenres;

	public DisplayGenresDialog(CCMovieGenreList list, int width, Component rel) {
		super();
		setUndecorated(true);
		this.genreList = list;
		this.cwidth = Math.max(width, 10);
		
		initGUI();
		initDisplay();
		
		if (rel != null) {
			setLocation((int) rel.getLocationOnScreen().getX(), (int) (rel.getLocationOnScreen().getY() + rel.getSize().getHeight()));
		}
		
		addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent arg0) {
				dispose();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent arg0) {
				//Not needed
			}
		});
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setType(Type.POPUP);
		setResizable(false);
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		lsGenres = new JList<>();
		lsGenres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getContentPane().add(lsGenres);
	}
	
	private void initDisplay() {
		int h = Math.max(genreList.getGenreCount(), 1);
		
		DefaultListModel<Object> dlm = new DefaultListModel<>();
		for (int i = 0; i < h; i++) {
			dlm.addElement(genreList.getGenre(i).asString());
		}
		lsGenres.setModel(dlm);
		
		lsGenres.setVisibleRowCount(h);
		
		pack();
		
		setSize(Math.max(cwidth, (int) getSize().getWidth()), (int) getSize().getHeight());
		lsGenres.setSize(Math.max(cwidth, (int) lsGenres.getSize().getWidth()), (int) lsGenres.getSize().getHeight());
	}
}
