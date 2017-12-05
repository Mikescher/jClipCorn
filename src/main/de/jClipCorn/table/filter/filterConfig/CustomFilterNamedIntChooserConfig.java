package de.jClipCorn.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

public class CustomFilterNamedIntChooserConfig extends CustomFilterConfig {

	private final Func0to1<Integer> valueGetter;
	private final Func1to0<Integer> valueSetter;

	private final List<String> names;
	
	public CustomFilterNamedIntChooserConfig(Func0to1<Integer> get, Func1to0<Integer> set, List<String> v) {
		valueGetter = get;
		valueSetter = set;
		names = v;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		int initial = valueGetter.invoke();
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
		for (String str : names) model.addElement(str);
		
		JComboBox<String> cbx = new JComboBox<>(model);
		
		cbx.setSelectedIndex(initial);

		cbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueSetter.invoke(cbx.getSelectedIndex());
				onChange.run();
			}
		});
		
		return cbx;
	}
}
