package de.jClipCorn.properties.property;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;

public class CCPathVarProperty extends CCProperty<PathSyntaxVar> {

	private class CCPathVarPropertyPanel extends JPanel {
		private static final long serialVersionUID = -9214196751986446909L;
		JTextField Field1; 
		JTextField Field2;
	}

	public CCPathVarProperty(CCPropertyCategory cat, CCProperties prop, String ident, PathSyntaxVar standard) {
		super(cat, PathSyntaxVar.class, prop, ident, standard);
	}

	@Override
	@SuppressWarnings("nls")
	public Component getComponent() {
		CCPathVarPropertyPanel pnl = new CCPathVarPropertyPanel();
		pnl.setLayout(new FormLayout(
			new ColumnSpec[]
			{
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("default"),
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("80px"),
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("default"),
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.UNRELATED_GAP_COLSPEC,
			},
			new RowSpec[]
			{
				FormSpecs.PREF_ROWSPEC,
			}));

		pnl.add(new JLabel(LocaleBundle.getString("CCPathVarProperty.Key")), "2, 1, fill, default"); //$NON-NLS-1$
		pnl.add(pnl.Field1 = new JTextField(), "4, 1, fill, default"); //$NON-NLS-1$
		pnl.add(new JLabel(LocaleBundle.getString("CCPathVarProperty.Value")), "6, 1, fill, default"); //$NON-NLS-1$
		pnl.add(pnl.Field2 = new JTextField(), "8, 1, fill, default"); //$NON-NLS-1$

		return pnl;
	}

	@Override
	public void setComponentValueToValue(Component c, PathSyntaxVar val) {
		((CCPathVarPropertyPanel)c).Field1.setText(val.Key);
		((CCPathVarPropertyPanel)c).Field2.setText(val.Value);
	}

	@Override
	public PathSyntaxVar getComponentValue(Component c) {
		return new PathSyntaxVar(((CCPathVarPropertyPanel)c).Field1.getText(), ((CCPathVarPropertyPanel)c).Field2.getText());
	}

	@Override
	public PathSyntaxVar getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try {
			String[] sval = val.split(";"); //$NON-NLS-1$
			if (sval.length == 0 || sval.length == 1) return PathSyntaxVar.EMPTY;
			return new PathSyntaxVar(Str.fromBase64(sval[0]), Str.fromBase64(sval[1]));
		} catch(NumberFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorNumber", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public PathSyntaxVar setValue(PathSyntaxVar val) {
		properties.setProperty(identifier, Str.toBase64(val.Key) + ";" + Str.toBase64(val.Value)); //$NON-NLS-1$

		return getValue();
	}
}
