package de.jClipCorn.gui.frames.statisticsFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.statistics.ClipCornStatistics;
import de.jClipCorn.features.statistics.StatisticsGroup;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.TimeKeeper;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.lambda.Func1to1;

public class StatisticsFrame extends JFrame {
	private static final long serialVersionUID = 2443934162053374481L;

	private final CCMovieList movielist;
	private List<JLabel> sidebarValueLabels = new ArrayList<>();
	private StatisticsGroup currentChart = null;

	private int selectedYear = -1;
	private int minYear;
	private int maxYear;
	private StatisticsTypeFilter selectedType = StatisticsTypeFilter.BOTH;

	private boolean _supressChartUpdate = false;

	private JPanel pnlTop;
	private JPanel pnlLeft;
	private JPanel pnlCenter;
	private JPanel pnlSidebar;
	private JScrollPane sclPnlLeft;
	private JComboBox<StatisticsGroup> cbxChooseChart;
	private JButton btnPrevChart;
	private JLabel lblChartCaption;
	private JButton btnNxtChart;
	private SeriesCheckBoxList seriesList;
	private JScrollPane pnlCheckSeries;
	private JPanel pnlLeftBottom;
	private JPanel pnlYearRange;
	private JButton btnNewButton;
	private JButton btnNewButton_1;
	private JLabel lblYear;
	private JToggleButton btnMovies;
	private JToggleButton btnSeries;
	private JToggleButton btnBoth;
	private JList<StatisticsGroup> lsAltLeft;
	
