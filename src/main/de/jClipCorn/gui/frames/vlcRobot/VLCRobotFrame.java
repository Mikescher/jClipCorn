package de.jClipCorn.gui.frames.vlcRobot;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.vlcquery.VLCConnection;
import de.jClipCorn.util.vlcquery.VLCStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class VLCRobotFrame extends JFrame {

	private static final long serialVersionUID = -9210243063139292492L;

	private final JPanel contentPanel = new JPanel();
	private VLCPlaylistTable lsData;
	private JLabel lblStatus;
	private JLabel lblTime;
	private JProgressBar progressBar;
	private JPanel panel;
	private JButton btnClose;
	private JButton btnStart;

	private final Object _timerLock = new Object();
	private volatile boolean _timerActive = false;
	private Timer _timer = null;

	private static VLCRobotFrame _instance = null;

	/**
	 * @wbp.parser.constructor
	 */
	private VLCRobotFrame(JFrame owner) {
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

	public static VLCRobotFrame show(JFrame owner)
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
			_instance.setLocationRelativeTo(owner);
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
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lsData = new VLCPlaylistTable();
		contentPanel.add(lsData, "2, 2, 5, 1, fill, fill"); //$NON-NLS-1$
		
		lblStatus = new JLabel();
		contentPanel.add(lblStatus, "2, 4");
		
		progressBar = new JProgressBar();
		contentPanel.add(progressBar, "4, 4, fill, fill"); //$NON-NLS-1$
		
		lblTime = new JLabel();
		contentPanel.add(lblTime, "6, 4"); //$NON-NLS-1$
		
		panel = new JPanel();
		contentPanel.add(panel, "2, 6, 5, 1, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("62px"), //$NON-NLS-1$
				ColumnSpec.decode("0dlu:grow"), //$NON-NLS-1$
				ColumnSpec.decode("66px"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("26px"),})); //$NON-NLS-1$
		
		btnStart = new JButton(LocaleBundle.getString("VLCRobotFrame.btnStart"));
		btnStart.setHorizontalAlignment(SwingConstants.LEFT);
		btnStart.addActionListener(this::onStart);
		panel.add(btnStart, "1, 1, left, top"); //$NON-NLS-1$
		
		btnClose = new JButton(LocaleBundle.getString("VLCRobotFrame.btnClose"));
		btnClose.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnClose, "3, 1, left, top"); //$NON-NLS-1$
	}

	private void onBackgroundUpdate()
	{
		final VLCStatus status = VLCConnection.getStatus();

		SwingUtilities.invokeLater(() ->
		{
			if (status.Status == VLCStatus.VLCPlayerStatus.PLAYING)
			{
				progressBar.setMaximum(status.CurrentLength);
				progressBar.setValue(status.CurrentTime);
				btnStart.setEnabled(false);
				btnClose.setEnabled(true);
				lblStatus.setText("Playing");
				lblTime.setText(TimeIntervallFormatter.formatLengthSeconds(status.CurrentTime) + " / " + TimeIntervallFormatter.formatLengthSeconds(status.CurrentLength));
			}
			else if (status.Status == VLCStatus.VLCPlayerStatus.STOPPED)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				lblStatus.setText("Stopped");
				lblTime.setText("");
			}
			else if (status.Status == VLCStatus.VLCPlayerStatus.NOT_RUNNING)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(true);
				btnClose.setEnabled(false);
				lblStatus.setText("NOT RUNNING");
				lblTime.setText("");
			}
			else if (status.Status == VLCStatus.VLCPlayerStatus.ERROR)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				lblStatus.setText("ERROR");
				lblTime.setText("");
			}
			else if (status.Status == VLCStatus.VLCPlayerStatus.DISABLED)
			{
				progressBar.setValue(0);
				btnStart.setEnabled(false);
				btnClose.setEnabled(false);
				lblStatus.setText("DISABLED");
				lblTime.setText("");
			}
		});
	}

	private void onStart(ActionEvent ae)
	{
		String vlc = MoviePlayer.getVLCPath();

		if (Str.isNullOrWhitespace(vlc)) {
			CCLog.addWarning(LocaleBundle.getString("LogMessage.VLCNotFound"));
			return;
		}

		try {
			java.util.List<String> parameters = MoviePlayer.getParameters(vlc);

			ProcessBuilder pb = new ProcessBuilder(parameters);
			pb.redirectOutput(new File(ApplicationHelper.getNullFile()));
			pb.redirectError(new File(ApplicationHelper.getNullFile()));
			pb.start();

		} catch (IOException e) {
			CCLog.addError(e);
		}
	}
}
