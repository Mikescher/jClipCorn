package de.jClipCorn.util.colorquantizer.mediancut;

import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.smartk8.ISmartK8ColorQuantizer;
import de.jClipCorn.util.colorquantizer.util.RGBColor;
import de.jClipCorn.util.datatypes.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedianCutQuantizer implements ISmartK8ColorQuantizer
{
	private final List<RGBColor> colorList;
	private final List<MedianCutCube> cubeList;
	private final HashMap<RGBColor, Integer> cache;

	public MedianCutQuantizer()
	{
		cache = new HashMap<>();
		cubeList = new ArrayList<>();
		colorList = new ArrayList<>();
	}

	private void SplitCubes() throws ColorQuantizerException {
		// creates a holder for newly added cubes
		List<MedianCutCube> newCubes = new ArrayList<>();

		for (MedianCutCube cube : cubeList)
		{
			// if another new cubes should be over the top; don't do it and just stop here
			// if (newCubes.Count >= colorCount) break;

			MedianCutCube newMedianCutCubeA, newMedianCutCubeB;

			// splits the cube along the red axis
			if (cube.RedSize() >= cube.GreenSize() && cube.RedSize() >= cube.BlueSize())
			{
				Tuple<MedianCutCube, MedianCutCube> t = cube.SplitAtMedian(0);
				newMedianCutCubeA = t.Item1;
				newMedianCutCubeB = t.Item2;
			}
			else if (cube.GreenSize() >= cube.BlueSize()) // splits the cube along the green axis
			{
				Tuple<MedianCutCube, MedianCutCube> t = cube.SplitAtMedian(1);
				newMedianCutCubeA = t.Item1;
				newMedianCutCubeB = t.Item2;
			}
			else // splits the cube along the blue axis
			{
				Tuple<MedianCutCube, MedianCutCube> t = cube.SplitAtMedian(2);
				newMedianCutCubeA = t.Item1;
				newMedianCutCubeB = t.Item2;
			}

			// adds newly created cubes to our list; but one by one and if there's enough cubes stops the process
			newCubes.add(newMedianCutCubeA);
			// if (newCubes.Count >= colorCount) break;
			newCubes.add(newMedianCutCubeB);
		}

		// clears the old cubes
		cubeList.clear();

		// adds the new cubes to the official cube list
		cubeList.addAll(newCubes);
	}

	@Override
	public void AddColor(RGBColor color)
	{
		colorList.add(color);
	}

	@Override
	public List<RGBColor> GetPalette(int colorCount) throws ColorQuantizerException {
		// creates the initial cube covering all the pixels in the image
		MedianCutCube initalMedianCutCube = new MedianCutCube(colorList);
		cubeList.add(initalMedianCutCube);

		// finds the minimum iterations needed to achieve the cube count (color count) we need
		int iterationCount = 1;
		while ((1 << iterationCount) < colorCount) { iterationCount++; }

		for (int iteration = 0; iteration < iterationCount; iteration++)
		{
			SplitCubes();
		}

		// initializes the result palette
		List<RGBColor> result = new ArrayList<>();
		int paletteIndex = 0;

		// adds all the cubes' colors to the palette, and mark that cube with palette index for later use
		for (MedianCutCube cube : cubeList)
		{
			result.add(cube.Color());
			cube.SetPaletteIndex(paletteIndex++);
		}

		// returns the palette (should contain <= ColorCount colors)
		return result;
	}

	@Override
	public int GetPaletteIndex(RGBColor color)
	{
		if (!cache.containsKey(color))
		{
			for (MedianCutCube cube : cubeList)
			{
				if (cube.IsColorIn(color))
				{
					return cube.PaletteIndex;
					// break;
				}
			}

			return 0;
		}
		else
		{
			return cache.get(color);
		}
	}

	@Override
	public int GetColorCount()
	{
		return (int)colorList.stream().distinct().count();
	}

	@Override
	public void Clear()
	{
		cache.clear();
		cubeList.clear();
		colorList.clear();
	}
}
