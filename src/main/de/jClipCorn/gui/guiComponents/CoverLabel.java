package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.ImageUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CoverLabel extends JLabel implements MouseListener {
	private static final long serialVersionUID = -1565590903873295610L;

	private final boolean isHalfSize;

	private BufferedImage original = null;
	
	public CoverLabel(boolean halfsize) {
		super();
		isHalfSize = halfsize;
		addMouseListener(this);
	}

	public Icon getStandardIcon() {
		if (isHalfSize) {
			return Resources.ICN_COVER_STANDARD_SMALL.get();
		} else {
			return Resources.ICN_COVER_STANDARD.get();
		}
	}

	public boolean isIconSet() {
		return super.getIcon() != null;
	}

	@Override
	public Icon getIcon() {
		if (isIconSet()) {
			return super.getIcon();
		} else {
			return getStandardIcon();
		}
	}

	public BufferedImage getOriginalCover() {
		if (original != null) return original;
		return ImageUtilities.iconToImage((ImageIcon) getIcon());
	}

	@Override
	@Deprecated
	public void setText(String text) {
		super.setText(Str.Empty);
	}

	@Override
	@Deprecated
	public void setIcon(Icon icon) {
		super.setIcon(null);
	}

	protected void setInternalIntermediateIconDirect(Icon icon) {
		super.setIcon(icon);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
	    super.setBounds(
	    		x,
	    		y,
	    		isHalfSize ? ImageUtilities.HALF_COVER_WIDTH  : ImageUtilities.BASE_COVER_WIDTH,
	    		isHalfSize ? ImageUtilities.HALF_COVER_HEIGHT : ImageUtilities.BASE_COVER_HEIGHT);
	}

	public void setPosition(int x, int y) {
	    super.setBounds(
	    		x,
	    		y,
	    		isHalfSize ? ImageUtilities.HALF_COVER_WIDTH  : ImageUtilities.BASE_COVER_WIDTH,
	    		isHalfSize ? ImageUtilities.HALF_COVER_HEIGHT : ImageUtilities.BASE_COVER_HEIGHT);
	}

	public void setCoverDirect(BufferedImage cover, BufferedImage orig) {
		original = orig;

		if (cover == null) { super.setIcon(null); return; }

		if (isHalfSize)
			super.setIcon(new ImageIcon(ImageUtilities.resizeCoverImageForHalfSizeUI(cover)));
		else
			super.setIcon(new ImageIcon(ImageUtilities.resizeCoverImageForFullSizeUI(cover)));
	}

	public void setAndResizeCover(BufferedImage cover) {
		original = cover;

		if (cover == null) { super.setIcon(null); return; }

		if (isHalfSize)
			super.setIcon(new ImageIcon(ImageUtilities.resizeCoverImageForHalfSizeUI(cover)));
		else
			super.setIcon(new ImageIcon(ImageUtilities.resizeCoverImageForFullSizeUI(cover)));
	}

	public void clearCover() {
		original = null;
		super.setIcon(null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			new CoverPreviewFrame(CoverLabel.this, getOriginalCover()).setVisible(true);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {
		onMouseEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		onMouseEvent(e);
	}

	private void onMouseEvent(MouseEvent e) {
		if (e.isPopupTrigger()) {
			if (!isIconSet()) {
				return;
			}

			JPopupMenu menu = new JPopupMenu();
			JMenuItem item2 = new JMenuItem(LocaleBundle.getString("MainFrame.showCoverPopup"), Resources.ICN_MENUBAR_SHOWCOVER.get16x16()); //$NON-NLS-1$
			JMenuItem item1 = new JMenuItem(LocaleBundle.getString("MainFrame.saveCoverPopup"), Resources.ICN_MENUBAR_SAVE.get16x16()); //$NON-NLS-1$
			menu.add(item2);
			menu.add(item1);

			item1.addActionListener(e2 -> saveIconAction());

			item2.addActionListener(e2 -> new CoverPreviewFrame(CoverLabel.this, getOriginalCover()).setVisible(true));

			menu.show(this, e.getX(), e.getY());
		}
	}

	private void saveIconAction() {
		JFileChooser fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);

		FileFilter filterPng;
		FileFilter filterJpg;
		FileFilter filterBmp;

		fc.setFileFilter(filterPng = FileChooserHelper.createFileFilter("PNG-Image (*.png)", "png")); //$NON-NLS-1$ //$NON-NLS-2$

		fc.addChoosableFileFilter(filterJpg = FileChooserHelper.createFileFilter("JPEG-Image (*.jpg)", "jpg", "jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		fc.addChoosableFileFilter(filterBmp = FileChooserHelper.createFileFilter("Bitmap-Image (*.bmp)", "bmp")); //$NON-NLS-1$ //$NON-NLS-2$

		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			FileFilter choosen = fc.getFileFilter();
			String path = fc.getSelectedFile().getAbsolutePath();
			String format = ""; //$NON-NLS-1$

			if (choosen.equals(filterPng)) {
				format = "png"; //$NON-NLS-1$

			} else if (choosen.equals(filterJpg)) {
				format = "jpg"; //$NON-NLS-1$

			} else if (choosen.equals(filterBmp)) {
				format = "bmp"; //$NON-NLS-1$
			}

			path = PathFormatter.forceExtension(path, format);

			try {
				ImageIO.write(getOriginalCover(), format, new File(path));
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}
}
