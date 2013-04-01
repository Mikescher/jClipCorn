package de.jClipCorn.gui.frames.changeScoreFrame;

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
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public class ChangeScoreFrame extends JFrame implements KeyListener {
	private static final long serialVersionUID = 9048482551231383355L;
	
	private final CCMovieList movielist;
	private int position;
	private boolean running = false;
	
	private JLabel lblCover;
	private JButton btnScore0;
	private JButton btnScore3;
	private JLabel lblCurrent;
	private JLabel lblKeyViewed;
	private JLabel lblKeyUnviewed;
	private JLabel lblTitle;
	private JButton btnScore1;
	private JButton btnScore2;
	private JButton btnScore4;
	private JButton btnScore5;
	private JLabel label;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JButton btnScoreNo;
	private JLabel lblbackspace;

	public ChangeScoreFrame(Component owner, CCMovieList list) { //TODO Option skip already rated movies
		super();
		this.movielist = list;
		
		initGUI();
		
		addKeyListener(this);
		setFocusable(true);
		
		setLocationRelativeTo(owner);
		init();
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("ChangedScoreFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setSize(new Dimension(430, 330));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		lblCover = new JLabel();
		lblCover.setIcon(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_STANDARD));
		lblCover.setBounds(10, 36, 182, 254);
		getContentPane().add(lblCover);
		
		lblCurrent = new JLabel();
		lblCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrent.setBounds(202, 270, 120, 20);
		getContentPane().add(lblCurrent);
		
		lblKeyViewed = new JLabel("(1)"); //$NON-NLS-1$
		lblKeyViewed.setHorizontalAlignment(SwingConstants.CENTER);
		lblKeyViewed.setBounds(332, 40, 80, 14);
		getContentPane().add(lblKeyViewed);
		
		lblKeyUnviewed = new JLabel("(2)"); //$NON-NLS-1$
		lblKeyUnviewed.setHorizontalAlignment(SwingConstants.CENTER);
		lblKeyUnviewed.setBounds(332, 74, 80, 14);
		getContentPane().add(lblKeyUnviewed);
		
		lblTitle = new JLabel();
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 356, 14);
		getContentPane().add(lblTitle);
		
		btnScore0 = new JButton(CCMovieScore.RATING_0.asString());
		btnScore0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCMovieScore.RATING_0);
			}
		});
		btnScore0.setBounds(202, 36, 120, 23);
		getContentPane().add(btnScore0);
		
		btnScore1 = new JButton(CCMovieScore.RATING_I.asString());
		btnScore1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCMovieScore.RATING_I);
			}
		});
		btnScore1.setBounds(202, 70, 120, 23);
		getContentPane().add(btnScore1);
		
		btnScore2 = new JButton(CCMovieScore.RATING_II.asString());
		btnScore2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCMovieScore.RATING_II);
			}
		});
		btnScore2.setBounds(202, 104, 120, 23);
		getContentPane().add(btnScore2);
		
		btnScore3 = new JButton(CCMovieScore.RATING_III.asString());
		btnScore3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCMovieScore.RATING_III);
			}
		});
		btnScore3.setBounds(202, 138, 120, 23);
		getContentPane().add(btnScore3);
		
		btnScore4 = new JButton(CCMovieScore.RATING_IV.asString());
		btnScore4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionNextMovie(CCMovieScore.RATING_IV);
			}
		});
		btnScore4.setBounds(202, 172, 120, 23);
		getContentPane().add(btnScore4);
		
		btnScore5 = new JButton(CCMovieScore.RATING_V.asString());
		btnScore5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionNextMovie(CCMovieScore.RATING_V);
			}
		});
		btnScore5.setBounds(202, 206, 120, 23);
		getContentPane().add(btnScore5);
		
		btnScoreNo = new JButton(CCMovieScore.RATING_NO.asString());
		btnScoreNo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionNextMovie(CCMovieScore.RATING_NO);
			}
		});
		btnScoreNo.setBounds(202, 240, 120, 23);
		getContentPane().add(btnScoreNo);
		
		label = new JLabel("(3)"); //$NON-NLS-1$
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(332, 108, 80, 14);
		getContentPane().add(label);
		
		label_1 = new JLabel("(4)"); //$NON-NLS-1$
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setBounds(332, 142, 80, 14);
		getContentPane().add(label_1);
		
		label_2 = new JLabel("(5)"); //$NON-NLS-1$
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setBounds(332, 176, 80, 14);
		getContentPane().add(label_2);
		
		label_3 = new JLabel("(6)"); //$NON-NLS-1$
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		label_3.setBounds(332, 210, 80, 14);
		getContentPane().add(label_3);
		
		lblbackspace = new JLabel("(BACKSPACE)"); //$NON-NLS-1$
		lblbackspace.setHorizontalAlignment(SwingConstants.CENTER);
		lblbackspace.setBounds(332, 244, 80, 14);
		getContentPane().add(lblbackspace);
	}
	
	private void init() {
		position = -1;
		running = true;
		
		nextMovie();
	}
	
	private void actionNextMovie(CCMovieScore score) {
		if (! running) {
			return;
		}
		
		CCMovie m = (CCMovie) movielist.getDatabaseElementBySort(position);
		m.setScore(score);
		
		nextMovie();
	}
	
	private void nextMovie() {
		position++;
		
		if (position < movielist.getElementCount()) {
			CCDatabaseElement del = movielist.getDatabaseElementBySort(position);
			if (del.isMovie()) {
				CCMovie m = (CCMovie) del;
				lblCover.setIcon(m.getCoverIcon());
				lblCurrent.setIcon(m.getScore().getIcon());
				lblTitle.setText(m.getCompleteTitle());
			} else {
				nextMovie();
				return;
			}
		} else {
			btnScore0.setEnabled(false);
			btnScore1.setEnabled(false);
			btnScore2.setEnabled(false);
			btnScore3.setEnabled(false);
			btnScore4.setEnabled(false);
			btnScore5.setEnabled(false);
			btnScoreNo.setEnabled(false);
			running = false;
			return;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_1) {
			actionNextMovie(CCMovieScore.RATING_0);
		} else if (e.getKeyCode() == KeyEvent.VK_2) {
			actionNextMovie(CCMovieScore.RATING_I);
		} else if (e.getKeyCode() == KeyEvent.VK_3) {
			actionNextMovie(CCMovieScore.RATING_II);
		} else if (e.getKeyCode() == KeyEvent.VK_4) {
			actionNextMovie(CCMovieScore.RATING_III);
		} else if (e.getKeyCode() == KeyEvent.VK_5) {
			actionNextMovie(CCMovieScore.RATING_IV);
		} else if (e.getKeyCode() == KeyEvent.VK_6) {
			actionNextMovie(CCMovieScore.RATING_V);
		} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			actionNextMovie(CCMovieScore.RATING_NO);
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
