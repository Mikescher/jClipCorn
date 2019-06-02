package de.jClipCorn.util.colorquantizer.octree;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OctreeNode {

	private int _colorR;
	private int _colorG;
	private int _colorB;
	private int _pixelCount;
	private int _paletteIndex;
	private OctreeNode[] _children;

	public OctreeNode(int level, OctreeQuantizer parent) {
		_colorR = 0;
		_colorG = 0;
		_colorB = 0;
		_pixelCount = 0;
		_paletteIndex = 0;
		_children = new OctreeNode[8];

		if (level < OctreeQuantizer.MAX_DEPTH - 1)	parent.addLevelNode(level, this);
	}

	public void addColor(int color, int level, OctreeQuantizer parent) {
		int cr = (color >> 16) & 0xFF;
		int cg = (color >> 8) & 0xFF;
		int cb = color & 0xFF;
		if (level >= OctreeQuantizer.MAX_DEPTH) {
			_colorR += cr;
			_colorG += cg;
			_colorB += cb;
			_pixelCount++;
			return;
		}
		int index = getColorIndexForLevel(cr, cg, cb, level);
		if (_children[index] == null) _children[index] = new OctreeNode(level, parent);
		_children[index].addColor(color, level + 1, parent);
	}

	private int getColorIndexForLevel(int colorR, int colorG, int colorB, int level) {
		int index = 0;
		int mask = 0x80 >> level;
		if ((colorR & mask) != 0) index |= 4;
		if ((colorG & mask) != 0) index |= 2;
		if ((colorB & mask) != 0) index |= 1;
		return index;
	}

	public List<OctreeNode> getLeaveNodes() {
		List<OctreeNode> leafNodes = new ArrayList<>();

		for (int i = 0; i < 8; i++) {
			OctreeNode node = _children[i];
			if (node != null) {
				if (node.isLeaf()) leafNodes.add(node);
				else leafNodes.addAll(node.getLeaveNodes());
			}
		}
		return leafNodes;
	}

	public boolean isLeaf() {
		return _pixelCount > 0;
	}

	public int removeLeaves() {
		int result = 0;

		for (int i = 0; i < 8; i++) {
			OctreeNode node = _children[i];
			if (node != null) {
				_colorR += node._colorR;
				_colorG += node._colorG;
				_colorB += node._colorB;
				_pixelCount += node._pixelCount;
				result++;
			}
		}
		return result-1;
	}

	public void setPaletteIndex(int idx) {
		_paletteIndex = idx;
	}

	public Color getColor() {
		return new Color(_colorR / _pixelCount, _colorG / _pixelCount, _colorB / _pixelCount);
	}

	public int getPaletteIndex(int colorR, int colorG, int colorB, int level) throws OctreeException {
		if (isLeaf()) return _paletteIndex;

		int index = getColorIndexForLevel(colorR, colorG, colorB, level);
		if (_children[index] != null) return _children[index].getPaletteIndex(colorR, colorG, colorB, level+1);

		for (int i = 0; i < 8; i++) {
			if (_children[i] != null) return _children[i].getPaletteIndex(colorR, colorG, colorB, level+1);
		}

		throw new OctreeException("PaletteIndex not found"); //$NON-NLS-1$
	}
}
