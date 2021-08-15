package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipPopupMenu;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.listener.ActionCallbackListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.IOException;

public class SerCoverChooserPopupMenu extends ClipPopupMenu {
	private static final long serialVersionUID = 1726753702894240378L;

	private final CCSeason season;
	private final PreviewSeriesFrame owner;

	public SerCoverChooserPopupMenu(CCSeason s, PreviewSeriesFrame frame) {
		super(s.getMovieList());
		this.season = s;
		this.owner = frame;
		init();
	}

	@SuppressWarnings("nls")
	@Override
	protected void init() {
		addAction("AddEpisodes");
		addAction("BatchEditSeason");
		addAction("EditSeason");
		addAction("OpenSeasonFolder");
		addAction("RemSeason");

		addSeparator();

		addAction("ShowSeasonHistory");

		addSeparator();

		addCustomAction(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.ShowCover"), Resources.ICN_MENUBAR_SHOWCOVER, this::showCover);
		addCustomAction(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.SaveCover"), Resources.ICN_MENUBAR_SAVE,      this::saveCover);
	}

	@Override
	protected IActionSourceObject getSourceObject() {
		return season;
	}

	@Override
	protected Component getSourceFrame() {
		return owner;
	}

	@Override
	protected ActionCallbackListener getSourceListener() {
		return new ActionCallbackListener()
		{
			@Override
			public void onUpdate(Object o) { owner.onUpdate(o); }
			@Override
			public void onCallbackPlayed(CCEpisode e) { /* */ }
		};
	}

	private void saveCover() {
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
			var path = FSPath.create(fc.getSelectedFile());
			String format = ""; //$NON-NLS-1$

			if (choosen.equals(filterPng)) format = "png"; //$NON-NLS-1$
			if (choosen.equals(filterJpg)) format = "jpg"; //$NON-NLS-1$
			if (choosen.equals(filterBmp)) format = "bmp"; //$NON-NLS-1$

			path = path.forceExtension(format);

			try {
				ImageIO.write(season.getCover(), format, path.toFile());
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}

	private void showCover() {
		new CoverPreviewFrame(SerCoverChooserPopupMenu.this, season).setVisible(true);
	}

}
