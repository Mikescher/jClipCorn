package de.jClipCorn.gui.frames.randomMovieFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.ImageUtilities;

public class RandomMovieFrame extends JFrame implements Runnable {
	private static final long serialVersionUID = -8142348609793380058L;

	private final static int CVRCOUNT = 100;
	private final static int MAXSPEED = 50;

	private JPanel panel;
	private JButton btnMain;
	private JCoverChooser chooser;

	private List<CCMovie> choosableList;
	
	private boolean isShuffled = false;

	public RandomMovieFrame(Component parent, CCMovieList movielist) {
		super();
		generateList(movielist);
		initGUI();
		setLocationRelativeTo(parent);
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("RandomMovieFrame.this.title")); //$NON-NLS-1$

		chooser = new JCoverChooser(true);
		chooser.set3DMode(true);
		chooser.setEnabled(false);
		chooser.setCoverWidth(ImageUtilities.HALF_COVER_WIDTH);
		chooser.setCoverHeight(ImageUtilities.HALF_COVER_HEIGHT);
		chooser.setCoverGap(10);
		chooser.setCircleRadius(300);
		getContentPane().add(chooser, BorderLayout.CENTER);

		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		btnMain = new JButton(LocaleBundle.getString("RandomMovieFrame.btnShuffle.text")); //$NON-NLS-1$
		btnMain.addActionListener(arg0 ->
		{
			if (! isShuffled) {
			btnMain.setText(LocaleBundle.getString("RandomMovieFrame.btnPlay.text")); //$NON-NLS-1$
			btnMain.setEnabled(false);
			new Thread(RandomMovieFrame.this).start();
			} else {
				CCMovie m = (CCMovie) chooser.getSelectedObject();
				m.play(true);
				dispose();
			}
		});
		panel.add(btnMain);

		pack();
	}

	private void generateList(CCMovieList movielist) {
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
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						CCMovie mov = getNextRandMovie();
						chooser.addCover(mov, mov);
						chooser.inc();
						chooser.repaint();
					}
				});
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						CCMovie mov = getNextRandMovie();
						chooser.addCover(mov, mov);
						chooser.repaint();
					}
				});
			}
			

			try {
				int speed = (int) (MAXSPEED / getSpeedPercentage((i * 1.0) / CVRCOUNT));
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				CCLog.addError(e);
			}
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				isShuffled = true;
				btnMain.setEnabled(true);
			}
		});
	}

	private double getSpeedPercentage(double progresspercentage) {
		double x = progresspercentage;
		double r = (((10 * Math.exp(10 * x + 5)) / (Math.pow((Math.exp(10 * x) + Math.exp(5)), 2)))*2)/5;
		return r;
	}
}
