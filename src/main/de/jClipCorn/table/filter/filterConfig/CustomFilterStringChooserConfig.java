package de.jClipCorn.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class CustomFilterStringChooserConfig extends CustomFilterConfig {

	private final Supplier<String> valueGetter;
	private final Consumer<String> valueSetter;

	private final List<String> values;
	private final boolean allowNotInList;
	private final boolean allowEmpty;
	
	public CustomFilterStringChooserConfig(Supplier<String> get, Consumer<String> set, List<String> v, boolean nil, boolean e) {
		valueGetter = get;
		valueSetter = set;
		values = v;
		allowNotInList = nil;
		allowEmpty = e;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		String initial = valueGetter.get();
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

		boolean contained = false;
		
		if (allowEmpty) {
			model.addElement(""); //$NON-NLS-1$
			
			if (initial.equals("")) contained = true; //$NON-NLS-1$
		}
		
		for (String str : values) {
			model.addElement(str);
			if (str.equals(initial)) contained = true;
		}
		if (!contained && allowNotInList) {
			model.insertElementAt(initial, allowEmpty ? 1 : 0);
			contained = true;
		}
		
		JComboBox<String> cbx = new JComboBox<>(model);
		
		if (contained) cbx.setSelectedItem(initial);

		cbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueSetter.accept((String)cbx.getSelectedItem());
				onChange.run();
			}
		});
		
		return cbx;
	}
}
