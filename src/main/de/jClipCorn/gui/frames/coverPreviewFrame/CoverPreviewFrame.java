package de.jClipCorn.gui.frames.coverPreviewFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JFrame;

import de.jClipCorn.database.databaseElement.ICCCoveredElement;
import de.jClipCorn.gui.guiComponents.ScalablePane;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.ImageUtilities;

public class CoverPreviewFrame extends JFrame {
	private static final long serialVersionUID = -807033167837187549L;
	
	private ScalablePane lblImg;
	
	public CoverPreviewFrame(Component owner, ICCCoveredElement elem) {
		super();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI(elem.getCover(), elem.getTitle());
		setLocationRelativeTo(findWindow(owner));
		
		addKeyListener(new KeyAdapter() {
		    @Override
			public void keyPressed(KeyEvent e) {
		    	if (e.getKeyCode() == KeyEvent.VK_ESCAPE) CoverPreviewFrame.this.dispose();
		    }
		});
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public CoverPreviewFrame(Component owner, BufferedImage img) {
		super();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI(img, LocaleBundle.getString("CoverPreviewFrame.title")); //$NON-NLS-1$
		setLocationRelativeTo(findWindow(owner));
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
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
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
}
