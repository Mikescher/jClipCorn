package de.jClipCorn.util.colorquantizer.util;

import de.jClipCorn.util.colorquantizer.smartk8.ColorModelEnum;
import de.jClipCorn.util.datatypes.Tuple3;

import java.awt.*;

public class RGBColor {
	private final static int X = 0;
	private final static int Y = 1;
	private final static int Z = 2;

	private final static float Epsilon    = 1E-05f;
	private final static float OneThird   = 1.0f / 3.0f;
	private final static float TwoThirds  = 2.0f * OneThird;
	private final static double HueFactor = 1.4117647058823529411764705882353;

	private static final float[] XYZWhite = new float[] { 95.05f, 100.00f, 108.90f };

	public final short R;
	public final short G;
	public final short B;

	public RGBColor(short r, short g, short b) {
		R=r;
		G=g;
		B=b;
	}

	public RGBColor(Color c) {
		R=(short)c.getRed();
		G=(short)c.getGreen();
		B=(short)c.getBlue();
	}

	public static RGBColor FromRGB(int v) {
		return new RGBColor((short)((v>>16)&0xFF), (short)((v>>8)&0xFF), (short)(v&0xFF));
	}

	public static RGBColor FromRGB(int r, int g, int b) {
		return new RGBColor((short)(r&0xFF), (short)(g&0xFF), (short)(b&0xFF));
	}

	public static RGBColor FromHSB(float hue, float saturation, float brightness) {
		// initializes the default black
		int red   = 0;
		int green = 0;
		int blue  = 0;

		// only if there is some brightness; otherwise leave it pitch black
		if (brightness > 0.0f)
		{
			// if there is no saturation; leave it gray based on the brightness only
			if (Math.abs(saturation - 0.0f) < Epsilon)
			{
				red = green = blue = (int)(255.0f * brightness);
			}
			else // the color is more complex
			{
				// converts HSL cylinder to one slice (its factors)
				float factorHue = hue / 360.0f;
				float factorA = brightness < 0.5f ? brightness * (1.0f + saturation) : (brightness + saturation) - (brightness * saturation);
				float factorB = (2.0f * brightness) - factorA;

				// maps HSL slice to a RGB cube
				red   = getColorComponent(factorB, factorA, factorHue + OneThird);
				green = getColorComponent(factorB, factorA, factorHue);
				blue  = getColorComponent(factorB, factorA, factorHue - OneThird);
			}
		}

		int argb = red << 16 | green << 8 | blue;
		return RGBColor.FromRGB(argb);
	}

	public int toRGB() {
		return (R<<16)|(G<<8)|(B);
	}

	// HSB Hue in degree from 0..360
	public float getHue() {
		if (R == G && G == B) return 0;

		float fr = R / 255f;
		float fg = G / 255f;
		float fb = B / 255f;

		float min = fr;
		if (fg < min) min = fg;
		if (fb < min) min = fb;

		if (fr >= fg && fr >= fb) // red = max
		{
			float hue = ((fg-fb)/(fr-min))*60;
			if (hue < 0) hue += 360;
			return hue;
		}
		else if (fg >= fr && fg >= fb) // green = max
		{
			float hue = (2f + (fb-fr)/(fg-min))*60;
			if (hue < 0) hue += 360;
			return hue;
		}
		else  // blue = max
		{
			float hue = (4f + (fr - fg) / (fb - min)) * 60;
			if (hue < 0) hue += 360;
			return hue;
		}
	}

	// HSB Saturation in percentage from 0..1
	public float getSaturation() {

		float min = R;
		if (G < min) min = G;
		if (B < min) min = B;

		float max = R;
		if (G > max) max = G;
		if (B > max) max = B;

		if (max == min) return 0;

		float l = (max-min *  1f) / 2;

		if (l <= .5f) return (max - min)/(max + min);
		else return (max - min)/(2 - max - min);
	}

	// HSB Brightness in percentage from 0..1
	public float getBrightness() {
		float min = R;
		if (G < min) min = G;
		if (B < min) min = B;

		float max = R;
		if (G > max) max = G;
		if (B > max) max = B;

		return ((max/255f) + (min/255f)) / 2;
	}

	private static int getColorComponent(float v1, float v2, float hue) {
		float preresult;

		if (hue < 0.0f) hue++;
		if (hue > 1.0f) hue--;

		if ((6.0f * hue) < 1.0f)
			preresult = v1 + (((v2 - v1) * 6.0f) * hue);
		else if ((2.0f * hue) < 1.0f)
			preresult = v2;
		else if ((3.0f * hue) < 2.0f)
			preresult = v1 + (((v2 - v1) * (TwoThirds - hue)) * 6.0f);
		else
			preresult = v1;

		return (int)(255.0f * preresult);
	}

