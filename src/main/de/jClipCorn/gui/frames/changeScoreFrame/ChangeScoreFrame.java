package de.jClipCorn.gui.frames.changeScoreFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.PropertyCheckbox;
import de.jClipCorn.gui.guiComponents.cover.CoverLabelFullsize;
import de.jClipCorn.gui.localization.LocaleBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ChangeScoreFrame extends JCCFrame {
	private static final long serialVersionUID = 9048482551231383355L;

	private int position;
	private boolean running = false;

	public ChangeScoreFrame(Component owner, CCMovieList ml) {
		super(ml);

		initComponents();
		postInit();

		initMap();

		setLocationRelativeTo(owner);
		init();
	}

	private void postInit() {
		btnScore0.setText(CCUserScore.RATING_0.asString());
		btnScore1.setText(CCUserScore.RATING_I.asString());
		btnScore2.setText(CCUserScore.RATING_II.asString());
		btnScoreM.setText(CCUserScore.RATING_MID.asString());
		btnScore3.setText(CCUserScore.RATING_III.asString());
		btnScore4.setText(CCUserScore.RATING_IV.asString());
		btnScore5.setText(CCUserScore.RATING_V.asString());
		btnScoreNo.setText(CCUserScore.RATING_NO.asString());

		pack();
	}

	private void init() {
		position = -1;
		running = true;

		nextMovie();
	}

	private void onScore0()   { actionNextMovie(CCUserScore.RATING_0); }
	private void onScore1()   { actionNextMovie(CCUserScore.RATING_I); }
	private void onScore2()   { actionNextMovie(CCUserScore.RATING_II); }
	private void onScoreMid() { actionNextMovie(CCUserScore.RATING_MID); }
	private void onScore3()   { actionNextMovie(CCUserScore.RATING_III); }
	private void onScore4()   { actionNextMovie(CCUserScore.RATING_IV); }
	private void onScore5()   { actionNextMovie(CCUserScore.RATING_V); }
	private void onScoreNo()  { actionNextMovie(CCUserScore.RATING_NO); }

	private void actionNextMovie(CCUserScore score) {
		if (! running) {
			return;
		}

		CCMovie mov = movielist.getDatabaseElementBySort(position).asMovie();
		mov.Score.set(score);

		nextMovie();
	}

	private void nextMovie() {
		position++;

		if (position < movielist.getElementCount()) {
			CCDatabaseElement del = movielist.getDatabaseElementBySort(position);
			if (del.isMovie()) {
				CCMovie mov = del.asMovie();

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
				lblCurrent.setIcon(mov.Score.get().getIcon(false));
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblTitle = new JLabel();
        lblCover = new CoverLabelFullsize(movielist);
        pnlButtons = new JPanel();
        btnScore0 = new JButton();
        lblKey1 = new JLabel();
        btnScore1 = new JButton();
        lblKey2 = new JLabel();
        btnScore2 = new JButton();
        lblKey3 = new JLabel();
        btnScoreM = new JButton();
        lblKey4 = new JLabel();
        btnScore3 = new JButton();
        lblKey5 = new JLabel();
        btnScore4 = new JButton();
        lblKey6 = new JLabel();
        btnScore5 = new JButton();
        lblKey7 = new JLabel();
        btnScoreNo = new JButton();
        lblKeyBack = new JLabel();
        lblCurrent = new JLabel();
        cbSkipRated = new PropertyCheckbox(ccprops().PROP_MASSCHANGESCORE_SKIPRATED);
        cbOnlyViewed = new PropertyCheckbox(ccprops().PROP_MASSCHANGESCORE_ONLYVIEWED);

        //======== this ========
        setTitle(LocaleBundle.getString("ChangedScoreFrame.this.title")); //$NON-NLS-1$
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        var contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$ugap, default, $ugap, default:grow, $ugap", //$NON-NLS-1$
            "$ugap, default, $lgap, default, $ugap, default, $lgap, default, 16dlu")); //$NON-NLS-1$

        //---- lblTitle ----
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblTitle, CC.xywh(2, 2, 3, 1, CC.FILL, CC.DEFAULT));
        contentPane.add(lblCover, CC.xy(2, 4, CC.DEFAULT, CC.TOP));

        //======== pnlButtons ========
        {
            pnlButtons.setLayout(new FormLayout(
                "[50dlu,default]:grow, $ugap, default", //$NON-NLS-1$
                "7*(default, $lgap), default, $ugap, default")); //$NON-NLS-1$

            //---- btnScore0 ----
            btnScore0.setText("(auto)"); //$NON-NLS-1$
            btnScore0.addActionListener(e -> onScore0());
            pnlButtons.add(btnScore0, CC.xy(1, 1, CC.FILL, CC.DEFAULT));

            //---- lblKey1 ----
            lblKey1.setText("(1)"); //$NON-NLS-1$
            lblKey1.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblKey1, CC.xy(3, 1, CC.FILL, CC.DEFAULT));

            //---- btnScore1 ----
            btnScore1.setText("(auto)"); //$NON-NLS-1$
            btnScore1.addActionListener(e -> onScore1());
            pnlButtons.add(btnScore1, CC.xy(1, 3, CC.FILL, CC.DEFAULT));

            //---- lblKey2 ----
            lblKey2.setText("(2)"); //$NON-NLS-1$
            lblKey2.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblKey2, CC.xy(3, 3, CC.FILL, CC.DEFAULT));

            //---- btnScore2 ----
            btnScore2.setText("(auto)"); //$NON-NLS-1$
            btnScore2.addActionListener(e -> onScore2());
            pnlButtons.add(btnScore2, CC.xy(1, 5, CC.FILL, CC.DEFAULT));

            //---- lblKey3 ----
            lblKey3.setText("(3)"); //$NON-NLS-1$
            lblKey3.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblKey3, CC.xy(3, 5, CC.FILL, CC.DEFAULT));

            //---- btnScoreM ----
            btnScoreM.setText("(auto)"); //$NON-NLS-1$
            btnScoreM.addActionListener(e -> onScoreMid());
            pnlButtons.add(btnScoreM, CC.xy(1, 7, CC.FILL, CC.DEFAULT));

            //---- lblKey4 ----
            lblKey4.setText("(4)"); //$NON-NLS-1$
            lblKey4.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblKey4, CC.xy(3, 7, CC.FILL, CC.DEFAULT));

            //---- btnScore3 ----
            btnScore3.setText("(auto)"); //$NON-NLS-1$
            btnScore3.addActionListener(e -> onScore3());
            pnlButtons.add(btnScore3, CC.xy(1, 9, CC.FILL, CC.DEFAULT));

            //---- lblKey5 ----
            lblKey5.setText("(5)"); //$NON-NLS-1$
            lblKey5.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblKey5, CC.xy(3, 9, CC.FILL, CC.DEFAULT));

            //---- btnScore4 ----
            btnScore4.setText("(auto)"); //$NON-NLS-1$
            btnScore4.addActionListener(e -> onScore4());
            pnlButtons.add(btnScore4, CC.xy(1, 11, CC.FILL, CC.DEFAULT));

            //---- lblKey6 ----
            lblKey6.setText("(6)"); //$NON-NLS-1$
            lblKey6.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblKey6, CC.xy(3, 11, CC.FILL, CC.DEFAULT));

            //---- btnScore5 ----
            btnScore5.setText("(auto)"); //$NON-NLS-1$
            btnScore5.addActionListener(e -> onScore5());
            pnlButtons.add(btnScore5, CC.xy(1, 13, CC.FILL, CC.DEFAULT));

            //---- lblKey7 ----
            lblKey7.setText("(7)"); //$NON-NLS-1$
            lblKey7.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblKey7, CC.xy(3, 13, CC.FILL, CC.DEFAULT));

            //---- btnScoreNo ----
            btnScoreNo.setText("(auto)"); //$NON-NLS-1$
            btnScoreNo.addActionListener(e -> onScoreNo());
            pnlButtons.add(btnScoreNo, CC.xy(1, 15, CC.FILL, CC.DEFAULT));

            //---- lblKeyBack ----
            lblKeyBack.setText("(BACKSPACE)"); //$NON-NLS-1$
            lblKeyBack.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblKeyBack, CC.xy(3, 15, CC.FILL, CC.DEFAULT));

            //---- lblCurrent ----
            lblCurrent.setHorizontalAlignment(SwingConstants.CENTER);
            pnlButtons.add(lblCurrent, CC.xy(1, 17, CC.FILL, CC.DEFAULT));
        }
        contentPane.add(pnlButtons, CC.xy(4, 4, CC.FILL, CC.TOP));
        contentPane.add(cbSkipRated, CC.xywh(2, 6, 3, 1));
        contentPane.add(cbOnlyViewed, CC.xywh(2, 8, 3, 1));
        pack();
        setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblTitle;
    private CoverLabelFullsize lblCover;
    private JPanel pnlButtons;
    private JButton btnScore0;
    private JLabel lblKey1;
    private JButton btnScore1;
    private JLabel lblKey2;
    private JButton btnScore2;
    private JLabel lblKey3;
    private JButton btnScoreM;
    private JLabel lblKey4;
    private JButton btnScore3;
    private JLabel lblKey5;
    private JButton btnScore4;
    private JLabel lblKey6;
    private JButton btnScore5;
    private JLabel lblKey7;
    private JButton btnScoreNo;
    private JLabel lblKeyBack;
    private JLabel lblCurrent;
    private PropertyCheckbox cbSkipRated;
    private PropertyCheckbox cbOnlyViewed;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
