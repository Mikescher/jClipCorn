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
import de.jClipCorn.properties.types.BinaryPathVar;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.stream.CCStreams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class CCExecutableProperty extends CCProperty<BinaryPathVar> {
	public interface ExecPathValidator {
		boolean Validate(FSPath p);
	}

	private static class CCExecutablePropertyPanel extends JPanel {
		java.util.List<CCExecutablePropertySingleSet> Groups;
		FormLayout Layout;
	}

	private static class CCExecutablePropertySingleSet {
		JTextField       Host;
		JFSPathTextField Path;
		JTextField       Args;
	}

	private final ExecPathValidator _validator;

	public CCExecutableProperty(CCPropertyCategory cat, CCProperties prop, String ident, FSPath standard, ExecPathValidator cfg) {
		super(cat, BinaryPathVar.class, prop, ident, new BinaryPathVar(Collections.singletonList(new BinaryPathVar.SingleBinaryPathVar(Str.Empty, standard, Str.Empty))));
		_validator = cfg;
	}

	@Override
	@SuppressWarnings("nls")
	public Component getComponent() {
		CCExecutablePropertyPanel pnl = new CCExecutablePropertyPanel();
		pnl.Groups = new ArrayList<>();

		var cs = new ColumnSpec[]
		{
			FormSpecs.UNRELATED_GAP_COLSPEC,

			ColumnSpec.decode("default"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("160px"),

			FormSpecs.UNRELATED_GAP_COLSPEC,

			ColumnSpec.decode("default"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("default:grow"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("default"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("50dlu"), // same as secondary-component-column
		};

		var fl = new FormLayout(cs, new RowSpec[]{});

		pnl.setLayout(fl);

		pnl.Layout = fl;

		return pnl;
	}

	@Override
	public void setComponentValueToValue(Component c, BinaryPathVar val) {

		var comp = (CCExecutablePropertyPanel)c;

		comp.removeAll();
		comp.Groups.clear();
		while (comp.Layout.getRowCount()>0)comp.Layout.removeRow(1);

		for (int i = 0; i < Math.max(val.Values.size(), 1); i++) {

			addRow(comp, (i < val.Values.size()) ? val.Values.get(i) : null, false);

		}
	}

	@SuppressWarnings({"nls", "ConstantValue"})
	private void addRow(CCExecutablePropertyPanel comp, BinaryPathVar.SingleBinaryPathVar value, boolean relayout) {

		var i = comp.Groups.size();

		if (i > 0) comp.Layout.appendRow(FormSpecs.UNRELATED_GAP_ROWSPEC);
		comp.Layout.appendRow(FormSpecs.PREF_ROWSPEC);
		comp.Layout.appendRow(FormSpecs.RELATED_GAP_ROWSPEC);
		comp.Layout.appendRow(RowSpec.decode("20dlu"));

		var set = new CCExecutablePropertySingleSet();

		set.Host = new JTextField();
		set.Path = new JFSPathTextField();
		set.Args = new JTextField();

		if (value != null) {
			set.Host.setText(value.Hostname);
			set.Path.setPath(value.Path);
			set.Args.setText(value.Args);
		}

		if (i == 0) {

			comp.add(new JLabel(LocaleBundle.getString("CCExecutableProperty.Host")), "2, "+(4*i + 2 - 1)+", fill, default");
			comp.add(set.Host, "4, "+(4*i + 2 - 1)+", fill, default");

			comp.add(new JLabel(LocaleBundle.getString("CCExecutableProperty.Path")), "6, "+(4*i + 2 - 1)+", fill, default");
			comp.add(set.Path, "8, "+(4*i + 2 - 1)+", 1, 1, fill, default");

			var btnChoose = new JButton("...");
			btnChoose.addActionListener(e -> {
				JFileChooser vc = new JFileChooser();
				vc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				vc.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

				if (vc.showOpenDialog(comp.getParent()) == JFileChooser.APPROVE_OPTION) {
					set.Path.setPath(FSPath.create(vc.getSelectedFile()));
				}
			});

			var btnCheck = new JButton("Check");
			btnCheck.addActionListener(e -> doCheck(comp));

			comp.add(btnCheck, "10, "+(4*i + 2 - 1)+", 1, 1, fill, default");
			comp.add(btnChoose, "12, "+(4*i + 2 - 1)+", 1, 1, left, default");

			var btnAdd = new JButton("+");
			btnAdd.addActionListener(e -> addRow(comp, new BinaryPathVar.SingleBinaryPathVar(ApplicationHelper.getHostname(), FSPath.Empty, Str.Empty), true));

			comp.add(new JLabel(LocaleBundle.getString("CCExecutableProperty.Args")), "2, "+(4*i + 4 - 1)+", fill, default");
			comp.add(set.Args, "4, "+(4*i + 4 - 1)+", 7, 1, fill, default");

			comp.add(btnAdd, "12, "+(4*i + 4 - 1)+", 1, 1, left, default");

		} else {

			comp.add(new JLabel(LocaleBundle.getString("CCExecutableProperty.Host")), "2, "+(4*i + 2 - 1)+", fill, default");
			comp.add(set.Host, "4, "+(4*i + 2 - 1)+", fill, default");

			comp.add(new JLabel(LocaleBundle.getString("CCExecutableProperty.Path")), "6, "+(4*i + 2 - 1)+", fill, default");
			comp.add(set.Path, "8, "+(4*i + 2 - 1)+", 3, 1, fill, default");

			var btnChoose = new JButton("...");
			btnChoose.addActionListener(e -> {
				JFileChooser vc = new JFileChooser();
				vc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				vc.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

				if (vc.showOpenDialog(comp.getParent()) == JFileChooser.APPROVE_OPTION) {
					set.Path.setPath(FSPath.create(vc.getSelectedFile()));
				}
			});

			comp.add(btnChoose, "12, "+(4*i + 2 - 1)+", 1, 1, left, default");

			comp.add(new JLabel(LocaleBundle.getString("CCExecutableProperty.Args")), "2, "+(4*i + 4 - 1)+", fill, default");
			comp.add(set.Args, "4, "+(4*i + 4 - 1)+", 7, 1, fill, default");

		}

		comp.Groups.add(set);

		if (relayout)
		{
			comp.getParent().invalidate();
			comp.getParent().revalidate();
			comp.getParent().repaint();

			comp.invalidate();
			comp.revalidate();
			comp.repaint();
		}
	}

	private void doCheck(CCExecutablePropertyPanel comp) {

		var hostname = ApplicationHelper.getHostname();

		for (var g : comp.Groups)
		{
			if (Str.isNullOrEmpty(g.Host.getText()))
			{
				g.Host.setBackground(g.Args.getBackground());
				g.Host.setForeground(g.Args.getForeground());
			}
			else if (g.Host.getText().equalsIgnoreCase(hostname))
			{
				g.Host.setBackground(Color.GREEN);
				g.Host.setForeground(Color.BLACK);
			}
			else
			{
				g.Host.setBackground(Color.ORANGE);
				g.Host.setForeground(Color.BLACK);
			}

			if (g.Path.getPath().isEmpty())
			{
				g.Path.setBackground(g.Args.getBackground());
				g.Path.setForeground(g.Args.getForeground());
			}
			else if (Str.isNullOrEmpty(g.Host.getText()) || g.Host.getText().equalsIgnoreCase(hostname))
			{
				if (_validator.Validate(g.Path.getPath()))
				{
					g.Path.setBackground(Color.GREEN);
					g.Path.setForeground(Color.BLACK);
				}
				else
				{
					g.Path.setBackground(Color.RED);
					g.Path.setForeground(Color.BLACK);
				}
			}
			else
			{
				g.Path.setBackground(g.Args.getBackground());
				g.Path.setForeground(g.Args.getForeground());
			}
		}
	}

	@Override
	public BinaryPathVar getComponentValue(Component c) {

		var comp = (CCExecutablePropertyPanel)c;

		var sbpv = CCStreams
				.iterate(comp.Groups)
				.map(p -> new BinaryPathVar.SingleBinaryPathVar(p.Host.getText(), p.Path.getPath(), p.Args.getText()))
				.filter(p -> !p.Path.isEmpty())
				.toList();

		return new BinaryPathVar(sbpv);
	}

	@Override
	public BinaryPathVar getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try
		{
			return transformFromStorage(val);
		}
		catch (Exception e)
		{
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.PropFormatError", identifier), e); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public BinaryPathVar setValue(BinaryPathVar val) {
		properties.setProperty(identifier, transformToStorage(val));

		return getValue();
	}

	public BinaryPathVar setValueSingle(FSPath val) {
		return setValue(new BinaryPathVar(Collections.singletonList(new BinaryPathVar.SingleBinaryPathVar(Str.Empty, val, Str.Empty))));
	}

	@Override
	public boolean isValue(BinaryPathVar val) {
		if (val == null) return false;
		return Str.equals(transformToStorage(val), transformToStorage(getValue()));
	}

	@Override
	public boolean getComponent1ColStretch() {
		return true;
	}

	@Override
	public boolean getComponentBottomMargin() {
		return true;
	}

	@SuppressWarnings("nls")
	private String transformToStorage(BinaryPathVar pvalue) {

		var pValArray = CCStreams.iterate(pvalue.Values).filter(p -> !p.Path.isEmpty()).toList();

		if (pValArray.size() == 0) return Str.Empty;

		if (pValArray.size() == 1)
		{
			var f0 = pValArray.get(0);
			if (Str.isNullOrEmpty(f0.Hostname) && Str.isNullOrEmpty(f0.Args) && !f0.Path.toString().startsWith("{"))
			{
				return f0.Path.toString();
			}
		}

		var jroot = new JSONObject();

		var jarr = new JSONArray();

		for (var sbpv: pValArray) {
			var jobj = new JSONObject();

			if(!Str.isNullOrWhitespace(sbpv.Hostname)) jobj.put("host", sbpv.Hostname);
			jobj.put("path", sbpv.Path.toString());
			if(!Str.isNullOrWhitespace(sbpv.Args)) jobj.put("args", sbpv.Args);

			jarr.put(jobj);
		}

		jroot.put("d", jarr);

		return jroot.toString();
	}

	@SuppressWarnings("nls")
	private BinaryPathVar transformFromStorage(String value) throws Exception {

		if (Str.isNullOrWhitespace(value)) return new BinaryPathVar(new ArrayList<>());

		if (!value.startsWith("{")) {
			return new BinaryPathVar(Collections.singletonList(new BinaryPathVar.SingleBinaryPathVar(Str.Empty, FSPath.create(value), Str.Empty)));
		}

		JSONObject root = new JSONObject(new JSONTokener(value));

		var jarr = root.getJSONArray("d");

		var sbpvl = new ArrayList<BinaryPathVar.SingleBinaryPathVar>();

		for (int i = 0; i < jarr.length(); i++) {
			var jobj = jarr.getJSONObject(i);
			var host = jobj.has("host") ? jobj.getString("host") : Str.Empty;
			var path = FSPath.create(jobj.getString("path"));
			var args = jobj.has("args") ? jobj.getString("args") : Str.Empty;
			sbpvl.add(new BinaryPathVar.SingleBinaryPathVar(host, path, args));
		}

		return new BinaryPathVar(sbpvl);
	}
}
