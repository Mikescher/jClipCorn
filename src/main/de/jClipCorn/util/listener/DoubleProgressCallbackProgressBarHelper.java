package de.jClipCorn.util.listener;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;

public class DoubleProgressCallbackProgressBarHelper implements DoubleProgressCallbackListener {
	private final JProgressBar pbRoot;
	private final JLabel lblRoot;
	private final JProgressBar pbSub;
	private final JLabel lblSub;

	private int progressRoot;
	private int progressSub;

	private int maxRoot;
	private int maxSub;

	private String msgRoot;
	private String msgSub;

	public DoubleProgressCallbackProgressBarHelper(JProgressBar pRoot, JLabel lblRoot, JProgressBar pSub, JLabel lblSub) {
		this.pbRoot  = pRoot;
		this.lblRoot = lblRoot;
		this.pbSub   = pSub;
		this.lblSub  = lblSub;

		progressRoot = 0;
		maxRoot      = 1;

		progressSub = 0;
		maxSub      = 1;

		msgRoot     = Str.Empty;
		msgSub      = Str.Empty;
	}

	@Override
	public void setValueBoth(int valueRoot, int valueSub, String txtRoot, String txtSub) {
		this.progressRoot = valueRoot;
		this.msgRoot = txtRoot;

		this.progressSub = valueSub;
		this.msgSub = txtSub;

		update();
	}

	@Override
	public void setValueSub(int valueSub, String txtSub) {
		this.progressSub = valueSub;
		this.msgSub = txtSub;

		update();
	}

	@Override
	public void setMaxAndResetValueBoth(int maxRoot, int maxSub) {
		this.maxRoot = maxRoot;
		this.maxSub  = maxSub;

		this.progressRoot = 0;
		this.progressSub  = 0;

		this.msgRoot = Str.Empty;
		this.msgSub  = Str.Empty;

		update();
	}

	@Override
	public void setMaxAndResetValueSub(int maxSub) {
		this.maxSub = maxSub;

		this.progressSub = 0;

		this.msgSub = Str.Empty;

		update();
	}

	@Override
	public void stepRootAndResetSub(String msgRoot, int maxSub) {
		this.progressRoot++;
		this.msgRoot = msgRoot;

		this.progressSub = 0;
		this.maxSub = maxSub;
		this.msgSub = Str.Empty;
	}

	@Override
	public void reset() {
		this.progressRoot = 0;
		this.maxRoot      = 1;

		this.progressSub  = 0;
		this.maxSub       = 1;

		this.msgRoot      = Str.Empty;
		this.msgSub       = Str.Empty;

		update();
	}

	@Override
	public void setSubMax(int maxSub) {
		this.maxSub = maxSub;

		update();
	}

	@Override
	public void stepSub(String msgSub) {
		this.progressSub++;
		this.msgSub = msgSub;

		update();
	}

	private void update() {
		SwingUtils.invokeLater(() ->
		{
			pbRoot.setMaximum(maxRoot);
			pbRoot.setValue(progressRoot);
			lblRoot.setText(msgRoot);

			if (progressRoot > maxRoot) CCLog.addWarning("Invalid progress value ("+progressRoot+">"+maxRoot+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			pbSub.setMaximum(maxSub);
			pbSub.setValue(progressSub);
			lblSub.setText(msgSub);

			if (progressSub > maxSub) CCLog.addWarning("Invalid progress value ("+progressSub+">"+maxSub+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		});
	}
}
