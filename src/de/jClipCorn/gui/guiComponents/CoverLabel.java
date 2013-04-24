package de.jClipCorn.gui.guiComponents;import java.awt.event.ActionEvent;import java.awt.event.ActionListener;import java.awt.event.MouseEvent;import java.awt.event.MouseListener;import java.io.File;import java.io.IOException;import javax.imageio.ImageIO;import javax.swing.Icon;import javax.swing.ImageIcon;import javax.swing.JFileChooser;import javax.swing.JLabel;import javax.swing.JMenuItem;import javax.swing.JPopupMenu;import javax.swing.filechooser.FileFilter;import de.jClipCorn.gui.CachedResourceLoader;import de.jClipCorn.gui.Resources;import de.jClipCorn.gui.localization.LocaleBundle;import de.jClipCorn.gui.log.CCLog;import de.jClipCorn.util.FileChooserHelper;import de.jClipCorn.util.ImageUtilities;import de.jClipCorn.util.PathFormatter;public class CoverLabel extends JLabel implements MouseListener {	private static final long serialVersionUID = -1565590903873295610L;	private final boolean isHalfSize;	public CoverLabel(boolean halfsize) {		super();		isHalfSize = halfsize;		addMouseListener(this);	}	public Icon getStandardIcon() {		if (isHalfSize) {			return CachedResourceLoader.getResizedImageIcon(					Resources.IMG_COVER_STANDARD,					ImageUtilities.COVER_WIDTH / 2,					ImageUtilities.COVER_HEIGHT / 2);		} else {			return CachedResourceLoader					.getImageIcon(Resources.IMG_COVER_STANDARD);		}	}		public boolean isIconSet() {		return super.getIcon() != null;	}	@Override	public Icon getIcon() {		if (isIconSet()) {			return super.getIcon();		} else {			return getStandardIcon();		}	}	@Override	public void mouseClicked(MouseEvent e) {		// Nothing	}	@Override	public void mouseEntered(MouseEvent e) {		// Nothing	}	@Override	public void mouseExited(MouseEvent e) {		// Nothing	}	@Override	public void mousePressed(MouseEvent e) {		onMouseEvent(e);	}	@Override	public void mouseReleased(MouseEvent e) {		onMouseEvent(e);	}	private void onMouseEvent(MouseEvent e) {		if (e.isPopupTrigger()) {			if (!isIconSet()) {				return;			}			JPopupMenu menu = new JPopupMenu();			JMenuItem item = new JMenuItem(					LocaleBundle.getString("MainFrame.saveCoverPopup")); //$NON-NLS-1$			menu.add(item);						item.addActionListener(new ActionListener() {				@Override				public void actionPerformed(ActionEvent e) {					saveIconAction();				}			});			menu.show(this, e.getX(), e.getY());		}	}	private void saveIconAction() {		JFileChooser fc = new JFileChooser();		fc.setAcceptAllFileFilterUsed(false);		FileFilter filterPng;		FileFilter filterJpg;		FileFilter filterBmp;		fc.setFileFilter(filterPng = FileChooserHelper.createFileFilter(				"PNG-Image (*.png)", "png")); //$NON-NLS-1$ //$NON-NLS-2$		fc.addChoosableFileFilter(filterJpg = FileChooserHelper				.createFileFilter("JPEG-Image (*.jpg)", "jpg", "jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$		fc.addChoosableFileFilter(filterBmp = FileChooserHelper				.createFileFilter("Bitmap-Image (*.bmp)", "bmp")); //$NON-NLS-1$ //$NON-NLS-2$		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {			FileFilter choosen = fc.getFileFilter();			String path = fc.getSelectedFile().getAbsolutePath();			String format = ""; //$NON-NLS-1$			if (choosen.equals(filterPng)) {				format = "png"; //$NON-NLS-1$			} else if (choosen.equals(filterJpg)) {				format = "jpg"; //$NON-NLS-1$			} else if (choosen.equals(filterBmp)) {				format = "bmp"; //$NON-NLS-1$			}			path = PathFormatter.forceExtension(path, format);			try {				ImageIO.write(						ImageUtilities.iconToImage((ImageIcon) getIcon()),						format, new File(path));			} catch (IOException e) {				CCLog.addError(e);			}		}	}}