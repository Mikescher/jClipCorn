package de.jClipCorn.gui.frames.showUpdateFrame;

import java.awt.event.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.UpdateConnector;

import javax.swing.*;
import java.awt.*;

public class ShowUpdateFrame extends JCCFrame
{
	private final UpdateConnector uconn;
	
	public ShowUpdateFrame(Component owner, CCMovieList ml, UpdateConnector uc, boolean found)
	{
		super(ml);
		uconn = uc;

		initComponents();
		postInit(found);

		setLocationRelativeTo(owner);
	}

	private void postInit(boolean found)
	{
		if (found) {
			lblText.setText(LocaleBundle.getString("ShowUpdateFrame.label.text")); //$NON-NLS-1$
			lblText.setForeground(Color.RED);
		} else {
			lblText.setText(LocaleBundle.getString("ShowUpdateFrame.label.alttext")); //$NON-NLS-1$
		}

		btnDownload.setEnabled(found);
	}

	private void download(ActionEvent e) {
		uconn.openURL();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblText = new JLabel();
		btnDownload = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("ShowUpdateFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

		//---- lblText ----
		lblText.setText("<dynamic>"); //$NON-NLS-1$
		lblText.setHorizontalAlignment(SwingConstants.CENTER);
		lblText.setFont(lblText.getFont().deriveFont(lblText.getFont().getStyle() | Font.BOLD, 32f));
		contentPane.add(lblText, CC.xy(2, 2, CC.FILL, CC.FILL));

		//---- btnDownload ----
		btnDownload.setText(LocaleBundle.getString("ShowUpdateFrame.btnDownload.caption")); //$NON-NLS-1$
		btnDownload.addActionListener(e -> download(e));
		contentPane.add(btnDownload, CC.xy(2, 4, CC.CENTER, CC.DEFAULT));
		setSize(650, 260);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblText;
	private JButton btnDownload;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
