package de.jClipCorn.gui.guiComponents.jCheckBoxList;

import de.jClipCorn.gui.guiComponents.StringDisplayConverter;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.CBListModel.CBFilter;
import de.jClipCorn.util.datatypes.Tuple;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

public class JCheckBoxList<T> extends JList<JCheckBox> {
	private static final long serialVersionUID = -449508648731560934L;
	
	private DefaultListModel<JCheckBox> innermodel = new DefaultListModel<>();
	private CBListModel outermodel = new CBListModel(innermodel);
	
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

	public void addCheckBoxChangedActionListener(final CheckBoxChangedActionListener<T> l) {
		listenerList.add(CheckBoxChangedActionListener.class, l);
	}
	public void removeCheckBoxChangedActionListener(final CheckBoxChangedActionListener<T> l) {
		listenerList.remove(CheckBoxChangedActionListener.class, l);
	}

	private void init() {
		setCellRenderer(new CBCellRenderer());
		setModel(outermodel);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					JCheckBox checkbox = getModel().getElementAt(index);
					if (e.getX() < 16) {
						checkbox.setSelected(!checkbox.isSelected());
						repaint();
						raiseCheckBoxChangedEvent(checkbox, findValue(checkbox), index, checkbox.isSelected());
					}
				}
			}
		});

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	@SuppressWarnings("unchecked")
	private void raiseCheckBoxChangedEvent(JCheckBox cb, T data, int idx, boolean newVal) {
		var l = listenerList.getListeners(CheckBoxChangedActionListener.class);
		if (l.length == 0) return;

		var evt = new CheckBoxChangedActionListener.CheckBoxChangedEvent<>(this, newVal ? 101 : 102, String.valueOf(idx), data, cb, newVal);

		for (var lst : l) ((CheckBoxChangedActionListener<T>)lst).actionPerformed(evt);
	}

	public void clear() {
		map.clear();
		innermodel.clear();
		repaint();
	}	

	public void addAll(List<T> ls) {
		for (T elem : ls) {
			add(elem);
		}
		repaint();
	}
	
	public void add(T value) {
		add(value, false);
	}
	
	public void add(T value, boolean checked) {
		JCheckBox cb = new JCheckBox(converter.toDisplayString(value));
		cb.setSelected(checked);
		map.add(new Tuple<>(value, cb));
		innermodel.addElement(cb);
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
	
	public boolean setCheckedFast(T value, boolean checked) {
		for (Tuple<T, JCheckBox> tuple : map) {
			if (tuple.Item1 == value) {
				tuple.Item2.setSelected(checked);
				return true;
			}
		}
		
		return false;
	}
	
	public List<T> getAllElements() {
		List<T> result = new ArrayList<>();
		
		for (Enumeration<JCheckBox> e = innermodel.elements(); e.hasMoreElements();) {
			JCheckBox cb = e.nextElement();
			T value = findValue(cb);
			
			if (value != null) result.add(value);
		}

		return result;
	}
	
	public List<T> getCheckedElements() {
		List<T> result = new ArrayList<>();
		
		for (Enumeration<JCheckBox> e = innermodel.elements(); e.hasMoreElements();) {
			JCheckBox cb = e.nextElement();
			T value = findValue(cb);
			
			if (value != null && cb.isSelected()) result.add(value);
		}

		return result;
	}
	
	public List<T> getUncheckedElements() {
		List<T> result = new ArrayList<>();
		
		for (Enumeration<JCheckBox> e = innermodel.elements(); e.hasMoreElements();) {
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

	public void setFilter(CBFilter f) {
		outermodel.setFilter(f);
	}
}
