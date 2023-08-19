package de.jClipCorn.gui.frames.statisticsFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.statistics.ClipCornStatistics;
import de.jClipCorn.features.statistics.StatisticsGroup;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.TimeKeeper;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.lambda.Func1to1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class StatisticsFrame extends JCCFrame
{
	private StatisticsGroup currentChart = null;

	private int selectedYear = -1;
	private int minYear;
	private int maxYear;
	private StatisticsTypeFilter selectedType = StatisticsTypeFilter.STF_MOVIES;

	private boolean _supressChartUpdate = false;

	public StatisticsFrame(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		DefaultListModel<SeriesCheckBoxList.SeriesCheckBoxListElement> seriesListModel = new DefaultListModel<>();
		seriesListModel.addElement(new SeriesCheckBoxList.SeriesCheckBoxListElement());
		for (CCSeries ser : movielist.iteratorSeriesSorted()) {
			seriesListModel.addElement(new SeriesCheckBoxList.SeriesCheckBoxListElement(ser));
		}
		seriesList.setModel(seriesListModel);

		pnlYearRange.setVisible(false);
		pnlCheckSeries.setVisible(false);

		pnlLeftTop.removeAll();
		pnlLeftTop.setLayout(new BorderLayout());
		pnlLeftTop.add(scrlFilterListLeft, BorderLayout.CENTER);

		TimeKeeper.start();
		{
			selectedYear = -1;
			minYear = StatisticsHelper.getFirstAddDate(movielist.iteratorPlayables()).getYear();
			if (minYear == CCDate.YEAR_UNSPECIFIED) minYear = CCDate.getCurrentDate().getYear();
			maxYear = CCDate.getCurrentDate().getYear();

			pnlSidebarValues.init(movielist);
			initCharts();
		}
		long init_time = TimeKeeper.stop();

		if (Main.DEBUG)
			CCLog.addDebug(String.format("Statistics initialization time: %d ms", init_time)); //$NON-NLS-1$

		cbxChooseChart.setSelectedIndex(-1);

	}

	public void switchTo(Class<? extends StatisticsPanel> pnlClass) {
		for (int i = 0; i < cbxChooseChart.getItemCount(); i++) {
			if (cbxChooseChart.getModel().getElementAt(i).getTemplate().getClass() == pnlClass) {
				cbxChooseChart.setSelectedIndex(i);
				return;
			}
		}
	}

	private void onSelectChart(ActionEvent e) {
		selectChart(cbxChooseChart.getItemAt(cbxChooseChart.getSelectedIndex()));
	}

	private void selectChart(StatisticsGroup chart) {
		if (_supressChartUpdate) return;

		_supressChartUpdate = true;
		{
			if (cbxChooseChart.getSelectedItem() != chart) cbxChooseChart.setSelectedItem(chart);
			if (lsFilterListLeft.getSelectedValue() != chart) lsFilterListLeft.setSelectedValue(chart, true);
		}
		_supressChartUpdate = false;

		pnlCheckSeries.setVisible((chart != null) && chart.usesFilterableSeries());
		pnlYearRange.setVisible((chart != null) && chart.usesFilterableYearRange());

		updateStatGroup(chart);
	}

	private void onPrevChart() {
		if (cbxChooseChart.getItemCount() > 0)
			cbxChooseChart.setSelectedIndex((cbxChooseChart.getSelectedIndex() + cbxChooseChart.getItemCount() - 1) % cbxChooseChart.getItemCount());
	}

	private void onNextChart() {
		if (cbxChooseChart.getItemCount() > 0)
			cbxChooseChart.setSelectedIndex((cbxChooseChart.getSelectedIndex() + 1) % cbxChooseChart.getItemCount());
	}

	private void onPrevYear() {
		if (selectedYear == -1) selectedYear = maxYear;
		else selectedYear--;

		if (selectedYear < minYear) selectedYear = -1;

		updateYearSelection(selectedYear);
	}

	private void onNextYear() {
		if (selectedYear == -1) selectedYear = minYear;
		else selectedYear++;

		if (selectedYear > maxYear) selectedYear = -1;

		updateYearSelection(selectedYear);
	}

	private void updateStatGroup(StatisticsGroup statchart) {
		currentChart = statchart;

		pnlCenter.removeAll();
		pnlCenter.setVisible(false);
		if (currentChart != null)
		{
			if (!Arrays.asList(currentChart.supportedTypes()).contains(selectedType))
			{
				if (Arrays.asList(currentChart.supportedTypes()).contains(StatisticsTypeFilter.STF_MOVIES))
					setTypeFilter(StatisticsTypeFilter.STF_MOVIES, false);
				else if (Arrays.asList(currentChart.supportedTypes()).contains(StatisticsTypeFilter.STF_SERIES))
					setTypeFilter(StatisticsTypeFilter.STF_SERIES, false);
				else
					setTypeFilter(currentChart.supportedTypes()[0], false);
			}

			pnlLeftTop.removeAll();
			pnlLeftTop.add(scrlFilterListLeft, BorderLayout.CENTER);

			updateFilterButtons(currentChart.supportedTypes());

			JComponent comp = currentChart.getComponent(selectedType);

			pnlCenter.add(comp, BorderLayout.CENTER);
			lblChartCaption.setText(statchart.getTitle());

			currentChart.onChangeFilter(seriesList.getMap());
			currentChart.onFilterYearRange(selectedYear);

			pnlCenter.validate();

			new Thread(() -> { ThreadUtils.safeSleep(1); SwingUtils.invokeLater(() -> { if (currentChart!=null) currentChart.onShow(); }); }).start();
		} else {
			lblChartCaption.setText(""); //$NON-NLS-1$

			updateFilterButtons(new StatisticsTypeFilter[0]);

			pnlYearRange.setVisible(false);
			pnlCheckSeries.setVisible(false);

			pnlLeftTop.removeAll();
			pnlLeftTop.add(sclSidebarDataLeft, BorderLayout.CENTER);
		}

		pnlCenter.setVisible(true);
		pnlCenter.repaint();
	}

	private void updateFilterButtons(StatisticsTypeFilter[] filters) {
		var flist = Arrays.asList(filters);

		btnFilterMovies.setVisible(flist.contains(StatisticsTypeFilter.STF_MOVIES));
		btnFilterSeries.setVisible(flist.contains(StatisticsTypeFilter.STF_SERIES));
		btnFilterSeasons.setVisible(flist.contains(StatisticsTypeFilter.STF_SEASONS));
		btnFilterEpisodes.setVisible(flist.contains(StatisticsTypeFilter.STF_EPISODES));
		btnFilterMovesAndSeries.setVisible(flist.contains(StatisticsTypeFilter.STF_MOVIES_AND_SEASONS));
		btnFilterMovesAndSeasons.setVisible(flist.contains(StatisticsTypeFilter.STF_MOVIES_AND_SEASONS));
		btnFilterMovesAndEpisodes.setVisible(flist.contains(StatisticsTypeFilter.STF_MOVIES_AND_EPISODES));
		btnFilterAll.setVisible(flist.contains(StatisticsTypeFilter.STF_ALL));

		btnFilterMovies.setSelected(selectedType == StatisticsTypeFilter.STF_MOVIES);
		btnFilterSeries.setSelected(selectedType == StatisticsTypeFilter.STF_SERIES);
		btnFilterSeasons.setSelected(selectedType == StatisticsTypeFilter.STF_SEASONS);
		btnFilterEpisodes.setSelected(selectedType == StatisticsTypeFilter.STF_EPISODES);
		btnFilterMovesAndSeries.setSelected(selectedType == StatisticsTypeFilter.STF_MOVIES_AND_SEASONS);
		btnFilterMovesAndSeasons.setSelected(selectedType == StatisticsTypeFilter.STF_MOVIES_AND_SEASONS);
		btnFilterMovesAndEpisodes.setSelected(selectedType == StatisticsTypeFilter.STF_MOVIES_AND_EPISODES);
		btnFilterAll.setSelected(selectedType == StatisticsTypeFilter.STF_ALL);
	}

	private void updateYearSelection(int year) {
		if (year == -1) lblYear.setText(LocaleBundle.getString("StatisticsFrame.this.allTime")); //$NON-NLS-1$
		else  lblYear.setText(Integer.toString(year));

		if (currentChart != null) currentChart.onFilterYearRange(selectedYear);
		if (currentChart != null && currentChart.resetFrameOnYearRange()) updateStatGroup(currentChart);
	}

	private void onSeriesListAction() {
		StatisticsGroup chart = cbxChooseChart.getItemAt(cbxChooseChart.getSelectedIndex());

		if (chart != null) {
			chart.onChangeFilter(seriesList.getMap());
			if (chart.resetFrameOnFilter()) updateStatGroup(currentChart);
		}
	}

	private void setTypeFilter(StatisticsTypeFilter tf) {
		setTypeFilter(tf, true);
	}

	private void setTypeFilter(StatisticsTypeFilter tf, boolean triggerUpdate) {
		boolean changed = (selectedType != tf);

		selectedType = tf;

		updateFilterButtons((currentChart != null) ? currentChart.supportedTypes() : new StatisticsTypeFilter[0]);

		if (triggerUpdate && changed) {
			updateStatGroup(currentChart);
		}
	}

	private void initCharts() {
		_supressChartUpdate = true;
		{
			DefaultListModel<StatisticsGroup> dlm = new DefaultListModel<>();
			for (Func1to1<CCMovieList, StatisticsGroup> supplier : ClipCornStatistics.STATISTICS) {
				StatisticsGroup g = supplier.invoke(movielist);
				cbxChooseChart.addItem(g);
				dlm.addElement(g);
			}
			lsFilterListLeft.setModel(dlm);
		}
		_supressChartUpdate = false;
	}

	private void onFilterMovies() {
		setTypeFilter(StatisticsTypeFilter.STF_MOVIES);
	}

	private void onFilterSeries() {
		setTypeFilter(StatisticsTypeFilter.STF_SERIES);
	}

	private void onFilterSeasons() {
		setTypeFilter(StatisticsTypeFilter.STF_SEASONS);
	}

	private void onFilterEpisodes() {
		setTypeFilter(StatisticsTypeFilter.STF_EPISODES);
	}

	private void onFilterMovesAndSeries() {
		setTypeFilter(StatisticsTypeFilter.STF_MOVIES_AND_SERIES);
	}

	private void onFilterMovesAndSeasons() {
		setTypeFilter(StatisticsTypeFilter.STF_MOVIES_AND_SEASONS);
	}

	private void onFilterMovesAndEpisodes() {
		setTypeFilter(StatisticsTypeFilter.STF_MOVIES_AND_EPISODES);
	}

	private void onFilterAll() {
		setTypeFilter(StatisticsTypeFilter.STF_ALL);
	}

	private void lsAltLeftValueChanged() {
		ThreadUtils.delay(100, () -> selectChart(lsFilterListLeft.getModel().getElementAt(lsFilterListLeft.getSelectedIndex())));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlTop = new JPanel();
		cbxChooseChart = new JComboBox<>();
		btnPrevChart = new JButton();
		lblChartCaption = new JLabel();
		btnNextChart = new JButton();
		pnlFilter = new JPanel();
		btnFilterMovies = new JToggleButton();
		btnFilterSeries = new JToggleButton();
		btnFilterSeasons = new JToggleButton();
		btnFilterEpisodes = new JToggleButton();
		btnFilterMovesAndSeries = new JToggleButton();
		btnFilterMovesAndSeasons = new JToggleButton();
		btnFilterMovesAndEpisodes = new JToggleButton();
		btnFilterAll = new JToggleButton();
		pnlLeft = new JPanel();
		pnlLeftTop = new JPanel();
		sclSidebarDataLeft = new JScrollPane();
		pnlSidebarValues = new StatisticsDataListPanel();
		scrlFilterListLeft = new JScrollPane();
		lsFilterListLeft = new JList<>();
		pnlLeftBottom = new JPanel();
		pnlYearRange = new JPanel();
		button1 = new JButton();
		lblYear = new JLabel();
		button2 = new JButton();
		pnlCheckSeries = new JScrollPane();
		seriesList = new SeriesCheckBoxList();
		pnlCenter = new JPanel();

		//======== this ========
		setTitle(LocaleBundle.getString("StatisticsFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(925, 485));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 185dlu, $lcgap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, $ugap")); //$NON-NLS-1$

		//======== pnlTop ========
		{
			pnlTop.setLayout(new FormLayout(
				"100dlu, $lcgap, 82dlu, $lcgap, pref, $lcgap, 300px, $lcgap, pref, $lcgap, default:grow", //$NON-NLS-1$
				"default")); //$NON-NLS-1$

			//---- cbxChooseChart ----
			cbxChooseChart.setMaximumRowCount(32);
			cbxChooseChart.addActionListener(e -> onSelectChart(e));
			pnlTop.add(cbxChooseChart, CC.xy(1, 1));

			//---- btnPrevChart ----
			btnPrevChart.setText("<"); //$NON-NLS-1$
			btnPrevChart.addActionListener(e -> onPrevChart());
			pnlTop.add(btnPrevChart, CC.xy(5, 1));

			//---- lblChartCaption ----
			lblChartCaption.setText(LocaleBundle.getString("StatisticsFrame.this.title")); //$NON-NLS-1$
			lblChartCaption.setHorizontalAlignment(SwingConstants.CENTER);
			lblChartCaption.setFont(lblChartCaption.getFont().deriveFont(lblChartCaption.getFont().getStyle() | Font.BOLD));
			pnlTop.add(lblChartCaption, CC.xy(7, 1));

			//---- btnNextChart ----
			btnNextChart.setText(">"); //$NON-NLS-1$
			btnNextChart.addActionListener(e -> onNextChart());
			pnlTop.add(btnNextChart, CC.xy(9, 1));

			//======== pnlFilter ========
			{
				pnlFilter.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

				//---- btnFilterMovies ----
				btnFilterMovies.setText(LocaleBundle.getString("StatisticsFrame.this.toggleMovies")); //$NON-NLS-1$
				btnFilterMovies.addActionListener(e -> onFilterMovies());
				pnlFilter.add(btnFilterMovies);

				//---- btnFilterSeries ----
				btnFilterSeries.setText(LocaleBundle.getString("StatisticsFrame.this.toggleSeries")); //$NON-NLS-1$
				btnFilterSeries.addActionListener(e -> onFilterSeries());
				pnlFilter.add(btnFilterSeries);

				//---- btnFilterSeasons ----
				btnFilterSeasons.setText(LocaleBundle.getString("StatisticsFrame.this.toggleSeasons")); //$NON-NLS-1$
				btnFilterSeasons.addActionListener(e -> onFilterSeasons());
				pnlFilter.add(btnFilterSeasons);

				//---- btnFilterEpisodes ----
				btnFilterEpisodes.setText(LocaleBundle.getString("StatisticsFrame.this.toggleEpisodes")); //$NON-NLS-1$
				btnFilterEpisodes.addActionListener(e -> onFilterEpisodes());
				pnlFilter.add(btnFilterEpisodes);

				//---- btnFilterMovesAndSeries ----
				btnFilterMovesAndSeries.setText(LocaleBundle.getString("StatisticsFrame.this.toggleBoth")); //$NON-NLS-1$
				btnFilterMovesAndSeries.addActionListener(e -> onFilterMovesAndSeries());
				pnlFilter.add(btnFilterMovesAndSeries);

				//---- btnFilterMovesAndSeasons ----
				btnFilterMovesAndSeasons.setText(LocaleBundle.getString("StatisticsFrame.this.toggleBoth")); //$NON-NLS-1$
				btnFilterMovesAndSeasons.addActionListener(e -> onFilterMovesAndSeasons());
				pnlFilter.add(btnFilterMovesAndSeasons);

				//---- btnFilterMovesAndEpisodes ----
				btnFilterMovesAndEpisodes.setText(LocaleBundle.getString("StatisticsFrame.this.toggleBoth")); //$NON-NLS-1$
				btnFilterMovesAndEpisodes.addActionListener(e -> onFilterMovesAndEpisodes());
				pnlFilter.add(btnFilterMovesAndEpisodes);

				//---- btnFilterAll ----
				btnFilterAll.setText(LocaleBundle.getString("StatisticsFrame.this.toggleAll")); //$NON-NLS-1$
				btnFilterAll.addActionListener(e -> onFilterAll());
				pnlFilter.add(btnFilterAll);
			}
			pnlTop.add(pnlFilter, CC.xy(11, 1, CC.FILL, CC.FILL));
		}
		contentPane.add(pnlTop, CC.xywh(2, 2, 3, 1));

		//======== pnlLeft ========
		{
			pnlLeft.setLayout(new BorderLayout());

			//======== pnlLeftTop ========
			{
				pnlLeftTop.setLayout(new FormLayout(
					"default:grow", //$NON-NLS-1$
					"fill:0dlu:grow, 0dlu:grow")); //$NON-NLS-1$

				//======== sclSidebarDataLeft ========
				{
					sclSidebarDataLeft.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					sclSidebarDataLeft.setViewportView(pnlSidebarValues);
				}
				pnlLeftTop.add(sclSidebarDataLeft, CC.xy(1, 1, CC.DEFAULT, CC.FILL));

				//======== scrlFilterListLeft ========
				{

					//---- lsFilterListLeft ----
					lsFilterListLeft.addListSelectionListener(e -> lsAltLeftValueChanged());
					scrlFilterListLeft.setViewportView(lsFilterListLeft);
				}
				pnlLeftTop.add(scrlFilterListLeft, CC.xy(1, 2, CC.DEFAULT, CC.FILL));
			}
			pnlLeft.add(pnlLeftTop, BorderLayout.CENTER);

			//======== pnlLeftBottom ========
			{
				pnlLeftBottom.setLayout(new FormLayout(
					"0dlu:grow", //$NON-NLS-1$
					"$lgap, fill:default:grow, $lgap, [0dlu,pref]")); //$NON-NLS-1$

				//======== pnlYearRange ========
				{
					pnlYearRange.setLayout(new FormLayout(
						"default, $lcgap, default:grow, $lcgap, default", //$NON-NLS-1$
						"fill:default:grow")); //$NON-NLS-1$

					//---- button1 ----
					button1.setText("<"); //$NON-NLS-1$
					button1.addActionListener(e -> onPrevYear());
					pnlYearRange.add(button1, CC.xy(1, 1));

					//---- lblYear ----
					lblYear.setText(LocaleBundle.getString("StatisticsFrame.this.allTime")); //$NON-NLS-1$
					lblYear.setHorizontalAlignment(SwingConstants.CENTER);
					lblYear.setFont(new Font("Courier New", lblYear.getFont().getStyle() | Font.BOLD, 16)); //$NON-NLS-1$
					pnlYearRange.add(lblYear, CC.xy(3, 1));

					//---- button2 ----
					button2.setText(">"); //$NON-NLS-1$
					button2.addActionListener(e -> onNextYear());
					pnlYearRange.add(button2, CC.xy(5, 1));
				}
				pnlLeftBottom.add(pnlYearRange, CC.xy(1, 2));

				//======== pnlCheckSeries ========
				{

					//---- seriesList ----
					seriesList.setVisibleRowCount(16);
					seriesList.addActionListener(e -> onSeriesListAction());
					pnlCheckSeries.setViewportView(seriesList);
				}
				pnlLeftBottom.add(pnlCheckSeries, CC.xy(1, 4));
			}
			pnlLeft.add(pnlLeftBottom, BorderLayout.PAGE_END);
		}
		contentPane.add(pnlLeft, CC.xy(2, 4, CC.FILL, CC.FILL));

		//======== pnlCenter ========
		{
			pnlCenter.setLayout(new BorderLayout());
		}
		contentPane.add(pnlCenter, CC.xy(4, 4, CC.FILL, CC.FILL));
		setSize(1000, 600);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlTop;
	private JComboBox<StatisticsGroup> cbxChooseChart;
	private JButton btnPrevChart;
	private JLabel lblChartCaption;
	private JButton btnNextChart;
	private JPanel pnlFilter;
	private JToggleButton btnFilterMovies;
	private JToggleButton btnFilterSeries;
	private JToggleButton btnFilterSeasons;
	private JToggleButton btnFilterEpisodes;
	private JToggleButton btnFilterMovesAndSeries;
	private JToggleButton btnFilterMovesAndSeasons;
	private JToggleButton btnFilterMovesAndEpisodes;
	private JToggleButton btnFilterAll;
	private JPanel pnlLeft;
	private JPanel pnlLeftTop;
	private JScrollPane sclSidebarDataLeft;
	private StatisticsDataListPanel pnlSidebarValues;
	private JScrollPane scrlFilterListLeft;
	private JList<StatisticsGroup> lsFilterListLeft;
	private JPanel pnlLeftBottom;
	private JPanel pnlYearRange;
	private JButton button1;
	private JLabel lblYear;
	private JButton button2;
	private JScrollPane pnlCheckSeries;
	private SeriesCheckBoxList seriesList;
	private JPanel pnlCenter;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
