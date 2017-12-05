package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import javax.swing.Icon;

import de.jClipCorn.table.renderer.TableRenderer;
import de.jClipCorn.util.lambda.Func1to1;

public class JCCSimpleTableCellRenderer<TData> extends TableRenderer {
	private static final long serialVersionUID = 7572425038209544688L;

	private Func1to1<TData, String> text;
	private Func1to1<TData, Icon> icon;

	public JCCSimpleTableCellRenderer(Func1to1<TData, String> _text, Func1to1<TData, Icon> _icon) {
		super();

		text = _text;
		icon = _icon;
	}

	@Override
	@SuppressWarnings({ "unchecked", "nls" })
	public void setValue(Object value) {
		TData el = (TData) value;

		setText((text == null) ? ("") : (text.invoke(el)));
		setIcon((icon == null) ? (null) : (icon.invoke(el)));
	}
}
