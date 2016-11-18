package de.jClipCorn.gui.guiComponents;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.ToolTipManager;

public class JTooltipLabel extends JLabel implements MouseListener {

	private int initialDelay_default;
	private int dismissDelay_default;
	private int reshowDelay_default;

	private int initialDelay_this;
	private int dismissDelay_this;
	private int reshowDelay_this;
	
	public JTooltipLabel(String txt, int initial, int dismiss, int reshow) {
		super(txt);

		initialDelay_default = ToolTipManager.sharedInstance().getDismissDelay();
		dismissDelay_default = ToolTipManager.sharedInstance().getInitialDelay();
		reshowDelay_default = ToolTipManager.sharedInstance().getReshowDelay();
		
		initialDelay_this = initial;
		dismissDelay_this = dismiss;
		reshowDelay_this = reshow;
		
		addMouseListener(this);
	}

	private static final long serialVersionUID = -2467147783838805576L;

	@Override
	public void mouseClicked(MouseEvent e) {
		// NOP
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// NOP
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// NOP
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (initialDelay_this >= 0) ToolTipManager.sharedInstance().setInitialDelay(initialDelay_this);
		if (dismissDelay_this >= 0) ToolTipManager.sharedInstance().setDismissDelay(dismissDelay_this);
		if (reshowDelay_this >= 0) ToolTipManager.sharedInstance().setReshowDelay(reshowDelay_this);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (initialDelay_this >= 0) ToolTipManager.sharedInstance().setInitialDelay(initialDelay_default);
		if (dismissDelay_this >= 0) ToolTipManager.sharedInstance().setDismissDelay(dismissDelay_default);
		if (reshowDelay_this >= 0) ToolTipManager.sharedInstance().setReshowDelay(reshowDelay_default);
	}

}
