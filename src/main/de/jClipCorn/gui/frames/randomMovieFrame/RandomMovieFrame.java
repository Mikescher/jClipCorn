package de.jClipCorn.gui.frames.randomMovieFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RandomMovieFrame extends JCCFrame implements Runnable {
	private static final long serialVersionUID = -8142348609793380058L;

	private final static int CVRCOUNT = 100;
	private final static int MAXSPEED = 50;

	private List<CCMovie> choosableList;

	private boolean isShuffled = false;

	public RandomMovieFrame(Component parent, CCMovieList movielist) {
		super(movielist);

		generateList();

		initComponents();
		postInit();

		setLocationRelativeTo(parent);
	}

	private void postInit() {
		// 3D-mode, circle-radius etc. are configured (and the window packed) in initComponents,
		// so the frame sizes itself to the cover-carousel automatically.
	}

	private void onMainButton() {
		if (!isShuffled) {
			btnMain.setText(LocaleBundle.getString("RandomMovieFrame.btnPlay.text")); //$NON-NLS-1$
			btnMain.setEnabled(false);
			new Thread(this).start();
		} else {
			CCMovie m = (CCMovie) chooser.getSelectedObject();
			m.play(this, true);
			dispose();
		}
	}

	private void generateList() {
		choosableList = new ArrayList<>();

		for (CCMovie mov : movielist.iteratorMovies()) {
			choosableList.add(mov);
		}
	}

	private CCMovie getNextRandMovie() {
		double r = Math.random() * choosableList.size();
		int i = (int) r;
		CCMovie m  = choosableList.get(i);
		if (choosableList.size() > 1) {
			choosableList.remove(i);
		}
		return m;
	}

	@Override
	public void run() {
		for (int i = 0; i < CVRCOUNT; i++) {
			if (i > 4) {
				SwingUtils.invokeLater(() -> {
					CCMovie mov = getNextRandMovie();
					chooser.addCover(mov);
					chooser.inc();
					chooser.repaint();
				});
			} else {
				SwingUtils.invokeLater(() -> {
					CCMovie mov = getNextRandMovie();
					chooser.addCover(mov);
					chooser.repaint();
				});
			}

			try {
				int speed = (int) (MAXSPEED / getSpeedPercentage((i * 1.0) / CVRCOUNT));
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				CCLog.addError(e);
			}
		}

		SwingUtils.invokeLater(() -> {
			isShuffled = true;
			btnMain.setEnabled(true);
		});
	}

	private double getSpeedPercentage(double progresspercentage) {
		double x = progresspercentage;
		double r = (((10 * Math.exp(10 * x + 5)) / (Math.pow((Math.exp(10 * x) + Math.exp(5)), 2)))*2)/5;
		return r;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		chooser = new JCoverChooser(movielist, true);
		pnlBottom = new JPanel();
		btnMain = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("RandomMovieFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"default:grow",
			"default:grow, default"));

		//---- chooser ----
		chooser.set3DMode(true);
		chooser.setCircleRadius(300);
		chooser.setCoverGap(10);
		chooser.setEnabled(false);
		contentPane.add(chooser, CC.xy(1, 1, CC.FILL, CC.FILL));

		//======== pnlBottom ========
		{
			pnlBottom.setLayout(new FormLayout(
				"default:grow, default, default:grow",
				"$ugap, default, $ugap"));

			//---- btnMain ----
			btnMain.setText(LocaleBundle.getString("RandomMovieFrame.btnShuffle.text"));
			btnMain.addActionListener(e -> onMainButton());
			pnlBottom.add(btnMain, CC.xy(2, 2));
		}
		contentPane.add(pnlBottom, CC.xy(1, 2, CC.FILL, CC.DEFAULT));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCoverChooser chooser;
	private JPanel pnlBottom;
	private JButton btnMain;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
