package de.jClipCorn.features.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

public class CustomFilterEnumChooserConfig<T extends ContinoousEnum<T>> extends CustomFilterConfig {

	private final Func0to1<T> valueGetter;
	private final Func1to0<T> valueSetter;
	private final EnumWrapper<T> enumWrapper;
	
	public CustomFilterEnumChooserConfig(Func0to1<T> get, Func1to0<T> set, EnumWrapper<T> wrap) {
		valueGetter = get;
		valueSetter = set;
		enumWrapper = wrap;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {
		JComboBox<String> cbx = new JComboBox<>();
		cbx.setModel(new DefaultComboBoxModel<>(enumWrapper.getList()));
		cbx.setSelectedIndex(valueGetter.invoke().asInt());
		
		cbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueSetter.invoke(enumWrapper.findOrNull(cbx.getSelectedIndex()));
				onChange.run();
			}
		});
		
		return cbx;
	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(enumWrapper.randomValue(r));
	}
}
