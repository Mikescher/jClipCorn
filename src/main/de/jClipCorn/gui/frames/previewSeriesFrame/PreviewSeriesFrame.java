package de.jClipCorn.gui.frames.previewSeriesFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.util.NextEpisodeHelper;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.IActionRootFrame;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.impl.ClipEpisodePopup;
import de.jClipCorn.features.actionTree.menus.impl.PreviewSeriesMenuBar;
import de.jClipCorn.features.actionTree.menus.impl.SerCoverChooserPopupMenu;
import de.jClipCorn.gui.frames.previewSeriesFrame.serTable.SerTable;
import de.jClipCorn.gui.frames.quickAddEpisodeDialog.QuickAddEpisodeDialog;
import de.jClipCorn.gui.frames.vlcRobot.VLCRobotFrame;
import de.jClipCorn.gui.guiComponents.*;
import de.jClipCorn.gui.guiComponents.displaySearchResultsDialog.DisplaySearchResultsDialog;
import de.jClipCorn.gui.guiComponents.jCoverChooser.CoverChooseEvent;
import de.jClipCorn.gui.guiComponents.jCoverChooser.JCoverChooser;
import de.jClipCorn.gui.guiComponents.language.LanguageDisplay;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.CCDBUpdateAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.ActionCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreviewSeriesFrame extends JFrame implements UpdateCallbackListener, ActionCallbackListener, IActionRootFrame
{
	private static final List<Tuple<CCSeries, PreviewSeriesFrame>> _activeFrames = new ArrayList<>();

	private final CCSeries dispSeries;

	private PreviewSeriesFrame(Component owner, CCSeries ser)
	{
		super();

		dispSeries = ser;

		initComponents();
		postInit();

		CCSeason sea = dispSeries.getInitialDisplaySeason();
		if (sea != null) { changeSeason(sea); cvrChooser.setCurrSelected(sea.getSeasonNumber()); }

		setLocationRelativeTo(owner);
	}

	private PreviewSeriesFrame(Component owner, CCSeason sea)
	{
		super();

		dispSeries = sea.getSeries();

		initComponents();
		postInit();

		changeSeason(sea);
		cvrChooser.setCurrSelected(sea.getSeasonNumber());

		setLocationRelativeTo(owner);
	}

	private PreviewSeriesFrame(Component owner, CCEpisode epi)
	{
		super();

		dispSeries = epi.getSeries();

		initComponents();
		postInit();

		changeSeason(epi.getSeason());
		cvrChooser.setCurrSelected(epi.getSeason().getSeasonNumber());

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());

		if (Main.DEBUG) {
			setTitle("<LID:" + dispSeries.getLocalID() + "> " + dispSeries.getTitle() + " (" + dispSeries.getCoverID() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			setTitle(dispSeries.getTitle());
		}

		_activeFrames.add(Tuple.Create(dispSeries, this));

		setJMenuBar(new PreviewSeriesMenuBar(this, this.dispSeries, () -> onUpdate(null)));

		updateData();

		// adjust height to match [pack]
		// because Genres and Groups in the left panel can have dynamic height
		var size1 = getSize();
		pack();
		var size2 = getSize();
		setSize(size1.width, Math.max(size1.height, size2.height));
		setMinimumSize(new Dimension(getMinimumSize().width, size2.height));

		dispSeries.getMovieList().addChangeListener(new CCDBUpdateAdapter() {
			@Override
			public void onChangeDatabaseElement(CCDatabaseElement el) {
				if (el.equals(dispSeries)) updateData();
			}

			@Override
			public void onAddDatabaseElement(CCDatabaseElement mov) {
				if (mov.equals(dispSeries)) updateData();
			}
		});

		if (!dispSeries.isEmpty())
		{
			var cep = new ClipEpisodePopup(this, dispSeries.getFirstEpisode());
			cep.implementDirectKeyListener(this, (JPanel) getContentPane());
		}

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e ->
		{
			if (e.getID() == KeyEvent.KEY_PRESSED && containsKeyboardFocus())
			{
				if (e.getKeyCode() == KeyEvent.VK_LEFT && cvrChooser.getSelectedIndex() > 0)
				{
					cvrChooser.setCurrSelected(cvrChooser.getSelectedIndex() - 1);
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT && cvrChooser.getSelectedIndex() + 1 < cvrChooser.getElementCount())
				{
					cvrChooser.setCurrSelected(cvrChooser.getSelectedIndex() + 1);
				}
				else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP)
				{
					if (!tabSeason.isFocusOwner()) tabSeason.requestFocus();
				}
			}

			return false;
		});

		new FileDrop(tabSeason, true, this::onFilesDropped);
	}

	public static void show(Component owner, CCSeries data, boolean forceNoSingleton) {
		if (forceNoSingleton || !CCProperties.getInstance().PROP_PREVIEWSERIES_SINGLETON.getValue()) {
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

	public static void show(Component owner, CCSeason data, boolean forceNoSingleton) {
		if (forceNoSingleton || !CCProperties.getInstance().PROP_PREVIEWSERIES_SINGLETON.getValue()) {
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

	public static void show(Component owner, CCEpisode data, boolean forceNoSingleton) {
		if (forceNoSingleton || !CCProperties.getInstance().PROP_PREVIEWSERIES_SINGLETON.getValue()) {
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

	private void updateData() {
		if (dispSeries == null) return;

		lblTitle.setText(dispSeries.getTitle());

		int ccidx = cvrChooser.getSelectedIndex();

		cvrChooser.clear();
		for(var v : dispSeries.iteratorSeasons()) cvrChooser.addCover(v, v);

		cvrChooser.setCurrSelected(ccidx);

		lblOnlineScore.setOnlineScore(dispSeries.getOnlinescore());

		lblLength.setText(TimeIntervallFormatter.formatPointed(dispSeries.getLength()));
		lblLength.setToolTipText(dispSeries.getMediaInfoLength().map(TimeIntervallFormatter::formatSeconds).orElse(null));

		lblSize.setText(FileSizeFormatter.format(dispSeries.getFilesize()));

		lblViewed.setText(dispSeries.getViewedCount() + "/" + dispSeries.getEpisodeCount()); //$NON-NLS-1$

		lblViewed.setIcon(ImageUtilities.sliceImage(Resources.ICN_TABLE_VIEWED_TRUE.get(), 0d, (dispSeries.getViewedCount() * 1d) / dispSeries.getEpisodeCount()));

		lblScore.setText(Str.Empty);
		lblScore.setIcon(dispSeries.Score.get().getIcon());

		ctrlLang.setValue(dispSeries.getAllLanguages());

		ctrlTags.setValue(dispSeries.getTags());

		lblFSK.setText(Str.Empty);
		lblFSK.setIcon(dispSeries.getFSK().getIcon());

		lblCover.setModeCover(dispSeries);

		btnOnline.setValue(dispSeries.getOnlineReference());

		lblGroups.setText(dispSeries.getGroups().iterate().stringjoin(p->p.Name, "\n"));

		lblGenres.setText(dispSeries.getGenres().iterate().stringjoin(CCGenre::asString, "\n"));
	}

	private void autoPlay() {
		var next = NextEpisodeHelper.findNextEpisode(dispSeries);
		VLCRobotFrame.show(this).enqueue(dispSeries);
		if (next != null) {
			changeSeason(next.getSeason());
			tabSeason.select(next);
		}
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

	public void changeSeason(CCSeason s) {
		if (s == null)
			return;

		if (Main.DEBUG) {
			lblSeason.setText(String.format("<%d> %s (%d) (%s)", s.getLocalID(), s.getTitle(), s.getYear(), s.getCoverID())); //$NON-NLS-1$
		} else {
			lblSeason.setText(String.format("%s (%d)", s.getTitle(), s.getYear())); //$NON-NLS-1$
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

	private void startSearch() {
		List<CCEpisode> el = dispSeries.getEpisodeList();
		List<CCEpisode> found = new ArrayList<>();

		for (CCEpisode ccEpisode : el) {
			if (StringUtils.containsIgnoreCase(ccEpisode.getTitle(), edSearch.getText())) {
				found.add(ccEpisode);
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

	public CCSeason getSelectedSeason() {
		return tabSeason.getSeason();
	}

	public CCEpisode getSelectedEpisode() {
		return tabSeason.getSelectedEpisode();
	}

	public void onEpisodeDblClick(CCEpisode ep) {
		if (ep != null) {
			ep.play(PreviewSeriesFrame.this, true);
		}
	}

	@Override
	public IActionSourceObject getSelectedActionSource() {
		final CCSeason s = tabSeason.getSeason();
		if (s == null) return null;

		return tabSeason.getSelectedEpisode();
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

	private void onWindowClosed() {
		_activeFrames.removeIf(p -> p.Item2 == this);
	}

	private void onPlayNext() {
		CCActionTree.getInstance().find("ResumeSeries").execute(this, ActionSource.PREV_SER_FRAME, Collections.singletonList(dispSeries), this);
	}

	private void onCoverChooserSelected() {
		changeSeason(dispSeries.getSeasonByArrayIndex(cvrChooser.getSelectedIndex()));
	}

	private void onCoverChooserPopup(CoverChooseEvent e) {
		var pm = new SerCoverChooserPopupMenu(dispSeries.getSeasonByArrayIndex(e.CoverID), this);
		pm.show(cvrChooser, e.InnerEvent.getX(), e.InnerEvent.getY());
	}

	private void onFilesDropped(File[] files) {
		if (files.length != 1) return;

		if (files[0] == null) return;

		final var f0 = FSPath.create(files[0]);

		if (!CCFileFormat.isValidMovieFormat(f0)) return;

		final CCSeason s = tabSeason.getSeason();
		if (s == null) return;

		SwingUtils.invokeLater(() -> QuickAddEpisodeDialog.show(this, s, f0));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlTop = new JPanel();
		pnlTopLeft = new JPanel();
		btnPlayNext = new JButton();
		btnVLCRobot = new JVLCRobotButton();
		lblTitle = new JLabel();
		pnlSearch = new JPanel();
		edSearch = new JTextField();
		btnSearch = new JSearchButton();
		cvrChooser = new JCoverChooser();
		pnlInfo = new JPanel();
		lblCover = new DatabaseElementPreviewLabel();
		lblOnlineScore = new OnlineScoreDisplay();
		label1 = new JLabel();
		lblLength = new JLabel();
		label2 = new JLabel();
		lblSize = new JLabel();
		label3 = new JLabel();
		lblViewed = new JLabel();
		label4 = new JLabel();
		lblScore = new JLabel();
		label7 = new JLabel();
		ctrlLang = new LanguageDisplay();
		label5 = new JLabel();
		lblFSK = new JLabel();
		label9 = new JLabel();
		ctrlTags = new TagDisplay();
		label6 = new JLabel();
		lblGroups = new MultiLineTextLabel();
		label8 = new JLabel();
		lblGenres = new MultiLineTextLabel();
		btnOnline = new OnlineRefButton();
		pnlMain = new JPanel();
		lblSeason = new JLabel();
		tabSeason = new SerTable(this);

		//======== this ========
		setTitle("<dynamic>"); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(750, 680));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onWindowClosed();
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 182px, $lcgap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, $ugap")); //$NON-NLS-1$

		//======== pnlTop ========
		{
			pnlTop.setLayout(new FormLayout(
				"110dlu, $lcgap, default:grow, $lcgap, [80dlu,pref], $lcgap, 110dlu", //$NON-NLS-1$
				"default, pref")); //$NON-NLS-1$

			//======== pnlTopLeft ========
			{
				pnlTopLeft.setLayout(new FormLayout(
					"default:grow", //$NON-NLS-1$
					"default, $lgap, default")); //$NON-NLS-1$

				//---- btnPlayNext ----
				btnPlayNext.setText(LocaleBundle.getString("PreviewSeriesFrame.btnPlayNext.caption")); //$NON-NLS-1$
				btnPlayNext.addActionListener(e -> onPlayNext());
				pnlTopLeft.add(btnPlayNext, CC.xy(1, 1));

				//---- btnVLCRobot ----
				btnVLCRobot.setText(LocaleBundle.getString("PreviewSeriesFrame.btnPlayRobot.caption")); //$NON-NLS-1$
				btnVLCRobot.addActionListener(e -> autoPlay());
				pnlTopLeft.add(btnVLCRobot, CC.xy(1, 3));
			}
			pnlTop.add(pnlTopLeft, CC.xywh(1, 1, 1, 2));

			//---- lblTitle ----
			lblTitle.setText("<Title>"); //$NON-NLS-1$
			lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getStyle() | Font.BOLD, 28f));
			lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
			pnlTop.add(lblTitle, CC.xy(3, 1, CC.FILL, CC.DEFAULT));

			//======== pnlSearch ========
			{
				pnlSearch.setLayout(new FormLayout(
					"pref:grow, $lcgap, default", //$NON-NLS-1$
					"default")); //$NON-NLS-1$

				//---- edSearch ----
				edSearch.setColumns(24);
				edSearch.addActionListener(e -> startSearch());
				pnlSearch.add(edSearch, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

				//---- btnSearch ----
				btnSearch.addActionListener(e -> startSearch());
				pnlSearch.add(btnSearch, CC.xy(3, 1, CC.DEFAULT, CC.FILL));
			}
			pnlTop.add(pnlSearch, CC.xywh(5, 1, 3, 1, CC.DEFAULT, CC.CENTER));

			//---- cvrChooser ----
			cvrChooser.setCircleRadius(300);
			cvrChooser.setCoverHalfSize(true);
			cvrChooser.addSelectionListener(e -> onCoverChooserSelected());
			cvrChooser.addPopupListener(e -> onCoverChooserPopup(e));
			pnlTop.add(cvrChooser, CC.xywh(3, 2, 3, 1, CC.FILL, CC.FILL));
		}
		contentPane.add(pnlTop, CC.xywh(2, 2, 3, 1, CC.FILL, CC.FILL));

		//======== pnlInfo ========
		{
			pnlInfo.setLayout(new FormLayout(
				"default, $lcgap, 0dlu:grow", //$NON-NLS-1$
				"default, $lgap, default, $ugap, 7*(default, $lgap), 2*(pref, $lgap), default:grow, $lgap, pref")); //$NON-NLS-1$

			//---- lblCover ----
			lblCover.setNoOverlay(true);
			pnlInfo.add(lblCover, CC.xywh(1, 1, 3, 1));

			//---- lblOnlineScore ----
			lblOnlineScore.setHorizontalAlignment(SwingConstants.CENTER);
			pnlInfo.add(lblOnlineScore, CC.xywh(1, 3, 3, 1));

			//---- label1 ----
			label1.setText(LocaleBundle.getString("PreviewSeriesFrame.lblLength.text")); //$NON-NLS-1$
			label1.setVerticalAlignment(SwingConstants.TOP);
			label1.setVerticalTextPosition(SwingConstants.TOP);
			label1.setFont(label1.getFont().deriveFont(12f));
			pnlInfo.add(label1, CC.xy(1, 5, CC.DEFAULT, CC.TOP));

			//---- lblLength ----
			lblLength.setText("<dynamic>"); //$NON-NLS-1$
			lblLength.setFont(lblLength.getFont().deriveFont(12f));
			pnlInfo.add(lblLength, CC.xy(3, 5));

			//---- label2 ----
			label2.setText(LocaleBundle.getString("PreviewSeriesFrame.lblSize.text")); //$NON-NLS-1$
			label2.setVerticalAlignment(SwingConstants.TOP);
			label2.setVerticalTextPosition(SwingConstants.TOP);
			label2.setFont(label2.getFont().deriveFont(12f));
			pnlInfo.add(label2, CC.xy(1, 7, CC.DEFAULT, CC.TOP));

			//---- lblSize ----
			lblSize.setText("<dynamic>"); //$NON-NLS-1$
			lblSize.setFont(lblSize.getFont().deriveFont(12f));
			pnlInfo.add(lblSize, CC.xy(3, 7));

			//---- label3 ----
			label3.setText(LocaleBundle.getString("PreviewSeriesFrame.lblViewed.text")); //$NON-NLS-1$
			label3.setVerticalAlignment(SwingConstants.TOP);
			label3.setFont(label3.getFont().deriveFont(12f));
			pnlInfo.add(label3, CC.xy(1, 9, CC.DEFAULT, CC.TOP));

			//---- lblViewed ----
			lblViewed.setText("<dynamic>"); //$NON-NLS-1$
			lblViewed.setHorizontalTextPosition(SwingConstants.LEADING);
			lblViewed.setFont(lblViewed.getFont().deriveFont(12f));
			pnlInfo.add(lblViewed, CC.xy(3, 9));

			//---- label4 ----
			label4.setText(LocaleBundle.getString("PreviewSeriesFrame.lblScore.text")); //$NON-NLS-1$
			label4.setVerticalAlignment(SwingConstants.TOP);
			label4.setVerticalTextPosition(SwingConstants.TOP);
			label4.setFont(label4.getFont().deriveFont(12f));
			pnlInfo.add(label4, CC.xy(1, 11, CC.DEFAULT, CC.TOP));

			//---- lblScore ----
			lblScore.setText("<dynamic>"); //$NON-NLS-1$
			lblScore.setHorizontalTextPosition(SwingConstants.LEADING);
			lblScore.setFont(lblScore.getFont().deriveFont(12f));
			pnlInfo.add(lblScore, CC.xy(3, 11));

			//---- label7 ----
			label7.setText(LocaleBundle.getString("PreviewSeriesFrame.lblLanguage.text")); //$NON-NLS-1$
			label7.setVerticalAlignment(SwingConstants.TOP);
			label7.setVerticalTextPosition(SwingConstants.TOP);
			label7.setFont(label7.getFont().deriveFont(12f));
			pnlInfo.add(label7, CC.xy(1, 13, CC.DEFAULT, CC.TOP));
			pnlInfo.add(ctrlLang, CC.xy(3, 13, CC.FILL, CC.FILL));

			//---- label5 ----
			label5.setText(LocaleBundle.getString("PreviewSeriesFrame.lblFSK.text")); //$NON-NLS-1$
			label5.setVerticalAlignment(SwingConstants.TOP);
			label5.setVerticalTextPosition(SwingConstants.TOP);
			label5.setFont(label5.getFont().deriveFont(12f));
			pnlInfo.add(label5, CC.xy(1, 15, CC.DEFAULT, CC.TOP));

			//---- lblFSK ----
			lblFSK.setText("<dynamic>"); //$NON-NLS-1$
			lblFSK.setHorizontalTextPosition(SwingConstants.LEADING);
			lblFSK.setFont(lblFSK.getFont().deriveFont(12f));
			pnlInfo.add(lblFSK, CC.xy(3, 15));

			//---- label9 ----
			label9.setText(LocaleBundle.getString("PreviewSeriesFrame.lblTags.text")); //$NON-NLS-1$
			label9.setVerticalAlignment(SwingConstants.TOP);
			label9.setVerticalTextPosition(SwingConstants.TOP);
			label9.setFont(label9.getFont().deriveFont(12f));
			pnlInfo.add(label9, CC.xy(1, 17, CC.DEFAULT, CC.TOP));
			pnlInfo.add(ctrlTags, CC.xy(3, 17, CC.FILL, CC.FILL));

			//---- label6 ----
			label6.setText(LocaleBundle.getString("PreviewSeriesFrame.lblGroups")); //$NON-NLS-1$
			label6.setVerticalAlignment(SwingConstants.TOP);
			label6.setVerticalTextPosition(SwingConstants.TOP);
			label6.setFont(label6.getFont().deriveFont(12f));
			pnlInfo.add(label6, CC.xy(1, 19, CC.DEFAULT, CC.TOP));

			//---- lblGroups ----
			lblGroups.setLabelFont(lblGroups.getLabelFont().deriveFont(12f));
			pnlInfo.add(lblGroups, CC.xy(3, 19, CC.FILL, CC.FILL));

			//---- label8 ----
			label8.setText(LocaleBundle.getString("PreviewSeriesFrame.lblGenres.text")); //$NON-NLS-1$
			label8.setVerticalAlignment(SwingConstants.TOP);
			label8.setVerticalTextPosition(SwingConstants.TOP);
			label8.setFont(label8.getFont().deriveFont(12f));
			pnlInfo.add(label8, CC.xy(1, 21, CC.DEFAULT, CC.TOP));

			//---- lblGenres ----
			lblGenres.setLabelFont(lblGenres.getLabelFont().deriveFont(12f));
			pnlInfo.add(lblGenres, CC.xy(3, 21, CC.FILL, CC.FILL));
			pnlInfo.add(btnOnline, CC.xywh(1, 25, 3, 1, CC.RIGHT, CC.DEFAULT));
		}
		contentPane.add(pnlInfo, CC.xy(2, 4, CC.FILL, CC.FILL));

		//======== pnlMain ========
		{
			pnlMain.setBorder(LineBorder.createGrayLineBorder());
			pnlMain.setLayout(new FormLayout(
				"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
				"$lgap, default, $lgap, default:grow, $lgap")); //$NON-NLS-1$

			//---- lblSeason ----
			lblSeason.setText("<Season>"); //$NON-NLS-1$
			lblSeason.setFont(lblSeason.getFont().deriveFont(25f));
			lblSeason.setHorizontalAlignment(SwingConstants.CENTER);
			pnlMain.add(lblSeason, CC.xy(2, 2));
			pnlMain.add(tabSeason, CC.xy(2, 4, CC.FILL, CC.FILL));
		}
		contentPane.add(pnlMain, CC.xy(4, 4, CC.FILL, CC.FILL));
		setSize(1000, 750);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlTop;
	private JPanel pnlTopLeft;
	private JButton btnPlayNext;
	private JVLCRobotButton btnVLCRobot;
	private JLabel lblTitle;
	private JPanel pnlSearch;
	private JTextField edSearch;
	private JSearchButton btnSearch;
	private JCoverChooser cvrChooser;
	private JPanel pnlInfo;
	private DatabaseElementPreviewLabel lblCover;
	private OnlineScoreDisplay lblOnlineScore;
	private JLabel label1;
	private JLabel lblLength;
	private JLabel label2;
	private JLabel lblSize;
	private JLabel label3;
	private JLabel lblViewed;
	private JLabel label4;
	private JLabel lblScore;
	private JLabel label7;
	private LanguageDisplay ctrlLang;
	private JLabel label5;
	private JLabel lblFSK;
	private JLabel label9;
	private TagDisplay ctrlTags;
	private JLabel label6;
	private MultiLineTextLabel lblGroups;
	private JLabel label8;
	private MultiLineTextLabel lblGenres;
	private OnlineRefButton btnOnline;
	private JPanel pnlMain;
	private JLabel lblSeason;
	private SerTable tabSeason;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
