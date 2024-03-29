package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Random;

public class CustomFilterStringConfig extends CustomFilterConfig {

	private final Func0to1<String> valueGetter;
	private final Func1to0<String> valueSetter;
	
	public CustomFilterStringConfig(CCMovieList ml, Func0to1<String> get, Func1to0<String> set) {
		super(ml);

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
			b.append((char) (Math.abs(r.nextInt()) + 1));
		}
		valueSetter.invoke(b.toString());
	}
}
