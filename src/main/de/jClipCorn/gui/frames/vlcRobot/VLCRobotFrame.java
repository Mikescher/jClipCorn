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
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class VLCRobotFrame extends JFrame {

	private static final long serialVersionUID = -9210243063139292492L;

	// Wait {x} ms before attempting to manually set the VLC position
	// and then wait {y} ms more before doing the second run
	// and then wait {z} ms more before doing the third run
	private static final int DELAY_SET_VLC_POSITION_INITIAL =  500;
	private static final int DELAY_SET_VLC_POSITION_RUN_2   = 1500;
	private static final int DELAY_SET_VLC_POSITION_RUN_3   = 2500;

	// ===========================================================================

	private VLCPlaylistTable lsData;
	private JLabel lblStatus;
	private JLabel lblTime;
	private JProgressBar progressBar;
	private JPanel panel;
	private JButton btnClose;
	private JButton btnStart;
	private JCheckBox cbxKeepPosition;
	private JButton btnPlayPause;
	private JPanel pnlMain;
	private JTabbedPane tabbedPane;
	private JPanel pnlLog;
	private VLCRobotLogTable logTable;
	private JSplitPane splitPane;
	private JPanel panel_1;
	private JTextArea edLogOld;
	private JTextArea edLogNew;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JLabel lblFrequency;
	private CCEnumComboBox<VLCRobotFrequency> cbxFreq;
	private JLabel lblTitle;

	private volatile boolean _stopThread = false;
	private final Object _threadLock = true;

	private VLCStatus _lastStatus = null;
	private VLCStatus _lastPositionalStatus = null;
	private final java.util.List<VLCPlaylistEntry> _clientQueue = new ArrayList<>();
	private volatile VLCRobotFrequency updateFreq = VLCRobotFrequency.MS_0500;

	private static VLCRobotFrame _instance = null;
	private JLabel lblNewLabel;
	private JPanel pnlInfo;
	private JTextArea lblText;

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
				_stopThread = true;
				synchronized (_threadLock) { _stopThread = true; }
				VLCRobotFrame.this.dispose();
				super.windowClosing(e);
			}
		});

		Thread tthread = new Thread(this::onThreadRun);
		tthread.start();
	}

	private void onClose(ActionEvent actionEvent) {
		_instance = null;
		_stopThread = true;
		synchronized (_threadLock) { _stopThread = true; }
		dispose();
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
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		pnlMain = new JPanel();
		tabbedPane.addTab(LocaleBundle.getString("VLCRobotFrame.tabMain"), null, pnlMain, null); //$NON-NLS-1$
		pnlMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlMain.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(35dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(35dlu;default)"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,}));
		
		lblTitle = new JLabel("ROBOT"); //$NON-NLS-1$
		pnlMain.add(lblTitle, "1, 1, center, center"); //$NON-NLS-1$
		
		cbxFreq = new CCEnumComboBox<>(VLCRobotFrequency.getWrapper());
		cbxFreq.setSelectedEnum(this.updateFreq = CCProperties.getInstance().PROP_VLC_ROBOT_FREQUENCY.getValue());
		cbxFreq.addActionListener(e ->
		{
			this.updateFreq = cbxFreq.getSelectedEnum();
			CCProperties.getInstance().PROP_VLC_ROBOT_FREQUENCY.setValue(this.updateFreq);
		});
		
		lblNewLabel = new JLabel(LocaleBundle.getString("VLCRobotFrame.lblFreq")); //$NON-NLS-1$
		pnlMain.add(lblNewLabel, "3, 1, right, fill"); //$NON-NLS-1$
		pnlMain.add(cbxFreq, "5, 1, fill, fill"); //$NON-NLS-1$
		
		lblFrequency = new JLabel("[FREQ]"); //$NON-NLS-1$
		pnlMain.add(lblFrequency, "7, 1, fill, fill"); //$NON-NLS-1$
		
		lsData = new VLCPlaylistTable();
		pnlMain.add(lsData, "1, 3, 7, 1, fill, fill"); //$NON-NLS-1$
		
		lblStatus = new JLabel();
		pnlMain.add(lblStatus, "1, 5, fill, fill"); //$NON-NLS-1$
		
		progressBar = new JProgressBar();
		pnlMain.add(progressBar, "3, 5, 3, 1, fill, fill"); //$NON-NLS-1$
		
		lblTime = new JLabel();
		pnlMain.add(lblTime, "7, 5, fill, fill"); //$NON-NLS-1$
		
		cbxKeepPosition = new JCheckBox(LocaleBundle.getString("VLCRobotFrame.cbxKeepPosition")); //$NON-NLS-1$
		cbxKeepPosition.setSelected(ApplicationHelper.isWindows() && CCProperties.getInstance().PROP_VLC_ROBOT_KEEP_POSITION.getValue());
		cbxKeepPosition.addActionListener(e ->  CCProperties.getInstance().PROP_VLC_ROBOT_KEEP_POSITION.setValue(cbxKeepPosition.isSelected()));
		cbxKeepPosition.setEnabled(ApplicationHelper.isWindows());
		pnlMain.add(cbxKeepPosition, "1, 7, 7, 1"); //$NON-NLS-1$
		
		panel = new JPanel();
		pnlMain.add(panel, "1, 9, 7, 1, fill, fill"); //$NON-NLS-1$
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
		
		btnPlayPause = new JButton(LocaleBundle.getString("VLCRobotFrame.btnPlay")); //$NON-NLS-1$
		btnPlayPause.addActionListener(this::onPlayPause);
		panel.add(btnPlayPause, "3, 1, fill, fill"); //$NON-NLS-1$
		
		btnClose = new JButton(LocaleBundle.getString("VLCRobotFrame.btnClose")); //$NON-NLS-1$
		btnClose.setHorizontalAlignment(SwingConstants.RIGHT);
		btnClose.addActionListener(this::onClose);
		panel.add(btnClose, "6, 1, fill, fill"); //$NON-NLS-1$
		
		pnlLog = new JPanel();
		tabbedPane.addTab(LocaleBundle.getString("VLCRobotFrame.tabLog"), null, pnlLog, null); //$NON-NLS-1$
		pnlLog.setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setContinuousLayout(true);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pnlLog.add(splitPane, BorderLayout.CENTER);
		
		logTable = new VLCRobotLogTable(this);
		splitPane.setLeftComponent(logTable);
		
		panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("50dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("50dlu:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("80px:grow"),})); //$NON-NLS-1$
		
		scrollPane = new JScrollPane();
		panel_1.add(scrollPane, "1, 1, fill, fill"); //$NON-NLS-1$
		
		edLogOld = new JTextArea();
		scrollPane.setViewportView(edLogOld);
		edLogOld.setEditable(false);
		
		scrollPane_1 = new JScrollPane();
		panel_1.add(scrollPane_1, "3, 1, fill, fill"); //$NON-NLS-1$
		
		edLogNew = new JTextArea();
		scrollPane_1.setViewportView(edLogNew);
		edLogNew.setEditable(false);

		tabbedPane.setSelectedIndex(0);
		
		pnlInfo = new JPanel();
		tabbedPane.addTab(LocaleBundle.getString("VLCRobotFrame.tabInfo"), null, pnlInfo, null); //$NON-NLS-1$
		pnlInfo.setLayout(new BorderLayout(0, 0));
		
		lblText = new JTextArea(LocaleBundle.getString("VLCRobotFrame.helpText")); //$NON-NLS-1$
		lblText.setEditable(false);
		lblText.setLineWrap(true);
		lblText.setBorder(new EmptyBorder(4, 4, 4, 4));
		pnlInfo.add(lblText);
	}

	private void onBackgroundUpdate(int idx)
	{
		final VLCStatus status = VLCConnection.getStatus();

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
			sold.ActiveEntry == sold.Playlist.get(sold.Playlist.size()-1) && snew.ActiveEntry == snew.Playlist.get(0) &&
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

	@SuppressWarnings("nls")
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
}
