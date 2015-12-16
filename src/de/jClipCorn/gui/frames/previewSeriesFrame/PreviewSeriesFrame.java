package de.jClipCorn.gui.frames.previewSeriesFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.addEpisodesFrame.AddEpisodesFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.createSeriesFolderStructureFrame.CreateSeriesFolderStructureFrame;
import de.jClipCorn.gui.frames.displayGenresDialog.DisplayGenresDialog;
import de.jClipCorn.gui.frames.displaySearchResultsDialog.DisplaySearchResultsDialog;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.frames.moveSeriesFrame.MoveSeriesDialog;
import de.jClipCorn.gui.frames.previewSeriesFrame.serTable.SerTable;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooser;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooserPopupEvent;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.helper.TextFileUtils;
import de.jClipCorn.util.listener.EpisodeSearchCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.imageparser.ImDBImageParser;

public class PreviewSeriesFrame extends JFrame implements ListSelectionListener, JCoverChooserPopupEvent, UpdateCallbackListener {
	private static final long serialVersionUID = 5484205983855802992L;

	private CCSeries dispSeries;
	
	private JPanel pnlTop;
	private JPanel pnlInfo;
	private JPanel pnlMainIntern;
	private CoverLabel lblCover;
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
	private JMenu mntmRateSeries;
	private JMenuItem mntmShowInFolder;
	private JMenuItem mntmEditSeason;
	private JMenuItem mntmShowSeasonInFolder;
	private JMenuItem mntmExportSeries;
	private JMenuItem mntmEpisodeguide;
	private JMenuItem mntmMoveSeries;
	private JMenuItem mntmCreateFolderStruct;
	private JPanel pnlTopLeft;
	private JButton btnPlayNext;

	/**
	 * @wbp.parser.constructor
	 */
	public PreviewSeriesFrame(Component owner, CCSeries ser) {
		this.dispSeries = ser;
		initGUI();
		setSize(new Dimension(1000, 700));
		setMinimumSize(new Dimension(750, 680));

		updateData();

		if (dispSeries.getSeasonCount() > 0) {
			changeSeason(dispSeries.getSeason(0));
		}
		
		setLocationRelativeTo(owner);
		initListener(ser);
	}
	
	public PreviewSeriesFrame(Component owner, CCSeason sea) {
		this.dispSeries = sea.getSeries();
		initGUI();
		setSize(new Dimension(1000, 700));
		setMinimumSize(new Dimension(750, 680));

		updateData();

		if (dispSeries.getSeasonCount() > 0) {
			changeSeason(dispSeries.getSeason(0));
		}
		
		cvrChooser.setCurrSelected(sea.getSeasonNumber());
		
		setLocationRelativeTo(owner);
		initListener(sea.getSeries());
	}
	
	public PreviewSeriesFrame(Component owner, CCEpisode epi) {
		this.dispSeries = epi.getSeries();
		initGUI();
		setSize(new Dimension(1000, 700));
		setMinimumSize(new Dimension(750, 680));

		updateData();

		if (dispSeries.getSeasonCount() > 0) {
			changeSeason(dispSeries.getSeason(0));
		}
		
		cvrChooser.setCurrSelected(epi.getSeason().getSeasonNumber());
		
		tabSeason.select(epi);
		
		setLocationRelativeTo(owner);
		initListener(epi.getSeries());
	}

	private void initListener(CCSeries ser) {
		ser.getMovieList().addChangeListener(new CCDBUpdateListener() {
			@Override
			public void onRemMovie(CCDatabaseElement el) {
				// Do nothing
			}
			
			@Override
			public void onRefresh() {
				// Do nothing
			}
			
			@Override
			public void onAfterLoad() {
				// Do nothing
			}
			
			@Override
			public void onChangeDatabaseElement(CCDatabaseElement el) {
				if (el.equals(dispSeries)) {
					updateData();
				}
			}
			
			@Override
			public void onAddDatabaseElement(CCDatabaseElement mov) {
				if (mov.equals(dispSeries)) {
					updateData();
				}
			}
		});
	}

