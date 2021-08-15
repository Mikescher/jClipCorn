package de.jClipCorn.gui.frames.addMovieFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.MovieDataPack;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseOnlineFrame.ParseOnlineDialog;
import de.jClipCorn.gui.guiComponents.*;
import de.jClipCorn.gui.guiComponents.JMediaInfoButton;
import de.jClipCorn.gui.guiComponents.JReadableCCPathTextField;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.jYearSpinner.JYearSpinner;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.parser.FilenameParser;
import de.jClipCorn.util.parser.FilenameParserResult;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddMovieFrame extends JCCFrame implements ParseResultHandler, UserDataProblemHandler
{
	private boolean firstChooseClick = true;

	private final JFileChooser videoFileChooser;

	private CCDateTimeList forceViewedHistory = null;

	private volatile boolean _isDirtyLanguage = false;
	private volatile boolean _isDirtyMediaInfo = false;

	public AddMovieFrame(Component owner, CCMovieList mlist) {
		this(owner, mlist, null);
	}

	public AddMovieFrame(Component owner, CCMovieList mlist, FSPath firstPath)
	{
		super(mlist);
		this.videoFileChooser = new JFileChooser(mlist.getCommonPathForMovieFileChooser().toFile());

		initComponents();
		postInit();

		if (firstPath != null)
		{
			setFilepath(0, firstPath);
			setEnabledAll(true);
			parseFromFile(firstPath);
			firstChooseClick = false;
			updateFilesize();
		}

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());

		initFileChooser();
		setDefaultValues();

		setEnabledAll(false);
		pbLanguageLoad.setVisible(false);
		btnChoose0.setEnabled(true);
	}

	public void parseFromTemp(CCMovie tmpMov, boolean resetAddDate, boolean resetScore) {
		if (!tmpMov.getAddDate().isMinimum() && !resetAddDate)
			spnAddDate.setValue(tmpMov.getAddDate());

		setFilesize(tmpMov.getFilesize());
		setMovieFormat(tmpMov.getFormat());
		setMediaInfo(tmpMov.mediaInfo().get());
		setLength(tmpMov.getLength());

		for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
			if (!tmpMov.Parts.get(i).isEmpty())
				setDirectFilepath(i, tmpMov.Parts.get(i));
		}

		forceViewedHistory = tmpMov.ViewedHistory.get();

		setYear(tmpMov.getYear());
		setZyklus(tmpMov.getZyklus().getTitle());
		setZyklusNumber(tmpMov.getZyklus().getNumber());
		setMovieName(tmpMov.getTitle());
		setOnlineReference(tmpMov.getOnlineReference());
		setGroups(tmpMov.getGroups());
		setScore(tmpMov.getOnlinescore());
		setMovieLanguage(tmpMov.getLanguage());
		setFSK(tmpMov.getFSK());

		if (! resetScore) cbxScore.setSelectedEnum(tmpMov.Score.get());

		for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
			setGenre(i, tmpMov.Genres.get(i));
		}

		setEnabledAll(true);
		firstChooseClick = false;
	}

	private void onBtnOK(boolean check) throws EnumFormatException, EnumValueNotFoundException {
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
			InputErrorDialog amied = new InputErrorDialog(movielist, problems, this, this);
			amied.setVisible(true);
			return;
		}

		CCMovie newM = movielist.createNewEmptyMovie();

		newM.beginUpdating();

		//#####################################################################################

		if (forceViewedHistory != null) newM.setViewedHistoryFromUI(forceViewedHistory);

		newM.Parts.set(0, edPart0.getPath());
		newM.Parts.set(1, edPart1.getPath());
		newM.Parts.set(2, edPart2.getPath());
		newM.Parts.set(3, edPart3.getPath());
		newM.Parts.set(4, edPart4.getPath());
		newM.Parts.set(5, edPart5.getPath());

		newM.Title.set(edTitle.getText());
		newM.Zyklus.setTitle(edZyklus.getText());
		newM.Zyklus.setNumber((int) spnZyklus.getValue());

		newM.MediaInfo.set(ctrlMediaInfo.getValue());
		newM.Language.set(cbxLanguage.getValue());

		newM.Length.set((int) spnLength.getValue());

		newM.AddDate.set(spnAddDate.getValue());

		newM.OnlineScore.set((int) spnOnlineScore.getValue());

		newM.FSK.set(cbxFSK.getSelectedEnum().asFSK());
		newM.Format.set(cbxFormat.getSelectedEnum());

		newM.Year.set(spnYear.getValue());
		newM.FileSize.set((long) spnSize.getValue());

		newM.Genres.set(cbxGenre0.getSelectedEnum(), 0);
		newM.Genres.set(cbxGenre1.getSelectedEnum(), 1);
		newM.Genres.set(cbxGenre2.getSelectedEnum(), 2);
		newM.Genres.set(cbxGenre3.getSelectedEnum(), 3);
		newM.Genres.set(cbxGenre4.getSelectedEnum(), 4);
		newM.Genres.set(cbxGenre5.getSelectedEnum(), 5);
		newM.Genres.set(cbxGenre6.getSelectedEnum(), 6);
		newM.Genres.set(cbxGenre7.getSelectedEnum(), 7);

		newM.Score.set(cbxScore.getSelectedEnum());
		newM.OnlineReference.set(edReference.getValue());
		newM.Groups.set(edGroups.getValue());

		newM.setCover(edCvrControl.getResizedImageForStorage());

		newM.endUpdating();

		dispose();
	}

	private void cancel() {
		this.dispose();
	}

	private void showOnlineParser() {
		(new ParseOnlineDialog(this, movielist, this, CCDBElementTyp.MOVIE)).setVisible(true);
	}

	private void initFileChooser() {
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$

		videoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$
	}

	private void setDefaultValues() {
		cbxScore.setSelectedEnum(CCUserScore.RATING_NO);

		cbxFSK.setSelectedEnum(CCOptionalFSK.NULL);

		spnAddDate.setValue(CCDate.getCurrentDate());
		spnZyklus.setValue(-1);

		edReference.setValue(CCOnlineReferenceList.EMPTY);

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
		cbxLanguage.setReadOnly(!e);
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
		edReference.setEnabled(e);
		edGroups.setEnabled(e);
		btnMediaInfoMain.setEnabled(e);
		btnMediaInfoLang.setEnabled(e);
		btnMediaInfoLen.setEnabled(e);
		ctrlMediaInfo.setEnabled(e);
		btnQueryMediaInfo.setEnabled(e);
	}

	private void onBtnChooseClicked(int cNmbr) {
		int returnval = videoFileChooser.showOpenDialog(this);

		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}

		setFilepath(cNmbr, FSPath.create(videoFileChooser.getSelectedFile()));

		if (firstChooseClick) {
			setEnabledAll(true);
			parseFromFile(FSPath.create(videoFileChooser.getSelectedFile()));

			firstChooseClick = false;

			updateFilesize();
		}
		else
		{
			updateFilesize();

			runMediaInfoInBackground();
		}
	}

	private void parseFromFile(FSPath fp) {
		FilenameParserResult r = FilenameParser.parse(movielist, fp);

		if (r == null) return;

		if (r.Title != null) setMovieName(r.Title);
		if (r.Zyklus != null) setZyklus(r.Zyklus.getTitle());
		if (r.Zyklus != null) setZyklusNumber(r.Zyklus.getNumber());

		if (r.Language != null) setMovieLanguage(r.Language);
		if (r.Format != null) setMovieFormat(r.Format);

		if (r.Groups != null) setGroupList(r.Groups);

		if (r.AdditionalFiles != null)
			for (var addFile : r.AdditionalFiles.entrySet())
				setFilepath(addFile.getKey()-1, addFile.getValue());

		runMediaInfoInBackground();
	}

	private void onBtnClearClicked(int cNmbr) {
		switch (cNmbr) {
			case 0: edPart0.setPath(CCPath.Empty); break; //$NON-NLS-1$
			case 1: edPart1.setPath(CCPath.Empty); break; //$NON-NLS-1$
			case 2: edPart2.setPath(CCPath.Empty); break; //$NON-NLS-1$
			case 3: edPart3.setPath(CCPath.Empty); break; //$NON-NLS-1$
			case 4: edPart4.setPath(CCPath.Empty); break; //$NON-NLS-1$
			case 5: edPart5.setPath(CCPath.Empty); break; //$NON-NLS-1$
		}

		updateFilesize();
	}

	@Override
	public void setMovieFormat(CCFileFormat cmf) {
		cbxFormat.setSelectedEnum(cmf);
	}

	public void setGroupList(CCGroupList gl) {
		edGroups.setValue(gl);
	}

	public void setFilepath(int p, CCPath t) {
		setDirectFilepath(p, t);

		updateFilesize();
	}

	public void setFilepath(int p, FSPath t) {
		setDirectFilepath(p, CCPath.createFromFSPath(t, this));

		updateFilesize();
	}

	private void setDirectFilepath(int p, CCPath pt) {
		switch (p) {
		case 0:
			edPart0.setPath(pt);
			edPart0.setCaretPosition(0);
			break;
		case 1:
			edPart1.setPath(pt);
			edPart1.setCaretPosition(0);
			break;
		case 2:
			edPart2.setPath(pt);
			edPart2.setCaretPosition(0);
			break;
		case 3:
			edPart3.setPath(pt);
			edPart3.setCaretPosition(0);
			break;
		case 4:
			edPart4.setPath(pt);
			edPart4.setCaretPosition(0);
			break;
		case 5:
			edPart5.setPath(pt);
			edPart5.setCaretPosition(0);
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
	public void setFilesize(CCFileSize size) {
		spnSize.setValue(size.getBytes());
	}

	public void setMovieLanguage(CCDBLanguageList lang) {
		cbxLanguage.setValue(lang);
	}

	public void setMediaInfo(CCMediaInfo mi) {
		ctrlMediaInfo.setValue(mi.toPartial());
	}

	@Override
	public void setYear(int y) {
		spnYear.setValue(y);
	}

	@Override
	public void setGenre(int gid, CCGenre movGenre) {
		switch (gid) {
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
		cbxFSK.setSelectedEnum(fsk.asOptionalFSK());
	}

	@Override
	public void setLength(int l) {
		spnLength.setValue(l);
	}

	@Override
	public void setScore(CCOnlineScore s) {
		spnOnlineScore.setValue(s.asInt());
	}

	@Override
	public void onFinishInserting() {
		//
	}

	private void updateByteDisp() {
		lblFileSizeDisp.setText("Byte = " + FileSizeFormatter.format((long) spnSize.getValue())); //$NON-NLS-1$
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

	private void updateFilesize() {
		CCFileSize size = CCFileSize.ZERO;

		if (! edPart0.getPath().isEmpty()) size = CCFileSize.add(size, edPart0.getPath().toFSPath(this).filesize());
		if (! edPart1.getPath().isEmpty()) size = CCFileSize.add(size, edPart1.getPath().toFSPath(this).filesize());
		if (! edPart2.getPath().isEmpty()) size = CCFileSize.add(size, edPart2.getPath().toFSPath(this).filesize());
		if (! edPart3.getPath().isEmpty()) size = CCFileSize.add(size, edPart3.getPath().toFSPath(this).filesize());
		if (! edPart4.getPath().isEmpty()) size = CCFileSize.add(size, edPart4.getPath().toFSPath(this).filesize());
		if (! edPart5.getPath().isEmpty()) size = CCFileSize.add(size, edPart5.getPath().toFSPath(this).filesize());

		setFilesize(size);
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

	private boolean checkUserData(List<UserDataProblem> ret) {
		try
		{
			var mpack = new MovieDataPack
			(
				new CCMovieZyklus(edZyklus.getText(), (int) spnZyklus.getValue()),
				ctrlMediaInfo.getValue(),
				(int) spnLength.getValue(),
				spnAddDate.getValue(),
				cbxFormat.getSelectedEnum(),
				spnYear.getValue(),
				new CCFileSize((long) spnSize.getValue()),
				Arrays.asList(edPart0.getPath(), edPart1.getPath(), edPart2.getPath(), edPart3.getPath(), edPart4.getPath(), edPart5.getPath()),
				CCDateTimeList.createEmpty(),
				cbxLanguage.getValue(),
				edTitle.getText(),
				CCGenreList.create(cbxGenre0.getSelectedEnum(), cbxGenre1.getSelectedEnum(), cbxGenre2.getSelectedEnum(), cbxGenre3.getSelectedEnum(), cbxGenre4.getSelectedEnum(), cbxGenre5.getSelectedEnum(), cbxGenre6.getSelectedEnum(), cbxGenre7.getSelectedEnum()),
				CCOnlineScore.getWrapper().findOrNull((int) spnOnlineScore.getValue()),
				cbxFSK.getSelectedEnum().asFSKOrNull(),
				cbxScore.getSelectedEnum(),
				edReference.getValue(),
				edGroups.getValue(),
				CCTagList.EMPTY,
				edCvrControl.getResizedImageForStorage()
			);

			UserDataProblem.testMovieData(ret, movielist, null, mpack);

			return ret.isEmpty();
		}
		catch (Exception e)
		{
			return false;
		}
	}

	@Override
	public void onAMIEDIgnoreClicked()
	{
		try {
			onBtnOK(false);
		} catch (EnumFormatException | EnumValueNotFoundException e) {
			CCLog.addError(e);
		}
	}

	private void calculateMediaInfoAndSetLength() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<MediaQueryResult> dat = new ArrayList<>();

			var mq = new MediaQueryRunner(movielist);

			if (!edPart0.getPath().isEmpty()) dat.add(mq.query(edPart0.getPath().toFSPath(this), false));
			if (!edPart1.getPath().isEmpty()) dat.add(mq.query(edPart1.getPath().toFSPath(this), false));
			if (!edPart2.getPath().isEmpty()) dat.add(mq.query(edPart2.getPath().toFSPath(this), false));
			if (!edPart3.getPath().isEmpty()) dat.add(mq.query(edPart3.getPath().toFSPath(this), false));
			if (!edPart4.getPath().isEmpty()) dat.add(mq.query(edPart4.getPath().toFSPath(this), false));
			if (!edPart5.getPath().isEmpty()) dat.add(mq.query(edPart5.getPath().toFSPath(this), false));

			if (dat.isEmpty()) {
				lblLenAuto.setText(Str.Empty);
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			lblLenAuto.setText(Str.Empty);

			int dur = (int) (CCStreams.iterate(dat).any(d -> d.Duration == -1) ? -1 : (CCStreams.iterate(dat).sumDouble(d -> d.Duration)/60));
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			setLength(dur);

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void calculateMediaInfoAndSetLanguage() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<MediaQueryResult> dat = new ArrayList<>();

			var mq = new MediaQueryRunner(movielist);

			if (!edPart0.getPath().isEmpty()) dat.add(mq.query(edPart0.getPath().toFSPath(this), false));
			if (!edPart1.getPath().isEmpty()) dat.add(mq.query(edPart1.getPath().toFSPath(this), false));
			if (!edPart2.getPath().isEmpty()) dat.add(mq.query(edPart2.getPath().toFSPath(this), false));
			if (!edPart3.getPath().isEmpty()) dat.add(mq.query(edPart3.getPath().toFSPath(this), false));
			if (!edPart4.getPath().isEmpty()) dat.add(mq.query(edPart4.getPath().toFSPath(this), false));
			if (!edPart5.getPath().isEmpty()) dat.add(mq.query(edPart5.getPath().toFSPath(this), false));

			if (dat.isEmpty()) {
				lblLenAuto.setText(Str.Empty);
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			int dur = (int) (CCStreams.iterate(dat).any(d -> d.Duration == -1) ? -1 : (CCStreams.iterate(dat).sumDouble(d -> d.Duration)/60));
			if (dur != -1) lblLenAuto.setText("("+dur+")"); //$NON-NLS-1$ //$NON-NLS-2$
			if (dur == -1) lblLenAuto.setText(Str.Empty);

			if (CCStreams.iterate(dat).any(d -> d.AudioLanguages == null)) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			CCDBLanguageList dbll = dat.get(0).AudioLanguages;
			for (int i = 1; i < dat.size(); i++) dbll = CCDBLanguageList.intersection(dbll, dat.get(i).AudioLanguages);

			if (dbll.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			} else {
				setMovieLanguage(dbll);
			}

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void calculateAndSetMediaInfo() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			List<MediaQueryResult> dat = new ArrayList<>();

			var mq = new MediaQueryRunner(movielist);

			if (!edPart0.getPath().isEmpty()) dat.add(mq.query(edPart0.getPath().toFSPath(this), true));
			if (!edPart1.getPath().isEmpty()) dat.add(mq.query(edPart1.getPath().toFSPath(this), true));
			if (!edPart2.getPath().isEmpty()) dat.add(mq.query(edPart2.getPath().toFSPath(this), true));
			if (!edPart3.getPath().isEmpty()) dat.add(mq.query(edPart3.getPath().toFSPath(this), true));
			if (!edPart4.getPath().isEmpty()) dat.add(mq.query(edPart4.getPath().toFSPath(this), true));
			if (!edPart5.getPath().isEmpty()) dat.add(mq.query(edPart5.getPath().toFSPath(this), true));

			if (dat.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			}

			int dur = (int) (CCStreams.iterate(dat).any(d -> d.Duration == -1) ? -1 : (CCStreams.iterate(dat).sumDouble(d -> d.Duration)/60));
			if (dur != -1) lblLenAuto.setText("("+dur+")"); //$NON-NLS-1$ //$NON-NLS-2$
			if (dur == -1) lblLenAuto.setText(Str.Empty);

			ctrlMediaInfo.setValue(dat.get(0));

		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void calculateAndShowMediaInfo() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		StringBuilder b = new StringBuilder();

		var mq = new MediaQueryRunner(movielist);

		try {
			if (!edPart0.getPath().isEmpty()) b.append(mq.queryRaw(edPart0.getPath().toFSPath(this))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart1.getPath().isEmpty()) b.append(mq.queryRaw(edPart1.getPath().toFSPath(this))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart2.getPath().isEmpty()) b.append(mq.queryRaw(edPart2.getPath().toFSPath(this))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart3.getPath().isEmpty()) b.append(mq.queryRaw(edPart3.getPath().toFSPath(this))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart4.getPath().isEmpty()) b.append(mq.queryRaw(edPart4.getPath().toFSPath(this))).append("\n\n\n\n\n"); //$NON-NLS-1$
			if (!edPart5.getPath().isEmpty()) b.append(mq.queryRaw(edPart5.getPath().toFSPath(this))).append("\n\n\n\n\n"); //$NON-NLS-1$

			GenericTextDialog.showText(this, getTitle(), b.toString(), false);
		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void runMediaInfoInBackground() {
		var p0 = edPart0.getPath();
		var p1 = edPart1.getPath();
		var p2 = edPart2.getPath();
		var p3 = edPart3.getPath();
		var p4 = edPart4.getPath();
		var p5 = edPart5.getPath();

		_isDirtyLanguage = false;
		_isDirtyMediaInfo = false;

		new Thread(() -> {

			var mq = new MediaQueryRunner(movielist);

			try	{

				SwingUtils.invokeLater(() -> pbLanguageLoad.setVisible(true));

				MediaQueryResult dat0 = null;
				if (!CCPath.isNullOrEmpty(p0)) dat0 = mq.query(p0.toFSPath(this), true);

				final MediaQueryResult _fdat0 = dat0;
				if (dat0 != null && !_isDirtyMediaInfo) SwingUtils.invokeLater(() -> ctrlMediaInfo.setValue(_fdat0));

				List<MediaQueryResult> dat = new ArrayList<>();

				if (!CCPath.isNullOrEmpty(p0)) dat.add(mq.query(p0.toFSPath(this), false));
				if (!CCPath.isNullOrEmpty(p1)) dat.add(mq.query(p1.toFSPath(this), false));
				if (!CCPath.isNullOrEmpty(p2)) dat.add(mq.query(p2.toFSPath(this), false));
				if (!CCPath.isNullOrEmpty(p3)) dat.add(mq.query(p3.toFSPath(this), false));
				if (!CCPath.isNullOrEmpty(p4)) dat.add(mq.query(p4.toFSPath(this), false));
				if (!CCPath.isNullOrEmpty(p5)) dat.add(mq.query(p5.toFSPath(this), false));

				if (dat.isEmpty()) return;

				int dur = (int) (CCStreams.iterate(dat).any(d -> d.Duration == -1) ? -1 : (CCStreams.iterate(dat).sumDouble(d -> d.Duration)/60));
				if (dur != -1) SwingUtils.invokeLater(() -> lblLenAuto.setText("("+dur+")")); //$NON-NLS-1$ //$NON-NLS-2$
				if (dur == -1) SwingUtils.invokeLater(() -> lblLenAuto.setText(Str.Empty));

				if (CCStreams.iterate(dat).any(d -> d.AudioLanguages == null)) return;

				CCDBLanguageList dbll = dat.get(0).AudioLanguages;
				for (int i = 1; i < dat.size(); i++) dbll = CCDBLanguageList.intersection(dbll, dat.get(i).AudioLanguages);

				final CCDBLanguageList dbll2 = dbll;
				if (!dbll.isEmpty() && !_isDirtyLanguage) SwingUtils.invokeLater(() -> setMovieLanguage(dbll2) );

			} catch (Exception e) {

				CCLog.addWarning(e);
				SwingUtils.invokeLater(() ->
				{
					lblLenAuto.setText("<html><font color='red'>!!!</font></html>"); //$NON-NLS-1$
				});

			} finally {

				SwingUtils.invokeLater(() -> pbLanguageLoad.setVisible(false));

			}
		}, "MINFO_QUERY").start(); //$NON-NLS-1$
	}

	private void onChoose0(ActionEvent e) {
		onBtnChooseClicked(0);
	}

	private void onChoose1(ActionEvent e) {
		onBtnChooseClicked(1);
	}

	private void onChoose2(ActionEvent e) {
		onBtnChooseClicked(2);
	}

	private void onChoose3(ActionEvent e) {
		onBtnChooseClicked(3);
	}

	private void onChoose4(ActionEvent e) {
		onBtnChooseClicked(4);
	}

	private void onChoose5(ActionEvent e) {
		onBtnChooseClicked(5);
	}

	private void onClear1(ActionEvent e) {
		onBtnClearClicked(1);
	}

	private void onClear2(ActionEvent e) {
		onBtnClearClicked(2);
	}

	private void onClear3(ActionEvent e) {
		onBtnClearClicked(3);
	}

	private void onClear4(ActionEvent e) {
		onBtnClearClicked(4);
	}

	private void onClear5(ActionEvent e) {
		onBtnClearClicked(5);
	}

	private void onLanguageChanged(PropertyChangeEvent e) {
		_isDirtyLanguage = true;
	}

	private void onMediaInfoChanged() {
		_isDirtyMediaInfo = true;
	}

	private void onOkay() {
		try {
			onBtnOK(true);
		}
		catch (EnumFormatException | EnumValueNotFoundException e1) {
			CCLog.addError(e1);
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
		btnMediaInfoLen = new JMediaInfoButton();
		lblLenAuto = new JLabel();
		label20 = new JLabel();
		cbxLanguage = new LanguageChooser();
		btnMediaInfoLang = new JMediaInfoButton();
		btnQueryMediaInfo = new JButton();
		label21 = new JLabel();
		ctrlMediaInfo = new JMediaInfoControl(movielist, () -> edPart0.getPath().toFSPath(this));
		btnMediaInfoMain = new JMediaInfoButton();
		pbLanguageLoad = new JProgressBar();
		label22 = new JLabel();
		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), CCDate.getMinimumDate(), null);
		label23 = new JLabel();
		spnOnlineScore = new JSpinner();
		label30 = new JLabel();
		label24 = new JLabel();
		cbxFSK = new CCEnumComboBox<CCOptionalFSK>(CCOptionalFSK.getWrapper());
		label25 = new JLabel();
		cbxFormat = new CCEnumComboBox<CCFileFormat>(CCFileFormat.getWrapper());
		label26 = new JLabel();
		spnYear = new JYearSpinner();
		label27 = new JLabel();
		spnSize = new JSpinner();
		lblFileSizeDisp = new JLabel();
		label28 = new JLabel();
		cbxScore = new CCEnumComboBox<CCUserScore>(CCUserScore.getWrapper());
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
		edCvrControl = new EditCoverControl(this, this);
		pnlBottom = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("AddMovieFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 0dlu:grow, 10dlu, 125dlu, $ugap", //$NON-NLS-1$
			"$ugap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

		//======== pnlLeft ========
		{
			pnlLeft.setLayout(new FormLayout(
				"0dlu:grow", //$NON-NLS-1$
				"default, 10dlu, default:grow, default")); //$NON-NLS-1$

			//======== pnlParts ========
			{
				pnlParts.setLayout(new FormLayout(
					"default, $lcgap, 0dlu:grow, 2*($lcgap, default)", //$NON-NLS-1$
					"5*(default, $lgap), default")); //$NON-NLS-1$

				//---- label1 ----
				label1.setText(LocaleBundle.getString("AddMovieFrame.lblPart.text")); //$NON-NLS-1$
				pnlParts.add(label1, CC.xy(1, 1));
				pnlParts.add(edPart0, CC.xywh(3, 1, 3, 1));

				//---- btnChoose0 ----
				btnChoose0.setText("..."); //$NON-NLS-1$
				btnChoose0.addActionListener(e -> onChoose0(e));
				pnlParts.add(btnChoose0, CC.xy(7, 1));

				//---- label2 ----
				label2.setText(LocaleBundle.getString("AddMovieFrame.lblPart_1.text")); //$NON-NLS-1$
				pnlParts.add(label2, CC.xy(1, 3));
				pnlParts.add(edPart1, CC.xy(3, 3));

				//---- btnChoose1 ----
				btnChoose1.setText("..."); //$NON-NLS-1$
				btnChoose1.addActionListener(e -> onChoose1(e));
				pnlParts.add(btnChoose1, CC.xy(5, 3));

				//---- btnClear1 ----
				btnClear1.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear1.addActionListener(e -> onClear1(e));
				pnlParts.add(btnClear1, CC.xy(7, 3));

				//---- label3 ----
				label3.setText(LocaleBundle.getString("AddMovieFrame.lblPart_2.text")); //$NON-NLS-1$
				pnlParts.add(label3, CC.xy(1, 5));
				pnlParts.add(edPart2, CC.xy(3, 5));

				//---- btnChoose2 ----
				btnChoose2.setText("..."); //$NON-NLS-1$
				btnChoose2.addActionListener(e -> onChoose2(e));
				pnlParts.add(btnChoose2, CC.xy(5, 5));

				//---- btnClear2 ----
				btnClear2.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear2.addActionListener(e -> onClear2(e));
				pnlParts.add(btnClear2, CC.xy(7, 5));

				//---- label5 ----
				label5.setText(LocaleBundle.getString("AddMovieFrame.lblPart_3.text")); //$NON-NLS-1$
				pnlParts.add(label5, CC.xy(1, 7));
				pnlParts.add(edPart3, CC.xy(3, 7));

				//---- btnChoose3 ----
				btnChoose3.setText("..."); //$NON-NLS-1$
				btnChoose3.addActionListener(e -> onChoose3(e));
				pnlParts.add(btnChoose3, CC.xy(5, 7));

				//---- btnClear3 ----
				btnClear3.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear3.addActionListener(e -> onClear3(e));
				pnlParts.add(btnClear3, CC.xy(7, 7));

				//---- label6 ----
				label6.setText(LocaleBundle.getString("AddMovieFrame.lblPart_4.text")); //$NON-NLS-1$
				pnlParts.add(label6, CC.xy(1, 9));
				pnlParts.add(edPart4, CC.xy(3, 9));

				//---- btnChoose4 ----
				btnChoose4.setText("..."); //$NON-NLS-1$
				btnChoose4.addActionListener(e -> onChoose4(e));
				pnlParts.add(btnChoose4, CC.xy(5, 9));

				//---- btnClear4 ----
				btnClear4.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear4.addActionListener(e -> onClear4(e));
				pnlParts.add(btnClear4, CC.xy(7, 9));

				//---- label4 ----
				label4.setText(LocaleBundle.getString("AddMovieFrame.lblPart_5.text")); //$NON-NLS-1$
				pnlParts.add(label4, CC.xy(1, 11));
				pnlParts.add(edPart5, CC.xy(3, 11));

				//---- btnChoose5 ----
				btnChoose5.setText("..."); //$NON-NLS-1$
				btnChoose5.addActionListener(e -> onChoose5(e));
				pnlParts.add(btnChoose5, CC.xy(5, 11));

				//---- btnClear5 ----
				btnClear5.setText(LocaleBundle.getString("AddMovieFrame.btnClear.text")); //$NON-NLS-1$
				btnClear5.addActionListener(e -> onClear5(e));
				pnlParts.add(btnClear5, CC.xy(7, 11));
			}
			pnlLeft.add(pnlParts, CC.xy(1, 1, CC.FILL, CC.FILL));

			//======== pnlData ========
			{
				pnlData.setLayout(new FormLayout(
					"default, $lcgap, 0dlu:grow, $lcgap, [40dlu,default], $lcgap, 16dlu, $lcgap, 25dlu, $lcgap, [30dlu,default]", //$NON-NLS-1$
					"13*(default, $lgap), default")); //$NON-NLS-1$

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
				pnlData.add(edGroups, CC.xywh(3, 7, 3, 1, CC.DEFAULT, CC.FILL));

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
				btnMediaInfoLen.setText("text"); //$NON-NLS-1$
				btnMediaInfoLen.addActionListener(e -> calculateMediaInfoAndSetLength());
				pnlData.add(btnMediaInfoLen, CC.xy(7, 9));
				pnlData.add(lblLenAuto, CC.xywh(9, 9, 3, 1, CC.FILL, CC.FILL));

				//---- label20 ----
				label20.setText(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
				pnlData.add(label20, CC.xy(1, 11));

				//---- cbxLanguage ----
				cbxLanguage.addPropertyChangeListener(e -> onLanguageChanged(e));
				pnlData.add(cbxLanguage, CC.xywh(3, 11, 3, 1));

				//---- btnMediaInfoLang ----
				btnMediaInfoLang.addActionListener(e -> calculateMediaInfoAndSetLanguage());
				pnlData.add(btnMediaInfoLang, CC.xy(7, 11));

				//---- btnQueryMediaInfo ----
				btnQueryMediaInfo.setText("..."); //$NON-NLS-1$
				btnQueryMediaInfo.addActionListener(e -> calculateAndShowMediaInfo());
				pnlData.add(btnQueryMediaInfo, CC.xy(9, 11));

				//---- label21 ----
				label21.setText(LocaleBundle.getString("AddMovieFrame.lblMediaInfo")); //$NON-NLS-1$
				pnlData.add(label21, CC.xy(1, 13));

				//---- ctrlMediaInfo ----
				ctrlMediaInfo.addPropertyChangeListener(e -> onMediaInfoChanged());
				pnlData.add(ctrlMediaInfo, CC.xywh(3, 13, 3, 1, CC.DEFAULT, CC.FILL));

				//---- btnMediaInfoMain ----
				btnMediaInfoMain.addActionListener(e -> calculateAndSetMediaInfo());
				pnlData.add(btnMediaInfoMain, CC.xy(7, 13));

				//---- pbLanguageLoad ----
				pbLanguageLoad.setIndeterminate(true);
				pnlData.add(pbLanguageLoad, CC.xy(9, 13));

				//---- label22 ----
				label22.setText(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
				pnlData.add(label22, CC.xy(1, 15));
				pnlData.add(spnAddDate, CC.xywh(3, 15, 3, 1));

				//---- label23 ----
				label23.setText(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
				pnlData.add(label23, CC.xy(1, 17));

				//---- spnOnlineScore ----
				spnOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
				pnlData.add(spnOnlineScore, CC.xywh(3, 17, 3, 1));

				//---- label30 ----
				label30.setText("/ 10"); //$NON-NLS-1$
				pnlData.add(label30, CC.xy(7, 17));

				//---- label24 ----
				label24.setText(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
				pnlData.add(label24, CC.xy(1, 19));
				pnlData.add(cbxFSK, CC.xywh(3, 19, 3, 1));

				//---- label25 ----
				label25.setText(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
				pnlData.add(label25, CC.xy(1, 21));
				pnlData.add(cbxFormat, CC.xywh(3, 21, 3, 1));

				//---- label26 ----
				label26.setText(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
				pnlData.add(label26, CC.xy(1, 23));
				pnlData.add(spnYear, CC.xywh(3, 23, 3, 1));

				//---- label27 ----
				label27.setText(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
				pnlData.add(label27, CC.xy(1, 25));

				//---- spnSize ----
				spnSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
				spnSize.addChangeListener(e -> updateByteDisp());
				pnlData.add(spnSize, CC.xywh(3, 25, 3, 1));

				//---- lblFileSizeDisp ----
				lblFileSizeDisp.setText("Byte = 0"); //$NON-NLS-1$
				pnlData.add(lblFileSizeDisp, CC.xywh(7, 25, 5, 1));

				//---- label28 ----
				label28.setText(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
				pnlData.add(label28, CC.xy(1, 27));
				pnlData.add(cbxScore, CC.xywh(3, 27, 3, 1));
			}
			pnlLeft.add(pnlData, CC.xy(1, 3, CC.FILL, CC.FILL));
		}
		contentPane.add(pnlLeft, CC.xy(2, 2, CC.FILL, CC.FILL));

		//======== pnlRight ========
		{
			pnlRight.setLayout(new FormLayout(
				"default:grow", //$NON-NLS-1$
				"default:grow, 10dlu, default:grow")); //$NON-NLS-1$

			//======== pnlGenres ========
			{
				pnlGenres.setLayout(new FormLayout(
					"default, $lcgap, default:grow", //$NON-NLS-1$
					"8*(default, $lgap), [50dlu,default]")); //$NON-NLS-1$

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
				pnlGenres.add(btnParseIMDB, CC.xywh(1, 17, 3, 1, CC.FILL, CC.FILL));
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
		setSize(750, 700);
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
	private JMediaInfoButton btnMediaInfoLen;
	private JLabel lblLenAuto;
	private JLabel label20;
	private LanguageChooser cbxLanguage;
	private JMediaInfoButton btnMediaInfoLang;
	private JButton btnQueryMediaInfo;
	private JLabel label21;
	private JMediaInfoControl ctrlMediaInfo;
	private JMediaInfoButton btnMediaInfoMain;
	private JProgressBar pbLanguageLoad;
	private JLabel label22;
	private JCCDateSpinner spnAddDate;
	private JLabel label23;
	private JSpinner spnOnlineScore;
	private JLabel label30;
	private JLabel label24;
	private CCEnumComboBox<CCOptionalFSK> cbxFSK;
	private JLabel label25;
	private CCEnumComboBox<CCFileFormat> cbxFormat;
	private JLabel label26;
	private JYearSpinner spnYear;
	private JLabel label27;
	private JSpinner spnSize;
	private JLabel lblFileSizeDisp;
	private JLabel label28;
	private CCEnumComboBox<CCUserScore> cbxScore;
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
	private EditCoverControl edCvrControl;
	private JPanel pnlBottom;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
