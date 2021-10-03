package de.jClipCorn.gui.frames.coverPreviewFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCCoveredElement;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.ScalablePane;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.colorquantizer.util.ColorQuantizerConverter;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ImageUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CoverPreviewFrame extends JCCDialog {
	private static final long serialVersionUID = -807033167837187549L;

	private final BufferedImage _image;
	private final FSPath _path;

	private ScalablePane lblImg;
	
	public CoverPreviewFrame(Component owner, ICCCoveredElement elem) {
		super(elem.getMovieList());

		_image = elem.getCover();
		_path = elem.getMovieList().getCoverCache().getFilepath(elem.getCoverInfo());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI(elem.getCover(), elem.title().get());
		setLocationRelativeTo(findWindow(owner));
		
		addKeyListener(new KeyAdapter() {
		    @Override
			public void keyPressed(KeyEvent e) {
		    	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) CoverPreviewFrame.this.dispose();
		    }
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				onMouseEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				onMouseEvent(e);
			}
		});

		fixModality(owner);
	}

	public CoverPreviewFrame(Component owner, CCMovieList ml, BufferedImage img) {
		super(ml);

		_image = img;
		_path = null;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI(img, LocaleBundle.getString("CoverPreviewFrame.title")); //$NON-NLS-1$
		setLocationRelativeTo(findWindow(owner));

		addKeyListener(new KeyAdapter() {
		    @Override
			public void keyPressed(KeyEvent e) {
		    	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) CoverPreviewFrame.this.dispose();
		    }
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				onMouseEvent(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				onMouseEvent(e);
			}
		});

		fixModality(owner);
	}

	private void fixModality(Component owner) {
		if (owner == null) {
			return;
		} else if (owner instanceof JFrame) {
			return;
		} else if (owner instanceof JDialog) {
			if (((JDialog)owner).isModal()) setModal(true);
			return;
		}

		fixModality(owner.getParent());
	}

	private static Component findWindow(Component c) {
		
		Component rec = c;
		
		for(;;) {
			if (rec == null) return c;

			if (rec instanceof JFrame) return rec;
			if (rec instanceof JDialog) return rec;
			
			
			rec = rec.getParent();
		}
		
	}
	
	private void initGUI(BufferedImage img, String title) {
		setTitle(title + " (" + img.getWidth() + " x " + img.getHeight() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		//setSize(new Dimension(ImageUtilities.BASE_COVER_WIDTH * 3, ImageUtilities.BASE_COVER_HEIGHT	 * 3));

		lblImg = new ScalablePane(img, true);
		
		getContentPane().add(lblImg, BorderLayout.CENTER);

		getContentPane().setPreferredSize(getSize(img));
		pack();
	}

	private Dimension getSize(BufferedImage img) {
		double ratioImage = img.getWidth() * 1d / img.getHeight();

		int h = ImageUtilities.BASE_COVER_HEIGHT * 3;
		if (h > img.getHeight()) h = img.getHeight();

		int w = (int)Math.round(h * ratioImage);

		return new Dimension(w, h);
	}

	private void onMouseEvent(MouseEvent e) {
		if (e.isPopupTrigger())
		{
			JPopupMenu menu = new JPopupMenu();
			{
				if (_path != null)
				{
					JMenuItem item1 = new JMenuItem(LocaleBundle.getString("CoverPreviewFrame.openSourcePopup"), Resources.ICN_MENUBAR_FOLDER.get16x16()); //$NON-NLS-1$
					item1.addActionListener(e2 -> openSourceAction());
					menu.add(item1);
				}

				JMenuItem item2 = new JMenuItem(LocaleBundle.getString("MainFrame.saveCoverPopup"), Resources.ICN_MENUBAR_SAVE.get16x16()); //$NON-NLS-1$
				item2.addActionListener(e2 -> saveAction());
				menu.add(item2);

				JMenu item3 = new JMenu(LocaleBundle.getString("MainFrame.quantizePopup")); //$NON-NLS-1$
				{
					for (ColorQuantizerMethod m : ColorQuantizerMethod.values())
					{
						final ColorQuantizerMethod cqm = m;
						JMenuItem item31 = new JMenuItem(m.asString());
						item31.addActionListener(e2 -> showQuantization(cqm));
						item3.add(item31);
					}
				}
				menu.add(item3);

				JMenu item5 = new JMenu(LocaleBundle.getString("MainFrame.resizedQuantizePopup")); //$NON-NLS-1$
				{
					for (ColorQuantizerMethod m : ColorQuantizerMethod.values())
					{
						final ColorQuantizerMethod cqm = m;
						JMenuItem item51 = new JMenuItem(m.asString());
						item51.addActionListener(e2 -> showResizedQuantization(cqm));
						item5.add(item51);
					}
				}
				menu.add(item5);

				JMenu item4 = new JMenu(LocaleBundle.getString("MainFrame.previewPopup")); //$NON-NLS-1$
				{
					for (ColorQuantizerMethod m : ColorQuantizerMethod.values())
					{
						final ColorQuantizerMethod cqm = m;
						JMenuItem item41 = new JMenuItem(m.asString());
						item41.addActionListener(e2 -> showPreview(cqm));
						item4.add(item41);
					}
				}
				menu.add(item4);

			}
			menu.show(this, e.getX(), e.getY());
		}
	}

	private void showPreview(ColorQuantizerMethod m) {
		try {
			ColorQuantizer quant = m.create();
			quant.analyze(_image, 16);
			lblImg.setImage(quant.quantize(ColorQuantizerConverter.shrink(_image, 24)));
		} catch (ColorQuantizerException e) {
			CCLog.addError(e);
		}
	}

	private void showResizedQuantization(ColorQuantizerMethod m) {
		try {
			ColorQuantizer quant = m.create();
			quant.analyze(_image, 16);
			lblImg.setImage(quant.quantize(ImageUtilities.resizeCoverImageForFullSizeUI(_image)));
		} catch (ColorQuantizerException e) {
			CCLog.addError(e);
		}
	}

	private void showQuantization(ColorQuantizerMethod m) {
		try {
			ColorQuantizer quant = m.create();
			quant.analyze(_image, 16);
			lblImg.setImage(quant.quantize(_image));
		} catch (ColorQuantizerException e) {
			CCLog.addError(e);
		}
	}

	private void openSourceAction() {
		FilesystemUtils.showInExplorer(_path);
	}

	private void saveAction() {
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
			var path = FSPath.create(fc.getSelectedFile());
			String format = ""; //$NON-NLS-1$

			if (choosen.equals(filterPng)) {
				format = "png"; //$NON-NLS-1$

			} else if (choosen.equals(filterJpg)) {
				format = "jpg"; //$NON-NLS-1$

			} else if (choosen.equals(filterBmp)) {
				format = "bmp"; //$NON-NLS-1$
			}

			path = path.forceExtension(format);

			try {
				ImageIO.write(_image, format, path.toFile());
			} catch (IOException e) {
				CCLog.addError(e);
			}
		}
	}
}
