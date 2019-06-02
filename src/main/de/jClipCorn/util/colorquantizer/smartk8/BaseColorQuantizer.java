package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.util.RGBColor;
import de.jClipCorn.util.colorquantizer.util.XYCoord;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

// @original https://www.codeproject.com/Articles/66341/A-Simple-Yet-Quite-Powerful-Palette-Quantizer-in-C
public abstract class BaseColorQuantizer
{
	protected static final int InvalidIndex = -1; // This index will represent invalid palette index.

	private Boolean paletteFound;
	private long uniqueColorIndex;
	private IPathProvider pathProvider;
	protected final HashMap<Integer, Short> UniqueColors;

	protected BaseColorQuantizer()
	{
		pathProvider = null;
		uniqueColorIndex = -1;
		UniqueColors = new HashMap<>();
	}

	public void ChangePathProvider(IPathProvider pathProvider)
	{
		this.pathProvider = pathProvider;
	}

	private IPathProvider GetPathProvider()
	{
		// if there is no path provider, it attempts to create a default one; integrated in the quantizer
		IPathProvider result = pathProvider;
		if (pathProvider==null) pathProvider = OnCreateDefaultPathProvider();

		// if the provider exists; or default one was created for these purposes.. use it
		if (result == null)
		{
			String message = "The path provider is not initialized! Please use SetPathProvider() method on quantizer."; //$NON-NLS-1$
			throw new NullPointerException(message);
		}

		// provider was obtained somehow, use it
		return result;
	}

	protected void OnPrepare(BufferedImage image)
	{
		uniqueColorIndex = -1;
		paletteFound = false;
		UniqueColors.clear();
	}

	protected void OnAddColor(RGBColor color, int key, int x, int y)
	{
		UniqueColors.compute(key, (k, old) -> {
			if (old==null) { uniqueColorIndex++; return (short)(uniqueColorIndex&0xFF); }
			return old;
		});
	}

	protected IPathProvider OnCreateDefaultPathProvider()
	{
		pathProvider = new StandardPathProvider();
		return new StandardPathProvider();
	}

	protected List<RGBColor> OnGetPalette(int colorCount)
	{
		// early optimalization, in case the color count is lower than total unique color count
		if (UniqueColors.size() > 0 && colorCount >= UniqueColors.size())
		{
			// palette was found
			paletteFound = true;

			// generates the palette from unique numbers
			return CCStreams
					.iterate(UniqueColors)
					.autosortByProperty(p -> p.getValue())
					.map(pair -> RGBColor.FromRGB(pair.getKey()))
					.enumerate();
		}

		// otherwise make it descendant responsibility
		return null;
	}

	protected int OnGetPaletteIndex(RGBColor color, int key, int x, int y)
	{
		// if we previously found palette quickly (without quantization), use it
		if (paletteFound)
		{
			Short v = UniqueColors.get(key);
			if (v != null) return v;
		}

		// by default unknown index is returned
		return InvalidIndex;
	}

	protected int OnGetColorCount()
	{
		return UniqueColors.size();
	}

	protected void OnFinish()
	{
		// do nothing here
	}

	public List<XYCoord> GetPointPath(int width, int heigth)
	{
		return GetPathProvider().GetPointPath(width, heigth);
	}

	public void Prepare(BufferedImage image)
	{
		OnPrepare(image);
	}

	public void AddColor(RGBColor color, int x, int y)
	{
		int key = color.toRGB();
		OnAddColor(color, key, x, y);
	}

	public int GetColorCount()
	{
		return OnGetColorCount();
	}

	public List<RGBColor> GetPalette(int colorCount)
	{
		return OnGetPalette(colorCount);
	}

	public int GetPaletteIndex(RGBColor color, int x, int y)
	{
		int key = color.toRGB();
		return OnGetPaletteIndex(color, key, x, y);
	}

	public void Finish()
	{
		OnFinish();
	}
}
