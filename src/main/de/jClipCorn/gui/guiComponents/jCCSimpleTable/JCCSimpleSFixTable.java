package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.jClipCorn.gui.guiComponents.SFixTable;
import de.jClipCorn.properties.CCProperties;

public class JCCSimpleSFixTable<TData> extends SFixTable {
	private static final long serialVersionUID = 5729868556740947063L;
	
	private final List<JCCSimpleColumnPrototype<TData>> columns;
	
	public JCCSimpleSFixTable(TableModel dm, List<JCCSimpleColumnPrototype<TData>> _columns) {
		super(dm);
		
		columns = _columns;
		
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "null"); //$NON-NLS-1$
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "null"); //$NON-NLS-1$
		
		this.getTableHeader().setReorderingAllowed(false);
	}
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		column = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...

		return columns.get(column).CellRenderer;
	}
	
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return CCProperties.getInstance().PROP_MAINFRAME_SCROLLSPEED.getValue();
	}
	
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return super.getScrollableBlockIncrement(visibleRect, orientation, direction);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected String getTooltip(int column, int row, Object value) {
		column = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...
		
		TData el = (TData)value;
		
		return columns.get(column).GetTooltip.apply(el);
	}
}
