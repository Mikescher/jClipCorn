package de.jClipCorn.gui.guiComponents.iconComponents;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.lambda.Func0to1;

import javax.swing.*;

public enum Icon16RefLink
{
	ICN_GENERIC_ORB_GRAY (Resources.ICN_GENERIC_ORB_GRAY::get16x16),
	ICN_MENUBAR_FOLDER   (Resources.ICN_MENUBAR_FOLDER::get16x16),
	ICN_MENUBAR_MEDIAINFO(Resources.ICN_MENUBAR_MEDIAINFO::get16x16),
	ICN_FRAMES_SEARCH    (Resources.ICN_FRAMES_SEARCH::get16x16),
	ICN_WARNING_TRIANGLE (Resources.ICN_WARNING_TRIANGLE::get16x16),
	ICN_MENUBAR_VLCROBOT (Resources.ICN_MENUBAR_VLCROBOT::get16x16);

	private final Func0to1<ImageIcon> getter;
	Icon16RefLink(Func0to1<ImageIcon> fn) { getter = fn; }

	public Icon get() {
		return getter.invoke();
	}
}
