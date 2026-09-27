package de.jClipCorn.gui.frames.shutdownFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;

import javax.swing.*;
import java.awt.*;

public class ShutdownFrame extends JCCFrame
{
	private final DefaultListModel<String> _steps = new DefaultListModel<>();
	private final Timer _timer = new Timer(250, e -> updateCurrentStep(System.nanoTime()));

	private String _currentStep = null;
	private long _currentStepStart; // System.nanoTime()

	public ShutdownFrame(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		lstSteps.setModel(_steps);
		_timer.start();
	}

	/** Must be called on the EDT - startNanos is the System.nanoTime() the step started at. */
	public void step(String msg, long startNanos)
	{
		updateCurrentStep(startNanos);

		_currentStep = msg;
		_currentStepStart = startNanos;
		_steps.addElement(msg);
		lstSteps.ensureIndexIsVisible(_steps.size() - 1);
	}

	private void updateCurrentStep(long nowNanos)
	{
		if (_currentStep == null) return;

		_steps.set(_steps.size() - 1, String.format("%s  (%.1fs)", _currentStep, (nowNanos - _currentStepStart) / 1_000_000_000.0)); //$NON-NLS-1$
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblInfo = new JLabel();
		pbProgress = new JProgressBar();
		scrollPane1 = new JScrollPane();
		lstSteps = new JList<>();

		//======== this ========
		setTitle(LocaleBundle.getString("ShutdownFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap",
			"$ugap, 2*(default, $lgap), default:grow, $ugap"));

		//---- lblInfo ----
		lblInfo.setText(LocaleBundle.getString("ShutdownFrame.lblInfo.text"));
		lblInfo.setFont(lblInfo.getFont().deriveFont(lblInfo.getFont().getStyle() | Font.BOLD));
		contentPane.add(lblInfo, CC.xy(2, 2));

		//---- pbProgress ----
		pbProgress.setIndeterminate(true);
		contentPane.add(pbProgress, CC.xy(2, 4, CC.FILL, CC.DEFAULT));

		//======== scrollPane1 ========
		{

			//---- lstSteps ----
			lstSteps.setFocusable(false);
			scrollPane1.setViewportView(lstSteps);
		}
		contentPane.add(scrollPane1, CC.xy(2, 6, CC.FILL, CC.FILL));
		setSize(450, 260);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblInfo;
	private JProgressBar pbProgress;
	private JScrollPane scrollPane1;
	private JList<String> lstSteps;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
