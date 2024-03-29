package de.jClipCorn.gui.frames.editMovieFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.MovieDataPack;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.VideoMetadata;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.features.metadata.impl.MediaInfoRunner;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseOnlineFrame.ParseOnlineDialog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.JReadableCCPathTextField;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.guiComponents.filesize.CCFileSizeSpinner;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.iconComponents.CCIcon16Button;
import de.jClipCorn.gui.guiComponents.iconComponents.CCIcon16Label;
import de.jClipCorn.gui.guiComponents.iconComponents.Icon16RefLink;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.jYearSpinner.JYearSpinner;
import de.jClipCorn.gui.guiComponents.language.LanguageListChooser;
import de.jClipCorn.gui.guiComponents.language.LanguageSetChooser;
import de.jClipCorn.gui.guiComponents.onlinescore.OnlineScoreControl;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.guiComponents.tags.TagPanel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.UpdateCallbackAdapter;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.DirtyUtil;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.ImageCropperResultListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditMovieFrame extends JCCFrame implements ParseResultHandler, UserDataProblemHandler, ImageCropperResultListener
{
	private final JFileChooser videoFileChooser;

	private final CCMovie movie;

	private final UpdateCallbackListener listener;

	private boolean _initFinished = false;
	private boolean _isDirty = false;

	public EditMovieFrame(Component owner, CCMovie movie, UpdateCallbackListener ucl)
	{
		super(movie.getMovieList());
		this.movie = movie;
		this.videoFileChooser = new JFileChooser(movie.getMovieList().getCommonPathForMovieFileChooser().toFile());
		this.listener = (ucl == null) ? new UpdateCallbackAdapter() : ucl;

		initComponents();
		postInit();

		_initFinished = true;
		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setDefaultValues();
		initFileChooser();
		initFields();

		setTitle(LocaleBundle.getFormattedString("EditMovieFrame.this.title", movie.getCompleteTitle())); //$NON-NLS-1$

		lblMISubWarning.setVisible(false);

		DirtyUtil.initDirtyListenerRecursive(this::setDirty, this);
	}

	private void setDefaultValues() {
		spnAddDate.setValue(CCDate.getCurrentDate());
		spnZyklus.setValue(-1);

		edViewedHistory.setValue(CCDateTimeList.createEmpty());
		edReference.setValue(CCOnlineReferenceList.EMPTY);
		edGroups.setValue(CCGroupList.EMPTY);
	}

	private void updateFilesize() {
		CCFileSize size = CCFileSize.ZERO;

		if (! edPart0.getPath().isEmpty()) size = CCFileSize.add(size, edPart0.getPath().toFSPath(this).filesize());
		if (! edPart1.getPath().isEmpty()) size = CCFileSize.add(size, edPart1.getPath().toFSPath(this).filesize());
		if (! edPart2.getPath().isEmpty()) size = CCFileSize.add(size, edPart2.getPath().toFSPath(this).filesize());
		if (! edPart3.getPath().isEmpty()) size = CCFileSize.add(size, edPart3.getPath().toFSPath(this).filesize());
		if (! edPart4.getPath().isEmpty()) size = CCFileSize.add(size, edPart4.getPath().toFSPath(this).filesize());
		if (! edPart5.getPath().isEmpty()) size = CCFileSize.add(size, edPart5.getPath().toFSPath(this).filesize());

		if (size.getBytes() > 0) {
			setFilesize(size);
		} else {
			testPaths();
		}
	}

	private void testPaths() {
		Color c1 = UIManager.getColor("TextField.background"); //$NON-NLS-1$
		Color c2 = Color.RED;

		if (! edPart0.getPath().isEmpty()) edPart0.setBackground(edPart0.getPath().toFSPath(this).exists()?c1:c2); else edPart0.setBackground(c1);
		if (! edPart1.getPath().isEmpty()) edPart1.setBackground(edPart1.getPath().toFSPath(this).exists()?c1:c2); else edPart1.setBackground(c1);
		if (! edPart2.getPath().isEmpty()) edPart2.setBackground(edPart2.getPath().toFSPath(this).exists()?c1:c2); else edPart2.setBackground(c1);
		if (! edPart3.getPath().isEmpty()) edPart3.setBackground(edPart3.getPath().toFSPath(this).exists()?c1:c2); else edPart3.setBackground(c1);
		if (! edPart4.getPath().isEmpty()) edPart4.setBackground(edPart4.getPath().toFSPath(this).exists()?c1:c2); else edPart4.setBackground(c1);
		if (! edPart5.getPath().isEmpty()) edPart5.setBackground(edPart5.getPath().toFSPath(this).exists()?c1:c2); else edPart5.setBackground(c1);
	}

	private void initFileChooser() {
		//$NON-NLS-1$
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat));

		videoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$
	}

	private void initFields() {
		edPart0.setPath(movie.Parts.get(0));
		edPart1.setPath(movie.Parts.get(1));
		edPart2.setPath(movie.Parts.get(2));
		edPart3.setPath(movie.Parts.get(3));
		edPart4.setPath(movie.Parts.get(4));
		edPart5.setPath(movie.Parts.get(5));

		edTitle.setText(movie.Title.get());
		edZyklus.setText(movie.Zyklus.get().getTitle());
		spnZyklus.setValue(movie.Zyklus.get().getNumber());
		cbxLanguage.setValue(movie.Language.get());
		cbxSubtitles.setValue(movie.Subtitles.get());
		spnLength.setValue(movie.Length.get());
		spnAddDate.setValue(movie.AddDate.get());
		spnOnlineScore.setValue(movie.OnlineScore.get());
		cbxFSK.setSelectedEnum(movie.FSK.get());
		cbxFormat.setSelectedEnum(movie.Format.get());
		spnYear.setValue(movie.Year.get());
		spnSize.setValue(movie.FileSize.get());

		cbxGenre0.setSelectedEnum(movie.Genres.get().getGenre(0));
		cbxGenre1.setSelectedEnum(movie.Genres.get().getGenre(1));
		cbxGenre2.setSelectedEnum(movie.Genres.get().getGenre(2));
		cbxGenre3.setSelectedEnum(movie.Genres.get().getGenre(3));
		cbxGenre4.setSelectedEnum(movie.Genres.get().getGenre(4));
		cbxGenre5.setSelectedEnum(movie.Genres.get().getGenre(5));
		cbxGenre6.setSelectedEnum(movie.Genres.get().getGenre(6));
		cbxGenre7.setSelectedEnum(movie.Genres.get().getGenre(7));

		cbxScore.setSelectedEnum(movie.Score.get());
		memoScoreComment.setText(movie.ScoreComment.get());
		tagPnl.setValue(movie.Tags.get());

		edCvrControl.setCover(movie.getCover());

		edViewedHistory.setValue(movie.ViewedHistory.get());
		edReference.setValue(movie.OnlineReference.get());
		edGroups.setValue(movie.Groups.get());

		ctrlMediaInfo.setValue(movie.MediaInfo.get());

		testPaths();
	}

	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
	}

	@Override
	public void setOnlineReference(CCOnlineReferenceList ref) {
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
	public String getTitleForParser() {
		return edTitle.getText();
	}

	@Override
	public CCOnlineReferenceList getSearchReference() {
		return edReference.getValue();
	}

	@Override
	public void setMovieFormat(CCFileFormat cmf) {
		cbxFormat.setSelectedEnum(cmf);
	}

	public void setMovieLanguage(CCDBLanguageSet lang) {
		cbxLanguage.setValue(lang);
	}

	public void setSubtitleLanguage(CCDBLanguageList lang) {
		cbxSubtitles.setValue(lang);
	}

	public void setFilepath(int p, FSPath at) {
		var t = CCPath.createFromFSPath(at, this);

		switch (p) {
		case 0:
			edPart0.setPath(t);
			edPart0.setCaretPosition(0);
			break;
		case 1:
			edPart1.setPath(t);
			edPart1.setCaretPosition(0);
			break;
		case 2:
			edPart2.setPath(t);
			edPart2.setCaretPosition(0);
			break;
		case 3:
			edPart3.setPath(t);
			edPart3.setCaretPosition(0);
			break;
		case 4:
			edPart4.setPath(t);
			edPart4.setCaretPosition(0);
			break;
		case 5:
			edPart5.setPath(t);
			edPart5.setCaretPosition(0);
			break;
		}

		testPaths();

		updateFilesize();

		CCFileFormat fmt = CCFileFormat.getMovieFormatFromPaths(edPart0.getPath().toFSPath(this), edPart1.getPath().toFSPath(this), edPart2.getPath().toFSPath(this), edPart3.getPath().toFSPath(this), edPart4.getPath().toFSPath(this), edPart5.getPath().toFSPath(this));
		if (fmt != null) cbxFormat.setSelectedEnum(fmt);
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
	public void setFilesize(CCFileSize size) {
		spnSize.setValue(size);
	}

	@Override
	public void setYear(int y) {
		spnYear.setValue(y);
	}

	@Override
	public void setGenre(int gid, CCGenre movGenre) {
		switch (gid)
		{
			case 0: cbxGenre0.setSelectedEnum(movGenre); break;
			case 1: cbxGenre1.setSelectedEnum(movGenre); break;
			case 2: cbxGenre2.setSelectedEnum(movGenre); break;
			case 3: cbxGenre3.setSelectedEnum(movGenre); break;
			case 4: cbxGenre4.setSelectedEnum(movGenre); break;
			case 5: cbxGenre5.setSelectedEnum(movGenre); break;
			case 6: cbxGenre6.setSelectedEnum(movGenre); break;
			case 7: cbxGenre7.setSelectedEnum(movGenre); break;
		}
	}

	@Override
	public void setFSK(CCFSK fsk) {
		cbxFSK.setSelectedEnum(fsk);
	}

	@Override
	public void setLength(int l) {
		spnLength.setValue(l);
	}

	@Override
	public void setScore(CCOnlineScore s) {
		spnOnlineScore.setValue(s);
	}

	@Override
	public void onFinishInserting() {
		// nothing
	}

	private void onBtnChooseClicked(int cNmbr) {
		int returnval = videoFileChooser.showOpenDialog(this);

		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}

		setFilepath(cNmbr, FSPath.create(videoFileChooser.getSelectedFile()));
	}

	private void onBtnClearClicked(int cNmbr) {
		setFilepath(cNmbr, FSPath.Empty); //$NON-NLS-1$
	}

	private void onBtnOK(boolean check) {
		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserData(problems);

		boolean fatalErr = false;

		// some problems are too fatal
		if (!edCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			probvalue = false;
			fatalErr = true;
		}
		if (edTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
			fatalErr = true;
		}

		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(this, movielist, problems, this, !fatalErr);
			amied.setVisible(true);
			return;
		}

		movie.beginUpdating();

		//#####################################################################################

		movie.Parts.set(0, edPart0.getPath());
		movie.Parts.set(1, edPart1.getPath());
		movie.Parts.set(2, edPart2.getPath());
		movie.Parts.set(3, edPart3.getPath());
		movie.Parts.set(4, edPart4.getPath());
		movie.Parts.set(5, edPart5.getPath());

		movie.Title.set(edTitle.getText());
		movie.Zyklus.setTitle(edZyklus.getText());
		movie.Zyklus.setNumber((int) spnZyklus.getValue());

		movie.Language.set(cbxLanguage.getValue());
		movie.Subtitles.set(cbxSubtitles.getValue());

		movie.Length.set((int) spnLength.getValue());

		movie.AddDate.set(spnAddDate.getValue());

		movie.OnlineScore.set(spnOnlineScore.getValue());

		movie.FSK.set(cbxFSK.getSelectedEnum());
		movie.Format.set(cbxFormat.getSelectedEnum());

		movie.Year.set(spnYear.getValue());
		movie.FileSize.set(spnSize.getValue());

		movie.Genres.set(cbxGenre0.getSelectedEnum(), 0);
		movie.Genres.set(cbxGenre1.getSelectedEnum(), 1);
		movie.Genres.set(cbxGenre2.getSelectedEnum(), 2);
		movie.Genres.set(cbxGenre3.getSelectedEnum(), 3);
		movie.Genres.set(cbxGenre4.getSelectedEnum(), 4);
		movie.Genres.set(cbxGenre5.getSelectedEnum(), 5);
		movie.Genres.set(cbxGenre6.getSelectedEnum(), 6);
		movie.Genres.set(cbxGenre7.getSelectedEnum(), 7);

		movie.Tags.set(tagPnl.getValue());
		movie.Score.set(cbxScore.getSelectedEnum());
		movie.ScoreComment.set(memoScoreComment.getText());

		movie.setCover(edCvrControl.getResizedImageForStorage());

		movie.ViewedHistory.set(edViewedHistory.getValue());
		movie.OnlineReference.set(edReference.getValue());
		movie.Groups.set(edGroups.getValue());

		movie.MediaInfo.set(ctrlMediaInfo.getValue());

		//#####################################################################################

		movie.endUpdating();

		if (listener != null) {
			listener.onUpdate(movie);
		}

		dispose();
	}

	private boolean checkUserData(List<UserDataProblem> ret)
	{
		var mpack = new MovieDataPack
		(
			new CCMovieZyklus(edZyklus.getText(), (int) spnZyklus.getValue()),
			ctrlMediaInfo.getValue(),
			(int) spnLength.getValue(),
			spnAddDate.getValue(),
			cbxFormat.getSelectedEnum(),
			spnYear.getValue(),
			spnSize.getValue(),
			Arrays.asList(edPart0.getPath(), edPart1.getPath(), edPart2.getPath(), edPart3.getPath(), edPart4.getPath(), edPart5.getPath()),
			CCDateTimeList.createEmpty(),
			cbxLanguage.getValue(),
			cbxSubtitles.getValue(),
			edTitle.getText(),
			CCGenreList.create(cbxGenre0.getSelectedEnum(), cbxGenre1.getSelectedEnum(), cbxGenre2.getSelectedEnum(), cbxGenre3.getSelectedEnum(), cbxGenre4.getSelectedEnum(), cbxGenre5.getSelectedEnum(), cbxGenre6.getSelectedEnum(), cbxGenre7.getSelectedEnum()),
			spnOnlineScore.getValue(),
			cbxFSK.getSelectedEnum(),
			cbxScore.getSelectedEnum(),
			memoScoreComment.getText(),
			edReference.getValue(),
			edGroups.getValue(),
			CCTagList.EMPTY,
			edCvrControl.getResizedImageForStorage()
		);
		UserDataProblem.testMovieData(ret, movie.getMovieList(), movie, mpack);

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

	private void calculateMediaInfoAndSetLength() {
		if (!ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue().existsAndCanExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<VideoMetadata> dat = new ArrayList<>();

			var mq = new MediaInfoRunner(movielist);

			if (!edPart0.getPath().isEmpty()) dat.add(mq.run(edPart0.getPath().toFSPath(this)));
			if (!edPart1.getPath().isEmpty()) dat.add(mq.run(edPart1.getPath().toFSPath(this)));
			if (!edPart2.getPath().isEmpty()) dat.add(mq.run(edPart2.getPath().toFSPath(this)));
			if (!edPart3.getPath().isEmpty()) dat.add(mq.run(edPart3.getPath().toFSPath(this)));
			if (!edPart4.getPath().isEmpty()) dat.add(mq.run(edPart4.getPath().toFSPath(this)));
			if (!edPart5.getPath().isEmpty()) dat.add(mq.run(edPart5.getPath().toFSPath(this)));

			if (dat.isEmpty()) {
				lblLenAuto.setText(Str.Empty);
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			lblLenAuto.setText(Str.Empty);

			if (CCStreams.iterate(dat).any(d -> d.Duration.isEmpty())) {
				throw new MediaQueryException("some files have missing <Duration>"); //$NON-NLS-1$
			}

			var dur = CCStreams.iterate(dat).sumDouble(d -> d.Duration.get())/60;
			setLength((int)dur);

		} catch (IOException | MetadataQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@SuppressWarnings("nls")
	private void calculateMediaInfoAndSetLanguage() {
		if (!ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue().existsAndCanExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<VideoMetadata> dat = new ArrayList<>();

			var mq = new MediaInfoRunner(movielist);

			if (!edPart0.getPath().isEmpty()) dat.add(mq.run(edPart0.getPath().toFSPath(this)));
			if (!edPart1.getPath().isEmpty()) dat.add(mq.run(edPart1.getPath().toFSPath(this)));
			if (!edPart2.getPath().isEmpty()) dat.add(mq.run(edPart2.getPath().toFSPath(this)));
			if (!edPart3.getPath().isEmpty()) dat.add(mq.run(edPart3.getPath().toFSPath(this)));
			if (!edPart4.getPath().isEmpty()) dat.add(mq.run(edPart4.getPath().toFSPath(this)));
			if (!edPart5.getPath().isEmpty()) dat.add(mq.run(edPart5.getPath().toFSPath(this)));

			if (dat.isEmpty()) {
				lblLenAuto.setText(Str.Empty);
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			if (CCStreams.iterate(dat).any(d -> d.Duration.isEmpty())) {
				var dur = CCStreams.iterate(dat).sumDouble(d -> d.Duration.get())/60;
				setLength((int)dur);
				lblLenAuto.setText("("+((int)dur)+")");
			} else {
				lblLenAuto.setText(Str.Empty);
			}

			if (CCStreams.iterate(dat).any(d -> d.AudioLanguages.size() == 0)) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			var anyAudioLangErr = false;
			var anyAudioLangEmpty = false;

			var errOutput = new StringBuilder();
			for (var vmd : dat) {
				errOutput.append(Str.format("{0}\n", vmd.InputFile.getFilenameWithExt()));
				errOutput.append("=======================================================\n");
				for (var alang : vmd.AudioLanguages) {
					if (alang.isEmpty()) {
						errOutput.append(Str.format("[---] (empty)\n"));
						anyAudioLangEmpty = true;
					}
					else if (alang.isError()) {
						errOutput.append(Str.format("[---] {0}\n", alang.getErr().TextShort));
						anyAudioLangErr = true;
					}
					else {
						errOutput.append(Str.format("[{0}] (ok)\n", alang.get().getShortString()));
					}
				}
				errOutput.append("\n");
			}

			if (anyAudioLangErr)
			{
				var dialogRes = DialogHelper.showYesNoDlg(this, LocaleBundle.getString("Dialogs.MetadataError_caption"), LocaleBundle.getString("AddMovieFrame.dialog_errlang") + "\n\n" + errOutput);
				if (!dialogRes) return;
			}
			else if (anyAudioLangEmpty)
			{
				var dialogRes = DialogHelper.showYesNoDlg(this, LocaleBundle.getString("Dialogs.MetadataError_caption"), LocaleBundle.getString("AddMovieFrame.dialog_emptylang") + "\n\n" + errOutput);
				if (!dialogRes) return;
			}

			CCDBLanguageSet dbll = dat.get(0).getValidAudioLanguages();
			for (int i = 1; i < dat.size(); i++) dbll = CCDBLanguageSet.intersection(dbll, dat.get(i).getValidAudioLanguages());

			if (dbll.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
			} else {
				setMovieLanguage(dbll);
			}

		} catch (IOException | MetadataQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@SuppressWarnings("nls")
	private void calculateAndSetMediaInfo() {
		if (!ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue().existsAndCanExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<VideoMetadata> dat = new ArrayList<>();

			var mq = new MediaInfoRunner(movielist);

			if (!edPart0.getPath().isEmpty()) dat.add(mq.run(edPart0.getPath().toFSPath(this)));
			if (!edPart1.getPath().isEmpty()) dat.add(mq.run(edPart1.getPath().toFSPath(this)));
			if (!edPart2.getPath().isEmpty()) dat.add(mq.run(edPart2.getPath().toFSPath(this)));
			if (!edPart3.getPath().isEmpty()) dat.add(mq.run(edPart3.getPath().toFSPath(this)));
			if (!edPart4.getPath().isEmpty()) dat.add(mq.run(edPart4.getPath().toFSPath(this)));
			if (!edPart5.getPath().isEmpty()) dat.add(mq.run(edPart5.getPath().toFSPath(this)));

			if (dat.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			if (CCStreams.iterate(dat).any(d -> d.Duration.isEmpty()))
			{
				SwingUtils.invokeLater(() -> lblLenAuto.setText(Str.Empty));
			}
			else
			{
				var dur = CCStreams.iterate(dat).sumDouble(d -> d.Duration.get())/60;
				SwingUtils.invokeLater(() -> lblLenAuto.setText("("+((int)dur)+")"));
			}

			ctrlMediaInfo.setValue(dat.get(0));

		} catch (IOException | MetadataQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@SuppressWarnings("nls")
	private void calculateAndShowMediaInfo() {
		if (!ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue().existsAndCanExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		StringBuilder b = new StringBuilder();

		var mq = new MediaInfoRunner(movielist);

		try {
			if (!edPart0.getPath().isEmpty()) b.append(mq.run(edPart0.getPath().toFSPath(this)).RawOutput).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart1.getPath().isEmpty()) b.append(mq.run(edPart1.getPath().toFSPath(this)).RawOutput).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart2.getPath().isEmpty()) b.append(mq.run(edPart2.getPath().toFSPath(this)).RawOutput).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart3.getPath().isEmpty()) b.append(mq.run(edPart3.getPath().toFSPath(this)).RawOutput).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart4.getPath().isEmpty()) b.append(mq.run(edPart4.getPath().toFSPath(this)).RawOutput).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart5.getPath().isEmpty()) b.append(mq.run(edPart5.getPath().toFSPath(this)).RawOutput).append("\n\n\n\n\n"); //$NON-NLS-1$

			GenericTextDialog.showText(this, getTitle(), b.toString(), false);
		} catch (IOException | MetadataQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@SuppressWarnings("nls")
	private void calculateMediaInfoAndSetSubs() {
		if (!ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue().existsAndCanExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<VideoMetadata> dat = new ArrayList<>();

			var mq = new MediaInfoRunner(movielist);

			if (!edPart0.getPath().isEmpty()) dat.add(mq.run(edPart0.getPath().toFSPath(this)));
			if (!edPart1.getPath().isEmpty()) dat.add(mq.run(edPart1.getPath().toFSPath(this)));
			if (!edPart2.getPath().isEmpty()) dat.add(mq.run(edPart2.getPath().toFSPath(this)));
			if (!edPart3.getPath().isEmpty()) dat.add(mq.run(edPart3.getPath().toFSPath(this)));
			if (!edPart4.getPath().isEmpty()) dat.add(mq.run(edPart4.getPath().toFSPath(this)));
			if (!edPart5.getPath().isEmpty()) dat.add(mq.run(edPart5.getPath().toFSPath(this)));

			if (dat.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			var anySubLangErr = false;
			var anySubLangEmpty = false;

			var errOutput = new StringBuilder();
			for (var vmd : dat) {
				errOutput.append(Str.format("{0}\n", vmd.InputFile.getFilenameWithExt()));
				errOutput.append("=======================================================\n");
				for (var alang : vmd.SubtitleLanguages) {
					if (alang.isEmpty()) {
						errOutput.append(Str.format("[---] (empty)\n"));
						anySubLangEmpty = true;
					}
					else if (alang.isError()) {
						errOutput.append(Str.format("[---] {0}\n", alang.getErr().TextShort));
						anySubLangErr = true;
					}
					else {
						errOutput.append(Str.format("[{0}] (ok)\n", alang.get().getShortString()));
					}
				}
				errOutput.append("\n");
			}

			if (anySubLangErr)
			{
				var dialogRes = DialogHelper.showYesNoDlg(this, LocaleBundle.getString("Dialogs.MetadataError_caption"), LocaleBundle.getString("AddMovieFrame.dialog_errlang") + "\n\n" + errOutput);
				if (!dialogRes) return;
			}
			else if (anySubLangEmpty)
			{
				var dialogRes = DialogHelper.showYesNoDlg(this, LocaleBundle.getString("Dialogs.MetadataError_caption"), LocaleBundle.getString("AddMovieFrame.dialog_emptylang") + "\n\n" + errOutput);
				if (!dialogRes) return;
			}

			CCDBLanguageList dbll = dat.get(0).getValidSubtitleLanguages();
			for (int i = 1; i < dat.size(); i++) if (!dat.get(i).getValidSubtitleLanguages().isEqual(dbll)) throw new MediaQueryException("Files [1] and [" + (i+1)+"] have different subtitles");

			lblMISubWarning.setVisible(false);
			setSubtitleLanguage(dbll);

		} catch (IOException | MetadataQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
			lblMISubWarning.setVisible(true);
		}
	}

	private void setDirty() {
		if (!_initFinished) return;
		setTitle(LocaleBundle.getFormattedString("EditMovieFrame.this.title", movie.getCompleteTitle()) + "*"); //$NON-NLS-1$ //$NON-NLS-2$
		_isDirty = true;
	}

	private void onChoose0() {
		onBtnChooseClicked(0);
	}

	private void onChoose1() {
		onBtnChooseClicked(1);
	}

	private void onChoose2() {
		onBtnChooseClicked(2);
	}

	private void onChoose3() {
		onBtnChooseClicked(3);
	}

	private void onChoose4() {
		onBtnChooseClicked(4);
	}

	private void onChoose5() {
		onBtnChooseClicked(5);
	}

	private void onClear1() {
		onBtnClearClicked(1);
	}

	private void onClear2() {
		onBtnClearClicked(2);
	}

	private void onClear3() {
		onBtnClearClicked(3);
	}

	private void onClear4() {
		onBtnClearClicked(4);
	}

	private void onClear5() {
		onBtnClearClicked(5);
	}

	private void showOnlineParser() {
		(new ParseOnlineDialog(this, movielist, this, CCDBElementTyp.MOVIE)).setVisible(true);
	}

	private void onOkay() {
		onBtnOK(true);
	}

	private void cancel() {
		dispose();
	}

	private void onSetToday() {
		spnAddDate.setValue(CCDate.getCurrentDate());
	}

	private void onClosing() {
		if (_isDirty) {
			if (DialogHelper.showLocaleYesNoDefaultNo(this, "Dialogs.CloseButDirty")) this.dispose(); //$NON-NLS-1$
		} else {
			this.dispose();
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlLeft = new JPanel();
		pnlParts = new JPanel();
		label1 = new JLabel();
		edPart0 = new JReadableCCPathTextField();
		btnChoose0 = new JButton();
		label2 = new JLabel();
		edPart1 = new JReadableCCPathTextField();
		btnChoose1 = new JButton();
		btnClear1 = new JButton();
		label3 = new JLabel();
		edPart2 = new JReadableCCPathTextField();
		btnChoose2 = new JButton();
		btnClear2 = new JButton();
		label5 = new JLabel();
		edPart3 = new JReadableCCPathTextField();
		btnChoose3 = new JButton();
		btnClear3 = new JButton();
		label6 = new JLabel();
		edPart4 = new JReadableCCPathTextField();
		btnChoose4 = new JButton();
		btnClear4 = new JButton();
		label4 = new JLabel();
		edPart5 = new JReadableCCPathTextField();
		btnChoose5 = new JButton();
		btnClear5 = new JButton();
		btnTestParts = new JButton();
		pnlData = new JPanel();
		label15 = new JLabel();
		edTitle = new JTextField();
		label16 = new JLabel();
		edReference = new JReferenceChooser(movielist);
		label17 = new JLabel();
		edZyklus = new JTextField();
		spnZyklus = new JSpinner();
		label18 = new JLabel();
		edGroups = new GroupListEditor(movielist);
		label19 = new JLabel();
		spnLength = new JSpinner();
		label29 = new JLabel();
		btnMediaInfoLen = new CCIcon16Button();
		lblLenAuto = new JLabel();
		label20 = new JLabel();
		cbxLanguage = new LanguageSetChooser();
		btnMediaInfoLang = new CCIcon16Button();
		btnQueryMediaInfo = new JButton();
		label33 = new JLabel();
		cbxSubtitles = new LanguageListChooser();
		btnMediaInfoSubs = new CCIcon16Button();
		lblMISubWarning = new CCIcon16Label();
		label21 = new JLabel();
		ctrlMediaInfo = new JMediaInfoControl(movielist, () -> edPart0.getPath().toFSPath(this));
		btnMediaInfoMain = new CCIcon16Button();
		label22 = new JLabel();
		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), CCDate.getMinimumDate(), null);
		btnToday = new JButton();
		label23 = new JLabel();
		spnOnlineScore = new OnlineScoreControl();
		label24 = new JLabel();
		cbxFSK = new CCEnumComboBox<CCFSK>(CCFSK.getWrapper());
		label25 = new JLabel();
		cbxFormat = new CCEnumComboBox<CCFileFormat>(CCFileFormat.getWrapper());
		label26 = new JLabel();
		spnYear = new JYearSpinner();
		label27 = new JLabel();
		spnSize = new CCFileSizeSpinner();
		btnRecalculateSize = new JButton();
		label28 = new JLabel();
		cbxScore = new CCEnumComboBox<CCUserScore>(CCUserScore.getWrapper());
		label30 = new JLabel();
		scrollPane1 = new JScrollPane();
		memoScoreComment = new JTextArea();
		label31 = new JLabel();
		tagPnl = new TagPanel();
		pnlRight = new JPanel();
		pnlGenres = new JPanel();
		label7 = new JLabel();
		cbxGenre0 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label8 = new JLabel();
		cbxGenre1 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label9 = new JLabel();
		cbxGenre2 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label10 = new JLabel();
		cbxGenre3 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label11 = new JLabel();
		cbxGenre4 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label12 = new JLabel();
		cbxGenre5 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label13 = new JLabel();
		cbxGenre6 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label14 = new JLabel();
		cbxGenre7 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		btnParseIMDB = new JButton();
		label32 = new JLabel();
		edViewedHistory = new DateTimeListEditor();
		edCvrControl = new EditCoverControl(this, this);
		pnlBottom = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("EditMovieFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(670, 900));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onClosing();
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 0dlu:grow, 10dlu, pref, $ugap", //$NON-NLS-1$
			"$ugap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

		//======== pnlLeft ========
		{
			pnlLeft.setLayout(new FormLayout(
				"0dlu:grow", //$NON-NLS-1$
				"default, 10dlu, default:grow")); //$NON-NLS-1$

			//======== pnlParts ========
			{
				pnlParts.setLayout(new FormLayout(
					"default, $lcgap, 0dlu:grow, 2*($lcgap, default)", //$NON-NLS-1$
					"6*(default, $lgap), default")); //$NON-NLS-1$

				//---- label1 ----
				label1.setText(LocaleBundle.getString("AddMovieFrame.lblPart.text")); //$NON-NLS-1$
				pnlParts.add(label1, CC.xy(1, 1));
				pnlParts.add(edPart0, CC.xywh(3, 1, 3, 1));

				//---- btnChoose0 ----
				btnChoose0.setText("..."); //$NON-NLS-1$
				btnChoose0.addActionListener(e -> onChoose0());
				pnlParts.add(btnChoose0, CC.xy(7, 1));

				//---- label2 ----
				label2.setText(LocaleBundle.getString("AddMovieFrame.lblPart_1.text")); //$NON-NLS-1$
				pnlParts.add(label2, CC.xy(1, 3));
				pnlParts.add(edPart1, CC.xy(3, 3));

				//---- btnChoose1 ----
				btnChoose1.setText("..."); //$NON-NLS-1$
				btnChoose1.addActionListener(e -> onChoose1());
				pnlParts.add(btnChoose1, CC.xy(5, 3));

				//---- btnClear1 ----
				btnClear1.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear1.addActionListener(e -> onClear1());
				pnlParts.add(btnClear1, CC.xy(7, 3));

				//---- label3 ----
				label3.setText(LocaleBundle.getString("AddMovieFrame.lblPart_2.text")); //$NON-NLS-1$
				pnlParts.add(label3, CC.xy(1, 5));
				pnlParts.add(edPart2, CC.xy(3, 5));

				//---- btnChoose2 ----
				btnChoose2.setText("..."); //$NON-NLS-1$
				btnChoose2.addActionListener(e -> onChoose2());
				pnlParts.add(btnChoose2, CC.xy(5, 5));

				//---- btnClear2 ----
				btnClear2.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear2.addActionListener(e -> onClear2());
				pnlParts.add(btnClear2, CC.xy(7, 5));

				//---- label5 ----
				label5.setText(LocaleBundle.getString("AddMovieFrame.lblPart_3.text")); //$NON-NLS-1$
				pnlParts.add(label5, CC.xy(1, 7));
				pnlParts.add(edPart3, CC.xy(3, 7));

				//---- btnChoose3 ----
				btnChoose3.setText("..."); //$NON-NLS-1$
				btnChoose3.addActionListener(e -> onChoose3());
				pnlParts.add(btnChoose3, CC.xy(5, 7));

				//---- btnClear3 ----
				btnClear3.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear3.addActionListener(e -> onClear3());
				pnlParts.add(btnClear3, CC.xy(7, 7));

				//---- label6 ----
				label6.setText(LocaleBundle.getString("AddMovieFrame.lblPart_4.text")); //$NON-NLS-1$
				pnlParts.add(label6, CC.xy(1, 9));
				pnlParts.add(edPart4, CC.xy(3, 9));

				//---- btnChoose4 ----
				btnChoose4.setText("..."); //$NON-NLS-1$
				btnChoose4.addActionListener(e -> onChoose4());
				pnlParts.add(btnChoose4, CC.xy(5, 9));

				//---- btnClear4 ----
				btnClear4.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear4.addActionListener(e -> onClear4());
				pnlParts.add(btnClear4, CC.xy(7, 9));

				//---- label4 ----
				label4.setText(LocaleBundle.getString("AddMovieFrame.lblPart_5.text")); //$NON-NLS-1$
				pnlParts.add(label4, CC.xy(1, 11));
				pnlParts.add(edPart5, CC.xy(3, 11));

				//---- btnChoose5 ----
				btnChoose5.setText("..."); //$NON-NLS-1$
				btnChoose5.addActionListener(e -> onChoose5());
				pnlParts.add(btnChoose5, CC.xy(5, 11));

				//---- btnClear5 ----
				btnClear5.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear5.addActionListener(e -> onClear5());
				pnlParts.add(btnClear5, CC.xy(7, 11));

				//---- btnTestParts ----
				btnTestParts.setText(LocaleBundle.getString("EditMovieFrame.btnTestParts.text")); //$NON-NLS-1$
				btnTestParts.addActionListener(e -> testPaths());
				pnlParts.add(btnTestParts, CC.xywh(5, 13, 3, 1));
			}
			pnlLeft.add(pnlParts, CC.xy(1, 1, CC.FILL, CC.FILL));

			//======== pnlData ========
			{
				pnlData.setLayout(new FormLayout(
					"default, $lcgap, 0dlu:grow, $lcgap, [40dlu,default], $lcgap, 16dlu, $lcgap, 25dlu, $lcgap, [30dlu,default]", //$NON-NLS-1$
					"15*(default, $lgap), 16dlu:grow, $lgap, default")); //$NON-NLS-1$

				//---- label15 ----
				label15.setText(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
				pnlData.add(label15, CC.xy(1, 1));
				pnlData.add(edTitle, CC.xywh(3, 1, 3, 1));

				//---- label16 ----
				label16.setText(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
				pnlData.add(label16, CC.xy(1, 3));
				pnlData.add(edReference, CC.xywh(3, 3, 3, 1));

				//---- label17 ----
				label17.setText(LocaleBundle.getString("AddMovieFrame.lblZyklus.text")); //$NON-NLS-1$
				pnlData.add(label17, CC.xy(1, 5));
				pnlData.add(edZyklus, CC.xy(3, 5));

				//---- spnZyklus ----
				spnZyklus.setModel(new SpinnerNumberModel(-1, -1, null, 1));
				pnlData.add(spnZyklus, CC.xy(5, 5));

				//---- label18 ----
				label18.setText(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
				pnlData.add(label18, CC.xy(1, 7));
				pnlData.add(edGroups, CC.xywh(3, 7, 3, 1));

				//---- label19 ----
				label19.setText(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
				pnlData.add(label19, CC.xy(1, 9));

				//---- spnLength ----
				spnLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
				pnlData.add(spnLength, CC.xy(3, 9));

				//---- label29 ----
				label29.setText("min."); //$NON-NLS-1$
				pnlData.add(label29, CC.xy(5, 9));

				//---- btnMediaInfoLen ----
				btnMediaInfoLen.setIconRef(Icon16RefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfoLen.setToolTipText("MediaInfo"); //$NON-NLS-1$
				btnMediaInfoLen.addActionListener(e -> calculateMediaInfoAndSetLength());
				pnlData.add(btnMediaInfoLen, CC.xy(7, 9));
				pnlData.add(lblLenAuto, CC.xywh(9, 9, 3, 1, CC.FILL, CC.FILL));

				//---- label20 ----
				label20.setText(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
				pnlData.add(label20, CC.xy(1, 11));
				pnlData.add(cbxLanguage, CC.xywh(3, 11, 3, 1));

				//---- btnMediaInfoLang ----
				btnMediaInfoLang.setIconRef(Icon16RefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfoLang.setToolTipText("MediaInfo"); //$NON-NLS-1$
				btnMediaInfoLang.addActionListener(e -> calculateMediaInfoAndSetLanguage());
				pnlData.add(btnMediaInfoLang, CC.xy(7, 11));

				//---- btnQueryMediaInfo ----
				btnQueryMediaInfo.setText("..."); //$NON-NLS-1$
				btnQueryMediaInfo.addActionListener(e -> calculateAndShowMediaInfo());
				pnlData.add(btnQueryMediaInfo, CC.xy(9, 11));

				//---- label33 ----
				label33.setText(LocaleBundle.getString("AddMovieFrame.lblSubtitles")); //$NON-NLS-1$
				pnlData.add(label33, CC.xy(1, 13));
				pnlData.add(cbxSubtitles, CC.xywh(3, 13, 3, 1));

				//---- btnMediaInfoSubs ----
				btnMediaInfoSubs.setIconRef(Icon16RefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfoSubs.setToolTipText("MediaInfo"); //$NON-NLS-1$
				btnMediaInfoSubs.addActionListener(e -> calculateMediaInfoAndSetSubs());
				pnlData.add(btnMediaInfoSubs, CC.xy(7, 13));

				//---- lblMISubWarning ----
				lblMISubWarning.setIconRef(Icon16RefLink.ICN_WARNING_TRIANGLE);
				pnlData.add(lblMISubWarning, CC.xy(9, 13, CC.LEFT, CC.FILL));

				//---- label21 ----
				label21.setText(LocaleBundle.getString("AddMovieFrame.lblMediaInfo")); //$NON-NLS-1$
				pnlData.add(label21, CC.xy(1, 15));
				pnlData.add(ctrlMediaInfo, CC.xywh(3, 15, 3, 1, CC.DEFAULT, CC.FILL));

				//---- btnMediaInfoMain ----
				btnMediaInfoMain.setIconRef(Icon16RefLink.ICN_MENUBAR_MEDIAINFO);
				btnMediaInfoMain.setToolTipText("MediaInfo"); //$NON-NLS-1$
				btnMediaInfoMain.addActionListener(e -> calculateAndSetMediaInfo());
				pnlData.add(btnMediaInfoMain, CC.xy(7, 15));

				//---- label22 ----
				label22.setText(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
				pnlData.add(label22, CC.xy(1, 17));
				pnlData.add(spnAddDate, CC.xywh(3, 17, 3, 1));

				//---- btnToday ----
				btnToday.setText(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
				btnToday.addActionListener(e -> onSetToday());
				pnlData.add(btnToday, CC.xywh(7, 17, 3, 1));

				//---- label23 ----
				label23.setText(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
				pnlData.add(label23, CC.xy(1, 19));
				pnlData.add(spnOnlineScore, CC.xywh(3, 19, 3, 1));

				//---- label24 ----
				label24.setText(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
				pnlData.add(label24, CC.xy(1, 21));
				pnlData.add(cbxFSK, CC.xywh(3, 21, 3, 1));

				//---- label25 ----
				label25.setText(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
				pnlData.add(label25, CC.xy(1, 23));
				pnlData.add(cbxFormat, CC.xywh(3, 23, 3, 1));

				//---- label26 ----
				label26.setText(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
				pnlData.add(label26, CC.xy(1, 25));
				pnlData.add(spnYear, CC.xywh(3, 25, 3, 1));

				//---- label27 ----
				label27.setText(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
				pnlData.add(label27, CC.xy(1, 27));
				pnlData.add(spnSize, CC.xywh(3, 27, 3, 1));

				//---- btnRecalculateSize ----
				btnRecalculateSize.addActionListener(e -> updateFilesize());
				pnlData.add(btnRecalculateSize, CC.xy(7, 27, CC.FILL, CC.FILL));

				//---- label28 ----
				label28.setText(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
				pnlData.add(label28, CC.xy(1, 29));
				pnlData.add(cbxScore, CC.xywh(3, 29, 3, 1));

				//---- label30 ----
				label30.setText(LocaleBundle.getString("EditMovieFrame.lblScoreComment")); //$NON-NLS-1$
				label30.setBorder(new EmptyBorder(5, 0, 0, 0));
				pnlData.add(label30, CC.xy(1, 31, CC.DEFAULT, CC.TOP));

				//======== scrollPane1 ========
				{
					scrollPane1.setViewportView(memoScoreComment);
				}
				pnlData.add(scrollPane1, CC.xywh(3, 31, 3, 1, CC.DEFAULT, CC.FILL));

				//---- label31 ----
				label31.setText(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
				pnlData.add(label31, CC.xy(1, 33));
				pnlData.add(tagPnl, CC.xywh(3, 33, 3, 1));
			}
			pnlLeft.add(pnlData, CC.xy(1, 3, CC.FILL, CC.FILL));
		}
		contentPane.add(pnlLeft, CC.xy(2, 2, CC.FILL, CC.FILL));

		//======== pnlRight ========
		{
			pnlRight.setLayout(new FormLayout(
				"default:grow", //$NON-NLS-1$
				"default:grow, 10dlu, pref")); //$NON-NLS-1$

			//======== pnlGenres ========
			{
				pnlGenres.setLayout(new FormLayout(
					"default, $lcgap, default:grow", //$NON-NLS-1$
					"7*(default, $lgap), default, $pgap, [50dlu,default], $pgap, [65dlu,default]:grow")); //$NON-NLS-1$

				//---- label7 ----
				label7.setText(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
				pnlGenres.add(label7, CC.xy(1, 1));
				pnlGenres.add(cbxGenre0, CC.xy(3, 1));

				//---- label8 ----
				label8.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
				pnlGenres.add(label8, CC.xy(1, 3));
				pnlGenres.add(cbxGenre1, CC.xy(3, 3));

				//---- label9 ----
				label9.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
				pnlGenres.add(label9, CC.xy(1, 5));
				pnlGenres.add(cbxGenre2, CC.xy(3, 5));

				//---- label10 ----
				label10.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
				pnlGenres.add(label10, CC.xy(1, 7));
				pnlGenres.add(cbxGenre3, CC.xy(3, 7));

				//---- label11 ----
				label11.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
				pnlGenres.add(label11, CC.xy(1, 9));
				pnlGenres.add(cbxGenre4, CC.xy(3, 9));

				//---- label12 ----
				label12.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
				pnlGenres.add(label12, CC.xy(1, 11));
				pnlGenres.add(cbxGenre5, CC.xy(3, 11));

				//---- label13 ----
				label13.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
				pnlGenres.add(label13, CC.xy(1, 13));
				pnlGenres.add(cbxGenre6, CC.xy(3, 13));

				//---- label14 ----
				label14.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
				pnlGenres.add(label14, CC.xy(1, 15));
				pnlGenres.add(cbxGenre7, CC.xy(3, 15));

				//---- btnParseIMDB ----
				btnParseIMDB.setText(LocaleBundle.getString("AddMovieFrame.btnParseIMDB.text")); //$NON-NLS-1$
				btnParseIMDB.setFont(btnParseIMDB.getFont().deriveFont(btnParseIMDB.getFont().getStyle() | Font.BOLD, btnParseIMDB.getFont().getSize() + 4f));
				btnParseIMDB.addActionListener(e -> showOnlineParser());
				pnlGenres.add(btnParseIMDB, CC.xy(3, 17, CC.FILL, CC.FILL));

				//---- label32 ----
				label32.setText(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
				pnlGenres.add(label32, CC.xy(1, 19, CC.DEFAULT, CC.TOP));
				pnlGenres.add(edViewedHistory, CC.xy(3, 19, CC.DEFAULT, CC.FILL));
			}
			pnlRight.add(pnlGenres, CC.xy(1, 1, CC.FILL, CC.FILL));
			pnlRight.add(edCvrControl, CC.xy(1, 3, CC.RIGHT, CC.BOTTOM));
		}
		contentPane.add(pnlRight, CC.xy(4, 2, CC.FILL, CC.FILL));

		//======== pnlBottom ========
		{
			pnlBottom.setLayout(new FlowLayout());

			//---- btnOK ----
			btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			btnOK.addActionListener(e -> onOkay());
			pnlBottom.add(btnOK);

			//---- btnCancel ----
			btnCancel.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
			btnCancel.addActionListener(e -> cancel());
			pnlBottom.add(btnCancel);
		}
		contentPane.add(pnlBottom, CC.xywh(2, 4, 3, 1));
		setSize(900, 925);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlLeft;
	private JPanel pnlParts;
	private JLabel label1;
	private JReadableCCPathTextField edPart0;
	private JButton btnChoose0;
	private JLabel label2;
	private JReadableCCPathTextField edPart1;
	private JButton btnChoose1;
	private JButton btnClear1;
	private JLabel label3;
	private JReadableCCPathTextField edPart2;
	private JButton btnChoose2;
	private JButton btnClear2;
	private JLabel label5;
	private JReadableCCPathTextField edPart3;
	private JButton btnChoose3;
	private JButton btnClear3;
	private JLabel label6;
	private JReadableCCPathTextField edPart4;
	private JButton btnChoose4;
	private JButton btnClear4;
	private JLabel label4;
	private JReadableCCPathTextField edPart5;
	private JButton btnChoose5;
	private JButton btnClear5;
	private JButton btnTestParts;
	private JPanel pnlData;
	private JLabel label15;
	private JTextField edTitle;
	private JLabel label16;
	private JReferenceChooser edReference;
	private JLabel label17;
	private JTextField edZyklus;
	private JSpinner spnZyklus;
	private JLabel label18;
	private GroupListEditor edGroups;
	private JLabel label19;
	private JSpinner spnLength;
	private JLabel label29;
	private CCIcon16Button btnMediaInfoLen;
	private JLabel lblLenAuto;
	private JLabel label20;
	private LanguageSetChooser cbxLanguage;
	private CCIcon16Button btnMediaInfoLang;
	private JButton btnQueryMediaInfo;
	private JLabel label33;
	private LanguageListChooser cbxSubtitles;
	private CCIcon16Button btnMediaInfoSubs;
	private CCIcon16Label lblMISubWarning;
	private JLabel label21;
	private JMediaInfoControl ctrlMediaInfo;
	private CCIcon16Button btnMediaInfoMain;
	private JLabel label22;
	private JCCDateSpinner spnAddDate;
	private JButton btnToday;
	private JLabel label23;
	private OnlineScoreControl spnOnlineScore;
	private JLabel label24;
	private CCEnumComboBox<CCFSK> cbxFSK;
	private JLabel label25;
	private CCEnumComboBox<CCFileFormat> cbxFormat;
	private JLabel label26;
	private JYearSpinner spnYear;
	private JLabel label27;
	private CCFileSizeSpinner spnSize;
	private JButton btnRecalculateSize;
	private JLabel label28;
	private CCEnumComboBox<CCUserScore> cbxScore;
	private JLabel label30;
	private JScrollPane scrollPane1;
	private JTextArea memoScoreComment;
	private JLabel label31;
	private TagPanel tagPnl;
	private JPanel pnlRight;
	private JPanel pnlGenres;
	private JLabel label7;
	private CCEnumComboBox<CCGenre> cbxGenre0;
	private JLabel label8;
	private CCEnumComboBox<CCGenre> cbxGenre1;
	private JLabel label9;
	private CCEnumComboBox<CCGenre> cbxGenre2;
	private JLabel label10;
	private CCEnumComboBox<CCGenre> cbxGenre3;
	private JLabel label11;
	private CCEnumComboBox<CCGenre> cbxGenre4;
	private JLabel label12;
	private CCEnumComboBox<CCGenre> cbxGenre5;
	private JLabel label13;
	private CCEnumComboBox<CCGenre> cbxGenre6;
	private JLabel label14;
	private CCEnumComboBox<CCGenre> cbxGenre7;
	private JButton btnParseIMDB;
	private JLabel label32;
	private DateTimeListEditor edViewedHistory;
	private EditCoverControl edCvrControl;
	private JPanel pnlBottom;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
