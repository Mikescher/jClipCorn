package de.jClipCorn.gui.frames.addMovieFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.findCoverFrame.FindCoverDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseImDBFrame.ParseImDBDialog;
import de.jClipCorn.gui.guiComponents.CCDateEditor;
import de.jClipCorn.gui.guiComponents.SpinnerCCDateModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.FileSizeFormatter;
import de.jClipCorn.util.HTTPUtilities;
import de.jClipCorn.util.ImageUtilities;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.Validator;
import de.jClipCorn.util.parser.FilenameParser;
import de.jClipCorn.util.parser.ImDBParser;
import de.jClipCorn.util.parser.ParseResultHandler;
import de.jClipCorn.util.userdataProblem.UserDataProblem;
import de.jClipCorn.util.userdataProblem.UserDataProblemHandler;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class AddMovieFrame extends JFrame implements ParseResultHandler, UserDataProblemHandler {
	private static final long serialVersionUID = -5912378114066741528L;
	
	private static final CCDate MIN_DATE = CCDate.getNewMinimumDate();
	
	private boolean firstChooseClick = true;;
	
	private final JFileChooser videoFileChooser;
	private final JFileChooser coverFileChooser;
	
	private BufferedImage currentCoverImage = null;
	
	private CCMovieList movieList;
	
	private JPanel contentPane;
	private JTextField ed_Part0;
	private JTextField ed_Part1;
	private JTextField ed_Part2;
	private JTextField ed_Part3;
	private JTextField ed_Part4;
	private JTextField ed_Part5;
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
	private JComboBox cbxGenre0;
	private JComboBox cbxGenre1;
	private JComboBox cbxGenre2;
	private JComboBox cbxGenre3;
	private JComboBox cbxGenre7;
	private JComboBox cbxGenre4;
	private JComboBox cbxGenre5;
	private JComboBox cbxGenre6;
	private JLabel lblCover;
	private JButton btnOpenCover;
	private JButton btnFindCover;
	private JCheckBox cbxViewed;
	private JComboBox cbxQuality;
	private JComboBox cbxLanguage;
	private JSpinner spnLength;
	private JSpinner spnAddDate;
	private JSpinner spnOnlineScore;
	private JComboBox cbxFSK;
	private JComboBox cbxFormat;
	private JSpinner spnYear;
	private JSpinner spnSize;
	private JButton btnOK;
	private JButton btnCancel;
	private JTextField edTitle;
	private JSpinner spnZyklus;
	private JButton btnParseIMDB;
	private JButton btnOpenIMDb;
	private JLabel lblFileSizeDisp;

	/**
	 * @wbp.parser.constructor
	 */
	public AddMovieFrame(Component owner, CCMovieList mlist) {		
		super();
		this.movieList = mlist;
		this.videoFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		this.coverFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		
		init(owner);
	}
	
	public AddMovieFrame(Component owner, CCMovieList mlist, String firstPath) {		
		super();
		this.movieList = mlist;
		this.videoFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		this.coverFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		
		init(owner);
		
		setFilepath(0, firstPath);
		setEnabledAll(true);
		new FilenameParser(this).parse(firstPath);	
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
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 747, 785);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		ed_Part0 = new JTextField();
		ed_Part0.setEditable(false);
		ed_Part0.setBounds(76, 14, 281, 22);
		contentPane.add(ed_Part0);
		ed_Part0.setColumns(10);
		
		btnChoose0 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onBtnChooseClicked(0);
			}
		});
		btnChoose0.setBounds(369, 13, 41, 25);
		contentPane.add(btnChoose0);
		
		ed_Part1 = new JTextField();
		ed_Part1.setEditable(false);
		ed_Part1.setColumns(10);
		ed_Part1.setBounds(76, 50, 191, 22);
		contentPane.add(ed_Part1);
		
		btnChoose1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(1);
			}
		});
		btnChoose1.setBounds(279, 49, 41, 25);
		contentPane.add(btnChoose1);
		
		btnClear1 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(1);
			}
		});
		btnClear1.setBounds(332, 49, 78, 25);
		contentPane.add(btnClear1);
		
		JLabel lblPart = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart.text")); //$NON-NLS-1$
		lblPart.setBounds(12, 17, 52, 16);
		contentPane.add(lblPart);
		
		ed_Part2 = new JTextField();
		ed_Part2.setEditable(false);
		ed_Part2.setColumns(10);
		ed_Part2.setBounds(76, 86, 191, 22);
		contentPane.add(ed_Part2);
		
		btnChoose2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(2);
			}
		});
		btnChoose2.setBounds(279, 87, 41, 25);
		contentPane.add(btnChoose2);
		
		btnClear2 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(2);
			}
		});
		btnClear2.setBounds(332, 85, 78, 25);
		contentPane.add(btnClear2);
		
		ed_Part3 = new JTextField();
		ed_Part3.setEditable(false);
		ed_Part3.setColumns(10);
		ed_Part3.setBounds(76, 122, 191, 22);
		contentPane.add(ed_Part3);
		
		btnChoose3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(3);
			}
		});
		btnChoose3.setBounds(279, 121, 41, 25);
		contentPane.add(btnChoose3);
		
		btnClear3 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(3);
			}
		});
		btnClear3.setBounds(332, 121, 78, 25);
		contentPane.add(btnClear3);
		
		ed_Part4 = new JTextField();
		ed_Part4.setEditable(false);
		ed_Part4.setColumns(10);
		ed_Part4.setBounds(76, 158, 191, 22);
		contentPane.add(ed_Part4);
		
		btnChoose4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(4);
			}
		});
		btnChoose4.setBounds(279, 157, 41, 25);
		contentPane.add(btnChoose4);
		
		btnClear4 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(4);
			}
		});
		btnClear4.setBounds(332, 157, 78, 25);
		contentPane.add(btnClear4);
		
		ed_Part5 = new JTextField();
		ed_Part5.setEditable(false);
		ed_Part5.setColumns(10);
		ed_Part5.setBounds(76, 194, 191, 22);
		contentPane.add(ed_Part5);
		
		btnChoose5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnChooseClicked(5);
			}
		});
		btnChoose5.setBounds(279, 193, 41, 25);
		contentPane.add(btnChoose5);
		
		btnClear5 = new JButton(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
		btnClear5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnClearClicked(5);
			}
		});
		btnClear5.setBounds(332, 193, 78, 25);
		contentPane.add(btnClear5);
		
		JLabel lblPart_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_1.text")); //$NON-NLS-1$
		lblPart_1.setBounds(12, 53, 52, 16);
		contentPane.add(lblPart_1);
		
		JLabel lblPart_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_2.text")); //$NON-NLS-1$
		lblPart_2.setBounds(12, 89, 52, 16);
		contentPane.add(lblPart_2);
		
		JLabel lblPart_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_3.text")); //$NON-NLS-1$
		lblPart_3.setBounds(12, 125, 52, 16);
		contentPane.add(lblPart_3);
		
		JLabel lblPart_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_4.text")); //$NON-NLS-1$
		lblPart_4.setBounds(12, 161, 52, 16);
		contentPane.add(lblPart_4);
		
		JLabel lblPart_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblPart_5.text")); //$NON-NLS-1$
		lblPart_5.setBounds(12, 197, 52, 16);
		contentPane.add(lblPart_5);
		
		edZyklus = new JTextField();
		edZyklus.setBounds(76, 303, 148, 20);
		contentPane.add(edZyklus);
		edZyklus.setColumns(10);
		
		JLabel lblZyklus = new JLabel(LocaleBundle.getString("AddMovieFrame.lblZyklus.text")); //$NON-NLS-1$
		lblZyklus.setBounds(12, 305, 52, 16);
		contentPane.add(lblZyklus);
		
		pnlRelPaths = new JPanel();
		pnlRelPaths.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), LocaleBundle.getString("AddMovieFrame.pnlRelPaths.borderTitle"), TitledBorder.LEFT, TitledBorder.TOP, null, Color.BLACK)); //$NON-NLS-1$
		pnlRelPaths.setBounds(420, 5, 300, 73);
		contentPane.add(pnlRelPaths);
		pnlRelPaths.setLayout(new GridLayout(0, 1, 0, 0));
		
		rdbtnEnableAutomRelative = new JRadioButton(LocaleBundle.getString("AddMovieFrame.radioButton.text")); //$NON-NLS-1$
		rdbtnEnableAutomRelative.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.setValue(true);
			}
		});
		rdbtnEnableAutomRelative.setSelected(true);
		bgrpRelPaths.add(rdbtnEnableAutomRelative);
		rdbtnEnableAutomRelative.setVerticalAlignment(SwingConstants.TOP);
		rdbtnEnableAutomRelative.setHorizontalAlignment(SwingConstants.LEFT);
		pnlRelPaths.add(rdbtnEnableAutomRelative);
		
		rdbtnDisableAutomRelative = new JRadioButton(LocaleBundle.getString("AddMovieFrame.rdbtnEnableAutomRelative.text")); //$NON-NLS-1$
		rdbtnDisableAutomRelative.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.setValue(false);
			}
		});
		bgrpRelPaths.add(rdbtnDisableAutomRelative);
		rdbtnDisableAutomRelative.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnDisableAutomRelative.setVerticalAlignment(SwingConstants.TOP);
		pnlRelPaths.add(rdbtnDisableAutomRelative);
		
		cbxGenre0 = new JComboBox<Object>();
		cbxGenre0.setBounds(508, 86, 212, 22);
		contentPane.add(cbxGenre0);
		
		JLabel lblGenre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		lblGenre.setBounds(420, 89, 52, 16);
		contentPane.add(lblGenre);
		
		cbxGenre1 = new JComboBox<Object>();
		cbxGenre1.setBounds(508, 122, 212, 22);
		contentPane.add(cbxGenre1);
		
		JLabel lblGenre_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		lblGenre_1.setBounds(420, 125, 52, 16);
		contentPane.add(lblGenre_1);
		
		cbxGenre2 = new JComboBox<Object>();
		cbxGenre2.setBounds(508, 158, 212, 22);
		contentPane.add(cbxGenre2);
		
		JLabel lblGenre_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		lblGenre_2.setBounds(420, 161, 52, 16);
		contentPane.add(lblGenre_2);
		
		cbxGenre3 = new JComboBox<Object>();
		cbxGenre3.setBounds(508, 193, 212, 22);
		contentPane.add(cbxGenre3);
		
		JLabel lblGenre_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		lblGenre_3.setBounds(420, 196, 52, 16);
		contentPane.add(lblGenre_3);
		
		cbxGenre7 = new JComboBox<Object>();
		cbxGenre7.setBounds(508, 335, 212, 22);
		contentPane.add(cbxGenre7);
		
		JLabel lblGenre_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		lblGenre_7.setBounds(420, 338, 52, 16);
		contentPane.add(lblGenre_7);
		
		JLabel lblGenre_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		lblGenre_6.setBounds(420, 303, 52, 16);
		contentPane.add(lblGenre_6);
		
		JLabel lblGenre_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		lblGenre_5.setBounds(420, 267, 52, 16);
		contentPane.add(lblGenre_5);
		
		JLabel lblGenre_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		lblGenre_4.setBounds(420, 231, 52, 16);
		contentPane.add(lblGenre_4);
		
		cbxGenre4 = new JComboBox<Object>();
		cbxGenre4.setBounds(508, 228, 212, 22);
		contentPane.add(cbxGenre4);
		
		cbxGenre5 = new JComboBox<Object>();
		cbxGenre5.setBounds(508, 264, 212, 22);
		contentPane.add(cbxGenre5);
		
		cbxGenre6 = new JComboBox<Object>();
		cbxGenre6.setBounds(508, 300, 212, 22);
		contentPane.add(cbxGenre6);
		
		lblCover = new JLabel(""); //$NON-NLS-1$
		lblCover.setHorizontalAlignment(SwingConstants.CENTER);
		lblCover.setBounds(508, 459, ImageUtilities.COVER_WIDTH, ImageUtilities.COVER_HEIGHT);
		contentPane.add(lblCover);
		
		btnOpenCover = new JButton(LocaleBundle.getString("AddMovieFrame.btnOpenCover.text")); //$NON-NLS-1$
		btnOpenCover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openChooseCoverDialog();
			}
		});
		btnOpenCover.setBounds(643, 421, 47, 25);
		contentPane.add(btnOpenCover);
		
		btnFindCover = new JButton(LocaleBundle.getString("AddMovieFrame.btnFindCover.text")); //$NON-NLS-1$
		btnFindCover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showFindCoverDialog();
			}
		});
		btnFindCover.setBounds(560, 421, 71, 25);
		contentPane.add(btnFindCover);
		
		JLabel lblGesehen = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGesehen.text")); //$NON-NLS-1$
		lblGesehen.setBounds(12, 341, 52, 16);
		contentPane.add(lblGesehen);
		
		JLabel lblQuality = new JLabel(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
		lblQuality.setBounds(12, 377, 52, 16);
		contentPane.add(lblQuality);
		
		JLabel lblLanguage = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		lblLanguage.setBounds(12, 412, 52, 16);
		contentPane.add(lblLanguage);
		
		cbxViewed = new JCheckBox(""); //$NON-NLS-1$
		cbxViewed.setBounds(76, 337, 212, 25);
		contentPane.add(cbxViewed);
		
		cbxQuality = new JComboBox<Object>();
		cbxQuality.setBounds(76, 374, 212, 22);
		contentPane.add(cbxQuality);
		
		cbxLanguage = new JComboBox<Object>();
		cbxLanguage.setBounds(76, 409, 212, 22);
		contentPane.add(cbxLanguage);
		
		spnLength = new JSpinner();
		spnLength.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spnLength.setBounds(76, 442, 212, 20);
		contentPane.add(spnLength);
		
		JLabel lblLength = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		lblLength.setBounds(12, 442, 52, 16);
		contentPane.add(lblLength);
		
		spnAddDate = new JSpinner();
		spnAddDate.setModel(new SpinnerCCDateModel(CCDate.getNewMinimumDate(), MIN_DATE, null));
		spnAddDate.setEditor(new CCDateEditor(spnAddDate));
		spnAddDate.setBounds(95, 473, 193, 20);
		contentPane.add(spnAddDate);
		
		JLabel lblMin = new JLabel("min."); //$NON-NLS-1$
		lblMin.setBounds(305, 444, 52, 16);
		contentPane.add(lblMin);
		
		JLabel lblEinfgDatum = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		lblEinfgDatum.setBounds(12, 474, 71, 16);
		contentPane.add(lblEinfgDatum);
		
		spnOnlineScore = new JSpinner();
		spnOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnOnlineScore.setBounds(95, 506, 193, 20);
		contentPane.add(spnOnlineScore);
		
		JLabel lblOnlinescore = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		lblOnlinescore.setBounds(12, 507, 71, 16);
		contentPane.add(lblOnlinescore);
		
		JLabel label = new JLabel("/ 10"); //$NON-NLS-1$
		label.setBounds(305, 511, 52, 16);
		contentPane.add(label);
		
		cbxFSK = new JComboBox<Object>();
		cbxFSK.setBounds(76, 541, 212, 22);
		contentPane.add(cbxFSK);
		
		JLabel lblFsk = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		lblFsk.setBounds(12, 544, 52, 16);
		contentPane.add(lblFsk);
		
		cbxFormat = new JComboBox<Object>();
		cbxFormat.setBounds(76, 576, 212, 22);
		contentPane.add(cbxFormat);
		
		JLabel lblFormat = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		lblFormat.setBounds(12, 579, 52, 16);
		contentPane.add(lblFormat);
		
		spnYear = new JSpinner();
		spnYear.setModel(new SpinnerNumberModel(new Integer(1900), new Integer(1900), null, new Integer(1)));
		spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "0")); //$NON-NLS-1$
		spnYear.setBounds(76, 611, 212, 20);
		contentPane.add(spnYear);
		
		JLabel lblYear = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		lblYear.setBounds(12, 615, 52, 16);
		contentPane.add(lblYear);
		
		spnSize = new JSpinner();
		spnSize.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateByteDisp();
			}
		});
		spnSize.setModel(new SpinnerNumberModel(new Long(0), new Long(0), null, new Long(1)));
		spnSize.setBounds(76, 647, 212, 20);
		contentPane.add(spnSize);
		
		JLabel lblGre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		lblGre.setBounds(12, 651, 52, 16);
		contentPane.add(lblGre);
		
		JLabel lblNewLabel = new JLabel("Byte = "); //$NON-NLS-1$
		lblNewLabel.setBounds(296, 649, 37, 16);
		contentPane.add(lblNewLabel);
		
		btnOK = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnOK(true);
			}
		});
		btnOK.setBounds(265, 724, 95, 25);
		contentPane.add(btnOK);
		
		btnCancel = new JButton(LocaleBundle.getString("AddMovieFrame.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancel();
			}
		});
		btnCancel.setBounds(379, 724, 95, 25);
		contentPane.add(btnCancel);
		
		edTitle = new JTextField();
		edTitle.setColumns(10);
		edTitle.setBounds(76, 266, 212, 20);
		contentPane.add(edTitle);
		
		JLabel label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label_1.setBounds(12, 267, 52, 16);
		contentPane.add(label_1);
		
		spnZyklus = new JSpinner();
		spnZyklus.setModel(new SpinnerNumberModel(-1, -1, 255, 1));
		spnZyklus.setBounds(236, 304, 52, 20);
		contentPane.add(spnZyklus);
		
		btnParseIMDB = new JButton(LocaleBundle.getString("AddMovieFrame.btnParseIMDB.text")); //$NON-NLS-1$
		btnParseIMDB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showIMDBParser();
			}
		});
		btnParseIMDB.setFont(new Font("Tahoma", Font.BOLD, 15)); //$NON-NLS-1$
		btnParseIMDB.setBounds(508, 368, 212, 42);
		contentPane.add(btnParseIMDB);
		
		btnOpenIMDb = new JButton(CachedResourceLoader.getImageIcon(Resources.ICN_FRAMES_IMDB));
		btnOpenIMDb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HTTPUtilities.openInBrowser(ImDBParser.getSearchURL(getFullTitle(), CCMovieTyp.MOVIE));
			}
		});
		btnOpenIMDb.setBounds(300, 265, 57, 23);
		contentPane.add(btnOpenIMDb);
		
		lblFileSizeDisp = new JLabel();
		lblFileSizeDisp.setBounds(343, 649, 129, 16);
		contentPane.add(lblFileSizeDisp);
	}
	
	private void onBtnOK(boolean check) {
		ArrayList<UserDataProblem> problems = new ArrayList<>();
		
		boolean probvalue = (! check) || checkUserData(problems);
		
		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, this, this);
			amied.setVisible(true);
			return;
		}
		
		CCMovie newM = movieList.createNewEmptyMovie();
		
		newM.beginUpdating();
		
		//#####################################################################################
		
		newM.setPart(0, ed_Part0.getText());
		newM.setPart(1, ed_Part1.getText());
		newM.setPart(2, ed_Part2.getText());
		newM.setPart(3, ed_Part3.getText());
		newM.setPart(4, ed_Part4.getText());
		newM.setPart(5, ed_Part5.getText());
		
		newM.setTitle(edTitle.getText());
		newM.setZyklusTitle(edZyklus.getText());
		newM.setZyklusID((int) spnZyklus.getValue());
		
		newM.setViewed(cbxViewed.isSelected());
		
		newM.setQuality(cbxQuality.getSelectedIndex());
		newM.setLanguage(cbxLanguage.getSelectedIndex());
		
		newM.setLength((int) spnLength.getValue());
		
		newM.setDate((CCDate) spnAddDate.getValue());
		
		newM.setOnlinescore((int) spnOnlineScore.getValue());
		
		newM.setFsk(cbxFSK.getSelectedIndex());
		newM.setFormat(cbxFormat.getSelectedIndex());
		
		newM.setYear((int) spnYear.getValue());
		newM.setFilesize((long) spnSize.getValue());
		
		newM.setGenre(CCMovieGenre.find(cbxGenre0.getSelectedIndex()), 0);
		newM.setGenre(CCMovieGenre.find(cbxGenre1.getSelectedIndex()), 1);
		newM.setGenre(CCMovieGenre.find(cbxGenre2.getSelectedIndex()), 2);
		newM.setGenre(CCMovieGenre.find(cbxGenre3.getSelectedIndex()), 3);
		newM.setGenre(CCMovieGenre.find(cbxGenre4.getSelectedIndex()), 4);
		newM.setGenre(CCMovieGenre.find(cbxGenre5.getSelectedIndex()), 5);
		newM.setGenre(CCMovieGenre.find(cbxGenre6.getSelectedIndex()), 6);
		newM.setGenre(CCMovieGenre.find(cbxGenre7.getSelectedIndex()), 7);
		
		newM.setCover(currentCoverImage);
		
		//#####################################################################################
		
		newM.endUpdating();
		
		dispose();
	}
	
	private void cancel() {
		this.dispose();
	}
	
	private void showFindCoverDialog() {
		(new FindCoverDialog(this, this, CCMovieTyp.MOVIE)).setVisible(true);
	}
	
	private void showIMDBParser() {
		(new ParseImDBDialog(this, this, CCMovieTyp.MOVIE)).setVisible(true);
	}
	
	private void openChooseCoverDialog() {
		int returnval = coverFileChooser.showOpenDialog(this);
		
		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}
		try {
			BufferedImage read = ImageIO.read(coverFileChooser.getSelectedFile());
			setCover(read);
		} catch (IOException e) {
			CCLog.addError(e);
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
		
		//###################################################################################################################################
		
		coverFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.coverFileChooser.filterDescription", "png", "bmp", "gif", "jpg", "jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		
		coverFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.coverFileChooser.title")); //$NON-NLS-1$
	}

	private void setDefaultValues() {
		if (CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()) {
			rdbtnEnableAutomRelative.setSelected(true);
		} else {
			rdbtnDisableAutomRelative.setSelected(true);
		}
		
		lblCover.setIcon(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_STANDARD));
		
		cbxQuality.setModel(new DefaultComboBoxModel(CCMovieQuality.getList()));
		
		cbxLanguage.setModel(new DefaultComboBoxModel(CCMovieLanguage.getList()));
		
		DefaultComboBoxModel cbFSKdcbm;
		cbxFSK.setModel(cbFSKdcbm = new DefaultComboBoxModel(CCMovieFSK.getList()));
		cbFSKdcbm.addElement(" "); //$NON-NLS-1$
		cbxFSK.setSelectedIndex(cbFSKdcbm.getSize() - 1);
		
		cbxFormat.setModel(new DefaultComboBoxModel(CCMovieFormat.getList()));
		
		cbxGenre0.setModel(new DefaultComboBoxModel(CCMovieGenre.getTrimmedList()));
		cbxGenre1.setModel(new DefaultComboBoxModel(CCMovieGenre.getTrimmedList()));
		cbxGenre2.setModel(new DefaultComboBoxModel(CCMovieGenre.getTrimmedList()));
		cbxGenre3.setModel(new DefaultComboBoxModel(CCMovieGenre.getTrimmedList()));
		cbxGenre4.setModel(new DefaultComboBoxModel(CCMovieGenre.getTrimmedList()));
		cbxGenre5.setModel(new DefaultComboBoxModel(CCMovieGenre.getTrimmedList()));
		cbxGenre6.setModel(new DefaultComboBoxModel(CCMovieGenre.getTrimmedList()));
		cbxGenre7.setModel(new DefaultComboBoxModel(CCMovieGenre.getTrimmedList()));
		
		spnAddDate.setValue(new CCDate());
		spnZyklus.setValue(-1);
		
		updateByteDisp();
	}
	
	private void setEnabledAll(boolean e) {
		edZyklus.setEnabled(e);
		btnParseIMDB.setEnabled(e);
		btnOpenIMDb.setEnabled(e);
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
		btnOpenCover.setEnabled(e);
		btnFindCover.setEnabled(e);
		cbxViewed.setEnabled(e);
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
	}
	
	private void onBtnChooseClicked(int cNmbr) {
		int returnval = videoFileChooser.showOpenDialog(this);
		
		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}
		
		setFilepath(cNmbr, videoFileChooser.getSelectedFile().getAbsolutePath());
		
		if (firstChooseClick) {
			setEnabledAll(true);
			new FilenameParser(this).parse(videoFileChooser.getSelectedFile().getAbsolutePath());
			
			firstChooseClick = false;
		}
		
		updateFilesize();
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
	public void setMovieFormat(CCMovieFormat cmf) {
		cbxFormat.setSelectedIndex(cmf.asInt());
	}

	@Override
	public void setFilepath(int p, String t) {
		String pt;
		if (CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue()) {
			pt = PathFormatter.getRelative(t);
		} else {
			pt = t;
		}
				
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
		
		updateFilesize();
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
	
	private void updateFilesize() {
		CCMovieSize size = new CCMovieSize();
		
		if (! ed_Part0.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.getAbsolute(ed_Part0.getText())));
		}
		
		if (! ed_Part1.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.getAbsolute(ed_Part1.getText())));
		}
		
		if (! ed_Part2.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.getAbsolute(ed_Part3.getText())));
		}
		
		if (! ed_Part3.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.getAbsolute(ed_Part3.getText())));
		}
		
		if (! ed_Part4.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.getAbsolute(ed_Part4.getText())));
		}
		
		if (! ed_Part5.getText().isEmpty()) {
			size.add(FileSizeFormatter.getFileSize(PathFormatter.getAbsolute(ed_Part5.getText())));
		}
		
		setFilesize(size.getBytes());
	}
	
	@Override
	public void setCover(BufferedImage nci) {
		nci = ImageUtilities.resizeCoverImage(nci);
		
		this.currentCoverImage = nci;
		if (nci != null) {
			lblCover.setIcon(new ImageIcon(currentCoverImage));
		} else {
			lblCover.setIcon(null);
		}
	}
	
	public boolean checkUserData(ArrayList<UserDataProblem> ret) {
		String path0 = ed_Part0.getText();
		String path1 = ed_Part1.getText();
		String path2 = ed_Part2.getText();
		String path3 = ed_Part3.getText();
		String path4 = ed_Part4.getText();
		String path5 = ed_Part5.getText();
		
		String title = edTitle.getText();
		String zyklus = edZyklus.getText();
		int zyklusID = (int) spnZyklus.getValue();
		
		int len = (int) spnLength.getValue();
		CCDate adddate = (CCDate) spnAddDate.getValue();
		int oscore = (int) spnOnlineScore.getValue();
		
		int fskidx = cbxFSK.getSelectedIndex();
		int year = (int) spnYear.getValue();
		long fsize = (long) spnSize.getValue();
		String csExtn  = CCMovieFormat.find(cbxFormat.getSelectedIndex()).asString();
		String csExta = CCMovieFormat.find(cbxFormat.getSelectedIndex()).asString_Alt();
		
		int gen0 = cbxGenre0.getSelectedIndex();
		int gen1 = cbxGenre1.getSelectedIndex();
		int gen2 = cbxGenre2.getSelectedIndex();
		int gen3 = cbxGenre3.getSelectedIndex();
		int gen4 = cbxGenre4.getSelectedIndex();
		int gen5 = cbxGenre5.getSelectedIndex();
		int gen6 = cbxGenre6.getSelectedIndex();
		int gen7 = cbxGenre7.getSelectedIndex();
		
		//################################################################################################################
		
		if (path0.isEmpty() && path1.isEmpty() && path2.isEmpty() && path3.isEmpty() && path4.isEmpty() && path5.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_PATH));
		}
		
		//################################################################################################################
		
		if ((path0.isEmpty() && ! path1.isEmpty()) || (path1.isEmpty() && ! path2.isEmpty()) || (path2.isEmpty() && ! path3.isEmpty()) || (path3.isEmpty() && ! path4.isEmpty()) || (path4.isEmpty() && ! path5.isEmpty())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_HOLE_IN_PATH));
		}
		
		//################################################################################################################
		
		if (title.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################
		
		if (zyklus.isEmpty() && zyklusID != -1) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSID_IS_SET));
		}
		
		//################################################################################################################
		
		if (! zyklus.isEmpty() && zyklusID == 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSID_IS_ZERO));
		}
		
		//################################################################################################################
		
		if (len <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_LENGTH));
		}
		
		//################################################################################################################

		if (adddate.isLessEqualsThan(MIN_DATE)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DATE_TOO_LESS));
		}
		
		//################################################################################################################
		
		if (oscore <= 0 || oscore >= 10) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_ONLINESCORE));
		}
		
		//################################################################################################################
		
		if (fskidx == (cbxFSK.getModel().getSize() - 1)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_FSK_NOT_SET));
		}
		
		//################################################################################################################
		
		ArrayList<String> extensions = new ArrayList<>();
		
		if (! ed_Part0.getText().isEmpty()) {
			extensions.add(PathFormatter.getExtension(path0));
		}
		
		if (! ed_Part1.getText().isEmpty()) {
			extensions.add(PathFormatter.getExtension(path1));
		}
		
		if (! ed_Part2.getText().isEmpty()) {
			extensions.add(PathFormatter.getExtension(path2));
		}
		
		if (! ed_Part3.getText().isEmpty()) {
			extensions.add(PathFormatter.getExtension(path3));
		}
		
		if (! ed_Part4.getText().isEmpty()) {
			extensions.add(PathFormatter.getExtension(path4));
		}
		
		if (! ed_Part5.getText().isEmpty()) {
			extensions.add(PathFormatter.getExtension(path5));
		}
		
		if (! (extensions.contains(csExtn) || extensions.contains(csExta))) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EXTENSION_UNEQUALS_FILENAME));
		}
		
		//################################################################################################################
		
		if (year <= CCDate.YEAR_MIN) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_YEAR));
		}
		
		//################################################################################################################
		
		if (fsize <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_FILESIZE));
		}
		
		//################################################################################################################
		
		int gc = gen0 + gen1 + gen2 + gen3 + gen4 + gen5 + gen6 + gen7;

		if (gc <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_GENRE_SET));
		}
		//################################################################################################################
		
		if ((gen0 == 0 && gen1 != 0) || (gen1 == 0 && gen2 != 0) || (gen2 == 0 && gen3 != 0) || (gen3 == 0 && gen4 != 0) || (gen4 == 0 && gen5 != 0) || (gen5 == 0 && gen6 != 0) || (gen6 == 0 && gen7 != 0)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_HOLE_IN_GENRE));
		}
		
		//################################################################################################################
		
		if (currentCoverImage == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER_SET));
		}
		
		//################################################################################################################
		
		return ret.isEmpty();
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		onBtnOK(false);
	}
}
