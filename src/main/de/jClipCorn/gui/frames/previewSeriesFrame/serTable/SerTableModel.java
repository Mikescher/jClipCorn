package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.features.table.renderer.TableModelRowColorInterface;

public class SerTableModel extends AbstractTableModel implements TableModelRowColorInterface{
	private static final long serialVersionUID = -2056843389761330885L;

	public final static int COLUMN_EPISODE = 0;
	public final static int COLUMN_NAME = 1;
	public final static int COLUMN_VIEWED = 2;
	public final static int COLUMN_LASTVIEWED = 3;
	public final static int COLUMN_MEDIAINFO = 4;
	public final static int COLUMN_LANGUAGE = 5;
	public final static int COLUMN_LENGTH = 6;
	public final static int COLUMN_TAGS = 7;
	public final static int COLUMN_ADDDATE = 8;
	public final static int COLUMN_FORMAT = 9;
	public final static int COLUMN_SIZE = 10;
	
	private String[] COLUMN_NAMES = { 
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Episode"), //$NON-NLS-1$
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Name"), //$NON-NLS-1$
		"", //$NON-NLS-1$
		"_ERROR_", //$NON-NLS-1$
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Quality"), //$NON-NLS-1$
		"", //$NON-NLS-1$
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
		
		switch (CCProperties.getInstance().PROP_SERIES_DISPLAYED_DATE.getValue()) {
		case LAST_VIEWED:
			COLUMN_NAMES[3] = LocaleBundle.getString("PreviewSeriesFrame.serTable.ViewedHistory_1"); //$NON-NLS-1$
			break;
		case FIRST_VIEWED:
			COLUMN_NAMES[3] = LocaleBundle.getString("PreviewSeriesFrame.serTable.ViewedHistory_2"); //$NON-NLS-1$
			break;
		case AVERAGE:
			COLUMN_NAMES[3] = LocaleBundle.getString("PreviewSeriesFrame.serTable.ViewedHistory_3"); //$NON-NLS-1$
			break;
		}
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

		CCEpisode ep = season.getEpisodeByArrayIndex(row);

		switch (col) {
		case COLUMN_EPISODE:    return ep.getEpisodeNumber();
		case COLUMN_NAME:       return ep.getTitle();
		case COLUMN_VIEWED:     return ep.getExtendedViewedState();
		case COLUMN_LASTVIEWED: return ep.getViewedHistory();
		case COLUMN_MEDIAINFO:  return ep.getMediaInfoCategory();
		case COLUMN_LANGUAGE:   return ep.getLanguage();
		case COLUMN_LENGTH:     return ep.getLength();
		case COLUMN_TAGS:       return ep.getTags();
		case COLUMN_ADDDATE:    return ep.getAddDate();
		case COLUMN_FORMAT:     return ep.getFormat();
		case COLUMN_SIZE:       return ep.getFilesize();
		default:                return null;
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
