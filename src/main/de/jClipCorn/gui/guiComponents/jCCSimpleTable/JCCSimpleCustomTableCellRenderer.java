package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import de.jClipCorn.features.table.renderer.TableRenderer;
import de.jClipCorn.util.lambda.Func2to1;

import javax.swing.*;
import java.awt.*;

public class JCCSimpleCustomTableCellRenderer<TData> extends TableRenderer {
	private static final long serialVersionUID = 7572425038209544688L;

	private final Func2to1<TData, Component, Component> component;

	public JCCSimpleCustomTableCellRenderer(Func2to1<TData, Component, Component> _comp) {
		super();

		component = _comp;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component sup = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (component != null) return component.invoke((TData) value, sup);

		return sup;
	}

	@Override
	public boolean getNeedsExtraSpacing() {
		return true; // default for text columns
	}
}
