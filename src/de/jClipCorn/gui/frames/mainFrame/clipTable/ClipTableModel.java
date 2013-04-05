package de.jClipCorn.gui.frames.mainFrame.clipTable;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableModelRowColorInterface;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.YearRange;

public class ClipTableModel extends AbstractTableModel implements TableModelRowColorInterface {
	private static final long serialVersionUID = -3060547018013428568L;
	
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
	
	private static final String[] columnNames = {
			"", // Score	 								//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Title"), //$NON-NLS-1$
			"", // Gesehen								//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Zyklus"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Quality"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Language"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Genre"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Parts"), //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Length"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Added"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Score"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.FSK"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Format"), //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Year"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Size")  //$NON-NLS-1$
	};

	private final CCMovieList movielist;
	private JTable owner;

	public ClipTableModel(CCMovieList ml) {
		super();
		this.movielist = ml;
	}
	
	public void setTable(JTable t) {
		this.owner = t;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col].toString();
	}

	@Override
	public int getRowCount() {
		if (movielist == null)
			return 0;
		return movielist.getElementCount();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (movielist == null)
			return ""; //$NON-NLS-1$

		CCDatabaseElement el = movielist.getDatabaseElementBySort(row);
		
		if (el.isMovie()) {
			CCMovie mov = (CCMovie) el;
			switch (col) {
			case 0: // Score
				return mov.getScore();
			case 1: // Name
				return mov;
			case 2: // Viewed
				return mov.isViewed();
			case 3: // Zyklus
				return mov.getZyklus();
			case 4: // Quality
				return mov.getCombinedQuality();
			case 5: // Language
				return mov.getLanguage();
			case 6: // Genres
				return mov.getGenres();
			case 7: // Partcount
				return mov.getPartcount();
			case 8: // Length
				return mov.getLength();
			case 9: // Date
				return mov.getAddDate();
			case 10: // OnlineScore
				return mov.getOnlinescore();
			case 11: // FSK
				return mov.getFSK();
			case 12: // Format
				return mov.getFormat();
			case 13: // Year
				return new YearRange(mov.getYear());
			case 14: // Filesize
				return mov.getFilesize();
			default:
				return null; 
			}
		} else {
			CCSeries ser = (CCSeries) el;
			switch (col) {
			case 0: // Score
				return ser.getScore();
			case 1: // Name
				return ser;
			case 2: // Viewed
				return ser.isViewed();
			case 3: // Zyklus
				return new CCMovieZyklus();
			case 4: // Quality
				return ser.getCombinedQuality();
			case 5: // Language
				return ser.getLanguage();
			case 6: // Genres
				return ser.getGenres();
			case 7: // Partcount
				return ser.getEpisodeCount();
			case 8: // Length
				return ser.getLength();
			case 9: // Date
				return ser.getAddDate();
			case 10: // OnlineScore
				return ser.getOnlinescore();
			case 11: // FSK
				return ser.getFSK();
			case 12: // Format
				return ser.getFormat();
			case 13: // Year
				return ser.getYearRange();
			case 14: // Filesize
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
		fireTableCellUpdated(row, col);
	}

	@Override
	public Color getRowColor(int row) {
		switch (CCProperties.getInstance().PROP_MAINFRAME_TABLEBACKGROUND.getValue()) {
		case 0:
			return Color.WHITE;
		case 1:
			return (row%2==0) ? (Color.WHITE) : (COLOR_BACKGROUNDGRAY);
		case 2:
			return COLOR_ONLINESCORE[movielist.getDatabaseElementBySort(owner.convertRowIndexToModel(row)).getOnlinescore().asInt()];
		default:
			return Color.MAGENTA;
		}
	}
}
