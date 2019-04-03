package de.jClipCorn.features.statistics.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import de.jClipCorn.util.datetime.CCDate;

public class TimelineDateCaptionComponent extends JComponent {
	private static final long serialVersionUID = -9133712754093362059L;

	private final static int CELL_WIDTH = 4;
	private final static int CELL_HEIGHT = 16;
	
	private final CCDate start;
	private final CCDate end;
	private final int dayCount;
	
    public TimelineDateCaptionComponent(CCDate s, CCDate e) {
		super();
		
		start = s;
		end = e;
		dayCount = s.getDayDifferenceTo(e) + 1;
	}

	@Override
    public Dimension getMinimumSize() {
        return new Dimension(CELL_WIDTH * dayCount, CELL_HEIGHT);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(CELL_WIDTH * dayCount, CELL_HEIGHT);
    }
    
	@Override
	public void paintComponent(Graphics graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		graphics.setColor(Color.BLACK);
		graphics.drawLine(0, 0, 0, CELL_HEIGHT);
		
		int monthYear = 0;
		
		CCDate d = CCDate.create(start);
		while(d.isLessEqualsThan(end)) {
			int currMonthYear = d.getMonth() + d.getYear() * 100;
			
			if (d.getDay() == 1 && currMonthYear != monthYear) {
				int dd = (start.getDayDifferenceTo(d));
				
				monthYear = currMonthYear;
				
				graphics.drawLine(dd*CELL_WIDTH, 0, dd*CELL_WIDTH, CELL_HEIGHT);
				
				graphics.drawString(d.getMonthName() + " " + d.getYear(), dd*CELL_WIDTH + 5, CELL_HEIGHT - 3); //$NON-NLS-1$
			}
			
			d = d.getAddDay(1);
		}
	}
}
