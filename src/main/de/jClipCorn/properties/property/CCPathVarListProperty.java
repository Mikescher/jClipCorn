package de.jClipCorn.properties.property;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.properties.types.PathSyntaxVarList;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.stream.CCStreams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CCPathVarListProperty extends CCProperty<PathSyntaxVarList> {

	private static class CCPathVarListPropertyPanel extends JPanel {
		final List<CCPathVarRow> Rows = new ArrayList<>();
		FormLayout Layout;
	}

	private static class CCPathVarRow {
		JTextField       Host;
		JTextField       Key;
		JCCPathTextField Value;

		PathSyntaxVar get() {
			return new PathSyntaxVar(Host.getText(), Key.getText(), Value.getPath());
		}
	}

	private String            _valueCacheKey = null;
	private PathSyntaxVarList _valueCache    = null;

	public CCPathVarListProperty(CCPropertyCategory cat, CCProperties prop, String ident, PathSyntaxVarList standard) {
		super(cat, PathSyntaxVarList.class, prop, ident, standard);
	}

	/**
	 * Column layout of a single variable row, shared with the readonly commandline-override rows in the settings frame
	 * (host label: 2, host: 4, key label: 6, key: 8, value label: 10, value: 12, remove: 14, add: 16)
	 */
	@SuppressWarnings("nls")
	public static ColumnSpec[] createRowColumnSpecs(ColumnSpec hostLabelColumn) {
		return new ColumnSpec[]
		{
			FormSpecs.UNRELATED_GAP_COLSPEC,
			hostLabelColumn,
			FormSpecs.UNRELATED_GAP_COLSPEC,
			ColumnSpec.decode("165px"),
			FormSpecs.UNRELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.UNRELATED_GAP_COLSPEC,
			ColumnSpec.decode("120px"),
			FormSpecs.UNRELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.UNRELATED_GAP_COLSPEC,
			ColumnSpec.decode("default:grow"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("24dlu"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("24dlu"),
			FormSpecs.UNRELATED_GAP_COLSPEC,
		};
	}

	@Override
	public Component getComponent() {
		var pnl = new CCPathVarListPropertyPanel();
		pnl.Layout = new FormLayout(createRowColumnSpecs(FormSpecs.DEFAULT_COLSPEC), new RowSpec[0]);
		pnl.setLayout(pnl.Layout);
		return pnl;
	}

	@Override
	public void setComponentValueToValue(Component c, PathSyntaxVarList val) {
		rebuild((CCPathVarListPropertyPanel)c, val.Values);
	}

	private void rebuild(CCPathVarListPropertyPanel comp, List<PathSyntaxVar> values) {
		comp.removeAll();
		comp.Rows.clear();
		while (comp.Layout.getRowCount() > 0) comp.Layout.removeRow(1);

		if (values.isEmpty()) addRow(comp, PathSyntaxVar.EMPTY);
		else for (var v : values) addRow(comp, v);

		comp.revalidate();
		comp.repaint();
		if (comp.getParent() != null) {
			comp.getParent().revalidate();
			comp.getParent().repaint();
		}
	}

	@SuppressWarnings("nls")
	private void addRow(CCPathVarListPropertyPanel comp, PathSyntaxVar value) {
		var first = comp.Rows.isEmpty();

		if (!first) comp.Layout.appendRow(FormSpecs.RELATED_GAP_ROWSPEC);
		comp.Layout.appendRow(FormSpecs.PREF_ROWSPEC);
		var r = comp.Layout.getRowCount();

		var row = new CCPathVarRow();
		row.Host  = new JTextField(value.Hostname);
		row.Key   = new JTextField(value.Key);
		row.Value = new JCCPathTextField();
		row.Value.setPath(value.Value);

		comp.add(new JLabel(LocaleBundle.getString("CCPathVarListProperty.Host")), "2, "+r+", fill, default");
		comp.add(row.Host, "4, "+r+", fill, default");
		comp.add(new JLabel(LocaleBundle.getString("CCPathVarListProperty.Key")), "6, "+r+", fill, default");
		comp.add(row.Key, "8, "+r+", fill, default");
		comp.add(new JLabel(LocaleBundle.getString("CCPathVarListProperty.Value")), "10, "+r+", fill, default");
		comp.add(row.Value, "12, "+r+", fill, default");

		var btnRemove = new JButton("-");
		btnRemove.addActionListener(e ->
		{
			var values = CCStreams.iterate(comp.Rows).filter(p -> p != row).map(CCPathVarRow::get).enumerate();
			rebuild(comp, values);
		});
		comp.add(btnRemove, "14, "+r+", fill, default");

		if (first) {
			var btnAdd = new JButton("+");
			btnAdd.addActionListener(e ->
			{
				var values = CCStreams.iterate(comp.Rows).map(CCPathVarRow::get).enumerate();
				values.add(PathSyntaxVar.EMPTY);
				rebuild(comp, values);
			});
			comp.add(btnAdd, "16, "+r+", fill, default");
		}

		comp.Rows.add(row);
	}

	@Override
	public PathSyntaxVarList getComponentValue(Component c) {
		var comp = (CCPathVarListPropertyPanel)c;
		return new PathSyntaxVarList(CCStreams.iterate(comp.Rows).map(CCPathVarRow::get).filter(p -> !p.isEmpty()).enumerate());
	}

	@Override
	public PathSyntaxVarList getValue() {
		String val = properties.getProperty(identifier);

		if (val != null && val.equals(_valueCacheKey)) return _valueCache;

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try {
			var result = transformFromStorage(val);

			_valueCacheKey = val;
			_valueCache    = result;

			return result;
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.PropFormatError", identifier), e); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public PathSyntaxVarList setValue(PathSyntaxVarList val) {
		properties.setProperty(identifier, transformToStorage(val));

		return getValue();
	}

	@Override
	public boolean isValue(PathSyntaxVarList val) {
		if (val == null) return false;
		return Str.equals(transformToStorage(val), transformToStorage(getValue()));
	}

	@Override
	public String getLabelRowAlign() {
		return "top"; //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
	private static String transformToStorage(PathSyntaxVarList val) {
		var jarr = new JSONArray();

		for (var v : val.Values) {
			if (v.isEmpty()) continue;

			var jobj = new JSONObject();
			jobj.put("host", v.Hostname);
			jobj.put("key", v.Key);
			jobj.put("value", v.Value.toString());
			jarr.put(jobj);
		}

		return jarr.toString();
	}

	@SuppressWarnings("nls")
	private static PathSyntaxVarList transformFromStorage(String val) {
		if (Str.isNullOrWhitespace(val)) return PathSyntaxVarList.EMPTY;

		var jarr = new JSONArray(new JSONTokener(val));

		var result = new ArrayList<PathSyntaxVar>();
		for (int i = 0; i < jarr.length(); i++) {
			var jobj = jarr.getJSONObject(i);
			result.add(new PathSyntaxVar(jobj.optString("host", Str.Empty), jobj.optString("key", Str.Empty), CCPath.create(jobj.optString("value", Str.Empty))));
		}

		return new PathSyntaxVarList(result);
	}
}