	public StatisticsFrame(Component owner, CCMovieList mlist) {
		super();
		this.movielist = mlist;
		
		initGUI();

		TimeKeeper.start();
		{
			selectedYear = -1;
			minYear = StatisticsHelper.getFirstAddDate(mlist.iteratorPlayables()).getYear();
			if (minYear == CCDate.YEAR_UNSPECIFIED) minYear = CCDate.getCurrentDate().getYear();
			maxYear = CCDate.getCurrentDate().getYear();
			
			initSidebarValues();
			initCharts();
		}
		long init_time = TimeKeeper.stop();
		
		if (Main.DEBUG)
			CCLog.addDebug(String.format("Statistics initialization time: %d ms", init_time)); //$NON-NLS-1$
		
		cbxChooseChart.setSelectedIndex(-1);

		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("StatisticsFrame.this.title")); //$NON-NLS-1$
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("175dlu"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("16px:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("16px:grow"),})); //$NON-NLS-1$
		
		pnlTop = new JPanel();
		pnlTop.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlTop, "1, 1, 3, 1, fill, top"); //$NON-NLS-1$
		pnlTop.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("15dlu"), //$NON-NLS-1$
				ColumnSpec.decode("250px"), //$NON-NLS-1$
				ColumnSpec.decode("5dlu:grow"), //$NON-NLS-1$
				FormSpecs.PREF_COLSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("300px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		cbxChooseChart = new JComboBox<>();
		cbxChooseChart.addActionListener(e -> onSelectChart(cbxChooseChart.getItemAt(cbxChooseChart.getSelectedIndex())));
		cbxChooseChart.setMaximumRowCount(32);
		pnlTop.add(cbxChooseChart, "2, 2, fill, center"); //$NON-NLS-1$

		lsAltLeft = new JList<>();
		lsAltLeft.addListSelectionListener(e -> ThreadUtils.delay(100, () -> onSelectChart(lsAltLeft.getModel().getElementAt(lsAltLeft.getSelectedIndex()))));
		
		btnPrevChart = new JButton("<"); //$NON-NLS-1$
		btnPrevChart.addActionListener(arg0 ->
		{
			if (cbxChooseChart.getItemCount() > 0)
				cbxChooseChart.setSelectedIndex((cbxChooseChart.getSelectedIndex() + cbxChooseChart.getItemCount() - 1) % cbxChooseChart.getItemCount());
		});
		pnlTop.add(btnPrevChart, "4, 2, left, top"); //$NON-NLS-1$
		
		lblChartCaption = new JLabel();
		pnlTop.add(lblChartCaption, "6, 2, center, center"); //$NON-NLS-1$
		
		btnNxtChart = new JButton(">"); //$NON-NLS-1$
		btnNxtChart.addActionListener(arg0 ->
		{
			if (cbxChooseChart.getItemCount() > 0)
				cbxChooseChart.setSelectedIndex((cbxChooseChart.getSelectedIndex() + 1) % cbxChooseChart.getItemCount());
		});
		pnlTop.add(btnNxtChart, "8, 2, left, top"); //$NON-NLS-1$
		
		pnlLeft = new JPanel();
		getContentPane().add(pnlLeft, "1, 3, fill, fill"); //$NON-NLS-1$
		pnlLeft.setLayout(new BorderLayout(0, 0));
		
		sclPnlLeft = new JScrollPane();
		sclPnlLeft.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pnlLeft.add(sclPnlLeft, BorderLayout.CENTER); 
		
		pnlSidebar = getSidebarPanel();
		sclPnlLeft.setViewportView(pnlSidebar);
		
		pnlLeftBottom = new JPanel();
		pnlLeft.add(pnlLeftBottom, BorderLayout.SOUTH);
		pnlLeftBottom.setLayout(new BorderLayout(0, 0));
		
		pnlYearRange = new JPanel();
		pnlYearRange.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlLeftBottom.add(pnlYearRange, BorderLayout.NORTH);
		pnlYearRange.setLayout(new BorderLayout(0, 0));
		pnlYearRange.setVisible(false);
		
		btnNewButton = new JButton("<"); //$NON-NLS-1$
		btnNewButton.addActionListener(e ->
		{
			if (selectedYear == -1) selectedYear = maxYear;
			else selectedYear--;

			if (selectedYear < minYear) selectedYear = -1;

			updateYearSelection(selectedYear);
		});
		pnlYearRange.add(btnNewButton, BorderLayout.WEST);
		
		lblYear = new JLabel(LocaleBundle.getString("StatisticsFrame.this.allTime")); //$NON-NLS-1$
		lblYear.setFont(new Font("Courier New", Font.BOLD, 16)); //$NON-NLS-1$
		lblYear.setHorizontalAlignment(SwingConstants.CENTER);
		pnlYearRange.add(lblYear);
		
		btnNewButton_1 = new JButton(">"); //$NON-NLS-1$
		btnNewButton_1.addActionListener(e ->
		{
			if (selectedYear == -1) selectedYear = minYear;
			else selectedYear++;

			if (selectedYear > maxYear) selectedYear = -1;

			updateYearSelection(selectedYear);
		});
		pnlYearRange.add(btnNewButton_1, BorderLayout.EAST);
		
		pnlCheckSeries = new JScrollPane();
		pnlLeftBottom.add(pnlCheckSeries);
		pnlCheckSeries.setVisible(false);
		pnlCheckSeries.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		seriesList = new SeriesCheckBoxList();
		seriesList.setVisibleRowCount(16);
		pnlCheckSeries.setViewportView(seriesList);
		DefaultListModel<SeriesCheckBoxList.SeriesCheckBoxListElement> seriesListModel = new DefaultListModel<>();
		seriesListModel.addElement(new SeriesCheckBoxList.SeriesCheckBoxListElement());
		for (CCSeries ser : movielist.iteratorSeriesSorted()) {
			seriesListModel.addElement(new SeriesCheckBoxList.SeriesCheckBoxListElement(ser));
		}
		seriesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		seriesList.setModel(seriesListModel);
		seriesList.addActionListener(e ->
		{
			StatisticsGroup chart = cbxChooseChart.getItemAt(cbxChooseChart.getSelectedIndex());

			if (chart != null) {
				chart.onChangeFilter(seriesList.getMap());
				if (chart.resetFrameOnFilter()) updateStatGroup(currentChart);
			}
		});
		
		pnlCenter = new JPanel(new BorderLayout());
		pnlCenter.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlCenter, "3, 3, fill, fill"); //$NON-NLS-1$
		
		btnMovies = new JToggleButton(LocaleBundle.getString("StatisticsFrame.this.toggleMovies")); //$NON-NLS-1$
		btnMovies.addActionListener(e -> setTypeFilter(StatisticsTypeFilter.MOVIES));
		pnlTop.add(btnMovies, "10, 2"); //$NON-NLS-1$
		
		btnSeries = new JToggleButton(LocaleBundle.getString("StatisticsFrame.this.toggleSeries")); //$NON-NLS-1$
		btnSeries.addActionListener(e -> setTypeFilter(StatisticsTypeFilter.SERIES));
		pnlTop.add(btnSeries, "11, 2"); //$NON-NLS-1$
		
		btnBoth = new JToggleButton(LocaleBundle.getString("StatisticsFrame.this.toggleBoth")); //$NON-NLS-1$
		btnBoth.addActionListener(e -> setTypeFilter(StatisticsTypeFilter.BOTH));
		btnBoth.setSelected(true);
		pnlTop.add(btnBoth, "12, 2"); //$NON-NLS-1$
				
		setPreferredSize(new Dimension(1000, 550));
		setMinimumSize(new Dimension(925, 485));
		setSize(1000, 550);
	}

