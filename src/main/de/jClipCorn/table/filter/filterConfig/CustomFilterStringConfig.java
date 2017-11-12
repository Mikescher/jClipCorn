package de.jClipCorn.table.filter.filterConfig;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CustomFilterStringConfig extends CustomFilterConfig {

	private final Supplier<String> valueGetter;
	private final Consumer<String> valueSetter;
	
	public CustomFilterStringConfig(Supplier<String> get, Consumer<String> set) {
		valueGetter = get;
		valueSetter = set;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		JTextField tf = new JTextField();
		tf.setText(valueGetter.get());
		
		tf.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				valueSetter.accept(tf.getText());
				onChange.run();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				valueSetter.accept(tf.getText());
				onChange.run();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				valueSetter.accept(tf.getText());
				onChange.run();
			}
		});
		
		return tf;
	}
}
