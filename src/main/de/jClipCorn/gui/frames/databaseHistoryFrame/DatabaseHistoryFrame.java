package de.jClipCorn.gui.frames.databaseHistoryFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.database.history.CCHistoryAction;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.jSplitButton.JSplitButton;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.lambda.Func0to0WithException;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Collections;

public class DatabaseHistoryFrame extends JCCFrame
{
	private String _triggerError = Str.Empty;
	private Boolean _historyActive = null;

	public DatabaseHistoryFrame(Component owner, CCMovieList mlist) {
		super(mlist);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		execute(this::loadStatus);
	}

	public DatabaseHistoryFrame(Component owner, CCMovieList mlist, String idfilter) {
		super(mlist);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		cbxIgnoreTrivial.setSelected(false);

		if (Str.isNullOrWhitespace(idfilter)) {
			execute(this::loadStatus);
		} else {
			edFilter.setText(idfilter);
			var q = readQueryOptions(null, Opt.empty());
			execute(() -> { loadStatus(); runQuery(q); });
		}
	}

	private void postInit()
	{
		ccprops().PROP_FSIZE_DATABASEHISTORYFRAME.applyOrSkip(this);

		edStatus.setText("..."); //$NON-NLS-1$
		edTrigger.setText("..."); //$NON-NLS-1$
		edTableSize.setText("..."); //$NON-NLS-1$
	}

	/** Runs {@code action} off the EDT with all controls disabled - the actions must not overlap. */
	private void execute(Func0to0WithException<Exception> action)
	{
		setBusy(true);

		new Thread(() ->
		{
			try
			{
				action.invoke();
			}
			catch (Throwable e)
			{
				CCLog.addError(e);
				DialogHelper.showLocalError(this, "Dialogs.GenericError"); //$NON-NLS-1$
			}
			finally
			{
				SwingUtils.invokeLater(() -> setBusy(false));
			}
		}, "HISTORY_FRAME").start(); //$NON-NLS-1$
	}

	private void setBusy(boolean busy)
	{
		boolean ro = movielist.isReadonly();

		btnGetHistory.setEnabled(!busy);
		btnEnableTrigger.setEnabled(!busy && !ro && Boolean.FALSE.equals(_historyActive));
		btnDisableTrigger.setEnabled(!busy && !ro && Boolean.TRUE.equals(_historyActive));
		btnTriggerMore.setEnabled(!busy && !Str.isNullOrEmpty(_triggerError));
		cbxIgnoreTrivial.setEnabled(!busy);
		cbxDoAgressiveMerges.setEnabled(!busy);
		cbxUpdatesOnly.setEnabled(!busy);
		edFilter.setEnabled(!busy);
	}

	private void loadStatus()
	{
		CCDatabaseHistory h = movielist.getHistory();

		boolean active = h.isHistoryActive();

		RefParam<String> err = new RefParam<>();
		boolean triggerOk = h.testTrigger(active, err);

		SwingUtils.invokeLater(() ->
		{
			_historyActive = active;
			_triggerError  = triggerOk ? Str.Empty : err.Value;

			edStatus.setText(LocaleBundle.getString(active ? "DatabaseHistoryFrame.Active" : "DatabaseHistoryFrame.Inactive")); //$NON-NLS-1$ //$NON-NLS-2$

			edTrigger.setText(LocaleBundle.getString(triggerOk ? "DatabaseHistoryFrame.Okay" : "DatabaseHistoryFrame.Error")); //$NON-NLS-1$ //$NON-NLS-2$
			edTrigger.setBackground(triggerOk ? Color.GREEN : Color.RED);
			edTrigger.setForeground(Color.BLACK);
		});

		showTableSizes(Str.Empty);
	}

