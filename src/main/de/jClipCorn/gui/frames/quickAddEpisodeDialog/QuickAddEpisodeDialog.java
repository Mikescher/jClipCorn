package de.jClipCorn.gui.frames.quickAddEpisodeDialog;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.EpisodeDataPack;
import de.jClipCorn.database.databaseElement.datapacks.IEpisodeData;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.guiComponents.*;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.JCCPathTextField;
import de.jClipCorn.gui.guiComponents.JReadableFSPathTextField;
import de.jClipCorn.gui.guiComponents.iconComponents.CCIcon16Button;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.language.LanguageListChooser;
import de.jClipCorn.gui.guiComponents.language.LanguageSetChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.EpisodeFilenameParserResult;
import de.jClipCorn.util.parser.FilenameParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuickAddEpisodeDialog extends JCCDialog
{
	private final CCSeason season;
	private final FSPath source;

	private final UpdateCallbackListener ucListener;

	private boolean suppressEdTargetEvents = false;

	private volatile int progressValueCache;
	public QuickAddEpisodeDialog(Component owner, UpdateCallbackListener listener, CCSeason s, FSPath f)
	{
		super(s.getMovieList());
		ucListener = listener;
		season = s;
		source = f;

		initComponents();
		postInit();
		initData();

		setLocationRelativeTo(owner);
	}

	public static void show(PreviewSeriesFrame owner, CCSeason s, FSPath f) {
		QuickAddEpisodeDialog qaed = new QuickAddEpisodeDialog(owner, owner, s, f);
		qaed.setVisible(true);
	}

	public static void show(Component owner, UpdateCallbackListener lst, CCSeason s, FSPath f) {
		QuickAddEpisodeDialog qaed = new QuickAddEpisodeDialog(owner, lst, s, f);
		qaed.setVisible(true);
	}

	private void postInit()
	{
		edTarget.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				if (suppressEdTargetEvents) return;
				if (cbRename.isSelected()) cbRename.setSelected(false);
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				if (suppressEdTargetEvents) return;
				if (cbRename.isSelected()) cbRename.setSelected(false);
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (suppressEdTargetEvents) return;
				if (cbRename.isSelected()) cbRename.setSelected(false);
			}
		});
		edTitle.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				suppressEdTargetEvents = true;
				if (cbRename.isSelected()) edTarget.setPath(createTarget());
				suppressEdTargetEvents = false;
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				suppressEdTargetEvents = true;
				if (cbRename.isSelected()) edTarget.setPath(createTarget());
				suppressEdTargetEvents = false;
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				suppressEdTargetEvents = true;
				if (cbRename.isSelected()) edTarget.setPath(createTarget());
				suppressEdTargetEvents = false;
			}
		});
	}

	private void initData() {
		edSource.setPath(source);

		spnEpisode.setValue(season.getNextEpisodeNumber());
		spnLength.setValue(season.getSeries().getAutoEpisodeLength(season));

		EpisodeFilenameParserResult result = FilenameParser.parseEpisode(source);
		if (result != null) {
			spnEpisode.setValue(result.EpisodeNumber);
			edTitle.setText(result.Title);
		} else {
			edTitle.setText(source.getFilenameWithoutExt());
		}

		edTarget.setPath(createTarget());

		cbCopy.setSelected(!edTarget.getPath().toFSPath(this).equalsOnFilesystem(edSource.getPath()));
		cbRename.setSelected(cbCopy.isSelected());

		CCEpisode last = season.getSeries().getLastAddedEpisode();
		CCDBLanguageSet lang = CCDBLanguageSet.single(ccprops().PROP_DATABASE_DEFAULTPARSERLANG.getValue());
		if (last != null) lang = last.getLanguage();
		ctrlLang.setValue(lang);

		ctrlSubs.setValue(CCDBLanguageList.EMPTY);

		pbarMediaInfo.setVisible(true);
		new Thread(() ->  {

			try {
				MediaQueryResult dat = new MediaQueryRunner(movielist).query(edSource.getPath(), true);

				SwingUtils.invokeLater(() ->
				{
					if (dat.AudioLanguages != null) {
						CCDBLanguageSet dbll = dat.AudioLanguages;

						if (!dbll.isEmpty()) ctrlLang.setValue(dbll);
					}

					if (dat.SubtitleLanguages != null) {
						ctrlSubs.setValue(dat.SubtitleLanguages);
					}

					edMediaInfo.setValue(dat);
				});

			} catch (IOException | MediaQueryException e) {
				// ignore
				CCLog.addWarning(e);
			} finally {
				SwingUtils.invokeLater(() -> pbarMediaInfo.setVisible(false));
			}

		}, "QA_MINFO").start(); //$NON-NLS-1$
	}

	private CCPath createTarget() {
		return createTarget(season, (int)spnEpisode.getValue(), edTitle.getText());
	}

	private CCPath createTarget(CCSeason season, int episode, String title) {
		var root = season.getSeries().guessSeriesRootPath();
		if (FSPath.isNullOrEmpty(root)) return CCPath.Empty;

		var dst = season.getPathForCreatedFolderstructure(root, title, episode, CCFileFormat.getMovieFormatFromPath(edSource.getPath()), null);
		if (dst == null) return CCPath.Empty;

		return CCPath.createFromFSPath(dst, this);
	}

	private void tryAdd(boolean check) {

		var src = edSource.getPath();
		var dst = cbCopy.isSelected() ? edTarget.getPath().toFSPath(this) : edSource.getPath();

		int episodenumber = (int)spnEpisode.getValue();
		int length = (int)spnLength.getValue();
		String title = edTitle.getText().trim();
		CCDBLanguageSet lang = ctrlLang.getValue();
		CCDBLanguageList subs = ctrlSubs.getValue();

		var imd = dst.getParent().append(src.getFilenameWithExt());

		CCDate adddate = CCDate.getCurrentDate();
		CCDateTimeList history = CCDateTimeList.createEmpty();
		CCTagList tags = CCTagList.EMPTY;
		var filesize = src.filesize();
		CCFileFormat format = CCFileFormat.getMovieFormatFromPath(src);
		PartialMediaInfo minfo = edMediaInfo.getValue();

		var epack = new EpisodeDataPack(episodenumber, title, length, format, filesize, CCPath.createFromFSPath(dst, this), adddate, history, tags, lang, subs, minfo);

		java.util.List<UserDataProblem> problems = new ArrayList<>();
		boolean probvalue = !check || checkUserDataEpisode(problems, epack, src, dst);

		// some problems are too fatal
		if (probvalue && Str.isNullOrWhitespace(title)) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}

		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(movielist, problems, () -> tryAdd(false), this);
			amied.setVisible(true);
			return;
		}

		rootpnl.setEnabled(false);
		new Thread(() ->
		{
			try
			{
				if (cbCopy.isSelected())
				{
					FileUtils.forceMkdir(dst.getParent().toFile());

					progressValueCache = 0;
					SwingUtils.invokeAndWait(() ->
					{
						progressBar.setVisible(true);
						progressBar.setMaximum(100);
					});

					//FileUtils.copyFile(srcFile, dstFile);
					SimpleFileUtils.copyWithProgress(src, dst, (val, max) ->
					{
						int newvalue = (int)(((val * 100) / max));
						if (progressValueCache != newvalue)
						{
							progressValueCache = newvalue;
							SwingUtils.invokeLater(() -> { progressBar.setValue(newvalue); });
						}
					});
				}

				SwingUtils.invokeLater(() ->
				{
					CCEpisode newEp = season.createNewEmptyEpisode();
					newEp.beginUpdating();
					newEp.Title.set(title);
					newEp.EpisodeNumber.set(episodenumber);
					newEp.Format.set(format);
					newEp.MediaInfo.set(minfo);
					newEp.Length.set(length);
					newEp.FileSize.set(filesize);
					newEp.AddDate.set(adddate);
					newEp.ViewedHistory.set(history);
					newEp.Part.set(CCPath.createFromFSPath(imd, Opt.False, this));
					newEp.Tags.set(tags);
					newEp.Language.set(lang);
					newEp.Subtitles.set(subs);
					newEp.endUpdating();

					newEp.beginUpdating();
					newEp.Part.set(CCPath.createFromFSPath(dst, this));
					newEp.endUpdating();

					if (ucListener != null) ucListener.onUpdate(newEp);
					dispose();
				});

			} catch (Exception e) {
				SwingUtils.invokeLater(() ->
				{
					CCLog.addError(e);
					DialogHelper.showDispatchError(this, LocaleBundle.getString("QuickAddEpisodeDialog.dialogs.error_caption"), LocaleBundle.getString("QuickAddEpisodeDialog.dialogs.error")); //$NON-NLS-1$ //$NON-NLS-2$
				});
			} finally {
				SwingUtils.invokeLater(() -> rootpnl.setEnabled(true) );
			}
		}).start();
	}

	private boolean checkUserDataEpisode(List<UserDataProblem> ret, IEpisodeData newdata, FSPath src, FSPath dst) {

		UserDataProblem.testEpisodeData(ret, season.getMovieList(), season, null, newdata);

		if (!src.fileExists()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INPUT_FILE_NOT_FOUND));
		}

		if (dst.fileExists()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DESTINTAION_FILE_ALREADY_EXISTS));
		}

		return ret.isEmpty();
	}

	private void parseCodecMetadata_Lang() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edSource.getPath(), false);

			if (dat.AudioLanguages == null) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			CCDBLanguageSet dbll = dat.AudioLanguages;

			if (dbll.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			} else {
				ctrlLang.setValue(dbll);
			}

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void parseCodecMetadata_Subs() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edSource.getPath(), false);

			if (dat.SubtitleLanguages == null) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			ctrlSubs.setValue(dat.SubtitleLanguages);

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void parseCodecMetadata_Len() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edSource.getPath(), true);

			int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			spnLength.setValue(dur);

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void showCodecMetadata() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			String dat = new MediaQueryRunner(movielist).queryRaw(edSource.getPath());

			GenericTextDialog.showText(this, getTitle(), dat, false);
		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void onOkay() {
		tryAdd(true);
	}

	private void onCancel() {
		dispose();
	}

	private void copyFileChanged() {
		edTarget.setEditable(cbCopy.isSelected());
		cbRename.setEnabled(cbCopy.isSelected());
		if (!cbCopy.isSelected()) cbRename.setSelected(false);
	}

	private void renameFileChanged() {
		//
	}

	private void episodeChanged() {
		if (cbRename.isSelected()) edTarget.setPath(createTarget());
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		rootpnl = new JPanel();
		label1 = new JLabel();
		edSource = new JReadableFSPathTextField();
		label2 = new JLabel();
		edTarget = new JCCPathTextField();
		cbCopy = new JCheckBox();
		cbRename = new JCheckBox();
		label3 = new JLabel();
		spnEpisode = new JSpinner();
		label4 = new JLabel();
		edMediaInfo = new JMediaInfoControl(movielist, () -> edSource.getPath());
		pbarMediaInfo = new JProgressBar();
		label5 = new JLabel();
		edTitle = new JTextField();
		label6 = new JLabel();
		ctrlLang = new LanguageSetChooser();
		cCIcon16Button3 = new CCIcon16Button();
		button1 = new JButton();
		label8 = new JLabel();
		ctrlSubs = new LanguageListChooser();
		cCIcon16Button2 = new CCIcon16Button();
		label7 = new JLabel();
		spnLength = new JSpinner();
		label9 = new JLabel();
		cCIcon16Button1 = new CCIcon16Button();
		panel1 = new JPanel();
		progressBar = new JProgressBar();
		button2 = new JButton();
		button3 = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("QuickAddEpisodeDialog.title")); //$NON-NLS-1$
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"default:grow", //$NON-NLS-1$
			"3dlu:grow")); //$NON-NLS-1$

		//======== rootpnl ========
		{
			rootpnl.setLayout(new FormLayout(
				"$ugap, default, $rgap, default:grow, $lcgap, default, $lcgap, 16dlu, $lcgap, default, $ugap", //$NON-NLS-1$
				"$ugap, 10*(default, $lgap), default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

			//---- label1 ----
			label1.setText(LocaleBundle.getString("QuickAddEpisodeDialog.lblSource")); //$NON-NLS-1$
			rootpnl.add(label1, CC.xy(2, 2, CC.RIGHT, CC.DEFAULT));
			rootpnl.add(edSource, CC.xywh(4, 2, 3, 1));

			//---- label2 ----
			label2.setText(LocaleBundle.getString("QuickAddEpisodeDialog.lblTarget")); //$NON-NLS-1$
			rootpnl.add(label2, CC.xy(2, 4, CC.RIGHT, CC.DEFAULT));
			rootpnl.add(edTarget, CC.xywh(4, 4, 3, 1));

			//---- cbCopy ----
			cbCopy.setText(LocaleBundle.getString("QuickAddEpisodeDialog.cbCopy")); //$NON-NLS-1$
			cbCopy.addActionListener(e -> copyFileChanged());
			rootpnl.add(cbCopy, CC.xywh(4, 6, 3, 1));

			//---- cbRename ----
			cbRename.setText(LocaleBundle.getString("QuickAddEpisodeDialog.cbRename")); //$NON-NLS-1$
			cbRename.addActionListener(e -> renameFileChanged());
			rootpnl.add(cbRename, CC.xywh(4, 8, 3, 1));

			//---- label3 ----
			label3.setText(LocaleBundle.getString("QuickAddEpisodeDialog.lblEpisode")); //$NON-NLS-1$
			rootpnl.add(label3, CC.xy(2, 10, CC.RIGHT, CC.DEFAULT));

			//---- spnEpisode ----
			spnEpisode.setModel(new SpinnerNumberModel(0, 0, null, 1));
			spnEpisode.addChangeListener(e -> episodeChanged());
			rootpnl.add(spnEpisode, CC.xy(4, 10));

			//---- label4 ----
			label4.setText(LocaleBundle.getString("AddMovieFrame.lblMediaInfo")); //$NON-NLS-1$
			rootpnl.add(label4, CC.xy(2, 12, CC.RIGHT, CC.DEFAULT));
			rootpnl.add(edMediaInfo, CC.xy(4, 12));
			rootpnl.add(pbarMediaInfo, CC.xywh(6, 12, 5, 1, CC.FILL, CC.FILL));

			//---- label5 ----
			label5.setText(LocaleBundle.getString("QuickAddEpisodeDialog.lblTitle")); //$NON-NLS-1$
			rootpnl.add(label5, CC.xy(2, 14, CC.RIGHT, CC.DEFAULT));
			rootpnl.add(edTitle, CC.xy(4, 14));

			//---- label6 ----
			label6.setText(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
			rootpnl.add(label6, CC.xy(2, 16, CC.RIGHT, CC.DEFAULT));
			rootpnl.add(ctrlLang, CC.xy(4, 16));

			//---- cCIcon16Button3 ----
			cCIcon16Button3.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			cCIcon16Button3.addActionListener(e -> parseCodecMetadata_Lang());
			rootpnl.add(cCIcon16Button3, CC.xy(8, 16));

			//---- button1 ----
			button1.setText("..."); //$NON-NLS-1$
			button1.addActionListener(e -> showCodecMetadata());
			rootpnl.add(button1, CC.xy(10, 16));

			//---- label8 ----
			label8.setText(LocaleBundle.getString("AddMovieFrame.lblSubtitles")); //$NON-NLS-1$
			rootpnl.add(label8, CC.xy(2, 18, CC.RIGHT, CC.DEFAULT));
			rootpnl.add(ctrlSubs, CC.xy(4, 18));

			//---- cCIcon16Button2 ----
			cCIcon16Button2.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			cCIcon16Button2.addActionListener(e -> parseCodecMetadata_Subs());
			rootpnl.add(cCIcon16Button2, CC.xy(8, 18));

			//---- label7 ----
			label7.setText(LocaleBundle.getString("QuickAddEpisodeDialog.lblLength")); //$NON-NLS-1$
			rootpnl.add(label7, CC.xy(2, 20, CC.RIGHT, CC.DEFAULT));

			//---- spnLength ----
			spnLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
			rootpnl.add(spnLength, CC.xy(4, 20));

			//---- label9 ----
			label9.setText("min."); //$NON-NLS-1$
			rootpnl.add(label9, CC.xy(6, 20));

			//---- cCIcon16Button1 ----
			cCIcon16Button1.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			cCIcon16Button1.addActionListener(e -> parseCodecMetadata_Len());
			rootpnl.add(cCIcon16Button1, CC.xy(8, 20));

			//======== panel1 ========
			{
				panel1.setLayout(new FormLayout(
					"default:grow, 2*($lcgap, default)", //$NON-NLS-1$
					"default")); //$NON-NLS-1$
				panel1.add(progressBar, CC.xy(1, 1, CC.FILL, CC.FILL));

				//---- button2 ----
				button2.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
				button2.addActionListener(e -> onOkay());
				panel1.add(button2, CC.xy(3, 1));

				//---- button3 ----
				button3.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
				button3.addActionListener(e -> onCancel());
				panel1.add(button3, CC.xy(5, 1));
			}
			rootpnl.add(panel1, CC.xywh(2, 24, 9, 1));
		}
		contentPane.add(rootpnl, CC.xy(1, 1, CC.FILL, CC.FILL));
		setSize(550, 425);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel rootpnl;
	private JLabel label1;
	private JReadableFSPathTextField edSource;
	private JLabel label2;
	private JCCPathTextField edTarget;
	private JCheckBox cbCopy;
	private JCheckBox cbRename;
	private JLabel label3;
	private JSpinner spnEpisode;
	private JLabel label4;
	private JMediaInfoControl edMediaInfo;
	private JProgressBar pbarMediaInfo;
	private JLabel label5;
	private JTextField edTitle;
	private JLabel label6;
	private LanguageSetChooser ctrlLang;
	private CCIcon16Button cCIcon16Button3;
	private JButton button1;
	private JLabel label8;
	private LanguageListChooser ctrlSubs;
	private CCIcon16Button cCIcon16Button2;
	private JLabel label7;
	private JSpinner spnLength;
	private JLabel label9;
	private CCIcon16Button cCIcon16Button1;
	private JPanel panel1;
	private JProgressBar progressBar;
	private JButton button2;
	private JButton button3;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
