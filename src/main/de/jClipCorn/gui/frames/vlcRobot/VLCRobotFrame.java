package de.jClipCorn.gui.frames.vlcRobot;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.util.NextEpisodeHelper;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.vlcquery.VLCConnection;
import de.jClipCorn.util.vlcquery.VLCStatus;
import de.jClipCorn.util.vlcquery.VLCStatusPlaylistEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import static de.jClipCorn.util.vlcquery.VLCStatus.VLCPlayerStatus;

public class VLCRobotFrame extends JFrame {

	private static final long serialVersionUID = -9210243063139292492L;

	// only if current entry position is greater {x} seconds we will
	// remember the current window position for later reference
	private static final int MIN_TIME_FOR_POSITIONAL_CACHE = 10;

	// if the current entry is {x} seconds before it's end we
	// enqueue the next entry from the client queue
	private static final int DELTA_NEARLY_FINISHED = 15;

	// if we transition from the last playlist entry with more than {x} seconds
	// remaining to the first playlist entry with less than {y} seconds playtime
	// we interpret this as a manual skip and enqueue the next entry from the client queue
	private static final int MIN_TIME_FOR_FORWARD_SKIP_NOT_FINISHED = 10;
	private static final int MIN_TIME_FOR_FORWARD_SKIP_JUST_STARTED = 5;

	// Wait {x} ms before attempting to manually set the VLC position
	// and then wait {y} ms more before doing the second run
	private static final int DELAY_SET_VLC_POSITION_INITIAL = 500;
	private static final int DELAY_SET_VLC_POSITION_RE_DO = 1500;

	// ===========================================================================

	private final JPanel contentPanel = new JPanel();
	private VLCPlaylistTable lsData;
	private JLabel lblStatus;
	private JLabel lblTime;
	private JProgressBar progressBar;
	private JPanel panel;
	private JButton btnClose;
	private JButton btnStart;
	private JCheckBox cbxKeepPosition;
	private JButton btnPlayPause;

	private final Object _timerLock = new Object();
	private volatile boolean _timerActive = false;
	private Timer _timer = null;
	private VLCStatus _lastStatus = null;
	private VLCStatus _lastPositionalStatus = null;
	private final java.util.List<VLCPlaylistEntry> _clientQueue = new ArrayList<>();

	private static VLCRobotFrame _instance = null;

