package de.jClipCorn.util.adapter;

import de.jClipCorn.util.lambda.Func0to0;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionLambdaAdapter implements ActionListener {

	private final Func0to0 _function;

	public ActionLambdaAdapter(Func0to0 fn) {
		_function = fn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		_function.invoke();
	}
}
