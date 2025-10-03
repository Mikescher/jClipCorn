package de.jClipCorn.gui.frames.scanFolderFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.jSplitButton.JSplitButton;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ScanFolderFrame extends JCCFrame
{
	@SuppressWarnings("nls")
	private final static String REGEX_PART_N = ".*\\(Part [2-9]\\)\\.[A-Za-z0-9]{2,4}"; // .*\(Part [2-9]\)\.[A-Za-z0-9]{2,4}

	private final JFileChooser folderchooser;
	private final MainFrame owner;

	private DefaultListModel<FSPath> lsModel;

	public ScanFolderFrame(MainFrame owner, CCMovieList ml)
	{
		super(ml);
		this.folderchooser = new JFileChooser(ml.getCommonPathForMovieFileChooser().toFile());
		this.owner = owner;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		ccprops().PROP_FSIZE_SCANFOLDERFRAME.applyOrSkip(this);

		cbIncludeSeries.setSelected(ccprops().PROP_SCANFOLDER_INCLUDESERIES.getValue());
		cbExcludeIfo.setSelected(ccprops().PROP_SCANFOLDER_EXCLUDEIFOS.getValue());

		lsFiles.setModel(lsModel = new DefaultListModel<>());

		initFileChooser();
		edPath.setText(getMovieList().getCommonPathForMovieFileChooser().toAbsolutePathString());
		
		initFindButton();

		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]
		{
			btnOpenFolder,
			btnRemoveAdditionalParts,
			btnAddAll,
			cbIncludeSeries,
			cbExcludeIfo,
		}));
	}

	private void initFileChooser() {
		folderchooser.setDialogTitle(LocaleBundle.getString("ScanFolderFrame.dlg.title")); //$NON-NLS-1$
		folderchooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
	}

	@SuppressWarnings("nls")
	private void initFindButton() {

		var popupMenu = new JPopupMenu();
		{
			{
				var p0 = FilesystemUtils.getAbsoluteSelfDirectory(ccprops());
				if (!p0.isEmpty()) {
					var menuitem = new JMenuItem("Self (" + p0.toAbsolutePathString() + ")");
					menuitem.addActionListener(e -> edPath.setText(p0.toAbsolutePathString()));
					popupMenu.add(menuitem);
				}
			}

			popupMenu.addSeparator();

			{
				var p0 = getMovieList().getCommonMoviesPath();
				if (!p0.isEmpty()) {
					var menuitem = new JMenuItem("Movies (" + p0.toFSPath(ccprops()).toAbsolutePathString() + ")");
					menuitem.addActionListener(e -> edPath.setText(p0.toFSPath(ccprops()).toAbsolutePathString()));
					popupMenu.add(menuitem);
				}
			}

			{
				var p0 = getMovieList().getCommonSeriesPath();
				if (!p0.isEmpty()) {
					var menuitem = new JMenuItem("Series (" + p0.toFSPath(ccprops()).toAbsolutePathString() + ")");
					menuitem.addActionListener(e -> edPath.setText(p0.toFSPath(ccprops()).toAbsolutePathString()));
					popupMenu.add(menuitem);
				}
			}

			popupMenu.addSeparator();
			
			for (var path : ccprops().getActivePathVariables()) {
				var fspath = path.Value.toFSPath(ccprops());
				var menuitem = new JMenuItem("Path<"+path.Key+"> (" + fspath.toAbsolutePathString() + ")");
				menuitem.addActionListener(e -> edPath.setText(fspath.toAbsolutePathString()));
				popupMenu.add(menuitem);
			}
		}

		btnDialog.setPopupMenu(popupMenu);
	} 
	
	private void runThread(FSPath dir, boolean includeSeries, boolean excludeIfos) {
		SwingUtils.invokeLater(() -> {
			btnOpenFolder.setEnabled(false);
			btnDialog.setEnabled(false);
			lsModel.clear();
			progressBar.setIndeterminate(true);
		});

		// List of Files in Directory
		var filelist = new ArrayList<FSPath>();
		searchFiles(dir, filelist, excludeIfos);

		// List of Files in in Database
		var movielist = owner.getMovielist().getAbsolutePathList(includeSeries);

		filelist.removeAll(movielist);

		for (var f : filelist) {
			addToList(f);
		}

		SwingUtils.invokeLater(() ->
		{
			btnRemoveAdditionalParts.setEnabled(true);
			btnAddAll.setEnabled(true);
			cbIncludeSeries.setEnabled(false);
			cbExcludeIfo.setEnabled(false);
			progressBar.setIndeterminate(false);
			updateCount();
		});
	}

	private void scan(ActionEvent e) {

		var includeSeries = cbIncludeSeries.isSelected();
		var excludeIfos = cbExcludeIfo.isSelected();

		var dir = FSPath.create(edPath.getText());

		if (!dir.isDirectory()) {
			DialogHelper.showDispatchLocalInformation(this, "Dialogs.NotADirectory"); //$NON-NLS-1$
			return;
		}

		Thread run = new Thread(() -> this.runThread(dir, includeSeries, excludeIfos), "THREAD_SCAN_FOLDER_FOR_MOVIES"); //$NON-NLS-1$
		run.start();
	}

	private void searchFiles(FSPath dir, java.util.List<FSPath> filelist, boolean excludeIfos) {

		var files = dir.list(p ->
		{
			if (p.isDirectory()) return true;

			if (excludeIfos && p.getExtension().equalsIgnoreCase("ifo")) return false; //$NON-NLS-1$

			return CCFileFormat.isValidMovieFormat(p);
		});

		for (var f : files) {
			if (f.isDirectory()) {
				searchFiles(f, filelist, excludeIfos);
			} else {
				filelist.add(f);
			}
		}
	}

	private void addToList(final FSPath f) {
		SwingUtils.invokeLater(() -> lsModel.addElement(f));
	}

	private void removeAdditional(ActionEvent e) {
		for (int i = lsModel.size() - 1; i >= 0; i--) {
			if (lsModel.get(i).toString().matches(REGEX_PART_N)) {
				lsModel.remove(i);
			}
		}

		updateCount();
	}

	private void updateCount() {
		btnAddAll.setText(LocaleBundle.getString("ScanFolderFrame.btnAddAll.text") + String.format(" (%03d)", lsModel.size()));  //$NON-NLS-1$//$NON-NLS-2$
	}

	private void addAll(ActionEvent e) {
		if (lsModel.size() > 16) {
			DialogHelper.showDispatchLocalInformation(this, "Dialogs.TooManyAddFrames"); //$NON-NLS-1$
			return;
		}

		for (int i = 0; i < lsModel.size(); i++) {
			var path = lsModel.get(i);
			AddMovieFrame amf = new AddMovieFrame(this, owner.getMovielist(), path);
			amf.setVisible(true);
		}
		dispose();
	}

	private void lsFilesMouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && lsFiles.getSelectedIndex() >= 0) {
			var path = lsFiles.getSelectedValue();
			lsModel.remove(lsFiles.getSelectedIndex());
			AddMovieFrame amf = new AddMovieFrame(this, owner.getMovielist(), path);
			amf.setVisible(true);

			updateCount();
		}
	}

	private void openFolder(ActionEvent e) {
		if (folderchooser.showOpenDialog(ScanFolderFrame.this) == JFileChooser.APPROVE_OPTION) {
			edPath.setText(folderchooser.getSelectedFile().getAbsolutePath());
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		edPath = new JTextField();
		btnDialog = new JSplitButton();
		scrollPane1 = new JScrollPane();
		lsFiles = new JList<>();
		btnOpenFolder = new JButton();
		progressBar = new JProgressBar();
		btnRemoveAdditionalParts = new JButton();
		btnAddAll = new JButton();
		cbIncludeSeries = new JCheckBox();
		cbExcludeIfo = new JCheckBox();

		//======== this ========
		setTitle(LocaleBundle.getString("ScanFolderFrame.dlg.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(400, 300));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 0dlu:grow, $lcgap, 60dlu, 2*($lcgap, 0dlu:grow), $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, 2*($lgap, default), $ugap")); //$NON-NLS-1$
		contentPane.add(edPath, CC.xywh(2, 2, 5, 1));

		//---- btnDialog ----
		btnDialog.setText("..."); //$NON-NLS-1$
		btnDialog.addButtonClickedActionListener(e -> openFolder(e));
		contentPane.add(btnDialog, CC.xy(8, 2));

		//======== scrollPane1 ========
		{

			//---- lsFiles ----
			lsFiles.setVisibleRowCount(24);
			lsFiles.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					lsFilesMouseClicked(e);
				}
			});
			scrollPane1.setViewportView(lsFiles);
		}
		contentPane.add(scrollPane1, CC.xywh(2, 4, 7, 1, CC.FILL, CC.FILL));

		//---- btnOpenFolder ----
		btnOpenFolder.setText(LocaleBundle.getString("ScanFolderFrame.btnChooseFolder.text")); //$NON-NLS-1$
		btnOpenFolder.addActionListener(e -> scan(e));
		contentPane.add(btnOpenFolder, CC.xy(2, 6));
		contentPane.add(progressBar, CC.xy(4, 6, CC.DEFAULT, CC.FILL));

		//---- btnRemoveAdditionalParts ----
		btnRemoveAdditionalParts.setText(LocaleBundle.getString("ScanFolderFrame.btnCleanUp.text")); //$NON-NLS-1$
		btnRemoveAdditionalParts.setEnabled(false);
		btnRemoveAdditionalParts.addActionListener(e -> removeAdditional(e));
		contentPane.add(btnRemoveAdditionalParts, CC.xy(6, 6));

		//---- btnAddAll ----
		btnAddAll.setText(LocaleBundle.getString("ScanFolderFrame.btnAddAll.text")); //$NON-NLS-1$
		btnAddAll.setEnabled(false);
		btnAddAll.addActionListener(e -> addAll(e));
		contentPane.add(btnAddAll, CC.xy(8, 6));

		//---- cbIncludeSeries ----
		cbIncludeSeries.setText(LocaleBundle.getString("ScanFolderFrame.cbIncludeSeries.text")); //$NON-NLS-1$
		contentPane.add(cbIncludeSeries, CC.xywh(2, 8, 3, 1));

		//---- cbExcludeIfo ----
		cbExcludeIfo.setText(LocaleBundle.getString("ScanFolderFrame.cbExcludeIfos.text")); //$NON-NLS-1$
		contentPane.add(cbExcludeIfo, CC.xywh(6, 8, 3, 1));
		setSize(600, 500);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTextField edPath;
	private JSplitButton btnDialog;
	private JScrollPane scrollPane1;
	private JList<FSPath> lsFiles;
	private JButton btnOpenFolder;
	private JProgressBar progressBar;
	private JButton btnRemoveAdditionalParts;
	private JButton btnAddAll;
	private JCheckBox cbIncludeSeries;
	private JCheckBox cbExcludeIfo;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
