package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

public class CustomFilterNamedIntChooserConfig extends CustomFilterConfig {

	private final Func0to1<Integer> valueGetter;
	private final Func1to0<Integer> valueSetter;

	private final List<String> names;
	
	public CustomFilterNamedIntChooserConfig(CCMovieList ml, Func0to1<Integer> get, Func1to0<Integer> set, List<String> v) {
		super(ml);

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

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(r.nextInt(names.size()));
	}
}
