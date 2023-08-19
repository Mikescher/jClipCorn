package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.features.statistics.PieRotator;
import de.jClipCorn.features.statistics.StatisticsChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.features.statistics.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.stream.CCStream;

public class StatisticsLanguageChart extends StatisticsChart {
	public StatisticsLanguageChart(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist, StatisticsTypeFilter source) {
		JFreeChart chart = ChartFactory.createPieChart3D(
	            "",      //$NON-NLS-1$
	            getDataSet(movielist, source),               
	            false,                  
	            true, // tooltips
	            false
	    );

		chart.removeLegend();

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);

		plot.setBackgroundPaint(XYBACKGROUND_COLOR);

		chart.setBackgroundPaint(null);

		if (ROTATE_PIE) {
			PieRotator rotator = new PieRotator(plot);
			rotator.start();
		}

	    return chart;
	}
	
	private PieDataset getDataSet(CCMovieList movielist, StatisticsTypeFilter source) {
		CCStream<ICCPlayableElement> it = source.iterator(movielist).cast();
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		int[] values = StatisticsHelper.getCountForAllLanguages(it);
		
		for (int i = 0; i < values.length; i++) {
			if (values[i] > 0) {
				dataset.setValue(CCDBLanguage.getWrapper().findOrNull(i).asString() + " [" + values[i] + "]", values[i]); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
        return dataset;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.language"); //$NON-NLS-1$
	}

	@Override
	public boolean usesFilterableSeries() {
		return false;
	}
	
	@Override
	public boolean usesFilterableYearRange() {
		return false;
	}

	@Override
	public StatisticsTypeFilter[] supportedTypes() {
		return new StatisticsTypeFilter[]{StatisticsTypeFilter.STF_MOVIES, StatisticsTypeFilter.STF_EPISODES, StatisticsTypeFilter.STF_MOVIES_AND_EPISODES};
	}
}
