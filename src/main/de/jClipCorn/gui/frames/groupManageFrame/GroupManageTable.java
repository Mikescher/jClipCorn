package de.jClipCorn.gui.frames.groupManageFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
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

	@DesignCreate
	private static GroupManageTable designCreate() { return new GroupManageTable(ICCWindow.Dummy.frame()); }

	public GroupManageTable(ICCWindow f) {
		super(f);
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<Tuple<CCGroup, Integer>> configureColumns() {
		JCCSimpleColumnList<Tuple<CCGroup, Integer>> r = new JCCSimpleColumnList<>(this);

		r.add("GroupManagerFrame.colColor")
				.withSize("auto")
				.withTooltip(e -> e.Item1.getHexColor())
				.withRenderer(this::renderColor);

		r.add("GroupManagerFrame.colName")
				.withSize("auto")
				.withText(e -> e.Item1.Name)
				.withSize("2*,min=auto");

		r.add("GroupManagerFrame.colSerialization")
				.withSize("auto")
				.withText(e -> e.Item1.DoSerialize
						? LocaleBundle.getString("ImportElementsFrame.common.bool_true")
						: LocaleBundle.getString("ImportElementsFrame.common.bool_false"));

		r.add("GroupManagerFrame.colVisible")
				.withText(e -> e.Item1.Visible
						? LocaleBundle.getString("ImportElementsFrame.common.bool_true")
						: LocaleBundle.getString("ImportElementsFrame.common.bool_false"));

		r.add("GroupManagerFrame.colParent")
				.withSize("1*,min=auto")
				.withText(e -> e.Item1.Parent);

		r.add("GroupManagerFrame.colCount")
				.withSize("auto")
				.withText(e -> String.valueOf(e.Item2));

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
