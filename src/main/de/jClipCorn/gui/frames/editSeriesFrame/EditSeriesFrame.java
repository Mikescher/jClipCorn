package de.jClipCorn.gui.frames.editSeriesFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.EpisodeDataPack;
import de.jClipCorn.database.databaseElement.datapacks.SeasonDataPack;
import de.jClipCorn.database.databaseElement.datapacks.SeriesDataPack;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.gui.frames.addMultiEpisodesFrame.AddMultiEpisodesFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.batchEditFrame.BatchEditFrame;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseOnlineFrame.ParseOnlineDialog;
import de.jClipCorn.gui.guiComponents.HFixListCellRenderer;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.JReadableCCPathTextField;
import de.jClipCorn.gui.guiComponents.tags.TagPanel;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.iconComponents.CCIcon16Button;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.jYearSpinner.JYearSpinner;
import de.jClipCorn.gui.guiComponents.language.LanguageListChooser;
import de.jClipCorn.gui.guiComponents.language.LanguageSetChooser;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.DirtyUtil;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditSeriesFrame extends JCCFrame
{
	private final JFileChooser videoFileChooser;

	private final CCSeries series;

	private final UpdateCallbackListener listener;

	private boolean _initFinished = false;
	private boolean _isDirtySeries  = false;
	private boolean _isDirtySeason  = false;
	private boolean _isDirtyEpisode = false;

	private boolean _ignoreSeriesDirty    = false;
	private boolean _ignoreSeasonDirty    = false;
	private boolean _ignoreEpisodeDirty   = false;
	private boolean _ignoreUIChangeEvents = false;

	private CCSeason _currentSeasonMemory = null;
	private CCEpisode _currentEpisodeMemory = null;

	public EditSeriesFrame(Component owner, CCSeries ser, UpdateCallbackListener ucl) {
		this(owner, ser, null, null, ucl);
	}

	public EditSeriesFrame(Component owner, CCSeason sea, UpdateCallbackListener ucl) {
		this(owner, sea.getSeries(), sea, null, ucl);
	}

	public EditSeriesFrame(Component owner, CCEpisode ep, UpdateCallbackListener ucl) {
		this(owner, ep.getSeries(), ep.getSeason(), ep, ucl);
	}

	private EditSeriesFrame(Component owner, CCSeries ser, CCSeason sea, CCEpisode ep, UpdateCallbackListener ucl)
	{
		super(ser.getMovieList());
		this.series = ser;
		this.listener = ucl;
		this.videoFileChooser = new JFileChooser(ser.getMovieList().getCommonPathForSeriesFileChooser().toFile());

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		if (sea != null)
		{
			selectSeason(sea);
			if (ep != null) selectEpisode(ep);
		}

		_initFinished = true;
	}

	private void postInit()
	{
		setTitle(LocaleBundle.getFormattedString("EditSeriesFrame.this.title", series.getTitle())); //$NON-NLS-1$

		lsSeasons.setCellRenderer(new HFixListCellRenderer());
		lsEpisodes.setCellRenderer(new HFixListCellRenderer());

		DirtyUtil.initDirtyListenerRecursive(this::setDirtySeries, pnlEditSeries);
		DirtyUtil.initDirtyListenerRecursive(this::setDirtySeason, pnlEditSeason);
		DirtyUtil.initDirtyListenerRecursive(this::setDirtyEpisode, pnlEditEpisode);

		initFileChooser();

		updateSeriesPanel();
	}

	private void selectEpisode(CCEpisode e) {
		if (e == null)
			lsEpisodes.setSelectedIndex(-1); // Calls the Listener
		else
			lsEpisodes.setSelectedIndex(e.getEpisodeIndexInSeason()); // Calls the Listener
	}

	private void selectSeason(CCSeason s) {
		if (s == null)
			lsSeasons.setSelectedIndex(-1); // Calls the Listener
		else
			lsSeasons.setSelectedIndex(s.getSeasonNumber()); // Calls the Listener
	}

	private ParseResultHandler getSeriesCoverControlHandler() {
		return new ParseResultHandler() {

			@Override
			public String getFullTitle() {
				return edSeriesTitle.getText();
			}

			@Override
			public String getTitleForParser() {
				return edSeriesTitle.getText();
			}

			@Override
			public CCOnlineReferenceList getSearchReference() {
				return edSeriesReference.getValue();
			}

			@Override
			public void setMovieFormat(CCFileFormat cmf) {
				// NOP
			}

			@Override
			public void setMovieName(String name) {
				// NOP
			}

			@Override
			public void setZyklus(String mZyklusTitle) {
				// NOP
			}

			@Override
			public void setZyklusNumber(int iRoman) {
				// NOP
			}

			@Override
			public void setFilesize(CCFileSize size) {
				// NOP
			}

			@Override
			public void setYear(int y) {
				// NOP
			}

			@Override
			public void setGenre(int gid, CCGenre movGenre) {
				// NOP
			}

			@Override
			public void setFSK(CCFSK fsk) {
				// NOP
			}

			@Override
			public void setLength(int l) {
				// NOP
			}

			@Override
			public void setScore(CCOnlineScore s) {
				// NOP
			}

			@Override
			public void setCover(BufferedImage nci) {
				setSeriesCover(nci);
			}

			@Override
			public void setOnlineReference(CCOnlineReferenceList ref) {
				edSeriesReference.setValue(ref);
			}

			@Override
			public void onFinishInserting() {
				// nothing
			}
		};
	}

	private ParseResultHandler getSeasonCoverControlHandler() {
		return new ParseResultHandler() {

			@Override
			public String getFullTitle() {
				return edSeriesTitle.getText() + " - " + edSeasonTitle.getText(); //$NON-NLS-1$
			}

			@Override
			public String getTitleForParser() {
				return edSeriesTitle.getText() + " - " + edSeasonTitle.getText(); //$NON-NLS-1$
			}

			@Override
			public CCOnlineReferenceList getSearchReference() {
				return CCOnlineReferenceList.EMPTY;
			}

			@Override
			public void setMovieFormat(CCFileFormat cmf) {
				// NOP
			}

			@Override
			public void setMovieName(String name) {
				// NOP
			}

			@Override
			public void setZyklus(String mZyklusTitle) {
				// NOP
			}

			@Override
			public void setZyklusNumber(int iRoman) {
				// NOP
			}

			@Override
			public void setFilesize(CCFileSize size) {
				// NOP
			}

			@Override
			public void setYear(int y) {
				// NOP
			}

			@Override
			public void setGenre(int gid, CCGenre movGenre) {
				// NOP
			}

			@Override
			public void setFSK(CCFSK fsk) {
				// NOP
			}

			@Override
			public void setLength(int l) {
				// NOP
			}

			@Override
			public void setScore(CCOnlineScore s) {
				// NOP
			}

			@Override
			public void setCover(BufferedImage nci) {
				edSeasonCvrControl.setCover(nci);
			}

			@Override
			public void setOnlineReference(CCOnlineReferenceList ref) {
				// nothing
			}

			@Override
			public void onFinishInserting() {
				// nothing
			}
		};
	}

	private void queryFromOnline() {
		(new ParseOnlineDialog(this, movielist, new ParseResultHandler()
		{
			@Override
			public String getFullTitle() {
				return edSeriesTitle.getText();
			}

			@Override
			public String getTitleForParser() {
				return edSeriesTitle.getText();
			}

			@Override
			public CCOnlineReferenceList getSearchReference() {
				return edSeriesReference.getValue();
			}

			@Override
			public void setMovieFormat(CCFileFormat cmf) {
				// NOP
			}

			@Override
			public void setMovieName(String name) {
				edSeriesTitle.setText(name);
			}

			@Override
			public void setZyklus(String mZyklusTitle) {
				// NOP
			}

			@Override
			public void setZyklusNumber(int iRoman) {
				// NOP
			}

			@Override
			public void setFilesize(CCFileSize size) {
				// NOP
			}

			@Override
			public void setYear(int y) {
				// NOP
			}

			@Override
			public void setGenre(int gid, CCGenre serGenre) {
				switch (gid)
				{
					case 0: cbxSeriesGenre_0.setSelectedEnum(serGenre); break;
					case 1: cbxSeriesGenre_1.setSelectedEnum(serGenre); break;
					case 2: cbxSeriesGenre_2.setSelectedEnum(serGenre); break;
					case 3: cbxSeriesGenre_3.setSelectedEnum(serGenre); break;
					case 4: cbxSeriesGenre_4.setSelectedEnum(serGenre); break;
					case 5: cbxSeriesGenre_5.setSelectedEnum(serGenre); break;
					case 6: cbxSeriesGenre_6.setSelectedEnum(serGenre); break;
					case 7: cbxSeriesGenre_7.setSelectedEnum(serGenre); break;
				}
			}

			@Override
			public void setFSK(CCFSK fsk) {
				cbxSeriesFSK.setSelectedEnum(fsk);
			}

			@Override
			public void setLength(int l) {
				// NOP
			}

			@Override
			public void setScore(CCOnlineScore s) {
				spnSeriesOnlineScore.setValue(s.asInt());
			}

			@Override
			public void setCover(BufferedImage nci) {
				setSeriesCover(nci);
			}

			@Override
			public void setOnlineReference(CCOnlineReferenceList ref) {
				edSeriesReference.setValue(ref);
			}

			@Override
			public void onFinishInserting() {
				// nothing
			}
		}, CCDBElementTyp.SERIES)).setVisible(true);
	}

	private CCSeason getSelectedSeason() {
		int sel = lsSeasons.getSelectedIndex();

		if (sel < 0) {
			return null;
		} else {
			return series.getSeasonByArrayIndex(sel);
		}
	}

	private List<CCSeason> getSelectedSeasons() {
		List<CCSeason> result = new ArrayList<>();

		for (int idx : lsSeasons.getSelectedIndices()) {
			result.add(series.getSeasonByArrayIndex(idx));

		}

		return result;
	}

	private CCEpisode getSelectedEpisode() {
		CCSeason season = getSelectedSeason();

		if (season == null) {
			return null;
		}

		int sel = lsEpisodes.getSelectedIndex();

		if (sel < 0) {
			return null;
		} else {
			return season.getEpisodeByArrayIndex(sel);
		}
	}

	private List<CCEpisode> getSelectedEpisodes() {
		CCSeason season = getSelectedSeason();

		if (season == null) {
			return new ArrayList<>();
		}

		List<CCEpisode> result = new ArrayList<>();

		for (int idx : lsEpisodes.getSelectedIndices()) {
			result.add(season.getEpisodeByArrayIndex(idx));
		}

		return result;
	}

	private void updateSeriesPanel() {
		if (_ignoreUIChangeEvents) return;

		_ignoreSeriesDirty = true;
		_ignoreUIChangeEvents = true;
		{
			edSeriesCvrControl.setCover(series.getCover());

			edSeriesTitle.setText(series.getTitle());
			spnSeriesOnlineScore.setValue(series.getOnlinescore().asInt());
			cbxSeriesFSK.setSelectedEnum(series.getFSK());
			cbxSeriesScore.setSelectedEnum(series.Score.get());

			cbxSeriesGenre_0.setSelectedEnum(series.Genres.get().getGenre(0));
			cbxSeriesGenre_1.setSelectedEnum(series.Genres.get().getGenre(1));
			cbxSeriesGenre_2.setSelectedEnum(series.Genres.get().getGenre(2));
			cbxSeriesGenre_3.setSelectedEnum(series.Genres.get().getGenre(3));
			cbxSeriesGenre_4.setSelectedEnum(series.Genres.get().getGenre(4));
			cbxSeriesGenre_5.setSelectedEnum(series.Genres.get().getGenre(5));
			cbxSeriesGenre_6.setSelectedEnum(series.Genres.get().getGenre(6));
			cbxSeriesGenre_7.setSelectedEnum(series.Genres.get().getGenre(7));

			edSeriesGroups.setValue(series.getGroups());

			edSeriesReference.setValue(series.getOnlineReference());

			edSeriesTags.setValue(series.getTags());

			lsSeasons.setSelectedIndex(-1);
			DefaultListModel<String> ml;
			lsSeasons.setModel(ml = new DefaultListModel<>());
			for (int i = 0; i < series.getSeasonCount(); i++) {
				ml.addElement(series.getSeasonByArrayIndex(i).getTitle());
			}

		}
		_ignoreSeriesDirty = false;
		_ignoreUIChangeEvents = false;
		_isDirtySeries = false;

		updateTitle();

		updateSeasonPanel(false);
	}

	private void updateSeasonPanel(boolean check) {
		if (_ignoreUIChangeEvents) return;

		if (check)
		{
			if (_currentSeasonMemory != null && _currentEpisodeMemory != null && _isDirtyEpisode)
			{
				if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) //$NON-NLS-1$
				{
					_ignoreUIChangeEvents = true;
					selectSeason(_currentSeasonMemory);
					selectEpisode(_currentEpisodeMemory);
					_ignoreUIChangeEvents = false;
					return;
				}
			}

			if (_currentSeasonMemory != null && _isDirtySeason)
			{
				if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeSeasonButDirty")) //$NON-NLS-1$
				{
					_ignoreUIChangeEvents = true;
					selectSeason(_currentSeasonMemory);
					selectEpisode(_currentEpisodeMemory);
					_ignoreUIChangeEvents = false;
					return;
				}
			}
		}

		CCSeason season = getSelectedSeason();

		_ignoreSeasonDirty = true;
		_ignoreUIChangeEvents = true;
		{
			_currentSeasonMemory = season;

			if (season == null) {
				pnlEditSeason.setVisible(false);
			} else {
				pnlEditSeason.setVisible(true);

				edSeasonCvrControl.setCover(season.getCover());
				edSeasonTitle.setText(season.getTitle());
				spnSeasonYear.setValue(season.getYear());

				lsEpisodes.setSelectedIndex(-1);
				DefaultListModel<String> ml;
				lsEpisodes.setModel(ml = new DefaultListModel<>());
				for (int i = 0; i < season.getEpisodeCount(); i++) {
					ml.addElement(season.getEpisodeByArrayIndex(i).getTitle());
				}
			}
		}
		_ignoreUIChangeEvents = false;
		_ignoreSeasonDirty = false;
		_isDirtySeason = false;

		updateTitle();

		updateEpisodePanel(false);
	}

	private void updateEpisodePanel(boolean check) {
		if (_ignoreUIChangeEvents) return;

		if (check)
		{
			if (_currentEpisodeMemory != null && _isDirtyEpisode)
			{
				if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) //$NON-NLS-1$
				{
					_ignoreUIChangeEvents = true;
					selectSeason(_currentSeasonMemory);
					selectEpisode(_currentEpisodeMemory);
					_ignoreUIChangeEvents = false;
					return;
				}
			}

		}

		CCEpisode episode = getSelectedEpisode();

		_ignoreEpisodeDirty = true;
		{
			_currentEpisodeMemory = episode;

			if (episode == null) {
				pnlEditEpisode.setVisible(false);
				return;
			} else {
				pnlEditEpisode.setVisible(true);
			}

			edEpisodeTitle.setText(episode.Title.get());
			spnEpisodeEpisode.setValue(episode.EpisodeNumber.get());
			cbxEpisodeFormat.setSelectedEnum(episode.Format.get());
			spnEpisodeLength.setValue(episode.Length.get());
			spnEpisodeSize.setValue(episode.FileSize.get().getBytes());
			spnEpisodeAdded.setValue(episode.AddDate.get());
			ctrlEpisodeHistory.setValue(episode.ViewedHistory.get());
			edEpisodePart.setPath(episode.Part.get());
			edEpisodeTags.setValue(episode.Tags.get());
			ctrlEpisodeLanguage.setValue(episode.Language.get());
			ctrlEpisodeSubtitles.setValue(episode.Subtitles.get());
			ctrlEpisodeMediaInfo.setValue(episode.MediaInfo.getPartial());

			updateEpisodesFilesizeDisplay();
			testEpisodePart();
		}
		_ignoreEpisodeDirty = false;
		_isDirtyEpisode = false;

		updateTitle();
	}

	private void initFileChooser() {
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$

		videoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$
	}

	private void updateEpisodesFilesizeDisplay() {
		lblEpisodeFilesizePreview.setText("= " + FileSizeFormatter.format((long) spnEpisodeSize.getValue()));
	}

	private void setSeriesCover(BufferedImage nci) {
		edSeriesCvrControl.setCover(nci);
	}

	private void addEmptySeason() {
		series.createNewEmptySeason().Title.set("<untitled>"); //$NON-NLS-1$

		updateSeriesPanel();
	}

	private void addSeason() {
		(new AddSeasonFrame(this, series, o -> updateSeriesPanel())).setVisible(true);
	}

	private void removeSeason() {
		CCSeason season = getSelectedSeason();

		if (season == null) {
			return;
		}

		if (DialogHelper.showYesNoDlg(this, LocaleBundle.getString("EditSeriesFrame.dlgDeleteSeason.caption"), LocaleBundle.getString("EditSeriesFrame.dlgDeleteSeason.text"))) {  //$NON-NLS-1$//$NON-NLS-2$
			series.deleteSeason(season);
		}

		updateSeriesPanel();
	}

	private boolean onOKSeries(boolean check) {
		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserDataSeries(problems);

		// some problems are too fatal
		if (probvalue && ! edSeriesCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			probvalue = false;
		}
		if (probvalue && edSeriesTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}

		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(movielist, problems, () -> {
				onOKSeries(false);
			}, this) ;
			amied.setVisible(true);
			return false;
		}

		series.beginUpdating();

		//#####################################################################################

		series.setCover(edSeriesCvrControl.getResizedImageForStorage());
		series.Title.set(edSeriesTitle.getText());
		series.OnlineScore.set((int) spnSeriesOnlineScore.getValue());
		series.FSK.set(cbxSeriesFSK.getSelectedEnum());
		series.Score.set(cbxSeriesScore.getSelectedEnum());

		series.Genres.set(cbxSeriesGenre_0.getSelectedEnum(), 0);
		series.Genres.set(cbxSeriesGenre_1.getSelectedEnum(), 1);
		series.Genres.set(cbxSeriesGenre_2.getSelectedEnum(), 2);
		series.Genres.set(cbxSeriesGenre_3.getSelectedEnum(), 3);
		series.Genres.set(cbxSeriesGenre_4.getSelectedEnum(), 4);
		series.Genres.set(cbxSeriesGenre_5.getSelectedEnum(), 5);
		series.Genres.set(cbxSeriesGenre_6.getSelectedEnum(), 6);
		series.Genres.set(cbxSeriesGenre_7.getSelectedEnum(), 7);

		series.OnlineReference.set(edSeriesReference.getValue());
		series.Groups.set(edSeriesGroups.getValue());

		series.Tags.set(edSeriesTags.getValue());

		//#####################################################################################

		series.endUpdating();

		updateSeriesPanel();

		return true;
	}

	private boolean checkUserDataSeries(List<UserDataProblem> ret)
	{
		var spack = new SeriesDataPack
		(
			edSeriesTitle.getText(),
			CCGenreList.create(cbxSeriesGenre_0.getSelectedEnum(), cbxSeriesGenre_1.getSelectedEnum(), cbxSeriesGenre_2.getSelectedEnum(), cbxSeriesGenre_3.getSelectedEnum(), cbxSeriesGenre_4.getSelectedEnum(), cbxSeriesGenre_5.getSelectedEnum(), cbxSeriesGenre_6.getSelectedEnum(), cbxSeriesGenre_7.getSelectedEnum()),
			CCOnlineScore.getWrapper().findOrNull((int) spnSeriesOnlineScore.getValue()),
			cbxSeriesFSK.getSelectedEnum(),
			cbxSeriesScore.getSelectedEnum(),
			edSeriesReference.getValue(),
			edSeriesGroups.getValue(),
			edSeriesTags.getValue(),
			edSeriesCvrControl.getResizedImageForStorage()
		);

		UserDataProblem.testSeriesData(ret, series.getMovieList(), series, spack);

		return ret.isEmpty();
	}

	private void addEmptyEpisode() {

		CCSeason season = getSelectedSeason();

		if (season == null) return;

		if (_currentEpisodeMemory != null && _isDirtyEpisode) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) return; //$NON-NLS-1$
		if (_isDirtySeason) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeSeasonButDirty")) return; //$NON-NLS-1$

		CCEpisode newEp = season.createNewEmptyEpisode();

		newEp.Title.set("<untitled>"); //$NON-NLS-1$
		newEp.AddDate.set(CCDate.getCurrentDate());
		newEp.EpisodeNumber.set(season.getNextEpisodeNumber());
		Integer commonLen = season.getCommonEpisodeLength();
		if (commonLen == null) commonLen = season.getConsensEpisodeLength();
		if (commonLen != null) newEp.Length.set(commonLen);

		updateSeasonPanel(false);
	}

	private void batchEditEpisodes() {
		CCSeason season = getSelectedSeason();

		if (season == null) return;

		if (_currentEpisodeMemory != null && _isDirtyEpisode) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) return; //$NON-NLS-1$
		if (_isDirtySeason) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeSeasonButDirty")) return; //$NON-NLS-1$

		(new BatchEditFrame(this, season, o -> updateSeasonPanel(false))).setVisible(true);
	}

	private void batchEditSelectedEpisodes() {
		CCSeason season = getSelectedSeason();

		if (season == null) return;

		if (_currentEpisodeMemory != null && _isDirtyEpisode) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) return; //$NON-NLS-1$
		if (_isDirtySeason) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeSeasonButDirty")) return; //$NON-NLS-1$

		List<CCEpisode> selection = getSelectedEpisodes();
		if (selection.size() == 0) return;

		(new BatchEditFrame(this, season, selection, o -> updateSeasonPanel(false))).setVisible(true);
	}

	private void batchEditSelectedSeasons() {
		var season = getSelectedSeasons();

		if (_currentEpisodeMemory != null && _isDirtyEpisode) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) return; //$NON-NLS-1$
		if (_isDirtySeason) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeSeasonButDirty")) return; //$NON-NLS-1$

		List<CCEpisode> selection = CCStreams.iterate(season).map(CCSeason::getEpisodeList).flatten(CCStreams::iterate).toList();
		if (selection.size() == 0) return;

		(new BatchEditFrame(this, series, selection, o -> updateSeasonPanel(false))).setVisible(true);
	}

	private void multiAddEpisodes() {
		CCSeason season = getSelectedSeason();

		if (season == null) return;

		if (_currentEpisodeMemory != null && _isDirtyEpisode) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) return; //$NON-NLS-1$
		if (_isDirtySeason) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeSeasonButDirty")) return; //$NON-NLS-1$

		(new AddMultiEpisodesFrame(this, season, o -> updateSeasonPanel(false))).setVisible(true);
	}

	private void removeEpisode() {
		CCSeason season = getSelectedSeason();
		if (season == null) return;

		if (_currentEpisodeMemory != null && _isDirtyEpisode) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) return; //$NON-NLS-1$
		if (_isDirtySeason) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeSeasonButDirty")) return; //$NON-NLS-1$

		if (getSelectedEpisodes().size() <= 1) {
			CCEpisode episode = getSelectedEpisode();

			if (episode == null) {
				return;
			}

			if (DialogHelper.showYesNoDlg(this, LocaleBundle.getString("EditSeriesFrame.dlgDeleteEpisode.caption"), LocaleBundle.getString("EditSeriesFrame.dlgDeleteEpisode.text"))) {  //$NON-NLS-1$//$NON-NLS-2$
				season.deleteEpisode(episode);
			}
		} else {
			List<CCEpisode> listEpisodes = getSelectedEpisodes();

			if (DialogHelper.showYesNoDlg(this, LocaleBundle.getString("EditSeriesFrame.dlgDeleteMultipleEpisode.caption"), LocaleBundle.getFormattedString("EditSeriesFrame.dlgDeleteMultipleEpisode.text", listEpisodes.size()))) {  //$NON-NLS-1$//$NON-NLS-2$
				for (CCEpisode ep : listEpisodes) {
					season.deleteEpisode(ep);
				}
			}
		}

		updateSeasonPanel(false);
	}

	private void onOKSeason(boolean check) {
		CCSeason season = getSelectedSeason();

		if (season == null) return;

		if (_currentEpisodeMemory != null && _isDirtyEpisode) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeEpisodeButDirty")) return; //$NON-NLS-1$

		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserDataSeason(problems);

		// some problems are too fatal
		if (probvalue && ! edSeasonCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			probvalue = false;
		}
		if (probvalue && edSeasonTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}

		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(movielist, problems, () -> onOKSeason(false), this);
			amied.setVisible(true);
			return;
		}

		String prevTitle = season.getTitle();

		season.beginUpdating();

		//#####################################################################################

		season.setCover(edSeasonCvrControl.getResizedImageForStorage());
		season.Title.set(edSeasonTitle.getText());
		season.Year.set(spnSeasonYear.getValue());

		//#####################################################################################

		season.endUpdating();

		updateSeasonPanel(false);

		if (! prevTitle.equals(season.getTitle())) {
			updateSeriesPanel();
			selectSeason(season);
		}
	}

	private boolean checkUserDataSeason(List<UserDataProblem> ret)
	{
		var spack = new SeasonDataPack
		(
			edSeasonTitle.getText(),
			spnSeasonYear.getValue(),
			edSeasonCvrControl.getResizedImageForStorage()
		);

		UserDataProblem.testSeasonData(ret, series.getMovieList(), getSelectedSeason(), spack);

		return ret.isEmpty();
	}

	private void testEpisodePart() {
		if (edEpisodePart.getPath().toFSPath(this).exists()) {
			edEpisodePart.setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		} else {
			edEpisodePart.setBackground(Color.RED);
		}
	}

	private void recalcEpisodeFilesize() {
		spnEpisodeSize.setValue(edEpisodePart.getPath().toFSPath(this).filesize().getBytes());
	}

	private void recalcEpisodeFormat() {
		CCFileFormat fmt = CCFileFormat.getMovieFormatFromPath(edEpisodePart.getPath().toFSPath(this));

		cbxEpisodeFormat.setSelectedEnum(fmt);
	}

	private void openEpisodePart() {
		int returnval = videoFileChooser.showOpenDialog(this);

		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}

		var path = CCPath.createFromFSPath(FSPath.create(videoFileChooser.getSelectedFile()), this);
		edEpisodePart.setPath(path);

		recalcEpisodeFilesize();
		recalcEpisodeFormat();

		testEpisodePart();
	}

	private void onOKEpisode(boolean check) {
		CCEpisode episode = getSelectedEpisode();

		if (episode == null) return;

		if (_isDirtySeason) if (!DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.ChangeSeasonButDirty")) return; //$NON-NLS-1$

		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserDataEpisode(problems);

		// some problems are too fatal
		if (probvalue && edEpisodeTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}

		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(movielist, problems, () -> onOKEpisode(false), this) ;
			amied.setVisible(true);
			return;
		}

		String prevTitle = episode.getTitle();

		episode.beginUpdating();

		//#####################################################################################

		episode.Title.set(edEpisodeTitle.getText());
		episode.EpisodeNumber.set((int) spnEpisodeEpisode.getValue());
		episode.Format.set(cbxEpisodeFormat.getSelectedEnum());
		episode.MediaInfo.set(ctrlEpisodeMediaInfo.getValue());
		episode.Length.set((int) spnEpisodeLength.getValue());
		episode.FileSize.set((long) spnEpisodeSize.getValue());
		episode.AddDate.set(spnEpisodeAdded.getValue());
		episode.ViewedHistory.set(ctrlEpisodeHistory.getValue());
		episode.Part.set(edEpisodePart.getPath());
		episode.Tags.set(edEpisodeTags.getValue());
		episode.Language.set(ctrlEpisodeLanguage.getValue());
		episode.Subtitles.set(ctrlEpisodeSubtitles.getValue());

		//#####################################################################################

		episode.endUpdating();

		updateEpisodePanel(false);

		if (! prevTitle.equals(episode.getTitle())) {
			updateSeasonPanel(false);
			selectEpisode(episode);
		}
	}

	private boolean checkUserDataEpisode(List<UserDataProblem> ret) {
		CCSeason season = getSelectedSeason();
		CCEpisode episode = getSelectedEpisode();

		var epack = new EpisodeDataPack
		(
			(int) spnEpisodeEpisode.getValue(),
			edEpisodeTitle.getText(),
			(int) spnEpisodeLength.getValue(),
			cbxEpisodeFormat.getSelectedEnum(),
			new CCFileSize((long) spnEpisodeSize.getValue()),
			edEpisodePart.getPath(),
			spnEpisodeAdded.getValue(),
			ctrlEpisodeHistory.getValue(),
			edEpisodeTags.getValue(),
			ctrlEpisodeLanguage.getValue(),
			ctrlEpisodeSubtitles.getValue(),
			ctrlEpisodeMediaInfo.getValue()
		);

		UserDataProblem.testEpisodeData(ret, series.getMovieList(), season, episode, epack);

		return ret.isEmpty();
	}

	private void onEditAllSeasonsInBatch() {
		(new BatchEditFrame(this, series, o -> updateSeriesPanel())).setVisible(true);
	}

	private void onSeriesOkay() {
		onOKSeries(true);
	}

	private void onSeriesOkayAndClose() {
		if (onOKSeries(true)) EditSeriesFrame.this.dispatchEvent(new WindowEvent(EditSeriesFrame.this, WindowEvent.WINDOW_CLOSING));
	}

	private void onSeasonOkay() {
		onOKSeason(true);
	}

	private void onEpisodeOkay() {
		onOKEpisode(true);
	}

	private void onSetEpisodeAddedToToday() {
		spnEpisodeAdded.setValue(CCDate.getCurrentDate());
	}

	private void onWindowClosing() {
		if (_isDirtyEpisode || _isDirtySeason || _isDirtySeries) {
			if (DialogHelper.showLocaleYesNoDefaultNo(EditSeriesFrame.this, "Dialogs.CloseButDirty")) EditSeriesFrame.this.dispose(); //$NON-NLS-1$
		} else {
			EditSeriesFrame.this.dispose();
		}
	}

	private void parseCodecMetadata_Lang() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edEpisodePart.getPath().toFSPath(this), false);

			if (dat.AudioLanguages == null) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			CCDBLanguageSet dbll = dat.AudioLanguages;

			if (dbll.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
				return;
			} else {
				ctrlEpisodeLanguage.setValue(dbll);
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
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edEpisodePart.getPath().toFSPath(this), false);

			if (dat.SubtitleLanguages == null) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			ctrlEpisodeSubtitles.setValue(dat.SubtitleLanguages);

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
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edEpisodePart.getPath().toFSPath(this), true);

			int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			spnEpisodeLength.setValue(dur);

		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void parseCodecMetadata_MI() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edEpisodePart.getPath().toFSPath(this), true);

			ctrlEpisodeMediaInfo.setValue(dat);

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
			String dat = new MediaQueryRunner(movielist).queryRaw(edEpisodePart.getPath().toFSPath(this));

			GenericTextDialog.showText(this, getTitle(), dat, false);
		} catch (IOException | MediaQueryException e) {
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void setDirtySeries() {
		if (!_initFinished) return;
		if (_ignoreSeriesDirty) return;

		_isDirtySeries = true;
		updateTitle();
	}

	private void setDirtySeason() {
		if (!_initFinished) return;
		if (_ignoreSeasonDirty) return;

		_isDirtySeason = true;
		updateTitle();
	}

	private void setDirtyEpisode() {
		if (!_initFinished) return;
		if (_ignoreEpisodeDirty) return;

		_isDirtyEpisode = true;
		updateTitle();
	}

	private void updateTitle() {
		String str = LocaleBundle.getFormattedString("EditSeriesFrame.this.title", series.getTitle()); //$NON-NLS-1$
		str += " ["; //$NON-NLS-1$
		str += _isDirtySeries  ? "*" : "_";  //$NON-NLS-1$//$NON-NLS-2$
		str += _isDirtySeason  ? "*" : "_";  //$NON-NLS-1$//$NON-NLS-2$
		str += _isDirtyEpisode ? "*" : "_";  //$NON-NLS-1$//$NON-NLS-2$
		str += "]"; //$NON-NLS-1$
		setTitle(str);
	}

	private void onSeasonSelected() {
		updateSeasonPanel(true);
	}

	private void onEpisodeSelected() {
		updateEpisodePanel(true);
	}

	private void onWindowClosed() {
		if (listener != null) {
			listener.onUpdate(series);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlEditSeries = new JPanel();
		panel3 = new JPanel();
		edSeriesCvrControl = new EditCoverControl(this, getSeriesCoverControlHandler());
		label1 = new JLabel();
		cbxSeriesGenre_0 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label2 = new JLabel();
		cbxSeriesGenre_1 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label3 = new JLabel();
		cbxSeriesGenre_2 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label4 = new JLabel();
		cbxSeriesGenre_3 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label5 = new JLabel();
		cbxSeriesGenre_4 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label6 = new JLabel();
		cbxSeriesGenre_5 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label7 = new JLabel();
		cbxSeriesGenre_6 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		label8 = new JLabel();
		cbxSeriesGenre_7 = new CCEnumComboBox<CCGenre>(CCGenre.getWrapper());
		panel5 = new JPanel();
		label9 = new JLabel();
		panel6 = new JPanel();
		edSeriesTitle = new JTextField();
		btnParseOnline = new JButton();
		label10 = new JLabel();
		panel7 = new JPanel();
		spnSeriesOnlineScore = new JSpinner();
		label16 = new JLabel();
		label11 = new JLabel();
		cbxSeriesFSK = new CCEnumComboBox<CCFSK>(CCFSK.getWrapper());
		label12 = new JLabel();
		cbxSeriesScore = new CCEnumComboBox<CCUserScore>(CCUserScore.getWrapper());
		label13 = new JLabel();
		edSeriesReference = new JReferenceChooser(movielist);
		label14 = new JLabel();
		edSeriesGroups = new GroupListEditor(movielist);
		label15 = new JLabel();
		edSeriesTags = new TagPanel();
		panel8 = new JPanel();
		scrollPane1 = new JScrollPane();
		lsSeasons = new JList<>();
		btnAddEmptySeason = new JButton();
		btnAddSeason = new JButton();
		btnEditSerinBatch = new JButton();
		btnEditSelSerInBatch = new JButton();
		btnRemoveSeason = new JButton();
		panel4 = new JPanel();
		btnSeriesOk = new JButton();
		btnOkClose = new JButton();
		pnlEditSeason = new JPanel();
		edSeasonCvrControl = new EditCoverControl(this, getSeasonCoverControlHandler());
		label17 = new JLabel();
		edSeasonTitle = new JTextField();
		label18 = new JLabel();
		spnSeasonYear = new JYearSpinner();
		scrollPane2 = new JScrollPane();
		lsEpisodes = new JList<>();
		btnAddEpisode = new JButton();
		btnAddMultipleEpisodes = new JButton();
		btnEditAllEpisodesInBatch = new JButton();
		btnEditSelectedEpisodesInBatch = new JButton();
		btnRemoveEpisodes = new JButton();
		panel9 = new JPanel();
		button12 = new JButton();
		pnlEditEpisode = new JPanel();
		label19 = new JLabel();
		edEpisodeTitle = new JTextField();
		label20 = new JLabel();
		spnEpisodeEpisode = new JSpinner();
		label21 = new JLabel();
		cbxEpisodeFormat = new CCEnumComboBox<CCFileFormat>(CCFileFormat.getWrapper());
		label22 = new JLabel();
		ctrlEpisodeMediaInfo = new JMediaInfoControl(movielist, () -> edEpisodePart.getPath().toFSPath(this));
		btnEpisodeMediaInfoMain = new CCIcon16Button();
		label23 = new JLabel();
		ctrlEpisodeLanguage = new LanguageSetChooser();
		btnEpisodeMediaInfoLang = new CCIcon16Button();
		btnEpisodeMediaInfoShow = new JButton();
		label27 = new JLabel();
		ctrlEpisodeSubtitles = new LanguageListChooser();
		btnEpisodeMediaInfoSubs = new CCIcon16Button();
		label24 = new JLabel();
		spnEpisodeLength = new JSpinner();
		label26 = new JLabel();
		btnEpisodeMediaInfoLength = new CCIcon16Button();
		label25 = new JLabel();
		spnEpisodeSize = new JSpinner();
		lblEpisodeFilesizePreview = new JLabel();
		button14 = new JButton();
		label28 = new JLabel();
		spnEpisodeAdded = new JCCDateSpinner();
		button15 = new JButton();
		label29 = new JLabel();
		edEpisodePart = new JReadableCCPathTextField();
		button16 = new JButton();
		label30 = new JLabel();
		edEpisodeTags = new TagPanel();
		label31 = new JLabel();
		ctrlEpisodeHistory = new DateTimeListEditor();
		panel10 = new JPanel();
		button17 = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("EditSeriesFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(900, 725));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onWindowClosed();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				onWindowClosing();
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"3*($ugap, 0dlu:grow), $ugap", //$NON-NLS-1$
			"$ugap, default:grow, $ugap")); //$NON-NLS-1$

		//======== pnlEditSeries ========
		{
			pnlEditSeries.setBorder(LineBorder.createBlackLineBorder());
			pnlEditSeries.setLayout(new FormLayout(
				"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
				"$lgap, default, $lgap, pref, $lgap, default:grow, $lgap, default, $lgap")); //$NON-NLS-1$

			//======== panel3 ========
			{
				panel3.setLayout(new FormLayout(
					"default, 12dlu, default, $lcgap, 0dlu:grow", //$NON-NLS-1$
					"8*(default, $lgap), default:grow")); //$NON-NLS-1$
				panel3.add(edSeriesCvrControl, CC.xywh(1, 1, 1, 17));

				//---- label1 ----
				label1.setText(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
				panel3.add(label1, CC.xy(3, 1));
				panel3.add(cbxSeriesGenre_0, CC.xy(5, 1));

				//---- label2 ----
				label2.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
				panel3.add(label2, CC.xy(3, 3));
				panel3.add(cbxSeriesGenre_1, CC.xy(5, 3));

				//---- label3 ----
				label3.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
				panel3.add(label3, CC.xy(3, 5));
				panel3.add(cbxSeriesGenre_2, CC.xy(5, 5));

				//---- label4 ----
				label4.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
				panel3.add(label4, CC.xy(3, 7));
				panel3.add(cbxSeriesGenre_3, CC.xy(5, 7));

				//---- label5 ----
				label5.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
				panel3.add(label5, CC.xy(3, 9));
				panel3.add(cbxSeriesGenre_4, CC.xy(5, 9));

				//---- label6 ----
				label6.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
				panel3.add(label6, CC.xy(3, 11));
				panel3.add(cbxSeriesGenre_5, CC.xy(5, 11));

				//---- label7 ----
				label7.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
				panel3.add(label7, CC.xy(3, 13));
				panel3.add(cbxSeriesGenre_6, CC.xy(5, 13));

				//---- label8 ----
				label8.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
				panel3.add(label8, CC.xy(3, 15));
				panel3.add(cbxSeriesGenre_7, CC.xy(5, 15));
			}
			pnlEditSeries.add(panel3, CC.xy(2, 2, CC.FILL, CC.FILL));

			//======== panel5 ========
			{
				panel5.setLayout(new FormLayout(
					"default, $lcgap, 0dlu:grow", //$NON-NLS-1$
					"14dlu, 6*($lgap, pref)")); //$NON-NLS-1$

				//---- label9 ----
				label9.setText(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
				panel5.add(label9, CC.xy(1, 1));

				//======== panel6 ========
				{
					panel6.setLayout(new FormLayout(
						"default:grow, $lcgap, default", //$NON-NLS-1$
						"14dlu")); //$NON-NLS-1$
					panel6.add(edSeriesTitle, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

					//---- btnParseOnline ----
					btnParseOnline.setText(LocaleBundle.getString("EditSeriesFrame.btnOnline")); //$NON-NLS-1$
					btnParseOnline.addActionListener(e -> queryFromOnline());
					panel6.add(btnParseOnline, CC.xy(3, 1));
				}
				panel5.add(panel6, CC.xy(3, 1));

				//---- label10 ----
				label10.setText(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
				panel5.add(label10, CC.xy(1, 3));

				//======== panel7 ========
				{
					panel7.setLayout(new FormLayout(
						"default:grow, $lcgap, default", //$NON-NLS-1$
						"default")); //$NON-NLS-1$

					//---- spnSeriesOnlineScore ----
					spnSeriesOnlineScore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
					panel7.add(spnSeriesOnlineScore, CC.xy(1, 1));

					//---- label16 ----
					label16.setText(" / 10"); //$NON-NLS-1$
					panel7.add(label16, CC.xy(3, 1, CC.DEFAULT, CC.CENTER));
				}
				panel5.add(panel7, CC.xy(3, 3));

				//---- label11 ----
				label11.setText(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
				panel5.add(label11, CC.xy(1, 5));
				panel5.add(cbxSeriesFSK, CC.xy(3, 5));

				//---- label12 ----
				label12.setText(LocaleBundle.getString("EditSeriesFrame.lblScore.text")); //$NON-NLS-1$
				panel5.add(label12, CC.xy(1, 7));
				panel5.add(cbxSeriesScore, CC.xy(3, 7));

				//---- label13 ----
				label13.setText(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
				panel5.add(label13, CC.xy(1, 9));
				panel5.add(edSeriesReference, CC.xy(3, 9));

				//---- label14 ----
				label14.setText(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
				panel5.add(label14, CC.xy(1, 11));
				panel5.add(edSeriesGroups, CC.xy(3, 11));

				//---- label15 ----
				label15.setText(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
				panel5.add(label15, CC.xy(1, 13));
				panel5.add(edSeriesTags, CC.xy(3, 13));
			}
			pnlEditSeries.add(panel5, CC.xy(2, 4, CC.FILL, CC.FILL));

			//======== panel8 ========
			{
				panel8.setLayout(new FormLayout(
					"0dlu:grow, $lcgap, default", //$NON-NLS-1$
					"6*(default, $lgap), default:grow")); //$NON-NLS-1$

				//======== scrollPane1 ========
				{

					//---- lsSeasons ----
					lsSeasons.addListSelectionListener(e -> onSeasonSelected());
					scrollPane1.setViewportView(lsSeasons);
				}
				panel8.add(scrollPane1, CC.xywh(1, 1, 1, 13));

				//---- btnAddEmptySeason ----
				btnAddEmptySeason.setText(LocaleBundle.getString("EditSeriesFrame.btnAddEmptySeason.text")); //$NON-NLS-1$
				btnAddEmptySeason.addActionListener(e -> addEmptySeason());
				panel8.add(btnAddEmptySeason, CC.xy(3, 1));

				//---- btnAddSeason ----
				btnAddSeason.setText(LocaleBundle.getString("EditSeriesFrame.btnAddSeason.text")); //$NON-NLS-1$
				btnAddSeason.addActionListener(e -> addSeason());
				panel8.add(btnAddSeason, CC.xy(3, 3));

				//---- btnEditSerinBatch ----
				btnEditSerinBatch.setText(LocaleBundle.getString("EditSeriesFrame.btnBatchEdit.text")); //$NON-NLS-1$
				btnEditSerinBatch.addActionListener(e -> onEditAllSeasonsInBatch());
				panel8.add(btnEditSerinBatch, CC.xy(3, 5));

				//---- btnEditSelSerInBatch ----
				btnEditSelSerInBatch.setText(LocaleBundle.getString("EditSeriesFrame.btnBatchEditSelection.text")); //$NON-NLS-1$
				btnEditSelSerInBatch.addActionListener(e -> batchEditSelectedSeasons());
				panel8.add(btnEditSelSerInBatch, CC.xy(3, 7));

				//---- btnRemoveSeason ----
				btnRemoveSeason.setText(LocaleBundle.getString("EditSeriesFrame.btnRemoveSeason.text")); //$NON-NLS-1$
				btnRemoveSeason.addActionListener(e -> removeSeason());
				panel8.add(btnRemoveSeason, CC.xy(3, 11));
			}
			pnlEditSeries.add(panel8, CC.xy(2, 6, CC.FILL, CC.FILL));

			//======== panel4 ========
			{
				panel4.setLayout(new FlowLayout());

				//---- btnSeriesOk ----
				btnSeriesOk.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
				btnSeriesOk.addActionListener(e -> onSeriesOkay());
				panel4.add(btnSeriesOk);

				//---- btnOkClose ----
				btnOkClose.setText(LocaleBundle.getString("UIGeneric.btnOK_and_Close.text")); //$NON-NLS-1$
				btnOkClose.addActionListener(e -> onSeriesOkayAndClose());
				panel4.add(btnOkClose);
			}
			pnlEditSeries.add(panel4, CC.xy(2, 8));
		}
		contentPane.add(pnlEditSeries, CC.xy(2, 2, CC.FILL, CC.FILL));

		//======== pnlEditSeason ========
		{
			pnlEditSeason.setBorder(LineBorder.createBlackLineBorder());
			pnlEditSeason.setLayout(new FormLayout(
				"2*($lcgap, default), $lcgap, 0dlu:grow, $lcgap, default, $lcgap", //$NON-NLS-1$
				"3*($lgap, pref), 6*($lgap, default), $lgap, default:grow, $lgap, default, $lgap")); //$NON-NLS-1$
			pnlEditSeason.add(edSeasonCvrControl, CC.xywh(2, 2, 7, 1));

			//---- label17 ----
			label17.setText(LocaleBundle.getString("EditSeriesFrame.label17.text")); //$NON-NLS-1$
			pnlEditSeason.add(label17, CC.xy(2, 4));
			pnlEditSeason.add(edSeasonTitle, CC.xywh(4, 4, 5, 1));

			//---- label18 ----
			label18.setText(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
			pnlEditSeason.add(label18, CC.xy(2, 6));
			pnlEditSeason.add(spnSeasonYear, CC.xywh(4, 6, 5, 1));

			//======== scrollPane2 ========
			{

				//---- lsEpisodes ----
				lsEpisodes.addListSelectionListener(e -> onEpisodeSelected());
				scrollPane2.setViewportView(lsEpisodes);
			}
			pnlEditSeason.add(scrollPane2, CC.xywh(2, 8, 5, 13));

			//---- btnAddEpisode ----
			btnAddEpisode.setText(LocaleBundle.getString("EditSeriesFrame.btnAddEmptyEpisode.text")); //$NON-NLS-1$
			btnAddEpisode.addActionListener(e -> addEmptyEpisode());
			pnlEditSeason.add(btnAddEpisode, CC.xy(8, 8));

			//---- btnAddMultipleEpisodes ----
			btnAddMultipleEpisodes.setText(LocaleBundle.getString("EditSeriesFrame.btnAddMultipleEpisodes.text")); //$NON-NLS-1$
			btnAddMultipleEpisodes.addActionListener(e -> multiAddEpisodes());
			pnlEditSeason.add(btnAddMultipleEpisodes, CC.xy(8, 10));

			//---- btnEditAllEpisodesInBatch ----
			btnEditAllEpisodesInBatch.setText(LocaleBundle.getString("EditSeriesFrame.btnBatchEdit.text")); //$NON-NLS-1$
			btnEditAllEpisodesInBatch.addActionListener(e -> batchEditEpisodes());
			pnlEditSeason.add(btnEditAllEpisodesInBatch, CC.xy(8, 12));

			//---- btnEditSelectedEpisodesInBatch ----
			btnEditSelectedEpisodesInBatch.setText(LocaleBundle.getString("EditSeriesFrame.btnBatchEditSelection.text")); //$NON-NLS-1$
			btnEditSelectedEpisodesInBatch.addActionListener(e -> batchEditSelectedEpisodes());
			pnlEditSeason.add(btnEditSelectedEpisodesInBatch, CC.xy(8, 14));

			//---- btnRemoveEpisodes ----
			btnRemoveEpisodes.setText(LocaleBundle.getString("EditSeriesFrame.btnRemoveEpisode.text")); //$NON-NLS-1$
			btnRemoveEpisodes.addActionListener(e -> removeEpisode());
			pnlEditSeason.add(btnRemoveEpisodes, CC.xy(8, 18));

			//======== panel9 ========
			{
				panel9.setLayout(new FlowLayout());

				//---- button12 ----
				button12.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
				button12.addActionListener(e -> onSeasonOkay());
				panel9.add(button12);
			}
			pnlEditSeason.add(panel9, CC.xywh(2, 22, 7, 1));
		}
		contentPane.add(pnlEditSeason, CC.xy(4, 2, CC.FILL, CC.FILL));

		//======== pnlEditEpisode ========
		{
			pnlEditEpisode.setBorder(LineBorder.createBlackLineBorder());
			pnlEditEpisode.setLayout(new FormLayout(
				"$lcgap, default, $lcgap, 0dlu:grow, $lcgap, default, $lcgap, 14dlu, $lcgap, default, $lcgap", //$NON-NLS-1$
				"3*($lgap, pref), 4*($lgap, 14dlu), 2*($lgap, pref), 2*($lgap, 14dlu), 2*($lgap, pref), $lgap, pref:grow, $lgap, pref, $lgap")); //$NON-NLS-1$

			//---- label19 ----
			label19.setText(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label19, CC.xy(2, 2));
			pnlEditEpisode.add(edEpisodeTitle, CC.xywh(4, 2, 7, 1));

			//---- label20 ----
			label20.setText(LocaleBundle.getString("AddEpisodeFrame.lblEpisode.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label20, CC.xy(2, 4));

			//---- spnEpisodeEpisode ----
			spnEpisodeEpisode.setModel(new SpinnerNumberModel(0, 0, null, 1));
			pnlEditEpisode.add(spnEpisodeEpisode, CC.xywh(4, 4, 7, 1));

			//---- label21 ----
			label21.setText(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label21, CC.xy(2, 6));
			pnlEditEpisode.add(cbxEpisodeFormat, CC.xywh(4, 6, 7, 1));

			//---- label22 ----
			label22.setText(LocaleBundle.getString("AddMovieFrame.lblMediaInfo")); //$NON-NLS-1$
			pnlEditEpisode.add(label22, CC.xy(2, 8));
			pnlEditEpisode.add(ctrlEpisodeMediaInfo, CC.xy(4, 8));

			//---- btnEpisodeMediaInfoMain ----
			btnEpisodeMediaInfoMain.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			btnEpisodeMediaInfoMain.setToolTipText("MediaInfo"); //$NON-NLS-1$
			btnEpisodeMediaInfoMain.addActionListener(e -> parseCodecMetadata_MI());
			pnlEditEpisode.add(btnEpisodeMediaInfoMain, CC.xy(8, 8));

			//---- label23 ----
			label23.setText(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label23, CC.xy(2, 10));
			pnlEditEpisode.add(ctrlEpisodeLanguage, CC.xy(4, 10));

			//---- btnEpisodeMediaInfoLang ----
			btnEpisodeMediaInfoLang.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			btnEpisodeMediaInfoLang.setToolTipText("MediaInfo"); //$NON-NLS-1$
			btnEpisodeMediaInfoLang.addActionListener(e -> parseCodecMetadata_Lang());
			pnlEditEpisode.add(btnEpisodeMediaInfoLang, CC.xy(8, 10));

			//---- btnEpisodeMediaInfoShow ----
			btnEpisodeMediaInfoShow.setText("..."); //$NON-NLS-1$
			btnEpisodeMediaInfoShow.addActionListener(e -> showCodecMetadata());
			pnlEditEpisode.add(btnEpisodeMediaInfoShow, CC.xy(10, 10));

			//---- label27 ----
			label27.setText(LocaleBundle.getString("EditSeriesFrame.lblSubs")); //$NON-NLS-1$
			pnlEditEpisode.add(label27, CC.xy(2, 12));
			pnlEditEpisode.add(ctrlEpisodeSubtitles, CC.xy(4, 12));

			//---- btnEpisodeMediaInfoSubs ----
			btnEpisodeMediaInfoSubs.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			btnEpisodeMediaInfoSubs.setToolTipText("MediaInfo"); //$NON-NLS-1$
			btnEpisodeMediaInfoSubs.addActionListener(e -> parseCodecMetadata_Subs());
			pnlEditEpisode.add(btnEpisodeMediaInfoSubs, CC.xy(8, 12));

			//---- label24 ----
			label24.setText(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label24, CC.xy(2, 14));

			//---- spnEpisodeLength ----
			spnEpisodeLength.setModel(new SpinnerNumberModel(0, 0, null, 1));
			pnlEditEpisode.add(spnEpisodeLength, CC.xy(4, 14));

			//---- label26 ----
			label26.setText("min."); //$NON-NLS-1$
			pnlEditEpisode.add(label26, CC.xy(6, 14));

			//---- btnEpisodeMediaInfoLength ----
			btnEpisodeMediaInfoLength.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			btnEpisodeMediaInfoLength.setToolTipText("MediaInfo"); //$NON-NLS-1$
			btnEpisodeMediaInfoLength.addActionListener(e -> parseCodecMetadata_Len());
			pnlEditEpisode.add(btnEpisodeMediaInfoLength, CC.xy(8, 14));

			//---- label25 ----
			label25.setText(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label25, CC.xy(2, 16));

			//---- spnEpisodeSize ----
			spnEpisodeSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
			spnEpisodeSize.addChangeListener(e -> updateEpisodesFilesizeDisplay());
			pnlEditEpisode.add(spnEpisodeSize, CC.xy(4, 16));

			//---- lblEpisodeFilesizePreview ----
			lblEpisodeFilesizePreview.setText("= ..."); //$NON-NLS-1$
			pnlEditEpisode.add(lblEpisodeFilesizePreview, CC.xywh(6, 16, 5, 1, CC.FILL, CC.FILL));

			//---- button14 ----
			button14.setText(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
			button14.addActionListener(e -> recalcEpisodeFilesize());
			pnlEditEpisode.add(button14, CC.xy(4, 18));

			//---- label28 ----
			label28.setText(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label28, CC.xy(2, 20));
			pnlEditEpisode.add(spnEpisodeAdded, CC.xy(4, 20));

			//---- button15 ----
			button15.setText(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
			button15.addActionListener(e -> onSetEpisodeAddedToToday());
			pnlEditEpisode.add(button15, CC.xywh(6, 20, 5, 1));

			//---- label29 ----
			label29.setText(LocaleBundle.getString("AddEpisodeFrame.lblPart.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label29, CC.xy(2, 22));
			pnlEditEpisode.add(edEpisodePart, CC.xywh(4, 22, 5, 1));

			//---- button16 ----
			button16.setText("..."); //$NON-NLS-1$
			button16.addActionListener(e -> openEpisodePart());
			pnlEditEpisode.add(button16, CC.xy(10, 22));

			//---- label30 ----
			label30.setText(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label30, CC.xy(2, 24));
			pnlEditEpisode.add(edEpisodeTags, CC.xywh(4, 24, 7, 1));

			//---- label31 ----
			label31.setText(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
			pnlEditEpisode.add(label31, CC.xy(2, 26));
			pnlEditEpisode.add(ctrlEpisodeHistory, CC.xywh(4, 26, 7, 4));

			//======== panel10 ========
			{
				panel10.setLayout(new FlowLayout());

				//---- button17 ----
				button17.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
				button17.addActionListener(e -> onEpisodeOkay());
				panel10.add(button17);
			}
			pnlEditEpisode.add(panel10, CC.xywh(2, 30, 9, 1));
		}
		contentPane.add(pnlEditEpisode, CC.xy(6, 2, CC.FILL, CC.FILL));
		setSize(1200, 775);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlEditSeries;
	private JPanel panel3;
	private EditCoverControl edSeriesCvrControl;
	private JLabel label1;
	private CCEnumComboBox<CCGenre> cbxSeriesGenre_0;
	private JLabel label2;
	private CCEnumComboBox<CCGenre> cbxSeriesGenre_1;
	private JLabel label3;
	private CCEnumComboBox<CCGenre> cbxSeriesGenre_2;
	private JLabel label4;
	private CCEnumComboBox<CCGenre> cbxSeriesGenre_3;
	private JLabel label5;
	private CCEnumComboBox<CCGenre> cbxSeriesGenre_4;
	private JLabel label6;
	private CCEnumComboBox<CCGenre> cbxSeriesGenre_5;
	private JLabel label7;
	private CCEnumComboBox<CCGenre> cbxSeriesGenre_6;
	private JLabel label8;
	private CCEnumComboBox<CCGenre> cbxSeriesGenre_7;
	private JPanel panel5;
	private JLabel label9;
	private JPanel panel6;
	private JTextField edSeriesTitle;
	private JButton btnParseOnline;
	private JLabel label10;
	private JPanel panel7;
	private JSpinner spnSeriesOnlineScore;
	private JLabel label16;
	private JLabel label11;
	private CCEnumComboBox<CCFSK> cbxSeriesFSK;
	private JLabel label12;
	private CCEnumComboBox<CCUserScore> cbxSeriesScore;
	private JLabel label13;
	private JReferenceChooser edSeriesReference;
	private JLabel label14;
	private GroupListEditor edSeriesGroups;
	private JLabel label15;
	private TagPanel edSeriesTags;
	private JPanel panel8;
	private JScrollPane scrollPane1;
	private JList<String> lsSeasons;
	private JButton btnAddEmptySeason;
	private JButton btnAddSeason;
	private JButton btnEditSerinBatch;
	private JButton btnEditSelSerInBatch;
	private JButton btnRemoveSeason;
	private JPanel panel4;
	private JButton btnSeriesOk;
	private JButton btnOkClose;
	private JPanel pnlEditSeason;
	private EditCoverControl edSeasonCvrControl;
	private JLabel label17;
	private JTextField edSeasonTitle;
	private JLabel label18;
	private JYearSpinner spnSeasonYear;
	private JScrollPane scrollPane2;
	private JList<String> lsEpisodes;
	private JButton btnAddEpisode;
	private JButton btnAddMultipleEpisodes;
	private JButton btnEditAllEpisodesInBatch;
	private JButton btnEditSelectedEpisodesInBatch;
	private JButton btnRemoveEpisodes;
	private JPanel panel9;
	private JButton button12;
	private JPanel pnlEditEpisode;
	private JLabel label19;
	private JTextField edEpisodeTitle;
	private JLabel label20;
	private JSpinner spnEpisodeEpisode;
	private JLabel label21;
	private CCEnumComboBox<CCFileFormat> cbxEpisodeFormat;
	private JLabel label22;
	private JMediaInfoControl ctrlEpisodeMediaInfo;
	private CCIcon16Button btnEpisodeMediaInfoMain;
	private JLabel label23;
	private LanguageSetChooser ctrlEpisodeLanguage;
	private CCIcon16Button btnEpisodeMediaInfoLang;
	private JButton btnEpisodeMediaInfoShow;
	private JLabel label27;
	private LanguageListChooser ctrlEpisodeSubtitles;
	private CCIcon16Button btnEpisodeMediaInfoSubs;
	private JLabel label24;
	private JSpinner spnEpisodeLength;
	private JLabel label26;
	private CCIcon16Button btnEpisodeMediaInfoLength;
	private JLabel label25;
	private JSpinner spnEpisodeSize;
	private JLabel lblEpisodeFilesizePreview;
	private JButton button14;
	private JLabel label28;
	private JCCDateSpinner spnEpisodeAdded;
	private JButton button15;
	private JLabel label29;
	private JReadableCCPathTextField edEpisodePart;
	private JButton button16;
	private JLabel label30;
	private TagPanel edEpisodeTags;
	private JLabel label31;
	private DateTimeListEditor ctrlEpisodeHistory;
	private JPanel panel10;
	private JButton button17;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
