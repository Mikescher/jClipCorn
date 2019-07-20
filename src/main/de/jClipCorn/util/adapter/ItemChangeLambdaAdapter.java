package de.jClipCorn.util.adapter;

import de.jClipCorn.util.lambda.Func0to0;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ItemChangeLambdaAdapter implements ItemListener {

	private final Func0to0 _function;
	private final int _type;

	public ItemChangeLambdaAdapter(Func0to0 fn, int type) {
		_function = fn;
		_type = type;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (_type == -1)
			_function.invoke();
		else if (e.getStateChange() == _type)
		_function.invoke();
	}
}
