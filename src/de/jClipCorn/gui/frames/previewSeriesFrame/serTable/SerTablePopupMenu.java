package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.PathFormatter;

public class SerTablePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1726753702894240378L;

	private final CCEpisode episode;
	private final PreviewSeriesFrame owner;

	public SerTablePopupMenu(CCEpisode e, PreviewSeriesFrame frame) {
		super();
		this.episode = e;
		this.owner = frame;

		JMenu tags = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.ChangeTags")); //$NON-NLS-1$
		tags.setIcon(CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_TAGS));
		for (int i = 0; i < CCMovieTags.ACTIVETAGS; i++) {
			JMenuItem mi = new JMenuItem(CCMovieTags.getName(i), CCMovieTags.getOnIcon(i));
			final int curr = i;
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					episode.switchTag(curr);
					
					owner.updateSeason();
				}
			});
			tags.add(mi);
		}
		add(tags);

		JMenuItem openFolder = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.OpenFolder"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_FOLDER)); //$NON-NLS-1$
		openFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PathFormatter.showInExplorer(episode.getAbsolutePart());
				owner.updateSeason();
			}
		});
		add(openFolder);

		JMenuItem setViewed = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.SetViewed"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_SIDEBAR_VIEWED)); //$NON-NLS-1$
		setViewed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				episode.setViewed(true);
				episode.setLastViewed(new CCDate());
				owner.updateSeason();
			}
		});
		add(setViewed);

		JMenuItem setUnviewed = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.SetUnviewed"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_SIDEBAR_UNVIEWED)); //$NON-NLS-1$
		setUnviewed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				episode.setViewed(false);
				owner.updateSeason();
			}
		});
		add(setUnviewed);

		JMenuItem edit = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.Edit"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_EDIT_MOV)); //$NON-NLS-1$
		edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EditSeriesFrame esf = new EditSeriesFrame(owner, episode, owner);
				esf.setVisible(true);
			}
		});
		add(edit);

		JMenuItem delete = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.Delete"), CachedResourceLoader.getSmallImageIcon(Resources.ICN_MENUBAR_REMOVE)); //$NON-NLS-1$
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (DialogHelper.showLocaleYesNo(owner, "Dialogs.DeleteEpisode")) { //$NON-NLS-1$
					episode.delete();
					owner.updateSeason();
				}
			}
		});
		add(delete);
	}
}
