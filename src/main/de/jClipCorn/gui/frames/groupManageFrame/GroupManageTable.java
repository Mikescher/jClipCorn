package de.jClipCorn.gui.frames.groupManageFrame;

import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GroupManageTable extends JCCSimpleTable<Tuple<CCGroup, Integer>> {

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<Tuple<CCGroup, Integer>>> configureColumns() {
		List<JCCSimpleColumnPrototype<Tuple<CCGroup, Integer>>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"GroupManagerFrame.colColor",
				this::renderColor,
				e -> e.Item1.getHexColor()));

		r.add(new JCCSimpleColumnPrototype<>(
				"2*,min=auto",
				"GroupManagerFrame.colName",
				e -> e.Item1.Name,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"GroupManagerFrame.colSerialization",
				e -> e.Item1.DoSerialize
						? LocaleBundle.getString("ImportElementsFrame.common.bool_true")
						: LocaleBundle.getString("ImportElementsFrame.common.bool_false"),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"GroupManagerFrame.colVisible",
				e -> e.Item1.Visible
						? LocaleBundle.getString("ImportElementsFrame.common.bool_true")
						: LocaleBundle.getString("ImportElementsFrame.common.bool_false"),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"1*,min=auto",
				"GroupManagerFrame.colParent",
				e -> e.Item1.Parent,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"GroupManagerFrame.colCount",
				e -> String.valueOf(e.Item2),
				null,
				null));

		return r;
	}

	private Component renderColor(Tuple<CCGroup, Integer> value, Component component) {
		JPanel pnl = new JPanel();

		pnl.setBackground(value.Item1.Color);

		JPanel pnl2 = new JPanel(new BorderLayout());
		pnl2.setBorder(new EmptyBorder(2, 2, 2, 2));
		pnl2.add(pnl, BorderLayout.CENTER);
		pnl2.setBackground(component.getBackground());

		return pnl2;
	}

	@Override
	protected void OnDoubleClickElement(Tuple<CCGroup, Integer> element) {
		//
	}

	@Override
	protected void OnSelectElement(Tuple<CCGroup, Integer> element) {
		//
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
