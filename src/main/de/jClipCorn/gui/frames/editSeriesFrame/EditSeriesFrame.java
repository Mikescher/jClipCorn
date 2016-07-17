package de.jClipCorn.gui.frames.editSeriesFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.addEpisodesFrame.AddEpisodesFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.guiComponents.HFixListCellRenderer;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.TagPanel;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Validator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.onlineparser.ParseResultHandler;
import de.jClipCorn.util.userdataProblem.UserDataProblem;
import de.jClipCorn.util.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;

public class EditSeriesFrame extends JFrame implements ParseResultHandler, WindowListener {
	private static final long serialVersionUID = -3694533463871522503L;
	
	private static CCDate MIN_DATE = CCDate.getMinimumDate();
	
	private final JFileChooser videoFileChooser;
	
	private final CCSeries series;
	
	private final UpdateCallbackListener listener;
	
	private JList<String> lsSeasons;
	private JLabel label_1;
	private JTextField edSeriesTitle;
	private JComboBox<String> cbxSeriesGenre_7;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel label_5;
	private JLabel label_6;
	private JLabel label_7;
	private JLabel label_8;
	private JLabel label_9;
	private JComboBox<String> cbxSeriesGenre_0;
	private JComboBox<String> cbxSeriesGenre_1;
	private JComboBox<String> cbxSeriesGenre_2;
	private JComboBox<String> cbxSeriesGenre_3;
	private JComboBox<String> cbxSeriesGenre_4;
	private JComboBox<String> cbxSeriesGenre_5;
	private JComboBox<String> cbxSeriesGenre_6;
	private JComboBox<String> cbxSeriesLanguage;
	private JLabel label_10;
	private JLabel label_11;
	private JSpinner spnSeriesOnlineScore;
	private JLabel label_12;
	private JLabel label_13;
	private JComboBox<String> cbxSeriesFSK;
	private JPanel pnlSeries;
	private JScrollPane scrollPane;
	private JComboBox<String> cbxSeriesScore;
	private JLabel lblScore;
	private JPanel pnlSeason;
	private JLabel label_15;
	private JTextField edSeasonTitle;
	private JLabel label_16;
	private JSpinner spnSeasonYear;
	private JList<String> lsEpisodes;
	private JScrollPane scrollPane_1;
	private JPanel pnlEpisode;
	private JLabel label_17;
	private JTextField edEpisodeTitle;
	private JLabel label_18;
	private JSpinner spnEpisodeEpisode;
	private JLabel label_19;
	private JCheckBox cbEpisodeViewed;
	private JLabel label_20;
	private JComboBox<String> cbxEpisodeFormat;
	private JLabel label_21;
	private JComboBox<String> cbxEpisodeQuality;
	private JLabel label_22;
	private JSpinner spnEpisodeLength;
	private JLabel label_23;
	private JLabel label_24;
	private JLabel lblEpisodeFilesize;
	private JSpinner spnEpisodeSize;
	private JLabel label_26;
	private JButton button_2;
	private JButton btnEpisodeToday;
	private JCCDateSpinner spnEpisodeAdded;
	private JLabel label_28;
	private JLabel label_29;
	private ReadableTextField edEpisodePart;
	private JButton btnEpisodeOpenPart;
	private JLabel lblTags;
	private TagPanel tagPnl;
	private JLabel lblSeriesSeriesID;
	private JLabel lblSeasonID;
	private JButton btnAddSeason;
	private JButton btnAddEpisode;
	private JButton btnRemoveEpisode;
	private JButton btnRemoveSeason;
	private JButton btnAddMultipleEpisodes;
	private JButton btnAddEmptySeason;
	private JButton btnSeriesOk;
	private JButton btnSeasonOK;
	private JButton btnEpisodeOK;
	private JButton btnEpisodeCalcQuality;
	private EditCoverControl edSeriesCvrControl;
	private EditCoverControl edSeasonCvrControl;
	private JLabel lblHistory;
	private DateTimeListEditor cmpEpisodeViewedHistory;
	private JReferenceChooser edSeriesReference;
	private JLabel label;

	/**
	 * @wbp.parser.constructor
	 */
	public EditSeriesFrame(Component owner, CCSeries ser, UpdateCallbackListener ucl) {
		super();
		this.series = ser;
		this.listener = ucl;
		this.videoFileChooser = new JFileChooser(PathFormatter.fromCCPath(ser.getCommonPathStart(true)));
		
		initGUI();
		setDefaultValues();
		initFileChooser();
		
		if (series != null) { //Sonst mag mich der WindowBuilder nicht mehr  :'(
			updateSeriesPanel();
		}
		
		setLocationRelativeTo(owner);
		
		addWindowListener(this);
		updateFocusTraversalPolicy();
	}
	
	public EditSeriesFrame(Component owner, CCSeason sea, UpdateCallbackListener ucl) {
		super();
		this.series = sea.getSeries();
		this.listener = ucl;
		this.videoFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		
		initGUI();
		setDefaultValues();
		initFileChooser();
		
		if (series != null) { //Sonst mag mich der WindowBuilder nicht mehr  :'(
			updateSeriesPanel();
		}
		
		setLocationRelativeTo(owner);
		
		addWindowListener(this);
		updateFocusTraversalPolicy();
		
		selectSeason(sea);
	}
	
