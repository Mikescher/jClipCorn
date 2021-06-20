package de.jClipCorn.gui.frames.applyPatchFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.DoubleProgressCallbackProgressBarHelper;
import de.jClipCorn.util.xml.CCXMLParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public class ApplyPatchFrame extends JFrame
{
	private final CCMovieList movielist;

	private java.util.List<ActionVM> _actions = null;
	private PatchExecState _state = null;
	private Thread activeThread = null;

	public ApplyPatchFrame(Component owner, CCMovieList ml)
	{
		super();
		movielist = ml;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	public ApplyPatchFrame(Component owner, CCMovieList ml, File f)
	{
		super();
		movielist = ml;

		initComponents();
		postInit();

		edPathPatchfile.setText(f.getAbsolutePath());

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());

		edPathDestMovies.setText(PathFormatter.fromCCPath(movielist.getCommonMoviesPath()));
		edPathDestSeries.setText(PathFormatter.fromCCPath(movielist.getCommonSeriesPath()));

		edPathDestTrashMov.setText(PathFormatter.combine(PathFormatter.getParentPath(PathFormatter.fromCCPath(movielist.getCommonMoviesPath()), 1), "trash"));
		edPathDestTrashSer.setText(PathFormatter.combine(PathFormatter.getParentPath(PathFormatter.fromCCPath(movielist.getCommonSeriesPath()), 1), "trash"));
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

		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(this);

		if (returnval == JFileChooser.APPROVE_OPTION) edPathPatchfile.setText(chooser.getSelectedFile().getAbsolutePath());

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
			var state = new PatchExecState();
			state.load(edPathPatchfile.getText() + ".state");

			CCXMLParser doc = CCXMLParser.parse(SimpleFileUtils.readUTF8TextFile(edPathPatchfile.getText()));

			var actions = new ArrayList<ActionVM>();

			for (var xelem: doc.getRoot().getAllChildren("action").autosortByProperty(p -> Integer.parseInt(p.getAttributeValueOrDefault("ctr", null))))
			{
				actions.add(new ActionVM(xelem, state));
			}

			tableMain.setData(actions);
			tableMain.autoResize();
			_actions = actions;
			_state = null;
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
				edPathPatchfile.getText(),
				edPathPatchfile.getText()+".state",
				edPathDestMovies.getText(),
				edPathDestSeries.getText(),
				edPathDestTrashMov.getText(),
				edPathDestTrashSer.getText(),
				PathFormatter.combine(edPathPatchfile.getText(), "patch_data"),
				cbPorcelain.isSelected());

		activeThread = new Thread(() ->
		{
			try
			{
				SwingUtils.invokeLater(() -> MainFrame.getInstance().beginBlockingIntermediate());

				if (!new File(opt.DestinationMovies).exists()) {
					throw new Exception("DestinationMovies does not exist");
				}
				if (!new File(opt.DestinationSeries).exists()) {
					throw new Exception("DestinationMovies does not exist");
				}
				if (!new File(opt.DataDir).exists()) {
					throw new Exception("DestinationMovies does not exist");
				}
				if (!new File(opt.DestinationTrashMovies).exists() && !new File(opt.DestinationTrashMovies).mkdirs()) {
					throw new Exception("DestinationTrashMovies not found");
				}
				if (!new File(opt.DestinationTrashSeries).exists() && !new File(opt.DestinationTrashSeries).mkdirs()) {
					throw new Exception("DestinationTrashSeries not found");
				}



				APFWorker.applyPatch(actlist, movielist, state, opt, cb, (dold, dnew) -> SwingUtils.invokeLater(() -> tableMain.changeData(dold, dnew)));

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
		edPathPatchfile = new JTextField();
		btnChoose = new JButton();
		label2 = new JLabel();
		edPathDestMovies = new JTextField();
		label3 = new JLabel();
		edPathDestSeries = new JTextField();
		label4 = new JLabel();
		edPathDestTrashMov = new JTextField();
		label5 = new JLabel();
		edPathDestTrashSer = new JTextField();
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
			"$ugap, default, $lcgap, default:grow, $lcgap, default, $lcgap, [80dlu,default], $ugap", //$NON-NLS-1$
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
		contentPane.add(edPathDestSeries, CC.xywh(4, 6, 5, 1, CC.DEFAULT, CC.FILL));

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
	private JTextField edPathPatchfile;
	private JButton btnChoose;
	private JLabel label2;
	private JTextField edPathDestMovies;
	private JLabel label3;
	private JTextField edPathDestSeries;
	private JLabel label4;
	private JTextField edPathDestTrashMov;
	private JLabel label5;
	private JTextField edPathDestTrashSer;
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
