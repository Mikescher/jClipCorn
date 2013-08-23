package de.jClipCorn.gui.frames.backupManagerFrame;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;

public class BackupsManagerFrame extends JFrame {
	private static final long serialVersionUID = 1277211351977537864L;
	private JPanel panel;
	private JList lsBackups;
	private JScrollPane scrollPane;
	private JButton btnCreateBackup;
	private JButton btnCreatePersistentBackup;
	private JLabel lblNames;
	private JLabel lblDates;
	private JLabel lblSizes;
	private JLabel lblPersistents;
	private JLabel lblVersions;
	private JLabel lblDbversion;
	private JButton btnMakePersistent;
	private JButton btnOpenInExplorer;
	private JButton btnDelete;
	private JButton btnInsert;
	private JLabel lblDeletionIns;

	public BackupsManagerFrame(Component parent) {
		super();
		
		initGUI(); //TODO deactivatev things on READ-ONLY
		
		setLocationRelativeTo(parent);
	}
	
	private void initGUI() {
		setTitle("title");
		setMinimumSize(new Dimension(650, 350));
		setSize(new Dimension(800, 500));
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(2)"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(panel, "2, 2, fill, fill");
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
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
				RowSpec.decode("default:grow"),
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
		
		btnCreateBackup = new JButton("Create Backup");
		panel.add(btnCreateBackup, "2, 2");
		
		btnCreatePersistentBackup = new JButton("Create Persistent Backup");
		panel.add(btnCreatePersistentBackup, "4, 2, left, default");
		
		lblNames = new JLabel("Name: %s");
		panel.add(lblNames, "2, 4, 3, 1");
		
		lblDates = new JLabel("Date: %s");
		panel.add(lblDates, "2, 6, 3, 1");
		
		lblSizes = new JLabel("Size: %s");
		panel.add(lblSizes, "2, 8, 3, 1");
		
		lblPersistents = new JLabel("Persistent: %s");
		panel.add(lblPersistents, "2, 10, 3, 1");
		
		lblVersions = new JLabel("Version: %s");
		panel.add(lblVersions, "2, 12, 3, 1");
		
		lblDbversion = new JLabel("DB-Version: %s (Current: %s)");
		panel.add(lblDbversion, "2, 14, 3, 1");
		
		lblDeletionIns = new JLabel("Deletion In: %s");
		panel.add(lblDeletionIns, "2, 16, 3, 1");
		
		btnInsert = new JButton("Restore Backup");
		panel.add(btnInsert, "2, 20, 3, 1, left, default");
		
		btnMakePersistent = new JButton("Make Persistent / non-Persistent");
		panel.add(btnMakePersistent, "2, 22, 3, 1, left, default");
		
		btnOpenInExplorer = new JButton("Open In Explorer");
		panel.add(btnOpenInExplorer, "2, 24, 3, 1, left, default");
		
		btnDelete = new JButton("Delete");
		panel.add(btnDelete, "2, 26, 3, 1, left, default");
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "4, 2, fill, fill");
		
		lsBackups = new JList<>();
		scrollPane.setViewportView(lsBackups);
	}

}
