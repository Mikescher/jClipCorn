package de.jClipCorn.gui.frames.addMultiEpisodesFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.gui.frames.omniParserFrame.OmniParserFrame;
import de.jClipCorn.gui.guiComponents.language.LanguageChooserDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddMultiEpisodesFrame extends JFrame {
	private static final long serialVersionUID = -400962568375180620L;
	
	private JPanel contentPane;
	private MultiEpisodesTable lsData;
	private JButton btnAddFiles;
	private JButton btnInsertTitles;
	private JButton btnGetLength;
	private JButton btnGetLanguages;
	private JButton btnSetDestination;
	private JButton btnOkayCopy;
	private JButton btnOkayMove;
	private JButton btnOkayRename;
	private JProgressBar progressBar;
	private JProgressBar progressBar2;
	private JButton btnKeepDestination;
	private JButton btnGetMediainfo;

	private final JFileChooser massVideoFileChooser;

	private final String _globalSeriesRoot;
	private final CCSeason target;
	private final UpdateCallbackListener callback;

	private boolean _hasFiles       = false;
	private boolean _hasTitles      = false;
	private boolean _hasLength      = false;
	private boolean _hasLanguage    = false;
	private boolean _hasMediaInfo   = false;
	private JButton btnAddMoreFiles;

	public AddMultiEpisodesFrame(Component owner, CCSeason season, UpdateCallbackListener ucl) {		
		super();
		this.callback  = ucl;
		this.target    = season;

		String cPathStart = season.getSeries().getCommonPathStart(true);
		massVideoFileChooser = new JFileChooser(PathFormatter.fromCCPath(cPathStart));
		massVideoFileChooser.setMultiSelectionEnabled(true);
		massVideoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$
		massVideoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$

		_globalSeriesRoot = season.getMovieList().guessSeriesRootPath();

		init(owner);
	}

	private void init(Component owner) {
		initGUI();

		setLocationRelativeTo(owner);
		
		updateButtons();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getFormattedString("AddMultiEpisodesFrame.title", target.getSeries().getTitle())); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(new Dimension(1350, 600));
		setMinimumSize(new Dimension(800, 300));
		contentPane = new JPanel();
		contentPane.setFocusable(false);
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lsData = new MultiEpisodesTable(target.getSeries());
		contentPane.add(lsData, "2, 2, 15, 1, fill, fill"); //$NON-NLS-1$
		
		btnAddFiles = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_11")); //$NON-NLS-1$
		btnAddFiles.addActionListener(this::onAddFiles);
		contentPane.add(btnAddFiles, "2, 4"); //$NON-NLS-1$
		
		btnInsertTitles = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_2")); //$NON-NLS-1$
		btnInsertTitles.addActionListener(this::onInsertTitles);
		contentPane.add(btnInsertTitles, "4, 4"); //$NON-NLS-1$
		
		btnGetLength = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_31")); //$NON-NLS-1$
		btnGetLength.addActionListener(this::onGetLength);
		contentPane.add(btnGetLength, "6, 4"); //$NON-NLS-1$
		
		btnSetDestination = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_51")); //$NON-NLS-1$
		btnSetDestination.addActionListener(this::onSetDestination);
		contentPane.add(btnSetDestination, "10, 4"); //$NON-NLS-1$
		
		btnOkayMove = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_62")); //$NON-NLS-1$
		btnOkayMove.addActionListener(e -> onOkay(1));
		
		btnOkayRename = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_63")); //$NON-NLS-1$
		btnOkayRename.addActionListener(e -> onOkay(2));
		contentPane.add(btnOkayRename, "14, 8, right, default"); //$NON-NLS-1$
		
		btnAddMoreFiles = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_12")); //$NON-NLS-1$
		btnAddMoreFiles.addActionListener(this::onAddMoreFiles);
		contentPane.add(btnAddMoreFiles, "2, 6, fill, fill"); //$NON-NLS-1$
		
		btnOkayCopy = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_61")); //$NON-NLS-1$
		btnOkayCopy.addActionListener(e -> onOkay(0));
		contentPane.add(btnOkayMove, "16, 8"); //$NON-NLS-1$
		
		btnGetLanguages = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_32")); //$NON-NLS-1$
		btnGetLanguages.addActionListener(this::onGetLanguages);
		contentPane.add(btnGetLanguages, "6, 6"); //$NON-NLS-1$
		
		btnKeepDestination = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_52")); //$NON-NLS-1$
		btnKeepDestination.addActionListener(this::onKeepDestination);
		contentPane.add(btnKeepDestination, "10, 6"); //$NON-NLS-1$
		contentPane.add(btnOkayCopy, "16, 6"); //$NON-NLS-1$
		
		btnGetMediainfo = new JButton(LocaleBundle.getString("AddMultiEpisodesFrame.Button_33")); //$NON-NLS-1$
		btnGetMediainfo.addActionListener(this::onGetMediaInfo);
		contentPane.add(btnGetMediainfo, "6, 8"); //$NON-NLS-1$

		progressBar = new JProgressBar();
		contentPane.add(progressBar, "2, 10, 9, 1"); //$NON-NLS-1$
		
		progressBar2 = new JProgressBar();
		contentPane.add(progressBar2, "12, 10, 5, 1"); //$NON-NLS-1$
	}

	private String getCommonFolderPathStart() {
		List<String> paths = new ArrayList<>();

		for (NewEpisodeVM vm : lsData.getDataDirect()) paths.add(vm.SourcePath);

		return PathFormatter.fromCCPath(PathFormatter.getCommonFolderPath(paths));
	}

	private void onAddFiles(ActionEvent evt) {
		int returnval = massVideoFileChooser.showOpenDialog(this);
		if (returnval != JFileChooser.APPROVE_OPTION) return;

		File[] files = massVideoFileChooser.getSelectedFiles();

		CCEpisode last = target.getSeries().getLastAddedEpisode();
		CCDBLanguageList lang = CCDBLanguageList.single(CCProperties.getInstance().PROP_DATABASE_DEFAULTPARSERLANG.getValue());
		if (last != null) lang = last.getLanguage();

		List<NewEpisodeVM> data = new ArrayList<>();

		int epid = target.getNextEpisodeNumber();
		for (File f : files) {
			NewEpisodeVM vm  = new NewEpisodeVM();
			vm.SourcePath    = f.getAbsolutePath();
			vm.EpisodeNumber = epid;
			vm.Length        = 0;
			vm.Filesize      = f.length();
			vm.IsValid       = false;
			vm.Language      = lang;
			vm.Title         = PathFormatter.getFilename(f.getAbsolutePath());
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

	private void onAddMoreFiles(ActionEvent evt) {
		int returnval = massVideoFileChooser.showOpenDialog(this);
		if (returnval != JFileChooser.APPROVE_OPTION) return;

		File[] files = massVideoFileChooser.getSelectedFiles();

		CCEpisode last = target.getSeries().getLastAddedEpisode();
		CCDBLanguageList lang = CCDBLanguageList.single(CCProperties.getInstance().PROP_DATABASE_DEFAULTPARSERLANG.getValue());
		if (last != null) lang = last.getLanguage();

		List<NewEpisodeVM> data = new ArrayList<>(lsData.getDataCopy());

		int epid = target.getNextEpisodeNumber() + data.size();
		for (File f : files) {
			NewEpisodeVM vm  = new NewEpisodeVM();
			vm.SourcePath    = f.getAbsolutePath();
			vm.EpisodeNumber = epid;
			vm.Length        = 0;
			vm.Filesize      = f.length();
			vm.IsValid       = false;
			vm.Language      = lang;
			vm.Title         = PathFormatter.getFilename(f.getAbsolutePath());
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

	private void onInsertTitles(ActionEvent evt) {
		if (lsData.getDataCopy().size() == 0) return;

		OmniParserFrame oframe = new OmniParserFrame(
			this,
			(d) ->
			{
				List<NewEpisodeVM> data = lsData.getDataCopy();
				for (int i = 0; i < data.size(); i++) {
					data.get(i).Title = d.get(i);
					data.get(i).updateTarget(target, CCDBLanguageList.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
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

	private void onGetLength(ActionEvent evt) {
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
							data.get(i).MediaQueryResult = MediaQueryRunner.query(data.get(i).SourcePath, true);
						} catch (IOException | MediaQueryException e) {
							data.get(i).MediaQueryResult = null;
						}
					}

					lens[i] = (data.get(i).MediaQueryResult == null || data.get(i).MediaQueryResult.Duration == -1) ? -1 : (data.get(i).MediaQueryResult.Duration / 60);

					if (data.get(i).MediaQueryResult == null || data.get(i).MediaQueryResult.Duration == -1) {
						lens[i] = -1;
					} else {
						lens[i] = (data.get(i).MediaQueryResult.Duration / 60);
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

	private void onGetLanguages(ActionEvent evt) {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		setAllEnabled(false);
		progressBar.setValue(0);
		progressBar.setMaximum(data.size());

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
							data.get(i).MediaQueryResult = MediaQueryRunner.query(data.get(i).SourcePath, true);
						} catch (IOException | MediaQueryException e) {
							data.get(i).MediaQueryResult = null;
						}
					}

					if (data.get(i).MediaQueryResult == null || data.get(i).MediaQueryResult.AudioLanguages == null || data.get(i).MediaQueryResult.AudioLanguages.isEmpty()) {
						errors.add(i);
					} else {
						data.get(i).Language = data.get(i).MediaQueryResult.AudioLanguages;
					}
				}

				final String errorsList = CCStreams.iterate(errors).stringjoin(p -> p+"", ", "); //$NON-NLS-1$ //$NON-NLS-2$

				SwingUtils.invokeLater(() ->
				{
					if (!errors.isEmpty()) {

						if (DialogHelper.showLocaleFormattedYesNo(AddMultiEpisodesFrame.this, "Dialogs.SameLangErr", errorsList)) { //$NON-NLS-1$
							new LanguageChooserDialog(this, v ->
							{
								for (int i = 0; i < data.size(); i++) data.get(i).Language = v;
								for (int i = 0; i < data.size(); i++) data.get(i).validate(target);
								lsData.forceDataChangedRedraw();
								_hasLanguage = true;
								updateButtons();

							}, CCDBLanguageList.EMPTY).setVisible(true);
							return;
						}
					}

					for (int i = 0; i < data.size(); i++) data.get(i).updateTarget(target, CCDBLanguageList.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
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

	private void onGetMediaInfo(ActionEvent evt) {
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
							data.get(i).MediaQueryResult = MediaQueryRunner.query(data.get(i).SourcePath, true);
						} catch (IOException | MediaQueryException e) {
							data.get(i).MediaQueryResult = null;
						}
					}

					data.get(i).MediaInfo = (data.get(i).MediaQueryResult == null) ? CCMediaInfo.EMPTY : data.get(i).MediaQueryResult.toMediaInfo();
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

	private void onSetDestination(ActionEvent evt) {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		JFileChooser vc = new JFileChooser();
		vc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		vc.setAcceptAllFileFilterUsed(false);
		String r = target.getSeries().guessSeriesRootPath();
		if (Str.isNullOrWhitespace(r)) r = _globalSeriesRoot;
		if (!Str.isNullOrWhitespace(r)) vc.setCurrentDirectory(new File(r));

		if (vc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

		for (int i = 0; i < data.size(); i++) {
			data.get(i).TargetRoot = vc.getSelectedFile().getAbsolutePath();
			data.get(i).NoMove = false;
			data.get(i).updateTarget(target, CCDBLanguageList.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
			data.get(i).validate(target);
		}
		lsData.forceDataChangedRedraw();
		lsData.autoResize();

		updateButtons();
	}

	private void onKeepDestination(ActionEvent evt) {
		List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		for (int i = 0; i < data.size(); i++) {
			data.get(i).TargetRoot = ""; //$NON-NLS-1$
			data.get(i).NoMove = true;
			data.get(i).updateTarget(target, CCDBLanguageList.union(CCStreams.iterate(data).map(p -> p.Language)), _globalSeriesRoot);
			data.get(i).validate(target);
		}
		lsData.forceDataChangedRedraw();

		updateButtons();
	}

	private void onOkay(int mode) {
		final List<NewEpisodeVM> data = lsData.getDataCopy();
		if (data.size() == 0) return;

		if (!_hasFiles) return;
		if (!_hasTitles) return;
		if (!_hasLanguage) return;
		if (!_hasLength) return;
		if (!_hasMediaInfo) return;

		for (NewEpisodeVM vm : data) {
			vm.validate(target);
			if (!vm.IsValid) return;
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
				for (NewEpisodeVM vm : data) {
					try {
						final int fi = i;
						SwingUtils.invokeAndWait(() -> { progressBar.setValue(fi); });
						i++;

						File srcFile = new File(vm.SourcePath);
						File dstFile = new File(PathFormatter.fromCCPath(vm.TargetPath));

						String realTargetPath = PathFormatter.getCCPath(vm.SourcePath);

						if (!srcFile.getAbsolutePath().equalsIgnoreCase(dstFile.getAbsolutePath()) && !vm.NoMove)
						{
							try
							{
								FileUtils.forceMkdir(dstFile.getParentFile());
								if (mode == 0)
								{
									SimpleFileUtils.copyWithProgress(srcFile, dstFile, (val, max) ->
									{
										int newvalue = (int)(((val * 100) / max));
										SwingUtils.invokeLater(() -> { progressBar2.setValue(newvalue); progressBar2.setMaximum(100); });
									});
								}
								else if (mode == 1)
								{
									SimpleFileUtils.copyWithProgress(srcFile, dstFile, (val, max) ->
									{
										int newvalue = (int)(((val * 100) / max));
										SwingUtils.invokeLater(() -> { progressBar2.setValue(newvalue); progressBar2.setMaximum(100); });
									});
									if (!srcFile.delete()) throw new Exception("Delete of '"+srcFile.getAbsolutePath()+"' failed"); //$NON-NLS-1$ //$NON-NLS-2$
								}
								else if (mode == 2)
								{
									if (!srcFile.renameTo(dstFile)) throw new Exception("Rename of '"+srcFile.getAbsolutePath()+"' to '"+dstFile.getAbsolutePath()+"' failed"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								}
								else throw new Exception("Unknown copy mode = " + mode); //$NON-NLS-1$

								realTargetPath = vm.TargetPath;
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

								// continue - copy/move failed but we continue with old <realTargetPath>
							}
						}

						final String final_realTargetPath = realTargetPath;
						SwingUtils.invokeAndWait(() ->
						{
							CCEpisode newEp = target.createNewEmptyEpisode();
							newEp.beginUpdating();
							newEp.setTitle(vm.Title);
							newEp.setEpisodeNumber(vm.EpisodeNumber);
							newEp.setFormat(vm.getFormat());
							newEp.setMediaInfo(vm.MediaInfo);
							newEp.setLength(vm.Length);
							newEp.setFilesize(vm.Filesize);
							newEp.setAddDate(vm.getAddDate());
							newEp.setViewedHistory(vm.getViewedHistory());
							newEp.setPart(final_realTargetPath);
							newEp.setTags(CCTagList.EMPTY);
							newEp.setLanguage(vm.Language);
							newEp.endUpdating();
						});

					} catch (Exception e) {
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

	private void setAllEnabled(boolean b) {
		setEnabled(b);
		updateButtons();
	}

	private void updateButtons() {
		boolean iskeep = CCStreams.iterate(lsData.getDataDirect()).any(d -> d.NoMove);
		boolean allvalid = CCStreams.iterate(lsData.getDataDirect()).all(p -> p.IsValid);

		boolean bb = isEnabled();

		btnAddFiles.setEnabled(bb);
		btnInsertTitles.setEnabled(bb && _hasFiles);
		btnGetLength.setEnabled(bb && _hasFiles && _hasTitles);
		btnGetLanguages.setEnabled(bb && _hasFiles && _hasTitles);
		btnGetMediainfo.setEnabled(bb && _hasFiles && _hasTitles);
		btnSetDestination.setEnabled(bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && _hasMediaInfo);
		btnKeepDestination.setEnabled(bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && _hasMediaInfo);
		btnOkayCopy.setEnabled(bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && _hasMediaInfo && !iskeep && allvalid);
		btnOkayMove.setEnabled(bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && _hasMediaInfo && !iskeep && allvalid);
		btnOkayRename.setEnabled(bb && _hasFiles && _hasTitles && _hasLanguage && _hasLength && _hasMediaInfo && allvalid);
		btnAddMoreFiles.setEnabled(bb && lsData.getDataCopy().size()>0);
	}

}
