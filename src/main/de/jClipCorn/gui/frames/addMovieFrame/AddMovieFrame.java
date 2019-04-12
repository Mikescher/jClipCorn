package de.jClipCorn.gui.frames.addMovieFrame;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseOnlineFrame.ParseOnlineDialog;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.parser.FilenameParser;
import de.jClipCorn.util.parser.FilenameParserResult;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;

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
	private final ButtonGroup bgrpRelPaths = new ButtonGroup();
	private JPanel pnlRelPaths;
	private JRadioButton rdbtnEnableAutomRelative;
	private JRadioButton rdbtnDisableAutomRelative;
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
	private JComboBox<String> cbxGenre0;
	private JComboBox<String> cbxGenre1;
	private JComboBox<String> cbxGenre2;
	private JComboBox<String> cbxGenre3;
	private JComboBox<String> cbxGenre7;
	private JComboBox<String> cbxGenre4;
	private JComboBox<String> cbxGenre5;
	private JComboBox<String> cbxGenre6;
	private JComboBox<String> cbxQuality;
	private LanguageChooser cbxLanguage;
	private JSpinner spnLength;
	private JCCDateSpinner spnAddDate;
	private JSpinner spnOnlineScore;
	private JComboBox<String> cbxFSK;
	private JComboBox<String> cbxFormat;
	private JSpinner spnYear;
	private JSpinner spnSize;
	private JButton btnOK;
	private JButton btnCancel;
	private JTextField edTitle;
	private JSpinner spnZyklus;
	private JButton btnParseIMDB;
	private JLabel lblFileSizeDisp;
	private JComboBox<String> cbxScore;
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
	private JLabel lblQuality;
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
	private JLabel lblNewLabel;
	private JLabel label_1;
	private JButton btnCalcQuality;
	private EditCoverControl edCvrControl;
	private JReferenceChooser edReference;
	private JLabel lblOnlineid;
	private JLabel lblGroups;
	private GroupListEditor edGroups;

	/**
	 * @wbp.parser.constructor
	 */
	public AddMovieFrame(Component owner, CCMovieList mlist) {		
		super();
		this.movieList = mlist;
		this.videoFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		
		init(owner);
	}
	
	public AddMovieFrame(Component owner, CCMovieList mlist, String firstPath) {		
		super();
		this.movieList = mlist;
		this.videoFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		
		init(owner);
		
		setFilepath(0, firstPath);
		setEnabledAll(true);
		parseFromFile(firstPath);
		firstChooseClick = false;	
		updateFilesize();
	}

	private void init(Component owner) {
		setResizable(false);

		initGUI();
		initFileChooser();
		setDefaultValues();
		setEnabledAll(false);

		setLocationRelativeTo(owner);

		btnChoose0.setEnabled(true);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("AddMovieFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 747, 785);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		ed_Part0 = new ReadableTextField();
		ed_Part0.setBounds(76, 14, 281, 22);
		contentPane.add(ed_Part0);
		ed_Part0.setColumns(10);
		
		btnChoose0 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose0.addActionListener(arg0 -> onBtnChooseClicked(0));
		btnChoose0.setBounds(369, 13, 41, 25);
		contentPane.add(btnChoose0);
		
		ed_Part1 = new ReadableTextField();
		ed_Part1.setColumns(10);
		ed_Part1.setBounds(76, 42, 191, 22);
		contentPane.add(ed_Part1);
		
		btnChoose1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose1.addActionListener(e -> onBtnChooseClicked(1));
		btnChoose1.setBounds(279, 41, 41, 25);
		contentPane.add(btnChoose1);
		
		btnClear1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear1.addActionListener(e -> onBtnClearClicked(1));
		btnClear1.setBounds(332, 41, 78, 25);
		contentPane.add(btnClear1);
		
		lblPart = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart.text")); //$NON-NLS-1$
		lblPart.setBounds(12, 17, 52, 16);
		contentPane.add(lblPart);
		
		ed_Part2 = new ReadableTextField();
		ed_Part2.setColumns(10);
		ed_Part2.setBounds(76, 70, 191, 22);
		contentPane.add(ed_Part2);
		
		btnChoose2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose2.addActionListener(e -> onBtnChooseClicked(2));
		btnChoose2.setBounds(279, 69, 41, 25);
		contentPane.add(btnChoose2);
		
		btnClear2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear2.addActionListener(e -> onBtnClearClicked(2));
		btnClear2.setBounds(332, 69, 78, 25);
		contentPane.add(btnClear2);
		
		ed_Part3 = new ReadableTextField();
		ed_Part3.setColumns(10);
		ed_Part3.setBounds(76, 98, 191, 22);
		contentPane.add(ed_Part3);
		
		btnChoose3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose3.addActionListener(e -> onBtnChooseClicked(3));
		btnChoose3.setBounds(279, 97, 41, 25);
		contentPane.add(btnChoose3);
		
		btnClear3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear3.addActionListener(e -> onBtnClearClicked(3));
		btnClear3.setBounds(332, 97, 78, 25);
		contentPane.add(btnClear3);
		
		ed_Part4 = new ReadableTextField();
		ed_Part4.setColumns(10);
		ed_Part4.setBounds(76, 126, 191, 22);
		contentPane.add(ed_Part4);
		
		btnChoose4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose4.addActionListener(e -> onBtnChooseClicked(4));
		btnChoose4.setBounds(279, 125, 41, 25);
		contentPane.add(btnChoose4);
		
		btnClear4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear4.addActionListener(e -> onBtnClearClicked(4));
		btnClear4.setBounds(332, 125, 78, 25);
		contentPane.add(btnClear4);
		
		ed_Part5 = new ReadableTextField();
		ed_Part5.setColumns(10);
		ed_Part5.setBounds(76, 154, 191, 22);
		contentPane.add(ed_Part5);
		
		btnChoose5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose5.addActionListener(e -> onBtnChooseClicked(5));
		btnChoose5.setBounds(279, 153, 41, 25);
		contentPane.add(btnChoose5);
		
		btnClear5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear5.addActionListener(e -> onBtnClearClicked(5));
		btnClear5.setBounds(332, 153, 78, 25);
		contentPane.add(btnClear5);
		
		lblPart_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_1.text")); //$NON-NLS-1$
		lblPart_1.setBounds(12, 45, 52, 16);
		contentPane.add(lblPart_1);
		
		lblPart_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_2.text")); //$NON-NLS-1$
		lblPart_2.setBounds(12, 73, 52, 16);
		contentPane.add(lblPart_2);
		
		lblPart_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_3.text")); //$NON-NLS-1$
		lblPart_3.setBounds(12, 101, 52, 16);
		contentPane.add(lblPart_3);
		
		lblPart_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_4.text")); //$NON-NLS-1$
		lblPart_4.setBounds(12, 129, 52, 16);
		contentPane.add(lblPart_4);
		
		lblPart_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_5.text")); //$NON-NLS-1$
		lblPart_5.setBounds(12, 157, 52, 16);
		contentPane.add(lblPart_5);
		
		edZyklus = new JTextField();
		edZyklus.setBounds(95, 304, 148, 20);
		contentPane.add(edZyklus);
		edZyklus.setColumns(10);
		
		lblZyklus = new JLabel(LocaleBundle.getString("AddMovieFrame.lblZyklus.text")); //$NON-NLS-1$
		lblZyklus.setBounds(12, 305, 71, 16);
		contentPane.add(lblZyklus);
		
		pnlRelPaths = new JPanel();
		pnlRelPaths.setBorder(new TitledBorder(new LineBorder(UIManager.getColor("TitledBorder.titleColor")), LocaleBundle.getString("AddMovieFrame.pnlRelPaths.borderTitle"), TitledBorder.LEFT, TitledBorder.TOP, null, UIManager.getColor("TitledBorder.titleColor"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pnlRelPaths.setBounds(420, 5, 300, 73);
		contentPane.add(pnlRelPaths);
		pnlRelPaths.setLayout(new GridLayout(0, 1, 0, 0));
		
		rdbtnEnableAutomRelative = new JRadioButton(LocaleBundle.getString("AddMovieFrame.radioButton.text")); //$NON-NLS-1$
		rdbtnEnableAutomRelative.addActionListener(arg0 -> CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.setValue(true));
		rdbtnEnableAutomRelative.setSelected(true);
		bgrpRelPaths.add(rdbtnEnableAutomRelative);
		rdbtnEnableAutomRelative.setVerticalAlignment(SwingConstants.TOP);
		rdbtnEnableAutomRelative.setHorizontalAlignment(SwingConstants.LEFT);
		pnlRelPaths.add(rdbtnEnableAutomRelative);
		
		rdbtnDisableAutomRelative = new JRadioButton(LocaleBundle.getString("AddMovieFrame.rdbtnEnableAutomRelative.text")); //$NON-NLS-1$
		rdbtnDisableAutomRelative.addActionListener(e -> CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.setValue(false));
		bgrpRelPaths.add(rdbtnDisableAutomRelative);
		rdbtnDisableAutomRelative.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnDisableAutomRelative.setVerticalAlignment(SwingConstants.TOP);
		pnlRelPaths.add(rdbtnDisableAutomRelative);
		
		cbxGenre0 = new JComboBox<>();
		cbxGenre0.setBounds(508, 86, 212, 22);
		contentPane.add(cbxGenre0);
		
		lblGenre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		lblGenre.setBounds(420, 89, 60, 16);
		contentPane.add(lblGenre);
		
		cbxGenre1 = new JComboBox<>();
		cbxGenre1.setBounds(508, 122, 212, 22);
		contentPane.add(cbxGenre1);
		
		lblGenre_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		lblGenre_1.setBounds(420, 125, 60, 16);
		contentPane.add(lblGenre_1);
		
		cbxGenre2 = new JComboBox<>();
		cbxGenre2.setBounds(508, 158, 212, 22);
		contentPane.add(cbxGenre2);
		
		lblGenre_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		lblGenre_2.setBounds(420, 161, 60, 16);
		contentPane.add(lblGenre_2);
		
		cbxGenre3 = new JComboBox<>();
		cbxGenre3.setBounds(508, 193, 212, 22);
		contentPane.add(cbxGenre3);
		
		lblGenre_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		lblGenre_3.setBounds(420, 196, 60, 16);
		contentPane.add(lblGenre_3);
		
		cbxGenre7 = new JComboBox<>();
		cbxGenre7.setBounds(508, 335, 212, 22);
		contentPane.add(cbxGenre7);
		
		lblGenre_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		lblGenre_7.setBounds(420, 338, 60, 16);
		contentPane.add(lblGenre_7);
		
		lblGenre_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		lblGenre_6.setBounds(420, 303, 60, 16);
		contentPane.add(lblGenre_6);
		
		lblGenre_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		lblGenre_5.setBounds(420, 267, 60, 16);
		contentPane.add(lblGenre_5);
		
		lblGenre_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		lblGenre_4.setBounds(420, 231, 60, 16);
		contentPane.add(lblGenre_4);
		
		cbxGenre4 = new JComboBox<>();
		cbxGenre4.setBounds(508, 228, 212, 22);
		contentPane.add(cbxGenre4);
		
		cbxGenre5 = new JComboBox<>();
		cbxGenre5.setBounds(508, 264, 212, 22);
		contentPane.add(cbxGenre5);
		
		cbxGenre6 = new JComboBox<>();
		cbxGenre6.setBounds(508, 300, 212, 22);
		contentPane.add(cbxGenre6);
		
		edCvrControl = new EditCoverControl(this, this);
		edCvrControl.setBounds(508, 421, EditCoverControl.CTRL_WIDTH, EditCoverControl.CTRL_HEIGHT);
		contentPane.add(edCvrControl);
		
		lblQuality = new JLabel(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
		lblQuality.setBounds(12, 436, 71, 16);
		contentPane.add(lblQuality);
		
		lblLanguage = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		lblLanguage.setBounds(12, 405, 71, 16);
		contentPane.add(lblLanguage);
		
		cbxQuality = new JComboBox<>();
		cbxQuality.setBounds(95, 434, 212, 22);
		contentPane.add(cbxQuality);
		
		cbxLanguage = new LanguageChooser();
		cbxLanguage.setBounds(95, 403, 212, 22);
		contentPane.add(cbxLanguage);
		
		spnLength = new JSpinner();
		spnLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
		spnLength.setBounds(95, 372, 212, 20);
		contentPane.add(spnLength);
		
		lblLength = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		lblLength.setBounds(12, 373, 71, 16);
		contentPane.add(lblLength);
		
		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), CCDate.getMinimumDate(), null);
		spnAddDate.setBounds(114, 467, 193, 20);
		contentPane.add(spnAddDate);
		
		lblMin = new JLabel("min."); //$NON-NLS-1$
		lblMin.setBounds(324, 374, 52, 16);
		contentPane.add(lblMin);
		
		lblEinfgDatum = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		lblEinfgDatum.setBounds(12, 467, 84, 16);
		contentPane.add(lblEinfgDatum);
		
		spnOnlineScore = new JSpinner();
		spnOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnOnlineScore.setBounds(114, 500, 193, 20);
		contentPane.add(spnOnlineScore);
		
		lblOnlinescore = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		lblOnlinescore.setBounds(12, 500, 84, 16);
		contentPane.add(lblOnlinescore);
		
		label = new JLabel("/ 10"); //$NON-NLS-1$
		label.setBounds(324, 505, 52, 16);
		contentPane.add(label);
		
		cbxFSK = new JComboBox<>();
		cbxFSK.setBounds(95, 535, 212, 22);
		contentPane.add(cbxFSK);
		
		lblFsk = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		lblFsk.setBounds(12, 537, 71, 16);
		contentPane.add(lblFsk);
		
		cbxFormat = new JComboBox<>();
		cbxFormat.setBounds(95, 570, 212, 22);
		contentPane.add(cbxFormat);
		
		lblFormat = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		lblFormat.setBounds(12, 572, 71, 16);
		contentPane.add(lblFormat);
		
		spnYear = new JSpinner();
		spnYear.setModel(new SpinnerNumberModel(1900, 1900, null, 1));
		spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "0")); //$NON-NLS-1$
		spnYear.setBounds(95, 605, 212, 20);
		contentPane.add(spnYear);
		
		lblYear = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		lblYear.setBounds(12, 608, 71, 16);
		contentPane.add(lblYear);
		
		spnSize = new JSpinner();
		spnSize.addChangeListener(arg0 -> updateByteDisp());
		spnSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
		spnSize.setBounds(95, 641, 212, 20);
		contentPane.add(spnSize);
		
		lblGre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		lblGre.setBounds(12, 644, 71, 16);
		contentPane.add(lblGre);
		
		lblNewLabel = new JLabel("Byte = "); //$NON-NLS-1$
		lblNewLabel.setBounds(315, 643, 50, 16);
		contentPane.add(lblNewLabel);
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> {try {
			onBtnOK(true);
		} catch (EnumFormatException e1) {
			CCLog.addError(e1);
		}});
		btnOK.setBounds(265, 724, 95, 25);
		contentPane.add(btnOK);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(arg0 -> cancel());
		btnCancel.setBounds(379, 724, 125, 25);
		contentPane.add(btnCancel);
		
		edTitle = new JTextField();
		edTitle.setColumns(10);
		edTitle.setBounds(95, 231, 212, 20);
		contentPane.add(edTitle);
		
		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label_1.setBounds(12, 231, 71, 16);
		contentPane.add(label_1);
		
		spnZyklus = new JSpinner();
		spnZyklus.setModel(new SpinnerNumberModel(-1, -1, 255, 1));
		spnZyklus.setBounds(255, 305, 52, 20);
		contentPane.add(spnZyklus);
		
		btnParseIMDB = new JButton(LocaleBundle.getString("AddMovieFrame.btnParseIMDB.text")); //$NON-NLS-1$
		btnParseIMDB.addActionListener(arg0 -> showIMDBParser());
		btnParseIMDB.setFont(new Font("Tahoma", Font.BOLD, 15)); //$NON-NLS-1$
		btnParseIMDB.setBounds(508, 368, 212, 42);
		contentPane.add(btnParseIMDB);
		
		lblFileSizeDisp = new JLabel();
		lblFileSizeDisp.setBounds(374, 643, 129, 16);
		contentPane.add(lblFileSizeDisp);
		
		cbxScore = new JComboBox<>();
		cbxScore.setBounds(95, 677, 212, 20);
		contentPane.add(cbxScore);
		
		lblScore = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
		lblScore.setBounds(12, 680, 71, 16);
		contentPane.add(lblScore);
		
		btnCalcQuality = new JButton(LocaleBundle.getString("AddMovieFrame.btnCalcQuality.text")); //$NON-NLS-1$
		btnCalcQuality.addActionListener(e -> onCalcQuality());
		btnCalcQuality.setEnabled(false);
		btnCalcQuality.setBounds(319, 434, 91, 23);
		contentPane.add(btnCalcQuality);
		
		edReference = new JReferenceChooser();
		edReference.setBounds(95, 267, 212, 20);
		contentPane.add(edReference);
		
		lblOnlineid = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
		lblOnlineid.setBounds(12, 267, 71, 16);
		contentPane.add(lblOnlineid);
		
		lblGroups = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
		lblGroups.setBounds(12, 338, 71, 16);
		contentPane.add(lblGroups);
		
		edGroups = new GroupListEditor(movieList);
		edGroups.setEnabled(false);
		edGroups.setBounds(95, 337, 212, 20);
		contentPane.add(edGroups);
	}
	
	public void parseFromTemp(CCMovie tmpMov, boolean resetAddDate, boolean resetScore) {
		if (!tmpMov.getAddDate().isMinimum() && !resetAddDate)
			spnAddDate.setValue(tmpMov.getAddDate());
		
		setFilesize(tmpMov.getFilesize().getBytes());
		setMovieFormat(tmpMov.getFormat());
		setQuality(tmpMov.getQuality());
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
		setScore(tmpMov.getOnlinescore().asInt());
		setMovieLanguage(tmpMov.getLanguage());
		setFSK(tmpMov.getFSK().asInt());

		if (! resetScore)
			cbxScore.setSelectedIndex(tmpMov.getScore().asInt());
		
		for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
			setGenre(i, tmpMov.getGenre(i).asInt());
		}
		
		setEnabledAll(true);
		firstChooseClick = false;
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
		
		CCMovie newM = movieList.createNewEmptyMovie();
		
		newM.beginUpdating();
		
		//#####################################################################################
		
		if (forceViewed != null) newM.setViewed(forceViewed);
		
		if (forceViewedHistory != null) newM.setViewedHistory(forceViewedHistory);
		
		newM.setPart(0, ed_Part0.getText());
		newM.setPart(1, ed_Part1.getText());
		newM.setPart(2, ed_Part2.getText());
		newM.setPart(3, ed_Part3.getText());
		newM.setPart(4, ed_Part4.getText());
		newM.setPart(5, ed_Part5.getText());
		
		newM.setTitle(edTitle.getText());
		newM.setZyklusTitle(edZyklus.getText());
		newM.setZyklusID((int) spnZyklus.getValue());
		
		newM.setQuality(cbxQuality.getSelectedIndex());
		newM.setLanguage(cbxLanguage.getValue());
		
		newM.setLength((int) spnLength.getValue());
		
		newM.setAddDate(spnAddDate.getValue());
		
		newM.setOnlinescore((int) spnOnlineScore.getValue());
		
		newM.setFsk(cbxFSK.getSelectedIndex());
		newM.setFormat(cbxFormat.getSelectedIndex());
		
		newM.setYear((int) spnYear.getValue());
		newM.setFilesize((long) spnSize.getValue());
		
		newM.setGenre(CCGenre.getWrapper().findOrException(cbxGenre0.getSelectedIndex()), 0);
		newM.setGenre(CCGenre.getWrapper().findOrException(cbxGenre1.getSelectedIndex()), 1);
		newM.setGenre(CCGenre.getWrapper().findOrException(cbxGenre2.getSelectedIndex()), 2);
		newM.setGenre(CCGenre.getWrapper().findOrException(cbxGenre3.getSelectedIndex()), 3);
		newM.setGenre(CCGenre.getWrapper().findOrException(cbxGenre4.getSelectedIndex()), 4);
		newM.setGenre(CCGenre.getWrapper().findOrException(cbxGenre5.getSelectedIndex()), 5);
		newM.setGenre(CCGenre.getWrapper().findOrException(cbxGenre6.getSelectedIndex()), 6);
		newM.setGenre(CCGenre.getWrapper().findOrException(cbxGenre7.getSelectedIndex()), 7);
		
		newM.setScore(cbxScore.getSelectedIndex());
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
		if (CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()) {
			rdbtnEnableAutomRelative.setSelected(true);
		} else {
			rdbtnDisableAutomRelative.setSelected(true);
		}
		
		cbxQuality.setModel(new DefaultComboBoxModel<>(CCQuality.getWrapper().getList()));
		
		cbxScore.setModel(new DefaultComboBoxModel<>(CCUserScore.getWrapper().getList()));
		cbxScore.setSelectedIndex(cbxScore.getModel().getSize() - 1);
		
		DefaultComboBoxModel<String> cbFSKdcbm;
		cbxFSK.setModel(cbFSKdcbm = new DefaultComboBoxModel<>(CCFSK.getWrapper().getList()));
		cbFSKdcbm.addElement(" "); //$NON-NLS-1$
		cbxFSK.setSelectedIndex(cbFSKdcbm.getSize() - 1);
		
		cbxFormat.setModel(new DefaultComboBoxModel<>(CCFileFormat.getWrapper().getList()));
		
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
		
		edReference.setValue(CCOnlineReferenceList.createEmpty());
		
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
		cbxQuality.setEnabled(e);
		cbxLanguage.setEnabled(e);
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
		btnCalcQuality.setEnabled(e);
		edReference.setEnabled(e);
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
		}
		
		updateFilesize();
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
	
	private void onCalcQuality() {
		setQuality(CCQuality.calculateQuality((long)spnSize.getValue(), (int) spnLength.getValue(), getPartCount()));
	}

	@Override
	public void setMovieFormat(CCFileFormat cmf) {
		cbxFormat.setSelectedIndex(cmf.asInt());
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
	public void setFilesize(long size) {
		spnSize.setValue(size);
	}

	public void setMovieLanguage(CCDBLanguageList lang) {
		cbxLanguage.setValue(lang);
	}

	@Override
	public void setQuality(CCQuality q) {
		cbxQuality.setSelectedIndex(q.asInt());
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
		onCalcQuality();
	}
	
	private void updateByteDisp() {
		lblFileSizeDisp.setText(FileSizeFormatter.format((long) spnSize.getValue()));
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
		
		setFilesize(size.getBytes());
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
			int fskidx = cbxFSK.getSelectedIndex();
			int year = (int) spnYear.getValue();
			long fsize = (long) spnSize.getValue();
			int quality = cbxQuality.getSelectedIndex();
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
			
			UserDataProblem.testMovieData(ret, null, i, movieList, p0, p1, p2, p3, p4, p5, title, zyklus, zyklusID, len, adddate, oscore, fskidx, year, fsize, csExtn, csExta, g0, g1, g2, g3, g4, g5, g6, g7, quality, lang, ref);
		
			return ret.isEmpty();
		} catch (Exception e) {
			return false;
		}
	}
	
	private int getPartCount() {
		int c = 0;
		c += ed_Part0.getText().isEmpty() ? 0 : 1;
		c += ed_Part1.getText().isEmpty() ? 0 : 1;
		c += ed_Part2.getText().isEmpty() ? 0 : 1;
		c += ed_Part3.getText().isEmpty() ? 0 : 1;
		c += ed_Part4.getText().isEmpty() ? 0 : 1;
		c += ed_Part5.getText().isEmpty() ? 0 : 1;
		return c;
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		try {
			onBtnOK(false);
		} catch (EnumFormatException e) {
			CCLog.addError(e);
		}
	}
}
