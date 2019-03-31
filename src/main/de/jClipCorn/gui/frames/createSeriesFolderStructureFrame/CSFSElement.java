package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.MultiIconRef;
import de.jClipCorn.gui.resources.Resources;

import javax.swing.*;

public class CSFSElement {

	public enum CSFSState { Move, Nothing, Warning, Error }

	public CSFSState State;

	public String CCPathOld;
	public String CCPathNew;

	public String FSPathOld;
	public String FSPathNew;

	public static MultiIconRef getIconRef(CSFSState state)
	{
		if (state == CSFSState.Nothing) return Resources.ICN_GENERIC_ORB_GRAY;
		if (state == CSFSState.Move)    return Resources.ICN_GENERIC_ORB_GREEN;
		if (state == CSFSState.Error)   return Resources.ICN_GENERIC_ORB_RED;
		if (state == CSFSState.Warning) return Resources.ICN_GENERIC_ORB_ORANGE;

		return null;
	}

	public static Icon getIcon(CSFSState state)
	{
		return CachedResourceLoader.getIcon(getIconRef(state).icon16x16);
	}
}
