package de.jClipCorn.gui.frames.addEpisodesFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.omniParserFrame.OmniParserFrame;
import de.jClipCorn.gui.guiComponents.HFixListCellRenderer;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Validator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.OmniParserCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;

public class AddEpisodesFrame extends JFrame implements UserDataProblemHandler, OmniParserCallbackListener {
	private static final long serialVersionUID = 8825373383589912037L;

	private static CCDate MIN_DATE = CCDate.getMinimumDate();

	private final CCSeason parent;
	private final JFileChooser videoFileChooser;
	private final JFileChooser massVideoFileChooser;

	private final UpdateCallbackListener listener;

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
	private JPanel pnlInfo;
	private JLabel lblEpisode;
	private JSpinner spnEpisode;
	private JScrollPane scrollPane;
	private JPanel pnlEdit;
	private JButton btnEpCancel;
	private JButton btnEpOk;
	private JButton btnNext;
	private JButton btnOK;
	private JPanel pnlEpisodeInfo;
	private JButton btnAddEpisodes;
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
	private JComboBox<String> cbxQuality;
	private JLabel lblQuality;
	private JComboBox<String> cbxSideQuality;
	private JButton btnSide_14;
	private JButton btnOpen;
	private JButton btnSide_15;
	private JButton btnOmniparser;
	private JButton btnAutoMeta;
	private JSpinner spnSide_05;
	private JButton btnIncEpisodeNumbers;
	private JButton btnCalcQuality;

	public AddEpisodesFrame(Component owner, CCSeason ss, UpdateCallbackListener ucl) {
		super();
		setSize(new Dimension(1100, 650));
		this.parent = ss;
		this.listener = ucl;
		
		String cPathStart = ss.getSeries().getCommonPathStart(true);
		
		this.videoFileChooser = new JFileChooser(PathFormatter.fromCCPath(cPathStart));
		this.massVideoFileChooser = new JFileChooser(PathFormatter.fromCCPath(cPathStart));

		initGUI();

		setLocationRelativeTo(owner);

		updateList();
		initFileChooser();

		setDefaultValues();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getFormattedString("AddEpisodeFrame.this.title", parent.getSeries().getTitle())); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		pnlEpisodeInfo = new JPanel();
		pnlEpisodeInfo.setBounds(355, 3, 358, 581);
		getContentPane().add(pnlEpisodeInfo);
		pnlEpisodeInfo.setLayout(null);

		pnlInfo = new JPanel();
		pnlInfo.setBounds(0, 10, 357, 560);
		pnlEpisodeInfo.add(pnlInfo);
		pnlInfo.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlInfo.setLayout(null);

		label = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label.setBounds(12, 13, 52, 16);
		pnlInfo.add(label);

