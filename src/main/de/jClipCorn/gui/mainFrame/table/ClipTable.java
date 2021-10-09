package de.jClipCorn.gui.mainFrame.table;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.TableCustomFilter;
import de.jClipCorn.features.table.filter.customFilter.CustomUserScoreFilter;
import de.jClipCorn.features.table.filter.customFilter.CustomZyklusFilter;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.guiComponents.jCCPrimaryTable.JCCPrimaryColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCPrimaryTable.JCCPrimaryTable;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.enumerations.MainFrameColumn;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.CCDBUpdateAdapter;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

@SuppressWarnings("restriction")
public class ClipTable extends JCCPrimaryTable<CCDatabaseElement, MainFrameColumn> {
	private static final long serialVersionUID = -1226727910191440220L;

	private final MainFrame owner;

	private TableCustomFilter currentFilter = null;
	private boolean suppressRowFilterResetEvents = false;

	private String _adjusterConfig = Str.Empty;

	private final static Color COLOR_BACKGROUNDGRAY     = new Color(240, 240, 240); // F0F0F0 (clBtnFace)
	private final static Color COLOR_BACKGROUNDDARKGRAY = new Color(16,   16,  16); // 1 - COLOR_BACKGROUNDGRAY

	private final static Color[] COLOR_ONLINESCORE = {
			new Color(0xFF4900),
			new Color(0xFF7400),
			new Color(0xFF9200),
			new Color(0xFFAA00),
			new Color(0xFFBF00),
			new Color(0xFFD300),
			new Color(0xFFE800),
			new Color(0xFFFF00),
			new Color(0xCCF600),
			new Color(0x9FEE00),
			new Color(0x67E300)
	};

	@DesignCreate
	private static ClipTable designCreate() { return new ClipTable(CCMovieList.createStub(), null); }

	public ClipTable(CCMovieList ml, MainFrame owner) {
		super(ml);
		this.owner = owner;

		postInit();
	}

