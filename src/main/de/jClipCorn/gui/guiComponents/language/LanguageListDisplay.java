package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LanguageListDisplay extends JPanel {
	private static final long serialVersionUID = 2017286148720080714L;

	private final static int ICON_WIDTH  = 16;
	private final static int ICON_HEIGHT = 16;

	private final static int GAP_X = 4;
	private final static int GAP_Y = 1;

	private boolean calcPrefSize = false;

	private CCDBLanguageList value = CCDBLanguageList.EMPTY;

	public LanguageListDisplay() {
		super();
		init();
		update();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, GAP_X, GAP_Y));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				LanguageListDisplay.this.recalcSize();
			}
		});
	}

	private void recalcSize() {
		if (!calcPrefSize) return;

		var countX = 0;
		if (this.getWidth() >= 16) {
			countX = 1 + (this.getWidth()-ICON_WIDTH) / (ICON_WIDTH + GAP_X);
		}

		var prefX = countX * ICON_WIDTH + Math.max(0, (countX-1)*GAP_X);

		var countY = (int)Math.ceil((1.0 * this.value.size()) / countX);

		var prefY = countY * ICON_HEIGHT + Math.max(0, (countY-1)*GAP_Y);

		var prefDim = new Dimension(prefX, prefY);

		if (!getPreferredSize().equals(prefDim)) setPreferredSize(prefDim);
	}

	private void update() {
		removeAll();

		for (CCDBLanguage lng : value.ccstream()) {
			JLabel l = new JLabel(lng.getIcon());
			l.setToolTipText(lng.asString());
			add(l);
		}

		recalcSize();

		Component parent = getParent();
		if (parent != null) {
			validate();
			getParent().revalidate();
		}
	}

	public CCDBLanguageList getValue() {
		return value;
	}

	public void setValue(CCDBLanguageList v) {
		value = v;
		update();
	}

	public boolean getDoCalculatePrefSize() {
		return calcPrefSize;
	}

	public void setDoCalculatePrefSize(boolean v) {
		calcPrefSize = v;
		recalcSize();
	}
}
