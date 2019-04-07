package de.jClipCorn.gui.mainFrame.clipTable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.guiComponents.SFixTable;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.features.table.renderer.TableModelRowColorInterface;
import de.jClipCorn.util.datetime.YearRange;

public class ClipTableModel extends AbstractTableModel implements TableModelRowColorInterface, TableModelListener {
	private static final long serialVersionUID = -3060547018013428568L;
	
	public final static int COLUMN_SCORE = 0;
	public final static int COLUMN_TITLE = 1;
	public final static int COLUMN_VIEWED = 2;
	public final static int COLUMN_ZYKLUS = 3;
	public final static int COLUMN_QUALITY = 4;
	public final static int COLUMN_LANGUAGE = 5;
	public final static int COLUMN_GENRE = 6;
	public final static int COLUMN_PARTCOUNT = 7;
	public final static int COLUMN_LENGTH = 8;
	public final static int COLUMN_DATE = 9;
	public final static int COLUMN_ONLINESCORE = 10;
	public final static int COLUMN_TAGS = 11;
	public final static int COLUMN_FSK = 12;
	public final static int COLUMN_FORMAT = 13;
	public final static int COLUMN_YEAR = 14;
	public final static int COLUMN_SIZE = 15;
	
	private List<Integer> mapping = null;
	private boolean lockShuffleMapping = false;
	
	private static final String[] COLUMN_NAMES = {
			"", 			 									//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Title"), 	//$NON-NLS-1$
			"", 												//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Zyklus"),  	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Quality"),  	//$NON-NLS-1$
			"",                                                 //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Genre"),  	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Parts"), 	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Length"),  	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Added"),  	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Score"),  	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Tags"),  	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.FSK"),  		//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Format"), 	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Year"),  	//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Size")  		//$NON-NLS-1$
	};
	
	private final static Color COLOR_BACKGROUNDGRAY = new Color(240, 240, 240); // F0F0F0 (clBtnFace)
	
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

	private final CCMovieList movielist;
	private SFixTable owner;

	public ClipTableModel(CCMovieList ml) {
		super();
		this.movielist = ml;
		
		this.addTableModelListener(this);
	}
	
	public void setTable(SFixTable t) {
		this.owner = t;
	}

	@Override
	public String getColumnName(int col) {
		return COLUMN_NAMES[col].toString();
	}

	@Override
	public int getRowCount() {
		if (movielist == null) {
			return 0;
		}
		return movielist.getElementCount();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (movielist == null) {
			return ""; //$NON-NLS-1$
		}

		CCDatabaseElement el = getDatabaseElementByRow(row);

		if (el.isMovie()) {
			CCMovie mov = (CCMovie) el;
			switch (col) {
			case COLUMN_SCORE: // Score
				return mov.getScore();
			case COLUMN_TITLE: // Name
				return mov;
			case COLUMN_VIEWED: // Viewed
				return mov.getExtendedViewedState();
			case COLUMN_ZYKLUS: // Zyklus
				return mov.getZyklus();
			case COLUMN_QUALITY: // Quality
				return mov.getQuality();
			case COLUMN_LANGUAGE: // Language
				return mov.getLanguage();
			case COLUMN_GENRE: // Genres
				return mov.getGenres();
			case COLUMN_PARTCOUNT: // Partcount
				return mov.getPartcount();
			case COLUMN_LENGTH: // Length
				return mov.getLength();
			case COLUMN_DATE: // Date
				return mov.getAddDate();
			case COLUMN_ONLINESCORE: // OnlineScore
				return mov.getOnlinescore();
			case COLUMN_TAGS: // Tags
				return mov.getTags();
			case COLUMN_FSK: // FSK
				return mov.getFSK();
			case COLUMN_FORMAT: // Format
				return mov.getFormat();
			case COLUMN_YEAR: // Year
				return new YearRange(mov.getYear());
			case COLUMN_SIZE: // Filesize
				return mov.getFilesize();
			default:
				return null; 
			}
		} else {
			CCSeries ser = (CCSeries) el;
			switch (col) {
			case COLUMN_SCORE: // Score
				return ser.getScore();
			case COLUMN_TITLE: // Name
				return ser;
			case COLUMN_VIEWED: // Viewed
				return ser.getExtendedViewedState();
			case COLUMN_ZYKLUS: // Zyklus
				return new CCMovieZyklus();
			case COLUMN_QUALITY: // Quality
				return ser.getQuality();
			case COLUMN_LANGUAGE: // Language
				return ser.getCommonOrAllLanguages();
			case COLUMN_GENRE: // Genres
				return ser.getGenres();
			case COLUMN_PARTCOUNT: // Partcount
				return ser.getEpisodeCount();
			case COLUMN_LENGTH: // Length
				return ser.getLength();
			case COLUMN_DATE: // Date
				return ser.getAddDate();
			case COLUMN_ONLINESCORE: // OnlineScore
				return ser.getOnlinescore();
			case COLUMN_TAGS: // Tags
				return ser.getTags();
			case COLUMN_FSK: // FSK
				return ser.getFSK();
			case COLUMN_FORMAT: // Format
				return ser.getFormat();
			case COLUMN_YEAR: // Year
				return ser.getYearRange();
			case COLUMN_SIZE: // Filesize
				return ser.getFilesize();
			default:
				return null; 
			}
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		clearMapping();
		fireTableCellUpdated(row, col);
	}

	@Override
	public Color getRowColor(int row) {
		switch (CCProperties.getInstance().PROP_MAINFRAME_TABLEBACKGROUND.getValue()) {
		case WHITE:
			return Color.WHITE;
		case STRIPED:
			return (row%2==0) ? (Color.WHITE) : (COLOR_BACKGROUNDGRAY);
		case SCORE:
			return COLOR_ONLINESCORE[getDatabaseElementByRow(owner.convertRowIndexToModel(row)).getOnlinescore().asInt()];
		default:
			return Color.MAGENTA;
		}
	}
	
	public void shuffle() {
		owner.resetSort();
		
		mapping = new ArrayList<>();
		for (int i = 0; i < getRowCount(); i++) {
			mapping.add(i);
		}
		
		Collections.shuffle(mapping);
		
		lockShuffleMapping = true;
		fireTableDataChanged();
		lockShuffleMapping = false;
	}	

	@Override
	public void tableChanged(TableModelEvent e) {
		clearMapping();
	}

	public void clearMapping() {
		if (! lockShuffleMapping && mapping != null) {
			mapping = null;
			fireTableDataChanged();
		}
	}

	public CCDatabaseElement getDatabaseElementByRow(int row) {
		if (mapping != null && row < mapping.size()) {
			return movielist.getDatabaseElementBySort(mapping.get(row));
		} else {
			clearMapping();
			return movielist.getDatabaseElementBySort(row);
		}
	}
}
