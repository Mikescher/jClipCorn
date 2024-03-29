package de.jClipCorn.gui.mainFrame.statusbar;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.Globals;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.log.CCLogType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.CCLogChangedAdapter;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.SwingUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ClipStatusBar extends AbstractClipStatusbar implements CCDBUpdateListener {
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

	@DesignCreate
	private static ClipStatusBar designCreate() { return new ClipStatusBar(null, null); }

	public ClipStatusBar(MainFrame owner, CCMovieList ml) {
		super();
		this.owner = owner;
		this.movielist = ml;
		
		if (ml != null)
		{
			ml.addChangeListener(this);

			CCLog.addChangeListener(new CCLogChangedAdapter()
			{
				@Override
				public void onChanged()
				{
					try { updateLabels_Log(); } catch (ConcurrentModificationException e) { /* Not so bad ... */ }
				}
			});

			intializeGUI();

			intializeListener();
		}
		else
		{
			startInitColumns();
			addLabel("(designCreate)", true); //$NON-NLS-1$
			addSeparator(true);
			addLabel("(designCreate)", true); //$NON-NLS-1$
			addSeparator(true);
			addLabel("(designCreate)", true); //$NON-NLS-1$
			addSeparator(true);
			addLabel("(designCreate)", true); //$NON-NLS-1$
			addSeparator(true);
			addLabel("(designCreate)", true); //$NON-NLS-1$
			endInitColumns();
		}
	}
	
	private void intializeGUI() {
		boolean propMov  = owner.ccprops().PROP_STATBAR_ELCOUNT.getValue();
		boolean propBar  = owner.ccprops().PROP_STATBAR_PROGRESSBAR.getValue();
		boolean propLog  = owner.ccprops().PROP_STATBAR_LOG.getValue();
		boolean propView = owner.ccprops().PROP_STATBAR_VIEWEDCOUNT.getValue();
		boolean propSer  = owner.ccprops().PROP_STATBAR_SERIESCOUNT.getValue();
		boolean propLen  = owner.ccprops().PROP_STATBAR_LENGTH.getValue();
		boolean propSize = owner.ccprops().PROP_STATBAR_SIZE.getValue();
		boolean propTime = owner.ccprops().PROP_STATBAR_STARTTIME.getValue();
		boolean propScan = owner.ccprops().PROP_STATBAR_DRIVESCAN.getValue();

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
					CCActionTree.getInstance().find("ShowLog").execute(owner, ActionSource.DIRECT_CLICK, Collections.emptyList(), null); //$NON-NLS-1$
				}
			}
		});
	}
	
	private void updateLabels() {
		if (movielist.isBlocked()) return;
		
		updateLables_Movies();
		updateLables_Viewed();
		updateLables_Series();
		updateLables_Length();
		updateLables_Size();
		updateLables_Starttime();
		updateLables_DriveScan();
		updateLabels_Log();
	}

	@SuppressWarnings("nls")
	private void updateLabels_Log() {
		if (lblLog != null) {
			lblLog.setIcon(CCLog.getHighestType().getSmallIcon());

			StringBuilder tooltip = new StringBuilder();
			tooltip.append("<html>");

			tooltip.append(LocaleBundle.getString("CCLog.Informations")).append(": ").append(CCLog.getCount(CCLogType.LOG_ELEM_INFORMATION));
			if (CCLog.hasUnwatched(CCLogType.LOG_ELEM_INFORMATION))tooltip.append(" (!)");
			tooltip.append("<br/>");

			tooltip.append(LocaleBundle.getString("CCLog.Warnings")).append(": ").append(CCLog.getCount(CCLogType.LOG_ELEM_WARNING));
			if (CCLog.hasUnwatched(CCLogType.LOG_ELEM_WARNING))tooltip.append(" (!)");
			tooltip.append("<br/>");

			tooltip.append(LocaleBundle.getString("CCLog.Errors")).append(": ").append(CCLog.getCount(CCLogType.LOG_ELEM_ERROR));
			if (CCLog.hasUnwatched(CCLogType.LOG_ELEM_ERROR))tooltip.append(" (!)");
			tooltip.append("<br/>");

			tooltip.append(LocaleBundle.getString("CCLog.Undefinieds")).append(": ").append(CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED));
			if (CCLog.hasUnwatched(CCLogType.LOG_ELEM_UNDEFINED))tooltip.append(" (!)");
			tooltip.append("<br/>");

			tooltip.append("<br/>");

			tooltip.append(LocaleBundle.getString("CCLog.SQL")).append(": ").append(CCLog.getSQLCount());
			tooltip.append("<br/>");

			tooltip.append("<br/>");

			tooltip.append(LocaleBundle.getString("CCLog.Changes")).append(": ").append(CCLog.getChangeCount());
			tooltip.append("<br/>");

			tooltip.append("</html>"); //$NON-NLS-1$
			lblLog.setToolTipText(tooltip.toString());
		}
	}
	
	public void updateLables_Movies() {
		lblMovies.setText(LocaleBundle.getFormattedString("ClipStatusBar.Elements", owner.getClipTable().getFilteredRowCount(), movielist.getElementCount())); //$NON-NLS-1$

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
		lblLength.setText(LocaleBundle.getFormattedString("ClipStatusBar.Length", TimeIntervallFormatter.formatPointed(movielist.getTotalLength(true, owner.ccprops().PROP_STATUSBAR_CALC_SERIES_IN_LENGTH.getValue())))); //$NON-NLS-1$

		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html>"); //$NON-NLS-1$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Length_Movies", TimeIntervallFormatter.formatPointed(movielist.getTotalLength(true, false)))).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Length_Series", TimeIntervallFormatter.formatPointed(movielist.getTotalLength(false, true)))).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append(LocaleBundle.getFormattedString("ClipStatusBar.Length",        TimeIntervallFormatter.formatPointed(movielist.getTotalLength(true, true)))).append("<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
		tooltip.append("</html>"); //$NON-NLS-1$
		lblLength.setToolTipText(tooltip.toString());
	}
	
	private void updateLables_Size() {
		lblSize.setText(  LocaleBundle.getFormattedString("ClipStatusBar.Size", FileSizeFormatter.format(movielist.getTotalSize(true, owner.ccprops().PROP_STATUSBAR_CALC_SERIES_IN_SIZE.getValue())))); //$NON-NLS-1$

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
		condAppend(tooltip, Globals.TIMING_LOAD_MOVIELIST_FILL_GROUPS);
		condAppend(tooltip, Globals.TIMING_LOAD_MOVIELIST_FILL_ELEMENTS);
		condAppend(tooltip, Globals.TIMING_LOAD_MOVIELIST_FILL_COVERS);
		condAppend(tooltip, Globals.TIMING_LOAD_CREATEBACKUP);
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

		List<Tuple3<Character, String, String>> drives = movielist.getDriveMap().getCopy();

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

		lblDriveScan.setText(String.format("%s: %s", LocaleBundle.getString("ClipStatusBar.DriveScan"), movielist.getDriveMap().getStatus())); //$NON-NLS-1$ //$NON-NLS-2$
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
	public void onAddSeason(CCSeason el) {
		/**/
	}

	@Override
	public void onAddEpisode(CCEpisode el) {
		/**/
	}

	@Override
	public void onRemDatabaseElement(CCDatabaseElement el) {
		updateLabels();
	}

	@Override
	public void onRemSeason(CCSeason el) {
		/**/
	}

	@Override
	public void onRemEpisode(CCEpisode el) {
		/**/
	}

	@Override
	public void onChangeDatabaseElement(CCDatabaseElement root, ICCDatabaseStructureElement el, String[] props) {
		updateLabels();
	}
	
	@Override
	public void onRefresh() {
		updateLabels();
	}

	@Override
	public void onAfterLoad() {
		SwingUtils.invokeLater(this::updateLabels);
	}
	
	public JProgressBar getProgressbar() {
		return progressBar;
	}
}
