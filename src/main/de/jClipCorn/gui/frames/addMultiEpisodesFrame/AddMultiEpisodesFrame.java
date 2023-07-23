package de.jClipCorn.gui.frames.addMultiEpisodesFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.features.metadata.impl.MediaInfoRunner;
import de.jClipCorn.gui.frames.omniParserFrame.OmniParserFrame;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.language.LanguageSetChooserDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddMultiEpisodesFrame extends JCCFrame
{
	private enum CopyMode { KeepFile, Rename, Move, Copy }

	private final FSPath _globalSeriesRoot;
	private final CCSeason target;
	private final UpdateCallbackListener callback;

	private JFileChooser massVideoFileChooser;

	private boolean _hasFiles       = false;
	private boolean _hasTitles      = false;
	private boolean _hasLength      = false;
	private boolean _hasLanguage    = false;
	private boolean _hasMediaInfo   = false;

	public AddMultiEpisodesFrame(Component owner, CCSeason season, UpdateCallbackListener ucl)
	{
		super(season.getMovieList());
		this.callback  = ucl;
		this.target    = season;

		_globalSeriesRoot = season.getMovieList().guessSeriesRootPath();

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		var cPathStart = target.getSeries().getCommonPathStart(true);
		massVideoFileChooser = new JFileChooser(cPathStart.toFSPath(this).toFile());
		massVideoFileChooser.setMultiSelectionEnabled(true);
		massVideoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$
		massVideoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$

		setTitle(LocaleBundle.getFormattedString("AddMultiEpisodesFrame.title", target.getSeries().getTitle())); //$NON-NLS-1$

		spnFirstEpNumber.setValue(target.getNextEpisodeNumber());

		updateButtons();
	}

	private void updateButtons() {
		boolean iskeep   = CCStreams.iterate(lsData.getDataDirect()).any(d -> d.NoMove);
		boolean allvalid = cbxIgnoreProblems.isSelected() || CCStreams.iterate(lsData.getDataDirect()).all(p -> p.IsValid);

		boolean hasMI = cbxIgnoreProblems.isSelected() || _hasMediaInfo;

		boolean bb = isEnabled();

		btnAddFiles.setEnabled(       bb);
		cbSortedAdd.setEnabled(       bb);
		btnInsertTitles.setEnabled(   bb && _hasFiles);
		spnFirstEpNumber.setEnabled(  bb && _hasFiles);
		lblFirstEpNumber.setEnabled(  bb && _hasFiles);
		btnGetLength.setEnabled(      bb && _hasFiles && _hasTitles);
		btnGetLanguages.setEnabled(   bb && _hasFiles && _hasTitles);
		btnGetMediainfo.setEnabled(   bb && _hasFiles && _hasTitles);
		cbxIgnoreProblems.setEnabled( bb && _hasFiles && _hasTitles);
		btnSetDestination.setEnabled( bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && hasMI);
		btnKeepDestination.setEnabled(bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && hasMI);
		btnOkayCopy.setEnabled(       bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && hasMI && !iskeep && allvalid);
		btnOkayMove.setEnabled(       bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && hasMI && !iskeep && allvalid);
		btnOkayRename.setEnabled(     bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && hasMI && !iskeep && allvalid);
		btnOkayKeep.setEnabled(       bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && hasMI &&  iskeep && allvalid);
		btnAddMoreFiles.setEnabled(   bb && lsData.getDataCopy().size()>0);
	}

	private FSPath getCommonFolderPathStart() {
		List<FSPath> paths = new ArrayList<>();

		for (NewEpisodeVM vm : lsData.getDataDirect()) paths.add(vm.SourcePath);

		return FSPath.getCommonPath(paths);
	}

	private void setAllEnabled(boolean b) {
		setEnabled(b);
		updateButtons();
	}

	private void onAddFiles() {
		int returnval = massVideoFileChooser.showOpenDialog(this);
		if (returnval != JFileChooser.APPROVE_OPTION) return;

		File[] files = massVideoFileChooser.getSelectedFiles();
		if (cbSortedAdd.isSelected()) files = CCStreams.iterate(files).autosortByProperty(p -> FSPath.create(p).getFilenameWithExt().toLowerCase()).toArray(new File[0]);

		CCEpisode last = target.getSeries().getLastAddedEpisode();
		CCDBLanguageSet lang = CCDBLanguageSet.single(ccprops().PROP_DATABASE_DEFAULTPARSERLANG.getValue());
		if (last != null) lang = last.getLanguage();

		List<NewEpisodeVM> data = new ArrayList<>();

		int epid = (int)spnFirstEpNumber.getValue();
		for (File f : files) {
			var fp = FSPath.create(f);

			NewEpisodeVM vm  = new NewEpisodeVM(movielist);
			vm.SourcePath    = fp;
			vm.EpisodeNumber = epid;
			vm.Length        = 0;
			vm.Filesize      = fp.filesize();
			vm.IsValid       = false;
			vm.Language      = lang;
			vm.Title         = fp.getFilenameWithoutExt();
			vm.MediaInfo     = CCMediaInfo.EMPTY;

			vm.updateTarget(target, lang, _globalSeriesRoot);
			vm.validate(target);

			data.add(vm);

			epid++;
		}

		lsData.setData(data);
		lsData.autoResize();

		_hasFiles = (data.size()>0);
		_hasTitles = _hasLength = _hasLanguage = _hasMediaInfo = false;

		updateButtons();
	}

	private void onAddMoreFiles() {
		int returnval = massVideoFileChooser.showOpenDialog(this);
		if (returnval != JFileChooser.APPROVE_OPTION) return;

		File[] files = massVideoFileChooser.getSelectedFiles();
		if (cbSortedAdd.isSelected()) files = CCStreams.iterate(files).autosortByProperty(p -> FSPath.create(p).getFilenameWithExt().toLowerCase()).toArray(new File[0]);

		CCEpisode last = target.getSeries().getLastAddedEpisode();
		CCDBLanguageSet lang = CCDBLanguageSet.single(ccprops().PROP_DATABASE_DEFAULTPARSERLANG.getValue());
		if (last != null) lang = last.getLanguage();

		List<NewEpisodeVM> data = new ArrayList<>(lsData.getDataCopy());

		int epid = (int)spnFirstEpNumber.getValue() + data.size();
		for (File f : files) {
			var fp = FSPath.create(f);

			NewEpisodeVM vm  = new NewEpisodeVM(movielist);
			vm.SourcePath    = fp;
			vm.EpisodeNumber = epid;
			vm.Length        = 0;
			vm.Filesize      = fp.filesize();
			vm.IsValid       = false;
			vm.Language      = lang;
			vm.Title         = fp.getFilenameWithoutExt();
			vm.MediaInfo     = CCMediaInfo.EMPTY;

			vm.updateTarget(target, lang, _globalSeriesRoot);
			vm.validate(target);

			data.add(vm);

			epid++;
		}

		lsData.setData(data);
		lsData.autoResize();

		_hasFiles = (data.size()>0);
		_hasTitles = _hasLength = _hasLanguage = _hasMediaInfo = false;

		updateButtons();
	}

	private void onInsertTitles() {
		if (lsData.getDataCopy().size() == 0) return;

		OmniParserFrame oframe = new OmniParserFrame(
				this,
				movielist,
				(d) ->
				{
					List<NewEpisodeVM> data = lsData.getDataCopy();
					for (int i = 0; i < data.size(); i++) {
						data.get(i).Title = d.get(i);
						data.get(i).updateTarget(target, CCDBLanguageSet.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
						data.get(i).validate(target);
					}
					lsData.forceDataChangedRedraw();
					lsData.autoResize();

					_hasTitles = true;
					updateButtons();

				},
				CCStreams.iterate(lsData.getDataDirect()).map(p -> p.Title).enumerate(),
				getCommonFolderPathStart(),
				CCStreams.iterate(lsData.getDataDirect()).stringjoin(p -> p.Title, "\n"), //$NON-NLS-1$
				true);

		oframe.setVisible(true);
	}

	@SuppressWarnings("nls")
	private void onGetLength() {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		setAllEnabled(false);
		progressBar.setValue(0);
		progressBar.setMaximum(data.size());

		new Thread(() ->
		{
			try
			{
				double[] lens = new double[data.size()];
				double successsum = 0;
				int successcount = 0;
				for (int i = 0; i < data.size(); i++)
				{
					final int fi = i;
					SwingUtils.invokeLater(() -> progressBar.setValue(fi) );
					if (data.get(i).MediaQueryResult == null)
					{
						try {
							data.get(i).MediaQueryResult = new MediaInfoRunner(movielist).run(data.get(i).SourcePath);
						} catch (IOException | MetadataQueryException e) {
							CCLog.addError(e);
							data.get(i).MediaQueryResult = null;
						}
					}

					lens[i] = (data.get(i).MediaQueryResult == null || !data.get(i).MediaQueryResult.Duration.isPresent()) ? -1 : (data.get(i).MediaQueryResult.Duration.orElse(0.0) / 60);

					if (data.get(i).MediaQueryResult == null || !data.get(i).MediaQueryResult.Duration.isPresent()) {
						lens[i] = -1;
					} else {
						lens[i] = (data.get(i).MediaQueryResult.Duration.orElse(0.0) / 60);
						successsum += lens[i];
						successcount++;
					}
				}

				final double _successsum = successsum;
				final int _successcount = successcount;

				SwingUtils.invokeLater(() ->
				{

					int avg = (_successcount == 0) ? 0 : (int)Math.round(_successsum / _successcount);
					int serdef = target.getSeries().getAutoEpisodeLength();

					String[] options = new String[]
							{
									LocaleBundle.getString("AddMultiEpisodesFrame.OptionLength_1"), //$NON-NLS-1$
									LocaleBundle.getString("AddMultiEpisodesFrame.OptionLength_2"), //$NON-NLS-1$
									LocaleBundle.getString("AddMultiEpisodesFrame.OptionLength_3"), //$NON-NLS-1$
									LocaleBundle.getString("AddMultiEpisodesFrame.OptionLength_4"), //$NON-NLS-1$
									LocaleBundle.getString("AddMultiEpisodesFrame.OptionLength_5"), //$NON-NLS-1$
							};

					String values = CCStreams.iterate(lens).stringjoin(p -> (p==-1) ? "ERROR" : Math.round(p)+ " min.", "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

					int response = JOptionPane.showOptionDialog(
							AddMultiEpisodesFrame.this,
							LocaleBundle.getFormattedString("AddMultiEpisodesFrame.OptionLength", values, avg, serdef), //$NON-NLS-1$
							LocaleBundle.getString("AddMultiEpisodesFrame.OptionLength_caption"), //$NON-NLS-1$
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.PLAIN_MESSAGE,
							null,
							options,
							options[4]);

					if (response == 0) {
						//det values

						for (int i = 0; i < data.size(); i++) data.get(i).Length = (int)Math.round(lens[i]);
						for (int i = 0; i < data.size(); i++) data.get(i).validate(target);
						lsData.forceDataChangedRedraw();
						lsData.autoResize();
						_hasLength = true;
						updateButtons();

					} else if (response == 1) {
						// average

						for (int i = 0; i < data.size(); i++) data.get(i).Length = avg;
						for (int i = 0; i < data.size(); i++) data.get(i).validate(target);
						lsData.forceDataChangedRedraw();
						lsData.autoResize();
						_hasLength = true;
						updateButtons();

					} else if (response == 2) {
						// custom

						Integer len = null;
						while (len == null || len <= 0) {
							try {
								String dialogresult = DialogHelper.showLocalInputDialog(this, "AddEpisodeFrame.inputMetaTextLenDialog.text", "0"); //$NON-NLS-1$ //$NON-NLS-2$

								if (dialogresult == null) return; // abort

								len = Integer.parseInt(dialogresult);
							} catch (NumberFormatException nfe) {
								len = -1;
							}
						}

						for (int i = 0; i < data.size(); i++) data.get(i).Length = len;
						for (int i = 0; i < data.size(); i++) data.get(i).validate(target);
						lsData.forceDataChangedRedraw();
						lsData.autoResize();
						_hasLength = true;
						updateButtons();

					} else if (response == 3) {
						// default

						for (int i = 0; i < data.size(); i++) data.get(i).Length = serdef;
						for (int i = 0; i < data.size(); i++) data.get(i).validate(target);
						lsData.forceDataChangedRedraw();
						lsData.autoResize();
						_hasLength = true;
						updateButtons();

					}
				});

			}
			finally
			{
				SwingUtils.invokeLater(() ->
				{
					setAllEnabled(true);
					progressBar.setValue(0);
					progressBar.setMaximum(100);
				});
			}

		}).start();
	}

	@SuppressWarnings("nls")
	private void onGetLanguages() {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		setAllEnabled(false);
		progressBar.setValue(0);
		progressBar.setMaximum(data.size());

		final var ignoreInvLangs = cbxIgnoreInvalidLanguages.isSelected();

		new Thread(() ->
		{
			try
			{
				List<Integer> errors = new ArrayList<>();
				for (int i = 0; i < data.size(); i++)
				{
					final int fi = i;
					SwingUtils.invokeLater(() -> progressBar.setValue(fi) );
					if (data.get(i).MediaQueryResult == null)
					{
						try {
							data.get(i).MediaQueryResult = new MediaInfoRunner(movielist).run(data.get(i).SourcePath);
						} catch (IOException | MetadataQueryException e) {
							data.get(i).MediaQueryResult = null;
						}
					}

					if (data.get(i).MediaQueryResult == null ||
						(
							!ignoreInvLangs &&
							(
								data.get(i).MediaQueryResult.hasErrorAudioLanguages() ||
								data.get(i).MediaQueryResult.hasEmptyAudioLanguages() ||
								data.get(i).MediaQueryResult.AudioLanguages.isEmpty() ||
								data.get(i).MediaQueryResult.hasErrorSubtitleLanguages() ||
								data.get(i).MediaQueryResult.hasEmptySubtitleLanguages()
							)
						))
					{
						errors.add(i);
					}
					else
					{
						data.get(i).Language = data.get(i).MediaQueryResult.getValidAudioLanguages();
						data.get(i).Subtitles = data.get(i).MediaQueryResult.getValidSubtitleLanguages();
					}
				}

				final String errorsList = CCStreams.iterate(errors).stringjoin(p -> p+"", ", ");

				SwingUtils.invokeLater(() ->
				{
					if (!errors.isEmpty()) {

						if (DialogHelper.showLocaleFormattedYesNo(AddMultiEpisodesFrame.this, "Dialogs.SameLangErr", errorsList)) { //$NON-NLS-1$
							new LanguageSetChooserDialog(this, v ->
							{
								for (int i = 0; i < data.size(); i++) data.get(i).Language = v;
								for (int i = 0; i < data.size(); i++) data.get(i).validate(target);
								lsData.forceDataChangedRedraw();
								_hasLanguage = true;
								updateButtons();

							}, CCDBLanguageSet.EMPTY).setVisible(true);
							return;
						}
					}

					for (int i = 0; i < data.size(); i++) data.get(i).updateTarget(target, CCDBLanguageSet.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
					for (int i = 0; i < data.size(); i++) data.get(i).validate(target);

					lsData.forceDataChangedRedraw();
					lsData.autoResize();
					_hasLanguage = true;
					updateButtons();
				});

			}
			finally
			{
				SwingUtils.invokeLater(() ->
				{
					setAllEnabled(true);
					progressBar.setValue(0);
					progressBar.setMaximum(100);
				});
			}

		}).start();
	}

	@SuppressWarnings("nls")
	private void onGetMediaInfo() {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		setAllEnabled(false);
		progressBar.setValue(0);
		progressBar.setMaximum(data.size());

		new Thread(() ->
		{
			try
			{
				boolean err = false;
				for (int i = 0; i < data.size(); i++)
				{
					final int fi = i;
					SwingUtils.invokeLater(() -> progressBar.setValue(fi) );
					if (data.get(i).MediaQueryResult == null)
					{
						try {
							data.get(i).MediaQueryResult = new MediaInfoRunner(movielist).run(data.get(i).SourcePath);
						} catch (IOException | MetadataQueryException e) {
							data.get(i).MediaQueryResult = null;
						}
					}

					data.get(i).MediaInfo = (data.get(i).MediaQueryResult == null) ? CCMediaInfo.EMPTY : data.get(i).MediaQueryResult.toPartialMediaInfo().toMediaInfo();
					if (data.get(i).MediaInfo.isUnset()) err = true;
					data.get(i).validate(target);
				}

				final boolean _err = err;

				SwingUtils.invokeLater(() ->
				{
					lsData.forceDataChangedRedraw();
					lsData.autoResize();
					_hasMediaInfo = !_err;
					updateButtons();
				});

			}
			finally
			{
				SwingUtils.invokeLater(() ->
				{
					setAllEnabled(true);
					progressBar.setValue(0);
					progressBar.setMaximum(100);
				});
			}

		}).start();
	}

	private void onSetDestination() {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		JFileChooser vc = new JFileChooser();
		vc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		vc.setAcceptAllFileFilterUsed(false);
		var r = target.getSeries().guessSeriesRootPath();
		if (FSPath.isNullOrEmpty(r)) r = _globalSeriesRoot;
		if (!FSPath.isNullOrEmpty(r)) vc.setCurrentDirectory(r.toFile());

		if (vc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

		for (int i = 0; i < data.size(); i++) {
			data.get(i).TargetRoot = FSPath.create(vc.getSelectedFile());
			data.get(i).NoMove = false;
			data.get(i).updateTarget(target, CCDBLanguageSet.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
			data.get(i).validate(target);
		}
		lsData.forceDataChangedRedraw();
		lsData.autoResize();

		updateButtons();
	}

	private void onKeepDestination() {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		for (int i = 0; i < data.size(); i++) {
			data.get(i).TargetRoot = FSPath.Empty;
			data.get(i).NoMove = true;
			data.get(i).updateTarget(target, CCDBLanguageSet.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
			data.get(i).validate(target);
		}
		lsData.forceDataChangedRedraw();

		updateButtons();
	}

	private void onFirstEpNumberChanged() {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		int num_offset = (int)spnFirstEpNumber.getValue();

		for (int i = 0; i < data.size(); i++) {
			data.get(i).TargetRoot = FSPath.Empty;
			data.get(i).EpisodeNumber = i+num_offset;
			data.get(i).updateTarget(target, CCDBLanguageSet.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
			data.get(i).validate(target);
		}
		lsData.forceDataChangedRedraw();

		updateButtons();
	}

	private void onIgnoreProblemsChanged() {
		updateButtons();
	}

	private void onOkayKeep() { onOkay(CopyMode.KeepFile); }

	private void onOkayRename() { onOkay(CopyMode.Rename); }

	private void onOkayCopy() { onOkay(CopyMode.Copy); }

	private void onOkayMove() { onOkay(CopyMode.Move); }

	private void onOkay(CopyMode mode) {
		final List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		if (!_hasFiles) return;
		if (!_hasTitles) return;
		if (!_hasLanguage) return;
		if (!_hasLength) return;

		if (!cbxIgnoreProblems.isSelected())
		{
			if (!_hasMediaInfo) return;

			for (NewEpisodeVM vm : data) {
				vm.validate(target);
				if (!vm.IsValid) return;
			}
		}

		setAllEnabled(false);
		progressBar.setValue(0);
		progressBar.setMaximum(data.size());
		progressBar2.setMaximum(100);
		new Thread(() ->
		{
			try
			{
				int i = 0;
				for (NewEpisodeVM vm : data)
				{
					try
					{
						final int fi = i;
						SwingUtils.invokeAndWait(() -> progressBar.setValue(fi));
						i++;

						var srcFile = vm.SourcePath;
						var dstFile = vm.TargetPath.toFSPath(this);

						if (mode == CopyMode.KeepFile) { dstFile = srcFile; }

						try
						{
							FileUtils.forceMkdir(dstFile.getParent().toFile());
							if (mode == CopyMode.Copy)
							{
								SimpleFileUtils.copyWithProgress(srcFile, dstFile, (val, max) ->
								{
									int newvalue = (int)(((val * 100) / max));
									SwingUtils.invokeLater(() -> { progressBar2.setValue(newvalue); progressBar2.setMaximum(100); });
								});
							}
							else if (mode == CopyMode.Move)
							{
								SimpleFileUtils.copyWithProgress(srcFile, dstFile, (val, max) ->
								{
									int newvalue = (int)(((val * 100) / max));
									SwingUtils.invokeLater(() -> { progressBar2.setValue(newvalue); progressBar2.setMaximum(100); });
								});

								if (!srcFile.equalsOnFilesystem(dstFile))
								{
									srcFile.deleteWithException();
								}
							}
							else if (mode == CopyMode.Rename)
							{
								if (!srcFile.equalsOnFilesystem(dstFile))
								{
									srcFile.renameToWithException(dstFile);
								}
							}
							else if (mode == CopyMode.KeepFile)
							{
								// nothing
							}
							else throw new Exception("Unknown copy mode = " + mode); //$NON-NLS-1$
						}
						catch (Exception e)
						{
							final int fi2 = i;

							SwingUtils.invokeLater(() ->
							{
								CCLog.addError(e);
								DialogHelper.showDispatchError(
										this,
										LocaleBundle.getString("AddMultiEpisodesFrame.dialogs_error_caption"), //$NON-NLS-1$
										LocaleBundle.getFormattedString("AddMultiEpisodesFrame.dialogs_error", data.get(fi2).Title, e.getMessage())); //$NON-NLS-1$
							});

							// continue - copy/move failed but we continue with original file
							dstFile = srcFile;
						}

						final var final_realImmediatePath = CCPath.createFromFSPath(dstFile.getParent().append(srcFile.getFilenameWithExt()), this);
						final var final_realTargetPath    = CCPath.createFromFSPath(dstFile, this);

						SwingUtils.invokeAndWait(() ->
						{
							CCEpisode newEp = target.createNewEmptyEpisode();
							newEp.beginUpdating();
							newEp.Title.set(vm.Title);
							newEp.EpisodeNumber.set(vm.EpisodeNumber);
							newEp.Format.set(vm.getFormat());
							newEp.MediaInfo.set(vm.MediaInfo);
							newEp.Length.set(vm.Length);
							newEp.FileSize.set(vm.Filesize);
							newEp.AddDate.set(vm.getAddDate());
							newEp.ViewedHistory.set(vm.getViewedHistory());
							newEp.Part.set(final_realImmediatePath);
							newEp.Tags.set(CCTagList.EMPTY);
							newEp.Language.set(vm.Language);
							newEp.Subtitles.set(vm.Subtitles);
							newEp.endUpdating();

							newEp.beginUpdating();
							newEp.Part.set(final_realTargetPath);
							newEp.endUpdating();
						});
					}
					catch (Exception e)
					{
						final int fi = i;

						SwingUtils.invokeLater(() ->
						{
							CCLog.addError(e);
							DialogHelper.showDispatchError(
									this,
									LocaleBundle.getString("AddMultiEpisodesFrame.dialogs_error_caption"), //$NON-NLS-1$
									LocaleBundle.getFormattedString("AddMultiEpisodesFrame.dialogs_error", data.get(fi).Title, e.getMessage())); //$NON-NLS-1$
						});
					}
				}

				SwingUtils.invokeLater(() ->
				{
					if (callback != null) callback.onUpdate(target);
					dispose();
				});
			}
			finally
			{
				SwingUtils.invokeLater(() ->
				{
					setAllEnabled(true);
					progressBar.setValue(0);
					progressBar.setMaximum(100);
				});
			}
		}).start();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lsData = new MultiEpisodesTable(target.getSeries());
		panel1 = new JPanel();
		btnAddFiles = new JButton();
		btnInsertTitles = new JButton();
		btnGetLength = new JButton();
		btnSetDestination = new JButton();
		btnAddMoreFiles = new JButton();
		btnGetLanguages = new JButton();
		btnKeepDestination = new JButton();
		btnOkayKeep = new JButton();
		btnOkayCopy = new JButton();
		cbSortedAdd = new JCheckBox();
		btnGetMediainfo = new JButton();
		cbxIgnoreProblems = new JCheckBox();
		btnOkayRename = new JButton();
		btnOkayMove = new JButton();
		cbxIgnoreInvalidLanguages = new JCheckBox();
		lblFirstEpNumber = new JLabel();
		spnFirstEpNumber = new JSpinner();
		panel2 = new JPanel();
		progressBar = new JProgressBar();
		progressBar2 = new JProgressBar();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(LocaleBundle.getString("AddMultiEpisodesFrame.title")); //$NON-NLS-1$
		setMinimumSize(new Dimension(1000, 500));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, default:grow, $rgap", //$NON-NLS-1$
			"$rgap, default:grow, $lgap, 2*(default, $rgap)")); //$NON-NLS-1$
		contentPane.add(lsData, CC.xy(2, 2, CC.DEFAULT, CC.FILL));

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"6*(default, $lcgap), [50dlu,default], $lcgap, 0dlu:grow, 2*($lcgap, default)", //$NON-NLS-1$
				"3*(default, $lgap), default")); //$NON-NLS-1$

			//---- btnAddFiles ----
			btnAddFiles.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_11")); //$NON-NLS-1$
			btnAddFiles.addActionListener(e -> onAddFiles());
			panel1.add(btnAddFiles, CC.xy(1, 1));

			//---- btnInsertTitles ----
			btnInsertTitles.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_2")); //$NON-NLS-1$
			btnInsertTitles.addActionListener(e -> onInsertTitles());
			panel1.add(btnInsertTitles, CC.xy(3, 1));

			//---- btnGetLength ----
			btnGetLength.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_31")); //$NON-NLS-1$
			btnGetLength.addActionListener(e -> onGetLength());
			panel1.add(btnGetLength, CC.xy(5, 1));

			//---- btnSetDestination ----
			btnSetDestination.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_51")); //$NON-NLS-1$
			btnSetDestination.addActionListener(e -> onSetDestination());
			panel1.add(btnSetDestination, CC.xy(9, 1));

			//---- btnAddMoreFiles ----
			btnAddMoreFiles.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_12")); //$NON-NLS-1$
			btnAddMoreFiles.addActionListener(e -> onAddMoreFiles());
			panel1.add(btnAddMoreFiles, CC.xy(1, 3));

			//---- btnGetLanguages ----
			btnGetLanguages.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_32")); //$NON-NLS-1$
			btnGetLanguages.addActionListener(e -> onGetLanguages());
			panel1.add(btnGetLanguages, CC.xy(5, 3));

			//---- btnKeepDestination ----
			btnKeepDestination.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_52")); //$NON-NLS-1$
			btnKeepDestination.addActionListener(e -> onKeepDestination());
			panel1.add(btnKeepDestination, CC.xy(9, 3));

			//---- btnOkayKeep ----
			btnOkayKeep.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_64")); //$NON-NLS-1$
			btnOkayKeep.addActionListener(e -> onOkayKeep());
			panel1.add(btnOkayKeep, CC.xy(17, 3));

			//---- btnOkayCopy ----
			btnOkayCopy.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_61")); //$NON-NLS-1$
			btnOkayCopy.addActionListener(e -> onOkayCopy());
			panel1.add(btnOkayCopy, CC.xy(19, 3));

			//---- cbSortedAdd ----
			cbSortedAdd.setText(LocaleBundle.getString("AddMultiEpisodesFrame.cbSort")); //$NON-NLS-1$
			panel1.add(cbSortedAdd, CC.xy(1, 5));

			//---- btnGetMediainfo ----
			btnGetMediainfo.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_33")); //$NON-NLS-1$
			btnGetMediainfo.addActionListener(e -> onGetMediaInfo());
			panel1.add(btnGetMediainfo, CC.xy(5, 5));

			//---- cbxIgnoreProblems ----
			cbxIgnoreProblems.setText(LocaleBundle.getString("AddMultiEpisodesFrame.ChkbxIgnoreProblems")); //$NON-NLS-1$
			cbxIgnoreProblems.addActionListener(e -> onIgnoreProblemsChanged());
			panel1.add(cbxIgnoreProblems, CC.xywh(11, 5, 3, 1));

			//---- btnOkayRename ----
			btnOkayRename.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_63")); //$NON-NLS-1$
			btnOkayRename.addActionListener(e -> onOkayRename());
			panel1.add(btnOkayRename, CC.xy(17, 5));

			//---- btnOkayMove ----
			btnOkayMove.setText(LocaleBundle.getString("AddMultiEpisodesFrame.Button_62")); //$NON-NLS-1$
			btnOkayMove.addActionListener(e -> onOkayMove());
			panel1.add(btnOkayMove, CC.xy(19, 5));

			//---- cbxIgnoreInvalidLanguages ----
			cbxIgnoreInvalidLanguages.setText(LocaleBundle.getString("AddMultiEpisodesFrame.cbxIgnoreInvalidLanguages")); //$NON-NLS-1$
			panel1.add(cbxIgnoreInvalidLanguages, CC.xywh(5, 7, 5, 1));

			//---- lblFirstEpNumber ----
			lblFirstEpNumber.setText(LocaleBundle.getString("AddMultiEpisodesFrame.NumberSpinnerLabel")); //$NON-NLS-1$
			panel1.add(lblFirstEpNumber, CC.xy(11, 7));

			//---- spnFirstEpNumber ----
			spnFirstEpNumber.setModel(new SpinnerNumberModel(1, 0, null, 1));
			spnFirstEpNumber.addChangeListener(e -> onFirstEpNumberChanged());
			panel1.add(spnFirstEpNumber, CC.xy(13, 7));
		}
		contentPane.add(panel1, CC.xy(2, 4));

		//======== panel2 ========
		{
			panel2.setLayout(new FormLayout(
				"default:grow, $lcgap, default:grow", //$NON-NLS-1$
				"default")); //$NON-NLS-1$
			panel2.add(progressBar, CC.xy(1, 1));
			panel2.add(progressBar2, CC.xy(3, 1));
		}
		contentPane.add(panel2, CC.xy(2, 6));
		setSize(1600, 650);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private MultiEpisodesTable lsData;
	private JPanel panel1;
	private JButton btnAddFiles;
	private JButton btnInsertTitles;
	private JButton btnGetLength;
	private JButton btnSetDestination;
	private JButton btnAddMoreFiles;
	private JButton btnGetLanguages;
	private JButton btnKeepDestination;
	private JButton btnOkayKeep;
	private JButton btnOkayCopy;
	private JCheckBox cbSortedAdd;
	private JButton btnGetMediainfo;
	private JCheckBox cbxIgnoreProblems;
	private JButton btnOkayRename;
	private JButton btnOkayMove;
	private JCheckBox cbxIgnoreInvalidLanguages;
	private JLabel lblFirstEpNumber;
	private JSpinner spnFirstEpNumber;
	private JPanel panel2;
	private JProgressBar progressBar;
	private JProgressBar progressBar2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