	private void initGUI() {
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		if (Main.DEBUG) {
			setTitle("<LID:" + dispSeries.getLocalID() + "><SID:" + dispSeries.getSeriesID() + "> " + dispSeries.getTitle() + " (" + dispSeries.getCoverName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		} else {
			setTitle(dispSeries.getTitle());
		}
		
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
		edSearch.setColumns(24);

		btnSearch = new JButton();
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startSearch();
			}
		});
		btnSearch.setIcon(CachedResourceLoader.getSmallImageIcon(Resources.ICN_FRAMES_SEARCH));
		pnlSearch.add(btnSearch);
		
		pnlTopLeft = new JPanel();
		pnlTopInfo.add(pnlTopLeft, BorderLayout.WEST);
		pnlTopLeft.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnPlayNext = new JButton(LocaleBundle.getString("PreviewSeriesFrame.btnPlayNext.caption")); //$NON-NLS-1$
		btnPlayNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resumePlaying();
			}
		});
		pnlTopLeft.add(btnPlayNext);

		pnlCoverChooser = new JPanel();
		pnlTop.add(pnlCoverChooser, BorderLayout.CENTER);

		cvrChooser = new JCoverChooser();
		pnlCoverChooser.add(cvrChooser);
		cvrChooser.setCurrSelected(0);
		cvrChooser.setCoverGap(10);
		cvrChooser.setCircleRadius(300);
		cvrChooser.setCoverWidth(ImageUtilities.COVER_WIDTH / 2);
		cvrChooser.setCoverHeight(ImageUtilities.COVER_HEIGHT / 2);
		cvrChooser.addSelectionListener(this);
		cvrChooser.addPopupListener(this);

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
		
		mntmRateSeries = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.RateSeries")); //$NON-NLS-1$
		mnSerie.add(mntmRateSeries);
		for (final CCMovieScore score : CCMovieScore.values()) {
			JMenuItem itm = new JMenuItem(score.asString(), score.getIcon());
			mntmRateSeries.add(itm);
			itm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					dispSeries.setScore(score);
					updateData();
				}
			});
		}
		
		mntmShowInFolder = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.ShowInFolder")); //$NON-NLS-1$
		mntmShowInFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PathFormatter.showInExplorer(PathFormatter.fromCCPath(dispSeries.getCommonPathStart(false)));
			}
		});
		mnSerie.add(mntmShowInFolder);
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
		
		mntmEditSeason = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season.EditSeason")); //$NON-NLS-1$
		mntmEditSeason.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EditSeriesFrame esf = new EditSeriesFrame(PreviewSeriesFrame.this, tabSeason.getSeason(), PreviewSeriesFrame.this);
				esf.setVisible(true);
			}
		});
		mnStaffel.add(mntmEditSeason);
		
		mntmShowSeasonInFolder = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season.ShowInFolder")); //$NON-NLS-1$
		mntmShowSeasonInFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PathFormatter.showInExplorer(PathFormatter.fromCCPath(tabSeason.getSeason().getCommonPathStart()));
			}
		});
		mnStaffel.add(mntmShowSeasonInFolder);
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
		
		mntmExportSeries = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ExportSeries")); //$NON-NLS-1$
		mntmExportSeries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onExportSeries();
			}
		});
		mnExtras.add(mntmExportSeries);
		
		mntmEpisodeguide = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ExportEpisodeGuide")); //$NON-NLS-1$
		mntmEpisodeguide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onExportEpisodeGuide();
			}
		});
		mnExtras.add(mntmEpisodeguide);
		
		mntmMoveSeries = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.MoveSeries")); //$NON-NLS-1$
		mntmMoveSeries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MoveSeriesDialog msf = new MoveSeriesDialog(PreviewSeriesFrame.this, dispSeries);
				msf.setVisible(true);
			}
		});
		mnExtras.add(mntmMoveSeries);
		
		mntmCreateFolderStruct = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.CreateFolderStructure")); //$NON-NLS-1$
		mntmCreateFolderStruct.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CreateSeriesFolderStructureFrame csfsf = new CreateSeriesFolderStructureFrame(PreviewSeriesFrame.this, dispSeries);
				csfsf.setVisible(true);
			}
		});
		mnExtras.add(mntmCreateFolderStruct);
		mnExtras.add(mntmAufImdbAnzeigen);

		pnlLeft = new JPanel();
		getContentPane().add(pnlLeft, BorderLayout.WEST);
		pnlLeft.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		pnlInfo = new JPanel();
		pnlLeft.add(pnlInfo);
		pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));

		lblCover = new CoverLabel(false);
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
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED){
					if (e.getKeyCode() == KeyEvent.VK_LEFT && cvrChooser.getSelectedIndex() > 0) {
						cvrChooser.setCurrSelected(cvrChooser.getSelectedIndex() - 1);
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT && cvrChooser.getSelectedIndex()+1 < cvrChooser.getElementCount()) {
						cvrChooser.setCurrSelected(cvrChooser.getSelectedIndex() + 1);
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
						if (! tabSeason.isFocusOwner()) tabSeason.requestFocus();
					}
				}
				
				return false;
			}
		});
	}

	private void updateData() {
		if (dispSeries == null) {
			return;
		}

		lblTitle.setText(dispSeries.getTitle());

		int ccidx = cvrChooser.getSelectedIndex();
		
		cvrChooser.clear();
		for (int i = 0; i < dispSeries.getSeasonCount(); i++) {
			cvrChooser.addCover(dispSeries.getSeason(i).getHalfsizeCover());
		}

		cvrChooser.setCurrSelected(ccidx);

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
		
		//if (dispSeries.isViewed()) {
			lblViewed.setIcon(ImageUtilities.sliceImage(CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_VIEWED_TRUE), 0d, (dispSeries.getViewedCount() *1d) / dispSeries.getEpisodeCount()));
		//}

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
		changeSeason(dispSeries.getSeason(cvrChooser.getSelectedIndex()));
	}
	
	@Override
	public void onPopup(int coverID, MouseEvent e) {
		(new SerCoverChooserPopupMenu(dispSeries.getSeason(coverID), this)).show(cvrChooser, e.getX(), e.getY());
	}

	public void changeSeason(CCSeason s) {
		if (s == null)
			return;
		
		if (Main.DEBUG) {
			lblStaffel.setText(String.format("<%d> %s (%d) (%s)", s.getSeasonID(), s.getTitle(), s.getYear(), s.getCoverName())); //$NON-NLS-1$
		} else {
			lblStaffel.setText(String.format("%s (%d)", s.getTitle(), s.getYear())); //$NON-NLS-1$
		}
		
		if (tabSeason.getSeason() == s)
		{
			int row_s = tabSeason.getSelectedRow();
			int pos_v = tabSeason.getVerticalScrollBar().getValue();
			int pos_h = tabSeason.getHorizontalScrollBar().getValue();

			tabSeason.changeSeason(s);
			
			tabSeason.getVerticalScrollBar().setValue(pos_v);
			tabSeason.getHorizontalScrollBar().setValue(pos_h);
			tabSeason.setSelectedRow(row_s);
		}
		else
		{
			tabSeason.changeSeason(s);			
		}
		
	}
	
	public void updateSeason() {
		changeSeason(tabSeason.getSeason());
	}

	@Override
	public void onUpdate(Object o) {
		updateData();

//		if (dispSeries.getSeasonCount() > 0) {
//			changeSeason(dispSeries.getSeason(0));
//		}
	}
	
	public void onEpisodeDblClick(CCEpisode ep) {
		if (ep != null) {
			ep.play(true);
		}
	}
	
	private void playRandomEpisode() {
		CCEpisode e = dispSeries.getRandomEpisode();
		
		if (e != null) {
			cvrChooser.setCurrSelected(e.getSeason().getSeasonNumber());
			tabSeason.select(e);
			e.play(true);
		}
	}
	
	private void playRandomViewedStateEpisode(boolean v) {
		CCEpisode e = dispSeries.getRandomEpisodeWithViewState(v);
		
		if (e != null) {
			cvrChooser.setCurrSelected(e.getSeason().getSeasonNumber());
			tabSeason.select(e);
			e.play(true);
		}
	}
	
	private void resumePlaying() {
		CCEpisode e = dispSeries.getNextEpisode();
		
		if (e != null) {
			cvrChooser.setCurrSelected(e.getSeason().getSeasonNumber());
			tabSeason.select(e);
			e.play(true);
		}
	}
	
	private void startSearch() {
		List<CCEpisode> el = dispSeries.getEpisodeList();
		List<CCEpisode> found = new ArrayList<>();
		
		for (int i = 0; i < el.size(); i++) {
			if (StringUtils.containsIgnoreCase(el.get(i).getTitle(), edSearch.getText())) {
					found.add(el.get(i));
			}
		}
		
		if (found.isEmpty()) {
			DisplaySearchResultsDialog.disposeInstance();
		} else {
			DisplaySearchResultsDialog dsrd = new DisplaySearchResultsDialog(found, edSearch);
			dsrd.addListener(new EpisodeSearchCallbackListener() {
				@Override
				public void show(CCEpisode episode) {
					cvrChooser.setCurrSelected(episode.getSeason().getSeasonNumber());
					tabSeason.select(episode);
				}
			});
			dsrd.setVisible(true);
		}
	}

	private void onExportSeries() {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jsccexport.description", ExportHelper.EXTENSION_SINGLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showSaveDialog(PreviewSeriesFrame.this);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			final boolean includeCover = 0 == DialogHelper.showLocaleOptions(PreviewSeriesFrame.this, "ExportHelper.dialogs.exportCover"); //$NON-NLS-1$

			new Thread(new Runnable() {
				@Override
				public void run() {
					MainFrame.getInstance().beginBlockingIntermediate();

					ExportHelper.exportSeries(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_SINGLEEXPORT), dispSeries.getMovieList(), dispSeries, includeCover);
					
					MainFrame.getInstance().endBlockingIntermediate();
				}
			}, "THREAD_EXPORT_JSCCEXPORT_SERIES").start(); //$NON-NLS-1$
		}
	}
	
	private void onExportEpisodeGuide() {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_txtguide.description", ExportHelper.EXTENSION_EPISODEGUIDE)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showSaveDialog(PreviewSeriesFrame.this);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			try {
				TextFileUtils.writeTextFile(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_EPISODEGUIDE), dispSeries.getEpisodeGuide());
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}
}
