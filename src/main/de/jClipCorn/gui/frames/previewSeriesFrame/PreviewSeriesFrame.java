package de.jClipCorn.gui.frames.previewSeriesFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.gui.frames.quickAddEpisodeDialog.QuickAddEpisodeDialog;
import de.jClipCorn.gui.guiComponents.FileDrop;
import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.frames.addEpisodesFrame.AddEpisodesFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.createSeriesFolderStructureFrame.CreateSeriesFolderStructureFrame;
import de.jClipCorn.gui.frames.displayGenresDialog.DisplayGenresDialog;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.frames.moveSeriesFrame.MoveSeriesDialog;
import de.jClipCorn.gui.frames.previewSeriesFrame.serTable.SerTable;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.guiComponents.OnlineRefButton;
import de.jClipCorn.gui.guiComponents.displaySearchResultsDialog.DisplaySearchResultsDialog;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooser;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooserPopupEvent;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public class PreviewSeriesFrame extends JFrame implements ListSelectionListener, JCoverChooserPopupEvent, UpdateCallbackListener, FileDrop.Listener {
	private static final long serialVersionUID = 5484205983855802992L;

	private CCSeries dispSeries;

	private JPanel pnlTop;
	private JPanel pnlMainIntern;
	private DatabaseElementPreviewLabel lblCover;
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
	private JLabel lblViewed;
	private JLabel lblScore;
	private JLabel lblLanguage;
	private JPanel pnlAddInfo;
	private JButton btnAdditionalInfo;
	private JLabel lblfsk;
	private JMenu mnSerie;
	private JMenu mnStaffel;
	private JMenuItem mntmEpisodeHinzufgen;
	private JMenuItem mntmStaffelLschen;
	private JMenuItem mntmStaffelHinzufgen;
	private JMenuItem mntmSerieBearbeiten;
	private JMenuItem mntmSerieLschen;
	private JMenu mnExtras;
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
	private OnlineRefButton btnOnline;
	private JLabel lblGroupsMark;
	private JLabel lblGroups;
	private JPanel pnlInfo2;

	/**
	 * @wbp.parser.constructor
	 */
	public PreviewSeriesFrame(Component owner, CCSeries ser) {
		this.dispSeries = ser;
		initGUI();
		setSize(new Dimension(1000, getInitFrameHeight()));
		setMinimumSize(new Dimension(750, 680));

		updateData();

		CCSeason sea = dispSeries.getInitialDisplaySeason();
		if (sea != null) {
			changeSeason(sea);

			cvrChooser.setCurrSelected(sea.getSeasonNumber());
		}

		setLocationRelativeTo(owner);
		initListener(ser);
	}

	public PreviewSeriesFrame(Component owner, CCSeason sea) {
		this.dispSeries = sea.getSeries();
		initGUI();
		setSize(new Dimension(1000, getInitFrameHeight()));
		setMinimumSize(new Dimension(750, 680));

		updateData();

		changeSeason(sea);
		cvrChooser.setCurrSelected(sea.getSeasonNumber());

		setLocationRelativeTo(owner);
		initListener(sea.getSeries());
	}

	public PreviewSeriesFrame(Component owner, CCEpisode epi) {
		this.dispSeries = epi.getSeries();
		initGUI();
		setSize(new Dimension(1000, getInitFrameHeight()));
		setMinimumSize(new Dimension(750, 680));

		updateData();

		changeSeason(epi.getSeason());
		cvrChooser.setCurrSelected(epi.getSeason().getSeasonNumber());

		tabSeason.select(epi);

		setLocationRelativeTo(owner);
		initListener(epi.getSeries());
	}

	private static int getInitFrameHeight() {
		int epCount = CCProperties.getInstance().PROP_SERIES_PREVIEWFRAME_HEIGHT.getValue();

		int szTitlebar = 29;
		int szMenubar = 21;
		int szTopArea = 194;
		int szTitleArea = 42;
		int szTableHeader = 26;
		int szTable = 20 * epCount;
		int szBottomMargin = 11;
		int szBorderBottom = 8;

		return szTitlebar + szMenubar + szTopArea + szTitleArea + szTableHeader + szTable + szBottomMargin + szBorderBottom;
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
		edSearch.addActionListener(e -> startSearch());
		pnlSearch.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("268px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("50px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC, },
				new RowSpec[] { FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("26px"), //$NON-NLS-1$
						FormSpecs.LINE_GAP_ROWSPEC, }));
		pnlSearch.add(edSearch, "2, 2, fill, fill"); //$NON-NLS-1$
		edSearch.setColumns(24);

		btnSearch = new JButton();
		btnSearch.addActionListener(e -> startSearch());
		btnSearch.setIcon(CachedResourceLoader.getIcon(Resources.ICN_FRAMES_SEARCH.icon16x16));
		pnlSearch.add(btnSearch, "4, 2, left, top"); //$NON-NLS-1$

		pnlTopLeft = new JPanel();
		pnlTopInfo.add(pnlTopLeft, BorderLayout.WEST);
		pnlTopLeft.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnPlayNext = new JButton(LocaleBundle.getString("PreviewSeriesFrame.btnPlayNext.caption")); //$NON-NLS-1$
		btnPlayNext.addActionListener(e -> resumePlaying());
		pnlTopLeft.add(btnPlayNext);

		pnlCoverChooser = new JPanel();
		pnlTop.add(pnlCoverChooser, BorderLayout.CENTER);

		cvrChooser = new JCoverChooser(false);
		pnlCoverChooser.add(cvrChooser);
		cvrChooser.setCurrSelected(0);
		cvrChooser.setCoverGap(10);
		cvrChooser.setCircleRadius(300);
		cvrChooser.setCoverWidth(ImageUtilities.HALF_COVER_WIDTH);
		cvrChooser.setCoverHeight(ImageUtilities.HALF_COVER_HEIGHT);
		cvrChooser.addSelectionListener(this);
		cvrChooser.addPopupListener(this);

		pnlMain = new JPanel();
		pnlMain.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new BorderLayout(0, 0));

		pnlMainIntern = new JPanel();
		pnlMain.add(pnlMainIntern);
		pnlMainIntern.setLayout(new BorderLayout(0, 0));

		tabSeason = new SerTable(null, this);
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
		mntmStaffelHinzufgen.addActionListener(e -> (new AddSeasonFrame(PreviewSeriesFrame.this, dispSeries, PreviewSeriesFrame.this)).setVisible(true));
		mnSerie.add(mntmStaffelHinzufgen);

		mntmSerieBearbeiten = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.EditSeries")); //$NON-NLS-1$
		mntmSerieBearbeiten.addActionListener(e -> (new EditSeriesFrame(PreviewSeriesFrame.this, dispSeries, PreviewSeriesFrame.this)).setVisible(true));

		mntmRateSeries = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.RateSeries")); //$NON-NLS-1$
		mnSerie.add(mntmRateSeries);
		for (final CCUserScore score : CCUserScore.values()) {
			JMenuItem itm = new JMenuItem(score.asString(), score.getIcon());
			mntmRateSeries.add(itm);
			itm.addActionListener(e -> {dispSeries.setScore(score); updateData(); });
		}

		mntmShowInFolder = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.ShowInFolder")); //$NON-NLS-1$
		mntmShowInFolder.addActionListener(e -> PathFormatter.showInExplorer(PathFormatter.fromCCPath(dispSeries.getCommonPathStart(false))));
		mnSerie.add(mntmShowInFolder);
		mnSerie.add(mntmSerieBearbeiten);

		mntmSerieLschen = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Series.DeleteSeries")); //$NON-NLS-1$
		mntmSerieLschen.addActionListener(e ->
		{
			if (DialogHelper.showLocaleYesNo(PreviewSeriesFrame.this, "Dialogs.DeleteSeries")) {//$NON-NLS-1$
				dispSeries.delete();
				dispose();
			}
		});
		mnSerie.add(mntmSerieLschen);

		mnStaffel = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season")); //$NON-NLS-1$
		menuBar.add(mnStaffel);

		mntmEpisodeHinzufgen = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season.AddEpisodes")); //$NON-NLS-1$
		mntmEpisodeHinzufgen.addActionListener(e -> (new AddEpisodesFrame(PreviewSeriesFrame.this, tabSeason.getSeason(), PreviewSeriesFrame.this)).setVisible(true));
		mnStaffel.add(mntmEpisodeHinzufgen);

		mntmStaffelLschen = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season.DeleteSeason")); //$NON-NLS-1$
		mntmStaffelLschen.addActionListener(e ->
		{
			if (DialogHelper.showLocaleYesNo(PreviewSeriesFrame.this, "Dialogs.DeleteSeason")) {//$NON-NLS-1$
				tabSeason.getSeason().delete();

				onUpdate(tabSeason.getSeason());
			}
		});

		mntmEditSeason = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season.EditSeason")); //$NON-NLS-1$
		mntmEditSeason.addActionListener(e ->
		{
			EditSeriesFrame esf = new EditSeriesFrame(PreviewSeriesFrame.this, tabSeason.getSeason(), PreviewSeriesFrame.this);
			esf.setVisible(true);
		});
		mnStaffel.add(mntmEditSeason);

		mntmShowSeasonInFolder = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Season.ShowInFolder")); //$NON-NLS-1$
		mntmShowSeasonInFolder.addActionListener(e -> PathFormatter.showInExplorer(PathFormatter.fromCCPath(tabSeason.getSeason().getCommonPathStart())));
		mnStaffel.add(mntmShowSeasonInFolder);
		mnStaffel.add(mntmStaffelLschen);

		mnExtras = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras")); //$NON-NLS-1$
		menuBar.add(mnExtras);

		mnZuflligeEpisodeAbspielen = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.Random")); //$NON-NLS-1$
		mnExtras.add(mnZuflligeEpisodeAbspielen);

		mntmAlle = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.Random.All")); //$NON-NLS-1$
		mntmAlle.addActionListener(e -> playRandomEpisode());
		mnZuflligeEpisodeAbspielen.add(mntmAlle);

		mntmGesehene = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.Random.Viewed")); //$NON-NLS-1$
		mntmGesehene.addActionListener(e -> playRandomViewedStateEpisode(true));
		mnZuflligeEpisodeAbspielen.add(mntmGesehene);

		mntmNewMenuItem = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.Random.NotViewed")); //$NON-NLS-1$
		mntmNewMenuItem.addActionListener(e -> playRandomViewedStateEpisode(false));
		mnZuflligeEpisodeAbspielen.add(mntmNewMenuItem);

		if (dispSeries.getOnlineReference().hasAdditional()) {
			JMenu mntmShowOnline = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ViewOnline")); //$NON-NLS-1$
			for	(final CCSingleOnlineReference soref : dispSeries.getOnlineReference()) {
				JMenuItem subitem = new JMenuItem(soref.hasDescription() ? soref.description : soref.type.asString());
				subitem.setIcon(soref.getIcon16x16());
				subitem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (soref.isSet() && soref.isValid()) HTTPUtilities.openInBrowser(soref.getURL());
					}
				});
				mntmShowOnline.add(subitem);

				if (soref == dispSeries.getOnlineReference().Main && dispSeries.getOnlineReference().hasAdditional()) mntmShowOnline.addSeparator();
			}
			mnExtras.add(mntmShowOnline);
		}
		else {
			JMenuItem mntmShowOnline = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ViewOnline")); //$NON-NLS-1$
			mntmShowOnline.addActionListener(e ->
			{
				if (dispSeries.getOnlineReference().Main.isSet() && dispSeries.getOnlineReference().isValid())
					HTTPUtilities.openInBrowser(dispSeries.getOnlineReference().Main.getURL());
			});
			mnExtras.add(mntmShowOnline);
		}

		mntmResumePlaying = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ResumePlay")); //$NON-NLS-1$
		mntmResumePlaying.addActionListener(e -> resumePlaying());
		mnExtras.add(mntmResumePlaying);

		mntmExportSeries = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ExportSeries")); //$NON-NLS-1$
		mntmExportSeries.addActionListener(e -> onExportSeries());
		mnExtras.add(mntmExportSeries);

		mntmEpisodeguide = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.ExportEpisodeGuide")); //$NON-NLS-1$
		mntmEpisodeguide.addActionListener(e -> onExportEpisodeGuide());
		mnExtras.add(mntmEpisodeguide);

		mntmMoveSeries = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.MoveSeries")); //$NON-NLS-1$
		mntmMoveSeries.addActionListener(e -> {
			MoveSeriesDialog msf = new MoveSeriesDialog(PreviewSeriesFrame.this, dispSeries);
			msf.setVisible(true);
		});
		mnExtras.add(mntmMoveSeries);

		mntmCreateFolderStruct = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.Menu.Extras.CreateFolderStructure")); //$NON-NLS-1$
		mntmCreateFolderStruct.addActionListener(e ->
		{
			CreateSeriesFolderStructureFrame csfsf = new CreateSeriesFolderStructureFrame(PreviewSeriesFrame.this, dispSeries);
			csfsf.setVisible(true);
		});
		mnExtras.add(mntmCreateFolderStruct);

		pnlInfo2 = new JPanel();
		getContentPane().add(pnlInfo2, BorderLayout.WEST);
		pnlInfo2.setSize(new Dimension(100, 100));
		pnlInfo2.setBorder(new EmptyBorder(0, 4, 8, 4));
		pnlInfo2.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.DEFAULT_COLSPEC, }, new RowSpec[] { FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, RowSpec.decode("default:grow"), RowSpec.decode("default:grow"), FormSpecs.DEFAULT_ROWSPEC, })); //$NON-NLS-1$ //$NON-NLS-2$

		lblCover = new DatabaseElementPreviewLabel(true);
		pnlInfo2.add(lblCover, "1, 1"); //$NON-NLS-1$

		pnlOnlinescore = new JPanel();
		pnlInfo2.add(pnlOnlinescore, "1, 3"); //$NON-NLS-1$
		pnlOnlinescore.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlOnlinescore.setAlignmentX(Component.LEFT_ALIGNMENT);

		lblOnlineScore = new JLabel();
		lblOnlineScore.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_pnlOnlinescore = new GroupLayout(pnlOnlinescore);
		gl_pnlOnlinescore.setHorizontalGroup(gl_pnlOnlinescore.createParallelGroup(Alignment.LEADING).addComponent(lblOnlineScore, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE));
		gl_pnlOnlinescore.setVerticalGroup(gl_pnlOnlinescore.createParallelGroup(Alignment.LEADING).addComponent(lblOnlineScore));
		pnlOnlinescore.setLayout(gl_pnlOnlinescore);

		lblLength = new JLabel();
		pnlInfo2.add(lblLength, "1, 5"); //$NON-NLS-1$
		lblLength.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$

		lblSize = new JLabel();
		pnlInfo2.add(lblSize, "1, 7"); //$NON-NLS-1$
		lblSize.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$

		lblViewed = new JLabel();
		pnlInfo2.add(lblViewed, "1, 9"); //$NON-NLS-1$
		lblViewed.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		lblViewed.setHorizontalTextPosition(SwingConstants.LEADING);

		lblScore = new JLabel();
		pnlInfo2.add(lblScore, "1, 11"); //$NON-NLS-1$
		lblScore.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		lblScore.setHorizontalTextPosition(SwingConstants.LEADING);

		lblLanguage = new JLabel();
		pnlInfo2.add(lblLanguage, "1, 13"); //$NON-NLS-1$
		lblLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		lblLanguage.setHorizontalTextPosition(SwingConstants.LEADING);

		lblfsk = new JLabel();
		pnlInfo2.add(lblfsk, "1, 15"); //$NON-NLS-1$
		lblfsk.setHorizontalTextPosition(SwingConstants.LEADING);
		lblfsk.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$

		lblGroupsMark = new JLabel(LocaleBundle.getString("PreviewSeriesFrame.lblGroups")); //$NON-NLS-1$
		pnlInfo2.add(lblGroupsMark, "1, 17"); //$NON-NLS-1$
		lblGroupsMark.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$

		lblGroups = new JLabel("<dynamic>"); //$NON-NLS-1$
		pnlInfo2.add(lblGroups, "1, 18"); //$NON-NLS-1$
		lblGroups.setBorder(new EmptyBorder(0, 64, 0, 0));
		lblGroups.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$

		pnlAddInfo = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlAddInfo.getLayout();
		flowLayout.setVgap(0);
		pnlAddInfo.setBorder(null);
		pnlInfo2.add(pnlAddInfo, "1, 21"); //$NON-NLS-1$
		pnlAddInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

		btnAdditionalInfo = new JButton("Genres"); //$NON-NLS-1$
		btnAdditionalInfo.addActionListener(e ->
		{
			DisplayGenresDialog dgd = new DisplayGenresDialog(dispSeries.getGenres(), btnAdditionalInfo.getWidth(), btnAdditionalInfo);
			dgd.setVisible(true);
		});
		pnlAddInfo.add(btnAdditionalInfo);

		btnOnline = new OnlineRefButton();
		pnlAddInfo.add(btnOnline);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e ->
		{
			if (e.getID() == KeyEvent.KEY_PRESSED && containsKeyboardFocus()) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT && cvrChooser.getSelectedIndex() > 0) {
					cvrChooser.setCurrSelected(cvrChooser.getSelectedIndex() - 1);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT && cvrChooser.getSelectedIndex() + 1 < cvrChooser.getElementCount()) {
					cvrChooser.setCurrSelected(cvrChooser.getSelectedIndex() + 1);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
					if (!tabSeason.isFocusOwner())
						tabSeason.requestFocus();
				}
			}

			return false;
		});

		new FileDrop(tabSeason, true, this);
	}

	private boolean containsKeyboardFocus() {
		Component focus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

		while (focus != null) {
			if (focus == this)
				return true;

			focus = focus.getParent();
		}

		return false;
	}

	private void updateData() {
		if (dispSeries == null) {
			return;
		}

		lblTitle.setText(dispSeries.getTitle());

		int ccidx = cvrChooser.getSelectedIndex();

		cvrChooser.clear();
		for (int i = 0; i < dispSeries.getSeasonCount(); i++) {
			cvrChooser.addCover(dispSeries.getSeasonByArrayIndex(i), dispSeries.getSeasonByArrayIndex(i));
		}

		cvrChooser.setCurrSelected(ccidx);

		switch (dispSeries.getOnlinescore()) {
		case STARS_0_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_0));
			break;
		case STARS_0_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_1));
			break;
		case STARS_1_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_2));
			break;
		case STARS_1_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_3));
			break;
		case STARS_2_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_4));
			break;
		case STARS_2_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_5));
			break;
		case STARS_3_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_6));
			break;
		case STARS_3_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_7));
			break;
		case STARS_4_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_8));
			break;
		case STARS_4_5:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_9));
			break;
		case STARS_5_0:
			lblOnlineScore.setIcon(CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_10));
			break;
		}

		lblLength.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblLength.text", TimeIntervallFormatter.formatPointed(dispSeries.getLength()))); //$NON-NLS-1$

		lblSize.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblSize.text", FileSizeFormatter.format(dispSeries.getFilesize()))); //$NON-NLS-1$

		lblViewed.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblViewed.text", dispSeries.getViewedCount(), dispSeries.getEpisodeCount())); //$NON-NLS-1$

		// if (dispSeries.isViewed()) {
		lblViewed.setIcon(ImageUtilities.sliceImage(CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_TRUE), 0d, (dispSeries.getViewedCount() * 1d) / dispSeries.getEpisodeCount()));
		// }

		if (dispSeries.getScore() == CCUserScore.RATING_NO) {
			lblScore.setText(LocaleBundle.getString("PreviewSeriesFrame.lblScore.text") + " - "); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			lblScore.setText(LocaleBundle.getString("PreviewSeriesFrame.lblScore.text") + " " + dispSeries.getScore().asString()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		lblScore.setIcon(dispSeries.getScore().getIcon());

		lblLanguage.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblLanguage.text", dispSeries.getLanguage().asString())); //$NON-NLS-1$
		lblLanguage.setIcon(dispSeries.getLanguage().getIcon());

		lblfsk.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblFSK.text", dispSeries.getFSK().asString())); //$NON-NLS-1$
		lblfsk.setIcon(dispSeries.getFSK().getIcon());

		lblCover.setModeCover(dispSeries);

		btnOnline.setValue(dispSeries.getOnlineReference());

		lblGroups.setText("<html>" + dispSeries.getGroups().iterate().stringjoin(g -> "[" + g.Name + "]", "<br>") + "</html>"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		lblGroups.setVisible(!dispSeries.getGroups().isEmpty());
		lblGroupsMark.setVisible(!dispSeries.getGroups().isEmpty());
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		changeSeason(dispSeries.getSeasonByArrayIndex(cvrChooser.getSelectedIndex()));
	}

	@Override
	public void onPopup(int coverID, MouseEvent e) {
		(new SerCoverChooserPopupMenu(dispSeries.getSeasonByArrayIndex(coverID), this)).show(cvrChooser, e.getX(), e.getY());
	}

	public void changeSeason(CCSeason s) {
		if (s == null)
			return;

		if (Main.DEBUG) {
			lblStaffel.setText(String.format("<%d> %s (%d) (%s)", s.getSeasonID(), s.getTitle(), s.getYear(), s.getCoverName())); //$NON-NLS-1$
		} else {
			lblStaffel.setText(String.format("%s (%d)", s.getTitle(), s.getYear())); //$NON-NLS-1$
		}

		if (tabSeason.getSeason() == s) {
			int row_s = tabSeason.getSelectedRow();
			int pos_v = tabSeason.getVerticalScrollBar().getValue();
			int pos_h = tabSeason.getHorizontalScrollBar().getValue();

			tabSeason.changeSeason(s);

			tabSeason.getVerticalScrollBar().setValue(pos_v);
			tabSeason.getHorizontalScrollBar().setValue(pos_h);
			tabSeason.setSelectedRow(row_s);
		} else {
			tabSeason.changeSeason(s);
		}

	}

	public void updateSeason() {
		changeSeason(tabSeason.getSeason());
	}

	@Override
	public void onUpdate(Object o) {
		updateData();

		// if (dispSeries.getSeasonCount() > 0) {
		// changeSeason(dispSeries.getSeason(0));
		// }
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
			dsrd.addListener(episode ->
			{
				cvrChooser.setCurrSelected(episode.getSeason().getSeasonNumber());
				tabSeason.select(episode);
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

			new Thread(() ->
			{
				MainFrame.getInstance().beginBlockingIntermediate();
				ExportHelper.exportSeries(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_SINGLEEXPORT), dispSeries.getMovieList(), dispSeries, includeCover);
				MainFrame.getInstance().endBlockingIntermediate();
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
				SimpleFileUtils.writeTextFile(PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_EPISODEGUIDE), dispSeries.getEpisodeGuide());
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}

	@Override
	public void filesDropped(File[] files) {
		if (files.length != 1) return;

		final CCSeason s = tabSeason.getSeason();
		if (s == null) return;

		final File f = files[0];
		if (f == null) return;

		SwingUtilities.invokeLater(() -> QuickAddEpisodeDialog.show(this, s, f));
	}
}
