package de.jClipCorn.gui.frames.previewSeriesFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.addEpisodesFrame.AddEpisodesFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.PathFormatter;

public class SerCoverChooserPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1726753702894240378L;

	private final CCSeason season;
	private final PreviewSeriesFrame owner;

	public SerCoverChooserPopupMenu(CCSeason s, PreviewSeriesFrame frame) {
		super();
		this.season = s;
		this.owner = frame;

		JMenuItem addEpisodes = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.AddEpisodes"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_ADD_SEA)); //$NON-NLS-1$
		addEpisodes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AddEpisodesFrame aef = new AddEpisodesFrame(owner, season, owner);
				aef.setVisible(true);
			}
		});
		add(addEpisodes);
		
		JMenuItem editSeason = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.Edit"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_EDIT_SER)); //$NON-NLS-1$
		editSeason.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EditSeriesFrame esf = new EditSeriesFrame(owner, season, owner);
				esf.setVisible(true);
			}
		});
		add(editSeason);
		
		JMenuItem openFolder = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.OpenFolder"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_FOLDER)); //$NON-NLS-1$
		openFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PathFormatter.showInExplorer(PathFormatter.getAbsolute(season.getCommonPathStart()));
				owner.updateSeason();
			}
		});
		add(openFolder);
		
		JMenuItem delSeason = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.Delete"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_REMOVE)); //$NON-NLS-1$
		delSeason.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (DialogHelper.showLocaleYesNo(owner, "Dialogs.DeleteSeason")) { //$NON-NLS-1$
					season.delete();
					owner.updateSeason();
				}
			}
		});
		add(delSeason);
		
		addSeparator();
		
		JMenuItem saveCover = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenuCover.SaveCover"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_SAVE)); //$NON-NLS-1$
		saveCover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
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
			}
		});
		add(saveCover);
	}
}
