package de.jClipCorn.gui.frames.statisticsFrame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.jClipCorn.gui.localization.LocaleBundle;

public class StatisticsSidebarFactory {
	private List<JLabel> labels_left = new ArrayList<>();
	private List<JLabel> labels_right = new ArrayList<>();
	private List<GridBagConstraints> constraints_left = new ArrayList<>();
	private List<GridBagConstraints> constraints_right = new ArrayList<>();
	private List<Integer> rowHeights = new ArrayList<>();
	private List<Double> rowWeights = new ArrayList<>();
	
	public JLabel addRow(String description, String value) {
		JLabel lblLeft = new JLabel(LocaleBundle.getString(description) + ": "); //$NON-NLS-1$
		JLabel lblRight = new JLabel(value);
		
		GridBagConstraints gbc_l = new GridBagConstraints();
		gbc_l.fill = GridBagConstraints.BOTH;
		gbc_l.insets = new Insets(0, 0, 5, 5);
		gbc_l.gridx = 0;
		gbc_l.gridy = rowHeights.size();
		
		GridBagConstraints gbc_r = new GridBagConstraints();
		gbc_r.insets = new Insets(0, 0, 5, 0);
		gbc_r.anchor = GridBagConstraints.WEST;
		gbc_r.fill = GridBagConstraints.VERTICAL;
		gbc_r.gridx = 1;
		gbc_r.gridy = rowHeights.size();
		
		labels_left.add(lblLeft);
		labels_right.add(lblRight);
		
		constraints_left.add(gbc_l);
		constraints_right.add(gbc_r);
		
		rowHeights.add(0);
		rowWeights.add(0.0);
		
		return lblRight;
	}
	
	private GridBagLayout getGBLayout() {
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[]{1, 18, 0};
		gbl.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		
		gbl.rowHeights = new int[rowHeights.size() + 1];
		gbl.rowWeights = new double[rowWeights.size() + 1];
		
		for (int i = 0; i < rowHeights.size(); i++) {
			gbl.rowHeights[i] = rowHeights.get(i);
			gbl.rowWeights[i] = rowWeights.get(i);
		}
		gbl.rowHeights[rowHeights.size()] = 0;
		gbl.rowWeights[rowWeights.size()] = Double.MIN_VALUE;
		
		return gbl;
	}
	
	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(getGBLayout());
		
		for (int i = 0; i < labels_left.size(); i++) {
			panel.add(labels_left.get(i), constraints_left.get(i));
			panel.add(labels_right.get(i), constraints_right.get(i));
		}
		
		return panel;
	}
}