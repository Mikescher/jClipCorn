package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.showIncompleteFilmSeriesFrame.ShowIncompleteFilmSeriesFrame;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

	}

	@Override
	public boolean shouldGrow() {
		return true;
	}

	public void onFilterDataChanged(JComponent comp, AbstractCustomFilter filter) {
		DefaultListModel<String> m = new DefaultListModel<>();
		for (String str : previewGetter.invoke()) m.addElement(str);
		((JList<String>)((JScrollPane)comp).getViewport().getView()).setModel(m);
	}
}
