package de.jClipCorn.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.JComponent;

import de.jClipCorn.gui.guiComponents.JTextFieldLimit;

public class CustomFilterCharConfig extends CustomFilterConfig {

	private final Supplier<String> valueGetter;
	private final Consumer<String> valueSetter;
	
	public CustomFilterCharConfig(Supplier<String> get, Consumer<String> set) {
		valueGetter = get;
		valueSetter = set;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		JTextFieldLimit tf = new JTextFieldLimit();
		tf.setMaxLength(1);
		tf.setText(valueGetter.get());
		
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String txt = tf.getText();
				if (txt.length() == 0) return;

				valueSetter.accept(txt.substring(0, 1));
				onChange.run();
			}
		});
		
		return tf;
	}
}
