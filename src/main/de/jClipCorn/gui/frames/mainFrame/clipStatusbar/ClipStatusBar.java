package de.jClipCorn.gui.frames.mainFrame.clipStatusbar;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import de.jClipCorn.Globals;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.gui.actionTree.ActionSource;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.log.CCLogChangedListener;
import de.jClipCorn.gui.log.CCLogType;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import org.apache.commons.lang3.StringUtils;

public class ClipStatusBar extends AbstractClipStatusbar implements CCDBUpdateListener, CCLogChangedListener {
	private static final long serialVersionUID = -5283785610114988490L;
	
	private final CCMovieList movielist;
	private final MainFrame owner;
	
	private JProgressBar progressBar;
	private JLabel lblMovies;
	private JLabel lblViewed;
	private JLabel lblSeries;
	private JLabel lblLength;
	private JLabel lblSize;
	private JLabel lblStarttime;
	private JLabel lblDriveScan;
	private JLabel lblLog;

	public ClipStatusBar(MainFrame owner, CCMovieList ml) {
		super();
		this.owner = owner;
		this.movielist = ml;
		
		if (ml != null) {	// Sonst meckert der WindowsBuilder
			ml.addChangeListener(this);
			CCLog.addChangeListener(this);
			intializeGUI();
			intializeListener();
		}
	}
	
	private void intializeGUI() {
		boolean propMov  = CCProperties.getInstance().PROP_STATBAR_ELCOUNT.getValue();
		boolean propBar  = CCProperties.getInstance().PROP_STATBAR_PROGRESSBAR.getValue();
		boolean propLog  = CCProperties.getInstance().PROP_STATBAR_LOG.getValue();
		boolean propView = CCProperties.getInstance().PROP_STATBAR_VIEWEDCOUNT.getValue();
		boolean propSer  = CCProperties.getInstance().PROP_STATBAR_SERIESCOUNT.getValue();
		boolean propLen  = CCProperties.getInstance().PROP_STATBAR_LENGTH.getValue();
		boolean propSize = CCProperties.getInstance().PROP_STATBAR_SIZE.getValue();
		boolean propTime = CCProperties.getInstance().PROP_STATBAR_STARTTIME.getValue();
		boolean propScan = CCProperties.getInstance().PROP_STATBAR_DRIVESCAN.getValue();

		//##################################################
		startInitColumns();
		//##################################################
		
		lblMovies = addLabel("", propMov); //$NON-NLS-1$
		
		progressBar = addProgressbar(100, propBar);
		
		addPlaceholder(propMov || propBar);
		
		lblLog = addLabel("", propLog); //$NON-NLS-1$
		
		addSeparator(propLog);
		
		lblViewed = addLabel("", propView); //$NON-NLS-1$
		
		addSeparator(propView);
		
		lblSeries = addLabel("", propSer); //$NON-NLS-1$
		
		addSeparator(propSer);
		
		lblLength = addLabel("", propLen); //$NON-NLS-1$
		
		addSeparator(propLen);
		
		lblSize = addLabel("", propSize); //$NON-NLS-1$
		
		addSeparator(propSize);
		
		lblStarttime = addLabel("", propTime); //$NON-NLS-1$

		addSeparator(propTime);

		lblDriveScan = addLabel("", propScan); //$NON-NLS-1$

		//##################################################
		endInitColumns();
		//##################################################
		
		updateLabels();
	}
	
