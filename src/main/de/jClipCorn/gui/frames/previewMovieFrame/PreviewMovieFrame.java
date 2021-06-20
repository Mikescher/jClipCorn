package de.jClipCorn.gui.frames.previewMovieFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.features.actionTree.menus.impl.PreviewMovieMenuBar;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.OnlineRefButton;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.TagDisplay;
import de.jClipCorn.gui.guiComponents.language.LanguageDisplay;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class PreviewMovieFrame extends JFrame implements UpdateCallbackListener {
	private static final long serialVersionUID = 7483476533745432416L;

	private static List<Tuple<CCMovie, PreviewMovieFrame>> _activeFrames = new ArrayList<>();

	private final CCMovie movie;
	
	private CoverLabel lblCover;
	private JLabel lblTitle;
	private JList<String> lsGenres;
	private ReadableTextField edPart0;
	private ReadableTextField edPart1;
	private ReadableTextField edPart2;
	private ReadableTextField edPart3;
	private ReadableTextField edPart4;
	private ReadableTextField edPart5;
	private JLabel lblQuality;
	private JLabel lblLanguage;
	private JLabel lblLength;
	private JLabel lblAdded;
	private JLabel lblScore;
	private JLabel lblFsk;
	private JLabel lblFormat;
	private JLabel lblYear;
	private JLabel lblSize;
	private OnlineRefButton btnOnlineRef;
	private JLabel lblGenre;
	private JLabel lblScore_1;
	private JLabel lbl_Quality;
	private LanguageDisplay lbl_Language;
	private JLabel lbl_Length;
	private JLabel lbl_Added;
	private JLabel lbl_FSK;
	private JLabel lbl_Format;
	private JLabel lbl_Year;
	private JLabel lbl_Size;
	private JLabel lbl_OnlineScore;
	private JLabel lbl_Score;
	private JButton btnPlay;
	private JLabel lblTags;
	private JList<String> lsHistory;
	private JLabel lblViewedHistory;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JLabel lblGruppen;
	private JScrollPane scrollPane_2;
	private JList<String> lsGroups;
	private JTabbedPane tabbedPane;
	private JPanel pnlTabMain;
	private JPanel pnlTabMediaInfo;
	private JPanel pnlTabPaths;
	private TagDisplay pnlTags;
	private JLabel lblTitel;
	private JLabel lblTitle2;
	private JLabel lblZyklus;
	private JLabel lblZyklus2;
	private JPanel panel_1;
	private JLabel lblViewed;
	private JPanel panel;
	private JPanel pnlMI_General;
	private JPanel pnlMI_Video;
	private JPanel pnlMI_Audio;
	private JLabel lblCdate;
	private JLabel lblMdate;
	private JLabel lblFilesize;
	private JLabel lblDuration;
	private JLabel lblBitrate;
	private JLabel lblFormat_1;
	private JLabel lblWidthHeight;
	private ReadableTextField edMI_CDate;
	private ReadableTextField edMI_MDate;
	private ReadableTextField edMI_Filesize;
	private ReadableTextField edMI_Duration;
	private ReadableTextField edMI_Bitrate;
	private JLabel lblFramerate;
	private JLabel lblBitdepth;
	private JLabel lblFramecount;
	private JLabel lblCodec;
	private JLabel lblFormat_2;
	private JLabel lblChannels;
	private JLabel lblCodec_1;
	private JLabel lblSamplerate;
	private ReadableTextField edMI_VideoFormat;
	private ReadableTextField edMI_VideoResolution;
	private ReadableTextField edMI_VideoFramerate;
	private ReadableTextField edMI_VideoBitdepth;
	private ReadableTextField edMI_VideoFramecount;
	private ReadableTextField edMI_VideoCodec;
	private ReadableTextField edMI_AudioFormat;
	private ReadableTextField edMI_AudioChannels;
	private ReadableTextField edMI_AudioCodec;
	private ReadableTextField edMI_AudioSamplerate;
	private JButton btnMediaInfo1;
	private JButton btnMediaInfo2;
	private JButton btnMediaInfo3;
	private JButton btnMediaInfo4;
	private JButton btnMediaInfo5;
	private JButton btnMediaInfo6;
	private JButton btnOpenDir6;
	private JButton btnOpenDir5;
	private JButton btnOpenDir4;
	private JButton btnOpenDir3;
	private JButton btnOpenDir2;
	private JButton btnOpenDir1;
	private JPanel pnlTabHistory;
	private PMHistoryTableEntries tabHistoryEntries;
	private PMHistoryTableChanges tabHistoryChanges;
	private JButton btnQueryHistory;

	/**
	 * @wbp.parser.constructor
	 */
	private PreviewMovieFrame() {
		super();
		this.movie = null;
		
		initGUI();
	}
	
	private PreviewMovieFrame(Component owner, CCMovie m) {
		super();
		this.movie = m;
		
		initGUI();
		setJMenuBar(new PreviewMovieMenuBar(this, m, this::updateFields));
		
		updateFields();
		
		setLocationRelativeTo(owner);
		initListener(m);
	}

	public static void show(Component owner, CCMovie data, boolean forceNoSingleton) {
		if (forceNoSingleton || !CCProperties.getInstance().PROP_PREVIEWMOVIE_SINGLETON.getValue()) {
			new PreviewMovieFrame(owner, data).setVisible(true);
			return;
		}

		Tuple<CCMovie, PreviewMovieFrame> frame = CCStreams.iterate(_activeFrames).firstOrNull(f -> f.Item1 == data);
		if (frame != null) {
			if(frame.Item2.getState() != Frame.NORMAL) frame.Item2.setState(Frame.NORMAL);
			frame.Item2.toFront();
			frame.Item2.repaint();
			return;
		}

		new PreviewMovieFrame(owner, data).setVisible(true);
	}

	private void initListener(CCMovie mov) {
		final Tuple<CCMovie, PreviewMovieFrame> d = Tuple.Create(mov, this);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				_activeFrames.remove(d);
			}
		});
		_activeFrames.add(d);
	}
	
	private void initGUI() {
		setMinimumSize(new Dimension(675, 550));
		setSize(new Dimension(750, 615));
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("148px:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		panel_1 = new JPanel();
		getContentPane().add(panel_1, "2, 1, 3, 1, fill, fill"); //$NON-NLS-1$
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("30dlu"), //$NON-NLS-1$
				ColumnSpec.decode("1px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("231px:grow"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("30dlu"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lblViewed = new JLabel(""); //$NON-NLS-1$
		panel_1.add(lblViewed, "1, 2, center, center"); //$NON-NLS-1$
		
		lblTitle = new JLabel();
		panel_1.add(lblTitle, "4, 2, fill, center"); //$NON-NLS-1$
		lblTitle.setText("<<DYNAMIC>>"); //$NON-NLS-1$
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 28)); //$NON-NLS-1$
		
		btnPlay = new JButton(Resources.ICN_MENUBAR_PLAY.get32x32());
		panel_1.add(btnPlay, "6, 2, center, center"); //$NON-NLS-1$
		btnPlay.addActionListener(e -> playMovie());
		
		lblCover = new CoverLabel(false);
		lblCover.setPosition(10, 53);
		getContentPane().add(lblCover, "2, 3, left, top"); //$NON-NLS-1$
		
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		getContentPane().add(tabbedPane, "4, 3, fill, fill"); //$NON-NLS-1$
		
		pnlTabMain = new JPanel();
		tabbedPane.addTab(LocaleBundle.getString("PreviewMovieFrame.TabGeneral"), null, pnlTabMain, null); //$NON-NLS-1$
		pnlTabMain.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("80dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("12dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lblTitel = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		pnlTabMain.add(lblTitel, "2, 2, fill, fill"); //$NON-NLS-1$
		
		lblTitle2 = new JLabel(""); //$NON-NLS-1$
		pnlTabMain.add(lblTitle2, "4, 2, fill, fill"); //$NON-NLS-1$
		
		btnOnlineRef = new OnlineRefButton();
		pnlTabMain.add(btnOnlineRef, "6, 2, 1, 3, fill, top"); //$NON-NLS-1$
		
		lblZyklus = new JLabel(LocaleBundle.getString("AddMovieFrame.lblZyklus.text")); //$NON-NLS-1$
		pnlTabMain.add(lblZyklus, "2, 4, fill, fill"); //$NON-NLS-1$
		
		lblZyklus2 = new JLabel(""); //$NON-NLS-1$
		pnlTabMain.add(lblZyklus2, "4, 4, fill, fill"); //$NON-NLS-1$
		
		lblQuality = new JLabel(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
		pnlTabMain.add(lblQuality, "2, 6, fill, fill"); //$NON-NLS-1$
		
		lbl_Quality = new JLabel();
		pnlTabMain.add(lbl_Quality, "4, 6, 3, 1, fill, fill"); //$NON-NLS-1$
		
		lblScore = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		pnlTabMain.add(lblScore, "2, 8, fill, fill"); //$NON-NLS-1$
		
		lbl_OnlineScore = new JLabel();
		pnlTabMain.add(lbl_OnlineScore, "4, 8, 3, 1, fill, fill"); //$NON-NLS-1$
		
		lblLanguage = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		pnlTabMain.add(lblLanguage, "2, 10, fill, fill"); //$NON-NLS-1$
		
		lbl_Language = new LanguageDisplay();
		pnlTabMain.add(lbl_Language, "4, 10, 3, 1, fill, fill"); //$NON-NLS-1$
		
		lblLength = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		pnlTabMain.add(lblLength, "2, 12, fill, fill"); //$NON-NLS-1$
		
		lbl_Length = new JLabel();
		pnlTabMain.add(lbl_Length, "4, 12, 3, 1, fill, fill"); //$NON-NLS-1$
		
		lblAdded = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		pnlTabMain.add(lblAdded, "2, 14, fill, fill"); //$NON-NLS-1$
		
		lbl_Added = new JLabel();
		pnlTabMain.add(lbl_Added, "4, 14, 3, 1, fill, fill"); //$NON-NLS-1$
		
		lblFsk = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		pnlTabMain.add(lblFsk, "2, 16, fill, fill"); //$NON-NLS-1$
		
		lbl_FSK = new JLabel();
		pnlTabMain.add(lbl_FSK, "4, 16, 3, 1, fill, fill"); //$NON-NLS-1$
		
		lblFormat = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		pnlTabMain.add(lblFormat, "2, 18, fill, fill"); //$NON-NLS-1$
		
		lbl_Format = new JLabel();
		pnlTabMain.add(lbl_Format, "4, 18, 3, 1, fill, fill"); //$NON-NLS-1$

		lblYear = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		pnlTabMain.add(lblYear, "2, 20, fill, fill"); //$NON-NLS-1$

		lbl_Year = new JLabel();
		pnlTabMain.add(lbl_Year, "4, 20, 3, 1, fill, fill"); //$NON-NLS-1$

		lblSize = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		pnlTabMain.add(lblSize, "2, 22, fill, fill"); //$NON-NLS-1$

		lbl_Size = new JLabel();
		pnlTabMain.add(lbl_Size, "4, 22, 3, 1, fill, fill"); //$NON-NLS-1$

		lblScore_1 = new JLabel(LocaleBundle.getString("PreviewMovieFrame.btnScore.text")); //$NON-NLS-1$
		pnlTabMain.add(lblScore_1, "2, 24, fill, fill"); //$NON-NLS-1$

		lbl_Score = new JLabel();
		pnlTabMain.add(lbl_Score, "4, 24, 3, 1, fill, fill"); //$NON-NLS-1$

		lblTags = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
		pnlTabMain.add(lblTags, "2, 26, fill, fill"); //$NON-NLS-1$

		pnlTags = new TagDisplay();
		pnlTabMain.add(pnlTags, "4, 26, 3, 1, fill, fill"); //$NON-NLS-1$

		panel = new JPanel();
		pnlTabMain.add(panel, "2, 28, 5, 1, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("50dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("50dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("50dlu:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$

		lblGruppen = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
		panel.add(lblGruppen, "1, 1"); //$NON-NLS-1$

		lblGenre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		panel.add(lblGenre, "3, 1"); //$NON-NLS-1$

		lblViewedHistory = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
		panel.add(lblViewedHistory, "5, 1"); //$NON-NLS-1$

		scrollPane_2 = new JScrollPane();
		panel.add(scrollPane_2, "1, 3, fill, fill"); //$NON-NLS-1$

		lsGroups = new JList<>();
		scrollPane_2.setViewportView(lsGroups);

		scrollPane = new JScrollPane();
		panel.add(scrollPane, "3, 3, fill, fill"); //$NON-NLS-1$
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		lsGenres = new JList<>();
		scrollPane.setViewportView(lsGenres);

		scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1, "5, 3, fill, fill"); //$NON-NLS-1$
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		lsHistory = new JList<>();
		scrollPane_1.setViewportView(lsHistory);

		pnlTabMediaInfo = new JPanel();
		tabbedPane.addTab(LocaleBundle.getString("PreviewMovieFrame.TabMediaInfo"), null, pnlTabMediaInfo, null); //$NON-NLS-1$
		pnlTabMediaInfo.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,}));

		pnlMI_General = new JPanel();
		pnlMI_General.setBorder(new TitledBorder(null, LocaleBundle.getString("EditMediaInfoDialog.header1"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		pnlTabMediaInfo.add(pnlMI_General, "2, 2, 3, 1, fill, fill"); //$NON-NLS-1$
		pnlMI_General.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
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
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		lblCdate = new JLabel("CDate"); //$NON-NLS-1$
		pnlMI_General.add(lblCdate, "2, 2, right, default"); //$NON-NLS-1$

		edMI_CDate = new ReadableTextField();
		pnlMI_General.add(edMI_CDate, "4, 2, fill, default"); //$NON-NLS-1$
		edMI_CDate.setColumns(10);

		lblMdate = new JLabel("MDate"); //$NON-NLS-1$
		pnlMI_General.add(lblMdate, "2, 4, right, default"); //$NON-NLS-1$

		edMI_MDate = new ReadableTextField();
		pnlMI_General.add(edMI_MDate, "4, 4, fill, default"); //$NON-NLS-1$
		edMI_MDate.setColumns(10);

		lblFilesize = new JLabel("Filesize"); //$NON-NLS-1$
		pnlMI_General.add(lblFilesize, "2, 6, right, default"); //$NON-NLS-1$

		edMI_Filesize = new ReadableTextField();
		pnlMI_General.add(edMI_Filesize, "4, 6, fill, default"); //$NON-NLS-1$
		edMI_Filesize.setColumns(10);

		lblDuration = new JLabel("Duration"); //$NON-NLS-1$
		pnlMI_General.add(lblDuration, "2, 8, right, default"); //$NON-NLS-1$

		edMI_Duration = new ReadableTextField();
		pnlMI_General.add(edMI_Duration, "4, 8, fill, default"); //$NON-NLS-1$
		edMI_Duration.setColumns(10);

		lblBitrate = new JLabel("Bitrate"); //$NON-NLS-1$
		pnlMI_General.add(lblBitrate, "2, 10, right, default"); //$NON-NLS-1$

		edMI_Bitrate = new ReadableTextField();
		pnlMI_General.add(edMI_Bitrate, "4, 10, fill, default"); //$NON-NLS-1$
		edMI_Bitrate.setColumns(10);

		pnlMI_Video = new JPanel();
		pnlMI_Video.setBorder(new TitledBorder(null, LocaleBundle.getString("EditMediaInfoDialog.header2"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		pnlTabMediaInfo.add(pnlMI_Video, "2, 4, fill, fill"); //$NON-NLS-1$
		pnlMI_Video.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		lblFormat_1 = new JLabel("Format"); //$NON-NLS-1$
		pnlMI_Video.add(lblFormat_1, "2, 2, right, default"); //$NON-NLS-1$

		edMI_VideoFormat = new ReadableTextField();
		pnlMI_Video.add(edMI_VideoFormat, "4, 2, fill, default"); //$NON-NLS-1$
		edMI_VideoFormat.setColumns(10);

		lblWidthHeight = new JLabel("Resolution"); //$NON-NLS-1$
		pnlMI_Video.add(lblWidthHeight, "2, 4, right, default"); //$NON-NLS-1$

		edMI_VideoResolution = new ReadableTextField();
		pnlMI_Video.add(edMI_VideoResolution, "4, 4, fill, default"); //$NON-NLS-1$
		edMI_VideoResolution.setColumns(10);

		lblFramerate = new JLabel("Framerate"); //$NON-NLS-1$
		pnlMI_Video.add(lblFramerate, "2, 6, right, default"); //$NON-NLS-1$

		edMI_VideoFramerate = new ReadableTextField();
		pnlMI_Video.add(edMI_VideoFramerate, "4, 6, fill, default"); //$NON-NLS-1$
		edMI_VideoFramerate.setColumns(10);

		lblBitdepth = new JLabel("Bitdepth"); //$NON-NLS-1$
		pnlMI_Video.add(lblBitdepth, "2, 8, right, default"); //$NON-NLS-1$

		edMI_VideoBitdepth = new ReadableTextField();
		pnlMI_Video.add(edMI_VideoBitdepth, "4, 8, fill, default"); //$NON-NLS-1$
		edMI_VideoBitdepth.setColumns(10);

		lblFramecount = new JLabel("Framecount"); //$NON-NLS-1$
		pnlMI_Video.add(lblFramecount, "2, 10, right, default"); //$NON-NLS-1$

		edMI_VideoFramecount = new ReadableTextField();
		pnlMI_Video.add(edMI_VideoFramecount, "4, 10, fill, default"); //$NON-NLS-1$
		edMI_VideoFramecount.setColumns(10);

		lblCodec = new JLabel("Codec"); //$NON-NLS-1$
		pnlMI_Video.add(lblCodec, "2, 12, right, default"); //$NON-NLS-1$

		edMI_VideoCodec = new ReadableTextField();
		pnlMI_Video.add(edMI_VideoCodec, "4, 12, fill, default"); //$NON-NLS-1$
		edMI_VideoCodec.setColumns(10);

		pnlMI_Audio = new JPanel();
		pnlMI_Audio.setBorder(new TitledBorder(null, LocaleBundle.getString("EditMediaInfoDialog.header3"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		pnlTabMediaInfo.add(pnlMI_Audio, "4, 4, fill, fill"); //$NON-NLS-1$
		pnlMI_Audio.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		lblFormat_2 = new JLabel("Format"); //$NON-NLS-1$
		pnlMI_Audio.add(lblFormat_2, "2, 2, right, default"); //$NON-NLS-1$

		edMI_AudioFormat = new ReadableTextField();
		pnlMI_Audio.add(edMI_AudioFormat, "4, 2, fill, default"); //$NON-NLS-1$
		edMI_AudioFormat.setColumns(10);

		lblChannels = new JLabel("Channels"); //$NON-NLS-1$
		pnlMI_Audio.add(lblChannels, "2, 4, right, default"); //$NON-NLS-1$

		edMI_AudioChannels = new ReadableTextField();
		pnlMI_Audio.add(edMI_AudioChannels, "4, 4, fill, default"); //$NON-NLS-1$
		edMI_AudioChannels.setColumns(10);

		lblCodec_1 = new JLabel("Codec"); //$NON-NLS-1$
		pnlMI_Audio.add(lblCodec_1, "2, 6, right, default"); //$NON-NLS-1$

		edMI_AudioCodec = new ReadableTextField();
		pnlMI_Audio.add(edMI_AudioCodec, "4, 6, fill, default"); //$NON-NLS-1$
		edMI_AudioCodec.setColumns(10);

		lblSamplerate = new JLabel("Samplerate"); //$NON-NLS-1$
		pnlMI_Audio.add(lblSamplerate, "2, 8, right, default"); //$NON-NLS-1$

		edMI_AudioSamplerate = new ReadableTextField();
		pnlMI_Audio.add(edMI_AudioSamplerate, "4, 8, fill, default"); //$NON-NLS-1$
		edMI_AudioSamplerate.setColumns(10);

		pnlTabPaths = new JPanel();
		tabbedPane.addTab(LocaleBundle.getString("PreviewMovieFrame.TabPaths"), null, pnlTabPaths, null); //$NON-NLS-1$
		pnlTabPaths.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
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
				FormSpecs.DEFAULT_ROWSPEC,}));

		edPart0 = new ReadableTextField();
		pnlTabPaths.add(edPart0, "2, 2, fill, center"); //$NON-NLS-1$
		edPart0.setColumns(10);

		btnMediaInfo1 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		btnMediaInfo1.addActionListener((e) -> showMediaInfo(0));
		pnlTabPaths.add(btnMediaInfo1, "4, 2, fill, fill"); //$NON-NLS-1$

		btnOpenDir1 = new JButton(Resources.ICN_MENUBAR_FOLDER.get16x16());
		btnOpenDir1.addActionListener((e) -> openDir(0));
		pnlTabPaths.add(btnOpenDir1, "6, 2, fill, fill"); //$NON-NLS-1$

		edPart1 = new ReadableTextField();
		pnlTabPaths.add(edPart1, "2, 4, fill, center"); //$NON-NLS-1$
		edPart1.setColumns(10);

		btnMediaInfo2 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		btnMediaInfo2.addActionListener((e) -> showMediaInfo(1));
		pnlTabPaths.add(btnMediaInfo2, "4, 4, fill, fill"); //$NON-NLS-1$

		btnOpenDir2 = new JButton(Resources.ICN_MENUBAR_FOLDER.get16x16());
		btnOpenDir2.addActionListener((e) -> openDir(1));
		pnlTabPaths.add(btnOpenDir2, "6, 4, fill, fill"); //$NON-NLS-1$

		edPart2 = new ReadableTextField();
		pnlTabPaths.add(edPart2, "2, 6, fill, center"); //$NON-NLS-1$
		edPart2.setColumns(10);

		btnMediaInfo3 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		btnMediaInfo3.addActionListener((e) -> showMediaInfo(2));
		pnlTabPaths.add(btnMediaInfo3, "4, 6, fill, fill"); //$NON-NLS-1$

		btnOpenDir3 = new JButton(Resources.ICN_MENUBAR_FOLDER.get16x16());
		btnOpenDir3.addActionListener((e) -> openDir(2));
		pnlTabPaths.add(btnOpenDir3, "6, 6, fill, fill"); //$NON-NLS-1$

		edPart3 = new ReadableTextField();
		pnlTabPaths.add(edPart3, "2, 8, fill, center"); //$NON-NLS-1$
		edPart3.setColumns(10);

		btnMediaInfo4 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		btnMediaInfo4.addActionListener((e) -> showMediaInfo(3));
		pnlTabPaths.add(btnMediaInfo4, "4, 8, fill, fill"); //$NON-NLS-1$

		btnOpenDir4 = new JButton(Resources.ICN_MENUBAR_FOLDER.get16x16());
		btnOpenDir4.addActionListener((e) -> openDir(3));
		pnlTabPaths.add(btnOpenDir4, "6, 8, fill, fill"); //$NON-NLS-1$

		edPart4 = new ReadableTextField();
		pnlTabPaths.add(edPart4, "2, 10, fill, center"); //$NON-NLS-1$
		edPart4.setColumns(10);

		btnMediaInfo5 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		btnMediaInfo5.addActionListener((e) -> showMediaInfo(4));
		pnlTabPaths.add(btnMediaInfo5, "4, 10, fill, fill"); //$NON-NLS-1$

		btnOpenDir5 = new JButton(Resources.ICN_MENUBAR_FOLDER.get16x16());
		btnOpenDir5.addActionListener((e) -> openDir(4));
		pnlTabPaths.add(btnOpenDir5, "6, 10, fill, fill"); //$NON-NLS-1$

		edPart5 = new ReadableTextField();
		pnlTabPaths.add(edPart5, "2, 12, fill, center"); //$NON-NLS-1$
		edPart5.setColumns(10);

		btnMediaInfo6 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		btnMediaInfo6.addActionListener((e) -> showMediaInfo(5));
		pnlTabPaths.add(btnMediaInfo6, "4, 12, fill, fill"); //$NON-NLS-1$

		btnOpenDir6 = new JButton(Resources.ICN_MENUBAR_FOLDER.get16x16());
		btnOpenDir6.addActionListener((e) -> openDir(5));
		pnlTabPaths.add(btnOpenDir6, "6, 12, fill, fill"); //$NON-NLS-1$
		
		pnlTabHistory = new JPanel();
		tabbedPane.addTab(LocaleBundle.getString("PreviewMovieFrame.TabHistory"), null, pnlTabHistory, null); //$NON-NLS-1$
		pnlTabHistory.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("1dlu:grow(1)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		tabHistoryEntries = new PMHistoryTableEntries(elem -> {
			if (elem == null) {
				tabHistoryChanges.clearData();
			} else {
				tabHistoryChanges.setData(CCStreams.iterate(elem.Changes).autosortByProperty(p -> p.Field.toLowerCase()).enumerate());
				tabHistoryChanges.autoResize();
			}
		});
		pnlTabHistory.add(tabHistoryEntries, "2, 2, fill, fill"); //$NON-NLS-1$
		tabHistoryEntries.autoResize();
		
		tabHistoryChanges = new PMHistoryTableChanges();
		pnlTabHistory.add(tabHistoryChanges, "2, 4, fill, fill"); //$NON-NLS-1$
		tabHistoryChanges.autoResize();
		
		btnQueryHistory = new JButton(LocaleBundle.getString("PreviewMovieFrame.btnQuery")); //$NON-NLS-1$
		btnQueryHistory.addActionListener(this::queryHistory);
		pnlTabHistory.add(btnQueryHistory, "2, 6, right, center"); //$NON-NLS-1$
	}

	private void queryHistory(ActionEvent evt) {
		try {
			List<CCCombinedHistoryEntry> data = movie.getMovieList().getHistory().query(movie.getMovieList(), false, false, false, true, null, null, Integer.toString(movie.getLocalID()));
			tabHistoryEntries.setData(data);
			tabHistoryChanges.clearData();
			tabHistoryEntries.autoResize();
		} catch (CCFormatException e) {
			CCLog.addError(e);
			DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
		}
	}

	private void playMovie() {
		movie.play(PreviewMovieFrame.this, true);
	}

	private void updateFields() {
		if (Main.DEBUG) {
			setTitle("<" + movie.getLocalID() + "> " + movie.getCompleteTitle() + " (" + movie.getCoverID() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			setTitle(movie.getCompleteTitle());
		}
		
		lblCover.setAndResizeCover(movie.getCover());
		lblTitle.setText(movie.getCompleteTitle());
		
		lblTitle2.setText(movie.getTitle());
		lblZyklus2.setText(movie.getZyklus().isEmpty() ? Str.Empty : movie.getZyklus().getFormatted());
		
		lblViewed.setIcon(movie.isViewed()?Resources.ICN_TABLE_VIEWED_TRUE.get():null);
		
		CCQualityCategory qcat = movie.getMediaInfoCategory();
		lbl_Quality.setIcon(qcat.getIcon());
		lbl_Quality.setText(qcat.getShortText());
		lbl_Quality.setToolTipText(qcat.getTooltip());
		
		lbl_Language.setValue(movie.getLanguage());
		
		lbl_Length.setText(TimeIntervallFormatter.formatPointed(movie.getLength()));
		
		lbl_Added.setText(movie.getAddDate().toStringUINormal());
		
		lbl_FSK.setIcon(movie.getFSK().getIcon());
		lbl_FSK.setText(movie.getFSK().asString());
		
		lbl_Format.setIcon(movie.getFormat().getIcon());
		lbl_Format.setText(movie.getFormat().asString());
		
		lbl_Year.setText(movie.getYear() + ""); //$NON-NLS-1$
		
		lbl_Score.setIcon(movie.Score.get().getIcon());
		lbl_Score.setToolTipText(movie.Score.get().asString());
		
		lbl_Size.setText(FileSizeFormatter.format(movie.getFilesize()));
		
		pnlTags.setValue(movie.getTags());
		
		lbl_OnlineScore.setIcon(movie.getOnlinescore().getIcon());
		
		DefaultListModel<String> dlsmGenre;
		lsGenres.setModel(dlsmGenre = new DefaultListModel<>());
		for (int i = 0; i < movie.Genres.get().getGenreCount(); i++) {
			dlsmGenre.addElement(movie.Genres.get().getGenre(i).asString());
		}

		DefaultListModel<String> dlsmGroups;
		lsGroups.setModel(dlsmGroups = new DefaultListModel<>());
		for (CCGroup group : movie.getGroups()) {
			dlsmGroups.addElement(group.Name);
		}
				
		edPart0.setText(movie.Parts.get(0));
		edPart1.setText(movie.Parts.get(1));
		edPart2.setText(movie.Parts.get(2));
		edPart3.setText(movie.Parts.get(3));
		edPart4.setText(movie.Parts.get(4));
		edPart5.setText(movie.Parts.get(5));
		
		DefaultListModel<String> dlsmViewed;
		lsHistory.setModel(dlsmViewed = new DefaultListModel<>());
		for (CCDateTime dt : movie.ViewedHistory.get().ccstream()) {
			dlsmViewed.addElement(dt.toStringUINormal());
		}
		
		btnOnlineRef.setValue(movie.getOnlineReference());
		
		CCMediaInfo mi = movie.mediaInfo().get();

		edMI_CDate.setText(mi.isUnset() ? Str.Empty : CCDateTime.createFromFileTimestamp(mi.getCDate(), TimeZone.getDefault()).toStringISO());
		edMI_MDate.setText(mi.isUnset() ? Str.Empty : CCDateTime.createFromFileTimestamp(mi.getMDate(), TimeZone.getDefault()).toStringISO());
		edMI_Filesize.setText(mi.isUnset() ? Str.Empty : FileSizeFormatter.formatPrecise(mi.getFilesize()));
		edMI_Duration.setText(mi.isUnset() ? Str.Empty : TimeIntervallFormatter.formatSeconds(mi.getDuration()));
		edMI_Bitrate.setText(mi.isUnset() ? Str.Empty : Str.spacegroupformat(mi.getNormalizedBitrate()) + " kbit/s"); //$NON-NLS-1$

		edMI_VideoFormat.setText(mi.isUnset() ? Str.Empty : mi.getVideoFormat());
		edMI_VideoResolution.setText(mi.isUnset() ? Str.Empty : Str.format("{0,number,#} x {1,number,#}", mi.getWidth(), mi.getHeight())); //$NON-NLS-1$
		edMI_VideoFramerate.setText(mi.isUnset() ? Str.Empty : String.valueOf((int)Math.round(mi.getFramerate())));
		edMI_VideoBitdepth.setText(mi.isUnset() ? Str.Empty : String.valueOf(mi.getBitdepth()));
		edMI_VideoFramecount.setText(mi.isUnset() ? Str.Empty : Str.spacegroupformat(mi.getFramecount()));
		edMI_VideoCodec.setText(mi.isUnset() ? Str.Empty : mi.getVideoCodec());

		edMI_AudioFormat.setText(mi.isUnset() ? Str.Empty : mi.getAudioFormat());
		edMI_AudioChannels.setText(mi.isUnset() ? Str.Empty : String.valueOf(mi.getAudioChannels()));
		edMI_AudioCodec.setText(mi.isUnset() ? Str.Empty : mi.getAudioCodec());
		edMI_AudioSamplerate.setText(mi.isUnset() ? Str.Empty : Str.spacegroupformat(mi.getAudioSamplerate()));

		btnMediaInfo1.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(0)));
		btnOpenDir1.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(0)));

		btnMediaInfo2.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(1)));
		btnOpenDir2.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(1)));

		btnMediaInfo3.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(2)));
		btnOpenDir3.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(2)));

		btnMediaInfo4.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(3)));
		btnOpenDir4.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(3)));

		btnMediaInfo5.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(4)));
		btnOpenDir5.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(4)));

		btnMediaInfo6.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(5)));
		btnOpenDir6.setEnabled(!Str.isNullOrWhitespace(movie.Parts.get(5)));

	}

	@Override
	public void onUpdate(Object o) {
		updateFields();
	}

	private void showMediaInfo(int index) {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			GenericTextDialog.showText(this, getTitle(), MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(movie.Parts.get(index))), false);
		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void openDir(int index) {
		PathFormatter.showInExplorer(PathFormatter.fromCCPath(movie.Parts.get(index)));
	}
}
