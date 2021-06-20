package de.jClipCorn.gui.frames.applyPatchFrame;

import de.jClipCorn.util.xml.CCXMLElement;

public class ActionCommandVM
{
	public final CCXMLElement XML;
	public final String Command;

	public ActionCommandVM(CCXMLElement xml)
	{
		this.XML         = xml;
		this.Command     = xml.getName();
	}
}
