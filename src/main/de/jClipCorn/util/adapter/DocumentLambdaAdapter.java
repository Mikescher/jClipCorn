package de.jClipCorn.util.adapter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.jClipCorn.util.lambda.Func0to0;

public class DocumentLambdaAdapter implements DocumentListener {

	private final Func0to0 _function;
	
	public DocumentLambdaAdapter(Func0to0 fn) {
		_function = fn;
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		_function.invoke();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		_function.invoke();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		_function.invoke();
	}

}
