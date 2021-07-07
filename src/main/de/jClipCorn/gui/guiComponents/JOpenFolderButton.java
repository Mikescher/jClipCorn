package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

import javax.swing.*;

public class JOpenFolderButton extends JButton
{
	public JOpenFolderButton()
	{
		super(Resources.ICN_MENUBAR_FOLDER.get16x16());
	}

	@Override
	public void setText(String text)
	{
		super.setText(Str.Empty);
	}
}
