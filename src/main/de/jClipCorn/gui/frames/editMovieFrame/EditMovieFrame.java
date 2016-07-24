package de.jClipCorn.gui.frames.editMovieFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.TagPanel;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Validator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.ImageCropperResultListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.onlineparser.ParseResultHandler;
import de.jClipCorn.util.userdataProblem.UserDataProblem;
import de.jClipCorn.util.userdataProblem.UserDataProblemHandler;

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
	private JLabel label_17;
	private JLabel label_18;
	private JCheckBox cbViewed;
	private JComboBox<String> cbxQuality;
	private JComboBox<String> cbxLanguage;
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
	private JLabel lblMID;
	private JButton btnRecalculateSize;
	private JComboBox<String> cbxScore;
	private JLabel label_32;
	private TagPanel tagPnl;
	private JLabel label_33;
	private JButton btnToday;
	private JButton btnTestParts;
	private JButton btnCalcQuality;
	private EditCoverControl edCvrControl;
	private JLabel label_15;
	private JReferenceChooser edReference;
	private DateTimeListEditor edViewedHistory;
	private JLabel label_30;
	private JLabel lblGruppen;
	private GroupListEditor edGroups;

	public EditMovieFrame(Component owner, CCMovie movie, UpdateCallbackListener ucl) {
		super();
		this.movie = movie;
		this.videoFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		this.listener = ucl;
		
		setSize(new Dimension(740, 780));
		
		initGUI();
		setDefaultValues();
		initFileChooser();
		initFields();
		
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getFormattedString("EditMovieFrame.this.title", movie.getCompleteTitle())); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		getContentPane().setLayout(null);
		
		edPart0 = new ReadableTextField();
		edPart0.setColumns(10);
		edPart0.setBounds(74, 12, 281, 22);
		getContentPane().add(edPart0);
		
		btnChoose0 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(0);
			}
		});
		btnChoose0.setBounds(367, 11, 41, 25);
		getContentPane().add(btnChoose0);
		
		edPart1 = new ReadableTextField();
		edPart1.setColumns(10);
		edPart1.setBounds(74, 42, 191, 22);
		getContentPane().add(edPart1);
		
		btnChoose1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(1);
			}
		});
		btnChoose1.setBounds(277, 41, 41, 25);
		getContentPane().add(btnChoose1);
		
		btnClear1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(1);
			}
		});
		btnClear1.setBounds(330, 41, 78, 25);
		getContentPane().add(btnClear1);
		
		label = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart.text")); //$NON-NLS-1$
		label.setBounds(10, 15, 52, 16);
		getContentPane().add(label);
		
		edPart2 = new ReadableTextField();
		edPart2.setColumns(10);
		edPart2.setBounds(74, 72, 191, 22);
		getContentPane().add(edPart2);
		
		btnChoose2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(2);
			}
		});
		btnChoose2.setBounds(277, 71, 41, 25);
		getContentPane().add(btnChoose2);
		
		btnClear2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(2);
			}
		});
		btnClear2.setBounds(330, 71, 78, 25);
		getContentPane().add(btnClear2);
		
		edPart3 = new ReadableTextField();
		edPart3.setColumns(10);
		edPart3.setBounds(74, 102, 191, 22);
		getContentPane().add(edPart3);
		
		btnChoose3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(3);
			}
		});
		btnChoose3.setBounds(277, 101, 41, 25);
		getContentPane().add(btnChoose3);
		
		btnClear3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(3);
			}
		});
		btnClear3.setBounds(330, 101, 78, 25);
		getContentPane().add(btnClear3);
		
		edPart4 = new ReadableTextField();
		edPart4.setColumns(10);
		edPart4.setBounds(74, 132, 191, 22);
		getContentPane().add(edPart4);
		
		btnChoose4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(4);
			}
		});
		btnChoose4.setBounds(277, 131, 41, 25);
		getContentPane().add(btnChoose4);
		
		btnClear4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(4);
			}
		});
		btnClear4.setBounds(330, 131, 78, 25);
		getContentPane().add(btnClear4);
		
		edPart5 = new ReadableTextField();
		edPart5.setColumns(10);
		edPart5.setBounds(74, 162, 191, 22);
		getContentPane().add(edPart5);
		
		btnChoose5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(5);
			}
		});
		btnChoose5.setBounds(277, 161, 41, 25);
		getContentPane().add(btnChoose5);
		
		btnClear5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(5);
			}
		});
		btnClear5.setBounds(330, 161, 78, 25);
		getContentPane().add(btnClear5);
		
		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_1.text")); //$NON-NLS-1$
		label_1.setBounds(10, 45, 52, 16);
		getContentPane().add(label_1);
		
		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_2.text")); //$NON-NLS-1$
		label_2.setBounds(10, 75, 52, 16);
		getContentPane().add(label_2);
		
		label_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_3.text")); //$NON-NLS-1$
		label_3.setBounds(10, 105, 52, 16);
		getContentPane().add(label_3);
		
		label_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_4.text")); //$NON-NLS-1$
		label_4.setBounds(10, 135, 52, 16);
		getContentPane().add(label_4);
		
		label_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_5.text")); //$NON-NLS-1$
		label_5.setBounds(10, 165, 52, 16);
		getContentPane().add(label_5);
		
		edZyklus = new JTextField();
		edZyklus.setColumns(10);
		edZyklus.setBounds(93, 285, 148, 20);
		getContentPane().add(edZyklus);
		
		label_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblZyklus.text")); //$NON-NLS-1$
		label_6.setBounds(10, 286, 52, 16);
		getContentPane().add(label_6);
		
		cbxGenre0 = new JComboBox<>();
		cbxGenre0.setBounds(506, 11, 212, 22);
		getContentPane().add(cbxGenre0);
		
		label_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		label_7.setBounds(418, 14, 60, 16);
		getContentPane().add(label_7);
		
		cbxGenre1 = new JComboBox<>();
		cbxGenre1.setBounds(506, 38, 212, 22);
		getContentPane().add(cbxGenre1);
		
		label_8 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		label_8.setBounds(418, 41, 60, 16);
		getContentPane().add(label_8);
		
		cbxGenre2 = new JComboBox<>();
		cbxGenre2.setBounds(506, 65, 212, 22);
		getContentPane().add(cbxGenre2);
		
		label_9 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		label_9.setBounds(418, 68, 60, 16);
		getContentPane().add(label_9);
		
		cbxGenre3 = new JComboBox<>();
		cbxGenre3.setBounds(506, 92, 212, 22);
		getContentPane().add(cbxGenre3);
		
		label_10 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		label_10.setBounds(418, 95, 60, 16);
		getContentPane().add(label_10);
		
		cbxGenre7 = new JComboBox<>();
		cbxGenre7.setBounds(506, 200, 212, 22);
		getContentPane().add(cbxGenre7);
		
		label_11 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		label_11.setBounds(418, 203, 60, 16);
		getContentPane().add(label_11);
		
		label_12 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		label_12.setBounds(418, 176, 60, 16);
		getContentPane().add(label_12);
		
		label_13 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		label_13.setBounds(418, 149, 60, 16);
		getContentPane().add(label_13);
		
		label_14 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		label_14.setBounds(418, 122, 60, 16);
		getContentPane().add(label_14);
		
		cbxGenre4 = new JComboBox<>();
		cbxGenre4.setBounds(506, 119, 212, 22);
		getContentPane().add(cbxGenre4);
		
		cbxGenre5 = new JComboBox<>();
		cbxGenre5.setBounds(506, 146, 212, 22);
		getContentPane().add(cbxGenre5);
		
		cbxGenre6 = new JComboBox<>();
		cbxGenre6.setBounds(506, 173, 212, 22);
		getContentPane().add(cbxGenre6);
		
		label_16 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGesehen.text")); //$NON-NLS-1$
		label_16.setBounds(10, 316, 52, 16);
		getContentPane().add(label_16);
		
		label_17 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
		label_17.setBounds(10, 346, 52, 16);
		getContentPane().add(label_17);
		
		label_18 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		label_18.setBounds(10, 376, 65, 16);
		getContentPane().add(label_18);
		
		cbViewed = new JCheckBox();
		cbViewed.setBounds(93, 315, 212, 25);
		getContentPane().add(cbViewed);
		
		cbxQuality = new JComboBox<>();
		cbxQuality.setBounds(93, 345, 212, 22);
		getContentPane().add(cbxQuality);
		
		cbxLanguage = new JComboBox<>();
		cbxLanguage.setBounds(93, 375, 212, 22);
		getContentPane().add(cbxLanguage);
		
		spnLength = new JSpinner();
		spnLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
		spnLength.setBounds(93, 405, 212, 20);
		getContentPane().add(spnLength);
		
		label_19 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		label_19.setBounds(10, 406, 52, 16);
		getContentPane().add(label_19);
		
		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), MIN_DATE, null);
		spnAddDate.setBounds(93, 435, 212, 20);
		getContentPane().add(spnAddDate);
		
		label_20 = new JLabel("min."); //$NON-NLS-1$
		label_20.setBounds(322, 406, 52, 16);
		getContentPane().add(label_20);
		
		label_21 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		label_21.setBounds(10, 436, 71, 16);
		getContentPane().add(label_21);
		
		spnOnlineScore = new JSpinner();
		spnOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnOnlineScore.setBounds(93, 465, 212, 20);
		getContentPane().add(spnOnlineScore);
		
		label_22 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		label_22.setBounds(10, 466, 104, 16);
		getContentPane().add(label_22);
		
		label_23 = new JLabel("/ 10"); //$NON-NLS-1$
		label_23.setBounds(322, 465, 52, 16);
		getContentPane().add(label_23);
		
		cbxFSK = new JComboBox<>();
		cbxFSK.setBounds(93, 495, 212, 22);
		getContentPane().add(cbxFSK);
		
		label_24 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		label_24.setBounds(10, 496, 52, 16);
		getContentPane().add(label_24);
		
		cbxFormat = new JComboBox<>();
		cbxFormat.setBounds(93, 525, 212, 22);
		getContentPane().add(cbxFormat);
		
		label_25 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		label_25.setBounds(10, 526, 52, 16);
		getContentPane().add(label_25);
		
		spnYear = new JSpinner();
		spnYear.setModel(new SpinnerNumberModel(1900, 1900, null, 1));
		spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "0")); //$NON-NLS-1$
		spnYear.setBounds(93, 555, 212, 20);
		getContentPane().add(spnYear);
		
		label_26 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		label_26.setBounds(10, 556, 52, 16);
		getContentPane().add(label_26);
		
		spnSize = new JSpinner();
		spnSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateByteDisp();
			}
		});
		spnSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
		spnSize.setBounds(93, 585, 212, 20);
		getContentPane().add(spnSize);
		
		label_27 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		label_27.setBounds(10, 586, 52, 16);
		getContentPane().add(label_27);
		
		label_28 = new JLabel("Byte = "); //$NON-NLS-1$
		label_28.setBounds(314, 586, 60, 16);
		getContentPane().add(label_28);
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnOK(true);
			}
		});
		btnOK.setBounds(263, 722, 95, 25);
		getContentPane().add(btnOK);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setBounds(377, 722, 95, 25);
		getContentPane().add(btnCancel);
		
		edTitle = new JTextField();
		edTitle.setColumns(10);
		edTitle.setBounds(93, 225, 212, 20);
		getContentPane().add(edTitle);
		
		label_29 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label_29.setBounds(10, 226, 52, 16);
		getContentPane().add(label_29);
		
		spnZyklus = new JSpinner();
		spnZyklus.setModel(new SpinnerNumberModel(0, new Integer(-1), null, 1));
		spnZyklus.setBounds(253, 285, 52, 20);
		getContentPane().add(spnZyklus);
		
		lblFileSizeDisp = new JLabel();
		lblFileSizeDisp.setBounds(355, 586, 129, 16);
		getContentPane().add(lblFileSizeDisp);
		
		lblMID = new JLabel();
		lblMID.setBounds(294, 267, 46, 14);
		getContentPane().add(lblMID);
		
		btnRecalculateSize = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
		btnRecalculateSize.setToolTipText(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
		btnRecalculateSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFilesize();
			}
		});
		btnRecalculateSize.setBounds(93, 615, 212, 23);
		getContentPane().add(btnRecalculateSize);
		
		cbxScore = new JComboBox<>();
		cbxScore.setBounds(93, 645, 212, 22);
		getContentPane().add(cbxScore);
		
		label_32 = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
		label_32.setBounds(10, 646, 46, 18);
		getContentPane().add(label_32);
		
		tagPnl = new TagPanel();
		tagPnl.setBounds(93, 674, 212, 22);
		getContentPane().add(tagPnl);
		
		label_33 = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
		label_33.setBounds(10, 676, 52, 16);
		getContentPane().add(label_33);
		
		btnToday = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
		btnToday.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				spnAddDate.setValue(CCDate.getCurrentDate());
			}
		});
		btnToday.setBounds(313, 435, 95, 23);
		getContentPane().add(btnToday);
		
		btnTestParts = new JButton(LocaleBundle.getString("EditMovieFrame.btnTestParts.text")); //$NON-NLS-1$
		btnTestParts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				testPaths();
			}
		});
		btnTestParts.setBounds(277, 191, 131, 23);
		getContentPane().add(btnTestParts);
		
		btnCalcQuality = new JButton(LocaleBundle.getString("AddMovieFrame.btnCalcQuality.text")); //$NON-NLS-1$
		btnCalcQuality.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recalcQuality();
			}
		});
		btnCalcQuality.setBounds(313, 345, 95, 23);
		getContentPane().add(btnCalcQuality);
		
		edCvrControl = new EditCoverControl(this, this);
		edCvrControl.setBounds(536, 422, EditCoverControl.CTRL_WIDTH, EditCoverControl.CTRL_HEIGHT);
		getContentPane().add(edCvrControl);
		
		label_15 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
		label_15.setBounds(10, 256, 52, 16);
		getContentPane().add(label_15);
		
		edReference = new JReferenceChooser();
		edReference.setBounds(93, 255, 212, 20);
		getContentPane().add(edReference);
		
		edViewedHistory = new DateTimeListEditor();
		edViewedHistory.setBounds(506, 264, 212, 133);
		getContentPane().add(edViewedHistory);
		
		label_30 = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
		label_30.setBounds(418, 261, 52, 16);
		getContentPane().add(label_30);
		
		lblGruppen = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
		lblGruppen.setBounds(418, 231, 52, 16);
		getContentPane().add(lblGruppen);
		
		edGroups = new GroupListEditor(movie.getMovieList());
		edGroups.setBounds(506, 230, 212, 22);
		getContentPane().add(edGroups);
	}

	private void setDefaultValues() {
		cbxQuality.setModel(new DefaultComboBoxModel<>(CCMovieQuality.getList()));
		
		cbxLanguage.setModel(new DefaultComboBoxModel<>(CCMovieLanguage.getList()));
		
		cbxFSK.setModel(new DefaultComboBoxModel<>(CCMovieFSK.getList()));
		
		cbxFormat.setModel(new DefaultComboBoxModel<>(CCMovieFormat.getList()));
		cbxScore.setModel(new DefaultComboBoxModel<>(CCMovieScore.getList()));
		
		cbxGenre0.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre1.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre2.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre3.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre4.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre5.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre6.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre7.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		
		spnAddDate.setValue(CCDate.getCurrentDate());
		spnZyklus.setValue(-1);
		
		edViewedHistory.setValue(CCDateTimeList.createEmpty());
		edReference.setValue(CCOnlineReference.createNone());
		edGroups.setValue(CCGroupList.createEmpty());
		
		updateByteDisp();
	}
	
	private void updateByteDisp() {
		lblFileSizeDisp.setText(FileSizeFormatter.format((long) spnSize.getValue()));
	}
	
	private void updateFilesize() {
		CCMovieSize size = new CCMovieSize();
		
		if (! edPart0.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart0.getText())));
		}
		
		if (! edPart1.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart1.getText())));
		}
		
		if (! edPart2.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart2.getText())));
		}
		
		if (! edPart3.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart3.getText())));
		}
		
		if (! edPart4.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart4.getText())));
		}
		
		if (! edPart5.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.fromCCPath(edPart5.getText())));
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
				return CCMovieFormat.isValidMovieFormat(val);
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
		cbxQuality.setSelectedIndex(movie.getQuality().asInt());
		cbxLanguage.setSelectedIndex(movie.getLanguage().asInt());
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
		
		updateByteDisp();
		testPaths();
	}
	
	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
	}
	
	@Override
	public void setOnlineReference(CCOnlineReference ref) {
		edReference.setValue(ref);
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		onBtnOK(false);
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
	public CCOnlineReference getSearchReference() {
		return edReference.getValue();
	}

	@Override
	public void setMovieFormat(CCMovieFormat cmf) {
		cbxFormat.setSelectedIndex(cmf.asInt());
	}

	@Override
	public void setFilepath(int p, String t) {
		String pt = t;
		if (!t.isEmpty() && CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()) {
			pt = PathFormatter.getCCPath(t, CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue());
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
		
		CCMovieFormat fmt = CCMovieFormat.getMovieFormatFromPaths(edPart0.getText(), edPart1.getText(), edPart2.getText(), edPart3.getText(), edPart4.getText(), edPart5.getText());
		if (fmt != null) cbxFormat.setSelectedIndex(fmt.asInt());
		
		recalcQuality();
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
	public void setMovieLanguage(CCMovieLanguage lang) {
		cbxLanguage.setSelectedIndex(lang.asInt());
		
		testPaths();
	}

	@Override
	public void setQuality(CCMovieQuality q) {
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
		// nothing
	}
	
	private void recalcQuality() {
		setQuality(CCMovieQuality.calculateQuality((long)spnSize.getValue(), (int) spnLength.getValue(), getPartCount()));
	}
	
	private int getPartCount() {
		int c = 0;
		c += edPart0.getText().isEmpty() ? 0 : 1;
		c += edPart1.getText().isEmpty() ? 0 : 1;
		c += edPart2.getText().isEmpty() ? 0 : 1;
		c += edPart3.getText().isEmpty() ? 0 : 1;
		c += edPart4.getText().isEmpty() ? 0 : 1;
		c += edPart5.getText().isEmpty() ? 0 : 1;
		return c;
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
	
	private void onBtnOK(boolean check) {
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
		
		movie.setQuality(cbxQuality.getSelectedIndex());
		movie.setLanguage(cbxLanguage.getSelectedIndex());
		
		movie.setLength((int) spnLength.getValue());
		
		movie.setAddDate(spnAddDate.getValue());
		
		movie.setOnlinescore((int) spnOnlineScore.getValue());
		
		movie.setFsk(cbxFSK.getSelectedIndex());
		movie.setFormat(cbxFormat.getSelectedIndex());
		
		movie.setYear((int) spnYear.getValue());
		movie.setFilesize((long) spnSize.getValue());
		
		movie.setGenre(CCMovieGenre.find(cbxGenre0.getSelectedIndex()), 0);
		movie.setGenre(CCMovieGenre.find(cbxGenre1.getSelectedIndex()), 1);
		movie.setGenre(CCMovieGenre.find(cbxGenre2.getSelectedIndex()), 2);
		movie.setGenre(CCMovieGenre.find(cbxGenre3.getSelectedIndex()), 3);
		movie.setGenre(CCMovieGenre.find(cbxGenre4.getSelectedIndex()), 4);
		movie.setGenre(CCMovieGenre.find(cbxGenre5.getSelectedIndex()), 5);
		movie.setGenre(CCMovieGenre.find(cbxGenre6.getSelectedIndex()), 6);
		movie.setGenre(CCMovieGenre.find(cbxGenre7.getSelectedIndex()), 7);
		
		movie.setTags(tagPnl.getValue());
		movie.setScore(cbxScore.getSelectedIndex());
		
		movie.setCover(edCvrControl.getResizedImage());

		movie.setViewedHistory(edViewedHistory.getValue());
		movie.setOnlineReference(edReference.getValue());
		movie.setGroups(edGroups.getValue());
		
		//#####################################################################################
		
		movie.endUpdating();
		
		if (listener != null) {
			listener.onUpdate(movie);
		}
		
		dispose();
	}
	
	public boolean checkUserData(List<UserDataProblem> ret) {
		BufferedImage i = edCvrControl.getResizedImage();
		
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
		
		int fskidx = cbxFSK.getSelectedIndex();
		int year = (int) spnYear.getValue();
		long fsize = (long) spnSize.getValue();
		int quality = cbxQuality.getSelectedIndex();
		int lang = cbxLanguage.getSelectedIndex();
		String csExtn  = CCMovieFormat.find(cbxFormat.getSelectedIndex()).asString();
		String csExta = CCMovieFormat.find(cbxFormat.getSelectedIndex()).asStringAlt();
		
		int g0 = cbxGenre0.getSelectedIndex();
		int g1 = cbxGenre1.getSelectedIndex();
		int g2 = cbxGenre2.getSelectedIndex();
		int g3 = cbxGenre3.getSelectedIndex();
		int g4 = cbxGenre4.getSelectedIndex();
		int g5 = cbxGenre5.getSelectedIndex();
		int g6 = cbxGenre6.getSelectedIndex();
		int g7 = cbxGenre7.getSelectedIndex();
		
		CCOnlineReference ref = edReference.getValue();
		
		UserDataProblem.testMovieData(ret, movie, i, movie.getMovieList(), p0, p1, p2, p3, p4, p5, title, zyklus, zyklusID, len, adddate, oscore, fskidx, year, fsize, csExtn, csExta, g0, g1, g2, g3, g4, g5, g6, g7, quality, lang, ref);
		
		return ret.isEmpty();
	}

	@Override
	public void editingFinished(BufferedImage i) {
		setCover(i);
	}

	@Override
	public void editingCanceled() {
		// nothing
	}
}