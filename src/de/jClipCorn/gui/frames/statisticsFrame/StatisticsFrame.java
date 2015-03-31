package de.jClipCorn.gui.frames.statisticsFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsAddDateChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsEpisodesViewedChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsFSKChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsFormatChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsGenreChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsHoursMovChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsHoursSerChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsHoursSerMovChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsLanguageChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsMovieLengthChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsOnlinescoreChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsQualityChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsScoreChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsSeriesTotalViewedChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsSeriesViewedChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsSizeChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsTagChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsViewedChart;
import de.jClipCorn.gui.frames.statisticsFrame.charts.StatisticsYearChart;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.TimeKeeper;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsFrame extends JFrame {
	private static final long serialVersionUID = 2443934162053374481L;

	private final CCMovieList movielist;
	private List<JLabel> sidebarValueLabels = new ArrayList<>();
	private StatisticsChart currentChart = null;
	
	private JPanel pnlTop;
	private JPanel pnlLeft;
	private JPanel pnlCenter;
	private JPanel pnlSidebar;
	private JScrollPane sclPnlLeft;
	private JComboBox<StatisticsChart> cbxChooseChart;
	private JButton btnPrevChart;
	private JLabel lblChartCaption;
	private JButton btnNxtChart;
	private ChartPanel chartPanel;
	
	public StatisticsFrame(Component owner, CCMovieList mlist) {
		super();
		this.movielist = mlist;
		
		initGUI();

		TimeKeeper.start();
		{
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
				ColumnSpec.decode("41px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("300px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("41px"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		cbxChooseChart = new JComboBox<>();
		cbxChooseChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				assignChart(cbxChooseChart.getItemAt(cbxChooseChart.getSelectedIndex()));
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
		
		pnlLeft = new JPanel(new BorderLayout());
		getContentPane().add(pnlLeft, BorderLayout.WEST);
		
		sclPnlLeft = new JScrollPane();
		sclPnlLeft.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pnlLeft.add(sclPnlLeft, BorderLayout.CENTER);
		
		pnlSidebar = getSidebarPanel();
		sclPnlLeft.setViewportView(pnlSidebar);
		
		pnlCenter = new JPanel(new BorderLayout());
		pnlCenter.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(pnlCenter, BorderLayout.CENTER);
		
		chartPanel = null;
		if (CCProperties.getInstance().PROP_STATISTICS_INTERACTIVECHARTS.getValue()) {
			chartPanel = new ChartPanel(new JFreeChart(new XYPlot()));
		} else {
			chartPanel = new FixedChartPanel(new JFreeChart(new XYPlot()));
		}
		chartPanel.setVisible(false);
		chartPanel.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {/**/}
			
			@Override
			public void componentResized(ComponentEvent e) {
				if (currentChart != null) currentChart.onResize(e);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {/**/}
			
			@Override
			public void componentHidden(ComponentEvent e) {/**/}
		});
		pnlCenter.add(chartPanel, BorderLayout.CENTER);
		
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
	
	@SuppressWarnings("nls")
	private void initSidebarValues() {
		List<JLabel> lst = sidebarValueLabels; // Locale Alias
		
		lst.get(0).setText("" + movielist.getMovieCount());
		lst.get(1).setText("" + StatisticsHelper.getViewedMovieCount(movielist));
		lst.get(2).setText("" + movielist.getSeriesCount());
		lst.get(3).setText("" + movielist.getEpisodeCount());
		lst.get(4).setText("" + StatisticsHelper.getViewedEpisodeCount(movielist));
		lst.get(5).setText("" + movielist.getElementCount());
		
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
		
		lst.get(16).setText("" + StatisticsHelper.getAvgImDbRating(movielist));
	
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
	}
	
	private void assignChart(StatisticsChart statchart) {
		currentChart = statchart;
		
		if (statchart == null) {
			chartPanel.setVisible(false);
			
			chartPanel.setChart(new JFreeChart(new XYPlot()));	
			lblChartCaption.setText(""); //$NON-NLS-1$
		} else {
			chartPanel.setVisible(true);
			
			chartPanel.setChart(statchart.getChart());	
			lblChartCaption.setText(statchart.getTitle());
		}
	}
}