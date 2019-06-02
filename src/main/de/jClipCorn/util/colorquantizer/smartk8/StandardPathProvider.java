package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.util.XYCoord;

import java.util.ArrayList;
import java.util.List;

public class StandardPathProvider implements IPathProvider {
	@Override
	public List<XYCoord> GetPointPath(int width, int height)
	{
		List<XYCoord> result = new ArrayList<>(width*height);
		for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) result.add(new XYCoord(x, y));
		return result;
	}
}
