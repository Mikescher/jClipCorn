package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.renderer.TableRenderer;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.lambda.Func2to1;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class JCCSimpleColumnPrototype<TData> {

	private final CCMovieList movielist;

	public String AutoResizeConfig;

	public String Caption;

	private boolean CustomCellRenderer = false;
	public TableRenderer CellRenderer;

	public Func1to1<TData, String> GetTooltip;

	public boolean IsSortable;

	public JCCSimpleColumnPrototype(IJCCSimpleTable o, String _captionIdent) {
		super();

		this.movielist = o.getMovielist();

		AutoResizeConfig = "1";
		Caption = _captionIdent.isEmpty() ? Str.Empty : LocaleBundle.getString(_captionIdent);
		CellRenderer = new JCCSimpleTableCellRenderer<>(o.getMovielist());
		CustomCellRenderer = false;
		GetTooltip = (e -> null);
		IsSortable = false;
	}

	@SuppressWarnings("unchecked")
	public JCCSimpleColumnPrototype<TData> withText(Func1to1<TData, String> v) {
		if (this.CustomCellRenderer) throw new Error("cannot set text on an custom cell-renderer");

		((JCCSimpleTableCellRenderer<TData>)this.CellRenderer).setTextFunc(v);
		return this;
	}

	public JCCSimpleColumnPrototype<TData> withRenderer(Func2to1<TData, Component, Component> v) {
		this.CustomCellRenderer = true;
		this.CellRenderer = new JCCSimpleCustomTableCellRenderer<>(movielist, v);
		return this;
	}

	public JCCSimpleColumnPrototype<TData> withSize(String v) {
		this.AutoResizeConfig = v;
		return this;
	}

	@SuppressWarnings("unchecked")
	public JCCSimpleColumnPrototype<TData> withIcon(Func1to1<TData, Icon> v) {
		if (this.CustomCellRenderer) throw new Error("cannot set icon on an custom cell-renderer");

		((JCCSimpleTableCellRenderer<TData>)this.CellRenderer).setIconRenderer(v);
		return this;
	}

	public JCCSimpleColumnPrototype<TData> withTooltip(Func1to1<TData, String> v) {
		this.GetTooltip = (v == null) ? (e -> null) : (v);
		return this;
	}

	public JCCSimpleColumnPrototype<TData> sortable() {
		this.IsSortable = true;
		return this;
	}

	public JCCSimpleColumnPrototype<TData> unsortable() {
		this.IsSortable = false;
		return this;
	}

	@SuppressWarnings("unchecked")
	public JCCSimpleColumnPrototype<TData> withForeground(Func1to1<TData, Color> v) {
		if (this.CustomCellRenderer) throw new Error("cannot set foreground on an custom cell-renderer");

		((JCCSimpleTableCellRenderer<TData>)this.CellRenderer).setForegroundFunc(v);
		return this;
	}

	@SuppressWarnings("unchecked")
	public JCCSimpleColumnPrototype<TData> withBackground(Func1to1<TData, Color> v) {
		if (this.CustomCellRenderer) throw new Error("cannot set background on an custom cell-renderer");

		((JCCSimpleTableCellRenderer<TData>)this.CellRenderer).setBackgroundFunc(v);
		return this;
	}
}
