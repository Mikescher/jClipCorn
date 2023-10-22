package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.gui.resources.reftypes.IconRef;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public abstract class IconRefListDisplay extends JPanel {
	public static class Entry {
		public final ImageIcon Icon;
		public final String Tooltip;

		public Entry(ImageIcon icon, String tooltip) {
			Icon = icon;
			Tooltip = tooltip;
		}
	}

	private final int iconWidth;
	private final int iconHeight;

	private final int gapX;
	private final int gapY;

	private boolean calcPrefSize = false;

	private java.util.List<Entry> value = new ArrayList<>();

	public IconRefListDisplay(int iconWidth, int iconHeight, int gapX, int gapY) {
		super();

		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
		this.gapX = gapX;
		this.gapY = gapY;

		init();
		update();
	}

	private void init() {
		var layout = new FlowLayout(FlowLayout.LEFT, 0, gapY);
		setLayout(layout);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				IconRefListDisplay.this.recalcSize();
			}
		});
	}

	private void recalcSize() {
		if (!calcPrefSize) return;

		var countX = 0;
		if (this.getWidth() >= 16) {
			countX = 1 + (this.getWidth()- iconWidth) / (iconWidth + gapX);
		}

		var prefX = countX * iconWidth + Math.max(0, (countX-1)* gapX);

		var countY = (int)Math.ceil((1.0 * this.value.size()) / countX);

		var prefY = countY * iconHeight + Math.max(0, (countY-1)* gapY);

		var prefDim = new Dimension(prefX, prefY);

		if (!getPreferredSize().equals(prefDim)) setPreferredSize(prefDim);
	}

	private void update() {
		removeAll();

		for (var entry : value) {
			JLabel l = new JLabel(entry.Icon);
			l.setToolTipText(entry.Tooltip);
			add(l);

			var spacer = new JPanel(null);
			spacer.setPreferredSize(new Dimension(gapX, iconHeight));
			spacer.setMinimumSize(new Dimension(gapX, iconHeight));
			spacer.setMaximumSize(new Dimension(gapX, iconHeight));
			add(spacer);
		}

		Component parent = getParent();
		if (parent != null) {
			validate();
			getParent().revalidate();
		}
	}

	public java.util.List<Entry> getIcons() {
		return value;
	}

	protected void setIcons(java.util.List<Entry> v) {
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
