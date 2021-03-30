package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.lambda.Func2to1;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class JCCSimpleColumnPrototype<TData> {

	public final String AutoResizeConfig;
	public final String Caption;
	public final TableCellRenderer CellRenderer;
	public final Func1to1<TData, String> GetTooltip;
	
	@SuppressWarnings("nls")
	public JCCSimpleColumnPrototype(String autoResize, String _captionIdent, Func1to1<TData, String> _rendererText, Func1to1<TData, Icon> _rendererIcon, Func1to1<TData, String> _getTooltip) {
		super();

		AutoResizeConfig = autoResize;
		Caption = _captionIdent.isEmpty() ? "" : LocaleBundle.getString(_captionIdent);
		CellRenderer = new JCCSimpleTableCellRenderer<>(_rendererText, _rendererIcon);
		GetTooltip = (_getTooltip == null) ? (e -> null) : (_getTooltip);
	}

	@SuppressWarnings("nls")
	public JCCSimpleColumnPrototype(String autoResize, String _captionIdent, Func2to1<TData, Component, Component> _renderer, Func1to1<TData, String> _getTooltip) {
		super();

		AutoResizeConfig = autoResize;
		Caption = _captionIdent.isEmpty() ? "" : LocaleBundle.getString(_captionIdent);
		CellRenderer = new JCCSimpleCustomTableCellRenderer<>(_renderer);
		GetTooltip = (_getTooltip == null) ? (e -> null) : (_getTooltip);
	}
}
