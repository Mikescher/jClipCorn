package de.jClipCorn.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

public class CustomFilterBoolConfig extends CustomFilterConfig {

	private final Supplier<Boolean> valueGetter;
	private final Consumer<Boolean> valueSetter;
	
	private final String description;
	
	public CustomFilterBoolConfig(Supplier<Boolean> get, Consumer<Boolean> set, String desc) {
		valueGetter = get;
		valueSetter = set;
		description = desc;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		JCheckBox cb = new JCheckBox(description);
		cb.setSelected(valueGetter.get());
		
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueSetter.accept(cb.isSelected());
				onChange.run();
			}
		});
		
		return cb;
	}
}
