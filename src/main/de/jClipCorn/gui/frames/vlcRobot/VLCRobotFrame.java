package de.jClipCorn.gui.frames.vlcRobot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;

public class VLCRobotFrame extends JFrame {

	private static final long serialVersionUID = -9210243063139292492L;

	private final JPanel contentPanel = new JPanel();
	private VLCPlaylistTable lsData;
	private JLabel lblStatus;
	private JLabel label;
	private JProgressBar progressBar;
	private JPanel panel;
	private JButton btnClose;
	private JButton btnStart;

	/**
	 * @wbp.parser.constructor
	 */
	public VLCRobotFrame(JFrame owner) {
		super();

		initGUI();

		setLocationRelativeTo(owner);
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
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lsData = new VLCPlaylistTable();
		contentPanel.add(lsData, "2, 2, 5, 1, fill, fill");
		
		lblStatus = new JLabel("STATUS");
		contentPanel.add(lblStatus, "2, 4");
		
		progressBar = new JProgressBar();
		contentPanel.add(progressBar, "4, 4, fill, fill");
		
		label = new JLabel("00:30 / 00:47");
		contentPanel.add(label, "6, 4");
		
		panel = new JPanel();
		contentPanel.add(panel, "2, 6, 5, 1, fill, fill");
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("62px"),
				ColumnSpec.decode("0dlu:grow"),
				ColumnSpec.decode("66px"),},
			new RowSpec[] {
				RowSpec.decode("26px"),}));
		
		btnStart = new JButton("Start");
		btnStart.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(btnStart, "1, 1, left, top");
		
		btnClose = new JButton("Close");
		btnClose.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(btnClose, "3, 1, left, top");
	}

}
