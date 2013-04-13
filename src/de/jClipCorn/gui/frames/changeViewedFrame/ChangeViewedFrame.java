package de.jClipCorn.gui.frames.changeViewedFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;

import javax.swing.JCheckBox;

public class ChangeViewedFrame extends JFrame {
	private static final long serialVersionUID = 9048482551231383355L;
	
	private final CCMovieList movielist;
	private int position;
	private boolean running = false;
	
	private CoverLabel lblCover;
	private JButton btnViewed;
	private JButton btnUnviewed;
	private JLabel lblCurrent;
	private JLabel lblKeyViewed;
	private JLabel lblKeyUnviewed;
	private JLabel lblTitle;
	private JCheckBox cbOnlyUnviewed;

	public ChangeViewedFrame(Component owner, CCMovieList list) {
		super();
		this.movielist = list;
		
		initGUI();
		
		initMap();
		setFocusable(true);
		
		setLocationRelativeTo(owner);
		init();
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("ChangedViewedFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setSize(new Dimension(382, 445));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		lblCover = new CoverLabel(false);
		lblCover.setIcon(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_STANDARD));
		lblCover.setBounds(100, 47, 182, 254);
		getContentPane().add(lblCover);
		
		btnViewed = new JButton(LocaleBundle.getString("FilterTree.Viewed.Viewed")); //$NON-NLS-1$
		btnViewed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(true);
			}
		});
		btnViewed.setBounds(10, 339, 89, 23);
		getContentPane().add(btnViewed);
		
		btnUnviewed = new JButton(LocaleBundle.getString("FilterTree.Viewed.Unviewed")); //$NON-NLS-1$
		btnUnviewed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(false);
			}
		});
		btnUnviewed.setBounds(277, 339, 89, 23);
		getContentPane().add(btnUnviewed);
		
		lblCurrent = new JLabel();
		lblCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrent.setBounds(100, 300, 182, 28);
		getContentPane().add(lblCurrent);
		
		lblKeyViewed = new JLabel("(1)"); //$NON-NLS-1$
		lblKeyViewed.setHorizontalAlignment(SwingConstants.CENTER);
		lblKeyViewed.setBounds(10, 367, 89, 14);
		getContentPane().add(lblKeyViewed);
		
		lblKeyUnviewed = new JLabel("(2)"); //$NON-NLS-1$
		lblKeyUnviewed.setHorizontalAlignment(SwingConstants.CENTER);
		lblKeyUnviewed.setBounds(277, 367, 89, 14);
		getContentPane().add(lblKeyUnviewed);
		
		lblTitle = new JLabel();
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 11, 356, 14);
		getContentPane().add(lblTitle);
		
		cbOnlyUnviewed = new JCheckBox(CCProperties.getInstance().PROP_MASSCHANGEVIEWED_ONLYUNVIEWED.getDescription());
		cbOnlyUnviewed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CCProperties.getInstance().PROP_MASSCHANGEVIEWED_ONLYUNVIEWED.setValue(cbOnlyUnviewed.isSelected());
			}
		});
		cbOnlyUnviewed.setBounds(10, 388, 356, 23);
		cbOnlyUnviewed.setSelected(CCProperties.getInstance().PROP_MASSCHANGEVIEWED_ONLYUNVIEWED.getValue());
		getContentPane().add(cbOnlyUnviewed);
	}
	
	private void init() {
		position = -1;
		running = true;
		
		nextMovie();
	}
	
	private void actionNextMovie(boolean viewed) {
		if (! running) {
			return;
		}
		
		CCMovie m = (CCMovie) movielist.getDatabaseElementBySort(position);
		m.setViewed(viewed);
		
		nextMovie();
	}
	
	private void nextMovie() {
		position++;
		
		if (position < movielist.getElementCount()) {
			CCDatabaseElement del = movielist.getDatabaseElementBySort(position);
			if (del.isMovie()) {
				CCMovie m = (CCMovie) del;
				
				if (cbOnlyUnviewed.isSelected()) {
					if (m.isViewed()) {
						nextMovie();
						return;
					}
				}
				
				lblCover.setIcon(m.getCoverIcon());
				lblCurrent.setIcon(CachedResourceLoader.getImageIcon((m.isViewed() ? (Resources.ICN_TABLE_VIEWED_TRUE) : (Resources.ICN_TABLE_VIEWED_FALSE))));
				lblTitle.setText(m.getCompleteTitle());
			} else {
				nextMovie();
				return;
			}
		} else {
			btnUnviewed.setEnabled(false);
			btnViewed.setEnabled(false);
			running = false;
			return;
		}
	}
	
	@SuppressWarnings("nls")
	private void initMap() {
		InputMap map = ((JPanel) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap act = ((JPanel) getContentPane()).getActionMap();
		
		map.put(KeyStroke.getKeyStroke('1'), "KEYPRESSED_1");
		map.put(KeyStroke.getKeyStroke('2'), "KEYPRESSED_2");
		
		act.put("KEYPRESSED_1", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(true);
			}
		});
		act.put("KEYPRESSED_2", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(false);
			}
		});
	}
}
