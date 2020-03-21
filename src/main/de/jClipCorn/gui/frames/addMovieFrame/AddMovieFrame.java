package de.jClipCorn.gui.frames.addMovieFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseOnlineFrame.ParseOnlineDialog;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.ActionLambdaAdapter;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.LookAndFeelManager;
import de.jClipCorn.util.parser.FilenameParser;
import de.jClipCorn.util.parser.FilenameParserResult;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class AddMovieFrame extends JFrame implements ParseResultHandler, UserDataProblemHandler {
	private static final long serialVersionUID = -5912378114066741528L;
	
	private boolean firstChooseClick = true;
	
	private final JFileChooser videoFileChooser;
	
	private CCMovieList movieList;
	
	private Boolean forceViewed = null;
	private CCDateTimeList forceViewedHistory = null;
	
	private JPanel contentPane;
	private ReadableTextField ed_Part0;
	private ReadableTextField ed_Part1;
	private ReadableTextField ed_Part2;
	private ReadableTextField ed_Part3;
	private ReadableTextField ed_Part4;
	private ReadableTextField ed_Part5;
	private JTextField edZyklus;
	private JButton btnClear5;
	private JButton btnClear4;
	private JButton btnClear3;
	private JButton btnClear2;
	private JButton btnClear1;
	private JButton btnChoose0;
	private JButton btnChoose1;
	private JButton btnChoose2;
	private JButton btnChoose3;
	private JButton btnChoose4;
	private JButton btnChoose5;
	private CCEnumComboBox<CCGenre> cbxGenre0;
	private CCEnumComboBox<CCGenre> cbxGenre1;
	private CCEnumComboBox<CCGenre> cbxGenre2;
	private CCEnumComboBox<CCGenre> cbxGenre3;
	private CCEnumComboBox<CCGenre> cbxGenre7;
	private CCEnumComboBox<CCGenre> cbxGenre4;
	private CCEnumComboBox<CCGenre> cbxGenre5;
	private CCEnumComboBox<CCGenre> cbxGenre6;
	private LanguageChooser cbxLanguage;
	private JSpinner spnLength;
	private JCCDateSpinner spnAddDate;
	private JSpinner spnOnlineScore;
	private CCEnumComboBox<CCOptionalFSK> cbxFSK;
	private CCEnumComboBox<CCFileFormat> cbxFormat;
	private JSpinner spnYear;
	private JSpinner spnSize;
	private JButton btnOK;
	private JButton btnCancel;
	private JTextField edTitle;
	private JSpinner spnZyklus;
	private JButton btnParseIMDB;
	private CCEnumComboBox<CCUserScore> cbxScore;
	private JLabel lblScore;
	private JLabel lblPart;
	private JLabel lblPart_1;
	private JLabel lblPart_2;
	private JLabel lblPart_3;
	private JLabel lblPart_4;
	private JLabel lblPart_5;
	private JLabel lblZyklus;
	private JLabel lblGenre;
	private JLabel lblGenre_1;
	private JLabel lblGenre_2;
	private JLabel lblGenre_3;
	private JLabel lblGenre_7;
	private JLabel lblGenre_6;
	private JLabel lblGenre_5;
	private JLabel lblGenre_4;
	private JLabel lblMediaInfo;
	private JLabel lblLanguage;
	private JLabel lblLength;
	private JLabel lblMin;
	private JLabel lblEinfgDatum;
	private JLabel lblOnlinescore;
	private JLabel label;
	private JLabel lblFsk;
	private JLabel lblFormat;
	private JLabel lblYear;
	private JLabel lblGre;
	private JLabel lblFileSizeDisp;
	private JLabel label_1;
	private EditCoverControl edCvrControl;
	private JReferenceChooser edReference;
	private JLabel lblOnlineid;
	private JLabel lblGroups;
	private GroupListEditor edGroups;
	private JButton btnMediaInfo;
	private JButton btnMediaInfo2;
	private JLabel lblLenAuto;
	private JProgressBar pbLanguageLoad;
	private JMediaInfoControl ctrlMediaInfo;

	private volatile boolean _isDirtyLanguage = false;
	private volatile boolean _isDirtyMediaInfo = false;
	private JButton btnQueryMediaInfo;
	private JPanel pnlBase;
	private JPanel pnlLeft;
	private JPanel pnlRight;
	private JPanel pnlBot;
	private JPanel pnlPaths;
	private JPanel pnlData;

	/**
	 * @wbp.parser.constructor
	 */
	public AddMovieFrame(Component owner, CCMovieList mlist) {		
		super();
		this.movieList = mlist;
		this.videoFileChooser = new JFileChooser(mlist.getCommonPathForMovieFileChooser());
		
		init(owner);
	}
	
	public AddMovieFrame(Component owner, CCMovieList mlist, String firstPath) {		
		super();
		this.movieList = mlist;
		this.videoFileChooser = new JFileChooser(mlist.getCommonPathForMovieFileChooser());
		
		init(owner);
		
		setFilepath(0, firstPath);
		setEnabledAll(true);
		parseFromFile(firstPath);
		firstChooseClick = false;	
		updateFilesize();
	}

	private void init(Component owner) {
		initGUI();
		initFileChooser();
		setDefaultValues();
		
		setEnabledAll(false);
		btnChoose0.setEnabled(true);

		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("AddMovieFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 750, LookAndFeelManager.isWindows() ? 675 : 750);
		setMinimumSize(new Dimension(650, 660));
		setResizable(true);
		contentPane = new JPanel();
		contentPane.setFocusable(false);
		contentPane.setBorder(null);
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		pnlBase = new JPanel();
		pnlBase.setBorder(null);
		contentPane.add(pnlBase);
		pnlBase.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				ColumnSpec.decode("20dlu"), //$NON-NLS-1$
				ColumnSpec.decode("135dlu"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,}));
		
		pnlLeft = new JPanel();
		pnlBase.add(pnlLeft, "1, 1, fill, fill"); //$NON-NLS-1$
		pnlLeft.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.PREF_ROWSPEC,
				RowSpec.decode("16dlu"), //$NON-NLS-1$
				RowSpec.decode("1dlu:grow"),})); //$NON-NLS-1$
		
		pnlPaths = new JPanel();
		pnlLeft.add(pnlPaths, "1, 1, fill, fill"); //$NON-NLS-1$
		pnlPaths.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
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
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblPart = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart.text")); //$NON-NLS-1$
		pnlPaths.add(lblPart, "2, 2"); //$NON-NLS-1$
		
		ed_Part0 = new ReadableTextField();
		pnlPaths.add(ed_Part0, "4, 2, 3, 1"); //$NON-NLS-1$
		ed_Part0.setColumns(10);
				
		btnChoose0 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose0, "8, 2"); //$NON-NLS-1$
		btnChoose0.addActionListener(arg0 -> onBtnChooseClicked(0));
		
		lblPart_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_1.text")); //$NON-NLS-1$
		pnlPaths.add(lblPart_1, "2, 4"); //$NON-NLS-1$
		
		ed_Part1 = new ReadableTextField();
		pnlPaths.add(ed_Part1, "4, 4"); //$NON-NLS-1$
		ed_Part1.setColumns(10);
		
		btnChoose1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose1, "6, 4"); //$NON-NLS-1$
		btnChoose1.addActionListener(e -> onBtnChooseClicked(1));
		
		btnClear1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear1, "8, 4"); //$NON-NLS-1$
		btnClear1.addActionListener(e -> onBtnClearClicked(1));
		
		lblPart_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_2.text")); //$NON-NLS-1$
		pnlPaths.add(lblPart_2, "2, 6"); //$NON-NLS-1$
		
		ed_Part2 = new ReadableTextField();
		pnlPaths.add(ed_Part2, "4, 6"); //$NON-NLS-1$
		ed_Part2.setColumns(10);
		
		btnChoose2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose2, "6, 6"); //$NON-NLS-1$
		btnChoose2.addActionListener(e -> onBtnChooseClicked(2));
		
		btnClear2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear2, "8, 6"); //$NON-NLS-1$
		btnClear2.addActionListener(e -> onBtnClearClicked(2));
		
		lblPart_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_3.text")); //$NON-NLS-1$
		pnlPaths.add(lblPart_3, "2, 8"); //$NON-NLS-1$
		
		ed_Part3 = new ReadableTextField();
		pnlPaths.add(ed_Part3, "4, 8"); //$NON-NLS-1$
		ed_Part3.setColumns(10);
		
		btnChoose3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose3, "6, 8"); //$NON-NLS-1$
		btnChoose3.addActionListener(e -> onBtnChooseClicked(3));
		
		btnClear3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear3, "8, 8"); //$NON-NLS-1$
		btnClear3.addActionListener(e -> onBtnClearClicked(3));
		
		lblPart_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_4.text")); //$NON-NLS-1$
		pnlPaths.add(lblPart_4, "2, 10"); //$NON-NLS-1$
		
		ed_Part4 = new ReadableTextField();
		pnlPaths.add(ed_Part4, "4, 10"); //$NON-NLS-1$
		ed_Part4.setColumns(10);
		
		btnChoose4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose4, "6, 10"); //$NON-NLS-1$
		btnChoose4.addActionListener(e -> onBtnChooseClicked(4));
		
		btnClear4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear4, "8, 10"); //$NON-NLS-1$
		btnClear4.addActionListener(e -> onBtnClearClicked(4));
		
		lblPart_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_5.text")); //$NON-NLS-1$
		pnlPaths.add(lblPart_5, "2, 12"); //$NON-NLS-1$
		
		ed_Part5 = new ReadableTextField();
		pnlPaths.add(ed_Part5, "4, 12"); //$NON-NLS-1$
		ed_Part5.setColumns(10);
		
		btnChoose5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose5, "6, 12"); //$NON-NLS-1$
		
		btnClear5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear5, "8, 12"); //$NON-NLS-1$
		btnClear5.addActionListener(e -> onBtnClearClicked(5));
		btnChoose5.addActionListener(e -> onBtnChooseClicked(5));
		
		pnlData = new JPanel();
		pnlLeft.add(pnlData, "1, 3, fill, fill"); //$NON-NLS-1$
		pnlData.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("25dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("30dlu"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"),})); //$NON-NLS-1$
		
		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		pnlData.add(label_1, "2, 2"); //$NON-NLS-1$
		
		edTitle = new JTextField();
		pnlData.add(edTitle, "4, 2, 3, 1, fill, center"); //$NON-NLS-1$
		edTitle.setColumns(10);
		
		lblOnlineid = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
		pnlData.add(lblOnlineid, "2, 4"); //$NON-NLS-1$
		
		edReference = new JReferenceChooser();
		pnlData.add(edReference, "4, 4, 3, 1, fill, center"); //$NON-NLS-1$
		
		lblZyklus = new JLabel(LocaleBundle.getString("AddMovieFrame.lblZyklus.text")); //$NON-NLS-1$
		pnlData.add(lblZyklus, "2, 6"); //$NON-NLS-1$
		
		edZyklus = new JTextField();
		pnlData.add(edZyklus, "4, 6, fill, center"); //$NON-NLS-1$
		edZyklus.setColumns(10);
		
		spnZyklus = new JSpinner();
		pnlData.add(spnZyklus, "6, 6, fill, center"); //$NON-NLS-1$
		spnZyklus.setModel(new SpinnerNumberModel(-1, -1, 255, 1));
		
		lblGroups = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
		pnlData.add(lblGroups, "2, 8"); //$NON-NLS-1$
		
		edGroups = new GroupListEditor(movieList);
		pnlData.add(edGroups, "4, 8, 3, 1, fill, center"); //$NON-NLS-1$
		edGroups.setEnabled(false);
		
		lblLength = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		pnlData.add(lblLength, "2, 10"); //$NON-NLS-1$
		
		spnLength = new JSpinner();
		pnlData.add(spnLength, "4, 10, 3, 1, fill, center"); //$NON-NLS-1$
		spnLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
		
		lblMin = new JLabel("min."); //$NON-NLS-1$
		pnlData.add(lblMin, "8, 10"); //$NON-NLS-1$
		
		lblLenAuto = new JLabel(""); //$NON-NLS-1$
		pnlData.add(lblLenAuto, "10, 10, 3, 1, fill, fill"); //$NON-NLS-1$
		
		lblLanguage = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		pnlData.add(lblLanguage, "2, 12"); //$NON-NLS-1$
		
		cbxLanguage = new LanguageChooser();
		pnlData.add(cbxLanguage, "4, 12, 3, 1, fill, center"); //$NON-NLS-1$
		cbxLanguage.addChangeListener(new ActionLambdaAdapter(() -> { _isDirtyLanguage = true; }));
		
		btnMediaInfo = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		pnlData.add(btnMediaInfo, "8, 12, fill, default"); //$NON-NLS-1$
		btnMediaInfo.addActionListener(e -> parseCodecMetadata());
		btnMediaInfo.setToolTipText("MediaInfo"); //$NON-NLS-1$
		
		btnMediaInfo2 = new JButton("..."); //$NON-NLS-1$
		pnlData.add(btnMediaInfo2, "10, 12"); //$NON-NLS-1$
		btnMediaInfo2.addActionListener(e -> showCodecMetadata());
		btnMediaInfo2.setToolTipText("MediaInfo"); //$NON-NLS-1$
		btnMediaInfo2.addActionListener(e -> getMediaInfo());
		
		lblMediaInfo = new JLabel("MediaInfo"); //$NON-NLS-1$
		pnlData.add(lblMediaInfo, "2, 14"); //$NON-NLS-1$
		
		ctrlMediaInfo = new JMediaInfoControl(() -> Str.isNullOrWhitespace(ed_Part0.getText()) ? null : PathFormatter.fromCCPath(ed_Part0.getText()));
		pnlData.add(ctrlMediaInfo, "4, 14, 3, 1"); //$NON-NLS-1$
		ctrlMediaInfo.addChangeListener(new ActionLambdaAdapter(() -> { _isDirtyMediaInfo = true; }));
		
		btnQueryMediaInfo = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		pnlData.add(btnQueryMediaInfo, "8, 14, fill, fill"); //$NON-NLS-1$
		btnQueryMediaInfo.setToolTipText("MediaInfo"); //$NON-NLS-1$
		btnQueryMediaInfo.setEnabled(false);
		
		pbLanguageLoad = new JProgressBar();
		pnlData.add(pbLanguageLoad, "10, 14, fill, center"); //$NON-NLS-1$
		pbLanguageLoad.setVisible(false);
		pbLanguageLoad.setIndeterminate(true);
		
		lblEinfgDatum = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		pnlData.add(lblEinfgDatum, "2, 16"); //$NON-NLS-1$
		
		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), CCDate.getMinimumDate(), null);
		pnlData.add(spnAddDate, "4, 16, 3, 1, fill, center"); //$NON-NLS-1$
		
		lblOnlinescore = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		pnlData.add(lblOnlinescore, "2, 18"); //$NON-NLS-1$
		
		spnOnlineScore = new JSpinner();
		pnlData.add(spnOnlineScore, "4, 18, 3, 1, fill, center"); //$NON-NLS-1$
		spnOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		
		label = new JLabel("/ 10"); //$NON-NLS-1$
		pnlData.add(label, "8, 18, 5, 1"); //$NON-NLS-1$
		
		lblFsk = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		pnlData.add(lblFsk, "2, 20"); //$NON-NLS-1$
		
		cbxFSK = new CCEnumComboBox<>(CCOptionalFSK.getWrapper());
		pnlData.add(cbxFSK, "4, 20, 3, 1, fill, center"); //$NON-NLS-1$
		
		lblFormat = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		pnlData.add(lblFormat, "2, 22"); //$NON-NLS-1$
		
		cbxFormat = new CCEnumComboBox<>(CCFileFormat.getWrapper());
		pnlData.add(cbxFormat, "4, 22, 3, 1, fill, center"); //$NON-NLS-1$
		
		lblYear = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		pnlData.add(lblYear, "2, 24"); //$NON-NLS-1$
		
		spnYear = new JSpinner();
		pnlData.add(spnYear, "4, 24, 3, 1, fill, center"); //$NON-NLS-1$
		spnYear.setModel(new SpinnerNumberModel(1900, 1900, null, 1));
		spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "0")); //$NON-NLS-1$
		
		lblGre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		pnlData.add(lblGre, "2, 26"); //$NON-NLS-1$
		
		spnSize = new JSpinner();
		pnlData.add(spnSize, "4, 26, 3, 1, fill, center"); //$NON-NLS-1$
		spnSize.addChangeListener(arg0 -> updateByteDisp());
		spnSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
		
		lblFileSizeDisp = new JLabel("Byte = "); //$NON-NLS-1$
		pnlData.add(lblFileSizeDisp, "8, 26, 5, 1"); //$NON-NLS-1$
		
		lblScore = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
		pnlData.add(lblScore, "2, 28"); //$NON-NLS-1$
		
		cbxScore = new CCEnumComboBox<>(CCUserScore.getWrapper());
		pnlData.add(cbxScore, "4, 28, 3, 1, fill, center"); //$NON-NLS-1$
		
		pnlRight = new JPanel();
		pnlBase.add(pnlRight, "3, 1, fill, fill"); //$NON-NLS-1$
		pnlRight.setLayout(new FormLayout(new ColumnSpec[] {
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
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("40dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		lblGenre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		pnlRight.add(lblGenre, "1, 2"); //$NON-NLS-1$
		
		cbxGenre0 = new CCEnumComboBox<>(CCGenre.getWrapper());
		pnlRight.add(cbxGenre0, "3, 2"); //$NON-NLS-1$
		
		lblGenre_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		pnlRight.add(lblGenre_1, "1, 4"); //$NON-NLS-1$
		
		cbxGenre1 = new CCEnumComboBox<>(CCGenre.getWrapper());
		pnlRight.add(cbxGenre1, "3, 4"); //$NON-NLS-1$
		
		lblGenre_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		pnlRight.add(lblGenre_2, "1, 6"); //$NON-NLS-1$
		
		cbxGenre2 = new CCEnumComboBox<>(CCGenre.getWrapper());
		pnlRight.add(cbxGenre2, "3, 6"); //$NON-NLS-1$
		
		lblGenre_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		pnlRight.add(lblGenre_3, "1, 8"); //$NON-NLS-1$
		
		cbxGenre3 = new CCEnumComboBox<>(CCGenre.getWrapper());
		pnlRight.add(cbxGenre3, "3, 8"); //$NON-NLS-1$
		
		lblGenre_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		pnlRight.add(lblGenre_4, "1, 10"); //$NON-NLS-1$
		
		cbxGenre4 = new CCEnumComboBox<>(CCGenre.getWrapper());
		pnlRight.add(cbxGenre4, "3, 10"); //$NON-NLS-1$
		
		lblGenre_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		pnlRight.add(lblGenre_5, "1, 12"); //$NON-NLS-1$
		
		cbxGenre5 = new CCEnumComboBox<>(CCGenre.getWrapper());
		pnlRight.add(cbxGenre5, "3, 12"); //$NON-NLS-1$
		
		lblGenre_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		pnlRight.add(lblGenre_6, "1, 14"); //$NON-NLS-1$
		
		cbxGenre6 = new CCEnumComboBox<>(CCGenre.getWrapper());
		pnlRight.add(cbxGenre6, "3, 14"); //$NON-NLS-1$
		
		lblGenre_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		pnlRight.add(lblGenre_7, "1, 16"); //$NON-NLS-1$
		
		cbxGenre7 = new CCEnumComboBox<>(CCGenre.getWrapper());
		pnlRight.add(cbxGenre7, "3, 16"); //$NON-NLS-1$
		
		btnParseIMDB = new JButton(LocaleBundle.getString("AddMovieFrame.btnParseIMDB.text")); //$NON-NLS-1$
		pnlRight.add(btnParseIMDB, "1, 18, 3, 1, fill, fill"); //$NON-NLS-1$
		btnParseIMDB.addActionListener(arg0 -> showIMDBParser());
		btnParseIMDB.setFont(new Font("Tahoma", Font.BOLD, 15)); //$NON-NLS-1$
		
		edCvrControl = new EditCoverControl(this, this);
		pnlRight.add(edCvrControl, "1, 20, 3, 1, right, bottom"); //$NON-NLS-1$
		
		pnlBot = new JPanel();
		pnlBase.add(pnlBot, "1, 3, 3, 1, fill, fill"); //$NON-NLS-1$
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		pnlBot.add(btnOK);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		pnlBot.add(btnCancel);
		btnCancel.addActionListener(arg0 -> cancel());
		btnOK.addActionListener(e -> {try {
			onBtnOK(true);
		} catch (EnumFormatException | EnumValueNotFoundException e1) {
			CCLog.addError(e1);
		}});
	}
	
	public void parseFromTemp(CCMovie tmpMov, boolean resetAddDate, boolean resetScore) {
		if (!tmpMov.getAddDate().isMinimum() && !resetAddDate)
			spnAddDate.setValue(tmpMov.getAddDate());
		
		setFilesize(tmpMov.getFilesize());
		setMovieFormat(tmpMov.getFormat());
		setMediaInfo(tmpMov.getMediaInfo());
		setLength(tmpMov.getLength());

		for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
			if (!tmpMov.getPart(i).isEmpty())
				setDirectFilepath(i, tmpMov.getPart(i));
		}
		
		forceViewed = tmpMov.isViewed();
		forceViewedHistory = tmpMov.getViewedHistory();
		
		setYear(tmpMov.getYear());
		setZyklus(tmpMov.getZyklus().getTitle());
		setZyklusNumber(tmpMov.getZyklus().getNumber());
		setMovieName(tmpMov.getTitle());
		setOnlineReference(tmpMov.getOnlineReference());
		setGroups(tmpMov.getGroups());
		setScore(tmpMov.getOnlinescore());
		setMovieLanguage(tmpMov.getLanguage());
		setFSK(tmpMov.getFSK());

		if (! resetScore) cbxScore.setSelectedEnum(tmpMov.getScore());
		
		for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
			setGenre(i, tmpMov.getGenre(i));
		}
		
		setEnabledAll(true);
		firstChooseClick = false;
	}
	
	private void onBtnOK(boolean check) throws EnumFormatException, EnumValueNotFoundException {
		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserData(problems);
		
		// some problems are too fatal
		if (probvalue && ! edCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			probvalue = false;
		}
		if (probvalue && edTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}
		
		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, this, this);
			amied.setVisible(true);
			return;
		}
		
		CCMovie newM = movieList.createNewEmptyMovie();
		
		newM.beginUpdating();
		
		//#####################################################################################
		
		if (forceViewed != null) newM.setViewed(forceViewed);
		
		if (forceViewedHistory != null) newM.setViewedHistoryFromUI(forceViewedHistory);
		
		newM.setPart(0, ed_Part0.getText());
		newM.setPart(1, ed_Part1.getText());
		newM.setPart(2, ed_Part2.getText());
		newM.setPart(3, ed_Part3.getText());
		newM.setPart(4, ed_Part4.getText());
		newM.setPart(5, ed_Part5.getText());
		
		newM.setTitle(edTitle.getText());
		newM.setZyklusTitle(edZyklus.getText());
		newM.setZyklusID((int) spnZyklus.getValue());
		
		newM.setMediaInfo(ctrlMediaInfo.getValue());
		newM.setLanguage(cbxLanguage.getValue());
		
		newM.setLength((int) spnLength.getValue());
		
		newM.setAddDate(spnAddDate.getValue());
		
		newM.setOnlinescore((int) spnOnlineScore.getValue());
		
		newM.setFsk(cbxFSK.getSelectedEnum().asFSK());
		newM.setFormat(cbxFormat.getSelectedEnum());
		
		newM.setYear((int) spnYear.getValue());
		newM.setFilesize((long) spnSize.getValue());
		
		newM.setGenre(cbxGenre0.getSelectedEnum(), 0);
		newM.setGenre(cbxGenre1.getSelectedEnum(), 1);
		newM.setGenre(cbxGenre2.getSelectedEnum(), 2);
		newM.setGenre(cbxGenre3.getSelectedEnum(), 3);
		newM.setGenre(cbxGenre4.getSelectedEnum(), 4);
		newM.setGenre(cbxGenre5.getSelectedEnum(), 5);
		newM.setGenre(cbxGenre6.getSelectedEnum(), 6);
		newM.setGenre(cbxGenre7.getSelectedEnum(), 7);
		
		newM.setScore(cbxScore.getSelectedEnum());
		newM.setOnlineReference(edReference.getValue());
		newM.setGroups(edGroups.getValue());

		newM.setCover(edCvrControl.getResizedImageForStorage());
		
		newM.endUpdating();
		
		dispose();
	}
	
	private void cancel() {
		this.dispose();
	}
	
	private void showIMDBParser() {
		(new ParseOnlineDialog(this, this, CCDBElementTyp.MOVIE)).setVisible(true);
	}
	
	private void initFileChooser() {
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$
		
		videoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$
	}

	private void setDefaultValues() {
		cbxScore.setSelectedEnum(CCUserScore.RATING_NO);
		
		cbxFSK.setSelectedEnum(CCOptionalFSK.NULL);

		spnAddDate.setValue(CCDate.getCurrentDate());
		spnZyklus.setValue(-1);
		
		edReference.setValue(CCOnlineReferenceList.EMPTY);
		
		updateByteDisp();
	}
	
	private void setEnabledAll(boolean e) {
		edZyklus.setEnabled(e);
		btnParseIMDB.setEnabled(e);
		btnClear5.setEnabled(e);
		btnClear4.setEnabled(e);
		btnClear3.setEnabled(e);
		btnClear2.setEnabled(e);
		btnClear1.setEnabled(e);
		btnChoose0.setEnabled(e);
		btnChoose1.setEnabled(e);
		btnChoose2.setEnabled(e);
		btnChoose3.setEnabled(e);
		btnChoose4.setEnabled(e);
		btnChoose5.setEnabled(e);
		cbxGenre0.setEnabled(e);
		cbxGenre1.setEnabled(e);
		cbxGenre2.setEnabled(e);
		cbxGenre3.setEnabled(e);
		cbxGenre7.setEnabled(e);
		cbxGenre4.setEnabled(e);
		cbxGenre5.setEnabled(e);
		cbxGenre6.setEnabled(e);
		edCvrControl.setEnabled(e);
		cbxLanguage.setReadOnly(!e);
		spnLength.setEnabled(e);
		spnAddDate.setEnabled(e);
		spnOnlineScore.setEnabled(e);
		cbxFSK.setEnabled(e);
		cbxFormat.setEnabled(e);
		spnYear.setEnabled(e);
		spnSize.setEnabled(e);
		btnOK.setEnabled(e);
		btnCancel.setEnabled(e);
		edTitle.setEnabled(e);
		spnZyklus.setEnabled(e);
		cbxScore.setEnabled(e);
		edReference.setEnabled(e);
		edGroups.setEnabled(e);
		btnMediaInfo.setEnabled(e);
		btnMediaInfo2.setEnabled(e);
		ctrlMediaInfo.setEnabled(e);
		btnQueryMediaInfo.setEnabled(e);
	}
	
	private void onBtnChooseClicked(int cNmbr) {
		int returnval = videoFileChooser.showOpenDialog(this);
		
		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		setFilepath(cNmbr, videoFileChooser.getSelectedFile().getAbsolutePath());
		
		if (firstChooseClick) {
			setEnabledAll(true);
			parseFromFile(videoFileChooser.getSelectedFile().getAbsolutePath());
			
			firstChooseClick = false;

			updateFilesize();
		}
		else
		{
			updateFilesize();

			runMediaInfoInBackground();
		}
	}
	
	private void parseFromFile(String fp) {
		FilenameParserResult r = FilenameParser.parse(movieList, fp);
		
		if (r == null) return;
		
		if (r.Title != null) setMovieName(r.Title);
		if (r.Zyklus != null) setZyklus(r.Zyklus.getTitle());
		if (r.Zyklus != null) setZyklusNumber(r.Zyklus.getNumber());
		
		if (r.Language != null) setMovieLanguage(r.Language);
		if (r.Format != null) setMovieFormat(r.Format);

		if (r.Groups != null) setGroupList(r.Groups);
		
		if (r.AdditionalFiles != null) 
			for (Entry<Integer, String> addFile : r.AdditionalFiles.entrySet())
				setFilepath(addFile.getKey()-1, addFile.getValue());

		runMediaInfoInBackground();
	}
	
	private void onBtnClearClicked(int cNmbr) {
		switch (cNmbr) {
		case 0:
			ed_Part0.setText(""); //$NON-NLS-1$
			break;
		case 1:
			ed_Part1.setText(""); //$NON-NLS-1$
			break;
		case 2:
			ed_Part2.setText(""); //$NON-NLS-1$
			break;
		case 3:
			ed_Part3.setText(""); //$NON-NLS-1$
			break;
		case 4:
			ed_Part4.setText(""); //$NON-NLS-1$
			break;
		case 5:
			ed_Part5.setText(""); //$NON-NLS-1$
			break;
		}
		
		updateFilesize();
	}
	
	@Override
	public void setMovieFormat(CCFileFormat cmf) {
		cbxFormat.setSelectedEnum(cmf);
	}

	public void setGroupList(CCGroupList gl) {
		edGroups.setValue(gl);
	}

	@Override
	public void setFilepath(int p, String t) {
		t = PathFormatter.getCCPath(t);
				
		setDirectFilepath(p, t);
		
		updateFilesize();
	}
	
	private void setDirectFilepath(int p, String pt) {
		switch (p) {
		case 0:
			ed_Part0.setText(pt);
			ed_Part0.setCaretPosition(0);
			break;
		case 1:
			ed_Part1.setText(pt);
			ed_Part1.setCaretPosition(0);
			break;
		case 2:
			ed_Part2.setText(pt);
			ed_Part2.setCaretPosition(0);
			break;
		case 3:
			ed_Part3.setText(pt);
			ed_Part3.setCaretPosition(0);
			break;
		case 4:
			ed_Part4.setText(pt);
			ed_Part4.setCaretPosition(0);
			break;
		case 5:
			ed_Part5.setText(pt);
			ed_Part5.setCaretPosition(0);
			break;
		}
	}

	@Override
	public void setMovieName(String name) {
		edTitle.setText(name);
	}

	@Override
	public void setZyklus(String mZyklusTitle) {
		edZyklus.setText(mZyklusTitle);
	}

	@Override
	public void setZyklusNumber(int iRoman) {
		spnZyklus.setValue(iRoman);
	}

	@Override
	public void setFilesize(CCFileSize size) {
		spnSize.setValue(size.getBytes());
	}

	public void setMovieLanguage(CCDBLanguageList lang) {
		cbxLanguage.setValue(lang);
	}

	public void setMediaInfo(CCMediaInfo mi) {
		ctrlMediaInfo.setValue(mi);
	}
	
	@Override
	public void setYear(int y) {
		spnYear.setValue(y);
	}
	
	@Override
	public void setGenre(int gid, CCGenre movGenre) {
		switch (gid) {
		case 0: cbxGenre0.setSelectedEnum(movGenre); break;
		case 1: cbxGenre1.setSelectedEnum(movGenre); break;
		case 2: cbxGenre2.setSelectedEnum(movGenre); break;
		case 3: cbxGenre3.setSelectedEnum(movGenre); break;
		case 4: cbxGenre4.setSelectedEnum(movGenre); break;
		case 5: cbxGenre5.setSelectedEnum(movGenre); break;
		case 6: cbxGenre6.setSelectedEnum(movGenre); break;
		case 7: cbxGenre7.setSelectedEnum(movGenre); break;
		}
	}
	
	@Override
	public void setFSK(CCFSK fsk) {
		cbxFSK.setSelectedEnum(fsk.asOptionalFSK());
	}
	
	@Override
	public void setLength(int l) {
		spnLength.setValue(l);
	}
	
	@Override
	public void setScore(CCOnlineScore s) {
		spnOnlineScore.setValue(s.asInt());
	}
	
	@Override
	public void onFinishInserting() {
		//
	}
	
	private void updateByteDisp() {
		lblFileSizeDisp.setText("Byte = " + FileSizeFormatter.format((long) spnSize.getValue())); //$NON-NLS-1$
	}
	
	private String getMovieTitle() {
		return edTitle.getText();
	}
	
	private String getMovieZyklus() {
		return edZyklus.getText();
	}

	@Override
	public String getFullTitle() {
		if (getMovieZyklus().isEmpty()) {
			return edTitle.getText();
		} else {
			return edZyklus.getText() + " - " + getMovieTitle(); //$NON-NLS-1$
		}
	}

	@Override
	public String getTitleForParser() {
		return edTitle.getText();
	}

	@Override
	public CCOnlineReferenceList getSearchReference() {
		return edReference.getValue();
	}
	
	private void updateFilesize() {
		CCFileSize size = CCFileSize.ZERO;
		
		if (! ed_Part0.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(ed_Part0.getText())));
		}
		
		if (! ed_Part1.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(ed_Part1.getText())));
		}
		
		if (! ed_Part2.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(ed_Part3.getText())));
		}
		
		if (! ed_Part3.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(ed_Part3.getText())));
		}
		
		if (! ed_Part4.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(ed_Part4.getText())));
		}
		
		if (! ed_Part5.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(ed_Part5.getText())));
		}
		
		setFilesize(size);
	}
	
	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
	}
	
	@Override
	public void setOnlineReference(CCOnlineReferenceList ref) {
		edReference.setValue(ref);
	}
	
	public void setGroups(CCGroupList gl) {
		edGroups.setValue(gl);
	}
	
	public boolean checkUserData(List<UserDataProblem> ret) { 
		BufferedImage i = edCvrControl.getResizedImageForStorage();
		
		String p0 = ed_Part0.getText();
		String p1 = ed_Part1.getText();
		String p2 = ed_Part2.getText();
		String p3 = ed_Part3.getText();
		String p4 = ed_Part4.getText();
		String p5 = ed_Part5.getText();
		
		String title = edTitle.getText();
		String zyklus = edZyklus.getText();
		int zyklusID = (int) spnZyklus.getValue();
		
		int len = (int) spnLength.getValue();
		CCDate adddate = spnAddDate.getValue();
		int oscore = (int) spnOnlineScore.getValue();
		
		try {
			int fskidx = cbxFSK.getSelectedEnum().asInt();
			int year = (int) spnYear.getValue();
			long fsize = (long) spnSize.getValue();
			CCMediaInfo minfo = ctrlMediaInfo.getValue();
			CCDBLanguageList lang = cbxLanguage.getValue();
			String csExtn = cbxFormat.getSelectedEnum().asString();
			String csExta = cbxFormat.getSelectedEnum().asStringAlt();
			
			int g0 = cbxGenre0.getSelectedEnum().asInt();
			int g1 = cbxGenre1.getSelectedEnum().asInt();
			int g2 = cbxGenre2.getSelectedEnum().asInt();
			int g3 = cbxGenre3.getSelectedEnum().asInt();
			int g4 = cbxGenre4.getSelectedEnum().asInt();
			int g5 = cbxGenre5.getSelectedEnum().asInt();
			int g6 = cbxGenre6.getSelectedEnum().asInt();
			int g7 = cbxGenre7.getSelectedEnum().asInt();
			
			CCOnlineReferenceList ref = edReference.getValue();
			
			UserDataProblem.testMovieData(ret, null, i, movieList, p0, p1, p2, p3, p4, p5, title, zyklus, zyklusID, len, adddate, oscore, fskidx, year, fsize, csExtn, csExta, g0, g1, g2, g3, g4, g5, g6, g7, minfo, lang, ref);
		
			return ret.isEmpty();
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void onAMIEDIgnoreClicked() {
		try {
			onBtnOK(false);
		} catch (EnumFormatException | EnumValueNotFoundException e) {
			CCLog.addError(e);
		}
	}

	private void parseCodecMetadata() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<MediaQueryResult> dat = new ArrayList<>();

			if (!Str.isNullOrWhitespace(ed_Part0.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part0.getText()), false));
			if (!Str.isNullOrWhitespace(ed_Part1.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part1.getText()), false));
			if (!Str.isNullOrWhitespace(ed_Part2.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part2.getText()), false));
			if (!Str.isNullOrWhitespace(ed_Part3.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part3.getText()), false));
			if (!Str.isNullOrWhitespace(ed_Part4.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part4.getText()), false));
			if (!Str.isNullOrWhitespace(ed_Part5.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part5.getText()), false));

			if (dat.isEmpty()) {
				lblLenAuto.setText(Str.Empty);
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			int dur = (int) (CCStreams.iterate(dat).any(d -> d.Duration == -1) ? -1 : (CCStreams.iterate(dat).sumDouble(d -> d.Duration)/60));
			if (dur != -1) lblLenAuto.setText("("+dur+")"); //$NON-NLS-1$ //$NON-NLS-2$
			if (dur == -1) lblLenAuto.setText(Str.Empty);

			if (CCStreams.iterate(dat).any(d -> d.AudioLanguages == null)) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			CCDBLanguageList dbll = dat.get(0).AudioLanguages;
			for (int i = 1; i < dat.size(); i++) dbll = CCDBLanguageList.intersection(dbll, dat.get(i).AudioLanguages);

			if (dbll.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			} else {
				setMovieLanguage(dbll);
			}

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void getMediaInfo() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<MediaQueryResult> dat = new ArrayList<>();

			if (!Str.isNullOrWhitespace(ed_Part0.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part0.getText()), true));
			if (!Str.isNullOrWhitespace(ed_Part1.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part1.getText()), true));
			if (!Str.isNullOrWhitespace(ed_Part2.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part2.getText()), true));
			if (!Str.isNullOrWhitespace(ed_Part3.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part3.getText()), true));
			if (!Str.isNullOrWhitespace(ed_Part4.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part4.getText()), true));
			if (!Str.isNullOrWhitespace(ed_Part5.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(ed_Part5.getText()), true));

			if (dat.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			ctrlMediaInfo.setValue(dat.get(0));

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void showCodecMetadata() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		StringBuilder b = new StringBuilder();

		try {
			if (!Str.isNullOrWhitespace(ed_Part0.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(ed_Part0.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(ed_Part1.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(ed_Part1.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(ed_Part2.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(ed_Part2.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(ed_Part3.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(ed_Part3.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(ed_Part4.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(ed_Part4.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(ed_Part5.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(ed_Part5.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$

			GenericTextDialog.showText(this, getTitle(), b.toString(), false);
		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void runMediaInfoInBackground() {
		String p0 = ed_Part0.getText();
		String p1 = ed_Part1.getText();
		String p2 = ed_Part2.getText();
		String p3 = ed_Part3.getText();
		String p4 = ed_Part4.getText();
		String p5 = ed_Part5.getText();

		_isDirtyLanguage = false;
		_isDirtyMediaInfo = false;

		new Thread(() -> {

			try	{

				SwingUtilities.invokeLater(() -> pbLanguageLoad.setVisible(true));

				MediaQueryResult dat0 = null;
				if (!Str.isNullOrWhitespace(p0)) dat0 = MediaQueryRunner.query(PathFormatter.fromCCPath(p0), true);

				final MediaQueryResult _fdat0 = dat0;
				if (dat0 != null && !_isDirtyMediaInfo) SwingUtilities.invokeLater(() -> ctrlMediaInfo.setValue(_fdat0));

				List<MediaQueryResult> dat = new ArrayList<>();

				if (!Str.isNullOrWhitespace(p0)) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(p0), false));
				if (!Str.isNullOrWhitespace(p1)) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(p1), false));
				if (!Str.isNullOrWhitespace(p2)) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(p2), false));
				if (!Str.isNullOrWhitespace(p3)) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(p3), false));
				if (!Str.isNullOrWhitespace(p4)) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(p4), false));
				if (!Str.isNullOrWhitespace(p5)) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(p5), false));

				if (dat.isEmpty()) return;

				int dur = (int) (CCStreams.iterate(dat).any(d -> d.Duration == -1) ? -1 : (CCStreams.iterate(dat).sumDouble(d -> d.Duration)/60));
				if (dur != -1) SwingUtilities.invokeLater(() -> lblLenAuto.setText("("+dur+")")); //$NON-NLS-1$ //$NON-NLS-2$
				if (dur == -1) SwingUtilities.invokeLater(() -> lblLenAuto.setText(Str.Empty));

				if (CCStreams.iterate(dat).any(d -> d.AudioLanguages == null)) return;

				CCDBLanguageList dbll = dat.get(0).AudioLanguages;
				for (int i = 1; i < dat.size(); i++) dbll = CCDBLanguageList.intersection(dbll, dat.get(i).AudioLanguages);

				final CCDBLanguageList dbll2 = dbll;
				if (!dbll.isEmpty() && !_isDirtyLanguage) SwingUtilities.invokeLater(() -> setMovieLanguage(dbll2) );

			} catch (Exception e) {

				CCLog.addWarning(e);
				SwingUtilities.invokeLater(() ->
				{
					lblLenAuto.setText("<html><font color='red'>!!!</font></html>"); //$NON-NLS-1$
				});

			} finally {

				SwingUtilities.invokeLater(() -> pbLanguageLoad.setVisible(false));

			}
		}, "MINFO_QUERY").start(); //$NON-NLS-1$
	}
}
