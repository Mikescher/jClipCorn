package de.jClipCorn.gui.frames.coverCropFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.gui.guiComponents.jCanvasLabel.JCanvasLabel;
import de.jClipCorn.gui.guiComponents.jCanvasLabel.PaintComponentEvent;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.ClipboardUtilities;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.listener.ImageCropperResultListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.regex.Pattern;

public class CoverCropDialog extends JFrame
{
	private final static int MAX_DRAG_PIXELDISTANCE = 5;
	private final static int MIN_WIDTH = 50;
	private final static int MIN_HEIGHT = 50;

	private final static float CROP_TRANSPARENCY = 0.85f;

	private final static int CROPSTATE_NOTHING 		= 0b00000;

	private final static int CROPSTATE_TOP 			= 0b01000;
	private final static int CROPSTATE_RIGHT 		= 0b00100;
	private final static int CROPSTATE_BOTTOM 		= 0b00010;
	private final static int CROPSTATE_LEFT 		= 0b00001;

	private final static int CROPSTATE_TOPLEFT 		= CROPSTATE_TOP 	| CROPSTATE_LEFT;
	private final static int CROPSTATE_TOPRIGHT 	= CROPSTATE_TOP 	| CROPSTATE_RIGHT;
	private final static int CROPSTATE_BOTTOMLEFT 	= CROPSTATE_BOTTOM 	| CROPSTATE_LEFT;
	private final static int CROPSTATE_BOTTOMRIGHT 	= CROPSTATE_BOTTOM 	| CROPSTATE_RIGHT;

	private final static int CROPSTATE_DRAG 		= 0b10000;

	private ImageCropperResultListener listener;

	private BufferedImage img;
	private int imgheight, imgwidth;

	private double displayScale = 1;

	private Point crop_tl = new Point();
	private Point crop_br = new Point();

	private Point currZoomPos = new Point();

	private Point dragStartPoint = new Point();
	private Point dragStartCrop_tl = new Point();
	private Point dragStartCrop_br = new Point();

	private int dragMode = CROPSTATE_NOTHING;

	private TexturePaint transparencyPattern;

	public CoverCropDialog(Component owner, BufferedImage i, ImageCropperResultListener rl)
	{
		this(owner, i, rl, false);
	}

	public CoverCropDialog(Component owner, BufferedImage i, ImageCropperResultListener rl, boolean isSeries)
	{
		super();

		initComponents();

		this.img = ImageUtilities.deepCopyImage(i);
		this.listener = rl;
		chckbxSeriesPreview.setSelected(isSeries);

		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());

		var dBig = new Dimension(ImageUtilities.BASE_COVER_WIDTH, ImageUtilities.BASE_COVER_HEIGHT);
		lblCoverPreviewBig.setPreferredSize(dBig);
		lblCoverPreviewBig.setMinimumSize(dBig);
		lblCoverPreviewBig.setMaximumSize(dBig);

		var dSmall = new Dimension(ImageUtilities.HALF_COVER_WIDTH, ImageUtilities.HALF_COVER_HEIGHT);
		lblCoverPreviewSmall.setPreferredSize(dSmall);
		lblCoverPreviewSmall.setMinimumSize(dSmall);
		lblCoverPreviewSmall.setMaximumSize(dSmall);


