package de.jClipCorn.gui.guiComponents;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class VerticalScrollPaneSynchronizer implements AdjustmentListener {
	private final JScrollBar sb1;
	private final JScrollBar sb2;
	
	public VerticalScrollPaneSynchronizer(JScrollPane pspane1, JScrollPane pspane2) {
		this.sb1 = pspane1.getVerticalScrollBar();
		this.sb2 = pspane2.getVerticalScrollBar();
	}
	
	public VerticalScrollPaneSynchronizer(JScrollBar bar1, JScrollBar bar2) {
		this.sb1 = bar1;
		this.sb2 = bar2;
	}
	
	public void start() {
		sb1.addAdjustmentListener(this);
		sb2.addAdjustmentListener(this);
	}
	
	public void stop() {
		sb1.removeAdjustmentListener(this);
		sb2.removeAdjustmentListener(this);
	}
	
	public JScrollBar getScrollBar1() {
		return sb1;
	}
	
	public JScrollBar getScrollBar2() {
		return sb2;
	}
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		JScrollBar source = (JScrollBar) e.getSource();
		
		if (source.equals(sb1)) {
			sb2.setValue(source.getValue());
		} else if (source.equals(sb2)) {
			sb1.setValue(source.getValue());
		}
	}

}
