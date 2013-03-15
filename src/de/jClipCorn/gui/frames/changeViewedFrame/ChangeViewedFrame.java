package de.jClipCorn.gui.frames.changeViewedFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public class ChangeViewedFrame extends JFrame implements KeyListener {
	private static final long serialVersionUID = 9048482551231383355L;
	
	private final CCMovieList movielist;
	private int position;
	private boolean running = false;
	
	private JLabel lblCover;
	private JButton btnViewed;
	private JButton btnUnviewed;
	private JLabel lblCurrent;
	private JLabel lblKeyViewed;
	private JLabel lblKeyUnviewed;
	private JLabel lblTitle;

	public ChangeViewedFrame(Component owner, CCMovieList list) {
		super();
		this.movielist = list;
		
		initGUI();
		
		addKeyListener(this);
		setFocusable(true);
		
		setLocationRelativeTo(owner);
		init();
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("ChangedViewedFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setSize(new Dimension(382, 420));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		lblCover = new JLabel();
		lblCover.setIcon(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_STANDARD));
		lblCover.setBounds(100, 47, 182, 254);
		getContentPane().add(lblCover);
		
		btnViewed = new JButton(LocaleBundle.getString("FilterTree.Viewed.Viewed")); //$NON-NLS-1$
		btnViewed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(true);
			}
		});
		btnViewed.setBounds(10, 339, 89, 23);
		getContentPane().add(btnViewed);
		
		btnUnviewed = new JButton(LocaleBundle.getString("FilterTree.Viewed.Unviewed")); //$NON-NLS-1$
		btnUnviewed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(false);
			}
		});
		btnUnviewed.setBounds(277, 339, 89, 23);
		getContentPane().add(btnUnviewed);
		
		lblCurrent = new JLabel();
		lblCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrent.setBounds(100, 300, 182, 28);
		getContentPane().add(lblCurrent);
		
		lblKeyViewed = new JLabel("(1)"); //$NON-NLS-1$
		lblKeyViewed.setHorizontalAlignment(SwingConstants.CENTER);
		lblKeyViewed.setBounds(10, 367, 89, 14);
		getContentPane().add(lblKeyViewed);
		
		lblKeyUnviewed = new JLabel("(2)"); //$NON-NLS-1$
		lblKeyUnviewed.setHorizontalAlignment(SwingConstants.CENTER);
		lblKeyUnviewed.setBounds(277, 367, 89, 14);
		getContentPane().add(lblKeyUnviewed);
		
		lblTitle = new JLabel();
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 356, 14);
		getContentPane().add(lblTitle);
	}
	
	private void init() {
		position = -1;
		running = true;
		
		nextMovie();
	}
	
	private void actionNextMovie(boolean viewed) {
		if (! running) {
			return;
		}
		
		CCMovie m = (CCMovie) movielist.getDatabaseElementBySort(position);
		m.setViewed(viewed);
		
		nextMovie();
	}
	
	private void nextMovie() {
		position++;
		
		if (position < movielist.getElementCount()) {
			CCDatabaseElement del = movielist.getDatabaseElementBySort(position);
			if (del.isMovie()) {
				CCMovie m = (CCMovie) del;
				lblCover.setIcon(m.getCoverIcon());
				lblCurrent.setIcon(CachedResourceLoader.getImageIcon((m.isViewed() ? (Resources.ICN_TABLE_VIEWED_TRUE) : (Resources.ICN_TABLE_VIEWED_FALSE))));
				lblTitle.setText(m.getCompleteTitle());
			} else {
				nextMovie();
				return;
			}
		} else {
			btnUnviewed.setEnabled(false);
			btnViewed.setEnabled(false);
			running = false;
			return;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_1) {
			actionNextMovie(true);
		} else if (e.getKeyCode() == KeyEvent.VK_2) {
			actionNextMovie(false);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// nothing
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// nothing
	}
}
