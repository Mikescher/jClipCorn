package de.jClipCorn.gui.guiComponents.jCoverChooser;

import de.jClipCorn.database.databaseElement.ICCCoveredElement;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JCoverChooser extends JComponent implements MouseListener {
	private static final long serialVersionUID = 4981485357566897454L;

	private final static int MAX_EXTRACOVERCOUNT = 4;

	private final HashMap<Integer, TransformRectangle> rectangles = new HashMap<>();
	
	private final List<ListSelectionListener> listener_selection = new ArrayList<>();
	private final List<JCoverChooserPopupEvent> listener_popup = new ArrayList<>();

	private int coverWidth = ImageUtilities.BASE_COVER_WIDTH;
	private int coverHeight = ImageUtilities.BASE_COVER_HEIGHT;
	private int coverGap = 10;
	private int circleRadius = 500;
	
	private boolean mode3d = CCProperties.getInstance().PROP_PREVSERIES_3DCOVER.getValue();

	private final List<BufferedImage> images_full  = new ArrayList<>();
	private final List<BufferedImage> images_scale = new ArrayList<>();
	private final List<Object> objects = new ArrayList<>();
	private int currSelected = 0;

	private int lastClickTarget = -1;
	
	private final boolean asyncLoading;
	
	public JCoverChooser(boolean forcenoAsync) {
		asyncLoading = CCProperties.getInstance().PROP_MAINFRAME_ASYNC_COVER_LOADING.getValue() && !forcenoAsync;
		
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
		if (ns >= 0 && ns <= images_full.size()) {
			currSelected = ns;
		
			update();
			
			for (ListSelectionListener ls : listener_selection) {
				ls.valueChanged(new ListSelectionEvent(this, currSelected, currSelected, false));
			}
		}
	}
	
	public void addSelectionListener(ListSelectionListener l) {
		listener_selection.add(l);
	}
	
	public void addPopupListener(JCoverChooserPopupEvent l) {
		listener_popup.add(l);
	}
	
	public int getSelectedIndex() {
		return currSelected;
	}
	
	public BufferedImage getSelectedImage() {
		return images_full.get(getSelectedIndex());
	}
	
	public Object getSelectedObject() {
		return objects.get(getSelectedIndex());
	}

	public void setCircleRadius(int rad) {
		circleRadius = rad;

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
			return (MAX_EXTRACOVERCOUNT*2 + 1) * (getCoverWidth() + coverGap);
		}
	}

	private int getCoverHeight() {
		return coverHeight;
	}

	private int getCoverWidth() {
		return coverWidth;
	}

	public void addCover(ICCCoveredElement elem, Object obj) {

		if (asyncLoading) {

			BufferedImage bi = Resources.IMG_COVER_STANDARD.get();
			
			int idx = images_full.size();
			
			images_full.add(bi);
			Tuple<Integer, Integer> sz = ImageUtilities.calcImageSizeToFit(bi.getWidth(), bi.getHeight(), coverWidth, coverHeight);
			images_scale.add(ImageUtilities.getScaledInstance(bi, sz.Item1, sz.Item2));
			
			objects.add(obj);
			
			update();
			
			Thread t = new Thread(() -> LoadAsync(elem, idx));
			t.start();
			
		} else {

			BufferedImage bi = elem.getCover();
			
			images_full.add(bi);
			Tuple<Integer, Integer> sz = ImageUtilities.calcImageSizeToFit(bi.getWidth(), bi.getHeight(), coverWidth, coverHeight);
			images_scale.add(ImageUtilities.getScaledInstance(bi, sz.Item1, sz.Item2));
			
			objects.add(obj);
			
			update();
		}
	}

	private void LoadAsync(ICCCoveredElement elem, int idx) {
		BufferedImage img_full = elem.getCover();
		Tuple<Integer, Integer> sz = ImageUtilities.calcImageSizeToFit(img_full.getWidth(), img_full.getHeight(), coverWidth, coverHeight);
		BufferedImage img_scale = ImageUtilities.getScaledInstance(img_full, sz.Item1, sz.Item2);

		SwingUtils.invokeAndWaitSafe(() ->
		{
			images_full.set(idx, img_full);
			images_scale.set(idx, img_scale);
			
			update(true);
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		g.translate(getWidth() / 2, getHeight() / 2);

		if (g instanceof Graphics2D) {
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		paintCover(g);
	}

	private int getCoverX(int cCount) {
		int cw = getCoverWidth();
		int mid = -cw / 2;

		return mid + cCount * (cw + coverGap);
	}
	
	private boolean coverIsSet(int id) {
		return id >= 0 && id < images_full.size();
	}

	private int getExtraCoverCount()
	{
		int right = getWidth() / 2;

		if (rectangles.size() == 0) return MAX_EXTRACOVERCOUNT;

		for (int i=1; i<=MAX_EXTRACOVERCOUNT; i++)
		{
			if (!rectangles.containsKey(i)) return i-1;

			TransformRectangle tr = rectangles.get(i);

			if (tr.topRight.x > right) return i-1;
			if (tr.bottomRight.x > right) return i-1;
		}
		return MAX_EXTRACOVERCOUNT;
	}

	private void paintCover(Graphics g) {
		int ecc = getExtraCoverCount();

		for (int i = -ecc; i <= ecc; i++) {
			int imgid = currSelected + i;

			TransformRectangle tr = rectangles.get(i);

			if (coverIsSet(imgid)) {
				
				if (i == 0) {
					g.drawImage(images_scale.get(imgid), tr.topLeft.x, tr.topLeft.y, tr.bottomRight.x - tr.topLeft.x, tr.bottomRight.y - tr.topLeft.y, null);
				} else {
					tr.draw(g, images_scale.get(imgid), false);
				}
				
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

		int ecc = getExtraCoverCount();

		for (int i = -ecc; i <= ecc; i++) {
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
		if (isEnabled()) {
			if (e.getButton() == MouseEvent.BUTTON1) {

				int imgid = getCoverForPoint(e.getX(), e.getY());

				if (imgid != Integer.MIN_VALUE) {

					setCurrSelected(imgid);
					
					if (e.getClickCount() == 2 && imgid == lastClickTarget) {
						
						Object o = getSelectedObject();
						BufferedImage i = getSelectedImage();
						
						if (o instanceof ICCCoveredElement) {
							new CoverPreviewFrame(this, (ICCCoveredElement)o).setVisible(true);
						} else if (i != null) {
							new CoverPreviewFrame(this, i).setVisible(true);
						}
						
					}
				}
				
				lastClickTarget = imgid;
				
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				int imgid = getCoverForPoint(e.getX(), e.getY());

				if (imgid != Integer.MIN_VALUE) {
					for (JCoverChooserPopupEvent ev : listener_popup) {
						ev.onPopup(imgid, e);
					}
				}
				
				lastClickTarget = -1;
			}
		}
	}
	
	private int getCoverForPoint(int px, int py) {
		int x = px - getWidth()/2;
		int y = py - getHeight()/2;

		int ecc = getExtraCoverCount();

		for (int i = -ecc; i <= ecc; i++) {
			int imgid = currSelected + i;
			if (coverIsSet(imgid)) {
				TransformRectangle tr = rectangles.get(i);
				if (tr.includesPoint(x, y)) {
					return imgid;
				}
			}
		}
		
		return Integer.MIN_VALUE;
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
		images_full.clear();
		images_scale.clear();
		objects.clear();
		rectangles.clear();
		
		update();
	}

	public void set3DMode(boolean mode3d) {
		this.mode3d = mode3d;
	}
	
	public int getElementCount() {
		return images_full.size();
	}
}
