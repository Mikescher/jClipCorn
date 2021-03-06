package de.jClipCorn.gui.frames.quickAddEpisodeDialog;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.EpisodeDataPack;
import de.jClipCorn.database.databaseElement.datapacks.IEpisodeData;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.guiComponents.JCCPathTextField;
import de.jClipCorn.gui.guiComponents.JReadableFSPathTextField;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
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
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuickAddEpisodeDialog extends JDialog {
	private static final long serialVersionUID = -184393538006518026L;

	private final JPanel contentPanel = new JPanel();
	private JReadableFSPathTextField edSource;
	private JCCPathTextField edTarget;
	private JTextField edTitle;
	private JCheckBox cbCopy;
	private JCheckBox cbRename;
	private JSpinner spnEpisode;
	private JSpinner spnLength;

	private final CCSeason season;
	private final FSPath source;

	private final UpdateCallbackListener ucListener;

	private boolean suppressEdTargetEvents = false;
	private JProgressBar progressBar;

	/**
	 * @wbp.parser.constructor
	 */
	private QuickAddEpisodeDialog(Component owner, UpdateCallbackListener listener, CCSeason s, FSPath f) {
		super();
		ucListener = listener;
		season = s;
		source = f;

		initGUI();

		setLocationRelativeTo(owner);
		
		initData();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("QuickAddEpisodeDialog.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setBounds(100, 100, 550, 365);
		setMinimumSize(new Dimension(300, 300));

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(30dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
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
				RowSpec.decode("22px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("22px"), //$NON-NLS-1$
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$

		JLabel lblSource = new JLabel(LocaleBundle.getString("QuickAddEpisodeDialog.lblSource")); //$NON-NLS-1$
		lblSource.setHorizontalAlignment(SwingConstants.TRAILING);

		contentPanel.add(lblSource, "1, 1, right, default"); //$NON-NLS-1$
		edSource = new JReadableFSPathTextField();
		contentPanel.add(edSource, "3, 1, 3, 1, fill, default"); //$NON-NLS-1$
		edSource.setColumns(10);

		JLabel lblTarget = new JLabel(LocaleBundle.getString("QuickAddEpisodeDialog.lblTarget")); //$NON-NLS-1$
		lblTarget.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblTarget, "1, 3, right, default"); //$NON-NLS-1$
		edTarget = new JCCPathTextField();
		contentPanel.add(edTarget, "3, 3, 3, 1, fill, default"); //$NON-NLS-1$
		edTarget.setColumns(10);
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

		cbCopy = new JCheckBox(LocaleBundle.getString("QuickAddEpisodeDialog.cbCopy")); //$NON-NLS-1$
		contentPanel.add(cbCopy, "3, 5, 3, 1"); //$NON-NLS-1$
		cbCopy.addActionListener((e) -> {edTarget.setEditable(cbCopy.isSelected()); cbRename.setEnabled(cbCopy.isSelected()); if (!cbCopy.isSelected()) cbRename.setSelected(false); });

		cbRename = new JCheckBox(LocaleBundle.getString("QuickAddEpisodeDialog.cbRename")); //$NON-NLS-1$
		contentPanel.add(cbRename, "3, 7, 3, 1"); //$NON-NLS-1$
		JSeparator separator = new JSeparator();
		contentPanel.add(separator, "1, 9, 5, 1"); //$NON-NLS-1$

		JLabel lblEpisode = new JLabel(LocaleBundle.getString("QuickAddEpisodeDialog.lblEpisode")); //$NON-NLS-1$
		lblEpisode.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblEpisode, "1, 11"); //$NON-NLS-1$

		spnEpisode = new JSpinner();
		spnEpisode.setModel(new SpinnerNumberModel(0, 0, null, 1));
		spnEpisode.addChangeListener((e) -> { if (cbRename.isSelected()) edTarget.setPath(createTarget()); });
		contentPanel.add(spnEpisode, "3, 11"); //$NON-NLS-1$
		
		JLabel lblQuality = new JLabel("MediaInfo"); //$NON-NLS-1$
		contentPanel.add(lblQuality, "1, 13, right, default"); //$NON-NLS-1$
		
		edMediaInfo = new JMediaInfoControl(() -> edSource.getPath());
		contentPanel.add(edMediaInfo, "3, 13, fill, default"); //$NON-NLS-1$
		
		pbar = new JProgressBar();
		pbar.setIndeterminate(true);
		contentPanel.add(pbar, "5, 13, 5, 1, fill, fill"); //$NON-NLS-1$

		JLabel lblTitle = new JLabel(LocaleBundle.getString("QuickAddEpisodeDialog.lblTitle")); //$NON-NLS-1$
		lblTitle.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblTitle, "1, 15, right, default"); //$NON-NLS-1$
		edTitle = new JTextField();
		contentPanel.add(edTitle, "3, 15, fill, default"); //$NON-NLS-1$
		edTitle.setColumns(10);
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
		
		JLabel lblNewLabel = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		contentPanel.add(lblNewLabel, "1, 17"); //$NON-NLS-1$
		
		ctrlLang = new LanguageChooser();
		contentPanel.add(ctrlLang, "3, 17, fill, fill"); //$NON-NLS-1$
		
		JButton btnMediaInfo1 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		btnMediaInfo1.setPreferredSize(new Dimension(22, 22));
		btnMediaInfo1.setToolTipText("MediaInfo"); //$NON-NLS-1$
		btnMediaInfo1.addActionListener(e -> parseCodecMetadata_Lang());
		contentPanel.add(btnMediaInfo1, "7, 17"); //$NON-NLS-1$
		
		JButton btnMediaInfoRaw = new JButton("..."); //$NON-NLS-1$
		btnMediaInfoRaw.setPreferredSize(new Dimension(43, 22));
		btnMediaInfoRaw.setToolTipText("MediaInfo"); //$NON-NLS-1$
		btnMediaInfoRaw.addActionListener(e -> showCodecMetadata());
		contentPanel.add(btnMediaInfoRaw, "9, 17"); //$NON-NLS-1$
		
		JLabel lblLength = new JLabel(LocaleBundle.getString("QuickAddEpisodeDialog.lblLength")); //$NON-NLS-1$
		lblLength.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblLength, "1, 19"); //$NON-NLS-1$
		
		spnLength = new JSpinner();
		spnLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
		contentPanel.add(spnLength, "3, 19"); //$NON-NLS-1$

		JLabel lblMin = new JLabel("min."); //$NON-NLS-1$
		contentPanel.add(lblMin, "5, 19"); //$NON-NLS-1$
		
		JButton btnMediaInfo2 = new JButton(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
		btnMediaInfo2.setPreferredSize(new Dimension(22, 22));
		btnMediaInfo2.setToolTipText("MediaInfo"); //$NON-NLS-1$
		btnMediaInfo2.addActionListener(e -> parseCodecMetadata_Len());
		contentPanel.add(btnMediaInfo2, "7, 19"); //$NON-NLS-1$

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("51px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("96px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("26px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
				
				progressBar = new JProgressBar();
				buttonPane.add(progressBar, "2, 2, fill, fill"); //$NON-NLS-1$
				progressBar.setVisible(false);
		
				JButton okButton = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
				buttonPane.add(okButton, "4, 2, left, top"); //$NON-NLS-1$
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener((e) -> tryAdd(true));
		
				JButton cancelButton = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
				buttonPane.add(cancelButton, "6, 2, left, top"); //$NON-NLS-1$
		cancelButton.addActionListener((e) -> dispose());
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

		cbCopy.setSelected(!edTarget.getPath().toFSPath().equalsOnFilesystem(edSource.getPath()));
		cbRename.setSelected(cbCopy.isSelected());
		
		CCEpisode last = season.getSeries().getLastAddedEpisode();
		CCDBLanguageList lang = CCDBLanguageList.single(CCProperties.getInstance().PROP_DATABASE_DEFAULTPARSERLANG.getValue());
		if (last != null) lang = last.getLanguage();
		ctrlLang.setValue(lang);

		pbar.setVisible(true);
		new Thread(() ->  {

			try {
				MediaQueryResult dat = MediaQueryRunner.query(edSource.getPath(), true);

				SwingUtils.invokeLater(() ->
				{
					if (dat.AudioLanguages != null) {
						CCDBLanguageList dbll = dat.AudioLanguages;

						if (!dbll.isEmpty()) ctrlLang.setValue(dbll);
					}

					edMediaInfo.setValue(dat);
				});
				
			} catch (IOException | MediaQueryException e) {
				// ignore
				CCLog.addWarning(e);
			} finally {
				SwingUtils.invokeLater(() -> pbar.setVisible(false));
			}
			
		}, "QA_MINFO").start(); //$NON-NLS-1$
	}

	public static void show(PreviewSeriesFrame owner, CCSeason s, FSPath f) {
		QuickAddEpisodeDialog qaed = new QuickAddEpisodeDialog(owner, owner, s, f);
		qaed.setVisible(true);
	}

	public static void show(Component owner, UpdateCallbackListener lst, CCSeason s, FSPath f) {
		QuickAddEpisodeDialog qaed = new QuickAddEpisodeDialog(owner, lst, s, f);
		qaed.setVisible(true);
	}

	private CCPath createTarget() {
		return createTarget(season, (int)spnEpisode.getValue(), edTitle.getText());
	}

	private CCPath createTarget(CCSeason season, int episode, String title) {
		var root = season.getSeries().guessSeriesRootPath();
		if (FSPath.isNullOrEmpty(root)) return CCPath.Empty;

		var dst = season.getPathForCreatedFolderstructure(root, title, episode, CCFileFormat.getMovieFormatFromPath(edSource.getPath()), null);
		if (dst == null) return CCPath.Empty;

		return CCPath.createFromFSPath(dst);
	}

	private volatile int progressValueCache;
	private LanguageChooser ctrlLang;
	private JMediaInfoControl edMediaInfo;
	private JProgressBar pbar;

	private void tryAdd(boolean check) {

		var src = edSource.getPath();
		var dst = cbCopy.isSelected() ? edTarget.getPath().toFSPath() : edSource.getPath();

		int episodenumber = (int)spnEpisode.getValue();
		int length = (int)spnLength.getValue();
		String title = edTitle.getText().trim();
		CCDBLanguageList lang = ctrlLang.getValue();

		var imd = dst.getParent().append(src.getFilenameWithExt());

		CCDate adddate = CCDate.getCurrentDate();
		CCDateTimeList history = CCDateTimeList.createEmpty();
		CCTagList tags = CCTagList.EMPTY;
		var filesize = src.filesize();
		CCFileFormat format = CCFileFormat.getMovieFormatFromPath(src);
		CCMediaInfo minfo = edMediaInfo.getValue();

		var epack = new EpisodeDataPack(episodenumber, title, length, format, filesize, CCPath.createFromFSPath(dst), adddate, history, tags, lang, minfo);

		List<UserDataProblem> problems = new ArrayList<>();
		boolean probvalue = !check || checkUserDataEpisode(problems, epack, src, dst);

		// some problems are too fatal
		if (probvalue && Str.isNullOrWhitespace(title)) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}

		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, () -> tryAdd(false), this);
			amied.setVisible(true);
			return;
		}

		contentPanel.setEnabled(false);
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
					newEp.Part.set(CCPath.createFromFSPath(imd, Opt.False));
					newEp.Tags.set(tags);
					newEp.Language.set(lang);
					newEp.endUpdating();

					newEp.beginUpdating();
					newEp.Part.set(CCPath.createFromFSPath(dst));
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
				SwingUtils.invokeLater(() -> contentPanel.setEnabled(true) );
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
		var mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = MediaQueryRunner.query(edSource.getPath(), false);

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
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void parseCodecMetadata_Len() {
		var mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = MediaQueryRunner.query(edSource.getPath(), true);

			int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			spnLength.setValue(dur);

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void showCodecMetadata() {
		var mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			String dat = MediaQueryRunner.queryRaw(edSource.getPath());

			GenericTextDialog.showText(this, getTitle(), dat, false);
		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
