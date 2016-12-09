package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

import de.jClipCorn.gui.localization.LocaleBundle;

public class JCCSimpleColumnPrototype<TData> {

	public final int ID;
	public final String Caption;
	public final TableCellRenderer CellRenderer;

	public final Function<TData, String> GetTooltip;
	
	public JCCSimpleColumnPrototype(int _id, String _captionIdent, Function<TData, String> _rendererText, Function<TData, Icon> _rendererIcon, Function<TData, String> _getTooltip) {
		super();
		
		ID = _id;
		Caption = LocaleBundle.getString(_captionIdent);
		CellRenderer = new JCCSimpleTableCellRenderer<>(_rendererText, _rendererIcon);
		GetTooltip = (_getTooltip == null) ? (e -> null) : (_getTooltip);
	}
}
