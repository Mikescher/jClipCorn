package de.jClipCorn.gui.frames.statisticsFrame;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesCheckBoxList extends JList<SeriesCheckBoxList.SeriesCheckBoxListElement>{
	private static final long serialVersionUID = 2770307068259908722L;
	
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	private final List<ActionListener> actionListener = new ArrayList<>();
	
	public SeriesCheckBoxList() {
		super();
		
		setCellRenderer(new CellRenderer());
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (index != -1) {
					JCheckBox checkbox = getModel().getElementAt(index).checkbox;
					checkbox.setSelected(!checkbox.isSelected());
					
					if (getModel().getElementAt(index).series == null) {
						boolean selected = checkbox.isSelected();
						
						for (int i = 0; i < getModel().getSize(); i++) {
							if (getModel().getElementAt(i).series != null) 
								getModel().getElementAt(i).checkbox.setSelected(selected);
						}
					} else {
						boolean allSelected = true;
						
						for (int i = 0; i < getModel().getSize(); i++) {
							if (getModel().getElementAt(i).series != null && !getModel().getElementAt(i).checkbox.isSelected()) 
								allSelected = false;
						}
	
						for (int i = 0; i < getModel().getSize(); i++) {
							if (getModel().getElementAt(i).series == null) 
								getModel().getElementAt(i).checkbox.setSelected(allSelected);
						}
					}
					
					for (ActionListener listener : actionListener) {
						listener.actionPerformed(new ActionEvent(getModel().getElementAt(index), index, null));
					}
					
					repaint();
				}
			}
		});
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public SeriesCheckBoxList(ListModel<SeriesCheckBoxList.SeriesCheckBoxListElement> model) {
		this();
		
		setModel(model);
	}

	protected class CellRenderer implements ListCellRenderer<SeriesCheckBoxListElement> {
		@Override
		public Component getListCellRendererComponent(JList<? extends SeriesCheckBoxListElement> list, SeriesCheckBoxListElement value, int index, boolean isSelected, boolean cellHasFocus) {
			JCheckBox checkbox = value.checkbox;

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
	
	public static class SeriesCheckBoxListElement {
		public CCSeries series;
		public JCheckBox checkbox;
		
		public SeriesCheckBoxListElement(CCSeries s) {
			this.series = s;
			if (series == null) {
				this.checkbox = new JCheckBox(LocaleBundle.getString("StatisticsFrame.this.allSeries"));	 //$NON-NLS-1$
			} else {
				this.checkbox = new JCheckBox(s.toString());				
			}
			this.checkbox.setSelected(true);
		}

		public SeriesCheckBoxListElement() {
			this(null);
		}
	}

	public void addActionListener(ActionListener al) {
		actionListener.add(al);
	}
	public void removeActionListener(ActionListener al) {
		actionListener.remove(al);
	}

	public Map<CCSeries, Boolean> getMap() {
		Map<CCSeries, Boolean> result = new HashMap<>();
		
		for (int i = 0; i < getModel().getSize(); i++) {
			SeriesCheckBoxListElement element = getModel().getElementAt(i);
			if (element != null) {
				result.put(element.series, element.checkbox.isSelected());
			}
		}
		
		return result;
	}
}
