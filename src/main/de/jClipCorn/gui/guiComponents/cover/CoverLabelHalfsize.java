package de.jClipCorn.gui.guiComponents.cover;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;

public class CoverLabelHalfsize extends CoverLabel
{
	@DesignCreate
	private static CoverLabelHalfsize designCreate() { return new CoverLabelHalfsize(null); }

	public CoverLabelHalfsize(CCMovieList ml)
	{
		super(ml, true);
	}
}