package de.jClipCorn.gui.frames.filenameRulesFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public class FilenameRuleFrame extends JFrame {
	private static final long serialVersionUID = 692779597355844596L;
	private JScrollPane scrollPane;
	private JTextArea memoMain;
	private JPanel pnlBottom;
	private JButton btnOK;
	
	public FilenameRuleFrame(Component owner) {
		super();
		
		initGUI();
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("FilenameRulesFrame.btnTitle.text")); //$NON-NLS-1$
		setSize(new Dimension(750, 500));
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		memoMain = new JTextArea();
		memoMain.setFont(new Font("Courier New", Font.PLAIN, 12)); //$NON-NLS-1$
		memoMain.setEditable(false);
		scrollPane.setViewportView(memoMain);
		
		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		btnOK = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		pnlBottom.add(btnOK);

		memoMain.setText(LocaleBundle.getString("FilenameRulesFrame.rules")); //$NON-NLS-1$
	}
}
