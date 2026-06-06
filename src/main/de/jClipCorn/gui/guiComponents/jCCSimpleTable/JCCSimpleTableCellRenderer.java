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
	private Func1to1<TData, Double> transparency;

	public JCCSimpleTableCellRenderer(CCMovieList ml) {
		super(ml);

		text         = null;
		icon         = null;
		foreground   = null;
		background   = null;
		transparency = null;
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

	public void setTransparencyFunc(Func1to1<TData, Double> v) {
		this.transparency = v;
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

		var fg = UIManager.getColor("TextField.foreground");

		if (this.foreground != null) {
			var fgFunc = this.foreground.invoke(el);
			if (fg != null) c.setForeground(fg = fgFunc);
		}

		if (this.transparency != null) {
			var tp = this.transparency.invoke(el);
			if (tp != null && fg != null) {
				c.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), (int) (255 * tp)));
			}
		}
	}

	@Override
	public boolean getNeedsExtraSpacing() {
		return true; // default for text columns
	}
}
