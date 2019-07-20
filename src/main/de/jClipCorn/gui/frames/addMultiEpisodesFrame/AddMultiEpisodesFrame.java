package de.jClipCorn.gui.frames.addMultiEpisodesFrame;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.*;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.gui.frames.omniParserFrame.OmniParserFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.listener.OmniParserCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.event.ActionEvent;
import java.io.File;
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

	private final JFileChooser massVideoFileChooser;

	private final CCMovieList movieList;
	private final CCSeason target;
	private final UpdateCallbackListener callback;
	
	private int _currentStep = 1; // activeStep
	
	public AddMultiEpisodesFrame(Component owner, CCSeason season, UpdateCallbackListener ucl) {		
		super();
		this.movieList = season.getMovieList();
		this.callback  = ucl;
		this.target    = season;

		String cPathStart = season.getSeries().getCommonPathStart(true);
		massVideoFileChooser = new JFileChooser(PathFormatter.fromCCPath(cPathStart));
		massVideoFileChooser.setMultiSelectionEnabled(true);
		massVideoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$
		massVideoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$

		init(owner);
	}

	private void init(Component owner) {
		initGUI();

		setLocationRelativeTo(owner);
		
		updateButtons();
	}

	private void initGUI() {
		setTitle("Add Episodes to <???>");
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
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lsData = new MultiEpisodesTable();
		contentPane.add(lsData, "2, 2, 13, 1, fill, fill");
		
		btnAddFiles = new JButton("(1) Add files");
		btnAddFiles.addActionListener(this::onAddFiles);
		contentPane.add(btnAddFiles, "2, 4");
		
		btnInsertTitles = new JButton("(2) Insert titles");
		btnInsertTitles.addActionListener(this::onInsertTitles);
		contentPane.add(btnInsertTitles, "4, 4");
		
		btnGetLength = new JButton("(3) Get Length");
		btnGetLength.addActionListener(this::onGetLength);
		contentPane.add(btnGetLength, "6, 4");
		
		btnGetLanguages = new JButton("(4) Get Languages");
		btnGetLanguages.addActionListener(this::onGetLanguages);
		contentPane.add(btnGetLanguages, "8, 4");
		
		btnSetDestination = new JButton("(5) Set Destination");
		btnSetDestination.addActionListener(this::onSetDestination);
		contentPane.add(btnSetDestination, "10, 4");
		
		btnOkayCopy = new JButton("Okay (+ Copy)");
		btnOkayCopy.addActionListener(this::onOkayCopy);
		contentPane.add(btnOkayCopy, "14, 4");
		
		btnOkayMove = new JButton("Okay (+ Move)");
		btnOkayMove.addActionListener(this::onOkayMove);
		contentPane.add(btnOkayMove, "14, 6");
		
	}

	private void updateButtons() {
		btnAddFiles.setEnabled(_currentStep >= 1);
		btnInsertTitles.setEnabled(_currentStep >= 2);
		btnGetLength.setEnabled(_currentStep >= 3);
		btnGetLanguages.setEnabled(_currentStep >= 4);
		btnSetDestination.setEnabled(_currentStep >= 5);
		btnOkayCopy.setEnabled(_currentStep >= 6);
		btnOkayMove.setEnabled(_currentStep >= 6);
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

		int epid = target.getNewUnusedEpisodeNumber();
		for (File f : files) {
			NewEpisodeVM vm  = new NewEpisodeVM();
			vm.SourcePath    = f.getAbsolutePath();
			vm.EpisodeNumber = epid;
			vm.Length        = 0;
			vm.Filesize      = f.length();
			vm.IsValid       = false;
			vm.Language      = lang;
			vm.Title         = PathFormatter.getFilename(f.getAbsolutePath());

			vm.updateTarget(target);
			vm.validate(target);

			data.add(vm);

			epid++;
		}

		lsData.setData(data);
		lsData.autoResize();

		_currentStep = 2;
		updateButtons();
	}

	private void onInsertTitles(ActionEvent evt) {

		OmniParserFrame oframe = new OmniParserFrame(
			this,
			(d) ->
			{
				List<NewEpisodeVM> data = lsData.getDataCopy();
				for (int i = 0; i < data.size(); i++) {
					data.get(i).Title = d.get(i);
					data.get(i).updateTarget(target);
					data.get(i).validate(target);
				}
				lsData.setData(data);

				_currentStep = Math.max(_currentStep, 3);
				updateButtons();

			},
			CCStreams.iterate(lsData.getDataDirect()).map(p -> p.Title).enumerate(),
			getCommonFolderPathStart(),
			CCStreams.iterate(lsData.getDataDirect()).stringjoin(p -> p.Title, "\n"),
			true);

		oframe.setVisible(true);
	}

	private void onGetLength(ActionEvent evt) {

		_currentStep = Math.max(_currentStep, 4);
		updateButtons();
	}

	private void onGetLanguages(ActionEvent evt) {

		_currentStep = Math.max(_currentStep, 5);
		updateButtons();
	}

	private void onSetDestination(ActionEvent evt) {

		_currentStep = Math.max(_currentStep, 6);
		updateButtons();
	}

	private void onOkayCopy(ActionEvent evt) {

		dispose();
	}

	private void onOkayMove(ActionEvent evt) {

		dispose();
	}
}
