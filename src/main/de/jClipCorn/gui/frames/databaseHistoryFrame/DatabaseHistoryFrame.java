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
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.ProgressCallbackProgressBarHelper;
import de.jClipCorn.util.stream.CCStreams;
import org.gpl.JSplitButton.JSplitButton;
import org.gpl.JSplitButton.action.SplitButtonActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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
	private JCheckBox cbxDoAgressiveMerges;
	private JLabel lblFilter;
	private JTextField edFilter;

	private String _triggerError = Str.Empty;
	private int _tcount;
	private JSplitPane splitPane;
	private ReadableTextField tfOldValue;
	private ReadableTextField tfNewValue;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;

	/**
	 * @wbp.parser.constructor
	 */
	public DatabaseHistoryFrame(Component owner, CCMovieList mlist) {
		super();
		
		movielist = mlist;
		
		initGUI();
		setLocationRelativeTo(owner);

		updateUI();
	}

	public DatabaseHistoryFrame(Component owner, CCMovieList mlist, String idfilter) {
		super();

		movielist = mlist;

		initGUI();
		setLocationRelativeTo(owner);

		updateUI();

		cbxIgnoreTrivial.setSelected(false);
		cbxIgnoreIDChanges.setSelected(false);

		if (!Str.isNullOrWhitespace(idfilter)) {
			edFilter.setText(idfilter);
			queryHistory(null, true);
		}
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
				ColumnSpec.decode("100dlu:grow"), //$NON-NLS-1$
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("15dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("1dlu:grow(2)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
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
		contentPane.add(cbxIgnoreTrivial, "2, 8, 7, 1"); //$NON-NLS-1$
		
		btnGetHistory = new JSplitButton(LocaleBundle.getString("DatabaseHistoryFrame.btnGetter")); //$NON-NLS-1$
		btnGetHistory.addSplitButtonActionListener(new SplitButtonActionListener() {
			@Override public void splitButtonClicked(ActionEvent arg0) { /* */ }
			@Override public void buttonClicked(ActionEvent evt) { queryHistory(null); }
		});
		btnGetHistory.setPopupMenu(getQueryPopupMenu());
		contentPane.add(btnGetHistory, "12, 6, 1, 3, fill, fill"); //$NON-NLS-1$
		
		cbxIgnoreIDChanges = new JCheckBox(LocaleBundle.getString("DatabaseHistoryFrame.IgnoreTrivial2")); //$NON-NLS-1$
		cbxIgnoreIDChanges.setSelected(true);
		contentPane.add(cbxIgnoreIDChanges, "2, 10, 7, 1"); //$NON-NLS-1$
		
		lblFilter = new JLabel("Filter"); //$NON-NLS-1$
		lblFilter.setHorizontalAlignment(SwingConstants.LEADING);
		contentPane.add(lblFilter, "12, 10, default, bottom"); //$NON-NLS-1$
		
		cbxDoAgressiveMerges = new JCheckBox(LocaleBundle.getString("DatabaseHistoryFrame.MergeAggressive")); //$NON-NLS-1$
		cbxDoAgressiveMerges.setSelected(true);
		contentPane.add(cbxDoAgressiveMerges, "2, 12, 7, 1"); //$NON-NLS-1$
		
		edFilter = new JTextField();
		contentPane.add(edFilter, "12, 12, fill, top"); //$NON-NLS-1$
		edFilter.setColumns(10);
		
		progressBar = new JProgressBar();
		contentPane.add(progressBar, "2, 14, 11, 1, fill, fill"); //$NON-NLS-1$
		
		splitPane = new JSplitPane();
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, "2, 16, 11, 3, fill, fill"); //$NON-NLS-1$
		
		lblNewLabel = new JLabel(LocaleBundle.getString("DatabaseHistoryFrame.Table.ColumnOld")); //$NON-NLS-1$
		contentPane.add(lblNewLabel, "2, 20, right, fill"); //$NON-NLS-1$
		
		tfOldValue = new ReadableTextField();
		contentPane.add(tfOldValue, "4, 20, 9, 1, fill, fill"); //$NON-NLS-1$
		tfOldValue.setColumns(10);
		
		lblNewLabel_1 = new JLabel(LocaleBundle.getString("DatabaseHistoryFrame.Table.ColumnNew")); //$NON-NLS-1$
		contentPane.add(lblNewLabel_1, "2, 22, right, fill"); //$NON-NLS-1$
		
		tfNewValue = new ReadableTextField();
		contentPane.add(tfNewValue, "4, 22, 9, 1, fill, fill"); //$NON-NLS-1$
		tfNewValue.setColumns(10);

		tableChanges = new DatabaseHistoryChangesTable(tfOldValue, tfNewValue);
		splitPane.setRightComponent(tableChanges);
		
		tableEntries = new DatabaseHistoryTable(this);
		splitPane.setLeftComponent(tableEntries);

		tableEntries.autoResize();
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

			popupMenu.add(new JSeparator());

			JMenuItem m5 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeSubDay")); //$NON-NLS-1$
			m5.addActionListener(e -> queryHistory(CCDateTime.getCurrentDateTime().getSubDay(1)));
			popupMenu.add(m5);

			JMenuItem m6 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeSubWeek")); //$NON-NLS-1$
			m6.addActionListener(e -> queryHistory(CCDateTime.getCurrentDateTime().getSubDay(7)));
			popupMenu.add(m6);

			JMenuItem m7 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeSubMonth")); //$NON-NLS-1$
			m7.addActionListener(e -> queryHistory(CCDateTime.getCurrentDateTime().getSubDay(30)));
			popupMenu.add(m7);

			JMenuItem m8 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeSubYear")); //$NON-NLS-1$
			m8.addActionListener(e -> queryHistory(CCDateTime.getCurrentDateTime().getSubDay(365)));
			popupMenu.add(m8);

			popupMenu.add(new JSeparator());

			JMenuItem m9 = new JMenuItem(LocaleBundle.getString("DatabaseHistoryFrame.TimeAll")); //$NON-NLS-1$
			m9.addActionListener(e -> queryHistory(null));
			popupMenu.add(m9);
		}
		return popupMenu;
	}

	private void queryHistory(CCDateTime dt) { queryHistory(dt, false); }

	private void queryHistory(CCDateTime dt, boolean stayDisabled) {

		boolean optTrivial1 = cbxIgnoreTrivial.isSelected();
		boolean optTrivial2 = cbxIgnoreIDChanges.isSelected();
		boolean optAggressive = cbxDoAgressiveMerges.isSelected();
		
		new Thread(() ->
		{
			try
			{
				SwingUtils.invokeLater(() ->
				{
					setEnabled(false);
					MainFrame.getInstance().beginBlockingIntermediate();
					btnGetHistory.setEnabled(false);
					btnTriggerMore.setEnabled(false);
					btnEnableTrigger.setEnabled(false);
					btnDisableTrigger.setEnabled(false);
					edFilter.setEnabled(false);
				});

				String filter = edFilter.getText();
				if (Str.isNullOrWhitespace(filter)) filter = null;

				List<CCCombinedHistoryEntry> data = movielist.getHistory().query(
						optTrivial1,
						optTrivial2,
						optTrivial1,
						optAggressive,
						dt,
						new ProgressCallbackProgressBarHelper(progressBar, 100),
						filter);

				Collections.reverse(data);

				SwingUtils.invokeLater(() ->
				{
					tableEntries.setData(data);

					tableEntries.autoResize();
					updateUI();
					setEnabled(true);
					MainFrame.getInstance().endBlockingIntermediate();
					if (!stayDisabled) btnGetHistory.setEnabled(true);
					if (!stayDisabled) edFilter.setEnabled(true);
					edTableSize.setText(_tcount + " (" + tableEntries.getDataDirect().size()+")"); //$NON-NLS-1$ //$NON-NLS-2$
				});

			} catch (CCFormatException e) {
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

		btnTriggerMore.setEnabled(false);
		btnEnableTrigger.setEnabled(false);
		btnDisableTrigger.setEnabled(false);
		btnGetHistory.setEnabled(false);

		edStatus.setText("..."); //$NON-NLS-1$
		edTableSize.setText("..."); //$NON-NLS-1$
		edTrigger.setText("..."); //$NON-NLS-1$
		edTrigger.setBackground(Color.WHITE);
		edTrigger.setForeground(Color.BLACK);

		new Thread(() ->
		{
			var hcount  = h.getCount();
			var hactive = h.isHistoryActive();

			SwingUtils.invokeAndWaitSafe(() ->
			{
				if (hactive) {
					edStatus.setText(LocaleBundle.getString("DatabaseHistoryFrame.Active")); //$NON-NLS-1$
				} else {
					edStatus.setText(LocaleBundle.getString("DatabaseHistoryFrame.Inactive")); //$NON-NLS-1$
				}

				RefParam<String> err = new RefParam<>();
				boolean ok = h.testTrigger(hactive, err);
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

				btnEnableTrigger.setEnabled(!hactive && !CCProperties.getInstance().ARG_READONLY);
				btnDisableTrigger.setEnabled(hactive && !CCProperties.getInstance().ARG_READONLY);
				btnGetHistory.setEnabled(true);

				edTableSize.setText(Integer.toString(_tcount = hcount));
			});
		}).start();

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
