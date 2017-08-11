package de.jClipCorn.gui.guiComponents;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class JTextFieldLimit extends JTextField {
	private static final long serialVersionUID = -7729248331983545397L;

	private class JTextFieldLimitDoc extends PlainDocument {
		private static final long serialVersionUID = 8123868080590292667L;
		
		private int limit;

		JTextFieldLimitDoc(int limit) {
			super();
			this.limit = limit;
		}

		@Override
		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null)
				return;

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}
	}

	private JTextFieldLimitDoc doc;
	
	public JTextFieldLimit() {
		super();
		
		setDocument(doc = new JTextFieldLimitDoc(128));
	}
	
	public void setMaxLength(int ml) {
		doc.limit = ml;
	}
	
	public int getMaxLength() {
		return doc.limit;
	}
}
