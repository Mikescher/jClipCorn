package de.jClipCorn.gui.frames.showUpdateFrame;

import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.UpdateConnector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShowUpdateFrame extends JCCDialog {
	private static final long serialVersionUID = -2550625269769000346L;
	private JLabel lblText;
	private JLabel lblVersion;
	private JPanel panel;
	private JButton btnDownload;
	
	public ShowUpdateFrame(MainFrame owner, UpdateConnector uc, boolean found) {
		super(owner.getMovielist());
		
		initGUI(uc, found);
		
		setLocationRelativeTo(owner);
	}
	
	private void initGUI(final UpdateConnector uc, boolean found) {
		setTitle(LocaleBundle.getString("ShowUpdateFrame.this.title")); //$NON-NLS-1$
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(new Dimension(450, 140));
		
		lblText = null;
		if (found) {
			lblText = new JLabel(LocaleBundle.getString("ShowUpdateFrame.label.text")); //$NON-NLS-1$
			lblText.setForeground(Color.RED);
		} else {
			lblText = new JLabel(LocaleBundle.getString("ShowUpdateFrame.label.alttext")); //$NON-NLS-1$
		}
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
		btnDownload.setEnabled(found);
		panel.add(btnDownload);
	}
}
