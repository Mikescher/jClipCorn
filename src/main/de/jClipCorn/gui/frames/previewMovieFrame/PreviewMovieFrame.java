package de.jClipCorn.gui.frames.previewMovieFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.features.actionTree.menus.impl.PreviewMovieMenuBar;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.*;
import de.jClipCorn.gui.guiComponents.cover.CoverLabelFullsize;
import de.jClipCorn.gui.guiComponents.iconComponents.*;
import de.jClipCorn.gui.guiComponents.language.*;
import de.jClipCorn.gui.guiComponents.language.LanguageSetDisplay;
import de.jClipCorn.gui.guiComponents.tags.TagDisplay;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.CCDBUpdateAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TimeZone;

public class PreviewMovieFrame extends JCCFrame implements UpdateCallbackListener
{
	private final static java.util.List<Tuple<CCMovie, PreviewMovieFrame>> _activeFrames = new ArrayList<>();

	private CCDBUpdateListener _mlListener;

	private final CCMovie movie;

	public PreviewMovieFrame(Component owner, CCMovie m)
	{
		super(m.getMovieList());
		movie = m;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		_activeFrames.add(Tuple.Create(movie, this));

		setJMenuBar(new PreviewMovieMenuBar(this, movie, this::updateData));

		updateData();

		movielist.addChangeListener(_mlListener = new CCDBUpdateAdapter() {
			@Override
			public void onChangeDatabaseElement(CCDatabaseElement root, ICCDatabaseStructureElement el, String[] props) {
				if (root.equals(movie)) updateData();
			}

			@Override
			public void onAddDatabaseElement(CCDatabaseElement el) {
				if (el.equals(movie)) updateData();
			}
		});
	}

	public static void show(Component owner, CCMovie data, boolean forceNoSingleton) {
		if (forceNoSingleton || !data.getMovieList().ccprops().PROP_PREVIEWMOVIE_SINGLETON.getValue()) {
			new PreviewMovieFrame(owner, data).setVisible(true);
			return;
		}

		Tuple<CCMovie, PreviewMovieFrame> frame = CCStreams.iterate(_activeFrames).firstOrNull(f -> f.Item1 == data);
		if (frame != null) {
			if(frame.Item2.getState() != Frame.NORMAL) frame.Item2.setState(Frame.NORMAL);
			frame.Item2.toFront();
			frame.Item2.repaint();
			return;
		}

		new PreviewMovieFrame(owner, data).setVisible(true);
	}

