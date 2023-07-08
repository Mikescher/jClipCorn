package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.renderer.TableRenderer;
import de.jClipCorn.util.lambda.Func1to1;

import javax.swing.*;
import java.awt.*;

public class JCCSimpleTableCellRenderer<TData> extends TableRenderer {
	private static final long serialVersionUID = 7572425038209544688L;

	private Func1to1<TData, String> text;
	private Func1to1<TData, Icon> icon;
	private Func1to1<TData, Color> foreground;
	private Func1to1<TData, Color> background;

	public JCCSimpleTableCellRenderer(CCMovieList ml) {
		super(ml);

		text       = null;
		icon       = null;
		foreground = null;
		background = null;
	}

	public void setTextFunc(Func1to1<TData, String> v) {
		this.text = v;
	}

	public void setIconRenderer(Func1to1<TData, Icon> v) {
		this.icon = v;
	}

	public void setForegroundFunc(Func1to1<TData, Color> v) {
		this.foreground = v;
	}

	public void setBackgroundFunc(Func1to1<TData, Color> v) {
		this.background = v;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public void setValue(Object value) {
		TData el = (TData) value;

		setText((text == null) ? ("") : (text.invoke(el)));
		setIcon((icon == null) ? (null) : (icon.invoke(el)));
	}

	@SuppressWarnings({ "unchecked" })
	protected void patchComponent(Component c, Object value) {
		TData el = (TData) value;

		if (this.background != null) {
			var bg = this.background.invoke(el);
			if (bg != null) c.setBackground(bg);
		}

		if (this.foreground != null) {
			var fg = this.foreground.invoke(el);
			if (fg != null) c.setForeground(fg);
		}
	}

	@Override
	public boolean getNeedsExtraSpacing() {
		return true; // default for text columns
	}
}