	private void setTypeFilter(StatisticsTypeFilter tf) {
		setTypeFilter(tf, true);
	}
	
	private void setTypeFilter(StatisticsTypeFilter tf, boolean triggerUpdate) {
		boolean changed = (selectedType != tf);
		
		selectedType = tf;

		btnMovies.setSelected(tf == StatisticsTypeFilter.MOVIES);
		btnSeries.setSelected(tf == StatisticsTypeFilter.SERIES);
		btnBoth.setSelected(tf == StatisticsTypeFilter.BOTH);
		
		if (triggerUpdate && changed) {
			updateStatGroup(currentChart);
		}
	}
	
	private JPanel getSidebarPanel() {
		StatisticsSidebarFactory factory = new StatisticsSidebarFactory();
		
		String placeholder = "000000000000"; //$NON-NLS-1$
		
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.Movies", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.ViewedMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.Series", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.Episodes", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.ViewedSer", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.Elements", placeholder)); //$NON-NLS-1$
		
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.DurationMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.DurationSer", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.DurationSerMov", placeholder)); //$NON-NLS-1$
		
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.SizeMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.SizeSer", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.SizeSerMov", placeholder)); //$NON-NLS-1$
		
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.AvgLengthMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.AvgLengthSer", placeholder)); //$NON-NLS-1$
		
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.AvgSizeMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.AvgSizeSer", placeholder)); //$NON-NLS-1$
		
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.AvgImDb", placeholder)); //$NON-NLS-1$
		
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.TimeMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.TimeSer", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(factory.addRow("StatisticsFrame.Sidebar.TimeSerMov", placeholder)); //$NON-NLS-1$
		
		return factory.getPanel();
	}
	
	private void initSidebarValues() {
		List<JLabel> lst = sidebarValueLabels; // Locale Alias
		
		lst.get(0).setText("" + movielist.getMovieCount()); //$NON-NLS-1$
		lst.get(1).setText("" + StatisticsHelper.getViewedCount(movielist.iteratorMovies().cast())); //$NON-NLS-1$
		lst.get(2).setText("" + movielist.getSeriesCount()); //$NON-NLS-1$
		lst.get(3).setText("" + movielist.getEpisodeCount()); //$NON-NLS-1$
		lst.get(4).setText("" + StatisticsHelper.getViewedCount(movielist.iteratorEpisodes().cast())); //$NON-NLS-1$
		lst.get(5).setText("" + movielist.getElementCount()); //$NON-NLS-1$
		
		lst.get(6).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getMovieDuration(movielist)));
		lst.get(7).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getSeriesDuration(movielist)));
		lst.get(8).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getTotalDuration(movielist)));
		
		lst.get(9).setText(StatisticsHelper.getMovieSize(movielist).getFormatted());
		lst.get(10).setText(StatisticsHelper.getSeriesSize(movielist).getFormatted());
		lst.get(11).setText(StatisticsHelper.getTotalSize(movielist).getFormatted());
		
		lst.get(12).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.failProofDiv(StatisticsHelper.getMovieDuration(movielist), movielist.getMovieCount())));
		lst.get(13).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.failProofDiv(StatisticsHelper.getSeriesDuration(movielist), movielist.getEpisodeCount())));
	
		lst.get(14).setText(StatisticsHelper.getAvgMovieSize(movielist).getFormatted());
		lst.get(15).setText(StatisticsHelper.getAvgSeriesSize(movielist).getFormatted());
		
		lst.get(16).setText("" + StatisticsHelper.getAvgImDbRating(movielist)); //$NON-NLS-1$
	
		lst.get(17).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getViewedMovieDuration(movielist)));
		lst.get(18).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getViewedSeriesDuration(movielist)));
		lst.get(19).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getViewedTotalDuration(movielist)));
	}
	
	private void initCharts() {
		DefaultListModel<StatisticsGroup> dlm = new DefaultListModel<>();
		for (Func1to1<CCMovieList, StatisticsGroup> supplier : ClipCornStatistics.STATISTICS) {
			StatisticsGroup g = supplier.invoke(movielist);
			cbxChooseChart.addItem(g);
			dlm.addElement(g);
		}
		lsAltLeft.setModel(dlm);
	}

	private void onSelectChart(StatisticsGroup chart) {
		if (_supressChartUpdate) return;

		_supressChartUpdate = true;
		{
			if (cbxChooseChart.getSelectedItem() != chart) cbxChooseChart.setSelectedItem(chart);
			if (lsAltLeft.getSelectedValue() != chart) lsAltLeft.setSelectedValue(chart, true);
		}
		_supressChartUpdate = false;

		pnlCheckSeries.setVisible((chart != null) && chart.usesFilterableSeries());
		pnlYearRange.setVisible((chart != null) && chart.usesFilterableYearRange());

		updateStatGroup(chart);
	}

	private void updateStatGroup(StatisticsGroup statchart) {
		currentChart = statchart;
		
		pnlCenter.removeAll();
		pnlCenter.setVisible(false);
		if (currentChart != null) {
			if (currentChart.supportedTypes() == StatisticsTypeFilter.MOVIES) setTypeFilter(StatisticsTypeFilter.MOVIES, false);
			if (currentChart.supportedTypes() == StatisticsTypeFilter.SERIES) setTypeFilter(StatisticsTypeFilter.SERIES, false);

			btnMovies.setEnabled(currentChart.supportedTypes().containsMovies());
			btnSeries.setEnabled(currentChart.supportedTypes().containsSeries());
			btnBoth.setEnabled(currentChart.supportedTypes().containsBoth());
			
			JComponent comp = currentChart.getComponent(selectedType);
			
			pnlCenter.add(comp, BorderLayout.CENTER);
			lblChartCaption.setText(statchart.getTitle());
			btnSeries.setText(statchart.getCaptionToggle2());

			currentChart.onChangeFilter(seriesList.getMap());
			currentChart.onFilterYearRange(selectedYear);

			if (sclPnlLeft.getViewport().getView() != lsAltLeft) sclPnlLeft.setViewportView(lsAltLeft);

			pnlCenter.validate();

			new Thread(() -> { ThreadUtils.safeSleep(1); SwingUtils.invokeLater(() -> { if (currentChart!=null) currentChart.onShow(); }); }).start();
		} else {
			lblChartCaption.setText(""); //$NON-NLS-1$

			btnMovies.setEnabled(false);
			btnSeries.setEnabled(false);
			btnBoth.setEnabled(false);

			sclPnlLeft.setViewportView(pnlSidebar);
		}

		pnlCenter.setVisible(true);
		pnlCenter.repaint();
	}

	private void updateYearSelection(int year) {
		if (year == -1) lblYear.setText(LocaleBundle.getString("StatisticsFrame.this.allTime")); //$NON-NLS-1$
		else  lblYear.setText(Integer.toString(year));

		if (currentChart != null) currentChart.onFilterYearRange(selectedYear);
		if (currentChart != null && currentChart.resetFrameOnYearRange()) updateStatGroup(currentChart);
	}

	public void switchTo(Class<? extends StatisticsPanel> pnlClass) {
		for (int i = 0; i < cbxChooseChart.getItemCount(); i++) {
			if (cbxChooseChart.getModel().getElementAt(i).getTemplate().getClass() == pnlClass) {
				cbxChooseChart.setSelectedIndex(i);
				return;
			}
		}
	}
}