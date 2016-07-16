package de.jClipCorn.gui.frames.mainFrame.clipTable;

import java.awt.Dimension;
import java.awt.Rectangle;

import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;

public class ClipVerticalScrollbarUI extends WindowsScrollBarUI  {
	
	private final int height;
	
	public ClipVerticalScrollbarUI(int _height) {
		super();
		
		height = _height;
	}
	
	@Override
	protected void setThumbBounds(int _x, int _y, int _width, int _height) {
	 	super.setThumbBounds(_x, _y, _width, height);
	}

	@Override
	protected Rectangle getThumbBounds() {
		return new Rectangle(super.getThumbBounds().x, super.getThumbBounds().y, super.getThumbBounds().width, height);
	}

	@Override
	protected Dimension getMinimumThumbSize() {
		return new Dimension(0, height);
	}
}