		imageChanged();
		autoTransparencyCalcCrops();
	}

	private void onFormResized(ComponentEvent e) {
		recalcSizes();
	}

	private void onWindowOpened(WindowEvent e) {
		repaintAll();
	}

	private void onOkay(ActionEvent e) {
		if (listener != null) listener.editingFinished(getNewCroppedImage());
		dispose();
	}

	private void onCancel(ActionEvent e) {
		if (listener != null) listener.editingCanceled();
		dispose();
	}

	private void onRotateCW(ActionEvent e) {
		rotate90(1);
	}

	private void onRotateCCW(ActionEvent e) {
		rotate90(-1);
	}

	private void onFlipX(ActionEvent e) {
		flip(true, false);
	}

	private void onFlipY(ActionEvent e) {
		flip(false, true);
	}

	private void autoCalc(ActionEvent e) {
		autoCalcCrops();
		repaintAll();
	}

	private void reset(ActionEvent e) {
		resetCrops();
	}

	private void onImageMouseReleased(MouseEvent e) {
		endDrag(e);
	}

	private void onImageMousePressed(MouseEvent e) {
		startDrag(getDragModeForMousePos(e), e);
	}

	private void onImageMouseExited(MouseEvent e) {
		endDrag(e);
	}

	private void onImageMouseMoved(MouseEvent e) {
		repaintZoomImage(lblZoom.getGraphics(), e);
		updateCursor(e);
		updateMouseInfoLabel(e);
	}

	private void onImageMouseDragged(MouseEvent e) {
		repaintZoomImage(lblZoom.getGraphics(), e);
		updateCursor(e);
		updateDrag(e);
		updateMouseInfoLabel(e);
	}

	private void onCropTop(ActionEvent e) {
		int widthCurr = crop_br.x - crop_tl.x;
		int heightCurr = crop_br.y - crop_tl.y;

		int neededH = (int) Math.round(widthCurr / ImageUtilities.COVER_RATIO);

		setBottomCropChecked(crop_br.y - (heightCurr - neededH));

		updateCropInfoLabel();
		repaintAll();
	}

	private void onCropLeft(ActionEvent e) {
		int widthCurr = crop_br.x - crop_tl.x;
		int heightCurr = crop_br.y - crop_tl.y;

		int neededW = (int) Math.round(heightCurr * ImageUtilities.COVER_RATIO);

		setRightCropChecked(crop_br.x - (widthCurr - neededW));

		updateCropInfoLabel();
		repaintAll();
	}

	private void onCropBottom(ActionEvent e) {
		int widthCurr = crop_br.x - crop_tl.x;
		int heightCurr = crop_br.y - crop_tl.y;

		int neededH = (int) Math.round(widthCurr / ImageUtilities.COVER_RATIO);

		setTopCropChecked(crop_tl.y + (heightCurr - neededH));

		updateCropInfoLabel();
		repaintAll();
	}

	private void onCropRight(ActionEvent e) {
		int widthCurr = crop_br.x - crop_tl.x;
		int heightCurr = crop_br.y - crop_tl.y;

		int neededW = (int) Math.round(heightCurr * ImageUtilities.COVER_RATIO);

		setLeftCropChecked(crop_tl.x + (widthCurr - neededW));

		updateCropInfoLabel();
		repaintAll();
	}

	private void onCropCenter(ActionEvent e) {
		onCropHorizontal(e);
		onCropVertical(e);
	}

	private void onCropVertical(ActionEvent e) {
		int widthCurr = crop_br.x - crop_tl.x;
		int heightCurr = crop_br.y - crop_tl.y;

		int neededH = (int) Math.round(widthCurr / ImageUtilities.COVER_RATIO);

		setTopCropChecked(crop_tl.y    + (heightCurr - neededH)/2);
		setBottomCropChecked(crop_br.y - (heightCurr - neededH)/2);

		updateCropInfoLabel();
		repaintAll();
	}

	private void onCropHorizontal(ActionEvent e) {
		int widthCurr = crop_br.x - crop_tl.x;
		int heightCurr = crop_br.y - crop_tl.y;

		int neededW = (int) Math.round(heightCurr * ImageUtilities.COVER_RATIO);

		setLeftCropChecked(crop_tl.x +  (widthCurr - neededW)/2);
		setRightCropChecked(crop_br.x - (widthCurr - neededW)/2);

		updateCropInfoLabel();
		repaintAll();
	}

	private void onZoomChanged() {
		repaintZoomImage(lblZoom.getGraphics(), null);
	}

	private void imageChanged() {
		imgheight = img.getHeight();
		imgwidth = img.getWidth();
		crop_tl = new Point(0, 0);
		crop_br = new Point(imgwidth, imgheight);

		BufferedImage pattern = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics gpat = pattern.getGraphics();
		gpat.setColor(Color.LIGHT_GRAY);
		gpat.fillRect(0, 0, 16, 16);
		gpat.setColor(Color.WHITE);
		gpat.fillRect(0, 0, 8, 8);
		gpat.fillRect(8, 8, 16, 16);
		transparencyPattern = new TexturePaint(pattern, new Rectangle(0, 0, 16, 16));

		updateCropInfoLabel();
	}

	private void autoCalcCrops() {
		Rectangle r =  ImageUtilities.getNonColorImageRect(img, img.getRGB(0,  0));

		crop_tl.x = r.x;
		crop_tl.y = r.y;
		crop_br.x = r.x + r.width;
		crop_br.y = r.y + r.height;

		updateCropInfoLabel();
	}

	private void autoTransparencyCalcCrops() {
		Rectangle r =  ImageUtilities.getNonTransparentImageRect(img);

		crop_tl.x = r.x;
		crop_tl.y = r.y;
		crop_br.x = r.x + r.width;
		crop_br.y = r.y + r.height;

		updateCropInfoLabel();
	}

	private void updateCropInfoLabel() {
		if (lblPosition == null || lblSize == null || lblRatio == null) return;

		int widthCurr = crop_br.x - crop_tl.x;
		int heightCurr = crop_br.y - crop_tl.y;
		int widthStorage = ImageUtilities.calcImageSizeForStorage(widthCurr, heightCurr).Item1;
		int heightStorage = ImageUtilities.calcImageSizeForStorage(widthCurr, heightCurr).Item2;

		lblPosition.setText(LocaleBundle.getFormattedString("CoverCropFrame.lblPosition.text", crop_tl.x, crop_tl.y)); //$NON-NLS-1$
		lblSize.setText(LocaleBundle.getFormattedString("CoverCropFrame.lblSize.text", widthCurr, heightCurr, widthStorage, heightStorage)); //$NON-NLS-1$
		lblRatio.setText(LocaleBundle.getFormattedString("CoverCropFrame.lblRatio.text", (crop_br.x - crop_tl.x * 1d) / (crop_br.y - crop_tl.y), ImageUtilities.COVER_RATIO)); //$NON-NLS-1$

		lblRatioState.setText(LocaleBundle.getString(isRatioAcceptable() ? "CoverCropFrame.lblOK.textOK" : "CoverCropFrame.lblOK.textNOK")); //$NON-NLS-1$ //$NON-NLS-2$
		lblRatioState.setForeground(isRatioAcceptable() ? Color.GREEN : Color.RED);
	}

	private boolean isRatioAcceptable() {
		return ImageUtilities.isImageRatioAcceptable(crop_br.x - crop_tl.x, crop_br.y - crop_tl.y);
	}

	public void repaintAll() {
		recalcSizes();

		repaintMainImage(lblImage.getGraphics());
		repaintZoomImage(lblZoom.getGraphics(), null);
		repaintPreviewImagesBig(lblCoverPreviewBig.getGraphics());
		repaintPreviewImagesSmall(lblCoverPreviewSmall.getGraphics());

		updateCropInfoLabel();
	}

	private void recalcSizes() {
		displayScale = getDispScale();
	}

	private double getDispScale() {
		int newWidth = lblImage.getWidth();
		int newHeight = lblImage.getHeight();

		double ratio = img.getWidth() / (img.getHeight() * 1d);
		double newRatio = newWidth / (newHeight*1d);

		double nW;
		double nH;

		if (ratio > newRatio) {
			nW = newWidth;
			nH = (int) (1 / ratio * newWidth)*1d;

			return nH / img.getHeight();
		} else {
			nH = newHeight;
			nW = (int) (ratio * newHeight)*1d;

			return nW / img.getWidth();
		}
	}

	private void repaintPreviewImagesBig(Graphics g) {
		if (g == null) return;

		if (chckbxShowTransparency.isSelected())
			((Graphics2D)g).setPaint(transparencyPattern);
		else
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, ImageUtilities.BASE_COVER_WIDTH, ImageUtilities.BASE_COVER_HEIGHT);

		if (chckbxSeriesPreview.isSelected()) {
			BufferedImage bid = ImageUtilities.resizeCoverImageForFullSizeUI(getCroppedImage());
			ImageUtilities.makeFullSizeSeriesCover(bid);
			g.drawImage(bid, 0, 0, null);
		} else {
			g.drawImage(ImageUtilities.resizeCoverImageForFullSizeUI(getCroppedImage()), 0, 0, null);
		}

		if(chckbxShowImageBorders.isSelected()) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, 0, ImageUtilities.BASE_COVER_HEIGHT-1);
			g.drawLine(0, ImageUtilities.BASE_COVER_HEIGHT-1, ImageUtilities.BASE_COVER_WIDTH-1, ImageUtilities.BASE_COVER_HEIGHT-1);
			g.drawLine(ImageUtilities.BASE_COVER_WIDTH-1, ImageUtilities.BASE_COVER_HEIGHT-1, ImageUtilities.BASE_COVER_WIDTH-1, 0);
			g.drawLine(ImageUtilities.BASE_COVER_WIDTH-1, 0, 0, 0);
		}
	}

	private BufferedImage getCroppedImage() {
		return img.getSubimage(crop_tl.x, crop_tl.y, crop_br.x - crop_tl.x, crop_br.y - crop_tl.y);
	}

	private BufferedImage getNewCroppedImage() {
		BufferedImage b = new BufferedImage(crop_br.x - crop_tl.x, crop_br.y - crop_tl.y, img.getType());
		Graphics g = b.getGraphics();
		g.drawImage(img, -crop_tl.x, -crop_tl.y, null);
		g.dispose();
		return b;
	}

	private void repaintPreviewImagesSmall(Graphics g) {
		if (g == null) return;

		if (chckbxShowTransparency.isSelected())
			((Graphics2D)g).setPaint(transparencyPattern);
		else
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, ImageUtilities.BASE_COVER_WIDTH, ImageUtilities.BASE_COVER_HEIGHT);

		if (chckbxSeriesPreview.isSelected()) {
			BufferedImage bid = ImageUtilities.resizeCoverImageForFullSizeUI(getCroppedImage());
			ImageUtilities.makeFullSizeSeriesCover(bid);
			g.drawImage(ImageUtilities.resizeCoverImageForHalfSizeUI(bid), 0, 0, null);
		} else
			g.drawImage(ImageUtilities.resizeCoverImageForHalfSizeUI(getCroppedImage()), 0, 0, null);

		if(chckbxShowImageBorders.isSelected()) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, 0, ImageUtilities.HALF_COVER_HEIGHT-1);
			g.drawLine(0, ImageUtilities.HALF_COVER_HEIGHT-1, ImageUtilities.HALF_COVER_WIDTH-1, ImageUtilities.HALF_COVER_HEIGHT-1);
			g.drawLine(ImageUtilities.HALF_COVER_WIDTH-1, ImageUtilities.HALF_COVER_HEIGHT-1, ImageUtilities.HALF_COVER_WIDTH-1, 0);
			g.drawLine(ImageUtilities.HALF_COVER_WIDTH-1, 0, 0, 0);
		}
	}

	private void repaintZoomImage(Graphics g_zoom, MouseEvent e) {
		if (g_zoom == null) return;

		Graphics2D g = (Graphics2D) lblZoom.getDoubleBufferStrategy().getCurrent().getGraphics();

		int w = lblZoom.getWidth();
		int rh = lblZoom.getHeight();
		int h = Math.min(rh, 250);
		int zoom = (int) ((spnZoom == null) ? 2 : spnZoom.getValue());

		Point mp;

		if (e == null) {
			mp = new Point(currZoomPos);
		} else {
			mp = e.getLocationOnScreen();
			mp.x -= lblImage.getLocationOnScreen().x;
			mp.y -= lblImage.getLocationOnScreen().y;

			currZoomPos = new Point(mp);
		}

		g.setColor(Color.BLACK);

		if (mp.x < 0 || mp.y < 0 || mp.x > lblImage.getWidth() || mp.y > lblImage.getHeight()) {
			g.fillRect(0, 0, w, rh);
			return;
		}

		int posx = (int) (mp.x / displayScale - w/(2*zoom));
		int posy = (int) (mp.y / displayScale - h/(2*zoom));

		int capx = (int) (mp.x / displayScale + w/(2*zoom));
		int capy = (int) (mp.y / displayScale + h/(2*zoom));

		if (capx > imgwidth) {
			posx -= capx - imgwidth;
			capx = imgwidth;
		}

		if (capy > imgheight) {
			posy -= capy - imgheight;
			capy = imgheight;
		}

		if (posx < 0) {
			capx -= posx;
			posx = 0;
		}

		if (posy < 0) {
			capy -= posy;
			posy = 0;
		}

		if (chckbxShowTransparency.isSelected())
			g.setPaint(transparencyPattern);
		else
			g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.BLACK);
		g.drawImage(img, 0, 0, w, h, posx, posy, capx, capy, null);

		int ch_x = (int) (w * (mp.x - posx * displayScale) / (displayScale * (capx - posx)));
		int ch_y = (int) (h * (mp.y - posy * displayScale) / (displayScale * (capy - posy)));

		if (ch_y <= h) {
			g.drawLine(0, ch_y, w, ch_y);
		}

		g.drawLine(ch_x, 0, ch_x, h);

		g_zoom.drawImage(lblZoom.getDoubleBufferStrategy().getCurrent(), 0, 0, null);

		lblZoom.getDoubleBufferStrategy().doSwitch();
	}

	private void repaintMainImage(Graphics g_img) {
		if (g_img == null) return;

		Graphics2D g = (Graphics2D) lblImage.getDoubleBufferStrategy().getCurrent().getGraphics();

		if (chckbxShowTransparency.isSelected())
			g.setPaint(transparencyPattern);
		else
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, lblImage.getWidth(), lblImage.getHeight());

		int w = (int)(imgwidth * displayScale);
		int h = (int)(imgheight * displayScale);

		int t = (int) (crop_tl.y * displayScale);
		int r = (int) (crop_br.x * displayScale);
		int b = (int) (crop_br.y * displayScale);
		int l = (int) (crop_tl.x * displayScale);

		g.drawImage(img, 0, 0, w, h, null);

		if(chckbxShowImageBorders.isSelected()) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, 0, h-1);
			g.drawLine(0, h-1, w-1, h-1);
			g.drawLine(w-1, h-1, w-1, 0);
			g.drawLine(w-1, 0, 0, 0);
		}

		// Draw Crop Outline

		if (chckbxShowCropOutline.isSelected()) {
			g.setColor(Color.BLACK);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, CROP_TRANSPARENCY));
			g.fillRect(0, 0, l, h);
			g.fillRect(l, 0, r - l, t);
			g.fillRect(r, 0, w - r, h);
			g.fillRect(l, b, r - l, h - b);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		}

		g.setColor(((dragMode & CROPSTATE_TOP) != 0) ? Color.RED : Color.BLACK);
		g.drawLine(l, t, r, t);
		g.setColor(((dragMode & CROPSTATE_RIGHT) != 0) ? Color.RED : Color.BLACK);
		g.drawLine(r, t, r, b);
		g.setColor(((dragMode & CROPSTATE_BOTTOM) != 0) ? Color.RED : Color.BLACK);
		g.drawLine(l, b, r, b);
		g.setColor(((dragMode & CROPSTATE_LEFT) != 0) ? Color.RED : Color.BLACK);
		g.drawLine(l, t, l, b);
		g.setColor(Color.BLACK);

		g_img.drawImage(lblImage.getDoubleBufferStrategy().getCurrent(), 0, 0, null);

		lblImage.getDoubleBufferStrategy().doSwitch();
	}

	private void updateCursor(MouseEvent e) {
		int drag = getDragModeForMousePos(e);

		if (drag == CROPSTATE_NOTHING) {
			lblImage.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else {
			setDragCursor(drag);
		}
	}

	private void setDragCursor(int drag) {
		if (drag == CROPSTATE_TOP) {
			lblImage.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_TOPRIGHT) {
			lblImage.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_RIGHT) {
			lblImage.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_BOTTOMRIGHT) {
			lblImage.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_BOTTOM) {
			lblImage.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_BOTTOMLEFT) {
			lblImage.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_LEFT) {
			lblImage.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_TOPLEFT) {
			lblImage.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_DRAG) {
			lblImage.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} else {
			lblImage.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	private int getDragModeForMousePos(MouseEvent e) {
		int drag = CROPSTATE_NOTHING;

		double dist_top = Math.abs(e.getY() - crop_tl.y * displayScale);
		double dist_right = Math.abs(e.getX() - crop_br.x * displayScale);
		double dist_bottom = Math.abs(e.getY() - crop_br.y * displayScale);
		double dist_left = Math.abs(e.getX() - crop_tl.x * displayScale);

		if (dist_top < MAX_DRAG_PIXELDISTANCE) {
			drag |= CROPSTATE_TOP;
		} else if (dist_bottom < MAX_DRAG_PIXELDISTANCE) {
			drag |= CROPSTATE_BOTTOM;
		}

		if (dist_right < MAX_DRAG_PIXELDISTANCE) {
			drag |= CROPSTATE_RIGHT;
		} else if (dist_left < MAX_DRAG_PIXELDISTANCE) {
			drag |= CROPSTATE_LEFT;
		}

		if (drag == CROPSTATE_NOTHING && e.getY() > crop_tl.y * displayScale && e.getX() < crop_br.x * displayScale && e.getY() < crop_br.y * displayScale && e.getX() > crop_tl.x * displayScale) {
			drag = CROPSTATE_DRAG;
		}

		if (chckbxLockRatio.isSelected()) {
			drag = simplifyDragMode(drag);
		}

		return drag;
	}

	private int simplifyDragMode(int drag) {
		if ((drag & CROPSTATE_RIGHT) != 0) return CROPSTATE_RIGHT;
		if ((drag & CROPSTATE_BOTTOM) != 0) return CROPSTATE_BOTTOM;
		if ((drag & CROPSTATE_TOP) != 0) return CROPSTATE_TOP;
		if ((drag & CROPSTATE_LEFT) != 0) return CROPSTATE_LEFT;
		if ((drag & CROPSTATE_DRAG) != 0) return CROPSTATE_DRAG;

		return CROPSTATE_NOTHING;
	}

	private void startDrag(int mode, MouseEvent e) {
		dragMode = mode;

		dragStartPoint = new Point(e.getX(), e.getY());
		dragStartCrop_tl = new Point(crop_tl);
		dragStartCrop_br = new Point(crop_br);

		updateDrag(e);
	}

	private void endDrag(MouseEvent e) {
		updateDrag(e);

		dragMode = CROPSTATE_NOTHING;

		repaintAll();
	}

	private void updateDrag(MouseEvent e) {
		if (dragMode == CROPSTATE_NOTHING) return;

		if ((dragMode & CROPSTATE_TOP) != 0) {
			int next = (int)(e.getY() / displayScale);
			setTopCropChecked(next);

			if (chckbxLockRatio.isSelected()) {
				setRightCropChecked((int) ((crop_br.y - crop_tl.y) * ImageUtilities.COVER_RATIO) + crop_tl.x);
			}
		}

		if ((dragMode & CROPSTATE_RIGHT) != 0) {
			int next = (int)(e.getX() / displayScale);
			setRightCropChecked(next);

			if (chckbxLockRatio.isSelected()) {
				setBottomCropChecked((int) ((crop_br.x - crop_tl.x) / ImageUtilities.COVER_RATIO) + crop_tl.y);
			}
		}

		if ((dragMode & CROPSTATE_BOTTOM) != 0) {
			int next = (int)(e.getY() / displayScale);
			setBottomCropChecked(next);

			if (chckbxLockRatio.isSelected()) {
				setRightCropChecked((int) ((crop_br.y - crop_tl.y) * ImageUtilities.COVER_RATIO) + crop_tl.x);
			}
		}

		if ((dragMode & CROPSTATE_LEFT) != 0) {
			int next = (int)(e.getX() / displayScale);
			setLeftCropChecked(next);

			if (chckbxLockRatio.isSelected()) {
				setBottomCropChecked((int) ((crop_br.x - crop_tl.x) / ImageUtilities.COVER_RATIO) + crop_tl.y);
			}
		}

		if ((dragMode & CROPSTATE_DRAG) != 0) {
			int movX = e.getX() - dragStartPoint.x;
			int movY = e.getY() - dragStartPoint.y;

			movX = (int) (movX / displayScale);
			movY = (int) (movY / displayScale);

			if (movX < 0) {
				setLeftCropChecked(dragStartCrop_tl.x + movX);
				setRightCropChecked(dragStartCrop_br.x + movX);
			} else {
				setRightCropChecked(dragStartCrop_br.x + movX);
				setLeftCropChecked(dragStartCrop_tl.x + movX);
			}

			if (movY < 0) {
				setTopCropChecked(dragStartCrop_tl.y + movY);
				setBottomCropChecked(dragStartCrop_br.y + movY);
			} else {
				setBottomCropChecked(dragStartCrop_br.y + movY);
				setTopCropChecked(dragStartCrop_tl.y + movY);
			}
		}

		setDragCursor(dragMode);

		repaintMainImage(lblImage.getGraphics());

		updateCropInfoLabel();
	}

	private void setLeftCropChecked(int next) {
		next = Math.min(crop_br.x-MIN_WIDTH, next);
		next = Math.max(0, next);
		crop_tl.x = next;
	}

	private void setBottomCropChecked(int next) {
		next = Math.min(imgheight, next);
		next = Math.max(crop_tl.y + MIN_HEIGHT, next);
		crop_br.y = next;
	}

	private void setRightCropChecked(int next) {
		next = Math.max(crop_tl.x+MIN_WIDTH, next);
		next = Math.min(imgwidth, next);
		crop_br.x = next;
	}

	private void setTopCropChecked(int next) {
		next = Math.max(0, next);
		next = Math.min(crop_br.y-MIN_HEIGHT, next);
		crop_tl.y = next;
	}

	private void rotate90(int dir) {
		BufferedImage newb = new BufferedImage(imgheight, imgwidth, BufferedImage.TYPE_4BYTE_ABGR);

		AffineTransform at = new AffineTransform();
		at.translate(imgheight / 2, imgwidth / 2);
		at.rotate(Math.PI / 2 * dir);
		at.translate(-imgwidth / 2, -imgheight / 2);

		((Graphics2D)newb.getGraphics()).drawImage(img, at, null);
		img = newb;
		imageChanged();
		repaintAll();
	}

	private void flip(boolean flipx, boolean flipy) {
		BufferedImage newb = new BufferedImage(imgwidth, imgheight, BufferedImage.TYPE_4BYTE_ABGR);

		AffineTransform at = new AffineTransform();
		at.translate(flipx ? imgwidth : 0, flipy ? imgheight : 0);
		at.scale(flipx ? -1 : 1, flipy ? -1 : 1);

		((Graphics2D)newb.getGraphics()).drawImage(img, at, null);
		img = newb;
		imageChanged();
		repaintAll();
	}

	private void updateMouseInfoLabel(MouseEvent e) {
		int x = (int)(e.getX() / displayScale);
		int y = (int)(e.getY() / displayScale);

		String col = "?"; //$NON-NLS-1$

		if (x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight()) {
			int clr = img.getRGB(x, y);
			col = String.format("#%02X%02X%02X", (clr & 0x00ff0000) >> 16, (clr & 0x0000ff00) >> 8, (clr & 0x000000ff) >> 0); //$NON-NLS-1$
		}

		lblMouse.setText(LocaleBundle.getFormattedString("CoverCropFrame.lblMouse.text", x, y, col)); //$NON-NLS-1$
	}

	private void resetCrops() {
		crop_tl.x = 0;
		crop_tl.y = 0;
		crop_br.x = imgwidth;
		crop_br.y = imgheight;

		repaintAll();
	}

	private void onMainImagePaint(PaintComponentEvent e) {
		repaintMainImage(e.Graphics);
	}

	private void onZoomImagePaint(PaintComponentEvent e) {
		repaintZoomImage(e.Graphics, null);
	}

	private void onBigCoverPaint(PaintComponentEvent e) {
		repaintPreviewImagesBig(e.Graphics);
	}

	private void onSmallCoverPaint(PaintComponentEvent e) {
		repaintPreviewImagesSmall(e.Graphics);
	}

	@SuppressWarnings("nls")
	private void copyParams(ActionEvent e) {
		var cb = Str.format("@CROP_PARAMS'{'{0},{1},{2},{3},{4},{5}'}'", imgwidth, imgheight, crop_tl.x, crop_tl.y, crop_br.x, crop_br.y);
		ClipboardUtilities.setString(cb);
	}

	@SuppressWarnings("nls")
	private void pasteParams(ActionEvent e) {
		var cb = ClipboardUtilities.getString();
		if (cb == null) return;

		var rex = Pattern.compile("@CROP_PARAMS\\{(?<w>[0-9]+),(?<h>[0-9]+),(?<tlx>[0-9]+),(?<tly>[0-9]+),(?<brx>[0-9]+),(?<bry>[0-9]+)}");
		var m = rex.matcher(cb);

		if (m.matches())
		{
			//var w = Integer.parseInt(m.group("w"));
			//var h = Integer.parseInt(m.group("h"));
			var tlx = Integer.parseInt(m.group("tlx"));
			var tly = Integer.parseInt(m.group("tly"));
			var brx = Integer.parseInt(m.group("brx"));
			var bry = Integer.parseInt(m.group("bry"));

			crop_tl.x = 0;
			crop_tl.y = 0;
			crop_br.x = imgwidth;
			crop_br.y = imgheight;

			setLeftCropChecked(tlx);
			setTopCropChecked(tly);
			setRightCropChecked(brx);
			setBottomCropChecked(bry);

			repaintAll();
		}
	}

	@SuppressWarnings("nls")
	private void pasteParamsPerc(ActionEvent e) {
		var cb = ClipboardUtilities.getString();
		if (cb == null) return;

		var rex = Pattern.compile("@CROP_PARAMS\\{(?<w>[0-9]+),(?<h>[0-9]+),(?<tlx>[0-9]+),(?<tly>[0-9]+),(?<brx>[0-9]+),(?<bry>[0-9]+)}");
		var m = rex.matcher(cb);

		if (m.matches())
		{
			var w = Integer.parseInt(m.group("w"));
			var h = Integer.parseInt(m.group("h"));
			var tlx = Integer.parseInt(m.group("tlx"));
			var tly = Integer.parseInt(m.group("tly"));
			var brx = Integer.parseInt(m.group("brx"));
			var bry = Integer.parseInt(m.group("bry"));

			var r_tlx = (int)((tlx / (w * 1.0)) * imgwidth);
			var r_tly = (int)((tly / (h * 1.0)) * imgheight);
			var r_brx = (int)((brx / (w * 1.0)) * imgwidth);
			var r_bry = (int)((bry / (h * 1.0)) * imgheight);

			crop_tl.x = 0;
			crop_tl.y = 0;
			crop_br.x = imgwidth;
			crop_br.y = imgheight;

			setLeftCropChecked(r_tlx);
			setTopCropChecked(r_tly);
			setRightCropChecked(r_brx);
			setBottomCropChecked(r_bry);

			repaintAll();
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel1 = new JPanel();
		btnReset = new JButton();
		panel3 = new JPanel();
		btnAutoCalc = new JButton();
		panel4 = new JPanel();
		btnRotateCW = new JButton();
		panel10 = new JPanel();
		btnRotateCCW = new JButton();
		panel5 = new JPanel();
		btnFlipX = new JButton();
		panel11 = new JPanel();
		btnFlipY = new JButton();
		panel12 = new JPanel();
		lblImage = new JCanvasLabel();
		panel6 = new JPanel();
		chckbxLockRatio = new JCheckBox();
		chckbxShowCropOutline = new JCheckBox();
		chckbxShowImageBorders = new JCheckBox();
		chckbxShowTransparency = new JCheckBox();
		chckbxSeriesPreview = new JCheckBox();
		lblPosition = new JLabel();
		lblSize = new JLabel();
		lblRatio = new JLabel();
		lblRatioState = new JLabel();
		lblMouse = new JLabel();
		panel13 = new JPanel();
		btnCopyParams = new JButton();
		btnPasteAbs = new JButton();
		btnPasteRel = new JButton();
		panel7 = new JPanel();
		spnZoom = new JSpinner();
		lblZoom = new JCanvasLabel();
		panel8 = new JPanel();
		lblCoverPreviewBig = new JCanvasLabel();
		lblCoverPreviewSmall = new JCanvasLabel();
		panel9 = new JPanel();
		btnResizeTop = new JButton();
		btnResizeLeft = new JButton();
		btnResizeCenter = new JButton();
		btnResizeRight = new JButton();
		btnResizeVert = new JButton();
		btnResizeBottom = new JButton();
		btnResizeHorz = new JButton();
		panel2 = new JPanel();
		btnOK = new JButton();
		btnAbort = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("CoverCropFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(800, 600));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				onFormResized(e);
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				onWindowOpened(e);
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $lcgap, [170dlu,pref], $lcgap, 170dlu, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, 2*($lgap, default), $ugap")); //$NON-NLS-1$

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

			//---- btnReset ----
			btnReset.setText(LocaleBundle.getString("CoverCropFrame.btnReset.text")); //$NON-NLS-1$
			btnReset.addActionListener(e -> reset(e));
			panel1.add(btnReset);

			//======== panel3 ========
			{
				panel3.setMinimumSize(new Dimension(12, 12));
				panel3.setPreferredSize(new Dimension(12, 12));
				panel3.setLayout(null);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel3.getComponentCount(); i++) {
						Rectangle bounds = panel3.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel3.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel3.setMinimumSize(preferredSize);
					panel3.setPreferredSize(preferredSize);
				}
			}
			panel1.add(panel3);

			//---- btnAutoCalc ----
			btnAutoCalc.setText(LocaleBundle.getString("CoverCropFrame.btnAutoCalc.text")); //$NON-NLS-1$
			btnAutoCalc.addActionListener(e -> autoCalc(e));
			panel1.add(btnAutoCalc);

			//======== panel4 ========
			{
				panel4.setMinimumSize(new Dimension(12, 12));
				panel4.setPreferredSize(new Dimension(12, 12));
				panel4.setLayout(null);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel4.getComponentCount(); i++) {
						Rectangle bounds = panel4.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel4.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel4.setMinimumSize(preferredSize);
					panel4.setPreferredSize(preferredSize);
				}
			}
			panel1.add(panel4);

			//---- btnRotateCW ----
			btnRotateCW.setText(LocaleBundle.getString("CoverCropFrame.btnRotateCW.text")); //$NON-NLS-1$
			btnRotateCW.addActionListener(e -> onRotateCW(e));
			panel1.add(btnRotateCW);

			//======== panel10 ========
			{
				panel10.setMinimumSize(new Dimension(5, 5));
				panel10.setPreferredSize(new Dimension(5, 5));
				panel10.setLayout(null);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel10.getComponentCount(); i++) {
						Rectangle bounds = panel10.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel10.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel10.setMinimumSize(preferredSize);
					panel10.setPreferredSize(preferredSize);
				}
			}
			panel1.add(panel10);

			//---- btnRotateCCW ----
			btnRotateCCW.setText(LocaleBundle.getString("CoverCropFrame.btnRotateCCW.text")); //$NON-NLS-1$
			btnRotateCCW.addActionListener(e -> onRotateCCW(e));
			panel1.add(btnRotateCCW);

			//======== panel5 ========
			{
				panel5.setMinimumSize(new Dimension(12, 12));
				panel5.setPreferredSize(new Dimension(12, 12));
				panel5.setLayout(null);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel5.getComponentCount(); i++) {
						Rectangle bounds = panel5.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel5.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel5.setMinimumSize(preferredSize);
					panel5.setPreferredSize(preferredSize);
				}
			}
			panel1.add(panel5);

			//---- btnFlipX ----
			btnFlipX.setText(LocaleBundle.getString("CoverCropFrame.btnFlipX.text")); //$NON-NLS-1$
			btnFlipX.addActionListener(e -> onFlipX(e));
			panel1.add(btnFlipX);

			//======== panel11 ========
			{
				panel11.setMinimumSize(new Dimension(5, 5));
				panel11.setPreferredSize(new Dimension(5, 5));
				panel11.setLayout(null);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel11.getComponentCount(); i++) {
						Rectangle bounds = panel11.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel11.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel11.setMinimumSize(preferredSize);
					panel11.setPreferredSize(preferredSize);
				}
			}
			panel1.add(panel11);

			//---- btnFlipY ----
			btnFlipY.setText(LocaleBundle.getString("CoverCropFrame.btnFlipY.text")); //$NON-NLS-1$
			btnFlipY.addActionListener(e -> onFlipY(e));
			panel1.add(btnFlipY);
		}
		contentPane.add(panel1, CC.xywh(2, 2, 5, 1, CC.FILL, CC.FILL));

		//======== panel12 ========
		{
			panel12.setBorder(new EtchedBorder());
			panel12.setLayout(new FormLayout(
				"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
				"$lgap, default:grow, $lgap")); //$NON-NLS-1$

			//---- lblImage ----
			lblImage.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					onImageMouseExited(e);
				}
				@Override
				public void mousePressed(MouseEvent e) {
					onImageMousePressed(e);
				}
				@Override
				public void mouseReleased(MouseEvent e) {
					onImageMouseReleased(e);
				}
			});
			lblImage.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					onImageMouseDragged(e);
				}
				@Override
				public void mouseMoved(MouseEvent e) {
					onImageMouseMoved(e);
				}
			});
			lblImage.addPaintComponentListener(e -> onMainImagePaint(e));
			panel12.add(lblImage, CC.xy(2, 2, CC.FILL, CC.FILL));
		}
		contentPane.add(panel12, CC.xywh(2, 4, 1, 3));

		//======== panel6 ========
		{
			panel6.setBorder(new EtchedBorder());
			panel6.setLayout(new FormLayout(
				"$lcgap, default:grow, $lcgap, [30dlu,default], $lcgap", //$NON-NLS-1$
				"9*($lgap, default), $lgap, default:grow, $lgap, default, $lgap")); //$NON-NLS-1$

			//---- chckbxLockRatio ----
			chckbxLockRatio.setText(LocaleBundle.getString("CoverCropFrame.btnLockRatio.text")); //$NON-NLS-1$
			chckbxLockRatio.addItemListener(e -> repaintAll());
			panel6.add(chckbxLockRatio, CC.xywh(2, 2, 3, 1));

			//---- chckbxShowCropOutline ----
			chckbxShowCropOutline.setText(LocaleBundle.getString("CoverCropFrame.btnShowCropOutline.text")); //$NON-NLS-1$
			chckbxShowCropOutline.setSelected(true);
			chckbxShowCropOutline.addItemListener(e -> repaintAll());
			panel6.add(chckbxShowCropOutline, CC.xywh(2, 4, 3, 1));

			//---- chckbxShowImageBorders ----
			chckbxShowImageBorders.setText(LocaleBundle.getString("CoverCropFrame.btnShowImageBorders.text")); //$NON-NLS-1$
			chckbxShowImageBorders.addItemListener(e -> repaintAll());
			panel6.add(chckbxShowImageBorders, CC.xywh(2, 6, 3, 1));

			//---- chckbxShowTransparency ----
			chckbxShowTransparency.setText(LocaleBundle.getString("CoverCropFrame.btnShowTransparency.text")); //$NON-NLS-1$
			chckbxShowTransparency.setSelected(true);
			chckbxShowTransparency.addItemListener(e -> repaintAll());
			panel6.add(chckbxShowTransparency, CC.xywh(2, 8, 3, 1));

			//---- chckbxSeriesPreview ----
			chckbxSeriesPreview.setText(LocaleBundle.getString("CoverCropFrame.btnShowSeriesPreview.text")); //$NON-NLS-1$
			chckbxSeriesPreview.addItemListener(e -> repaintAll());
			panel6.add(chckbxSeriesPreview, CC.xywh(2, 10, 3, 1));

			//---- lblPosition ----
			lblPosition.setText(LocaleBundle.getString("CoverCropFrame.lblPosition.text")); //$NON-NLS-1$
			panel6.add(lblPosition, CC.xy(2, 12));

			//---- lblSize ----
			lblSize.setText(LocaleBundle.getString("CoverCropFrame.lblSize.text")); //$NON-NLS-1$
			panel6.add(lblSize, CC.xy(2, 14));

			//---- lblRatio ----
			lblRatio.setText(LocaleBundle.getString("CoverCropFrame.lblRatio.text")); //$NON-NLS-1$
			panel6.add(lblRatio, CC.xy(2, 16));

			//---- lblRatioState ----
			lblRatioState.setText(LocaleBundle.getString("CoverCropFrame.lblOK.textOK")); //$NON-NLS-1$
			lblRatioState.setHorizontalAlignment(SwingConstants.TRAILING);
			lblRatioState.setFont(lblRatioState.getFont().deriveFont(lblRatioState.getFont().getStyle() | Font.BOLD));
			panel6.add(lblRatioState, CC.xy(4, 16));

			//---- lblMouse ----
			lblMouse.setText(LocaleBundle.getString("CoverCropFrame.lblMouse.text")); //$NON-NLS-1$
			panel6.add(lblMouse, CC.xy(2, 18));

			//======== panel13 ========
			{
				panel13.setLayout(new FlowLayout(FlowLayout.LEFT));

				//---- btnCopyParams ----
				btnCopyParams.setText(LocaleBundle.getString("UIGeneric.copy")); //$NON-NLS-1$
				btnCopyParams.addActionListener(e -> copyParams(e));
				panel13.add(btnCopyParams);

				//---- btnPasteAbs ----
				btnPasteAbs.setText(LocaleBundle.getString("UIGeneric.Paste")); //$NON-NLS-1$
				btnPasteAbs.addActionListener(e -> pasteParams(e));
				panel13.add(btnPasteAbs);

				//---- btnPasteRel ----
				btnPasteRel.setText(LocaleBundle.getString("CoverCropFrame.PasteRel")); //$NON-NLS-1$
				btnPasteRel.addActionListener(e -> pasteParamsPerc(e));
				panel13.add(btnPasteRel);
			}
			panel6.add(panel13, CC.xywh(2, 22, 3, 1));
		}
		contentPane.add(panel6, CC.xy(4, 4, CC.FILL, CC.FILL));

		//======== panel7 ========
		{
			panel7.setBorder(new EtchedBorder());
			panel7.setLayout(new FormLayout(
				"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
				"$lgap, default, $lgap, default:grow, $lgap")); //$NON-NLS-1$

			//---- spnZoom ----
			spnZoom.setModel(new SpinnerNumberModel(2, 1, 10, 1));
			spnZoom.addChangeListener(e -> onZoomChanged());
			panel7.add(spnZoom, CC.xy(2, 2));

			//---- lblZoom ----
			lblZoom.addPaintComponentListener(e -> onZoomImagePaint(e));
			panel7.add(lblZoom, CC.xy(2, 4, CC.FILL, CC.FILL));
		}
		contentPane.add(panel7, CC.xy(6, 4, CC.FILL, CC.FILL));

		//======== panel8 ========
		{
			panel8.setBorder(new EtchedBorder());
			panel8.setLayout(new FormLayout(
				"2dlu, 2*(default, $lcgap), default:grow, $lcgap, default, 1dlu", //$NON-NLS-1$
				"$nlgap, default:grow, $lgap, default, $nlgap")); //$NON-NLS-1$

			//---- lblCoverPreviewBig ----
			lblCoverPreviewBig.setMinimumSize(new Dimension(182, 254));
			lblCoverPreviewBig.setMaximumSize(new Dimension(182, 254));
			lblCoverPreviewBig.setPreferredSize(new Dimension(182, 254));
			lblCoverPreviewBig.addPaintComponentListener(e -> onBigCoverPaint(e));
			panel8.add(lblCoverPreviewBig, CC.xywh(2, 2, 1, 3, CC.DEFAULT, CC.BOTTOM));

			//---- lblCoverPreviewSmall ----
			lblCoverPreviewSmall.setMaximumSize(new Dimension(91, 127));
			lblCoverPreviewSmall.setMinimumSize(new Dimension(91, 127));
			lblCoverPreviewSmall.setPreferredSize(new Dimension(91, 127));
			lblCoverPreviewSmall.addPaintComponentListener(e -> onSmallCoverPaint(e));
			panel8.add(lblCoverPreviewSmall, CC.xywh(4, 2, 1, 3, CC.DEFAULT, CC.BOTTOM));

			//======== panel9 ========
			{
				panel9.setLayout(new FormLayout(
					"[18dlu,pref], 2dlu, pref, 2dlu, [18dlu,pref], 4dlu, [18dlu,pref]", //$NON-NLS-1$
					"2*([18dlu,pref], 2dlu), [18dlu,pref], 4dlu, 18dlu")); //$NON-NLS-1$

				//---- btnResizeTop ----
				btnResizeTop.setText("\u25b2"); //$NON-NLS-1$
				btnResizeTop.addActionListener(e -> onCropTop(e));
				panel9.add(btnResizeTop, CC.xy(3, 1, CC.FILL, CC.FILL));

				//---- btnResizeLeft ----
				btnResizeLeft.setText("\u25c0"); //$NON-NLS-1$
				btnResizeLeft.addActionListener(e -> onCropLeft(e));
				panel9.add(btnResizeLeft, CC.xy(1, 3, CC.FILL, CC.FILL));

				//---- btnResizeCenter ----
				btnResizeCenter.setText("\u2716"); //$NON-NLS-1$
				btnResizeCenter.addActionListener(e -> onCropCenter(e));
				panel9.add(btnResizeCenter, CC.xy(3, 3, CC.FILL, CC.FILL));

				//---- btnResizeRight ----
				btnResizeRight.setText("\u25b6"); //$NON-NLS-1$
				btnResizeRight.addActionListener(e -> onCropRight(e));
				panel9.add(btnResizeRight, CC.xy(5, 3, CC.FILL, CC.FILL));

				//---- btnResizeVert ----
				btnResizeVert.setText("\u2b0d"); //$NON-NLS-1$
				btnResizeVert.setFont(btnResizeVert.getFont().deriveFont(btnResizeVert.getFont().getSize() + 8f));
				btnResizeVert.addActionListener(e -> onCropVertical(e));
				panel9.add(btnResizeVert, CC.xywh(7, 1, 1, 5, CC.FILL, CC.FILL));

				//---- btnResizeBottom ----
				btnResizeBottom.setText("\u25bc"); //$NON-NLS-1$
				btnResizeBottom.addActionListener(e -> onCropBottom(e));
				panel9.add(btnResizeBottom, CC.xy(3, 5, CC.FILL, CC.FILL));

				//---- btnResizeHorz ----
				btnResizeHorz.setText("\u2b0c"); //$NON-NLS-1$
				btnResizeHorz.setFont(btnResizeHorz.getFont().deriveFont(btnResizeHorz.getFont().getSize() + 8f));
				btnResizeHorz.addActionListener(e -> onCropHorizontal(e));
				panel9.add(btnResizeHorz, CC.xywh(1, 7, 5, 1, CC.FILL, CC.FILL));
			}
			panel8.add(panel9, CC.xy(8, 4));
		}
		contentPane.add(panel8, CC.xywh(4, 6, 3, 1, CC.FILL, CC.FILL));

		//======== panel2 ========
		{
			panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

			//---- btnOK ----
			btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			btnOK.addActionListener(e -> onOkay(e));
			panel2.add(btnOK);

			//---- btnAbort ----
			btnAbort.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
			btnAbort.addActionListener(e -> onCancel(e));
			panel2.add(btnAbort);
		}
		contentPane.add(panel2, CC.xywh(2, 8, 5, 1));
		setSize(1125, 695);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel1;
	private JButton btnReset;
	private JPanel panel3;
	private JButton btnAutoCalc;
	private JPanel panel4;
	private JButton btnRotateCW;
	private JPanel panel10;
	private JButton btnRotateCCW;
	private JPanel panel5;
	private JButton btnFlipX;
	private JPanel panel11;
	private JButton btnFlipY;
	private JPanel panel12;
	private JCanvasLabel lblImage;
	private JPanel panel6;
	private JCheckBox chckbxLockRatio;
	private JCheckBox chckbxShowCropOutline;
	private JCheckBox chckbxShowImageBorders;
	private JCheckBox chckbxShowTransparency;
	private JCheckBox chckbxSeriesPreview;
	private JLabel lblPosition;
	private JLabel lblSize;
	private JLabel lblRatio;
	private JLabel lblRatioState;
	private JLabel lblMouse;
	private JPanel panel13;
	private JButton btnCopyParams;
	private JButton btnPasteAbs;
	private JButton btnPasteRel;
	private JPanel panel7;
	private JSpinner spnZoom;
	private JCanvasLabel lblZoom;
	private JPanel panel8;
	private JCanvasLabel lblCoverPreviewBig;
	private JCanvasLabel lblCoverPreviewSmall;
	private JPanel panel9;
	private JButton btnResizeTop;
	private JButton btnResizeLeft;
	private JButton btnResizeCenter;
	private JButton btnResizeRight;
	private JButton btnResizeVert;
	private JButton btnResizeBottom;
	private JButton btnResizeHorz;
	private JPanel panel2;
	private JButton btnOK;
	private JButton btnAbort;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
