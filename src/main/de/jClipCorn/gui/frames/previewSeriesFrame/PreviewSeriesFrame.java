package de.jClipCorn.gui.frames.previewSeriesFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.menus.impl.PreviewSeriesMenuBar;
import de.jClipCorn.features.actionTree.menus.impl.SerCoverChooserPopupMenu;
import de.jClipCorn.gui.frames.displayGenresDialog.DisplayGenresDialog;
import de.jClipCorn.gui.frames.previewSeriesFrame.serTable.SerTable;
import de.jClipCorn.gui.frames.quickAddEpisodeDialog.QuickAddEpisodeDialog;
import de.jClipCorn.gui.frames.vlcRobot.VLCRobotFrame;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.guiComponents.FileDrop;
import de.jClipCorn.gui.guiComponents.OnlineRefButton;
import de.jClipCorn.gui.guiComponents.displaySearchResultsDialog.DisplaySearchResultsDialog;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooser;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooserPopupEvent;
import de.jClipCorn.gui.guiComponents.language.LanguageDisplay;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.listener.ActionCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreviewSeriesFrame extends JFrame implements ListSelectionListener, JCoverChooserPopupEvent, UpdateCallbackListener, FileDrop.Listener, ActionCallbackListener {
	private static final long serialVersionUID = 5484205983855802992L;

	private static List<Tuple<CCSeries, PreviewSeriesFrame>> _activeFrames = new ArrayList<>();

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
	private JLabel lblTitle;
	private JTextField edSearch;
	private JPanel pnlSearch;
	private JButton btnSearch;
	private PreviewSeriesMenuBar menuBar;
	private JPanel pnlOnlinescore;
	private JLabel lblLength;
	private JLabel lblSize;
	private JLabel lblViewed;
	private JLabel lblScore;
	private JLabel lblLanguage;
	private JPanel pnlAddInfo;
	private JButton btnAdditionalInfo;
	private JLabel lblfsk;
	private Component vStrut_9;
	private JPanel pnlTopLeft;
	private JButton btnPlayNext;
	private OnlineRefButton btnOnline;
	private JLabel lblGroupsMark;
	private JLabel lblGroups;
	private JPanel pnlInfo2;
	private JPanel pnlLang;
	private LanguageDisplay ctrlLang;
	private JButton btnVLCRobot;

	/**
	 * @wbp.parser.constructor
	 */
	private PreviewSeriesFrame() {
		initGUI(true);
		setSize(new Dimension(1000, getInitFrameHeight()));
		setMinimumSize(new Dimension(750, 680));
	}
	
	private PreviewSeriesFrame(Component owner, CCSeries ser) {
		this.dispSeries = ser;
		initGUI(false);
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

	private PreviewSeriesFrame(Component owner, CCSeason sea) {
		this.dispSeries = sea.getSeries();
		initGUI(false);
		setSize(new Dimension(1000, getInitFrameHeight()));
		setMinimumSize(new Dimension(750, 680));

		updateData();

		changeSeason(sea);
		cvrChooser.setCurrSelected(sea.getSeasonNumber());

		setLocationRelativeTo(owner);
		initListener(sea.getSeries());
	}

	private PreviewSeriesFrame(Component owner, CCEpisode epi) {
		this.dispSeries = epi.getSeries();
		initGUI(false);
		setSize(new Dimension(1000, getInitFrameHeight()));
		setMinimumSize(new Dimension(750, 680));

		updateData();

		changeSeason(epi.getSeason());
		cvrChooser.setCurrSelected(epi.getSeason().getSeasonNumber());

		tabSeason.select(epi);

		setLocationRelativeTo(owner);
		initListener(epi.getSeries());
	}

	public static void show(Component owner, CCSeries data) {
		if (!CCProperties.getInstance().PROP_PREVIEWSERIES_SINGLETON.getValue()) {
			new PreviewSeriesFrame(owner, data).setVisible(true);
			return;
		}

		Tuple<CCSeries, PreviewSeriesFrame> frame = CCStreams.iterate(_activeFrames).firstOrNull(f -> f.Item1 == data);
		if (frame != null) {
			if(frame.Item2.getState() != Frame.NORMAL) frame.Item2.setState(Frame.NORMAL);
			frame.Item2.toFront();
			frame.Item2.repaint();
			return;
		}

		new PreviewSeriesFrame(owner, data).setVisible(true);
	}

	public static void show(Component owner, CCSeason data) {
		if (!CCProperties.getInstance().PROP_PREVIEWSERIES_SINGLETON.getValue()) {
			new PreviewSeriesFrame(owner, data).setVisible(true);
			return;
		}

		Tuple<CCSeries, PreviewSeriesFrame> frame = CCStreams.iterate(_activeFrames).firstOrNull(f -> f.Item1 == data.getSeries());
		if (frame != null) {
			if(frame.Item2.getState() != Frame.NORMAL) frame.Item2.setState(Frame.NORMAL);
			frame.Item2.toFront();
			frame.Item2.repaint();
			frame.Item2.changeSeason(data);
			frame.Item2.cvrChooser.setCurrSelected(data.getSeasonNumber());
			frame.Item2.repaint();
			return;
		}

		new PreviewSeriesFrame(owner, data).setVisible(true);
	}

	public static void show(Component owner, CCEpisode data) {
		if (!CCProperties.getInstance().PROP_PREVIEWSERIES_SINGLETON.getValue()) {
			new PreviewSeriesFrame(owner, data).setVisible(true);
			return;
		}

		Tuple<CCSeries, PreviewSeriesFrame> frame = CCStreams.iterate(_activeFrames).firstOrNull(f -> f.Item1 == data.getSeries());
		if (frame != null) {
			if(frame.Item2.getState() != Frame.NORMAL) frame.Item2.setState(Frame.NORMAL);
			frame.Item2.toFront();
			frame.Item2.repaint();
			frame.Item2.changeSeason(data.getSeason());
			frame.Item2.cvrChooser.setCurrSelected(data.getSeason().getSeasonNumber());
			frame.Item2.tabSeason.select(data);
			frame.Item2.repaint();
			return;
		}

		new PreviewSeriesFrame(owner, data).setVisible(true);
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
		final Tuple<CCSeries, PreviewSeriesFrame> d = Tuple.Create(ser, this);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				_activeFrames.remove(d);
			}
		});
		_activeFrames.add(d);

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

	private void initGUI(boolean windowBuilder) {
		setIconImage(Resources.IMG_FRAME_ICON.get());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		if (Main.DEBUG) {
			setTitle("<LID:" + dispSeries.getLocalID() + "><SID:" + dispSeries.getSeriesID() + "> " + dispSeries.getTitle() + " (" + dispSeries.getCoverID() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		} else {
			setTitle(dispSeries.getTitle());
		}

		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(110dlu;pref)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("0dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(80dlu;pref)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(110dlu;pref)"),}, //$NON-NLS-1$
				new RowSpec[] {
						FormSpecs.DEFAULT_ROWSPEC,
						RowSpec.decode("max(50dlu;pref):grow"),})); //$NON-NLS-1$

		pnlTopLeft = new JPanel();
		pnlTop.add(pnlTopLeft, "1, 1, 1, 2, fill, fill"); //$NON-NLS-1$

		btnPlayNext = new JButton(LocaleBundle.getString("PreviewSeriesFrame.btnPlayNext.caption")); //$NON-NLS-1$
		btnPlayNext.addActionListener(e -> CCActionTree.getInstance().find("ResumeSeries").execute(this, ActionSource.PREV_SER_FRAME, Collections.singletonList(dispSeries), this)); //$NON-NLS-1$
		pnlTopLeft.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.LINE_GAP_ROWSPEC,
						RowSpec.decode("26px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,}));
		pnlTopLeft.add(btnPlayNext, "2, 2, fill, top"); //$NON-NLS-1$

		btnVLCRobot = new JButton(LocaleBundle.getString("PreviewSeriesFrame.btnPlayRobot.caption"), Resources.ICN_MENUBAR_VLCROBOT.get16x16()); //$NON-NLS-1$
		btnVLCRobot.addActionListener(e -> autoPlay());
		btnVLCRobot.setEnabled(CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue() && !Str.isNullOrWhitespace(MoviePlayer.getVLCPath()));
		pnlTopLeft.add(btnVLCRobot, "2, 4, fill, top"); //$NON-NLS-1$

		lblTitle = new JLabel("<Title>"); //$NON-NLS-1$
		pnlTop.add(lblTitle, "3, 1, fill, fill"); //$NON-NLS-1$
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 28)); //$NON-NLS-1$

		pnlSearch = new JPanel();
		pnlTop.add(pnlSearch, "5, 1, 3, 1, fill, fill"); //$NON-NLS-1$
		pnlSearch.setBorder(null);

		edSearch = new JTextField();
		edSearch.addActionListener(e -> startSearch());
		pnlSearch.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("pref:grow"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("50px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.LINE_GAP_ROWSPEC,
						RowSpec.decode("26px"), //$NON-NLS-1$
						FormSpecs.LINE_GAP_ROWSPEC,}));
		pnlSearch.add(edSearch, "2, 2, fill, fill"); //$NON-NLS-1$
		edSearch.setColumns(24);

		btnSearch = new JButton();
		btnSearch.addActionListener(e -> startSearch());
		btnSearch.setIcon(Resources.ICN_FRAMES_SEARCH.get16x16());
		pnlSearch.add(btnSearch, "4, 2, left, top"); //$NON-NLS-1$

		cvrChooser = new JCoverChooser(false);
		pnlTop.add(cvrChooser, "3, 2, 3, 1, fill, fill"); //$NON-NLS-1$
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

		lblStaffel = new JLabel("<Season>"); //$NON-NLS-1$
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

		menuBar = new PreviewSeriesMenuBar(this, this.dispSeries, () -> onUpdate(null));
		if (!windowBuilder) setJMenuBar(menuBar);

		pnlInfo2 = new JPanel();
		getContentPane().add(pnlInfo2, BorderLayout.WEST);
		pnlInfo2.setSize(new Dimension(100, 100));
		pnlInfo2.setBorder(new EmptyBorder(0, 4, 8, 4));
		pnlInfo2.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(22px;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.DEFAULT_ROWSPEC,}));

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
		
		pnlLang = new JPanel();
		pnlInfo2.add(pnlLang, "1, 13, fill, fill"); //$NON-NLS-1$
		pnlLang.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
				
		lblLanguage = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		pnlLang.add(lblLanguage, "1, 1, left, center"); //$NON-NLS-1$
		lblLanguage.setFont(new Font("Tahoma", Font.PLAIN, 12)); //$NON-NLS-1$
		lblLanguage.setHorizontalTextPosition(SwingConstants.LEADING);
		
		ctrlLang = new LanguageDisplay();
		pnlLang.add(ctrlLang, "3, 1, fill, center"); //$NON-NLS-1$

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

	private void autoPlay() {
		VLCRobotFrame.show(this);
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
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_0.get());
			break;
		case STARS_0_5:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_1.get());
			break;
		case STARS_1_0:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_2.get());
			break;
		case STARS_1_5:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_3.get());
			break;
		case STARS_2_0:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_4.get());
			break;
		case STARS_2_5:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_5.get());
			break;
		case STARS_3_0:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_6.get());
			break;
		case STARS_3_5:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_7.get());
			break;
		case STARS_4_0:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_8.get());
			break;
		case STARS_4_5:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_9.get());
			break;
		case STARS_5_0:
			lblOnlineScore.setIcon(Resources.ICN_TABLE_ONLINESCORE_10.get());
			break;
		}

		lblLength.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblLength.text", TimeIntervallFormatter.formatPointed(dispSeries.getLength()))); //$NON-NLS-1$

		lblSize.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblSize.text", FileSizeFormatter.format(dispSeries.getFilesize()))); //$NON-NLS-1$

		lblViewed.setText(LocaleBundle.getFormattedString("PreviewSeriesFrame.lblViewed.text", dispSeries.getViewedCount(), dispSeries.getEpisodeCount())); //$NON-NLS-1$

		// if (dispSeries.isViewed()) {
		lblViewed.setIcon(ImageUtilities.sliceImage(Resources.ICN_TABLE_VIEWED_TRUE.get(), 0d, (dispSeries.getViewedCount() * 1d) / dispSeries.getEpisodeCount()));
		// }

		if (dispSeries.getScore() == CCUserScore.RATING_NO) {
			lblScore.setText(LocaleBundle.getString("PreviewSeriesFrame.lblScore.text") + " - "); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			lblScore.setText(LocaleBundle.getString("PreviewSeriesFrame.lblScore.text") + " " + dispSeries.getScore().asString()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		lblScore.setIcon(dispSeries.getScore().getIcon());

		ctrlLang.setValue(dispSeries.getAllLanguages());

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
			lblStaffel.setText(String.format("<%d> %s (%d) (%s)", s.getLocalID(), s.getTitle(), s.getYear(), s.getCoverID())); //$NON-NLS-1$
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
		if (tabSeason.getSeason() != null) changeSeason(tabSeason.getSeason());
	}

	@Override
	public void onCallbackPlayed(CCEpisode e) {
		if (e == null) return;
		cvrChooser.setCurrSelected(e.getSeason().getSeasonNumber());
		tabSeason.select(e);
	}

	public void onEpisodeDblClick(CCEpisode ep) {
		if (ep != null) {
			ep.play(true);
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

	@Override
	public void filesDropped(File[] files) {
		if (files.length != 1) return;

		if (!CCFileFormat.isValidMovieFormat(files[0])) return;

		final CCSeason s = tabSeason.getSeason();
		if (s == null) return;

		final File f = files[0];
		if (f == null) return;

		SwingUtilities.invokeLater(() -> QuickAddEpisodeDialog.show(this, s, f));
	}

	public CCSeason getSelectedSeason() {
		return tabSeason.getSeason();
	}

	public CCEpisode getSelectedEpisode() {
		return tabSeason.getSelectedEpisode();
	}
}
