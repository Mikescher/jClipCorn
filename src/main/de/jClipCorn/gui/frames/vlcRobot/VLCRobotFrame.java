package de.jClipCorn.gui.frames.vlcRobot;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.util.NextEpisodeHelper;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.vlcquery.VLCPlayerStatus;
import de.jClipCorn.util.vlcquery.VLCStatus;
import de.jClipCorn.util.vlcquery.VLCStatusPlaylistEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class VLCRobotFrame extends JCCFrame {

	private static VLCRobotFrame _instance = null;
	private final VLCRobot _robot;

	private VLCRobotFrame(Component owner, CCMovieList ml) {
		super(ml);

		var freq = ccprops().PROP_VLC_ROBOT_FREQUENCY.getValue();
		var fixvlc = ApplicationHelper.isWindows() && ccprops().PROP_VLC_ROBOT_KEEP_POSITION.getValue();
		var qpreempt = ccprops().PROP_VLC_ROBOT_QUEUE_PREEMPTIVE.getValue();
		_robot = new VLCRobot(ml, freq, fixvlc, qpreempt, createRobotListener());

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		_robot.start();
	}

	private void postInit() {
		setIconImage(Resources.IMG_FRAME_ICON.get());

		lsData.autoResize();

		cbxKeepPosition.setEnabled(ApplicationHelper.isWindows());

		tabbedPane.setSelectedIndex(0);
	}

	private VLCRobot.VLCRobotEventListener createRobotListener() {
		return new VLCRobot.VLCRobotEventListener() {
			@Override
			public void onFrequency(double hertz) {
				var htz = Str.format("{0,number,#.##} Hz", hertz); //$NON-NLS-1$
				lblFrequency.setText(htz);
			}

			@Override
			public void addLog(VLCRobotLogEntry entry) {
				logTable.addData(entry);
				logTable.autoResize();
			}

			@Override
			public void updateUI(VLCStatus lastStatus, VLCStatus status) {
				updateUIFromRobot(lastStatus, status);
			}

			@Override
			public void updateList(VLCStatus lastStatus) {
				updateQueueList(lastStatus);
			}
		};
	}

	private void updateUIFromRobot(VLCStatus lastStatus, VLCStatus status) {
		cbxFreq.setSelectedEnum(_robot.UpdateFrequency);
		cbxKeepPosition.setSelected(_robot.FixVLCPosition);
		cbxQueuePreemptive.setSelected(_robot.QueuePreemptive);

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

			updateQueueList(status);
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

			updateQueueList(status);
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

			updateQueueList(status);
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

			updateQueueList(status);
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
		}

		if (!cbxKeepPosition.isSelected())
			cbxKeepPosition.setText(LocaleBundle.getString("VLCRobotFrame.cbxKeepPosition")); //$NON-NLS-1$
		else if (lastStatus != null && lastStatus.Fullscreen)
			cbxKeepPosition.setText(LocaleBundle.getString("VLCRobotFrame.cbxKeepPosition_fullscreen")); //$NON-NLS-1$
		else if (lastStatus != null && lastStatus.WindowRect != null)
			cbxKeepPosition.setText(LocaleBundle.getMFFormattedString("VLCRobotFrame.cbxKeepPosition_value", lastStatus.WindowRect.x, lastStatus.WindowRect.y, lastStatus.WindowRect.width, lastStatus.WindowRect.height)); //$NON-NLS-1$
		else
			cbxKeepPosition.setText(LocaleBundle.getString("VLCRobotFrame.cbxKeepPosition")); //$NON-NLS-1$
	}

	private void updateQueueList(VLCStatus status)
	{
		ArrayList<VLCPlaylistEntry> data = new ArrayList<>();

		var clientQueue = _robot.getClientQueue();

		

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
				var inqueue = CCStreams
						.iterate(clientQueue)
						.firstOrNull(p -> p.IsPreemptiveAndEquals(pe.Uri));

				if (inqueue != null) {
					data.add(VLCPlaylistEntry.createVLCQueuePreemptive(pe, inqueue.Element, inqueue.Length));
					clientQueue.remove(inqueue);
				} else {
					data.add(VLCPlaylistEntry.createVLCPlayList(VLCPlaylistEntry.VPEType.VLC_OLD, pe));
				}
			}
			else
			{
				var inqueue = CCStreams
						.iterate(clientQueue)
						.firstOrNull(p -> p.IsPreemptiveAndEquals(pe.Uri));

				if (inqueue != null) {
					data.add(VLCPlaylistEntry.createVLCQueuePreemptive(pe, inqueue.Element, inqueue.Length));
					clientQueue.remove(inqueue);
				} else {
					data.add(VLCPlaylistEntry.createVLCPlayList(VLCPlaylistEntry.VPEType.VLC_QUEUE, pe));
				}
			}
		}

		data.addAll(clientQueue);

		if (queuediff(lsData.getDataCopy(), data)) {
			lsData.clearData();
			lsData.setData(data);
			lsData.autoResize();
		}
	}

	private boolean queuediff(List<VLCPlaylistEntry> d1, ArrayList<VLCPlaylistEntry> d2) {
		if (d1.size() != d2.size()) return true;

		for (int i=0; i < d1.size(); i++) {
			if (!d1.get(i).isEqual(d2.get(i))) return true;
		}

		return false;
	}

	public static VLCRobotFrame show(Component owner, CCMovieList ml)
	{
		if (_instance == null || !_instance.isVisible() || _instance.movielist != ml)
		{
			_instance = new VLCRobotFrame(owner, ml);
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

	public void enqueue(ICCPlayableElement v)
	{
		_robot.enqueue(VLCPlaylistEntry.createQueueSingle(v));
	}

	public void enqueue(CCSeason v)
	{
		var next = NextEpisodeHelper.findNextEpisode(v);
		if (next == null) return;
		var episodeList = CCStreams.iterate(v.getEpisodeList()).skipWhile(e -> e != next).<ICCPlayableElement>cast().toList();
		_robot.enqueue(VLCPlaylistEntry.createQueueAuto( v.getSeries().getTitle() + " - " + v.getTitle(), episodeList)); //$NON-NLS-1$
	}

	public void enqueue(CCSeries v)
	{
		var next = NextEpisodeHelper.findNextEpisode(v);
		if (next == null) return;
		var episodeList = CCStreams.iterate(v.getSortedEpisodeList()).skipWhile(e -> e != next).<ICCPlayableElement>cast().toList();
		_robot.enqueue(VLCPlaylistEntry.createQueueAuto(v.getTitle(), episodeList));
	}

	private void onFrequencyChanged() {
		_robot.UpdateFrequency = cbxFreq.getSelectedEnum();
		ccprops().PROP_VLC_ROBOT_FREQUENCY.setValue(_robot.UpdateFrequency);
	}

	private void onKeepPositionChanged() {
		_robot.FixVLCPosition = cbxKeepPosition.isSelected();
		ccprops().PROP_VLC_ROBOT_KEEP_POSITION.setValue(_robot.FixVLCPosition);
	}

	private void onQueuePreemptiveChanged() {
		_robot.QueuePreemptive = cbxQueuePreemptive.isSelected();
		ccprops().PROP_VLC_ROBOT_QUEUE_PREEMPTIVE.setValue(_robot.QueuePreemptive);
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
				edLogNew.setText(element.NewEntry.title().get());
			}
		}
	}

	private void onWindowClosing() {
		_instance = null;
		_robot.stop();
		dispose();
	}

	private void onStart() {
		_robot.getVLCConnection().startPlayer();
	}

	private void onPlayPause() {
		_robot.playpause();
	}

	private void onClose() {
		_instance = null;
		_robot.stop();
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
		lsData = new VLCPlaylistTable(this);
		lblStatus = new JLabel();
		progressBar = new JProgressBar();
		lblTime = new JLabel();
		cbxKeepPosition = new JCheckBox();
		cbxQueuePreemptive = new JCheckBox();
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
					"$rgap, default, $lgap, default:grow, 4*($lgap, default), $rgap")); //$NON-NLS-1$

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

				//---- cbxQueuePreemptive ----
				cbxQueuePreemptive.setText(LocaleBundle.getString("VLCRobotFrame.cbxQueuePreemptive")); //$NON-NLS-1$
				cbxQueuePreemptive.addActionListener(e -> onQueuePreemptiveChanged());
				pnlMain.add(cbxQueuePreemptive, CC.xywh(2, 10, 7, 1));

				//---- btnStart ----
				btnStart.setText(LocaleBundle.getString("VLCRobotFrame.btnStart")); //$NON-NLS-1$
				btnStart.addActionListener(e -> onStart());
				pnlMain.add(btnStart, CC.xy(2, 12));

				//---- btnPlayPause ----
				btnPlayPause.setText(LocaleBundle.getString("VLCRobotFrame.btnPlay")); //$NON-NLS-1$
				btnPlayPause.addActionListener(e -> onPlayPause());
				pnlMain.add(btnPlayPause, CC.xy(4, 12, CC.LEFT, CC.DEFAULT));

				//---- btnClose ----
				btnClose.setText(LocaleBundle.getString("VLCRobotFrame.btnClose")); //$NON-NLS-1$
				btnClose.addActionListener(e -> onClose());
				pnlMain.add(btnClose, CC.xy(8, 12));
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
		setSize(500, 380);
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
	private JCheckBox cbxQueuePreemptive;
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
