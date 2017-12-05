package de.jClipCorn.table.filter.filterConfig;

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
}
