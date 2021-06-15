package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipMenuBar;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
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
		super(m.getMovieList(), postAction);
		_movie = m;
		_frame = f;
		init();
	}

	@Override
	@SuppressWarnings("nls")
	protected void init()
	{
		addMaster("PreviewMovieFrame.Menubar.Movie");
		{
			addActionNode("PlayMovie");
			addActionNode("PlayMovieAnonymous");
			if (CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue() && !Str.isNullOrWhitespace(MoviePlayer.getVLCPath()))
			{
				addActionNode("QueueMovieInRobot");
			}
			addSeparator();
			addActionTreeNode("SetMovieRating");
			addActionTreeNode("SetTags_Movies");
			addActionNode("SetMovieViewed");
			addActionNode("SetMovieUnviewed");
			addActionNode("UndoMovieViewed");
			addSeparator();
			addActionNode("EditMovie");
			addActionNode("RemMovie");
		}
		addMaster("PreviewMovieFrame.Menubar.Extras");
		{
			addActionNode("OpenFolder");
			addOpenInBrowserActionNodes(_movie.getOnlineReference());
		}
		addMaster("PreviewMovieFrame.Menubar.Export");
		{
			addActionNode("ExportSingleMovie");
			addActionNode("AddMovieToExportList");
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
