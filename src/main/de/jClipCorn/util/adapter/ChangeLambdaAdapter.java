package de.jClipCorn.util.adapter;

import de.jClipCorn.util.lambda.Func0to0;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ChangeLambdaAdapter implements ChangeListener {

	private final Func0to0 _function;

	public ChangeLambdaAdapter(Func0to0 fn) {
		_function = fn;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		_function.invoke();
	}
}
