package de.jClipCorn.gui.guiComponents.jCheckBoxList;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.gui.guiComponents.StringDisplayConverter;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.lambda.Func1to1;

import javax.swing.*;
import java.util.List;

public class JOrderableCheckBoxList<T> extends JPanel {
	private static final long serialVersionUID = 5208339446311385724L;

	private final JCheckBoxList<T> list;
	private final JButton btnUp;
	private final JButton btnDown;

	public JOrderableCheckBoxList(StringDisplayConverter<T> conv, List<T> values) {
		super();

		list    = new JCheckBoxList<>(conv, values);
		btnUp   = new JButton(Resources.ICN_GENERIC_BULLET_UP.get());
		btnDown = new JButton(Resources.ICN_GENERIC_BULLET_DOWN.get());

		setLayout(new FormLayout(
			new ColumnSpec[] { ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC }, //$NON-NLS-1$
			new RowSpec[] { FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, RowSpec.decode("default:grow") })); //$NON-NLS-1$

		add(list,    CC.xywh(1, 1, 1, 4, CC.FILL, CC.FILL));
		add(btnUp,   CC.xy(3, 1));
		add(btnDown, CC.xy(3, 3));

		btnUp.addActionListener(e -> moveSelected(-1));
		btnDown.addActionListener(e -> moveSelected(+1));
		list.addListSelectionListener(e -> updateButtons());

		updateButtons();
	}

	private void moveSelected(int delta) {
		int from = list.getSelectedIndex();
		int to = from + delta;
		if (from < 0 || to < 0 || to >= list.getModel().getSize()) return;

		list.moveElement(from, to);
		list.setSelectedIndex(to);
		list.ensureIndexIsVisible(to);
	}

	private void updateButtons() {
		int idx = list.getSelectedIndex();
		btnUp.setEnabled(idx > 0);
		btnDown.setEnabled(idx >= 0 && idx < list.getModel().getSize() - 1);
	}

	public void setValues(List<T> order, Func1to1<T, Boolean> checked) {
		list.clear();
		for (T v : order) list.add(v, checked.invoke(v));
		updateButtons();
	}

	public List<T> getAllElements() {
		return list.getAllElements();
	}

	public List<T> getCheckedElements() {
		return list.getCheckedElements();
	}
}
