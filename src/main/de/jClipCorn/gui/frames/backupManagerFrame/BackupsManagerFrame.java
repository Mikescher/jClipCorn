package de.jClipCorn.gui.frames.backupManagerFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.features.backupManager.CCBackup;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;

public class BackupsManagerFrame extends JFrame {
	private static final long serialVersionUID = 1277211351977537864L;
	private JPanel panel;
	private JList<CCBackup> lsBackups;
	private JScrollPane scrollPane;
	private JLabel lblNames;
	private JLabel lblDates;
	private JLabel lblSizes;
	private JLabel lblPersistents;
	private JLabel lblVersions;
	private JLabel lblDbversion;
	private JLabel lblDeletionIns;
	private JButton btnCreateBackup;
	private JButton btnCreatePersistentBackup;
	private JButton btnMakePersistent;
	private JButton btnOpenInExplorer;
	private JButton btnDelete;
	private JButton btnInsert;
	
	private final BackupManager manager;
	private final CCMovieList movielist;
	
	private CCBackup currentSelected = null;
	private JButton btnChangeName;
	private JPanel panel_1;
	private JLabel lblInfoCreateBackups;
	private JLabel lblInfoCreationTime;
	private JLabel lblInfoAutoDelete;
	private JLabel lblInfoAutoDeletAfter;
	private JLabel lblInfoLastBackup;

