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
import java.io.IOException;
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

import org.apache.commons.lang3.exception.ExceptionUtils;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.gui.frames.addEpisodesFrame.AddEpisodesFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.guiComponents.HFixListCellRenderer;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.TagPanel;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.Validator;
import de.jClipCorn.util.adapter.UpdateCallbackAdapter;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.MediaQueryException;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.mediaquery.MediaQueryResult;
import de.jClipCorn.util.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

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
	private JLabel label_11;
	private JSpinner spnSeriesOnlineScore;
	private JLabel label_12;
	private JLabel label_13;
	private JComboBox<String> cbxSeriesFSK;
	private JScrollPane scrollPane;
	private JComboBox<String> cbxSeriesScore;
	private JLabel lblScore;
	private JLabel label_15;
	private JTextField edSeasonTitle;
	private JLabel label_16;
	private JSpinner spnSeasonYear;
	private JList<String> lsEpisodes;
	private JScrollPane scrollPane_1;
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
	private JLabel lblGroups;
	private GroupListEditor edSeriesGroups;
	private JButton btnOkClose;
	private TagPanel tagPanel;
	private JLabel label_14;
	private JLabel lblLanguage;
	private LanguageChooser ctrlLang;
	private JButton btnMediaInfo1;
	private JButton btnMediaInfo2;
	private JButton btnMediaInfoRaw;
	private JPanel panel;
	private JPanel pnlEditSeriesInner;
	private JPanel pnlEditSeasonInner;
	private JPanel pnlEditEpisodeInner;
	private JPanel panel_1;
	private JPanel panel_4;
	private JPanel pnlEditSeriesOuter;
	private JPanel pnlEditSeasonOuter;
	private JPanel panel_7;
	private JPanel pnlEditEpisodeOuter;
	private JPanel panel_2;
	private JPanel panel_3;

	/**
	 * @wbp.parser.constructor
	 */
	public EditSeriesFrame(Component owner, CCSeries ser, UpdateCallbackListener ucl) {
		super();
		setMinimumSize(new Dimension(1163, 775));
		this.series = ser;
		this.videoFileChooser = new JFileChooser(PathFormatter.fromCCPath(ser.getCommonPathStart(true)));

		if (ucl == null)
			this.listener = new UpdateCallbackAdapter();
		else
			this.listener = ucl;
		
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
		lsEpisodes.setSelectedIndex(e.getEpisodeIndexInSeason()); // Calls the Listener
	}

	private void selectSeason(CCSeason s) {
		lsSeasons.setSelectedIndex(s.getSeasonNumber()); // Calls the Listener
	}

	private void initGUI() {
		setSize(new Dimension(1300, 800));
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("EditSeriesFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(4, 4, 4, 4));
		getContentPane().add(panel);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("32dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("32dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("32dlu:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		pnlEditSeriesOuter = new JPanel();
		pnlEditSeriesOuter.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.add(pnlEditSeriesOuter, "1, 1, fill, fill"); //$NON-NLS-1$
		pnlEditSeriesOuter.setLayout(new BorderLayout(0, 0));
		
		pnlEditSeriesInner = new JPanel();
		pnlEditSeriesOuter.add(pnlEditSeriesInner);
		pnlEditSeriesInner.setBorder(new EmptyBorder(4, 4, 4, 4));
		pnlEditSeriesInner.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("120px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("0dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
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
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		edSeriesCvrControl = new EditCoverControl(this, this);
		pnlEditSeriesInner.add(edSeriesCvrControl, "1, 1, 3, 1, left, default"); //$NON-NLS-1$
				
				panel_2 = new JPanel();
				pnlEditSeriesInner.add(panel_2, "5, 1, 3, 1, fill, fill"); //$NON-NLS-1$
				panel_2.setLayout(new FormLayout(new ColumnSpec[] {
						FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("0dlu:grow"),}, //$NON-NLS-1$
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
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,}));
				
				label_9 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
				panel_2.add(label_9, "1, 1"); //$NON-NLS-1$
				
				cbxSeriesGenre_0 = new JComboBox<>();
				panel_2.add(cbxSeriesGenre_0, "3, 1"); //$NON-NLS-1$
				
				label_8 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
				panel_2.add(label_8, "1, 3"); //$NON-NLS-1$
				
				cbxSeriesGenre_1 = new JComboBox<>();
				panel_2.add(cbxSeriesGenre_1, "3, 3"); //$NON-NLS-1$
				
				label_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
				panel_2.add(label_7, "1, 5"); //$NON-NLS-1$
				
				cbxSeriesGenre_2 = new JComboBox<>();
				panel_2.add(cbxSeriesGenre_2, "3, 5"); //$NON-NLS-1$
				
				label_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
				panel_2.add(label_6, "1, 7"); //$NON-NLS-1$
				
				cbxSeriesGenre_3 = new JComboBox<>();
				panel_2.add(cbxSeriesGenre_3, "3, 7"); //$NON-NLS-1$
				
				label_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
				panel_2.add(label_5, "1, 9"); //$NON-NLS-1$
				
				cbxSeriesGenre_4 = new JComboBox<>();
				panel_2.add(cbxSeriesGenre_4, "3, 9"); //$NON-NLS-1$
				
				label_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
				panel_2.add(label_4, "1, 11"); //$NON-NLS-1$
				
				cbxSeriesGenre_5 = new JComboBox<>();
				panel_2.add(cbxSeriesGenre_5, "3, 11"); //$NON-NLS-1$
				
				label_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
				panel_2.add(label_3, "1, 13"); //$NON-NLS-1$
				
				cbxSeriesGenre_6 = new JComboBox<>();
				panel_2.add(cbxSeriesGenre_6, "3, 13"); //$NON-NLS-1$
				
				label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
				panel_2.add(label_2, "1, 15"); //$NON-NLS-1$
				
				cbxSeriesGenre_7 = new JComboBox<>();
				panel_2.add(cbxSeriesGenre_7, "3, 15"); //$NON-NLS-1$
		
				label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
				pnlEditSeriesInner.add(label_1, "1, 3"); //$NON-NLS-1$
				
				edSeriesTitle = new JTextField();
				pnlEditSeriesInner.add(edSeriesTitle, "3, 3, 5, 1"); //$NON-NLS-1$
				edSeriesTitle.setColumns(10);
				
						label_11 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
						pnlEditSeriesInner.add(label_11, "1, 5"); //$NON-NLS-1$
						
						spnSeriesOnlineScore = new JSpinner();
						pnlEditSeriesInner.add(spnSeriesOnlineScore, "3, 5, 3, 1"); //$NON-NLS-1$
						spnSeriesOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
						
						label_12 = new JLabel(" / 10"); //$NON-NLS-1$
						pnlEditSeriesInner.add(label_12, "7, 5"); //$NON-NLS-1$
						
						label_13 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
						pnlEditSeriesInner.add(label_13, "1, 7"); //$NON-NLS-1$
						
						cbxSeriesFSK = new JComboBox<>();
						pnlEditSeriesInner.add(cbxSeriesFSK, "3, 7, 5, 1"); //$NON-NLS-1$
						
						lblScore = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
						pnlEditSeriesInner.add(lblScore, "1, 9"); //$NON-NLS-1$
						
						cbxSeriesScore = new JComboBox<>();
						pnlEditSeriesInner.add(cbxSeriesScore, "3, 9, 5, 1"); //$NON-NLS-1$
						
						label = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
						pnlEditSeriesInner.add(label, "1, 11"); //$NON-NLS-1$
						
						edSeriesReference = new JReferenceChooser();
						pnlEditSeriesInner.add(edSeriesReference, "3, 11, 5, 1"); //$NON-NLS-1$
						
						lblGroups = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
						pnlEditSeriesInner.add(lblGroups, "1, 13"); //$NON-NLS-1$
						
						edSeriesGroups = new GroupListEditor(series.getMovieList());
						pnlEditSeriesInner.add(edSeriesGroups, "3, 13, 5, 1"); //$NON-NLS-1$
						
						label_14 = new JLabel("Tags"); //$NON-NLS-1$
						pnlEditSeriesInner.add(label_14, "1, 15"); //$NON-NLS-1$
						
						tagPanel = new TagPanel();
						pnlEditSeriesInner.add(tagPanel, "3, 15, 5, 1"); //$NON-NLS-1$
						
						panel_3 = new JPanel();
						pnlEditSeriesInner.add(panel_3, "1, 17, 7, 1, fill, fill"); //$NON-NLS-1$
						panel_3.setLayout(new FormLayout(new ColumnSpec[] {
								FormSpecs.RELATED_GAP_COLSPEC,
								ColumnSpec.decode("default:grow"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.DEFAULT_COLSPEC,},
							new RowSpec[] {
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								RowSpec.decode("default:grow"),})); //$NON-NLS-1$
						
						scrollPane = new JScrollPane();
						panel_3.add(scrollPane, "2, 2, 1, 5"); //$NON-NLS-1$
						
						lsSeasons = new JList<>();
						lsSeasons.setCellRenderer(new HFixListCellRenderer());
						lsSeasons.addListSelectionListener(arg0 -> updateSeasonPanel());
						scrollPane.setViewportView(lsSeasons);
						
						btnAddEmptySeason = new JButton(LocaleBundle.getString("EditSeriesFrame.btnAddEmptySeason.text")); //$NON-NLS-1$
						panel_3.add(btnAddEmptySeason, "4, 2"); //$NON-NLS-1$
						
						btnAddSeason = new JButton(LocaleBundle.getString("EditSeriesFrame.btnAddSeason.text")); //$NON-NLS-1$
						panel_3.add(btnAddSeason, "4, 4"); //$NON-NLS-1$
						
						btnRemoveSeason = new JButton(LocaleBundle.getString("EditSeriesFrame.btnRemoveSeason.text")); //$NON-NLS-1$
						panel_3.add(btnRemoveSeason, "4, 6, default, top"); //$NON-NLS-1$
						btnRemoveSeason.addActionListener(e -> removeSeason());
						btnAddSeason.addActionListener(e -> addSeason());
						btnAddEmptySeason.addActionListener(e -> addEmptySeason());
						
						panel_4 = new JPanel();
						pnlEditSeriesInner.add(panel_4, "1, 19, 7, 1, fill, fill"); //$NON-NLS-1$
						
						btnSeriesOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
						btnSeriesOk.setPreferredSize(new Dimension(100, 26));
						panel_4.add(btnSeriesOk);
						
						btnOkClose = new JButton(LocaleBundle.getString("UIGeneric.btnOK_and_Close.text")); //$NON-NLS-1$
						btnOkClose.setPreferredSize(new Dimension(100, 26));
						panel_4.add(btnOkClose);
						btnOkClose.addActionListener(e -> {
							try {
								if (onOKSeries(true)) EditSeriesFrame.this.dispatchEvent(new WindowEvent(EditSeriesFrame.this, WindowEvent.WINDOW_CLOSING));
							} catch (EnumFormatException e1) {
								CCLog.addError(e1);
							}
						});
						btnSeriesOk.addActionListener(e -> {try {
							onOKSeries(true);
						} catch (EnumFormatException e1) {
							CCLog.addError(e1);
						}});
						
						pnlEditSeasonOuter = new JPanel();
						pnlEditSeasonOuter.setBorder(new LineBorder(new Color(0, 0, 0)));
						panel.add(pnlEditSeasonOuter, "3, 1, fill, fill"); //$NON-NLS-1$
						pnlEditSeasonOuter.setLayout(new BorderLayout(0, 0));
						
						pnlEditSeasonInner = new JPanel();
						pnlEditSeasonOuter.add(pnlEditSeasonInner);
						pnlEditSeasonInner.setBorder(new EmptyBorder(4, 4, 4, 4));
						pnlEditSeasonInner.setLayout(new FormLayout(new ColumnSpec[] {
								ColumnSpec.decode("max(30dlu;default)"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_COLSPEC,
								ColumnSpec.decode("100px:grow"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.PREF_COLSPEC,},
							new RowSpec[] {
								FormSpecs.PREF_ROWSPEC,
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
								RowSpec.decode("default:grow"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,}));
						
						edSeasonCvrControl = new EditCoverControl(this, this);
						pnlEditSeasonInner.add(edSeasonCvrControl, "1, 1, 3, 1, left, default"); //$NON-NLS-1$
						
						label_15 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
						pnlEditSeasonInner.add(label_15, "1, 3"); //$NON-NLS-1$
						
						edSeasonTitle = new JTextField();
						pnlEditSeasonInner.add(edSeasonTitle, "3, 3"); //$NON-NLS-1$
						edSeasonTitle.setColumns(10);
						
						lblSeasonID = new JLabel();
						pnlEditSeasonInner.add(lblSeasonID, "5, 3"); //$NON-NLS-1$
						
						label_16 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
						pnlEditSeasonInner.add(label_16, "1, 5"); //$NON-NLS-1$
						
						spnSeasonYear = new JSpinner();
						pnlEditSeasonInner.add(spnSeasonYear, "3, 5"); //$NON-NLS-1$
						spnSeasonYear.setEditor(new JSpinner.NumberEditor(spnSeasonYear, "0")); //$NON-NLS-1$
						
						scrollPane_1 = new JScrollPane();
						pnlEditSeasonInner.add(scrollPane_1, "1, 7, 3, 7, fill, fill"); //$NON-NLS-1$
						
						lsEpisodes = new JList<>();
						lsEpisodes.setCellRenderer(new HFixListCellRenderer());
						lsEpisodes.addListSelectionListener(e -> updateEpisodePanel());
						scrollPane_1.setViewportView(lsEpisodes);
						
						btnAddEpisode = new JButton(LocaleBundle.getString("EditSeriesFrame.btnAddEmptyEpisode.text")); //$NON-NLS-1$
						pnlEditSeasonInner.add(btnAddEpisode, "5, 7"); //$NON-NLS-1$
						btnAddEpisode.addActionListener(e -> addEmptyEpisode());
						
						btnAddMultipleEpisodes = new JButton(LocaleBundle.getString("EditSeriesFrame.btnAddMultipleEpisodes.text")); //$NON-NLS-1$
						pnlEditSeasonInner.add(btnAddMultipleEpisodes, "5, 9"); //$NON-NLS-1$
						btnAddMultipleEpisodes.addActionListener(e -> addMultipleEpisodes());
						
						btnRemoveEpisode = new JButton(LocaleBundle.getString("EditSeriesFrame.btnRemoveEpisode.text")); //$NON-NLS-1$
						pnlEditSeasonInner.add(btnRemoveEpisode, "5, 11"); //$NON-NLS-1$
						btnRemoveEpisode.addActionListener(e -> removeEpisode());
						
						panel_1 = new JPanel();
						pnlEditSeasonInner.add(panel_1, "1, 15, 5, 1, fill, fill"); //$NON-NLS-1$
						
						btnSeasonOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
						btnSeasonOK.setPreferredSize(new Dimension(128, 26));
						panel_1.add(btnSeasonOK);
						btnSeasonOK.addActionListener(e -> onOKSeason(true));
						
						pnlEditEpisodeOuter = new JPanel();
						pnlEditEpisodeOuter.setBorder(new LineBorder(new Color(0, 0, 0)));
						panel.add(pnlEditEpisodeOuter, "5, 1, fill, fill"); //$NON-NLS-1$
						pnlEditEpisodeOuter.setLayout(new BorderLayout(0, 0));
						
						pnlEditEpisodeInner = new JPanel();
						pnlEditEpisodeOuter.add(pnlEditEpisodeInner);
						pnlEditEpisodeInner.setBorder(new EmptyBorder(4, 4, 4, 4));
						pnlEditEpisodeInner.setLayout(new FormLayout(new ColumnSpec[] {
								FormSpecs.DEFAULT_COLSPEC,
								FormSpecs.RELATED_GAP_COLSPEC,
								ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.DEFAULT_COLSPEC,
								FormSpecs.RELATED_GAP_COLSPEC,
								ColumnSpec.decode("22px"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.DEFAULT_COLSPEC,},
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
								RowSpec.decode("22px"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_ROWSPEC,
								RowSpec.decode("22px"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								RowSpec.decode("12dlu"), //$NON-NLS-1$
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								RowSpec.decode("default:grow"), //$NON-NLS-1$
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,}));
						
						label_17 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_17, "1, 2"); //$NON-NLS-1$
						
						edEpisodeTitle = new JTextField();
						pnlEditEpisodeInner.add(edEpisodeTitle, "3, 2, 7, 1"); //$NON-NLS-1$
						edEpisodeTitle.setColumns(10);
						
						label_18 = new JLabel(LocaleBundle.getString("AddEpisodeFrame.lblEpisode.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_18, "1, 4"); //$NON-NLS-1$
						
						spnEpisodeEpisode = new JSpinner();
						pnlEditEpisodeInner.add(spnEpisodeEpisode, "3, 4, 7, 1"); //$NON-NLS-1$
						spnEpisodeEpisode.setModel(new SpinnerNumberModel(0, 0, null, 1));
						
						label_19 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGesehen.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_19, "1, 6"); //$NON-NLS-1$
						
						cbEpisodeViewed = new JCheckBox();
						pnlEditEpisodeInner.add(cbEpisodeViewed, "3, 6, 7, 1"); //$NON-NLS-1$
						
						label_20 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_20, "1, 8"); //$NON-NLS-1$
						
						cbxEpisodeFormat = new JComboBox<>();
						pnlEditEpisodeInner.add(cbxEpisodeFormat, "3, 8, 7, 1"); //$NON-NLS-1$
						
						label_21 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_21, "1, 10"); //$NON-NLS-1$
						
						cbxEpisodeQuality = new JComboBox<>();
						pnlEditEpisodeInner.add(cbxEpisodeQuality, "3, 10"); //$NON-NLS-1$
						
						btnEpisodeCalcQuality = new JButton(LocaleBundle.getString("EditSeriesFrame.btnCalcQuality.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(btnEpisodeCalcQuality, "5, 10, 5, 1"); //$NON-NLS-1$
						btnEpisodeCalcQuality.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								recalcEpisodeQuality();
							}
						});
						
						lblLanguage = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(lblLanguage, "1, 12"); //$NON-NLS-1$
						
						ctrlLang = new LanguageChooser();
						pnlEditEpisodeInner.add(ctrlLang, "3, 12"); //$NON-NLS-1$
						
						btnMediaInfo1 = new JButton(Resources.ICN_MENUBAR_UPDATECODECDATA.get16x16());
						pnlEditEpisodeInner.add(btnMediaInfo1, "7, 12"); //$NON-NLS-1$
						btnMediaInfo1.addActionListener(e -> parseCodecMetadata_Lang());
						btnMediaInfo1.setToolTipText("MediaInfo"); //$NON-NLS-1$
						
						btnMediaInfoRaw = new JButton("..."); //$NON-NLS-1$
						pnlEditEpisodeInner.add(btnMediaInfoRaw, "9, 12"); //$NON-NLS-1$
						btnMediaInfoRaw.addActionListener(e -> showCodecMetadata());
						btnMediaInfoRaw.setToolTipText("MediaInfo"); //$NON-NLS-1$
						
						label_22 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_22, "1, 14"); //$NON-NLS-1$
						
						spnEpisodeLength = new JSpinner();
						pnlEditEpisodeInner.add(spnEpisodeLength, "3, 14"); //$NON-NLS-1$
						spnEpisodeLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
						
						label_23 = new JLabel("min."); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_23, "5, 14"); //$NON-NLS-1$
						
						btnMediaInfo2 = new JButton(Resources.ICN_MENUBAR_UPDATECODECDATA.get16x16());
						pnlEditEpisodeInner.add(btnMediaInfo2, "7, 14"); //$NON-NLS-1$
						btnMediaInfo2.addActionListener(e -> parseCodecMetadata_Len());
						btnMediaInfo2.setToolTipText("MediaInfo"); //$NON-NLS-1$
						
						label_26 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_26, "1, 16"); //$NON-NLS-1$
						
						spnEpisodeSize = new JSpinner();
						pnlEditEpisodeInner.add(spnEpisodeSize, "3, 16"); //$NON-NLS-1$
						spnEpisodeSize.addChangeListener(arg0 -> updateEpisodesFilesizeDisplay());
						spnEpisodeSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
						
						label_24 = new JLabel("Byte = "); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_24, "5, 16, 5, 1"); //$NON-NLS-1$
						
						lblEpisodeFilesize = new JLabel();
						pnlEditEpisodeInner.add(lblEpisodeFilesize, "3, 18, 7, 1"); //$NON-NLS-1$
						
						button_2 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(button_2, "3, 20, 7, 1"); //$NON-NLS-1$
						
						label_28 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_28, "1, 22"); //$NON-NLS-1$
						
						spnEpisodeAdded = new JCCDateSpinner(CCDate.getMinimumDate(), MIN_DATE, null);
						pnlEditEpisodeInner.add(spnEpisodeAdded, "3, 22"); //$NON-NLS-1$
						
						btnEpisodeToday = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(btnEpisodeToday, "5, 22, 5, 1"); //$NON-NLS-1$
						
						label_29 = new JLabel(LocaleBundle.getString("AddEpisodeFrame.lblPart.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(label_29, "1, 24"); //$NON-NLS-1$
						
						edEpisodePart = new ReadableTextField();
						pnlEditEpisodeInner.add(edEpisodePart, "3, 24, 3, 1"); //$NON-NLS-1$
						edEpisodePart.setColumns(10);
						
						btnEpisodeOpenPart = new JButton(LocaleBundle.getString("EditSeriesFrame.btnEpisodeOpenPart.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(btnEpisodeOpenPart, "7, 24, 3, 1"); //$NON-NLS-1$
						
						lblTags = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(lblTags, "1, 26"); //$NON-NLS-1$
						
						tagPnl = new TagPanel();
						pnlEditEpisodeInner.add(tagPnl, "3, 26, 7, 1"); //$NON-NLS-1$
						
						lblHistory = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
						pnlEditEpisodeInner.add(lblHistory, "1, 28, default, top"); //$NON-NLS-1$
						
						cmpEpisodeViewedHistory = new DateTimeListEditor();
						pnlEditEpisodeInner.add(cmpEpisodeViewedHistory, "3, 28, 7, 1, fill, fill"); //$NON-NLS-1$
						cmpEpisodeViewedHistory.setBackground(Color.WHITE);
						
						panel_7 = new JPanel();
						pnlEditEpisodeInner.add(panel_7, "1, 30, 9, 1, fill, fill"); //$NON-NLS-1$
						
						btnEpisodeOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
						btnEpisodeOK.setPreferredSize(new Dimension(100, 26));
						panel_7.add(btnEpisodeOK);
						btnEpisodeOK.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									onOKEpisode(true);
								} catch (EnumFormatException e1) {
									CCLog.addError(e1);
								}
							}
						});
						btnEpisodeOpenPart.addActionListener(e -> openEpisodePart());
						btnEpisodeToday.addActionListener(e -> spnEpisodeAdded.setValue(CCDate.getCurrentDate()));
						button_2.addActionListener(e -> recalcEpisodeFilesize());
	}

	private void setDefaultValues() {
		//#########################   SERIES   ###########################################################
		
		cbxSeriesGenre_0.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxSeriesGenre_1.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxSeriesGenre_2.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxSeriesGenre_3.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxSeriesGenre_4.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxSeriesGenre_5.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxSeriesGenre_6.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxSeriesGenre_7.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
	
		cbxSeriesFSK.setModel(new DefaultComboBoxModel<>(CCFSK.getWrapper().getList()));
		cbxSeriesScore.setModel(new DefaultComboBoxModel<>(CCUserScore.getWrapper().getList()));
		
		//#########################   SEASON   ############################################################
		
		//#########################   EPISODE   ###########################################################
		
		cbxEpisodeFormat.setModel(new DefaultComboBoxModel<>(CCFileFormat.getWrapper().getList()));
		cbxEpisodeQuality.setModel(new DefaultComboBoxModel<>(CCQuality.getWrapper().getList()));
	}
	
	private CCSeason getSelectedSeason() {
		int sel = lsSeasons.getSelectedIndex();
		
		if (sel < 0) {
			return null;
		} else {
			return series.getSeasonByArrayIndex(sel);
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
			return season.getEpisodeByArrayIndex(sel);
		}
	}
	
	private List<CCEpisode> getSelectedEpisodes() {
		CCSeason season = getSelectedSeason();
		
		if (season == null) {
			return new ArrayList<>();
		}
		
		List<CCEpisode> result = new ArrayList<>();
		
		for (int idx : lsEpisodes.getSelectedIndices()) {
			result.add(season.getEpisodeByArrayIndex(idx));
		}
		
		return result;
	}
	
	private void updateSeriesPanel() {
		edSeriesCvrControl.setCover(series.getCover());
		
		edSeriesTitle.setText(series.getTitle());
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
		
		edSeriesGroups.setValue(series.getGroups());
		
		edSeriesReference.setValue(series.getOnlineReference());
		
		tagPanel.setValue(series.getTags());
		
		lsSeasons.setSelectedIndex(-1);
		DefaultListModel<String> ml;
		lsSeasons.setModel(ml = new DefaultListModel<>());
		for (int i = 0; i < series.getSeasonCount(); i++) {
			ml.addElement(series.getSeasonByArrayIndex(i).getTitle());
		}
		
		updateSeasonPanel();
	}
	
	private void updateSeasonPanel() {
		CCSeason season = getSelectedSeason();
		
		if (season == null) {
			pnlEditSeasonOuter.setVisible(false);
		} else {
			pnlEditSeasonOuter.setVisible(true);
			
			edSeasonCvrControl.setCover(season.getCover());
			edSeasonTitle.setText(season.getTitle());
			spnSeasonYear.setValue(season.getYear());
			
			lsEpisodes.setSelectedIndex(-1);
			DefaultListModel<String> ml;
			lsEpisodes.setModel(ml = new DefaultListModel<>());
			for (int i = 0; i < season.getEpisodeCount(); i++) {
				ml.addElement(season.getEpisodeByArrayIndex(i).getTitle());
			}
		}
		
		updateEpisodePanel();
	}
	
	private void updateEpisodePanel() {
		CCEpisode episode = getSelectedEpisode();
		
		if (episode == null) {
			pnlEditEpisodeOuter.setVisible(false);
			return;
		} else {
			pnlEditEpisodeOuter.setVisible(true);
		}
		
		edEpisodeTitle.setText(episode.getTitle());
		spnEpisodeEpisode.setValue(episode.getEpisodeNumber());
		cbEpisodeViewed.setSelected(episode.isViewed());
		cbxEpisodeFormat.setSelectedIndex(episode.getFormat().asInt());
		cbxEpisodeQuality.setSelectedIndex(episode.getQuality().asInt());
		spnEpisodeLength.setValue(episode.getLength());
		spnEpisodeSize.setValue(episode.getFilesize().getBytes());
		spnEpisodeAdded.setValue(episode.getAddDate());
		cmpEpisodeViewedHistory.setValue(episode.getViewedHistory());
		edEpisodePart.setText(episode.getPart());
		tagPnl.setValue(episode.getTags());
		ctrlLang.setValue(episode.getLanguage());
		
		updateEpisodesFilesizeDisplay();
		testEpisodePart();
	}
	
	private void initFileChooser() {
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", new Validator<String>() { //$NON-NLS-1$
			@Override
			public boolean validate(String val) {
				return CCFileFormat.isValidMovieFormat(val);
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
	public String getTitleForParser() {
		return edSeriesTitle.getText();
	}

	@Override
	public CCOnlineReferenceList getSearchReference() {
		return edSeriesReference.getValue();
	}

	@Override
	public void setMovieFormat(CCFileFormat cmf) {
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
	public void setQuality(CCQuality q) {
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
	public void setOnlineReference(CCOnlineReferenceList ref) {
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
	
	private boolean onOKSeries(boolean check) throws EnumFormatException {
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
			InputErrorDialog amied = new InputErrorDialog(problems, () -> {
				try {
					onOKSeries(false);
				} catch (CCFormatException e) {
					CCLog.addError(e);
				}
			}, this) ;
			amied.setVisible(true);
			return false;
		}
		
		series.beginUpdating();
		
		//#####################################################################################
		
		series.setCover(edSeriesCvrControl.getResizedImageForStorage());
		series.setTitle(edSeriesTitle.getText());
		series.setOnlinescore((int) spnSeriesOnlineScore.getValue());
		series.setFsk(cbxSeriesFSK.getSelectedIndex());
		series.setScore(cbxSeriesScore.getSelectedIndex());
		
		series.setGenre(CCGenre.getWrapper().findOrException(cbxSeriesGenre_0.getSelectedIndex()), 0);
		series.setGenre(CCGenre.getWrapper().findOrException(cbxSeriesGenre_1.getSelectedIndex()), 1);
		series.setGenre(CCGenre.getWrapper().findOrException(cbxSeriesGenre_2.getSelectedIndex()), 2);
		series.setGenre(CCGenre.getWrapper().findOrException(cbxSeriesGenre_3.getSelectedIndex()), 3);
		series.setGenre(CCGenre.getWrapper().findOrException(cbxSeriesGenre_4.getSelectedIndex()), 4);
		series.setGenre(CCGenre.getWrapper().findOrException(cbxSeriesGenre_5.getSelectedIndex()), 5);
		series.setGenre(CCGenre.getWrapper().findOrException(cbxSeriesGenre_6.getSelectedIndex()), 6);
		series.setGenre(CCGenre.getWrapper().findOrException(cbxSeriesGenre_7.getSelectedIndex()), 7);
		
		series.setOnlineReference(edSeriesReference.getValue());
		series.setGroups(edSeriesGroups.getValue());
		
		series.setTags(tagPanel.getValue());
		
		//#####################################################################################
		
		series.endUpdating();
		
		updateSeriesPanel();

		return true;
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
		
		CCOnlineReferenceList ref = edSeriesReference.getValue();
		
		UserDataProblem.testSeriesData(ret, edSeriesCvrControl.getResizedImageForStorage(), title, oscore, gen0, gen1, gen2, gen3, gen4, gen5, gen6, gen7, fskidx, ref);
		
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
		if (commonLen == null) commonLen = season.getConsensEpisodeLength();
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
		if (season == null) return;
		
		if (getSelectedEpisodes().size() <= 1) {
			CCEpisode episode = getSelectedEpisode();
			
			if (episode == null) {
				return;
			}
			
			if (DialogHelper.showYesNoDlg(this, LocaleBundle.getString("EditSeriesFrame.dlgDeleteEpisode.caption"), LocaleBundle.getString("EditSeriesFrame.dlgDeleteEpisode.text"))) {  //$NON-NLS-1$//$NON-NLS-2$
				season.deleteEpisode(episode);
			}
		} else {
			List<CCEpisode> listEpisodes = getSelectedEpisodes();
			
			if (DialogHelper.showYesNoDlg(this, LocaleBundle.getString("EditSeriesFrame.dlgDeleteMultipleEpisode.caption"), LocaleBundle.getFormattedString("EditSeriesFrame.dlgDeleteMultipleEpisode.text", listEpisodes.size()))) {  //$NON-NLS-1$//$NON-NLS-2$
				for (CCEpisode ep : listEpisodes) {
					season.deleteEpisode(ep);
				}
			}
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
			InputErrorDialog amied = new InputErrorDialog(problems, () -> onOKSeason(false), this);
			amied.setVisible(true);
			return;
		}
		
		String prevTitle = season.getTitle();
		
		season.beginUpdating();
		
		//#####################################################################################
		
		season.setCover(edSeasonCvrControl.getResizedImageForStorage());
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

		UserDataProblem.testSeasonData(ret, edSeasonCvrControl.getResizedImageForStorage(), title, year);
		
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
		
		CCFileFormat fmt = CCFileFormat.getMovieFormatFromPath(path);
		
		cbxEpisodeFormat.setSelectedIndex(fmt.asInt());
	}

	private void recalcEpisodeQuality() {
		cbxEpisodeQuality.setSelectedIndex(CCQuality.calculateQuality((Long)spnEpisodeSize.getValue(), (Integer)spnEpisodeLength.getValue(), 1).asInt());
	}
	
	private void openEpisodePart() {
		int returnval = videoFileChooser.showOpenDialog(this);
		
		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		String abspath = videoFileChooser.getSelectedFile().getAbsolutePath();
		String path = abspath;
		if (CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()) {
			path = PathFormatter.getCCPath(abspath);
		}
		
		edEpisodePart.setText(path);
		
		recalcEpisodeFilesize();
		recalcEpisodeFormat();
		recalcEpisodeQuality();
		
		testEpisodePart();
	}
	
	private void onOKEpisode(boolean check) throws EnumFormatException {
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
			InputErrorDialog amied = new InputErrorDialog(problems, () -> {
				try {
					onOKEpisode(false);
				} catch (CCFormatException e) {
					CCLog.addError(e);
				}
			}, this) ;
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
		episode.setLanguage(ctrlLang.getValue());
		
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

		try {
			long fsize = (long) spnEpisodeSize.getValue();
			int quality = cbxEpisodeQuality.getSelectedIndex();
			String csExtn  = CCFileFormat.getWrapper().findOrException(cbxEpisodeFormat.getSelectedIndex()).asString();
			String csExta = CCFileFormat.getWrapper().findOrException(cbxEpisodeFormat.getSelectedIndex()).asStringAlt();
			CCDBLanguageList lng = ctrlLang.getValue();
			
			String part = edEpisodePart.getText();
			
			UserDataProblem.testEpisodeData(ret, season, episode, title, len, epNum, adddate, lvdate, fsize, csExtn, csExta, part, quality, lng);
			
			return ret.isEmpty();
		} catch (CCFormatException e) {
			return false;
		}
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

	private void parseCodecMetadata_Lang() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(edEpisodePart.getText()));

			if (dat.AudioLanguages == null) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			CCDBLanguageList dbll = dat.AudioLanguages;

			if (dbll.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			} else {
				ctrlLang.setValue(dbll);
			}

		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void parseCodecMetadata_Len() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(edEpisodePart.getText()));

			int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			spnEpisodeLength.setValue(dur);

		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void showCodecMetadata() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			String dat = MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(edEpisodePart.getText()));

			GenericTextDialog.showText(this, getTitle(), dat, false);
		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
