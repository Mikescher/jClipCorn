package de.jClipCorn.gui.guiComponents.enumComboBox;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.columnTypes.CCOptionalFSK;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

import javax.swing.*;
import java.text.Normalizer;

public class CCEnumComboBox<T extends ContinoousEnum<T>> extends JComboBox<T> {
	private static final long serialVersionUID = -3587427835677751149L;

	private final EnumWrapper<T> _wrapper;

	@DesignCreate
	@SuppressWarnings("rawtypes")
	private static CCEnumComboBox designCreate()
	{
		return new CCEnumComboBox<>(CCOptionalFSK.getWrapper());
	}

	public CCEnumComboBox(EnumWrapper<T> wrapper) {
		super();

		_wrapper = wrapper;

		setModel(wrapper.getComboBoxModel());
		setRenderer(wrapper.getComboBoxRenderer());
	}

	@SuppressWarnings("unchecked")
	public T getSelectedEnum() {
		return (T) getSelectedItem();
	}

	public void clearSelectedEnum() {
		setSelectedIndex(-1);
	}

	public boolean hasSelection() {
		return (getSelectedIndex() >= 0);
	}

	public void setSelectedEnum(T value) {
		setSelectedItem(value);
	}

	@Override
	@Deprecated
	public void setModel(ComboBoxModel<T> aModel) {
		super.setModel(aModel);
	}

	@Override
	@Deprecated
	public ComboBoxModel<T> getModel() {
		return super.getModel();
	}

	@Override
	@Deprecated
	public void setEditable(boolean aFlag) {
		super.setEditable(aFlag);
	}

	@Override
	@Deprecated
	public void setRenderer(ListCellRenderer<? super T> aRenderer) {
		super.setRenderer(aRenderer);
	}

	@Override
	@Deprecated
	public void setSelectedItem(Object anObject) {
		super.setSelectedItem(anObject);
	}

	@Override
	@Deprecated
	public Object getSelectedItem() {
		return super.getSelectedItem();
	}

	@Override
	@Deprecated
	public void setSelectedIndex(int anIndex) {
		super.setSelectedIndex(anIndex);
	}

	@Override
	@Deprecated
	public int getSelectedIndex() {
		return super.getSelectedIndex();
	}

	@Override
	@Deprecated
	public void addItem(T item) {
		super.addItem(item);
	}

	@Override
	@Deprecated
	public void insertItemAt(T item, int index) {
		super.insertItemAt(item, index);
	}

	@Override
	@Deprecated
	public void removeItem(Object anObject) {
		super.removeItem(anObject);
	}

	@Override
	@Deprecated
	public void removeItemAt(int anIndex) {
		super.removeItemAt(anIndex);
	}

	@Override
	@Deprecated
	public void removeAllItems() {
		super.removeAllItems();
	}

	@Override
	public boolean selectWithKeyChar(char keyChar)
	{
		// only works for single key presses ... but good enough for now

		for(int i = 0; i < getModel().getSize(); i++)
		{
			String normalize = Normalizer.normalize(_wrapper.asDisplayString(getModel().getElementAt(i)), Normalizer.Form.NFD);
			normalize = normalize.replaceAll("\\p{M}", ""); //$NON-NLS-1$ //$NON-NLS-2$
			normalize = normalize.toLowerCase();

			if (normalize.startsWith(String.valueOf(keyChar).toLowerCase())) { setSelectedIndex(i); return true; }
		}

		return false;
	}
}
