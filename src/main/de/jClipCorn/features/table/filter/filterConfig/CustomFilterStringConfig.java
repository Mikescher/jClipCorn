package de.jClipCorn.features.table.filter.filterConfig;

import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

public class CustomFilterStringConfig extends CustomFilterConfig {

	private final Func0to1<String> valueGetter;
	private final Func1to0<String> valueSetter;
	
	public CustomFilterStringConfig(Func0to1<String> get, Func1to0<String> set) {
		valueGetter = get;
		valueSetter = set;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		JTextField tf = new JTextField();
		tf.setText(valueGetter.invoke());
		
		tf.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				valueSetter.invoke(tf.getText());
				onChange.run();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				valueSetter.invoke(tf.getText());
				onChange.run();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				valueSetter.invoke(tf.getText());
				onChange.run();
			}
		});
		
		return tf;
	}

	@Override
	public void setValueRandom(Random r) {
		StringBuilder b = new StringBuilder();
		int len = r.nextInt(128);
		for (int i = 0; i < len; i++) {
			b.append(String.valueOf((char)(Math.abs(r.nextInt())+1)));
		}
		valueSetter.invoke(b.toString());
	}
}