		edTitle = new JTextField();
		edTitle.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					onBtnNext();
				}
			}
		});
		edTitle.setBounds(74, 13, 273, 20);
		pnlInfo.add(edTitle);
		edTitle.setColumns(10);

		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		label_2.setBounds(12, 158, 52, 16);
		pnlInfo.add(label_2);

		spnLength = new JSpinner();
		spnLength.setBounds(74, 158, 193, 20);
		pnlInfo.add(spnLength);

		label_3 = new JLabel("min."); //$NON-NLS-1$
		label_3.setBounds(276, 160, 71, 16);
		pnlInfo.add(label_3);

		label_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		label_4.setBounds(12, 293, 71, 16);
		pnlInfo.add(label_4);

		label_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		label_5.setBounds(12, 88, 52, 16);
		pnlInfo.add(label_5);

		cbxFormat = new JComboBox<>();
		cbxFormat.setBounds(74, 84, 193, 22);
		pnlInfo.add(cbxFormat);

		label_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		label_6.setBounds(12, 196, 52, 16);
		pnlInfo.add(label_6);

		spnSize = new JSpinner();
		spnSize.setModel(new SpinnerNumberModel(new Long(0), null, null, new Long(1)));
		spnSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateFilesizeDisplay();
			}
		});
		spnSize.setBounds(74, 195, 193, 20);
		pnlInfo.add(spnSize);

		label_7 = new JLabel("Byte = "); //$NON-NLS-1$
		label_7.setBounds(276, 196, 71, 16);
		pnlInfo.add(label_7);

		lblFileSize = new JLabel();
		lblFileSize.setBounds(74, 267, 193, 16);
		pnlInfo.add(lblFileSize);

		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), MIN_DATE, null);
		spnAddDate.setBounds(74, 291, 193, 20);
		pnlInfo.add(spnAddDate);

		lblEpisode = new JLabel(LocaleBundle.getString("AddEpisodeFrame.lblEpisode.text")); //$NON-NLS-1$
		lblEpisode.setBounds(12, 51, 46, 14);
		pnlInfo.add(lblEpisode);

		spnEpisode = new JSpinner();
		spnEpisode.setModel(new SpinnerNumberModel(0, 0, null, 1));
		spnEpisode.setBounds(74, 49, 193, 20);
		pnlInfo.add(spnEpisode);

		btnEpCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnEpCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelInfoDisplay();
			}
		});
		btnEpCancel.setBounds(12, 526, 97, 23);
		pnlInfo.add(btnEpCancel);

		btnEpOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnEpOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				okInfoDisplay(true);
			}
		});
		btnEpOk.setBounds(121, 526, 85, 23);
		pnlInfo.add(btnEpOk);

		btnNext = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnNext.text")); //$NON-NLS-1$
		btnNext.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12)); //$NON-NLS-1$
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnNext();
			}
		});
		btnNext.setBounds(276, 526, 71, 23);
		pnlInfo.add(btnNext);

		btnRecalcSize = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
		btnRecalcSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recalcFilesize();
			}
		});
		btnRecalcSize.setBounds(74, 227, 193, 23);
		pnlInfo.add(btnRecalcSize);

		btnToday = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
		btnToday.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				spnAddDate.setValue(CCDate.getCurrentDate());
			}
		});
		btnToday.setBounds(276, 290, 71, 23);
		pnlInfo.add(btnToday);

		edPart = new ReadableTextField();
		edPart.setBounds(74, 322, 193, 20);
		pnlInfo.add(edPart);
		edPart.setColumns(10);

		lblNewLabel = new JLabel(LocaleBundle.getString("AddEpisodeFrame.lblPart.text")); //$NON-NLS-1$
		lblNewLabel.setBounds(12, 325, 46, 14);
		pnlInfo.add(lblNewLabel);

		cbxQuality = new JComboBox<>();
		cbxQuality.setBounds(74, 121, 193, 22);
		pnlInfo.add(cbxQuality);

		lblQuality = new JLabel(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
		lblQuality.setBounds(12, 118, 46, 14);
		pnlInfo.add(lblQuality);

		btnOpen = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openPart();
			}
		});
		btnOpen.setBounds(276, 321, 71, 23);
		pnlInfo.add(btnOpen);
		
		btnCalcQuality = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnCalcQuality.text")); //$NON-NLS-1$
		btnCalcQuality.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CCQuality q = CCQuality.calculateQuality((long)spnSize.getValue(), (int) spnLength.getValue(), 1);
				
				cbxQuality.setSelectedIndex(q.asInt());
			}
		});
		btnCalcQuality.setBounds(276, 121, 71, 22);
		pnlInfo.add(btnCalcQuality);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 47, 329, 503);
		getContentPane().add(scrollPane);

		lsEpisodes = new JList<>();
		lsEpisodes.setCellRenderer(new HFixListCellRenderer());
		lsEpisodes.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				updateDisplayPanel();
			}
		});
		lsEpisodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(lsEpisodes);

		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOKClicked(true);
			}
		});
		btnOK.setBounds(507, 588, 89, 23);
		getContentPane().add(btnOK);

		btnAddEpisodes = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnAddEpisodes.text")); //$NON-NLS-1$
		btnAddEpisodes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addMultipleEpisodes();
			}
		});
		btnAddEpisodes.setBounds(251, 13, 89, 23);
		getContentPane().add(btnAddEpisodes);

		lblSeason = new JLabel(parent.getTitle());
		lblSeason.setFont(new Font("Tahoma", Font.PLAIN, 16)); //$NON-NLS-1$
		lblSeason.setHorizontalAlignment(SwingConstants.CENTER);
		lblSeason.setBounds(12, 13, 229, 23);
		getContentPane().add(lblSeason);

		pnlEdit = new JPanel();
		pnlEdit.setBounds(725, 3, 358, 569);
		getContentPane().add(pnlEdit);
		pnlEdit.setBorder(new TitledBorder(new LineBorder(UIManager.getColor("TitledBorder.titleColor")), LocaleBundle.getString("AddEpisodeFrame.pnlEdit.caption"), TitledBorder.LEFT, TitledBorder.TOP, null, UIManager.getColor("TitledBorder.titleColor"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pnlEdit.setLayout(null);

		btnSide_01 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteFirst.text")); //$NON-NLS-1$
		btnSide_01.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delFirstChars();
			}
		});
		btnSide_01.setBounds(12, 22, 247, 23);
		pnlEdit.add(btnSide_01);

		spnSide_01 = new JSpinner();
		spnSide_01.setModel(new SpinnerNumberModel(1, 0, null, 1));
		spnSide_01.setBounds(271, 24, 75, 20);
		pnlEdit.add(spnSide_01);

		btnSide_02 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDeleteLast.text")); //$NON-NLS-1$
		btnSide_02.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delLastChars();
			}
		});
		btnSide_02.setBounds(12, 58, 247, 23);
		pnlEdit.add(btnSide_02);

		spnSide_02 = new JSpinner();
		spnSide_02.setModel(new SpinnerNumberModel(1, null, null, 1));
		spnSide_02.setBounds(271, 60, 75, 20);
		pnlEdit.add(spnSide_02);

		edSide_01 = new JTextField();
		edSide_01.setBounds(12, 94, 75, 22);
		pnlEdit.add(edSide_01);
		edSide_01.setColumns(10);

		btnSide_03 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnReplace.text")); //$NON-NLS-1$
		btnSide_03.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				replaceChars();
			}
		});
		btnSide_03.setBounds(99, 94, 160, 23);
		pnlEdit.add(btnSide_03);

		edSide_02 = new JTextField();
		edSide_02.setColumns(10);
		edSide_02.setBounds(271, 94, 75, 22);
		pnlEdit.add(edSide_02);

		btnSide_04 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnTrim.text")); //$NON-NLS-1$
		btnSide_04.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				trimChars();
			}
		});
		btnSide_04.setBounds(12, 129, 334, 23);
		pnlEdit.add(btnSide_04);

		edSide_03 = new JTextField();
		edSide_03.setBounds(12, 165, 100, 22);
		pnlEdit.add(edSide_03);
		edSide_03.setColumns(10);

		btnSide_05 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatStart.text")); //$NON-NLS-1$
		btnSide_05.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				concatStartChars();
			}
		});
		btnSide_05.setBounds(124, 165, 222, 23);
		pnlEdit.add(btnSide_05);

		edSide_04 = new JTextField();
		edSide_04.setColumns(10);
		edSide_04.setBounds(12, 200, 100, 22);
		pnlEdit.add(edSide_04);

		btnSide_06 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnConcatEnd.text")); //$NON-NLS-1$
		btnSide_06.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				concatEndChars();
			}
		});
		btnSide_06.setBounds(124, 201, 222, 23);
		pnlEdit.add(btnSide_06);

		btnSide_07 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnDelete.text")); //$NON-NLS-1$
		btnSide_07.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteChars();
			}
		});
		btnSide_07.setBounds(12, 235, 210, 23);
		pnlEdit.add(btnSide_07);

		spnSide_04 = new JSpinner();
		spnSide_04.setBounds(296, 237, 50, 20);
		pnlEdit.add(spnSide_04);

		spnSide_03 = new JSpinner();
		spnSide_03.setBounds(234, 237, 50, 20);
		pnlEdit.add(spnSide_03);

		edSide_05 = new JTextField();
		edSide_05.setBounds(12, 271, 100, 22);
		pnlEdit.add(edSide_05);
		edSide_05.setColumns(10);

		btnSide_08 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSearchAndDel.text")); //$NON-NLS-1$
		btnSide_08.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAndDeleteChars();
			}
		});
		btnSide_08.setBounds(124, 271, 222, 23);
		pnlEdit.add(btnSide_08);

		btnSide_10 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetViewed.text")); //$NON-NLS-1$
		btnSide_10.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				massSetViewed(true);
			}
		});
		btnSide_10.setBounds(12, 397, 163, 23);
		pnlEdit.add(btnSide_10);

		btnSide_11 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetUnviewed.text")); //$NON-NLS-1$
		btnSide_11.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				massSetViewed(false);
			}
		});
		btnSide_11.setBounds(183, 398, 163, 23);
		pnlEdit.add(btnSide_11);

		btnSide_12 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpLength.text")); //$NON-NLS-1$
		btnSide_12.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				massSetLength();
			}
		});
		btnSide_12.setBounds(12, 433, 210, 23);
		pnlEdit.add(btnSide_12);

		spnSideLength = new JSpinner();
		spnSideLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
		spnSideLength.setBounds(234, 435, 112, 20);
		pnlEdit.add(spnSideLength);

		btnSide_13 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpFormat.text")); //$NON-NLS-1$
		btnSide_13.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				massSetFormat();
			}
		});
		btnSide_13.setBounds(12, 469, 210, 23);
		pnlEdit.add(btnSide_13);

		cbxSideFormat = new JComboBox<>();
		cbxSideFormat.setBounds(234, 468, 112, 22);
		pnlEdit.add(cbxSideFormat);

		btnSide_09 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpSize.text")); //$NON-NLS-1$
		btnSide_09.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				massRecalcSizes();
			}
		});
		btnSide_09.setBounds(12, 337, 334, 23);
		pnlEdit.add(btnSide_09);

		cbxSideQuality = new JComboBox<>();
		cbxSideQuality.setBounds(234, 503, 112, 20);
		pnlEdit.add(cbxSideQuality);

		btnSide_14 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnSetEpQuality.text")); //$NON-NLS-1$
		btnSide_14.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				massSetQuality();
			}
		});
		btnSide_14.setBounds(12, 502, 210, 23);
		pnlEdit.add(btnSide_14);
		
		btnSide_15 = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnCalcEpQuality.text")); //$NON-NLS-1$
		btnSide_15.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				massCalcQuality();
			}
		});
		btnSide_15.setBounds(12, 536, 334, 23);
		pnlEdit.add(btnSide_15);
		
		spnSide_05 = new JSpinner();
		spnSide_05.setModel(new SpinnerNumberModel(new Integer(1), null, null, new Integer(1)));
		spnSide_05.setBounds(12, 304, 75, 20);
		pnlEdit.add(spnSide_05);
		
		btnIncEpisodeNumbers = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnIncEpisodeNumbers.text")); //$NON-NLS-1$
		btnIncEpisodeNumbers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				massIncNumber();
			}
		});
		btnIncEpisodeNumbers.setBounds(99, 303, 247, 23);
		pnlEdit.add(btnIncEpisodeNumbers);
		
		btnOmniparser = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnOmniParser.text")); //$NON-NLS-1$
		btnOmniparser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				OmniParserFrame oframe = new OmniParserFrame(AddEpisodesFrame.this, AddEpisodesFrame.this, getTitleList(), getCommonFolderPathStart());
				oframe.setVisible(true);
			}
		});
		btnOmniparser.setBounds(12, 561, 110, 23);
		getContentPane().add(btnOmniparser);
		
		btnAutoMeta = new JButton(LocaleBundle.getString("AddEpisodeFrame.btnAutoMeta.text")); //$NON-NLS-1$
		btnAutoMeta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				autoMetaDataCalc();
			}
		});
		btnAutoMeta.setBounds(134, 561, 207, 23);
		getContentPane().add(btnAutoMeta);
	}

	private void initFileChooser() {
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", new Validator<String>() { //$NON-NLS-1$
			@Override
			public boolean validate(String val) {
				return CCFileFormat.isValidMovieFormat(val);
			}
		}));

		videoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$

		// ######################################################################################################################

		massVideoFileChooser.setMultiSelectionEnabled(true);
		
		massVideoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", new Validator<String>() { //$NON-NLS-1$
			@Override
			public boolean validate(String val) {
				return CCFileFormat.isValidMovieFormat(val);
			}
		}));

		massVideoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$
	}

	private void onOKClicked(boolean check) {
		if (check) {
			List<UserDataProblem> problems = new ArrayList<>();

			for (int i = 0; i < parent.getEpisodeCount(); i++) {
				CCEpisode episode = parent.getEpisodeByArrayIndex(i);

				UserDataProblem.testEpisodeData(problems, parent, episode, episode.getTitle(), episode.getLength(), episode.getEpisodeNumber(), episode.getAddDate(), episode.getViewedHistory(), episode.getFilesize().getBytes(), episode.getFormat().asString(), episode.getFormat().asStringAlt(), episode.getPart(), episode.getQuality().asInt());
			}

			if (problems.size() > 0) {
				InputErrorDialog amied = new InputErrorDialog(problems, new UserDataProblemHandler() {
					@Override
					public void onAMIEDIgnoreClicked() {
						onOKClicked(false);
					}
				}, this);
				amied.setVisible(true);
				return;
			}
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
		cbxQuality.setModel(new DefaultComboBoxModel<>(CCQuality.getWrapper().getList()));
		cbxSideQuality.setModel(new DefaultComboBoxModel<>(CCQuality.getWrapper().getList()));

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

	private CCEpisode getSelectedEpisode() {
		int index = lsEpisodes.getSelectedIndex();

		if (index < 0) {
			return null;
		} else {
			return parent.getEpisodeByArrayIndex(index);
		}
	}

	private boolean okInfoDisplay(boolean check) {
		CCEpisode episode = getSelectedEpisode();

		if (episode == null) {
			return false;
		}

		List<UserDataProblem> problems = new ArrayList<>();
		boolean probvalue = (!check) || checkUserData(problems);

		if (!probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, this, this);
			amied.setVisible(true);
			return false;
		}

		episode.beginUpdating();

		episode.setTitle(edTitle.getText());
		episode.setEpisodeNumber((int) spnEpisode.getValue());
		episode.setFormat(cbxFormat.getSelectedIndex());
		episode.setLength((int) spnLength.getValue());
		episode.setFilesize((long) spnSize.getValue());
		episode.setAddDate(spnAddDate.getValue());
		episode.setPart(edPart.getText());
		episode.setQuality(cbxQuality.getSelectedIndex());

		episode.endUpdating();

		lsEpisodes.setSelectedIndex(-1);
		updateList();

		return true;
	}

	public boolean checkUserData(List<UserDataProblem> ret) {
		CCEpisode sel = getSelectedEpisode();

		String title = edTitle.getText();

		int len = (int) spnLength.getValue();
		int epNum = (int) spnEpisode.getValue();
		CCDate adddate = spnAddDate.getValue();
		CCDateTimeList lvdate = CCDateTimeList.createEmpty();

		long fsize = (long) spnSize.getValue();
		int quality = cbxQuality.getSelectedIndex();
		String csExtn = CCFileFormat.getWrapper().find(cbxFormat.getSelectedIndex()).asString();
		String csExta = CCFileFormat.getWrapper().find(cbxFormat.getSelectedIndex()).asStringAlt();

		String part = edPart.getText();

		UserDataProblem.testEpisodeData(ret, parent, sel, title, len, epNum, adddate, lvdate, fsize, csExtn, csExta, part, quality);

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

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			model.add(i, parent.getEpisodeByArrayIndex(i).getTitle());
		}

		lsEpisodes.setSelectedIndex(-1);

		updateDisplayPanel();
	}
	
	private List<String> getTitleList() {
		List<String> result = new ArrayList<>();
		
		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			result.add(i, parent.getEpisodeByArrayIndex(i).getTitle());
		}
		
		return result;
	}
	
	private String getCommonFolderPathStart() {
		List<String> paths = new ArrayList<>();

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			paths.add(i, parent.getEpisodeByArrayIndex(i).getPart());
		}
		
		return PathFormatter.fromCCPath(PathFormatter.getCommonFolderPath(paths));
	}

	private void updateDisplayPanel() {
		CCEpisode episode = getSelectedEpisode();

		if (episode == null) {
			pnlEpisodeInfo.setVisible(false);
		} else {
			pnlEpisodeInfo.setVisible(true);

			edTitle.setText(episode.getTitle());
			spnEpisode.setValue(episode.getEpisodeNumber());
			cbxFormat.setSelectedIndex(episode.getFormat().asInt());
			spnLength.setValue(episode.getLength());
			spnSize.setValue(episode.getFilesize().getBytes());
			spnAddDate.setValue(episode.getAddDate());
			edPart.setText(episode.getPart());
			cbxQuality.setSelectedIndex(episode.getQuality().asInt());

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

	public void setFilepath(String t) {
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

	private void addMultipleEpisodes() {
		int returnval = massVideoFileChooser.showOpenDialog(this);

		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File[] ff = massVideoFileChooser.getSelectedFiles();

		for (int i = 0; i < ff.length; i++) {
			String abspath = ff[i].getAbsolutePath();

			int epid = parent.getNewUnusedEpisodeNumber();

			CCEpisode ep = parent.createNewEmptyEpisode();

			ep.beginUpdating();

			ep.setTitle(PathFormatter.getFilename(abspath));
			ep.setEpisodeNumber(epid);
			ep.setViewed(false);
			ep.setLength(0);
			ep.setFormat(CCFileFormat.getMovieFormatOrDefault(PathFormatter.getExtension(abspath)));
			ep.setFilesize(ff[i].length());

			ep.setPart(PathFormatter.getCCPath(abspath));

			ep.setQuality(CCQuality.STREAM);
			ep.setAddDate(CCDate.getCurrentDate());

			ep.endUpdating();
		}

		updateList();
	}

	private void delFirstChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);
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

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);
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

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setTitle(ep.getTitle().replace(edSide_01.getText(), edSide_02.getText()));
		}

		updateList();
	}

	private void trimChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setTitle(ep.getTitle().trim());
		}

		updateList();
	}

	private void concatStartChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setTitle(edSide_03.getText().concat(ep.getTitle()));
		}

		updateList();
	}

	private void concatEndChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setTitle(ep.getTitle().concat(edSide_04.getText()));
		}

		updateList();
	}

	private void deleteChars() {
		int start = (int) spnSide_03.getValue();
		int end = (int) spnSide_04.getValue();

		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

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

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setEpisodeNumber(ep.getEpisodeNumber() + delta);
		}

		updateList();
	}

	private void searchAndDeleteChars() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setTitle(ep.getTitle().replace(edSide_05.getText(), "")); //$NON-NLS-1$
		}

		updateList();
	}

	private void massRecalcSizes() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			File f = new File(ep.getAbsolutePart());
			if (f.exists()) {
				ep.setFilesize(f.length());
			}
		}

		updateList();
	}

	private void massSetViewed(boolean viewed) {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setViewed(viewed);
		}

		updateList();
	}

	private void massSetLength() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setLength((int) spnSideLength.getValue());
		}

		updateList();
	}

	private void massSetFormat() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setFormat(cbxSideFormat.getSelectedIndex());
		}

		updateList();
	}

	private void massSetQuality() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			ep.setQuality(cbxSideQuality.getSelectedIndex());
		}

		updateList();
	}
	
	private void massCalcQuality() {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);
			ep.setQuality(CCQuality.calculateQuality(ep.getFilesize(), ep.getLength(), 1));
		}

		updateList();
	}

	@Override
	public void updateTitles(ArrayList<String> newTitles) {
		for (int i = 0; i < Math.min(parent.getEpisodeCount(), newTitles.size()); i++) {
			parent.getEpisodeByArrayIndex(i).setTitle(newTitles.get(i));
		}
		
		updateList();
	}

	public void autoMetaDataCalc() {
		lsEpisodes.setSelectedIndex(-1);

		Integer len = parent.getCommonEpisodeLength();
		if (len == 0) len = parent.getConsensEpisodeLength();

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
		
		for (int i = 0; i < parent.getEpisodeCount(); i++) {
			CCEpisode ep = parent.getEpisodeByArrayIndex(i);

			File f = new File(ep.getAbsolutePart());
			if (f.exists()) {
				ep.setFilesize(f.length());
			}

			ep.setLength(len);
			
			ep.setFormat(CCFileFormat.getMovieFormatOrDefault(PathFormatter.getExtension(ep.getAbsolutePart())));
			
			ep.setQuality(CCQuality.calculateQuality(ep.getFilesize(), ep.getLength(), 1));
		}
		
		//####################################

		updateList();
	}
}
