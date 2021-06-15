package de.jClipCorn.gui.frames.databaseHistoryFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.jSplitButton.JSplitButton;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.ProgressCallbackProgressBarHelper;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class DatabaseHistoryFrame extends JFrame
{
	private final CCMovieList movielist;

	private String _triggerError = Str.Empty;
	private int _tcount;

	public DatabaseHistoryFrame(Component owner, CCMovieList mlist) {
		super();

		movielist = mlist;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		updateUI();
	}

	public DatabaseHistoryFrame(Component owner, CCMovieList mlist, String idfilter) {
		super();

		movielist = mlist;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		updateUI();

		cbxIgnoreTrivial.setSelected(false);
		cbxIgnoreIDChanges.setSelected(false);

		if (!Str.isNullOrWhitespace(idfilter)) {
			edFilter.setText(idfilter);
			queryHistory(null, true);
		}
	}

	public CCMovieList getMovieList() {
		return movielist;
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());
	}

	private void updateUI()
	{
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

				btnEnableTrigger.setEnabled(!hactive && !movielist.isReadonly());
				btnDisableTrigger.setEnabled(hactive && !movielist.isReadonly());
				btnGetHistory.setEnabled(true);
				cbxDoAgressiveMerges.setEnabled(true);
				cbxIgnoreIDChanges.setEnabled(true);
				cbxIgnoreTrivial.setEnabled(true);

				edTableSize.setText(Integer.toString(_tcount = hcount));
			});
		}).start();
	}

	private void queryHistory() { queryHistory(null, false); }

	private void queryHistory(CCDateTime dt) { queryHistory(dt, false); }

	private void queryHistory(CCDateTime dt, boolean stayDisabled)
	{
		boolean optTrivial1 = cbxIgnoreTrivial.isSelected();
		boolean optTrivial2 = cbxIgnoreIDChanges.isSelected();
		boolean optAggressive = cbxDoAgressiveMerges.isSelected();

		new Thread(() ->
		{
			try
			{
				SwingUtils.invokeLater(() ->
				{
					btnGetHistory.setEnabled(false);
					btnTriggerMore.setEnabled(false);
					btnEnableTrigger.setEnabled(false);
					btnDisableTrigger.setEnabled(false);
					cbxDoAgressiveMerges.setEnabled(false);
					cbxIgnoreIDChanges.setEnabled(false);
					cbxIgnoreTrivial.setEnabled(false);
					edFilter.setEnabled(false);
				});

				String filter = edFilter.getText();
				if (Str.isNullOrWhitespace(filter)) filter = null;

				List<CCCombinedHistoryEntry> data = movielist.getHistory().query(
						movielist,
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
					if (!stayDisabled)
					{
						btnGetHistory.setEnabled(true);
						edFilter.setEnabled(true);
					}
					edTableSize.setText(_tcount + " (" + tableEntries.getDataDirect().size()+")"); //$NON-NLS-1$ //$NON-NLS-2$
				});

			} catch (Throwable e) {
				CCLog.addError(e);
				DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
			}
		}, "HISTORY_QUERY").start(); //$NON-NLS-1$
	}

	public void showChanges(CCCombinedHistoryEntry elem)
	{
		if (elem == null) {
			tableChanges.clearData();
		} else {
			tableChanges.setData(CCStreams.iterate(elem.Changes).autosortByProperty(p -> p.Field.toLowerCase()).enumerate());
			tableChanges.autoResize();
		}
	}

	private void enableTrigger() {
		try {
			movielist.getHistory().enableTrigger();
			tableEntries.clearData();
			updateUI();
		} catch (SQLException e) {
			CCLog.addError(e);
			DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
		}
	}

	private void disableTrigger() {
		try {
			movielist.getHistory().disableTrigger();
			tableEntries.clearData();
			updateUI();
		} catch (SQLException e) {
			CCLog.addError(e);
			DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
		}
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

	private void showTrigger() {
		GenericTextDialog.showText(DatabaseHistoryFrame.this, "Trigger", _triggerError, true); //$NON-NLS-1$
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		edStatus = new ReadableTextField();
		btnEnableTrigger = new JButton();
		btnDisableTrigger = new JButton();
		label2 = new JLabel();
		edTrigger = new ReadableTextField();
		btnTriggerMore = new JButton();
		label3 = new JLabel();
		edTableSize = new ReadableTextField();
		btnGetHistory = new JSplitButton();
		cbxIgnoreTrivial = new JCheckBox();
		cbxIgnoreIDChanges = new JCheckBox();
		label4 = new JLabel();
		cbxDoAgressiveMerges = new JCheckBox();
		edFilter = new JTextField();
		progressBar = new JProgressBar();
		splitPane1 = new JSplitPane();
		tableEntries = new DatabaseHistoryTable(this);
		tableChanges = new DatabaseHistoryChangesTable();
		label5 = new JLabel();
		tfOldValue = new ReadableTextField();
		label6 = new JLabel();
		tfNewValue = new ReadableTextField();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(LocaleBundle.getString("DatabaseHistoryFrame.title")); //$NON-NLS-1$
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, default, $lcgap, 100dlu:grow, 3*($lcgap, default), $lcgap, [70dlu,default], $rgap", //$NON-NLS-1$
			"$rgap, 6*(default, $lgap), 15dlu, $lgap, default:grow, 2*($lgap, default), $rgap")); //$NON-NLS-1$

		//---- label1 ----
		label1.setText(LocaleBundle.getString("DatabaseHistoryFrame.lblStatus")); //$NON-NLS-1$
		contentPane.add(label1, CC.xy(2, 2));
		contentPane.add(edStatus, CC.xy(4, 2, CC.DEFAULT, CC.CENTER));

		//---- btnEnableTrigger ----
		btnEnableTrigger.setText(LocaleBundle.getString("DatabaseHistoryFrame.btnAktivieren")); //$NON-NLS-1$
		btnEnableTrigger.addActionListener(e -> enableTrigger());
		contentPane.add(btnEnableTrigger, CC.xy(6, 2));

		//---- btnDisableTrigger ----
		btnDisableTrigger.setText(LocaleBundle.getString("DatabaseHistoryFrame.btnDeaktivieren")); //$NON-NLS-1$
		btnDisableTrigger.addActionListener(e -> disableTrigger());
		contentPane.add(btnDisableTrigger, CC.xy(8, 2));

		//---- label2 ----
		label2.setText(LocaleBundle.getString("DatabaseHistoryFrame.lblTrigger")); //$NON-NLS-1$
		contentPane.add(label2, CC.xy(2, 4));
		contentPane.add(edTrigger, CC.xy(4, 4, CC.DEFAULT, CC.CENTER));

		//---- btnTriggerMore ----
		btnTriggerMore.setText("..."); //$NON-NLS-1$
		btnTriggerMore.addActionListener(e -> showTrigger());
		contentPane.add(btnTriggerMore, CC.xy(6, 4));

		//---- label3 ----
		label3.setText(LocaleBundle.getString("DatabaseHistoryFrame.lblTablesize")); //$NON-NLS-1$
		contentPane.add(label3, CC.xy(2, 6));
		contentPane.add(edTableSize, CC.xy(4, 6, CC.DEFAULT, CC.CENTER));

		//---- btnGetHistory ----
		btnGetHistory.setText(LocaleBundle.getString("DatabaseHistoryFrame.btnGetter")); //$NON-NLS-1$
		btnGetHistory.addButtonClickedActionListener(e -> queryHistory());
		btnGetHistory.setPopupMenu(getQueryPopupMenu());
		contentPane.add(btnGetHistory, CC.xywh(12, 6, 1, 3));

		//---- cbxIgnoreTrivial ----
		cbxIgnoreTrivial.setText(LocaleBundle.getString("DatabaseHistoryFrame.IgnoreTrivial")); //$NON-NLS-1$
		cbxIgnoreTrivial.setSelected(true);
		contentPane.add(cbxIgnoreTrivial, CC.xywh(2, 8, 7, 1));

		//---- cbxIgnoreIDChanges ----
		cbxIgnoreIDChanges.setText(LocaleBundle.getString("DatabaseHistoryFrame.IgnoreTrivial2")); //$NON-NLS-1$
		cbxIgnoreIDChanges.setSelected(true);
		contentPane.add(cbxIgnoreIDChanges, CC.xywh(2, 10, 7, 1));

		//---- label4 ----
		label4.setText(LocaleBundle.getString("DatabaseHistoryFrame.Filter")); //$NON-NLS-1$
		contentPane.add(label4, CC.xy(12, 10));

		//---- cbxDoAgressiveMerges ----
		cbxDoAgressiveMerges.setText(LocaleBundle.getString("DatabaseHistoryFrame.MergeAggressive")); //$NON-NLS-1$
		cbxDoAgressiveMerges.setSelected(true);
		contentPane.add(cbxDoAgressiveMerges, CC.xywh(2, 12, 7, 1));
		contentPane.add(edFilter, CC.xy(12, 12));
		contentPane.add(progressBar, CC.xywh(2, 14, 11, 1, CC.DEFAULT, CC.FILL));

		//======== splitPane1 ========
		{
			splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane1.setResizeWeight(0.75);
			splitPane1.setContinuousLayout(true);

			//======== tableEntries ========
			{
				tableEntries.autoResize();
			}
			splitPane1.setTopComponent(tableEntries);

			//======== tableChanges ========
			{
				tableChanges.autoResize();
				tableChanges.initRefs(tfOldValue, tfNewValue);
			}
			splitPane1.setBottomComponent(tableChanges);
		}
		contentPane.add(splitPane1, CC.xywh(2, 16, 11, 1, CC.DEFAULT, CC.FILL));

		//---- label5 ----
		label5.setText(LocaleBundle.getString("DatabaseHistoryFrame.Table.ColumnOld")); //$NON-NLS-1$
		label5.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPane.add(label5, CC.xy(2, 18));
		contentPane.add(tfOldValue, CC.xywh(4, 18, 9, 1));

		//---- label6 ----
		label6.setText(LocaleBundle.getString("DatabaseHistoryFrame.Table.ColumnNew")); //$NON-NLS-1$
		label6.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPane.add(label6, CC.xy(2, 20));
		contentPane.add(tfNewValue, CC.xywh(4, 20, 9, 1));
		setSize(600, 600);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private ReadableTextField edStatus;
	private JButton btnEnableTrigger;
	private JButton btnDisableTrigger;
	private JLabel label2;
	private ReadableTextField edTrigger;
	private JButton btnTriggerMore;
	private JLabel label3;
	private ReadableTextField edTableSize;
	private JSplitButton btnGetHistory;
	private JCheckBox cbxIgnoreTrivial;
	private JCheckBox cbxIgnoreIDChanges;
	private JLabel label4;
	private JCheckBox cbxDoAgressiveMerges;
	private JTextField edFilter;
	private JProgressBar progressBar;
	private JSplitPane splitPane1;
	private DatabaseHistoryTable tableEntries;
	private DatabaseHistoryChangesTable tableChanges;
	private JLabel label5;
	private ReadableTextField tfOldValue;
	private JLabel label6;
	private ReadableTextField tfNewValue;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
