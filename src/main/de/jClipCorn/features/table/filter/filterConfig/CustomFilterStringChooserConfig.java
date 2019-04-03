package de.jClipCorn.features.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

public class CustomFilterStringChooserConfig extends CustomFilterConfig {

	private final Func0to1<String> valueGetter;
	private final Func1to0<String> valueSetter;

	private final List<String> values;
	private final boolean allowNotInList;
	private final boolean allowEmpty;
	
	public CustomFilterStringChooserConfig(Func0to1<String> get, Func1to0<String> set, List<String> v, boolean nil, boolean e) {
		valueGetter = get;
		valueSetter = set;
		values = v;
		allowNotInList = nil;
		allowEmpty = e;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		String initial = valueGetter.invoke();
		
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
				valueSetter.invoke((String)cbx.getSelectedItem());
				onChange.run();
			}
		});
		
		return cbx;
	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(values.size()==0 ? Str.Empty : values.get(r.nextInt(values.size())));
	}
}
