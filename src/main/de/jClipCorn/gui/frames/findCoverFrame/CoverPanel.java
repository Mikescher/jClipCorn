package de.jClipCorn.gui.frames.findCoverFrame;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import de.jClipCorn.gui.guiComponents.WrapFlowLayout;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public class CoverPanel extends JPanel implements UpdateCallbackListener {
	private static final long serialVersionUID = -1988259985804809637L;
	
	private final JScrollPane scrollpane;
	
	private List<BufferedImage> images = new ArrayList<>();
	private List<JLabel> labels = new ArrayList<>();
	private int selctionID = -1;
	
	public CoverPanel(JScrollPane pane) {
		this.scrollpane = pane;
		setLayout(new WrapFlowLayout(WrapFlowLayout.LEADING, 5, 5));
	}
	
	public void addCover(BufferedImage i) {
		JLabel newlabel = new JLabel();
		newlabel.setIcon(new ImageIcon(ImageUtilities.resizeCoverImageForHalfSizeUI(i)));
		add(newlabel);
		
		final int id = labels.size();
		
		newlabel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// NOTHING
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// NOTHING
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// NOTHING
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// NOTHING
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				onLabelClicked(id);
			}
		});
		
		images.add(i);
		labels.add(newlabel);
		
		validate();
		scrollpane.revalidate();
	}
	
	private void onLabelClicked(int id) {
		if (selctionID >= 0) {
			labels.get(selctionID).setIcon(new ImageIcon(ImageUtilities.resizeCoverImageForHalfSizeUI(images.get(selctionID))));
		}
		
		selctionID = id;
		
		BufferedImage newImg = ImageUtilities.resizeCoverImageForHalfSizeUI(images.get(selctionID));
		ImageUtilities.drawActualBorder(newImg, Color.BLUE, 2);
		labels.get(selctionID).setIcon(new ImageIcon(newImg));
	}

	@Override
	public void onUpdate(final Object o) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				addCover((BufferedImage) o);
			}
		});
	}

	public void reset() {
		images.clear();
		labels.clear();
		removeAll();
		selctionID = -1;
	}
	
	public BufferedImage getSelectedCover() {
		if (selctionID >= 0) {
			return images.get(selctionID);
		} else {
			return null;
		}
	}
}
