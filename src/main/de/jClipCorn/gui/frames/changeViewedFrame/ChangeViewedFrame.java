package de.jClipCorn.gui.frames.changeViewedFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.PropertyCheckbox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datetime.CCDateTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangeViewedFrame extends JCCFrame {
	private static final long serialVersionUID = 9048482551231383355L;
	
	private int position;
	private boolean running = false;
	
	private CoverLabel lblCover;
	private JButton btnViewed;
	private JButton btnUnviewed;
	private JLabel lblCurrent;
	private JLabel lblKeyViewed;
	private JLabel lblKeyUnviewed;
	private JLabel lblTitle;
	private PropertyCheckbox cbOnlyUnviewed;

	public ChangeViewedFrame(Component owner, CCMovieList ml) {
		super(ml);

		initGUI();
		
		initMap();
		setFocusable(true);
		
		setLocationRelativeTo(owner);
		init();
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("ChangedViewedFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setSize(new Dimension(382, 445));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		lblCover = new CoverLabel(movielist, false);
		lblCover.setPosition(100, 47);
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
		
		cbOnlyUnviewed = new PropertyCheckbox(ccprops().PROP_MASSCHANGEVIEWED_ONLYUNVIEWED);
		cbOnlyUnviewed.setBounds(10, 388, 356, 23);
		getContentPane().add(cbOnlyUnviewed);
	}
	
	private void init() {
		position = -1;
		running = true;
		
		nextMovie();
	}
	
	private void actionNextMovie(boolean viewed) {
		if (! running) return;
		
		CCMovie mov = (CCMovie) movielist.getDatabaseElementBySort(position);
		if (viewed && !mov.isViewed()) mov.ViewedHistory.add(CCDateTime.getUnspecified());
		if (!viewed) mov.ViewedHistory.set(CCDateTimeList.createEmpty());
		
		nextMovie();
	}
	
	private void nextMovie() {
		position++;
		
		if (position < movielist.getElementCount()) {
			CCDatabaseElement del = movielist.getDatabaseElementBySort(position);
			if (del.isMovie()) {
				CCMovie mov = (CCMovie) del;
				
				if (cbOnlyUnviewed.isSelected()) {
					if (mov.isViewed()) {
						nextMovie();
						return;
					}
				}
				
				lblCover.setAndResizeCover(mov.getCover());
				lblCurrent.setIcon((mov.isViewed() ? (Resources.ICN_TABLE_VIEWED_TRUE.get()) : (Resources.ICN_TABLE_VIEWED_FALSE.get())));
				lblTitle.setText(mov.getCompleteTitle());
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
