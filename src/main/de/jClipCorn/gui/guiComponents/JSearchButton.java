package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

import javax.swing.*;

public class JSearchButton extends JButton
{
	public JSearchButton()
	{
		super(Resources.ICN_FRAMES_SEARCH.get16x16());
	}

	@Override
	public void setText(String text)
	{
		super.setText(Str.Empty);
	}
}