	/**
	 * @wbp.parser.constructor
	 */
	private VLCRobotFrame(Component owner) {
		super();

		initGUI();

		setLocationRelativeTo(owner);

		lsData.autoResize();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				_instance = null;
				if (_timer != null) _timer.stop();
				VLCRobotFrame.this.dispose();
				super.windowClosing(e);
			}
		});

		_timer = new Timer(500, this::onTimer);
		onTimer(null);
		_timer.start();
	}

	private void onTimer(ActionEvent ae) {
		new Thread(() ->
		{
			synchronized (_timerLock)
			{
				if (_timerActive) return;
				_timerActive = true;
			}
			try {
				onBackgroundUpdate();
			} finally {
				synchronized (_timerLock) { _timerActive = false; }
			}
		}).start();
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

	private void initGUI() {
		setTitle(LocaleBundle.getString("VLCRobotFrame.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setBounds(100, 100, 500, 300);
		setMinimumSize(new Dimension(300, 300));

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(35dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(35dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lsData = new VLCPlaylistTable();
		contentPanel.add(lsData, "2, 2, 5, 1, fill, fill"); //$NON-NLS-1$
		
		lblStatus = new JLabel();
		contentPanel.add(lblStatus, "2, 4, fill, fill"); //$NON-NLS-1$
		
		progressBar = new JProgressBar();
		contentPanel.add(progressBar, "4, 4, fill, fill"); //$NON-NLS-1$
		
		lblTime = new JLabel();
		contentPanel.add(lblTime, "6, 4, fill, fill"); //$NON-NLS-1$
		
		cbxKeepPosition = new JCheckBox("Fensterposition beibehalten");
		cbxKeepPosition.setSelected(ApplicationHelper.isWindows() && CCProperties.getInstance().PROP_VLC_ROBOT_KEEP_POSITION.getValue());
		cbxKeepPosition.addActionListener(e ->  CCProperties.getInstance().PROP_VLC_ROBOT_KEEP_POSITION.setValue(cbxKeepPosition.isSelected()));
		cbxKeepPosition.setEnabled(ApplicationHelper.isWindows());
		contentPanel.add(cbxKeepPosition, "2, 6, 5, 1"); //$NON-NLS-1$
		
		panel = new JPanel();
		contentPanel.add(panel, "2, 8, 5, 1, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("0dlu:grow"), //$NON-NLS-1$
				FormSpecs.PREF_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("26px"),})); //$NON-NLS-1$
		
		btnStart = new JButton(LocaleBundle.getString("VLCRobotFrame.btnStart")); //$NON-NLS-1$
		btnStart.setHorizontalAlignment(SwingConstants.LEFT);
		btnStart.addActionListener(this::onStart);
		panel.add(btnStart, "1, 1, fill, fill"); //$NON-NLS-1$
		
		btnPlayPause = new JButton("Play");
		btnPlayPause.addActionListener(this::onPlayPause);
		panel.add(btnPlayPause, "3, 1, fill, fill"); //$NON-NLS-1$
		
		btnClose = new JButton(LocaleBundle.getString("VLCRobotFrame.btnClose")); //$NON-NLS-1$
		btnClose.setHorizontalAlignment(SwingConstants.RIGHT);
		btnPlayPause.addActionListener(this::onClose);
		panel.add(btnClose, "6, 1, fill, fill"); //$NON-NLS-1$
	}

	private void onBackgroundUpdate()
	{
		final VLCStatus status = VLCConnection.getStatus();

		if (status.Status == VLCPlayerStatus.PLAYING && status.Repeat) VLCConnection.toggleRepeat();
		if (status.Status == VLCPlayerStatus.PLAYING && status.Random) VLCConnection.toggleRandom();
		if (status.Status == VLCPlayerStatus.PLAYING && status.Loop)   VLCConnection.toggleLoop();

		SwingUtilities.invokeLater(() ->
		{
			if (status.Status == VLCPlayerStatus.PLAYING)
			{
				progressBar.setMaximum(status.CurrentLength);
				progressBar.setValue(status.CurrentTime);
				btnStart.setEnabled(false);
				btnClose.setEnabled(true);
				btnPlayPause.setEnabled(true);
				btnPlayPause.setText("Pause");
				lblStatus.setText("[PLAYING]");
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText(TimeIntervallFormatter.formatLengthSeconds(status.CurrentTime) + " / " + TimeIntervallFormatter.formatLengthSeconds(status.CurrentLength));

				updateList(status);

				if (!status.isEntryJustStarted(MIN_TIME_FOR_POSITIONAL_CACHE)) _lastPositionalStatus = status;
			}
			else if (status.Status == VLCPlayerStatus.STOPPED)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				btnPlayPause.setEnabled(false);
				btnPlayPause.setText("Play");
				lblStatus.setText("[STOPPED]");
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText("");

				updateList(status);
			}
			else if (status.Status == VLCPlayerStatus.PAUSED)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				btnPlayPause.setEnabled(true);
				btnPlayPause.setText("Play");
				lblStatus.setText("[PAUSED]");
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText("");

				updateList(status);
			}
			else if (status.Status == VLCPlayerStatus.NOT_RUNNING)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(true);
				btnClose.setEnabled(false);
				btnPlayPause.setEnabled(false);
				btnPlayPause.setText("Play");
				lblStatus.setText("[NOT RUNNING]");
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText("");
				_lastPositionalStatus = null;
			}
			else if (status.Status == VLCPlayerStatus.ERROR)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				btnPlayPause.setEnabled(false);
				btnPlayPause.setText("Play");
				lblStatus.setText("[ERROR]");
				lblStatus.setForeground(Color.RED);
				lblTime.setText("");
				_lastPositionalStatus = null;
			}
			else if (status.Status == VLCPlayerStatus.DISABLED)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				btnPlayPause.setEnabled(false);
				btnPlayPause.setText("Play");
				lblStatus.setText("[DISABLED]");
				lblStatus.setForeground(Color.BLACK);
				lblTime.setText("");
				_lastPositionalStatus = null;
			}

			if (!cbxKeepPosition.isSelected())
				cbxKeepPosition.setText("Fensterposition beibehalten");
			else if (_lastStatus != null && _lastStatus.Fullscreen)
				cbxKeepPosition.setText("Fensterposition beibehalten" + " (fullscreen)");
			else if (_lastStatus != null && _lastStatus.WindowRect != null)
				cbxKeepPosition.setText("Fensterposition beibehalten" + Str.format(" ([{0,number,#};{1,number,#}]:[{2,number,#}x{3,number,#}])", _lastStatus.WindowRect.x, _lastStatus.WindowRect.y, _lastStatus.WindowRect.width, _lastStatus.WindowRect.height));
			else
				cbxKeepPosition.setText("Fensterposition beibehalten");

			onStatusChanged(_lastStatus, status);

			_lastStatus = status;
		});
	}

	private void onStatusChanged(VLCStatus sold, VLCStatus snew)
	{
		if (sold == null && snew.Status == VLCPlayerStatus.STOPPED && _clientQueue.size()>0)
		{
			CCLog.addDebug("VLCRobot Status changed (NULL --> STOPPED)"); //$NON-NLS-1$
			postNextQueueEntry(false, true);
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

		if(sold.Status != snew.Status) CCLog.addDebug("VLCRobot Status changed ("+sold.Status+" --> "+snew.Status+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$


		if (sold.Status == VLCPlayerStatus.NOT_RUNNING && snew.Status == VLCPlayerStatus.STOPPED && _clientQueue.size()>0)
		{
			CCLog.addDebug("VLCRobot Status changed (NOT_RUNNING --> STOPPED)"); //$NON-NLS-1$
			postNextQueueEntry(false, true);
			return;
		}

		if (sold.Status == VLCPlayerStatus.NOT_RUNNING) return;
		if (snew.Status == VLCPlayerStatus.NOT_RUNNING) return;

		if (sold.Status == VLCPlayerStatus.DISABLED) return;
		if (snew.Status == VLCPlayerStatus.DISABLED) return;

		if (sold.Status == VLCPlayerStatus.ERROR) return;
		if (snew.Status == VLCPlayerStatus.ERROR) return;

		if (sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.STOPPED && sold.isPlayingLastPlaylistEntry())
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING --> STOPPED)"); //$NON-NLS-1$
			postNextQueueEntry(true, true);
			return;
		}

		if (sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
			sold.isPlayingLastPlaylistEntry() && !sold.isEntryNearlyFinished(DELTA_NEARLY_FINISHED) && snew.isEntryNearlyFinished(DELTA_NEARLY_FINISHED))
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING --> PLAYING[nearly_finished])"); //$NON-NLS-1$
			postNextQueueEntry(false, false);
			return;
		}

		if (_clientQueue.size() > 0 &&
			sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
			sold.ActiveEntry != null && snew.ActiveEntry != null && !Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri) &&
			sold.ActiveEntry == sold.Playlist.get(sold.Playlist.size()-1) && snew.ActiveEntry == snew.Playlist.get(0) &&
			!sold.isEntryNearlyFinished(MIN_TIME_FOR_FORWARD_SKIP_NOT_FINISHED) && snew.isEntryJustStarted(MIN_TIME_FOR_FORWARD_SKIP_JUST_STARTED))
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[last] --skip--> PLAYING[first])"); //$NON-NLS-1$
			postNextQueueEntry(true, true);
			return;
		}

		if (sold.Status == VLCPlayerStatus.PLAYING && snew.Status == VLCPlayerStatus.PLAYING &&
			sold.ActiveEntry != null && snew.ActiveEntry != null && !Str.equals(sold.ActiveEntry.Uri, snew.ActiveEntry.Uri))
		{
			CCLog.addDebug("VLCRobot StatusTrigger (PLAYING[a] --> PLAYING[b])"); //$NON-NLS-1$
			fixVLCPosition();
			return;
		}
	}

	private void postNextQueueEntry(boolean fixPosition, boolean startPlayback)
	{
		if (_clientQueue.size() == 0) return;

		VLCPlaylistEntry q = _clientQueue.get(0);
		_clientQueue.remove(0);

		CCLog.addDebug("PostNextVLCEntry: [" + q.getText() + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		new Thread(() ->
		{
			List<String> parts = q.Element.getParts();
			for (int i=0; i<parts.size(); i++)
			{
				boolean ok = VLCConnection.enqueue(PathFormatter.fromCCPath(parts.get(i)), startPlayback && i==0);
				if (!ok)
				{
					SwingUtilities.invokeLater(() -> _clientQueue.add(0, q));
					return;
				}
			}

			SwingUtilities.invokeLater(() -> q.Element.updateViewedAndHistory());

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
							SwingUtilities.invokeLater(() -> _clientQueue.add(0, q));
							return;
						}
					}
				}
			}

			if (fixPosition) SwingUtilities.invokeLater(this::fixVLCPosition);

		}).start();
	}

	private void fixVLCPosition()
	{
		if (_lastPositionalStatus == null) return;
		if (!cbxKeepPosition.isSelected()) return;

		Rectangle rect = _lastPositionalStatus.WindowRect;

		new Thread(() ->
		{
			ThreadUtils.safeSleep(DELAY_SET_VLC_POSITION_INITIAL);

			VLCStatus current = _lastStatus;
			if (current == null) return;

			if (_lastPositionalStatus.Fullscreen)
			{
				//if (current.Fullscreen) return;
				//
				//VLCConnection.toggleFullscreen();
				return;
			}

			if (_lastPositionalStatus.WindowRect == null) return;
			if (current.WindowRect == null) return;
			if (current.WindowRect.equals(_lastPositionalStatus.WindowRect)) return;

			CCLog.addDebug("Manually adjust VLC Position: " + rect); //$NON-NLS-1$

			VLCConnection.setWindowPosition(rect);
			ThreadUtils.safeSleep(DELAY_SET_VLC_POSITION_RE_DO);
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

	private void onStart(ActionEvent ae)
	{
		VLCConnection.startPlayer();
	}

	private void onPlayPause(ActionEvent ae)
	{
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

	private void onClose(ActionEvent actionEvent) {
		//TODO
	}

	public void enqueue(ICCPlayableElement v)
	{
		if (_lastStatus == null)
		{
			_clientQueue.add(VLCPlaylistEntry.createQueueSingle(v));
		}
		else if (_lastStatus.Status == VLCPlayerStatus.STOPPED)
		{
			_clientQueue.add(VLCPlaylistEntry.createQueueSingle(v));
			updateList(_lastStatus);
			postNextQueueEntry(false, true);
		}
		else if (_lastStatus.Status == VLCPlayerStatus.NOT_RUNNING)
		{
			_clientQueue.add(VLCPlaylistEntry.createQueueSingle(v));
			updateList(_lastStatus);
			VLCConnection.startPlayer();
		}
		else
		{
			_clientQueue.add(VLCPlaylistEntry.createQueueSingle(v));
			updateList(_lastStatus);
		}
	}

	public void enqueue(CCSeason v)
	{
		//TODO
	}

	public void enqueue(CCSeries v)
	{
		var e = NextEpisodeHelper.findNextEpisode(v);
		//TODO
	}
}
