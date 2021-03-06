package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.JTextFieldLimit;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class CustomFilterCharConfig extends CustomFilterConfig {

	private final Func0to1<String> valueGetter;
	private final Func1to0<String> valueSetter;
	
	public CustomFilterCharConfig(CCMovieList ml, Func0to1<String> get, Func1to0<String> set) {
		super(ml);

		valueGetter = get;
		valueSetter = set;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		JTextFieldLimit tf = new JTextFieldLimit();
		tf.setMaxLength(1);
		tf.setText(valueGetter.invoke());
		
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String txt = tf.getText();
				if (txt.length() == 0) return;

				valueSetter.invoke(txt.substring(0, 1));
				onChange.run();
			}
		});
		
		return tf;
	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(String.valueOf((char)r.nextInt()));
	}
}
