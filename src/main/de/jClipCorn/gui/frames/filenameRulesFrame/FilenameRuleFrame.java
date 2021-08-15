package de.jClipCorn.gui.frames.filenameRulesFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.filesystem.SimpleFileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class FilenameRuleFrame extends JCCFrame {
	private static final long serialVersionUID = 692779597355844596L;

	private JScrollPane scrollPane;
	private JTextArea memoMain;
	private JPanel pnlBottom;
	private JButton btnOK;
	
	public FilenameRuleFrame(Component owner, CCMovieList ml) {
		super(ml);
		
		initGUI();
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("FilenameRulesFrame.btnTitle.text")); //$NON-NLS-1$
		setSize(new Dimension(750, 500));
		setIconImage(Resources.IMG_FRAME_ICON.get());
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		memoMain = new JTextArea();
		memoMain.setFont(new Font("Courier New", Font.PLAIN, 12)); //$NON-NLS-1$
		memoMain.setEditable(false);
		scrollPane.setViewportView(memoMain);
		
		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		pnlBottom.add(btnOK);

		memoMain.setText(getDescription());
	}
	
	private String getDescription() {
		try {
			String txt = LocaleBundle.getString("FilenameRulesFrame.rules"); //$NON-NLS-1$
			txt = txt.replace("{grammar}", SimpleFileUtils.readTextResource("/grammar.txt", this.getClass())); //$NON-NLS-1$ //$NON-NLS-2$
			return txt;
		} catch (IOException e) {
			CCLog.addError(e);
			return "??"; //$NON-NLS-1$
		}
	}
}
