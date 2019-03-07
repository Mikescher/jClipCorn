package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsHelper;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.frames.statisticsFrame.timeline.TimelineCaptionComponent;
import de.jClipCorn.gui.frames.statisticsFrame.timeline.TimelineDateCaptionComponent;
import de.jClipCorn.gui.frames.statisticsFrame.timeline.TimelineDisplayComponent;
import de.jClipCorn.gui.frames.statisticsFrame.timeline.TimelineEmptyCaptionComponent;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDatespan;

public class StatisticsSeriesTimeline extends StatisticsPanel {
	private CCMovieList movielist;
	
	private Map<CCSeries, Boolean> mapCache = null;
	
	private HashMap<CCSeries, List<CCDatespan>> seriesMap;
	private HashMap<CCSeries, List<CCDatespan>> seriesMapZero;
	private List<CCSeries> seriesList;
	private CCDate seriesMapStart;
	private CCDate seriesMapEnd;
	
	public StatisticsSeriesTimeline(CCMovieList ml, StatisticsTypeFilter _source) {
		super(_source);
		movielist = ml;
	}
	
	private void collectData() {
		seriesMap = StatisticsHelper.getAllSeriesTimespans(movielist, CCProperties.getInstance().PROP_STATISTICS_TIMELINEGRAVITY.getValue(), true);
		seriesMapZero = StatisticsHelper.getAllSeriesTimespans(movielist, 0, true);
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
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.seriesTimeline"); //$NON-NLS-1$
	}

	@Override
	public boolean resetFrameOnFilter() {
		return true;
	}

	@Override
	protected StatisticsTypeFilter supportedTypes() {
		return StatisticsTypeFilter.SERIES;
	}

	@Override
	public String createToggleTwoCaption() {
		return LocaleBundle.getString("StatisticsFrame.this.toggleEpisodes"); //$NON-NLS-1$
	}
}
