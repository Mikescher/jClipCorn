package de.jClipCorn.gui.frames.filenameRulesFrame;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JButton;

import de.jClipCorn.gui.localization.LocaleBundle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextArea;

public class FilenameRuleFrame extends JFrame {
	private static final long serialVersionUID = 692779597355844596L;
	private JScrollPane scrollPane;
	private JTextArea memoMain;
	private JPanel pnlBottom;
	private JButton btnOK;
	
	public FilenameRuleFrame(Component owner) {
		setTitle(LocaleBundle.getString("FilenameRulesFrame.btnTitle.text")); //$NON-NLS-1$
		setSize(new Dimension(750, 500));
		initGUI();
		setLocationRelativeTo(owner);
		memoMain.setText(LocaleBundle.getString("FilenameRulesFrame.rules")); //$NON-NLS-1$
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		memoMain = new JTextArea();
		memoMain.setFont(new Font("Courier New", Font.PLAIN, 12));
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
	}
}
