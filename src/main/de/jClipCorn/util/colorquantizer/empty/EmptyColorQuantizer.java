package de.jClipCorn.util.colorquantizer.empty;

import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.octree.OctreeException;
import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmptyColorQuantizer implements ColorQuantizer {

	private List<RGBColor> _palette = new ArrayList<>();

	public EmptyColorQuantizer() {
		_palette.add(new RGBColor(Color.WHITE));
	}

	@Override
	public void analyze(BufferedImage img, int colorcount) {
		//
	}

	@Override
	public List<RGBColor> getPalette() {
		return Collections.unmodifiableList(_palette);
	}

	@Override
	public BufferedImage quantize(BufferedImage img) {
		BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = out.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		return out;
	}

	@Override
	public int getPaletteIndex(RGBColor c, int x, int y) {
		return 0;
	}
}
