package de.jClipCorn.gui.guiComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;

import javax.swing.*;

public class JCCDialog extends JDialog implements ICCWindow
{
	protected final CCMovieList movielist;

	@DesignCreate
	private static JCCDialog designCreate() { return new JCCDialog(null); }

	public JCCDialog(CCMovieList ml)
	{
		super();
		movielist = ml;

		setIconImage(Resources.IMG_FRAME_ICON.get());
	}

	@Override
	public CCMovieList getMovieList() {
		return movielist;
	}

	@Override
	public CCProperties ccprops()
	{
		return movielist.ccprops();
	}
}
