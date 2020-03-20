package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipPopupMenu;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.listener.ActionCallbackListener;

import java.awt.*;

public class ClipEpisodePopup extends ClipPopupMenu {
	private static final long serialVersionUID = -6475272518552625501L;

	private final CCEpisode _episode;
	private final Component _frame;

	public ClipEpisodePopup(Component f, CCEpisode e) {
		super();
		_episode = e;
		_frame = f;
		init();
	}

	@SuppressWarnings("nls")
	@Override
	protected void init() {
		addAction("PlayEpisode");
		addAction("PlayEpisodeAnonymous");

		if (CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue() && !Str.isNullOrWhitespace(MoviePlayer.getVLCPath()))
			addAction("QueueEpisodeInRobot");

		//#############
		addSeparator();
		//#############

		addAction("ShowEpisodeHistory");

		//#############
		addSeparator();
		//#############

		addAction("SetEpisodeViewed");
		addAction("SetEpisodeUnviewed");
		addAction("UndoEpisodeViewed");

		//#############
		addSeparator();
		//#############
		
		addActionMenuTree("SetTags_Episode");
		addAction("OpenEpisodeFolder");

		//#############
		addSeparator();
		//#############

		addAction("EditEpisode");
		addAction("RemEpisode");

	}

	@Override
	protected IActionSourceObject getSourceObject() {
		return _episode;
	}

	@Override
	protected Component getSourceFrame() {
		return _frame;
	}

	@Override
	protected ActionCallbackListener getSourceListener() {
		return null;
	}
}
