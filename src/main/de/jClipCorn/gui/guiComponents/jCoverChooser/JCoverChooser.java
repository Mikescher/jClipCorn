package de.jClipCorn.gui.guiComponents.jCoverChooser;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCCoveredElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
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
	private final List<JCoverChooserPopupListener> listener_popup = new ArrayList<>();

	private boolean coverHalfSize = false;
	private int coverGap = 10;
	private int circleRadius = 500;
	
	private boolean mode3d;
	private boolean showScores = false;

	private final List<BufferedImage>     images_full  = new ArrayList<>();
	private final List<BufferedImage>     images_scale = new ArrayList<>();
	private final List<ICCCoveredElement> objects      = new ArrayList<>();
	private int currSelected = 0;

	private int lastClickTarget = -1;

	private final CCMovieList movielist;
	private final boolean asyncLoading;
	private final boolean designmode;

	@DesignCreate
	private static JCoverChooser designCreate() { return new JCoverChooser(CCMovieList.createStub(), false, true); }

	public JCoverChooser(CCMovieList ml) {
		this(ml, false, false);
	}

	public JCoverChooser(CCMovieList ml, boolean forcenoAsync) {
		this(ml, forcenoAsync, false);
	}

	public JCoverChooser(CCMovieList ml, boolean forcenoAsync, boolean designmode)
	{
		super();

		this.designmode = designmode;

		if (designmode) { asyncLoading = false; movielist = null; mode3d = false; update(false); return; }

		asyncLoading = ml.ccprops().PROP_MAINFRAME_ASYNC_COVER_LOADING.getValue() && !forcenoAsync;
		movielist = ml;
		mode3d = ml.ccprops().PROP_PREVSERIES_3DCOVER.getValue();

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

	public int getCurrSelected() {
		return currSelected;
	}
	
	public void addSelectionListener(ListSelectionListener l) {
		listener_selection.add(l);
	}
	public void removeSelectionListener(ListSelectionListener l) {
		listener_selection.remove(l);
	}

	public void addPopupListener(JCoverChooserPopupListener l) {
		listener_popup.add(l);
	}
	public void removePopupListener(JCoverChooserPopupListener l) {
		listener_popup.remove(l);
	}
	
	public int getSelectedIndex() {
		return currSelected;
	}
	
	public BufferedImage getSelectedImage() {
		if (getSelectedIndex() < images_full.size()) return null;
		return images_full.get(getSelectedIndex());
	}
	
	public ICCCoveredElement getSelectedObject() {
		if (getSelectedIndex() < objects.size()) return null;
		return objects.get(getSelectedIndex());
	}

	public void setCircleRadius(int rad) {
		circleRadius = rad;

		update();
	}

	public int getCircleRadius() {
		return circleRadius;
	}

	public void setCoverGap(int gap) {
		coverGap = gap;

		update();
	}

	public int getCoverGap() {
		return coverGap;
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
		return coverHalfSize ? ImageUtilities.HALF_COVER_HEIGHT : ImageUtilities.BASE_COVER_HEIGHT;
	}

	private int getCoverWidth() {
		return coverHalfSize ? ImageUtilities.HALF_COVER_WIDTH : ImageUtilities.BASE_COVER_WIDTH;
	}

	public void addCover(ICCCoveredElement elem) {

		if (asyncLoading) {

			BufferedImage bi = Resources.IMG_COVER_STANDARD.get();
			
			int idx = images_full.size();
			
			images_full.add(bi);
			Tuple<Integer, Integer> sz = ImageUtilities.calcImageSizeToFit(bi.getWidth(), bi.getHeight(), getCoverWidth(), getCoverHeight());
			images_scale.add(ImageUtilities.getScaledInstance(bi, sz.Item1, sz.Item2));
			
			objects.add(elem);
			
			update();
			
			Thread t = new Thread(() -> LoadAsync(elem, idx));
			t.start();
			
		} else {

			BufferedImage bi = elem.getCover();
			
			images_full.add(bi);
			Tuple<Integer, Integer> sz = ImageUtilities.calcImageSizeToFit(bi.getWidth(), bi.getHeight(), getCoverWidth(), getCoverHeight());
			images_scale.add(ImageUtilities.getScaledInstance(bi, sz.Item1, sz.Item2));
			
			objects.add(elem);
			
			update();
		}
	}

	private void LoadAsync(ICCCoveredElement elem, int idx) {
		BufferedImage img_full = elem.getCover();
		Tuple<Integer, Integer> sz = ImageUtilities.calcImageSizeToFit(img_full.getWidth(), img_full.getHeight(), getCoverWidth(), getCoverHeight());
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

	private int getCoverX(int cCount, int cw, int cwSmall) {
		int mid = -cw / 2;

		if (cCount > 0) {

			return mid + cw + coverGap + (cCount-1) * (cwSmall + coverGap);

		} else {
			return mid + cCount * (cwSmall + coverGap);
		}

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

	private void paintCover(Graphics g)
	{
		int ecc = getExtraCoverCount();

		for (int i = -ecc; i <= ecc; i++)
		{
			int imgid = currSelected + i;

			TransformRectangle tr = rectangles.get(i);

			if (coverIsSet(imgid))
			{
				if (i == 0)
				{
					g.drawImage(images_scale.get(imgid), tr.topLeft.x, tr.topLeft.y, tr.bottomRight.x - tr.topLeft.x, tr.bottomRight.y - tr.topLeft.y, null);
				}
				else
				{
					tr.draw(g, images_scale.get(imgid), false);
				}

				if (showScores && objects.get(imgid).score().get() != CCUserScore.RATING_NO)
				{
					var icn = objects.get(imgid).score().get().getIconRef(!Str.isNullOrWhitespace(objects.get(imgid).scoreComment().get()));
					if (icn != null)
					{
						if (i == 0) g.drawImage(icn.getImage16x16(), tr.bottomRight.x-18, tr.bottomRight.y-18, 16, 16, null);
						else        g.drawImage(icn.getImage16x16(), tr.bottomRight.x-8,  tr.bottomRight.y-8,  16, 16, null);
					}
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

		int y1 = (-ch / 2);
		int y2 = (+ch / 2);

		int ecc = getExtraCoverCount();

		var props = designmode ? CCProperties.createInMemory() : movielist.ccprops();

		var sideScaleDown = props.PROP_PREVSERIES_SMALLERCOVER_FACTOR.getValue();
		var sideScaleEnabled = props.PROP_PREVSERIES_SMALLER_COVER.getValue();

		int cw_small = cw;
		int y1_small = y1;
		int y2_small = y2;

		if (sideScaleEnabled && sideScaleDown != 1.0) {
			y1_small = -(int)((ch*sideScaleDown) / 2.0);
			y2_small = +(int)((ch*sideScaleDown) / 2.0);
			cw_small = (int)(cw * sideScaleDown);
		}

		for (int i = -ecc; i <= ecc; i++) {
			TransformRectangle tr;

			var ucw = cw;
			var uy1 = y1;
			var uy2 = y2;

			if (i != 0 && sideScaleDown != 1.0) {
				ucw = cw_small;
				uy1 = y1_small;
				uy2 = y2_small;
			}

			if (mode3d) {
				tr = new TransformRectangle3D(props, new Point(getCoverX(i, cw, cw_small), uy1), new Point(getCoverX(i, cw, cw_small) + ucw, uy2));
			} else {
				tr = new TransformRectangle2D(props, new Point(getCoverX(i, cw, cw_small), uy1), new Point(getCoverX(i, cw, cw_small) + ucw, uy2));
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
							new CoverPreviewFrame(this, movielist, i).setVisible(true);
						}
						
					}
				}
				
				lastClickTarget = imgid;
				
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				int imgid = getCoverForPoint(e.getX(), e.getY());

				if (imgid != Integer.MIN_VALUE) {
					for (JCoverChooserPopupListener ev : listener_popup) {
						ev.onPopup(new JCoverChooserPopupListener.CoverChooseEvent(this, imgid, e));
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

	public void setCoverHalfSize(boolean hs) {
		coverHalfSize = hs;
	}

	public boolean getCoverHalfSize() {
		return coverHalfSize;
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

	public boolean get3DMode() {
		return mode3d;
	}

	public void setShowScores(boolean ss) {
		showScores = ss;
	}

	public boolean getShowScores() {
		return showScores;
	}

	public int getElementCount() {
		return images_full.size();
	}
}
