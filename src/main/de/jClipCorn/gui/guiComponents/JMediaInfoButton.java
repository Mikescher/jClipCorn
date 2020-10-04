package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

import javax.swing.*;

public class JMediaInfoButton extends JButton
{
	public JMediaInfoButton()
	{
		super(Resources.ICN_MENUBAR_MEDIAINFO.get16x16());
	}

	@Override
	public void setText(String text)
	{
		super.setText(Str.Empty);
	}
}
