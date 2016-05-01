package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.BorderLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.CCDatespan;
import de.jClipCorn.util.helper.StatisticsHelper;

public class StatisticsSeriesTimeline extends StatisticsPanel {

	private CCMovieList movielist;
	
	public StatisticsSeriesTimeline(CCMovieList ml) {
		movielist = ml;
	}
	
	private JComponent create() {
		HashMap<String, List<CCDatespan>> seriesMap = StatisticsHelper.getAllSeriesTimespans(movielist, CCProperties.getInstance().PROP_STATISTICS_TIMELINEGRAVITY.getValue());
		HashMap<String, List<CCDatespan>> seriesMapZero = StatisticsHelper.getAllSeriesTimespans(movielist, 0);
		List<String> seriesList = StatisticsHelper.convertMapToKeyList(seriesMap);
		CCDate seriesMapStart = StatisticsHelper.getSeriesTimespansStart(seriesMap);
		CCDate seriesMapEnd = StatisticsHelper.getSeriesTimespansEnd(seriesMap);
		
		JScrollPane left;
		JScrollPane right;
		
		JPanel root = new JPanel(new BorderLayout());
		{
			JPanel leftRoot = new JPanel(new BorderLayout());
			{
				TimelineCaptionComponent body = new TimelineCaptionComponent(seriesList);
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
				TimelineDisplayComponent body = new TimelineDisplayComponent(seriesMap, seriesMapZero, seriesMapStart, seriesMapEnd);
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
	public JComponent getComponent() {
		return create();
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

}
