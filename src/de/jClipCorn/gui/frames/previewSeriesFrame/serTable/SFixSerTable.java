package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.jClipCorn.gui.guiComponents.SFixTable;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableDateRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableFilesizeRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableFormatRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableLengthRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableQualityRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableStringTitleRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableViewedRenderer;

public class SFixSerTable extends SFixTable {
	private static final long serialVersionUID = 6982359339154624097L;
	
	private TableViewedRenderer renderer_viewed;
	private TableStringTitleRenderer renderer_title;
	private TableQualityRenderer renderer_quality;
	private TableLengthRenderer renderer_length;
	private TableDateRenderer renderer_date;
	private TableFormatRenderer renderer_format;
	private TableFilesizeRenderer renderer_filesize;

	public SFixSerTable(TableModel dm) {
		super(dm);
		init();
	}
	
	private void init() {
		createCellRenderer();
		//createRowSorter();
		
		//setRowSorter();
	}
	
	private void createCellRenderer() {
		renderer_viewed = new TableViewedRenderer();
		renderer_title = new TableStringTitleRenderer();
		renderer_quality = new TableQualityRenderer();
		renderer_length = new TableLengthRenderer();
		renderer_date = new TableDateRenderer();
		renderer_format = new TableFormatRenderer();
		renderer_filesize = new TableFilesizeRenderer();
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		column = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...
		
		switch (column) {
		case 0:		// Episode
			return super.getCellRenderer(row, column);//renderer_default;
		case 1:		// Name
			return renderer_title;
		case 2:		// Viewed
			return renderer_viewed;
		case 3:		// Last Viewed
			return renderer_date;
		case 4:		// Quality
			return renderer_quality;
		case 5:		// Length
			return renderer_length;
		case 6:		// Add Date
			return renderer_date;
		case 7:		// Format
			return renderer_format;
		case 8:		// Size
			return renderer_filesize;
		default:
			System.out.println("Mysterious switch jump in [SFixTable.java]"); //$NON-NLS-1$
			return super.getCellRenderer(row, column); //renderer_default;
		}
	}
}
