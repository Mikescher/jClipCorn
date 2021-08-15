package de.jClipCorn.gui.frames.backupManagerFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.features.backupManager.CCBackup;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.List;

public class BackupsManagerFrame extends JCCFrame
{
	private final BackupManager manager;

	public BackupsManagerFrame(Component owner, CCMovieList ml, BackupManager bm)
	{
		super(ml);
		this.manager = bm;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		updateList();
		updateInfo();
	}

	private void createNormalBackup() {
		createBackup(false, null);
	}

	private void createPersistentBackup() {
		String name = DialogHelper.showLocalInputDialog(BackupsManagerFrame.this, "BackupsManagerFrame.dialogs.inputPersistentName", manager.getStandardBackupname()); //$NON-NLS-1$
		createBackup(true, name);
	}

	private void editBackupName() {
		var sel = getCurrentSelected();
		if (sel == null) return;

		var str = DialogHelper.showPlainInputDialog(BackupsManagerFrame.this, sel.getName());
		if (str == null) return;

		sel.setName(str);
		updateInfo();
	}

	private void toggleBackupPersistent() {
		var sel = getCurrentSelected();
		if (sel == null) return;

		sel.setPersistent(!sel.isPersistent());
		updateInfo();
	}

	private void restoreBackup() {
		if (!isElementSelected()) return;
		if (!DialogHelper.showLocaleYesNo(BackupsManagerFrame.this, "BackupsManagerFrame.dialogs.restoreWarning")) return;

		restoreBackup(getCurrentSelected());
	}

	private void openBackupDir() {
		var sel = getCurrentSelected();
		if (sel == null) return;
		FilesystemUtils.showInExplorer(sel.getArchive());
	}

	private void deleteBackup() {
		var sel = getCurrentSelected();
		if (sel == null) return;

		manager.deleteBackupWithWait(sel);
		updateList();
	}

	private void onSelection() {
		updateInfo();
	}

	private void createBackup(final boolean persistent, final String name) {
		new Thread(() ->
		{
			if (persistent) {
				movielist.disconnectDatabase(false);
				manager.createBackupWithWait(BackupsManagerFrame.this, name, true);
				movielist.reconnectDatabase();
			} else {
				movielist.disconnectDatabase(false);
				manager.createBackupWithWait(BackupsManagerFrame.this);
				movielist.reconnectDatabase();
			}

			SwingUtils.invokeLater(this::updateList);
		}, "THREAD_BACKUPSMANAGERFRAME_CREATEBACKUP").start(); //$NON-NLS-1$
	}

	private void restoreBackup(final CCBackup bkp) {
		new Thread(() ->
		{
			movielist.disconnectDatabase(true);

			if (manager.restoreBackupWithWait(BackupsManagerFrame.this, bkp))
			{
				SwingUtils.invokeLater(() ->
				{
					DialogHelper.showDispatchLocalInformation(BackupsManagerFrame.this, "BackupsManagerFrame.dialogs.ApplicationIsRestarting"); //$NON-NLS-1$

					if (! ApplicationHelper.restartApplication())
					{
						DialogHelper.showLocalError(BackupsManagerFrame.this, "BackupsManagerFrame.dialogs.RestartFailed"); //$NON-NLS-1$

						ApplicationHelper.exitApplication(true);
					}
				});
			}
		}, "THREAD_BACKUPSMANAGeERFRAME_RESTOREBACKUP").start(); //$NON-NLS-1$
	}

	private CCBackup getCurrentSelected() {
		return lsBackups.getSelectedValue();
	}

	private boolean isElementSelected() {
		return getCurrentSelected() != null;
	}

