package de.jClipCorn.properties.property;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.types.FrameSizeVar;
import de.jClipCorn.properties.types.HostFrameSizeVar;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;

public class CCFrameSizeProperty extends CCProperty<HostFrameSizeVar> {

	private static class CCFrameSizePropertyPanel extends JPanel {
		JSpinner Field0;
		JSpinner Field1;
	}

	public CCFrameSizeProperty(CCPropertyCategory cat, CCProperties prop, String ident, FrameSizeVar standard) {
		super(cat, HostFrameSizeVar.class, prop, ident, new HostFrameSizeVar(standard));
	}

	@Override
	@SuppressWarnings("nls")
	public Component getComponent() {
		CCFrameSizePropertyPanel pnl = new CCFrameSizePropertyPanel();
		pnl.setLayout(new FormLayout(
			new ColumnSpec[]
			{
				FormSpecs.UNRELATED_GAP_COLSPEC,   //
				ColumnSpec.decode("default"),      // label
				FormSpecs.UNRELATED_GAP_COLSPEC,   //
				ColumnSpec.decode("196px"),        // Field0
				FormSpecs.UNRELATED_GAP_COLSPEC,   //
				ColumnSpec.decode("default"),      // label
				FormSpecs.UNRELATED_GAP_COLSPEC,   //
				ColumnSpec.decode("196px"),        // Field1
				FormSpecs.UNRELATED_GAP_COLSPEC,   //
			},
			new RowSpec[]
			{
				FormSpecs.PREF_ROWSPEC,
			}));

		pnl.add(new JLabel(LocaleBundle.getString("DimensionAxisType.WIDTH")), "2, 1, fill, default"); //$NON-NLS-1$
		pnl.add(pnl.Field0 = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)), "4, 1, fill, default"); //$NON-NLS-1$
		pnl.add(new JLabel(LocaleBundle.getString("DimensionAxisType.HEIGHT")), "6, 1, fill, default"); //$NON-NLS-1$
		pnl.add(pnl.Field1 = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)), "8, 1, fill, default"); //$NON-NLS-1$

		return pnl;
	}

	@Override
	public void setComponentValueToValue(Component c, HostFrameSizeVar val) {
		// The generic editor (used by the ExtendedSettingsFrame) only edits the default value;
		// per-host overrides are managed via the FrameSizesTablePanel in the main settings frame.
		((CCFrameSizePropertyPanel)c).Field0.setValue(val.Default.Width);
		((CCFrameSizePropertyPanel)c).Field1.setValue(val.Default.Height);
	}

	@Override
	public HostFrameSizeVar getComponentValue(Component c) {
		var def = new FrameSizeVar(
				(Integer) ((CCFrameSizePropertyPanel)c).Field0.getValue(),
				(Integer) ((CCFrameSizePropertyPanel)c).Field1.getValue());

		// preserve any existing per-host overrides when only the default is edited here
		return new HostFrameSizeVar(def, getValue().Hosts);
	}

	@Override
	public HostFrameSizeVar getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try {
			return HostFrameSizeVar.parse(val);
		} catch(Exception e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatError", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public HostFrameSizeVar setValue(HostFrameSizeVar val) {
		properties.setProperty(identifier, val.serialize());

		return getValue();
	}

	@Override
	public boolean isValue(HostFrameSizeVar val) {
		if (val == null) return false;
		return Str.equals(val.serialize(), getValue().serialize());
	}

	public void applyOrPack(JCCFrame frame) {
		var val = getValue().resolve();

		if (val.isZero()) {
			frame.pack();
		} else {
			frame.setSize(val.Width, val.Height);
		}

		frame.onSizeAppliedViaProp();
	}

	public void applyOrSkip(JCCFrame frame) {
		var val = getValue().resolve();

		if (val.isZero()) {
			// skip
		} else {
			frame.setSize(val.Width, val.Height);
		}

		frame.onSizeAppliedViaProp();
	}

	public void applyOrPack(JCCDialog frame) {
		var val = getValue().resolve();

		if (val.isZero()) {
			frame.pack();
		} else {
			frame.setSize(val.Width, val.Height);
		}

		frame.onSizeAppliedViaProp();
	}

	public void applyOrSkip(JCCDialog frame) {
		var val = getValue().resolve();

		if (val.isZero()) {
			// skip
		} else {
			frame.setSize(val.Width, val.Height);
		}

		frame.onSizeAppliedViaProp();
	}
}
