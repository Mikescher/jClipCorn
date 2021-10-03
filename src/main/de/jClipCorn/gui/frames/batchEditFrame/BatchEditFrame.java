package de.jClipCorn.gui.frames.batchEditFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.IEpisodeOwner;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.EpisodeDataPack;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.omniParserFrame.OmniParserFrame;
import de.jClipCorn.gui.guiComponents.HFixListCellRenderer;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.JReadableCCPathTextField;
import de.jClipCorn.gui.guiComponents.TagPanel;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.guiComponents.iconComponents.CCIcon16Button;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jCCDateTimeSpinner.JCCDateTimeSpinner;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.language.LanguageListChooser;
import de.jClipCorn.gui.guiComponents.language.LanguageSetChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.OmniParserCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchEditFrame extends JCCFrame implements UserDataProblemHandler, OmniParserCallbackListener
{
	private final UpdateCallbackListener listener;
	private final IEpisodeOwner target;

	protected final List<BatchEditEpisodeData> data;

	private final JFileChooser videoFileChooser;
	private final JFileChooser massVideoFileChooser;

	private boolean amied_isButtonNext = false;

	public BatchEditFrame(Component owner, IEpisodeOwner ss, UpdateCallbackListener ucl) { this(owner, ss, null, ucl); }

	public BatchEditFrame(Component owner, IEpisodeOwner ss, List<CCEpisode> eps, UpdateCallbackListener ucl)
	{
		super(ss.getMovieList());
		this.target = ss;
		this.listener = ucl;
		this.data = (eps!= null) ? CCStreams.iterate(eps).map(BatchEditEpisodeData::new).enumerate() : ss.iteratorEpisodes().map(BatchEditEpisodeData::new).enumerate();

		var cPathStart = ss.getSeries().getCommonPathStart(true);

		this.videoFileChooser     = new JFileChooser(cPathStart.toFSPath(this).toFile());
		this.massVideoFileChooser = new JFileChooser(cPathStart.toFSPath(this).toFile());

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		updateList();
		initFileChooser();
	}

	private void postInit()
	{
		setTitle(LocaleBundle.getFormattedString("AddEpisodeFrame.this.title", target.getSeries().getTitle())); //$NON-NLS-1$
		lblSeason.setText(target.title().get());
	}

	private void initFileChooser() {
		//$NON-NLS-1$
		videoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$

		videoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$

		// ######################################################################################################################

		massVideoFileChooser.setMultiSelectionEnabled(true);

		//$NON-NLS-1$
		massVideoFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.videoFileChooser.filterDescription", CCFileFormat::isValidMovieFormat)); //$NON-NLS-1$

		massVideoFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.videoFileChooser.title")); //$NON-NLS-1$
	}

	public void updateList()
	{
		DefaultListModel<String> model = new DefaultListModel<>();
		lsEpisodes.setModel(model);

		model.clear();

		for (int i = 0; i < data.size(); i++) {
			model.add(i, data.get(i).title + (data.get(i).isDirty() ? " **" : "")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		lsEpisodes.setSelectedIndex(-1);

		updateDisplayPanel();
	}

	public void setPanelEnabled(Container panel, Boolean isEnabled)
	{
		if (panel != this) panel.setEnabled(isEnabled);

		Component[] components = panel.getComponents();

		for (Component component : components)
		{
			if (component instanceof Container) setPanelEnabled((Container) component, isEnabled);
			component.setEnabled(isEnabled);
		}
	}

	private void showCodecMetadata() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			String dat = new MediaQueryRunner(movielist).queryRaw(edPart.getPath().toFSPath(this));

			GenericTextDialog.showText(this, getTitle(), dat, false);
		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void recalcFilesize() {
		var part = edPart.getPath().toFSPath(this);
		var fs = part.filesize();

		if (fs.getBytes() > 0) {
			spnSize.setValue(fs);
		}

		updateFilesizeDisplay();
	}

	private void setToday() {
		spnAddDate.setValue(CCDate.getCurrentDate());
	}

	private void openPart() {
		int returnval = videoFileChooser.showOpenDialog(this);

		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}

		setFilepath(FSPath.create(videoFileChooser.getSelectedFile()));

		recalcFilesize();

		testPart();
	}

	private void testPart() {
		var part = edPart.getPath().toFSPath(this);

		if (part.exists()) {
			edPart.setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		} else {
			edPart.setBackground(Color.RED);
		}
	}

	private void setFilepath(FSPath t) {
		edPart.setPath(CCPath.createFromFSPath(t, this));
		edPart.setCaretPosition(0);

		updateFilesizeDisplay();
	}

	private void parseCodecMetadata_Lang() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edPart.getPath().toFSPath(this), false);

			if (dat.AudioLanguages == null) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			CCDBLanguageSet dbll = dat.AudioLanguages;

			if (dbll.isEmpty()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoEmpty"); //$NON-NLS-1$
			} else {
				ctrlLang.setValue(dbll);
			}

		} catch (IOException | MediaQueryException e) {
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
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edPart.getPath().toFSPath(this), false);

			if (dat.SubtitleLanguages == null) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoFailed"); //$NON-NLS-1$
				return;
			}

			ctrlSubs.setValue(dat.SubtitleLanguages);

		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void parseCodecMetadata_MediaInfo() {
		var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
			DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
			return;
		}

		try {
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edPart.getPath().toFSPath(this), true);
			ctrlMediaInfo.setValue(dat.toPartial());
		} catch (IOException | MediaQueryException e) {
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
			MediaQueryResult dat = new MediaQueryRunner(movielist).query(edPart.getPath().toFSPath(this), true);

			int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
			if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
			spnLength.setValue(dur);

		} catch (IOException | MediaQueryException e) {
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void cancelInfoDisplay() {
		lsEpisodes.setSelectedIndex(-1);
		updateDisplayPanel();
	}

	private void okayInfoDisplay() {
		okInfoDisplay(!chckbxIgnoreUserDataErrors.isSelected(), false);
	}

	private boolean okInfoDisplay(boolean check, boolean next) {
		BatchEditEpisodeData episode = getSelected();

		if (episode == null) return false;

		List<UserDataProblem> problems = new ArrayList<>();
		boolean probvalue = (!check) || checkUserData(problems);

		if (!probvalue)
		{
			amied_isButtonNext = next;
			InputErrorDialog amied = new InputErrorDialog(movielist, problems, this, this);
			amied.setVisible(true);
			return false;
		}

		episode.title         = (edTitle.getText());
		episode.episodeNumber = ((int) spnEpisode.getValue());
		episode.format        = (cbxFormat.getSelectedEnum());
		episode.length        = ((int) spnLength.getValue());
		episode.tags          = ctrlTags.getValue();
		episode.filesize      = (new CCFileSize((long) spnSize.getValue()));
		episode.addDate       = (spnAddDate.getValue());
		episode.part          = (edPart.getPath());
		episode.language      = (ctrlLang.getValue());
		episode.subtitles     = (ctrlSubs.getValue());
		episode.mediaInfo     = (ctrlMediaInfo.getValue());
		episode.viewedHistory = (ctrlHistory.getValue());

		var idx = lsEpisodes.getSelectedIndex();
		lsEpisodes.setSelectedIndex(-1);
		updateList();

		if (next)
		{
			idx++;

			if (idx < lsEpisodes.getModel().getSize())
			{
				lsEpisodes.setSelectedIndex(idx);
				edTitle.requestFocus();
				edTitle.selectAll();
			}
		}
		else
		{
			lsEpisodes.setSelectedIndex(idx);
		}

		return true;
	}

	private boolean checkUserData(List<UserDataProblem> ret) {
		BatchEditEpisodeData sel = getSelected();
		if (sel == null) return false;

		var epack = new EpisodeDataPack
		(
			(int) spnEpisode.getValue(),
			edTitle.getText(),
			(int) spnEpisode.getValue(),
			cbxFormat.getSelectedEnum(),
			new CCFileSize((long) spnSize.getValue()),
			edPart.getPath(),
			spnAddDate.getValue(),
			CCDateTimeList.createEmpty(),
			ctrlTags.getValue(),
			ctrlLang.getValue(),
			ctrlSubs.getValue(),
			ctrlMediaInfo.getValue()
		);

		UserDataProblem.testEpisodeData(ret, target.getMovieList(), target, sel.getSource(), epack);

		return ret.isEmpty();
	}

	private void onBtnNext() {
		int curr = lsEpisodes.getSelectedIndex();
		boolean retval = okInfoDisplay(!chckbxIgnoreUserDataErrors.isSelected(), true);

		if (retval) {
			curr++;

			if (curr < lsEpisodes.getModel().getSize()) {
				lsEpisodes.setSelectedIndex(curr);

				edTitle.requestFocus();

				edTitle.selectAll();
			}
		}
	}

	private void onOKClicked() {
		onOKClicked(true);
	}

	private void onOKClicked(boolean check) {
		if (check) {
			var allproblems = new ArrayList<Tuple<String, UserDataProblem>>();

			for (BatchEditEpisodeData episode : data) {
				List<UserDataProblem> problems = new ArrayList<>();
				UserDataProblem.testEpisodeData(problems, target.getMovieList(), target, episode.getSource(), episode);
				allproblems.addAll(CCStreams.iterate(problems).map(p -> Tuple.Create((String.format("[%d] %s", episode.episodeNumber, episode.title)), p)).toList());//$NON-NLS-1$
			}

			if (allproblems.size() > 0) {
				InputErrorDialog amied = new InputErrorDialog(movielist, allproblems, () -> onOKClicked(false), this, true);
				amied.setVisible(true);
				return;
			}
		}

		for (BatchEditEpisodeData episode : data) {
			episode.apply();
		}

		if (listener != null) {
			listener.onUpdate(null);
		}

		dispose();
	}

	private BatchEditEpisodeData getSelected() {
		int index = lsEpisodes.getSelectedIndex();

		if (index < 0) {
			return null;
		} else {
			return data.get(index);
		}
	}

	private void updateDisplayPanel() {
		BatchEditEpisodeData episode = getSelected();

		if (episode == null) {
			pnlInfo.setVisible(false);
		} else {
			pnlInfo.setVisible(true);

			edTitle.setText(episode.title);
			spnEpisode.setValue(episode.episodeNumber);
			cbxFormat.setSelectedEnum(episode.format);
			spnLength.setValue(episode.length);
			ctrlTags.setValue(episode.tags);
			spnSize.setValue(episode.filesize.getBytes());
			spnAddDate.setValue(episode.addDate);
			edPart.setPath(episode.part);
			ctrlLang.setValue(episode.language);
			ctrlSubs.setValue(episode.subtitles);
			ctrlMediaInfo.setValue(episode.mediaInfo);
			ctrlHistory.setValue(episode.viewedHistory);

			lblDirtyTitle.setVisible(episode.isDirty_Title());
			lblDirtyEpisodeNumber.setVisible(episode.isDirty_EpisodeNumber());
			lblDirtyFormat.setVisible(episode.isDirty_Format());
			lblDirtyLength.setVisible(episode.isDirty_Length());
			lblDirtySize.setVisible(episode.isDirty_Filesize());
			lblDirtyAddDate.setVisible(episode.isDirty_AddDate());
			lblDirtyPath.setVisible(episode.isDirty_Part());
			lblDirtyLanguage.setVisible(episode.isDirty_Language());
			lblDirtySubtitles.setVisible(episode.isDirty_Subtitles());
			lblDirtyMediaInfo.setVisible(episode.isDirty_MediaInfo());
			lblDirtyHistory.setVisible(episode.isDirty_ViewedHistory());
			lblDirtyTags.setVisible(episode.isDirty_Tags());

			testPart();

			updateFilesizeDisplay();
		}
	}

	private void updateFilesizeDisplay() {
		lblFileSize.setText(FileSizeFormatter.format((long) spnSize.getValue()));
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		okInfoDisplay(false, amied_isButtonNext);
	}

	@Override
	public void updateTitles(List<String> newTitles) {
		lsEpisodes.setSelectedIndex(-1);

		for (int i = 0; i < Math.min(data.size(), newTitles.size()); i++) data.get(i).title = (newTitles.get(i));

		updateList();
	}

	private List<String> getTitleList() {
		List<String> result = new ArrayList<>();

		for (int i = 0; i < data.size(); i++) {
			result.add(i, data.get(i).title);
		}

		return result;
	}

	private FSPath getCommonFolderPathStart() {
		List<FSPath> paths = new ArrayList<>();

		for (int i = 0; i < data.size(); i++) {
			paths.add(i, data.get(i).part.toFSPath(this));
		}

		return FSPath.getCommonPath(paths);
	}

	private void showOmniParser() {
		OmniParserFrame oframe = new OmniParserFrame(this, movielist, this, getTitleList(), getCommonFolderPathStart(), Str.Empty, false);
		oframe.setVisible(true);
	}

	private File[] ShowPathFromDialogChooser() {
		var dlg = new JFileChooser(target.getSeries().guessSeriesRootPath().toFile());
		dlg.setMultiSelectionEnabled(true);

		int returnval = dlg.showOpenDialog(this);
		if (returnval != JFileChooser.APPROVE_OPTION) return null;

		return dlg.getSelectedFiles();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblSeason = new JLabel();
		scrlEpisodes = new JScrollPane();
		lsEpisodes = new JList<>();
		pnlInfo = new JPanel();
		lblDirtyTitle = new JLabel();
		label2 = new JLabel();
		edTitle = new JTextField();
		lblDirtyEpisodeNumber = new JLabel();
		label3 = new JLabel();
		spnEpisode = new JSpinner();
		lblDirtyFormat = new JLabel();
		label1 = new JLabel();
		cbxFormat = new CCEnumComboBox<>(CCFileFormat.getWrapper());
		lblDirtyMediaInfo = new JLabel();
		label4 = new JLabel();
		ctrlMediaInfo = new JMediaInfoControl(movielist, () -> edPart.getPath().toFSPath(this));
		btnMediaInfo3 = new CCIcon16Button();
		lblDirtyLanguage = new JLabel();
		label5 = new JLabel();
		ctrlLang = new LanguageSetChooser();
		btnMediaInfoRaw = new JButton();
		btnMediaInfo1 = new CCIcon16Button();
		lblDirtySubtitles = new JLabel();
		label13 = new JLabel();
		ctrlSubs = new LanguageListChooser();
		btnMediaInfo4 = new CCIcon16Button();
		lblDirtyLength = new JLabel();
		label6 = new JLabel();
		spnLength = new JSpinner();
		btnMediaInfo2 = new CCIcon16Button();
		lblDirtySize = new JLabel();
		label7 = new JLabel();
		spnSize = new JSpinner();
		label8 = new JLabel();
		btnRecalcSize = new JButton();
		lblFileSize = new JLabel();
		lblDirtyTags = new JLabel();
		label12 = new JLabel();
		ctrlTags = new TagPanel();
		lblDirtyAddDate = new JLabel();
		label9 = new JLabel();
		spnAddDate = new JCCDateSpinner(CCDate.getMinimumDate(), CCDate.getMinimumDate(), null);
		btnToday = new JButton();
		lblDirtyPath = new JLabel();
		label10 = new JLabel();
		edPart = new JReadableCCPathTextField();
		btnOpen = new JButton();
		lblDirtyHistory = new JLabel();
		label11 = new JLabel();
		ctrlHistory = new DateTimeListEditor();
		panel1 = new JPanel();
		btnEpCancel = new JButton();
		btnEpOk = new JButton();
		chckbxIgnoreUserDataErrors = new JCheckBox();
		btnEpNext = new JButton();
		batchProgress = new JProgressBar();
		tabbedPane1 = new JTabbedPane();
		pnlTitleEdit = new JPanel();
		button5 = new JButton();
		button5.addActionListener(e -> BatchEditMethods.TITLE_DELETE_FIRST_CHARS.run(this, (int) spnSide_01.getValue()));
		spnSide_01 = new JSpinner();
		button1 = new JButton();
		button1.addActionListener(e -> BatchEditMethods.TITLE_DELETE_LAST_CHARS.run(this, (int) spnSide_02.getValue()));
		spnSide_02 = new JSpinner();
		edSide_01 = new JTextField();
		button2 = new JButton();
		button2.addActionListener(e -> BatchEditMethods.TITLE_STRING_REPLACE.run(this, Tuple.Create(edSide_01.getText(), edSide_02.getText())));
		edSide_02 = new JTextField();
		edSide_R1 = new JTextField();
		button3 = new JButton();
		button3.addActionListener(e -> BatchEditMethods.TITLE_REGEX_REPLACE.run(this, Tuple.Create(edSide_R1.getText(), edSide_R2.getText())));
		edSide_R2 = new JTextField();
		button4 = new JButton();
		button4.addActionListener(e -> BatchEditMethods.TITLE_TRIM.run(this, null));
		edSide_03 = new JTextField();
		button6 = new JButton();
		button6.addActionListener(e -> BatchEditMethods.TITLE_PREPEND.run(this, edSide_03.getText()));
		edSide_04 = new JTextField();
		button7 = new JButton();
		button7.addActionListener(e -> BatchEditMethods.TITLE_APPEND.run(this, edSide_04.getText()));
		button8 = new JButton();
		button8.addActionListener(e -> BatchEditMethods.TITLE_SUBSTRING_DELETE.run(this, Tuple.Create((int) spnSide_03.getValue(), (int) spnSide_04.getValue())));
		spnSide_03 = new JSpinner();
		spnSide_04 = new JSpinner();
		edSide_05 = new JTextField();
		button9 = new JButton();
		button9.addActionListener(e -> BatchEditMethods.TITLE_SEARCH_AND_DELETE.run(this, edSide_05.getText()));
		pnlPartEdit = new JPanel();
		button10 = new JButton();
		button10.addActionListener(e -> BatchEditMethods.PATH_TO_CCPATH.run(this, null));
		button11 = new JButton();
		button11.addActionListener(e -> BatchEditMethods.PATH_FROM_CCPATH.run(this, null));
		button12 = new JButton();
		button12.addActionListener(e -> BatchEditMethods.PATH_DELETE_FILENAME_WITH_EXT.run(this, null));
		button13 = new JButton();
		button13.addActionListener(e -> BatchEditMethods.PATH_DELETE_FILENAME_WITHOUT_EXT.run(this, null));
		button14 = new JButton();
		button14.addActionListener(e -> BatchEditMethods.PATH_DELETE_FILEPATH.run(this, null));
		button15 = new JButton();
		button15.addActionListener(e -> BatchEditMethods.PATH_DELETE_EXTENSION.run(this, null));
		button16 = new JButton();
		button16.addActionListener(e -> BatchEditMethods.PATH_DELETE_FIRST_CHARS.run(this, (int) spnSidePart_01.getValue()));
		spnSidePart_01 = new JSpinner();
		button17 = new JButton();
		button17.addActionListener(e -> BatchEditMethods.PATH_DELETE_LAST_CHARS.run(this, (int) spnSidePart_02.getValue()));
		spnSidePart_02 = new JSpinner();
		edSidePart_01 = new JTextField();
		button18 = new JButton();
		button18.addActionListener(e -> BatchEditMethods.PATH_STRING_REPLACE.run(this, Tuple.Create(edSidePart_01.getText(), edSidePart_02.getText())));
		edSidePart_02 = new JTextField();
		edSidePart_R1 = new JTextField();
		button19 = new JButton();
		button19.addActionListener(e -> BatchEditMethods.PATH_REGEX_REPLACE.run(this, Tuple.Create(edSidePart_R1.getText(), edSidePart_R2.getText())));
		edSidePart_R2 = new JTextField();
		button20 = new JButton();
		button20.addActionListener(e -> BatchEditMethods.PATH_TRIM.run(this, null));
		edSidePart_03 = new JTextField();
		button21 = new JButton();
		button21.addActionListener(e -> BatchEditMethods.PATH_PREPEND.run(this, edSidePart_03.getText()));
		edSidePart_04 = new JTextField();
		button22 = new JButton();
		button22.addActionListener(e -> BatchEditMethods.PATH_APPEND.run(this, edSidePart_04.getText()));
		button23 = new JButton();
		button23.addActionListener(e -> BatchEditMethods.PATH_SUBSTRING_DELETE.run(this, Tuple.Create((int) spnSidePart_03.getValue(), (int) spnSidePart_04.getValue())));
		spnSidePart_03 = new JSpinner();
		spnSidePart_04 = new JSpinner();
		edSidePart_05 = new JTextField();
		button24 = new JButton();
		button24.addActionListener(e -> BatchEditMethods.PATH_SEARCH_AND_DELETE.run(this, edSidePart_05.getText()));
		pnlMiscEdit = new JPanel();
		spnSide_05 = new JSpinner();
		button25 = new JButton();
		button25.addActionListener(arg0 -> BatchEditMethods.EPISODEINDEX_ADD.run(this, (int) spnSide_05.getValue()));
		ctrlMultiLang = new LanguageSetChooser();
		button26 = new JButton();
		button26.addActionListener(arg0 -> BatchEditMethods.LANGUAGE_SET.run(this, ctrlMultiLang.getValue()));
		button28 = new JButton();
		button28.addActionListener(e -> BatchEditMethods.VIEWED_CLEAR.run(this, null));
		button29 = new JButton();
		button29.addActionListener(e -> BatchEditMethods.VIEWED_ADD.run(this, CCDateTime.getCurrentDateTime()));
		button30 = new JButton();
		button30.addActionListener(e -> BatchEditMethods.VIEWED_ADD.run(this, CCDateTime.getUnspecified()));
		button31 = new JButton();
		button31.addActionListener(e -> BatchEditMethods.LENGTH_SET.run(this, (int) spnSideLength.getValue()));
		spnSideLength = new JSpinner();
		button32 = new JButton();
		button32.addActionListener(e -> BatchEditMethods.FORMAT_SET.run(this, cbxSideFormat.getSelectedEnum()));
		cbxSideFormat = new CCEnumComboBox<>(CCFileFormat.getWrapper());
		ctrlSideHistoryVal = new JCCDateTimeSpinner();
		button33 = new JButton();
		button33.addActionListener(e -> BatchEditMethods.VIEWED_ADD.run(this, ctrlSideHistoryVal.getValue()));
		button34 = new JButton();
		button34.addActionListener(e -> BatchEditMethods.VIEWED_CLEAR.run(this, null));
		button36 = new JButton();
		button36.addActionListener(e -> BatchEditMethods.PATH_FROM_DIALOG.run(this, Tuple3.Create(ShowPathFromDialogChooser(), 0, true)));
		button37 = new JButton();
		button37.addActionListener(e -> BatchEditMethods.PATH_FROM_DIALOG.run(this, Tuple3.Create(ShowPathFromDialogChooser(), (int) spnPathOpenOffset.getValue(), false)));
		spnPathOpenOffset = new JSpinner();
		cbxSideTag1 = new CCEnumComboBox<>(CCSingleTag.getWrapper());
		button54 = new JButton();
		button54.addActionListener(e -> BatchEditMethods.TAG_ADD.run(this, cbxSideTag1.getSelectedEnum()));
		cbxSideTag2 = new CCEnumComboBox<>(CCSingleTag.getWrapper());
		button55 = new JButton();
		button55.addActionListener(e -> BatchEditMethods.TAG_REM.run(this, cbxSideTag2.getSelectedEnum()));
		pnlMetadata = new JPanel();
		button27 = new JButton();
		button27.addActionListener(e -> BatchEditMethods.FILESIZE_FROM_FILE.run(this, null));
		button35 = new JButton();
		button35.addActionListener(e -> BatchEditMethods.MEDIAINFO_CLEAR.run(this, null));
		button38 = new JButton();
		button38.addActionListener(e -> BatchEditMethods.FORMAT_FROM_FILE.run(this, null));
		button39 = new JButton();
		button39.addActionListener(e -> BatchEditMethods.LANGUAGE_FROM_FILE_MEDIAINFO.run(this, null));
		button56 = new JButton();
		button56.addActionListener(e -> BatchEditMethods.SUBTITLES_FROM_FILE_MEDIAINFO.run(this, null));
		button40 = new JButton();
		button40.addActionListener(e -> BatchEditMethods.LENGTH_FROM_FILE_MEDIAINFO.run(this, null));
		button41 = new JButton();
		button41.addActionListener(e -> BatchEditMethods.MEDIAINFO_FROM_FILE.run(this, null));
		button42 = new JButton();
		button42.addActionListener(e -> BatchEditMethods.MEDIAINFO_CALC_HASH.run(this, null));
		pnlReset = new JPanel();
		button43 = new JButton();
		button43.addActionListener(e -> BatchEditMethods.TITLE_RESET.run(this, null));
		button44 = new JButton();
		button44.addActionListener(e -> BatchEditMethods.EPISODEINDEX_RESET.run(this, null));
		button45 = new JButton();
		button45.addActionListener(e -> BatchEditMethods.FORMAT_RESET.run(this, null));
		button46 = new JButton();
		button46.addActionListener(e -> BatchEditMethods.MEDIAINFO_RESET.run(this, null));
		button47 = new JButton();
		button47.addActionListener(e -> BatchEditMethods.LANGUAGE_RESET.run(this, null));
		button57 = new JButton();
		button57.addActionListener(e -> BatchEditMethods.SUBTITLES_RESET.run(this, null));
		button48 = new JButton();
		button48.addActionListener(e -> BatchEditMethods.LENGTH_RESET.run(this, null));
		button49 = new JButton();
		button49.addActionListener(e -> BatchEditMethods.FILESIZE_RESET.run(this, null));
		button53 = new JButton();
		button53.addActionListener(e -> BatchEditMethods.TAGS_RESET.run(this, null));
		button50 = new JButton();
		button50.addActionListener(e -> BatchEditMethods.ADDDATE_RESET.run(this, null));
		button51 = new JButton();
		button51.addActionListener(e -> BatchEditMethods.PATH_RESET.run(this, null));
		button52 = new JButton();
		button52.addActionListener(e -> BatchEditMethods.VIEWED_RESET.run(this, null));
		btnOmniparser = new JButton();
		btnOK = new JButton();

		//======== this ========
		setTitle("<dynamic>"); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 225px, $ugap, 0dlu:grow, $ugap, 500px, $ugap", //$NON-NLS-1$
			"$pgap, pref, $ugap, default:grow, 2*($lgap, default), $lgap")); //$NON-NLS-1$

		//---- lblSeason ----
		lblSeason.setText("<dynamic>"); //$NON-NLS-1$
		lblSeason.setFont(lblSeason.getFont().deriveFont(lblSeason.getFont().getStyle() & ~Font.BOLD, lblSeason.getFont().getSize() + 5f));
		lblSeason.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblSeason, CC.xy(2, 2));

		//======== scrlEpisodes ========
		{

			//---- lsEpisodes ----
			lsEpisodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lsEpisodes.addListSelectionListener(e -> updateDisplayPanel());
			lsEpisodes.setCellRenderer(new HFixListCellRenderer());
			scrlEpisodes.setViewportView(lsEpisodes);
		}
		contentPane.add(scrlEpisodes, CC.xy(2, 4, CC.FILL, CC.FILL));

		//======== pnlInfo ========
		{
			pnlInfo.setBorder(LineBorder.createBlackLineBorder());
			pnlInfo.setLayout(new FormLayout(
				"$rgap, 5dlu, default, $rgap, 1dlu:grow, $rgap, 50px, $rgap, 22px, $rgap", //$NON-NLS-1$
				"$rgap, 5*(default, $ugap), default, $lgap, default, $ugap, 2*(default, $rgap), default, $lgap, 3*(default, $ugap), 90dlu, $lgap, 0dlu:grow, $lgap, default, $lgap")); //$NON-NLS-1$

			//---- lblDirtyTitle ----
			lblDirtyTitle.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyTitle, CC.xy(2, 2));

			//---- label2 ----
			label2.setText(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
			pnlInfo.add(label2, CC.xy(3, 2));
			pnlInfo.add(edTitle, CC.xywh(5, 2, 5, 1));

			//---- lblDirtyEpisodeNumber ----
			lblDirtyEpisodeNumber.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyEpisodeNumber, CC.xy(2, 4));

			//---- label3 ----
			label3.setText(LocaleBundle.getString("AddEpisodeFrame.lblEpisode.text")); //$NON-NLS-1$
			pnlInfo.add(label3, CC.xy(3, 4));

			//---- spnEpisode ----
			spnEpisode.setModel(new SpinnerNumberModel(0, 0, null, 1));
			pnlInfo.add(spnEpisode, CC.xy(5, 4));

			//---- lblDirtyFormat ----
			lblDirtyFormat.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyFormat, CC.xy(2, 6));

			//---- label1 ----
			label1.setText(LocaleBundle.getString("AddMovieFrame.lblFormat.text")); //$NON-NLS-1$
			pnlInfo.add(label1, CC.xy(3, 6));
			pnlInfo.add(cbxFormat, CC.xy(5, 6));

			//---- lblDirtyMediaInfo ----
			lblDirtyMediaInfo.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyMediaInfo, CC.xy(2, 8));

			//---- label4 ----
			label4.setText(LocaleBundle.getString("PreviewMovieFrame.TabMediaInfo")); //$NON-NLS-1$
			pnlInfo.add(label4, CC.xy(3, 8));
			pnlInfo.add(ctrlMediaInfo, CC.xy(5, 8));

			//---- btnMediaInfo3 ----
			btnMediaInfo3.setToolTipText("MediaInfo"); //$NON-NLS-1$
			btnMediaInfo3.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			btnMediaInfo3.addActionListener(e -> parseCodecMetadata_MediaInfo());
			pnlInfo.add(btnMediaInfo3, CC.xy(9, 8));

			//---- lblDirtyLanguage ----
			lblDirtyLanguage.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyLanguage, CC.xy(2, 10));

			//---- label5 ----
			label5.setText(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
			pnlInfo.add(label5, CC.xy(3, 10));
			pnlInfo.add(ctrlLang, CC.xy(5, 10));

			//---- btnMediaInfoRaw ----
			btnMediaInfoRaw.setText("..."); //$NON-NLS-1$
			btnMediaInfoRaw.setToolTipText(LocaleBundle.getString("PreviewMovieFrame.TabMediaInfo")); //$NON-NLS-1$
			btnMediaInfoRaw.addActionListener(e -> showCodecMetadata());
			pnlInfo.add(btnMediaInfoRaw, CC.xy(7, 10));

			//---- btnMediaInfo1 ----
			btnMediaInfo1.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			btnMediaInfo1.setToolTipText("MediaInfo"); //$NON-NLS-1$
			btnMediaInfo1.addActionListener(e -> parseCodecMetadata_Lang());
			pnlInfo.add(btnMediaInfo1, CC.xy(9, 10));

			//---- lblDirtySubtitles ----
			lblDirtySubtitles.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtySubtitles, CC.xy(2, 12));

			//---- label13 ----
			label13.setText(LocaleBundle.getString("AddMovieFrame.lblSubtitles")); //$NON-NLS-1$
			pnlInfo.add(label13, CC.xy(3, 12));
			pnlInfo.add(ctrlSubs, CC.xy(5, 12));

			//---- btnMediaInfo4 ----
			btnMediaInfo4.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			btnMediaInfo4.setToolTipText("MediaInfo"); //$NON-NLS-1$
			btnMediaInfo4.addActionListener(e -> parseCodecMetadata_Subs());
			pnlInfo.add(btnMediaInfo4, CC.xy(9, 12));

			//---- lblDirtyLength ----
			lblDirtyLength.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyLength, CC.xy(2, 14));

			//---- label6 ----
			label6.setText(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
			pnlInfo.add(label6, CC.xy(3, 14));
			pnlInfo.add(spnLength, CC.xy(5, 14));

			//---- btnMediaInfo2 ----
			btnMediaInfo2.setIconRef(CCIcon16Button.IconRefLink.ICN_MENUBAR_MEDIAINFO);
			btnMediaInfo2.setToolTipText("MediaInfo"); //$NON-NLS-1$
			btnMediaInfo2.addActionListener(e -> parseCodecMetadata_Len());
			pnlInfo.add(btnMediaInfo2, CC.xy(9, 14));

			//---- lblDirtySize ----
			lblDirtySize.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtySize, CC.xy(2, 16));

			//---- label7 ----
			label7.setText(LocaleBundle.getString("AddMovieFrame.lblGre.text")); //$NON-NLS-1$
			pnlInfo.add(label7, CC.xy(3, 16));

			//---- spnSize ----
			spnSize.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));
			pnlInfo.add(spnSize, CC.xy(5, 16));

			//---- label8 ----
			label8.setText("Byte = "); //$NON-NLS-1$
			pnlInfo.add(label8, CC.xy(7, 16));

			//---- btnRecalcSize ----
			btnRecalcSize.setText(LocaleBundle.getString("AddEpisodeFrame.btnRecalcSizes.text")); //$NON-NLS-1$
			btnRecalcSize.addActionListener(e -> recalcFilesize());
			pnlInfo.add(btnRecalcSize, CC.xy(5, 18));
			pnlInfo.add(lblFileSize, CC.xy(5, 20, CC.FILL, CC.FILL));

			//---- lblDirtyTags ----
			lblDirtyTags.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyTags, CC.xy(2, 22));

			//---- label12 ----
			label12.setText(LocaleBundle.getString("EditSeriesFrame.lblTags.text")); //$NON-NLS-1$
			pnlInfo.add(label12, CC.xy(3, 22));
			pnlInfo.add(ctrlTags, CC.xy(5, 22));

			//---- lblDirtyAddDate ----
			lblDirtyAddDate.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyAddDate, CC.xy(2, 24));

			//---- label9 ----
			label9.setText(LocaleBundle.getString("AddMovieFrame.lblEinfgDatum.text")); //$NON-NLS-1$
			pnlInfo.add(label9, CC.xy(3, 24));
			pnlInfo.add(spnAddDate, CC.xy(5, 24, CC.DEFAULT, CC.FILL));

			//---- btnToday ----
			btnToday.setText(LocaleBundle.getString("AddEpisodeFrame.btnToday.text")); //$NON-NLS-1$
			btnToday.addActionListener(e -> setToday());
			pnlInfo.add(btnToday, CC.xywh(7, 24, 3, 1));

			//---- lblDirtyPath ----
			lblDirtyPath.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyPath, CC.xy(2, 26));

			//---- label10 ----
			label10.setText(LocaleBundle.getString("AddEpisodeFrame.lblPart.text")); //$NON-NLS-1$
			pnlInfo.add(label10, CC.xy(3, 26));
			pnlInfo.add(edPart, CC.xy(5, 26));

			//---- btnOpen ----
			btnOpen.setText("..."); //$NON-NLS-1$
			btnOpen.addActionListener(e -> openPart());
			pnlInfo.add(btnOpen, CC.xywh(7, 26, 3, 1));

			//---- lblDirtyHistory ----
			lblDirtyHistory.setText("*"); //$NON-NLS-1$
			pnlInfo.add(lblDirtyHistory, CC.xy(2, 28, CC.DEFAULT, CC.TOP));

			//---- label11 ----
			label11.setText(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
			pnlInfo.add(label11, CC.xy(3, 28, CC.DEFAULT, CC.TOP));
			pnlInfo.add(ctrlHistory, CC.xywh(5, 28, 5, 1, CC.DEFAULT, CC.FILL));

			//======== panel1 ========
			{
				panel1.setLayout(new FormLayout(
					"default, $lcgap, default, 1dlu:grow, default", //$NON-NLS-1$
					"default")); //$NON-NLS-1$

				//---- btnEpCancel ----
				btnEpCancel.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
				btnEpCancel.addActionListener(e -> cancelInfoDisplay());
				panel1.add(btnEpCancel, CC.xy(1, 1));

				//---- btnEpOk ----
				btnEpOk.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
				btnEpOk.addActionListener(e -> okayInfoDisplay());
				panel1.add(btnEpOk, CC.xy(3, 1));

				//---- chckbxIgnoreUserDataErrors ----
				chckbxIgnoreUserDataErrors.setText(LocaleBundle.getString("BatchEditFrame.CbxIgnoreUserDataErrors")); //$NON-NLS-1$
				panel1.add(chckbxIgnoreUserDataErrors, CC.xy(4, 1));

				//---- btnEpNext ----
				btnEpNext.setText(LocaleBundle.getString("AddEpisodeFrame.btnNext.text")); //$NON-NLS-1$
				btnEpNext.setFont(btnEpNext.getFont().deriveFont(Font.BOLD|Font.ITALIC));
				btnEpNext.addActionListener(e -> onBtnNext());
				panel1.add(btnEpNext, CC.xy(5, 1));
			}
			pnlInfo.add(panel1, CC.xywh(2, 32, 8, 1, CC.DEFAULT, CC.FILL));
		}
		contentPane.add(pnlInfo, CC.xywh(4, 2, 1, 5));
		contentPane.add(batchProgress, CC.xy(6, 2, CC.FILL, CC.FILL));

		//======== tabbedPane1 ========
		{
			tabbedPane1.setTabPlacement(SwingConstants.BOTTOM);

			//======== pnlTitleEdit ========
			{
				pnlTitleEdit.setLayout(new FormLayout(
					"$lcgap, 0dlu:grow, 4*($lcgap, 0dlu:grow(0.5)), $lcgap", //$NON-NLS-1$
					"2*($lgap, default), $lgap, 7dlu, 2*($lgap, default), $lgap, 7dlu, 3*($lgap, default), $lgap, 7dlu, $lgap, default, $lgap, 7dlu, $lgap, default, $lgap, default:grow")); //$NON-NLS-1$

				//---- button5 ----
				button5.setText(LocaleBundle.getString("AddEpisodeFrame.btnDeleteFirst.text")); //$NON-NLS-1$
				pnlTitleEdit.add(button5, CC.xywh(2, 2, 5, 1));

				//---- spnSide_01 ----
				spnSide_01.setModel(new SpinnerNumberModel(1, 0, null, 1));
				pnlTitleEdit.add(spnSide_01, CC.xywh(8, 2, 3, 1));

				//---- button1 ----
				button1.setText(LocaleBundle.getString("AddEpisodeFrame.btnDeleteLast.text")); //$NON-NLS-1$
				pnlTitleEdit.add(button1, CC.xywh(2, 4, 5, 1));

				//---- spnSide_02 ----
				spnSide_02.setModel(new SpinnerNumberModel(1, 0, null, 1));
				pnlTitleEdit.add(spnSide_02, CC.xywh(8, 4, 3, 1));
				pnlTitleEdit.add(edSide_01, CC.xy(2, 8));

				//---- button2 ----
				button2.setText(LocaleBundle.getString("AddEpisodeFrame.btnReplace.text")); //$NON-NLS-1$
				pnlTitleEdit.add(button2, CC.xywh(4, 8, 3, 1));
				pnlTitleEdit.add(edSide_02, CC.xywh(8, 8, 3, 1));
				pnlTitleEdit.add(edSide_R1, CC.xy(2, 10));

				//---- button3 ----
				button3.setText(LocaleBundle.getString("BatchEditFrame.ReplaceRegex")); //$NON-NLS-1$
				pnlTitleEdit.add(button3, CC.xywh(4, 10, 3, 1));
				pnlTitleEdit.add(edSide_R2, CC.xywh(8, 10, 3, 1));

				//---- button4 ----
				button4.setText(LocaleBundle.getString("AddEpisodeFrame.btnTrim.text")); //$NON-NLS-1$
				pnlTitleEdit.add(button4, CC.xywh(2, 14, 9, 1));
				pnlTitleEdit.add(edSide_03, CC.xy(2, 16));

				//---- button6 ----
				button6.setText(LocaleBundle.getString("AddEpisodeFrame.btnConcatStart.text")); //$NON-NLS-1$
				pnlTitleEdit.add(button6, CC.xywh(4, 16, 7, 1));
				pnlTitleEdit.add(edSide_04, CC.xy(2, 18));

				//---- button7 ----
				button7.setText(LocaleBundle.getString("AddEpisodeFrame.btnConcatEnd.text")); //$NON-NLS-1$
				pnlTitleEdit.add(button7, CC.xywh(4, 18, 7, 1));

				//---- button8 ----
				button8.setText(LocaleBundle.getString("AddEpisodeFrame.btnDelete.text")); //$NON-NLS-1$
				pnlTitleEdit.add(button8, CC.xywh(2, 22, 5, 1));

				//---- spnSide_03 ----
				spnSide_03.setModel(new SpinnerNumberModel(0, 0, null, 1));
				pnlTitleEdit.add(spnSide_03, CC.xy(8, 22));

				//---- spnSide_04 ----
				spnSide_04.setModel(new SpinnerNumberModel(0, 0, null, 1));
				pnlTitleEdit.add(spnSide_04, CC.xy(10, 22));
				pnlTitleEdit.add(edSide_05, CC.xy(2, 26));

				//---- button9 ----
				button9.setText(LocaleBundle.getString("AddEpisodeFrame.btnSearchAndDel.text")); //$NON-NLS-1$
				pnlTitleEdit.add(button9, CC.xywh(4, 26, 7, 1));
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.TabTitle"), pnlTitleEdit); //$NON-NLS-1$

			//======== pnlPartEdit ========
			{
				pnlPartEdit.setLayout(new FormLayout(
					"$lcgap, 0dlu:grow, 4*($lcgap, 0dlu:grow(0.5)), $lcgap", //$NON-NLS-1$
					"$lgap, default, $lgap, 7dlu, 4*($lgap, default), $lgap, 7dlu, 2*($lgap, default), $lgap, 7dlu, 6*($lgap, default), $lgap, 7dlu, $lgap, default, $lgap, 7dlu, $lgap, default, $lgap, default:grow")); //$NON-NLS-1$

				//---- button10 ----
				button10.setText(LocaleBundle.getString("BatchEditFrame.ConverToCC")); //$NON-NLS-1$
				pnlPartEdit.add(button10, CC.xywh(2, 2, 3, 1));

				//---- button11 ----
				button11.setText(LocaleBundle.getString("BatchEditFrame.ConvertFromCC")); //$NON-NLS-1$
				pnlPartEdit.add(button11, CC.xywh(6, 2, 5, 1));

				//---- button12 ----
				button12.setText(LocaleBundle.getString("BatchEditFrame.DeleteFilename")); //$NON-NLS-1$
				pnlPartEdit.add(button12, CC.xywh(2, 6, 5, 1));

				//---- button13 ----
				button13.setText(LocaleBundle.getString("BatchEditFrame.DeleteFileNameWithoutExt")); //$NON-NLS-1$
				pnlPartEdit.add(button13, CC.xywh(2, 8, 5, 1));

				//---- button14 ----
				button14.setText(LocaleBundle.getString("BatchEditFrame.DeletePath")); //$NON-NLS-1$
				pnlPartEdit.add(button14, CC.xywh(2, 10, 5, 1));

				//---- button15 ----
				button15.setText(LocaleBundle.getString("BatchEditFrame.DeleteExt")); //$NON-NLS-1$
				pnlPartEdit.add(button15, CC.xywh(2, 12, 5, 1));

				//---- button16 ----
				button16.setText(LocaleBundle.getString("AddEpisodeFrame.btnDeleteFirst.text")); //$NON-NLS-1$
				pnlPartEdit.add(button16, CC.xywh(2, 16, 5, 1));

				//---- spnSidePart_01 ----
				spnSidePart_01.setModel(new SpinnerNumberModel(0, 0, null, 1));
				pnlPartEdit.add(spnSidePart_01, CC.xywh(8, 16, 3, 1));

				//---- button17 ----
				button17.setText(LocaleBundle.getString("AddEpisodeFrame.btnDeleteLast.text")); //$NON-NLS-1$
				pnlPartEdit.add(button17, CC.xywh(2, 18, 5, 1));

				//---- spnSidePart_02 ----
				spnSidePart_02.setModel(new SpinnerNumberModel(0, 0, null, 1));
				pnlPartEdit.add(spnSidePart_02, CC.xywh(8, 18, 3, 1));
				pnlPartEdit.add(edSidePart_01, CC.xy(2, 22));

				//---- button18 ----
				button18.setText(LocaleBundle.getString("AddEpisodeFrame.btnReplace.text")); //$NON-NLS-1$
				pnlPartEdit.add(button18, CC.xywh(4, 22, 3, 1));
				pnlPartEdit.add(edSidePart_02, CC.xywh(8, 22, 3, 1));
				pnlPartEdit.add(edSidePart_R1, CC.xy(2, 24));

				//---- button19 ----
				button19.setText(LocaleBundle.getString("BatchEditFrame.ReplaceRegex")); //$NON-NLS-1$
				pnlPartEdit.add(button19, CC.xywh(4, 24, 3, 1));
				pnlPartEdit.add(edSidePart_R2, CC.xywh(8, 24, 3, 1));

				//---- button20 ----
				button20.setText(LocaleBundle.getString("AddEpisodeFrame.btnTrim.text")); //$NON-NLS-1$
				pnlPartEdit.add(button20, CC.xywh(2, 28, 9, 1));
				pnlPartEdit.add(edSidePart_03, CC.xy(2, 30));

				//---- button21 ----
				button21.setText(LocaleBundle.getString("AddEpisodeFrame.btnConcatStart.text")); //$NON-NLS-1$
				pnlPartEdit.add(button21, CC.xywh(4, 30, 7, 1));
				pnlPartEdit.add(edSidePart_04, CC.xy(2, 32));

				//---- button22 ----
				button22.setText(LocaleBundle.getString("AddEpisodeFrame.btnConcatEnd.text")); //$NON-NLS-1$
				pnlPartEdit.add(button22, CC.xywh(4, 32, 7, 1));

				//---- button23 ----
				button23.setText(LocaleBundle.getString("AddEpisodeFrame.btnDelete.text")); //$NON-NLS-1$
				pnlPartEdit.add(button23, CC.xywh(2, 36, 5, 1));

				//---- spnSidePart_03 ----
				spnSidePart_03.setModel(new SpinnerNumberModel(0, 0, null, 1));
				pnlPartEdit.add(spnSidePart_03, CC.xy(8, 36));

				//---- spnSidePart_04 ----
				spnSidePart_04.setModel(new SpinnerNumberModel(0, 0, null, 1));
				pnlPartEdit.add(spnSidePart_04, CC.xy(10, 36));
				pnlPartEdit.add(edSidePart_05, CC.xy(2, 40));

				//---- button24 ----
				button24.setText(LocaleBundle.getString("AddEpisodeFrame.btnSearchAndDel.text")); //$NON-NLS-1$
				pnlPartEdit.add(button24, CC.xywh(4, 40, 7, 1));
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.TabPath"), pnlPartEdit); //$NON-NLS-1$

			//======== pnlMiscEdit ========
			{
				pnlMiscEdit.setLayout(new FormLayout(
					"$rgap, 0dlu:grow, 4*($lcgap, 0dlu:grow(0.5)), $lcgap", //$NON-NLS-1$
					"2*($lgap, default), $lgap, 7dlu, 3*($lgap, default), $lgap, 7dlu, 2*($lgap, default), $lgap, 7dlu, 2*($lgap, default), $lgap, 7dlu, 2*($lgap, default), $lgap, 7dlu, 2*($lgap, default), $lgap, 0dlu:grow")); //$NON-NLS-1$
				pnlMiscEdit.add(spnSide_05, CC.xy(2, 2));

				//---- button25 ----
				button25.setText(LocaleBundle.getString("AddEpisodeFrame.btnIncEpisodeNumbers.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button25, CC.xywh(4, 2, 7, 1));
				pnlMiscEdit.add(ctrlMultiLang, CC.xywh(2, 4, 5, 1));

				//---- button26 ----
				button26.setText(LocaleBundle.getString("AddEpisodeFrame.btnSetLang.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button26, CC.xywh(8, 4, 3, 1));

				//---- button28 ----
				button28.setText(LocaleBundle.getString("AddEpisodeFrame.btnSetUnviewed.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button28, CC.xywh(2, 8, 9, 1));

				//---- button29 ----
				button29.setText(LocaleBundle.getString("AddEpisodeFrame.btnSetViewedNow.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button29, CC.xywh(2, 10, 9, 1));

				//---- button30 ----
				button30.setText(LocaleBundle.getString("AddEpisodeFrame.btnSetViewedUndef.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button30, CC.xywh(2, 12, 9, 1));

				//---- button31 ----
				button31.setText(LocaleBundle.getString("AddEpisodeFrame.btnSetEpLength.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button31, CC.xywh(2, 16, 5, 1));
				pnlMiscEdit.add(spnSideLength, CC.xywh(8, 16, 3, 1));

				//---- button32 ----
				button32.setText(LocaleBundle.getString("AddEpisodeFrame.btnSetEpFormat.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button32, CC.xywh(2, 18, 5, 1));
				pnlMiscEdit.add(cbxSideFormat, CC.xywh(8, 18, 3, 1));
				pnlMiscEdit.add(ctrlSideHistoryVal, CC.xywh(2, 22, 3, 1));

				//---- button33 ----
				button33.setText(LocaleBundle.getString("AddEpisodeFrame.btnAddToHistory.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button33, CC.xywh(6, 22, 5, 1));

				//---- button34 ----
				button34.setText(LocaleBundle.getString("AddEpisodeFrame.btnClearHistory.text")); //$NON-NLS-1$
				pnlMiscEdit.add(button34, CC.xywh(6, 24, 5, 1));

				//---- button36 ----
				button36.setText(LocaleBundle.getString("BatchEditFrame.ReadPathFromDialogFull")); //$NON-NLS-1$
				pnlMiscEdit.add(button36, CC.xywh(2, 28, 7, 1));

				//---- button37 ----
				button37.setText(LocaleBundle.getString("BatchEditFrame.ReadPathFromDialogPartial")); //$NON-NLS-1$
				pnlMiscEdit.add(button37, CC.xywh(2, 30, 7, 1));
				pnlMiscEdit.add(spnPathOpenOffset, CC.xy(10, 30));
				pnlMiscEdit.add(cbxSideTag1, CC.xywh(2, 34, 3, 1));

				//---- button54 ----
				button54.setText(LocaleBundle.getString("BatchEditFrame.AddTag")); //$NON-NLS-1$
				pnlMiscEdit.add(button54, CC.xywh(6, 34, 5, 1));
				pnlMiscEdit.add(cbxSideTag2, CC.xywh(2, 36, 3, 1));

				//---- button55 ----
				button55.setText(LocaleBundle.getString("BatchEditFrame.DelTag")); //$NON-NLS-1$
				pnlMiscEdit.add(button55, CC.xywh(6, 36, 5, 1));
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.TabMisc"), pnlMiscEdit); //$NON-NLS-1$

			//======== pnlMetadata ========
			{
				pnlMetadata.setLayout(new FormLayout(
					"$rgap, 0dlu:grow, $lcgap, 0dlu:grow(0.75), $rgap", //$NON-NLS-1$
					"$lgap, default, $lgap, 7dlu, $lgap, default, $lgap, 7dlu, 4*($lgap, default), $lgap, 7dlu, $lgap, default")); //$NON-NLS-1$

				//---- button27 ----
				button27.setText(LocaleBundle.getString("AddEpisodeFrame.btnSetEpSize.text")); //$NON-NLS-1$
				pnlMetadata.add(button27, CC.xywh(2, 2, 3, 1));

				//---- button35 ----
				button35.setText(LocaleBundle.getString("BatchEditFrame.ClearMediaInfo")); //$NON-NLS-1$
				pnlMetadata.add(button35, CC.xywh(2, 6, 3, 1));

				//---- button38 ----
				button38.setText(LocaleBundle.getString("BatchEditFrame.btnFormatFromPath")); //$NON-NLS-1$
				pnlMetadata.add(button38, CC.xywh(2, 10, 3, 1));

				//---- button39 ----
				button39.setText(LocaleBundle.getString("AddEpisodeFrame.btnMassSetLang.title")); //$NON-NLS-1$
				pnlMetadata.add(button39, CC.xywh(2, 12, 3, 1));

				//---- button56 ----
				button56.setText(LocaleBundle.getString("BatchEditFrame.btnMassSetSubs.title")); //$NON-NLS-1$
				pnlMetadata.add(button56, CC.xywh(2, 14, 3, 1));

				//---- button40 ----
				button40.setText(LocaleBundle.getString("AddEpisodeFrame.btnMassSetLen.title")); //$NON-NLS-1$
				pnlMetadata.add(button40, CC.xywh(2, 16, 3, 1));

				//---- button41 ----
				button41.setText(LocaleBundle.getString("AddEpisodeFrame.btnMassSetMediaInfo.title")); //$NON-NLS-1$
				pnlMetadata.add(button41, CC.xy(2, 20));

				//---- button42 ----
				button42.setText(LocaleBundle.getString("BatchEditFrame.HashCalc")); //$NON-NLS-1$
				pnlMetadata.add(button42, CC.xy(4, 20));
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.TabMetadata"), pnlMetadata); //$NON-NLS-1$

			//======== pnlReset ========
			{
				pnlReset.setLayout(new FormLayout(
					"$rgap, 0dlu:grow, $rgap", //$NON-NLS-1$
					"8*($lgap, default), $lgap, 7dlu, 4*($lgap, default), $lgap, default:grow")); //$NON-NLS-1$

				//---- button43 ----
				button43.setText(LocaleBundle.getString("BatchEditFrame.ResetTitle")); //$NON-NLS-1$
				pnlReset.add(button43, CC.xy(2, 2));

				//---- button44 ----
				button44.setText(LocaleBundle.getString("BatchEditFrame.ResetEpisodenumber")); //$NON-NLS-1$
				pnlReset.add(button44, CC.xy(2, 4));

				//---- button45 ----
				button45.setText(LocaleBundle.getString("BatchEditFrame.ResetFormat")); //$NON-NLS-1$
				pnlReset.add(button45, CC.xy(2, 6));

				//---- button46 ----
				button46.setText(LocaleBundle.getString("BatchEditFrame.ResetMediaInfo")); //$NON-NLS-1$
				pnlReset.add(button46, CC.xy(2, 8));

				//---- button47 ----
				button47.setText(LocaleBundle.getString("BatchEditFrame.ResetLanguage")); //$NON-NLS-1$
				pnlReset.add(button47, CC.xy(2, 10));

				//---- button57 ----
				button57.setText(LocaleBundle.getString("BatchEditFrame.ResetSubtitles")); //$NON-NLS-1$
				pnlReset.add(button57, CC.xy(2, 12));

				//---- button48 ----
				button48.setText(LocaleBundle.getString("BatchEditFrame.ResetLength")); //$NON-NLS-1$
				pnlReset.add(button48, CC.xy(2, 14));

				//---- button49 ----
				button49.setText(LocaleBundle.getString("BatchEditFrame.ResetFilesize")); //$NON-NLS-1$
				pnlReset.add(button49, CC.xy(2, 16));

				//---- button53 ----
				button53.setText(LocaleBundle.getString("BatchEditFrame.ResetTags")); //$NON-NLS-1$
				pnlReset.add(button53, CC.xy(2, 20));

				//---- button50 ----
				button50.setText(LocaleBundle.getString("BatchEditFrame.ResetAddDate")); //$NON-NLS-1$
				pnlReset.add(button50, CC.xy(2, 22));

				//---- button51 ----
				button51.setText(LocaleBundle.getString("BatchEditFrame.ResetPart")); //$NON-NLS-1$
				pnlReset.add(button51, CC.xy(2, 24));

				//---- button52 ----
				button52.setText(LocaleBundle.getString("BatchEditFrame.ResetViewedHistory")); //$NON-NLS-1$
				pnlReset.add(button52, CC.xy(2, 26));
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.TabReset"), pnlReset); //$NON-NLS-1$
		}
		contentPane.add(tabbedPane1, CC.xywh(6, 4, 1, 5, CC.FILL, CC.FILL));

		//---- btnOmniparser ----
		btnOmniparser.setText(LocaleBundle.getString("AddEpisodeFrame.btnOmniParser.text")); //$NON-NLS-1$
		btnOmniparser.addActionListener(e -> showOmniParser());
		contentPane.add(btnOmniparser, CC.xy(2, 6));

		//---- btnOK ----
		btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOKClicked());
		contentPane.add(btnOK, CC.xy(4, 8, CC.CENTER, CC.FILL));
		setSize(1200, 750);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblSeason;
	private JScrollPane scrlEpisodes;
	JList<String> lsEpisodes;
	private JPanel pnlInfo;
	private JLabel lblDirtyTitle;
	private JLabel label2;
	private JTextField edTitle;
	private JLabel lblDirtyEpisodeNumber;
	private JLabel label3;
	private JSpinner spnEpisode;
	private JLabel lblDirtyFormat;
	private JLabel label1;
	private CCEnumComboBox<CCFileFormat> cbxFormat;
	private JLabel lblDirtyMediaInfo;
	private JLabel label4;
	private JMediaInfoControl ctrlMediaInfo;
	private CCIcon16Button btnMediaInfo3;
	private JLabel lblDirtyLanguage;
	private JLabel label5;
	private LanguageSetChooser ctrlLang;
	private JButton btnMediaInfoRaw;
	private CCIcon16Button btnMediaInfo1;
	private JLabel lblDirtySubtitles;
	private JLabel label13;
	private LanguageListChooser ctrlSubs;
	private CCIcon16Button btnMediaInfo4;
	private JLabel lblDirtyLength;
	private JLabel label6;
	private JSpinner spnLength;
	private CCIcon16Button btnMediaInfo2;
	private JLabel lblDirtySize;
	private JLabel label7;
	private JSpinner spnSize;
	private JLabel label8;
	private JButton btnRecalcSize;
	private JLabel lblFileSize;
	private JLabel lblDirtyTags;
	private JLabel label12;
	private TagPanel ctrlTags;
	private JLabel lblDirtyAddDate;
	private JLabel label9;
	private JCCDateSpinner spnAddDate;
	private JButton btnToday;
	private JLabel lblDirtyPath;
	private JLabel label10;
	private JReadableCCPathTextField edPart;
	private JButton btnOpen;
	private JLabel lblDirtyHistory;
	private JLabel label11;
	private DateTimeListEditor ctrlHistory;
	private JPanel panel1;
	private JButton btnEpCancel;
	private JButton btnEpOk;
	private JCheckBox chckbxIgnoreUserDataErrors;
	private JButton btnEpNext;
	public JProgressBar batchProgress;
	private JTabbedPane tabbedPane1;
	private JPanel pnlTitleEdit;
	private JButton button5;
	private JSpinner spnSide_01;
	private JButton button1;
	private JSpinner spnSide_02;
	private JTextField edSide_01;
	private JButton button2;
	private JTextField edSide_02;
	private JTextField edSide_R1;
	private JButton button3;
	private JTextField edSide_R2;
	private JButton button4;
	private JTextField edSide_03;
	private JButton button6;
	private JTextField edSide_04;
	private JButton button7;
	private JButton button8;
	private JSpinner spnSide_03;
	private JSpinner spnSide_04;
	private JTextField edSide_05;
	private JButton button9;
	private JPanel pnlPartEdit;
	private JButton button10;
	private JButton button11;
	private JButton button12;
	private JButton button13;
	private JButton button14;
	private JButton button15;
	private JButton button16;
	private JSpinner spnSidePart_01;
	private JButton button17;
	private JSpinner spnSidePart_02;
	private JTextField edSidePart_01;
	private JButton button18;
	private JTextField edSidePart_02;
	private JTextField edSidePart_R1;
	private JButton button19;
	private JTextField edSidePart_R2;
	private JButton button20;
	private JTextField edSidePart_03;
	private JButton button21;
	private JTextField edSidePart_04;
	private JButton button22;
	private JButton button23;
	private JSpinner spnSidePart_03;
	private JSpinner spnSidePart_04;
	private JTextField edSidePart_05;
	private JButton button24;
	private JPanel pnlMiscEdit;
	private JSpinner spnSide_05;
	private JButton button25;
	private LanguageSetChooser ctrlMultiLang;
	private JButton button26;
	private JButton button28;
	private JButton button29;
	private JButton button30;
	private JButton button31;
	private JSpinner spnSideLength;
	private JButton button32;
	private CCEnumComboBox<CCFileFormat> cbxSideFormat;
	private JCCDateTimeSpinner ctrlSideHistoryVal;
	private JButton button33;
	private JButton button34;
	private JButton button36;
	private JButton button37;
	private JSpinner spnPathOpenOffset;
	private CCEnumComboBox<CCSingleTag> cbxSideTag1;
	private JButton button54;
	private CCEnumComboBox<CCSingleTag> cbxSideTag2;
	private JButton button55;
	private JPanel pnlMetadata;
	private JButton button27;
	private JButton button35;
	private JButton button38;
	private JButton button39;
	private JButton button56;
	private JButton button40;
	private JButton button41;
	private JButton button42;
	private JPanel pnlReset;
	private JButton button43;
	private JButton button44;
	private JButton button45;
	private JButton button46;
	private JButton button47;
	private JButton button57;
	private JButton button48;
	private JButton button49;
	private JButton button53;
	private JButton button50;
	private JButton button51;
	private JButton button52;
	private JButton btnOmniparser;
	private JButton btnOK;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
