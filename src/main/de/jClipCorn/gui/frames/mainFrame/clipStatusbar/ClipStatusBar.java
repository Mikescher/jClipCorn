package de.jClipCorn.gui.frames.mainFrame.clipStatusbar;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ConcurrentModificationException;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import de.jClipCorn.Globals;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.log.CCLogChangedListener;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

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
					CCActionTree.getInstance().find("ShowLog").execute(); //$NON-NLS-1$
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
		updateLabels_Log();
	}
	
	private void updateLabels_Log() {
		if (lblLog != null) {
			lblLog.setIcon(CCLog.getHighestType().getSmallIcon());
		}
	}
	
	public void updateLables_Movies() {
		lblMovies.setText(LocaleBundle.getFormattedString("ClipStatusBar.Movies", owner.getClipTable().getRowCount(), movielist.getElementCount())); //$NON-NLS-1$
	}
	
	private void updateLables_Viewed() {
		lblViewed.setText(LocaleBundle.getFormattedString("ClipStatusBar.Viewed", movielist.getViewedCount())); //$NON-NLS-1$
	}
	
	private void updateLables_Series() {		
		lblSeries.setText(LocaleBundle.getFormattedString("ClipStatusBar.Series", movielist.getEpisodeCount())); //$NON-NLS-1$
	}
	
	private void updateLables_Length() {
		lblLength.setText(LocaleBundle.getFormattedString("ClipStatusBar.Length", TimeIntervallFormatter.formatPointed(movielist.getTotalLength(CCProperties.getInstance().PROP_STATUSBAR_CALC_SERIES_IN_LENGTH.getValue())))); //$NON-NLS-1$
	}
	
	private void updateLables_Size() {
		lblSize.setText(  LocaleBundle.getFormattedString("ClipStatusBar.Size", FileSizeFormatter.format(movielist.getTotalSize(CCProperties.getInstance().PROP_STATUSBAR_CALC_SERIES_IN_SIZE.getValue())))); //$NON-NLS-1$
	}
	
	public void updateLables_Starttime() {
		StringBuilder tooltip = new StringBuilder();
		
		tooltip.append("<html>"); //$NON-NLS-1$
		if (Globals.TIMINGS.contains(Globals.TIMING_LOAD_PROPERTIES))       tooltip.append(String.format("TIMING_LOAD_PROPERTIES       = %dms<br/>", Globals.TIMINGS.getMilliseconds(Globals.TIMING_LOAD_PROPERTIES))); //$NON-NLS-1$
		if (Globals.TIMINGS.contains(Globals.TIMING_LOAD_TESTREADONLY))     tooltip.append(String.format("TIMING_LOAD_TESTREADONLY     = %dms<br/>", Globals.TIMINGS.getMilliseconds(Globals.TIMING_LOAD_TESTREADONLY))); //$NON-NLS-1$
		if (Globals.TIMINGS.contains(Globals.TIMING_INIT_BACKUPMANAGER))    tooltip.append(String.format("TIMING_INIT_BACKUPMANAGER    = %dms<br/>", Globals.TIMINGS.getMilliseconds(Globals.TIMING_INIT_BACKUPMANAGER))); //$NON-NLS-1$
		if (Globals.TIMINGS.contains(Globals.TIMING_LOAD_PRELOADRESOURCES)) tooltip.append(String.format("TIMING_LOAD_PRELOADRESOURCES = %dms<br/>", Globals.TIMINGS.getMilliseconds(Globals.TIMING_LOAD_PRELOADRESOURCES))); //$NON-NLS-1$
		if (Globals.TIMINGS.contains(Globals.TIMING_LOAD_TOTAL))            tooltip.append(String.format("TIMING_LOAD_TOTAL            = %dms<br/>", Globals.TIMINGS.getMilliseconds(Globals.TIMING_LOAD_TOTAL))); //$NON-NLS-1$
		if (Globals.TIMINGS.contains(Globals.TIMING_MOVIELIST_FILL))        tooltip.append(String.format("TIMING_MOVIELIST_FILL        = %dms<br/>", Globals.TIMINGS.getMilliseconds(Globals.TIMING_MOVIELIST_FILL))); //$NON-NLS-1$
		if (Globals.TIMINGS.contains(Globals.TIMING_SCAN_DRIVES))           tooltip.append(String.format("TIMING_SCAN_DRIVES           = %dms<br/>", Globals.TIMINGS.getMilliseconds(Globals.TIMING_SCAN_DRIVES))); //$NON-NLS-1$
		tooltip.append("</html>"); //$NON-NLS-1$
		
		lblStarttime.setText(LocaleBundle.getFormattedString("ClipStatusBar.Starttime", (int)Globals.TIMINGS.getSecondsOrZero(Globals.TIMING_LOAD_TOTAL, 0), movielist.getTotalDatabaseCount())); //$NON-NLS-1$
		lblStarttime.setToolTipText(tooltip.toString());
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateLabels();
			}
		});
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
