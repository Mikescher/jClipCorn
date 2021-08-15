package de.jClipCorn.gui.guiComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;

import javax.swing.*;
import java.awt.*;

public class JCCFrame extends JFrame implements ICCWindow
{
	protected final CCMovieList movielist;

	@DesignCreate
	private static JCCFrame designCreate() { return new JCCFrame(null); }

	public JCCFrame(CCMovieList ml) throws HeadlessException
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
