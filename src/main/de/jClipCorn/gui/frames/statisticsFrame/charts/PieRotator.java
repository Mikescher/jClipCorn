package de.jClipCorn.gui.frames.statisticsFrame.charts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.jfree.chart.plot.PiePlot3D;

public class PieRotator extends Timer implements ActionListener {
	private static final long serialVersionUID = 2800961876802883010L;

	private PiePlot3D plot;

    private int angle = 270;

    public PieRotator(final PiePlot3D plot) {
        super(200, null);
        this.plot = plot;
        addActionListener(this);
    }

    @Override
	public void actionPerformed(final ActionEvent event) {
        this.plot.setStartAngle(this.angle);
        this.angle = this.angle + 1;
        if (this.angle == 360) {
            this.angle = 0;
        }
    }
}