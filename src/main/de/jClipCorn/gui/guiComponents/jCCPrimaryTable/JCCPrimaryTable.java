package de.jClipCorn.gui.guiComponents.jCCPrimaryTable;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.TableColumnAdjuster;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.List;

public abstract class JCCPrimaryTable<TData, TEnum> extends JScrollPane
{
	protected final CCMovieList movielist;

	protected final JCCPrimarySFixTable<TData, TEnum> table;
	protected final JCCPrimaryTableModel<TData, TEnum> model;
	protected final TableColumnAdjuster adjuster;

	protected final List<JCCPrimaryColumnPrototype<TData, TEnum>> config;

	public JCCPrimaryTable(@NotNull ICCWindow mlo, boolean instantTooltip, boolean infiniteTooltips) {
		this(mlo.getMovieList(), instantTooltip, infiniteTooltips);
	}

	public JCCPrimaryTable(CCMovieList ml, boolean instantTooltip, boolean infiniteTooltips) {
		super();

		this.movielist = ml;

		this.config = configureColumns();

		this.model = new JCCPrimaryTableModel<>(this);
		this.table = new JCCPrimarySFixTable<>(this);

		if (instantTooltip)   this.table.setTooltipDelay(-1);
		if (instantTooltip)   this.table.setTooltipReshow(-1);
		if (infiniteTooltips) this.table.setTooltipDuration(Integer.MAX_VALUE);

		configureTable();

		this.setViewportView(table);

		this.adjuster = new TableColumnAdjuster(this, table);
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	public CCMovieList getMovielist() {
		return movielist;
	}

	private void configureTable() {
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowHeight(18);

		table.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			var elem = getSelectedElement();
			if (elem == null) return;
			onElementSelected(elem);
		});

		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				var row = table.rowAtPoint(e.getPoint());
				if (row < 0) return; // click on whitespace
				var elem = getSelectedElement();
				if (elem == null) return;
				onElementClicked(elem, e.getClickCount(), e.getButton());
			}

			@Override
			public void mousePressed(MouseEvent e) {
				onMouseAction(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				onMouseAction(e);
			}

			private void onMouseAction(MouseEvent e) {
				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
					int row = table.rowAtPoint(e.getPoint());
					if (row >= 0 && row < table.getRowCount()) {
						table.setRowSelectionInterval(row, row);
					} else {
						table.clearSelection();
					}

					int rowindex = table.getSelectedRow();
					if (rowindex >= 0) {
						if (e.isPopupTrigger()) {
							var elem = getSelectedElement();
							if (elem != null) onElementPopupTrigger(elem, e);
						}
					}
				}

				if (e.getButton() == MouseEvent.BUTTON1)
				{
					var clck = getClickableCellAtPoint(e.getPoint());

					if (clck != null) clck.Item2.Click.invoke(clck.Item1);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) { }

			@Override
			public void mouseExited(MouseEvent e) { }
		});

		table.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {

			}

			@Override
			public void mouseMoved(MouseEvent e) {
				var clck = getClickableCellAtPoint(e.getPoint());

				if (clck != null) setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				else              setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}

	private Tuple<TData, JCCPrimaryColumnPrototype<TData, TEnum>> getClickableCellAtPoint(Point p) {
		int vcol = table.columnAtPoint(p);
		int vrow = table.rowAtPoint(p);
		if (vcol == -1 || vrow == -1) return null;

		int mcol = table.convertColumnIndexToModel(vcol);
		int mrow = table.convertRowIndexToModel(vrow);
		if (mcol < 0 || mcol >= config.size()) return null;
		if (mrow < 0 || mcol >= model.getRowCount()) return null;

		var elem = model.getElementByModelRowIndex(mrow);
		var ccol = config.get(mcol);

		if (!ccol.IsClickable.invoke(elem)) return null;

		return Tuple.Create(elem, ccol);
	}

	public TData getSelectedElement() {
		int selrow = table.getSelectedRowInModelIndex();
		if (selrow >= 0) {
			return model.getElementByModelRowIndex(selrow);
		}
		return null;
	}

	protected int compareCoalesce(int... values) {
		for (var v : values) if (v != 0) return v;
		return 0;
	}

	protected void noop() {}

	public int getRowCount() {
		return model.getRowCount();
	}

	public int getFilteredRowCount() {
		return table.getRowSorter().getViewRowCount();
	}

	protected String getDefaultAdjusterConfig() {
		String[] cfg = new String[config.size()];
		Arrays.fill(cfg, "auto"); //$NON-NLS-1$

		for (var idx=0; idx < config.size(); idx++) {
			if (config.get(idx).HideColumn.invoke())
				cfg[idx] = "hide";
			else
				cfg[idx] = config.get(idx).AdjusterConfig;
		}

		return CCStreams.iterate(cfg).stringjoin(e->e, "|"); //$NON-NLS-1$
	}

	public void setSelectedRow(int visualrow) {
		if (visualrow < 0) return;

		table.getSelectionModel().setSelectionInterval(visualrow, visualrow);
	}

	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	protected abstract List<JCCPrimaryColumnPrototype<TData, TEnum>> configureColumns();

	protected abstract void onElementSelected(TData elem);
	protected abstract void onElementClicked(TData elem, int clickCount, int button);
	protected abstract void onElementPopupTrigger(TData elem, MouseEvent e);

	public abstract int getElementCountInDatastore();
	public abstract TData getElementFromDatastoreByIndex(int dsrow);

	public abstract Opt<Color> getRowColor(int visualrow, int modelrow, TData element);

	public abstract Opt<Integer> getUnitScrollIncrement();
	public abstract Opt<Integer> getBlockScrollIncrement();
}
