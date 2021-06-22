package de.jClipCorn.gui.guiComponents.jCanvasLabel;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.SimpleDoubleBufferStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class JCanvasLabel extends JLabel {
	@DesignCreate
	private static JCanvasLabel designCreate() { var r = new JCanvasLabel(); r.configureDesignMode(); return r; }

	private SimpleDoubleBufferStrategy _strategy;
	private final List<PaintComponentListener> _pcListener = new ArrayList<>();

	public JCanvasLabel()
	{
		super();

		// ...
	}

	public SimpleDoubleBufferStrategy getDoubleBufferStrategy()
	{
		// call not before Label is properly set up (cannot be zero-sized)
		if (_strategy == null) _strategy = new SimpleDoubleBufferStrategy(this);
		return _strategy;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (var l: _pcListener) l.paintComponentCalled(new PaintComponentEvent(this, g));
	}

	public void addPaintComponentListener(final PaintComponentListener l) {
		_pcListener.add(l);
	}
	public void removePaintComponentListener(final PaintComponentListener l) {
		_pcListener.remove(l);
	}

	private void configureDesignMode() {

		BufferedImage pattern = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics gpat = pattern.getGraphics();
		gpat.setColor(Color.decode("#808080")); //$NON-NLS-1$
		gpat.fillRect(0, 0, 32, 32);
		gpat.setColor(Color.decode("#444444")); //$NON-NLS-1$
		gpat.fillRect(0, 0, 16, 16);
		gpat.fillRect(16, 16, 32, 32);

		var transparencyPattern = new TexturePaint(pattern, new Rectangle(0, 0, 16, 16));

		addPaintComponentListener(e ->
		{
			var w = e.Component.getWidth();
			var h = e.Component.getHeight();

			if (w <= 0 || h <= 0) return;

			e.Graphics.setPaint(transparencyPattern);
			e.Graphics.fillRect(0, 0, e.Component.getWidth(), e.Component.getHeight());
		});

		repaint();
	}
}
