package de.jClipCorn.util.colorquantizer.octree;

import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.util.RGBColor;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

//@original https://github.com/delimitry/octree_color_quantizer
public class OctreeQuantizer implements ColorQuantizer {

	public final static int MAX_DEPTH = 8;

	private OctreeNode _root = null;
	private ArrayList<ArrayList<OctreeNode>> _levels = null;

	private List<Color> _palette;

	public OctreeQuantizer() {
		//
	}

	private void makePalette(int colorcount) {
		List<Color> palette = new ArrayList<>();
		int paletteIndex = 0;
		int leafCount = getLeaves().size();

        // reduce nodes
        // up to 8 leaves can be reduced here and the palette will have
        // only 248 colors (in worst case) instead of expected 256 colors
		for (int level = MAX_DEPTH-1; level >= 0; level--) {
			if (!_levels.get(level).isEmpty()) {
				for (OctreeNode node : _levels.get(level)) {
					leafCount -= node.removeLeaves();
				}
				if (leafCount <= colorcount) break;
				_levels.set(level, new ArrayList<>());
			}
		}

        // build palette
		for (OctreeNode node : getLeaves()) {
			if (paletteIndex >= colorcount) break;
			if (node.isLeaf()) palette.add(node.getColor());
			node.setPaletteIndex(paletteIndex);
			paletteIndex++;
		}

		_palette = palette;
	}

	void addLevelNode(int level, OctreeNode node) {
		_levels.get(level).add(node);
	}

	private List<OctreeNode> getLeaves() {
		return _root.getLeaveNodes();
	}

	private int getPaletteIndex(int color) throws OctreeException {
		int cr = (color >> 16) & 0xFF;
		int cg = (color >> 8) & 0xFF;
		int cb = color & 0xFF;
		return _root.getPaletteIndex(cr, cg, cb, 0);
	}

	@Override
	public BufferedImage quantize(BufferedImage img) throws OctreeException {
		BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				output.setRGB(x, y, getPaletteIndex(img.getRGB(x, y)));
			}
		}

		return output;
	}

	@Override
	public int getPaletteIndex(RGBColor c, int x, int y) throws OctreeException {
		return getPaletteIndex(c.toRGB());
	}

	@Override
	public void analyze(BufferedImage img, int colorcount) {
		_levels = new ArrayList<>();
		for (int i = 0; i < MAX_DEPTH; i++) _levels.add(new ArrayList<>());

		_root = new OctreeNode(0, this);

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				_root.addColor(img.getRGB(x, y), 0, this);
			}
		}

		makePalette(colorcount);
	}

	@Override
	public List<RGBColor> getPalette() {
		return CCStreams.iterate(_palette).map(RGBColor::new).enumerate();
	}
}
