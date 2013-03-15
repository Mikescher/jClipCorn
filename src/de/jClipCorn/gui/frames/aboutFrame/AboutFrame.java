package de.jClipCorn.gui.frames.aboutFrame;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;

import de.jClipCorn.Main;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public class AboutFrame extends JFrame {
	private static final long serialVersionUID = -807033167837187549L;
	
	private JLabel lblImg;
	
	public AboutFrame(Component owner) {
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI();
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("UberDialog.this.title") + " v" + Main.VERSION); //$NON-NLS-1$ //$NON-NLS-2$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		
		lblImg = new JLabel(CachedResourceLoader.getImageIcon(Resources.IMG_FRAMES_ABOUT));
		getContentPane().add(lblImg, BorderLayout.CENTER);
		
		pack();
	}
}
