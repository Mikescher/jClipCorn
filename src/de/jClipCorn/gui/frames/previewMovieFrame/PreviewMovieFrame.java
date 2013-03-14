package de.jClipCorn.gui.frames.previewMovieFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.FileSizeFormatter;
import de.jClipCorn.util.HTTPUtilities;
import de.jClipCorn.util.TimeIntervallFormatter;
import de.jClipCorn.util.UpdateCallbackListener;
import de.jClipCorn.util.parser.ImDBParser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PreviewMovieFrame extends JFrame implements UpdateCallbackListener {
	private static final long serialVersionUID = 7483476533745432416L;
	
	private final CCMovie movie;
	private JLabel lblCover;
	private JLabel label;
	private JLabel lblViewed;
	private JList<String> lsGenres;
	private JTextField edPart0;
	private JTextField edPart1;
	private JTextField edPart2;
	private JTextField edPart3;
	private JTextField edPart4;
	private JTextField edPart5;
	private JLabel lblQuality;
	private JLabel lblLanguage;
	private JLabel lblLength;
	private JLabel lblAdded;
	private JLabel lblScore;
	private JLabel lblFsk;
	private JLabel lblFormat;
	private JLabel lblYear;
	private JLabel lblSize;
	private JButton btnImDB;
	private JLabel lblGenre;
	private JLabel lblScore_1;
	private JButton btnChange;
	private JLabel lblStatus;
	private JLabel lbl_Quality;
	private JLabel lbl_Language;
	private JLabel lbl_Length;
	private JLabel lbl_Added;
	private JLabel lbl_FSK;
	private JLabel lbl_Format;
	private JLabel lbl_Year;
	private JLabel lbl_Size;
	private JLabel lbl_OnlineScore;
	private JLabel lbl_Score;
	private JLabel lbl_Status;
	private JButton btnReset;
	private JMenuBar menuBar;
	private JMenu mnMovie;
	private JMenuItem mntmEditMovie;
	private JMenuItem mntmDeleetMovie;
	private JMenu mnExtras;
	private JMenuItem mntmShowInImdb;
	private JButton btnPlay;
	private JMenuItem mntmPlayMovie;
	
	public PreviewMovieFrame(Component owner, CCMovie m) {
		super();
		this.movie = m;
		initGUI();
		updateFields();
		
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setSize(new Dimension(700, 565));
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		lblCover = new JLabel();
		lblCover.setBounds(10, 53, 182, 254);
		getContentPane().add(lblCover);
		
		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.BOLD, 28)); //$NON-NLS-1$
		label.setBounds(0, 0, 694, 42);
		getContentPane().add(label);
		
		lblViewed = new JLabel();
		lblViewed.setBounds(10, 11, 24, 16);
		getContentPane().add(lblViewed);
		
		lsGenres = new JList<String>();
		lsGenres.setBounds(202, 343, 482, 87);
		getContentPane().add(lsGenres);
		
		edPart0 = new JTextField();
		edPart0.setEditable(false);
		edPart0.setBounds(201, 53, 483, 20);
		getContentPane().add(edPart0);
		edPart0.setColumns(10);
		
		edPart1 = new JTextField();
		edPart1.setEditable(false);
		edPart1.setColumns(10);
		edPart1.setBounds(202, 84, 483, 20);
		getContentPane().add(edPart1);
		
		edPart2 = new JTextField();
		edPart2.setEditable(false);
		edPart2.setColumns(10);
		edPart2.setBounds(202, 115, 483, 20);
		getContentPane().add(edPart2);
		
		edPart3 = new JTextField();
		edPart3.setEditable(false);
		edPart3.setColumns(10);
		edPart3.setBounds(202, 146, 483, 20);
		getContentPane().add(edPart3);
		
		edPart4 = new JTextField();
		edPart4.setEditable(false);
		edPart4.setColumns(10);
		edPart4.setBounds(202, 177, 483, 20);
		getContentPane().add(edPart4);
		
		edPart5 = new JTextField();
		edPart5.setEditable(false);
		edPart5.setColumns(10);
		edPart5.setBounds(201, 208, 483, 20);
		getContentPane().add(edPart5);
		
		lblQuality = new JLabel(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
		lblQuality.setBounds(10, 318, 63, 14);
		getContentPane().add(lblQuality);
		
		lblLanguage = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		lblLanguage.setBounds(10, 343, 63, 14);
		getContentPane().add(lblLanguage);
		
		lblLength = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		lblLength.setBounds(10, 368, 63, 14);
		getContentPane().add(lblLength);
		
		lblAdded = new JLabel(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
		lblAdded.setBounds(10, 393, 63, 14);
		getContentPane().add(lblAdded);
		
		lblScore = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		lblScore.setBounds(202, 270, 63, 14);
		getContentPane().add(lblScore);
		
		lblFsk = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		lblFsk.setBounds(10, 418, 63, 14);
		getContentPane().add(lblFsk);
		
		lblFormat = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
		lblFormat.setBounds(10, 443, 63, 14);
		getContentPane().add(lblFormat);
		
		lblYear = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		lblYear.setBounds(10, 468, 63, 14);
		getContentPane().add(lblYear);
		
		lblSize = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
		lblSize.setBounds(10, 493, 63, 14);
		getContentPane().add(lblSize);
		
		btnImDB = new JButton(CachedResourceLoader.getImageIcon(Resources.ICN_FRAMES_IMDB));
		btnImDB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openImDB();
			}
		});
		btnImDB.setBounds(627, 11, 57, 23);
		getContentPane().add(btnImDB);
		
		lblGenre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		lblGenre.setBounds(202, 318, 46, 14);
		getContentPane().add(lblGenre);
		
		lblScore_1 = new JLabel(LocaleBundle.getString("PreviewMovieFrame.btnScore.text")); //$NON-NLS-1$
		lblScore_1.setBounds(202, 441, 46, 14);
		getContentPane().add(lblScore_1);
		
		btnChange = new JButton(LocaleBundle.getString("PreviewMovieFrame.btnChange.text")); //$NON-NLS-1$
		btnChange.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//TODO Change Score
			}
		});
		btnChange.setBounds(332, 439, 80, 23);
		getContentPane().add(btnChange);
		
		lblStatus = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblStatus.text")); //$NON-NLS-1$
		lblStatus.setBounds(202, 466, 46, 14);
		getContentPane().add(lblStatus);
		
		lbl_Quality = new JLabel();
		lbl_Quality.setBounds(93, 317, 99, 16);
		getContentPane().add(lbl_Quality);
		
		lbl_Language = new JLabel();
		lbl_Language.setBounds(93, 342, 99, 16);
		getContentPane().add(lbl_Language);
		
		lbl_Length = new JLabel();
		lbl_Length.setBounds(93, 367, 99, 16);
		getContentPane().add(lbl_Length);
		
		lbl_Added = new JLabel();
		lbl_Added.setBounds(93, 392, 99, 16);
		getContentPane().add(lbl_Added);
		
		lbl_FSK = new JLabel();
		lbl_FSK.setBounds(93, 417, 99, 16);
		getContentPane().add(lbl_FSK);
	
		lbl_Format = new JLabel();
		lbl_Format.setBounds(93, 442, 99, 16);
		getContentPane().add(lbl_Format);
		
		lbl_Year = new JLabel();
		lbl_Year.setBounds(93, 467, 99, 16);
		getContentPane().add(lbl_Year);
		
		lbl_Size = new JLabel();
		lbl_Size.setBounds(93, 492, 99, 16);
		getContentPane().add(lbl_Size);
		
		lbl_OnlineScore = new JLabel();
		lbl_OnlineScore.setBounds(275, 269, 82, 16);
		getContentPane().add(lbl_OnlineScore);
		
		lbl_Score = new JLabel();
		lbl_Score.setBounds(258, 440, 16, 16);
		getContentPane().add(lbl_Score);
		
		lbl_Status = new JLabel();
		lbl_Status.setBounds(258, 465, 80, 16);
		getContentPane().add(lbl_Status);
		
		btnReset = new JButton(LocaleBundle.getString("PreviewMovieFrame.btnStatusReset.text")); //$NON-NLS-1$
		btnReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetStatus();
			}
		});
		btnReset.setBounds(332, 464, 80, 23);
		getContentPane().add(btnReset);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnMovie = new JMenu(LocaleBundle.getString("PreviewMovieFrame.Menubar.Movie")); //$NON-NLS-1$
		menuBar.add(mnMovie);
		
		mntmPlayMovie = new JMenuItem(LocaleBundle.getString("PreviewMovieFrame.Menubar.Movie.Play")); //$NON-NLS-1$
		mntmPlayMovie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playMovie();
			}
		});
		mnMovie.add(mntmPlayMovie);
		
		mntmEditMovie = new JMenuItem(LocaleBundle.getString("PreviewMovieFrame.Menubar.Movie.Edit")); //$NON-NLS-1$
		mntmEditMovie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editMovie();
			}
		});
		mnMovie.add(mntmEditMovie);
		
		mntmDeleetMovie = new JMenuItem(LocaleBundle.getString("PreviewMovieFrame.Menubar.Movie.Delete")); //$NON-NLS-1$
		mntmDeleetMovie.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteMovie();
			}
		});
		mnMovie.add(mntmDeleetMovie);
		
		mnExtras = new JMenu(LocaleBundle.getString("PreviewMovieFrame.Menubar.Extras")); //$NON-NLS-1$
		menuBar.add(mnExtras);
		
		mntmShowInImdb = new JMenuItem(LocaleBundle.getString("PreviewMovieFrame.Menubar.Extras.ImDB")); //$NON-NLS-1$
		mntmShowInImdb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openImDB();
			}
		});
		mnExtras.add(mntmShowInImdb);
		
		btnPlay = new JButton(CachedResourceLoader.getImageIcon(Resources.ICN_MENUBAR_PLAY));
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playMovie();
			}
		});
		btnPlay.setBounds(619, 466, 65, 41);
		getContentPane().add(btnPlay);
	}
	
	private void playMovie() {
		movie.play();
	}
	
	private void deleteMovie() {
		if (DialogHelper.showLocaleYesNo(this, "Dialogs.DeleteMovie")) { //$NON-NLS-1$
			movie.delete();
			
			dispose();
		}
	}
	
	private void editMovie() {
		EditMovieFrame emf =  new EditMovieFrame(this, movie, this);
		
		emf.setVisible(true);
	}

	private void resetStatus() {
		movie.setStatus(CCMovieStatus.STATUS_OK);
		updateFields();
	}
	
	private void updateFields() {
		setTitle(movie.getCompleteTitle());
		lblCover.setIcon(movie.getCoverIcon());
		label.setText(movie.getCompleteTitle());
		
		lblViewed.setIcon(movie.isViewed()?CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_VIEWED_TRUE):null);
		
		lbl_Quality.setIcon(movie.getQuality().getIcon());
		lbl_Quality.setText(movie.getQuality().asString());
		
		lbl_Language.setIcon(movie.getLanguage().getIcon());
		lbl_Language.setText(movie.getLanguage().asString());
		
		lbl_Length.setText(TimeIntervallFormatter.formatPointed(movie.getLength()));
		
		lbl_Added.setText(movie.getDate().getSimpleStringRepresentation());
		
		lbl_FSK.setIcon(movie.getFSK().getIcon());
		lbl_FSK.setText(movie.getFSK().asString());
		
		lbl_Format.setIcon(movie.getFormat().getIcon());
		lbl_Format.setText(movie.getFormat().asString());
		
		lbl_Year.setText(movie.getYear() + ""); //$NON-NLS-1$
		
		lbl_Score.setIcon(movie.getScore().getIcon());
		lbl_Score.setToolTipText(movie.getScore().asString());
		
		lbl_Status.setText(movie.getStatus().asString());
		
		lbl_Size.setText(FileSizeFormatter.format(movie.getFilesize()));
		
		lbl_OnlineScore.setIcon(movie.getOnlinescore().getIcon());
		
		DefaultListModel<String> dlsm;
		lsGenres.setModel(dlsm = new DefaultListModel<>());
		for (int i = 0; i < movie.getGenreCount(); i++) {
			dlsm.addElement(movie.getGenre(i).asString());
		}
		
		edPart0.setText(movie.getPart(0));
		edPart1.setText(movie.getPart(1));
		edPart2.setText(movie.getPart(2));
		edPart3.setText(movie.getPart(3));
		edPart4.setText(movie.getPart(4));
		edPart5.setText(movie.getPart(5));
	}

	private void openImDB() {
		HTTPUtilities.openInBrowser(ImDBParser.getSearchURL(movie.getCompleteTitle(), CCMovieTyp.MOVIE));
	}

	@Override
	public void onUpdate(Object o) {
		updateFields();
	}
}
