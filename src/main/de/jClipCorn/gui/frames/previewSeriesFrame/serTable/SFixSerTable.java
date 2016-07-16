package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
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
		
		addMouseListener(new MouseAdapter() {
		    final int defaultTimeout = ToolTipManager.sharedInstance().getInitialDelay();
		    
			@Override
			public void mouseExited(MouseEvent e) {
		        ToolTipManager.sharedInstance().setInitialDelay(defaultTimeout);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
		        ToolTipManager.sharedInstance().setInitialDelay(0);
			}
		});
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
		case SerTableModel.COLUMN_EPISODE:		// Episode
			return renderer_episode;
		case SerTableModel.COLUMN_NAME:			// Name
			return renderer_title;
		case SerTableModel.COLUMN_VIEWED:		// Viewed
			return renderer_viewed;
		case SerTableModel.COLUMN_LASTVIEWED:	// Last Viewed
			return renderer_datelist;
		case SerTableModel.COLUMN_QUALITY:		// Quality
			return renderer_quality;
		case SerTableModel.COLUMN_LENGTH:		// Length
			return renderer_length;
		case SerTableModel.COLUMN_TAGS:			// Tags
			return renderer_tags;
		case SerTableModel.COLUMN_ADDDATE:		// Add Date
			return renderer_date;
		case SerTableModel.COLUMN_FORMAT:		// Format
			return renderer_format;
		case SerTableModel.COLUMN_SIZE:			// Size
			return renderer_filesize;
		default:
			System.out.println("Mysterious switch jump in [SFixSerTable.java]"); //$NON-NLS-1$
			return super.getCellRenderer(row, column); //renderer_default;
		}
	}

    @Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        
        if (c instanceof JComponent) {
            Object value = getValueAt(row, column);
    		int realColumn = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...
            JComponent jc = (JComponent) c;
                        
    		switch (realColumn) {
    		case SerTableModel.COLUMN_EPISODE:		// Episode
    			break;
    		case SerTableModel.COLUMN_NAME:			// Name
    			break;
    		case SerTableModel.COLUMN_VIEWED:		// Viewed
    			break;
    		case SerTableModel.COLUMN_LASTVIEWED:	// Last Viewed
    			if (((CCDateTimeList)value).any()) jc.setToolTipText(((CCDateTimeList)value).getHTMLListFormatted(row));
    			break;
    		case SerTableModel.COLUMN_QUALITY:		// Quality
    			break;
    		case SerTableModel.COLUMN_LENGTH:		// Length
    			break;
    		case SerTableModel.COLUMN_TAGS:			// Tags
    			break;
    		case SerTableModel.COLUMN_ADDDATE:		// Add Date
    			break;
    		case SerTableModel.COLUMN_FORMAT:		// Format
    			break;
    		case SerTableModel.COLUMN_SIZE:			// Size
    			break;
    		default:
    			System.out.println("Mysterious switch jump in [SFixSerTable.java]"); //$NON-NLS-1$
    		}
        }
        
        return c;
    }
}
