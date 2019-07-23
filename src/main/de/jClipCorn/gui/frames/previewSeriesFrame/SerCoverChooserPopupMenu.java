package de.jClipCorn.gui.frames.previewSeriesFrame;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.gui.frames.addMultiEpisodesFrame.AddMultiEpisodesFrame;
import de.jClipCorn.gui.frames.batchEditFrame.BatchEditFrame;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;

public class SerCoverChooserPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1726753702894240378L;

	private final CCSeason season;
	private final PreviewSeriesFrame owner;

	public SerCoverChooserPopupMenu(CCSeason s, PreviewSeriesFrame frame) {
		super();
		this.season = s;
		this.owner = frame;

		JMenuItem addEpisodes = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.AddEpisodes"), Resources.ICN_MENUBAR_ADD_SEA.get16x16()); //$NON-NLS-1$
		addEpisodes.addActionListener(arg0 -> new AddMultiEpisodesFrame(owner, season, owner).setVisible(true));
		add(addEpisodes);

		JMenuItem batchEpisodes = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.BatchEdit"), Resources.ICN_MENUBAR_BATCHEDIT.get16x16()); //$NON-NLS-1$
		batchEpisodes.addActionListener(arg0 -> new BatchEditFrame(owner, season, owner).setVisible(true));
		add(batchEpisodes);
		
		JMenuItem editSeason = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.Edit"), Resources.ICN_MENUBAR_EDIT_SER.get16x16()); //$NON-NLS-1$
		editSeason.addActionListener(arg0 -> new EditSeriesFrame(owner, season, owner).setVisible(true));
		add(editSeason);
		
		JMenuItem openFolder = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.OpenFolder"), Resources.ICN_MENUBAR_FOLDER.get16x16()); //$NON-NLS-1$
		openFolder.addActionListener(arg0 -> {
			PathFormatter.showInExplorer(PathFormatter.fromCCPath(season.getCommonPathStart()));
			owner.updateSeason();
		});
		add(openFolder);
		
		JMenuItem delSeason = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.Delete"), Resources.ICN_MENUBAR_REMOVE.get16x16()); //$NON-NLS-1$
		delSeason.addActionListener(arg0 -> {
			if (DialogHelper.showLocaleYesNo(owner, "Dialogs.DeleteSeason")) { //$NON-NLS-1$
				season.delete();
				owner.updateSeason();
			}
		});
		add(delSeason);
		
		addSeparator();
		
		JMenuItem showCover = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.ShowCover"), Resources.ICN_MENUBAR_SHOWCOVER.get16x16()); //$NON-NLS-1$
		showCover.addActionListener(arg0 -> new CoverPreviewFrame(SerCoverChooserPopupMenu.this, s).setVisible(true));
		add(showCover);
		
		JMenuItem saveCover = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.SaveCover"), Resources.ICN_MENUBAR_SAVE.get16x16()); //$NON-NLS-1$
		saveCover.addActionListener(arg0 -> {
			JFileChooser fc = new JFileChooser();
			fc.setAcceptAllFileFilterUsed(false);

			FileFilter filterPng;
			FileFilter filterJpg;
			FileFilter filterBmp;

			fc.setFileFilter(filterPng = FileChooserHelper.createFileFilter("PNG-Image (*.png)", "png")); //$NON-NLS-1$ //$NON-NLS-2$

			fc.addChoosableFileFilter(filterJpg = FileChooserHelper.createFileFilter("JPEG-Image (*.jpg)", "jpg", "jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			fc.addChoosableFileFilter(filterBmp = FileChooserHelper.createFileFilter("Bitmap-Image (*.bmp)", "bmp")); //$NON-NLS-1$ //$NON-NLS-2$

			if (fc.showSaveDialog(SerCoverChooserPopupMenu.this) == JFileChooser.APPROVE_OPTION) {
				FileFilter choosen = fc.getFileFilter();
				String path = fc.getSelectedFile().getAbsolutePath();
				String format = ""; //$NON-NLS-1$

				if (choosen.equals(filterPng)) {
					format = "png"; //$NON-NLS-1$

				} else if (choosen.equals(filterJpg)) {
					format = "jpg"; //$NON-NLS-1$

				} else if (choosen.equals(filterBmp)) {
					format = "bmp"; //$NON-NLS-1$
				}

				path = PathFormatter.forceExtension(path, format);

				try {
					ImageIO.write(season.getCover(), format, new File(path));
				} catch (IOException e) {
					CCLog.addError(e);
				}
			}
		});
		add(saveCover);
	}
}
