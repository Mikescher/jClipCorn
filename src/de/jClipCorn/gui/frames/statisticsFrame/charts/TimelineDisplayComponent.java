package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JComponent;

import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.CCDatespan;
import de.jClipCorn.util.CCWeekday;

public class TimelineDisplayComponent extends JComponent {
	private static final long serialVersionUID = -9133712754093362059L;

	private final static int CELL_WIDTH = 4;
	private final static int CELL_HEIGHT = 16;
	
	private final HashMap<String, List<CCDatespan>> elements;
	private final HashMap<String, List<CCDatespan>> elementsNoGravity;
	private final Dimension dim;
	private final CCDate start;
	private final CCDate end;
	private final int dayCount;
	
	public TimelineDisplayComponent(HashMap<String, List<CCDatespan>> elGravity, HashMap<String, List<CCDatespan>> elZero, CCDate s, CCDate e) {
		super();
		
		elements = elGravity;
		elementsNoGravity = elZero;
				
		start = s;
		end = e;
		dayCount = start.getDayDifferenceTo(end) + 1;

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
        return new Dimension(dayCount * CELL_WIDTH, elements.size() * CELL_HEIGHT);
    }
	
	@Override
	public void paintComponent(Graphics graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		CCDate d = CCDate.create(start);
		while(d.isLessEqualsThan(end)) {
			int dd = (start.getDayDifferenceTo(d));
			
			if (d.getWeekdayEnum() == CCWeekday.MONDAY) {
				graphics.setColor(Color.GRAY);
			} else {
				graphics.setColor(Color.LIGHT_GRAY);
			}
			
			graphics.drawLine(dd*CELL_WIDTH, 0, dd*CELL_WIDTH, dim.height);
			
			d = d.getAddDay(1);
		}

		graphics.setColor(Color.BLACK);
		for (int i = 0; i <= elements.size(); i++) {
			graphics.drawLine(0, i*CELL_HEIGHT, dim.width, i*CELL_HEIGHT);
		}
		
		int i = 0;
		graphics.setColor(Color.BLACK);
		for (Entry<String, List<CCDatespan>> spanlist : elements.entrySet()) {
			
			for (CCDatespan span : elementsNoGravity.get(spanlist.getKey())) {
				int x = start.getDayDifferenceTo(span.start) * CELL_WIDTH;
				int y = i*CELL_HEIGHT + 2;
				int w = (span.start.getDayDifferenceTo(span.end) + 1) * CELL_WIDTH;
				int h = CELL_HEIGHT - 4;

				graphics.setColor(Color.RED);
				graphics.fillRect(x, y, w, h);
			}
			
			for (CCDatespan span : spanlist.getValue()) {
				int x = start.getDayDifferenceTo(span.start) * CELL_WIDTH;
				int y = i*CELL_HEIGHT + 2;
				int w = (span.start.getDayDifferenceTo(span.end) + 1) * CELL_WIDTH;
				int h = CELL_HEIGHT - 4;

				graphics.setColor(new Color(255, 0, 0, 160));
				graphics.fillRect(x, y, w, h);
				graphics.setColor(Color.BLACK);
				graphics.drawRect(x, y, w, h);
			}
			
			i++;
		}
	}
}
