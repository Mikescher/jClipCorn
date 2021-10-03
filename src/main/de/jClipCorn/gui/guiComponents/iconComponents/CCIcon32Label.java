package de.jClipCorn.gui.guiComponents.iconComponents;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.lambda.Func0to1;

import javax.swing.*;

public class CCIcon32Label extends JLabel
{
	public enum IconRefLink
	{
		ICN_GENERIC_ORB_GRAY (Resources.ICN_GENERIC_ORB_GRAY::get32x32),
		ICN_MENUBAR_FOLDER   (Resources.ICN_MENUBAR_FOLDER::get32x32),
		ICN_MENUBAR_MEDIAINFO(Resources.ICN_MENUBAR_MEDIAINFO::get32x32),
		ICN_FRAMES_SEARCH    (Resources.ICN_FRAMES_SEARCH::get32x32),
		ICN_WARNING_TRIANGLE (Resources.ICN_WARNING_TRIANGLE::get32x32),
		ICN_MENUBAR_VLCROBOT (Resources.ICN_MENUBAR_VLCROBOT::get32x32);

		final Func0to1<ImageIcon> getter;
		IconRefLink(Func0to1<ImageIcon> fn) { getter = fn; }
	}

	private IconRefLink _value = IconRefLink.ICN_GENERIC_ORB_GRAY;

	public CCIcon32Label()
	{
		super(CCIcon32Button.IconRefLink.ICN_GENERIC_ORB_GRAY.getter.invoke());
	}

	public IconRefLink getIconRef() {
		return _value;
	}

	public void setIconRef(IconRefLink t) {
		_value = t;
		super.setIcon(t.getter.invoke());
	}

	@Override
	@Deprecated
	public void setIcon(Icon icon) { /**/ }

	@Override
	@Deprecated
	public void setText(String text) { /**/ }
}
