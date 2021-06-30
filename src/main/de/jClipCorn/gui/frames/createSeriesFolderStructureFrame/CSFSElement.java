package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;

import javax.swing.*;

public class CSFSElement {

	public enum CSFSState { Move, Nothing, Warning, Error }

	public CSFSState State;

	public CCPath CCPathOld;
	public CCPath CCPathNew;

	public FSPath FSPathOld;
	public FSPath FSPathNew;

	public static MultiSizeIconRef getIconRef(CSFSState state)
	{
		if (state == CSFSState.Nothing) return Resources.ICN_GENERIC_ORB_GRAY;
		if (state == CSFSState.Move)    return Resources.ICN_GENERIC_ORB_GREEN;
		if (state == CSFSState.Error)   return Resources.ICN_GENERIC_ORB_RED;
		if (state == CSFSState.Warning) return Resources.ICN_GENERIC_ORB_ORANGE;

		return null;
	}

	public static Icon getIcon(CSFSState state)
	{
		return getIconRef(state).get16x16();
	}
}
