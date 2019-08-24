package de.jClipCorn.gui.frames.editMovieFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
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
import de.jClipCorn.util.adapter.*;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.MediaQueryException;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.ImageCropperResultListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.util.mediaquery.MediaQueryResult;
import de.jClipCorn.util.mediaquery.MediaQueryRunner;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import java.awt.BorderLayout;

public class EditMovieFrame extends JFrame implements ParseResultHandler, UserDataProblemHandler, ImageCropperResultListener {
	private static final long serialVersionUID = 4392838185334567222L;
	
	private static CCDate MIN_DATE = CCDate.getMinimumDate();
	
	private final JFileChooser videoFileChooser;
	
	private final CCMovie movie;
	
	private final UpdateCallbackListener listener;
	
	private ReadableTextField edPart0;
	private JButton btnChoose0;
	private ReadableTextField edPart1;
	private JButton btnChoose1;
	private JButton btnClear1;
	private JLabel label;
	private ReadableTextField edPart2;
	private JButton btnChoose2;
	private JButton btnClear2;
	private ReadableTextField edPart3;
	private JButton btnChoose3;
	private JButton btnClear3;
	private ReadableTextField edPart4;
	private JButton btnChoose4;
	private JButton btnClear4;
	private ReadableTextField edPart5;
	private JButton btnChoose5;
	private JButton btnClear5;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel label_5;
	private JTextField edZyklus;
	private JLabel label_6;
	private JComboBox<String> cbxGenre0;
	private JLabel label_7;
	private JComboBox<String> cbxGenre1;
	private JLabel label_8;
	private JComboBox<String> cbxGenre2;
	private JLabel label_9;
	private JComboBox<String> cbxGenre3;
	private JLabel label_10;
	private JComboBox<String> cbxGenre7;
	private JLabel label_11;
	private JLabel label_12;
	private JLabel label_13;
	private JLabel label_14;
	private JComboBox<String> cbxGenre4;
	private JComboBox<String> cbxGenre5;
	private JComboBox<String> cbxGenre6;
	private JLabel label_16;
	private JLabel label_18;
	private JCheckBox cbViewed;
	private LanguageChooser cbxLanguage;
	private JSpinner spnLength;
	private JLabel label_19;
	private JCCDateSpinner spnAddDate;
	private JLabel label_20;
	private JLabel label_21;
	private JSpinner spnOnlineScore;
	private JLabel label_22;
	private JLabel label_23;
	private JComboBox<String> cbxFSK;
	private JLabel label_24;
	private JComboBox<String> cbxFormat;
	private JLabel label_25;
	private JSpinner spnYear;
	private JLabel label_26;
	private JSpinner spnSize;
	private JLabel label_27;
	private JLabel label_28;
	private JButton btnOK;
	private JButton btnCancel;
	private JTextField edTitle;
	private JLabel label_29;
	private JSpinner spnZyklus;
	private JLabel lblFileSizeDisp;
	private JButton btnRecalculateSize;
	private JComboBox<String> cbxScore;
	private JLabel label_32;
	private TagPanel tagPnl;
	private JLabel label_33;
	private JButton btnToday;
	private JButton btnTestParts;
	private EditCoverControl edCvrControl;
	private JLabel label_15;
	private JReferenceChooser edReference;
	private DateTimeListEditor edViewedHistory;
	private JLabel label_30;
	private JLabel lblGruppen;
	private GroupListEditor edGroups;
	private JButton btnMediaInfo1;
	private JButton btnMediaInfoRaw;
	private JButton btnMediaInfo2;

	private boolean _initFinished = false;
	private boolean _isDirty = false;
	private JLabel label_17;
	private JMediaInfoControl ctrlMediaInfo;
	private JButton btnMediaInfo3;
	private JPanel pnlBase;
	private JPanel pnlLeft;
	private JPanel pnlRight;
	private JPanel pnlBot;
	private JPanel pnlPaths;
	private JPanel pnlData;

	public EditMovieFrame(Component owner, CCMovie movie, UpdateCallbackListener ucl) {
		super();
		setMinimumSize(new Dimension(650, 830));
		this.movie = movie;
		this.videoFileChooser = new JFileChooser(movie.getMovieList().getCommonPathForMovieFileChooser());

		if (ucl == null)
			this.listener = new UpdateCallbackAdapter();
		else
			this.listener = ucl;

		setSize(new Dimension(775, 830));
		
		initGUI();
		setDefaultValues();
		initFileChooser();
		initFields();

		_initFinished = true;
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getFormattedString("EditMovieFrame.this.title", movie.getCompleteTitle())); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		pnlBase = new JPanel();
		getContentPane().add(pnlBase);
		pnlBase.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("185dlu"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		pnlLeft = new JPanel();
		pnlBase.add(pnlLeft, "1, 1, fill, fill"); //$NON-NLS-1$
		pnlLeft.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("1dlu:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.PREF_ROWSPEC,
				RowSpec.decode("24dlu"), //$NON-NLS-1$
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		pnlPaths = new JPanel();
		pnlLeft.add(pnlPaths, "1, 1, fill, fill"); //$NON-NLS-1$
		pnlPaths.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
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
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		label = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart.text")); //$NON-NLS-1$
		pnlPaths.add(label, "2, 2"); //$NON-NLS-1$
		
		edPart0 = new ReadableTextField();
		pnlPaths.add(edPart0, "4, 2, 3, 1"); //$NON-NLS-1$
		edPart0.setColumns(10);
		edPart0.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::setDirty));
		
