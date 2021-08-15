package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.renderer.TableRenderer;
import de.jClipCorn.util.lambda.Func1to1;

import javax.swing.*;

public class JCCSimpleTableCellRenderer<TData> extends TableRenderer {
	private static final long serialVersionUID = 7572425038209544688L;

	private Func1to1<TData, String> text;
	private Func1to1<TData, Icon> icon;

	public JCCSimpleTableCellRenderer(CCMovieList ml, Func1to1<TData, String> _text, Func1to1<TData, Icon> _icon) {
		super(ml);

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

	@Override
	public boolean getNeedsExtraSpacing() {
		return true; // default for text columns
	}
}
