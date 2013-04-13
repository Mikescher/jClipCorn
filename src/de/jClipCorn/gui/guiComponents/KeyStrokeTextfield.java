package de.jClipCorn.gui.guiComponents;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.KeyStroke;

import de.jClipCorn.util.KeyStrokeUtil;

public class KeyStrokeTextfield extends JTextField implements KeyListener {
	private static final long serialVersionUID = -8054153238795304087L;

	private KeyStrokeDocument document;
	
	public KeyStrokeTextfield() {
		super(new KeyStrokeDocument(), "", 0); //$NON-NLS-1$
		document = (KeyStrokeDocument) getDocument();
		addKeyListener(this);
	}

	public void setKeyStroke(KeyStroke stroke) {
		document.setKeyStroke(stroke, this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// NOTHING
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			setKeyStroke(KeyStrokeUtil.getEmptyKeyStroke());
		} else {
			int k = e.getKeyCode();
			if (! (k == KeyEvent.VK_SHIFT || k == KeyEvent.VK_CONTROL || k == KeyEvent.VK_ALT || k == KeyEvent.VK_META)) {
				KeyStroke s = KeyStroke.getKeyStrokeForEvent(e);
				KeyStroke s2 = KeyStroke.getKeyStroke(s.getKeyCode(), s.getModifiers());
				setKeyStroke(s2);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		//NOTHING
	}

	public KeyStroke getKeyStroke() {
		return document.getKeyStroke();
	}
}
