package de.jClipCorn.gui.frames.previewMovieFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.features.actionTree.menus.impl.PreviewMovieMenuBar;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.OnlineRefButton;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.TagPanel;
import de.jClipCorn.gui.guiComponents.language.LanguageDisplay;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public class PreviewMovieFrame extends JFrame implements UpdateCallbackListener {
	private static final long serialVersionUID = 7483476533745432416L;
	
	private final CCMovie movie;
	private CoverLabel lblCover;
	private JLabel label;
	private JLabel lblViewed;
	private JList<String> lsGenres;
	private ReadableTextField edPart0;
	private ReadableTextField edPart1;
	private ReadableTextField edPart2;
	private ReadableTextField edPart3;
	private ReadableTextField edPart4;
	private ReadableTextField edPart5;
	private JLabel lblQuality;
	private JLabel lblLanguage;
	private JLabel lblLength;
	private JLabel lblAdded;
	private JLabel lblScore;
	private JLabel lblFsk;
	private JLabel lblFormat;
	private JLabel lblYear;
	private JLabel lblSize;
	private OnlineRefButton btnOnlineRef;
	private JLabel lblGenre;
	private JLabel lblScore_1;
	private JLabel lbl_Quality;
	private LanguageDisplay lbl_Language;
	private JLabel lbl_Length;
	private JLabel lbl_Added;
	private JLabel lbl_FSK;
	private JLabel lbl_Format;
	private JLabel lbl_Year;
	private JLabel lbl_Size;
	private JLabel lbl_OnlineScore;
	private JLabel lbl_Score;
	private PreviewMovieMenuBar menuBar;
	private JButton btnPlay;
	private TagPanel pnlTags;
	private JLabel lblTags;
	private JList<String> lsHistory;
	private JLabel lblViewedHistory;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JLabel lblGruppen;
	private JScrollPane scrollPane_2;
	private JList<String> lsGroups;
	
	public PreviewMovieFrame(Component owner, CCMovie m) {
		super();
		this.movie = m;
		initGUI(m);
		updateFields();
		
		setLocationRelativeTo(owner);
	}
	
	private void initGUI(CCMovie m) {
		setSize(new Dimension(700, 565));
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		lblCover = new CoverLabel(false);
		lblCover.setPosition(10, 53);
		getContentPane().add(lblCover);
		
		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.BOLD, 28)); //$NON-NLS-1$
		label.setBounds(44, 0, 650, 42);
		getContentPane().add(label);
		
		lblViewed = new JLabel();
		lblViewed.setBounds(10, 11, 30, 16);
		getContentPane().add(lblViewed);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(202, 343, 220, 87);
		getContentPane().add(scrollPane);
		
		lsGenres = new JList<>();
		scrollPane.setViewportView(lsGenres);
		
		edPart0 = new ReadableTextField();
		edPart0.setBounds(201, 53, 370, 20);
		getContentPane().add(edPart0);
		edPart0.setColumns(10);
		
		edPart1 = new ReadableTextField();
		edPart1.setColumns(10);
		edPart1.setBounds(202, 84, 483, 20);
		getContentPane().add(edPart1);
		
		edPart2 = new ReadableTextField();
		edPart2.setColumns(10);
		edPart2.setBounds(202, 115, 483, 20);
		getContentPane().add(edPart2);
		
		edPart3 = new ReadableTextField();
		edPart3.setColumns(10);
		edPart3.setBounds(202, 146, 483, 20);
		getContentPane().add(edPart3);
		
		edPart4 = new ReadableTextField();
		edPart4.setColumns(10);
		edPart4.setBounds(202, 177, 483, 20);
		getContentPane().add(edPart4);
		
		edPart5 = new ReadableTextField();
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
		lblScore.setBounds(202, 270, 88, 14);
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
		
		btnOnlineRef = new OnlineRefButton();
		btnOnlineRef.setBounds(583, 50, 101, 23);
		getContentPane().add(btnOnlineRef);
		
		lblGenre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		lblGenre.setBounds(202, 318, 46, 14);
		getContentPane().add(lblGenre);
		
		lblScore_1 = new JLabel(LocaleBundle.getString("PreviewMovieFrame.btnScore.text")); //$NON-NLS-1$
		lblScore_1.setBounds(202, 441, 46, 14);
		getContentPane().add(lblScore_1);
		
		lbl_Quality = new JLabel();
		lbl_Quality.setBounds(93, 317, 99, 16);
		getContentPane().add(lbl_Quality);
		
		lbl_Language = new LanguageDisplay();
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
		lbl_OnlineScore.setBounds(300, 269, 82, 16);
		getContentPane().add(lbl_OnlineScore);
		
		lbl_Score = new JLabel();
		lbl_Score.setBounds(258, 440, 16, 16);
		getContentPane().add(lbl_Score);
		
		menuBar = new PreviewMovieMenuBar(this, m, this::updateFields);
		setJMenuBar(menuBar);

		btnPlay = new JButton(Resources.ICN_MENUBAR_PLAY.get32x32());
		btnPlay.addActionListener(e -> playMovie());
		btnPlay.setBounds(619, 466, 65, 41);
		getContentPane().add(btnPlay);
		
		pnlTags = new TagPanel();
		pnlTags.setReadOnly(true);
		pnlTags.setBounds(268, 468, 341, 22);
		getContentPane().add(pnlTags);
		
		lblTags = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
		lblTags.setBounds(202, 468, 46, 14);
		getContentPane().add(lblTags);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setBounds(462, 343, 220, 87);
		getContentPane().add(scrollPane_1);
		
		lsHistory = new JList<>();
		scrollPane_1.setViewportView(lsHistory);
		
		lblViewedHistory = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
		lblViewedHistory.setBounds(462, 317, 46, 14);
		getContentPane().add(lblViewedHistory);
		
		lblGruppen = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
		lblGruppen.setBounds(462, 269, 55, 16);
		getContentPane().add(lblGruppen);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBorder(null);
		scrollPane_2.setBounds(535, 270, 147, 48);
		getContentPane().add(scrollPane_2);
		
		lsGroups = new JList<>();
		lsGroups.setOpaque(false);
		scrollPane_2.setViewportView(lsGroups);
	}
	
	private void playMovie() {
		movie.play(true);
	}

	private void updateFields() {
		if (Main.DEBUG) {
			setTitle("<" + movie.getLocalID() + "> " + movie.getCompleteTitle() + " (" + movie.getCoverName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			setTitle(movie.getCompleteTitle());
		}
		
		lblCover.setAndResizeCover(movie.getCover());
		label.setText(movie.getCompleteTitle());
		
		lblViewed.setIcon(movie.isViewed()?Resources.ICN_TABLE_VIEWED_TRUE.get():null);
		
		lbl_Quality.setIcon(movie.getQuality().getIcon());
		lbl_Quality.setText(movie.getQuality().asString());
		
		lbl_Language.setValue(movie.getLanguage());
		
		lbl_Length.setText(TimeIntervallFormatter.formatPointed(movie.getLength()));
		
		lbl_Added.setText(movie.getAddDate().toStringUINormal());
		
		lbl_FSK.setIcon(movie.getFSK().getIcon());
		lbl_FSK.setText(movie.getFSK().asString());
		
		lbl_Format.setIcon(movie.getFormat().getIcon());
		lbl_Format.setText(movie.getFormat().asString());
		
		lbl_Year.setText(movie.getYear() + ""); //$NON-NLS-1$
		
		lbl_Score.setIcon(movie.getScore().getIcon());
		lbl_Score.setToolTipText(movie.getScore().asString());
		
		lbl_Size.setText(FileSizeFormatter.format(movie.getFilesize()));
		
		pnlTags.setValue(movie.getTags());
		
		lbl_OnlineScore.setIcon(movie.getOnlinescore().getIcon());
		
		DefaultListModel<String> dlsmGenre;
		lsGenres.setModel(dlsmGenre = new DefaultListModel<>());
		for (int i = 0; i < movie.getGenreCount(); i++) {
			dlsmGenre.addElement(movie.getGenre(i).asString());
		}

		DefaultListModel<String> dlsmGroups;
		lsGroups.setModel(dlsmGroups = new DefaultListModel<>());
		for (CCGroup group : movie.getGroups()) {
			dlsmGroups.addElement(group.Name);
		}
				
		edPart0.setText(movie.getPart(0));
		edPart1.setText(movie.getPart(1));
		edPart2.setText(movie.getPart(2));
		edPart3.setText(movie.getPart(3));
		edPart4.setText(movie.getPart(4));
		edPart5.setText(movie.getPart(5));
		
		DefaultListModel<String> dlsmViewed;
		lsHistory.setModel(dlsmViewed = new DefaultListModel<>());
		for (CCDateTime dt : movie.getViewedHistory().iterator()) {
			dlsmViewed.addElement(dt.toStringUINormal());
		}
		
		btnOnlineRef.setValue(movie.getOnlineReference());
	}

	@Override
	public void onUpdate(Object o) {
		updateFields();
	}
}