	public Tuple3<Float, Float, Float> toXYZ()
	{
		// normalize red, green, blue values
		double redFactor   = R / 255.0;
		double greenFactor = G / 255.0;
		double blueFactor  = B / 255.0;

		// convert to a sRGB form
		double sRed   = (redFactor   > 0.04045) ? Math.pow((redFactor + 0.055) / (1 + 0.055), 2.2) : (redFactor / 12.92);
		double sGreen = (greenFactor > 0.04045) ? Math.pow((greenFactor + 0.055) / (1 + 0.055), 2.2) : (greenFactor / 12.92);
		double sBlue  = (blueFactor  > 0.04045) ? Math.pow((blueFactor + 0.055) / (1 + 0.055), 2.2) : (blueFactor / 12.92);

		// converts
		float x = (float)(sRed * 0.4124 + sGreen * 0.3576 + sBlue * 0.1805);
		float y = (float)(sRed * 0.2126 + sGreen * 0.7152 + sBlue * 0.0722);
		float z = (float)(sRed * 0.0193 + sGreen * 0.1192 + sBlue * 0.9505);

		return Tuple3.Create(x, y, z);
	}

	private static Tuple3<Float, Float, Float> XYZtoLab(float x, float y, float z)
	{
		float l = 116.0f * GetXYZValue(y / XYZWhite[Y]) - 16.0f;
		float a = 500.0f * (GetXYZValue(x / XYZWhite[X]) - GetXYZValue(y / XYZWhite[Y]));
		float b = 200.0f * (GetXYZValue(y / XYZWhite[Y]) - GetXYZValue(z / XYZWhite[Z]));

		return Tuple3.Create(l, a, b);
	}

	public Tuple3<Float, Float, Float> toLAB() {
		Tuple3<Float, Float, Float> xyz = toXYZ();
		return XYZtoLab(xyz.Item1, xyz.Item2, xyz.Item3);
	}

	private static float GetXYZValue(float value)
	{
		return value > 0.008856f ? (float)Math.pow(value, OneThird) : (7.787f * value + 16.0f / 116.0f);
	}

	public Tuple3<Float, Float, Float> GetColorComponents(ColorModelEnum colorModel)
	{
		switch (colorModel)
		{
			case RedGreenBlue: return Tuple3.Create(R*1f, G*1f, B*1f);
			case HueSaturationBrightness: return Tuple3.Create(getHue(), getSaturation(), getBrightness());
			case LabColorSpace: return toLAB();
			case XYZ: return toXYZ();
		}

		return Tuple3.Create(0f, 0f, 0f);
	}

	public Tuple3<Float, Float, Float> GetColorComponents(ColorModelEnum colorModel, RGBColor targetColor)
	{
		switch (colorModel)
		{
			case RedGreenBlue: return Tuple3.Create((R-targetColor.R)*1f, (G-targetColor.G)*1f, (B-targetColor.B)*1f);
			case HueSaturationBrightness: return Tuple3.Create(getHue() - targetColor.getHue(), getSaturation() - targetColor.getSaturation(), getBrightness() - targetColor.getBrightness());
			case LabColorSpace:
				Tuple3<Float, Float, Float> lab1 = toLAB();
				Tuple3<Float, Float, Float> lab2 = targetColor.toLAB();
				return Tuple3.Create(lab1.Item1-lab2.Item1, lab1.Item2-lab2.Item2, lab1.Item3-lab2.Item3);
			case XYZ:
				Tuple3<Float, Float, Float> xyz1 = toXYZ();
				Tuple3<Float, Float, Float> xyz2 = targetColor.toXYZ();
				return Tuple3.Create(xyz1.Item1-xyz2.Item1, xyz1.Item2-xyz2.Item2, xyz1.Item3-xyz2.Item3);
		}

		return Tuple3.Create(0f, 0f, 0f);
	}

	public int GetComponentA(ColorModelEnum colorModel)
	{
		switch (colorModel)
		{
			case RedGreenBlue: return R;
			case HueSaturationBrightness: return (int)(getHue()/HueFactor);
			case LabColorSpace: return (int) toLAB().Item1.floatValue();
			case XYZ: return (int) (100 * toXYZ().Item1);
		}

		return 0;
	}

	public int GetComponentB(ColorModelEnum colorModel)
	{
		switch (colorModel)
		{
			case RedGreenBlue: return G;
			case HueSaturationBrightness: return (int)(getSaturation()*255);
			case LabColorSpace: return (int) toLAB().Item2.floatValue();
			case XYZ: return (int) (100 * toXYZ().Item2);
		}

		return 0;
	}

	public int GetComponentC(ColorModelEnum colorModel)
	{
		switch (colorModel)
		{
			case RedGreenBlue: return B;
			case HueSaturationBrightness: return (int)(getBrightness()*255);
			case LabColorSpace: return (int) toLAB().Item3.floatValue();
			case XYZ: return (int) (100 * toXYZ().Item3);
		}

		return 0;
	}

}
