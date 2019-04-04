package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;

public class SerTablePopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 1726753702894240378L;

	private final CCEpisode episode;
	private final PreviewSeriesFrame owner;

	public SerTablePopupMenu(CCEpisode e, PreviewSeriesFrame frame) {
		super();
		this.episode = e;
		this.owner = frame;

		JMenuItem play = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.Play"), Resources.ICN_MENUBAR_PLAY.get16x16()); //$NON-NLS-1$
		play.addActionListener(arg0 -> owner.onEpisodeDblClick(episode));
		add(play);
		
		JMenuItem playHidden = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.PlayHidden"), Resources.ICN_MENUBAR_HIDDENPLAY.get16x16()); //$NON-NLS-1$
		playHidden.addActionListener(arg0 -> episode.play(false));
		add(playHidden);
		
		//#############
		addSeparator();
		//#############

		JMenuItem setViewed = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.SetViewed"), Resources.ICN_SIDEBAR_VIEWED.get()); //$NON-NLS-1$
		setViewed.addActionListener(arg0 ->
		{
			episode.setViewed(true);
			episode.addToViewedHistory(CCDateTime.getCurrentDateTime());
			owner.updateSeason();
		});
		add(setViewed);

		JMenuItem undoViewed = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.UndoViewed"), Resources.ICN_MENUBAR_UNDOVIEWED.get16x16()); //$NON-NLS-1$
		undoViewed.addActionListener(arg0 ->
		{
			if (episode != null && episode.isViewed()) {
				CCDateTimeList history = episode.getViewedHistory();
				history = history.removeLast();
				episode.setViewedHistory(history);

				if (history.isEmpty())
					episode.setViewed(false);
			}
		});
		add(undoViewed);

		JMenuItem setUnviewed = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.SetUnviewed"), Resources.ICN_SIDEBAR_UNVIEWED.get()); //$NON-NLS-1$
		setUnviewed.addActionListener(arg0 ->
		{
			episode.setViewed(false);
			owner.updateSeason();
		});
		add(setUnviewed);
		
		//#############
		addSeparator();
		//#############
		
		JMenu tags = new JMenu(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.ChangeTags")); //$NON-NLS-1$
		tags.setIcon(Resources.ICN_MENUBAR_TAGS.get16x16());
		for (final CCSingleTag tag : CCTagList.TAGS) {
			if (!tag.IsEpisodeTag) continue;
			JMenuItem mi = new JMenuItem(tag.Description, tag.getOnIcon());
			mi.addActionListener(e1 ->
			{
				episode.switchTag(tag);
				owner.updateSeason();
			});
			tags.add(mi);
		}
		add(tags);

		JMenuItem openFolder = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.OpenFolder"), Resources.ICN_MENUBAR_FOLDER.get16x16()); //$NON-NLS-1$
		openFolder.addActionListener(arg0 ->
		{
			PathFormatter.showInExplorer(episode.getAbsolutePart());
			owner.updateSeason();
		});
		add(openFolder);
		
		//#############
		addSeparator();
		//#############

		JMenuItem edit = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.Edit"), Resources.ICN_MENUBAR_EDIT_MOV.get16x16()); //$NON-NLS-1$
		edit.addActionListener(arg0 ->
		{
			EditSeriesFrame esf = new EditSeriesFrame(owner, episode, owner);
			esf.setVisible(true);
		});
		add(edit);

		JMenuItem delete = new JMenuItem(LocaleBundle.getString("PreviewSeriesFrame.PopupMenu.Delete"), Resources.ICN_MENUBAR_REMOVE.get16x16()); //$NON-NLS-1$
		delete.addActionListener(arg0 ->
		{
			if (DialogHelper.showLocaleYesNo(owner, "Dialogs.DeleteEpisode")) { //$NON-NLS-1$
				episode.delete();
				owner.updateSeason();
			}
		});
		add(delete);
	}
}
