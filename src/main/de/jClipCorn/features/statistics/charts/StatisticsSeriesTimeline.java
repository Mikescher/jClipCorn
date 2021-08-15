package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.features.statistics.timeline.TimelineCaptionComponent;
import de.jClipCorn.features.statistics.timeline.TimelineDateCaptionComponent;
import de.jClipCorn.features.statistics.timeline.TimelineDisplayComponent;
import de.jClipCorn.features.statistics.timeline.TimelineEmptyCaptionComponent;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDatespan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsSeriesTimeline extends StatisticsPanel {
	private Map<CCSeries, Boolean> mapCache = null;
	
	private HashMap<CCSeries, List<CCDatespan>> seriesMap;
	private HashMap<CCSeries, List<CCDatespan>> seriesMapZero;
	private List<CCSeries> seriesList;
	private CCDate seriesMapStart;
	private CCDate seriesMapEnd;
	
	public StatisticsSeriesTimeline(CCMovieList ml, StatisticsTypeFilter _source) {
		super(ml, _source);
	}
	
	private void collectData() {
		seriesMap = StatisticsHelper.getAllSeriesTimespans(movielist, ccprops().PROP_STATISTICS_TIMELINEGRAVITY.getValue(), StatisticsHelper.OrderMode.ENFORCED);
		seriesMapZero = StatisticsHelper.getAllSeriesTimespans(movielist, 0, StatisticsHelper.OrderMode.IGNORED);
		seriesList = StatisticsHelper.convertMapToOrderedKeyList(seriesMap, Comparator.comparing(o -> o.getTitle().toLowerCase()));
		seriesMapStart = StatisticsHelper.getSeriesTimespansStart(seriesMap);
		seriesMapEnd = CCDate.max(StatisticsHelper.getSeriesTimespansEnd(seriesMap).getSubDay(1), CCDate.getCurrentDate());
	}

	@Override
	public JComponent createComponent() {
		Map<CCSeries, Boolean> map = mapCache;
		if (map == null) map = new HashMap<>();
		
		if (seriesMap == null) collectData();

		TimelineCaptionComponent leftBody = new TimelineCaptionComponent(seriesList, map);
		TimelineEmptyCaptionComponent leftHeader = new TimelineEmptyCaptionComponent(leftBody);
		TimelineDisplayComponent rightBody = new TimelineDisplayComponent(seriesList, seriesMap, seriesMapZero, seriesMapStart, seriesMapEnd, map);
		TimelineDateCaptionComponent rightHeader = new TimelineDateCaptionComponent(seriesMapStart, seriesMapEnd);
		
		leftBody.onSelectedChanged = e -> rightBody.setSelectedDirect((CCSeries)e.getSource());

		rightBody.onSelectedChanged = e -> leftBody.setSelectedDirect((CCSeries)e.getSource());
		
		JScrollPane left;
		JScrollPane right;
		
		JPanel root = new JPanel(new BorderLayout());
		{
			JPanel leftRoot = new JPanel(new BorderLayout());
			{				
				left = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER,  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				{
					left.setBorder(new EmptyBorder(0, 0, 0, 0));
					left.setViewportView(leftBody);
				}
				leftRoot.add(left, BorderLayout.CENTER);

				leftRoot.add(leftHeader, BorderLayout.NORTH);
			}
			root.add(leftRoot, BorderLayout.WEST);

			JPanel rightRoot = new JPanel(new BorderLayout());
			{
				JScrollPane rightTop = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				{	
					rightTop.setBorder(new EmptyBorder(0, 0, 0, 0));
					rightTop.setViewportView(rightHeader);
				}
				rightRoot.add(rightTop, BorderLayout.NORTH);
				
				right = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				right.setBorder(new EmptyBorder(0, 0, 0, 0));
				{	
					right.getVerticalScrollBar().addAdjustmentListener(e -> left.getVerticalScrollBar().setValue(right.getVerticalScrollBar().getValue()));
					right.getHorizontalScrollBar().addAdjustmentListener(e -> rightTop.getHorizontalScrollBar().setValue(right.getHorizontalScrollBar().getValue()));
					right.setViewportView(rightBody);
				}
				rightRoot.add(right, BorderLayout.CENTER);
			}
			root.add(rightRoot, BorderLayout.CENTER);
		}

		return root;
	}

	@Override
	protected void onChangeFilter(Map<CCSeries, Boolean> map) {
		mapCache = map;
		invalidateComponent();
	}

	@Override
	public boolean usesFilterableSeries() {
		return true;
	}

	@Override
	public boolean usesFilterableYearRange() {
		return false;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.seriesTimeline"); //$NON-NLS-1$
	}

	@Override
	public boolean resetFrameOnFilter() {
		return true;
	}

	@Override
	public StatisticsTypeFilter supportedTypes() {
		return StatisticsTypeFilter.SERIES;
	}

	@Override
	public String createToggleTwoCaption() {
		return LocaleBundle.getString("StatisticsFrame.this.toggleEpisodes"); //$NON-NLS-1$
	}
}
