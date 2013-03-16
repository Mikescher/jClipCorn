package de.jClipCorn.gui.frames.previewSeriesFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.addEpisodesFrame.AddEpisodesFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.displayGenresDialog.DisplayGenresDialog;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.serTable.SerTable;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.FileSizeFormatter;
import de.jClipCorn.util.HTTPUtilities;
import de.jClipCorn.util.ImageUtilities;
import de.jClipCorn.util.TimeIntervallFormatter;
import de.jClipCorn.util.UpdateCallbackListener;
import de.jClipCorn.util.parser.imageparser.ImDBImageParser;

public class PreviewSeriesFrame extends JFrame implements ListSelectionListener, UpdateCallbackListener {
	private static final long serialVersionUID = 5484205983855802992L;

	private CCSeries dispSeries;
	
	private JPanel pnlTop;
	private JPanel pnlInfo;
	private JPanel pnlMainIntern;
	private JLabel lblCover;
	private JCoverChooser cvrChooser;
	private JLabel lblStaffel;
	private SerTable tabSeason;
	private JPanel pnlMain;
	private Component hStrut_2;
	private Component hStrut_3;
	private Component vStrut_1;
	private JLabel lblOnlineScore;
	private JPanel pnlTopInfo;
	private JPanel pnlCoverChooser;
	private JLabel lblTitle;
	private JTextField edSearch;
	private JPanel pnlSearch;
	private JButton btnSearch;
	private Component hStrut_1;
	private JMenuBar menuBar;
	private JPanel pnlOnlinescore;
	private JLabel lblLength;
	private JLabel lblSize;
	private Component vStrut_4;
	private Component vStrut_5;
	private Component vStrut_3;
	private Component vStrut_2;
	private JLabel lblViewed;
	private Component vStrut_6;
	private JLabel lblScore;
	private Component vStrut_7;
	private JLabel lblLanguage;
	private Component vStrut_8;
	private JPanel pnlLeft;
	private JPanel pnlAddInfo;
	private JButton btnAdditionalInfo;
	private JLabel lblfsk;
	private Component verticalStrut;
	private JMenu mnSerie;
	private JMenu mnStaffel;
	private JMenuItem mntmEpisodeHinzufgen;
	private JMenuItem mntmStaffelLschen;
	private JMenuItem mntmStaffelHinzufgen;
	private JMenuItem mntmSerieBearbeiten;
	private JMenuItem mntmSerieLschen;
	private JMenu mnExtras;
	private JMenuItem mntmAufImdbAnzeigen;
	private JMenu mnZuflligeEpisodeAbspielen;
	private JMenuItem mntmAlle;
	private JMenuItem mntmGesehene;
	private JMenuItem mntmNewMenuItem;
	private Component vStrut_9;
	private JMenuItem mntmResumePlaying;

