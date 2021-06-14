package de.jClipCorn.gui.frames.changeScoreFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.PropertyCheckbox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ChangeScoreFrame extends JFrame {
	private static final long serialVersionUID = 9048482551231383355L;
	
	private final CCMovieList movielist;
	private int position;
	private boolean running = false;
	
	private CoverLabel lblCover;
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
	private PropertyCheckbox cbSkipRated;
	private PropertyCheckbox cbOnlyViewed;
	private JButton btnScoreM;
	private JLabel label_4;

	public ChangeScoreFrame(Component owner, CCMovieList list) {
		super();
		this.movielist = list;
		
		initGUI();
		
		initMap();
		
		setLocationRelativeTo(owner);
		init();
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("ChangedScoreFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setSize(new Dimension(430, 428));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		lblCover = new CoverLabel(false);
		lblCover.setPosition(10, 36);
		getContentPane().add(lblCover);
		
		lblCurrent = new JLabel();
		lblCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrent.setBounds(202, 304, 120, 20);
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
		
		btnScore0 = new JButton(CCUserScore.RATING_0.asString());
		btnScore0.addActionListener(arg0 -> actionNextMovie(CCUserScore.RATING_0));
		btnScore0.setBounds(202, 36, 120, 23);
		getContentPane().add(btnScore0);
		
		btnScore1 = new JButton(CCUserScore.RATING_I.asString());
		btnScore1.addActionListener(arg0 -> actionNextMovie(CCUserScore.RATING_I));
		btnScore1.setBounds(202, 70, 120, 23);
		getContentPane().add(btnScore1);
		
		btnScore2 = new JButton(CCUserScore.RATING_II.asString());
		btnScore2.addActionListener(arg0 -> actionNextMovie(CCUserScore.RATING_II));
		btnScore2.setBounds(202, 104, 120, 23);
		getContentPane().add(btnScore2);
		
		btnScoreM = new JButton(CCUserScore.RATING_MID.asString());
		btnScoreM.addActionListener(arg0 -> actionNextMovie(CCUserScore.RATING_MID));
		btnScoreM.setBounds(202, 137, 120, 23);
		getContentPane().add(btnScoreM);
		
		btnScore3 = new JButton(CCUserScore.RATING_III.asString());
		btnScore3.addActionListener(arg0 -> actionNextMovie(CCUserScore.RATING_III));
		btnScore3.setBounds(202, 172, 120, 23);
		getContentPane().add(btnScore3);
		
		btnScore4 = new JButton(CCUserScore.RATING_IV.asString());
		btnScore4.addActionListener(e -> actionNextMovie(CCUserScore.RATING_IV));
		btnScore4.setBounds(202, 206, 120, 23);
		getContentPane().add(btnScore4);
		
		btnScore5 = new JButton(CCUserScore.RATING_V.asString());
		btnScore5.addActionListener(e -> actionNextMovie(CCUserScore.RATING_V));
		btnScore5.setBounds(202, 240, 120, 23);
		getContentPane().add(btnScore5);
		
		btnScoreNo = new JButton(CCUserScore.RATING_NO.asString());
		btnScoreNo.addActionListener(e -> actionNextMovie(CCUserScore.RATING_NO));
		btnScoreNo.setBounds(202, 274, 120, 23);
		getContentPane().add(btnScoreNo);
		
		label = new JLabel("(3)"); //$NON-NLS-1$
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(332, 108, 80, 14);
		getContentPane().add(label);
		
		label_4 = new JLabel("(4)"); //$NON-NLS-1$
		label_4.setHorizontalAlignment(SwingConstants.CENTER);
		label_4.setBounds(332, 142, 80, 14);
		getContentPane().add(label_4);
		
		label_1 = new JLabel("(5)"); //$NON-NLS-1$
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setBounds(332, 176, 80, 14);
		getContentPane().add(label_1);
		
		label_2 = new JLabel("(6)"); //$NON-NLS-1$
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setBounds(332, 210, 80, 14);
		getContentPane().add(label_2);
		
		label_3 = new JLabel("(7)"); //$NON-NLS-1$
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		label_3.setBounds(332, 244, 80, 14);
		getContentPane().add(label_3);
		
		lblbackspace = new JLabel("(BACKSPACE)"); //$NON-NLS-1$
		lblbackspace.setHorizontalAlignment(SwingConstants.CENTER);
		lblbackspace.setBounds(332, 278, 80, 14);
		getContentPane().add(lblbackspace);
		
		cbSkipRated = new PropertyCheckbox(CCProperties.getInstance().PROP_MASSCHANGESCORE_SKIPRATED);
		cbSkipRated.setBounds(10, 331, 402, 23);
		getContentPane().add(cbSkipRated);
		
		cbOnlyViewed = new PropertyCheckbox(CCProperties.getInstance().PROP_MASSCHANGESCORE_ONLYVIEWED);
		cbOnlyViewed.setBounds(10, 357, 402, 23);
		getContentPane().add(cbOnlyViewed);
	}
	
	private void init() {
		position = -1;
		running = true;
		
		nextMovie();
	}
	
	private void actionNextMovie(CCUserScore score) {
		if (! running) {
			return;
		}
		
		CCMovie mov = (CCMovie) movielist.getDatabaseElementBySort(position);
		mov.Score.set(score);
		
		nextMovie();
	}
	
	private void nextMovie() {
		position++;
		
		if (position < movielist.getElementCount()) {
			CCDatabaseElement del = movielist.getDatabaseElementBySort(position);
			if (del.isMovie()) {
				CCMovie mov = (CCMovie) del;
				
				if (cbSkipRated.isSelected()) {
					if (mov.Score.get() != CCUserScore.RATING_NO) {
						nextMovie();
						return;
					}
				}
				
				if (cbOnlyViewed.isSelected()) {
					if (! mov.isViewed()) {
						nextMovie();
						return;
					}
				}
				
				lblCover.setAndResizeCover(mov.getCover());
				lblCurrent.setIcon(mov.Score.get().getIcon());
				lblTitle.setText(mov.getCompleteTitle());
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
			btnScoreM.setEnabled(false);
			btnScoreNo.setEnabled(false);
			running = false;
			return;
		}
	}
	
	@SuppressWarnings("nls")
	private void initMap() {
		InputMap map = ((JPanel) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap act = ((JPanel) getContentPane()).getActionMap();
		
		map.put(KeyStroke.getKeyStroke('1'), "KEYPRESSED_1");
		map.put(KeyStroke.getKeyStroke('2'), "KEYPRESSED_2");
		map.put(KeyStroke.getKeyStroke('3'), "KEYPRESSED_3");
		map.put(KeyStroke.getKeyStroke('4'), "KEYPRESSED_4");
		map.put(KeyStroke.getKeyStroke('5'), "KEYPRESSED_5");
		map.put(KeyStroke.getKeyStroke('6'), "KEYPRESSED_6");
		map.put(KeyStroke.getKeyStroke('7'), "KEYPRESSED_7");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "KEYPRESSED_B");
		
		act.put("KEYPRESSED_1", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCUserScore.RATING_0);
			}
		});
		act.put("KEYPRESSED_2", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCUserScore.RATING_I);
			}
		});
		act.put("KEYPRESSED_3", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCUserScore.RATING_II);
			}
		});
		act.put("KEYPRESSED_4", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCUserScore.RATING_MID);
			}
		});
		act.put("KEYPRESSED_5", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCUserScore.RATING_III);
			}
		});
		act.put("KEYPRESSED_6", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCUserScore.RATING_IV);
			}
		});
		act.put("KEYPRESSED_7", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCUserScore.RATING_V);
			}
		});
		act.put("KEYPRESSED_B", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(CCUserScore.RATING_NO);
			}
		});
	}
}
