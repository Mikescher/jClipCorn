package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.lambda.Func1to1;

public class JCCSimpleColumnPrototype<TData> {

	public final String Caption;
	public final TableCellRenderer CellRenderer;
	public final Func1to1<TData, String> GetTooltip;
	
	@SuppressWarnings("nls")
	public JCCSimpleColumnPrototype(String _captionIdent, Func1to1<TData, String> _rendererText, Func1to1<TData, Icon> _rendererIcon, Func1to1<TData, String> _getTooltip) {
		super();
		
		Caption = _captionIdent.isEmpty() ? "" : LocaleBundle.getString(_captionIdent);
		CellRenderer = new JCCSimpleTableCellRenderer<>(_rendererText, _rendererIcon);
		GetTooltip = (_getTooltip == null) ? (e -> null) : (_getTooltip);
	}
}
