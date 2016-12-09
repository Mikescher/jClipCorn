package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import java.util.function.Function;

import javax.swing.Icon;

import de.jClipCorn.gui.guiComponents.tableRenderer.TableRenderer;

public class JCCSimpleTableCellRenderer<TData> extends TableRenderer {
	private static final long serialVersionUID = 7572425038209544688L;

	private Function<TData, String> text;
	private Function<TData, Icon> icon;

	public JCCSimpleTableCellRenderer(Function<TData, String> _text, Function<TData, Icon> _icon) {
		super();

		text = _text;
		icon = _icon;
	}

	@Override
	@SuppressWarnings({ "unchecked", "nls" })
	public void setValue(Object value) {
		TData el = (TData) value;

		setText((text == null) ? ("") : (text.apply(el)));
		setIcon((icon == null) ? (null) : (icon.apply(el)));
	}
}
