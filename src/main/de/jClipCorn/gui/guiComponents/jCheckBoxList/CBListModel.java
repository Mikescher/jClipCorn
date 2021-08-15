package de.jClipCorn.gui.guiComponents.jCheckBoxList;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class CBListModel extends AbstractListModel<JCheckBox> {
	private static final long serialVersionUID = 4465608143562507521L;

	public interface CBFilter {
        boolean accept(JCheckBox element);
    }

    private final ListModel<JCheckBox> _source;
    private CBFilter _filter;
    private final ArrayList<Integer> _indices = new ArrayList<>();

    @SuppressWarnings("nls")
	public CBListModel(ListModel<JCheckBox> source) {
        if (source == null)
            throw new IllegalArgumentException("Source is null");
        
        _source = source;
        _source.addListDataListener(new ListDataListener() {
            @Override
			public void intervalRemoved(ListDataEvent e) {
                doFilter();
            }

            @Override
			public void intervalAdded(ListDataEvent e) {
                doFilter();
            }

            @Override
			public void contentsChanged(ListDataEvent e) {
                doFilter();
            }
        });
    }

    public void setFilter(CBFilter f) {
        _filter = f;
        doFilter();
    }

    private void doFilter() {
        _indices.clear();

        CBFilter f = _filter;
        if (f != null) {
            int count = _source.getSize();
            for (int i = 0; i < count; i++) {
            	JCheckBox element = _source.getElementAt(i);
                if (f.accept(element)) {
                    _indices.add(i);
                }
            }
            fireContentsChanged(this, 0, getSize() - 1);
        }
    }

    @Override
	public int getSize() {
        return (_filter != null) ? _indices.size() : _source.getSize();
    }

    @Override
	public JCheckBox getElementAt(int index) {
        return (_filter != null) ? _source.getElementAt(_indices.get(index)) : _source.getElementAt(index);
    }
}
