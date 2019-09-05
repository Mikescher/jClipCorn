package de.jClipCorn.gui.frames.databaseHistoryFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.ProgressCallbackProgressBarHelper;
import de.jClipCorn.util.stream.CCStreams;
import org.gpl.JSplitButton.JSplitButton;
import org.gpl.JSplitButton.action.SplitButtonActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

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
	private JSplitButton btnGetHistory;
	private DatabaseHistoryTable tableEntries;
	private DatabaseHistoryChangesTable tableChanges;
	private JButton btnEnableTrigger;
	private JButton btnDisableTrigger;
	private JButton btnTriggerMore;
	private JCheckBox cbxIgnoreTrivial;
	private JProgressBar progressBar;
	private JCheckBox cbxIgnoreIDChanges;

	private String _triggerError = Str.Empty;
	private int _tcount;

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

		setMinimumSize(new Dimension(700, 415));
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
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("70dlu"), //$NON-NLS-1$
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
				RowSpec.decode("15dlu"), //$NON-NLS-1$
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
		btnEnableTrigger.setFocusable(false);
		contentPane.add(btnEnableTrigger, "6, 2"); //$NON-NLS-1$
		
		btnDisableTrigger = new JButton(LocaleBundle.getString("DatabaseHistoryFrame.btnDeaktivieren")); //$NON-NLS-1$
		btnDisableTrigger.addActionListener(this::disableTrigger);
		btnDisableTrigger.setFocusable(false);
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
		contentPane.add(cbxIgnoreTrivial, "2, 8, 5, 1"); //$NON-NLS-1$
		
		btnGetHistory = new JSplitButton(LocaleBundle.getString("DatabaseHistoryFrame.btnGetter")); //$NON-NLS-1$
		btnGetHistory.addSplitButtonActionListener(new SplitButtonActionListener() {
			@Override public void splitButtonClicked(ActionEvent arg0) { /* */ }
			@Override public void buttonClicked(ActionEvent evt) { queryHistory(null); }
		});
		btnGetHistory.setPopupMenu(getQueryPopupMenu());
		contentPane.add(btnGetHistory, "12, 6, 1, 3, fill, fill"); //$NON-NLS-1$
		
		cbxIgnoreIDChanges = new JCheckBox(LocaleBundle.getString("DatabaseHistoryFrame.IgnoreTrivial2")); //$NON-NLS-1$
		contentPane.add(cbxIgnoreIDChanges, "2, 10, 7, 1"); //$NON-NLS-1$
		
		progressBar = new JProgressBar();
		contentPane.add(progressBar, "2, 12, 11, 1, fill, fill"); //$NON-NLS-1$
		
		tableEntries = new DatabaseHistoryTable(this);
		contentPane.add(tableEntries, "2, 14, 12, 1, fill, fill"); //$NON-NLS-1$
		tableEntries.autoResize();
		
		tableChanges = new DatabaseHistoryChangesTable();
		contentPane.add(tableChanges, "2, 16, 11, 1, fill, fill"); //$NON-NLS-1$
		tableChanges.autoResize();
	}

	private JPopupMenu getQueryPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();
		{
			JMenuItem m1 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeToday")); //$NON-NLS-1$
			m1.addActionListener(e -> queryHistory(CCDateTime.getTodayStart()));
			popupMenu.add(m1);

			JMenuItem m2 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeWeek")); //$NON-NLS-1$
			m2.addActionListener(e -> queryHistory(CCDateTime.getWeekStart()));
			popupMenu.add(m2);

			JMenuItem m3 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeMonth")); //$NON-NLS-1$
			m3.addActionListener(e -> queryHistory(CCDateTime.getMonthStart()));
			popupMenu.add(m3);

			JMenuItem m4 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeYear")); //$NON-NLS-1$
			m4.addActionListener(e -> queryHistory(CCDateTime.getYearStart()));
			popupMenu.add(m4);
		}
		return popupMenu;
	}

	private void queryHistory(CCDateTime dt) {

		new Thread(() ->
		{
			try {
				SwingUtilities.invokeAndWait(() -> 
				{
					setEnabled(false);
					MainFrame.getInstance().beginBlockingIntermediate();
					btnGetHistory.setEnabled(false);
				});

				tableEntries.setData(movielist.getHistory().query(
						cbxIgnoreTrivial.isSelected(),
						cbxIgnoreIDChanges.isSelected(),
						cbxIgnoreTrivial.isSelected(),
						dt,
						new ProgressCallbackProgressBarHelper(progressBar, 100)));

				SwingUtilities.invokeAndWait(() -> 
				{
					tableEntries.autoResize();
					updateUI();
					setEnabled(true);
					MainFrame.getInstance().endBlockingIntermediate();
					btnGetHistory.setEnabled(true);
					edTableSize.setText(_tcount + " (" + tableEntries.getDataDirect().size()+")"); //$NON-NLS-1$ //$NON-NLS-2$
				});
			} catch (CCFormatException | InvocationTargetException | InterruptedException e) {
				CCLog.addError(e);
				DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
			}
		}, "HISTORY_QUERY").start(); //$NON-NLS-1$
		
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

		btnEnableTrigger.setEnabled(!active && !CCProperties.getInstance().ARG_READONLY);
		btnDisableTrigger.setEnabled(active && !CCProperties.getInstance().ARG_READONLY);

		edTableSize.setText(Integer.toString(_tcount = h.getCount()));
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
