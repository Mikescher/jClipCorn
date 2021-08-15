package de.jClipCorn.test;

import java.util.Collection;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.statistics.ClipCornStatistics;
import de.jClipCorn.features.statistics.StatisticsGroup;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.util.lambda.Func1to1;

@RunWith(Parameterized.class)
public class TestStatisticsCharts extends ClipCornBaseTest {

	@Parameters
	public static Collection<Object[]> data() { return ClipCornStatistics.STATISTICS.stream().map(p -> new Object[]{p}).collect(Collectors.toList()); }
	
	private final Func1to1<CCMovieList, StatisticsGroup> supplier;
	public TestStatisticsCharts(Func1to1<CCMovieList, StatisticsGroup> _supplier) {
		supplier = _supplier;
	}
	
	@Test
	public void testStatisticsChartCreation() throws Exception {
		CCMovieList ml = createExampleDB();

		StatisticsGroup group = supplier.invoke(ml);
		
		if (group.supportedTypes().containsMovies()) group.getComponent(StatisticsTypeFilter.MOVIES);

		if (group.supportedTypes().containsSeries()) group.getComponent(StatisticsTypeFilter.SERIES);

		if (group.supportedTypes().containsBoth()) group.getComponent(StatisticsTypeFilter.BOTH);
	}

	@Test
	public void testStatisticsChartCreationWithEmptyDB() {
		CCMovieList ml = createEmptyDB();

		StatisticsGroup group = supplier.invoke(ml);
		
		if (group.supportedTypes().containsMovies()) group.getComponent(StatisticsTypeFilter.MOVIES);

		if (group.supportedTypes().containsSeries()) group.getComponent(StatisticsTypeFilter.SERIES);

		if (group.supportedTypes().containsBoth()) group.getComponent(StatisticsTypeFilter.BOTH);
	}
}
