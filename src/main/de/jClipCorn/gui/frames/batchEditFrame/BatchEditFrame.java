package de.jClipCorn.gui.frames.batchEditFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.IEpisodeOwner;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.omniParserFrame.OmniParserFrame;
import de.jClipCorn.gui.guiComponents.HFixListCellRenderer;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner.JCCDateTimeSpinner;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.OmniParserCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchEditFrame extends JFrame implements UserDataProblemHandler, OmniParserCallbackListener {
	private static final long serialVersionUID = 8825373383589912037L;

	private static CCDate MIN_DATE = CCDate.getMinimumDate();

	private JList<String> lsEpisodes;
	private JLabel label;
	private JTextField edTitle;
	private JLabel label_2;
	private JSpinner spnLength;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel label_5;
	private CCEnumComboBox<CCFileFormat> cbxFormat;
	private JLabel label_6;
	private JSpinner spnSize;
	private JLabel label_7;
	private JLabel lblFileSize;
	private JCCDateSpinner spnAddDate;
	private JLabel lblEpisode;
	private JSpinner spnEpisode;
	private JScrollPane scrollPane;
	private JButton btnEpCancel;
	private JButton btnEpOk;
	private JButton btnNext;
	private JButton btnOK;
	private JButton btnSide_01;
	private JSpinner spnSide_01;
	private JButton btnSide_02;
	private JSpinner spnSide_02;
	private JTextField edSide_01;
	private JButton btnSide_03;
	private JTextField edSide_02;
	private JButton btnSide_04;
	private JTextField edSide_03;
	private JButton btnSide_05;
	private JTextField edSide_04;
	private JButton btnSide_06;
	private JButton btnSide_07;
	private JSpinner spnSide_04;
	private JSpinner spnSide_03;
	private JTextField edSide_05;
	private JButton btnSide_08;
	private JButton btnSide_11;
	private JButton btnSide_12;
	private JSpinner spnSideLength;
	private JButton btnSide_13;
	private CCEnumComboBox<CCFileFormat> cbxSideFormat;
	private JButton btnSide_09;
	private JButton btnRecalcSize;
	private JLabel lblSeason;
	private JButton btnToday;
	private ReadableTextField edPart;
	private JLabel lblNewLabel;
	private JButton btnOpen;
	private JButton btnOmniparser;
	private JButton btnAutoMeta;
	private JSpinner spnSide_05;
	private JButton btnIncEpisodeNumbers;
	private JLabel label_1;
	private LanguageChooser ctrlLang;
	private LanguageChooser ctrlMultiLang;
	private JButton btnSpracheSetzen;
	private JButton btnMediaInfo1;
	private JButton btnMediaInfo2;
	private JButton btnMediaInfoRaw;
	private JButton btnSideAutoLang;
	private JButton btnSideAutoLen;
	private JPanel pnlInfo;
	private JPanel pnlTitleEdit;
	private JPanel panel_1;
	private JLabel lblDirtyTitle;
	private JLabel lblDirtyEpisodeNumber;
	private JLabel lblDirtyFormat;
	private JLabel lblDirtyLanguage;
	private JLabel lblDirtyLength;
	private JLabel lblDirtySize;
	private JLabel lblDirtyAddDate;
	private JLabel lblDirtyPath;
	private JPanel panel;
	private JLabel label_8;
	private JMediaInfoControl ctrlMediaInfo;
	private JLabel lblDirtyMediaInfo;
	private JButton btnSiedAutoMediaInfo;
	private JButton btnMediaInfo3;
	private JButton btnViewedNow;
	private JButton btnViewedUnknown;
	private JCCDateTimeSpinner ctrlSideHistoryVal;
	private JButton btnAddToHistory;
	private JButton btnClearHistory;
	private JLabel lblNewLabel_2;
	private JLabel lblDirtyHistory;
	private DateTimeListEditor ctrlHistory;
	private JButton btnSidePart_01;
	private JSpinner spnSidePart_01;
	private JButton btnSidePart_02;
	private JSpinner spnSidePart_02;
	private JTextField edSidePart_01;
	private JButton btnSidePart_03;
	private JTextField edSidePart_02;
	private JTextField edSidePart_R1;
	private JButton btnRegexReplacePart;
	private JTextField edSidePart_R2;
	private JButton btnSidePart_04;
	private JTextField edSidePart_03;
	private JButton btnSidePart_05;
	private JTextField edSidePart_04;
	private JButton btnSidePart_06;
	private JButton btnSidePart_07;
	private JSpinner spnSidePart_03;
	private JSpinner spnSidePart_04;
	private JTextField edSidePart_05;
	private JButton btnSidePart_08;
	private final UpdateCallbackListener listener;
	private JTabbedPane tabbedPane;
	private JPanel pnlPartEdit;
	private JPanel pnlMiscEdit;
	private JTextField edSide_R1;
	private JTextField edSide_R2;
	private JButton btnRegexReplace;
	private JButton btnPathConvert;
	private JButton btnPathConvertBack;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;
	private JButton btnNewButton_3;


	private final IEpisodeOwner target;
	private final List<BatchEditEpisodeData> data;
	private final JFileChooser videoFileChooser;
	private final JFileChooser massVideoFileChooser;
	private JButton btnCalcHash;


	/**
	 * @wbp.parser.constructor
	 */
	public BatchEditFrame(Component owner, IEpisodeOwner ss, UpdateCallbackListener ucl) { this(owner, ss, null, ucl); }

	public BatchEditFrame(Component owner, IEpisodeOwner ss, List<CCEpisode> eps, UpdateCallbackListener ucl) {
		super();
		setMinimumSize(new Dimension(1150, 750));
		setSize(new Dimension(1250, 800));
		this.target = ss;
		this.listener = ucl;
		this.data = (eps!= null) ? CCStreams.iterate(eps).map(BatchEditEpisodeData::new).enumerate() : ss.iteratorEpisodes().map(BatchEditEpisodeData::new).enumerate();

		String cPathStart = ss.getSeries().getCommonPathStart(true);

		this.videoFileChooser     = new JFileChooser(PathFormatter.fromCCPath(cPathStart));
		this.massVideoFileChooser = new JFileChooser(PathFormatter.fromCCPath(cPathStart));

		initGUI();

		setLocationRelativeTo(owner);

		updateList();
		initFileChooser();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getFormattedString("AddEpisodeFrame.this.title", target.getSeries().getTitle())); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("200dlu"), //$NON-NLS-1$
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("250dlu:grow"), //$NON-NLS-1$
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("300dlu"), //$NON-NLS-1$
				FormSpecs.UNRELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.PARAGRAPH_GAP_ROWSPEC,
						RowSpec.decode("23px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("default:grow"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("23px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "2, 4, fill, fill"); //$NON-NLS-1$

		lsEpisodes = new JList<>();
		lsEpisodes.setCellRenderer(new HFixListCellRenderer());
		lsEpisodes.addListSelectionListener(arg0 -> updateDisplayPanel());
		lsEpisodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(lsEpisodes);

		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOKClicked(true));

		panel = new JPanel();
		getContentPane().add(panel, "2, 6, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
				new RowSpec[] {
						RowSpec.decode("23px"),})); //$NON-NLS-1$

		btnOmniparser = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnOmniParser.text")); //$NON-NLS-1$
		panel.add(btnOmniparser, "1, 1, fill, fill"); //$NON-NLS-1$

		btnAutoMeta = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnAutoMeta.text")); //$NON-NLS-1$
		panel.add(btnAutoMeta, "3, 1, fill, fill"); //$NON-NLS-1$
		btnAutoMeta.addActionListener(arg0 -> autoMetaDataCalc());
		btnOmniparser.addActionListener(arg0 -> {
			OmniParserFrame oframe = new OmniParserFrame(BatchEditFrame.this, BatchEditFrame.this, getTitleList(), getCommonFolderPathStart(), Str.Empty, false);
			oframe.setVisible(true);
		});

		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		getContentPane().add(tabbedPane, "6, 2, 1, 7, fill, fill"); //$NON-NLS-1$

		pnlTitleEdit = new JPanel();
		pnlTitleEdit.setBorder(null);
		tabbedPane.addTab(LocaleBundle.getString("BatchEditFrame.TabTitle"), null, pnlTitleEdit, null); //$NON-NLS-1$
		pnlTitleEdit.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow(2)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("pref:grow"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		pnlPartEdit = new JPanel();
		pnlPartEdit.setBorder(null);
		tabbedPane.addTab(LocaleBundle.getString("BatchEditFrame.TabPath"), null, pnlPartEdit, null); //$NON-NLS-1$
		pnlPartEdit.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow(2)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("11px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		btnPathConvert = new JButton(LocaleBundle.getString("BatchEditFrame.ConverToCC")); //$NON-NLS-1$
		pnlPartEdit.add(btnPathConvert, "2, 2, 3, 1"); //$NON-NLS-1$
		btnPathConvert.addActionListener(e -> convertPathToCCPath());

		btnPathConvertBack = new JButton(LocaleBundle.getString("BatchEditFrame.ConvertFromCC")); //$NON-NLS-1$
		pnlPartEdit.add(btnPathConvertBack, "6, 2, 5, 1"); //$NON-NLS-1$
		btnPathConvertBack.addActionListener(e -> convertPathFromCCPath());

		btnNewButton = new JButton(LocaleBundle.getString("BatchEditFrame.DeleteFilename")); //$NON-NLS-1$
		pnlPartEdit.add(btnNewButton, "2, 6, 5, 1"); //$NON-NLS-1$
		btnNewButton.addActionListener(e -> deleteFilenameWithExtension());

		btnNewButton_1 = new JButton(LocaleBundle.getString("BatchEditFrame.DeleteFileNameWithoutExt")); //$NON-NLS-1$
		pnlPartEdit.add(btnNewButton_1, "2, 8, 5, 1"); //$NON-NLS-1$
		btnNewButton_1.addActionListener(e -> deleteFilenameWithoutExtension());

		btnNewButton_2 = new JButton(LocaleBundle.getString("BatchEditFrame.DeletePath")); //$NON-NLS-1$
		pnlPartEdit.add(btnNewButton_2, "2, 10, 5, 1"); //$NON-NLS-1$
		btnNewButton_2.addActionListener(e -> deleteFilepath());

		btnNewButton_3 = new JButton(LocaleBundle.getString("BatchEditFrame.DeleteExt")); //$NON-NLS-1$
		pnlPartEdit.add(btnNewButton_3, "2, 12, 5, 1"); //$NON-NLS-1$
		btnNewButton_3.addActionListener(e -> deleteExtension());

		btnSidePart_01 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteFirst.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_01, "2, 16, 5, 1"); //$NON-NLS-1$
		btnSidePart_01.addActionListener(e -> delFirstPartChars());

		spnSidePart_01 = new JSpinner();
		pnlPartEdit.add(spnSidePart_01, "8, 16, 3, 1"); //$NON-NLS-1$
		spnSidePart_01.setModel(new SpinnerNumberModel(1, 0, null, 1));

		btnSidePart_02 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteLast.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_02, "2, 18, 5, 1"); //$NON-NLS-1$
		btnSidePart_02.addActionListener(e -> delLastPartChars());

		spnSidePart_02 = new JSpinner();
		pnlPartEdit.add(spnSidePart_02, "8, 18, 3, 1"); //$NON-NLS-1$
		spnSidePart_02.setModel(new SpinnerNumberModel(1, null, null, 1));

		edSidePart_01 = new JTextField();
		pnlPartEdit.add(edSidePart_01, "2, 22"); //$NON-NLS-1$
		edSidePart_01.setColumns(10);

		btnSidePart_03 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnReplace.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_03, "4, 22, 3, 1"); //$NON-NLS-1$
		btnSidePart_03.addActionListener(e -> replacePartChars());

		edSidePart_02 = new JTextField();
		pnlPartEdit.add(edSidePart_02, "8, 22, 3, 1"); //$NON-NLS-1$
		edSidePart_02.setColumns(10);

		edSidePart_R1 = new JTextField();
		pnlPartEdit.add(edSidePart_R1, "2, 24, fill, default"); //$NON-NLS-1$
		edSidePart_R1.setColumns(10);

		btnRegexReplacePart = new JButton(LocaleBundle.getString("BatchEditFrame.ReplaceRegex")); //$NON-NLS-1$
		pnlPartEdit.add(btnRegexReplacePart, "4, 24, 3, 1"); //$NON-NLS-1$
		btnRegexReplacePart.addActionListener(e -> replacePartCharsRegex());

		edSidePart_R2 = new JTextField();
		pnlPartEdit.add(edSidePart_R2, "8, 24, 3, 1, fill, default"); //$NON-NLS-1$
		edSidePart_R2.setColumns(10);

		btnSidePart_04 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnTrim.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_04, "2, 28, 9, 1"); //$NON-NLS-1$
		btnSidePart_04.addActionListener(e -> trimPartChars());

		edSidePart_03 = new JTextField();
		pnlPartEdit.add(edSidePart_03, "2, 30"); //$NON-NLS-1$
		edSidePart_03.setColumns(10);

		btnSidePart_05 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatStart.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_05, "4, 30, 7, 1"); //$NON-NLS-1$
		btnSidePart_05.addActionListener(e -> concatStartPartChars());

		edSidePart_04 = new JTextField();
		pnlPartEdit.add(edSidePart_04, "2, 32"); //$NON-NLS-1$
		edSidePart_04.setColumns(10);

		btnSidePart_06 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatEnd.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_06, "4, 32, 7, 1"); //$NON-NLS-1$
		btnSidePart_06.addActionListener(e -> concatEndPartChars());

		btnSidePart_07 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDelete.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_07, "2, 36, 5, 1"); //$NON-NLS-1$
		btnSidePart_07.addActionListener(e -> deletePartChars());

		spnSidePart_03 = new JSpinner();
		pnlPartEdit.add(spnSidePart_03, "8, 36"); //$NON-NLS-1$

		spnSidePart_04 = new JSpinner();
		pnlPartEdit.add(spnSidePart_04, "10, 36"); //$NON-NLS-1$

		edSidePart_05 = new JTextField();
		pnlPartEdit.add(edSidePart_05, "2, 40"); //$NON-NLS-1$
		edSidePart_05.setColumns(10);

		btnSidePart_08 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSearchAndDel.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_08, "4, 40, 7, 1"); //$NON-NLS-1$
		btnSidePart_08.addActionListener(e -> searchAndDeletePartChars());


		pnlMiscEdit = new JPanel();
		pnlMiscEdit.setBorder(null);
		tabbedPane.addTab(LocaleBundle.getString("BatchEditFrame.TabMisc"), null, pnlMiscEdit, null); //$NON-NLS-1$
		pnlMiscEdit.setLayout(null);

		pnlMiscEdit.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow(2)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("10dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px:grow"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		btnSide_01 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteFirst.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_01, "2, 2, 5, 1"); //$NON-NLS-1$

		spnSide_01 = new JSpinner();
		pnlTitleEdit.add(spnSide_01, "8, 2, 3, 1"); //$NON-NLS-1$
		spnSide_01.setModel(new SpinnerNumberModel(1, 0, null, 1));

		btnSide_02 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteLast.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_02, "2, 4, 5, 1"); //$NON-NLS-1$

		spnSide_02 = new JSpinner();
		pnlTitleEdit.add(spnSide_02, "8, 4, 3, 1"); //$NON-NLS-1$
		spnSide_02.setModel(new SpinnerNumberModel(1, null, null, 1));

		edSide_01 = new JTextField();
		pnlTitleEdit.add(edSide_01, "2, 8"); //$NON-NLS-1$
		edSide_01.setColumns(10);

		btnSide_03 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnReplace.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_03, "4, 8, 3, 1"); //$NON-NLS-1$
		btnSide_03.addActionListener(e -> replaceChars());

		edSide_02 = new JTextField();
		pnlTitleEdit.add(edSide_02, "8, 8, 3, 1"); //$NON-NLS-1$
		edSide_02.setColumns(10);

		edSide_R1 = new JTextField();
		pnlTitleEdit.add(edSide_R1, "2, 10, fill, default"); //$NON-NLS-1$
		edSide_R1.setColumns(10);

		btnRegexReplace = new JButton(LocaleBundle.getString("BatchEditFrame.ReplaceRegex")); //$NON-NLS-1$
		pnlTitleEdit.add(btnRegexReplace, "4, 10, 3, 1"); //$NON-NLS-1$
		btnRegexReplace.addActionListener(e -> replaceCharsRegex());

		edSide_R2 = new JTextField();
		pnlTitleEdit.add(edSide_R2, "8, 10, 3, 1, fill, default"); //$NON-NLS-1$
		edSide_R2.setColumns(10);

		btnSide_04 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnTrim.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_04, "2, 14, 9, 1"); //$NON-NLS-1$
		btnSide_04.addActionListener(e -> trimChars());

		edSide_03 = new JTextField();
		pnlTitleEdit.add(edSide_03, "2, 16"); //$NON-NLS-1$
		edSide_03.setColumns(10);

		btnSide_05 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatStart.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_05, "4, 16, 7, 1"); //$NON-NLS-1$
		btnSide_05.addActionListener(e -> concatStartChars());

		edSide_04 = new JTextField();
		pnlTitleEdit.add(edSide_04, "2, 18"); //$NON-NLS-1$
		edSide_04.setColumns(10);

		btnSide_06 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatEnd.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_06, "4, 18, 7, 1"); //$NON-NLS-1$
		btnSide_06.addActionListener(e -> concatEndChars());

		btnSide_07 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDelete.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_07, "2, 22, 5, 1"); //$NON-NLS-1$
		btnSide_07.addActionListener(e -> deleteChars());

		spnSide_03 = new JSpinner();
		pnlTitleEdit.add(spnSide_03, "8, 22"); //$NON-NLS-1$

		spnSide_04 = new JSpinner();
		pnlTitleEdit.add(spnSide_04, "10, 22"); //$NON-NLS-1$

		edSide_05 = new JTextField();
		pnlTitleEdit.add(edSide_05, "2, 26"); //$NON-NLS-1$
		edSide_05.setColumns(10);

		btnSide_08 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSearchAndDel.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_08, "4, 26, 7, 1"); //$NON-NLS-1$
		btnSide_08.addActionListener(e -> searchAndDeleteChars());

		spnSide_05 = new JSpinner();
		pnlMiscEdit.add(spnSide_05, "2, 2"); //$NON-NLS-1$
		spnSide_05.setModel(new SpinnerNumberModel(1, null, null, 1));

		btnIncEpisodeNumbers = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnIncEpisodeNumbers.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnIncEpisodeNumbers, "4, 2, 7, 1"); //$NON-NLS-1$
		btnIncEpisodeNumbers.addActionListener(arg0 -> massIncNumber());

		ctrlMultiLang = new LanguageChooser();
		pnlMiscEdit.add(ctrlMultiLang, "2, 4, 5, 1"); //$NON-NLS-1$

		btnSpracheSetzen = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetLang.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSpracheSetzen, "8, 4, 3, 1"); //$NON-NLS-1$
		btnSpracheSetzen.addActionListener(arg0 -> massSetLanguage());

		btnSide_09 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpSize.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSide_09, "2, 6, 9, 1"); //$NON-NLS-1$
		btnSide_09.addActionListener(e -> massRecalcSizes());

		btnSide_11 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetUnviewed.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSide_11, "2, 10, 9, 1"); //$NON-NLS-1$
		btnSide_11.addActionListener(e -> massSetNotViewed());

		btnViewedNow = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetViewedNow.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnViewedNow, "2, 12, 9, 1"); //$NON-NLS-1$
		btnViewedNow.addActionListener(e -> massAddToHistory(CCDateTime.getCurrentDateTime()));

		btnViewedUnknown = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetViewedUndef.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnViewedUnknown, "2, 14, 9, 1"); //$NON-NLS-1$
		btnViewedUnknown.addActionListener(e -> massAddToHistory(CCDateTime.getUnspecified()));

		btnSide_12 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpLength.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSide_12, "2, 18, 5, 1"); //$NON-NLS-1$
		btnSide_12.addActionListener(e -> massSetLength());

		spnSideLength = new JSpinner();
		pnlMiscEdit.add(spnSideLength, "8, 18, 3, 1"); //$NON-NLS-1$
		spnSideLength.setModel(new SpinnerNumberModel(0, 0, null, 1));

		btnSide_13 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpFormat.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSide_13, "2, 22, 5, 1"); //$NON-NLS-1$
		btnSide_13.addActionListener(e -> massSetFormat());

		cbxSideFormat = new CCEnumComboBox<>(CCFileFormat.getWrapper());
		pnlMiscEdit.add(cbxSideFormat, "8, 22, 3, 1"); //$NON-NLS-1$

		ctrlSideHistoryVal = new JCCDateTimeSpinner();
		pnlMiscEdit.add(ctrlSideHistoryVal, "2, 26, 3, 1"); //$NON-NLS-1$

		btnAddToHistory = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnAddToHistory.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnAddToHistory, "6, 26, 5, 1"); //$NON-NLS-1$
		btnAddToHistory.addActionListener(e -> massAddToHistory(ctrlSideHistoryVal.getValue()));

		btnClearHistory = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnClearHistory.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnClearHistory, "6, 28, 5, 1"); //$NON-NLS-1$
		btnClearHistory.addActionListener(e -> massClearHistory());

		btnSideAutoLang = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnMassSetLang.title")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSideAutoLang, "2, 32, 9, 1"); //$NON-NLS-1$
		btnSideAutoLang.addActionListener(e -> massMediaInfoLang());

		btnSideAutoLen = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnMassSetLen.title")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSideAutoLen, "2, 34, 9, 1"); //$NON-NLS-1$
		btnSideAutoLen.addActionListener(e -> massMediaInfoLen());

		btnSiedAutoMediaInfo = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnMassSetMediaInfo.title")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSiedAutoMediaInfo, "2, 36, 5, 1, fill, fill"); //$NON-NLS-1$
		
		btnCalcHash = new JButton(LocaleBundle.getString("BatchEditFrame.HashCalc")); //$NON-NLS-1$
		pnlMiscEdit.add(btnCalcHash, "8, 36, 3, 1"); //$NON-NLS-1$
		btnCalcHash.addActionListener(e -> massCalcHash());

		btnSiedAutoMediaInfo.addActionListener(e -> massMediaInfo());

		btnSide_02.addActionListener(e -> delLastChars());
		btnSide_01.addActionListener(e -> delFirstChars());
		getContentPane().add(btnOK, "4, 8, center, top"); //$NON-NLS-1$

		lblSeason = new JLabel(target.getTitle());
		lblSeason.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		lblSeason.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblSeason, "2, 2, fill, fill"); //$NON-NLS-1$

		pnlInfo = new JPanel();
		getContentPane().add(pnlInfo, "4, 2, 1, 5, fill, fill"); //$NON-NLS-1$
		pnlInfo.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlInfo.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("5dlu"), //$NON-NLS-1$
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("50px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("22px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("20px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("20px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("22px"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("20px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("20px"), //$NON-NLS-1$
						FormSpecs.UNRELATED_GAP_ROWSPEC,
						RowSpec.decode("80dlu"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						RowSpec.decode("default:grow"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,}));

		lblDirtyTitle = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyTitle, "2, 2"); //$NON-NLS-1$

		label = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		pnlInfo.add(label, "3, 2"); //$NON-NLS-1$

		edTitle = new JTextField();
		pnlInfo.add(edTitle, "5, 2, 5, 1"); //$NON-NLS-1$
		edTitle.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					onBtnNext();
				}
			}
		});
		edTitle.setColumns(10);

		lblDirtyEpisodeNumber = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyEpisodeNumber, "2, 4"); //$NON-NLS-1$

		lblEpisode = new JLabel(LocaleBundle.getString("AddEpisodeFrame.lblEpisode.text")); //$NON-NLS-1$
		pnlInfo.add(lblEpisode, "3, 4"); //$NON-NLS-1$

		spnEpisode = new JSpinner();
		pnlInfo.add(spnEpisode, "5, 4"); //$NON-NLS-1$
		spnEpisode.setModel(new SpinnerNumberModel(0, 0, null, 1));

		lblDirtyFormat = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyFormat, "2, 6"); //$NON-NLS-1$

		label_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		pnlInfo.add(label_5, "3, 6"); //$NON-NLS-1$

		cbxFormat = new CCEnumComboBox<>(CCFileFormat.getWrapper());
		pnlInfo.add(cbxFormat, "5, 6"); //$NON-NLS-1$

		lblDirtyMediaInfo = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyMediaInfo, "2, 8"); //$NON-NLS-1$

		label_8 = new JLabel("MediaInfo"); //$NON-NLS-1$
		pnlInfo.add(label_8, "3, 8"); //$NON-NLS-1$

		ctrlMediaInfo = new JMediaInfoControl(() -> PathFormatter.fromCCPath(edPart.getText()));
		pnlInfo.add(ctrlMediaInfo, "5, 8, fill, fill"); //$NON-NLS-1$

		btnMediaInfo3 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		pnlInfo.add(btnMediaInfo3, "9, 8"); //$NON-NLS-1$
		btnMediaInfo3.setToolTipText("MediaInfo"); //$NON-NLS-1$

		lblDirtyLanguage = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyLanguage, "2, 10"); //$NON-NLS-1$

		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		pnlInfo.add(label_1, "3, 10"); //$NON-NLS-1$

		ctrlLang = new LanguageChooser();
		pnlInfo.add(ctrlLang, "5, 10"); //$NON-NLS-1$

		btnMediaInfoRaw = new JButton("..."); //$NON-NLS-1$
		pnlInfo.add(btnMediaInfoRaw, "7, 10"); //$NON-NLS-1$
		btnMediaInfoRaw.setToolTipText("MediaInfo"); //$NON-NLS-1$

		btnMediaInfo1 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		pnlInfo.add(btnMediaInfo1, "9, 10"); //$NON-NLS-1$
		btnMediaInfo1.setToolTipText("MediaInfo"); //$NON-NLS-1$

		lblDirtyLength = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyLength, "2, 12"); //$NON-NLS-1$

		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		pnlInfo.add(label_2, "3, 12"); //$NON-NLS-1$

		spnLength = new JSpinner();
		pnlInfo.add(spnLength, "5, 12"); //$NON-NLS-1$

		label_3 = new JLabel("min."); //$NON-NLS-1$
		pnlInfo.add(label_3, "7, 12"); //$NON-NLS-1$

		btnMediaInfo2 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		pnlInfo.add(btnMediaInfo2, "9, 12"); //$NON-NLS-1$
		btnMediaInfo2.setToolTipText("MediaInfo"); //$NON-NLS-1$
		btnMediaInfo2.addActionListener(e -> parseCodecMetadata_Len());

		lblDirtySize = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtySize, "2, 14"); //$NON-NLS-1$

		label_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		pnlInfo.add(label_6, "3, 14"); //$NON-NLS-1$

		spnSize = new JSpinner();
		pnlInfo.add(spnSize, "5, 14"); //$NON-NLS-1$
		spnSize.setModel(new SpinnerNumberModel(0L, null, null, 1L));

		label_7 = new JLabel("Byte = "); //$NON-NLS-1$
		pnlInfo.add(label_7, "7, 14, 3, 1"); //$NON-NLS-1$

		btnRecalcSize = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
		pnlInfo.add(btnRecalcSize, "5, 16"); //$NON-NLS-1$

		lblFileSize = new JLabel();
		pnlInfo.add(lblFileSize, "5, 18, fill, fill"); //$NON-NLS-1$

		lblDirtyAddDate = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyAddDate, "2, 20"); //$NON-NLS-1$

		label_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		pnlInfo.add(label_4, "3, 20"); //$NON-NLS-1$

		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), MIN_DATE, null);
		pnlInfo.add(spnAddDate, "5, 20"); //$NON-NLS-1$

		btnToday = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
		pnlInfo.add(btnToday, "7, 20, 3, 1"); //$NON-NLS-1$

		lblDirtyPath = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyPath, "2, 22"); //$NON-NLS-1$

		lblNewLabel = new JLabel(LocaleBundle.getString("AddEpisodeFrame.lblPart.text")); //$NON-NLS-1$
		pnlInfo.add(lblNewLabel, "3, 22"); //$NON-NLS-1$

		edPart = new ReadableTextField();
		pnlInfo.add(edPart, "5, 22"); //$NON-NLS-1$
		edPart.setColumns(10);

		btnOpen = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		pnlInfo.add(btnOpen, "7, 22, 3, 1"); //$NON-NLS-1$

		lblDirtyHistory = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyHistory, "2, 24, default, top"); //$NON-NLS-1$

		lblNewLabel_2 = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
		pnlInfo.add(lblNewLabel_2, "3, 24, default, top"); //$NON-NLS-1$

		ctrlHistory = new DateTimeListEditor();
		pnlInfo.add(ctrlHistory, "5, 24, 5, 1, fill, fill"); //$NON-NLS-1$

		panel_1 = new JPanel();
		pnlInfo.add(panel_1, "3, 28, 7, 1, fill, fill"); //$NON-NLS-1$
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("96px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("51px"), //$NON-NLS-1$
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				ColumnSpec.decode("60px"),}, //$NON-NLS-1$
				new RowSpec[] {
						RowSpec.decode("26px"),})); //$NON-NLS-1$

		btnEpCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		panel_1.add(btnEpCancel, "1, 1, left, top"); //$NON-NLS-1$
		btnEpCancel.addActionListener(e -> cancelInfoDisplay());

		btnEpOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		panel_1.add(btnEpOk, "3, 1, left, top"); //$NON-NLS-1$

		btnNext = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnNext.text")); //$NON-NLS-1$
		panel_1.add(btnNext, "5, 1, left, top"); //$NON-NLS-1$
		btnNext.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12)); //$NON-NLS-1$
		btnNext.addActionListener(e -> onBtnNext());
		btnEpOk.addActionListener(e -> okInfoDisplay(true));
		btnOpen.addActionListener(e -> openPart());
		btnToday.addActionListener(arg0 -> spnAddDate.setValue(CCDate.getCurrentDate()));
		btnRecalcSize.addActionListener(e -> recalcFilesize());
		spnSize.addChangeListener(arg0 -> updateFilesizeDisplay());
		btnMediaInfo1.addActionListener(e -> parseCodecMetadata_Lang());
		btnMediaInfo3.addActionListener(e -> parseCodecMetadata_MediaInfo());
		btnMediaInfoRaw.addActionListener(e -> showCodecMetadata());
	}

	private void initFileChooser() {
		//$NON-NLS-1$
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$

		videoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$

		// ######################################################################################################################

		massVideoFileChooser.setMultiSelectionEnabled(true);

		//$NON-NLS-1$
		massVideoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$

		massVideoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$
	}

	private void onOKClicked(boolean check) {
		if (check) {
			var allproblems = new ArrayList<Tuple<String, UserDataProblem>>();

			for (BatchEditEpisodeData episode : data) {
				List<UserDataProblem> problems = new ArrayList<>();
				UserDataProblem.testEpisodeData(problems, target, episode.getSource(), episode.getTitle(), episode.getLength(), episode.getEpisodeNumber(), episode.getAddDate(), episode.getViewedHistory(), episode.getFilesize().getBytes(), episode.getFormat().asString(), episode.getFormat().asStringAlt(), episode.getPart(), episode.getMediaInfo(), episode.getLanguage());
				allproblems.addAll(CCStreams.iterate(problems).map(p -> Tuple.Create((String.format("[%d] %s", episode.getEpisodeNumber(), episode.getTitle())), p)).toList());//$NON-NLS-1$
			}

			if (allproblems.size() > 0) {
				InputErrorDialog amied = new InputErrorDialog(allproblems, () -> onOKClicked(false), this, true);
				amied.setVisible(true);
				return;
			}
		}

		for (BatchEditEpisodeData episode : data) {
			episode.apply();
		}

		if (listener != null) {
			listener.onUpdate(null);
		}

		dispose();
	}

	private void openPart() {
		int returnval = videoFileChooser.showOpenDialog(this);

		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}

		setFilepath(videoFileChooser.getSelectedFile().getAbsolutePath());

		recalcFilesize();

		testPart();
	}

	private void onBtnNext() {
		int curr = lsEpisodes.getSelectedIndex();
		boolean retval = okInfoDisplay(true);

		if (retval) {
			curr++;

			if (curr < lsEpisodes.getModel().getSize()) {
				lsEpisodes.setSelectedIndex(curr);

				edTitle.requestFocus();

				edTitle.selectAll();
			}
		}
	}

	private BatchEditEpisodeData getSelected() {
		int index = lsEpisodes.getSelectedIndex();

		if (index < 0) {
			return null;
		} else {
			return data.get(index);
		}
	}

	private boolean okInfoDisplay(boolean check) {
		BatchEditEpisodeData episode = getSelected();

		if (episode == null) return false;

		List<UserDataProblem> problems = new ArrayList<>();
		boolean probvalue = (!check) || checkUserData(problems);

		if (!probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, this, this);
			amied.setVisible(true);
			return false;
		}

		episode.setTitle(edTitle.getText());
		episode.setEpisodeNumber((int) spnEpisode.getValue());
		episode.setFormat(cbxFormat.getSelectedEnum());
		episode.setLength((int) spnLength.getValue());
		episode.setFilesize(new CCFileSize((long) spnSize.getValue()));
		episode.setAddDate(spnAddDate.getValue());
		episode.setPart(edPart.getText());
		episode.setLanguage(ctrlLang.getValue());
		episode.setMediaInfo(ctrlMediaInfo.getValue());
		episode.setViewedHistory(ctrlHistory.getValue());

		lsEpisodes.setSelectedIndex(-1);
		updateList();

		return true;
	}

	private boolean checkUserData(List<UserDataProblem> ret) {
		BatchEditEpisodeData sel = getSelected();
		if (sel == null) return false;

		String title = edTitle.getText();

		int len = (int) spnLength.getValue();
		int epNum = (int) spnEpisode.getValue();
		CCDate adddate = spnAddDate.getValue();
		CCDateTimeList lvdate = CCDateTimeList.createEmpty();
		CCDBLanguageList lang = ctrlLang.getValue();

		long fsize = (long) spnSize.getValue();
		String csExtn = cbxFormat.getSelectedEnum().asString();
		String csExta = cbxFormat.getSelectedEnum().asStringAlt();

		CCMediaInfo minfo = ctrlMediaInfo.getValue();

		String part = edPart.getText();

		UserDataProblem.testEpisodeData(ret, target, sel.getSource(), title, len, epNum, adddate, lvdate, fsize, csExtn, csExta, part, minfo, lang);

		return ret.isEmpty();
	}

	private void cancelInfoDisplay() {
		lsEpisodes.setSelectedIndex(-1);
		updateDisplayPanel();
	}

	private void updateList() {
		DefaultListModel<String> model = new DefaultListModel<>();
		lsEpisodes.setModel(model);

		model.clear();

		for (int i = 0; i < data.size(); i++) {
			model.add(i, data.get(i).getTitle() + (data.get(i).isDirty ? " **" : "")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		lsEpisodes.setSelectedIndex(-1);

		updateDisplayPanel();
	}

	private List<String> getTitleList() {
		List<String> result = new ArrayList<>();

		for (int i = 0; i < data.size(); i++) {
			result.add(i, data.get(i).getTitle());
		}

		return result;
	}

	private String getCommonFolderPathStart() {
		List<String> paths = new ArrayList<>();

		for (int i = 0; i < data.size(); i++) {
			paths.add(i, data.get(i).getPart());
		}

		return PathFormatter.fromCCPath(PathFormatter.getCommonFolderPath(paths));
	}

	private void updateDisplayPanel() {
		BatchEditEpisodeData episode = getSelected();

		if (episode == null) {
			pnlInfo.setVisible(false);
		} else {
			pnlInfo.setVisible(true);

			edTitle.setText(episode.getTitle());
			spnEpisode.setValue(episode.getEpisodeNumber());
			cbxFormat.setSelectedEnum(episode.getFormat());
			spnLength.setValue(episode.getLength());
			spnSize.setValue(episode.getFilesize().getBytes());
			spnAddDate.setValue(episode.getAddDate());
			edPart.setText(episode.getPart());
			ctrlLang.setValue(episode.getLanguage());
			ctrlMediaInfo.setValue(episode.getMediaInfo());
			ctrlHistory.setValue(episode.getViewedHistory());

			lblDirtyTitle.setVisible(episode.titleDirty);
			lblDirtyEpisodeNumber.setVisible(episode.episodeNumberDirty);
			lblDirtyFormat.setVisible(episode.formatDirty);
			lblDirtyLength.setVisible(episode.lengthDirty);
			lblDirtySize.setVisible(episode.filesizeDirty);
			lblDirtyAddDate.setVisible(episode.addDateDirty);
			lblDirtyPath.setVisible(episode.partDirty);
			lblDirtyLanguage.setVisible(episode.languageDirty);
			lblDirtyMediaInfo.setVisible(episode.mediaInfoDirty);
			lblDirtyHistory.setVisible(episode.viewedHistoryDirty);

			testPart();

			updateFilesizeDisplay();
		}
	}

	private void updateFilesizeDisplay() {
		lblFileSize.setText(FileSizeFormatter.format((long) spnSize.getValue()));
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		okInfoDisplay(false);
	}

	private void setFilepath(String t) {
		String pt = PathFormatter.getCCPath(t);

		edPart.setText(pt);
		edPart.setCaretPosition(0);

		updateFilesizeDisplay();
	}

	private void recalcFilesize() {
		String part = edPart.getText();
		part = PathFormatter.fromCCPath(part);
		long fs = FileSizeFormatter.getFileSize(part);

		if (fs > 0) {
			spnSize.setValue(fs);
		}

		updateFilesizeDisplay();
	}

	private void testPart() {
		String part = edPart.getText();
		part = PathFormatter.fromCCPath(part);

		if (new File(part).exists()) {
			edPart.setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		} else {
			edPart.setBackground(Color.RED);
		}
	}

	private void delFirstChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) {
			try {
				ep.setTitle(ep.getTitle().substring((int) spnSide_01.getValue()));
			} catch (IndexOutOfBoundsException e) {
				// doesnt mind...
			}
		}

		updateList();
	}

	private void delLastChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) {
			try {
				ep.setTitle(ep.getTitle().substring(0, ep.getTitle().length() - (int) spnSide_02.getValue()));
			} catch (IndexOutOfBoundsException e) {
				// doesnt mind...
			}
		}

		updateList();
	}

	private void replaceChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setTitle(ep.getTitle().replace(edSide_01.getText(), edSide_02.getText()));

		updateList();
	}

	private void replaceCharsRegex() {
		lsEpisodes.setSelectedIndex(-1);

		StringBuilder err = new StringBuilder();
		for (BatchEditEpisodeData ep : data)
		{
			try
			{
				ep.setTitle(ep.getTitle().replaceAll(edSide_R1.getText(), edSide_R2.getText()));
			}
			catch (Exception e) {
				err.append("[").append(ep.getEpisodeNumber()).append("] ").append(ep.getTitle()).append("\n").append(ExceptionUtils.getMessage(e)).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
		}

		updateList();

		if (!err.toString().isEmpty()) GenericTextDialog.showText(this, getTitle(), err.toString(), true);
	}

	private void trimChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setTitle(ep.getTitle().trim());

		updateList();
	}

	private void concatStartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setTitle(edSide_03.getText().concat(ep.getTitle()));

		updateList();
	}

	private void concatEndChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setTitle(ep.getTitle().concat(edSide_04.getText()));

		updateList();
	}

	private void deleteChars() {
		int start = (int) spnSide_03.getValue();
		int end = (int) spnSide_04.getValue();

		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) {

			try {
				String title = ep.getTitle();
				title = title.substring(0, start).concat(title.substring(end));

				ep.setTitle(title);
			} catch (IndexOutOfBoundsException e) {
				// doesnt mind...
			}
		}

		updateList();
	}

	private void searchAndDeletePartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(ep.getPart().replace(edSidePart_05.getText(), "")); //$NON-NLS-1$

		updateList();
	}

	private void delFirstPartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) {
			try {
				ep.setPart(ep.getPart().substring((int) spnSidePart_01.getValue()));
			} catch (IndexOutOfBoundsException e) {
				// doesnt mind...
			}
		}

		updateList();
	}

	private void delLastPartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) {
			try {
				ep.setPart(ep.getPart().substring(0, ep.getPart().length() - (int) spnSidePart_02.getValue()));
			} catch (IndexOutOfBoundsException e) {
				// doesnt mind...
			}
		}

		updateList();
	}

	private void replacePartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(ep.getPart().replace(edSidePart_01.getText(), edSidePart_02.getText()));

		updateList();
	}

	private void replacePartCharsRegex() {
		lsEpisodes.setSelectedIndex(-1);

		StringBuilder err = new StringBuilder();
		for (BatchEditEpisodeData ep : data)
		{
			try
			{
				ep.setPart(ep.getPart().replaceAll(edSidePart_R1.getText(), edSidePart_R2.getText()));
			}
			catch (Exception e) {
				err.append("[").append(ep.getEpisodeNumber()).append("] ").append(ep.getTitle()).append("\n").append(ExceptionUtils.getMessage(e)).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}

		}

		updateList();

		if (!err.toString().isEmpty()) GenericTextDialog.showText(this, getTitle(), err.toString(), true);
	}

	private void trimPartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(ep.getPart().trim());

		updateList();
	}

	private void concatStartPartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(edSidePart_03.getText().concat(ep.getPart()));

		updateList();
	}

	private void concatEndPartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(ep.getPart().concat(edSidePart_04.getText()));

		updateList();
	}

	private void deletePartChars() {
		int start = (int) spnSidePart_03.getValue();
		int end = (int) spnSidePart_04.getValue();

		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) {

			try {
				String title = ep.getPart();
				title = title.substring(0, start).concat(title.substring(end));

				ep.setPart(title);
			} catch (IndexOutOfBoundsException e) {
				// doesnt mind...
			}
		}

		updateList();
	}

	private void massIncNumber() {
		int delta = (int) spnSide_05.getValue();

		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setEpisodeNumber(ep.getEpisodeNumber() + delta);

		updateList();
	}

	private void searchAndDeleteChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setTitle(ep.getTitle().replace(edSide_05.getText(), "")); //$NON-NLS-1$

		updateList();
	}

	private void massRecalcSizes() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) {

			File f = new File(PathFormatter.fromCCPath(ep.getPart()));
			if (f.exists()) ep.setFilesize(new CCFileSize(f.length()));
		}

		updateList();
	}

	private void massSetNotViewed() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setViewedHistory(CCDateTimeList.createEmpty());

		updateList();
	}

	private void massSetLength() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setLength((int) spnSideLength.getValue());

		updateList();
	}

	private void massSetFormat() {
		CCFileFormat ff = cbxSideFormat.getSelectedEnum();
		if (ff == null) return;

		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setFormat(ff);

		updateList();
	}

	private void massSetLanguage() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setLanguage(ctrlMultiLang.getValue());

		updateList();
	}

	@Override
	public void updateTitles(List<String> newTitles) {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < Math.min(data.size(), newTitles.size()); i++) data.get(i).setTitle(newTitles.get(i));

		updateList();
	}

	private void massMediaInfoLang() {
		lsEpisodes.setSelectedIndex(-1);

		StringBuilder err = new StringBuilder();

		for (BatchEditEpisodeData ep : data) {
			try {
				MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.getPart()), false);

				if (dat.AudioLanguages == null) {
					err.append("[").append(ep.getEpisodeNumber()).append("] ").append(ep.getTitle()).append("\n").append("No language in file").append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					continue;
				}

				CCDBLanguageList dbll = dat.AudioLanguages;

				if (dbll.isEmpty()) {
					err.append("[").append(ep.getEpisodeNumber()).append("] ").append(ep.getTitle()).append("\n").append("Language is empty").append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
					continue;
				} else {
					ep.setLanguage(dbll);
				}

			} catch (IOException | MediaQueryException e) {
				err.append("[").append(ep.getEpisodeNumber()).append("] ").append(ep.getTitle()).append("\n").append(ExceptionUtils.getMessage(e)).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
		}

		updateList();

		if (!err.toString().isEmpty()) GenericTextDialog.showText(this, getTitle(), err.toString(), true);
	}

	private void massMediaInfoLen() {
		lsEpisodes.setSelectedIndex(-1);

		StringBuilder err = new StringBuilder();

		for (BatchEditEpisodeData ep : data) {
			try {
				MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.getPart()), true);

				int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
				if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
				ep.setLength(dur);

			} catch (IOException | MediaQueryException e) {
				err.append("[").append(ep.getEpisodeNumber()).append("] ").append(ep.getTitle()).append("\n").append(ExceptionUtils.getMessage(e)).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
		}

		updateList();

		if (!err.toString().isEmpty()) GenericTextDialog.showText(this, getTitle(), err.toString(), true);
	}

	private void massMediaInfo() {
		lsEpisodes.setSelectedIndex(-1);

		StringBuilder err = new StringBuilder();

		for (BatchEditEpisodeData ep : data) {
			try {
				MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.getPart()), true);
				CCMediaInfo minfo = dat.toMediaInfo();

				if (minfo.isSet()) ep.setMediaInfo(minfo);

			} catch (IOException | MediaQueryException e) {
				err.append("[").append(ep.getEpisodeNumber()).append("] ").append(ep.getTitle()).append("\n").append(ExceptionUtils.getMessage(e)).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
		}

		updateList();

		if (!err.toString().isEmpty()) GenericTextDialog.showText(this, getTitle(), err.toString(), true);
	}

	private void autoMetaDataCalc() {
		lsEpisodes.setSelectedIndex(-1);

		Integer len = target.getCommonEpisodeLength();
		if (len == 0) len = target.getConsensEpisodeLength();

		while (len == null || len <= 0) {
			try {
				String dialogresult = DialogHelper.showLocalInputDialog(this, "AddEpisodeFrame.inputMetaTextLenDialog.text", "0"); //$NON-NLS-1$ //$NON-NLS-2$

				if (dialogresult == null) return; // abort

				len = Integer.parseInt(dialogresult);
			} catch (NumberFormatException nfe) {
				len = -1;
			}
		}

		//####################################

		for (BatchEditEpisodeData ep : data) {
			File f = new File(PathFormatter.fromCCPath(ep.getPart()));
			if (f.exists()) {
				ep.setFilesize(new CCFileSize(f.length()));
			}

			ep.setLength(len);

			ep.setFormat(CCFileFormat.getMovieFormatOrDefault(PathFormatter.getExtension(PathFormatter.fromCCPath(ep.getPart()))));
		}

		//####################################

		updateList();
	}

	private void parseCodecMetadata_Lang() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(edPart.getText()), false);

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
			MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(edPart.getText()), true);

			int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			spnLength.setValue(dur);

		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void parseCodecMetadata_MediaInfo() {
		String mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqp) || !new File(mqp).exists() || !new File(mqp).isFile() || !new File(mqp).canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(edPart.getText()), true);
			CCMediaInfo minfo = dat.toMediaInfo();

			ctrlMediaInfo.setValue(minfo);
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
			String dat = MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(edPart.getText()));

			GenericTextDialog.showText(this, getTitle(), dat, false);
		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void massAddToHistory(CCDateTime t) {
		if (t == null) return;

		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setViewedHistory(ep.getViewedHistory().add(t));

		updateList();
	}

	private void massClearHistory() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setViewedHistory(CCDateTimeList.createEmpty());

		updateList();
	}

	private void convertPathToCCPath() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(PathFormatter.getCCPath(ep.getPart()));

		updateList();
	}

	private void convertPathFromCCPath() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(PathFormatter.fromCCPath(ep.getPart()));

		updateList();
	}

	private void deleteExtension() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(PathFormatter.getWithoutExtension(ep.getPart()));

		updateList();
	}

	private void massCalcHash() {
		lsEpisodes.setSelectedIndex(-1);

		try 
		{
			for (BatchEditEpisodeData ep : data) 
			{
				var p = ep.getMediaInfo().toPartial();
				p.Checksum = Opt.of(ChecksumHelper.fastVideoHash(new File(PathFormatter.fromCCPath(ep.getPart()))));
				ep.setMediaInfo(p.toMediaInfo());
			}
		} catch (IOException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}

		updateList();
	}

	private void deleteFilenameWithExtension() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(PathFormatter.getDirectory(ep.getPart()));

		updateList();
	}

	private void deleteFilenameWithoutExtension() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(PathFormatter.getDirectory(ep.getPart()).concat(PathFormatter.getExtension(ep.getPart())));

		updateList();
	}

	private void deleteFilepath() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setPart(PathFormatter.getFilenameWithExt(ep.getPart()));

		updateList();
	}

}
