package de.jClipCorn.gui.frames.logFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.Globals;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.CalculationCache;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.features.log.*;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.cover.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.reftypes.ResourceRef;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class LogFrame extends JCCFrame implements CCLogChangedListener
{
	private Timer liveDisplayTimer;

	private final java.util.List<CCLogChangedListener> extraListener = new ArrayList<>();

	public LogFrame(Component owner, CCMovieList movielist)
	{
		super(movielist);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
		setMinimumSize(getSize());
	}

	private void postInit()
	{
		ccprops().PROP_FSIZE_LOGFRAME.applyOrSkip(this);

		updateTabHeader();

		var modError = new LogListModel(lsErrors, CCLogType.LOG_ELEM_ERROR, memoErrorsText, memoErrorsTrace);
		var modWarn  = new LogListModel(lsWarnings, CCLogType.LOG_ELEM_WARNING, memoWarningsText, memoWarningsTrace);
		var modInfo  = new LogListModel(lsInformations, CCLogType.LOG_ELEM_INFORMATION, memoInformationsText, memoInformationsTrace);
		var modUndef = new LogListModel(lsUndefinied, CCLogType.LOG_ELEM_UNDEFINED, memoUndefiniedText, memoUndefiniedTrace);

		lsErrors.setModel(modError);
		lsWarnings.setModel(modWarn);
		lsInformations.setModel(modInfo);
		lsUndefinied.setModel(modUndef);

		extraListener.add(modError);
		extraListener.add(modWarn);
		extraListener.add(modInfo);
		extraListener.add(modUndef);

		lsChanges.setData(CCLog.getChangeElements());
		lsChanges.autoResize();

		lsSQL.setData(CCLog.getSQLElements());
		lsSQL.autoResize();

		DatabaseElementPreviewLabel cl = MainFrame.getInstance().getCoverLabel();
		if (cl.isErrorMode()) cl.setModeDefault();

		CCLog.addChangeListener(this);

		CCLog.setAllWatched();

		liveDisplayTimer = new Timer(300, this::onTimer);
		liveDisplayTimer.setInitialDelay(1000);
		liveDisplayTimer.start();
	}

	public void selectSQLLog(CCSQLLogElement v) {
		memoSQL.setText(v.source);
		memoSQL.setCaretPosition(0); // Scroll dat bitch to top
		edQueryMethod.setText(v.method);
		edQueryType.setText(v.statementType.toString());
		edQueryStart.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date.from(Instant.ofEpochMilli(v.startMillis))));
		edQueryEnd.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date.from(Instant.ofEpochMilli(v.endMillis))));
		edQueryDuration.setText( (v.endMillis - v.startMillis < 2000) ? Str.format("{0} milliseconds", v.endMillis - v.startMillis) : Str.format("{0} seconds", (v.endMillis - v.startMillis) / 1000.0) ); //$NON-NLS-1$ //$NON-NLS-2$
		edQuerySuccess.setText(Str.coalesce(v.error));
		edQuerySuccess.setBackground((v.error == null) ? Color.GREEN : Color.RED);
		edQuerySuccess.setForeground(Color.BLACK);
	}

	private void onTimer(ActionEvent actionEvent)
	{
		displErrorCount.setText(String.valueOf(CCLog.getCount(CCLogType.LOG_ELEM_ERROR)));
		displWarningsCount.setText(String.valueOf(CCLog.getCount(CCLogType.LOG_ELEM_WARNING)));
		displUndefiniedCount.setText(String.valueOf(CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED)));

		displQueryCount.setText(String.valueOf(CCLog.getSQLCount()));
		displRequestCount.setText(String.valueOf(movielist.getWebConnection().getTotalRequestCount()));

		displUptime.setText(String.valueOf(System.currentTimeMillis() - Globals.MILLIS_MAIN));

		displCacheHits.setText(CalculationCache.formatCacheHits());
		displCacheMisses.setText(CalculationCache.formatCacheMisses());
		displCacheInvalidations.setText(String.valueOf(CalculationCache.CacheInvalidations));
		displCacheTotalCount.setText(String.valueOf(CalculationCache.CacheSizeTotal));
		displPreInitCacheQueries.setText(String.valueOf(CalculationCache.PreInitRequests));

		displCacheIcons.setText(CachedResourceLoader.getIconCacheCount() + " / " + ResourceRef.allIconResources.size());
		displCacheImages.setText(CachedResourceLoader.getImageCacheCount() + " / " + ResourceRef.allImageResources.size());

		displCacheCovers.setText(String.valueOf(movielist.getCoverCache().getCoverCount()));
	}

	@Override
	public void onChanged() {
		updateTabHeader();

		for (var lstr : extraListener) lstr.onChanged();
	}

	@Override
	public void onSQLChanged(CCSQLLogElement cle) {
		lsSQL.addData(cle);
		updateTabHeader();

		for (var lstr : extraListener) lstr.onSQLChanged(cle);
	}

	@Override
	public void onPropsChanged(CCChangeLogElement cle) {
		lsChanges.addData(cle);
		updateTabHeader();

		for (var lstr : extraListener) lstr.onPropsChanged(cle);
	}

	private void updateTabHeader() {
		tpnlMain.setTitleAt(0, LocaleBundle.getString("CCLog.Error")        + " (" + CCLog.getCount(CCLogType.LOG_ELEM_ERROR)       + ")");
		tpnlMain.setTitleAt(1, LocaleBundle.getString("CCLog.Warnings")     + " (" + CCLog.getCount(CCLogType.LOG_ELEM_WARNING)     + ")");
		tpnlMain.setTitleAt(2, LocaleBundle.getString("CCLog.Informations") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_INFORMATION) + ")");
		tpnlMain.setTitleAt(3, LocaleBundle.getString("CCLog.Undefinieds")  + " (" + CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED)   + ")");
		tpnlMain.setTitleAt(4, LocaleBundle.getString("CCLog.SQL")          + " (" + CCLog.getSQLCount()                            + ")");
		tpnlMain.setTitleAt(5, LocaleBundle.getString("CCLog.Changes")      + " (" + CCLog.getChangeCount()                         + ")");
	}

	private void showMoreErrorsText() {
		if (!memoErrorsText.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoErrorsText.getText(), false); //$NON-NLS-1$
	}

	private void showMoreErrorsTrace() {
		if (!memoErrorsTrace.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoErrorsTrace.getText(), false); //$NON-NLS-1$
	}

	private void showMoreWarningsText() {
		if (!memoWarningsText.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoWarningsText.getText(), false); //$NON-NLS-1$
	}

	private void showMoreWarningsTrace() {
		if (!memoWarningsTrace.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoWarningsTrace.getText(), false); //$NON-NLS-1$
	}

	private void showMoreInformationsText() {
		if (!memoInformationsText.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoInformationsText.getText(), false); //$NON-NLS-1$
	}

	private void showMoreInformationsTrace() {
		if (!memoInformationsTrace.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoInformationsTrace.getText(), false); //$NON-NLS-1$
	}

	private void showMoreUndefiniedsText() {
		if (!memoUndefiniedText.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoUndefiniedText.getText(), false); //$NON-NLS-1$
	}

	private void showMoreUndefiniedsTrace() {
		if (!memoUndefiniedTrace.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoUndefiniedTrace.getText(), false); //$NON-NLS-1$
	}

	private void showMoreSQL() {
		if (!memoSQL.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoSQL.getText(), false); //$NON-NLS-1$
	}

	private void showCacheDistribution() {
		var caches = new ArrayList<ICalculationCache>();
		caches.add(movielist.getCache());
		for (var v: movielist.iteratorElements()) caches.add(v.getCache());
		for (var v: movielist.iteratorSeasons())  caches.add(v.getCache());
		for (var v: movielist.iteratorEpisodes()) caches.add(v.getCache());

		var counter = new HashMap<String, Integer>();

		for (var c: caches) for (var ck: c.listCachedKeys()) counter.compute(ck, (k,v) -> v==null ? 1 : v+1);

		var displ = CCStreams
				.iterate(counter)
				.autosortByProperty(p -> -p.getValue())
				.map(p -> StringUtils.leftPad(String.valueOf(p.getValue()), 6) + "  " + p.getKey())
				.stringjoin(p->p, "\n");

		GenericTextDialog.showText(LogFrame.this, "CalculationCache", displ, false); //$NON-NLS-1$

	}

	private void onClosed() {
		liveDisplayTimer.stop();
		CCLog.removeChangeListener(this);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tpnlMain = new JTabbedPane();
        tabErrors = new JPanel();
        scrollPane1 = new JScrollPane();
        lsErrors = new JList<>();
        panel1 = new JSplitPane();
        scrollPane6 = new JScrollPane();
        scrollPane12 = new JScrollPane();
        memoErrorsText = new JTextArea();
        scrollPane2 = new JScrollPane();
        memoErrorsTrace = new JTextArea();
        panel5 = new JPanel();
        button1 = new JButton();
        hSpacer1 = new JPanel(null);
        button7 = new JButton();
        tabWarnings = new JPanel();
        scrollPane3 = new JScrollPane();
        lsWarnings = new JList<>();
        panel2 = new JSplitPane();
        scrollPane7 = new JScrollPane();
        memoWarningsText = new JTextArea();
        scrollPane13 = new JScrollPane();
        scrollPane14 = new JScrollPane();
        memoWarningsTrace = new JTextArea();
        panel6 = new JPanel();
        button2 = new JButton();
        hSpacer2 = new JPanel(null);
        button10 = new JButton();
        tabInformations = new JPanel();
        scrollPane4 = new JScrollPane();
        lsInformations = new JList<>();
        panel3 = new JSplitPane();
        scrollPane8 = new JScrollPane();
        memoInformationsText = new JTextArea();
        scrollPane15 = new JScrollPane();
        scrollPane16 = new JScrollPane();
        memoInformationsTrace = new JTextArea();
        panel7 = new JPanel();
        button3 = new JButton();
        hSpacer3 = new JPanel(null);
        button9 = new JButton();
        tabUndefinied = new JPanel();
        scrollPane5 = new JScrollPane();
        lsUndefinied = new JList<>();
        panel4 = new JSplitPane();
        scrollPane9 = new JScrollPane();
        memoUndefiniedText = new JTextArea();
        scrollPane17 = new JScrollPane();
        scrollPane18 = new JScrollPane();
        memoUndefiniedTrace = new JTextArea();
        panel8 = new JPanel();
        button4 = new JButton();
        hSpacer4 = new JPanel(null);
        button8 = new JButton();
        tabSQL = new JPanel();
        lsSQL = new LogSQLTable(this, movielist);
        label15 = new JLabel();
        edQueryMethod = new ReadableTextField();
        label16 = new JLabel();
        edQueryType = new ReadableTextField();
        label17 = new JLabel();
        edQueryStart = new ReadableTextField();
        label18 = new JLabel();
        edQueryEnd = new ReadableTextField();
        label20 = new JLabel();
        edQueryDuration = new ReadableTextField();
        label19 = new JLabel();
        edQuerySuccess = new ReadableTextField();
        scrollPane10 = new JScrollPane();
        memoSQL = new JTextArea();
        button5 = new JButton();
        tabChanges = new JPanel();
        lsChanges = new LogChangesTable(this, movielist);
        tabLiveDisplay = new JPanel();
        scrollPane11 = new JScrollPane();
        pnLiveDisplay = new JPanel();
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
        label12 = new JLabel();
        displCacheIcons = new ReadableTextField();
        label14 = new JLabel();
        displCacheImages = new ReadableTextField();
        label13 = new JLabel();
        displCacheCovers = new ReadableTextField();
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
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                onClosed();
            }
        });
        var contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$rgap, default:grow, $rgap", //$NON-NLS-1$
            "$rgap, 0dlu:grow, $rgap")); //$NON-NLS-1$

        //======== tpnlMain ========
        {

            //======== tabErrors ========
            {
                tabErrors.setLayout(new FormLayout(
                    "$rgap, 275dlu, $lcgap, 0dlu:grow, $rgap", //$NON-NLS-1$
                    "$rgap, 0dlu:grow, $lgap, default, $rgap")); //$NON-NLS-1$

                //======== scrollPane1 ========
                {

                    //---- lsErrors ----
                    lsErrors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    scrollPane1.setViewportView(lsErrors);
                }
                tabErrors.add(scrollPane1, CC.xy(2, 2, CC.FILL, CC.FILL));

                //======== panel1 ========
                {
                    panel1.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    panel1.setResizeWeight(0.5);

                    //======== scrollPane6 ========
                    {

                        //======== scrollPane12 ========
                        {

                            //---- memoErrorsText ----
                            memoErrorsText.setForeground(new Color(0x00e000));
                            memoErrorsText.setBackground(new Color(0x202020));
                            memoErrorsText.setEditable(false);
                            scrollPane12.setViewportView(memoErrorsText);
                        }
                        scrollPane6.setViewportView(scrollPane12);
                    }
                    panel1.setTopComponent(scrollPane6);

                    //======== scrollPane2 ========
                    {

                        //---- memoErrorsTrace ----
                        memoErrorsTrace.setForeground(new Color(0x00e000));
                        memoErrorsTrace.setBackground(new Color(0x202020));
                        memoErrorsTrace.setEditable(false);
                        scrollPane2.setViewportView(memoErrorsTrace);
                    }
                    panel1.setBottomComponent(scrollPane2);
                }
                tabErrors.add(panel1, CC.xy(4, 2, CC.DEFAULT, CC.FILL));

                //======== panel5 ========
                {
                    panel5.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

                    //---- button1 ----
                    button1.setText("..."); //$NON-NLS-1$
                    button1.addActionListener(e -> showMoreErrorsText());
                    panel5.add(button1);
                    panel5.add(hSpacer1);

                    //---- button7 ----
                    button7.setText("..."); //$NON-NLS-1$
                    button7.addActionListener(e -> showMoreErrorsTrace());
                    panel5.add(button7);
                }
                tabErrors.add(panel5, CC.xy(4, 4));
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

                //======== panel2 ========
                {
                    panel2.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    panel2.setResizeWeight(0.5);

                    //======== scrollPane7 ========
                    {

                        //---- memoWarningsText ----
                        memoWarningsText.setForeground(new Color(0x00e000));
                        memoWarningsText.setBackground(new Color(0x202020));
                        memoWarningsText.setEditable(false);
                        scrollPane7.setViewportView(memoWarningsText);
                    }
                    panel2.setTopComponent(scrollPane7);

                    //======== scrollPane13 ========
                    {

                        //======== scrollPane14 ========
                        {

                            //---- memoWarningsTrace ----
                            memoWarningsTrace.setForeground(new Color(0x00e000));
                            memoWarningsTrace.setBackground(new Color(0x202020));
                            memoWarningsTrace.setEditable(false);
                            scrollPane14.setViewportView(memoWarningsTrace);
                        }
                        scrollPane13.setViewportView(scrollPane14);
                    }
                    panel2.setBottomComponent(scrollPane13);
                }
                tabWarnings.add(panel2, CC.xy(4, 2, CC.DEFAULT, CC.FILL));

                //======== panel6 ========
                {
                    panel6.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

                    //---- button2 ----
                    button2.setText("..."); //$NON-NLS-1$
                    button2.addActionListener(e -> showMoreWarningsText());
                    panel6.add(button2);
                    panel6.add(hSpacer2);

                    //---- button10 ----
                    button10.setText("..."); //$NON-NLS-1$
                    button10.addActionListener(e -> showMoreWarningsTrace());
                    panel6.add(button10);
                }
                tabWarnings.add(panel6, CC.xy(4, 4));
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

                //======== panel3 ========
                {
                    panel3.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    panel3.setResizeWeight(0.5);

                    //======== scrollPane8 ========
                    {

                        //---- memoInformationsText ----
                        memoInformationsText.setForeground(new Color(0x00e000));
                        memoInformationsText.setBackground(new Color(0x202020));
                        memoInformationsText.setEditable(false);
                        scrollPane8.setViewportView(memoInformationsText);
                    }
                    panel3.setTopComponent(scrollPane8);

                    //======== scrollPane15 ========
                    {

                        //======== scrollPane16 ========
                        {

                            //---- memoInformationsTrace ----
                            memoInformationsTrace.setForeground(new Color(0x00e000));
                            memoInformationsTrace.setBackground(new Color(0x202020));
                            memoInformationsTrace.setEditable(false);
                            scrollPane16.setViewportView(memoInformationsTrace);
                        }
                        scrollPane15.setViewportView(scrollPane16);
                    }
                    panel3.setBottomComponent(scrollPane15);
                }
                tabInformations.add(panel3, CC.xy(4, 2, CC.DEFAULT, CC.FILL));

                //======== panel7 ========
                {
                    panel7.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

                    //---- button3 ----
                    button3.setText("..."); //$NON-NLS-1$
                    button3.addActionListener(e -> showMoreInformationsText());
                    panel7.add(button3);
                    panel7.add(hSpacer3);

                    //---- button9 ----
                    button9.setText("..."); //$NON-NLS-1$
                    button9.addActionListener(e -> showMoreInformationsTrace());
                    panel7.add(button9);
                }
                tabInformations.add(panel7, CC.xy(4, 4));
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

                //======== panel4 ========
                {
                    panel4.setOrientation(JSplitPane.VERTICAL_SPLIT);
                    panel4.setResizeWeight(0.5);

                    //======== scrollPane9 ========
                    {

                        //---- memoUndefiniedText ----
                        memoUndefiniedText.setForeground(new Color(0x00e000));
                        memoUndefiniedText.setBackground(new Color(0x202020));
                        memoUndefiniedText.setEditable(false);
                        scrollPane9.setViewportView(memoUndefiniedText);
                    }
                    panel4.setTopComponent(scrollPane9);

                    //======== scrollPane17 ========
                    {

                        //======== scrollPane18 ========
                        {

                            //---- memoUndefiniedTrace ----
                            memoUndefiniedTrace.setForeground(new Color(0x00e000));
                            memoUndefiniedTrace.setBackground(new Color(0x202020));
                            memoUndefiniedTrace.setEditable(false);
                            scrollPane18.setViewportView(memoUndefiniedTrace);
                        }
                        scrollPane17.setViewportView(scrollPane18);
                    }
                    panel4.setBottomComponent(scrollPane17);
                }
                tabUndefinied.add(panel4, CC.xy(4, 2, CC.DEFAULT, CC.FILL));

                //======== panel8 ========
                {
                    panel8.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

                    //---- button4 ----
                    button4.setText("..."); //$NON-NLS-1$
                    button4.addActionListener(e -> showMoreUndefiniedsText());
                    panel8.add(button4);
                    panel8.add(hSpacer4);

                    //---- button8 ----
                    button8.setText("..."); //$NON-NLS-1$
                    button8.addActionListener(e -> showMoreUndefiniedsTrace());
                    panel8.add(button8);
                }
                tabUndefinied.add(panel8, CC.xy(4, 4));
            }
            tpnlMain.addTab(LocaleBundle.getString("CCLog.Undefinieds"), tabUndefinied); //$NON-NLS-1$

            //======== tabSQL ========
            {
                tabSQL.setLayout(new FormLayout(
                    "$rgap, 275dlu, $lcgap, pref, $lcgap, 0dlu:grow, $rgap", //$NON-NLS-1$
                    "$rgap, 6*(default, $lgap), default:grow, $lgap, default, $rgap")); //$NON-NLS-1$
                tabSQL.add(lsSQL, CC.xywh(2, 2, 1, 15));

                //---- label15 ----
                label15.setText(LocaleBundle.getString("LogFrame.lblQueryMethod")); //$NON-NLS-1$
                tabSQL.add(label15, CC.xy(4, 2));
                tabSQL.add(edQueryMethod, CC.xy(6, 2));

                //---- label16 ----
                label16.setText(LocaleBundle.getString("LogFrame.lblQueryType")); //$NON-NLS-1$
                tabSQL.add(label16, CC.xy(4, 4));
                tabSQL.add(edQueryType, CC.xy(6, 4));

                //---- label17 ----
                label17.setText(LocaleBundle.getString("LogFrame.lblQueryStart")); //$NON-NLS-1$
                tabSQL.add(label17, CC.xy(4, 6));
                tabSQL.add(edQueryStart, CC.xy(6, 6));

                //---- label18 ----
                label18.setText(LocaleBundle.getString("LogFrame.lblQueryEnd")); //$NON-NLS-1$
                tabSQL.add(label18, CC.xy(4, 8));
                tabSQL.add(edQueryEnd, CC.xy(6, 8));

                //---- label20 ----
                label20.setText(LocaleBundle.getString("LogFrame.lblQueryDuration")); //$NON-NLS-1$
                tabSQL.add(label20, CC.xy(4, 10));
                tabSQL.add(edQueryDuration, CC.xy(6, 10));

                //---- label19 ----
                label19.setText(LocaleBundle.getString("LogFrame.lblQuerySuccess")); //$NON-NLS-1$
                tabSQL.add(label19, CC.xy(4, 12));
                tabSQL.add(edQuerySuccess, CC.xy(6, 12));

                //======== scrollPane10 ========
                {

                    //---- memoSQL ----
                    memoSQL.setForeground(new Color(0x00e000));
                    memoSQL.setBackground(new Color(0x202020));
                    memoSQL.setEditable(false);
                    memoSQL.setLineWrap(true);
                    memoSQL.setWrapStyleWord(true);
                    scrollPane10.setViewportView(memoSQL);
                }
                tabSQL.add(scrollPane10, CC.xywh(4, 14, 3, 1, CC.FILL, CC.FILL));

                //---- button5 ----
                button5.setText("..."); //$NON-NLS-1$
                button5.addActionListener(e -> showMoreSQL());
                tabSQL.add(button5, CC.xywh(4, 16, 3, 1, CC.FILL, CC.DEFAULT));
            }
            tpnlMain.addTab(LocaleBundle.getString("CCLog.SQL"), tabSQL); //$NON-NLS-1$

            //======== tabChanges ========
            {
                tabChanges.setLayout(new FormLayout(
                    "default:grow", //$NON-NLS-1$
                    "default:grow")); //$NON-NLS-1$
                tabChanges.add(lsChanges, CC.xy(1, 1, CC.FILL, CC.FILL));
            }
            tpnlMain.addTab(LocaleBundle.getString("CCLog.Changes"), tabChanges); //$NON-NLS-1$

            //======== tabLiveDisplay ========
            {
                tabLiveDisplay.setLayout(new BorderLayout());

                //======== scrollPane11 ========
                {
                    scrollPane11.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                    //======== pnLiveDisplay ========
                    {
                        pnLiveDisplay.setLayout(new FormLayout(
                            "$ugap, default, $lcgap, 100dlu, $lcgap, default", //$NON-NLS-1$
                            "$ugap, default, $pgap, 4*(default, $lgap), default, $pgap, 2*(default, $lgap), default, $pgap, 2*(default, $lgap), default, $pgap, 2*(default, $lgap), default")); //$NON-NLS-1$

                        //---- label8 ----
                        label8.setText(LocaleBundle.getString("LogFrame.Uptime")); //$NON-NLS-1$
                        pnLiveDisplay.add(label8, CC.xy(2, 2));
                        pnLiveDisplay.add(displUptime, CC.xy(4, 2));

                        //---- label10 ----
                        label10.setText(LocaleBundle.getString("LogFrame.CacheSize")); //$NON-NLS-1$
                        pnLiveDisplay.add(label10, CC.xy(2, 4));
                        pnLiveDisplay.add(displCacheTotalCount, CC.xy(4, 4, CC.DEFAULT, CC.CENTER));

                        //---- button6 ----
                        button6.setText("..."); //$NON-NLS-1$
                        button6.addActionListener(e -> showCacheDistribution());
                        pnLiveDisplay.add(button6, CC.xy(6, 4));

                        //---- label11 ----
                        label11.setText(LocaleBundle.getString("LogFrame.lblPreInitCacheHits")); //$NON-NLS-1$
                        pnLiveDisplay.add(label11, CC.xy(2, 6));
                        pnLiveDisplay.add(displPreInitCacheQueries, CC.xy(4, 6));

                        //---- label1 ----
                        label1.setText(LocaleBundle.getString("LogFrame.CacheHits")); //$NON-NLS-1$
                        pnLiveDisplay.add(label1, CC.xy(2, 8));
                        pnLiveDisplay.add(displCacheHits, CC.xy(4, 8));

                        //---- label2 ----
                        label2.setText(LocaleBundle.getString("LogFrame.CacheMisses")); //$NON-NLS-1$
                        pnLiveDisplay.add(label2, CC.xy(2, 10));
                        pnLiveDisplay.add(displCacheMisses, CC.xy(4, 10));

                        //---- label9 ----
                        label9.setText(LocaleBundle.getString("LogFrame.CacheInvalidations")); //$NON-NLS-1$
                        pnLiveDisplay.add(label9, CC.xy(2, 12));
                        pnLiveDisplay.add(displCacheInvalidations, CC.xy(4, 12));

                        //---- label12 ----
                        label12.setText(LocaleBundle.getString("LogFrame.CacheIcons")); //$NON-NLS-1$
                        pnLiveDisplay.add(label12, CC.xy(2, 14));
                        pnLiveDisplay.add(displCacheIcons, CC.xy(4, 14));

                        //---- label14 ----
                        label14.setText(LocaleBundle.getString("LogFrame.CacheImages")); //$NON-NLS-1$
                        pnLiveDisplay.add(label14, CC.xy(2, 16));
                        pnLiveDisplay.add(displCacheImages, CC.xy(4, 16));

                        //---- label13 ----
                        label13.setText(LocaleBundle.getString("LogFrame.CacheCovers")); //$NON-NLS-1$
                        pnLiveDisplay.add(label13, CC.xy(2, 18));
                        pnLiveDisplay.add(displCacheCovers, CC.xy(4, 18));

                        //---- label3 ----
                        label3.setText(LocaleBundle.getString("CCLog.Warnings")); //$NON-NLS-1$
                        pnLiveDisplay.add(label3, CC.xy(2, 20));
                        pnLiveDisplay.add(displWarningsCount, CC.xy(4, 20));

                        //---- label4 ----
                        label4.setText(LocaleBundle.getString("CCLog.Errors")); //$NON-NLS-1$
                        pnLiveDisplay.add(label4, CC.xy(2, 22));
                        pnLiveDisplay.add(displErrorCount, CC.xy(4, 22));

                        //---- label5 ----
                        label5.setText(LocaleBundle.getString("CCLog.Undefinieds")); //$NON-NLS-1$
                        pnLiveDisplay.add(label5, CC.xy(2, 24));
                        pnLiveDisplay.add(displUndefiniedCount, CC.xy(4, 24));

                        //---- label6 ----
                        label6.setText(LocaleBundle.getString("LogFrame.DBQueries")); //$NON-NLS-1$
                        pnLiveDisplay.add(label6, CC.xy(2, 26));
                        pnLiveDisplay.add(displQueryCount, CC.xy(4, 26));

                        //---- label7 ----
                        label7.setText(LocaleBundle.getString("LogFrame.WebRequests")); //$NON-NLS-1$
                        pnLiveDisplay.add(label7, CC.xy(2, 28));
                        pnLiveDisplay.add(displRequestCount, CC.xy(4, 28));
                    }
                    scrollPane11.setViewportView(pnLiveDisplay);
                }
                tabLiveDisplay.add(scrollPane11, BorderLayout.CENTER);
            }
            tpnlMain.addTab(LocaleBundle.getString("LogFrame.TabLiveDisplay"), tabLiveDisplay); //$NON-NLS-1$
        }
        contentPane.add(tpnlMain, CC.xy(2, 2, CC.FILL, CC.FILL));
        setSize(1262, 875);
        setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tpnlMain;
    private JPanel tabErrors;
    private JScrollPane scrollPane1;
    private JList<String> lsErrors;
    private JSplitPane panel1;
    private JScrollPane scrollPane6;
    private JScrollPane scrollPane12;
    private JTextArea memoErrorsText;
    private JScrollPane scrollPane2;
    private JTextArea memoErrorsTrace;
    private JPanel panel5;
    private JButton button1;
    private JPanel hSpacer1;
    private JButton button7;
    private JPanel tabWarnings;
    private JScrollPane scrollPane3;
    private JList<String> lsWarnings;
    private JSplitPane panel2;
    private JScrollPane scrollPane7;
    private JTextArea memoWarningsText;
    private JScrollPane scrollPane13;
    private JScrollPane scrollPane14;
    private JTextArea memoWarningsTrace;
    private JPanel panel6;
    private JButton button2;
    private JPanel hSpacer2;
    private JButton button10;
    private JPanel tabInformations;
    private JScrollPane scrollPane4;
    private JList<String> lsInformations;
    private JSplitPane panel3;
    private JScrollPane scrollPane8;
    private JTextArea memoInformationsText;
    private JScrollPane scrollPane15;
    private JScrollPane scrollPane16;
    private JTextArea memoInformationsTrace;
    private JPanel panel7;
    private JButton button3;
    private JPanel hSpacer3;
    private JButton button9;
    private JPanel tabUndefinied;
    private JScrollPane scrollPane5;
    private JList<String> lsUndefinied;
    private JSplitPane panel4;
    private JScrollPane scrollPane9;
    private JTextArea memoUndefiniedText;
    private JScrollPane scrollPane17;
    private JScrollPane scrollPane18;
    private JTextArea memoUndefiniedTrace;
    private JPanel panel8;
    private JButton button4;
    private JPanel hSpacer4;
    private JButton button8;
    private JPanel tabSQL;
    private LogSQLTable lsSQL;
    private JLabel label15;
    private ReadableTextField edQueryMethod;
    private JLabel label16;
    private ReadableTextField edQueryType;
    private JLabel label17;
    private ReadableTextField edQueryStart;
    private JLabel label18;
    private ReadableTextField edQueryEnd;
    private JLabel label20;
    private ReadableTextField edQueryDuration;
    private JLabel label19;
    private ReadableTextField edQuerySuccess;
    private JScrollPane scrollPane10;
    private JTextArea memoSQL;
    private JButton button5;
    private JPanel tabChanges;
    private LogChangesTable lsChanges;
    private JPanel tabLiveDisplay;
    private JScrollPane scrollPane11;
    private JPanel pnLiveDisplay;
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
    private JLabel label12;
    private ReadableTextField displCacheIcons;
    private JLabel label14;
    private ReadableTextField displCacheImages;
    private JLabel label13;
    private ReadableTextField displCacheCovers;
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
