package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipMenuBar;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.util.lambda.Func0to0;
import de.jClipCorn.util.listener.ActionCallbackListener;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PreviewMovieMenuBar extends ClipMenuBar {
	private static final long serialVersionUID = 1164878970843040517L;
	
	private CCMovie _movie;
	private final PreviewMovieFrame _frame;

	public PreviewMovieMenuBar(PreviewMovieFrame f, CCMovie m, Func0to0 postAction)
	{
		super(postAction);
		_movie = m;
		_frame = f;
		init();
	}

	@Override
	@SuppressWarnings("nls")
	protected void init()
	{
		addMaster("PreviewMovieFrame.Menubar.Movie"); //$NON-NLS-1$
		{
			addActionNode("PlayMovie"); //$NON-NLS-1$
			addActionNode("PlayMovieAnonymous"); //$NON-NLS-1$
			addSeparator();
			addActionTreeNode("SetMovieRating"); //$NON-NLS-1$
			addActionTreeNode("SetTags_Movies"); //$NON-NLS-1$
			addActionNode("SetMovieViewed"); //$NON-NLS-1$
			addActionNode("SetMovieUnviewed"); //$NON-NLS-1$
			addActionNode("UndoMovieViewed"); //$NON-NLS-1$
			addSeparator();
			addActionNode("EditMovie"); //$NON-NLS-1$
			addActionNode("RemMovie"); //$NON-NLS-1$
		}
		addMaster("PreviewMovieFrame.Menubar.Extras"); //$NON-NLS-1$
		{
			addActionNode("OpenFolder"); //$NON-NLS-1$
			addOpenInBrowserActionNodes(_movie.getOnlineReference());
		}
		addMaster("PreviewMovieFrame.Menubar.Export"); //$NON-NLS-1$
		{
			addActionNode("ExportSingleMovie"); //$NON-NLS-1$
			addActionNode("AddMovieToExportList"); //$NON-NLS-1$
		}
	}

	@Override
	protected List<IActionSourceObject> getActionSourceObject() {
		return Collections.singletonList(_movie);
	}

	@Override
	protected ActionSource getActionSource() {
		return ActionSource.OTHER_MENU_BAR;
	}

	@Override
	protected ActionCallbackListener getSourceListener() {
		return new ActionCallbackListener()
		{
			@Override
			public void onUpdate(Object o) { _frame.onUpdate(o); }
			@Override
			public void onCallbackPlayed(CCEpisode e) { /* */ }
		};
	}

	@Override
	protected Component getSourceComponent() {
		return _frame;
	}
}
