package de.jClipCorn.gui.frames.allRatingsFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;

public class AllRatingsDialog extends JDialog {
	private static final long serialVersionUID = 568186116244028190L;
	
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JPanel pnlBottom;
	private JLabel lblBottom;
	private JLabel lblAverage;

	/**
	 * Create the frame.
	 */
	public AllRatingsDialog(Map<String, Integer> list, Component owner) {
		initGUI(owner);
		createContent(list);
	}
	
	private void initGUI(Component owner) {
		setResizable(false);
		setModal(true);
		setTitle(LocaleBundle.getString("AllRatingsFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(owner);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(null);
		
		pnlBottom = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlBottom.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(pnlBottom, BorderLayout.SOUTH);
		
		lblBottom = new JLabel(LocaleBundle.getString("AllRatingsFrame.lblAberage.text")); //$NON-NLS-1$
		pnlBottom.add(lblBottom);
		
		lblAverage = new JLabel("???"); //$NON-NLS-1$
		pnlBottom.add(lblAverage);
	}
	
	private void createContent(Map<String, Integer> list) {
		JProgressBar progressBar;
		JLabel lblLand;
		JLabel lblRating;
		
		
		
		int y = 0;
		int count = 0;
		int sum = 0;
		for (Iterator<Entry<String, Integer>> it = list.entrySet().iterator(); it.hasNext();) {
			Entry<String, Integer> element = it.next();
			int cy = 11 + y++ * 25;
			
			progressBar = new JProgressBar();
			progressBar.setBounds(156, cy, 256, 14);
			progressBar.setMaximum(21);
			progressBar.setValue(element.getValue());
			panel.add(progressBar);
			
			lblLand = new JLabel(element.getKey());
			lblLand.setBounds(10, cy, 80, 14);
			panel.add(lblLand);
			
			lblRating = new JLabel(element.getValue() + ""); //$NON-NLS-1$
			lblRating.setBounds(100, cy, 46, 14);
			panel.add(lblRating);
			
			count++;
			sum += element.getValue();
		}
		panel.setPreferredSize(new Dimension(0,11 + y * 25));
		
		if (count > 0) {
			lblAverage.setText(((int)((sum / (count * 1d)) * 10)) / 10d + ""); //$NON-NLS-1$
		} else {
			lblAverage.setText("  -  "); //$NON-NLS-1$
		}
		
	}
}