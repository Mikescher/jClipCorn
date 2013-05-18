package de.jClipCorn.gui.frames.mainFrame.clipTable;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.jClipCorn.gui.guiComponents.SFixTable;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableDateRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableFSKRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableFilesizeRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableFormatRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableGenreRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableLanguageRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableLengthRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableOnlinescoreRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TablePartRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableQualityRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableScoreRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableTitleRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableViewedRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableYearRenderer;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableZyklusRenderer;
import de.jClipCorn.gui.guiComponents.tableSorter.TableDateComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableFSKComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableFormatComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableGenreComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableIntelliTitleComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableLanguageComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableLengthComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableOnlineScoreComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TablePartComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableQualityComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableScoreComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableSizeComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableTitleComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableViewedComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableYearComparator;
import de.jClipCorn.gui.guiComponents.tableSorter.TableZyklusComparator;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;

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
	private TableDateRenderer renderer_date;
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
		renderer_date = new TableDateRenderer();
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
		
		sorter.setComparator(ClipTableModel.COLUMN_SCORE,  sorter_score); //TODO Remove them Magic Numbers (there are -unfortunately- everywhere)
		sorter.setComparator(ClipTableModel.COLUMN_TITLE,  sorter_title);
		sorter.setComparator(ClipTableModel.COLUMN_VIEWED,  sorter_viewed);
		sorter.setComparator(ClipTableModel.COLUMN_ZYKLUS,  sorter_zyklus);
		sorter.setComparator(ClipTableModel.COLUMN_QUALITY,  sorter_quality);
		sorter.setComparator(ClipTableModel.COLUMN_LANGUAGE,  sorter_language);
		sorter.setComparator(ClipTableModel.COLUMN_GENRE,  sorter_genre);
		sorter.setComparator(ClipTableModel.COLUMN_PARTCOUNT,  sorter_parts);
		sorter.setComparator(ClipTableModel.COLUMN_LENGTH,  sorter_length);
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
		case ClipTableModel.COLUMN_SCORE:		// Score
			return renderer_score;
		case ClipTableModel.COLUMN_TITLE:		// Name
			return renderer_title;
		case ClipTableModel.COLUMN_VIEWED:		// Viewed
			return renderer_viewed;
		case ClipTableModel.COLUMN_ZYKLUS:		// Zyklus
			return renderer_zyklus;
		case ClipTableModel.COLUMN_QUALITY:		// Quality
			return renderer_quality;
		case ClipTableModel.COLUMN_LANGUAGE:		// Language
			return renderer_language;
		case ClipTableModel.COLUMN_GENRE:		// Genres
			return renderer_genre;
		case ClipTableModel.COLUMN_PARTCOUNT:		// Partcount
			return renderer_parts;
		case ClipTableModel.COLUMN_LENGTH:		// Length
			return renderer_length;
		case ClipTableModel.COLUMN_DATE:		// Date
			return renderer_date;
		case ClipTableModel.COLUMN_ONLINESCORE:	// OnlineScore
			return renderer_onlinescore;
		case ClipTableModel.COLUMN_FSK:	// FSK
			return renderer_fsk;
		case ClipTableModel.COLUMN_FORMAT:	// Format
			return renderer_format;
		case ClipTableModel.COLUMN_YEAR:	// Year
			return renderer_year;
		case ClipTableModel.COLUMN_SIZE:	// Filesize
			return renderer_filesize;
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
}
