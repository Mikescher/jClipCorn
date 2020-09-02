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
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.OmniParserCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.exception.ExceptionUtils;

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

	private static final CCDate MIN_DATE = CCDate.getMinimumDate();

	protected JList<String> lsEpisodes;
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
	private JButton btnCalcHash;
	private JPanel pnlReset;
	private JButton btnNewButton_4;
	private JButton btnNewButton_5;
	private JButton btnNewButton_6;
	private JButton btnNewButton_7;
	private JButton btnNewButton_8;
	private JButton btnNewButton_9;
	private JButton btnNewButton_10;
	private JButton btnNewButton_11;
	private JButton btnNewButton_12;
	private JButton btnNewButton_13;
	private JButton btnNewButton_14;
	private JButton btnNewButton_15;
	private JButton btnNewButton_16;
	private JButton btnNewButton_17;
	private JSpinner spnPathOpenOffset;
	protected JProgressBar batchProgress;
	protected JPanel pnlRoot;


	private final IEpisodeOwner target;
	protected final List<BatchEditEpisodeData> data;
	private final JFileChooser videoFileChooser;
	private final JFileChooser massVideoFileChooser;
	private boolean amied_isButtonNext = false;
	private JCheckBox chckbxIgnoreUserDataErrors;


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
		
		pnlRoot = new JPanel();
		pnlRoot.setLayout(new FormLayout(new ColumnSpec[] {
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
		getContentPane().add(pnlRoot);

		batchProgress = new JProgressBar();
		pnlRoot.add(batchProgress, "6, 2, fill, fill"); //$NON-NLS-1$
		
		scrollPane = new JScrollPane();
		pnlRoot.add(scrollPane, "2, 4, fill, fill"); //$NON-NLS-1$

		lsEpisodes = new JList<>();
		lsEpisodes.setCellRenderer(new HFixListCellRenderer());
		lsEpisodes.addListSelectionListener(arg0 -> updateDisplayPanel());
		lsEpisodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(lsEpisodes);

		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOKClicked(true));

		panel = new JPanel();
		pnlRoot.add(panel, "2, 6, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("23px"),})); //$NON-NLS-1$

		btnOmniparser = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnOmniParser.text")); //$NON-NLS-1$
		panel.add(btnOmniparser, "1, 1, fill, fill"); //$NON-NLS-1$

		btnOmniparser.addActionListener(arg0 -> {
			OmniParserFrame oframe = new OmniParserFrame(BatchEditFrame.this, BatchEditFrame.this, getTitleList(), getCommonFolderPathStart(), Str.Empty, false);
			oframe.setVisible(true);
		});

		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		pnlRoot.add(tabbedPane, "6, 4, 1, 5, fill, fill"); //$NON-NLS-1$

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
		btnPathConvert.addActionListener(e -> BatchEditMethods.PATH_TO_CCPATH.run(this, null));

		btnPathConvertBack = new JButton(LocaleBundle.getString("BatchEditFrame.ConvertFromCC")); //$NON-NLS-1$
		pnlPartEdit.add(btnPathConvertBack, "6, 2, 5, 1"); //$NON-NLS-1$
		btnPathConvertBack.addActionListener(e -> BatchEditMethods.PATH_FROM_CCPATH.run(this, null));

		btnNewButton = new JButton(LocaleBundle.getString("BatchEditFrame.DeleteFilename")); //$NON-NLS-1$
		pnlPartEdit.add(btnNewButton, "2, 6, 5, 1"); //$NON-NLS-1$
		btnNewButton.addActionListener(e -> BatchEditMethods.PATH_DELETE_FILENAME_WITH_EXT.run(this, null));

		btnNewButton_1 = new JButton(LocaleBundle.getString("BatchEditFrame.DeleteFileNameWithoutExt")); //$NON-NLS-1$
		pnlPartEdit.add(btnNewButton_1, "2, 8, 5, 1"); //$NON-NLS-1$
		btnNewButton_1.addActionListener(e -> BatchEditMethods.PATH_DELETE_FILENAME_WITHOUT_EXT.run(this, null));

		btnNewButton_2 = new JButton(LocaleBundle.getString("BatchEditFrame.DeletePath")); //$NON-NLS-1$
		pnlPartEdit.add(btnNewButton_2, "2, 10, 5, 1"); //$NON-NLS-1$
		btnNewButton_2.addActionListener(e -> BatchEditMethods.PATH_DELETE_FILEPATH.run(this, null));

		btnNewButton_3 = new JButton(LocaleBundle.getString("BatchEditFrame.DeleteExt")); //$NON-NLS-1$
		pnlPartEdit.add(btnNewButton_3, "2, 12, 5, 1"); //$NON-NLS-1$
		btnNewButton_3.addActionListener(e -> BatchEditMethods.PATH_DELETE_EXTENSION.run(this, null));

		btnSidePart_01 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteFirst.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_01, "2, 16, 5, 1"); //$NON-NLS-1$
		btnSidePart_01.addActionListener(e -> BatchEditMethods.PATH_DELETE_FIRST_CHARS.run(this, (int) spnSidePart_01.getValue()));

		spnSidePart_01 = new JSpinner();
		pnlPartEdit.add(spnSidePart_01, "8, 16, 3, 1"); //$NON-NLS-1$
		spnSidePart_01.setModel(new SpinnerNumberModel(1, 0, null, 1));

		btnSidePart_02 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteLast.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_02, "2, 18, 5, 1"); //$NON-NLS-1$
		btnSidePart_02.addActionListener(e -> BatchEditMethods.PATH_DELETE_LAST_CHARS.run(this, (int) spnSidePart_02.getValue()));

		spnSidePart_02 = new JSpinner();
		pnlPartEdit.add(spnSidePart_02, "8, 18, 3, 1"); //$NON-NLS-1$
		spnSidePart_02.setModel(new SpinnerNumberModel(1, null, null, 1));

		edSidePart_01 = new JTextField();
		pnlPartEdit.add(edSidePart_01, "2, 22"); //$NON-NLS-1$
		edSidePart_01.setColumns(10);

		btnSidePart_03 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnReplace.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_03, "4, 22, 3, 1"); //$NON-NLS-1$
		btnSidePart_03.addActionListener(e -> BatchEditMethods.PATH_STRING_REPLACE.run(this, Tuple.Create(edSidePart_01.getText(), edSidePart_02.getText())));

		edSidePart_02 = new JTextField();
		pnlPartEdit.add(edSidePart_02, "8, 22, 3, 1"); //$NON-NLS-1$
		edSidePart_02.setColumns(10);

		edSidePart_R1 = new JTextField();
		pnlPartEdit.add(edSidePart_R1, "2, 24, fill, default"); //$NON-NLS-1$
		edSidePart_R1.setColumns(10);

		btnRegexReplacePart = new JButton(LocaleBundle.getString("BatchEditFrame.ReplaceRegex")); //$NON-NLS-1$
		pnlPartEdit.add(btnRegexReplacePart, "4, 24, 3, 1"); //$NON-NLS-1$
		btnRegexReplacePart.addActionListener(e -> BatchEditMethods.PATH_STRING_REPLACE.run(this, Tuple.Create(edSidePart_R1.getText(), edSidePart_R2.getText())));

		edSidePart_R2 = new JTextField();
		pnlPartEdit.add(edSidePart_R2, "8, 24, 3, 1, fill, default"); //$NON-NLS-1$
		edSidePart_R2.setColumns(10);

		btnSidePart_04 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnTrim.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_04, "2, 28, 9, 1"); //$NON-NLS-1$
		btnSidePart_04.addActionListener(e -> BatchEditMethods.PATH_TRIM.run(this, null));

		edSidePart_03 = new JTextField();
		pnlPartEdit.add(edSidePart_03, "2, 30"); //$NON-NLS-1$
		edSidePart_03.setColumns(10);

		btnSidePart_05 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatStart.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_05, "4, 30, 7, 1"); //$NON-NLS-1$
		btnSidePart_05.addActionListener(e -> BatchEditMethods.PATH_PREPEND.run(this, edSidePart_03.getText()));

		edSidePart_04 = new JTextField();
		pnlPartEdit.add(edSidePart_04, "2, 32"); //$NON-NLS-1$
		edSidePart_04.setColumns(10);

		btnSidePart_06 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatEnd.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_06, "4, 32, 7, 1"); //$NON-NLS-1$
		btnSidePart_06.addActionListener(e -> BatchEditMethods.PATH_APPEND.run(this, edSidePart_04.getText()));

		btnSidePart_07 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDelete.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_07, "2, 36, 5, 1"); //$NON-NLS-1$
		btnSidePart_07.addActionListener(e -> BatchEditMethods.PATH_SUBSTRING_DELETE.run(this, Tuple.Create((int) spnSidePart_03.getValue(), (int) spnSidePart_04.getValue())));

		spnSidePart_03 = new JSpinner();
		pnlPartEdit.add(spnSidePart_03, "8, 36"); //$NON-NLS-1$

		spnSidePart_04 = new JSpinner();
		pnlPartEdit.add(spnSidePart_04, "10, 36"); //$NON-NLS-1$

		edSidePart_05 = new JTextField();
		pnlPartEdit.add(edSidePart_05, "2, 40"); //$NON-NLS-1$
		edSidePart_05.setColumns(10);

		btnSidePart_08 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSearchAndDel.text")); //$NON-NLS-1$
		pnlPartEdit.add(btnSidePart_08, "4, 40, 7, 1"); //$NON-NLS-1$
		btnSidePart_08.addActionListener(e -> BatchEditMethods.PATH_SEARCH_AND_DELETE.run(this, edSidePart_05.getText()));


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
				RowSpec.decode("22px"), //$NON-NLS-1$
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
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("22px:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("22px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("22px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("22px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		btnSide_01 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteFirst.text")); //$NON-NLS-1$
		btnSide_01.addActionListener(e -> BatchEditMethods.TITLE_DELETE_FIRST_CHARS.run(this, (int) spnSide_01.getValue()));
		pnlTitleEdit.add(btnSide_01, "2, 2, 5, 1"); //$NON-NLS-1$

		spnSide_01 = new JSpinner();
		pnlTitleEdit.add(spnSide_01, "8, 2, 3, 1"); //$NON-NLS-1$
		spnSide_01.setModel(new SpinnerNumberModel(1, 0, null, 1));

		btnSide_02 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteLast.text")); //$NON-NLS-1$
		btnSide_02.addActionListener(e -> BatchEditMethods.TITLE_DELETE_LAST_CHARS.run(this, (int) spnSide_02.getValue()));
		pnlTitleEdit.add(btnSide_02, "2, 4, 5, 1"); //$NON-NLS-1$

		spnSide_02 = new JSpinner();
		pnlTitleEdit.add(spnSide_02, "8, 4, 3, 1"); //$NON-NLS-1$
		spnSide_02.setModel(new SpinnerNumberModel(1, null, null, 1));

		edSide_01 = new JTextField();
		pnlTitleEdit.add(edSide_01, "2, 8"); //$NON-NLS-1$
		edSide_01.setColumns(10);

		btnSide_03 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnReplace.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_03, "4, 8, 3, 1"); //$NON-NLS-1$
		btnSide_03.addActionListener(e -> BatchEditMethods.TITLE_STRING_REPLACE.run(this, Tuple.Create(edSide_01.getText(), edSide_02.getText())));

		edSide_02 = new JTextField();
		pnlTitleEdit.add(edSide_02, "8, 8, 3, 1"); //$NON-NLS-1$
		edSide_02.setColumns(10);

		edSide_R1 = new JTextField();
		pnlTitleEdit.add(edSide_R1, "2, 10, fill, default"); //$NON-NLS-1$
		edSide_R1.setColumns(10);

		btnRegexReplace = new JButton(LocaleBundle.getString("BatchEditFrame.ReplaceRegex")); //$NON-NLS-1$
		pnlTitleEdit.add(btnRegexReplace, "4, 10, 3, 1"); //$NON-NLS-1$
		btnRegexReplace.addActionListener(e -> BatchEditMethods.TITLE_REGEX_REPLACE.run(this, Tuple.Create(edSide_R1.getText(), edSide_R2.getText())));

		edSide_R2 = new JTextField();
		pnlTitleEdit.add(edSide_R2, "8, 10, 3, 1, fill, default"); //$NON-NLS-1$
		edSide_R2.setColumns(10);

		btnSide_04 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnTrim.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_04, "2, 14, 9, 1"); //$NON-NLS-1$
		btnSide_04.addActionListener(e -> BatchEditMethods.TITLE_TRIM.run(this, null));

		edSide_03 = new JTextField();
		pnlTitleEdit.add(edSide_03, "2, 16"); //$NON-NLS-1$
		edSide_03.setColumns(10);

		btnSide_05 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatStart.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_05, "4, 16, 7, 1"); //$NON-NLS-1$
		btnSide_05.addActionListener(e -> BatchEditMethods.TITLE_PREPEND.run(this, edSide_03.getText()));

		edSide_04 = new JTextField();
		pnlTitleEdit.add(edSide_04, "2, 18"); //$NON-NLS-1$
		edSide_04.setColumns(10);

		btnSide_06 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatEnd.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_06, "4, 18, 7, 1"); //$NON-NLS-1$
		btnSide_06.addActionListener(e -> BatchEditMethods.TITLE_APPEND.run(this, edSide_04.getText()));

		btnSide_07 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDelete.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_07, "2, 22, 5, 1"); //$NON-NLS-1$
		btnSide_07.addActionListener(e -> BatchEditMethods.TITLE_SUBSTRING_DELETE.run(this, Tuple.Create((int) spnSide_03.getValue(), (int) spnSide_04.getValue())));

		spnSide_03 = new JSpinner();
		pnlTitleEdit.add(spnSide_03, "8, 22"); //$NON-NLS-1$

		spnSide_04 = new JSpinner();
		pnlTitleEdit.add(spnSide_04, "10, 22"); //$NON-NLS-1$

		edSide_05 = new JTextField();
		pnlTitleEdit.add(edSide_05, "2, 26"); //$NON-NLS-1$
		edSide_05.setColumns(10);

		btnSide_08 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSearchAndDel.text")); //$NON-NLS-1$
		pnlTitleEdit.add(btnSide_08, "4, 26, 7, 1"); //$NON-NLS-1$
		btnSide_08.addActionListener(e -> BatchEditMethods.TITLE_SEARCH_AND_DELETE.run(this, edSide_05.getText()));

		spnSide_05 = new JSpinner();
		pnlMiscEdit.add(spnSide_05, "2, 2"); //$NON-NLS-1$
		spnSide_05.setModel(new SpinnerNumberModel(1, null, null, 1));

		btnIncEpisodeNumbers = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnIncEpisodeNumbers.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnIncEpisodeNumbers, "4, 2, 7, 1"); //$NON-NLS-1$
		btnIncEpisodeNumbers.addActionListener(arg0 -> BatchEditMethods.EPISODEINDEX_ADD.run(this, (int) spnSide_05.getValue()));

		ctrlMultiLang = new LanguageChooser();
		pnlMiscEdit.add(ctrlMultiLang, "2, 4, 5, 1"); //$NON-NLS-1$

		btnSpracheSetzen = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetLang.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSpracheSetzen, "8, 4, 3, 1"); //$NON-NLS-1$
		btnSpracheSetzen.addActionListener(arg0 -> BatchEditMethods.LANGUAGE_SET.run(this, ctrlMultiLang.getValue()));

		btnSide_09 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpSize.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSide_09, "2, 6, 9, 1"); //$NON-NLS-1$
		btnSide_09.addActionListener(e -> BatchEditMethods.FILESIZE_FROM_FILE.run(this, null));

		btnSide_11 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetUnviewed.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSide_11, "2, 10, 9, 1"); //$NON-NLS-1$
		btnSide_11.addActionListener(e -> BatchEditMethods.VIEWED_CLEAR.run(this, null));

		btnViewedNow = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetViewedNow.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnViewedNow, "2, 12, 9, 1"); //$NON-NLS-1$
		btnViewedNow.addActionListener(e -> BatchEditMethods.VIEWED_ADD.run(this, CCDateTime.getCurrentDateTime()));

		btnViewedUnknown = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetViewedUndef.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnViewedUnknown, "2, 14, 9, 1"); //$NON-NLS-1$
		btnViewedUnknown.addActionListener(e -> BatchEditMethods.VIEWED_ADD.run(this, CCDateTime.getUnspecified()));

		btnSide_12 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpLength.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSide_12, "2, 18, 5, 1"); //$NON-NLS-1$
		btnSide_12.addActionListener(e -> BatchEditMethods.LENGTH_SET.run(this, (int) spnSideLength.getValue()));

		spnSideLength = new JSpinner();
		pnlMiscEdit.add(spnSideLength, "8, 18, 3, 1"); //$NON-NLS-1$
		spnSideLength.setModel(new SpinnerNumberModel(0, 0, null, 1));

		btnSide_13 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpFormat.text")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSide_13, "2, 20, 5, 1"); //$NON-NLS-1$
		btnSide_13.addActionListener(e -> BatchEditMethods.FORMAT_SET.run(this, cbxSideFormat.getSelectedEnum()));

		cbxSideFormat = new CCEnumComboBox<>(CCFileFormat.getWrapper());
		pnlMiscEdit.add(cbxSideFormat, "8, 20, 3, 1"); //$NON-NLS-1$

		ctrlSideHistoryVal = new JCCDateTimeSpinner();
		pnlMiscEdit.add(ctrlSideHistoryVal, "2, 24, 3, 1"); //$NON-NLS-1$

		btnAddToHistory = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnAddToHistory.text")); //$NON-NLS-1$
		btnAddToHistory.addActionListener(e -> BatchEditMethods.VIEWED_ADD.run(this, ctrlSideHistoryVal.getValue()));
		pnlMiscEdit.add(btnAddToHistory, "6, 24, 5, 1"); //$NON-NLS-1$

		btnClearHistory = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnClearHistory.text")); //$NON-NLS-1$
		btnClearHistory.addActionListener(e -> BatchEditMethods.VIEWED_CLEAR.run(this, null));
		pnlMiscEdit.add(btnClearHistory, "6, 26, 5, 1"); //$NON-NLS-1$
		
		btnNewButton_14 = new JButton(LocaleBundle.getString("BatchEditFrame.btnFormatFromPath")); //$NON-NLS-1$
		btnNewButton_14.addActionListener(e -> BatchEditMethods.FORMAT_FROM_FILE.run(this, null));
		pnlMiscEdit.add(btnNewButton_14, "2, 40, 9, 1"); //$NON-NLS-1$
		
		btnNewButton_16 = new JButton(LocaleBundle.getString("BatchEditFrame.ReadPathFromDialogFull")); //$NON-NLS-1$
		btnNewButton_16.addActionListener(e -> BatchEditMethods.PATH_FROM_DIALOG.run(this, Tuple3.Create(ShowPathFromDialogChooser(), 0, true)));
		pnlMiscEdit.add(btnNewButton_16, "2, 34, 7, 1"); //$NON-NLS-1$
		
		btnNewButton_17 = new JButton(LocaleBundle.getString("BatchEditFrame.ReadPathFromDialogPartial")); //$NON-NLS-1$
		btnNewButton_17.addActionListener(e -> BatchEditMethods.PATH_FROM_DIALOG.run(this, Tuple3.Create(ShowPathFromDialogChooser(), (int) spnPathOpenOffset.getValue(), false)));
		pnlMiscEdit.add(btnNewButton_17, "2, 36, 7, 1"); //$NON-NLS-1$
		
		spnPathOpenOffset = new JSpinner();
		spnPathOpenOffset.setModel(new SpinnerNumberModel(0, 0, null, 1));
		pnlMiscEdit.add(spnPathOpenOffset, "10, 36"); //$NON-NLS-1$
		
		btnNewButton_15 = new JButton(LocaleBundle.getString("BatchEditFrame.ClearMediaInfo")); //$NON-NLS-1$
		btnNewButton_15.addActionListener(e -> BatchEditMethods.MEDIAINFO_CLEAR.run(this, null));
		pnlMiscEdit.add(btnNewButton_15, "2, 30, 9, 1"); //$NON-NLS-1$

		btnSideAutoLang = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnMassSetLang.title")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSideAutoLang, "2, 42, 9, 1"); //$NON-NLS-1$
		btnSideAutoLang.addActionListener(e -> BatchEditMethods.LANGUAGE_FROM_FILE_MEDIAINFO.run(this, null));

		btnSideAutoLen = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnMassSetLen.title")); //$NON-NLS-1$
		pnlMiscEdit.add(btnSideAutoLen, "2, 44, 9, 1"); //$NON-NLS-1$
		btnSideAutoLen.addActionListener(e -> BatchEditMethods.LENGTH_FROM_FILE_MEDIAINFO.run(this, null));

		btnSiedAutoMediaInfo = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnMassSetMediaInfo.title")); //$NON-NLS-1$
		btnSiedAutoMediaInfo.addActionListener(e -> BatchEditMethods.MEDIAINFO_FROM_FILE.run(this, null));
		pnlMiscEdit.add(btnSiedAutoMediaInfo, "2, 46, 5, 1, fill, fill"); //$NON-NLS-1$
		
		btnCalcHash = new JButton(LocaleBundle.getString("BatchEditFrame.HashCalc")); //$NON-NLS-1$
		btnCalcHash.addActionListener(e -> BatchEditMethods.MEDIAINFO_CALC_HASH.run(this, null));
		pnlMiscEdit.add(btnCalcHash, "8, 46, 3, 1"); //$NON-NLS-1$
		
		pnlReset = new JPanel();
		tabbedPane.addTab(LocaleBundle.getString("BatchEditFrame.TabReset"), null, pnlReset, null); //$NON-NLS-1$
		pnlReset.setLayout(new FormLayout(new ColumnSpec[] {
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		btnNewButton_4 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetTitle")); //$NON-NLS-1$
		btnNewButton_4.addActionListener(e -> BatchEditMethods.TITLE_RESET.run(this, null));
		pnlReset.add(btnNewButton_4, "2, 2"); //$NON-NLS-1$
		
		btnNewButton_5 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetEpisodenumber")); //$NON-NLS-1$
		btnNewButton_5.addActionListener(e -> BatchEditMethods.EPISODEINDEX_RESET.run(this, null));
		pnlReset.add(btnNewButton_5, "2, 4"); //$NON-NLS-1$
		
		btnNewButton_6 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetFormat")); //$NON-NLS-1$
		btnNewButton_6.addActionListener(e -> BatchEditMethods.FORMAT_RESET.run(this, null));
		pnlReset.add(btnNewButton_6, "2, 6"); //$NON-NLS-1$
		
		btnNewButton_7 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetMediaInfo")); //$NON-NLS-1$
		btnNewButton_7.addActionListener(e -> BatchEditMethods.MEDIAINFO_RESET.run(this, null));
		pnlReset.add(btnNewButton_7, "2, 8"); //$NON-NLS-1$
		
		btnNewButton_8 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetLanguage")); //$NON-NLS-1$
		btnNewButton_8.addActionListener(e -> BatchEditMethods.LANGUAGE_RESET.run(this, null));
		pnlReset.add(btnNewButton_8, "2, 10"); //$NON-NLS-1$
		
		btnNewButton_9 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetLength")); //$NON-NLS-1$
		btnNewButton_9.addActionListener(e -> BatchEditMethods.LENGTH_RESET.run(this, null));
		pnlReset.add(btnNewButton_9, "2, 12"); //$NON-NLS-1$
		
		btnNewButton_10 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetFilesize")); //$NON-NLS-1$
		btnNewButton_10.addActionListener(e -> BatchEditMethods.FILESIZE_RESET.run(this, null));
		pnlReset.add(btnNewButton_10, "2, 14"); //$NON-NLS-1$
		
		btnNewButton_11 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetAddDate")); //$NON-NLS-1$
		btnNewButton_11.addActionListener(e -> BatchEditMethods.ADDDATE_RESET.run(this, null));
		pnlReset.add(btnNewButton_11, "2, 18"); //$NON-NLS-1$
		
		btnNewButton_12 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetPart")); //$NON-NLS-1$
		btnNewButton_12.addActionListener(e -> BatchEditMethods.PATH_RESET.run(this, null));
		pnlReset.add(btnNewButton_12, "2, 20"); //$NON-NLS-1$
		
		btnNewButton_13 = new JButton(LocaleBundle.getString("BatchEditFrame.ResetViewedHistory")); //$NON-NLS-1$
		btnNewButton_13.addActionListener(e -> BatchEditMethods.VIEWED_RESET.run(this, null));
		pnlReset.add(btnNewButton_13, "2, 22"); //$NON-NLS-1$

		pnlRoot.add(btnOK, "4, 8, center, top"); //$NON-NLS-1$

		lblSeason = new JLabel(target.getTitle());
		lblSeason.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		lblSeason.setHorizontalAlignment(SwingConstants.CENTER);
		pnlRoot.add(lblSeason, "2, 2, fill, fill"); //$NON-NLS-1$

		pnlInfo = new JPanel();
		pnlRoot.add(pnlInfo, "4, 2, 1, 5, fill, fill"); //$NON-NLS-1$
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
		panel_1.add(btnEpCancel, "1, 1, fill, fill"); //$NON-NLS-1$
		btnEpCancel.addActionListener(e -> cancelInfoDisplay());

		btnEpOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		panel_1.add(btnEpOk, "3, 1, fill, fill"); //$NON-NLS-1$
		
		chckbxIgnoreUserDataErrors = new JCheckBox(LocaleBundle.getString("BatchEditFrame.CbxIgnoreUserDataErrors")); //$NON-NLS-1$
		panel_1.add(chckbxIgnoreUserDataErrors, "4, 1, fill, fill"); //$NON-NLS-1$

		btnNext = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnNext.text")); //$NON-NLS-1$
		panel_1.add(btnNext, "5, 1, fill, fill"); //$NON-NLS-1$
		btnNext.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12)); //$NON-NLS-1$
		btnNext.addActionListener(e -> onBtnNext());
		btnEpOk.addActionListener(e -> okInfoDisplay(!chckbxIgnoreUserDataErrors.isSelected(), false));
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
				UserDataProblem.testEpisodeData(problems, target, episode.getSource(), episode.title, episode.length, episode.episodeNumber, episode.addDate, episode.viewedHistory, episode.filesize.getBytes(), episode.format.asString(), episode.format.asStringAlt(), episode.part, episode.mediaInfo, episode.language);
				allproblems.addAll(CCStreams.iterate(problems).map(p -> Tuple.Create((String.format("[%d] %s", episode.episodeNumber, episode.title)), p)).toList());//$NON-NLS-1$
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
		boolean retval = okInfoDisplay(!chckbxIgnoreUserDataErrors.isSelected(), true);

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

	private boolean okInfoDisplay(boolean check, boolean next) {
		BatchEditEpisodeData episode = getSelected();

		if (episode == null) return false;

		List<UserDataProblem> problems = new ArrayList<>();
		boolean probvalue = (!check) || checkUserData(problems);

		if (!probvalue)
		{
			amied_isButtonNext = next;
			InputErrorDialog amied = new InputErrorDialog(problems, this, this);
			amied.setVisible(true);
			return false;
		}

		episode.title         = (edTitle.getText());
		episode.episodeNumber = ((int) spnEpisode.getValue());
		episode.format        = (cbxFormat.getSelectedEnum());
		episode.length        = ((int) spnLength.getValue());
		episode.filesize      = (new CCFileSize((long) spnSize.getValue()));
		episode.addDate       = (spnAddDate.getValue());
		episode.part          = (edPart.getText());
		episode.language      = (ctrlLang.getValue());
		episode.mediaInfo     = (ctrlMediaInfo.getValue());
		episode.viewedHistory = (ctrlHistory.getValue());

		var idx = lsEpisodes.getSelectedIndex();
		lsEpisodes.setSelectedIndex(-1);
		updateList();

		if (next)
		{
			idx++;

			if (idx < lsEpisodes.getModel().getSize())
			{
				lsEpisodes.setSelectedIndex(idx);
				edTitle.requestFocus();
				edTitle.selectAll();
			}
		}
		else
		{
			lsEpisodes.setSelectedIndex(idx);
		}

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

	public void updateList() {
		DefaultListModel<String> model = new DefaultListModel<>();
		lsEpisodes.setModel(model);

		model.clear();

		for (int i = 0; i < data.size(); i++) {
			model.add(i, data.get(i).title + (data.get(i).isDirty() ? " **" : "")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		lsEpisodes.setSelectedIndex(-1);

		updateDisplayPanel();
	}

	private List<String> getTitleList() {
		List<String> result = new ArrayList<>();

		for (int i = 0; i < data.size(); i++) {
			result.add(i, data.get(i).title);
		}

		return result;
	}

	private String getCommonFolderPathStart() {
		List<String> paths = new ArrayList<>();

		for (int i = 0; i < data.size(); i++) {
			paths.add(i, data.get(i).part);
		}

		return PathFormatter.fromCCPath(PathFormatter.getCommonFolderPath(paths));
	}

	private void updateDisplayPanel() {
		BatchEditEpisodeData episode = getSelected();

		if (episode == null) {
			pnlInfo.setVisible(false);
		} else {
			pnlInfo.setVisible(true);

			edTitle.setText(episode.title);
			spnEpisode.setValue(episode.episodeNumber);
			cbxFormat.setSelectedEnum(episode.format);
			spnLength.setValue(episode.length);
			spnSize.setValue(episode.filesize.getBytes());
			spnAddDate.setValue(episode.addDate);
			edPart.setText(episode.part);
			ctrlLang.setValue(episode.language);
			ctrlMediaInfo.setValue(episode.mediaInfo);
			ctrlHistory.setValue(episode.viewedHistory);

			lblDirtyTitle.setVisible(episode.isDirty_Title());
			lblDirtyEpisodeNumber.setVisible(episode.isDirty_EpisodeNumber());
			lblDirtyFormat.setVisible(episode.isDirty_Format());
			lblDirtyLength.setVisible(episode.isDirty_Length());
			lblDirtySize.setVisible(episode.isDirty_Filesize());
			lblDirtyAddDate.setVisible(episode.isDirty_AddDate());
			lblDirtyPath.setVisible(episode.isDirty_Part());
			lblDirtyLanguage.setVisible(episode.isDirty_Language());
			lblDirtyMediaInfo.setVisible(episode.isDirty_MediaInfo());
			lblDirtyHistory.setVisible(episode.isDirty_ViewedHistory());

			testPart();

			updateFilesizeDisplay();
		}
	}

	private void updateFilesizeDisplay() {
		lblFileSize.setText(FileSizeFormatter.format((long) spnSize.getValue()));
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		okInfoDisplay(false, amied_isButtonNext);
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

	@Override
	public void updateTitles(List<String> newTitles) {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < Math.min(data.size(), newTitles.size()); i++) data.get(i).title = (newTitles.get(i));

		updateList();
	}

	private File[] ShowPathFromDialogChooser() {
		var dlg = new JFileChooser(PathFormatter.fromCCPath(target.getSeries().guessSeriesRootPath()));
		dlg.setMultiSelectionEnabled(true);

		int returnval = dlg.showOpenDialog(this);
		if (returnval != JFileChooser.APPROVE_OPTION) return null;

		return dlg.getSelectedFiles();
	}

	public void setPanelEnabled(Container panel, Boolean isEnabled) {
		panel.setEnabled(isEnabled);

		Component[] components = panel.getComponents();

		for (Component component : components)
		{
			if (component instanceof Container) setPanelEnabled((Container) component, isEnabled);
			component.setEnabled(isEnabled);
		}
	}
}
