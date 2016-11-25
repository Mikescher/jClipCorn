package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.gui.guiComponents.SFixTable;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableDateListRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableDateRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableEpisodeRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableFilesizeRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableFormatRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableLengthRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableQualityRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableStringTitleRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableTagsRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableViewedRenderer;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

public class SFixSerTable extends SFixTable {
	private static final long serialVersionUID = 6982359339154624097L;
	
	private TableEpisodeRenderer renderer_episode;
	private TableViewedRenderer renderer_viewed;
	private TableStringTitleRenderer renderer_title;
	private TableQualityRenderer renderer_quality;
	private TableLengthRenderer renderer_length;
	private TableTagsRenderer renderer_tags;
	private TableDateRenderer renderer_date;
	private TableDateListRenderer renderer_datelist;
	private TableFormatRenderer renderer_format;
	private TableFilesizeRenderer renderer_filesize;

	public SFixSerTable(TableModel dm) {
		super(dm);
		init();
		
		this.getTableHeader().setReorderingAllowed(false);
	}
	
	private void init() {
		createCellRenderer();
		//createRowSorter();
		
		//setRowSorter();
	}
	
	private void createCellRenderer() {
		renderer_episode = new TableEpisodeRenderer();
		renderer_viewed = new TableViewedRenderer();
		renderer_title = new TableStringTitleRenderer();
		renderer_quality = new TableQualityRenderer();
		renderer_length = new TableLengthRenderer();
		renderer_tags = new TableTagsRenderer();
		renderer_date = new TableDateRenderer();
		renderer_datelist = new TableDateListRenderer();
		renderer_format = new TableFormatRenderer();
		renderer_filesize = new TableFilesizeRenderer();
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		column = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...
		
		switch (column) {
		case SerTableModel.COLUMN_EPISODE:
			return renderer_episode;
		case SerTableModel.COLUMN_NAME:
			return renderer_title;
		case SerTableModel.COLUMN_VIEWED:
			return renderer_viewed;
		case SerTableModel.COLUMN_LASTVIEWED:
			return renderer_datelist;
		case SerTableModel.COLUMN_QUALITY:
			return renderer_quality;
		case SerTableModel.COLUMN_LENGTH:
			return renderer_length;
		case SerTableModel.COLUMN_TAGS:
			return renderer_tags;
		case SerTableModel.COLUMN_ADDDATE:
			return renderer_date;
		case SerTableModel.COLUMN_FORMAT:
			return renderer_format;
		case SerTableModel.COLUMN_SIZE:
			return renderer_filesize;
		default:
			CCLog.addDefaultSwitchError(this);
			return super.getCellRenderer(row, column); //renderer_default;
		}
	}

	@Override
	protected String getTooltip(int column, int row, Object value) {
		switch (column) {
		case SerTableModel.COLUMN_EPISODE:
			return null;
		case SerTableModel.COLUMN_NAME:
			return null;
		case SerTableModel.COLUMN_VIEWED:
			return null;
		case SerTableModel.COLUMN_LASTVIEWED:
			return (((CCDateTimeList)value).any()) ? ((CCDateTimeList)value).getHTMLListFormatted(row) : null;
		case SerTableModel.COLUMN_QUALITY:
			return null;
		case SerTableModel.COLUMN_LENGTH:
			return TimeIntervallFormatter.format(((int)value));
		case SerTableModel.COLUMN_TAGS:
			return ((CCMovieTags) value).getAsString();
		case SerTableModel.COLUMN_ADDDATE:
			return null;
		case SerTableModel.COLUMN_FORMAT:
			return null;
		case SerTableModel.COLUMN_SIZE:
			return FileSizeFormatter.formatBytes((CCMovieSize)value);
		default:
			CCLog.addDefaultSwitchError(this);
			return null;
		}
	}
}
