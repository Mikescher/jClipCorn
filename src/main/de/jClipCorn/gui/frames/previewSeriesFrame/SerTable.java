package de.jClipCorn.gui.frames.previewSeriesFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.features.actionTree.menus.impl.ClipEpisodePopup;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.jCCPrimaryTable.JCCPrimaryColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCPrimaryTable.JCCPrimaryTable;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.table.ClipTable;
import de.jClipCorn.properties.enumerations.SeriesFrameColumn;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.HTMLFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SerTable extends JCCPrimaryTable<CCEpisode, SeriesFrameColumn> {

	private final PreviewSeriesFrame owner;

	private CCSeason season = null;
	private boolean hasScore = true;
	private boolean hasTags = true;

	@DesignCreate
	private static ClipTable designCreate() { return new ClipTable(CCMovieList.createStub(), null); }

	public SerTable(CCMovieList ml, PreviewSeriesFrame owner) {
		super(ml);
		this.owner = owner;
		autoResize();
	}

	@Override
	@SuppressWarnings("HardCodedStringLiteral")
	protected List<JCCPrimaryColumnPrototype<CCEpisode, SeriesFrameColumn>> configureColumns() {
		var ccr = new ArrayList<JCCPrimaryColumnPrototype<CCEpisode, SeriesFrameColumn>>();

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.USERSCORE,
			Str.Empty,
			"auto,min=24",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.UserScore"),
			(r,v) -> r.setIcon(v.Score.get().getIcon(!Str.isNullOrWhitespace(v.ScoreComment.get()))),
			(r) -> false,
			(v1,v2) -> CCUserScore.compare(v1.Score.get(), v2.Score.get()),
			false,
			(v,row) -> formatScoreTooltip(v.Score.get(), v.ScoreComment.get()),
			(v) -> false,
			(v) -> noop(),
			() -> !hasScore
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.EPISODE,
			"#",
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Episode"),
			(r,v) -> r.setText(v.EpisodeNumber.get().toString()),
			(r) -> true,
			(v1,v2) -> Integer.compare(v1.EpisodeNumber.get(), v2.EpisodeNumber.get()),
			false,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.TITLE,
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Name"),
			"*,min=auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Name"),
			(r,v) -> r.setText(v.Title.get()),
			(r) -> true,
			(v1,v2) -> v1.Title.get().compareToIgnoreCase(v2.Title.get()),
			false,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.VIEWED,
			Str.Empty,
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Viewed"),
			(r,v) -> r.setIcon(v.getExtendedViewedState().getIconTable(movielist)),
			(r) -> false,
			(v1,v2) -> compareCoalesce(v1.getExtendedViewedState().getType().compareTo(v2.getExtendedViewedState().getType()), Integer.compare(v2.getExtendedViewedState().getViewCount(), v1.getExtendedViewedState().getViewCount())),
			false,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.LASTVIEWED,
			getLastViewedHeader(),
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.LastViewed"),
			(r,v) ->
			{
				var d = v.ViewedHistory.get();
				if (d.isEmptyOrOnlyUnspecified()) { r.setText(" - "); return; }
				switch (ccprops().PROP_SERIES_DISPLAYED_DATE.getValue())
				{
					case AVERAGE:      r.setText(d.getAverageDateOrInvalid().toStringUINormal()); break;
					case FIRST_VIEWED: r.setText(d.getFirstDateOrInvalid().toStringUINormal());   break;
					case LAST_VIEWED:  r.setText(d.getLastDateOrInvalid().toStringUINormal());    break;
					default:           r.setText("??_ERR_??");                                    break;
				}
			},
			(r) -> true,
			(v1,v2) ->
			{
				var o1 = CCDate.getUnspecified();
				switch (ccprops().PROP_SERIES_DISPLAYED_DATE.getValue())
				{
					case AVERAGE:      o1 = v1.ViewedHistory.get().getAverageDateOrInvalid(); break;
					case FIRST_VIEWED: o1 = v1.ViewedHistory.get().getFirstDateOrInvalid();   break;
					case LAST_VIEWED:  o1 = v1.ViewedHistory.get().getLastDateOrInvalid();    break;
				}
				var o2 = CCDate.getUnspecified();
				switch (ccprops().PROP_SERIES_DISPLAYED_DATE.getValue())
				{
					case AVERAGE:      o2 = v2.ViewedHistory.get().getAverageDateOrInvalid(); break;
					case FIRST_VIEWED: o2 = v2.ViewedHistory.get().getFirstDateOrInvalid();   break;
					case LAST_VIEWED:  o2 = v2.ViewedHistory.get().getLastDateOrInvalid();    break;
				}
				return CCDate.compare(o1, o2);
			},
			false,
			(v,row) -> v.getExtendedViewedState().getHistory().any() ? v.getExtendedViewedState().getHistory().getHTMLListFormatted(row) : null,
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.QUALITY,
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Quality"),
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Quality"),
			(r,v) -> { var cat = v.getMediaInfoCategory(); r.setText(cat.getShortText()); r.setIcon(cat.getIcon()); },
			(r) -> true,
			(v1,v2) -> CCQualityCategory.compare(v1.getMediaInfoCategory(), v2.getMediaInfoCategory()),
			false,
			(v,row) -> v.getMediaInfoCategory().getTooltip(),
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.LANGUAGE,
			Str.Empty,
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Language"),
			(r,v) -> { r.setHorizontalAlignment(SwingConstants.CENTER); r.setIcon(v.Language.get().getIcon()); },
			(r) -> true,
			(v1,v2) -> CCDBLanguageSet.compare(v1.Language.get(), v2.Language.get()),
			false,
			(v,row) -> v.Language.get().toTooltipString(),
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.SUBTITLES,
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Subtitles"),
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Subtitles"),
			(r,v) -> { r.setHorizontalAlignment(SwingConstants.LEFT); r.setIcon(v.Subtitles.get().getIcon(ccprops().PROP_TABLE_MAX_SUBTITLE_COUNT.getValue())); },
			(r) -> true,
			(v1,v2) -> CCDBLanguageList.compare(v1.Subtitles.get(), v2.Subtitles.get()),
			false,
			(v,row) -> v.Subtitles.get().toTooltipString(),
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.LENGTH,
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Length"),
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Length"),
			(r,v) -> r.setText(TimeIntervallFormatter.formatShort(v.Length.get())),
			(r) -> true,
			(v1,v2) -> Integer.compare(v1.Length.get(), v2.Length.get()),
			false,
			(v,row) -> TimeIntervallFormatter.format(v.Length.get()),
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.TAGS,
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Tags"),
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Tags"),
			(r,v) -> r.setIcon(v.Tags.get().getIcon()),
			(r) -> false,
			(v1,v2) -> CCTagList.compare(v1.Tags.get(), v2.Tags.get()),
			false,
			(v,row) -> v.Tags.get().getAsString(),
			(v) -> false,
			(v) -> noop(),
			() -> !hasTags
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.ADDDATE,
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Added"),
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Added"),
			(r,v) -> r.setText(v.AddDate.get().isMinimum() ? " - " :  v.AddDate.get().toStringUINormal()),
			(r) -> true,
			(v1,v2) ->
			{
				var d1 = v1.AddDate.get();
				var d2 = v2.AddDate.get();

				if (d1.isMinimum()) d1 = CCDate.getMaximumDate();
				if (d2.isMinimum()) d2 = CCDate.getMaximumDate();

				return CCDate.compare(d1, d2);
			},
			false,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.FORMAT,
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Format"),
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Format"),
			(r,v) -> { r.setText(v.Format.get().asString()); r.setIcon(v.Format.get().getIcon()); },
			(r) -> true,
			(v1,v2) -> CCFileFormat.compare(v1.Format.get(), v2.Format.get()),
			false,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			SeriesFrameColumn.FILESIZE,
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Size"),
			"auto",
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Size"),
			(r,v) -> r.setText(v.FileSize.get().getFormatted()),
			(r) -> true,
			(v1,v2) -> CCFileSize.compare(v1.FileSize.get(), v2.FileSize.get()),
			false,
			(v,row) -> FileSizeFormatter.formatBytes(v.FileSize.get()),
			(v) -> false,
			(v) -> noop(),
			() -> false
		));

		return ccr;
	}

	private String formatScoreTooltip(CCUserScore score, String comm) {
		if (score == CCUserScore.RATING_NO) return null;
		if (Str.isNullOrWhitespace(comm)) return score.asString();

		return HTMLFormatter.formatTooltip(score.asString(), Str.trim(comm));
	}

	private void autoResize() {
		adjuster.adjustColumns(getDefaultAdjusterConfig());
	}

	@Override
	protected void onElementSelected(CCEpisode elem) {
		// .
	}

	@Override
	protected void onElementClicked(CCEpisode elem, int clickCount, int button) {
		if (clickCount == 2 && button == MouseEvent.BUTTON1) {
			owner.onEpisodeDblClick(elem);
		}
	}

	@Override
	protected void onElementPopupTrigger(CCEpisode elem, MouseEvent e) {
		new ClipEpisodePopup(owner, elem).show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public int getElementCountInDatastore() {
		if (season == null) return 0;
		return season.getEpisodeCount();
	}

	@Override
	public CCEpisode getElementFromDatastoreByIndex(int dsrow) {
		if (season == null) return null;
		return season.getEpisodeByArrayIndex(dsrow);
	}

	@Override
	public Opt<Color> getRowColor(int visualrow, int modelrow, CCEpisode element) {
		return Opt.empty();
	}

	@Override
	public Opt<Integer> getUnitScrollIncrement() {
		return Opt.empty();
	}

	@Override
	public Opt<Integer> getBlockScrollIncrement() {
		return Opt.empty();
	}

	@Override
	public void requestFocus() {
		table.requestFocus();
		if (table.getRowCount() > 0 && table.getColumnCount() > 0 && table.getSelectedRow() == -1) table.changeSelection(0, 0, false, false);
		else if (table.getRowCount() > 0 && table.getColumnCount() > 0 && table.getSelectedRow() >= 0) table.changeSelection(table.getSelectedRow(), 0, false, false);
	}

	@Override
	public boolean isFocusOwner() {
		return table.isFocusOwner();
	}

	private String getLastViewedHeader() {
		switch (ccprops().PROP_SERIES_DISPLAYED_DATE.getValue()) {
			case LAST_VIEWED:
				return LocaleBundle.getString("PreviewSeriesFrame.serTable.ViewedHistory_1"); //$NON-NLS-1$
			case FIRST_VIEWED:
				return LocaleBundle.getString("PreviewSeriesFrame.serTable.ViewedHistory_2"); //$NON-NLS-1$
			case AVERAGE:
				return LocaleBundle.getString("PreviewSeriesFrame.serTable.ViewedHistory_3"); //$NON-NLS-1$
			default:
				CCLog.addUndefinied("SerTableModel :: ccprops == null"); //$NON-NLS-1$
				return "__ERROR__";
		}
	}

	public void changeSeason(CCSeason s) {
		this.season = s;
		this.hasScore = (s == null) ? false : s.iteratorEpisodes().any(e -> e.Score.get() != CCUserScore.RATING_NO || !Str.isNullOrWhitespace(e.ScoreComment.get()));
		this.hasTags  = (s == null) ? false : s.iteratorEpisodes().any(e -> e.Tags.get().hasTags());

		getVerticalScrollBar().setValue(0);

		model.fireTableDataChanged();

		autoResize();
	}

	public CCSeason getSeason() {
		return season;
	}

	public void select(CCEpisode e) {
		if (e == null) {
			changeSeason(null);
			table.getSelectionModel().clearSelection();
		} else {
			changeSeason(e.getSeason());
			table.getSelectionModel().setSelectionInterval(e.getEpisodeIndexInSeason(), e.getEpisodeIndexInSeason());
			table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), false));
		}
	}
}
