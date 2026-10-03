package de.jClipCorn.properties.property;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCPathTextField;
import de.jClipCorn.gui.guiComponents.JValidatingCCPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class CCCCPathProperty extends CCProperty<CCPath> {

	private static class CCCCPathPropertyPanel extends JPanel {
		JValidatingCCPathTextField Field;

		// the settings frame puts this panel (not the field) into its tab order
		@Override
		public boolean requestFocusInWindow(FocusEvent.Cause cause) {
			return Field.requestFocusInWindow(cause);
		}
	}

	public CCCCPathProperty(CCPropertyCategory cat, CCProperties prop, String ident, CCPath standard) {
		super(cat, CCPath.class, prop, ident, standard);
	}

	/**
	 * Appends the button columns (choose: +2, remove: +4, add: +6 after the last leading column).
	 * Shared by all CCPath properties so their buttons line up in the settings frame.
	 */
	@SuppressWarnings("nls")
	public static ColumnSpec[] withButtonColumns(ColumnSpec... leading) {
		var r = new ArrayList<>(List.of(leading));
		for (int i = 0; i < 3; i++) {
			r.add(FormSpecs.RELATED_GAP_COLSPEC);
			r.add(ColumnSpec.decode("24dlu"));
		}
		return r.toArray(new ColumnSpec[0]);
	}

	public static JButton createChooseFolderButton(CCProperties ccprops, JCCPathTextField target, boolean insertVariables) {
		var btn = new JButton("..."); //$NON-NLS-1$
		btn.addActionListener(e ->
		{
			var chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

			var current = target.getPath().toFSPath(ccprops);
			if (!current.isEmpty() && current.directoryExists()) chooser.setSelectedFile(current.toFile());

			if (chooser.showOpenDialog(target) != JFileChooser.APPROVE_OPTION) return;

			target.setPath(CCPath.createFromFSPath(FSPath.create(chooser.getSelectedFile()), Opt.empty(), insertVariables, ccprops));
		});
		return btn;
	}

	@Override
	@SuppressWarnings("nls")
	public Component getComponent() {
		var pnl = new CCCCPathPropertyPanel();
		pnl.setLayout(new FormLayout(withButtonColumns(ColumnSpec.decode("default:grow")), new RowSpec[] { FormSpecs.PREF_ROWSPEC }));

		pnl.Field = new JValidatingCCPathTextField(properties, 1);
		pnl.add(pnl.Field, "1, 1, fill, default");
		pnl.add(createChooseFolderButton(properties, pnl.Field, true), "3, 1, fill, default");

		return pnl;
	}

	@Override
	public void setComponentValueToValue(Component c, CCPath val) {
		((CCCCPathPropertyPanel)c).Field.setPath(val);
	}

	@Override
	public CCPath getComponentValue(Component c) {
		return ((CCCCPathPropertyPanel)c).Field.getPath();
	}

	@Override
	public CCPath getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		return transformFromStorage(val);
	}

	@Override
	public CCPath setValue(CCPath val) {
		properties.setProperty(identifier, transformToStorage(val));

		return getValue();
	}

	@Override
	public boolean isValue(CCPath val) {
		return CCPath.isEqual(val, getValue());
	}

	protected String transformToStorage(CCPath value) {
		return value.toString();
	}

	protected CCPath transformFromStorage(String value) {
		return CCPath.create(value);
	}
}