	@SuppressWarnings("nls")
	private void updateInfo() {
		var currentSelected = getCurrentSelected();

		if (isElementSelected()) {
			lblName.setText(currentSelected.getName());
			lblDate.setText(currentSelected.getDate().toStringUIVerbose());
			lblSize.setText(FileSizeFormatter.format(currentSelected.getSize()));
			lblPermanent.setText(currentSelected.isPersistent() ? "[X]" : "[ ]");
			lblVersion.setText(currentSelected.getCCVersion()); //$NON-NLS-1$
			lblDBVersion.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblDBVersion.text", currentSelected.getDBVersion(), Main.DBVERSION));
			lblDeletion.setText(getDaysUntilDeletion(currentSelected));
		} else {
			lblName.setText(Str.Empty);
			lblDate.setText(Str.Empty);
			lblSize.setText(Str.Empty);
			lblPermanent.setText(Str.Empty);
			lblVersion.setText(Str.Empty);
			lblDBVersion.setText(Str.Empty);
			lblDeletion.setText(Str.Empty);
		}

		String on = LocaleBundle.getString("BackupsManagerFrame.infoLabels.lblON"); //$NON-NLS-1$
		String off = LocaleBundle.getString("BackupsManagerFrame.infoLabels.lblOFF"); //$NON-NLS-1$

		lblInfoCreateBackups.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.CreateBackups", ccprops().PROP_BACKUP_CREATEBACKUPS.getValue() ? on : off));
		lblInfoCreationTime.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.CreationTime", ccprops().PROP_BACKUP_BACKUPTIME.getValue()));
		lblInfoAutoDelete.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.AutoDelete", ccprops().PROP_BACKUP_AUTODELETEBACKUPS.getValue() ? on : off));
		lblInfoAutoDeletAfter.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.DeletionTime", ccprops().PROP_BACKUP_LIFETIME.getValue()));
		lblInfoLastBackup.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.LastBackup", ccprops().PROP_BACKUP_LASTBACKUP.getValue().toStringUIVerbose()));

		updateButtonStates();
	}

	private void updateList() {
		List<CCBackup> backups = manager.getBackupListWithWait();

		backups.sort((b1, b2) -> CCDate.compare(b1.getDate(), b2.getDate()));

		DefaultListModel<CCBackup> model = new DefaultListModel<>();

		for (CCBackup bkp : backups) model.addElement(bkp);

		lsBackups.setModel(model);

		updateInfo();
	}

	private void updateButtonStates() {
		boolean ra = ! movielist.isReadonly();
		btnCreateBackup.setEnabled(ra);
		btnCreatePersistentBackup.setEnabled(ra);
		btnChangeName.setEnabled(isElementSelected() && ra);
		btnMakePersistent.setEnabled(isElementSelected() && ra);
		btnOpenInExplorer.setEnabled(isElementSelected());
		btnDelete.setEnabled(isElementSelected() && ra);
		btnRestore.setEnabled(isElementSelected() && ra && getCurrentSelected().containsCovers());
	}

