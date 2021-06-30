package de.jClipCorn.gui.frames.vlcRobot;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.vlcquery.VLCConnection;
import de.jClipCorn.util.vlcquery.VLCPlayerStatus;
import de.jClipCorn.util.vlcquery.VLCStatus;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VLCRobot {
	// Wait {x} ms before attempting to manually set the VLC position
	// and then wait {y} ms more before doing the second run
	// and then wait {z} ms more before doing the third run
	private static final int DELAY_SET_VLC_POSITION_INITIAL =  500;
	private static final int DELAY_SET_VLC_POSITION_RUN_2   = 1500;
	private static final int DELAY_SET_VLC_POSITION_RUN_3   = 2500;

	private static final int DELAY_SLEEP_AFTER_SKIPPED_TICK =  75;

	public interface VLCRobotEventListener {
		void onFrequency(double hertz);
		void addLog(VLCRobotLogEntry entry);
		void updateUI(VLCStatus lastStatus, VLCStatus status);
		void updateList(VLCStatus lastStatus);
	}

	private final VLCRobotEventListener _listener;

	private volatile boolean _stopThread = false;
	private final Object _threadLock = true;

	private Thread _thread;

	private VLCStatus _lastStatus = null;
	private boolean _startupFinished = false;
	private boolean _queueChangedFromInitialEmpty = false;
	private int _skipCount = 0;
	private VLCStatus _lastPositionalStatus = null;
	private final java.util.List<VLCPlaylistEntry> _clientQueue = new ArrayList<>();

	public volatile VLCRobotFrequency UpdateFrequency;
	public volatile boolean FixVLCPosition;
	public volatile boolean QueuePreemptive;

	public VLCRobot(VLCRobotFrequency f, boolean fixPos, boolean preempt, VLCRobotEventListener lstr) {
		_listener = lstr;

		UpdateFrequency = f;
		FixVLCPosition = fixPos;
		QueuePreemptive = preempt;
	}

	public void start() {
		_thread = new Thread(this::onThreadRun);
		_thread.start();
	}

	public void stop() {
		_stopThread = true;
		synchronized (_threadLock) { _stopThread = true; }
		_thread = null;
	}

	private void onThreadRun()
	{
		if (_stopThread) return;

		List<Long> timings = new ArrayList<>(8);

		long last = System.currentTimeMillis();
		long lastUpdate = 0;

		for(int i= 1;;i++)
		{
			// Update Frequency
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

					var hertz = 1000.0 / (sum / timings.size());

					SwingUtils.invokeLater(() -> _listener.onFrequency(hertz));

					lastUpdate = now;
				}
			}

			// BackgroundUpdate
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

			// Sleep
			if (_stopThread) return;
			switch (UpdateFrequency)
			{
				case MAXIMUM: /* no sleep */ break;
				case MS_0250: ThreadUtils.safeSleep(250);  break;
				case MS_0500: ThreadUtils.safeSleep(500);  break;
				case MS_1000: ThreadUtils.safeSleep(1000); break;
				case MS_1500: ThreadUtils.safeSleep(1500); break;
				case MS_2000: ThreadUtils.safeSleep(2000); break;
				default: CCLog.addDefaultSwitchError(this, UpdateFrequency); ThreadUtils.safeSleep(500); break;
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
			var le = VLCRobotLogEntry.TickSkipped(idx, CCDateTime.getCurrentDateTime(), _lastStatus, status, _skipCount);
			SwingUtils.invokeLater(() -> _listener.addLog(le));
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
				if (status.Position == VLCStatus.PositionArea.MIDDLE || status.Position == VLCStatus.PositionArea.NEARLY_FINISHED) _lastPositionalStatus = status;
			}
			else if (status.Status == VLCPlayerStatus.NOT_RUNNING)
			{
				_lastPositionalStatus = null;
			}
			else if (status.Status == VLCPlayerStatus.ERROR)
			{
				_lastPositionalStatus = null;
			}
			else if (status.Status == VLCPlayerStatus.DISABLED)
			{
				_lastPositionalStatus = null;
			}

			_listener.updateUI(_lastStatus, status);

			onHandleStatus(_lastStatus, status, idx);

			_lastStatus = status;
		});
	}

	private void onHandleStatus(VLCStatus sold, VLCStatus snew, int idx)
	{
		if (sold == null && snew.Status == VLCPlayerStatus.STOPPED && _clientQueue.size()>0)
		{
			CCLog.addDebug("VLCRobot Status changed (NULL --> STOPPED)"); //$NON-NLS-1$
			postNextQueueEntry(idx, false, true, "LateErrorStartup"); //$NON-NLS-1$
			return;
		}

		var clientQueueRealCount = CCStreams
				.iterate(_clientQueue)
				.map(p -> {
					if (p.Type == VLCPlaylistEntry.VPEType.JCC_QUEUE_SINGLE) return 1;
					if (p.Type == VLCPlaylistEntry.VPEType.JCC_QUEUE_AUTO) return p.ElementQueue.size();
					return 0;
				}).sumInt(p -> p);

		if (sold == null && snew.Status == VLCPlayerStatus.NOT_RUNNING && clientQueueRealCount>0)
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
			_listener.addLog(VLCRobotLogEntry.StateChange(idx, CCDateTime.getCurrentDateTime(), sold, snew));
		}

		if (sold.Status == VLCPlayerStatus.NOT_RUNNING && snew.Status == VLCPlayerStatus.STOPPED && clientQueueRealCount>0)
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

		if (snew.Status == VLCPlayerStatus.PLAYING || snew.Status == VLCPlayerStatus.PAUSED) _startupFinished = true;
		if (snew.Status == VLCPlayerStatus.STOPPED && !_queueChangedFromInitialEmpty) _startupFinished = true;

		if (sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
			sold.isPlayingLastPlaylistEntry() &&
			sold.Position == VLCStatus.PositionArea.MIDDLE && snew.Position == VLCStatus.PositionArea.NEARLY_FINISHED)
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING --> PLAYING[nearly_finished])"); //$NON-NLS-1$
			postNextQueueEntry(idx, false, false, "PositionEnd"); //$NON-NLS-1$
			return;
		}

		if (clientQueueRealCount > 0 &&
			sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
			sold.ActiveEntry != null && snew.ActiveEntry != null && !Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri) &&
			sold.isActiveEntryLastOfPlaylist() && snew.isActiveEntryFirstOfPlaylist() &&
			sold.Position != VLCStatus.PositionArea.FINISHED && snew.Position == VLCStatus.PositionArea.STARTING)
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[last] --skip--> PLAYING[first])"); //$NON-NLS-1$
			postNextQueueEntry(idx, true, true, "ManualSkipLast"); //$NON-NLS-1$
			return;
		}

		if (clientQueueRealCount > 0 &&
			sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
			sold.ActiveEntry != null && snew.ActiveEntry != null && Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri) &&
			sold.CurrentTime > snew.CurrentTime &&
			sold.Playlist.size() == 1 &&
			sold.Position != VLCStatus.PositionArea.FINISHED && snew.Position == VLCStatus.PositionArea.STARTING)
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[only] --skip--> PLAYING[only])"); //$NON-NLS-1$
			postNextQueueEntry(idx, true, true, "ManualSkipSingleton"); //$NON-NLS-1$
			return;
		}

		if (sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
			sold.ActiveEntry != null && snew.ActiveEntry != null &&
			!Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri) &&
			snew.isPlayingPreemptive(_clientQueue))
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[a] --> PLAYING_PREEMPTIVE[b])"); //$NON-NLS-1$
			updatePreemptiveEntry(idx, true, snew);
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

		if (QueuePreemptive && sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
			sold.ActiveEntry != null && snew.ActiveEntry != null && Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri) &&
			!snew.isInStartSafeArea() && clientQueueRealCount>0 &&
			snew.isPlayingLastPlaylistEntry() && !snew.isPlayingPreemptive(_clientQueue))
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[safe_area] --> PLAYING[normal])"); //$NON-NLS-1$
			postPreemptiveQueueEntry(idx, "QueuePreemptive"); //$NON-NLS-1$
			return;
		}
	}

	private void postNextQueueEntry(int index, boolean fixPosition, boolean startPlayback, String reason)
	{
		var lastPos = _lastPositionalStatus;

		VLCPlaylistEntry q = CCStreams.iterate(_clientQueue).skipWhile(p -> p.Type== VLCPlaylistEntry.VPEType.JCC_QUEUE_PREEMPTIVE).firstOrNull();
		if (q == null) return;

		if (q.Type == VLCPlaylistEntry.VPEType.JCC_QUEUE_SINGLE)
		{
			_clientQueue.remove(0);

			CCLog.addDebug("PostNextVLCEntry[SINGLE]: [" + q.getText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

			_listener.addLog(VLCRobotLogEntry.Play(index, CCDateTime.getCurrentDateTime(), q.Element, reason));

			new Thread(() ->
			{
				List<CCPath> parts = q.Element.getParts();
				for (int i=0; i<parts.size(); i++)
				{
					boolean ok = VLCConnection.enqueue(parts.get(i).toFSPath(), startPlayback && i==0) != null;
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
							boolean ok = VLCConnection.enqueue(parts.get(i).toFSPath(), i==0) != null;
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

			_listener.addLog(VLCRobotLogEntry.Play(index, CCDateTime.getCurrentDateTime(), q.ElementQueue.get(0), reason));

			var nextSub = q.ElementQueue.remove(0);

			new Thread(() ->
			{
				List<CCPath> parts = nextSub.getParts();
				for (int i=0; i<parts.size(); i++)
				{
					boolean ok = VLCConnection.enqueue(parts.get(i).toFSPath(), startPlayback && i==0) != null;
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
							boolean ok = VLCConnection.enqueue(parts.get(i).toFSPath(), i==0) != null;
							if (!ok)
							{
								SwingUtils.invokeLater(() -> q.ElementQueue.add(0, nextSub));
								return;
							}
						}
					}
				}

				if (q.ElementQueue.size() == 0) SwingUtils.invokeLater(() -> _clientQueue.remove(q));

				if (fixPosition) SwingUtils.invokeLater(() -> fixVLCPosition(index, lastPos, "PostPlay")); //$NON-NLS-1$

			}).start();
		}
		else throw new Error("Invalid Queue Type: " + q.Type); //$NON-NLS-1$
	}

	private void postPreemptiveQueueEntry(int index, String reason)
	{
		VLCPlaylistEntry q = CCStreams.iterate(_clientQueue).skipWhile(p -> p.Type== VLCPlaylistEntry.VPEType.JCC_QUEUE_PREEMPTIVE).firstOrNull();
		if (q == null) return;

		if (q.Type == VLCPlaylistEntry.VPEType.JCC_QUEUE_SINGLE)
		{
			_clientQueue.remove(0);

			CCLog.addDebug("PostPreemptiveVLCEntry[SINGLE]: [" + q.getText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

			_listener.addLog(VLCRobotLogEntry.QueuePreemptive(index, CCDateTime.getCurrentDateTime(), q.Element, reason));

			new Thread(() ->
			{
				List<CCPath> parts = q.Element.getParts();
				List<String> uris = new ArrayList<>();
				for (int i=0; i<parts.size(); i++)
				{
					var uri_ok = VLCConnection.enqueue(parts.get(i).toFSPath(), false);
					if (uri_ok == null)
					{
						SwingUtils.invokeLater(() -> _clientQueue.add(0, q));
						return;
					}
					uris.add(uri_ok);
				}

				SwingUtils.invokeLater(() -> _clientQueue.add(0, VLCPlaylistEntry.createClientQueuePreemptive(uris, q.Element, q.Length)));

			}).start();
		}
		else if (q.Type == VLCPlaylistEntry.VPEType.JCC_QUEUE_AUTO)
		{
			CCLog.addDebug("PostPreemptiveVLCEntry[AUTO]: [" + q.getText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

			if (q.ElementQueue.size() == 0) { _clientQueue.remove(q); return; } // should never happen

			_listener.addLog(VLCRobotLogEntry.QueuePreemptive(index, CCDateTime.getCurrentDateTime(), q.ElementQueue.get(0), reason));

			var nextSub = q.ElementQueue.remove(0);

			new Thread(() ->
			{
				List<CCPath> parts = nextSub.getParts();
				List<String> uris = new ArrayList<>();
				for (int i=0; i<parts.size(); i++)
				{
					var uri_ok = VLCConnection.enqueue(parts.get(i).toFSPath(), false);
					if (uri_ok == null)
					{
						SwingUtils.invokeLater(() -> q.ElementQueue.add(0, nextSub));
						return;
					}
					uris.add(uri_ok);
				}

				if (q.ElementQueue.size() == 0) SwingUtils.invokeLater(() -> _clientQueue.remove(q));

				SwingUtils.invokeLater(() -> _clientQueue.add(0, VLCPlaylistEntry.createClientQueuePreemptive(uris, nextSub, nextSub.length().get()*60)));


			}).start();
		}
		else throw new Error("Invalid Queue Type: " + q.Type); //$NON-NLS-1$
	}

	private void updatePreemptiveEntry(int index, boolean fixPosition, VLCStatus status)
	{
		var lastPos = _lastPositionalStatus;

		if (_clientQueue.size() == 0) return;
		if (status.ActiveEntry == null) return;

		var q = CCStreams
				.iterate(_clientQueue)
				.firstOrNull(p -> p.IsPreemptiveAndEquals(status.ActiveEntry.Uri));

		if (q == null) return;

		q.Element.updateViewedAndHistoryFromUI();

		_clientQueue.remove(q);

		new Thread(() ->
		{
			if (fixPosition) SwingUtils.invokeLater(() -> fixVLCPosition(index, lastPos, "UpdatePlayPreemptive")); //$NON-NLS-1$
		}).start();
	}

	private void fixVLCPosition(int index, VLCStatus _lps, String reason)
	{
		if (_lps == null) _lps = _lastPositionalStatus;

		if (_lps == null) return;
		if (!FixVLCPosition) return;

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

			SwingUtils.invokeLater(() -> _listener.addLog(VLCRobotLogEntry.UpdateWindowPos(index, CCDateTime.getCurrentDateTime(), current.WindowRect, lps.WindowRect, reason)));

			VLCConnection.setWindowPosition(rect);
			ThreadUtils.safeSleep(DELAY_SET_VLC_POSITION_RUN_2);
			VLCConnection.setWindowPosition(rect);
			ThreadUtils.safeSleep(DELAY_SET_VLC_POSITION_RUN_3);
			VLCConnection.setWindowPosition(rect);

		}).start();
	}

	public void enqueue(VLCPlaylistEntry e)
	{
		if (_lastStatus == null)
		{
			_clientQueue.add(e);
			_queueChangedFromInitialEmpty = true;
		}
		else if (_lastStatus.Status == VLCPlayerStatus.STOPPED && _startupFinished)
		{
			_clientQueue.add(e);
			_queueChangedFromInitialEmpty = true;
			_listener.updateList(_lastStatus);
			postNextQueueEntry(-1, false, true, "Restart"); //$NON-NLS-1$
		}
		else if (_lastStatus.Status == VLCPlayerStatus.NOT_RUNNING)
		{
			_clientQueue.add(e);
			_queueChangedFromInitialEmpty = true;
			_listener.updateList(_lastStatus);
			VLCConnection.startPlayer();
		}
		else
		{
			_clientQueue.add(e);
			_queueChangedFromInitialEmpty = true;
			_listener.updateList(_lastStatus);
		}
	}

	public Collection<VLCPlaylistEntry> getClientQueue() {
		return new ArrayList<>(_clientQueue);
	}

	public void playpause() {
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
}
