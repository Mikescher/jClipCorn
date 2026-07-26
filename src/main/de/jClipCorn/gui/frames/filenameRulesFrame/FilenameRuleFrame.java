package de.jClipCorn.gui.frames.filenameRulesFrame;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.filesystem.SimpleFileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class FilenameRuleFrame extends JCCFrame {
	private static final long serialVersionUID = 692779597355844596L;

	public FilenameRuleFrame(Component owner, CCMovieList ml) {
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit() {
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

	private void onOK() {
		dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane = new JScrollPane();
		memoMain = new JTextArea();
		btnOK = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("FilenameRulesFrame.btnTitle.text"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap",
			"$ugap, default:grow, $lgap, default, $ugap"));

		//======== scrollPane ========
		{

			//---- memoMain ----
			memoMain.setEditable(false);
			memoMain.setFont(new Font("Courier New", Font.PLAIN, 12));
			scrollPane.setViewportView(memoMain);
		}
		contentPane.add(scrollPane, CC.xy(2, 2, CC.FILL, CC.FILL));

		//---- btnOK ----
		btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text"));
		btnOK.addActionListener(e -> onOK());
		contentPane.add(btnOK, CC.xy(2, 4, CC.CENTER, CC.DEFAULT));
		setSize(750, 500);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane;
	private JTextArea memoMain;
	private JButton btnOK;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
