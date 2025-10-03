package de.jClipCorn.gui.guiComponents;

import javax.swing.*;
import java.awt.*;
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
	private Integer maxIconsPerRow = null;

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
		setLayout(new WrapLayout());
	}

	private class WrapLayout implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component comp) {}

		@Override
		public void removeLayoutComponent(Component comp) {}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return calculateLayoutSize(parent, false);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return calculateLayoutSize(parent, true);
		}

		@Override
		public void layoutContainer(Container parent) {
			synchronized (parent.getTreeLock()) {
				Insets insets = parent.getInsets();
				int maxWidth = parent.getWidth() - insets.left - insets.right;

				int x = insets.left;
				int y = insets.top;
				int rowHeight = 0;
				int iconsInRow = 0;

				Component[] components = parent.getComponents();
				for (int i = 0; i < components.length; i++) {
					Component comp = components[i];
					if (!comp.isVisible()) continue;

					Dimension d = comp.getPreferredSize();

					// Check if we need to wrap to next row
					boolean shouldWrap = false;
					if (maxIconsPerRow != null && maxIconsPerRow > 0 && iconsInRow >= maxIconsPerRow) {
						shouldWrap = true;
					} else if (x + d.width > maxWidth && iconsInRow > 0) {
						shouldWrap = true;
					}

					if (shouldWrap) {
						x = insets.left;
						y += rowHeight + gapY;
						rowHeight = 0;
						iconsInRow = 0;
					}

					comp.setBounds(x, y, d.width, d.height);

					x += d.width + gapX;
					rowHeight = Math.max(rowHeight, d.height);
					iconsInRow++;
				}
			}
		}

		private Dimension calculateLayoutSize(Container parent, boolean minimum) {
			synchronized (parent.getTreeLock()) {
				Insets insets = parent.getInsets();
				int maxWidth = parent.getWidth() - insets.left - insets.right;
				if (maxWidth <= 0) maxWidth = Integer.MAX_VALUE;

				int x = 0;
				int y = 0;
				int rowHeight = 0;
				int maxRowWidth = 0;
				int iconsInRow = 0;

				Component[] components = parent.getComponents();
				for (Component comp : components) {
					if (!comp.isVisible()) continue;

					Dimension d = minimum ? comp.getMinimumSize() : comp.getPreferredSize();

					// Check if we need to wrap to next row
					boolean shouldWrap = false;
					if (maxIconsPerRow != null && maxIconsPerRow > 0 && iconsInRow >= maxIconsPerRow) {
						shouldWrap = true;
					} else if (x + d.width > maxWidth && iconsInRow > 0) {
						shouldWrap = true;
					}

					if (shouldWrap) {
						maxRowWidth = Math.max(maxRowWidth, x - gapX);
						x = 0;
						y += rowHeight + gapY;
						rowHeight = 0;
						iconsInRow = 0;
					}

					x += d.width + gapX;
					rowHeight = Math.max(rowHeight, d.height);
					iconsInRow++;
				}

				maxRowWidth = Math.max(maxRowWidth, x - gapX);
				int totalHeight = y + rowHeight;

				return new Dimension(
					insets.left + maxRowWidth + insets.right,
					insets.top + totalHeight + insets.bottom
				);
			}
		}
	}

	private void recalcSize() {
		if (!calcPrefSize) return;

		revalidate();
		repaint();
	}

	private void update() {
		removeAll();

		for (var entry : value) {
			JLabel l = new JLabel(entry.Icon);
			l.setToolTipText(entry.Tooltip);
			add(l);
		}

		revalidate();
		repaint();

		Component parent = getParent();
		if (parent != null) {
			parent.revalidate();
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

	public Integer getMaxIconsPerRow() {
		return maxIconsPerRow;
	}

	public void setMaxIconsPerRow(Integer maxIconsPerRow) {
		this.maxIconsPerRow = maxIconsPerRow;
		recalcSize();
		revalidate();
		repaint();
	}
}
