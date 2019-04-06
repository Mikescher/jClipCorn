package de.jClipCorn.gui.frames.aboutFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

import de.jClipCorn.Main;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.SimpleFileUtils;

public class AboutFrame extends JFrame implements ComponentListener {
	private static final long serialVersionUID = -807033167837187549L;
	
	private JLabel lblImg;
	private JList<String> memoLibs;
	
	public AboutFrame(Component owner) {
		super();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI();
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("UberDialog.this.title") + " v" + Main.VERSION); //$NON-NLS-1$ //$NON-NLS-2$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		addComponentListener(this);

		lblImg = new JLabel(Resources.ICN_FRAMES_ABOUT.get());
		lblImg.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {/**/}
			
			@Override
			public void mousePressed(MouseEvent e) {/**/}
			
			@Override
			public void mouseExited(MouseEvent e) {/**/}
			
			@Override
			public void mouseEntered(MouseEvent e) {/**/}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				lblImg.setVisible(false);
				memoLibs.setVisible(true);
				getContentPane().removeAll();
				getContentPane().add(memoLibs, BorderLayout.CENTER);
			}
		});

		memoLibs = new JList<>();
		load();
		
		getContentPane().add(lblImg, BorderLayout.CENTER);
		
		pack();
	}
	
	private void load() {
		DefaultListModel<String> dlm = new DefaultListModel<>();
		
		String libs;
		try {
			libs = SimpleFileUtils.readTextResource("/libraries.txt", this.getClass()); //$NON-NLS-1$
		} catch (IOException e) {
			CCLog.addError(e);
			return;
		}
		
		for (String line : SimpleFileUtils.splitLines(libs)) {
			if (line.isEmpty()) continue;
			
			dlm.addElement(line);
		}
		memoLibs.setModel(dlm);
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		lblImg.setVisible(true);
		memoLibs.setVisible(false);
		getContentPane().removeAll();
		getContentPane().add(lblImg, BorderLayout.CENTER);
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// NOP
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// NOP
	}

	@Override
	public void componentShown(ComponentEvent e) {
		lblImg.setVisible(true);
		memoLibs.setVisible(false);
		getContentPane().removeAll();
		getContentPane().add(lblImg, BorderLayout.CENTER);
	}
}
