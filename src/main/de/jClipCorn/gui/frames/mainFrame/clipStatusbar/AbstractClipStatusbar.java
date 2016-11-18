package de.jClipCorn.gui.frames.mainFrame.clipStatusbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import de.jClipCorn.gui.guiComponents.JTooltipLabel;

public class AbstractClipStatusbar extends JPanel {
	private static final long serialVersionUID = -7508283304287773899L;
	
	private final static int SB_HEIGHT = 16;
	
	private List<ClipStatusbarColumn> columns = new ArrayList<>();

	public AbstractClipStatusbar() {
		super();
	}
	
	protected void startInitColumns() {
		columns.clear();
	}
	
	protected void endInitColumns() {
		GridBagLayout gbl = createGridbagLayout();
		
		setLayout(gbl);
		
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		setPreferredSize(new Dimension(-1, SB_HEIGHT));
		
		for (int i = 0; i < columns.size(); i++) {
			add(columns.get(i).getComponent(), columns.get(i).getConstraints(i));
		}
	}

	private GridBagLayout createGridbagLayout() {
		GridBagLayout gbl = new GridBagLayout();
		
		gbl.columnWidths = new int[columns.size() + 1];
		gbl.columnWeights = new double[columns.size() + 1];
		for (int i = 0; i < columns.size(); i++) {
			gbl.columnWidths[i] = columns.get(i).getWidth();
			gbl.columnWeights[i] = 0;
		}
		gbl.columnWidths[columns.size()] = 0;
		gbl.columnWeights[columns.size()] = Double.MIN_VALUE;
		
		
		gbl.rowHeights = new int[] {SB_HEIGHT};
		gbl.rowWeights = new double[]{0.0};
		
		return gbl;
	}
	
	protected void addColumn(Component c, GridBagConstraints gbc, int w) {
		columns.add(new ClipStatusbarColumn(c, gbc, w));
	}
	
	protected void addSeparator(boolean visible) {
		if (! visible) {
			return;
		}
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(1, 0, 1, 0);
		gbc.gridy = 0;
		
		addColumn(separator, gbc, 10);
	}

	protected void addPlaceholder(boolean visible) {
		if (! visible) {
			return;
		}
		
		JLabel lbl = new JLabel();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(1, 0, 1, 0);
		gbc.gridy = 0;
		
		addColumn(lbl, gbc, 5);
	}
	
	protected JLabel addLabel(String txt, boolean visible) {
		if (! visible) {
			return new JLabel(txt);
		}
		
		JLabel lbl = new JTooltipLabel(txt, -1, Integer.MAX_VALUE, -1);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridy = 0;
		
		addColumn(lbl, gbc, -1);
		
		return lbl;
	}
	
	protected JProgressBar addProgressbar(int width, boolean visible) {
		if (! visible) {
			return new JProgressBar();
		}
		
		JProgressBar progress = new JProgressBar();
		progress.setIndeterminate(true);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.gridy = 0;
		
		addColumn(progress, gbc, width);
		
		return progress;
	}
}
