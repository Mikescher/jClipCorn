package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipMenuBar;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.lambda.Func0to0;

public class PreviewMovieMenuBar extends ClipMenuBar {
	private static final long serialVersionUID = 1164878970843040517L;
	
	private final CCMovie _movie;

	public PreviewMovieMenuBar(CCMovie m, Func0to0 postAction)
	{
		super(postAction);
		_movie = m;
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
			addOpenInBrowserActionNodets();
		}
		addMaster("PreviewMovieFrame.Menubar.Export"); //$NON-NLS-1$
		{
			addActionNode("ExportSingleMovie"); //$NON-NLS-1$
			addActionNode("AddMovieToExportList"); //$NON-NLS-1$
		}
	}

	@Override
	protected IActionSourceObject getActionSourceObject() {
		return _movie;
	}

	@Override
	protected ActionSource getActionSource() {
		return ActionSource.OTHER_MENU_BAR;
	}

	private void addOpenInBrowserActionNodets() {
		if (_movie.getOnlineReference().hasAdditional())
		{
			addSubMaster("PreviewMovieFrame.Menubar.Extras.ViewOnline", Resources.ICN_MENUBAR_ONLINEREFERENCE); //$NON-NLS-1$
			for	(final CCSingleOnlineReference soref : _movie.getOnlineReference()) {
				addSubNode("@" + (soref.hasDescription() ? soref.description : soref.type.asString()), () -> open(soref), soref.getIconRef(), false); //$NON-NLS-1$
				if (soref == _movie.getOnlineReference().Main && _movie.getOnlineReference().hasAdditional()) addSubSeparator();
			}
		}
		else
		{
			addNode("PreviewMovieFrame.Menubar.Extras.ViewOnline", () -> open(_movie.getOnlineReference().Main), Resources.ICN_MENUBAR_ONLINEREFERENCE, false); //$NON-NLS-1$
		}
	}

	private void open(CCSingleOnlineReference r) {
		if (r.isSet() && r.isValid()) HTTPUtilities.openInBrowser(r.getURL());
	}
}