	private void intializeListener() {
		lblLog.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// nothing
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// nothing
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// nothing
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// nothing
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					CCActionTree.getInstance().find("ShowLog").execute(ActionSource.DIRECT_CLICK); //$NON-NLS-1$
				}
			}
		});
	}
	
	private void updateLabels() {
		if (CCMovieList.isBlocked()) {
			return;
		}
		
		updateLables_Movies();
		updateLables_Viewed();
		updateLables_Series();
		updateLables_Length();
		updateLables_Size();
		updateLables_Starttime();
		updateLables_DriveScan();
		updateLabels_Log();
	}
	
	private void updateLabels_Log() {
		if (lblLog != null) {
			lblLog.setIcon(CCLog.getHighestType().getSmallIcon());

			StringBuilder tooltip = new StringBuilder();
			tooltip.append("<html>"); //$NON-NLS-1$
			tooltip.append(LocaleBundle.getString("CCLog.Informations")).append(": ").append(CCLog.getCount(CCLogType.LOG_ELEM_INFORMATION)).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			tooltip.append(LocaleBundle.getString("CCLog.Warnings")).append(": ").append(CCLog.getCount(CCLogType.LOG_ELEM_WARNING)).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			tooltip.append(LocaleBundle.getString("CCLog.Errors")).append(": ").append(CCLog.getCount(CCLogType.LOG_ELEM_ERROR)).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			tooltip.append(LocaleBundle.getString("CCLog.Undefinieds")).append(": ").append(CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED)).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			tooltip.append("</html>"); //$NON-NLS-1$
			lblLog.setToolTipText(tooltip.toString());
		}
	}
	
	public void updateLables_Movies() {
		lblMovies.setText(LocaleBundle.getFormattedString("ClipStatusBar.Elements", owner.getClipTable().getRowCount(), movielist.getElementCount())); //$NON-NLS-1$

		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html>"); //$NON-NLS-1$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Viewed",   movielist.getViewedCount())).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Movies",   movielist.getMovieCount())).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Series",   movielist.getSeriesCount())).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Seasons",  movielist.getSeasonCount())).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Episodes", movielist.getEpisodeCount())).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append("</html>"); //$NON-NLS-1$
		lblMovies.setToolTipText(tooltip.toString());
	}
	
	private void updateLables_Viewed() {
		lblViewed.setText(LocaleBundle.getFormattedString("ClipStatusBar.Viewed", movielist.getViewedCount())); //$NON-NLS-1$
	}
	
	private void updateLables_Series() {		
		lblSeries.setText(LocaleBundle.getFormattedString("ClipStatusBar.Episodes", movielist.getEpisodeCount())); //$NON-NLS-1$
	}
	
	private void updateLables_Length() {
		lblLength.setText(LocaleBundle.getFormattedString("ClipStatusBar.Length", TimeIntervallFormatter.formatPointed(movielist.getTotalLength(true, CCProperties.getInstance().PROP_STATUSBAR_CALC_SERIES_IN_LENGTH.getValue())))); //$NON-NLS-1$

		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html>"); //$NON-NLS-1$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Length_Movies", TimeIntervallFormatter.formatPointed(movielist.getTotalLength(true, false)))).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Length_Series", TimeIntervallFormatter.formatPointed(movielist.getTotalLength(false, true)))).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Length",        TimeIntervallFormatter.formatPointed(movielist.getTotalLength(true, true)))).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append("</html>"); //$NON-NLS-1$
		lblLength.setToolTipText(tooltip.toString());
	}
	
	private void updateLables_Size() {
		lblSize.setText(  LocaleBundle.getFormattedString("ClipStatusBar.Size", FileSizeFormatter.format(movielist.getTotalSize(true, CCProperties.getInstance().PROP_STATUSBAR_CALC_SERIES_IN_SIZE.getValue())))); //$NON-NLS-1$

		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html>"); //$NON-NLS-1$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Size_Movies", FileSizeFormatter.format(movielist.getTotalSize(true, false)))).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Size_Series", FileSizeFormatter.format(movielist.getTotalSize(false, true)))).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append("</html>"); //$NON-NLS-1$
		lblSize.setToolTipText(tooltip.toString());
	}
	
	public void updateLables_Starttime() {
		StringBuilder tooltip = new StringBuilder();
		
		tooltip.append("<html>"); //$NON-NLS-1$
		condAppend(tooltip, Globals.TIMING_INIT_LOAD_PROPERTIES);
		condAppend(tooltip, Globals.TIMING_INIT_TESTREADONLY);
		condAppend(tooltip, Globals.TIMING_INIT_PRELOADRESOURCES);
		condAppend(tooltip, Globals.TIMING_INIT_TOTAL);

		tooltip.append("<hr/>"); //$NON-NLS-1$

		condAppend(tooltip, Globals.TIMING_LOAD_INIT_BACKUPMANAGER);
		condAppend(tooltip, Globals.TIMING_LOAD_DATABASE_CONNECT);
		condAppend(tooltip, Globals.TIMING_LOAD_MOVIELIST_FILL);
		condAppend(tooltip, Globals.TIMING_LOAD_TOTAL);

		tooltip.append("<hr/>"); //$NON-NLS-1$

		condAppend(tooltip, Globals.TIMING_STARTUP_TOTAL);

		tooltip.append("<hr/>"); //$NON-NLS-1$

		condAppend(tooltip, Globals.TIMING_BACKGROUND_SCAN_DRIVES);
		condAppend(tooltip, Globals.TIMING_BACKGROUND_PRELOADRESOURCES);
		condAppend(tooltip, Globals.TIMING_BACKGROUND_INITBACKUPMANAGER);

		tooltip.append("</html>"); //$NON-NLS-1$
		
		lblStarttime.setText(LocaleBundle.getFormattedString("ClipStatusBar.Starttime", (int)Globals.TIMINGS.getSecondsOrZero(Globals.TIMING_STARTUP_TOTAL, 0), movielist.getTotalDatabaseCount())); //$NON-NLS-1$
		lblStarttime.setToolTipText(tooltip.toString());
	}

	public void updateLables_DriveScan() {
		StringBuilder tooltip = new StringBuilder();

		tooltip.append("<html>"); //$NON-NLS-1$

		List<Tuple3<Character, String, String>> drives = DriveMap.getCopy();

		int l2 = 0;
		int l3 = 0;
		for (Tuple3<Character, String, String> v : drives) l2 = Math.max(l2, v.Item2==null ? 0 : v.Item2.length());
		for (Tuple3<Character, String, String> v : drives) l3 = Math.max(l3, v.Item3==null ? 0 : v.Item3.length());

		for (Tuple3<Character, String, String> v : drives) {

			if (Str.isNullOrWhitespace(v.Item2) && Str.isNullOrWhitespace(v.Item3)) continue;

			tooltip.append("<font face=\"monospace\">"); //$NON-NLS-1$
			tooltip.append("[").append(v.Item1).append("]&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
			tooltip.append(StringUtils.rightPad(v.Item2==null ? Str.Empty : v.Item2, l2).replace(" ", "&nbsp;")); //$NON-NLS-1$ //$NON-NLS-2$
			tooltip.append("&nbsp;&nbsp;|&nbsp;&nbsp;"); //$NON-NLS-1$
			tooltip.append(StringUtils.rightPad(v.Item3==null ? Str.Empty : v.Item3, l3).replace(" ", "&nbsp;")); //$NON-NLS-1$ //$NON-NLS-2$
			tooltip.append("<br/>"); //$NON-NLS-1$
			tooltip.append("</font>"); //$NON-NLS-1$
			tooltip.append("\r\n"); //$NON-NLS-1$
		}

		tooltip.append("</html>"); //$NON-NLS-1$

		if (drives.isEmpty())
			lblDriveScan.setToolTipText(null);
		else
			lblDriveScan.setToolTipText(tooltip.toString());

		lblDriveScan.setText(String.format("%s: %s", LocaleBundle.getString("ClipStatusBar.DriveScan"), DriveMap.getStatus())); //$NON-NLS-1$ //$NON-NLS-2$
		lblDriveScan.setToolTipText(tooltip.toString());
	}

	private void condAppend(StringBuilder builder, int id) {
		switch (Globals.TIMINGS.contains(id))
		{
			case NotFound:
				{
					// Nothing
				}
				break;
			case Running:
				{
					String strid = StringUtils.rightPad(Globals.TIMING_IDS.get(id), 31).replace(" ", "&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
					String strvl = StringUtils.leftPad("[RUNNING]", 5).replace(" ", "&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					builder.append(String.format("<font face=\"monospace\">%s := %sms</font><br/>", strid, strvl)); //$NON-NLS-1$
				}
				break;
			case Finished:
				{
					String strid = StringUtils.rightPad(Globals.TIMING_IDS.get(id), 31).replace(" ", "&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
					String strvl = StringUtils.leftPad(Long.toString(Globals.TIMINGS.getMilliseconds(id)), 5).replace(" ", "&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
					builder.append(String.format("<font face=\"monospace\">%s := %s</font><br/>", strid, strvl)); //$NON-NLS-1$
				}
				break;
		}
	}

	@Override
	public void onAddDatabaseElement(CCDatabaseElement mov) {
		updateLabels();
	}

	@Override
	public void onRemMovie(CCDatabaseElement el) {
		updateLabels();
	}

	@Override
	public void onChangeDatabaseElement(CCDatabaseElement el) {
		updateLabels();
	}
	
	@Override
	public void onRefresh() {
		updateLabels();
	}

	@Override
	public void onAfterLoad() {
		SwingUtilities.invokeLater(this::updateLabels);
	}
	
	public JProgressBar getProgressbar() {
		return progressBar;
	}

	@Override
	public void onChanged() {
		try {
			updateLabels_Log();
		} catch (ConcurrentModificationException e) {
			// Not so bad ...
		}
	}
}
