package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableModelRowColorInterface;
import de.jClipCorn.gui.localization.LocaleBundle;

public class SerTableModel extends AbstractTableModel implements TableModelRowColorInterface{
	private static final long serialVersionUID = -2056843389761330885L;

	private static final String[] columnNames = { LocaleBundle.getString("PreviewSeriesFrame.serTable.Episode"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Name"), //$NON-NLS-1$
			"", //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Lastviewed"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Quality"), //$NON-NLS-1$
			LocaleBundle.getString("PreviewSeriesFrame.serTable.Length"), //$NON-NLS-1$
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
		return columnNames[col].toString();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		if (season == null)
			return 0;
		return season.getEpisodeCount();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (season == null)
			return ""; //$NON-NLS-1$

		CCEpisode ep = season.getEpisode(row);

		switch (col) {
		case 0: // Score
			return ep.getEpisode();
		case 1: // Name
			return ep.getTitle();
		case 2: // Viewed
			return ep.isViewed();
		case 3: // Last Viewed
			return ep.getLastViewed();
		case 4: // Quality
			return ep.getQuality();
		case 5: // Length
			return ep.getLength();
		case 6: // Add Date
			return ep.getAddDate();
		case 7: // Format
			return ep.getFormat();
		case 8: // Size
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
