package de.jClipCorn.gui.frames.mainFrame.clipTable;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.gui.guiComponents.SFixTable;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.table.renderer.TableDateRenderer;
import de.jClipCorn.table.renderer.TableFSKRenderer;
import de.jClipCorn.table.renderer.TableFilesizeRenderer;
import de.jClipCorn.table.renderer.TableFormatRenderer;
import de.jClipCorn.table.renderer.TableGenreRenderer;
import de.jClipCorn.table.renderer.TableLanguageRenderer;
import de.jClipCorn.table.renderer.TableLengthRenderer;
import de.jClipCorn.table.renderer.TableOnlinescoreRenderer;
import de.jClipCorn.table.renderer.TablePartRenderer;
import de.jClipCorn.table.renderer.TableQualityRenderer;
import de.jClipCorn.table.renderer.TableScoreRenderer;
import de.jClipCorn.table.renderer.TableTagsRenderer;
import de.jClipCorn.table.renderer.TableTitleRenderer;
import de.jClipCorn.table.renderer.TableViewedRenderer;
import de.jClipCorn.table.renderer.TableYearRenderer;
import de.jClipCorn.table.renderer.TableZyklusRenderer;
import de.jClipCorn.table.sorter.TableDateComparator;
import de.jClipCorn.table.sorter.TableFSKComparator;
import de.jClipCorn.table.sorter.TableFormatComparator;
import de.jClipCorn.table.sorter.TableGenreComparator;
import de.jClipCorn.table.sorter.TableIntelliTitleComparator;
import de.jClipCorn.table.sorter.TableLanguageComparator;
import de.jClipCorn.table.sorter.TableLengthComparator;
import de.jClipCorn.table.sorter.TableOnlineScoreComparator;
import de.jClipCorn.table.sorter.TablePartComparator;
import de.jClipCorn.table.sorter.TableQualityComparator;
import de.jClipCorn.table.sorter.TableScoreComparator;
import de.jClipCorn.table.sorter.TableSizeComparator;
import de.jClipCorn.table.sorter.TableTagsComparator;
import de.jClipCorn.table.sorter.TableTitleComparator;
import de.jClipCorn.table.sorter.TableViewedComparator;
import de.jClipCorn.table.sorter.TableYearComparator;
import de.jClipCorn.table.sorter.TableZyklusComparator;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

public class SFixClipTable extends SFixTable {
	private static final long serialVersionUID = 5729868556740947063L;
	
	private TableRowSorter<ClipTableModel> sorter;

	private TableScoreRenderer renderer_score;
	private TableViewedRenderer renderer_viewed;
	private TableTitleRenderer renderer_title;
	private TableZyklusRenderer renderer_zyklus;
	private TableQualityRenderer renderer_quality;
	private TableLanguageRenderer renderer_language;
	private TableGenreRenderer renderer_genre;
	private TablePartRenderer renderer_parts;
	private TableLengthRenderer renderer_length;
	private TableTagsRenderer renderer_tags;
	private TableDateRenderer renderer_adddate;
	private TableOnlinescoreRenderer renderer_onlinescore;
	private TableFSKRenderer renderer_fsk;
	private TableFormatRenderer renderer_format;
	private TableYearRenderer renderer_year;
	private TableFilesizeRenderer renderer_filesize;
	
	private TableScoreComparator sorter_score;
	private TableTitleComparator sorter_title;
	private TableViewedComparator sorter_viewed;
	private TableZyklusComparator sorter_zyklus;
	private TableQualityComparator sorter_quality;
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
		renderer_score = new TableScoreRenderer();
		renderer_viewed = new TableViewedRenderer();
		renderer_title = new TableTitleRenderer();
		renderer_zyklus = new TableZyklusRenderer();
		renderer_quality = new TableQualityRenderer();
		renderer_language = new TableLanguageRenderer();
		renderer_genre = new TableGenreRenderer();
		renderer_parts = new TablePartRenderer();
		renderer_length = new TableLengthRenderer();
		renderer_tags = new TableTagsRenderer();
		renderer_adddate = new TableDateRenderer();
		renderer_onlinescore = new TableOnlinescoreRenderer();
		renderer_fsk = new TableFSKRenderer();
		renderer_format = new TableFormatRenderer();
		renderer_year = new TableYearRenderer();
		renderer_filesize = new TableFilesizeRenderer();
	}
	
	private void createRowSorter() {
		sorter_score = new TableScoreComparator();
		if (CCProperties.getInstance().PROP_USE_INTELLISORT.getValue()) {
			sorter_title = new TableIntelliTitleComparator();
		} else {
			sorter_title = new TableTitleComparator();
		}
		sorter_viewed = new TableViewedComparator();
		sorter_zyklus = new TableZyklusComparator();
		sorter_quality = new TableQualityComparator();
		sorter_language = new TableLanguageComparator();
		sorter_genre = new TableGenreComparator();
		sorter_parts = new TablePartComparator();
		sorter_length = new TableLengthComparator();
		sorter_tags = new TableTagsComparator();
		sorter_date = new TableDateComparator();
		sorter_onlinescore = new TableOnlineScoreComparator();
		sorter_fsk = new TableFSKComparator();
		sorter_format = new TableFormatComparator();
		sorter_year = new TableYearComparator();
		sorter_size = new TableSizeComparator();
	}
	
	private void setRowSorter() {
		sorter = new TableRowSorter<>((ClipTableModel) getModel());
		setRowSorter(sorter);
		
		sorter.setComparator(ClipTableModel.COLUMN_SCORE,  sorter_score);
		sorter.setComparator(ClipTableModel.COLUMN_TITLE,  sorter_title);
		sorter.setComparator(ClipTableModel.COLUMN_VIEWED,  sorter_viewed);
		sorter.setComparator(ClipTableModel.COLUMN_ZYKLUS,  sorter_zyklus);
		sorter.setComparator(ClipTableModel.COLUMN_QUALITY,  sorter_quality);
		sorter.setComparator(ClipTableModel.COLUMN_LANGUAGE,  sorter_language);
		sorter.setComparator(ClipTableModel.COLUMN_GENRE,  sorter_genre);
		sorter.setComparator(ClipTableModel.COLUMN_PARTCOUNT,  sorter_parts);
		sorter.setComparator(ClipTableModel.COLUMN_LENGTH,  sorter_length);
		sorter.setComparator(ClipTableModel.COLUMN_TAGS, sorter_tags);
		sorter.setComparator(ClipTableModel.COLUMN_DATE,  sorter_date);
		sorter.setComparator(ClipTableModel.COLUMN_ONLINESCORE, sorter_onlinescore);
		sorter.setComparator(ClipTableModel.COLUMN_FSK, sorter_fsk);
		sorter.setComparator(ClipTableModel.COLUMN_FORMAT, sorter_format);
		sorter.setComparator(ClipTableModel.COLUMN_YEAR, sorter_year);
		sorter.setComparator(ClipTableModel.COLUMN_SIZE, sorter_size);
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
		case ClipTableModel.COLUMN_QUALITY:
			return renderer_quality;
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
			case ClipTableModel.COLUMN_QUALITY:
				return null;
			case ClipTableModel.COLUMN_LANGUAGE:
				return null;
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
}
