package de.jClipCorn.gui.frames.watchHistoryFrame.table;

import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableRenderer;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

public class WatchHistoryRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	private final int column;
	
	public WatchHistoryRenderer(int col) {
		super();
		column = col;
	}

	@Override
    public void setValue(Object value) {
		WatchHistoryElement el = (WatchHistoryElement)value;
		
		switch (column) {
		
		case WatchHistoryTableModel.COLUMN_NAME:
			setText(el.getName());
			setIcon(el.getNameIcon());
			setToolTipText(null);
			break;
			
		case WatchHistoryTableModel.COLUMN_DATE:
			setText(el.getTimestamp().getSimpleShortStringRepresentation());
			setIcon(null);
			setToolTipText(el.getTimestamp().getSimpleStringRepresentation());
			break;
			
		case WatchHistoryTableModel.COLUMN_QUALITY:
			setText(""); //$NON-NLS-1$
			setIcon(el.getQuality().getIcon());
			setToolTipText(null);
			break;
			
		case WatchHistoryTableModel.COLUMN_LENGTH:
			setText(TimeIntervallFormatter.formatShort(el.getLength()));
			setIcon(null);
			setToolTipText(TimeIntervallFormatter.format(el.getLength()));
			break;
			
		case WatchHistoryTableModel.COLUMN_LANGUAGE:
			setText(el.getLanguage().asString());
			setIcon(el.getLanguage().getIcon());
			setToolTipText(null);
			break;
			
		case WatchHistoryTableModel.COLUMN_TAGS:
			setText(""); //$NON-NLS-1$
			setIcon(el.getTags().getIcon());
			setToolTipText(el.getTags().getAsString());
			break;
			
		case WatchHistoryTableModel.COLUMN_FORMAT:
			setText(el.getFormat().asString());
			setIcon(el.getFormat().getIcon());
			setToolTipText(null);
			break;
			
		case WatchHistoryTableModel.COLUMN_SIZE:
			setText(el.getSize().getFormatted());
			setIcon(null);
			setToolTipText(FileSizeFormatter.formatBytes(el.getSize()));
			break;

		default:
			setText("???"); //$NON-NLS-1$
			setIcon(null);
			setToolTipText(null);
			break;
		}
		
    }
}
