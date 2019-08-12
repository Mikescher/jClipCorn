package de.jClipCorn.gui.frames.batchEditFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.IEpisodeOwner;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.omniParserFrame.OmniParserFrame;
import de.jClipCorn.gui.guiComponents.HFixListCellRenderer;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.MediaQueryException;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.OmniParserCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.mediaquery.MediaQueryResult;
import de.jClipCorn.util.mediaquery.MediaQueryRunner;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;

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
	private JComboBox<String> cbxFormat;
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
	private JButton btnSide_10;
	private JButton btnSide_11;
	private JButton btnSide_12;
	private JSpinner spnSideLength;
	private JButton btnSide_13;
	private JComboBox<String> cbxSideFormat;
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
	private JPanel pnlEdit;
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

	private final IEpisodeOwner target;
	private final List<BatchEditEpisodeData> data;
	private final JFileChooser videoFileChooser;
	private final JFileChooser massVideoFileChooser;

	private final UpdateCallbackListener listener;

	/**
	 * @wbp.parser.constructor
	 */
	public BatchEditFrame(Component owner, IEpisodeOwner ss, UpdateCallbackListener ucl) { this(owner, ss, null, ucl); }

	public BatchEditFrame(Component owner, IEpisodeOwner ss, List<CCEpisode> eps, UpdateCallbackListener ucl) {
		super();
		setMinimumSize(new Dimension(1150, 750));
		setSize(new Dimension(1200, 750));
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

		setDefaultValues();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getFormattedString("AddEpisodeFrame.this.title", target.getSeries().getTitle())); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("200dlu"), //$NON-NLS-1$
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("215dlu"), //$NON-NLS-1$
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
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

		cbxFormat = new JComboBox<>();
		pnlInfo.add(cbxFormat, "5, 6"); //$NON-NLS-1$
		
		lblDirtyLanguage = new JLabel("*"); //$NON-NLS-1$
		pnlInfo.add(lblDirtyLanguage, "2, 10"); //$NON-NLS-1$

		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		pnlInfo.add(label_1, "3, 10"); //$NON-NLS-1$

		ctrlLang = new LanguageChooser();
		pnlInfo.add(ctrlLang, "5, 10"); //$NON-NLS-1$

		btnMediaInfoRaw = new JButton("..."); //$NON-NLS-1$
		pnlInfo.add(btnMediaInfoRaw, "7, 10"); //$NON-NLS-1$
		btnMediaInfoRaw.setToolTipText("MediaInfo"); //$NON-NLS-1$

		btnMediaInfo1 = new JButton(Resources.ICN_MENUBAR_UPDATECODECDATA.get16x16());
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

		btnMediaInfo2 = new JButton(Resources.ICN_MENUBAR_UPDATECODECDATA.get16x16());
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

		panel_1 = new JPanel();
		pnlInfo.add(panel_1, "3, 26, 7, 1, fill, fill"); //$NON-NLS-1$
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
		
		pnlEdit = new JPanel();
		pnlEdit.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), LocaleBundle.getString("AddEpisodeFrame.pnlEdit.caption"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0))); //$NON-NLS-1$
		getContentPane().add(pnlEdit, "6, 2, 1, 5, fill, fill"); //$NON-NLS-1$
		pnlEdit.setLayout(new FormLayout(new ColumnSpec[] {
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
				RowSpec.decode("22px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("22px:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		btnSide_01 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteFirst.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_01, "2, 2, 5, 1"); //$NON-NLS-1$

		spnSide_01 = new JSpinner();
		pnlEdit.add(spnSide_01, "8, 2, 3, 1"); //$NON-NLS-1$
		spnSide_01.setModel(new SpinnerNumberModel(1, 0, null, 1));

		btnSide_02 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteLast.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_02, "2, 4, 5, 1"); //$NON-NLS-1$

		spnSide_02 = new JSpinner();
		pnlEdit.add(spnSide_02, "8, 4, 3, 1"); //$NON-NLS-1$
		spnSide_02.setModel(new SpinnerNumberModel(1, null, null, 1));

		edSide_01 = new JTextField();
		pnlEdit.add(edSide_01, "2, 6"); //$NON-NLS-1$
		edSide_01.setColumns(10);

		btnSide_03 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnReplace.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_03, "4, 6, 3, 1"); //$NON-NLS-1$

		edSide_02 = new JTextField();
		pnlEdit.add(edSide_02, "8, 6, 3, 1"); //$NON-NLS-1$
		edSide_02.setColumns(10);

		btnSide_04 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnTrim.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_04, "2, 8, 9, 1"); //$NON-NLS-1$

		edSide_03 = new JTextField();
		pnlEdit.add(edSide_03, "2, 10"); //$NON-NLS-1$
		edSide_03.setColumns(10);

		btnSide_05 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatStart.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_05, "4, 10, 7, 1"); //$NON-NLS-1$

		edSide_04 = new JTextField();
		pnlEdit.add(edSide_04, "2, 12"); //$NON-NLS-1$
		edSide_04.setColumns(10);

		btnSide_06 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatEnd.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_06, "4, 12, 7, 1"); //$NON-NLS-1$

		btnSide_07 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDelete.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_07, "2, 14, 5, 1"); //$NON-NLS-1$
		
				spnSide_03 = new JSpinner();
				pnlEdit.add(spnSide_03, "8, 14"); //$NON-NLS-1$

		spnSide_04 = new JSpinner();
		pnlEdit.add(spnSide_04, "10, 14"); //$NON-NLS-1$

		edSide_05 = new JTextField();
		pnlEdit.add(edSide_05, "2, 16"); //$NON-NLS-1$
		edSide_05.setColumns(10);

		btnSide_08 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSearchAndDel.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_08, "4, 16, 7, 1"); //$NON-NLS-1$

		spnSide_05 = new JSpinner();
		pnlEdit.add(spnSide_05, "2, 18"); //$NON-NLS-1$
		spnSide_05.setModel(new SpinnerNumberModel(1, null, null, 1));

		btnIncEpisodeNumbers = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnIncEpisodeNumbers.text")); //$NON-NLS-1$
		pnlEdit.add(btnIncEpisodeNumbers, "4, 18, 7, 1"); //$NON-NLS-1$

		btnSide_09 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpSize.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_09, "2, 20, 9, 1"); //$NON-NLS-1$

		ctrlMultiLang = new LanguageChooser();
		pnlEdit.add(ctrlMultiLang, "2, 22, 5, 1"); //$NON-NLS-1$

		btnSpracheSetzen = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetLang.text")); //$NON-NLS-1$
		pnlEdit.add(btnSpracheSetzen, "8, 22, 3, 1"); //$NON-NLS-1$
		btnSpracheSetzen.addActionListener(arg0 -> massSetLanguage());

		btnSide_10 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetViewed.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_10, "2, 24, 3, 1"); //$NON-NLS-1$

		btnSide_11 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetUnviewed.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_11, "6, 24, 5, 1"); //$NON-NLS-1$

		btnSide_12 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpLength.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_12, "2, 26, 5, 1"); //$NON-NLS-1$

		spnSideLength = new JSpinner();
		pnlEdit.add(spnSideLength, "8, 26, 3, 1"); //$NON-NLS-1$
		spnSideLength.setModel(new SpinnerNumberModel(0, 0, null, 1));

		btnSide_13 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpFormat.text")); //$NON-NLS-1$
		pnlEdit.add(btnSide_13, "2, 28, 5, 1"); //$NON-NLS-1$

		cbxSideFormat = new JComboBox<>();
		pnlEdit.add(cbxSideFormat, "8, 28, 3, 1"); //$NON-NLS-1$

		btnSideAutoLang = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnMassSetLang.title")); //$NON-NLS-1$
		pnlEdit.add(btnSideAutoLang, "2, 36, 9, 1"); //$NON-NLS-1$
		btnSideAutoLang.addActionListener(e -> massMediaInfoLang());

		btnSideAutoLen = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnMassSetLen.title")); //$NON-NLS-1$
		pnlEdit.add(btnSideAutoLen, "2, 38, 9, 1"); //$NON-NLS-1$

		btnSideAutoLen.addActionListener(e -> massMediaInfoLen());
		btnSide_13.addActionListener(e -> massSetFormat());
		btnSide_12.addActionListener(e -> massSetLength());
		btnSide_11.addActionListener(e -> massSetViewed(false));
		btnSide_10.addActionListener(e -> massSetViewed(true));
		btnSide_09.addActionListener(e -> massRecalcSizes());
		btnIncEpisodeNumbers.addActionListener(arg0 -> massIncNumber());
		btnSide_08.addActionListener(e -> searchAndDeleteChars());
		btnSide_07.addActionListener(e -> deleteChars());
		btnSide_06.addActionListener(e -> concatEndChars());
		btnSide_05.addActionListener(e -> concatStartChars());
		btnSide_04.addActionListener(e -> trimChars());
		btnSide_03.addActionListener(e -> replaceChars());
		btnSide_02.addActionListener(e -> delLastChars());
		btnSide_01.addActionListener(e -> delFirstChars());
		btnNext.addActionListener(e -> onBtnNext());
		btnEpOk.addActionListener(e -> okInfoDisplay(true));
		btnOpen.addActionListener(e -> openPart());
		btnToday.addActionListener(arg0 -> spnAddDate.setValue(CCDate.getCurrentDate()));
		btnRecalcSize.addActionListener(e -> recalcFilesize());
		spnSize.addChangeListener(arg0 -> updateFilesizeDisplay());
		btnMediaInfo1.addActionListener(e -> parseCodecMetadata_Lang());
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
			List<UserDataProblem> problems = new ArrayList<>();

			for (BatchEditEpisodeData episode : data) {
				UserDataProblem.testEpisodeData(problems, target, episode.getSource(), episode.getTitle(), episode.getLength(), episode.getEpisodeNumber(), episode.getAddDate(), episode.getViewedHistory(), episode.getFilesize().getBytes(), episode.getFormat().asString(), episode.getFormat().asStringAlt(), episode.getPart(), episode.getSource().getMediaInfo()/*TODO*/, episode.getLanguage());
			}

			if (problems.size() > 0) {
				InputErrorDialog amied = new InputErrorDialog(problems, () -> onOKClicked(false), this);
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

	private void setDefaultValues() {
		cbxFormat.setModel(new DefaultComboBoxModel<>(CCFileFormat.getWrapper().getList()));
		cbxSideFormat.setModel(new DefaultComboBoxModel<>(CCFileFormat.getWrapper().getList()));
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

		try {
			episode.setTitle(edTitle.getText());
			episode.setEpisodeNumber((int) spnEpisode.getValue());
			episode.setFormat(CCFileFormat.getWrapper().findOrException(cbxFormat.getSelectedIndex()));
			episode.setLength((int) spnLength.getValue());
			episode.setFilesize(new CCFileSize((long) spnSize.getValue()));
			episode.setAddDate(spnAddDate.getValue());
			episode.setPart(edPart.getText());
			episode.setLanguage(ctrlLang.getValue());
		} catch (CCFormatException e) {
			return false;
		}

		lsEpisodes.setSelectedIndex(-1);
		updateList();

		return true;
	}

	private boolean checkUserData(List<UserDataProblem> ret) {
		try {
			BatchEditEpisodeData sel = getSelected();
			if (sel == null) return false;

			String title = edTitle.getText();
	
			int len = (int) spnLength.getValue();
			int epNum = (int) spnEpisode.getValue();
			CCDate adddate = spnAddDate.getValue();
			CCDateTimeList lvdate = CCDateTimeList.createEmpty();
			CCDBLanguageList lang = ctrlLang.getValue();
	
			long fsize = (long) spnSize.getValue();
			String csExtn = CCFileFormat.getWrapper().findOrException(cbxFormat.getSelectedIndex()).asString();
			String csExta = CCFileFormat.getWrapper().findOrException(cbxFormat.getSelectedIndex()).asStringAlt();
	
			String part = edPart.getText();
	
			UserDataProblem.testEpisodeData(ret, target, sel.getSource(), title, len, epNum, adddate, lvdate, fsize, csExtn, csExta, part, sel.getSource().getMediaInfo()/*TODO*/, lang);
	
			return ret.isEmpty();
		}
		catch (CCFormatException e) {
			return false;
		}
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
			cbxFormat.setSelectedIndex(episode.getFormat().asInt());
			spnLength.setValue(episode.getLength());
			spnSize.setValue(episode.getFilesize().getBytes());
			spnAddDate.setValue(episode.getAddDate());
			edPart.setText(episode.getPart());
			ctrlLang.setValue(episode.getLanguage());

			lblDirtyTitle.setVisible(episode.titleDirty);
			lblDirtyEpisodeNumber.setVisible(episode.episodeNumberDirty);
			lblDirtyFormat.setVisible(episode.formatDirty);
			lblDirtyLength.setVisible(episode.lengthDirty);
			lblDirtySize.setVisible(episode.filesizeDirty);
			lblDirtyAddDate.setVisible(episode.addDateDirty);
			lblDirtyPath.setVisible(episode.partDirty);
			lblDirtyLanguage.setVisible(episode.languageDirty);

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

	private void massSetViewed(boolean viewed) {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setViewed(viewed);

		updateList();
	}

	private void massSetLength() {
		lsEpisodes.setSelectedIndex(-1);

		for (BatchEditEpisodeData ep : data) ep.setLength((int) spnSideLength.getValue());

		updateList();
	}

	private void massSetFormat() {
		CCFileFormat ff = CCFileFormat.getWrapper().findOrNull(cbxSideFormat.getSelectedIndex());
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
	public void updateTitles(ArrayList<String> newTitles) {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < Math.min(data.size(), newTitles.size()); i++) data.get(i).setTitle(newTitles.get(i));
		
		updateList();
	}

	private void massMediaInfoLang() {
		lsEpisodes.setSelectedIndex(-1);

		StringBuilder err = new StringBuilder();

		for (BatchEditEpisodeData ep : data) {
			try {
				MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.getPart()));

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
		
		if (!err.toString().isEmpty()) {
			GenericTextDialog.showText(this, getTitle(), err.toString(), true);
		}
	}

	private void massMediaInfoLen() {
		lsEpisodes.setSelectedIndex(-1);

		StringBuilder err = new StringBuilder();

		for (BatchEditEpisodeData ep : data) {
			try {
				MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.getPart()));

				int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
				if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
				ep.setLength(dur);

			} catch (IOException | MediaQueryException e) {
				err.append("[").append(ep.getEpisodeNumber()).append("] ").append(ep.getTitle()).append("\n").append(ExceptionUtils.getMessage(e)).append("\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
		}

		updateList();
		
		if (!err.toString().isEmpty()) {
			GenericTextDialog.showText(this, getTitle(), err.toString(), true);
		}
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
			MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(edPart.getText()));

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
			MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(edPart.getText()));

			int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			spnLength.setValue(dur);

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
}
