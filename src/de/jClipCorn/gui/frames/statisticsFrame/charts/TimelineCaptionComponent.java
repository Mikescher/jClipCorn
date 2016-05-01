package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;

public class TimelineCaptionComponent extends JComponent {
	private static final long serialVersionUID = -9133712754093362059L;

	private final static int CELL_HEIGHT = 16;
	
	private final List<String> elements;
	private final Dimension dim;
	
	public TimelineCaptionComponent(List<String> e) {
		super();
		
		elements = e;
		dim = calculateSize();
	}
	
    @Override
    public Dimension getMinimumSize() {
        return dim;
    }

    @Override
    public Dimension getPreferredSize() {
        return dim;
    }
    
    public Dimension calculateSize() {
		int height = elements.size() * CELL_HEIGHT;
		
		Font font = new Font("Consolas", Font.PLAIN, 12); //$NON-NLS-1$
		FontMetrics metrics = this.getFontMetrics(font);
		
		int width = 10;
		for (int i = 0; i < elements.size(); i++) {
			width = Math.max(width, metrics.stringWidth(elements.get(i)));
		}
				
        return new Dimension(width + 10, height);
    }
	
	@Override
	public void paintComponent(Graphics graphics) {
		int width = dim.width;
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		graphics.setColor(Color.BLACK);
		for (int i = 0; i <= elements.size(); i++) {
			graphics.drawLine(0, i*CELL_HEIGHT, width, i*CELL_HEIGHT);
		}

		graphics.setColor(Color.BLACK);
		for (int i = 0; i < elements.size(); i++) {
			graphics.setFont(new Font("Consolas", Font.PLAIN, 12)); //$NON-NLS-1$
			graphics.drawString(elements.get(i), 0, (i+1)*CELL_HEIGHT - 2);
		}
	}
}
