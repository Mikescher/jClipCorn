package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.util.lambda.Func0to1;
import javax.swing.*;
import java.util.List;
import java.util.Random;

public class CustomFilterPreviewListConfig extends CustomFilterConfig {

	private final Func0to1<List<String>> previewGetter;

	public CustomFilterPreviewListConfig(Func0to1<List<String>> get) {
		previewGetter = get;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		JScrollPane spane = new JScrollPane();
		spane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		JList<String> list = new JList<>();
		spane.setViewportView(list);

		return spane;
	}

	@Override
	public void setValueRandom(Random r) {
		// has no intrinsic value
	}

	@Override
	public boolean shouldGrow() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onFilterDataChanged(JComponent comp, AbstractCustomFilter filter) {
		DefaultListModel<String> m = new DefaultListModel<>();
		for (String str : previewGetter.invoke()) m.addElement(str);
		((JList<String>)((JScrollPane)comp).getViewport().getView()).setModel(m);
	}
}