	private String getDaysUntilDeletion(CCBackup bkp) {
		if (bkp.isPersistent()) return LocaleBundle.getFormattedString("BackupsManagerFrame.lblDeletion.never"); //$NON-NLS-1$

		if (! ccprops().PROP_BACKUP_AUTODELETEBACKUPS.getValue()) return LocaleBundle.getFormattedString("BackupsManagerFrame.lblDeletion.never"); //$NON-NLS-1$

		int age = bkp.getDate().getDayDifferenceTo(CCDate.getCurrentDate());

		int dud = ccprops().PROP_BACKUP_LIFETIME.getValue() - age;

		return LocaleBundle.getFormattedString("BackupsManagerFrame.lblDeletion.text", String.valueOf(dud));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel1 = new JPanel();
		btnCreateBackup = new JButton();
		btnCreatePersistentBackup = new JButton();
		label1 = new JLabel();
		lblName = new JLabel();
		btnChangeName = new JButton();
		label2 = new JLabel();
		lblDate = new JLabel();
		label3 = new JLabel();
		lblSize = new JLabel();
		label4 = new JLabel();
		lblPermanent = new JLabel();
		btnMakePersistent = new JButton();
		label5 = new JLabel();
		lblVersion = new JLabel();
		label6 = new JLabel();
		lblDBVersion = new JLabel();
		label7 = new JLabel();
		lblDeletion = new JLabel();
		panel2 = new JPanel();
		btnRestore = new JButton();
		panel3 = new JPanel();
		lblInfoCreateBackups = new JLabel();
		lblInfoCreationTime = new JLabel();
		lblInfoAutoDelete = new JLabel();
		lblInfoAutoDeletAfter = new JLabel();
		lblInfoLastBackup = new JLabel();
		btnOpenInExplorer = new JButton();
		btnDelete = new JButton();
		scrollPane1 = new JScrollPane();
		lsBackups = new JList<>();

		//======== this ========
		setTitle(LocaleBundle.getString("BackupsManagerFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(620, 450));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $lcgap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default:grow, $ugap")); //$NON-NLS-1$

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"pref, $rgap, 0dlu:grow, $lcgap, default", //$NON-NLS-1$
				"9*(default, $lgap), default:grow, $lgap, default")); //$NON-NLS-1$
			((FormLayout)panel1.getLayout()).setRowGroups(new int[][] {{5, 7, 9, 11, 13, 15, 17}});

			//---- btnCreateBackup ----
			btnCreateBackup.setText(LocaleBundle.getString("BackupsManagerFrame.btnCreateBackup.text")); //$NON-NLS-1$
			btnCreateBackup.addActionListener(e -> createNormalBackup());
			panel1.add(btnCreateBackup, CC.xywh(1, 1, 5, 1));

			//---- btnCreatePersistentBackup ----
			btnCreatePersistentBackup.setText(LocaleBundle.getString("BackupsManagerFrame.btnCreatePersistentBackup.text")); //$NON-NLS-1$
			btnCreatePersistentBackup.addActionListener(e -> createPersistentBackup());
			panel1.add(btnCreatePersistentBackup, CC.xywh(1, 3, 5, 1));

			//---- label1 ----
			label1.setText(LocaleBundle.getString("BackupsManagerFrame.lblName.text")); //$NON-NLS-1$
			panel1.add(label1, CC.xy(1, 5));
			panel1.add(lblName, CC.xy(3, 5, CC.FILL, CC.FILL));

			//---- btnChangeName ----
			btnChangeName.setText("..."); //$NON-NLS-1$
			btnChangeName.addActionListener(e -> editBackupName());
			panel1.add(btnChangeName, CC.xy(5, 5));

			//---- label2 ----
			label2.setText(LocaleBundle.getString("BackupsManagerFrame.lblDate.text")); //$NON-NLS-1$
			panel1.add(label2, CC.xy(1, 7));
			panel1.add(lblDate, CC.xy(3, 7, CC.FILL, CC.FILL));

			//---- label3 ----
			label3.setText(LocaleBundle.getString("BackupsManagerFrame.lblSize.text")); //$NON-NLS-1$
			panel1.add(label3, CC.xy(1, 9));
			panel1.add(lblSize, CC.xy(3, 9, CC.FILL, CC.FILL));

			//---- label4 ----
			label4.setText(LocaleBundle.getString("BackupsManagerFrame.lblPersistent.text")); //$NON-NLS-1$
			panel1.add(label4, CC.xy(1, 11));
			panel1.add(lblPermanent, CC.xy(3, 11, CC.FILL, CC.FILL));

			//---- btnMakePersistent ----
			btnMakePersistent.setText(LocaleBundle.getString("BackupsManagerFrame.btnSwitchPersistent.text")); //$NON-NLS-1$
			btnMakePersistent.addActionListener(e -> toggleBackupPersistent());
			panel1.add(btnMakePersistent, CC.xy(5, 11));

			//---- label5 ----
			label5.setText(LocaleBundle.getString("BackupsManagerFrame.lblVersion.text")); //$NON-NLS-1$
			panel1.add(label5, CC.xy(1, 13));
			panel1.add(lblVersion, CC.xy(3, 13, CC.FILL, CC.FILL));

			//---- label6 ----
			label6.setText(LocaleBundle.getString("BackupsManagerFrame.lblDBVersion.textEmpty")); //$NON-NLS-1$
			panel1.add(label6, CC.xy(1, 15));
			panel1.add(lblDBVersion, CC.xy(3, 15, CC.FILL, CC.FILL));

			//---- label7 ----
			label7.setText(LocaleBundle.getString("BackupsManagerFrame.lblDeletion.textEmpty")); //$NON-NLS-1$
			panel1.add(label7, CC.xy(1, 17));
			panel1.add(lblDeletion, CC.xy(3, 17, CC.FILL, CC.FILL));

			//======== panel2 ========
			{
				panel2.setLayout(new FormLayout(
					"pref, 12dlu, default:grow", //$NON-NLS-1$
					"3*(default, $lgap), default")); //$NON-NLS-1$

				//---- btnRestore ----
				btnRestore.setText(LocaleBundle.getString("BackupsManagerFrame.btnRestoreBackup.text")); //$NON-NLS-1$
				btnRestore.addActionListener(e -> restoreBackup());
				panel2.add(btnRestore, CC.xy(1, 1, CC.LEFT, CC.DEFAULT));

				//======== panel3 ========
				{
					panel3.setBorder(new EtchedBorder());
					panel3.setLayout(new FormLayout(
						"$lcgap, pref:grow, $lcgap", //$NON-NLS-1$
						"default, $lgap, default, 5dlu, default, $lgap, default, 5dlu, default")); //$NON-NLS-1$

					//---- lblInfoCreateBackups ----
					lblInfoCreateBackups.setText(LocaleBundle.getString("BackupsManagerFrame.infoLabels.CreateBackups")); //$NON-NLS-1$
					panel3.add(lblInfoCreateBackups, CC.xy(2, 1));

					//---- lblInfoCreationTime ----
					lblInfoCreationTime.setText(LocaleBundle.getString("BackupsManagerFrame.infoLabels.CreationTime")); //$NON-NLS-1$
					panel3.add(lblInfoCreationTime, CC.xy(2, 3));

					//---- lblInfoAutoDelete ----
					lblInfoAutoDelete.setText(LocaleBundle.getString("BackupsManagerFrame.infoLabels.AutoDelete")); //$NON-NLS-1$
					panel3.add(lblInfoAutoDelete, CC.xy(2, 5));

					//---- lblInfoAutoDeletAfter ----
					lblInfoAutoDeletAfter.setText(LocaleBundle.getString("BackupsManagerFrame.infoLabels.DeletionTime")); //$NON-NLS-1$
					panel3.add(lblInfoAutoDeletAfter, CC.xy(2, 7));

					//---- lblInfoLastBackup ----
					lblInfoLastBackup.setText(LocaleBundle.getString("BackupsManagerFrame.infoLabels.LastBackup")); //$NON-NLS-1$
					panel3.add(lblInfoLastBackup, CC.xy(2, 9));
				}
				panel2.add(panel3, CC.xywh(3, 1, 1, 7));

				//---- btnOpenInExplorer ----
				btnOpenInExplorer.setText(LocaleBundle.getString("BackupsManagerFrame.btnOpenInExplorer.text")); //$NON-NLS-1$
				btnOpenInExplorer.addActionListener(e -> openBackupDir());
				panel2.add(btnOpenInExplorer, CC.xy(1, 3, CC.LEFT, CC.DEFAULT));

				//---- btnDelete ----
				btnDelete.setText(LocaleBundle.getString("BackupsManagerFrame.btnDelete.text")); //$NON-NLS-1$
				btnDelete.addActionListener(e -> deleteBackup());
				panel2.add(btnDelete, CC.xy(1, 5, CC.LEFT, CC.DEFAULT));
			}
			panel1.add(panel2, CC.xywh(1, 21, 5, 1, CC.FILL, CC.FILL));
		}
		contentPane.add(panel1, CC.xy(2, 2, CC.FILL, CC.FILL));

		//======== scrollPane1 ========
		{

			//---- lsBackups ----
			lsBackups.addListSelectionListener(e -> onSelection());
			scrollPane1.setViewportView(lsBackups);
		}
		contentPane.add(scrollPane1, CC.xy(4, 2, CC.FILL, CC.FILL));
		setSize(800, 500);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel1;
	private JButton btnCreateBackup;
	private JButton btnCreatePersistentBackup;
	private JLabel label1;
	private JLabel lblName;
	private JButton btnChangeName;
	private JLabel label2;
	private JLabel lblDate;
	private JLabel label3;
	private JLabel lblSize;
	private JLabel label4;
	private JLabel lblPermanent;
	private JButton btnMakePersistent;
	private JLabel label5;
	private JLabel lblVersion;
	private JLabel label6;
	private JLabel lblDBVersion;
	private JLabel label7;
	private JLabel lblDeletion;
	private JPanel panel2;
	private JButton btnRestore;
	private JPanel panel3;
	private JLabel lblInfoCreateBackups;
	private JLabel lblInfoCreationTime;
	private JLabel lblInfoAutoDelete;
	private JLabel lblInfoAutoDeletAfter;
	private JLabel lblInfoLastBackup;
	private JButton btnOpenInExplorer;
	private JButton btnDelete;
	private JScrollPane scrollPane1;
	private JList<CCBackup> lsBackups;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
