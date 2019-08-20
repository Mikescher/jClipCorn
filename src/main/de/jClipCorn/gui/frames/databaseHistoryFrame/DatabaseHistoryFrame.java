package de.jClipCorn.gui.frames.databaseHistoryFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.JButton;
import javax.swing.JCheckBox;

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
	private DatabaseHistoryTable tableEntries;
	private DatabaseHistoryChangesTable tableChanges;
	private JButton btnEnableTrigger;
	private JButton btnDisableTrigger;
	private JButton btnTriggerMore;
	private JCheckBox cbxIgnoreTrivial;

	private String _triggerError = Str.Empty;

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
				ColumnSpec.decode("100dlu"), //$NON-NLS-1$
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("1dlu:grow(2)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lblStatus = new JLabel(LocaleBundle.getString("DatabaseHistoryFrame.lblStatus")); //$NON-NLS-1$
		contentPane.add(lblStatus, "2, 2, right, bottom"); //$NON-NLS-1$
		
		edStatus = new ReadableTextField();
		contentPane.add(edStatus, "4, 2, fill, bottom"); //$NON-NLS-1$
		edStatus.setColumns(10);
		
		btnEnableTrigger = new JButton(LocaleBundle.getString("DatabaseHistoryFrame.btnAktivieren")); //$NON-NLS-1$
		btnEnableTrigger.addActionListener(this::enableTrigger);
		contentPane.add(btnEnableTrigger, "6, 2"); //$NON-NLS-1$
		
		btnDisableTrigger = new JButton(LocaleBundle.getString("DatabaseHistoryFrame.btnDeaktivieren")); //$NON-NLS-1$
		btnDisableTrigger.addActionListener(this::disableTrigger);
		contentPane.add(btnDisableTrigger, "8, 2"); //$NON-NLS-1$
		
		lblTrigger = new JLabel(LocaleBundle.getString("DatabaseHistoryFrame.lblTrigger")); //$NON-NLS-1$
		contentPane.add(lblTrigger, "2, 4, right, fill"); //$NON-NLS-1$
		
		edTrigger = new ReadableTextField();
		contentPane.add(edTrigger, "4, 4, fill, default"); //$NON-NLS-1$
		edTrigger.setColumns(10);
		
		btnTriggerMore = new JButton("..."); //$NON-NLS-1$
		btnTriggerMore.addActionListener(e -> GenericTextDialog.showText(DatabaseHistoryFrame.this, "Trigger", _triggerError, true)); //$NON-NLS-1$
		contentPane.add(btnTriggerMore, "6, 4, left, default"); //$NON-NLS-1$
		
		lblTabellengre = new JLabel(LocaleBundle.getString("DatabaseHistoryFrame.lblTablesize")); //$NON-NLS-1$
		contentPane.add(lblTabellengre, "2, 6, right, fill"); //$NON-NLS-1$
		
		edTableSize = new ReadableTextField();
		contentPane.add(edTableSize, "4, 6, fill, default"); //$NON-NLS-1$
		edTableSize.setColumns(10);
		
		cbxIgnoreTrivial = new JCheckBox(LocaleBundle.getString("DatabaseHistoryFrame.IgnoreTrivial")); //$NON-NLS-1$
		cbxIgnoreTrivial.setSelected(true);
		contentPane.add(cbxIgnoreTrivial, "4, 8, 3, 1"); //$NON-NLS-1$
		
		btnGetHistory = new JButton(LocaleBundle.getString("DatabaseHistoryFrame.btnGetter")); //$NON-NLS-1$
		btnGetHistory.addActionListener(this::queryHistory);
		contentPane.add(btnGetHistory, "8, 6, 3, 3, right, fill"); //$NON-NLS-1$
		
		tableEntries = new DatabaseHistoryTable(this);
		contentPane.add(tableEntries, "2, 10, 10, 1, fill, fill"); //$NON-NLS-1$
		tableEntries.autoResize();
		
		tableChanges = new DatabaseHistoryChangesTable();
		contentPane.add(tableChanges, "2, 12, 9, 1, fill, fill"); //$NON-NLS-1$
		tableChanges.autoResize();
	}

	private void queryHistory(ActionEvent evt) {
		try {
			tableEntries.setData(movielist.getHistory().query(cbxIgnoreTrivial.isSelected()));
			tableEntries.autoResize();
			updateUI();
		} catch (CCFormatException e) {
			CCLog.addError(e);
			DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
		}
	}

	private void enableTrigger(ActionEvent evt) {
		try {
			movielist.getHistory().enableTrigger();
			tableEntries.clearData();
			updateUI();
		} catch (SQLException e) {
			CCLog.addError(e);
			DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
		}
	}

	private void disableTrigger(ActionEvent evt) {
		try {
			movielist.getHistory().disableTrigger();
			tableEntries.clearData();
			updateUI();
		} catch (SQLException e) {
			CCLog.addError(e);
			DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
		}
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
			btnTriggerMore.setEnabled(false);
			_triggerError = Str.Empty;
		} else {
			edTrigger.setText(LocaleBundle.getString("DatabaseHistoryFrame.Error")); //$NON-NLS-1$
			edTrigger.setBackground(Color.RED);
			edTrigger.setForeground(Color.BLACK);
			btnTriggerMore.setEnabled(true);
			_triggerError = err.Value;
		}

		btnEnableTrigger.setEnabled(!active);
		btnDisableTrigger.setEnabled(active);

		edTableSize.setText(Integer.toString(h.getCount()));
	}

	public void showChanges(CCCombinedHistoryEntry elem) {
		if (elem == null) {
			tableChanges.clearData();
		} else {
			tableChanges.setData(CCStreams.iterate(elem.Changes).autosortByProperty(p -> p.Field.toLowerCase()).enumerate());
			tableChanges.autoResize();
		}
	}
}
