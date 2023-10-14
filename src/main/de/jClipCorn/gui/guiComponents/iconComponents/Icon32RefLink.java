package de.jClipCorn.gui.guiComponents.iconComponents;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.lambda.Func0to1;

import javax.swing.*;

public enum Icon32RefLink
{
	ICN_GENERIC_ORB_GRAY   (Resources.ICN_GENERIC_ORB_GRAY::get32x32),
	ICN_MENUBAR_FOLDER     (Resources.ICN_MENUBAR_FOLDER::get32x32),
	ICN_MENUBAR_MEDIAINFO  (Resources.ICN_MENUBAR_MEDIAINFO::get32x32),
	ICN_FRAMES_SEARCH      (Resources.ICN_FRAMES_SEARCH::get32x32),
	ICN_WARNING_TRIANGLE   (Resources.ICN_WARNING_TRIANGLE::get32x32),
	ICN_MENUBAR_VLCROBOT   (Resources.ICN_MENUBAR_VLCROBOT::get32x32),
	ICN_MENUBAR_PLAY       (Resources.ICN_MENUBAR_PLAY::get32x32),
	ICN_MENUBAR_HIDDENPLAY (Resources.ICN_MENUBAR_HIDDENPLAY::get32x32),
	ICN_MENUBAR_PLAY_ACTIVE(Resources.ICN_MENUBAR_PLAY_ACTIVE::get32x32),
	ICN_MENUBAR_PLAY_QUEUED(Resources.ICN_MENUBAR_PLAY_QUEUED::get32x32),
	ICN_FRAMES_NEXT        (Resources.ICN_FRAMES_NEXT::get32x32);

	private final Func0to1<ImageIcon> getter;
	Icon32RefLink(Func0to1<ImageIcon> fn) { getter = fn; }

	public Icon get() {
		return getter.invoke();
	}
}
