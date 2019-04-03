package de.jClipCorn.features.statistics.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDatespan;
import de.jClipCorn.util.datetime.CCWeekday;

public class TimelineDisplayComponent extends JComponent implements MouseListener {
	private static final long serialVersionUID = -9133712754093362059L;

	private final static int CELL_WIDTH = 4;
	private final static int CELL_HEIGHT = 16;

	private final Map<CCSeries, Boolean> filter;

	private final List<CCSeries> list;
	private final HashMap<CCSeries, List<CCDatespan>> elements;
	private final HashMap<CCSeries, List<CCDatespan>> elementsNoGravity;
	private final Dimension dim;
	private final CCDate start;
	private final CCDate end;
	private final int dayCount;

	private CCSeries selected = null;
	
	public ActionListener onSelectedChanged = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) { /**/ }
	};
	
	public TimelineDisplayComponent(List<CCSeries> orderlist, HashMap<CCSeries, List<CCDatespan>> elGravity, HashMap<CCSeries, List<CCDatespan>> elZero, CCDate s, CCDate e, Map<CCSeries, Boolean> map) {
		super();
		
		filter = map;
		list = orderlist;
		elements = elGravity;
		elementsNoGravity = elZero;
				
		start = s;
		end = e;
		dayCount = start.getDayDifferenceTo(end) + 1;

		dim = calculateSize();
		
		addMouseListener(this);
	}
	
    @Override
    public Dimension getMinimumSize() {
        return dim;
    }

    @Override
    public Dimension getPreferredSize() {
        return dim;
    }
	
    @Override
    public Dimension getMaximumSize() {
        return dim;
    }
    
    public Dimension calculateSize() {
    	int count = 0;

		for (CCSeries key : list) {
			if (filter.getOrDefault(key, true)) count++;
		}
    	
        return new Dimension(dayCount * CELL_WIDTH, count * CELL_HEIGHT);
    }
	
	@Override
	public void paintComponent(Graphics graphics) {
		int i;
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		i = 0;
		for (CCSeries key : list) {
			if (!filter.getOrDefault(key, true)) continue;

			if (key == selected) {
				graphics.setColor(new Color(135, 206, 250));
				graphics.fillRect(0, i*CELL_HEIGHT, dim.width, CELL_HEIGHT);
				graphics.setColor(Color.BLACK);
			}
			
			i++;
		}
		
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
		i = 0;
		for (CCSeries key : list) {
			if (!filter.getOrDefault(key, true)) continue;

			graphics.drawLine(0, i*CELL_HEIGHT, dim.width, i*CELL_HEIGHT);
			
			i++;
		}
		graphics.drawLine(0, i*CELL_HEIGHT, dim.width, i*CELL_HEIGHT);
		
		i = 0;
		graphics.setColor(Color.BLACK);
		for (CCSeries key : list) {
			if (!filter.getOrDefault(key, true)) continue;
			
			for (CCDatespan span : elementsNoGravity.get(key)) {
				int x = start.getDayDifferenceTo(span.start) * CELL_WIDTH;
				int y = i*CELL_HEIGHT + 2;
				int w = span.start.getDayDifferenceTo(span.end) * CELL_WIDTH;
				int h = CELL_HEIGHT - 4;

				graphics.setColor(Color.RED);
				graphics.fillRect(x, y, w, h);
			}
			
			for (CCDatespan span : elements.getOrDefault(key, new ArrayList<>())) {
				int x = start.getDayDifferenceTo(span.start) * CELL_WIDTH;
				int y = i*CELL_HEIGHT + 2;
				int w = span.start.getDayDifferenceTo(span.end) * CELL_WIDTH;
				int h = CELL_HEIGHT - 4;

				graphics.setColor(new Color(255, 0, 0, 160));
				graphics.fillRect(x, y, w, h);
				graphics.setColor(Color.BLACK);
				graphics.drawRect(x, y, w, h);
			}
			
			i++;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int idx = e.getY() / CELL_HEIGHT;

		int i = 0;
		for (CCSeries key : list) {
			if (!filter.getOrDefault(key, true)) continue;
			
			if (idx == i) {
				selected = key;
				onSelectedChanged.actionPerformed(new ActionEvent(key, i, null));
				repaint();
				return;
			}
				
			i++;
		}
	}
	
	public void setSelectedDirect(CCSeries key) {
		selected = key;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) { /**/ }

	@Override
	public void mouseReleased(MouseEvent e) { /**/ }

	@Override
	public void mouseEntered(MouseEvent e) { /**/ }

	@Override
	public void mouseExited(MouseEvent e) { /**/ }
}
