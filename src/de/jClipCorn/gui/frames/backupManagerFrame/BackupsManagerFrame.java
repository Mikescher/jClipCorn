package de.jClipCorn.gui.frames.backupManagerFrame;

import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.database.util.backupManager.BackupManager;
import de.jClipCorn.database.util.backupManager.CCBackup;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
	
	private CCBackup currentSelected = null;
	private final BackupManager manager;

	public BackupsManagerFrame(Component parent) {
		super();
		this.manager = BackupManager.getInstance();
		
		initGUI(); //TODO deactivatev things on READ-ONLY
		updateList();
		updateInfo(null);
		
		setLocationRelativeTo(parent);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("BackupsManagerFrame.this.title")); //$NON-NLS-1$
		setMinimumSize(new Dimension(650, 350));
		setSize(new Dimension(800, 500));
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(2)"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(panel, "2, 2, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		btnCreateBackup = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnCreateBackup.text")); //$NON-NLS-1$
		btnCreateBackup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO code
			}
		});
		panel.add(btnCreateBackup, "2, 2"); //$NON-NLS-1$
		
		btnCreatePersistentBackup = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnCreatePersistentBackup.text")); //$NON-NLS-1$
		btnCreatePersistentBackup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO code
			}
		});
		panel.add(btnCreatePersistentBackup, "4, 2, left, default"); //$NON-NLS-1$
		
		lblNames = new JLabel();
		panel.add(lblNames, "2, 4, 3, 1"); //$NON-NLS-1$
		
		lblDates = new JLabel();
		panel.add(lblDates, "2, 6, 3, 1"); //$NON-NLS-1$
		
		lblSizes = new JLabel();
		panel.add(lblSizes, "2, 8, 3, 1"); //$NON-NLS-1$
		
		lblPersistents = new JLabel();
		panel.add(lblPersistents, "2, 10, 3, 1"); //$NON-NLS-1$
		
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
				//TODO code
			}
		});
		panel.add(btnInsert, "2, 20, 3, 1, left, default"); //$NON-NLS-1$
		
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
		panel.add(btnMakePersistent, "2, 22, 3, 1, left, default"); //$NON-NLS-1$
		
		btnOpenInExplorer = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnOpenInExplorer.text")); //$NON-NLS-1$
		btnOpenInExplorer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (isElementSelected()) PathFormatter.showInExplorer(currentSelected.getArchive());
			}
		});
		panel.add(btnOpenInExplorer, "2, 24, 3, 1, left, default"); //$NON-NLS-1$
		
		btnDelete = new JButton(LocaleBundle.getString("BackupsManagerFrame.btnDelete.text")); //$NON-NLS-1$
		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isElementSelected()) {
					manager.deleteBackup(currentSelected);
					updateList();
				}
			}
		});
		panel.add(btnDelete, "2, 26, 3, 1, left, default"); //$NON-NLS-1$
		
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
			lblDates.setText(LocaleBundle.getFormattedString("BackupsManagerFrame.lblDate.text", currentSelected.getDate().getLocalStringRepresentation())); //$NON-NLS-1$
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
		List<CCBackup> backups = manager.getBackupList();
		
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
		btnCreateBackup.setEnabled(isElementSelected() && ra);
		btnCreatePersistentBackup.setEnabled(isElementSelected() && ra);
		btnMakePersistent.setEnabled(isElementSelected() && ra);
		btnOpenInExplorer.setEnabled(isElementSelected());
		btnDelete.setEnabled(isElementSelected() && ra);
		btnInsert.setEnabled(isElementSelected() && ra);
	}
}