	public EditSeriesFrame(Component owner, CCEpisode ep, UpdateCallbackListener ucl) {
		super();
		this.series = ep.getSeries();
		this.listener = ucl;
		this.videoFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		
		initGUI();
		setDefaultValues();
		initFileChooser();
		
		if (series != null) { //Sonst mag mich der WindowBuilder nicht mehr  :'(
			updateSeriesPanel();
		}
		
		setLocationRelativeTo(owner);
		
		addWindowListener(this);
		updateFocusTraversalPolicy();
		
		selectSeason(ep.getSeason());
		selectEpisode(ep);
	}
	
	private void updateFocusTraversalPolicy() {
		// all ok
	}
	
	private void selectEpisode(CCEpisode e) {
		lsEpisodes.setSelectedIndex(e.getEpisodeNumber()); // Calls the Listener
	}

	private void selectSeason(CCSeason s) {
		lsSeasons.setSelectedIndex(s.getSeasonNumber()); // Calls the Listener
	}

	private void initGUI() {
		setSize(new Dimension(1200, 700));
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setResizable(false);
		setTitle(LocaleBundle.getString("EditSeriesFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		pnlSeries = new JPanel();
		pnlSeries.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlSeries.setBounds(12, 13, 419, 646);
		getContentPane().add(pnlSeries);
		pnlSeries.setLayout(null);
		
		edSeriesTitle = new JTextField();
		edSeriesTitle.setBounds(74, 314, 120, 20);
		pnlSeries.add(edSeriesTitle);
		edSeriesTitle.setColumns(10);
		
		cbxSeriesGenre_7 = new JComboBox<>();
		cbxSeriesGenre_7.setBounds(263, 202, 141, 22);
		pnlSeries.add(cbxSeriesGenre_7);
		
		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		label_2.setBounds(206, 204, 52, 16);
		pnlSeries.add(label_2);
		
		label_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		label_3.setBounds(206, 177, 52, 16);
		pnlSeries.add(label_3);
		
		label_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		label_4.setBounds(206, 150, 52, 16);
		pnlSeries.add(label_4);
		
		label_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		label_5.setBounds(206, 123, 52, 16);
		pnlSeries.add(label_5);
		
		label_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		label_6.setBounds(206, 96, 52, 16);
		pnlSeries.add(label_6);
		
		label_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		label_7.setBounds(206, 69, 52, 16);
		pnlSeries.add(label_7);
		
		label_8 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		label_8.setBounds(206, 42, 52, 16);
		pnlSeries.add(label_8);
		
		label_9 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text"));   //$NON-NLS-1$
		label_9.setBounds(206, 15, 76, 16);
		pnlSeries.add(label_9);
		
		cbxSeriesGenre_0 = new JComboBox<>();
		cbxSeriesGenre_0.setBounds(263, 13, 141, 22);
		pnlSeries.add(cbxSeriesGenre_0);
		
		cbxSeriesGenre_1 = new JComboBox<>();
		cbxSeriesGenre_1.setBounds(263, 40, 141, 22);
		pnlSeries.add(cbxSeriesGenre_1);
		
		cbxSeriesGenre_2 = new JComboBox<>();
		cbxSeriesGenre_2.setBounds(263, 67, 141, 22);
		pnlSeries.add(cbxSeriesGenre_2);
		
		cbxSeriesGenre_3 = new JComboBox<>();
		cbxSeriesGenre_3.setBounds(263, 94, 141, 22);
		pnlSeries.add(cbxSeriesGenre_3);
		
		cbxSeriesGenre_4 = new JComboBox<>();
		cbxSeriesGenre_4.setBounds(263, 121, 141, 22);
		pnlSeries.add(cbxSeriesGenre_4);
		
		cbxSeriesGenre_5 = new JComboBox<>();
		cbxSeriesGenre_5.setBounds(263, 148, 141, 22);
		pnlSeries.add(cbxSeriesGenre_5);
		
		cbxSeriesGenre_6 = new JComboBox<>();
		cbxSeriesGenre_6.setBounds(263, 175, 141, 22);
		pnlSeries.add(cbxSeriesGenre_6);
		
		cbxSeriesLanguage = new JComboBox<>();
		cbxSeriesLanguage.setBounds(76, 345, 118, 22);
		pnlSeries.add(cbxSeriesLanguage);
		
		label_11 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		label_11.setBounds(12, 381, 71, 16);
		pnlSeries.add(label_11);
		
		spnSeriesOnlineScore = new JSpinner();
		spnSeriesOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnSeriesOnlineScore.setBounds(95, 380, 98, 20);
		pnlSeries.add(spnSeriesOnlineScore);
		
		label_12 = new JLabel("/ 10"); //$NON-NLS-1$
		label_12.setBounds(206, 381, 52, 16);
		pnlSeries.add(label_12);
		
		label_13 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		label_13.setBounds(12, 413, 52, 16);
		pnlSeries.add(label_13);
		
		cbxSeriesFSK = new JComboBox<>();
		cbxSeriesFSK.setBounds(76, 410, 118, 22);
		pnlSeries.add(cbxSeriesFSK);
		
		label_10 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		label_10.setBounds(12, 348, 182, 16);
		pnlSeries.add(label_10);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 475, 182, 126);
		pnlSeries.add(scrollPane);
		
		lsSeasons = new JList<>();
		lsSeasons.setCellRenderer(new HFixListCellRenderer());
		lsSeasons.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				updateSeasonPanel();
			}
		});
		scrollPane.setViewportView(lsSeasons);

		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label_1.setBounds(12, 316, 52, 16);
		pnlSeries.add(label_1);
		
		cbxSeriesScore = new JComboBox<>();
		cbxSeriesScore.setBounds(74, 442, 120, 20);
		pnlSeries.add(cbxSeriesScore);
		
		lblScore = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
		lblScore.setBounds(12, 446, 46, 14);
		pnlSeries.add(lblScore);
		
		lblSeriesSeriesID = new JLabel();
		lblSeriesSeriesID.setBounds(206, 317, 76, 14);
		pnlSeries.add(lblSeriesSeriesID);
		
		btnAddSeason = new JButton(LocaleBundle.getString("EditSeriesFrame.btnAddSeason.text")); //$NON-NLS-1$
		btnAddSeason.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addSeason();
			}
		});
		btnAddSeason.setBounds(206, 507, 167, 23);
		pnlSeries.add(btnAddSeason);
		
		btnRemoveSeason = new JButton(LocaleBundle.getString("EditSeriesFrame.btnRemoveSeason.text")); //$NON-NLS-1$
		btnRemoveSeason.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeSeason();
			}
		});
		btnRemoveSeason.setBounds(206, 541, 167, 23);
		pnlSeries.add(btnRemoveSeason);
		
		btnAddEmptySeason = new JButton(LocaleBundle.getString("EditSeriesFrame.btnAddEmptySeason.text")); //$NON-NLS-1$
		btnAddEmptySeason.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addEmptySeason();
			}
		});
		btnAddEmptySeason.setBounds(206, 473, 167, 23);
		pnlSeries.add(btnAddEmptySeason);
		
		btnSeriesOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnSeriesOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOKSeries(true);
			}
		});
		btnSeriesOk.setBounds(162, 612, 89, 23);
		pnlSeries.add(btnSeriesOk);
		
		edSeriesCvrControl = new EditCoverControl(this, this);
		edSeriesCvrControl.setBounds(12, 14, EditCoverControl.CTRL_WIDTH, EditCoverControl.CTRL_HEIGHT);
		pnlSeries.add(edSeriesCvrControl);
		
		edSeriesReference = new JReferenceChooser();
		edSeriesReference.setBounds(263, 256, 141, 20);
		pnlSeries.add(edSeriesReference);
		
		label = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
		label.setBounds(206, 258, 52, 16);
		pnlSeries.add(label);
		
		pnlSeason = new JPanel();
		pnlSeason.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlSeason.setBounds(446, 13, 367, 646);
		getContentPane().add(pnlSeason);
		pnlSeason.setLayout(null);
		
		label_15 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label_15.setBounds(12, 318, 52, 16);
		pnlSeason.add(label_15);
		
		edSeasonTitle = new JTextField();
		edSeasonTitle.setColumns(10);
		edSeasonTitle.setBounds(74, 316, 118, 20);
		pnlSeason.add(edSeasonTitle);
		
		label_16 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		label_16.setBounds(12, 351, 52, 16);
		pnlSeason.add(label_16);
		
		spnSeasonYear = new JSpinner();
		spnSeasonYear.setBounds(74, 349, 118, 20);
		spnSeasonYear.setEditor(new JSpinner.NumberEditor(spnSeasonYear, "0")); //$NON-NLS-1$
		pnlSeason.add(spnSeasonYear);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 378, 182, 223);
		pnlSeason.add(scrollPane_1);
		
		lsEpisodes = new JList<>();
		lsEpisodes.setCellRenderer(new HFixListCellRenderer());
		lsEpisodes.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateEpisodePanel();
			}
		});
		scrollPane_1.setViewportView(lsEpisodes);
		
		lblSeasonID = new JLabel();
		lblSeasonID.setBounds(177, 320, 17, 14);
		pnlSeason.add(lblSeasonID);
		
		btnAddEpisode = new JButton(LocaleBundle.getString("EditSeriesFrame.btnAddEmptyEpisode.text")); //$NON-NLS-1$
		btnAddEpisode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addEmptyEpisode();
			}
		});
		btnAddEpisode.setBounds(204, 378, 153, 23);
		pnlSeason.add(btnAddEpisode);
		
		btnRemoveEpisode = new JButton(LocaleBundle.getString("EditSeriesFrame.btnRemoveEpisode.text")); //$NON-NLS-1$
		btnRemoveEpisode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeEpisode();
			}
		});
		btnRemoveEpisode.setBounds(204, 446, 153, 23);
		pnlSeason.add(btnRemoveEpisode);
		
		btnAddMultipleEpisodes = new JButton(LocaleBundle.getString("EditSeriesFrame.btnAddMultipleEpisodes.text")); //$NON-NLS-1$
		btnAddMultipleEpisodes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addMultipleEpisodes();
			}
		});
		btnAddMultipleEpisodes.setBounds(204, 412, 153, 23);
		pnlSeason.add(btnAddMultipleEpisodes);
		
		btnSeasonOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnSeasonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOKSeason(true);
			}
		});
		btnSeasonOK.setBounds(145, 612, 89, 23);
		pnlSeason.add(btnSeasonOK);
		
		edSeasonCvrControl = new EditCoverControl(this, this);
		edSeasonCvrControl.setBounds(12, 13, EditCoverControl.CTRL_WIDTH, EditCoverControl.CTRL_HEIGHT);
		pnlSeason.add(edSeasonCvrControl);
		
		pnlEpisode = new JPanel();
		pnlEpisode.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlEpisode.setBounds(823, 13, 361, 646);
		getContentPane().add(pnlEpisode);
		pnlEpisode.setLayout(null);
		
		label_17 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label_17.setBounds(12, 13, 52, 16);
		pnlEpisode.add(label_17);
		
		edEpisodeTitle = new JTextField();
		edEpisodeTitle.setColumns(10);
		edEpisodeTitle.setBounds(74, 13, 271, 20);
		pnlEpisode.add(edEpisodeTitle);
		
		label_18 = new JLabel(LocaleBundle.getString("AddEpisodeFrame.lblEpisode.text")); //$NON-NLS-1$
		label_18.setBounds(12, 51, 46, 14);
		pnlEpisode.add(label_18);
		
		spnEpisodeEpisode = new JSpinner();
		spnEpisodeEpisode.setModel(new SpinnerNumberModel(0, 0, null, 1));
		spnEpisodeEpisode.setBounds(74, 49, 271, 20);
		pnlEpisode.add(spnEpisodeEpisode);
		
		label_19 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGesehen.text")); //$NON-NLS-1$
		label_19.setBounds(12, 89, 52, 16);
		pnlEpisode.add(label_19);
		
		cbEpisodeViewed = new JCheckBox();
		cbEpisodeViewed.setBounds(74, 84, 212, 25);
		pnlEpisode.add(cbEpisodeViewed);
		
		label_20 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		label_20.setBounds(12, 127, 52, 16);
		pnlEpisode.add(label_20);
		
		cbxEpisodeFormat = new JComboBox<>();
		cbxEpisodeFormat.setBounds(74, 123, 271, 22);
		pnlEpisode.add(cbxEpisodeFormat);
		
		label_21 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
		label_21.setBounds(12, 164, 46, 14);
		pnlEpisode.add(label_21);
		
		cbxEpisodeQuality = new JComboBox<>();
		cbxEpisodeQuality.setBounds(74, 160, 165, 22);
		pnlEpisode.add(cbxEpisodeQuality);
		
		label_22 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		label_22.setBounds(12, 197, 52, 16);
		pnlEpisode.add(label_22);
		
		spnEpisodeLength = new JSpinner();
		spnEpisodeLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
		spnEpisodeLength.setBounds(74, 197, 165, 20);
		pnlEpisode.add(spnEpisodeLength);
		
		label_23 = new JLabel("min."); //$NON-NLS-1$
		label_23.setBounds(245, 199, 94, 16);
		pnlEpisode.add(label_23);
		
		label_24 = new JLabel("Byte = "); //$NON-NLS-1$
		label_24.setBounds(245, 235, 100, 16);
		pnlEpisode.add(label_24);
		
		lblEpisodeFilesize = new JLabel();
		lblEpisodeFilesize.setBounds(74, 267, 193, 16);
		pnlEpisode.add(lblEpisodeFilesize);
		
		spnEpisodeSize = new JSpinner();
		spnEpisodeSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateEpisodesFilesizeDisplay();
			}
		});
		spnEpisodeSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
		spnEpisodeSize.setBounds(74, 234, 165, 20);
		pnlEpisode.add(spnEpisodeSize);
		
		label_26 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		label_26.setBounds(12, 235, 52, 16);
		pnlEpisode.add(label_26);
		
		button_2 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
		button_2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recalcEpisodeFilesize();
			}
		});
		button_2.setBounds(74, 296, 273, 23);
		pnlEpisode.add(button_2);
		
		btnEpisodeToday = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
		btnEpisodeToday.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				spnEpisodeAdded.setValue(CCDate.getCurrentDate());
			}
		});
		btnEpisodeToday.setBounds(251, 331, 96, 23);
		pnlEpisode.add(btnEpisodeToday);
		
		spnEpisodeAdded = new JCCDateSpinner(CCDate.getMinimumDate(), MIN_DATE, null);
		spnEpisodeAdded.setBounds(74, 332, 165, 20);
		pnlEpisode.add(spnEpisodeAdded);
		
		label_28 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		label_28.setBounds(12, 334, 71, 16);
		pnlEpisode.add(label_28);
		
		label_29 = new JLabel(LocaleBundle.getString("AddEpisodeFrame.lblPart.text")); //$NON-NLS-1$
		label_29.setBounds(12, 372, 46, 14);
		pnlEpisode.add(label_29);
		
		edEpisodePart = new ReadableTextField();
		edEpisodePart.setColumns(10);
		edEpisodePart.setBounds(74, 369, 212, 20);
		pnlEpisode.add(edEpisodePart);
		
		btnEpisodeOpenPart = new JButton(LocaleBundle.getString("EditSeriesFrame.btnEpisodeOpenPart.text")); //$NON-NLS-1$
		btnEpisodeOpenPart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openEpisodePart();
			}
		});
		btnEpisodeOpenPart.setBounds(295, 368, 52, 23);
		pnlEpisode.add(btnEpisodeOpenPart);
		
		lblTags = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
		lblTags.setBounds(12, 410, 52, 14);
		pnlEpisode.add(lblTags);
		
		tagPnl = new TagPanel();
		tagPnl.setBounds(74, 406, 273, 22);
		pnlEpisode.add(tagPnl);
		
		btnEpisodeOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnEpisodeOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOKEpisode(true);
			}
		});
		btnEpisodeOK.setBounds(148, 612, 89, 23);
		pnlEpisode.add(btnEpisodeOK);
		
		btnEpisodeCalcQuality = new JButton(LocaleBundle.getString("EditSeriesFrame.btnCalcQuality.text")); //$NON-NLS-1$
		btnEpisodeCalcQuality.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				recalcEpisodeQuality();
			}
		});
		btnEpisodeCalcQuality.setBounds(251, 159, 94, 23);
		pnlEpisode.add(btnEpisodeCalcQuality);
		
		lblHistory = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
		lblHistory.setBounds(12, 448, 52, 14);
		pnlEpisode.add(lblHistory);
		
		cmpEpisodeViewedHistory = new DateTimeListEditor();
		cmpEpisodeViewedHistory.setBackground(Color.WHITE);
		cmpEpisodeViewedHistory.setBounds(74, 448, 271, 152);
		pnlEpisode.add(cmpEpisodeViewedHistory);
	}

	private void setDefaultValues() {
		//#########################   SERIES   ###########################################################
		
		cbxSeriesGenre_0.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxSeriesGenre_1.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxSeriesGenre_2.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxSeriesGenre_3.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxSeriesGenre_4.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxSeriesGenre_5.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxSeriesGenre_6.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxSeriesGenre_7.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
	
		cbxSeriesFSK.setModel(new DefaultComboBoxModel<>(CCMovieFSK.getList()));
		cbxSeriesLanguage.setModel(new DefaultComboBoxModel<>(CCMovieLanguage.getList()));
		cbxSeriesScore.setModel(new DefaultComboBoxModel<>(CCMovieScore.getList()));
		
		//#########################   SEASON   ############################################################
		
		//#########################   EPISODE   ###########################################################
		
		cbxEpisodeFormat.setModel(new DefaultComboBoxModel<>(CCMovieFormat.getList()));
		cbxEpisodeQuality.setModel(new DefaultComboBoxModel<>(CCMovieQuality.getList()));
	}
	
	private CCSeason getSelectedSeason() {
		int sel = lsSeasons.getSelectedIndex();
		
		if (sel < 0) {
			return null;
		} else {
			return series.getSeason(sel);
		}
	}
	
	private CCEpisode getSelectedEpisode() {
		CCSeason season = getSelectedSeason();
		
		if (season == null) {
			return null;
		}
		
		int sel = lsEpisodes.getSelectedIndex();
		
		if (sel < 0) {
			return null;
		} else {
			return season.getEpisode(sel);
		}
	}
	
	private void updateSeriesPanel() {
		edSeriesCvrControl.setCover(series.getCover());
		
		lblSeriesSeriesID.setText(series.getSeriesID() + ""); //$NON-NLS-1$
		edSeriesTitle.setText(series.getTitle());
		cbxSeriesLanguage.setSelectedIndex(series.getLanguage().asInt());
		spnSeriesOnlineScore.setValue(series.getOnlinescore().asInt());
		cbxSeriesFSK.setSelectedIndex(series.getFSK().asInt());
		cbxSeriesScore.setSelectedIndex(series.getScore().asInt());
		
		cbxSeriesGenre_0.setSelectedIndex(series.getGenre(0).asInt());
		cbxSeriesGenre_1.setSelectedIndex(series.getGenre(1).asInt());
		cbxSeriesGenre_2.setSelectedIndex(series.getGenre(2).asInt());
		cbxSeriesGenre_3.setSelectedIndex(series.getGenre(3).asInt());
		cbxSeriesGenre_4.setSelectedIndex(series.getGenre(4).asInt());
		cbxSeriesGenre_5.setSelectedIndex(series.getGenre(5).asInt());
		cbxSeriesGenre_6.setSelectedIndex(series.getGenre(6).asInt());
		cbxSeriesGenre_7.setSelectedIndex(series.getGenre(7).asInt());
		
		edSeriesReference.setValue(series.getOnlineReference());
		
		lsSeasons.setSelectedIndex(-1);
		DefaultListModel<String> ml;
		lsSeasons.setModel(ml = new DefaultListModel<>());
		for (int i = 0; i < series.getSeasonCount(); i++) {
			ml.addElement(series.getSeason(i).getTitle());
		}
		
		updateSeasonPanel();
	}
	
	private void updateSeasonPanel() {
		CCSeason season = getSelectedSeason();
		
		if (season == null) {
			pnlSeason.setVisible(false);
		} else {
			pnlSeason.setVisible(true);
			
			edSeasonCvrControl.setCover(season.getCover());
			edSeasonTitle.setText(season.getTitle());
			spnSeasonYear.setValue(season.getYear());
			
			lsEpisodes.setSelectedIndex(-1);
			DefaultListModel<String> ml;
			lsEpisodes.setModel(ml = new DefaultListModel<>());
			for (int i = 0; i < season.getEpisodeCount(); i++) {
				ml.addElement(season.getEpisode(i).getTitle());
			}
		}
		
		updateEpisodePanel();
	}
	
	private void updateEpisodePanel() {
		CCEpisode episode = getSelectedEpisode();
		
		if (episode == null) {
			pnlEpisode.setVisible(false);
			return;
		} else {
			pnlEpisode.setVisible(true);
		}
		
		edEpisodeTitle.setText(episode.getTitle());
		spnEpisodeEpisode.setValue(episode.getEpisode());
		cbEpisodeViewed.setSelected(episode.isViewed());
		cbxEpisodeFormat.setSelectedIndex(episode.getFormat().asInt());
		cbxEpisodeQuality.setSelectedIndex(episode.getQuality().asInt());
		spnEpisodeLength.setValue(episode.getLength());
		spnEpisodeSize.setValue(episode.getFilesize().getBytes());
		spnEpisodeAdded.setValue(episode.getAddDate());
		cmpEpisodeViewedHistory.setValue(episode.getViewedHistory());
		edEpisodePart.setText(episode.getPart());
		tagPnl.setValue(episode.getTags());
		
		updateEpisodesFilesizeDisplay();
		testEpisodePart();
	}
	
	private void initFileChooser() {
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", new Validator<String>() { //$NON-NLS-1$
			@Override
			public boolean validate(String val) {
				return CCMovieFormat.isValidMovieFormat(val);
			}
		}));
		
		videoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$
	}
	
	private void updateEpisodesFilesizeDisplay() {
		lblEpisodeFilesize.setText(FileSizeFormatter.format((long) spnEpisodeSize.getValue()));
	}

	private void setSeriesCover(BufferedImage nci) {
		edSeriesCvrControl.setCover(nci);
	}

	@Override
	public String getFullTitle() {
		return edSeriesTitle.getText();
	}

	@Override
	public CCOnlineReference getSearchReference() {
		return edSeriesReference.getValue();
	}

	@Override
	public void setMovieFormat(CCMovieFormat cmf) {
		// NOP
	}

	@Override
	public void setFilepath(int p, String t) {
		// NOP
	}

	@Override
	public void setMovieName(String name) {
		// NOP
	}

	@Override
	public void setZyklus(String mZyklusTitle) {
		// NOP
	}

	@Override
	public void setZyklusNumber(int iRoman) {
		// NOP
	}

	@Override
	public void setFilesize(long size) {
		// NOP
	}

	@Override
	public void setMovieLanguage(CCMovieLanguage lang) {
		// NOP
	}

	@Override
	public void setQuality(CCMovieQuality q) {
		// NOP
	}

	@Override
	public void setYear(int y) {
		// NOP
	}

	@Override
	public void setGenre(int gid, int movGenre) {
		// NOP
	}

	@Override
	public void setFSK(int fsk) {
		// NOP
	}

	@Override
	public void setLength(int l) {
		// NOP
	}

	@Override
	public void setScore(int s) {
		// NOP
	}

	@Override
	public void setCover(BufferedImage nci) {
		setSeriesCover(nci);
	}
	
	@Override
	public void setOnlineReference(CCOnlineReference ref) {
		edSeriesReference.setValue(ref);
	}
	
	@Override
	public void onFinishInserting() {
		// nothing
	}
	
	private void addEmptySeason() {
		series.createNewEmptySeason().setTitle("<untitled>"); //$NON-NLS-1$
		
		updateSeriesPanel();
	}
	
	private void addSeason() {
		(new AddSeasonFrame(this, series, new UpdateCallbackListener() {
			@Override
			public void onUpdate(Object o) {
				updateSeriesPanel();
			}
		})).setVisible(true);
	}
	
	private void removeSeason() {
		CCSeason season = getSelectedSeason();
		
		if (season == null) {
			return;
		}
		
		if (DialogHelper.showYesNoDlg(this, LocaleBundle.getString("EditSeriesFrame.dlgDeleteSeason.caption"), LocaleBundle.getString("EditSeriesFrame.dlgDeleteSeason.text"))) {  //$NON-NLS-1$//$NON-NLS-2$
			series.deleteSeason(season);
		}
		
		updateSeriesPanel();
	}
	
	private void onOKSeries(boolean check) {
		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserDataSeries(problems);
		
		// some problems are too fatal
		if (probvalue && ! edSeriesCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			probvalue = false;
		}
		if (probvalue && edSeriesTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}
		
		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, new UserDataProblemHandler() {
				@Override
				public void onAMIEDIgnoreClicked() {
					onOKSeries(false);
				}
			}, this);
			amied.setVisible(true);
			return;
		}
		
		series.beginUpdating();
		
		//#####################################################################################
		
		series.setCover(edSeriesCvrControl.getResizedImage());
		series.setTitle(edSeriesTitle.getText());
		series.setLanguage(cbxSeriesLanguage.getSelectedIndex());
		series.setOnlinescore((int) spnSeriesOnlineScore.getValue());
		series.setFsk(cbxSeriesFSK.getSelectedIndex());
		series.setScore(cbxSeriesScore.getSelectedIndex());
		
		series.setGenre(CCMovieGenre.find(cbxSeriesGenre_0.getSelectedIndex()), 0);
		series.setGenre(CCMovieGenre.find(cbxSeriesGenre_1.getSelectedIndex()), 1);
		series.setGenre(CCMovieGenre.find(cbxSeriesGenre_2.getSelectedIndex()), 2);
		series.setGenre(CCMovieGenre.find(cbxSeriesGenre_3.getSelectedIndex()), 3);
		series.setGenre(CCMovieGenre.find(cbxSeriesGenre_4.getSelectedIndex()), 4);
		series.setGenre(CCMovieGenre.find(cbxSeriesGenre_5.getSelectedIndex()), 5);
		series.setGenre(CCMovieGenre.find(cbxSeriesGenre_6.getSelectedIndex()), 6);
		series.setGenre(CCMovieGenre.find(cbxSeriesGenre_7.getSelectedIndex()), 7);
		
		series.setOnlineReference(edSeriesReference.getValue());
		
		//#####################################################################################
		
		series.endUpdating();
		
		updateSeriesPanel();
	}
	
	public boolean checkUserDataSeries(List<UserDataProblem> ret) {
		String title = edSeriesTitle.getText();
		
		int oscore = (int) spnSeriesOnlineScore.getValue();
		
		int fskidx = cbxSeriesFSK.getSelectedIndex();
		
		int gen0 = cbxSeriesGenre_0.getSelectedIndex();
		int gen1 = cbxSeriesGenre_1.getSelectedIndex();
		int gen2 = cbxSeriesGenre_2.getSelectedIndex();
		int gen3 = cbxSeriesGenre_3.getSelectedIndex();
		int gen4 = cbxSeriesGenre_4.getSelectedIndex();
		int gen5 = cbxSeriesGenre_5.getSelectedIndex();
		int gen6 = cbxSeriesGenre_6.getSelectedIndex();
		int gen7 = cbxSeriesGenre_7.getSelectedIndex();
		
		CCOnlineReference ref = edSeriesReference.getValue();
		
		UserDataProblem.testSeriesData(ret, edSeriesCvrControl.getResizedImage(), title, oscore, gen0, gen1, gen2, gen3, gen4, gen5, gen6, gen7, fskidx, ref);
		
		return ret.isEmpty();
	}
	
	private void addEmptyEpisode() {
		CCSeason season = getSelectedSeason();
		
		if (season == null) {
			return;
		}
		
		CCEpisode newEp = season.createNewEmptyEpisode();
		
		newEp.setTitle("<untitled>"); //$NON-NLS-1$
		newEp.setAddDate(CCDate.getCurrentDate());
		newEp.setEpisodeNumber(season.getNextEpisodeNumber());
		Integer commonLen = season.getCommonEpisodeLength();
		if (commonLen != null) newEp.setLength(commonLen);
		
		updateSeasonPanel();
	}
	
	private void addMultipleEpisodes() {
		CCSeason season = getSelectedSeason();
		
		if (season == null) {
			return;
		}
		
		(new AddEpisodesFrame(this, season, new UpdateCallbackListener() {
			@Override
			public void onUpdate(Object o) {
				updateSeasonPanel();
			}
		})).setVisible(true);
	}
	
	private void removeEpisode() {
		CCSeason season = getSelectedSeason();
		CCEpisode episode = getSelectedEpisode();
		
		if (episode == null || season == null) {
			return;
		}
		
		if (DialogHelper.showYesNoDlg(this, LocaleBundle.getString("EditSeriesFrame.dlgDeleteEpisode.caption"), LocaleBundle.getString("EditSeriesFrame.dlgDeleteEpisode.text"))) {  //$NON-NLS-1$//$NON-NLS-2$
			season.deleteEpisode(episode);
		}
		
		updateSeasonPanel();
	}
	
	private void onOKSeason(boolean check) {
		CCSeason season = getSelectedSeason();
		
		if (season == null) {
			return;
		}
		
		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserDataSeason(problems);
		
		// some problems are too fatal
		if (probvalue && ! edSeasonCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			probvalue = false;
		}
		if (probvalue && edSeasonTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}
		
		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, new UserDataProblemHandler() {
				@Override
				public void onAMIEDIgnoreClicked() {
					onOKSeason(false);
				}
			}, this);
			amied.setVisible(true);
			return;
		}
		
		String prevTitle = season.getTitle();
		
		season.beginUpdating();
		
		//#####################################################################################
		
		season.setCover(edSeasonCvrControl.getResizedImage());
		season.setTitle(edSeasonTitle.getText());
		season.setYear((int) spnSeasonYear.getValue());
		
		//#####################################################################################
		
		season.endUpdating();
		
		updateSeasonPanel();
		
		if (! prevTitle.equals(season.getTitle())) {
			updateSeriesPanel();
			selectSeason(season);
		}
	}
	
	public boolean checkUserDataSeason(List<UserDataProblem> ret) {
		String title = edSeasonTitle.getText();
		int year = (int) spnSeasonYear.getValue();

		UserDataProblem.testSeasonData(ret, edSeasonCvrControl.getResizedImage(), title, year);
		
		return ret.isEmpty();
	}
	
	private void testEpisodePart() {
		String path = PathFormatter.fromCCPath(edEpisodePart.getText());
		
		if (new File(path).exists()) {
			edEpisodePart.setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		} else {
			edEpisodePart.setBackground(Color.RED);
		}
	}
	
	private void recalcEpisodeFilesize() {
		String path = PathFormatter.fromCCPath(edEpisodePart.getText());
		
		spnEpisodeSize.setValue(FileSizeFormatter.getFileSize(path));
	}

	private void recalcEpisodeFormat() {
		String path = PathFormatter.fromCCPath(edEpisodePart.getText());
		
		CCMovieFormat fmt = CCMovieFormat.getMovieFormatFromPath(path);
		
		cbxEpisodeFormat.setSelectedIndex(fmt.asInt());
	}

	private void recalcEpisodeQuality() {
		cbxEpisodeQuality.setSelectedIndex(CCMovieQuality.calculateQuality((Long)spnEpisodeSize.getValue(), (Integer)spnEpisodeLength.getValue(), 1).asInt());
	}
	
	private void openEpisodePart() {
		int returnval = videoFileChooser.showOpenDialog(this);
		
		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		String abspath = videoFileChooser.getSelectedFile().getAbsolutePath();
		String path = abspath;
		if (CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()) {
			path = PathFormatter.getCCPath(abspath, CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue());
		}
		
		edEpisodePart.setText(path);
		
		recalcEpisodeFilesize();
		recalcEpisodeFormat();
		recalcEpisodeQuality();
		
		testEpisodePart();
	}
	
	private void onOKEpisode(boolean check) {
		CCEpisode episode = getSelectedEpisode();
		
		if (episode == null) {
			return;
		}
		
		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserDataEpisode(problems);
		
		// some problems are too fatal
		if (probvalue && edEpisodeTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}
		
		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, new UserDataProblemHandler() {
				@Override
				public void onAMIEDIgnoreClicked() {
					onOKEpisode(false);
				}
			}, this);
			amied.setVisible(true);
			return;
		}
		
		String prevTitle = episode.getTitle();
		
		episode.beginUpdating();
		
		//#####################################################################################

		episode.setTitle(edEpisodeTitle.getText());
		episode.setEpisodeNumber((int) spnEpisodeEpisode.getValue());
		episode.setViewed(cbEpisodeViewed.isSelected());
		episode.setFormat(cbxEpisodeFormat.getSelectedIndex());
		episode.setQuality(cbxEpisodeQuality.getSelectedIndex());
		episode.setLength((int) spnEpisodeLength.getValue());
		episode.setFilesize((long) spnEpisodeSize.getValue());
		episode.setAddDate(spnEpisodeAdded.getValue());
		episode.setViewedHistory(cmpEpisodeViewedHistory.getValue());
		episode.setPart(edEpisodePart.getText());
		episode.setTags(tagPnl.getValue());
		
		//#####################################################################################
		
		episode.endUpdating();
		
		updateEpisodePanel();
		
		if (! prevTitle.equals(episode.getTitle())) {
			updateSeasonPanel();
			selectEpisode(episode);
		}
	}
	
	public boolean checkUserDataEpisode(List<UserDataProblem> ret) {
		CCSeason season = getSelectedSeason();
		CCEpisode episode = getSelectedEpisode();
		
		String title = edEpisodeTitle.getText();
		
		int len = (int) spnEpisodeLength.getValue();
		int epNum = (int) spnEpisodeEpisode.getValue();
		CCDate adddate = spnEpisodeAdded.getValue();
		CCDateTimeList lvdate = cmpEpisodeViewedHistory.getValue();

		long fsize = (long) spnEpisodeSize.getValue();
		int quality = cbxEpisodeQuality.getSelectedIndex();
		String csExtn  = CCMovieFormat.find(cbxEpisodeFormat.getSelectedIndex()).asString();
		String csExta = CCMovieFormat.find(cbxEpisodeFormat.getSelectedIndex()).asStringAlt();
		
		String part = edEpisodePart.getText();
		
		UserDataProblem.testEpisodeData(ret, season, episode, title, len, epNum, adddate, lvdate, fsize, csExtn, csExta, part, quality);
		
		return ret.isEmpty();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		if (listener != null) {
			listener.onUpdate(series);
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// nothing to do
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// nothing to do
	}
}
