package de.jClipCorn.gui.guiComponents.jCCPrimaryTable;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.renderer.TableRenderer;
import de.jClipCorn.util.lambda.Func1to0;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.lambda.Func2to0;
import de.jClipCorn.util.lambda.Func2to1;

import javax.swing.table.TableCellRenderer;
import java.util.Comparator;

public class JCCPrimaryColumnPrototype<TData, TEnum> {

	public final TEnum Identifier;
	public final String Header;
	public final String AdjusterConfig;
	public final String DisplayName;
	public final Func2to0<TableRenderer, TData> Renderer;
	public final Func1to1<TableRenderer, Boolean> RendererNeedsExtraSpace;
	public final Func2to1<TData, TData, Integer> Comparator;
	public final Func2to1<TData, Integer, String> Tooltip;
	public final Func1to1<TData, Boolean> IsClickable;
	public final Func1to0<TData> Click;

	public JCCPrimaryColumnPrototype(
			TEnum id,
			String header,
			String adj,
			String displayName,
			Func2to0<TableRenderer, TData> renderer,
			Func1to1<TableRenderer, Boolean> rendererNeedsExtraSpace,
			Func2to1<TData, TData, Integer> comparator,
			Func2to1<TData, Integer, String> tooltip,
			Func1to1<TData, Boolean> clickable,
			Func1to0<TData> click
	)
	{
		this.Identifier              = id;
		this.Header                  = header;
		this.AdjusterConfig          = adj;
		this.DisplayName             = displayName;
		this.Renderer                = renderer;
		this.RendererNeedsExtraSpace = rendererNeedsExtraSpace;
		this.Comparator              = comparator;
		this.Tooltip                 = tooltip;
		this.IsClickable             = clickable;
		this.Click                   = click;
	}

	@SuppressWarnings("unchecked")
	public java.util.Comparator<?> createComparator() {
		return new Comparator<>() {
			@Override
			public int compare(Object o1, Object o2) {
				return Comparator.invoke((TData) o1, (TData) o2);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public TableCellRenderer createRenderer(CCMovieList ml) {
		return new TableRenderer(ml) {
			@Override
			public void setValue(Object value) {
				Renderer.invoke(this, (TData) value);
			}

			@Override
			public boolean getNeedsExtraSpacing() {
				return RendererNeedsExtraSpace.invoke(this); // unnecessary for icon-only columns
			}
		};
	}
}
