package de.jClipCorn.features.statistics.charts;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsPanel;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsTypeFilter;
import de.jClipCorn.gui.guiComponents.HorizontalScalablePane;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datetime.CCChronos;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDatespan;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class StatisticsSeriesTimelineCombined extends StatisticsPanel {
	private final static Color LIGHT_RED  = new Color(255,   0,   0, 159);
	private final static Color DARK_RED   = new Color(255,   0,   0, 207);
	private final static Color BORDER_RED = new Color(191,   0,   0, 255);
	private final static Color LINE_GRAY  = new Color(192, 192, 192, 255);

	private CCMovieList movielist;

	private Map<CCSeries, Boolean> serFilter = null;
	private int yearFilter = -1;

	private List<Tuple<CCDate, List<Tuple<CCSeries, Boolean>>>> gridData;
	private List<BufferedImage> images;

	private JScrollPane scrlPane;

	public StatisticsSeriesTimelineCombined(CCMovieList ml, StatisticsTypeFilter _source) {
		super(_source);
		movielist = ml;
	}
	
	private void collectData()
	{
		HashMap<CCSeries, List<CCDatespan>> seriesMapStretch = StatisticsHelper.getAllSeriesTimespans(movielist, CCProperties.getInstance().PROP_STATISTICS_TIMELINEGRAVITY.getValue(), StatisticsHelper.OrderMode.STRICT);
		HashMap<CCSeries, List<CCDatespan>> seriesMapZero    = StatisticsHelper.getAllSeriesTimespans(movielist, 0, StatisticsHelper.OrderMode.IGNORED);

		CCDate start = StatisticsHelper.getSeriesTimespansStart(seriesMapZero);
		CCDate end = CCDate.max(StatisticsHelper.getSeriesTimespansEnd(seriesMapZero).getSubDay(1), CCDate.getCurrentDate());

		List<CCSeries> seriesList = StatisticsHelper.convertMapToOrderedKeyList(seriesMapZero, Comparator.comparing(o -> o.getTitle().toLowerCase()));

		if (yearFilter != -1) start = CCDate.max(start, CCDate.create(1,  1,  yearFilter));
		if (yearFilter != -1) end   = CCDate.max(end,   CCDate.create(1,  1,  yearFilter));
		if (yearFilter != -1) start = CCDate.min(start, CCDate.create(31, 12, yearFilter));
		if (yearFilter != -1) end   = CCDate.min(end,   CCDate.create(31, 12, yearFilter));

		if (serFilter != null) seriesList = CCStreams.iterate(seriesList).filter(s -> serFilter.containsKey(s) && serFilter.get(s)).enumerate();

		HashMap<CCDate, HashSet<CCSeries>> dayMapStretch = new HashMap<>();
		HashMap<CCDate, HashSet<CCSeries>> dayMapZero    = new HashMap<>();

		for (Map.Entry<CCSeries, List<CCDatespan>> entry : seriesMapStretch.entrySet())
		{
			if (!seriesList.contains(entry.getKey())) continue;
			for (CCDatespan ds : entry.getValue())
			{
				if (ds.getDayCount() == 1) continue;

				for (CCDate d : ds.iterateDays())
				{
					if (d.isLessThan(start)) continue;
					if (d.isGreaterThan(end)) continue;

					if (!dayMapStretch.containsKey(d)) dayMapStretch.put(d, new HashSet<>());
					dayMapStretch.get(d).add(entry.getKey());
				}
			}
		}

		for (Map.Entry<CCSeries, List<CCDatespan>> entry : seriesMapZero.entrySet())
		{
			if (!seriesList.contains(entry.getKey())) continue;
			for (CCDatespan ds : entry.getValue())
			{
				for (CCDate d : ds.iterateDays())
				{
					if (d.isLessThan(start)) continue;
					if (d.isGreaterThan(end)) continue;

					if (!dayMapZero.containsKey(d)) dayMapZero.put(d, new HashSet<>());
					dayMapZero.get(d).add(entry.getKey());
				}
			}
		}

		HashSet<CCSeries> hsEmpty = new HashSet<>();

		BufferedImage tmpimg = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = tmpimg.createGraphics();
		g2d.setFont(new Font("Segeo UI", Font.PLAIN, 12)); //$NON-NLS-1$
		FontMetrics fm = g2d.getFontMetrics();

		gridData = new ArrayList<>();

		List<Tuple<CCSeries, Boolean>> lastData = new ArrayList<>();
		List<Integer> countdown = new ArrayList<>();
		for (CCDate d : new CCDatespan(start, end.getAddDay(1)).iterateDays())
		{
			HashSet<CCSeries> serStretch = dayMapStretch.getOrDefault(d, hsEmpty);
			HashSet<CCSeries> serZero    = dayMapZero.getOrDefault(d, hsEmpty);

			List<Tuple<CCSeries, Boolean>> currData = new ArrayList<>();

			for (int i = 0; i < lastData.size(); i++)
			{
				Tuple<CCSeries, Boolean> dat = lastData.get(i);

				if (dat != null && serStretch.contains(dat.Item1)) listSet(currData, countdown, i, Tuple.Create(dat.Item1, serZero.contains(dat.Item1)), d.isFirstOfMonth(), fm);
			}

			for (CCSeries s : serStretch)
			{
				if (listContains(currData, s)) continue;
				listInsert(currData, countdown, Tuple.Create(s, serZero.contains(s)), fm);
			}

			gridData.add(Tuple.Create(d, currData));
			lastData = currData;

			for (int i=0; i<countdown.size();i++)countdown.set(i, countdown.get(i)-1);
		}

		g2d.dispose();

		int maxHeight = 1 + Math.max(1, CCStreams.iterate(gridData).maxOrDefault(p -> p.Item2.size(), Comparator.comparingInt(q->q), 1));

		images = new ArrayList<>();

		CCDate cdd = CCDate.create(1, start.getMonth(), start.getYear());
		while(cdd.isLessEqualsThan(end))
		{
			BufferedImage img = createImage(cdd, maxHeight);
			images.add(img);
			cdd = cdd.getAddMonth(1);
		}
	}

	@SuppressWarnings({"SuspiciousNameCombination", "PointlessArithmeticExpression", "DuplicateExpressions"})
	private BufferedImage createImage(CCDate cdd, int height)
	{
		final int daycount = 31;

		final int scale = 2;

		final int grid_sidelen           = scale * 16;
		final int outer_line_width       = scale * 1;
		final int outer_offset           = scale * 8;
		final int inner_grid_pad         = scale * 1;
		final int highlight_border_width = scale * 1;

		final int grid_line_width        = scale * 1;
		final int fontsize_dates         = scale * 10;
		final int fontsize_title         = scale * 12;
		final int fontsize_header        = scale * 12;

		final int margin_right           = scale * 160;

		String header = (cdd.getMonthName() + " " + cdd.getYear()); //$NON-NLS-1$
		int headersize = (int)Math.round(header.length() / 2d);

		int dom = CCChronos.getDaysOfMonth(cdd.getMonth(), cdd.getYear());
		int ww = grid_sidelen * daycount + outer_line_width;
		int hh = grid_sidelen * height   + outer_line_width;

		int rw = ww + outer_offset*2 + margin_right;
		int rh = hh + outer_offset*2;

		BufferedImage img = new BufferedImage(rw, rh, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = img.createGraphics();
		{
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, rw, rh);

			g.translate(outer_offset, outer_offset);

			g.setColor(LINE_GRAY);
			for (int i=0; i<=height; i++)
			{
				g.fillRect(0, grid_sidelen * i, (grid_sidelen * dom + outer_line_width), grid_line_width);
			}
			for (int i=0; i<=dom; i++)
			{
				int s = (i>0 && i<headersize) ? grid_sidelen : 0;
				g.fillRect(grid_sidelen*i, s, grid_line_width, hh - s);
			}

			g.setColor(Color.BLACK);
			g.fillRect(0,                   0,                   ww,               outer_line_width); // N
			g.fillRect(ww-outer_line_width, 0,                   outer_line_width, hh);               // E
			g.fillRect(0,                   hh-outer_line_width, ww,               outer_line_width); // S
			g.fillRect(0,                   0,                   outer_line_width, hh);               // W

			List<Tuple3<String, Integer, Integer>> strings = new ArrayList<>();

			List<Tuple<CCSeries, Boolean>> prev = getData(cdd.getSubDay(1));
			List<Tuple<CCSeries, Boolean>> curr = getData(cdd);
			List<Tuple<CCSeries, Boolean>> next = getData(cdd.getAddDay(1));
			for (int col = 0; col < dom; col++)
			{
				for (int row=0; row < curr.size(); row++)
				{
					Tuple<CCSeries, Boolean> dat = curr.get(row);
					if (dat == null) continue;

					boolean isBetw = (col==0) && listContains(prev, dat.Item1);
					boolean isRPre = (col!=0) && listContains(prev, dat.Item1);
					boolean isPrev =             listContains(prev, dat.Item1);
					boolean isNext =             listContains(next, dat.Item1);

					g.setColor(dat.Item2 ? DARK_RED : LIGHT_RED); // INNER

					if (!isPrev && !isNext) g.fillRect(col * grid_sidelen + 2*inner_grid_pad, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_sidelen-3*inner_grid_pad, grid_sidelen-3*inner_grid_pad);
					else if (!isPrev)       g.fillRect(col * grid_sidelen + 2*inner_grid_pad, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_sidelen-2*inner_grid_pad, grid_sidelen-3*inner_grid_pad);
					else if (!isNext)       g.fillRect(col * grid_sidelen + 1*inner_grid_pad, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_sidelen-2*inner_grid_pad, grid_sidelen-3*inner_grid_pad);
					else                    g.fillRect(col * grid_sidelen + 1*inner_grid_pad, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_sidelen-1*inner_grid_pad, grid_sidelen-3*inner_grid_pad);

					if (isBetw) g.fillRect((col+0) * grid_sidelen, hh-(row+1)*grid_sidelen+inner_grid_pad, inner_grid_pad, grid_sidelen-3*inner_grid_pad);
					if (isNext) g.fillRect((col+1) * grid_sidelen, hh-(row+1)*grid_sidelen+inner_grid_pad, inner_grid_pad, grid_sidelen-3*inner_grid_pad);

					g.setColor(BORDER_RED); // TOP

					if (!isPrev && !isNext) g.fillRect(col * grid_sidelen + 2*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_sidelen-3*highlight_border_width, highlight_border_width);
					else if (!isPrev)       g.fillRect(col * grid_sidelen + 2*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_sidelen-2*highlight_border_width, highlight_border_width);
					else if (!isNext)       g.fillRect(col * grid_sidelen + 1*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_sidelen-2*highlight_border_width, highlight_border_width);
					else                    g.fillRect(col * grid_sidelen + 1*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_sidelen-1*highlight_border_width, highlight_border_width);

					g.setColor(BORDER_RED); // BOTTOM

					if (!isPrev && !isNext) g.fillRect(col * grid_sidelen + 2*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad+(grid_sidelen-2*inner_grid_pad-2*grid_line_width), grid_sidelen-3*highlight_border_width, highlight_border_width);
					else if (!isPrev)       g.fillRect(col * grid_sidelen + 2*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad+(grid_sidelen-2*inner_grid_pad-2*grid_line_width), grid_sidelen-2*highlight_border_width, highlight_border_width);
					else if (!isNext)       g.fillRect(col * grid_sidelen + 1*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad+(grid_sidelen-2*inner_grid_pad-2*grid_line_width), grid_sidelen-2*highlight_border_width, highlight_border_width);
					else                    g.fillRect(col * grid_sidelen + 1*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad+(grid_sidelen-2*inner_grid_pad-2*grid_line_width), grid_sidelen-1*highlight_border_width, highlight_border_width);

					g.setColor(BORDER_RED); // EXTENSION

					if (isRPre) g.fillRect(col * grid_sidelen - grid_line_width, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_line_width, grid_line_width);
					if (isRPre) g.fillRect(col * grid_sidelen - grid_line_width, hh-(row+1)*grid_sidelen+inner_grid_pad+(grid_sidelen-2*inner_grid_pad-2*grid_line_width), grid_line_width, grid_line_width);
					if (isNext) g.fillRect(col * grid_sidelen + grid_sidelen, hh-(row+1)*grid_sidelen+inner_grid_pad, grid_line_width, grid_line_width);
					if (isNext) g.fillRect(col * grid_sidelen + grid_sidelen,  hh-(row+1)*grid_sidelen+inner_grid_pad+(grid_sidelen-2*inner_grid_pad-2*grid_line_width), grid_line_width, grid_line_width);

					if (!isPrev) g.fillRect((col+0) * grid_sidelen + 2*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad, highlight_border_width, grid_sidelen-3*highlight_border_width);
					if (!isNext) g.fillRect((col+1) * grid_sidelen - 2*highlight_border_width, hh-(row+1)*grid_sidelen+inner_grid_pad, highlight_border_width, grid_sidelen-3*highlight_border_width);

					// TEXT

					if (!isPrev || (col==0)) strings.add(Tuple3.Create(dat.Item1.getTitle(), col * grid_sidelen + (grid_sidelen-fontsize_title), hh-row*grid_sidelen-(grid_sidelen-fontsize_title)));
				}

				prev = curr;
				curr = next;
				next = getData(cdd.getAddDay(col+2));
			}

			g.setColor(Color.BLACK);
			g.setFont(new Font("Segeo UI", Font.PLAIN, fontsize_title)); //$NON-NLS-1$
			for (Tuple3<String, Integer, Integer> str : strings) {
				g.drawString(str.Item1, str.Item2, str.Item3);
			}

			g.setColor(Color.LIGHT_GRAY);
			g.setFont(new Font("Consolas", Font.PLAIN, fontsize_dates)); //$NON-NLS-1$
			for (int i = headersize+1; i <= dom; i++)
			{
				g.drawString(StringUtils.leftPad(Integer.toString(i), 2, '0'), (i-1)*grid_sidelen + (grid_sidelen - fontsize_dates)/2, outer_line_width + grid_sidelen - (grid_sidelen - fontsize_dates)/2);
			}

			g.setColor(Color.BLACK);
			g.setFont(new Font("Consolas", Font.PLAIN, fontsize_header)); //$NON-NLS-1$
			g.drawString(cdd.getMonthName() + " " + cdd.getYear(), outer_line_width + (grid_sidelen - fontsize_header)/2, outer_line_width + (grid_sidelen - (grid_sidelen - fontsize_header)/2)); //$NON-NLS-1$

			g.translate(-outer_offset, -outer_offset);

		}
		g.dispose();
		return img;
	}

	private List<Tuple<CCSeries, Boolean>> getData(CCDate day)
	{
		if (gridData.isEmpty()) return new ArrayList<>();

		int diff = gridData.get(0).Item1.getDayDifferenceTo(day);
		if (diff < 0 || diff >= gridData.size()) return new ArrayList<>();

		return gridData.get(diff).Item2;
	}

	private void listInsert(List<Tuple<CCSeries, Boolean>> list, List<Integer> countdown, Tuple<CCSeries, Boolean> value, FontMetrics fm)
	{
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i) == null && countdown.get(i)<=0)
			{
				list.set(i, value);
				listSet(countdown, i, value.Item1, fm);
				return;
			}
		}

		while(list.size() < countdown.size() && countdown.get(list.size())>0)list.add(null);
		list.add(value);
		listSet(countdown, list.size()-1, value.Item1, fm);
	}

	private boolean listContains(List<Tuple<CCSeries, Boolean>> list, CCSeries value)
	{
		for (Tuple<CCSeries, Boolean> v : list) {
		    if (v != null && v.Item1.equals(value)) return true;
		}
		return false;
	}

	private void listSet(List<Tuple<CCSeries, Boolean>> list, List<Integer> countdown, int idx, Tuple<CCSeries, Boolean> value, boolean refreshCountdown, FontMetrics fm)
	{
		while (list.size() <= idx) list.add(null);
		list.set(idx, value);
		if (refreshCountdown) listSet(countdown, idx, value.Item1, fm);
	}

	private void listSet(List<Integer> list, int idx, CCSeries value, FontMetrics fm)
	{
		while (list.size() <= idx) list.add(null);
		int len = (int)Math.ceil((fm.stringWidth(value.getTitle()) + 8) / 16.0);
		list.set(idx, len);
	}

	@Override
	protected void onChangeFilter(Map<CCSeries, Boolean> map) {
		if (map.equals(serFilter)) return;
		serFilter = map;
		invalidateComponent();
		gridData = null;
	}

	@Override
	protected void onFilterYearRange(int year) {
		if (year == yearFilter) return;
		yearFilter = year;
		invalidateComponent();
		gridData = null;
	}

	@Override
	public void onShow() {
		if (scrlPane != null) {
			JScrollBar sb = scrlPane.getVerticalScrollBar();
			sb.validate();
			sb.setValue(sb.getMaximum());
		}
	}

	@Override
	public JComponent createComponent() {

		if (gridData == null) collectData();

		JPanel root = new JPanel(new BorderLayout());
		{
			JScrollPane scroll = new JScrollPane();
			{
				JPanel pnlData = new JPanel();
				pnlData.setLayout(new BoxLayout(pnlData, BoxLayout.Y_AXIS));
				{
					for (BufferedImage img : images)
					{
					 	pnlData.add(new HorizontalScalablePane(img));
					}
				}
				scroll.setViewportView(pnlData);
				scroll.getVerticalScrollBar().setUnitIncrement(16);

				JScrollBar sb = scroll.getVerticalScrollBar();
				sb.validate();
				sb.setValue(sb.getMaximum());

				scrlPane = scroll;
			}
			root.add(scroll, BorderLayout.CENTER);
		}

		return root;
	}

	@Override
	public boolean usesFilterableSeries() {
		return true;
	}

	@Override
	public boolean usesFilterableYearRange() {
		return true;
	}

	@Override
	public String createTitle() {
		return LocaleBundle.getString("StatisticsFrame.charttitles.seriesTimelineCombined"); //$NON-NLS-1$
	}

	@Override
	public boolean resetFrameOnFilter() {
		return true;
	}

	@Override
	public boolean resetFrameOnYearRange() {
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
