package de.jClipCorn.util;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.renderer.ResizableColumnRenderer;
import de.jClipCorn.util.helper.RegExHelper;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {
	private final static int COLUMNBORDER_WIDTH = 6; //Wert durch testen ermittelt ...

	private enum TCACType { Auto, Fill, Fixed, Keep }
	private static class TCAConfig
	{
		public static final int PLACEHOLDER_AUTO = -99999;
		public static TCAConfig KEEP = new TCAConfig(TCACType.Keep, 0, 0, -1, -1, false);
		public final TCACType Type; public final int Length; public final int Weight;
		public final int Min; public final int Max; public final boolean OnlyExpand;
		public TCAConfig(TCACType t, int l, int w, int mi, int ma, boolean oe) { Type = t; Length = l; Weight = w; Max=ma; Min=mi; OnlyExpand=oe; }
	}

	private final JScrollPane owner;
	private final JTable table;
	private final int spacing;

	private int[] _autoCache = null;

	private String _currentConfigStr = null;
	private List<TCAConfig> _config = new ArrayList<>();

	private int _lastTableWidth = -1;
	private int _lastOwnerWidth = -1;

	/*
	 * Specify the table and use default spacing
	 */
	public TableColumnAdjuster(JScrollPane o, JTable table) {
		this(o, table, 6);
	}

	/*
	 * Specify the table and spacing
	 */
	public TableColumnAdjuster(JScrollPane o, JTable table, int spacing) {
		this.table = table;
		this.owner = o;
		this.spacing = spacing;
		configureTable();
		installResizeAdjuster();
	}

	private void configureTable() {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}

	private List<TCAConfig> getConfig(TableColumnModel tcm) {
		List<TCAConfig> r = new ArrayList<>(_config);

		if (r.size() != tcm.getColumnCount())
		{
			CCLog.addWarning("TableColumnAdjuster config does not match: '" + _currentConfigStr + "'"); //$NON-NLS-1$ //$NON-NLS-2$

			while (r.size() < tcm.getColumnCount()) r.add(TCAConfig.KEEP);
			while (r.size() > tcm.getColumnCount()) r.remove(r.size() - 1);
		}

		return r;
	}
	
	private void installResizeAdjuster() {
		table.getParent().addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				// <epmty>
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {

				int wo = owner.getWidth();
				int wt = table.getWidth();
				if (wo == _lastOwnerWidth && wt >= _lastTableWidth) return; // prevent column resize action triggering this

				_lastTableWidth = wt;
				_lastOwnerWidth = wo;

				TableColumnModel tcm = table.getColumnModel();
				if (getConfig(tcm).stream().anyMatch(e -> e.Type == TCACType.Fill)) _adjustColumns(tcm);
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				// <epmty>
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
				// <epmty>
			}
		});
	}

	private int getContainerWidth() {
		return table.getParent().getWidth();
	}

	@SuppressWarnings("nls")
	public void setConfig(String dat)
	{
		if (dat.equals(_currentConfigStr)) return;

		List<TCAConfig> cfg = new ArrayList<>();

		for (String d : dat.split("\\|"))
		{
			d = d.toLowerCase();

			String[] components = d.split(",");

			int min = -1;
			int max = -1;
			boolean expandOnly = false;

			for (int i=1; i<components.length; i++)
			{
				if (components[i].startsWith("max="))
				{
					String substr = components[i].substring("max=".length());
					if ("auto".equals(substr))
						max = TCAConfig.PLACEHOLDER_AUTO;
					else
						max = Integer.parseInt(substr);
				}
				else if (components[i].startsWith("min="))
				{
					String substr = components[i].substring("min=".length());
					if ("auto".equals(substr))
						min = TCAConfig.PLACEHOLDER_AUTO;
					else
						min = Integer.parseInt(substr);
				}
				else if (components[i].startsWith("expandonly"))
				{
					expandOnly = true;
				}
				else
				{
					throw new Error("Cannot parse TCA-config-string: '" + dat + "'");
				}
			}

			if ("auto".equals(components[0]))
			{
				cfg.add(new TCAConfig(TCACType.Auto, 0, 0, min, max, expandOnly));
			}
			else if (RegExHelper.isMatch("^[0-9]+$", components[0]))
			{
				cfg.add(new TCAConfig(TCACType.Fixed, Integer.parseInt(d), 0, min, max, expandOnly));
			}
			else if ("fill".equals(components[0]))
			{
				cfg.add(new TCAConfig(TCACType.Fill, 0, 1, min, max, expandOnly));
			}
			else if ("*".equals(components[0]))
			{
				cfg.add(new TCAConfig(TCACType.Fill, 0, 1, min, max, expandOnly));
			}
			else if ("star".equals(components[0]))
			{
				cfg.add(new TCAConfig(TCACType.Fill, 0, 1, min, max, expandOnly));
			}
			else if (RegExHelper.isMatch("^[0-9]+\\*$", components[0]))
			{
				cfg.add(new TCAConfig(TCACType.Fill, 0, Integer.parseInt(d.substring(0, d.length()-1)), min, max, expandOnly));
			}
			else if ("#".equals(components[0]))
			{
				cfg.add(new TCAConfig(TCACType.Keep, 0, 0, min, max, expandOnly));
			}
			else if ("keep".equals(components[0]))
			{
				cfg.add(new TCAConfig(TCACType.Keep, 0, 0, min, max, expandOnly));
			}
			else
			{
				throw new Error("Cannot parse TCA-config-string: '" + dat + "'");
			}
		}

		_currentConfigStr = dat;
		_config = cfg;
	}

	public void adjustColumns(String config) {
		_autoCache = null;
		setConfig(config);
		_adjustColumns(table.getColumnModel());
	}

	private void _adjustColumns(TableColumnModel tcm) {
		if (_currentConfigStr == null) return;

		List<TCAConfig> cfg = getConfig(tcm);

		int totalWidth = getContainerWidth() - COLUMNBORDER_WIDTH;

		float[] columns = new float[tcm.getColumnCount()];
		TableColumn[] tcols = new TableColumn[tcm.getColumnCount()];
		int[] auto = _autoCache;

		if (auto == null || auto.length != tcm.getColumnCount())
		{
			auto = new int[tcm.getColumnCount()];
			for (int i = 0; i < tcm.getColumnCount(); i++) auto[i] = Math.max(getColumnHeaderWidth(i), getColumnDataWidth(i));
			_autoCache = auto;
		}

		for (int i = 0; i < tcm.getColumnCount(); i++) columns[i] = Float.NaN;
		for (int i = 0; i < tcm.getColumnCount(); i++) tcols[i]   = table.getColumnModel().getColumn(i);

		// [1] Skip non-resizable columns
		for (int i = 0; i < tcm.getColumnCount(); i++) if (!tcols[i].getResizable()) columns[i] = tcols[i].getWidth();

		// [2] Process [KEEP] configured columns
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			if (Float.isNaN(columns[i]) && cfg.get(i).Type == TCACType.Keep)
			{
				int w = tcols[i].getWidth();
				if (w<0) w=0;
				if (cfg.get(i).Max > 0)  w = Math.min(w, cfg.get(i).Max);
				if (cfg.get(i).Min > 0)  w = Math.max(w, cfg.get(i).Min);
				if (cfg.get(i).Max == TCAConfig.PLACEHOLDER_AUTO)  w = Math.min(w, auto[i]);
				if (cfg.get(i).Min == TCAConfig.PLACEHOLDER_AUTO)  w = Math.max(w, auto[i]);
				//if (getNeedsSpacing(i)) w += spacing;
				if (cfg.get(i).OnlyExpand) w = Math.max(w, tcols[i].getWidth());
				w = Math.max(w, tcols[i].getMinWidth());
				columns[i] = w;
			}
		}

		// [3] Process [FIXED] configured columns
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			if (Float.isNaN(columns[i]) && cfg.get(i).Type == TCACType.Keep)
			{
				int w = cfg.get(i).Length;
				if (w<0) w=0;
				if (cfg.get(i).Max > 0)  w = Math.min(w, cfg.get(i).Max);
				if (cfg.get(i).Min > 0)  w = Math.max(w, cfg.get(i).Min);
				if (cfg.get(i).Max == TCAConfig.PLACEHOLDER_AUTO)  w = Math.min(w, auto[i]);
				if (cfg.get(i).Min == TCAConfig.PLACEHOLDER_AUTO)  w = Math.max(w, auto[i]);
				if (getNeedsSpacing(i)) w += spacing;
				if (cfg.get(i).OnlyExpand) w = Math.max(w, tcols[i].getWidth());
				w = Math.max(w, tcols[i].getMinWidth());
				columns[i] = w;
			}
		}

		// [4] Process [AUTO] configured columns
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			if (Float.isNaN(columns[i]) && cfg.get(i).Type == TCACType.Auto)
			{
				int w = auto[i];
				if (w<0) w=0;
				if (cfg.get(i).Max > 0)  w = Math.min(w, cfg.get(i).Max);
				if (cfg.get(i).Min > 0)  w = Math.max(w, cfg.get(i).Min);
				if (cfg.get(i).Max == TCAConfig.PLACEHOLDER_AUTO)  w = Math.min(w, auto[i]);
				if (cfg.get(i).Min == TCAConfig.PLACEHOLDER_AUTO)  w = Math.max(w, auto[i]);
				if (getNeedsSpacing(i)) w += spacing;
				if (cfg.get(i).OnlyExpand) w = Math.max(w, tcols[i].getWidth());
				w = Math.max(w, tcols[i].getMinWidth());
				columns[i] = w;
			}
		}

		// [5] Process [FILL] configured columns
		int remaining = totalWidth;
		int fillweight = 0;
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			if (Float.isNaN(columns[i]) && cfg.get(i).Type == TCACType.Fill) fillweight += cfg.get(i).Weight;
		}
		for (int i = 0; i < tcm.getColumnCount(); i++) if (!Float.isNaN(columns[i])) remaining -= columns[i];

		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			if (Float.isNaN(columns[i]) && cfg.get(i).Type == TCACType.Fill)
			{
				int w = (remaining * cfg.get(i).Weight) / fillweight;
				if (w<0) w=0;
				if (cfg.get(i).Max > 0)  w = Math.min(w, cfg.get(i).Max);
				if (cfg.get(i).Min > 0)  w = Math.max(w, cfg.get(i).Min);
				if (cfg.get(i).Max == TCAConfig.PLACEHOLDER_AUTO)  w = Math.min(w, auto[i]);
				if (cfg.get(i).Min == TCAConfig.PLACEHOLDER_AUTO)  w = Math.max(w, auto[i]);
				if (getNeedsSpacing(i)) w += spacing;
				if (cfg.get(i).OnlyExpand) w = Math.max(w, tcols[i].getWidth());
				w = Math.max(w, tcols[i].getMinWidth());
				columns[i] = w;
			}
		}

		// [6] Apply
		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			setColumnWidth(tcols[i], (int)columns[i]);
		}
	}

	private boolean getNeedsSpacing(int column) {
		TableCellRenderer cellRenderer = table.getCellRenderer(0, column);

		if (cellRenderer instanceof ResizableColumnRenderer) return ((ResizableColumnRenderer) cellRenderer).getNeedsExtraSpacing();

		return true;
	}

	private int getColumnHeaderWidth(int column) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		Object value = tableColumn.getHeaderValue();
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();

		if (renderer == null) {
			renderer = table.getTableHeader().getDefaultRenderer();
		}

		Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1, column);
		return c.getPreferredSize().width;
	}

	private int getColumnDataWidth(int column) {
		int preferredWidth = 0;
		int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();

		for (int row = 0; row < table.getRowCount(); row++) {
			preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));

			if (preferredWidth >= maxWidth) {
				break;
			}
		}

		return preferredWidth;
	}

	private int getCellDataWidth(int row, int column) {
		TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
		Component c = table.prepareRenderer(cellRenderer, row, column);
		return c.getPreferredSize().width + table.getIntercellSpacing().width;
	}

	private void setColumnWidth(final TableColumn tableColumn, final int width) {
		if (tableColumn.getWidth() == width) return;
		table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(width);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if ("model".equals(e.getPropertyName())) //$NON-NLS-1$
		{
			_autoCache = null;

			TableModel model = (TableModel) e.getOldValue();
			model.removeTableModelListener(this);

			model = (TableModel) e.getNewValue();
			model.addTableModelListener(this);
			_adjustColumns(table.getColumnModel());
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		_adjustColumns(table.getColumnModel());
	}

}