	public BackupsManagerFrame(BackupManager bm, Component parent) {
		super();
		this.manager = bm;
		this.movielist = CCMovieList.getInstance();
		
		initGUI();
		updateList();
		updateInfo(null);
		
		setLocationRelativeTo(parent);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("BackupsManagerFrame.this.title")); //$NON-NLS-1$
		setMinimumSize(new Dimension(650, 400));
		setSize(new Dimension(800, 500));
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(2)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(panel, "2, 2, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("25dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		btnCreateBackup = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnCreateBackup.text")); //$NON-NLS-1$
		btnCreateBackup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createBackup(false, null);
			}
		});
		panel.add(btnCreateBackup, "2, 2, left, default"); //$NON-NLS-1$
		
		btnCreatePersistentBackup = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnCreatePersistentBackup.text")); //$NON-NLS-1$
		btnCreatePersistentBackup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = DialogHelper.showLocalInputDialog(BackupsManagerFrame.this, "BackupsManagerFrame.dialogs.inputPersistentName", manager.getStandardBackupname()); //$NON-NLS-1$
				createBackup(true, name);
			}
		});
		panel.add(btnCreatePersistentBackup, "4, 2, left, default"); //$NON-NLS-1$
		
		lblNames = new JLabel();
		panel.add(lblNames, "2, 4"); //$NON-NLS-1$
		
		btnChangeName = new JButton("..."); //$NON-NLS-1$
		btnChangeName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isElementSelected()) {
					currentSelected.setName(DialogHelper.showPlainInputDialog(BackupsManagerFrame.this, currentSelected.getName()));
					updateInfo(currentSelected);
				}
			}
		});
		panel.add(btnChangeName, "4, 4, left, default"); //$NON-NLS-1$
		
		lblDates = new JLabel();
		panel.add(lblDates, "2, 6"); //$NON-NLS-1$
		
		lblSizes = new JLabel();
		panel.add(lblSizes, "2, 8"); //$NON-NLS-1$
		
		lblPersistents = new JLabel();
		panel.add(lblPersistents, "2, 10"); //$NON-NLS-1$
		
		btnMakePersistent = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnSwitchPersistent.text")); //$NON-NLS-1$
		btnMakePersistent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isElementSelected()) {
					currentSelected.setPersistent(! currentSelected.isPersistent());
					updateInfo(currentSelected);
				}
			}
		});
		panel.add(btnMakePersistent, "4, 10, left, default"); //$NON-NLS-1$
		
		lblVersions = new JLabel();
		panel.add(lblVersions, "2, 12, 3, 1"); //$NON-NLS-1$
		
		lblDbversion = new JLabel();
		panel.add(lblDbversion, "2, 14, 3, 1"); //$NON-NLS-1$
		
		lblDeletionIns = new JLabel();
		panel.add(lblDeletionIns, "2, 16, 3, 1"); //$NON-NLS-1$
		
		btnInsert = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnRestoreBackup.text")); //$NON-NLS-1$
		btnInsert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isElementSelected() &&DialogHelper.showLocaleYesNo(BackupsManagerFrame.this, "BackupsManagerFrame.dialogs.restoreWarning")) { //$NON-NLS-1$
					restoreBackup(currentSelected);
				}
			}
		});
		panel.add(btnInsert, "2, 20, left, default"); //$NON-NLS-1$
		
		btnOpenInExplorer = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnOpenInExplorer.text")); //$NON-NLS-1$
		btnOpenInExplorer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (isElementSelected()) PathFormatter.showInExplorer(currentSelected.getArchive());
			}
		});
		
		panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(panel_1, "4, 20, 1, 7, fill, fill"); //$NON-NLS-1$
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblInfoCreateBackups = new JLabel();
		panel_1.add(lblInfoCreateBackups, "2, 2"); //$NON-NLS-1$
		
		lblInfoCreationTime = new JLabel();
		panel_1.add(lblInfoCreationTime, "2, 4"); //$NON-NLS-1$
		
		lblInfoAutoDelete = new JLabel();
		panel_1.add(lblInfoAutoDelete, "2, 6"); //$NON-NLS-1$
		
		lblInfoAutoDeletAfter = new JLabel();
		panel_1.add(lblInfoAutoDeletAfter, "2, 8"); //$NON-NLS-1$
		
		lblInfoLastBackup = new JLabel();
		panel_1.add(lblInfoLastBackup, "2, 10"); //$NON-NLS-1$
		panel.add(btnOpenInExplorer, "2, 22, left, default"); //$NON-NLS-1$
		
		btnDelete = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnDelete.text")); //$NON-NLS-1$
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isElementSelected()) {
					manager.deleteBackupWithWait(currentSelected);
					updateList();
				}
			}
		});
		panel.add(btnDelete, "2, 24, left, default"); //$NON-NLS-1$
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "4, 2, fill, fill"); //$NON-NLS-1$
		
		lsBackups = new JList<>();
		lsBackups.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSelection();
			}
		});
		scrollPane.setViewportView(lsBackups);
	}
	
	private void onSelection() {
		updateInfo(lsBackups.getSelectedValue());
	}
	
	private boolean isElementSelected() {
		return currentSelected != null;
	}
	
	private void updateInfo(CCBackup bkp) {
		currentSelected = bkp;
		
		if (isElementSelected()) {
			lblNames.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblName.text", currentSelected.getName())); //$NON-NLS-1$
			lblDates.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblDate.text", currentSelected.getDate().toStringUIVerbose())); //$NON-NLS-1$
			lblSizes.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblSize.text", FileSizeFormatter.format(currentSelected.getSize()))); //$NON-NLS-1$
			lblPersistents.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblPersistent.text", currentSelected.isPersistent() ? "[X]" : "[ ]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			lblVersions.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblVersion.text", currentSelected.getCCVersion())); //$NON-NLS-1$
			lblDbversion.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblDBVersion.text", currentSelected.getDBVersion(), Main.DBVERSION)); //$NON-NLS-1$
			lblDeletionIns.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblDeletion.text", getDaysUntilDeletion(currentSelected))); //$NON-NLS-1$
		} else {
			lblNames.setText(LocaleBundle.getDeformattedString("BackupsManagerFrame.lblName.text")); //$NON-NLS-1$
			lblDates.setText(LocaleBundle.getDeformattedString("BackupsManagerFrame.lblDate.text")); //$NON-NLS-1$
			lblSizes.setText(LocaleBundle.getDeformattedString("BackupsManagerFrame.lblSize.text")); //$NON-NLS-1$
			lblPersistents.setText(LocaleBundle.getDeformattedString("BackupsManagerFrame.lblPersistent.text")); //$NON-NLS-1$
			lblVersions.setText(LocaleBundle.getDeformattedString("BackupsManagerFrame.lblVersion.text")); //$NON-NLS-1$
			lblDbversion.setText(LocaleBundle.getDeformattedString("BackupsManagerFrame.lblDBVersion.textEmpty")); //$NON-NLS-1$
			lblDeletionIns.setText(LocaleBundle.getDeformattedString("BackupsManagerFrame.lblDeletion.textEmpty")); //$NON-NLS-1$
		}
		
		String on = LocaleBundle.getString("BackupsManagerFrame.infoLabels.lblON"); //$NON-NLS-1$
		String off = LocaleBundle.getString("BackupsManagerFrame.infoLabels.lblOFF"); //$NON-NLS-1$
		
		lblInfoCreateBackups.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.CreateBackups", CCProperties.getInstance().PROP_BACKUP_CREATEBACKUPS.getValue() ? on : off)); //$NON-NLS-1$
		lblInfoCreationTime.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.CreationTime", CCProperties.getInstance().PROP_BACKUP_BACKUPTIME.getValue())); //$NON-NLS-1$
		lblInfoAutoDelete.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.AutoDelete", CCProperties.getInstance().PROP_BACKUP_AUTODELETEBACKUPS.getValue() ? on : off)); //$NON-NLS-1$
		lblInfoAutoDeletAfter.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.DeletionTime", CCProperties.getInstance().PROP_BACKUP_LIFETIME.getValue())); //$NON-NLS-1$
		lblInfoLastBackup.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.infoLabels.LastBackup", CCProperties.getInstance().PROP_BACKUP_LASTBACKUP.getValue().toStringUIVerbose())); //$NON-NLS-1$
		
		updateButtonStates();
	}
	
	private String getDaysUntilDeletion(CCBackup bkp) {
		if (bkp.isPersistent()) return "-"; //$NON-NLS-1$
		
		if (! CCProperties.getInstance().PROP_BACKUP_AUTODELETEBACKUPS.getValue()) return "-"; //$NON-NLS-1$
		
		int age = bkp.getDate().getDayDifferenceTo(CCDate.getCurrentDate());
		
		int dud = CCProperties.getInstance().PROP_BACKUP_LIFETIME.getValue() - age;
		
		return String.valueOf(dud);
	}

	private void updateList() {
		List<CCBackup> backups = manager.getBackupListWithWait();
		
		Collections.sort(backups, new Comparator<CCBackup>() {
			@Override
			public int compare(CCBackup b1, CCBackup b2) {
				return CCDate.compare(b1.getDate(), b2.getDate());
			}
		});
		
		DefaultListModel<CCBackup> model = new DefaultListModel<>();
		
		for (CCBackup bkp : backups) model.addElement(bkp);
		
		lsBackups.setModel(model);
		
		updateInfo(null);
	}
	
	private void updateButtonStates() {
		boolean ra = ! CCProperties.getInstance().ARG_READONLY;
		btnCreateBackup.setEnabled(ra);
		btnCreatePersistentBackup.setEnabled(ra);
		btnChangeName.setEnabled(isElementSelected() && ra);
		btnMakePersistent.setEnabled(isElementSelected() && ra);
		btnOpenInExplorer.setEnabled(isElementSelected());
		btnDelete.setEnabled(isElementSelected() && ra);
		btnInsert.setEnabled(isElementSelected() && ra && currentSelected.containsCovers());
	}
	
	private void createBackup(final boolean persistent, final String name) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (persistent) {
					movielist.disconnectDatabase(false);
					manager.createBackupWithWait(BackupsManagerFrame.this, name, true);
					movielist.reconnectDatabase();
				} else {
					movielist.disconnectDatabase(false);
					manager.createBackupWithWait(BackupsManagerFrame.this);
					movielist.reconnectDatabase();
				}
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateList();
					}
				});
			}
		}, "THREAD_BACKUPSMANAGERFRAME_CREATEBACKUP").start(); //$NON-NLS-1$
	}
	
	private void restoreBackup(final CCBackup bkp) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				movielist.disconnectDatabase(true);
				
				if (manager.restoreBackupWithWait(BackupsManagerFrame.this, bkp)) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							DialogHelper.showLocalInformation(BackupsManagerFrame.this, "BackupsManagerFrame.dialogs.ApplicationIsRestarting"); //$NON-NLS-1$

							if (! ApplicationHelper.restartApplication()) {
								DialogHelper.showLocalError(BackupsManagerFrame.this, "BackupsManagerFrame.dialogs.RestartFailed"); //$NON-NLS-1$
								
								ApplicationHelper.exitApplication();
							}
						}
					});
				}
			}
		}, "THREAD_BACKUPSMANAGeERFRAME_RESTOREBACKUP").start(); //$NON-NLS-1$
	}
}
