package de.jClipCorn.gui.frames.logFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.Globals;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.CalculationCache;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.log.CCLogChangedListener;
import de.jClipCorn.features.log.CCLogType;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.http.WebConnectionLayer;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class LogFrame extends JFrame implements CCLogChangedListener
{
	private Timer liveDisplayTimer;

	public LogFrame(Component owner)
	{
		super();

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
		setMinimumSize(getSize());
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());

		tpnlMain.setTitleAt(0, LocaleBundle.getString("CCLog.Errors")       + " (" + CCLog.getCount(CCLogType.LOG_ELEM_ERROR)       + ")");
		tpnlMain.setTitleAt(0, LocaleBundle.getString("CCLog.Warnings")     + " (" + CCLog.getCount(CCLogType.LOG_ELEM_WARNING)     + ")");
		tpnlMain.setTitleAt(0, LocaleBundle.getString("CCLog.Informations") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_INFORMATION) + ")");
		tpnlMain.setTitleAt(0, LocaleBundle.getString("CCLog.Undefinieds")  + " (" + CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED)   + ")");
		tpnlMain.setTitleAt(0, LocaleBundle.getString("CCLog.SQL")          + " (" + CCLog.getSQLCount()                            + ")");

		lsErrors.setModel(new LogListModel(lsErrors, CCLogType.LOG_ELEM_ERROR, memoErrors));
		lsWarnings.setModel(new LogListModel(lsWarnings, CCLogType.LOG_ELEM_WARNING, memoWarnings));
		lsInformations.setModel(new LogListModel(lsInformations, CCLogType.LOG_ELEM_INFORMATION, memoInformations));
		lsUndefinied.setModel(new LogListModel(lsUndefinied, CCLogType.LOG_ELEM_UNDEFINED, memoUndefinied));
		lsSQL.setModel(new LogSQLListModel(lsSQL, memoSQL));

		DatabaseElementPreviewLabel cl = MainFrame.getInstance().getCoverLabel();
		if (cl.isErrorMode()) cl.setModeDefault();

		CCLog.addChangeListener(this);

		CCLog.setAllWatched();

		liveDisplayTimer = new Timer(300, this::onTimer);
		liveDisplayTimer.setInitialDelay(1000);
		liveDisplayTimer.start();
	}

	private void onTimer(ActionEvent actionEvent)
	{
		if (tpnlMain.getSelectedIndex() != 5) return;

		displErrorCount.setText(String.valueOf(CCLog.getCount(CCLogType.LOG_ELEM_ERROR)));
		displWarningsCount.setText(String.valueOf(CCLog.getCount(CCLogType.LOG_ELEM_WARNING)));
		displUndefiniedCount.setText(String.valueOf(CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED)));

		displQueryCount.setText(String.valueOf(CCLog.getSQLCount()));
		displRequestCount.setText(String.valueOf(WebConnectionLayer.getTotalRequestCount()));

		displUptime.setText(String.valueOf(System.currentTimeMillis() - Globals.MILLIS_MAIN));

		displCacheHits.setText(CalculationCache.formatCacheHits());
		displCacheMisses.setText(CalculationCache.formatCacheMisses());
		displCacheInvalidations.setText(String.valueOf(CalculationCache.CacheInvalidations));
		displCacheTotalCount.setText(String.valueOf(CalculationCache.CacheSizeTotal));
		displPreInitCacheQueries.setText(String.valueOf(CalculationCache.PreInitRequests));
	}

	@Override
	public void onChanged() {
		tpnlMain.setTitleAt(0, LocaleBundle.getString("CCLog.Error")        + " (" + CCLog.getCount(CCLogType.LOG_ELEM_ERROR)       + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tpnlMain.setTitleAt(1, LocaleBundle.getString("CCLog.Warnings")     + " (" + CCLog.getCount(CCLogType.LOG_ELEM_WARNING)     + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tpnlMain.setTitleAt(2, LocaleBundle.getString("CCLog.Informations") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_INFORMATION) + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tpnlMain.setTitleAt(3, LocaleBundle.getString("CCLog.Undefinieds")  + " (" + CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED)   + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tpnlMain.setTitleAt(4, LocaleBundle.getString("CCLog.SQL")          + " (" + CCLog.getSQLCount()                            + ")"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}

	private void showMoreErrors() {
		if (!memoErrors.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoErrors.getText(), false); //$NON-NLS-1$
	}

	private void showMoreWarnings() {
		if (!memoWarnings.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoWarnings.getText(), false); //$NON-NLS-1$
	}

	private void showMoreInformations() {
		if (!memoInformations.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoInformations.getText(), false); //$NON-NLS-1$
	}

	private void showMoreUndefinieds() {
		if (!memoUndefinied.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoUndefinied.getText(), false); //$NON-NLS-1$
	}

	private void showMoreSQL() {
		if (!memoSQL.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoSQL.getText(), false); //$NON-NLS-1$
	}

	private void showCacheDistribution() {
		var ml = CCMovieList.getInstance();

		var caches = new ArrayList<ICalculationCache>();
		caches.add(ml.getCache());
		for (var v: ml.iteratorElements()) caches.add(v.getCache());
		for (var v: ml.iteratorSeasons())  caches.add(v.getCache());
		for (var v: ml.iteratorEpisodes()) caches.add(v.getCache());

		var counter = new HashMap<String, Integer>();

		for (var c: caches) for (var ck: c.listCachedKeys()) counter.compute(ck, (k,v) -> v==null ? 1 : v+1);

		var displ = CCStreams
				.iterate(counter)
				.autosortByProperty(p -> -p.getValue())
				.map(p -> StringUtils.leftPad(String.valueOf(p.getValue()), 6) + "  " + p.getKey())
				.stringjoin(p->p, "\n");

		GenericTextDialog.showText(LogFrame.this, "CalculationCache", displ, false); //$NON-NLS-1$

	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		tpnlMain = new JTabbedPane();
		tabErrors = new JPanel();
		scrollPane1 = new JScrollPane();
		lsErrors = new JList<>();
		scrollPane2 = new JScrollPane();
		memoErrors = new JTextArea();
		button1 = new JButton();
		tabWarnings = new JPanel();
		scrollPane3 = new JScrollPane();
		lsWarnings = new JList<>();
		scrollPane7 = new JScrollPane();
		memoWarnings = new JTextArea();
		button2 = new JButton();
		tabInformations = new JPanel();
		scrollPane4 = new JScrollPane();
		lsInformations = new JList<>();
		scrollPane8 = new JScrollPane();
		memoInformations = new JTextArea();
		button3 = new JButton();
		tabUndefinied = new JPanel();
		scrollPane5 = new JScrollPane();
		lsUndefinied = new JList<>();
		scrollPane9 = new JScrollPane();
		memoUndefinied = new JTextArea();
		button4 = new JButton();
		tabSQL = new JPanel();
		scrollPane6 = new JScrollPane();
		lsSQL = new JList<>();
		scrollPane10 = new JScrollPane();
		memoSQL = new JTextArea();
		button5 = new JButton();
		tabLiveDisplay = new JPanel();
		label8 = new JLabel();
		displUptime = new ReadableTextField();
		label10 = new JLabel();
		displCacheTotalCount = new ReadableTextField();
		button6 = new JButton();
		label11 = new JLabel();
		displPreInitCacheQueries = new ReadableTextField();
		label1 = new JLabel();
		displCacheHits = new ReadableTextField();
		label2 = new JLabel();
		displCacheMisses = new ReadableTextField();
		label9 = new JLabel();
		displCacheInvalidations = new ReadableTextField();
		label3 = new JLabel();
		displWarningsCount = new ReadableTextField();
		label4 = new JLabel();
		displErrorCount = new ReadableTextField();
		label5 = new JLabel();
		displUndefiniedCount = new ReadableTextField();
		label6 = new JLabel();
		displQueryCount = new ReadableTextField();
		label7 = new JLabel();
		displRequestCount = new ReadableTextField();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(LocaleBundle.getString("CCLogFrame.this.title")); //$NON-NLS-1$
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, default:grow, $rgap", //$NON-NLS-1$
			"$rgap, default:grow, $rgap")); //$NON-NLS-1$

		//======== tpnlMain ========
		{

			//======== tabErrors ========
			{
				tabErrors.setLayout(new FormLayout(
					"$rgap, 275dlu, $lcgap, 0dlu:grow, $rgap", //$NON-NLS-1$
					"$rgap, default:grow, $lgap, default, $rgap")); //$NON-NLS-1$

				//======== scrollPane1 ========
				{

					//---- lsErrors ----
					lsErrors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane1.setViewportView(lsErrors);
				}
				tabErrors.add(scrollPane1, CC.xy(2, 2, CC.FILL, CC.FILL));

				//======== scrollPane2 ========
				{

					//---- memoErrors ----
					memoErrors.setForeground(new Color(0, 224, 0));
					memoErrors.setBackground(new Color(32, 32, 32));
					memoErrors.setEditable(false);
					scrollPane2.setViewportView(memoErrors);
				}
				tabErrors.add(scrollPane2, CC.xy(4, 2, CC.FILL, CC.FILL));

				//---- button1 ----
				button1.setText("..."); //$NON-NLS-1$
				button1.addActionListener(e -> showMoreErrors());
				tabErrors.add(button1, CC.xy(4, 4, CC.RIGHT, CC.DEFAULT));
			}
			tpnlMain.addTab(LocaleBundle.getString("CCLog.Errors"), tabErrors); //$NON-NLS-1$

			//======== tabWarnings ========
			{
				tabWarnings.setLayout(new FormLayout(
					"$rgap, 275dlu, $lcgap, 0dlu:grow, $rgap", //$NON-NLS-1$
					"$rgap, default:grow, $lgap, default, $rgap")); //$NON-NLS-1$

				//======== scrollPane3 ========
				{

					//---- lsWarnings ----
					lsWarnings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane3.setViewportView(lsWarnings);
				}
				tabWarnings.add(scrollPane3, CC.xy(2, 2, CC.FILL, CC.FILL));

				//======== scrollPane7 ========
				{

					//---- memoWarnings ----
					memoWarnings.setForeground(new Color(0, 224, 0));
					memoWarnings.setBackground(new Color(32, 32, 32));
					memoWarnings.setEditable(false);
					scrollPane7.setViewportView(memoWarnings);
				}
				tabWarnings.add(scrollPane7, CC.xy(4, 2, CC.FILL, CC.FILL));

				//---- button2 ----
				button2.setText("..."); //$NON-NLS-1$
				button2.addActionListener(e -> showMoreWarnings());
				tabWarnings.add(button2, CC.xy(4, 4, CC.RIGHT, CC.DEFAULT));
			}
			tpnlMain.addTab(LocaleBundle.getString("CCLog.Warnings"), tabWarnings); //$NON-NLS-1$

			//======== tabInformations ========
			{
				tabInformations.setLayout(new FormLayout(
					"$rgap, 275dlu, $lcgap, 0dlu:grow, $rgap", //$NON-NLS-1$
					"$rgap, default:grow, $lgap, default, $rgap")); //$NON-NLS-1$

				//======== scrollPane4 ========
				{

					//---- lsInformations ----
					lsInformations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane4.setViewportView(lsInformations);
				}
				tabInformations.add(scrollPane4, CC.xy(2, 2, CC.FILL, CC.FILL));

				//======== scrollPane8 ========
				{

					//---- memoInformations ----
					memoInformations.setForeground(new Color(0, 224, 0));
					memoInformations.setBackground(new Color(32, 32, 32));
					memoInformations.setEditable(false);
					scrollPane8.setViewportView(memoInformations);
				}
				tabInformations.add(scrollPane8, CC.xy(4, 2, CC.FILL, CC.FILL));

				//---- button3 ----
				button3.setText("..."); //$NON-NLS-1$
				button3.addActionListener(e -> showMoreInformations());
				tabInformations.add(button3, CC.xy(4, 4, CC.RIGHT, CC.DEFAULT));
			}
			tpnlMain.addTab(LocaleBundle.getString("CCLog.Informations"), tabInformations); //$NON-NLS-1$

			//======== tabUndefinied ========
			{
				tabUndefinied.setLayout(new FormLayout(
					"$rgap, 275dlu, $lcgap, 0dlu:grow, $rgap", //$NON-NLS-1$
					"$rgap, default:grow, $lgap, default, $rgap")); //$NON-NLS-1$

				//======== scrollPane5 ========
				{

					//---- lsUndefinied ----
					lsUndefinied.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane5.setViewportView(lsUndefinied);
				}
				tabUndefinied.add(scrollPane5, CC.xy(2, 2, CC.FILL, CC.FILL));

				//======== scrollPane9 ========
				{

					//---- memoUndefinied ----
					memoUndefinied.setForeground(new Color(0, 224, 0));
					memoUndefinied.setBackground(new Color(32, 32, 32));
					memoUndefinied.setEditable(false);
					scrollPane9.setViewportView(memoUndefinied);
				}
				tabUndefinied.add(scrollPane9, CC.xy(4, 2, CC.FILL, CC.FILL));

				//---- button4 ----
				button4.setText("..."); //$NON-NLS-1$
				button4.addActionListener(e -> showMoreUndefinieds());
				tabUndefinied.add(button4, CC.xy(4, 4, CC.RIGHT, CC.DEFAULT));
			}
			tpnlMain.addTab(LocaleBundle.getString("CCLog.Undefinieds"), tabUndefinied); //$NON-NLS-1$

			//======== tabSQL ========
			{
				tabSQL.setLayout(new FormLayout(
					"$rgap, 275dlu, $lcgap, 0dlu:grow, $rgap", //$NON-NLS-1$
					"$rgap, default:grow, $lgap, default, $rgap")); //$NON-NLS-1$

				//======== scrollPane6 ========
				{

					//---- lsSQL ----
					lsSQL.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					scrollPane6.setViewportView(lsSQL);
				}
				tabSQL.add(scrollPane6, CC.xy(2, 2, CC.FILL, CC.FILL));

				//======== scrollPane10 ========
				{

					//---- memoSQL ----
					memoSQL.setForeground(new Color(0, 224, 0));
					memoSQL.setBackground(new Color(32, 32, 32));
					memoSQL.setEditable(false);
					scrollPane10.setViewportView(memoSQL);
				}
				tabSQL.add(scrollPane10, CC.xy(4, 2, CC.FILL, CC.FILL));

				//---- button5 ----
				button5.setText("..."); //$NON-NLS-1$
				button5.addActionListener(e -> showMoreSQL());
				tabSQL.add(button5, CC.xy(4, 4, CC.RIGHT, CC.DEFAULT));
			}
			tpnlMain.addTab(LocaleBundle.getString("CCLog.SQL"), tabSQL); //$NON-NLS-1$

			//======== tabLiveDisplay ========
			{
				tabLiveDisplay.setLayout(new FormLayout(
					"$ugap, default, $lcgap, 100dlu, $lcgap, default", //$NON-NLS-1$
					"$ugap, default, $pgap, 4*(default, $lgap), default, $pgap, 2*(default, $lgap), default, $pgap, 2*(default, $lgap), default")); //$NON-NLS-1$

				//---- label8 ----
				label8.setText(LocaleBundle.getString("LogFrame.Uptime")); //$NON-NLS-1$
				tabLiveDisplay.add(label8, CC.xy(2, 2));
				tabLiveDisplay.add(displUptime, CC.xy(4, 2));

				//---- label10 ----
				label10.setText(LocaleBundle.getString("LogFrame.CacheSize")); //$NON-NLS-1$
				tabLiveDisplay.add(label10, CC.xy(2, 4));
				tabLiveDisplay.add(displCacheTotalCount, CC.xy(4, 4, CC.DEFAULT, CC.CENTER));

				//---- button6 ----
				button6.setText("..."); //$NON-NLS-1$
				button6.addActionListener(e -> showCacheDistribution());
				tabLiveDisplay.add(button6, CC.xy(6, 4));

				//---- label11 ----
				label11.setText(LocaleBundle.getString("LogFrame.lblPreInitCacheHits")); //$NON-NLS-1$
				tabLiveDisplay.add(label11, CC.xy(2, 6));
				tabLiveDisplay.add(displPreInitCacheQueries, CC.xy(4, 6));

				//---- label1 ----
				label1.setText(LocaleBundle.getString("LogFrame.CacheHits")); //$NON-NLS-1$
				tabLiveDisplay.add(label1, CC.xy(2, 8));
				tabLiveDisplay.add(displCacheHits, CC.xy(4, 8));

				//---- label2 ----
				label2.setText(LocaleBundle.getString("LogFrame.CacheMisses")); //$NON-NLS-1$
				tabLiveDisplay.add(label2, CC.xy(2, 10));
				tabLiveDisplay.add(displCacheMisses, CC.xy(4, 10));

				//---- label9 ----
				label9.setText(LocaleBundle.getString("LogFrame.CacheInvalidations")); //$NON-NLS-1$
				tabLiveDisplay.add(label9, CC.xy(2, 12));
				tabLiveDisplay.add(displCacheInvalidations, CC.xy(4, 12));

				//---- label3 ----
				label3.setText(LocaleBundle.getString("CCLog.Warnings")); //$NON-NLS-1$
				tabLiveDisplay.add(label3, CC.xy(2, 14));
				tabLiveDisplay.add(displWarningsCount, CC.xy(4, 14));

				//---- label4 ----
				label4.setText(LocaleBundle.getString("CCLog.Errors")); //$NON-NLS-1$
				tabLiveDisplay.add(label4, CC.xy(2, 16));
				tabLiveDisplay.add(displErrorCount, CC.xy(4, 16));

				//---- label5 ----
				label5.setText(LocaleBundle.getString("CCLog.Undefinieds")); //$NON-NLS-1$
				tabLiveDisplay.add(label5, CC.xy(2, 18));
				tabLiveDisplay.add(displUndefiniedCount, CC.xy(4, 18));

				//---- label6 ----
				label6.setText(LocaleBundle.getString("LogFrame.DBQueries")); //$NON-NLS-1$
				tabLiveDisplay.add(label6, CC.xy(2, 20));
				tabLiveDisplay.add(displQueryCount, CC.xy(4, 20));

				//---- label7 ----
				label7.setText(LocaleBundle.getString("LogFrame.WebRequests")); //$NON-NLS-1$
				tabLiveDisplay.add(label7, CC.xy(2, 22));
				tabLiveDisplay.add(displRequestCount, CC.xy(4, 22));
			}
			tpnlMain.addTab(LocaleBundle.getString("LogFrame.TabLiveDisplay"), tabLiveDisplay); //$NON-NLS-1$
		}
		contentPane.add(tpnlMain, CC.xy(2, 2, CC.FILL, CC.FILL));
		setSize(1010, 385);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTabbedPane tpnlMain;
	private JPanel tabErrors;
	private JScrollPane scrollPane1;
	private JList<String> lsErrors;
	private JScrollPane scrollPane2;
	private JTextArea memoErrors;
	private JButton button1;
	private JPanel tabWarnings;
	private JScrollPane scrollPane3;
	private JList<String> lsWarnings;
	private JScrollPane scrollPane7;
	private JTextArea memoWarnings;
	private JButton button2;
	private JPanel tabInformations;
	private JScrollPane scrollPane4;
	private JList<String> lsInformations;
	private JScrollPane scrollPane8;
	private JTextArea memoInformations;
	private JButton button3;
	private JPanel tabUndefinied;
	private JScrollPane scrollPane5;
	private JList<String> lsUndefinied;
	private JScrollPane scrollPane9;
	private JTextArea memoUndefinied;
	private JButton button4;
	private JPanel tabSQL;
	private JScrollPane scrollPane6;
	private JList<String> lsSQL;
	private JScrollPane scrollPane10;
	private JTextArea memoSQL;
	private JButton button5;
	private JPanel tabLiveDisplay;
	private JLabel label8;
	private ReadableTextField displUptime;
	private JLabel label10;
	private ReadableTextField displCacheTotalCount;
	private JButton button6;
	private JLabel label11;
	private ReadableTextField displPreInitCacheQueries;
	private JLabel label1;
	private ReadableTextField displCacheHits;
	private JLabel label2;
	private ReadableTextField displCacheMisses;
	private JLabel label9;
	private ReadableTextField displCacheInvalidations;
	private JLabel label3;
	private ReadableTextField displWarningsCount;
	private JLabel label4;
	private ReadableTextField displErrorCount;
	private JLabel label5;
	private ReadableTextField displUndefiniedCount;
	private JLabel label6;
	private ReadableTextField displQueryCount;
	private JLabel label7;
	private ReadableTextField displRequestCount;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
