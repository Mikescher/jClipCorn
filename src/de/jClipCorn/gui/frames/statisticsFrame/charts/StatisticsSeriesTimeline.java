package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.BorderLayout;
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
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.frames.statisticsFrame.timeline.TimelineCaptionComponent;
import de.jClipCorn.gui.frames.statisticsFrame.timeline.TimelineDateCaptionComponent;
import de.jClipCorn.gui.frames.statisticsFrame.timeline.TimelineDisplayComponent;
import de.jClipCorn.gui.frames.statisticsFrame.timeline.TimelineEmptyCaptionComponent;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.CCDatespan;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsSeriesTimeline extends StatisticsPanel {
	private CCMovieList movielist;
	
	private JComponent statComp = null;
	
	private HashMap<CCSeries, List<CCDatespan>> seriesMap;
	private HashMap<CCSeries, List<CCDatespan>> seriesMapZero;
	private List<CCSeries> seriesList;
	private CCDate seriesMapStart;
	private CCDate seriesMapEnd;
	
	public StatisticsSeriesTimeline(CCMovieList ml) {
		movielist = ml;
	}
	
	private void collectData() {
		Comparator<CCSeries> comp = new Comparator<CCSeries>() {
			@Override
			public int compare(CCSeries o1, CCSeries o2) {
				return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
			}
		};
		
		seriesMap = StatisticsHelper.getAllSeriesTimespans(movielist, CCProperties.getInstance().PROP_STATISTICS_TIMELINEGRAVITY.getValue());
		seriesMapZero = StatisticsHelper.getAllSeriesTimespans(movielist, 0);
		seriesList = StatisticsHelper.convertMapToOrderedKeyList(seriesMap, comp);
		seriesMapStart = StatisticsHelper.getSeriesTimespansStart(seriesMap);
		seriesMapEnd = StatisticsHelper.getSeriesTimespansEnd(seriesMap);
	}
	
	private JComponent create(Map<CCSeries, Boolean> map) {
		if (map == null) map = new HashMap<>();
		
		if (seriesMap == null) collectData();
		
		JScrollPane left;
		JScrollPane right;
		
		JPanel root = new JPanel(new BorderLayout());
		{
			JPanel leftRoot = new JPanel(new BorderLayout());
			{
				TimelineCaptionComponent body = new TimelineCaptionComponent(seriesList, map);
				TimelineEmptyCaptionComponent header = new TimelineEmptyCaptionComponent(body);
				
				left = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER,  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				{
					left.setBorder(new EmptyBorder(0, 0, 0, 0));
					left.setViewportView(body);
				}
				leftRoot.add(left, BorderLayout.CENTER);

				leftRoot.add(header, BorderLayout.NORTH);
			}
			root.add(leftRoot, BorderLayout.WEST);

			JPanel rightRoot = new JPanel(new BorderLayout());
			{
				TimelineDisplayComponent body = new TimelineDisplayComponent(seriesList, seriesMap, seriesMapZero, seriesMapStart, seriesMapEnd, map);
				TimelineDateCaptionComponent header = new TimelineDateCaptionComponent(seriesMapStart, seriesMapEnd);
				
				JScrollPane rightTop = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				{	
					rightTop.setBorder(new EmptyBorder(0, 0, 0, 0));
					rightTop.setViewportView(header);
				}
				rightRoot.add(rightTop, BorderLayout.NORTH);
				
				right = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				right.setBorder(new EmptyBorder(0, 0, 0, 0));
				{	
					right.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
						@Override
						public void adjustmentValueChanged(AdjustmentEvent e) {
							left.getVerticalScrollBar().setValue(right.getVerticalScrollBar().getValue());
						}
					});
					right.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
						@Override
						public void adjustmentValueChanged(AdjustmentEvent e) {
							rightTop.getHorizontalScrollBar().setValue(right.getHorizontalScrollBar().getValue());
						}
					});
					right.setViewportView(body);
				}
				rightRoot.add(right, BorderLayout.CENTER);
			}
			root.add(rightRoot, BorderLayout.CENTER);
		}
		
		return root;
	}

	@Override
	public void onHideSeries(Map<CCSeries, Boolean> map) {
		statComp = create(map);
	}
	
	@Override
	public JComponent getComponent() {
		if (statComp != null) return statComp;
		
		return (statComp = create(null));
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
	public boolean resetFrameOnFilter() {
		return true;
	}

	@Override
	protected String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.seriesTimeline"); //$NON-NLS-1$
	}
}
