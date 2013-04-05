package de.jClipCorn.gui.guiComponents.jCoverChooser;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.ImageUtilities;

public class JCoverChooser extends JComponent implements MouseListener {
	private static final long serialVersionUID = 4981485357566897454L;

	private final static int EXTRACOVERCOUNT = 4;

	private HashMap<Integer, TransformRectangle> rectangles = new HashMap<>();
	
	private ArrayList<ListSelectionListener> listener = new ArrayList<>();

	private int coverWidth = ImageUtilities.COVER_WIDTH;
	private int coverHeight = ImageUtilities.COVER_HEIGHT;
	private int padding_top = 5;
	private int padding_bottom = 5;
	private int coverGap = 10;
	private int circleRadius = 500;
	
	private boolean mode3d = CCProperties.getInstance().PROP_PREVSERIES_3DCOVER.getValue();

	private ArrayList<BufferedImage> images = new ArrayList<>();
	private int currSelected = 0;

	public JCoverChooser() {
		addMouseListener(this);
		update(false);
	}

	public void inc() {
		setCurrSelected(currSelected + 1);
	}

	public void dec() {
		setCurrSelected(currSelected - 1);
	}
	
	public void setCurrSelected(int ns) {
		if (ns >= 0 && ns <= images.size()) {
			currSelected = ns;
		
			update();
			
			for (ListSelectionListener ls : listener) {
				ls.valueChanged(new ListSelectionEvent(this, currSelected, currSelected, false));
			}
		}
	}
	
	public void addListener(ListSelectionListener l) {
		listener.add(l);
	}
	
	public int getCurrentSelected() {
		return currSelected;
	}

	public void setCircleRadius(int rad) {
		circleRadius = rad;

		update();
	}

	public void setPadding(int padTop, int padBot) {
		padding_bottom = padBot;
		padding_top = padTop;

		update();
	}

	public void setCoverGap(int gap) {
		coverGap = gap;

		update();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getComponentWidth(), getComponentHeight());
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	private int getComponentHeight() {
		return getCoverHeight() + 10;
	}

	private int getComponentWidth() {
		if (mode3d) {
			return circleRadius * 2 + 16;
		} else {
			return (EXTRACOVERCOUNT*2 + 1) * (getCoverWidth() + coverGap);
		}
	}

	private int getCoverHeight() {
		return coverHeight + padding_top + padding_bottom;
	}

	private int getCoverWidth() {
		return coverWidth;
	}

	public void addCover(BufferedImage bi) {
		images.add(bi);
		
		update();
	}

	@Override
	public void paintComponent(Graphics g) {
		g.translate(getComponentWidth() / 2, getComponentHeight() / 2);

		paintCover(g);
	}

	private int getCoverX(int cCount) {
		int cw = getCoverWidth();
		int mid = -cw / 2;

		return mid + cCount * (cw + coverGap);
	}
	
	private boolean coverIsSet(int id) {
		return id >= 0 && id < images.size();
	}

	private void paintCover(Graphics g) {
		for (int i = -EXTRACOVERCOUNT; i <= EXTRACOVERCOUNT; i++) {
			int imgid = currSelected + i;

			TransformRectangle tr = rectangles.get(i);

			if (coverIsSet(imgid)) {
				tr.draw(g, images.get(imgid), i == 0);
			}
		}
	}
	
	private void update() {
		update(true);
	}

	private void update(boolean rp) {
		int cw = getCoverWidth();
		int ch = getCoverHeight();

		int y1 = -ch / 2;
		int y2 = ch / 2;

		for (int i = -EXTRACOVERCOUNT; i <= EXTRACOVERCOUNT; i++) {
			TransformRectangle tr;

			if (mode3d) {
				tr = new TransformRectangle3D(new Point(getCoverX(i), y1), new Point(getCoverX(i) + cw, y2));
			} else {
				tr = new TransformRectangle2D(new Point(getCoverX(i), y1), new Point(getCoverX(i) + cw, y2));
			}
				
			
			tr.transform(circleRadius);

			rectangles.put(i, tr);
		}
		
		if (rp) {
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {		
		int x = e.getX() - getComponentWidth()/2;
		int y = e.getY() - getComponentHeight()/2;

		for (int i = -EXTRACOVERCOUNT; i <= EXTRACOVERCOUNT; i++) {
			int imgid = currSelected + i;
			if (coverIsSet(imgid)) {
				TransformRectangle tr = rectangles.get(i);
				if (tr.includesPoint(x, y)) {
					setCurrSelected(imgid);
					return;
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// nothing
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// nothing
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// nothing
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// nothing
	}

	public void setCoverHeight(int coverHeight) {
		this.coverHeight = coverHeight;
	}

	public void setCoverWidth(int coverWidth) {
		this.coverWidth = coverWidth;
	}

	public void clear() {
		currSelected = 0;
		images.clear();
		rectangles.clear();
		
		update();
	}

	public void set3DMode(boolean mode3d) {
		this.mode3d = mode3d;
	}
}
