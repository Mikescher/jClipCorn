package de.jClipCorn.properties.property;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JFSPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CCNamedPathProperty extends CCProperty<NamedPathVar> {

	private static class CCNamedPathPropertyPanel extends JPanel {
		private static final long serialVersionUID = -9214196751986446909L;
		JTextField       Field1;
		JFSPathTextField Field2;
		JTextField       Field3;
	}

	private final String filterEnd;
	private final CCFSPathProperty.CCPathPropertyMode filterMode;

	public CCNamedPathProperty(CCPropertyCategory cat, CCProperties prop, String ident, NamedPathVar standard, String filter, CCFSPathProperty.CCPathPropertyMode mode) {
		super(cat, NamedPathVar.class, prop, ident, standard);
		filterEnd = filter;
		filterMode = mode;
	}

	public String getFilterEnding() {
		return filterEnd;
	}

	@Override
	@SuppressWarnings("nls")
	public Component getComponent() {
		CCNamedPathPropertyPanel pnl = new CCNamedPathPropertyPanel();
		pnl.setLayout(new FormLayout(
			new ColumnSpec[]
			{
				FormSpecs.UNRELATED_GAP_COLSPEC,

				ColumnSpec.decode("default"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("160px"), //$NON-NLS-1$

				FormSpecs.UNRELATED_GAP_COLSPEC,

				ColumnSpec.decode("default"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
			},
			new RowSpec[]
			{
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
			}));

		pnl.add(new JLabel(LocaleBundle.getString("CCNamedPathProperty.Name")), "2, 1, fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
		pnl.add(pnl.Field1 = new JTextField(), "4, 1, fill, default"); //$NON-NLS-1$

		pnl.add(new JLabel(LocaleBundle.getString("CCNamedPathProperty.Path")), "6, 1, fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
		pnl.add(pnl.Field2 = new JFSPathTextField(), "8, 1, fill, default"); //$NON-NLS-1$

		pnl.add(new JLabel(LocaleBundle.getString("CCNamedPathProperty.Args")), "2, 3, fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
		pnl.add(pnl.Field3 = new JTextField(), "4, 3, 5, 1, fill, default"); //$NON-NLS-1$

		return pnl;
	}

	@Override
	public Component getSecondaryComponent(final Component firstComponent) {
		JButton btnChoose = new JButton("..."); //$NON-NLS-1$

		btnChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String end = getFilterEnding();
				JFileChooser vc = new JFileChooser();

				switch (filterMode) {
					case FILES:
						vc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						break;
					case DIRECTORIES:
						vc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						break;
					default:
						CCLog.addDefaultSwitchError(this, filterMode);
						break;
				}

				if (end != null && !end.trim().isEmpty())
					vc.setFileFilter(FileChooserHelper.createFullValidateFileFilter("Filter: " + end, val -> StringUtils.endsWithIgnoreCase(val, end))); //$NON-NLS-1$

				vc.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

				if (vc.showOpenDialog(firstComponent.getParent()) == JFileChooser.APPROVE_OPTION) {
					((CCNamedPathPropertyPanel)firstComponent).Field2.setPath(FSPath.create(vc.getSelectedFile()));
				}
			}
		});

		return btnChoose;
	}

	@Override
	public void setComponentValueToValue(Component c, NamedPathVar val) {
		((CCNamedPathPropertyPanel)c).Field1.setText(val.Name);
		((CCNamedPathPropertyPanel)c).Field2.setPath(val.Path);
		((CCNamedPathPropertyPanel)c).Field3.setText(val.Arguments);
	}

	@Override
	public NamedPathVar getComponentValue(Component c) {
		return new NamedPathVar(((CCNamedPathPropertyPanel)c).Field1.getText(), ((CCNamedPathPropertyPanel)c).Field2.getPath(), ((CCNamedPathPropertyPanel)c).Field3.getText());
	}

	@Override
	public NamedPathVar getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try {
			String[] sval = val.split(";"); //$NON-NLS-1$
			if (sval.length == 0 || sval.length == 1 || sval.length == 2) return NamedPathVar.EMPTY;
			return new NamedPathVar(Str.fromBase64(sval[0]).trim(), FSPath.create(Str.fromBase64(sval[1]).trim()), Str.fromBase64(sval[2]).trim());
		} catch(NumberFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorNumber", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public NamedPathVar setValue(NamedPathVar val) {
		properties.setProperty(identifier, val.serialize());

		return getValue();
	}

	@Override
	public boolean isValue(NamedPathVar val) {
		if (val == null) return false;
		return Str.equals(val.serialize(), getValue().serialize());
	}

	@Override public String getLabelRowAlign()      { return "default"; } //$NON-NLS-1$
	@Override public String getComponent1RowAlign() { return "default"; } //$NON-NLS-1$
	@Override public String getComponent2RowAlign() { return "top";     } //$NON-NLS-1$
	@Override public boolean getComponentBottomMargin() { return true; }
}