	@Override
	@SuppressWarnings("HardCodedStringLiteral")
	protected List<JCCPrimaryColumnPrototype<CCDatabaseElement, MainFrameColumn>> configureColumns() {
		var ccr = new ArrayList<JCCPrimaryColumnPrototype<CCDatabaseElement, MainFrameColumn>>();

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.USERSCORE,
			Str.Empty,
			"auto",
			LocaleBundle.getString("ClipTableModel.Score"),
			(r,v) -> r.setIcon(v.Score.get().getIcon()),
			(r) -> false,
			(v1,v2) -> CCUserScore.compare(v1.Score.get(), v2.Score.get()),
			true,
			(v,row) -> v.Score.get() == CCUserScore.RATING_NO ? null : v.Score.get().asString(),
			(v) -> ccprops().PROP_MAINFRAME_CLICKABLESCORE.getValue() && v.Score.get() != CCUserScore.RATING_NO,
			(v) -> setRowFilter(CustomUserScoreFilter.create(owner.getMovielist(), v.Score.get()), RowFilterSource.TABLE_CLICKED)
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.TITLE,
			LocaleBundle.getString("ClipTableModel.Title"),
			"*,min=auto",
			LocaleBundle.getString("ClipTableModel.Title"),
			(r,v) -> { r.setText(v.getTitle()); r.setIcon(v.isSeries() ? Resources.ICN_TABLE_SERIES.get() : null); },
			(r) -> true,
			(v1,v2) -> movielist.ccprops().PROP_USE_INTELLISORT.getValue() ? compareTitleIntelligent(v1, v2) : v1.getTitle().compareToIgnoreCase(v2.getTitle()),
			true,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.VIEWED,
			Str.Empty,
			"auto",
			LocaleBundle.getString("ClipTableModel.Viewed"),
			(r,v) -> r.setIcon(v.getExtendedViewedState().getIconTable(r.getMovieList())),
			(r) -> false,
			(v1,v2) -> compareCoalesce(v1.getExtendedViewedState().getType().compareTo(v2.getExtendedViewedState().getType()), Integer.compare(v2.getExtendedViewedState().getViewCount(), v1.getExtendedViewedState().getViewCount())),
			true,
			(v,row) -> v.getExtendedViewedState().getHistory().any() ? v.getExtendedViewedState().getHistory().getHTMLListFormatted(row) : null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.LASTVIEWED,
			LocaleBundle.getString("ClipTableModel.LastViewed"),
			"auto",
			LocaleBundle.getString("ClipTableModel.LastViewed"),
			(r,v) ->
			{
				var ts = v.getLastViewed();
				if (!ts.isPresent()) { r.setText(Str.Empty); }
				else if (ts.get().isMinimum()) { r.setText("-"); }
				else { r.setText(ts.get().toStringUIDateOnly()); }
			},
			(r) -> true,
			(v1,v2) ->
			{
				var o1 = v1.getLastViewed();
				var o2 = v2.getLastViewed();
				if (!o1.isPresent() && !o2.isPresent()) return 0;
				if (!o1.isPresent()) return +1;
				if (!o2.isPresent()) return -1;
				return -1 * CCDateTime.compare(o1.get(), o2.get());
			},
			true,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.ZYKLUS,
			LocaleBundle.getString("ClipTableModel.Zyklus"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Zyklus"),
			(r,v) ->
			{
				if (v.isMovie()) {
					if (r.ccprops().PROP_MAINFRAME_CLICKABLEZYKLUS.getValue()) {
						if (r.ccprops().PROP_MAINFRAME_DONTCHANGEZYKLUSCOLOR.getValue()) {
							r.setText(v.asMovie().Zyklus.get().getSimpleHTMLFormatted());
						} else {
							r.setText(v.asMovie().Zyklus.get().getHTMLFormatted());
						}
					} else {
						r.setText(v.asMovie().Zyklus.get().getFormatted());
					}
				}
			},
			(r) -> true,
			(v1,v2) ->
			{
				var o1 = v1.isMovie() ? v1.asMovie().Zyklus.get() : new CCMovieZyklus();
				var o2 = v2.isMovie() ? v2.asMovie().Zyklus.get() : new CCMovieZyklus();
				return CCMovieZyklus.compare(o1, o2);
			},
			true,
			(v,row) -> v.isMovie() && v.asMovie().Zyklus.get().isSet() && v.asMovie().Zyklus.get().hasNumber() ? v.asMovie().Zyklus.get().getDecimalFormatted() : null,
			(v) -> ccprops().PROP_MAINFRAME_CLICKABLEZYKLUS.getValue() && v.isMovie() && v.asMovie().Zyklus.get().isSet(),
			(v) -> { if (v.isMovie()) setRowFilter(CustomZyklusFilter.create(owner.getMovielist(), v.asMovie().Zyklus.get()), RowFilterSource.TABLE_CLICKED); }
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.QUALITY,
			LocaleBundle.getString("ClipTableModel.Quality"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Quality"),
			(r,v) -> { var cat = v.getMediaInfoCategory(); r.setText(cat.getShortText()); r.setIcon(cat.getIcon()); },
			(r) -> true,
			(v1,v2) -> CCQualityCategory.compare(v1.getMediaInfoCategory(), v2.getMediaInfoCategory()),
			true,
			(v,row) -> v.getMediaInfoCategory().getTooltip(),
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.LANGUAGE,
			Str.Empty,
			"auto",
			LocaleBundle.getString("ClipTableModel.Language"),
			(r,v) -> { r.setHorizontalAlignment(SwingConstants.CENTER); r.setIcon((v.isMovie() ? v.asMovie().Language.get() : v.asSeries().getSemiCommonOrAllLanguages()).getIcon()); } ,
			(r) -> true,
			(v1,v2) ->
			{
				var o1 = (v1.isMovie() ? v1.asMovie().Language.get() : v1.asSeries().getSemiCommonOrAllLanguages());
				var o2 = (v2.isMovie() ? v2.asMovie().Language.get() : v2.asSeries().getSemiCommonOrAllLanguages());
				return CCDBLanguageSet.compare(o1, o2);
			},
			true,
			(v,row) -> (v.isMovie() ? v.asMovie().Language.get() : v.asSeries().getSemiCommonOrAllLanguages()).toOutputString(),
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.SUBTITLES,
			LocaleBundle.getString("ClipTableModel.Subtitle"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Subtitle"),
			(r,v) -> { r.setHorizontalAlignment(SwingConstants.LEFT); r.setIcon((v.isMovie() ? v.asMovie().Subtitles.get() : v.asSeries().getAllSubtitles()).getIcon()); } ,
			(r) -> true,
			(v1,v2) ->
			{
				var o1 = (v1.isMovie() ? v1.asMovie().Subtitles.get() : v1.asSeries().getAllSubtitles());
				var o2 = (v2.isMovie() ? v2.asMovie().Subtitles.get() : v2.asSeries().getAllSubtitles());
				return CCDBLanguageList.compare(o1, o2);
			},
			true,
			(v,row) -> (v.isMovie() ? v.asMovie().Subtitles.get() : v.asSeries().getAllSubtitles()).toOutputString(),
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.GENRES,
			LocaleBundle.getString("ClipTableModel.Genre"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Genre"),
			(r,v) -> r.setText(r.ccprops().PROP_MAINFRAME_SORT_GENRES.getValue() ? v.Genres.get().asSortedString() : v.Genres.get().asString()),
			(r) -> true,
			(v1,v2) -> CCGenreList.compare(v1.Genres.get(), v2.Genres.get()),
			true,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.PARTS,
			LocaleBundle.getString("ClipTableModel.Parts"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Parts"),
			(r,v) -> { /* stub */ },
			(r) -> true,
			(v1,v2) ->
			{
				var o1 = (v1.isMovie() ? v1.asMovie().getPartcount() : v1.asSeries().getEpisodeCount());
				var o2 = (v2.isMovie() ? v2.asMovie().getPartcount() : v2.asSeries().getEpisodeCount());
				return Integer.compare(o1, o2);
			},
			true,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.LENGTH,
			LocaleBundle.getString("ClipTableModel.Length"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Length"),
			(r,v) -> r.setText(TimeIntervallFormatter.formatShort(v.isMovie() ? v.asMovie().Length.get() : v.asSeries().getLength())),
			(r) -> true,
			(v1,v2) ->
			{
				var o1 = (v1.isMovie() ? v1.asMovie().Length.get() : v1.asSeries().getLength());
				var o2 = (v2.isMovie() ? v2.asMovie().Length.get() : v2.asSeries().getLength());
				return Integer.compare(o1, o2);
			},
			true,
			(v,row) -> TimeIntervallFormatter.format((v.isMovie() ? v.asMovie().Length.get() : v.asSeries().getLength())),
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.ADDDATE,
			LocaleBundle.getString("ClipTableModel.Added"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Added"),
			(r,v) -> r.setText(v.getAddDate().isMinimum() ? " - " :  v.getAddDate().toStringUINormal()),
			(r) -> true,
			(v1,v2) ->
			{
				var d1 = v1.getAddDate();
				var d2 = v2.getAddDate();

				if (v1.isSeries() && d1.isMinimum() && v1.asSeries().isEmpty()) d1 = CCDate.getMaximumDate();
				if (v2.isSeries() && d2.isMinimum() && v2.asSeries().isEmpty()) d2 = CCDate.getMaximumDate();

				return CCDate.compare(d1, d2);
			},
			true,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.ONLINESCORE,
			LocaleBundle.getString("ClipTableModel.Score"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Score"),
			(r,v) -> r.setIcon(v.OnlineScore.get().getIcon()),
			(r) -> false,
			(v1,v2) -> CCOnlineScore.compare(v1.OnlineScore.get(), v2.OnlineScore.get()),
			true,
			(v,row) -> LocaleBundle.getString("CCMovieScore.Score") + ": " + v.OnlineScore.get().asInt(),
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.TAGS,
			LocaleBundle.getString("ClipTableModel.Tags"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Tags"),
			(r,v) -> r.setIcon(v.Tags.get().getIcon()),
			(r) -> false,
			(v1,v2) -> CCTagList.compare(v1.Tags.get(), v2.Tags.get()),
			true,
			(v,row) -> v.Tags.get().getAsString(),
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.FSK,
			LocaleBundle.getString("ClipTableModel.FSK"),
			"auto",
			LocaleBundle.getString("ClipTableModel.FSK"),
			(r,v) -> { r.setText(v.FSK.get().asString()); r.setIcon(v.FSK.get().getIcon()); },
			(r) -> true,
			(v1,v2) -> CCFSK.compare(v1.FSK.get(), v2.FSK.get()),
			true,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.FORMAT,
			LocaleBundle.getString("ClipTableModel.Format"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Format"),
			(r,v) -> { r.setText(v.getFormat().asString()); r.setIcon(v.getFormat().getIcon()); },
			(r) -> true,
			(v1,v2) -> CCFileFormat.compare(v1.getFormat(), v2.getFormat()),
			true,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.YEAR,
			LocaleBundle.getString("ClipTableModel.Year"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Year"),
			(r,v) -> { r.setText(v.isMovie() ? String.valueOf(v.asMovie().Year.get()) : v.asSeries().getYearRange().asString()); },
			(r) -> true,
			(v1,v2) ->
			{
				var o1 = v1.isMovie() ? new YearRange(v1.asMovie().Year.get()) : v1.asSeries().getYearRange();
				var o2 = v2.isMovie() ? new YearRange(v2.asMovie().Year.get()) : v2.asSeries().getYearRange();
				return YearRange.compare(o1, o2);
			},
			true,
			(v,row) -> null,
			(v) -> false,
			(v) -> noop()
		));

		ccr.add(new JCCPrimaryColumnPrototype<>
		(
			MainFrameColumn.FILESIZE,
			LocaleBundle.getString("ClipTableModel.Size"),
			"auto",
			LocaleBundle.getString("ClipTableModel.Size"),
			(r,v) -> r.setText(v.getFilesize().getFormatted()),
			(r) -> true,
			(v1,v2) -> CCFileSize.compare(v1.getFilesize(), v2.getFilesize()),
			true,
			(v,row) -> FileSizeFormatter.formatBytes(v.getFilesize()),
			(v) -> false,
			(v) -> noop()
		));

		return ccr;
	}

	protected void postInit()
	{
		var _adjusterConfig = (owner == null) ? "" : ccprops().PROP_MAINFRAME_COLUMN_SIZE_CACHE.getValue();
		if (Str.isNullOrWhitespace(_adjusterConfig)) _adjusterConfig = CCStreams.iterate(MainFrameColumn.values()).stringjoin(e-> "keep", "|"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!adjuster.isValidConfig(_adjusterConfig)) _adjusterConfig = CCStreams.iterate(MainFrameColumn.values()).stringjoin(e-> "10", "|"); //$NON-NLS-1$ //$NON-NLS-2$

		if (owner == null) CCLog.addUndefinied("ClipTable :: owner == null"); //$NON-NLS-1$

		adjuster.adjustColumns(_adjusterConfig);

		if (movielist != null) { // Sonst meckert der WindowsBuilder
			movielist.addChangeListener(new CCDBUpdateAdapter()
			{
				@Override
				public void onAddDatabaseElement(CCDatabaseElement mov) {
					model.fireTableDataChanged();
				}

				@Override
				public void onRemDatabaseElement(CCDatabaseElement el) {
					model.fireTableDataChanged();
				}

				@Override
				public void onChangeDatabaseElement(CCDatabaseElement root, ICCDatabaseStructureElement el, String[] props) {
					int row = root.getMovieListPosition();

					if (row > 0) model.fireTableRowsUpdated(row, row);
				}

				@Override
				public void onRefresh() {
					model.fireTableDataChanged();
				}

				@Override
				public void onAfterLoad() {
					model.fireTableDataChanged();
					table.setSortKey(getInitialSortKey());
					autoResize();

					var columnconfig = adjuster.getCurrentStateAsConfig();
					ccprops().PROP_MAINFRAME_COLUMN_SIZE_CACHE.setValueIfDiff(columnconfig);
				}
			});
		}
	}

	@Override
	protected void onElementSelected(CCDatabaseElement elem) {
		owner.onClipTableSelectionChanged(elem);
	}

	@Override
	protected void onElementClicked(CCDatabaseElement elem, int clickCount, int button) {
		if (clickCount == 2 && button == MouseEvent.BUTTON1) {
			owner.onClipTableExecute(elem);
		}
	}

	@Override
	protected void onElementPopupTrigger(CCDatabaseElement elem, MouseEvent e) {
		owner.onClipTableSecondaryExecute(elem, e);
	}

	@Override
	public int getElementCountInDatastore() {
		return movielist.getElementCount();
	}

	@Override
	public CCDatabaseElement getElementFromDatastoreByIndex(int row) {
		return movielist.getDatabaseElementBySort(row);
	}

	@Override
	public Opt<Color> getRowColor(int visualrow, int modelrow, CCDatabaseElement element) {
		switch (movielist.ccprops().PROP_MAINFRAME_TABLEBACKGROUND.getValue()) {
			case DEFAULT:
				return Opt.empty();
			case WHITE:
				return Opt.of(Color.WHITE);
			case STRIPED:
				return (visualrow%2==0) ? Opt.of(owner.getBackground()) : Opt.of(LookAndFeelManager.isDark() ? COLOR_BACKGROUNDDARKGRAY : COLOR_BACKGROUNDGRAY);
			case SCORE:
				return Opt.of(COLOR_ONLINESCORE[element.OnlineScore.get().asInt()]);
			default:
				return Opt.of(Color.MAGENTA);
		}
	}

	@Override
	public Opt<Integer> getUnitScrollIncrement() {
		return Opt.of(movielist.ccprops().PROP_MAINFRAME_SCROLLSPEED.getValue());
	}

	@Override
	public Opt<Integer> getBlockScrollIncrement() {
		return Opt.empty();
	}

	@SuppressWarnings("unchecked")
	public void setRowFilter(AbstractCustomFilter filterimpl, RowFilterSource source) { // Source kann null sein
		if (model.hasVolatileRowIndexMapping()) model.clearRowIndexMapping();

		if (!suppressRowFilterResetEvents) {

			suppressRowFilterResetEvents = true;

			if (source != RowFilterSource.CHARSELECTOR) {
				owner.resetCharSelector();
			}
			if (source != RowFilterSource.SIDEBAR) {
				owner.resetSidebar();
			}
			if (source != RowFilterSource.TEXTFIELD) {
				owner.resetSearchField(true);
			}

			suppressRowFilterResetEvents = false;

		}

		if (filterimpl == null) {
			currentFilter = null;
			table.setRowFilter(null);
		} else {
			currentFilter = new TableCustomFilter(filterimpl);
			table.setRowFilter(currentFilter);
		}

		if (! table.isSortedByColumn()) {
			table.setSortKey(getInitialSortKey());
		}

		owner.getStatusBar().updateLables_Movies();
	}

	public void configureColumnVisibility(Set<MainFrameColumn> data, boolean initial) {
		String[] cfg = new String[config.size()];
		Arrays.fill(cfg, "auto"); //$NON-NLS-1$

		for (var idx=0; idx < config.size(); idx++) {
			var ccfg = config.get(idx);
			if (data.contains(ccfg.Identifier)) {
				cfg[idx] = ccfg.AdjusterConfig;

				if (initial) continue;
				TableColumn column = getColumn(ccfg.Identifier);
				column.setMinWidth(0);
				column.setMaxWidth(Integer.MAX_VALUE);
				column.setPreferredWidth(128);
				if (column.getWidth() == 0) column.setWidth(50);

			} else {
				TableColumn column = getColumn(ccfg.Identifier);
				column.setMinWidth(0);
				column.setMaxWidth(0);
				column.setPreferredWidth(0);

				cfg[idx] = "0"; //$NON-NLS-1$
			}
		}

		_adjusterConfig = CCStreams.iterate(cfg).stringjoin(e->e, "|"); //$NON-NLS-1$
	}

	public void autoResize() {
		adjuster.adjustColumns(_adjusterConfig);
	}

	public void shuffle() {
		table.resetSort();

		var mapping = new ArrayList<Integer>(getRowCount());
		for (int i = 0; i < getRowCount(); i++) mapping.add(i);

		Collections.shuffle(mapping);

		model.setRowIndexMapping(mapping, true);
	}

	public TableColumn getColumn(MainFrameColumn c) {
		return table.getColumnModel().getColumn(c.getColumnModelIndex(config));
	}

	private RowSorter.SortKey getInitialSortKey() {
		switch (owner.ccprops().PROP_VIEW_DB_START_SORT.getValue()) {
			case LOCALID:
				//Do nothing
				return null;
			case TITLE:
				return new RowSorter.SortKey(MainFrameColumn.TITLE.getColumnModelIndex(config), SortOrder.ASCENDING);
			case ADDDATE:
				return new RowSorter.SortKey(MainFrameColumn.ADDDATE.getColumnModelIndex(config), SortOrder.DESCENDING);
		}

		return null;
	}

	public TableCustomFilter getRowFilter() {
		return currentFilter;
	}

	private int compareTitleIntelligent(CCDatabaseElement v1, CCDatabaseElement v2) {
		String titleA = v1.getTitle();
		String titleB = v2.getTitle();

		if (v1.isMovie() && v2.isMovie() && v1.asMovie().getZyklus().isSet() && v2.asMovie().getZyklus().isSet()) {
			if (v1.asMovie().getZyklus().getTitle().equals(v2.asMovie().getZyklus().getTitle())) { //In the same Zyklus
				return Integer.compare(v1.asMovie().getZyklus().getNumber(), v2.asMovie().getZyklus().getNumber());
			}
		}

		if (v1.isMovie() && v1.asMovie().getZyklus().isSet()) titleA = v1.asMovie().getZyklus().getTitle();

		if (v2.isMovie() && v2.asMovie().getZyklus().isSet()) titleB = v2.asMovie().getZyklus().getTitle();

		return titleA.compareToIgnoreCase(titleB);
	}

	public MainFrame getMainFrame() {
		return owner;
	}
}