		btnChoose0 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose0, "8, 2"); //$NON-NLS-1$
		btnChoose0.addActionListener(e -> onBtnChooseClicked(0));
		
		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_1.text")); //$NON-NLS-1$
		pnlPaths.add(label_1, "2, 4"); //$NON-NLS-1$
		
		edPart1 = new ReadableTextField();
		pnlPaths.add(edPart1, "4, 4"); //$NON-NLS-1$
		edPart1.setColumns(10);
		edPart1.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::setDirty));
		
		btnChoose1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose1, "6, 4"); //$NON-NLS-1$
		btnChoose1.addActionListener(e -> onBtnChooseClicked(1));
		
		btnClear1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear1, "8, 4"); //$NON-NLS-1$
		btnClear1.addActionListener(e -> onBtnClearClicked(1));
		
		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_2.text")); //$NON-NLS-1$
		pnlPaths.add(label_2, "2, 6"); //$NON-NLS-1$
		
		edPart2 = new ReadableTextField();
		pnlPaths.add(edPart2, "4, 6"); //$NON-NLS-1$
		edPart2.setColumns(10);
		edPart2.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::setDirty));
		
		btnChoose2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose2, "6, 6"); //$NON-NLS-1$
		btnChoose2.addActionListener(e -> onBtnChooseClicked(2));
		
		btnClear2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear2, "8, 6"); //$NON-NLS-1$
		btnClear2.addActionListener(e -> onBtnClearClicked(2));
		
		label_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_3.text")); //$NON-NLS-1$
		pnlPaths.add(label_3, "2, 8"); //$NON-NLS-1$
		
		edPart3 = new ReadableTextField();
		pnlPaths.add(edPart3, "4, 8"); //$NON-NLS-1$
		edPart3.setColumns(10);
		edPart3.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::setDirty));
		
		btnChoose3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose3, "6, 8"); //$NON-NLS-1$
		btnChoose3.addActionListener(e -> onBtnChooseClicked(3));
		
		btnClear3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear3, "8, 8"); //$NON-NLS-1$
		btnClear3.addActionListener(e -> onBtnClearClicked(3));
		
		label_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_4.text")); //$NON-NLS-1$
		pnlPaths.add(label_4, "2, 10"); //$NON-NLS-1$
		
		edPart4 = new ReadableTextField();
		pnlPaths.add(edPart4, "4, 10"); //$NON-NLS-1$
		edPart4.setColumns(10);
		edPart4.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::setDirty));
		
		btnChoose4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose4, "6, 10"); //$NON-NLS-1$
		btnChoose4.addActionListener(e -> onBtnChooseClicked(4));
		
		btnClear4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear4, "8, 10"); //$NON-NLS-1$
		btnClear4.addActionListener(e -> onBtnClearClicked(4));
		
		label_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_5.text")); //$NON-NLS-1$
		pnlPaths.add(label_5, "2, 12"); //$NON-NLS-1$
		
		edPart5 = new ReadableTextField();
		pnlPaths.add(edPart5, "4, 12"); //$NON-NLS-1$
		edPart5.setColumns(10);
		
		btnChoose5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlPaths.add(btnChoose5, "6, 12"); //$NON-NLS-1$
		
		btnClear5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		pnlPaths.add(btnClear5, "8, 12"); //$NON-NLS-1$
		
		btnTestParts = new JButton(LocaleBundle.getString("EditMovieFrame.btnTestParts.text")); //$NON-NLS-1$
		pnlPaths.add(btnTestParts, "4, 14, 5, 1, right, default"); //$NON-NLS-1$
		btnTestParts.addActionListener(arg0 -> testPaths());
		btnClear5.addActionListener(e -> onBtnClearClicked(5));
		btnChoose5.addActionListener(e -> onBtnChooseClicked(5));
		edPart5.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::setDirty));
		
		pnlData = new JPanel();
		pnlLeft.add(pnlData, "1, 3, fill, fill"); //$NON-NLS-1$
		pnlData.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
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
				RowSpec.decode("16dlu"), //$NON-NLS-1$
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
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("14dlu"),})); //$NON-NLS-1$
		
		label_29 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		pnlData.add(label_29, "2, 2"); //$NON-NLS-1$
		
		edTitle = new JTextField();
		pnlData.add(edTitle, "4, 2, 3, 1, fill, center"); //$NON-NLS-1$
		edTitle.setColumns(10);
		edTitle.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::setDirty));
		
		label_15 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
		pnlData.add(label_15, "2, 4"); //$NON-NLS-1$
		
		edReference = new JReferenceChooser();
		pnlData.add(edReference, "4, 4, 3, 1, fill, center"); //$NON-NLS-1$
		edReference.addChangeListener(new ActionLambdaAdapter(this::setDirty));
		
		label_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblZyklus.text")); //$NON-NLS-1$
		pnlData.add(label_6, "2, 6"); //$NON-NLS-1$
		
		edZyklus = new JTextField();
		pnlData.add(edZyklus, "4, 6, fill, center"); //$NON-NLS-1$
		edZyklus.setColumns(10);
		edZyklus.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::setDirty));
		
		spnZyklus = new JSpinner();
		pnlData.add(spnZyklus, "6, 6, fill, center"); //$NON-NLS-1$
		spnZyklus.setModel(new SpinnerNumberModel(0, -1, null, 1));
		spnZyklus.addChangeListener(new ChangeLambdaAdapter(this::setDirty));
		
		label_16 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGesehen.text")); //$NON-NLS-1$
		pnlData.add(label_16, "2, 8"); //$NON-NLS-1$
		
		cbViewed = new JCheckBox();
		pnlData.add(cbViewed, "4, 8, 3, 1, fill, center"); //$NON-NLS-1$
		cbViewed.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, -1));
		
		label_17 = new JLabel("MediaInfo"); //$NON-NLS-1$
		pnlData.add(label_17, "2, 10"); //$NON-NLS-1$
		
		ctrlMediaInfo = new JMediaInfoControl(() -> Str.isNullOrWhitespace(edPart0.getText()) ? null : PathFormatter.fromCCPath(edPart0.getText()));
		pnlData.add(ctrlMediaInfo, "4, 10, 3, 1, fill, center"); //$NON-NLS-1$
		
		btnMediaInfo3 = new JButton(Resources.ICN_MENUBAR_UPDATECODECDATA.get16x16());
		pnlData.add(btnMediaInfo3, "8, 10"); //$NON-NLS-1$
		btnMediaInfo3.setToolTipText("MediaInfo"); //$NON-NLS-1$
		btnMediaInfo3.addActionListener(e -> parseCodecMetadata_MI());
		
		label_18 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		pnlData.add(label_18, "2, 12"); //$NON-NLS-1$
		
		cbxLanguage = new LanguageChooser();
		pnlData.add(cbxLanguage, "4, 12, 3, 1, fill, center"); //$NON-NLS-1$
		cbxLanguage.addChangeListener(new ActionLambdaAdapter(this::setDirty));
		
		btnMediaInfo1 = new JButton(Resources.ICN_MENUBAR_UPDATECODECDATA.get16x16());
		pnlData.add(btnMediaInfo1, "8, 12"); //$NON-NLS-1$
		btnMediaInfo1.addActionListener(e -> parseCodecMetadata_Lang());
		btnMediaInfo1.setToolTipText("MediaInfo"); //$NON-NLS-1$
		
		btnMediaInfoRaw = new JButton("..."); //$NON-NLS-1$
		pnlData.add(btnMediaInfoRaw, "10, 12, fill, fill"); //$NON-NLS-1$
		btnMediaInfoRaw.addActionListener(e -> showCodecMetadata());
		btnMediaInfoRaw.setToolTipText("MediaInfo"); //$NON-NLS-1$
		
		label_19 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		pnlData.add(label_19, "2, 14"); //$NON-NLS-1$
		
		spnLength = new JSpinner();
		pnlData.add(spnLength, "4, 14, fill, center"); //$NON-NLS-1$
		spnLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
		spnLength.addChangeListener(new ChangeLambdaAdapter(this::setDirty));
		
		label_20 = new JLabel("min."); //$NON-NLS-1$
		pnlData.add(label_20, "6, 14, fill, fill"); //$NON-NLS-1$
		
		btnMediaInfo2 = new JButton(Resources.ICN_MENUBAR_UPDATECODECDATA.get16x16());
		pnlData.add(btnMediaInfo2, "8, 14"); //$NON-NLS-1$
		btnMediaInfo2.addActionListener(e -> parseCodecMetadata_Len());
		btnMediaInfo2.setToolTipText("MediaInfo"); //$NON-NLS-1$
		
		label_21 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		pnlData.add(label_21, "2, 16"); //$NON-NLS-1$
		
		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), MIN_DATE, null);
		pnlData.add(spnAddDate, "4, 16, 3, 1, fill, center"); //$NON-NLS-1$
		spnAddDate.addChangeListener(new ChangeLambdaAdapter(this::setDirty));
		
		btnToday = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
		pnlData.add(btnToday, "8, 16, 3, 1"); //$NON-NLS-1$
		btnToday.addActionListener(arg0 -> spnAddDate.setValue(CCDate.getCurrentDate()));
		
		label_22 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		pnlData.add(label_22, "2, 18"); //$NON-NLS-1$
		
		spnOnlineScore = new JSpinner();
		pnlData.add(spnOnlineScore, "4, 18, fill, center"); //$NON-NLS-1$
		spnOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnOnlineScore.addChangeListener(new ChangeLambdaAdapter(this::setDirty));
		
		label_23 = new JLabel("/ 10"); //$NON-NLS-1$
		pnlData.add(label_23, "6, 18, fill, fill"); //$NON-NLS-1$
		
		label_24 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		pnlData.add(label_24, "2, 20"); //$NON-NLS-1$
		
		cbxFSK = new JComboBox<>();
		pnlData.add(cbxFSK, "4, 20, 3, 1, fill, center"); //$NON-NLS-1$
		cbxFSK.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_25 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		pnlData.add(label_25, "2, 22"); //$NON-NLS-1$
		
		cbxFormat = new JComboBox<>();
		pnlData.add(cbxFormat, "4, 22, 3, 1, fill, center"); //$NON-NLS-1$
		cbxFormat.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_26 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		pnlData.add(label_26, "2, 24"); //$NON-NLS-1$
		
		spnYear = new JSpinner();
		pnlData.add(spnYear, "4, 24, 3, 1, fill, center"); //$NON-NLS-1$
		spnYear.setModel(new SpinnerNumberModel(1900, 1900, null, 1));
		spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "0")); //$NON-NLS-1$
		spnYear.addChangeListener(new ChangeLambdaAdapter(this::setDirty));
		
		label_27 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		pnlData.add(label_27, "2, 26"); //$NON-NLS-1$
		
		spnSize = new JSpinner();
		pnlData.add(spnSize, "4, 26, fill, center"); //$NON-NLS-1$
		spnSize.addChangeListener(arg0 -> updateByteDisp());
		spnSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
		spnSize.addChangeListener(new ChangeLambdaAdapter(this::setDirty));
		
		label_28 = new JLabel("Byte = "); //$NON-NLS-1$
		pnlData.add(label_28, "6, 26"); //$NON-NLS-1$
		
		lblFileSizeDisp = new JLabel();
		pnlData.add(lblFileSizeDisp, "8, 26, 3, 1, fill, fill"); //$NON-NLS-1$
		
		btnRecalculateSize = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
		pnlData.add(btnRecalculateSize, "4, 28, 3, 1, fill, fill"); //$NON-NLS-1$
		btnRecalculateSize.setToolTipText(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
		btnRecalculateSize.addActionListener(e -> updateFilesize());
		
		label_32 = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
		pnlData.add(label_32, "2, 30"); //$NON-NLS-1$
		
		cbxScore = new JComboBox<>();
		pnlData.add(cbxScore, "4, 30, 3, 1, fill, center"); //$NON-NLS-1$
		cbxScore.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_33 = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
		pnlData.add(label_33, "2, 32"); //$NON-NLS-1$
		
		tagPnl = new TagPanel();
		pnlData.add(tagPnl, "4, 32, 3, 1, fill, center"); //$NON-NLS-1$
		tagPnl.addChangeListener(new ActionLambdaAdapter(this::setDirty));
		
		pnlRight = new JPanel();
		pnlBase.add(pnlRight, "3, 1, fill, fill"); //$NON-NLS-1$
		pnlRight.setLayout(new FormLayout(new ColumnSpec[] {
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
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("90dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		label_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		pnlRight.add(label_7, "2, 2"); //$NON-NLS-1$
		
		cbxGenre0 = new JComboBox<>();
		pnlRight.add(cbxGenre0, "4, 2"); //$NON-NLS-1$
		
		label_8 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		pnlRight.add(label_8, "2, 4"); //$NON-NLS-1$
		
		cbxGenre1 = new JComboBox<>();
		pnlRight.add(cbxGenre1, "4, 4"); //$NON-NLS-1$
		cbxGenre1.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_9 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		pnlRight.add(label_9, "2, 6"); //$NON-NLS-1$
		
		cbxGenre2 = new JComboBox<>();
		pnlRight.add(cbxGenre2, "4, 6"); //$NON-NLS-1$
		cbxGenre2.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_10 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		pnlRight.add(label_10, "2, 8"); //$NON-NLS-1$
		
		cbxGenre3 = new JComboBox<>();
		pnlRight.add(cbxGenre3, "4, 8"); //$NON-NLS-1$
		cbxGenre3.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_14 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		pnlRight.add(label_14, "2, 10"); //$NON-NLS-1$
		
		cbxGenre4 = new JComboBox<>();
		pnlRight.add(cbxGenre4, "4, 10"); //$NON-NLS-1$
		cbxGenre4.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_13 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		pnlRight.add(label_13, "2, 12"); //$NON-NLS-1$
		
		cbxGenre5 = new JComboBox<>();
		pnlRight.add(cbxGenre5, "4, 12"); //$NON-NLS-1$
		cbxGenre5.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_12 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		pnlRight.add(label_12, "2, 14"); //$NON-NLS-1$
		
		cbxGenre6 = new JComboBox<>();
		pnlRight.add(cbxGenre6, "4, 14"); //$NON-NLS-1$
		cbxGenre6.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		label_11 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		pnlRight.add(label_11, "2, 16"); //$NON-NLS-1$
		
		cbxGenre7 = new JComboBox<>();
		pnlRight.add(cbxGenre7, "4, 16"); //$NON-NLS-1$
		cbxGenre7.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		lblGruppen = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
		pnlRight.add(lblGruppen, "2, 18"); //$NON-NLS-1$
		
		edGroups = new GroupListEditor(movie.getMovieList());
		pnlRight.add(edGroups, "4, 18"); //$NON-NLS-1$
		edGroups.addChangeListener(new ActionLambdaAdapter(this::setDirty));
		
		label_30 = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
		pnlRight.add(label_30, "2, 20, default, top"); //$NON-NLS-1$
		
		edViewedHistory = new DateTimeListEditor();
		pnlRight.add(edViewedHistory, "4, 20, fill, fill"); //$NON-NLS-1$
		
		edCvrControl = new EditCoverControl(this, this);
		pnlRight.add(edCvrControl, "4, 22, right, top"); //$NON-NLS-1$
		edCvrControl.addChangeListener(new ActionLambdaAdapter(this::setDirty));
		edViewedHistory.addChangeListener(new ActionLambdaAdapter(this::setDirty));
		cbxGenre0.addItemListener(new ItemChangeLambdaAdapter(this::setDirty, ItemEvent.SELECTED));
		
		pnlBot = new JPanel();
		pnlBase.add(pnlBot, "1, 3, 3, 1, fill, fill"); //$NON-NLS-1$
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		pnlBot.add(btnOK);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		pnlBot.add(btnCancel);
		btnCancel.addActionListener(e -> dispose());
		btnOK.addActionListener(e ->
		{
			try {
				onBtnOK(true);
			} catch (EnumFormatException e1) {
				CCLog.addError(e1);
			}
		});

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (_isDirty) {
					if (DialogHelper.showLocaleYesNoDefaultNo(EditMovieFrame.this, "Dialogs.CloseButDirty")) EditMovieFrame.this.dispose(); //$NON-NLS-1$
				} else {
					EditMovieFrame.this.dispose();
				}

				super.windowClosing(e);
			}
		});
	}

	private void setDefaultValues() {
		cbxFSK.setModel(new DefaultComboBoxModel<>(CCFSK.getWrapper().getList()));
		
		cbxFormat.setModel(new DefaultComboBoxModel<>(CCFileFormat.getWrapper().getList()));
		cbxScore.setModel(new DefaultComboBoxModel<>(CCUserScore.getWrapper().getList()));
		
		cbxGenre0.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre1.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre2.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre3.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre4.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre5.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre6.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre7.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		
		spnAddDate.setValue(CCDate.getCurrentDate());
		spnZyklus.setValue(-1);
		
		edViewedHistory.setValue(CCDateTimeList.createEmpty());
		edReference.setValue(CCOnlineReferenceList.EMPTY);
		edGroups.setValue(CCGroupList.EMPTY);
		
		updateByteDisp();
	}
	
	private void updateByteDisp() {
		lblFileSizeDisp.setText(FileSizeFormatter.format((long) spnSize.getValue()));
	}
	
	private void updateFilesize() {
		CCFileSize size = CCFileSize.ZERO;
		
		if (! edPart0.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart0.getText())));
		}
		
		if (! edPart1.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart1.getText())));
		}
		
		if (! edPart2.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart2.getText())));
		}
		
		if (! edPart3.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart3.getText())));
		}
		
		if (! edPart4.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart4.getText())));
		}
		
		if (! edPart5.getText().isEmpty()) {
			size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart5.getText())));
		}
		
		if (size.getBytes() > 0) {
			setFilesize(size.getBytes());
		} else {
			testPaths();
		}
	}
	
	private void testPaths() {
		Color c1 = UIManager.getColor("TextField.background"); //$NON-NLS-1$
		Color c2 = Color.RED;
		
		if (! edPart0.getText().isEmpty()) {
			edPart0.setBackground(new File(PathFormatter.fromCCPath(edPart0.getText())).exists()?c1:c2); 
		} else {
			edPart0.setBackground(c1);
		}
		
		if (! edPart1.getText().isEmpty()) {
			edPart1.setBackground(new File(PathFormatter.fromCCPath(edPart1.getText())).exists()?c1:c2); 
		} else {
			edPart1.setBackground(c1);
		}
		
		if (! edPart2.getText().isEmpty()) {
			edPart2.setBackground(new File(PathFormatter.fromCCPath(edPart2.getText())).exists()?c1:c2); 
		} else {
			edPart2.setBackground(c1);
		}
		
		if (! edPart3.getText().isEmpty()) {
			edPart3.setBackground(new File(PathFormatter.fromCCPath(edPart3.getText())).exists()?c1:c2); 
		} else {
			edPart3.setBackground(c1);
		}
		
		if (! edPart4.getText().isEmpty()) {
			edPart4.setBackground(new File(PathFormatter.fromCCPath(edPart4.getText())).exists()?c1:c2); 
		} else {
			edPart4.setBackground(c1);
		}
		
		if (! edPart5.getText().isEmpty()) {
			edPart5.setBackground(new File(PathFormatter.fromCCPath(edPart5.getText())).exists()?c1:c2); 
		} else {
			edPart5.setBackground(c1);
		}
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
	
	private void initFields() {
		edPart0.setText(movie.getPart(0));
		edPart1.setText(movie.getPart(1));
		edPart2.setText(movie.getPart(2));
		edPart3.setText(movie.getPart(3));
		edPart4.setText(movie.getPart(4));
		edPart5.setText(movie.getPart(5));
		
		edTitle.setText(movie.getTitle());
		edZyklus.setText(movie.getZyklus().getTitle());
		spnZyklus.setValue(movie.getZyklus().getNumber());
		cbViewed.setSelected(movie.isViewed());
		cbxLanguage.setValue(movie.getLanguage());
		spnLength.setValue(movie.getLength());
		spnAddDate.setValue(movie.getAddDate());
		spnOnlineScore.setValue(movie.getOnlinescore().asInt());
		cbxFSK.setSelectedIndex(movie.getFSK().asInt());
		cbxFormat.setSelectedIndex(movie.getFormat().asInt());
		spnYear.setValue(movie.getYear());
		spnSize.setValue(movie.getFilesize().getBytes());
		
		cbxGenre0.setSelectedIndex(movie.getGenre(0).asInt());
		cbxGenre1.setSelectedIndex(movie.getGenre(1).asInt());
		cbxGenre2.setSelectedIndex(movie.getGenre(2).asInt());
		cbxGenre3.setSelectedIndex(movie.getGenre(3).asInt());
		cbxGenre4.setSelectedIndex(movie.getGenre(4).asInt());
		cbxGenre5.setSelectedIndex(movie.getGenre(5).asInt());
		cbxGenre6.setSelectedIndex(movie.getGenre(6).asInt());
		cbxGenre7.setSelectedIndex(movie.getGenre(7).asInt());
		
		cbxScore.setSelectedIndex(movie.getScore().asInt());
		tagPnl.setValue(movie.getTags());
		
		edCvrControl.setCover(movie.getCover());

		edViewedHistory.setValue(movie.getViewedHistory());
		edReference.setValue(movie.getOnlineReference());
		edGroups.setValue(movie.getGroups());

		ctrlMediaInfo.setValue(movie.getMediaInfo());

		updateByteDisp();
		testPaths();
	}
	
	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
	}
	
	@Override
	public void setOnlineReference(CCOnlineReferenceList ref) {
		edReference.setValue(ref);
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		try {
			onBtnOK(false);
		} catch (EnumFormatException e) {
			CCLog.addError(e);
		}
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

	@Override
	public void setMovieFormat(CCFileFormat cmf) {
		cbxFormat.setSelectedIndex(cmf.asInt());
	}

	public void setMovieLanguage(CCDBLanguageList lang) {
		cbxLanguage.setValue(lang);
	}

	@Override
	public void setFilepath(int p, String t) {
		String pt = t;
		if (!t.isEmpty() && CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()) {
			pt = PathFormatter.getCCPath(t);
		}
				
		switch (p) {
		case 0:
			edPart0.setText(pt);
			edPart0.setCaretPosition(0);
			break;
		case 1:
			edPart1.setText(pt);
			edPart1.setCaretPosition(0);
			break;
		case 2:
			edPart2.setText(pt);
			edPart2.setCaretPosition(0);
			break;
		case 3:
			edPart3.setText(pt);
			edPart3.setCaretPosition(0);
			break;
		case 4:
			edPart4.setText(pt);
			edPart4.setCaretPosition(0);
			break;
		case 5:
			edPart5.setText(pt);
			edPart5.setCaretPosition(0);
			break;
		}

		testPaths();
		
		updateFilesize();
		
		CCFileFormat fmt = CCFileFormat.getMovieFormatFromPaths(edPart0.getText(), edPart1.getText(), edPart2.getText(), edPart3.getText(), edPart4.getText(), edPart5.getText());
		if (fmt != null) cbxFormat.setSelectedIndex(fmt.asInt());
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
	public void setFilesize(long size) {
		spnSize.setValue(size);
	}

	@Override
	public void setYear(int y) {
		spnYear.setValue(y);
	}
	
	@Override
	public void setGenre(int gid, int movGenre) {
		switch (gid) {
		case 0:
			cbxGenre0.setSelectedIndex(movGenre);
			break;
		case 1:
			cbxGenre1.setSelectedIndex(movGenre);
			break;
		case 2:
			cbxGenre2.setSelectedIndex(movGenre);
			break;
		case 3:
			cbxGenre3.setSelectedIndex(movGenre);
			break;
		case 4:
			cbxGenre4.setSelectedIndex(movGenre);
			break;
		case 5:
			cbxGenre5.setSelectedIndex(movGenre);
			break;
		case 6:
			cbxGenre6.setSelectedIndex(movGenre);
			break;
		case 7:
			cbxGenre7.setSelectedIndex(movGenre);
			break;
		}
	}
	
	@Override
	public void setFSK(int fsk) {
		cbxFSK.setSelectedIndex(fsk);
	}
	
	@Override
	public void setLength(int l) {
		spnLength.setValue(l);
	}
	
	@Override
	public void setScore(int s) {
		spnOnlineScore.setValue(s);
	}
	
	@Override
	public void onFinishInserting() {
		// nothing
	}
	
	private void onBtnChooseClicked(int cNmbr) {
		int returnval = videoFileChooser.showOpenDialog(this);
		
		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		setFilepath(cNmbr, videoFileChooser.getSelectedFile().getAbsolutePath());
	}
	
	private void onBtnClearClicked(int cNmbr) {
		setFilepath(cNmbr, ""); //$NON-NLS-1$
	}
	
	private void onBtnOK(boolean check) throws EnumFormatException {
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

		movie.beginUpdating();
		
		//#####################################################################################
		
		movie.setPart(0, edPart0.getText());
		movie.setPart(1, edPart1.getText());
		movie.setPart(2, edPart2.getText());
		movie.setPart(3, edPart3.getText());
		movie.setPart(4, edPart4.getText());
		movie.setPart(5, edPart5.getText());
		
		movie.setTitle(edTitle.getText());
		movie.setZyklusTitle(edZyklus.getText());
		movie.setZyklusID((int) spnZyklus.getValue());
		
		movie.setViewed(cbViewed.isSelected());
		
		movie.setLanguage(cbxLanguage.getValue());
		
		movie.setLength((int) spnLength.getValue());
		
		movie.setAddDate(spnAddDate.getValue());
		
		movie.setOnlinescore((int) spnOnlineScore.getValue());
		
		movie.setFsk(cbxFSK.getSelectedIndex());
		movie.setFormat(cbxFormat.getSelectedIndex());
		
		movie.setYear((int) spnYear.getValue());
		movie.setFilesize((long) spnSize.getValue());
		
		movie.setGenre(CCGenre.getWrapper().findOrException(cbxGenre0.getSelectedIndex()), 0);
		movie.setGenre(CCGenre.getWrapper().findOrException(cbxGenre1.getSelectedIndex()), 1);
		movie.setGenre(CCGenre.getWrapper().findOrException(cbxGenre2.getSelectedIndex()), 2);
		movie.setGenre(CCGenre.getWrapper().findOrException(cbxGenre3.getSelectedIndex()), 3);
		movie.setGenre(CCGenre.getWrapper().findOrException(cbxGenre4.getSelectedIndex()), 4);
		movie.setGenre(CCGenre.getWrapper().findOrException(cbxGenre5.getSelectedIndex()), 5);
		movie.setGenre(CCGenre.getWrapper().findOrException(cbxGenre6.getSelectedIndex()), 6);
		movie.setGenre(CCGenre.getWrapper().findOrException(cbxGenre7.getSelectedIndex()), 7);
		
		movie.setTags(tagPnl.getValue());
		movie.setScore(cbxScore.getSelectedIndex());
		
		movie.setCover(edCvrControl.getResizedImageForStorage());

		movie.setViewedHistory(edViewedHistory.getValue());
		movie.setOnlineReference(edReference.getValue());
		movie.setGroups(edGroups.getValue());
		
		movie.setMediaInfo(ctrlMediaInfo.getValue());
		
		//#####################################################################################
		
		movie.endUpdating();
		
		if (listener != null) {
			listener.onUpdate(movie);
		}
		
		dispose();
	}
	
	public boolean checkUserData(List<UserDataProblem> ret) {
		BufferedImage i = edCvrControl.getResizedImageForStorage();
		
		String p0 = edPart0.getText();
		String p1 = edPart1.getText();
		String p2 = edPart2.getText();
		String p3 = edPart3.getText();
		String p4 = edPart4.getText();
		String p5 = edPart5.getText();
		
		String title = edTitle.getText();
		String zyklus = edZyklus.getText();
		int zyklusID = (int) spnZyklus.getValue();
		
		int len = (int) spnLength.getValue();
		CCDate adddate = spnAddDate.getValue();
		int oscore = (int) spnOnlineScore.getValue();
		
		try {
			int fskidx = cbxFSK.getSelectedIndex();
			int year = (int) spnYear.getValue();
			long fsize = (long) spnSize.getValue();
			CCDBLanguageList lang = cbxLanguage.getValue();
			String csExtn  = CCFileFormat.getWrapper().findOrException(cbxFormat.getSelectedIndex()).asString();
			String csExta = CCFileFormat.getWrapper().findOrException(cbxFormat.getSelectedIndex()).asStringAlt();
			
			int g0 = cbxGenre0.getSelectedIndex();
			int g1 = cbxGenre1.getSelectedIndex();
			int g2 = cbxGenre2.getSelectedIndex();
			int g3 = cbxGenre3.getSelectedIndex();
			int g4 = cbxGenre4.getSelectedIndex();
			int g5 = cbxGenre5.getSelectedIndex();
			int g6 = cbxGenre6.getSelectedIndex();
			int g7 = cbxGenre7.getSelectedIndex();
			
			CCOnlineReferenceList ref = edReference.getValue();
			
			CCMediaInfo mi = ctrlMediaInfo.getValue();
			
			UserDataProblem.testMovieData(ret, movie, i, movie.getMovieList(), p0, p1, p2, p3, p4, p5, title, zyklus, zyklusID, len, adddate, oscore, fskidx, year, fsize, csExtn, csExta, g0, g1, g2, g3, g4, g5, g6, g7, mi, lang, ref);
			
			return ret.isEmpty();
		} catch (CCFormatException e) {
			return false;
		}
	}

	@Override
	public void editingFinished(BufferedImage i) {
		setCover(i);
	}

	@Override
	public void editingCanceled() {
		// nothing
	}

	private void parseCodecMetadata_Lang() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<MediaQueryResult> dat = new ArrayList<>();

			if (!Str.isNullOrWhitespace(edPart0.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart0.getText()), false));
			if (!Str.isNullOrWhitespace(edPart1.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart1.getText()), false));
			if (!Str.isNullOrWhitespace(edPart2.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart2.getText()), false));
			if (!Str.isNullOrWhitespace(edPart3.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart3.getText()), false));
			if (!Str.isNullOrWhitespace(edPart4.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart4.getText()), false));
			if (!Str.isNullOrWhitespace(edPart5.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart5.getText()), false));

			if (dat.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

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
			List<MediaQueryResult> dat = new ArrayList<>();

			if (!Str.isNullOrWhitespace(edPart0.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart0.getText()), true));
			if (!Str.isNullOrWhitespace(edPart1.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart1.getText()), true));
			if (!Str.isNullOrWhitespace(edPart2.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart2.getText()), true));
			if (!Str.isNullOrWhitespace(edPart3.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart3.getText()), true));
			if (!Str.isNullOrWhitespace(edPart4.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart4.getText()), true));
			if (!Str.isNullOrWhitespace(edPart5.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart5.getText()), true));

			if (dat.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			int dur = (int) (CCStreams.iterate(dat).any(d -> d.Duration == -1) ? -1 : (CCStreams.iterate(dat).sumDouble(d -> d.Duration)/60));
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			setLength(dur);

		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void parseCodecMetadata_MI() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<MediaQueryResult> dat = new ArrayList<>();

			if (!Str.isNullOrWhitespace(edPart0.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart0.getText()), true));
			if (!Str.isNullOrWhitespace(edPart1.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart1.getText()), true));
			if (!Str.isNullOrWhitespace(edPart2.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart2.getText()), true));
			if (!Str.isNullOrWhitespace(edPart3.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart3.getText()), true));
			if (!Str.isNullOrWhitespace(edPart4.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart4.getText()), true));
			if (!Str.isNullOrWhitespace(edPart5.getText())) dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(edPart5.getText()), true));

			if (dat.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			ctrlMediaInfo.setValue(dat.get(0));

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

		StringBuilder b = new StringBuilder();

		try {
			if (!Str.isNullOrWhitespace(edPart0.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(edPart0.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(edPart1.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(edPart1.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(edPart2.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(edPart2.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(edPart3.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(edPart3.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(edPart4.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(edPart4.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(edPart5.getText())) b.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(edPart5.getText()))).append("\n\n\n\n\n"); //$NON-NLS-1$

			GenericTextDialog.showText(this, getTitle(), b.toString(), false);
		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private void setDirty() {
		if (!_initFinished) return;
		setTitle(LocaleBundle.getFormattedString("EditMovieFrame.this.title", movie.getCompleteTitle()) + "*"); //$NON-NLS-1$ //$NON-NLS-2$
		_isDirty = true;
	}
}
