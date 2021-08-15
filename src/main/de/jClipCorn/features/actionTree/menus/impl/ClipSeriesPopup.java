package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipPopupMenu;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.listener.ActionCallbackListener;

import java.awt.*;

public class ClipSeriesPopup extends ClipPopupMenu {
	private static final long serialVersionUID = -6475272518552625501L;

	private final CCSeries _series;
	private final Component _frame;

	public ClipSeriesPopup(Component f, CCSeries s) {
		super(s.getMovieList());
		_series = s;
		_frame = f;
		init();
	}

	@SuppressWarnings("nls")
	@Override
	protected void init() {

		if (ccprops().PROP_VLC_ROBOT_ENABLED.getValue() && !FSPath.isNullOrEmpty(MoviePlayer.getVLCPath(ccprops())))
		{
			addAction("QueueSeriesInRobot");

			//#############
			addSeparator();
			//#############
		}

		addAction("PrevSeries");
		addAction("ShowSeriesHistory");

		//#############
		addSeparator();
		//#############
		
		addAction("AddSeason");
		
		//#############
		addSeparator();
		//#############
		
		addAction("ExportSingleSeries");
		addAction("AddSeriesToExportList");
		addAction("SaveTXTEpisodeguide");
		
		//#############
		addSeparator();
		//#############
		
		addActionMenuTree("SetSeriesRating");
		addActionMenuTree("SetTags_Series");

		//#############
		addSeparator();
		//#############

		addAction("OpenFolder");
		addOpenInBrowserAction(_series, _series.getOnlineReference());

		//#############
		addSeparator();
		//#############

		addAction("MoveSeries");
		addAction("CreateFolderStructSeries");
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditSeries");
		addAction("RemSeries");
	}

	@Override
	protected IActionSourceObject getSourceObject() {
		return _series;
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
