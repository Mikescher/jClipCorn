package de.jClipCorn.gui.mainFrame.clipTable;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.renderer.*;
import de.jClipCorn.features.table.sorter.*;
import de.jClipCorn.gui.guiComponents.SFixTable;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.MainFrameColumn;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class SFixClipTable extends SFixTable {
	private static final long serialVersionUID = 5729868556740947063L;
	
	private TableRowSorter<ClipTableModel> sorter;

	private TableScoreRenderer renderer_score;
	private TableViewedRenderer renderer_viewed;
	private ClipTableTitleRenderer renderer_title;
	private TableZyklusRenderer renderer_zyklus;
	private TableMediaInfoCatRenderer renderer_mediainfo;
	private TableLanguageRenderer renderer_language;
	private TableGenreRenderer renderer_genre;
	private TablePartRenderer renderer_parts;
	private TableLengthRenderer renderer_length;
	private TableTagsRenderer renderer_tags;
	private ClipTableDateRenderer renderer_adddate;
	private TableOnlinescoreRenderer renderer_onlinescore;
	private TableFSKRenderer renderer_fsk;
	private TableFormatRenderer renderer_format;
	private TableYearRenderer renderer_year;
	private TableFilesizeRenderer renderer_filesize;
	
	private TableScoreComparator sorter_score;
	private TableTitleComparator sorter_title;
	private TableViewedComparator sorter_viewed;
	private TableZyklusComparator sorter_zyklus;
	private TableMediaInfoCatComparator sorter_mediainfo;
	private TableLanguageComparator sorter_language;
	private TableGenreComparator sorter_genre;
	private TablePartComparator sorter_parts;
	private TableLengthComparator sorter_length;
	private TableTagsComparator sorter_tags;
	private TableDateComparator sorter_date;
	private TableOnlineScoreComparator sorter_onlinescore;
	private TableFSKComparator sorter_fsk;
	private TableFormatComparator sorter_format;
	private TableYearComparator sorter_year;
	private TableSizeComparator sorter_size;

	public SFixClipTable(TableModel dm) {
		super(dm);
		init();
		
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "null"); //$NON-NLS-1$
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "null"); //$NON-NLS-1$
		
		this.getTableHeader().setReorderingAllowed(false);
	}
	
	private void init() {
		createCellRenderer();
		createRowSorter();
		
		setRowSorter();
	}
	
	private void createCellRenderer() {
		renderer_score       = new TableScoreRenderer();
		renderer_viewed      = new TableViewedRenderer();
		renderer_title       = new ClipTableTitleRenderer();
		renderer_zyklus      = new TableZyklusRenderer();
		renderer_mediainfo   = new TableMediaInfoCatRenderer();
		renderer_language    = new TableLanguageRenderer();
		renderer_genre       = new TableGenreRenderer();
		renderer_parts       = new TablePartRenderer();
		renderer_length      = new TableLengthRenderer();
		renderer_tags        = new TableTagsRenderer();
		renderer_adddate     = new ClipTableDateRenderer();
		renderer_onlinescore = new TableOnlinescoreRenderer();
		renderer_fsk         = new TableFSKRenderer();
		renderer_format      = new TableFormatRenderer();
		renderer_year        = new TableYearRenderer();
		renderer_filesize    = new TableFilesizeRenderer();
	}
	
	private void createRowSorter() {
		sorter_score       = new TableScoreComparator();
		sorter_title       = CCProperties.getInstance().PROP_USE_INTELLISORT.getValue() ? new TableIntelliTitleComparator() : new TableTitleComparator();
		sorter_viewed      = new TableViewedComparator();
		sorter_zyklus      = new TableZyklusComparator();
		sorter_mediainfo   = new TableMediaInfoCatComparator();
		sorter_language    = new TableLanguageComparator();
		sorter_genre       = new TableGenreComparator();
		sorter_parts       = new TablePartComparator();
		sorter_length      = new TableLengthComparator();
		sorter_tags        = new TableTagsComparator();
		sorter_date        = new TableDateComparator();
		sorter_onlinescore = new TableOnlineScoreComparator();
		sorter_fsk         = new TableFSKComparator();
		sorter_format      = new TableFormatComparator();
		sorter_year        = new TableYearComparator();
		sorter_size        = new TableSizeComparator();
	}
	
	private void setRowSorter() {
		sorter = new TableRowSorter<>((ClipTableModel) getModel());
		setRowSorter(sorter);
		
		sorter.setComparator(ClipTableModel.COLUMN_SCORE,       sorter_score);
		sorter.setComparator(ClipTableModel.COLUMN_TITLE,       sorter_title);
		sorter.setComparator(ClipTableModel.COLUMN_VIEWED,      sorter_viewed);
		sorter.setComparator(ClipTableModel.COLUMN_ZYKLUS,      sorter_zyklus);
		sorter.setComparator(ClipTableModel.COLUMN_MEDIAINFO,   sorter_mediainfo);
		sorter.setComparator(ClipTableModel.COLUMN_LANGUAGE,    sorter_language);
		sorter.setComparator(ClipTableModel.COLUMN_GENRE,       sorter_genre);
		sorter.setComparator(ClipTableModel.COLUMN_PARTCOUNT,   sorter_parts);
		sorter.setComparator(ClipTableModel.COLUMN_LENGTH,      sorter_length);
		sorter.setComparator(ClipTableModel.COLUMN_TAGS,        sorter_tags);
		sorter.setComparator(ClipTableModel.COLUMN_DATE,        sorter_date);
		sorter.setComparator(ClipTableModel.COLUMN_ONLINESCORE, sorter_onlinescore);
		sorter.setComparator(ClipTableModel.COLUMN_FSK,         sorter_fsk);
		sorter.setComparator(ClipTableModel.COLUMN_FORMAT,      sorter_format);
		sorter.setComparator(ClipTableModel.COLUMN_YEAR,        sorter_year);
		sorter.setComparator(ClipTableModel.COLUMN_SIZE,        sorter_size);
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		column = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...
		
		switch (column) {
		case ClipTableModel.COLUMN_SCORE:
			return renderer_score;
		case ClipTableModel.COLUMN_TITLE:
			return renderer_title;
		case ClipTableModel.COLUMN_VIEWED:
			return renderer_viewed;
		case ClipTableModel.COLUMN_ZYKLUS:
			return renderer_zyklus;
		case ClipTableModel.COLUMN_MEDIAINFO:
			return renderer_mediainfo;
		case ClipTableModel.COLUMN_LANGUAGE:
			return renderer_language;
		case ClipTableModel.COLUMN_GENRE:
			return renderer_genre;
		case ClipTableModel.COLUMN_PARTCOUNT:
			return renderer_parts;
		case ClipTableModel.COLUMN_LENGTH:
			return renderer_length;
		case ClipTableModel.COLUMN_DATE:
			return renderer_adddate;
		case ClipTableModel.COLUMN_ONLINESCORE:
			return renderer_onlinescore;
		case ClipTableModel.COLUMN_TAGS:
			return renderer_tags;
		case ClipTableModel.COLUMN_FSK:
			return renderer_fsk;
		case ClipTableModel.COLUMN_FORMAT:
			return renderer_format;
		case ClipTableModel.COLUMN_YEAR:
			return renderer_year;
		case ClipTableModel.COLUMN_SIZE:
			return renderer_filesize;
		default:
			CCLog.addDefaultSwitchError(this, column);
			return super.getCellRenderer(row, column);//renderer_default;
		}
	}
	
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return CCProperties.getInstance().PROP_MAINFRAME_SCROLLSPEED.getValue();
	}
	
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return super.getScrollableBlockIncrement(visibleRect, orientation, direction);
	}

	@Override
	protected String getTooltip(int column, int row, Object value) {
		switch (column) {
			case ClipTableModel.COLUMN_SCORE:
				return ((CCUserScore)value == CCUserScore.RATING_NO) ? null : ((CCUserScore)value).asString();
			case ClipTableModel.COLUMN_TITLE:
				return null;
			case ClipTableModel.COLUMN_VIEWED:
				return (((ExtendedViewedState)value).getHistory().any()) ? ((ExtendedViewedState)value).getHistory().getHTMLListFormatted(row) : null;
			case ClipTableModel.COLUMN_ZYKLUS:
				if (((CCMovieZyklus)value).isSet() && ((CCMovieZyklus)value).hasNumber())
					return ((CCMovieZyklus)value).getDecimalFormatted();
				else
					return null;
			case ClipTableModel.COLUMN_MEDIAINFO:
				return ((CCQualityCategory)value).getTooltip();
			case ClipTableModel.COLUMN_LANGUAGE:
				return ((CCDBLanguageList)value).toOutputString();
			case ClipTableModel.COLUMN_GENRE:
				return null;
			case ClipTableModel.COLUMN_PARTCOUNT:
				return null;
			case ClipTableModel.COLUMN_LENGTH:
				return TimeIntervallFormatter.format(((int)value));
			case ClipTableModel.COLUMN_DATE:
				return null;
			case ClipTableModel.COLUMN_ONLINESCORE:
				return LocaleBundle.getString("CCMovieScore.Score") + ": " + ((CCOnlineScore)value).asInt();  //$NON-NLS-1$//$NON-NLS-2$
			case ClipTableModel.COLUMN_TAGS:
				return ((CCTagList) value).getAsString();
			case ClipTableModel.COLUMN_FSK:
				return null;
			case ClipTableModel.COLUMN_FORMAT:
				return null;
			case ClipTableModel.COLUMN_YEAR:
				return null;
			case ClipTableModel.COLUMN_SIZE:
				return FileSizeFormatter.formatBytes((CCFileSize)value);
			default:
				CCLog.addDefaultSwitchError(this, column);
				return null;
		}
	}

	public TableColumn getColumn(MainFrameColumn c) {
		return this.getColumnModel().getColumn(c.ColumnIndex);
	}
}
