package de.jClipCorn.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class CustomFilterNamedIntChooserConfig extends CustomFilterConfig {

	private final Supplier<Integer> valueGetter;
	private final Consumer<Integer> valueSetter;

	private final List<String> names;
	
	public CustomFilterNamedIntChooserConfig(Supplier<Integer> get, Consumer<Integer> set, List<String> v) {
		valueGetter = get;
		valueSetter = set;
		names = v;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		int initial = valueGetter.get();
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		for (String str : names) model.addElement(str);
		
		JComboBox<String> cbx = new JComboBox<>(model);
		
		cbx.setSelectedIndex(initial);

		cbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueSetter.accept(cbx.getSelectedIndex());
				onChange.run();
			}
		});
		
		return cbx;
	}
}
