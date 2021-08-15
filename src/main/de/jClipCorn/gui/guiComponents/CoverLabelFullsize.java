package de.jClipCorn.gui.guiComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;

public class CoverLabelFullsize extends CoverLabel
{
	@DesignCreate
	private static CoverLabelFullsize designCreate() { return new CoverLabelFullsize(null); }

	public CoverLabelFullsize(CCMovieList ml)
	{
		super(ml, false);
	}
}
