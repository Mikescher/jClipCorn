package de.jClipCorn.gui.frames.aboutFrame;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.filesystem.SimpleFileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class AboutFrame extends JCCFrame
{
	public AboutFrame(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setTitle(LocaleBundle.getString("UberDialog.this.title") + " v" + Main.VERSION); //$NON-NLS-1$ //$NON-NLS-2$

		load();

		setMinimumSize(getSize());
	}

	private void onClicked() {
		lblImg.setVisible(false);
		memoLibs.setVisible(true);
		getContentPane().removeAll();
		getContentPane().add(memoLibs, BorderLayout.CENTER);
		setResizable(true);
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblImg = new JLabel();
		scrollPane1 = new JScrollPane();
		memoLibs = new JList<>();

		//======== this ========
		setTitle("<dynamic>"); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//---- lblImg ----
		lblImg.setIcon(new ImageIcon(getClass().getResource("/UberDialog.png"))); //$NON-NLS-1$
		lblImg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onClicked();
			}
		});
		contentPane.add(lblImg, BorderLayout.PAGE_START);

		//======== scrollPane1 ========
		{
			scrollPane1.setVisible(false);
			scrollPane1.setViewportView(memoLibs);
		}
		contentPane.add(scrollPane1, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblImg;
	private JScrollPane scrollPane1;
	private JList<String> memoLibs;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
