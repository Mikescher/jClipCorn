package de.jClipCorn.gui.frames.statisticsFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsAddDateChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsEpisodesViewedChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsFSKChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsFormatChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsFormatPerTimeChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsGenreChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsHoursMovChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsHoursSerChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsHoursSerMovChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsLanguageChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsMovieLengthChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsOnlinescoreChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsQualityChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsScoreChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsSeriesTimeline;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsSeriesTotalViewedChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsSeriesViewedChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsSizeChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsTagChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsViewedChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsYearChart;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.TimeKeeper;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsFrame extends JFrame {
	private static final long serialVersionUID = 2443934162053374481L;

	private final CCMovieList movielist;
	private List<JLabel> sidebarValueLabels = new ArrayList<>();
	private StatisticsPanel currentChart = null;

	private int selectedYear = -1;
	private int minYear;
	private int maxYear;
	
	private JPanel pnlTop;
	private JPanel pnlLeft;
	private JPanel pnlCenter;
	private JPanel pnlSidebar;
	private JScrollPane sclPnlLeft;
	private JComboBox<StatisticsPanel> cbxChooseChart;
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
	
	public StatisticsFrame(Component owner, CCMovieList mlist) {
		super();
		this.movielist = mlist;
		
		initGUI();

		TimeKeeper.start();
		{
			selectedYear = -1;
			minYear = Math.min(StatisticsHelper.getFirstMovieAddDate(mlist).getYear(),StatisticsHelper.getFirstSeriesAddDate(mlist).getYear());
			maxYear = CCDate.getCurrentDate().getYear();
			
			initSidebarValues();
			initCharts();
		}
		long init_time = TimeKeeper.stop();
		
		if (Main.DEBUG)
			System.out.println(String.format("[DBG] Statistics initialization time: %d ms", init_time)); //$NON-NLS-1$
		
		cbxChooseChart.setSelectedIndex(-1);

		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		
		BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
		borderLayout.setVgap(5);
		borderLayout.setHgap(5);
		setTitle(LocaleBundle.getString("StatisticsFrame.this.title")); //$NON-NLS-1$
		
		pnlTop = new JPanel();
		pnlTop.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("15dlu"), //$NON-NLS-1$
				ColumnSpec.decode("200px"), //$NON-NLS-1$
				ColumnSpec.decode("50dlu"), //$NON-NLS-1$
				FormSpecs.PREF_COLSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("300px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		cbxChooseChart = new JComboBox<>();
		cbxChooseChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StatisticsPanel chart = cbxChooseChart.getItemAt(cbxChooseChart.getSelectedIndex());
				
				pnlCheckSeries.setVisible((chart != null) && chart.usesFilterableSeries());
				pnlYearRange.setVisible((chart != null) && chart.usesFilterableYearRange());
				
				assignChart(chart);
				
				if (chart != null) chart.onHideSeries(seriesList.getMap());
				if (chart != null) chart.onFilterYearRange(selectedYear);
			}
		});
		cbxChooseChart.setMaximumRowCount(24);
		pnlTop.add(cbxChooseChart, "2, 2, fill, center"); //$NON-NLS-1$
		
		btnPrevChart = new JButton("<"); //$NON-NLS-1$
		btnPrevChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cbxChooseChart.getItemCount() > 0)
					cbxChooseChart.setSelectedIndex((cbxChooseChart.getSelectedIndex() + cbxChooseChart.getItemCount() - 1) % cbxChooseChart.getItemCount());
			}
		});
		pnlTop.add(btnPrevChart, "4, 2, left, top"); //$NON-NLS-1$
		
		lblChartCaption = new JLabel();
		pnlTop.add(lblChartCaption, "6, 2, center, center"); //$NON-NLS-1$
		
		btnNxtChart = new JButton(">"); //$NON-NLS-1$
		btnNxtChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cbxChooseChart.getItemCount() > 0)
					cbxChooseChart.setSelectedIndex((cbxChooseChart.getSelectedIndex() + 1) % cbxChooseChart.getItemCount());
			}
		});
		pnlTop.add(btnNxtChart, "8, 2, left, top"); //$NON-NLS-1$
		
		pnlLeft = new JPanel();
		getContentPane().add(pnlLeft, BorderLayout.WEST);
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
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedYear == -1) selectedYear = maxYear;
				else selectedYear--;
				
				if (selectedYear < minYear) selectedYear = -1;
				
				updateYearSelection(selectedYear);
			}
		});
		pnlYearRange.add(btnNewButton, BorderLayout.WEST);
		
		lblYear = new JLabel(LocaleBundle.getString("StatisticsFrame.this.allTime")); //$NON-NLS-1$
		lblYear.setFont(new Font("Courier New", Font.BOLD, 16)); //$NON-NLS-1$
		lblYear.setHorizontalAlignment(SwingConstants.CENTER);
		pnlYearRange.add(lblYear);
		
		btnNewButton_1 = new JButton(">"); //$NON-NLS-1$
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedYear == -1) selectedYear = minYear;
				else selectedYear++;
				
				if (selectedYear > maxYear) selectedYear = -1;
				
				updateYearSelection(selectedYear);
			}
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
		for (Iterator<CCSeries> it = movielist.iteratorSeriesSorted(); it.hasNext();) {
			seriesListModel.addElement(new SeriesCheckBoxList.SeriesCheckBoxListElement(it.next()));
		}
		seriesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		seriesList.setModel(seriesListModel);
		seriesList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StatisticsPanel chart = cbxChooseChart.getItemAt(cbxChooseChart.getSelectedIndex());
				
				if (chart != null) {
					chart.onHideSeries(seriesList.getMap());
					if (chart.resetFrameOnFilter()) {
						assignChart(chart);
					}
				}
			}
		});
		
		pnlCenter = new JPanel(new BorderLayout());
		pnlCenter.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlCenter, BorderLayout.CENTER);
				
		setSize(1000, 550);
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
		lst.get(1).setText("" + StatisticsHelper.getViewedMovieCount(movielist)); //$NON-NLS-1$
		lst.get(2).setText("" + movielist.getSeriesCount()); //$NON-NLS-1$
		lst.get(3).setText("" + movielist.getEpisodeCount()); //$NON-NLS-1$
		lst.get(4).setText("" + StatisticsHelper.getViewedEpisodeCount(movielist)); //$NON-NLS-1$
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
		boolean h_mov = movielist.hasMovies();
		boolean h_ser = movielist.hasSeries();
		
		if (h_mov) cbxChooseChart.addItem(new StatisticsAddDateChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsMovieLengthChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsFormatChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsQualityChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsOnlinescoreChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsScoreChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsViewedChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsYearChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsGenreChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsFSKChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsLanguageChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsTagChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsHoursMovChart(movielist));
		if (h_ser) cbxChooseChart.addItem(new StatisticsHoursSerChart(movielist));
		if (h_ser) cbxChooseChart.addItem(new StatisticsHoursSerMovChart(movielist));
		if (h_ser) cbxChooseChart.addItem(new StatisticsEpisodesViewedChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsSizeChart(movielist));
		if (h_ser) cbxChooseChart.addItem(new StatisticsSeriesViewedChart(movielist));
		if (h_ser) cbxChooseChart.addItem(new StatisticsSeriesTotalViewedChart(movielist));
		if (h_mov) cbxChooseChart.addItem(new StatisticsFormatPerTimeChart(movielist));
		if (h_ser) cbxChooseChart.addItem(new StatisticsSeriesTimeline(movielist));
	}
	
	private void assignChart(StatisticsPanel statchart) {
		currentChart = statchart;
		
		pnlCenter.removeAll();
		pnlCenter.setVisible(false);
		if (currentChart != null) {
			JComponent comp = currentChart.getComponent();
			
			pnlCenter.add(comp, BorderLayout.CENTER);
			lblChartCaption.setText(statchart.getTitle());
		} else {
			lblChartCaption.setText(""); //$NON-NLS-1$
		}

		pnlCenter.setVisible(true);
		pnlCenter.repaint();
	}

	private void updateYearSelection(int year) {
		if (year == -1) lblYear.setText(LocaleBundle.getString("StatisticsFrame.this.allTime")); //$NON-NLS-1$
		else  lblYear.setText(Integer.toString(year));

		if (currentChart != null) currentChart.onFilterYearRange(selectedYear);
	}
}