package de.jClipCorn.features.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

public class CustomFilterBoolConfig extends CustomFilterConfig {

	private final Func0to1<Boolean> valueGetter;
	private final Func1to0<Boolean> valueSetter;
	
	private final String description;
	
	public CustomFilterBoolConfig(Func0to1<Boolean> get, Func1to0<Boolean> set, String desc) {
		valueGetter = get;
		valueSetter = set;
		description = desc;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		JCheckBox cb = new JCheckBox(description);
		cb.setSelected(valueGetter.invoke());
		
		cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valueSetter.invoke(cb.isSelected());
				onChange.run();
			}
		});
		
		return cb;
	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(r.nextBoolean());
	}
}
