package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.types.FrameSizeVar;

import javax.swing.*;
import java.awt.*;

/**
 * Compact {@code [checkbox] {width} x {height} [capture]} editor for a single {@link FrameSizeVar}.
 * <p>
 * The width/height spinners accept at most 4 digits and use no thousand-separator. The leading checkbox is
 * optional (shown for per-host columns, hidden for the always-present default column); when it is shown and
 * unchecked the inputs are disabled and the control represents "no override".
 */
public class JCCFrameDimensionInput extends JPanel {
	private static final long serialVersionUID = 1L;

	private final JCheckBox chkEnabled;
	private final JSpinner  spnWidth;
	private final JSpinner  spnHeight;
	private final JButton   btnCapture;

	private Runnable captureAction = null;

	public JCCFrameDimensionInput(boolean withCheckbox) {
		super();

		setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));

		if (withCheckbox) {
			chkEnabled = new JCheckBox();
			chkEnabled.setMargin(new Insets(0, 0, 0, 0));
			chkEnabled.addActionListener(e -> updateEnabledState());
			add(chkEnabled);
		} else {
			chkEnabled = null;
		}

		spnWidth  = createSpinner();
		spnHeight = createSpinner();

		add(spnWidth);
		add(new JLabel("x")); //$NON-NLS-1$
		add(spnHeight);

		btnCapture = new JButton(Resources.ICN_GENERIC_BULLET_DOWN.get());
		btnCapture.setMargin(new Insets(0, 0, 0, 0));
		btnCapture.setPreferredSize(new Dimension(22, 22));
		btnCapture.setToolTipText(LocaleBundle.getString("FrameSizesTable.capture.tooltip")); //$NON-NLS-1$
		btnCapture.addActionListener(e -> { if (captureAction != null) captureAction.run(); });
		add(btnCapture);

		updateEnabledState();
	}

	private static JSpinner createSpinner() {
		var spn = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
		var editor = new JSpinner.NumberEditor(spn, "0"); //$NON-NLS-1$ - pattern "0" => no thousand-separator
		editor.getTextField().setColumns(4);
		spn.setEditor(editor);
		return spn;
	}

	private void updateEnabledState() {
		boolean en = isOverrideEnabled();
		spnWidth.setEnabled(en);
		spnHeight.setEnabled(en);
		btnCapture.setEnabled(en);
	}

	/** @return true if there is no checkbox (always-on default column) or the checkbox is checked. */
	public boolean isOverrideEnabled() {
		return chkEnabled == null || chkEnabled.isSelected();
	}

	public FrameSizeVar getFrameSize() {
		return new FrameSizeVar((Integer) spnWidth.getValue(), (Integer) spnHeight.getValue());
	}

	public void setValue(FrameSizeVar size, boolean overrideEnabled) {
		spnWidth.setValue(size.Width);
		spnHeight.setValue(size.Height);
		if (chkEnabled != null) chkEnabled.setSelected(overrideEnabled);
		updateEnabledState();
	}

	/** Fills the spinners with the given size and (if present) checks the override checkbox. */
	public void captureValue(FrameSizeVar size) {
		spnWidth.setValue(size.Width);
		spnHeight.setValue(size.Height);
		if (chkEnabled != null) chkEnabled.setSelected(true);
		updateEnabledState();
	}

	public void setCaptureAction(Runnable r) {
		this.captureAction = r;
	}
}
