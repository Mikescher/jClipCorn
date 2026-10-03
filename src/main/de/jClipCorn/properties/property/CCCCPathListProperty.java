package de.jClipCorn.properties.property;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JValidatingCCPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.types.CCPathList;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.stream.CCStreams;
import org.json.JSONArray;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CCCCPathListProperty extends CCProperty<CCPathList> {

	private static class CCPathListPropertyPanel extends JPanel {
		final List<JValidatingCCPathTextField> Rows = new ArrayList<>();
		FormLayout Layout;
	}

	private String     _valueCacheKey = null;
	private CCPathList _valueCache    = null;

	public CCCCPathListProperty(CCPropertyCategory cat, CCProperties prop, String ident, CCPathList standard) {
		super(cat, CCPathList.class, prop, ident, standard);
	}

	@Override
	@SuppressWarnings("nls")
	public Component getComponent() {
		var pnl = new CCPathListPropertyPanel();
		pnl.Layout = new FormLayout(CCCCPathProperty.withButtonColumns(ColumnSpec.decode("default:grow")), new RowSpec[0]);
		pnl.setLayout(pnl.Layout);
		return pnl;
	}

	@Override
	public void setComponentValueToValue(Component c, CCPathList val) {
		rebuild((CCPathListPropertyPanel)c, val.Values);
	}

	private void rebuild(CCPathListPropertyPanel comp, List<CCPath> values) {
		comp.removeAll();
		comp.Rows.clear();
		while (comp.Layout.getRowCount() > 0) comp.Layout.removeRow(1);

		if (values.isEmpty()) addRow(comp, CCPath.Empty);
		else for (var v : values) addRow(comp, v);

		comp.revalidate();
		comp.repaint();
		if (comp.getParent() != null) {
			comp.getParent().revalidate();
			comp.getParent().repaint();
		}
	}

	@SuppressWarnings("nls")
	private void addRow(CCPathListPropertyPanel comp, CCPath value) {
		var first = comp.Rows.isEmpty();

		if (!first) comp.Layout.appendRow(FormSpecs.RELATED_GAP_ROWSPEC);
		comp.Layout.appendRow(FormSpecs.PREF_ROWSPEC);
		var r = comp.Layout.getRowCount();

		var row = new JValidatingCCPathTextField(properties, 1);
		row.setPath(value);
		comp.add(row, "1, "+r+", fill, default");

		comp.add(CCCCPathProperty.createChooseFolderButton(properties, row, true), "3, "+r+", fill, default");

		var btnRemove = new JButton("-");
		btnRemove.addActionListener(e ->
		{
			var values = CCStreams.iterate(comp.Rows).filter(p -> p != row).map(JValidatingCCPathTextField::getPath).enumerate();
			rebuild(comp, values);
		});
		comp.add(btnRemove, "5, "+r+", fill, default");

		if (first) {
			var btnAdd = new JButton("+");
			btnAdd.addActionListener(e ->
			{
				var values = CCStreams.iterate(comp.Rows).map(JValidatingCCPathTextField::getPath).enumerate();
				values.add(CCPath.Empty);
				rebuild(comp, values);
			});
			comp.add(btnAdd, "7, "+r+", fill, default");
		}

		comp.Rows.add(row);
	}

	@Override
	public CCPathList getComponentValue(Component c) {
		var comp = (CCPathListPropertyPanel)c;
		return new CCPathList(CCStreams.iterate(comp.Rows).map(JValidatingCCPathTextField::getPath).filter(p -> !p.isEmpty()).enumerate());
	}

	@Override
	public CCPathList getValue() {
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
	public CCPathList setValue(CCPathList val) {
		properties.setProperty(identifier, transformToStorage(val));

		return getValue();
	}

	@Override
	public boolean isValue(CCPathList val) {
		if (val == null) return false;
		return Str.equals(transformToStorage(val), transformToStorage(getValue()));
	}

	@Override
	public String getLabelRowAlign() {
		return "top"; //$NON-NLS-1$
	}

	private static String transformToStorage(CCPathList val) {
		var jarr = new JSONArray();

		for (var v : val.Values) {
			if (v.isEmpty()) continue;
			jarr.put(v.toString());
		}

		return jarr.toString();
	}

	private static CCPathList transformFromStorage(String val) {
		if (Str.isNullOrWhitespace(val)) return CCPathList.EMPTY;

		var jarr = new JSONArray(new JSONTokener(val));

		var result = new ArrayList<CCPath>();
		for (int i = 0; i < jarr.length(); i++) {
			result.add(CCPath.create(jarr.getString(i)));
		}

		return new CCPathList(result);
	}
}
