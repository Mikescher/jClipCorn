package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableModelRowColorInterface;
import de.jClipCorn.gui.localization.LocaleBundle;

public class SerTableModel extends AbstractTableModel implements TableModelRowColorInterface{
	private static final long serialVersionUID = -2056843389761330885L;

	public final static int COLUMN_EPISODE = 0;
	public final static int COLUMN_NAME = 1;
	public final static int COLUMN_VIEWED = 2;
	public final static int COLUMN_LASTVIEWED = 3;
	public final static int COLUMN_QUALITY = 4;
	public final static int COLUMN_LENGTH = 5;
	public final static int COLUMN_TAGS = 6;
	public final static int COLUMN_ADDDATE = 7;
	public final static int COLUMN_FORMAT = 8;
	public final static int COLUMN_SIZE = 9;
	
	private static final String[] COLUMN_NAMES = { 
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Episode"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Name"), //$NON-NLS-1$
			"", //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Lastviewed"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Quality"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Length"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Tags"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Added"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Format"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Size"), //$NON-NLS-1$
	};

	private CCSeason season;

	public SerTableModel(CCSeason sea) {
		super();
		this.season = sea;
	}

	@Override
	public String getColumnName(int col) {
		return COLUMN_NAMES[col].toString();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getRowCount() {
		if (season == null) {
			return 0;
		}
		return season.getEpisodeCount();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (season == null) {
			return ""; //$NON-NLS-1$
		}

		CCEpisode ep = season.getEpisode(row);

		switch (col) {
		case COLUMN_EPISODE: // Episode
			return ep.getEpisode();
		case COLUMN_NAME: // Name
			return ep.getTitle();
		case COLUMN_VIEWED: // Viewed
			return ep.getExtendedViewedState();
		case COLUMN_LASTVIEWED: // Last Viewed
			return ep.getLastViewed();
		case COLUMN_QUALITY: // Quality
			return ep.getQuality();
		case COLUMN_LENGTH: // Length
			return ep.getLength();
		case COLUMN_TAGS: // Tags
			return ep.getTags();
		case COLUMN_ADDDATE: // Add Date
			return ep.getAddDate();
		case COLUMN_FORMAT: // Format
			return ep.getFormat();
		case COLUMN_SIZE: // Size
			return ep.getFilesize();
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public void changeSeason(CCSeason s) {
		this.season = s;
	}

	@Override
	public Color getRowColor(int row) {
		return Color.WHITE;
	}
}
