package de.jClipCorn.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public class CustomFilterEnumChooserConfig<T extends ContinoousEnum<T>> extends CustomFilterConfig {

	private final Supplier<T> valueGetter;
	private final Consumer<T> valueSetter;
	private final EnumWrapper<T> enumWrapper;
	
	public CustomFilterEnumChooserConfig(Supplier<T> get, Consumer<T> set, EnumWrapper<T> wrap) {
		valueGetter = get;
		valueSetter = set;
		enumWrapper = wrap;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {
		JComboBox<String> cbx = new JComboBox<>();
		cbx.setModel(new DefaultComboBoxModel<>(enumWrapper.getList()));
		cbx.setSelectedIndex(valueGetter.get().asInt());
		
		cbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueSetter.accept(enumWrapper.find(cbx.getSelectedIndex()));
				onChange.run();
			}
		});
		
		return cbx;
	}
}
