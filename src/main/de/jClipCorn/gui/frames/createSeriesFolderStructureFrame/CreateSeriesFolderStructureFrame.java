package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.JReadableFSPathTextField;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.cover.CoverLabelFullsize;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateSeriesFolderStructureFrame extends JCCFrame
{
	private final CCSeries series;

	public CreateSeriesFolderStructureFrame(Component owner, CCSeries ser)
	{
		super(ser.getMovieList());
		this.series = ser;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		lblCover.setAndResizeCover(series.getCover());
		lblTitle.setText(series.getTitle());
		edCommonPath.setText(series.getCommonPathStart(false).toFSPath(this).toString());
		edPath.setPath(series.guessSeriesRootPath());
		btnTest.setEnabled(! edPath.getPath().isEmpty());
	}

	private void onBtnChoose(ActionEvent evt) {
		var pStart = series.guessSeriesRootPath();
		if (pStart.isEmpty()) pStart = series.getCommonPathStart(false).toFSPath(this);

		JFileChooser folderchooser = new JFileChooser(pStart.toFile());
		folderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (folderchooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			var p = FSPath.create(folderchooser.getSelectedFile());
			if (p.isDirectory()) edPath.setPath(p);
		}

		btnOk.setEnabled(false);
		btnTest.setEnabled(true);
	}

	private void startTest(ActionEvent evt) {
		java.util.List<CSFSElement> elements = new ArrayList<>();

		FSPath parentfolder = edPath.getPath();

		for (int sea = 0; sea < series.getSeasonCount(); sea++) {
			CCSeason season = series.getSeasonByArrayIndex(sea);

			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode episode = season.getEpisodeByArrayIndex(epi);

				CSFSElement elem = new CSFSElement();

				var fileNew = episode.getPathForCreatedFolderstructure(parentfolder);

				elem.CCPathOld = episode.getPart();
				elem.FSPathOld = episode.getPart().toFSPath(this);

				elem.FSPathNew = (fileNew==null)? FSPath.Empty : fileNew;
				elem.CCPathNew = CCPath.createFromFSPath(elem.FSPathNew, this);

				if (fileNew==null)
				{
					elem.State = CSFSElement.CSFSState.Error;
				}
				else if (elem.FSPathNew.equals(elem.FSPathOld))
				{
					elem.State = CSFSElement.CSFSState.Nothing;
				}
				else
				{
					if (fileNew.exists())
						elem.State = CSFSElement.CSFSState.Warning;
					else
						elem.State = CSFSElement.CSFSState.Move;

				}

				for (CSFSElement e : elements) {
					if (e.FSPathNew.equals(elem.FSPathNew) || e.CCPathNew.equals(elem.CCPathNew)) {
						e.State = CSFSElement.CSFSState.Error;
					}
				}

				elements.add(elem);
			}
		}

		lsTest.setData(elements);

		lsTest.autoResize();

		btnOk.setEnabled(!CCStreams.iterate(elements).any(e -> e.State== CSFSElement.CSFSState.Error) && ! movielist.isReadonly());
	}

	@SuppressWarnings("nls")
	private void startMoving(ActionEvent e) {
		lsTest.setData(new ArrayList<>());

		if (! testMoving()) {
			DialogHelper.showDispatchLocalInformation(this, "CreateSeriesFolderStructureFrame.dialogs.couldnotmove");
			return;
		}

		if (DialogHelper.showLocaleYesNo(this, "CreateSeriesFolderStructureFrame.dialogs.sure")) {
			var parentfolder = edPath.getPath();

			new Thread(() ->
			{
				var errors = new ArrayList<Tuple<String, String>>();

				try {

					SwingUtils.invokeAndWaitSafe(() ->
					{
						btnOk.setEnabled(false);
						btnChoose.setEnabled(false);
						btnTest.setEnabled(false);
					});

					int total = series.getEpisodeCount();
					int curr  = 1;
					SwingUtils.invokeLater(() -> this.progress.setMaximum(total));

					for (int sea = 0; sea < series.getSeasonCount(); sea++) {
						CCSeason season = series.getSeasonByArrayIndex(sea);

						for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
							CCEpisode episode = season.getEpisodeByArrayIndex(epi);

							var file = episode.getPart().toFSPath(this);
							var newfile = episode.getPathForCreatedFolderstructure(parentfolder);

							var mkdirfolder = newfile.getParent();

							if (newfile.exists()) {
								if (file.equals(newfile)) {
									final int _v = curr++;
									SwingUtils.invokeLater(() -> this.progress.setValue(_v));
								} else {
									errors.add(Tuple.Create(episode.getTitle(), "Target already exists"));
								}
								continue;
							}

							if (! mkdirfolder.isDirectory()) {
								var succ = mkdirfolder.mkdirsSafe();
								if (!succ) {
									errors.add(Tuple.Create(episode.getTitle(), "Failed to create parent directory"));
									continue;
								}
							}

							{
								var succ = file.renameToSafe(newfile);
								if (!succ) {
									errors.add(Tuple.Create(episode.getTitle(), "Failed to rename file"));
									continue;
								}
							}

							episode.Part.set(CCPath.createFromFSPath(newfile, this));

							final int _v = curr++;
							SwingUtils.invokeLater(() -> this.progress.setValue(_v));
						}
					}

				} finally {

					var _errors = new ArrayList<>(errors);

					SwingUtils.invokeAndWaitSafe(() ->
					{
						if (_errors.isEmpty())
						{
							btnOk.setEnabled(false);
							btnChoose.setEnabled(false);
							btnTest.setEnabled(false);
							DialogHelper.showDispatchLocalInformation(this, "CreateSeriesFolderStructureFrame.dialogs.success");
							CreateSeriesFolderStructureFrame.this.dispose();
						}
						else
						{
							btnOk.setEnabled(false);
							btnChoose.setEnabled(false);
							btnTest.setEnabled(false);

							var pad = CCStreams.iterate(_errors).autoMax(p -> p.Item1.length()).orElse(0);

							var errStr = CCStreams
									.iterate(_errors)
									.map(p -> StringUtils.rightPad(p.Item1, pad) + "    :=    " + p.Item2).stringjoin("\n");

							GenericTextDialog.showMonoText(
									CreateSeriesFolderStructureFrame.this,
									LocaleBundle.getString("CreateSeriesFolderStructureFrame.dialogs.error_caption"),
									LocaleBundle.getString("CreateSeriesFolderStructureFrame.dialogs.error") + "\n\n\n" + errStr,
									true);
						}

					});

				}
			}, "THREAD_CSFS").start(); //$NON-NLS-1$
		}
	}

	private boolean testMoving() {
		var parentfolder = edPath.getPath();

		if (! parentfolder.isDirectory()) {
			return false;
		}

		List<FSPath> files = new ArrayList<>();

		for (int sea = 0; sea < series.getSeasonCount(); sea++) {
			CCSeason season = series.getSeasonByArrayIndex(sea);

			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode episode = season.getEpisodeByArrayIndex(epi);

				files.add(episode.getPathForCreatedFolderstructure(parentfolder));
			}
		}

		Collections.sort(files);
		for (int i = 1; i < files.size(); i++) {
			if (files.get(i-1).equals(files.get(i))) {
				return false;
			}
		}

		return true;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblCover = new CoverLabelFullsize(movielist);
		pnlTop = new JPanel();
		lblTitle = new JLabel();
		edCommonPath = new ReadableTextField();
		edPath = new JReadableFSPathTextField();
		btnChoose = new JButton();
		panel2 = new JPanel();
		btnOk = new JButton();
		btnTest = new JButton();
		progress = new JProgressBar();
		lsTest = new CSFSTable(this);

		//======== this ========
		setTitle(LocaleBundle.getString("CreateSeriesFolderStructureFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, $ugap")); //$NON-NLS-1$
		contentPane.add(lblCover, CC.xy(2, 2));

		//======== pnlTop ========
		{
			pnlTop.setLayout(new FormLayout(
				"default:grow, $lcgap, default", //$NON-NLS-1$
				"3*(default, $lgap), default:grow, 2*($lgap, default)")); //$NON-NLS-1$

			//---- lblTitle ----
			lblTitle.setText("<dynamic>"); //$NON-NLS-1$
			lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getStyle() | Font.BOLD));
			lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
			pnlTop.add(lblTitle, CC.xywh(1, 1, 3, 1));
			pnlTop.add(edCommonPath, CC.xy(1, 3));
			pnlTop.add(edPath, CC.xy(1, 5));

			//---- btnChoose ----
			btnChoose.setText("..."); //$NON-NLS-1$
			btnChoose.addActionListener(e -> onBtnChoose(e));
			pnlTop.add(btnChoose, CC.xy(3, 5));

			//======== panel2 ========
			{
				panel2.setLayout(new FormLayout(
					"default:grow, $lcgap, default:grow", //$NON-NLS-1$
					"default")); //$NON-NLS-1$

				//---- btnOk ----
				btnOk.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
				btnOk.setEnabled(false);
				btnOk.addActionListener(e -> startMoving(e));
				panel2.add(btnOk, CC.xy(1, 1, CC.FILL, CC.DEFAULT));

				//---- btnTest ----
				btnTest.setText(LocaleBundle.getString("MassMoveMoviesFrame.btnTest.text")); //$NON-NLS-1$
				btnTest.addActionListener(e -> startTest(e));
				panel2.add(btnTest, CC.xy(3, 1, CC.FILL, CC.DEFAULT));
			}
			pnlTop.add(panel2, CC.xywh(1, 9, 3, 1));
			pnlTop.add(progress, CC.xywh(1, 11, 3, 1));
		}
		contentPane.add(pnlTop, CC.xy(4, 2, CC.FILL, CC.FILL));
		contentPane.add(lsTest, CC.xywh(2, 4, 3, 1, CC.FILL, CC.FILL));
		setSize(1200, 700);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private CoverLabelFullsize lblCover;
	private JPanel pnlTop;
	private JLabel lblTitle;
	private ReadableTextField edCommonPath;
	private JReadableFSPathTextField edPath;
	private JButton btnChoose;
	private JPanel panel2;
	private JButton btnOk;
	private JButton btnTest;
	private JProgressBar progress;
	private CSFSTable lsTest;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
