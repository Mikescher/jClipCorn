package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.util.XYCoord;

import java.util.List;

public interface IPathProvider
{
	List<XYCoord> GetPointPath(int width, int height);
}
