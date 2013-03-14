package de.jClipCorn.gui.frames.showUpdateFrame;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.UpdateConnector;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class ShowUpdateFrame extends JDialog {
	private static final long serialVersionUID = -2550625269769000346L;
	private JLabel lblText;
	private JLabel lblVersion;
	private JPanel panel;
	private JButton btnDownload;
	
	public ShowUpdateFrame(MainFrame owner, UpdateConnector uc) {
		super();
		
		initGUI(uc);
		
		setLocationRelativeTo(owner);
	}
	
	private void initGUI(final UpdateConnector uc) {
		setTitle(LocaleBundle.getString("ShowUpdateFrame.this.title")); //$NON-NLS-1$
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(new Dimension(450, 140));
		
		lblText = new JLabel(LocaleBundle.getString("ShowUpdateFrame.label.text")); //$NON-NLS-1$
		lblText.setForeground(Color.RED);
		lblText.setFont(new Font("Tahoma", Font.BOLD, 15)); //$NON-NLS-1$
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblText, BorderLayout.NORTH);
		
		lblVersion = new JLabel(uc.getUpdateName() + " v" + uc.getUpdateVersion()); //$NON-NLS-1$
		lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
		lblVersion.setFont(new Font("Tahoma", Font.PLAIN, 15)); //$NON-NLS-1$
		getContentPane().add(lblVersion, BorderLayout.CENTER);
		
		panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		
		btnDownload = new JButton(LocaleBundle.getString("ShowUpdateFrame.btnDownload.caption")); //$NON-NLS-1$
		btnDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uc.openURL();
			}
		});
		panel.add(btnDownload);
	}
}
