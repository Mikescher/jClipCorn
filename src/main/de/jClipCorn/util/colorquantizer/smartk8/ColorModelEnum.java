package de.jClipCorn.util.colorquantizer.smartk8;

public enum ColorModelEnum {
	// The RGB color model is an additive color model in which red, green, and blue light is added together
	// in various ways to reproduce a broad array of colors. The name of the model comes from the initials
	// of the three additive primary colors, red, green, and blue.
	RedGreenBlue(0),

	// HSL is a common cylindrical-coordinate representations of points in an RGB color model, which rearrange
	// the geometry of RGB in an attempt to be more perceptually relevant than the cartesian representation.
	HueSaturationBrightness(1),

	// A Lab color space is a color-opponent space with dimension L for lightness and a and b for the
	// color-opponent dimensions, based on nonlinearly compressed CIE XYZ color space coordinates.
	LabColorSpace(2),

	// XYZ color space
	XYZ(3);

	public int Value;

	ColorModelEnum(int m) { Value = m; }
}
