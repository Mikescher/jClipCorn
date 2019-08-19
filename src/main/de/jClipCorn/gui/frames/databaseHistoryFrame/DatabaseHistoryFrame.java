package de.jClipCorn.gui.frames.databaseHistoryFrame;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.util.datatypes.RefParam;

import javax.swing.JButton;

public class DatabaseHistoryFrame extends JFrame {
	private static final long serialVersionUID = 2988337093531794844L;

	private final CCMovieList movielist;

	private JPanel contentPane;
	private JLabel lblStatus;
	private JLabel lblTrigger;
	private JLabel lblTabellengre;
	private ReadableTextField edStatus;
	private ReadableTextField edTrigger;
	private ReadableTextField edTableSize;
	private JButton btnGetHistory;
	private DatabaseHistoryTable table;
	private JButton btnAktivieren;
	private JButton btnDeaktivieren;
	
	public DatabaseHistoryFrame(Component owner, CCMovieList mlist) {
		super();
		
		movielist = mlist;
		
		initGUI();
		setLocationRelativeTo(owner);

		updateUI();
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("DatabaseHistoryFrame.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setMinimumSize(new Dimension(600, 415));
		setBounds(100, 100, 769, 584);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("175dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
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
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lblStatus = new JLabel(LocaleBundle.getString("DatabaseHistoryFrame.lblStatus")); //$NON-NLS-1$
		contentPane.add(lblStatus, "2, 2, right, bottom"); //$NON-NLS-1$
		
		edStatus = new ReadableTextField();
		contentPane.add(edStatus, "4, 2, fill, bottom"); //$NON-NLS-1$
		edStatus.setColumns(10);
		
		btnAktivieren = new JButton(LocaleBundle.getString("DatabaseHistoryFrame.btnAktivieren")); //$NON-NLS-1$
		contentPane.add(btnAktivieren, "6, 2"); //$NON-NLS-1$
		
		btnDeaktivieren = new JButton(LocaleBundle.getString("DatabaseHistoryFrame.btnDeaktivieren")); //$NON-NLS-1$
		contentPane.add(btnDeaktivieren, "8, 2"); //$NON-NLS-1$
		
		lblTrigger = new JLabel(LocaleBundle.getString("DatabaseHistoryFrame.lblTrigger")); //$NON-NLS-1$
		contentPane.add(lblTrigger, "2, 4, right, fill"); //$NON-NLS-1$
		
		edTrigger = new ReadableTextField();
		contentPane.add(edTrigger, "4, 4, fill, default"); //$NON-NLS-1$
		edTrigger.setColumns(10);
		
		btnGetHistory = new JButton(LocaleBundle.getString("DatabaseHistoryFrame.btnGetter")); //$NON-NLS-1$
		contentPane.add(btnGetHistory, "6, 4, 5, 3, right, fill"); //$NON-NLS-1$
		
		lblTabellengre = new JLabel(LocaleBundle.getString("DatabaseHistoryFrame.lblTablesize")); //$NON-NLS-1$
		contentPane.add(lblTabellengre, "2, 6, right, fill"); //$NON-NLS-1$
		
		edTableSize = new ReadableTextField();
		contentPane.add(edTableSize, "4, 6, fill, default"); //$NON-NLS-1$
		edTableSize.setColumns(10);
		
		table = new DatabaseHistoryTable();
		contentPane.add(table, "2, 8, 10, 1, fill, fill"); //$NON-NLS-1$
	}

	private void updateUI() {
		CCDatabaseHistory h = movielist.getHistory();

		boolean active = h.isHistoryActive();
		if (active) {
			edStatus.setText(LocaleBundle.getString("DatabaseHistoryFrame.Active")); //$NON-NLS-1$
		} else {
			edStatus.setText(LocaleBundle.getString("DatabaseHistoryFrame.Inactive")); //$NON-NLS-1$
		}

		RefParam<String> err = new RefParam<>();
		boolean ok = h.testTrigger(active, err);
		if (ok) {
			edTrigger.setText(LocaleBundle.getString("DatabaseHistoryFrame.Okay")); //$NON-NLS-1$
			edTrigger.setBackground(Color.GREEN);
			edTrigger.setForeground(Color.BLACK);
		}
		else {
			edTrigger.setText(LocaleBundle.getString("DatabaseHistoryFrame.Error") + " (" + err.Value + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			edTrigger.setBackground(Color.RED);
			edTrigger.setForeground(Color.BLACK);
		}

		edTableSize.setText(Integer.toString(h.getCount()));
	}
}
