package de.jClipCorn.gui.frames.compareDatabaseFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;

import javax.swing.*;
import java.awt.*;

public class CompareDatabaseFrame extends JFrame
{
	public CompareDatabaseFrame(Component owner, CCMovieList ml)
	{
		super();

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle("TODO"); //$NON-NLS-1$
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		edDatabasePath = new ReadableTextField();
		btnOpenDatabase = new JButton();
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		edRules = new JTextArea();
		btnCompare = new JButton();
		tabbedPane1 = new JTabbedPane();
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		panel5 = new JPanel();
		btnCreatePatch = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("CompareDatabaseFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $lcgap, default, $ugap", //$NON-NLS-1$
			"$ugap, 2*(default, $lgap), default:grow, $lgap, default, $lgap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$
		contentPane.add(edDatabasePath, CC.xy(2, 2, CC.FILL, CC.FILL));

		//---- btnOpenDatabase ----
		btnOpenDatabase.setText("..."); //$NON-NLS-1$
		contentPane.add(btnOpenDatabase, CC.xy(4, 2));

		//---- label1 ----
		label1.setText("Rules:"); //$NON-NLS-1$
		contentPane.add(label1, CC.xy(2, 4));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(edRules);
		}
		contentPane.add(scrollPane1, CC.xywh(2, 6, 3, 1, CC.FILL, CC.FILL));

		//---- btnCompare ----
		btnCompare.setText(LocaleBundle.getString("BatchEditFrame.btnCompare")); //$NON-NLS-1$
		contentPane.add(btnCompare, CC.xywh(2, 8, 3, 1));

		//======== tabbedPane1 ========
		{

			//======== panel1 ========
			{
				panel1.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.tabDelete"), panel1); //$NON-NLS-1$

			//======== panel2 ========
			{
				panel2.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateMeta"), panel2); //$NON-NLS-1$

			//======== panel3 ========
			{
				panel3.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateCover"), panel3); //$NON-NLS-1$

			//======== panel4 ========
			{
				panel4.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateFile"), panel4); //$NON-NLS-1$

			//======== panel5 ========
			{
				panel5.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.btnAdd"), panel5); //$NON-NLS-1$
		}
		contentPane.add(tabbedPane1, CC.xywh(2, 10, 3, 1, CC.FILL, CC.FILL));

		//---- btnCreatePatch ----
		btnCreatePatch.setText(LocaleBundle.getString("BatchEditFrame.btnCreatePatch")); //$NON-NLS-1$
		contentPane.add(btnCreatePatch, CC.xywh(2, 12, 3, 1));
		setSize(650, 500);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private ReadableTextField edDatabasePath;
	private JButton btnOpenDatabase;
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JTextArea edRules;
	private JButton btnCompare;
	private JTabbedPane tabbedPane1;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	private JPanel panel5;
	private JButton btnCreatePatch;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
