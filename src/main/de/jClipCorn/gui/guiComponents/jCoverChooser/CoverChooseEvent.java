package de.jClipCorn.gui.guiComponents.jCoverChooser;

import java.awt.event.MouseEvent;
import java.util.EventObject;

public class CoverChooseEvent extends EventObject {
	public final int CoverID;
	public final MouseEvent InnerEvent;

	public CoverChooseEvent(Object source, int cid, MouseEvent e) {
		super(source);
		CoverID = cid;
		InnerEvent = e;
	}
}