	public PreviewSeriesFrame(Component owner, CCSeries ser) {
		this.dispSeries = ser;
		initGUI();
		setSize(new Dimension(1000, 700));
		setMinimumSize(new Dimension(750, 680));

		intialize();

		if (dispSeries.getSeasonCount() > 0) {
			changeSeason(dispSeries.getSeason(0));
		}
		
		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(dispSeries.getTitle());

		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));

		pnlTopInfo = new JPanel();
		pnlTop.add(pnlTopInfo, BorderLayout.NORTH);
		pnlTopInfo.setLayout(new BorderLayout(0, 0));

		lblTitle = new JLabel();
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 28)); //$NON-NLS-1$
		pnlTopInfo.add(lblTitle, BorderLayout.CENTER);

		pnlSearch = new JPanel();
		pnlSearch.setBorder(null);
		pnlTopInfo.add(pnlSearch, BorderLayout.EAST);

		edSearch = new JTextField();
		edSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startSearch();
			}
		});
		pnlSearch.add(edSearch);
		edSearch.setColumns(16);

		btnSearch = new JButton();
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startSearch();
			}
		});
		btnSearch.setIcon(CachedResourceLoader.getSmallImageIcon(Resources.ICN_FRAMES_SEARCH));
		pnlSearch.add(btnSearch);

		hStrut_1 = Box.createHorizontalStrut(200);
		pnlTopInfo.add(hStrut_1, BorderLayout.WEST);

		pnlCoverChooser = new JPanel();
		pnlTop.add(pnlCoverChooser, BorderLayout.CENTER);

		cvrChooser = new JCoverChooser();
		pnlCoverChooser.add(cvrChooser);
		cvrChooser.setCurrSelected(0);
		cvrChooser.setCoverGap(10);
		cvrChooser.setCircleRadius(300);
		cvrChooser.setCoverWidth(ImageUtilities.COVER_WIDTH / 2);
		cvrChooser.setCoverHeight(ImageUtilities.COVER_HEIGHT / 2);
		cvrChooser.addListener(this);

		pnlMain = new JPanel();
		pnlMain.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new BorderLayout(0, 0));

		pnlMainIntern = new JPanel();
		pnlMain.add(pnlMainIntern);
		pnlMainIntern.setLayout(new BorderLayout(0, 0));

		tabSeason = new SerTable((CCSeason) null, this);
		pnlMainIntern.add(tabSeason);

		lblStaffel = new JLabel();
		lblStaffel.setFont(new Font("Tahoma", Font.PLAIN, 25)); //$NON-NLS-1$
		lblStaffel.setHorizontalAlignment(SwingConstants.CENTER);
		pnlMainIntern.add(lblStaffel, BorderLayout.NORTH);

		hStrut_2 = Box.createHorizontalStrut(8);
		pnlMain.add(hStrut_2, BorderLayout.WEST);

		hStrut_3 = Box.createHorizontalStrut(8);
		pnlMain.add(hStrut_3, BorderLayout.EAST);

		vStrut_1 = Box.createVerticalStrut(8);
		pnlMain.add(vStrut_1, BorderLayout.SOUTH);
		
		vStrut_9 = Box.createVerticalStrut(8);
		pnlMain.add(vStrut_9, BorderLayout.NORTH);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnSerie = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series")); //$NON-NLS-1$
		menuBar.add(mnSerie);

		mntmStaffelHinzufgen = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.AddSeason")); //$NON-NLS-1$
		mntmStaffelHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				(new AddSeasonFrame(PreviewSeriesFrame.this, dispSeries, PreviewSeriesFrame.this)).setVisible(true);
			}
		});
		mnSerie.add(mntmStaffelHinzufgen);

		mntmSerieBearbeiten = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.EditSeries")); //$NON-NLS-1$
		mntmSerieBearbeiten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new EditSeriesFrame(PreviewSeriesFrame.this, dispSeries, PreviewSeriesFrame.this)).setVisible(true);
			}
		});
		mnSerie.add(mntmSerieBearbeiten);

		mntmSerieLschen = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.DeleteSeries")); //$NON-NLS-1$
		mntmSerieLschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (DialogHelper.showLocaleYesNo(PreviewSeriesFrame.this, "Dialogs.DeleteSeries")) {//$NON-NLS-1$
					dispSeries.delete();

					dispose();
				}
			}
		});
		mnSerie.add(mntmSerieLschen);

		mnStaffel = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season")); //$NON-NLS-1$
		menuBar.add(mnStaffel);

		mntmEpisodeHinzufgen = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season.AddEpisodes")); //$NON-NLS-1$
		mntmEpisodeHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new AddEpisodesFrame(PreviewSeriesFrame.this, tabSeason.getSeason(), PreviewSeriesFrame.this)).setVisible(true);
			}
		});
		mnStaffel.add(mntmEpisodeHinzufgen);

		mntmStaffelLschen = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season.DeleteSeason")); //$NON-NLS-1$
		mntmStaffelLschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (DialogHelper.showLocaleYesNo(PreviewSeriesFrame.this, "Dialogs.DeleteSeason")) {//$NON-NLS-1$
					tabSeason.getSeason().delete();
					
					onUpdate(tabSeason.getSeason());
				}
			}
		});
		mnStaffel.add(mntmStaffelLschen);

		mnExtras = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras")); //$NON-NLS-1$
		menuBar.add(mnExtras);

		mnZuflligeEpisodeAbspielen = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.Random")); //$NON-NLS-1$
		mnExtras.add(mnZuflligeEpisodeAbspielen);

		mntmAlle = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.Random.All")); //$NON-NLS-1$
		mntmAlle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playRandomEpisode();
			}
		});
		mnZuflligeEpisodeAbspielen.add(mntmAlle);

		mntmGesehene = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.Random.Viewed")); //$NON-NLS-1$
		mntmGesehene.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playRandomViewedStateEpisode(true);
			}
		});
		mnZuflligeEpisodeAbspielen.add(mntmGesehene);

		mntmNewMenuItem = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.Random.NotViewed")); //$NON-NLS-1$
		mntmNewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playRandomViewedStateEpisode(false);
			}
		});
		mnZuflligeEpisodeAbspielen.add(mntmNewMenuItem);

		mntmAufImdbAnzeigen = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ViewImDB")); //$NON-NLS-1$
		mntmAufImdbAnzeigen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HTTPUtilities.openInBrowser(ImDBImageParser.getSearchURL(dispSeries.getTitle(), CCMovieTyp.SERIES));
			}
		});
		
		mntmResumePlaying = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ResumePlay")); //$NON-NLS-1$
		mntmResumePlaying.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resumePlaying();
			}
		});
		mnExtras.add(mntmResumePlaying);
		mnExtras.add(mntmAufImdbAnzeigen);

		pnlLeft = new JPanel();
		getContentPane().add(pnlLeft, BorderLayout.WEST);
		pnlLeft.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		pnlInfo = new JPanel();
		pnlLeft.add(pnlInfo);
		pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));

		lblCover = new JLabel();
		lblCover.setIcon(new ImageIcon(CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND)));
		pnlInfo.add(lblCover);

		vStrut_2 = Box.createVerticalStrut(4);
		pnlInfo.add(vStrut_2);

		pnlOnlinescore = new JPanel();
		pnlOnlinescore.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlOnlinescore.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlInfo.add(pnlOnlinescore);

		lblOnlineScore = new JLabel();
		lblOnlineScore.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_pnlOnlinescore = new GroupLayout(pnlOnlinescore);
		gl_pnlOnlinescore.setHorizontalGroup(gl_pnlOnlinescore.createParallelGroup(Alignment.LEADING).addComponent(lblOnlineScore, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE));
		gl_pnlOnlinescore.setVerticalGroup(gl_pnlOnlinescore.createParallelGroup(Alignment.LEADING).addComponent(lblOnlineScore));
		pnlOnlinescore.setLayout(gl_pnlOnlinescore);

		vStrut_3 = Box.createVerticalStrut(4);
		pnlInfo.add(vStrut_3);

		lblLength = new JLabel();
		lblLength.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		pnlInfo.add(lblLength);

		vStrut_4 = Box.createVerticalStrut(4);
		pnlInfo.add(vStrut_4);

		lblSize = new JLabel();
		lblSize.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		pnlInfo.add(lblSize);

		vStrut_5 = Box.createVerticalStrut(4);
		pnlInfo.add(vStrut_5);

		lblViewed = new JLabel();
		lblViewed.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		lblViewed.setHorizontalTextPosition(SwingConstants.LEADING);
		pnlInfo.add(lblViewed);

		vStrut_6 = Box.createVerticalStrut(4);
		pnlInfo.add(vStrut_6);

		lblScore = new JLabel();
		lblScore.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		lblScore.setHorizontalTextPosition(SwingConstants.LEADING);
		pnlInfo.add(lblScore);

		vStrut_7 = Box.createVerticalStrut(4);
		pnlInfo.add(vStrut_7);

		lblLanguage = new JLabel();
		lblLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		lblLanguage.setHorizontalTextPosition(SwingConstants.LEADING);
		pnlInfo.add(lblLanguage);

		vStrut_8 = Box.createVerticalStrut(4);
		pnlInfo.add(vStrut_8);

		lblfsk = new JLabel();
		lblfsk.setHorizontalTextPosition(SwingConstants.LEADING);
		pnlInfo.add(lblfsk);

		verticalStrut = Box.createVerticalStrut(4);
		pnlInfo.add(verticalStrut);

		pnlAddInfo = new JPanel();
		pnlAddInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlInfo.add(pnlAddInfo);

		btnAdditionalInfo = new JButton(LocaleBundle.getString("PreviewSeriesFrame.btnAdditional.text")); //$NON-NLS-1$
		btnAdditionalInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DisplayGenresDialog dgd = new DisplayGenresDialog(dispSeries.getGenres(), btnAdditionalInfo.getWidth(), btnAdditionalInfo);
				dgd.setVisible(true);
			}
		});
		pnlAddInfo.add(btnAdditionalInfo);
	}

	private void intialize() {
		if (dispSeries == null) {
			return;
		}

		lblTitle.setText(dispSeries.getTitle());

		cvrChooser.clear();
		for (int i = 0; i < dispSeries.getSeasonCount(); i++) {
			cvrChooser.addCover(dispSeries.getSeason(i).getHalfsizeCover());
		}

		switch (dispSeries.getOnlinescore()) {
		case STARS_0_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_0));
			break;
		case STARS_0_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_1));
			break;
		case STARS_1_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_2));
			break;
		case STARS_1_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_3));
			break;
		case STARS_2_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_4));
			break;
		case STARS_2_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_5));
			break;
		case STARS_3_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_6));
			break;
		case STARS_3_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_7));
			break;
		case STARS_4_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_8));
			break;
		case STARS_4_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_9));
			break;
		case STARS_5_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_ONLINESCORE_10));
			break;
		}

		lblLength.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblLength.text", TimeIntervallFormatter.formatPointed(dispSeries.getLength()))); //$NON-NLS-1$

		lblSize.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblSize.text", FileSizeFormatter.format(dispSeries.getFilesize()))); //$NON-NLS-1$

		lblViewed.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblViewed.text", dispSeries.getViewedCount(), dispSeries.getEpisodeCount())); //$NON-NLS-1$
		if (dispSeries.isViewed()) {
			lblViewed.setIcon(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_VIEWED_TRUE));
		}

		if (dispSeries.getScore() == CCMovieScore.RATING_NO) {
			lblScore.setText(LocaleBundle.getString("PreviewSeriesFrame.lblScore.text") + " - "); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			lblScore.setText(LocaleBundle.getString("PreviewSeriesFrame.lblScore.text") + " " + dispSeries.getScore().asString()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		lblScore.setIcon(dispSeries.getScore().getIcon());

		lblLanguage.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblLanguage.text", dispSeries.getLanguage().asString())); //$NON-NLS-1$
		lblLanguage.setIcon(dispSeries.getLanguage().getIcon());

		lblfsk.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblFSK.text", dispSeries.getFSK().asString())); //$NON-NLS-1$
		lblfsk.setIcon(dispSeries.getFSK().getIcon());

		lblCover.setIcon(dispSeries.getCoverIcon());
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		changeSeason(dispSeries.getSeason(cvrChooser.getCurrentSelected()));
	}

	public void changeSeason(CCSeason s) {
		lblStaffel.setText(String.format("%s (%d)", s.getTitle(), s.getYear())); //$NON-NLS-1$
		tabSeason.changeSeason(s);
	}
	
	public void updateSeason() {
		changeSeason(tabSeason.getSeason());
	}

	@Override
	public void onUpdate(Object o) {
		intialize();

		if (dispSeries.getSeasonCount() > 0) {
			changeSeason(dispSeries.getSeason(0));
		}
	}
	
	public void onEpisodeDblClick(CCEpisode ep) {
		if (ep != null) {
			ep.play();
		}
	}
	
	private void playRandomEpisode() {
		CCEpisode e = dispSeries.getRandomEpisode();
		
		if (e != null) {
			cvrChooser.setCurrSelected(e.getSeason().getSeasonNumber());
			tabSeason.select(e);
			e.play();
		}
	}
	
	private void playRandomViewedStateEpisode(boolean v) {
		CCEpisode e = dispSeries.getRandomEpisodeWithViewState(v);
		
		if (e != null) {
			cvrChooser.setCurrSelected(e.getSeason().getSeasonNumber());
			tabSeason.select(e);
			e.play();
		}
	}
	
	private void resumePlaying() {
		CCEpisode e = dispSeries.getNextEpisode();
		
		if (e != null) {
			cvrChooser.setCurrSelected(e.getSeason().getSeasonNumber());
			tabSeason.select(e);
			e.play();
		}
	}
	
	private void startSearch() {
		ArrayList<CCEpisode> el = dispSeries.getEpisodeList();
		
		boolean found = false;
		
		for (int a = 0; a < 2; a++) {
			for (int i = 0; i < el.size(); i++) {
				if (found) {
					if (StringUtils.containsIgnoreCase(el.get(i).getTitle(), edSearch.getText())) {
						cvrChooser.setCurrSelected(el.get(i).getSeason().getSeasonNumber());
						tabSeason.select(el.get(i));
						return;
					}
				}
				if (el.get(i) == tabSeason.getSelectedEpisode()) {
					found  = true;
				}
			}
			found = true;
		}
		
		tabSeason.select(null);
	}
}
