package de.jClipCorn.features.statistics.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import de.jClipCorn.database.databaseElement.CCSeries;

public class TimelineCaptionComponent extends JComponent implements MouseListener {
	private static final long serialVersionUID = -9133712754093362059L;

	private final static int CELL_HEIGHT = 16;

	private final Map<CCSeries, Boolean> filter;
	
	private final List<CCSeries> elements;
	private final Dimension dim;

	private CCSeries selected = null;
	
	public ActionListener onSelectedChanged = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) { /**/ }
	};
	
	public TimelineCaptionComponent(List<CCSeries> e, Map<CCSeries, Boolean> map) {
		super();
		
		filter = map;
		elements = e;
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
		Font font = new Font("Consolas", Font.PLAIN, 12); //$NON-NLS-1$
		FontMetrics metrics = this.getFontMetrics(font);
		
		int width = 10;
    	int count = 0;
		for (int i = 0; i < elements.size(); i++) {
			if (!filter.getOrDefault(elements.get(i), true)) continue;
			
			width = Math.max(width, metrics.stringWidth(elements.get(i).getTitle()));

			count++;
		}
    	
		int height = count * CELL_HEIGHT;
				
        return new Dimension(width + 10, height);
    }
	
	@Override
	public void paintComponent(Graphics graphics) {
		int width = dim.width;
		int i;
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		i = 0;
		for (CCSeries key : elements) {
			if (!filter.getOrDefault(key, true)) continue;

			if (key == selected) {
				graphics.setColor(new Color(135, 206, 250));
				graphics.fillRect(0, i*CELL_HEIGHT, dim.width, CELL_HEIGHT);
				graphics.setColor(Color.BLACK);
			}
			
			i++;
		}
		
		graphics.setColor(Color.BLACK);
		i = 0;
		for (int x = 0; x < elements.size(); x++) {
			if (!filter.getOrDefault(elements.get(x), true)) continue;

			graphics.drawLine(0, i*CELL_HEIGHT, dim.width, i*CELL_HEIGHT);
			
			i++;
		}
		graphics.drawLine(0, i*CELL_HEIGHT, width, i*CELL_HEIGHT);

		graphics.setColor(Color.BLACK);
		i = 0;
		for (int x = 0; x < elements.size(); x++) {
			if (!filter.getOrDefault(elements.get(x), true)) continue;
			
			graphics.setFont(new Font("Consolas", Font.PLAIN, 12)); //$NON-NLS-1$
			graphics.drawString(elements.get(x).getTitle(), 0, (i+1)*CELL_HEIGHT - 2);
			
			i++;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int idx = e.getY() / CELL_HEIGHT;

		int i = 0;
		for (CCSeries key : elements) {
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