	private void showTableSizes(String suffix)
	{
		String text;
		try
		{
			var sizes = movielist.getHistory().getTableSizes();
			text = sizes.Item1 + " + " + sizes.Item2 + " + " + sizes.Item3 + suffix; //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (SQLException e)
		{
			CCLog.addError(e);
			text = LocaleBundle.getString("DatabaseHistoryFrame.Error"); //$NON-NLS-1$
		}

		final String _text = text;
		SwingUtils.invokeLater(() -> edTableSize.setText(_text));
	}

	private static class QueryOptions
	{
		public CCDateTime Start;
		public Opt<Integer> Limit;
		public boolean IgnoreTrivial;
		public boolean MergeAggressive;
		public boolean UpdatesOnly;
		public String Filter;
	}

	private QueryOptions readQueryOptions(CCDateTime start, Opt<Integer> limit)
	{
		var q = new QueryOptions();
		q.Start           = start;
		q.Limit           = limit;
		q.IgnoreTrivial   = cbxIgnoreTrivial.isSelected();
		q.MergeAggressive = cbxDoAgressiveMerges.isSelected();
		q.UpdatesOnly     = cbxUpdatesOnly.isSelected();
		q.Filter          = Str.isNullOrWhitespace(edFilter.getText()) ? null : edFilter.getText().trim();
		return q;
	}

	private void queryHistory() { queryHistory(null, Opt.of(4096)); }

	private void queryHistory(CCDateTime dt) { queryHistory(dt, Opt.empty()); }

	private void queryHistory(CCDateTime dt, Opt<Integer> limit)
	{
		var q = readQueryOptions(dt, limit);
		execute(() -> runQuery(q));
	}

	private void runQuery(QueryOptions q) throws Exception
	{
		SwingUtils.invokeLater(() -> progressBar.setIndeterminate(true));

		boolean success = false;
		try
		{
			var queryRes = movielist.getHistory().query(movielist, q.IgnoreTrivial, q.IgnoreTrivial, q.MergeAggressive, q.Start, q.Limit, null, q.Filter);

			var data = queryRes.Item1;

			if (q.UpdatesOnly) data = CCStreams.iterate(data).filter(p -> p.Action == CCHistoryAction.UPDATE).toList();

			Collections.reverse(data);

			final var _data = data;

			SwingUtils.invokeLater(() ->
			{
				tableEntries.setData(_data);
				tableEntries.autoResize();
				tableChanges.clearData();
			});

			showTableSizes(" (" + queryRes.Item2 + " -> " + data.size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			success = true;
		}
		finally
		{
			final boolean _success = success;
			SwingUtils.invokeLater(() ->
			{
				progressBar.setIndeterminate(false);
				progressBar.setMaximum(1);
				progressBar.setValue(_success ? 1 : 0);
			});
		}
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
		execute(() -> { movielist.getHistory().enableTrigger(); onTriggerChanged(); });
	}

	private void disableTrigger() {
		execute(() -> { movielist.getHistory().disableTrigger(); onTriggerChanged(); });
	}

	private void onTriggerChanged() {
		SwingUtils.invokeLater(() -> { tableEntries.clearData(); tableChanges.clearData(); });
		loadStatus();
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
		label4 = new JLabel();
		cbxDoAgressiveMerges = new JCheckBox();
		edFilter = new JTextField();
		cbxUpdatesOnly = new JCheckBox();
		progressBar = new JProgressBar();
		splitPane1 = new JSplitPane();
		tableEntries = new DatabaseHistoryTable(this);
		tableChanges = new DatabaseHistoryChangesTable(this);
		label5 = new JLabel();
		tfOldValue = new ReadableTextField();
		label6 = new JLabel();
		tfNewValue = new ReadableTextField();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(LocaleBundle.getString("DatabaseHistoryFrame.title"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, default, $lcgap, 100dlu:grow, 3*($lcgap, default), $lcgap, [76dlu,default], $rgap",
			"$rgap, 6*(default, $lgap), 15dlu, $lgap, default:grow, 2*($lgap, default), $rgap"));

		//---- label1 ----
		label1.setText(LocaleBundle.getString("DatabaseHistoryFrame.lblStatus"));
		contentPane.add(label1, CC.xy(2, 2));
		contentPane.add(edStatus, CC.xy(4, 2, CC.DEFAULT, CC.CENTER));

		//---- btnEnableTrigger ----
		btnEnableTrigger.setText(LocaleBundle.getString("DatabaseHistoryFrame.btnAktivieren"));
		btnEnableTrigger.addActionListener(e -> enableTrigger());
		contentPane.add(btnEnableTrigger, CC.xy(6, 2));

		//---- btnDisableTrigger ----
		btnDisableTrigger.setText(LocaleBundle.getString("DatabaseHistoryFrame.btnDeaktivieren"));
		btnDisableTrigger.addActionListener(e -> disableTrigger());
		contentPane.add(btnDisableTrigger, CC.xy(8, 2));

		//---- label2 ----
		label2.setText(LocaleBundle.getString("DatabaseHistoryFrame.lblTrigger"));
		contentPane.add(label2, CC.xy(2, 4));
		contentPane.add(edTrigger, CC.xy(4, 4, CC.DEFAULT, CC.CENTER));

		//---- btnTriggerMore ----
		btnTriggerMore.setText("...");
		btnTriggerMore.addActionListener(e -> showTrigger());
		contentPane.add(btnTriggerMore, CC.xy(6, 4));

		//---- label3 ----
		label3.setText(LocaleBundle.getString("DatabaseHistoryFrame.lblTablesize"));
		contentPane.add(label3, CC.xy(2, 6));
		contentPane.add(edTableSize, CC.xy(4, 6, CC.DEFAULT, CC.CENTER));

		//---- btnGetHistory ----
		btnGetHistory.setText(LocaleBundle.getString("DatabaseHistoryFrame.btnGetter"));
		btnGetHistory.addButtonClickedActionListener(e -> queryHistory());
		btnGetHistory.setPopupMenu(getQueryPopupMenu());
		contentPane.add(btnGetHistory, CC.xywh(12, 6, 1, 3));

		//---- cbxIgnoreTrivial ----
		cbxIgnoreTrivial.setText(LocaleBundle.getString("DatabaseHistoryFrame.IgnoreTrivial"));
		cbxIgnoreTrivial.setSelected(true);
		contentPane.add(cbxIgnoreTrivial, CC.xywh(2, 8, 7, 1));

		//---- label4 ----
		label4.setText(LocaleBundle.getString("DatabaseHistoryFrame.Filter"));
		contentPane.add(label4, CC.xy(12, 10));

		//---- cbxDoAgressiveMerges ----
		cbxDoAgressiveMerges.setText(LocaleBundle.getString("DatabaseHistoryFrame.MergeAggressive"));
		cbxDoAgressiveMerges.setSelected(true);
		contentPane.add(cbxDoAgressiveMerges, CC.xywh(2, 10, 7, 1));
		contentPane.add(edFilter, CC.xy(12, 12));

		//---- cbxUpdatesOnly ----
		cbxUpdatesOnly.setText(LocaleBundle.getString("DatabaseHistoryFrame.cbxUpdatesOnly"));
		contentPane.add(cbxUpdatesOnly, CC.xywh(2, 12, 7, 1));
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
		label5.setText(LocaleBundle.getString("DatabaseHistoryFrame.Table.ColumnOld"));
		label5.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPane.add(label5, CC.xy(2, 18));
		contentPane.add(tfOldValue, CC.xywh(4, 18, 9, 1));

		//---- label6 ----
		label6.setText(LocaleBundle.getString("DatabaseHistoryFrame.Table.ColumnNew"));
		label6.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPane.add(label6, CC.xy(2, 20));
		contentPane.add(tfNewValue, CC.xywh(4, 20, 9, 1));
		setSize(715, 700);
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
	private JLabel label4;
	private JCheckBox cbxDoAgressiveMerges;
	private JTextField edFilter;
	private JCheckBox cbxUpdatesOnly;
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
