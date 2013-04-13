package de.jClipCorn.gui.guiComponents;

import javax.swing.KeyStroke;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import de.jClipCorn.util.KeyStrokeUtil;

public class KeyStrokeDocument extends DefaultStyledDocument {
	private static final long serialVersionUID = 2902824638430147997L;
	
	private boolean isEditing = false;
	
	private KeyStroke currentStroke = KeyStrokeUtil.getEmptyKeyStroke();
	
	public KeyStrokeDocument() {
		super();
	}
	
	@Override
    public void remove(int offs, int len) throws BadLocationException {
		if (isEditing) {
			super.remove(offs, len);
		}
    }
	
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if (isEditing) {
			super.insertString(offs, str, a);
		}
	}
	
	public void beginEditing() {
		isEditing = true;
	}
	
	public void endEditing() {
		isEditing = false;
	}

	public void setKeyStroke(KeyStroke stroke, KeyStrokeTextfield tf) {
		currentStroke = stroke;
		
		beginEditing();
		tf.setText(KeyStrokeUtil.keyStrokeToString(currentStroke));
		endEditing();
	}

	public KeyStroke getKeyStroke() {
		return currentStroke;
	}
}
