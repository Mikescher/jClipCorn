package de.jClipCorn.util.colorquantizer.smartk8.caches;

import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.util.HashMap;

public class OctreeCacheNode
{
	private static final short[] Mask = new short[] { 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x02, 0x01 };

	private final OctreeCacheNode[] nodes;
	private final HashMap<Integer, RGBColor> entries;

	/// <summary>
	/// Initializes a new instance of the <see cref="OctreeCacheNode"/> class.
	/// </summary>
	public OctreeCacheNode()
	{
		nodes = new OctreeCacheNode[8];
		entries = new HashMap<>();
	}

	public void AddColor(RGBColor color, int paletteIndex, int level)
	{
		// if this node is a leaf, then increase a color amount, and pixel presence
		entries.put(paletteIndex, color);

		if (level < 8) // otherwise goes one level deeper
		{
			// calculates an index for the next sub-branch
			int index = GetColorIndexAtLevel(color, level);

			// if that branch doesn't exist, grows it
			if (nodes[index] == null)
			{
				nodes[index] = new OctreeCacheNode();
			}

			// adds a color to that branch
			nodes[index].AddColor(color, paletteIndex, level + 1);
		}
	}

	/// <summary>
	/// Gets the index of the palette.
	/// </summary>
	public HashMap<Integer, RGBColor> GetPaletteIndex(RGBColor color, int level)
	{
		HashMap<Integer, RGBColor> result = entries;

		if (level < 8)
		{
			int index = GetColorIndexAtLevel(color, level);

			if (nodes[index] != null)
			{
				result = nodes[index].GetPaletteIndex(color, level + 1);
			}
		}

		return result;
	}

	private static int GetColorIndexAtLevel(RGBColor color, int level)
	{
		return ((color.R & Mask[level]) == Mask[level] ? 4 : 0) |
			   ((color.G & Mask[level]) == Mask[level] ? 2 : 0) |
			   ((color.B & Mask[level]) == Mask[level] ? 1 : 0);
	}
}
