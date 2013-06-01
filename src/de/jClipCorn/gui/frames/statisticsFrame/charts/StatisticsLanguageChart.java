package de.jClipCorn.gui.frames.statisticsFrame.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.StatisticsHelper;

public class StatisticsLanguageChart extends StatisticsChart {
	public StatisticsLanguageChart(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected JFreeChart createChart(CCMovieList movielist) {
		JFreeChart chart = ChartFactory.createPieChart3D(
	            "",      //$NON-NLS-1$
	            getDataSet(movielist),               
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
	
	private PieDataset getDataSet(CCMovieList movielist) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		int[] values = StatisticsHelper.getMovieCountForAllLanguages(movielist);
		
		for (int i = 0; i < values.length; i++) {
			if (values[i] > 0) {
				dataset.setValue(CCMovieLanguage.find(i).asString(), values[i]);
			}
		}
		
        return dataset;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.language"); //$NON-NLS-1$
	}
}
