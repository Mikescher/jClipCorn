package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipPopupMenu;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.listener.ActionCallbackListener;
import java.awt.*;

public class ClipMoviePopup extends ClipPopupMenu {
	private static final long serialVersionUID = -3030683884876620182L;

	private final CCMovie _movie;
	private final Component _frame;

	public ClipMoviePopup(Component f, CCMovie m) {
		super();
		_movie = m;
		_frame = f;
		init();
	}

	@SuppressWarnings("nls")
	@Override
	protected void init() {
		addPlayAction(_movie, false);
		addPlayAction(_movie, true);

		if (CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue() && !Str.isNullOrWhitespace(MoviePlayer.getVLCPath()))
		{
			addAction("QueueMovieInRobot");
		}

		addAction("PrevMovie");
		addAction("ShowMovieHistory");
		
		//#############
		addSeparator();
		//#############
		
		addActionMenuTree("SetMovieRating");
		addActionMenuTree("SetTags_Movies");
		addAction("SetMovieViewed");
		addAction("SetMovieUnviewed");
		addAction("UndoMovieViewed");
		
		//#############
		addSeparator();
		//#############
		
		addAction("ExportSingleMovie");
		addAction("AddMovieToExportList");
		
		//#############
		addSeparator();
		//#############
		
		addAction("OpenFolder");
		addOpenInBrowserAction(_movie, _movie.getOnlineReference());
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditMovie");
		addAction("RemMovie");
	}

	@Override
	protected IActionSourceObject getSourceObject() {
		return _movie;
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
