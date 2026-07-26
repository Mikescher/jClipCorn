package de.jClipCorn.gui.guiComponents;

import javax.swing.*;
import java.awt.*;

/**
 * A {@link JPanel} intended to be used directly as a {@link JScrollPane} viewport view.
 * It fills the available viewport width but keeps its content at its natural (preferred)
 * height.
 * <p>
 * A plain {@code JPanel} used as a scroll-pane view is vertically stretched by
 * {@link javax.swing.ViewportLayout} whenever the viewport is taller than the view's
 * preferred size ("view-fill" behavior). For a {@code FormLayout} form that makes the
 * row spacing wobble near the scroll threshold and lets the vertical scrollbar appear
 * too late (content can be clipped before the bar shows). Implementing {@link Scrollable}
 * with {@code getScrollableTracksViewportHeight() == false} pins the content to its
 * preferred height, so the gaps stay constant and the scrollbar appears exactly when the
 * content no longer fits.
 */
public class JScrollablePanel extends JPanel implements Scrollable {

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 16;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true; // fill the width (no horizontal scrolling)
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false; // keep natural height -> constant gaps + correct vertical scrollbar timing
	}
}
