package de.jClipCorn.gui.frames.statisticsFrame.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class TimelineEmptyCaptionComponent extends JComponent {
	private static final long serialVersionUID = -9133712754093362059L;

	private final static int CELL_HEIGHT = 16;
	
	private final JComponent captionComp;
	
	public TimelineEmptyCaptionComponent(JComponent cap) {
		super();
		
		captionComp = cap;
	}
	
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(captionComp.getMinimumSize().width, CELL_HEIGHT);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(captionComp.getPreferredSize().width, CELL_HEIGHT);
    }
    
	@Override
	public void paintComponent(Graphics graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
	}
}
