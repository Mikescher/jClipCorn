package de.jClipCorn.gui.frames.watchHistoryFrame.table;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.guiComponents.SFixTable;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

public class SFixWatchHistoryTable extends SFixTable {
	private static final long serialVersionUID = 5729868556740947063L;
	
	private WatchHistoryRenderer renderer_name;
	private WatchHistoryRenderer renderer_date;
	private WatchHistoryRenderer renderer_quality;
	private WatchHistoryRenderer renderer_language;
	private WatchHistoryRenderer renderer_length;
	private WatchHistoryRenderer renderer_tags;
	private WatchHistoryRenderer renderer_format;
	private WatchHistoryRenderer renderer_size;

	public SFixWatchHistoryTable(TableModel dm) {
		super(dm);
		renderer_name = new WatchHistoryRenderer(WatchHistoryTableModel.COLUMN_NAME);
		renderer_date = new WatchHistoryRenderer(WatchHistoryTableModel.COLUMN_DATE); 
		renderer_quality = new WatchHistoryRenderer(WatchHistoryTableModel.COLUMN_QUALITY);
		renderer_language = new WatchHistoryRenderer(WatchHistoryTableModel.COLUMN_LANGUAGE);
		renderer_length = new WatchHistoryRenderer(WatchHistoryTableModel.COLUMN_LENGTH);
		renderer_tags = new WatchHistoryRenderer(WatchHistoryTableModel.COLUMN_TAGS);
		renderer_format = new WatchHistoryRenderer(WatchHistoryTableModel.COLUMN_FORMAT);
		renderer_size = new WatchHistoryRenderer(WatchHistoryTableModel.COLUMN_SIZE);
		
		
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "null"); //$NON-NLS-1$
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "null"); //$NON-NLS-1$
		
		this.getTableHeader().setReorderingAllowed(false);
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		column = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...
		
		switch (column) {
		case WatchHistoryTableModel.COLUMN_NAME:
			return renderer_name;
		case WatchHistoryTableModel.COLUMN_DATE:
			return renderer_date;
		case WatchHistoryTableModel.COLUMN_QUALITY:
			return renderer_quality;
		case WatchHistoryTableModel.COLUMN_LANGUAGE:
			return renderer_language;
		case WatchHistoryTableModel.COLUMN_LENGTH:
			return renderer_length;
		case WatchHistoryTableModel.COLUMN_TAGS:
			return renderer_tags;
		case WatchHistoryTableModel.COLUMN_FORMAT:
			return renderer_format;
		case WatchHistoryTableModel.COLUMN_SIZE:
			return renderer_size;
		default:
			CCLog.addError(new Exception("Mysterious switch jump in [SFixTable.java]")); //$NON-NLS-1$
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
		WatchHistoryElement el = (WatchHistoryElement)value;
		
		switch (column) {
			case WatchHistoryTableModel.COLUMN_NAME:
				return null;
			case WatchHistoryTableModel.COLUMN_DATE:
				return el.getTimestamp().toStringUINormal();
			case WatchHistoryTableModel.COLUMN_QUALITY:
				return null;
			case WatchHistoryTableModel.COLUMN_LENGTH:
				return TimeIntervallFormatter.format(el.getLength());
			case WatchHistoryTableModel.COLUMN_LANGUAGE:
				return null;
			case WatchHistoryTableModel.COLUMN_TAGS:
				return el.getTags().getAsString();
			case WatchHistoryTableModel.COLUMN_FORMAT:
				return null;
			case WatchHistoryTableModel.COLUMN_SIZE:
				return FileSizeFormatter.formatBytes(el.getSize());
			default:
				return null;
	    }
	}
}
