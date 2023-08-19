package de.jClipCorn.gui.frames.statisticsFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.statistics.StatisticsHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDataListPanel extends JPanel
{
	private final List<JLabel> labels_left = new ArrayList<>();
	private final List<JLabel> labels_right = new ArrayList<>();
	private final List<GridBagConstraints> constraints_left = new ArrayList<>();
	private final List<GridBagConstraints> constraints_right = new ArrayList<>();
	private final List<Integer> rowHeights = new ArrayList<>();
	private final List<Double> rowWeights = new ArrayList<>();

	private final List<JLabel> sidebarValueLabels = new ArrayList<>();

	public StatisticsDataListPanel() {
		String placeholder = "000000000000"; //$NON-NLS-1$

		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.Movies", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.ViewedMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.Series", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.Episodes", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.ViewedSer", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.Elements", placeholder)); //$NON-NLS-1$

		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.DurationMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.DurationSer", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.DurationSerMov", placeholder)); //$NON-NLS-1$

		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.SizeMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.SizeSer", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.SizeSerMov", placeholder)); //$NON-NLS-1$

		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.AvgLengthMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.AvgLengthSer", placeholder)); //$NON-NLS-1$

		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.AvgSizeMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.AvgSizeSer", placeholder)); //$NON-NLS-1$

		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.AvgImDb", placeholder)); //$NON-NLS-1$

		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.TimeMov", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.TimeSer", placeholder)); //$NON-NLS-1$
		sidebarValueLabels.add(addRow("StatisticsFrame.Sidebar.TimeSerMov", placeholder)); //$NON-NLS-1$

		setLayout(getGBLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		for (int i = 0; i < labels_left.size(); i++) {
			add(labels_left.get(i), constraints_left.get(i));
			add(labels_right.get(i), constraints_right.get(i));
		}
	}

	private JLabel addRow(String description, String value) {
		JLabel lblLeft = new JLabel(LocaleBundle.getString(description) + ": "); //$NON-NLS-1$
		JLabel lblRight = new JLabel(value);

		GridBagConstraints gbc_l = new GridBagConstraints();
		gbc_l.fill = GridBagConstraints.BOTH;
		gbc_l.insets = new Insets(0, 0, 5, 5);
		gbc_l.gridx = 0;
		gbc_l.gridy = rowHeights.size();

		GridBagConstraints gbc_r = new GridBagConstraints();
		gbc_r.insets = new Insets(0, 0, 5, 0);
		gbc_r.anchor = GridBagConstraints.WEST;
		gbc_r.fill = GridBagConstraints.VERTICAL;
		gbc_r.gridx = 1;
		gbc_r.gridy = rowHeights.size();

		labels_left.add(lblLeft);
		labels_right.add(lblRight);

		constraints_left.add(gbc_l);
		constraints_right.add(gbc_r);

		rowHeights.add(0);
		rowWeights.add(0.0);

		return lblRight;
	}

	private GridBagLayout getGBLayout() {
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[]{1, 18, 0};
		gbl.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};

		gbl.rowHeights = new int[rowHeights.size() + 1];
		gbl.rowWeights = new double[rowWeights.size() + 1];

		for (int i = 0; i < rowHeights.size(); i++) {
			gbl.rowHeights[i] = rowHeights.get(i);
			gbl.rowWeights[i] = rowWeights.get(i);
		}
		gbl.rowHeights[rowHeights.size()] = 0;
		gbl.rowWeights[rowWeights.size()] = Double.MIN_VALUE;

		return gbl;
	}

	public void init(CCMovieList movielist) {
		List<JLabel> lst = sidebarValueLabels; // Locale Alias

		lst.get(0).setText("" + movielist.getMovieCount()); //$NON-NLS-1$
		lst.get(1).setText("" + StatisticsHelper.getViewedCount(movielist.iteratorMovies().cast())); //$NON-NLS-1$
		lst.get(2).setText("" + movielist.getSeriesCount()); //$NON-NLS-1$
		lst.get(3).setText("" + movielist.getEpisodeCount()); //$NON-NLS-1$
		lst.get(4).setText("" + StatisticsHelper.getViewedCount(movielist.iteratorEpisodes().cast())); //$NON-NLS-1$
		lst.get(5).setText("" + movielist.getElementCount()); //$NON-NLS-1$

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

		lst.get(16).setText("" + StatisticsHelper.getAvgImDbRating(movielist)); //$NON-NLS-1$

		lst.get(17).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getViewedMovieDuration(movielist)));
		lst.get(18).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getViewedSeriesDuration(movielist)));
		lst.get(19).setText(TimeIntervallFormatter.formatPointed(StatisticsHelper.getViewedTotalDuration(movielist)));
	}

}
