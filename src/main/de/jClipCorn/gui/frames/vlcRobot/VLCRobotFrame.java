package de.jClipCorn.gui.frames.vlcRobot;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.util.NextEpisodeHelper;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.vlcquery.VLCConnection;
import de.jClipCorn.util.vlcquery.VLCPlayerStatus;
import de.jClipCorn.util.vlcquery.VLCStatus;
import de.jClipCorn.util.vlcquery.VLCStatus.PositionArea;
import de.jClipCorn.util.vlcquery.VLCStatusPlaylistEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class VLCRobotFrame extends JFrame {
	// Wait {x} ms before attempting to manually set the VLC position
	// and then wait {y} ms more before doing the second run
	// and then wait {z} ms more before doing the third run
	private static final int DELAY_SET_VLC_POSITION_INITIAL =  500;
	private static final int DELAY_SET_VLC_POSITION_RUN_2   = 1500;
	private static final int DELAY_SET_VLC_POSITION_RUN_3   = 2500;


	private static final int DELAY_SLEEP_AFTER_SKIPPED_TICK =  75;

	private volatile boolean _stopThread = false;
	private final Object _threadLock = true;

	private VLCStatus _lastStatus = null;
	private int _skipCount = 0;
	private VLCStatus _lastPositionalStatus = null;
	private final java.util.List<VLCPlaylistEntry> _clientQueue = new ArrayList<>();
	private volatile VLCRobotFrequency updateFreq = VLCRobotFrequency.MS_0500;

	private static VLCRobotFrame _instance = null;

	private VLCRobotFrame(Component owner) {
		super();

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit() {
		setIconImage(Resources.IMG_FRAME_ICON.get());

		cbxFreq.setSelectedEnum(this.updateFreq = CCProperties.getInstance().PROP_VLC_ROBOT_FREQUENCY.getValue());

		lsData.autoResize();

		cbxKeepPosition.setSelected(ApplicationHelper.isWindows() && CCProperties.getInstance().PROP_VLC_ROBOT_KEEP_POSITION.getValue());
		cbxKeepPosition.setEnabled(ApplicationHelper.isWindows());

		tabbedPane.setSelectedIndex(0);

		Thread tthread = new Thread(this::onThreadRun);
		tthread.start();
	}

	public static VLCRobotFrame show(Component owner)
	{
		if (_instance == null || !_instance.isVisible())
		{
			_instance = new VLCRobotFrame(owner);
			_instance.setVisible(true);
		}
		else
		{
			_instance.toFront();
			_instance.repaint();
			//_instance.setLocationRelativeTo(owner);
		}

		return _instance;
	}

	private void onThreadRun()
	{
		if (_stopThread) return;

		List<Long> timings = new ArrayList<>(8);

		long last = System.currentTimeMillis();
		long lastUpdate = 0;

		for(int i= 1;;i++)
		{
			{
				var now = System.currentTimeMillis();
				var delta = now - last;
				last = now;

				while (timings.size() >= 8) timings.remove(0);
				timings.add(delta);

				if (now - lastUpdate > 750)
				{
					long sum = 0;
					for (var v : timings) sum += v;
					var htz = Str.format("{0,number,#.##} Hz", 1000.0 / (sum / timings.size())); //$NON-NLS-1$

					SwingUtils.invokeLater(() -> lblFrequency.setText(htz));

					lastUpdate = now;
				}
			}

			synchronized (_threadLock)
			{
				if (_stopThread) return;

				try
				{
					onBackgroundUpdate(i);
				}
				catch (Throwable e)
				{
					CCLog.addError(e);
				}
			}

			if (_stopThread) return;
			switch (updateFreq)
			{
				case MAXIMUM: /* no sleep */ break;
				case MS_0250: ThreadUtils.safeSleep(250);  break;
				case MS_0500: ThreadUtils.safeSleep(500);  break;
				case MS_1000: ThreadUtils.safeSleep(1000); break;
				case MS_1500: ThreadUtils.safeSleep(1500); break;
				case MS_2000: ThreadUtils.safeSleep(2000); break;
				default: CCLog.addDefaultSwitchError(this, updateFreq); ThreadUtils.safeSleep(500); break;
			}
			if (_stopThread) return;
		}
	}

	private void onBackgroundUpdate(int idx)
	{
		final VLCStatus status = VLCConnection.getStatus();

		if (status.doSkipStatusInStateMachine())
		{
			_skipCount++;
			var le = new VLCRobotLogEntry(idx, CCDateTime.getCurrentDateTime(), _lastStatus, status, _skipCount);
			SwingUtils.invokeLater(() -> addLog(le));
			ThreadUtils.safeSleep(DELAY_SLEEP_AFTER_SKIPPED_TICK);
			return;
		}

		_skipCount = 0;

		if (status.Status == VLCPlayerStatus.PLAYING && status.Repeat) VLCConnection.toggleRepeat();
		if (status.Status == VLCPlayerStatus.PLAYING && status.Random) VLCConnection.toggleRandom();
		if (status.Status == VLCPlayerStatus.PLAYING && status.Loop)   VLCConnection.toggleLoop();

		SwingUtils.invokeLater(() ->
		{
			if (status.Status == VLCPlayerStatus.PLAYING)
			{
				progressBar.setMaximum(status.CurrentLength);
				progressBar.setValue(status.CurrentTime);
				btnStart.setEnabled(false);
				btnClose.setEnabled(true);
				btnPlayPause.setEnabled(true);
				btnPlayPause.setText(LocaleBundle.getString("VLCRobotFrame.btnPause")); //$NON-NLS-1$
				lblStatus.setText(status.Status.asString());
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText(TimeIntervallFormatter.formatLengthSeconds(status.CurrentTime) + " / " + TimeIntervallFormatter.formatLengthSeconds(status.CurrentLength)); //$NON-NLS-1$

				updateList(status);

				if (status.Position == VLCStatus.PositionArea.MIDDLE || status.Position == VLCStatus.PositionArea.NEARLY_FINISHED) _lastPositionalStatus = status;
			}
			else if (status.Status == VLCPlayerStatus.PAUSED)
			{
				progressBar.setMaximum(status.CurrentLength);
				progressBar.setValue(status.CurrentTime);
				btnStart.setEnabled(false);
				btnClose.setEnabled(true);
				btnPlayPause.setEnabled(true);
				btnPlayPause.setText(LocaleBundle.getString("VLCRobotFrame.btnPlay")); //$NON-NLS-1$
				lblStatus.setText(status.Status.asString());
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText(TimeIntervallFormatter.formatLengthSeconds(status.CurrentTime) + " / " + TimeIntervallFormatter.formatLengthSeconds(status.CurrentLength)); //$NON-NLS-1$

				updateList(status);
			}
			else if (status.Status == VLCPlayerStatus.STOPPED)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(true);
				btnPlayPause.setEnabled(false);
				btnPlayPause.setText(LocaleBundle.getString("VLCRobotFrame.btnPlay")); //$NON-NLS-1$
				lblStatus.setText(status.Status.asString());
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText(Str.Empty);

				updateList(status);
			}
			else if (status.Status == VLCPlayerStatus.NOT_RUNNING)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(true);
				btnClose.setEnabled(true);
				btnPlayPause.setEnabled(false);
				btnPlayPause.setText(LocaleBundle.getString("VLCRobotFrame.btnPlay")); //$NON-NLS-1$
				lblStatus.setText(status.Status.asString());
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText(Str.Empty);
				_lastPositionalStatus = null;

				updateList(status);
			}
			else if (status.Status == VLCPlayerStatus.ERROR)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				btnPlayPause.setEnabled(false);
				btnPlayPause.setText(LocaleBundle.getString("VLCRobotFrame.btnPlay")); //$NON-NLS-1$
				lblStatus.setText(status.Status.asString());
				lblStatus.setForeground(Color.RED);
				lblTime.setText(Str.Empty);
				_lastPositionalStatus = null;
			}
			else if (status.Status == VLCPlayerStatus.DISABLED)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				btnPlayPause.setEnabled(false);
				btnPlayPause.setText(LocaleBundle.getString("VLCRobotFrame.btnPlay")); //$NON-NLS-1$
				lblStatus.setText(status.Status.asString());
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText(Str.Empty);
				_lastPositionalStatus = null;
			}

			if (!cbxKeepPosition.isSelected())
				cbxKeepPosition.setText(LocaleBundle.getString("VLCRobotFrame.cbxKeepPosition")); //$NON-NLS-1$
			else if (_lastStatus != null && _lastStatus.Fullscreen)
				cbxKeepPosition.setText(LocaleBundle.getString("VLCRobotFrame.cbxKeepPosition_fullscreen")); //$NON-NLS-1$
			else if (_lastStatus != null && _lastStatus.WindowRect != null)
				cbxKeepPosition.setText(LocaleBundle.getMFFormattedString("VLCRobotFrame.cbxKeepPosition_value", _lastStatus.WindowRect.x, _lastStatus.WindowRect.y, _lastStatus.WindowRect.width, _lastStatus.WindowRect.height)); //$NON-NLS-1$
			else
				cbxKeepPosition.setText(LocaleBundle.getString("VLCRobotFrame.cbxKeepPosition")); //$NON-NLS-1$

			onStatusChanged(_lastStatus, status, idx);

			_lastStatus = status;
		});
	}

	private void onStatusChanged(VLCStatus sold, VLCStatus snew, int idx)
	{
		if (sold == null && snew.Status == VLCPlayerStatus.STOPPED && _clientQueue.size()>0)
		{
			CCLog.addDebug("VLCRobot Status changed (NULL --> STOPPED)"); //$NON-NLS-1$
			postNextQueueEntry(idx, false, true, "LateErrorStartup"); //$NON-NLS-1$
			return;
		}

		if (sold == null && snew.Status == VLCPlayerStatus.NOT_RUNNING && _clientQueue.size()>0)
		{
			CCLog.addDebug("VLCRobot Status changed (NULL --> NOT_RUNNING)"); //$NON-NLS-1$
			VLCConnection.startPlayer();
			return;
		}

		if (sold == null) return;
		if (snew == null) return;

		if (VLCStatus.isDiff(sold, snew))
		{
			CCLog.addDebug("VLCRobot Status changed (" + VLCStatus.formatShortDiff(sold, snew) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			addLog(new VLCRobotLogEntry(idx, CCDateTime.getCurrentDateTime(), sold, snew));
		}

		if (sold.Status == VLCPlayerStatus.NOT_RUNNING && snew.Status == VLCPlayerStatus.STOPPED && _clientQueue.size()>0)
		{
			CCLog.addDebug("VLCRobot Status changed (NOT_RUNNING --> STOPPED)"); //$NON-NLS-1$
			postNextQueueEntry(idx, false, true, "LateStartup"); //$NON-NLS-1$
			return;
		}

		if (sold.Status == VLCPlayerStatus.NOT_RUNNING) return;
		if (snew.Status == VLCPlayerStatus.NOT_RUNNING) return;

		if (sold.Status == VLCPlayerStatus.DISABLED) return;
		if (snew.Status == VLCPlayerStatus.DISABLED) return;

		if (sold.Status == VLCPlayerStatus.ERROR) return;
		if (snew.Status == VLCPlayerStatus.ERROR) return;

		//if (sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.STOPPED && sold.isPlayingLastPlaylistEntry())
		//{
		//	CCLog.addDebug("VLCRobot StatusTrigger (PLAYING --> STOPPED)"); //$NON-NLS-1$
		//	postNextQueueEntry(idx, true, true, "Stopped"); //$NON-NLS-1$
		//	return;
		//}

		if (sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
				sold.isPlayingLastPlaylistEntry() && sold.Position == PositionArea.MIDDLE && snew.Position == PositionArea.NEARLY_FINISHED)
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING --> PLAYING[nearly_finished])"); //$NON-NLS-1$
			postNextQueueEntry(idx, false, false, "PositionEnd"); //$NON-NLS-1$
			return;
		}

		if (_clientQueue.size() > 0 &&
				sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
				sold.ActiveEntry != null && snew.ActiveEntry != null && !Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri) &&
				sold.isActiveEntryLastOfPlaylist() && snew.isActiveEntryFirstOfPlaylist() &&
				sold.Position != PositionArea.FINISHED && snew.Position == PositionArea.STARTING)
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[last] --skip--> PLAYING[first])"); //$NON-NLS-1$
			postNextQueueEntry(idx, true, true, "ManualSkipLast"); //$NON-NLS-1$
			return;
		}

		if (_clientQueue.size() > 0 &&
				sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
				sold.ActiveEntry != null && snew.ActiveEntry != null && Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri) &&
				sold.CurrentTime > snew.CurrentTime &&
				sold.Playlist.size() == 1 &&
				sold.Position != PositionArea.FINISHED && snew.Position == PositionArea.STARTING)
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[only] --skip--> PLAYING[only])"); //$NON-NLS-1$
			postNextQueueEntry(idx, true, true, "ManualSkipSingleton"); //$NON-NLS-1$
			return;
		}

		if (sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
				sold.ActiveEntry != null && snew.ActiveEntry != null &&
				!Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri))
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[a] --> PLAYING[b])"); //$NON-NLS-1$
			fixVLCPosition(idx, null, "PlayNext"); //$NON-NLS-1$
			return;
		}
	}

	public void enqueue(ICCPlayableElement v)
	{
		enqueue(VLCPlaylistEntry.createQueueSingle(v));
	}

	public void enqueue(CCSeason v)
	{
		var next = NextEpisodeHelper.findNextEpisode(v);
		if (next == null) return;
		var episodeList = CCStreams.iterate(v.getEpisodeList()).skipWhile(e -> e != next).<ICCPlayableElement>cast().toList();
		enqueue(VLCPlaylistEntry.createQueueAuto( v.getSeries().getTitle() + " - " + v.getTitle(), episodeList)); //$NON-NLS-1$
	}

	public void enqueue(CCSeries v)
	{
		var next = NextEpisodeHelper.findNextEpisode(v);
		if (next == null) return;
		var episodeList = CCStreams.iterate(v.getSortedEpisodeList()).skipWhile(e -> e != next).<ICCPlayableElement>cast().toList();
		enqueue(VLCPlaylistEntry.createQueueAuto(v.getTitle(), episodeList));
	}

	private void enqueue(VLCPlaylistEntry e)
	{
		if (_lastStatus == null)
		{
			_clientQueue.add(e);
		}
		else if (_lastStatus.Status == VLCPlayerStatus.STOPPED)
		{
			_clientQueue.add(e);
			updateList(_lastStatus);
			postNextQueueEntry(-1, false, true, "Startup"); //$NON-NLS-1$
		}
		else if (_lastStatus.Status == VLCPlayerStatus.NOT_RUNNING)
		{
			_clientQueue.add(e);
			updateList(_lastStatus);
			VLCConnection.startPlayer();
		}
		else
		{
			_clientQueue.add(e);
			updateList(_lastStatus);
		}
	}

	private void postNextQueueEntry(int index, boolean fixPosition, boolean startPlayback, String reason)
	{
		var lastPos = _lastPositionalStatus;

		if (_clientQueue.size() == 0) return;

		VLCPlaylistEntry q = _clientQueue.get(0);

		if (q.Type == VLCPlaylistEntry.VPEType.JCC_QUEUE_SINGLE)
		{
			_clientQueue.remove(0);

			CCLog.addDebug("PostNextVLCEntry[SINGLE]: [" + q.getText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

			addLog(new VLCRobotLogEntry(index, CCDateTime.getCurrentDateTime(), q.Element, reason));

			new Thread(() ->
			{
				List<String> parts = q.Element.getParts();
				for (int i=0; i<parts.size(); i++)
				{
					boolean ok = VLCConnection.enqueue(PathFormatter.fromCCPath(parts.get(i)), startPlayback && i==0);
					if (!ok)
					{
						SwingUtils.invokeLater(() -> _clientQueue.add(0, q));
						return;
					}
				}

				SwingUtils.invokeLater(() -> q.Element.updateViewedAndHistoryFromUI());

				if (startPlayback)
				{
					VLCStatus s = VLCConnection.getStatus();
					if (s.Status == VLCPlayerStatus.STOPPED)
					{
						for (int i=0; i<parts.size(); i++)
						{
							boolean ok = VLCConnection.enqueue(PathFormatter.fromCCPath(parts.get(i)), i==0);
							if (!ok)
							{
								SwingUtils.invokeLater(() -> _clientQueue.add(0, q));
								return;
							}
						}
					}
				}

				if (fixPosition) SwingUtils.invokeLater(() -> fixVLCPosition(index, lastPos, "PostPlay")); //$NON-NLS-1$

			}).start();
		}
		else if (q.Type == VLCPlaylistEntry.VPEType.JCC_QUEUE_AUTO)
		{
			CCLog.addDebug("PostNextVLCEntry[AUTO]: [" + q.getText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

			if (q.ElementQueue.size() == 0) { _clientQueue.remove(0); return; } // should never happen

			addLog(new VLCRobotLogEntry(index, CCDateTime.getCurrentDateTime(), q.ElementQueue.get(0), reason));

			var nextSub = q.ElementQueue.remove(0);

			new Thread(() ->
			{
				List<String> parts = nextSub.getParts();
				for (int i=0; i<parts.size(); i++)
				{
					boolean ok = VLCConnection.enqueue(PathFormatter.fromCCPath(parts.get(i)), startPlayback && i==0);
					if (!ok)
					{
						SwingUtils.invokeLater(() -> q.ElementQueue.add(0, nextSub));
						return;
					}
				}

				SwingUtils.invokeLater(nextSub::updateViewedAndHistoryFromUI);

				if (startPlayback)
				{
					VLCStatus s = VLCConnection.getStatus();
					if (s.Status == VLCPlayerStatus.STOPPED)
					{
						for (int i=0; i<parts.size(); i++)
						{
							boolean ok = VLCConnection.enqueue(PathFormatter.fromCCPath(parts.get(i)), i==0);
							if (!ok)
							{
								SwingUtils.invokeLater(() -> q.ElementQueue.add(0, nextSub));
								return;
							}
						}
					}
				}

				if (q.ElementQueue.size() == 0) _clientQueue.remove(q);

				if (fixPosition) SwingUtils.invokeLater(() -> fixVLCPosition(index, lastPos, "PostPlay")); //$NON-NLS-1$

			}).start();
		}
		else throw new Error("Invalid Queue Type: " + q.Type); //$NON-NLS-1$
	}

	private void fixVLCPosition(int index, VLCStatus _lps, String reason)
	{
		if (_lps == null) _lps = _lastPositionalStatus;

		if (_lps == null) return;
		if (!cbxKeepPosition.isSelected()) return;

		Rectangle rect = _lps.WindowRect;

		final var lps = _lps;

		new Thread(() ->
		{
			ThreadUtils.safeSleep(DELAY_SET_VLC_POSITION_INITIAL);

			VLCStatus current = _lastStatus;
			if (current == null) return;

			if (lps.Fullscreen)
			{
				//if (current.Fullscreen) return;
				//
				//VLCConnection.toggleFullscreen();
				return;
			}

			if (lps.WindowRect == null) return;
			if (current.WindowRect == null) return;
			if (current.WindowRect.equals(lps.WindowRect)) return;

			CCLog.addDebug("Manually adjust VLC Position: " + rect); //$NON-NLS-1$

			SwingUtils.invokeLater(() -> addLog(new VLCRobotLogEntry(index, CCDateTime.getCurrentDateTime(), current.WindowRect, lps.WindowRect, reason)));

			VLCConnection.setWindowPosition(rect);
			ThreadUtils.safeSleep(DELAY_SET_VLC_POSITION_RUN_2);
			VLCConnection.setWindowPosition(rect);
			ThreadUtils.safeSleep(DELAY_SET_VLC_POSITION_RUN_3);
			VLCConnection.setWindowPosition(rect);

		}).start();
	}

	private void updateList(VLCStatus status)
	{
		lsData.clearData();

		ArrayList<VLCPlaylistEntry> data = new ArrayList<>();

		boolean old = true;
		for (VLCStatusPlaylistEntry pe : status.Playlist)
		{
			if (pe == status.ActiveEntry)
			{
				old = false;
				data.add(VLCPlaylistEntry.createVLCPlayList(VLCPlaylistEntry.VPEType.VLC_ACTIVE, pe));
			}
			else if (old)
			{
				data.add(VLCPlaylistEntry.createVLCPlayList(VLCPlaylistEntry.VPEType.VLC_OLD, pe));
			}
			else
			{
				data.add(VLCPlaylistEntry.createVLCPlayList(VLCPlaylistEntry.VPEType.VLC_QUEUE, pe));
			}
		}

		data.addAll(_clientQueue);

		lsData.setData(data);
		lsData.autoResize();
	}

	private void onFrequencyChanged() {
		this.updateFreq = cbxFreq.getSelectedEnum();
		CCProperties.getInstance().PROP_VLC_ROBOT_FREQUENCY.setValue(this.updateFreq);
	}

	public void showLogEntry(VLCRobotLogEntry element) {
		if (element == null)
		{
			edLogOld.setText(Str.Empty);
			edLogNew.setText(Str.Empty);
		}
		else
		{
			if (element.StatusOld != null && element.StatusNew != null)
			{
				edLogOld.setText(element.StatusOld.format());
				edLogNew.setText(element.StatusNew.format());
			}
			else if (element.WindowRectOld != null && element.WindowRectNew != null)
			{
				edLogOld.setText(Str.format("X      = {0,number,#}\nY      = {1,number,#}\nWidth  = {2,number,#}\nHeight = {3,number,#}\n", element.WindowRectOld.x, element.WindowRectOld.y, element.WindowRectOld.width, element.WindowRectOld.height));
				edLogNew.setText(Str.format("X      = {0,number,#}\nY      = {1,number,#}\nWidth  = {2,number,#}\nHeight = {3,number,#}\n", element.WindowRectNew.x, element.WindowRectNew.y, element.WindowRectNew.width, element.WindowRectNew.height));
			}
			else if (element.NewEntry != null)
			{
				edLogOld.setText(Str.Empty);
				edLogNew.setText(element.NewEntry.getTitle());
			}
		}
	}

	private void addLog(VLCRobotLogEntry le) {
		logTable.addData(le);
		logTable.autoResize();
	}

	private void onWindowClosing() {
		_instance = null;
		_stopThread = true;
		synchronized (_threadLock) { _stopThread = true; }
		VLCRobotFrame.this.dispose();
	}

	private void onKeepPositionChanged() {
		CCProperties.getInstance().PROP_VLC_ROBOT_KEEP_POSITION.setValue(cbxKeepPosition.isSelected());
	}

	private void onStart() {
		VLCConnection.startPlayer();
	}

	private void onPlayPause() {
		if (_lastStatus == null) return;

		if (_lastStatus.Status == VLCPlayerStatus.PLAYING)
		{
			new Thread(VLCConnection::pause).start();
		}
		else if (_lastStatus.Status == VLCPlayerStatus.PAUSED)
		{
			new Thread(VLCConnection::play).start();
		}
	}

	private void onClose() {
		_instance = null;
		_stopThread = true;
		synchronized (_threadLock) { _stopThread = true; }
		dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		tabbedPane = new JTabbedPane();
		pnlMain = new JPanel();
		lblTitle = new JLabel();
		label2 = new JLabel();
		cbxFreq = new CCEnumComboBox<>(VLCRobotFrequency.getWrapper());
		lblFrequency = new JLabel();
		lsData = new VLCPlaylistTable();
		lblStatus = new JLabel();
		progressBar = new JProgressBar();
		lblTime = new JLabel();
		cbxKeepPosition = new JCheckBox();
		btnStart = new JButton();
		btnPlayPause = new JButton();
		btnClose = new JButton();
		pnlLog = new JPanel();
		splitPane1 = new JSplitPane();
		logTable = new VLCRobotLogTable(this);
		panel1 = new JPanel();
		scrollPane2 = new JScrollPane();
		edLogOld = new JTextArea();
		scrollPane3 = new JScrollPane();
		edLogNew = new JTextArea();
		pnlInfo = new JPanel();
		scrollPane1 = new JScrollPane();
		lblText = new JTextArea();

		//======== this ========
		setTitle(LocaleBundle.getString("VLCRobotFrame.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(300, 300));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onWindowClosing();
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== tabbedPane ========
		{
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setTabPlacement(SwingConstants.BOTTOM);

			//======== pnlMain ========
			{
				pnlMain.setLayout(new FormLayout(
					"$rgap, default, 2*($lcgap, default:grow), $lcgap, default, $rgap", //$NON-NLS-1$
					"$rgap, default, $lgap, default:grow, 3*($lgap, default), $rgap")); //$NON-NLS-1$

				//---- lblTitle ----
				lblTitle.setText("ROBOT"); //$NON-NLS-1$
				pnlMain.add(lblTitle, CC.xy(2, 2));

				//---- label2 ----
				label2.setText(LocaleBundle.getString("VLCRobotFrame.lblFreq")); //$NON-NLS-1$
				pnlMain.add(label2, CC.xy(4, 2, CC.RIGHT, CC.DEFAULT));

				//---- cbxFreq ----
				cbxFreq.addActionListener(e -> onFrequencyChanged());
				pnlMain.add(cbxFreq, CC.xy(6, 2));

				//---- lblFrequency ----
				lblFrequency.setText("[FREQ]"); //$NON-NLS-1$
				pnlMain.add(lblFrequency, CC.xy(8, 2));
				pnlMain.add(lsData, CC.xywh(2, 4, 7, 1, CC.FILL, CC.FILL));

				//---- lblStatus ----
				lblStatus.setText("[STATUS]"); //$NON-NLS-1$
				pnlMain.add(lblStatus, CC.xy(2, 6, CC.FILL, CC.FILL));
				pnlMain.add(progressBar, CC.xywh(4, 6, 3, 1, CC.DEFAULT, CC.FILL));

				//---- lblTime ----
				lblTime.setText("[TIME]"); //$NON-NLS-1$
				pnlMain.add(lblTime, CC.xy(8, 6, CC.FILL, CC.FILL));

				//---- cbxKeepPosition ----
				cbxKeepPosition.setText(LocaleBundle.getString("VLCRobotFrame.cbxKeepPosition")); //$NON-NLS-1$
				cbxKeepPosition.addActionListener(e -> onKeepPositionChanged());
				pnlMain.add(cbxKeepPosition, CC.xywh(2, 8, 7, 1));

				//---- btnStart ----
				btnStart.setText(LocaleBundle.getString("VLCRobotFrame.btnStart")); //$NON-NLS-1$
				btnStart.addActionListener(e -> onStart());
				pnlMain.add(btnStart, CC.xy(2, 10));

				//---- btnPlayPause ----
				btnPlayPause.setText(LocaleBundle.getString("VLCRobotFrame.btnPlay")); //$NON-NLS-1$
				btnPlayPause.addActionListener(e -> onPlayPause());
				pnlMain.add(btnPlayPause, CC.xy(4, 10, CC.LEFT, CC.DEFAULT));

				//---- btnClose ----
				btnClose.setText(LocaleBundle.getString("VLCRobotFrame.btnClose")); //$NON-NLS-1$
				btnClose.addActionListener(e -> onClose());
				pnlMain.add(btnClose, CC.xy(8, 10));
			}
			tabbedPane.addTab(LocaleBundle.getString("VLCRobotFrame.tabMain"), pnlMain); //$NON-NLS-1$

			//======== pnlLog ========
			{
				pnlLog.setLayout(new BorderLayout());

				//======== splitPane1 ========
				{
					splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
					splitPane1.setContinuousLayout(true);
					splitPane1.setResizeWeight(0.5);
					splitPane1.setTopComponent(logTable);

					//======== panel1 ========
					{
						panel1.setLayout(new FormLayout(
							"default:grow, $rgap, default:grow", //$NON-NLS-1$
							"default:grow")); //$NON-NLS-1$

						//======== scrollPane2 ========
						{

							//---- edLogOld ----
							edLogOld.setEditable(false);
							scrollPane2.setViewportView(edLogOld);
						}
						panel1.add(scrollPane2, CC.xy(1, 1, CC.FILL, CC.FILL));

						//======== scrollPane3 ========
						{

							//---- edLogNew ----
							edLogNew.setEditable(false);
							scrollPane3.setViewportView(edLogNew);
						}
						panel1.add(scrollPane3, CC.xy(3, 1, CC.FILL, CC.FILL));
					}
					splitPane1.setBottomComponent(panel1);
				}
				pnlLog.add(splitPane1, BorderLayout.CENTER);
			}
			tabbedPane.addTab(LocaleBundle.getString("VLCRobotFrame.tabLog"), pnlLog); //$NON-NLS-1$

			//======== pnlInfo ========
			{
				pnlInfo.setLayout(new BorderLayout());

				//======== scrollPane1 ========
				{

					//---- lblText ----
					lblText.setText(LocaleBundle.getString("VLCRobotFrame.helpText")); //$NON-NLS-1$
					lblText.setEditable(false);
					lblText.setLineWrap(true);
					lblText.setBorder(new EmptyBorder(4, 4, 4, 4));
					scrollPane1.setViewportView(lblText);
				}
				pnlInfo.add(scrollPane1, BorderLayout.CENTER);
			}
			tabbedPane.addTab(LocaleBundle.getString("VLCRobotFrame.tabInfo"), pnlInfo); //$NON-NLS-1$

			tabbedPane.setSelectedIndex(0);
		}
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		setSize(500, 300);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTabbedPane tabbedPane;
	private JPanel pnlMain;
	private JLabel lblTitle;
	private JLabel label2;
	private CCEnumComboBox<VLCRobotFrequency> cbxFreq;
	private JLabel lblFrequency;
	private VLCPlaylistTable lsData;
	private JLabel lblStatus;
	private JProgressBar progressBar;
	private JLabel lblTime;
	private JCheckBox cbxKeepPosition;
	private JButton btnStart;
	private JButton btnPlayPause;
	private JButton btnClose;
	private JPanel pnlLog;
	private JSplitPane splitPane1;
	private VLCRobotLogTable logTable;
	private JPanel panel1;
	private JScrollPane scrollPane2;
	private JTextArea edLogOld;
	private JScrollPane scrollPane3;
	private JTextArea edLogNew;
	private JPanel pnlInfo;
	private JScrollPane scrollPane1;
	private JTextArea lblText;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
