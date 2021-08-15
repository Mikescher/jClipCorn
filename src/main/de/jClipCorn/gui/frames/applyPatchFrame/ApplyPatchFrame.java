package de.jClipCorn.gui.frames.applyPatchFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.guiComponents.*;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.DoubleProgressCallbackProgressBarHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ApplyPatchFrame extends JCCFrame
{
	private java.util.List<ActionVM> _actions = null;
	private PatchExecState _state = null;
	private Thread activeThread = null;

	public ApplyPatchFrame(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	public ApplyPatchFrame(Component owner, CCMovieList ml, FSPath f)
	{
		super(ml);

		initComponents();
		postInit();

		edPathPatchfile.setPath(f);

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());

		edPathDestMovies.setPath(movielist.getCommonMoviesPath().toFSPath(this));
		edPathDestSeries.setPath(movielist.getCommonSeriesPath().toFSPath(this));

		edPathDestTrashMov.setPath(movielist.getCommonMoviesPath().toFSPath(this).getParent().append("trash")); //$NON-NLS-1$
		edPathDestTrashSer.setPath(movielist.getCommonSeriesPath().toFSPath(this).getParent().append("trash")); //$NON-NLS-1$
	}

	private void updateUI()
	{
		btnChoose.setEnabled(activeThread == null);
		btnLoad.setEnabled(activeThread == null);
		btnApply.setEnabled(activeThread == null && _actions != null && _state != null);
		btnCancel.setEnabled(activeThread != null);
	}

	private void choosePatchfile(ActionEvent ae)
	{
		if (activeThread != null) return;

		final JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);

		chooser.addChoosableFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jccpatch.description", ExportHelper.EXTENSION_PATCHFILE)); //$NON-NLS-1$

		chooser.setCurrentDirectory(FilesystemUtils.getRealSelfDirectory().toFile());

		int returnval = chooser.showOpenDialog(this);

		if (returnval == JFileChooser.APPROVE_OPTION) edPathPatchfile.setPath(FSPath.create(chooser.getSelectedFile()));

		tableMain.clearData();
		_actions = null;
		_state = null;

		updateUI();
	}

	@SuppressWarnings("nls")
	private void readPatch(ActionEvent ae)
	{
		if (activeThread != null) {
			updateUI();
			return;
		}

		try
		{
			var r = APFWorker.readPatch(edPathPatchfile.getPath());

			tableMain.setData(r.Item1);
			tableMain.autoResize();
			_actions = r.Item1;
			_state = r.Item2;
		}
		catch (Exception e)
		{
			DialogHelper.showDispatchError(this, "Error", e.toString()); //$NON-NLS-1$
		}

		updateUI();
	}

	@SuppressWarnings("nls")
	private void applyPatch(ActionEvent ae)
	{
		if (activeThread != null)
		{
			updateUI();
			return;
		}

		var actlist = _actions;
		var state   = _state;

		var cb = new DoubleProgressCallbackProgressBarHelper(progressBar1, lblProgress1, progressBar2, lblProgress2);

		var opt = new PatchExecOptions(
				edPathPatchfile.getPath(),
				FSPath.create(edPathPatchfile.getPath().toString()+".state"),
				edPathDestMovies.getPath(),
				edPathDestSeries.getPath(),
				chkbxSeriesAutoPath.isSelected(),
				edPathDestTrashMov.getPath(),
				edPathDestTrashSer.getPath(),
				edPathPatchfile.getPath().getParent().append("patch_data"),
				cbPorcelain.isSelected());

		activeThread = new Thread(() ->
		{
			try
			{
				SwingUtils.invokeLater(() -> MainFrame.getInstance().beginBlockingIntermediate());

				if (!opt.DestinationMovies.exists()) {
					throw new Exception("DestinationMovies does not exist");
				}
				if (!opt.DestinationSeries.exists()) {
					throw new Exception("DestinationMovies does not exist");
				}
				if (!opt.DataDir.exists()) {
					throw new Exception("DestinationMovies does not exist");
				}
				if (!opt.DestinationTrashMovies.exists() && !opt.DestinationTrashMovies.mkdirsSafe()) {
					throw new Exception("DestinationTrashMovies not found");
				}
				if (!opt.DestinationTrashSeries.exists() && !opt.DestinationTrashSeries.mkdirsSafe()) {
					throw new Exception("DestinationTrashSeries not found");
				}

				APFWorker.applyPatch(actlist, movielist, state, opt, cb, (dold, dnew) -> SwingUtils.invokeLater(() ->
				{
					tableMain.changeData(dold, dnew);
					tableMain.scrollIntoView(dnew);
				}));

				SwingUtils.invokeLater(this::updateUI);
			}
			catch (Throwable e)
			{
				DialogHelper.showDispatchError(this, "Error", e.toString()); //$NON-NLS-1$
			}
			finally
			{
				SwingUtils.invokeLater(() -> MainFrame.getInstance().endBlockingIntermediate());
				activeThread = null;
				cb.reset();
				SwingUtils.invokeLater(this::updateUI);
			}
		});
		activeThread.start();

		updateUI();
	}

	@SuppressWarnings("deprecation")
	private void cancelThread(ActionEvent e) {
		if (activeThread == null) { updateUI(); return; }

		activeThread.stop();
		updateUI();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		edPathPatchfile = new JReadableFSPathTextField();
		btnChoose = new JButton();
		label2 = new JLabel();
		edPathDestMovies = new JFSPathTextField();
		label3 = new JLabel();
		edPathDestSeries = new JFSPathTextField();
		chkbxSeriesAutoPath = new JCheckBox();
		label4 = new JLabel();
		edPathDestTrashMov = new JFSPathTextField();
		label5 = new JLabel();
		edPathDestTrashSer = new JFSPathTextField();
		btnLoad = new JButton();
		tableMain = new ActionListTable(this);
		btnApply = new JButton();
		btnCancel = new JButton();
		cbPorcelain = new JCheckBox();
		progressBar1 = new JProgressBar();
		lblProgress1 = new JLabel();
		progressBar2 = new JProgressBar();
		lblProgress2 = new JLabel();

		//======== this ========
		setTitle(LocaleBundle.getString("ApplyPatchFrame.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $lcgap, default, $lcgap, 80dlu, $ugap", //$NON-NLS-1$
			"$ugap, 6*(default, $lgap), default:grow, 3*($lgap, default), $ugap")); //$NON-NLS-1$

		//---- label1 ----
		label1.setText(LocaleBundle.getString("ApplyPatchFrame.lblInput")); //$NON-NLS-1$
		contentPane.add(label1, CC.xy(2, 2));
		contentPane.add(edPathPatchfile, CC.xywh(4, 2, 3, 1, CC.DEFAULT, CC.FILL));

		//---- btnChoose ----
		btnChoose.setText("..."); //$NON-NLS-1$
		btnChoose.addActionListener(e -> choosePatchfile(e));
		contentPane.add(btnChoose, CC.xy(8, 2));

		//---- label2 ----
		label2.setText(LocaleBundle.getString("ApplyPatchFrame.labelMovDest")); //$NON-NLS-1$
		contentPane.add(label2, CC.xy(2, 4));
		contentPane.add(edPathDestMovies, CC.xywh(4, 4, 5, 1, CC.DEFAULT, CC.FILL));

		//---- label3 ----
		label3.setText(LocaleBundle.getString("ApplyPatchFrame.labelDestSeries")); //$NON-NLS-1$
		contentPane.add(label3, CC.xy(2, 6));
		contentPane.add(edPathDestSeries, CC.xy(4, 6, CC.DEFAULT, CC.FILL));

		//---- chkbxSeriesAutoPath ----
		chkbxSeriesAutoPath.setText(LocaleBundle.getString("ApplyPatchFrame.chkbxSeriesAutoPath")); //$NON-NLS-1$
		contentPane.add(chkbxSeriesAutoPath, CC.xywh(6, 6, 3, 1));

		//---- label4 ----
		label4.setText(LocaleBundle.getString("ApplyPatchFrame.labelDestTrashMov")); //$NON-NLS-1$
		contentPane.add(label4, CC.xy(2, 8));
		contentPane.add(edPathDestTrashMov, CC.xywh(4, 8, 5, 1));

		//---- label5 ----
		label5.setText(LocaleBundle.getString("ApplyPatchFrame.labelDestTrashSer")); //$NON-NLS-1$
		contentPane.add(label5, CC.xy(2, 10));
		contentPane.add(edPathDestTrashSer, CC.xywh(4, 10, 5, 1));

		//---- btnLoad ----
		btnLoad.setText(LocaleBundle.getString("ApplyPatchFrame.btnLoad")); //$NON-NLS-1$
		btnLoad.addActionListener(e -> readPatch(e));
		contentPane.add(btnLoad, CC.xywh(2, 12, 7, 1));
		contentPane.add(tableMain, CC.xywh(2, 14, 7, 1, CC.FILL, CC.FILL));

		//---- btnApply ----
		btnApply.setText(LocaleBundle.getString("ApplyPatchFrame.btnApply")); //$NON-NLS-1$
		btnApply.addActionListener(e -> applyPatch(e));
		contentPane.add(btnApply, CC.xywh(2, 16, 3, 1));

		//---- btnCancel ----
		btnCancel.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(e -> cancelThread(e));
		contentPane.add(btnCancel, CC.xy(6, 16));

		//---- cbPorcelain ----
		cbPorcelain.setText(LocaleBundle.getString("BatchEditFrame.cbPorcelain")); //$NON-NLS-1$
		contentPane.add(cbPorcelain, CC.xy(8, 16));
		contentPane.add(progressBar1, CC.xywh(2, 18, 5, 1));
		contentPane.add(lblProgress1, CC.xy(8, 18));
		contentPane.add(progressBar2, CC.xywh(2, 20, 5, 1));
		contentPane.add(lblProgress2, CC.xy(8, 20, CC.FILL, CC.FILL));
		setSize(900, 625);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private JReadableFSPathTextField edPathPatchfile;
	private JButton btnChoose;
	private JLabel label2;
	private JFSPathTextField edPathDestMovies;
	private JLabel label3;
	private JFSPathTextField edPathDestSeries;
	private JCheckBox chkbxSeriesAutoPath;
	private JLabel label4;
	private JFSPathTextField edPathDestTrashMov;
	private JLabel label5;
	private JFSPathTextField edPathDestTrashSer;
	private JButton btnLoad;
	private ActionListTable tableMain;
	private JButton btnApply;
	private JButton btnCancel;
	private JCheckBox cbPorcelain;
	private JProgressBar progressBar1;
	private JLabel lblProgress1;
	private JProgressBar progressBar2;
	private JLabel lblProgress2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
