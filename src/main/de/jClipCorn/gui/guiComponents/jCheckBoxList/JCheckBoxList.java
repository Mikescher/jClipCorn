package de.jClipCorn.gui.guiComponents.jCheckBoxList;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.gui.guiComponents.StringDisplayConverter;
import de.jClipCorn.util.Tuple;

public class JCheckBoxList<T> extends JList<JCheckBox> {
	private static final long serialVersionUID = -449508648731560934L;

	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	
	private DefaultListModel<JCheckBox> model = new DefaultListModel<>();
	
	private final StringDisplayConverter<T> converter;
	private final List<Tuple<T, JCheckBox>> map = new ArrayList<>();
	
	public JCheckBoxList(StringDisplayConverter<T> conv, List<T> values) {
		super();
		
		converter = conv;
		
		init();
		
		for (T v : values) {
			add(v);
		}
	}
	
	public JCheckBoxList(StringDisplayConverter<T> conv) {
		super();
		
		converter = conv;
		
		init();
	}

	private void init() {
		setCellRenderer(new CBCellRenderer());
		setModel(model);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					JCheckBox checkbox = getModel().getElementAt(index);
					checkbox.setSelected(!checkbox.isSelected());
					repaint();
				}
			}
		});

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void add(T value) {
		add(value, false);
	}
	
	public void add(T value, boolean checked) {
		JCheckBox cb = new JCheckBox(converter.toDisplayString(value));
		cb.setSelected(checked);
		map.add(new Tuple<>(value, cb));
		model.addElement(cb);
	}
	
	public boolean getChecked(T value) {
		for (Tuple<T, JCheckBox> tuple : map) {
			if (tuple.Item1 == value) return tuple.Item2.isSelected();
		}
		
		return false;
	}
	
	private T findValue(JCheckBox cb) {
		for (Tuple<T, JCheckBox> tuple : map) {
			if (tuple.Item2 == cb) return tuple.Item1;
		}
		
		return null;
	}
	
	public boolean setChecked(T value, boolean checked) {
		for (Tuple<T, JCheckBox> tuple : map) {
			if (tuple.Item1 == value) {
				tuple.Item2.setSelected(checked);
				repaint();
				return true;
			}
		}
		
		return false;
	}
	
	public List<T> getAllElements() {
		List<T> result = new ArrayList<>();
		
		for (Enumeration<JCheckBox> e = model.elements(); e.hasMoreElements();) {
			JCheckBox cb = e.nextElement();
			T value = findValue(cb);
			
			if (value != null) result.add(value);
		}

		return result;
	}
	
	public List<T> getCheckedElements() {
		List<T> result = new ArrayList<>();
		
		for (Enumeration<JCheckBox> e = model.elements(); e.hasMoreElements();) {
			JCheckBox cb = e.nextElement();
			T value = findValue(cb);
			
			if (value != null && cb.isSelected()) result.add(value);
		}

		return result;
	}
	
	public List<T> getUncheckedElements() {
		List<T> result = new ArrayList<>();
		
		for (Enumeration<JCheckBox> e = model.elements(); e.hasMoreElements();) {
			JCheckBox cb = e.nextElement();
			T value = findValue(cb);
			
			if (value != null && !cb.isSelected()) result.add(value);
		}

		return result;
	}

	public void setCheckedValues(Set<T> set) {
		for (Tuple<T, JCheckBox> tuple : map) {
			tuple.Item2.setSelected(set.contains(tuple.Item1));
		}

		repaint();
	}

	protected class CBCellRenderer implements ListCellRenderer<JCheckBox> {
		@Override
		public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = value;
			checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
			checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
			checkbox.setEnabled(isEnabled());
			checkbox.setFont(getFont());
			checkbox.setFocusPainted(false);
			checkbox.setBorderPainted(true);
			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$
			return checkbox;
		}
	}
}
