package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LanguageSetDisplay extends JPanel {
	private static final long serialVersionUID = 2017286148720080714L;

	private final static int ICON_WIDTH  = 16;
	private final static int ICON_HEIGHT = 16;

	private final static int GAP_X = 4;
	private final static int GAP_Y = 1;

	private boolean calcPrefSize = false;

	private CCDBLanguageSet value = CCDBLanguageSet.EMPTY;

	public LanguageSetDisplay() {
		super();
		init();
		update();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, GAP_Y));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				LanguageSetDisplay.this.recalcSize();
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

		for (CCDBLanguage lng : value.ccstream().autosort()) {
			JLabel l = new JLabel(lng.getIcon());
			l.setToolTipText(lng.asString());
			add(l);

			var gap = new JPanel();
			gap.setPreferredSize(new Dimension(GAP_X, 1));
			gap.setMinimumSize(new Dimension(GAP_X, 1));
			gap.setMaximumSize(new Dimension(GAP_X, 1));
			add(gap);
		}

		Component parent = getParent();
		if (parent != null) {
			validate();
			getParent().revalidate();
		}

		setToolTipText(value.toOutputString());
	}

	public CCDBLanguageSet getValue() {
		return value;
	}

	public void setValue(CCDBLanguageSet v) {
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
