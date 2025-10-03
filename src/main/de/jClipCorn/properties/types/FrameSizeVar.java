package de.jClipCorn.properties.types;

public class FrameSizeVar {
	public final static FrameSizeVar ZERO = new FrameSizeVar(0, 0);

	public final int Width;
	public final int Height;

	public FrameSizeVar(int w, int h) {
		Width   = w;
		Height  = h;
	}

	@Override
	public String toString() {
		return Width + " x " + Height; //$NON-NLS-1$
	}

	public String serialize() {
		return "[" + Width + "," + Height + "]";
	}

	public boolean isZero() {
		return Width == 0 && Height == 0;
	}
}