	private void updateData() {
		if (movie == null) return;

		if (Main.DEBUG) {
			setTitle("<" + movie.getLocalID() + "> " + movie.getCompleteTitle() + " (" + movie.getCoverID() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			setTitle(movie.getCompleteTitle());
		}

		lblCover.setAndResizeCover(movie.getCover());
		lblHeaderTitle.setText(movie.getCompleteTitle());

		lblTitle.setText(movie.Title.get());
		lblZyklus.setText(movie.Zyklus.get().isEmpty() ? Str.Empty : movie.Zyklus.get().getFormatted());

		lblViewed.setIcon(movie.isViewed() ? Resources.ICN_TABLE_VIEWED_TRUE.get() : null);

		CCQualityCategory qcat = movie.getMediaInfoCategory();
		lblQualityIcon.setIcon(qcat.getIcon());
		lblQuality.setText(qcat.getShortText());
		lblQuality.setToolTipText(qcat.getTooltip());

		lblLanguage.setValue(movie.Language.get());
		lblSubtitles.setValue(movie.Subtitles.get());

		lblLength.setText(TimeIntervallFormatter.formatPointed(movie.Length.get()));

		lblAdded.setText(movie.AddDate.get().toStringUINormal());

		lblFSKIcon.setIcon(movie.FSK.get().getIcon());
		lblFSK.setText(movie.FSK.get().asString());

		lblFormatIcon.setIcon(movie.Format.get().getIcon());
		lblFormat.setText(movie.Format.get().asString());

		lblYear.setText(movie.Year.get() + Str.Empty); //$NON-NLS-1$

		lblUserScoreIcon.setIcon(movie.Score.get().getIcon());
		lblUserScore.setText(movie.Score.get().asString());

		lblSize.setText(FileSizeFormatter.format(movie.FileSize.get()));

		lblTags.setValue(movie.Tags.get());

		lblOnlineScore.setOnlineScore(movie.OnlineScore.get());

		DefaultListModel<String> dlsmGenre;
		lsGenres.setModel(dlsmGenre = new DefaultListModel<>());
		for (int i = 0; i < movie.Genres.get().getGenreCount(); i++) {
			dlsmGenre.addElement(movie.Genres.get().getGenre(i).asString());
		}

		DefaultListModel<String> dlsmGroups;
		lsGroups.setModel(dlsmGroups = new DefaultListModel<>());
		for (CCGroup group : movie.getGroups()) {
			dlsmGroups.addElement(group.Name);
		}

		edPart0.setPath(movie.Parts.get(0));
		edPart1.setPath(movie.Parts.get(1));
		edPart2.setPath(movie.Parts.get(2));
		edPart3.setPath(movie.Parts.get(3));
		edPart4.setPath(movie.Parts.get(4));
		edPart5.setPath(movie.Parts.get(5));

		DefaultListModel<String> dlsmViewed;
		lsHistory.setModel(dlsmViewed = new DefaultListModel<>());
		for (CCDateTime dt : movie.ViewedHistory.get().ccstream()) {
			dlsmViewed.addElement(dt.toStringUINormal());
		}

		btnOnlineRef.setValue(movie.OnlineReference.get());

		CCMediaInfo mi = movie.MediaInfo.get();

		edMI_CDate   .setText(mi.isUnset() ? Str.Empty : CCDateTime.createFromFileTimestamp(mi.getCDate(), TimeZone.getDefault()).toStringISO());
		edMI_MDate   .setText(mi.isUnset() ? Str.Empty : CCDateTime.createFromFileTimestamp(mi.getMDate(), TimeZone.getDefault()).toStringISO());
		edMI_Filesize.setText(mi.isUnset() ? Str.Empty : FileSizeFormatter.formatPrecise(mi.getFilesize()));
		edMI_Duration.setText(mi.isUnset() ? Str.Empty : TimeIntervallFormatter.formatSeconds(mi.getDuration()));
		edMI_Bitrate .setText(mi.isUnset() ? Str.Empty : Str.spacegroupformat(mi.getNormalizedBitrate()) + " kbit/s"); //$NON-NLS-1$

		edMI_VideoFormat    .setText(mi.isUnset() ? Str.Empty : mi.getVideoFormat());
		edMI_VideoResolution.setText(mi.isUnset() ? Str.Empty : Str.format("{0,number,#} x {1,number,#}", mi.getWidth(), mi.getHeight())); //$NON-NLS-1$
		edMI_VideoFramerate .setText(mi.isUnset() ? Str.Empty : String.valueOf((int)Math.round(mi.getFramerate())));
		edMI_VideoBitdepth  .setText(mi.isUnset() ? Str.Empty : String.valueOf(mi.getBitdepth()));
		edMI_VideoFramecount.setText(mi.isUnset() ? Str.Empty : Str.spacegroupformat(mi.getFramecount()));
		edMI_VideoCodec     .setText(mi.isUnset() ? Str.Empty : mi.getVideoCodec());

		edMI_AudioFormat    .setText(mi.isUnset() ? Str.Empty : mi.getAudioFormat());
		edMI_AudioChannels  .setText(mi.isUnset() ? Str.Empty : String.valueOf(mi.getAudioChannels()));
		edMI_AudioCodec     .setText(mi.isUnset() ? Str.Empty : mi.getAudioCodec());
		edMI_AudioSamplerate.setText(mi.isUnset() ? Str.Empty : Str.spacegroupformat(mi.getAudioSamplerate()));

		btnMediaInfo0.setEnabled(!movie.Parts.get(0).isEmpty());
		btnOpenDir0  .setEnabled(!movie.Parts.get(0).isEmpty());

		btnMediaInfo1.setEnabled(!movie.Parts.get(1).isEmpty());
		btnOpenDir1  .setEnabled(!movie.Parts.get(1).isEmpty());

		btnMediaInfo2.setEnabled(!movie.Parts.get(2).isEmpty());
		btnOpenDir2  .setEnabled(!movie.Parts.get(2).isEmpty());

		btnMediaInfo3.setEnabled(!movie.Parts.get(3).isEmpty());
		btnOpenDir3  .setEnabled(!movie.Parts.get(3).isEmpty());

		btnMediaInfo4.setEnabled(!movie.Parts.get(4).isEmpty());
		btnOpenDir4  .setEnabled(!movie.Parts.get(4).isEmpty());

		btnMediaInfo5.setEnabled(!movie.Parts.get(5).isEmpty());
		btnOpenDir5  .setEnabled(!movie.Parts.get(5).isEmpty());
	}

	@Override
	public void onUpdate(Object o) {
		updateData();
	}

	private void onHistorySelected(PMHSelectionListener.PMHSelectionEvent e) {
		if (e.Entry == null) {
			tabHistoryChanges.clearData();
		} else {
			tabHistoryChanges.setData(CCStreams.iterate(e.Entry.Changes).autosortByProperty(p -> p.Field.toLowerCase()).enumerate());
			tabHistoryChanges.autoResize();
		}
	}

	private void showMediaInfo(int index) {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			GenericTextDialog.showText(this, getTitle(), new MediaQueryRunner(movielist).queryRaw(movie.Parts.get(index).toFSPath(this)), false);
		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void openDir(int index) {
		FilesystemUtils.showInExplorer(movie.Parts.get(index).toFSPath(this));
	}

	private void onWindowClosed() {
		_activeFrames.removeIf(p -> p.Item2 == this);
		movielist.removeChangeListener(_mlListener);
	}

	private void playMovie() {
		movie.play(PreviewMovieFrame.this, true);
	}

	private void playMovieNoHistory() {
		movie.play(PreviewMovieFrame.this, false);
	}

	private void queryHistory() {
		try {
			var data = movielist.getHistory().query(movielist, false, false, false, true, null, null, Integer.toString(movie.getLocalID()));
			tabHistoryEntries.setData(data);
			tabHistoryChanges.clearData();
			tabHistoryEntries.autoResize();
		} catch (CCFormatException e) {
			CCLog.addError(e);
			DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
		}
	}

	private void onShowMediaInfo0() {
		showMediaInfo(0);
	}

	private void onShowMediaInfo1() {
		showMediaInfo(1);
	}

	private void onShowMediaInfo2() {
		showMediaInfo(2);
	}

	private void onShowMediaInfo3() {
		showMediaInfo(3);
	}

	private void onShowMediaInfo4() {
		showMediaInfo(4);
	}

	private void onShowMediaInfo5() {
		showMediaInfo(5);
	}

	private void onOpenDir0() {
		openDir(0);
	}

	private void onOpenDir1() {
		openDir(1);
	}

	private void onOpenDir2() {
		openDir(2);
	}

	private void onOpenDir3() {
		openDir(3);
	}

	private void onOpenDir4() {
		openDir(4);
	}

	private void onOpenDir5() {
		openDir(5);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlTop = new JPanel();
		lblViewed = new JLabel();
		lblHeaderTitle = new JLabel();
		btnPlay = new CCIcon32Button();
		btnPlayNoHistory = new CCIcon32Button();
		pnlLeft = new JPanel();
		lblCover = new CoverLabelFullsize(movielist);
		tabbedPane1 = new JTabbedPane();
		pnlTabMain = new JPanel();
		label1 = new JLabel();
		lblTitle = new JLabel();
		btnOnlineRef = new OnlineRefButton(movielist);
		label2 = new JLabel();
		lblZyklus = new JLabel();
		label3 = new JLabel();
		lblQualityIcon = new JLabel();
		lblQuality = new JLabel();
		label4 = new JLabel();
		lblOnlineScore = new OnlineScoreDisplay();
		label5 = new JLabel();
		lblLanguage = new LanguageSetDisplay();
		label7 = new JLabel();
		lblSubtitles = new LanguageListDisplay();
		label6 = new JLabel();
		lblLength = new JLabel();
		label19 = new JLabel();
		lblAdded = new JLabel();
		label8 = new JLabel();
		lblFSKIcon = new JLabel();
		lblFSK = new JLabel();
		label9 = new JLabel();
		lblFormatIcon = new JLabel();
		lblFormat = new JLabel();
		label10 = new JLabel();
		lblYear = new JLabel();
		label11 = new JLabel();
		lblSize = new JLabel();
		label12 = new JLabel();
		lblUserScoreIcon = new JLabel();
		lblUserScore = new JLabel();
		label13 = new JLabel();
		lblTags = new TagDisplay();
		panel6 = new JPanel();
		label14 = new JLabel();
		label15 = new JLabel();
		label16 = new JLabel();
		scrollPane1 = new JScrollPane();
		lsGroups = new JList<>();
		scrollPane2 = new JScrollPane();
		lsGenres = new JList<>();
		scrollPane3 = new JScrollPane();
		lsHistory = new JList<>();
		pnlTabMediaInfo = new JPanel();
		panel7 = new JPanel();
		label17 = new JLabel();
		edMI_CDate = new ReadableTextField();
		label18 = new JLabel();
		edMI_MDate = new ReadableTextField();
		label20 = new JLabel();
		edMI_Filesize = new ReadableTextField();
		label21 = new JLabel();
		edMI_Duration = new ReadableTextField();
		label22 = new JLabel();
		edMI_Bitrate = new ReadableTextField();
		panel8 = new JPanel();
		label23 = new JLabel();
		edMI_VideoFormat = new ReadableTextField();
		label24 = new JLabel();
		edMI_VideoResolution = new ReadableTextField();
		label25 = new JLabel();
		edMI_VideoFramerate = new ReadableTextField();
		label26 = new JLabel();
		edMI_VideoBitdepth = new ReadableTextField();
		label27 = new JLabel();
		edMI_VideoFramecount = new ReadableTextField();
		label28 = new JLabel();
		edMI_VideoCodec = new ReadableTextField();
		panel9 = new JPanel();
		label29 = new JLabel();
		edMI_AudioFormat = new ReadableTextField();
		label30 = new JLabel();
		edMI_AudioChannels = new ReadableTextField();
		label31 = new JLabel();
		edMI_AudioCodec = new ReadableTextField();
		label32 = new JLabel();
		edMI_AudioSamplerate = new ReadableTextField();
		pnlTabPaths = new JPanel();
		edPart0 = new JReadableCCPathTextField();
		btnMediaInfo0 = new CCIcon16Button();
		btnOpenDir0 = new CCIcon16Button();
		edPart1 = new JReadableCCPathTextField();
		btnMediaInfo1 = new CCIcon16Button();
		btnOpenDir1 = new CCIcon16Button();
		edPart2 = new JReadableCCPathTextField();
		btnMediaInfo2 = new CCIcon16Button();
		btnOpenDir2 = new CCIcon16Button();
		edPart3 = new JReadableCCPathTextField();
		btnMediaInfo3 = new CCIcon16Button();
		btnOpenDir3 = new CCIcon16Button();
		edPart4 = new JReadableCCPathTextField();
		btnMediaInfo4 = new CCIcon16Button();
		btnOpenDir4 = new CCIcon16Button();
		edPart5 = new JReadableCCPathTextField();
		btnMediaInfo5 = new CCIcon16Button();
		btnOpenDir5 = new CCIcon16Button();
		pnlTabHistory = new JPanel();
		tabHistoryEntries = new PMHistoryTableEntries(this);
		tabHistoryChanges = new PMHistoryTableChanges(this);
		btnQueryHistory = new JButton();

		//======== this ========
		setTitle("<dynamic>"); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(675, 550));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onWindowClosed();
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $ugap, default:grow, $ugap")); //$NON-NLS-1$

		//======== pnlTop ========
		{
			pnlTop.setLayout(new FormLayout(
				"63dlu, $lcgap, 0dlu:grow, 2*($lcgap, 30dlu)", //$NON-NLS-1$
				"default")); //$NON-NLS-1$
			pnlTop.add(lblViewed, CC.xy(1, 1, CC.FILL, CC.FILL));

			//---- lblHeaderTitle ----
			lblHeaderTitle.setText("<dynamic>"); //$NON-NLS-1$
			lblHeaderTitle.setFont(lblHeaderTitle.getFont().deriveFont(lblHeaderTitle.getFont().getStyle() | Font.BOLD, 28f));
			lblHeaderTitle.setHorizontalAlignment(SwingConstants.CENTER);
			pnlTop.add(lblHeaderTitle, CC.xy(3, 1));

			//---- btnPlay ----
			btnPlay.setIconRef(CCIcon32Button.IconRefLink.ICN_MENUBAR_PLAY);
			btnPlay.addActionListener(e -> playMovie());
			pnlTop.add(btnPlay, CC.xy(5, 1, CC.DEFAULT, CC.FILL));

			//---- btnPlayNoHistory ----
			btnPlayNoHistory.setIconRef(CCIcon32Button.IconRefLink.ICN_MENUBAR_HIDDENPLAY);
			btnPlayNoHistory.addActionListener(e -> playMovieNoHistory());
			pnlTop.add(btnPlayNoHistory, CC.xy(7, 1, CC.DEFAULT, CC.FILL));
		}
		contentPane.add(pnlTop, CC.xywh(2, 2, 3, 1, CC.FILL, CC.FILL));

		//======== pnlLeft ========
		{
			pnlLeft.setLayout(new FormLayout(
				"default:grow", //$NON-NLS-1$
				"default, $lgap, default")); //$NON-NLS-1$
			pnlLeft.add(lblCover, CC.xy(1, 1));
		}
		contentPane.add(pnlLeft, CC.xy(2, 4, CC.FILL, CC.FILL));

		//======== tabbedPane1 ========
		{
			tabbedPane1.setTabPlacement(SwingConstants.BOTTOM);

			//======== pnlTabMain ========
			{
				pnlTabMain.setLayout(new FormLayout(
					"2*($lcgap, default), $lcgap, default:grow, $lcgap, 80dlu, $lcgap", //$NON-NLS-1$
					"14*($lgap, default), $lgap, default:grow")); //$NON-NLS-1$

				//---- label1 ----
				label1.setText(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
				pnlTabMain.add(label1, CC.xy(2, 2));

				//---- lblTitle ----
				lblTitle.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblTitle, CC.xy(6, 2));
				pnlTabMain.add(btnOnlineRef, CC.xywh(8, 2, 1, 3, CC.DEFAULT, CC.TOP));

				//---- label2 ----
				label2.setText(LocaleBundle.getString("AddMovieFrame.lblZyklus.text")); //$NON-NLS-1$
				pnlTabMain.add(label2, CC.xy(2, 4));

				//---- lblZyklus ----
				lblZyklus.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblZyklus, CC.xy(6, 4));

				//---- label3 ----
				label3.setText(LocaleBundle.getString("AddMovieFrame.lblQuality.text")); //$NON-NLS-1$
				pnlTabMain.add(label3, CC.xy(2, 6));
				pnlTabMain.add(lblQualityIcon, CC.xy(4, 6, CC.FILL, CC.FILL));

				//---- lblQuality ----
				lblQuality.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblQuality, CC.xy(6, 6));

				//---- label4 ----
				label4.setText(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
				pnlTabMain.add(label4, CC.xy(2, 8));
				pnlTabMain.add(lblOnlineScore, CC.xy(6, 8));

				//---- label5 ----
				label5.setText(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
				pnlTabMain.add(label5, CC.xy(2, 10));
				pnlTabMain.add(lblLanguage, CC.xy(6, 10, CC.DEFAULT, CC.FILL));

				//---- label7 ----
				label7.setText(LocaleBundle.getString("PreviewMovieFrame.lblSubs")); //$NON-NLS-1$
				pnlTabMain.add(label7, CC.xy(2, 12));
				pnlTabMain.add(lblSubtitles, CC.xy(6, 12, CC.DEFAULT, CC.FILL));

				//---- label6 ----
				label6.setText(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
				pnlTabMain.add(label6, CC.xy(2, 14));

				//---- lblLength ----
				lblLength.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblLength, CC.xy(6, 14));

				//---- label19 ----
				label19.setText(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
				pnlTabMain.add(label19, CC.xy(2, 16));

				//---- lblAdded ----
				lblAdded.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblAdded, CC.xy(6, 16));

				//---- label8 ----
				label8.setText(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
				pnlTabMain.add(label8, CC.xy(2, 18));
				pnlTabMain.add(lblFSKIcon, CC.xy(4, 18, CC.FILL, CC.FILL));

				//---- lblFSK ----
				lblFSK.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblFSK, CC.xy(6, 18));

				//---- label9 ----
				label9.setText(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
				pnlTabMain.add(label9, CC.xy(2, 20));
				pnlTabMain.add(lblFormatIcon, CC.xy(4, 20, CC.FILL, CC.FILL));

				//---- lblFormat ----
				lblFormat.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblFormat, CC.xy(6, 20));

				//---- label10 ----
				label10.setText(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
				pnlTabMain.add(label10, CC.xy(2, 22));

				//---- lblYear ----
				lblYear.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblYear, CC.xy(6, 22));

				//---- label11 ----
				label11.setText(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
				pnlTabMain.add(label11, CC.xy(2, 24));

				//---- lblSize ----
				lblSize.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblSize, CC.xy(6, 24));

				//---- label12 ----
				label12.setText(LocaleBundle.getString("PreviewMovieFrame.btnScore.text")); //$NON-NLS-1$
				pnlTabMain.add(label12, CC.xy(2, 26));
				pnlTabMain.add(lblUserScoreIcon, CC.xy(4, 26, CC.FILL, CC.FILL));

				//---- lblUserScore ----
				lblUserScore.setText("<dynamic>"); //$NON-NLS-1$
				pnlTabMain.add(lblUserScore, CC.xy(6, 26));

				//---- label13 ----
				label13.setText(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
				pnlTabMain.add(label13, CC.xy(2, 28));
				pnlTabMain.add(lblTags, CC.xy(6, 28, CC.DEFAULT, CC.FILL));

				//======== panel6 ========
				{
					panel6.setLayout(new FormLayout(
						"0dlu:grow, 2*($lcgap, 1dlu:grow)", //$NON-NLS-1$
						"default, $lgap, default:grow")); //$NON-NLS-1$

					//---- label14 ----
					label14.setText(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
					panel6.add(label14, CC.xy(1, 1));

					//---- label15 ----
					label15.setText(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
					panel6.add(label15, CC.xy(3, 1));

					//---- label16 ----
					label16.setText(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
					panel6.add(label16, CC.xy(5, 1));

					//======== scrollPane1 ========
					{
						scrollPane1.setViewportView(lsGroups);
					}
					panel6.add(scrollPane1, CC.xy(1, 3, CC.FILL, CC.FILL));

					//======== scrollPane2 ========
					{
						scrollPane2.setViewportView(lsGenres);
					}
					panel6.add(scrollPane2, CC.xy(3, 3, CC.FILL, CC.FILL));

					//======== scrollPane3 ========
					{
						scrollPane3.setViewportView(lsHistory);
					}
					panel6.add(scrollPane3, CC.xy(5, 3, CC.FILL, CC.FILL));
				}
				pnlTabMain.add(panel6, CC.xywh(2, 30, 7, 1, CC.FILL, CC.FILL));
			}
			tabbedPane1.addTab(LocaleBundle.getString("PreviewMovieFrame.TabGeneral"), pnlTabMain); //$NON-NLS-1$

			//======== pnlTabMediaInfo ========
			{
				pnlTabMediaInfo.setLayout(new FormLayout(
					"0dlu:grow, $lcgap, 0dlu:grow", //$NON-NLS-1$
					"default, $lgap, default")); //$NON-NLS-1$

				//======== panel7 ========
				{
					panel7.setBorder(new TitledBorder(LocaleBundle.getString("EditMediaInfoDialog.header1"))); //$NON-NLS-1$
					panel7.setLayout(new FormLayout(
						"$lcgap, pref, $lcgap, default:grow, $lcgap", //$NON-NLS-1$
						"5*($lgap, default), $lgap")); //$NON-NLS-1$

					//---- label17 ----
					label17.setText(LocaleBundle.getString("EditMediaInfoDialog.CDate")); //$NON-NLS-1$
					panel7.add(label17, CC.xy(2, 2));
					panel7.add(edMI_CDate, CC.xy(4, 2));

					//---- label18 ----
					label18.setText(LocaleBundle.getString("EditMediaInfoDialog.MDate")); //$NON-NLS-1$
					panel7.add(label18, CC.xy(2, 4));
					panel7.add(edMI_MDate, CC.xy(4, 4));

					//---- label20 ----
					label20.setText(LocaleBundle.getString("EditMediaInfoDialog.Filesize")); //$NON-NLS-1$
					panel7.add(label20, CC.xy(2, 6));
					panel7.add(edMI_Filesize, CC.xy(4, 6));

					//---- label21 ----
					label21.setText(LocaleBundle.getString("EditMediaInfoDialog.Duration")); //$NON-NLS-1$
					panel7.add(label21, CC.xy(2, 8));
					panel7.add(edMI_Duration, CC.xy(4, 8));

					//---- label22 ----
					label22.setText(LocaleBundle.getString("EditMediaInfoDialog.Bitrate")); //$NON-NLS-1$
					panel7.add(label22, CC.xy(2, 10));
					panel7.add(edMI_Bitrate, CC.xy(4, 10));
				}
				pnlTabMediaInfo.add(panel7, CC.xywh(1, 1, 3, 1));

				//======== panel8 ========
				{
					panel8.setBorder(new TitledBorder(LocaleBundle.getString("EditMediaInfoDialog.header2"))); //$NON-NLS-1$
					panel8.setLayout(new FormLayout(
						"$lcgap, default, $lcgap, default:grow, $lcgap", //$NON-NLS-1$
						"6*($lgap, default), $lgap")); //$NON-NLS-1$

					//---- label23 ----
					label23.setText(LocaleBundle.getString("EditMediaInfoDialog.Format")); //$NON-NLS-1$
					panel8.add(label23, CC.xy(2, 2));
					panel8.add(edMI_VideoFormat, CC.xy(4, 2));

					//---- label24 ----
					label24.setText(LocaleBundle.getString("EditMediaInfoDialog.Resolution")); //$NON-NLS-1$
					panel8.add(label24, CC.xy(2, 4));
					panel8.add(edMI_VideoResolution, CC.xy(4, 4));

					//---- label25 ----
					label25.setText(LocaleBundle.getString("EditMediaInfoDialog.Framerate")); //$NON-NLS-1$
					panel8.add(label25, CC.xy(2, 6));
					panel8.add(edMI_VideoFramerate, CC.xy(4, 6));

					//---- label26 ----
					label26.setText(LocaleBundle.getString("EditMediaInfoDialog.Bitdepth")); //$NON-NLS-1$
					panel8.add(label26, CC.xy(2, 8));
					panel8.add(edMI_VideoBitdepth, CC.xy(4, 8));

					//---- label27 ----
					label27.setText(LocaleBundle.getString("EditMediaInfoDialog.Framecount")); //$NON-NLS-1$
					panel8.add(label27, CC.xy(2, 10));
					panel8.add(edMI_VideoFramecount, CC.xy(4, 10));

					//---- label28 ----
					label28.setText(LocaleBundle.getString("EditMediaInfoDialog.Codec")); //$NON-NLS-1$
					panel8.add(label28, CC.xy(2, 12));
					panel8.add(edMI_VideoCodec, CC.xy(4, 12));
				}
				pnlTabMediaInfo.add(panel8, CC.xy(1, 3, CC.FILL, CC.FILL));

				//======== panel9 ========
				{
					panel9.setBorder(new TitledBorder(LocaleBundle.getString("EditMediaInfoDialog.header3"))); //$NON-NLS-1$
					panel9.setLayout(new FormLayout(
						"$lcgap, default, $lcgap, default:grow, $lcgap", //$NON-NLS-1$
						"4*($lgap, default), $lgap")); //$NON-NLS-1$

					//---- label29 ----
					label29.setText(LocaleBundle.getString("EditMediaInfoDialog.Format")); //$NON-NLS-1$
					panel9.add(label29, CC.xy(2, 2));
					panel9.add(edMI_AudioFormat, CC.xy(4, 2));

					//---- label30 ----
					label30.setText(LocaleBundle.getString("EditMediaInfoDialog.Channels")); //$NON-NLS-1$
					panel9.add(label30, CC.xy(2, 4));
					panel9.add(edMI_AudioChannels, CC.xy(4, 4));

					//---- label31 ----
					label31.setText(LocaleBundle.getString("EditMediaInfoDialog.Codec")); //$NON-NLS-1$
					panel9.add(label31, CC.xy(2, 6));
					panel9.add(edMI_AudioCodec, CC.xy(4, 6));

					//---- label32 ----
					label32.setText(LocaleBundle.getString("EditMediaInfoDialog.Samplerate")); //$NON-NLS-1$
					panel9.add(label32, CC.xy(2, 8));
					panel9.add(edMI_AudioSamplerate, CC.xy(4, 8));
				}
				pnlTabMediaInfo.add(panel9, CC.xy(3, 3, CC.FILL, CC.FILL));
			}
			tabbedPane1.addTab(LocaleBundle.getString("PreviewMovieFrame.TabMediaInfo"), pnlTabMediaInfo); //$NON-NLS-1$

			//======== pnlTabPaths ========
			{
				pnlTabPaths.setLayout(new FormLayout(
					"$lcgap, default:grow, 2*($lcgap, 16dlu), $lcgap", //$NON-NLS-1$
					"6*($lgap, default), $lgap")); //$NON-NLS-1$
				pnlTabPaths.add(edPart0, CC.xy(2, 2, CC.DEFAULT, CC.FILL));

				//---- btnMediaInfo0 ----
				btnMediaInfo0.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfo0.addActionListener(e -> onShowMediaInfo0());
				pnlTabPaths.add(btnMediaInfo0, CC.xy(4, 2));

				//---- btnOpenDir0 ----
				btnOpenDir0.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_FOLDER);
				btnOpenDir0.addActionListener(e -> onOpenDir0());
				pnlTabPaths.add(btnOpenDir0, CC.xy(6, 2));
				pnlTabPaths.add(edPart1, CC.xy(2, 4, CC.DEFAULT, CC.FILL));

				//---- btnMediaInfo1 ----
				btnMediaInfo1.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfo1.addActionListener(e -> onShowMediaInfo1());
				pnlTabPaths.add(btnMediaInfo1, CC.xy(4, 4));

				//---- btnOpenDir1 ----
				btnOpenDir1.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_FOLDER);
				btnOpenDir1.addActionListener(e -> onOpenDir1());
				pnlTabPaths.add(btnOpenDir1, CC.xy(6, 4));
				pnlTabPaths.add(edPart2, CC.xy(2, 6, CC.DEFAULT, CC.FILL));

				//---- btnMediaInfo2 ----
				btnMediaInfo2.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfo2.addActionListener(e -> onShowMediaInfo2());
				pnlTabPaths.add(btnMediaInfo2, CC.xy(4, 6));

				//---- btnOpenDir2 ----
				btnOpenDir2.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_FOLDER);
				btnOpenDir2.addActionListener(e -> onOpenDir2());
				pnlTabPaths.add(btnOpenDir2, CC.xy(6, 6));
				pnlTabPaths.add(edPart3, CC.xy(2, 8, CC.DEFAULT, CC.FILL));

				//---- btnMediaInfo3 ----
				btnMediaInfo3.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfo3.addActionListener(e -> onShowMediaInfo3());
				pnlTabPaths.add(btnMediaInfo3, CC.xy(4, 8));

				//---- btnOpenDir3 ----
				btnOpenDir3.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_FOLDER);
				btnOpenDir3.addActionListener(e -> onOpenDir3());
				pnlTabPaths.add(btnOpenDir3, CC.xy(6, 8));
				pnlTabPaths.add(edPart4, CC.xy(2, 10, CC.DEFAULT, CC.FILL));

				//---- btnMediaInfo4 ----
				btnMediaInfo4.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfo4.addActionListener(e -> onShowMediaInfo4());
				pnlTabPaths.add(btnMediaInfo4, CC.xy(4, 10));

				//---- btnOpenDir4 ----
				btnOpenDir4.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_FOLDER);
				btnOpenDir4.addActionListener(e -> onOpenDir4());
				pnlTabPaths.add(btnOpenDir4, CC.xy(6, 10));
				pnlTabPaths.add(edPart5, CC.xy(2, 12, CC.DEFAULT, CC.FILL));

				//---- btnMediaInfo5 ----
				btnMediaInfo5.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfo5.addActionListener(e -> onShowMediaInfo5());
				pnlTabPaths.add(btnMediaInfo5, CC.xy(4, 12));

				//---- btnOpenDir5 ----
				btnOpenDir5.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_FOLDER);
				btnOpenDir5.addActionListener(e -> onOpenDir5());
				pnlTabPaths.add(btnOpenDir5, CC.xy(6, 12));
			}
			tabbedPane1.addTab(LocaleBundle.getString("PreviewMovieFrame.TabPaths"), pnlTabPaths); //$NON-NLS-1$

			//======== pnlTabHistory ========
			{
				pnlTabHistory.setLayout(new FormLayout(
					"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
					"2*($lgap, 0dlu:grow), $lgap, default, $lgap")); //$NON-NLS-1$

				//======== tabHistoryEntries ========
				{
					tabHistoryEntries.addSelectionListener(e -> onHistorySelected(e));
				}
				pnlTabHistory.add(tabHistoryEntries, CC.xy(2, 2, CC.FILL, CC.FILL));
				pnlTabHistory.add(tabHistoryChanges, CC.xy(2, 4, CC.DEFAULT, CC.FILL));

				//---- btnQueryHistory ----
				btnQueryHistory.setText(LocaleBundle.getString("PreviewMovieFrame.btnQuery")); //$NON-NLS-1$
				btnQueryHistory.addActionListener(e -> queryHistory());
				pnlTabHistory.add(btnQueryHistory, CC.xy(2, 6, CC.RIGHT, CC.DEFAULT));
			}
			tabbedPane1.addTab(LocaleBundle.getString("PreviewMovieFrame.TabHistory"), pnlTabHistory); //$NON-NLS-1$
		}
		contentPane.add(tabbedPane1, CC.xy(4, 4, CC.DEFAULT, CC.FILL));
		setSize(750, 615);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlTop;
	private JLabel lblViewed;
	private JLabel lblHeaderTitle;
	private CCIcon32Button btnPlay;
	private CCIcon32Button btnPlayNoHistory;
	private JPanel pnlLeft;
	private CoverLabelFullsize lblCover;
	private JTabbedPane tabbedPane1;
	private JPanel pnlTabMain;
	private JLabel label1;
	private JLabel lblTitle;
	private OnlineRefButton btnOnlineRef;
	private JLabel label2;
	private JLabel lblZyklus;
	private JLabel label3;
	private JLabel lblQualityIcon;
	private JLabel lblQuality;
	private JLabel label4;
	private OnlineScoreDisplay lblOnlineScore;
	private JLabel label5;
	private LanguageSetDisplay lblLanguage;
	private JLabel label7;
	private LanguageListDisplay lblSubtitles;
	private JLabel label6;
	private JLabel lblLength;
	private JLabel label19;
	private JLabel lblAdded;
	private JLabel label8;
	private JLabel lblFSKIcon;
	private JLabel lblFSK;
	private JLabel label9;
	private JLabel lblFormatIcon;
	private JLabel lblFormat;
	private JLabel label10;
	private JLabel lblYear;
	private JLabel label11;
	private JLabel lblSize;
	private JLabel label12;
	private JLabel lblUserScoreIcon;
	private JLabel lblUserScore;
	private JLabel label13;
	private TagDisplay lblTags;
	private JPanel panel6;
	private JLabel label14;
	private JLabel label15;
	private JLabel label16;
	private JScrollPane scrollPane1;
	private JList<String> lsGroups;
	private JScrollPane scrollPane2;
	private JList<String> lsGenres;
	private JScrollPane scrollPane3;
	private JList<String> lsHistory;
	private JPanel pnlTabMediaInfo;
	private JPanel panel7;
	private JLabel label17;
	private ReadableTextField edMI_CDate;
	private JLabel label18;
	private ReadableTextField edMI_MDate;
	private JLabel label20;
	private ReadableTextField edMI_Filesize;
	private JLabel label21;
	private ReadableTextField edMI_Duration;
	private JLabel label22;
	private ReadableTextField edMI_Bitrate;
	private JPanel panel8;
	private JLabel label23;
	private ReadableTextField edMI_VideoFormat;
	private JLabel label24;
	private ReadableTextField edMI_VideoResolution;
	private JLabel label25;
	private ReadableTextField edMI_VideoFramerate;
	private JLabel label26;
	private ReadableTextField edMI_VideoBitdepth;
	private JLabel label27;
	private ReadableTextField edMI_VideoFramecount;
	private JLabel label28;
	private ReadableTextField edMI_VideoCodec;
	private JPanel panel9;
	private JLabel label29;
	private ReadableTextField edMI_AudioFormat;
	private JLabel label30;
	private ReadableTextField edMI_AudioChannels;
	private JLabel label31;
	private ReadableTextField edMI_AudioCodec;
	private JLabel label32;
	private ReadableTextField edMI_AudioSamplerate;
	private JPanel pnlTabPaths;
	private JReadableCCPathTextField edPart0;
	private CCIcon16Button btnMediaInfo0;
	private CCIcon16Button btnOpenDir0;
	private JReadableCCPathTextField edPart1;
	private CCIcon16Button btnMediaInfo1;
	private CCIcon16Button btnOpenDir1;
	private JReadableCCPathTextField edPart2;
	private CCIcon16Button btnMediaInfo2;
	private CCIcon16Button btnOpenDir2;
	private JReadableCCPathTextField edPart3;
	private CCIcon16Button btnMediaInfo3;
	private CCIcon16Button btnOpenDir3;
	private JReadableCCPathTextField edPart4;
	private CCIcon16Button btnMediaInfo4;
	private CCIcon16Button btnOpenDir4;
	private JReadableCCPathTextField edPart5;
	private CCIcon16Button btnMediaInfo5;
	private CCIcon16Button btnOpenDir5;
	private JPanel pnlTabHistory;
	private PMHistoryTableEntries tabHistoryEntries;
	private PMHistoryTableChanges tabHistoryChanges;
	private JButton btnQueryHistory;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
