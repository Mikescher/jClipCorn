package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipMenuBar;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func0to0;
import de.jClipCorn.util.listener.ActionCallbackListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PreviewSeriesMenuBar extends ClipMenuBar {
	private static final long serialVersionUID = 1164878970843040517L;

	private final CCSeries _series;
	private final PreviewSeriesFrame _frame;

	public PreviewSeriesMenuBar(PreviewSeriesFrame f, CCSeries s, Func0to0 postAction)
	{
		super(postAction);
		_frame = f;
		_series = s;
		init();
	}

	@Override
	@SuppressWarnings("nls")
	protected void init()
	{
		addMaster("PreviewSeriesFrame.Menu.Series");
		{
			if (CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue() && !Str.isNullOrWhitespace(MoviePlayer.getVLCPath()))
			{
				addActionNode("QueueSeriesInRobot");
				addSeparator();
			}
			addActionNode("AddSeason");
			addActionNode("BatchEditSeries");
			addActionNode("EditSeries");
			addActionTreeNode("SetSeriesRating");
			addActionNode("OpenFolder");
			addActionNode("RemSeries");
		}
		addMaster("PreviewSeriesFrame.Menu.Season");
		{
			if (CCProperties.getInstance().PROP_VLC_ROBOT_ENABLED.getValue() && !Str.isNullOrWhitespace(MoviePlayer.getVLCPath()))
			{
				addActionNode("QueueSeasonInRobot");
				addSeparator();
			}
			addActionNode("AddEpisodes");
			addActionNode("AddSingleEpisodes");
			addActionNode("BatchEditSeason");
			addActionNode("RemSeason");
			addActionNode("EditSeason");
			addActionNode("OpenSeasonFolder");
		}
		addMaster("PreviewSeriesFrame.Menu.Extras");
		{
			addActionTreeNode("PlayRandomEpisode");
			addOpenInBrowserActionNodes(_series.getOnlineReference());
			addActionNode("ExportSingleSeries");
			addActionNode("SaveTXTEpisodeguide");
			addActionNode("MoveSeries");
			addActionNode("CreateFolderStructSeries");
		}
	}

	@Override
	protected List<IActionSourceObject> getActionSourceObject() {
		List<IActionSourceObject> r = new ArrayList<>();
		if (_frame.getSelectedEpisode() != null) r.add(_frame.getSelectedEpisode());
		if (_frame.getSelectedSeason() != null) r.add(_frame.getSelectedSeason());
		r.add(_series);
		return r;
	}

	@Override
	protected ActionCallbackListener getSourceListener() {
		return _frame;
	}

	@Override
	protected Component getSourceComponent() {
		return _frame;
	}

	@Override
	protected ActionSource getActionSource() {
		return ActionSource.OTHER_MENU_BAR;
	}
}
